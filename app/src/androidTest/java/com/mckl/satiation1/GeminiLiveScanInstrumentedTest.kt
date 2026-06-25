package com.mckl.satiation1

import android.app.Application
import android.graphics.BitmapFactory
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.mckl.satiation1.ai.GeminiNutritionClient
import com.mckl.satiation1.ai.GeminiNutritionResult
import com.mckl.satiation1.database.SatiationDatabase
import com.mckl.satiation1.database.UserProfile
import com.mckl.satiation1.navigation.SatiationViewModel
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GeminiLiveScanInstrumentedTest {

    @Test
    fun analyzeMealWithoutHint() = runBlocking {
        val (result, modelName) = runLiveAnalysis(userHint = null)
        assertTrue("Expected positive calories", result.totalCalories > 0.0)
        assertTrue("Expected non-empty item list", result.items.isNotEmpty())
        assertTrue("Expected at least one confidence value", result.items.any { it.confidence != null })
        println("LIVE_NO_HINT_RESULT[$modelName]: ${summarize(result)}")
    }

    @Test
    fun analyzeMealWithHintAndOptionalSave() = runBlocking {
        val arguments = InstrumentationRegistry.getArguments()
        val configuredHint = arguments.getString(ARG_SCAN_HINT)?.trim().orEmpty()
        val hint = configuredHint.ifBlank { "Braised Pork, Egg, and Vegetables" }
        val (result, modelName) = runLiveAnalysis(userHint = hint)
        assertTrue("Expected positive calories", result.totalCalories > 0.0)
        assertTrue("Expected non-empty item list", result.items.isNotEmpty())
        assertTrue("Expected at least one confidence value", result.items.any { it.confidence != null })
        println("LIVE_HINT_RESULT[$modelName]: ${summarize(result)}")

        if (arguments.getString(ARG_PERSIST_RESULT)?.toBooleanStrictOrNull() == true) {
            val application = ApplicationProvider.getApplicationContext<Application>()
            val database = SatiationDatabase.getDatabase(application)
            database.userProfileDao().insertOrUpdateProfile(
                UserProfile(
                    id = 1,
                    name = "Codex QA",
                    pronouns = "they/them",
                    heightCm = 170.0,
                    startWeightKg = 70.0,
                    currentWeightKg = 70.0
                )
            )
            val viewModel = SatiationViewModel(application)
            val latch = CountDownLatch(1)
            var saveSucceeded = false
            viewModel.saveAiMeal(result) { saveResult ->
                saveSucceeded = saveResult.isSuccess
                latch.countDown()
            }
            assertTrue("Timed out while saving live AI result", latch.await(5, TimeUnit.SECONDS))
            assertTrue("Expected live AI result save to succeed", saveSucceeded)
        }
    }

    private suspend fun runLiveAnalysis(userHint: String?): Pair<GeminiNutritionResult, String> {
        val arguments = InstrumentationRegistry.getArguments()
        val apiKey = arguments.getString(ARG_GEMINI_API_KEY)?.trim().orEmpty()
        val imagePath = arguments.getString(ARG_IMAGE_PATH)?.trim().orEmpty()

        assertTrue("Missing instrumentation arg `$ARG_GEMINI_API_KEY`", apiKey.isNotBlank())
        assertTrue("Missing instrumentation arg `$ARG_IMAGE_PATH`", imagePath.isNotBlank())

        val bitmap = BitmapFactory.decodeFile(imagePath)
        assertNotNull("Unable to decode image at $imagePath", bitmap)

        val analysis = GeminiNutritionClient.analyzeMeal(
            apiKey = apiKey,
            bitmap = bitmap!!,
            userHint = userHint
        )
        return analysis.result to analysis.modelName
    }

    private fun summarize(result: GeminiNutritionResult): String {
        val items = result.items.joinToString(" | ") { item ->
            val confidence = item.confidence?.let { "%.2f".format(it) } ?: "null"
            "${item.name}:${item.calories}/${confidence}"
        }
        return "totals=${result.totalCalories}/${result.totalProteinGrams}/${result.totalCarbsGrams}/${result.totalFatsGrams}; items=$items; notes=${result.notes}"
    }

    companion object {
        private const val ARG_GEMINI_API_KEY = "gemini_api_key"
        private const val ARG_IMAGE_PATH = "image_path"
        private const val ARG_SCAN_HINT = "scan_hint"
        private const val ARG_PERSIST_RESULT = "persist_result"
    }
}
