package com.mckl.satiation1

import android.app.Application
import android.graphics.Bitmap
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mckl.satiation1.ai.GeminiNutritionItem
import com.mckl.satiation1.ai.GeminiNutritionResult
import com.mckl.satiation1.database.SatiationDatabase
import com.mckl.satiation1.navigation.SatiationViewModel
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AiMealPersistenceInstrumentedTest {

    private lateinit var application: Application
    private lateinit var database: SatiationDatabase

    @Before
    fun setUp() = runBlocking {
        application = ApplicationProvider.getApplicationContext()
        database = SatiationDatabase.getDatabase(application)
        database.clearAllTables()
    }

    @Test
    fun saveAiMeal_persistsMealItemsAndClearsCapturedImage() = runBlocking {
        val viewModel = SatiationViewModel(application)
        val timestamp = 1_718_234_567_890L
        val analysis = GeminiNutritionResult(
            totalCalories = 540.0,
            totalProteinGrams = 32.0,
            totalCarbsGrams = 48.0,
            totalFatsGrams = 18.0,
            items = listOf(
                GeminiNutritionItem(
                    name = "Chicken",
                    category = "Protein",
                    calories = 300.0,
                    proteinGrams = 28.0,
                    carbsGrams = 0.0,
                    fatsGrams = 14.0,
                    confidence = 0.89
                ),
                GeminiNutritionItem(
                    name = "Rice",
                    category = "Carb",
                    calories = 240.0,
                    proteinGrams = 4.0,
                    carbsGrams = 48.0,
                    fatsGrams = 4.0,
                    confidence = null
                )
            ),
            notes = "Saved from AI scan"
        )
        val latch = CountDownLatch(1)
        var callbackResult: Result<Unit>? = null

        viewModel.setCapturedImage(Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_8888))
        assertNotNull(viewModel.capturedImage.value)

        viewModel.saveAiMeal(analysis, loggedAtEpochMillis = timestamp) { result ->
            callbackResult = result
            latch.countDown()
        }

        assertTrue("save callback timed out", latch.await(5, TimeUnit.SECONDS))
        assertTrue(callbackResult.toString(), callbackResult?.isSuccess == true)

        val meals = database.mealDao()
            .getMealsBetween(timestamp - 1, timestamp + 1)
            .first()

        assertEquals(1, meals.size)
        val meal = meals.single()
        assertEquals("ai_scan", meal.meal.sourceType)
        assertEquals(540.0, meal.meal.totalCalories, 0.0)
        assertEquals("Saved from AI scan", meal.meal.notes)
        assertEquals(2, meal.items.size)
        assertEquals("Chicken", meal.items.first().name)
        assertEquals(0.89, meal.items.first().confidence ?: 0.0, 0.0)
        assertNull(viewModel.capturedImage.value)
    }
}
