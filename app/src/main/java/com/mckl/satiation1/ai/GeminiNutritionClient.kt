package com.mckl.satiation1.ai

import android.graphics.Bitmap
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.delay

data class GeminiNutritionGeneration(
    val modelName: String,
    val rawText: String?,
    val sanitizedHint: String?
)

object GeminiNutritionClient {
    suspend fun analyzeMeal(
        apiKey: String,
        bitmap: Bitmap,
        userHint: String?
    ): GeminiNutritionAnalysis {
        val request = GeminiNutritionSupport.buildRequest(userHint)
        var lastFailure: Exception? = null
        var lastRawText: String? = null

        GeminiNutritionSupport.visionModelCandidates.forEachIndexed { index, modelName ->
            val model = GenerativeModel(
                modelName = modelName,
                apiKey = apiKey
            )
            var shouldTryNextModel = false

            for (attempt in 0 until MAX_ATTEMPTS_PER_MODEL) {
                try {
                    val response = model.generateContent(
                        content {
                            image(bitmap)
                            text(request.prompt)
                        }
                    )
                    lastRawText = response.text
                    when (val parseResult = GeminiNutritionSupport.parse(lastRawText)) {
                        is GeminiNutritionParseResult.Success -> {
                            return GeminiNutritionAnalysis(
                                modelName = modelName,
                                rawText = lastRawText,
                                sanitizedHint = request.sanitizedHint,
                                result = parseResult.result
                            )
                        }
                        is GeminiNutritionParseResult.Failure -> {
                            lastFailure = IllegalStateException(
                                "The AI returned malformed nutrition data: ${parseResult.message}"
                            )
                            val hasMoreAttempts = attempt < MAX_ATTEMPTS_PER_MODEL - 1
                            if (hasMoreAttempts) {
                                delay(RETRY_DELAY_MILLIS)
                                continue
                            }
                            if (
                                index == GeminiNutritionSupport.visionModelCandidates.lastIndex ||
                                !shouldRetryWithFallback(modelName, lastFailure as Exception)
                            ) {
                                throw lastFailure as Exception
                            }

                            shouldTryNextModel = true
                            break
                        }
                    }
                } catch (exception: Exception) {
                    lastFailure = exception
                    val hasMoreAttempts = attempt < MAX_ATTEMPTS_PER_MODEL - 1
                    if (hasMoreAttempts && shouldRetrySameModel(exception)) {
                        delay(RETRY_DELAY_MILLIS)
                        continue
                    }

                    if (
                        index == GeminiNutritionSupport.visionModelCandidates.lastIndex ||
                        !shouldRetryWithFallback(modelName, exception)
                    ) {
                        throw exception
                    }

                    shouldTryNextModel = true
                    break
                }
            }

            if (shouldTryNextModel) {
                return@forEachIndexed
            }
        }

        throw lastFailure ?: IllegalStateException(
            "Gemini analysis failed before any usable model response. Last raw text: $lastRawText"
        )
    }

    private fun shouldRetryWithFallback(modelName: String, exception: Exception): Boolean {
        if (modelName != GeminiNutritionSupport.preferredVisionModelName) {
            return false
        }

        val message = exception.message.orEmpty().lowercase()
        return message.contains("quota exceeded") ||
            message.contains("not found") ||
            message.contains("unsupported") ||
            message.contains("permission") ||
            message.contains("access")
    }

    private fun shouldRetrySameModel(exception: Exception): Boolean {
        val message = exception.message.orEmpty().lowercase()
        return message.contains("503") ||
            message.contains("high demand") ||
            message.contains("unavailable") ||
            message.contains("please try again later")
    }

    private const val MAX_ATTEMPTS_PER_MODEL = 3
    private const val RETRY_DELAY_MILLIS = 2_500L
}
