package com.mckl.satiation1

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mckl.satiation1.ai.GeminiAnalysisRequest
import com.mckl.satiation1.ai.GeminiNutritionDraft
import com.mckl.satiation1.ai.GeminiNutritionDraftItem
import com.mckl.satiation1.ai.GeminiNutritionDraftValidationResult
import com.mckl.satiation1.ai.GeminiNutritionParseResult
import com.mckl.satiation1.ai.GeminiNutritionSupport
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GeminiNutritionSupportInstrumentedTest {

    @Test
    fun parse_acceptsMarkdownFencedJson() {
        val response = """
            ```json
            {
              "totalCalories": 640,
              "totalProteinGrams": 38,
              "totalCarbsGrams": 52,
              "totalFatsGrams": 24,
              "notes": "Estimated from visible rice bowl.",
              "items": [
                {
                  "name": "Chicken",
                  "category": "Protein",
                  "calories": 320,
                  "proteinGrams": 31,
                  "carbsGrams": 0,
                  "fatsGrams": 16,
                  "confidence": 0.91
                },
                {
                  "name": "Rice",
                  "category": "Grain",
                  "calories": 320,
                  "proteinGrams": 7,
                  "carbsGrams": 52,
                  "fatsGrams": 8,
                  "confidence": 0.82
                }
              ]
            }
            ```
        """.trimIndent()

        val parseResult = GeminiNutritionSupport.parse(response)

        assertTrue(parseResult.toString(), parseResult is GeminiNutritionParseResult.Success)
        val success = parseResult as GeminiNutritionParseResult.Success
        assertEquals(640.0, success.result.totalCalories, 0.0)
        assertEquals(2, success.result.items.size)
        assertEquals("Chicken", success.result.items.first().name)
    }

    @Test
    fun parse_acceptsJsonWrappedWithExtraText() {
        val response = """
            Here is the result:
            {
              "total_calories": 500,
              "total_protein_grams": 20,
              "total_carbs_grams": 50,
              "total_fats_grams": 20,
              "items": [
                {
                  "name": "Noodles",
                  "category": "Carb",
                  "calories": 500,
                  "protein_grams": 20,
                  "carbs_grams": 50,
                  "fats_grams": 20,
                  "confidence_score": 0.75
                }
              ]
            }
            End.
        """.trimIndent()

        val parseResult = GeminiNutritionSupport.parse(response)

        assertTrue(parseResult.toString(), parseResult is GeminiNutritionParseResult.Success)
        val success = parseResult as GeminiNutritionParseResult.Success
        assertEquals(500.0, success.result.totalCalories, 0.0)
        assertEquals(0.75, success.result.items.first().confidence ?: 0.0, 0.0)
    }

    @Test
    fun parse_stopsAtFirstBalancedJsonObject() {
        val response = """
            ```json
            {
              "totalCalories": 300,
              "totalProteinGrams": 12,
              "totalCarbsGrams": 20,
              "totalFatsGrams": 14,
              "items": [
                {
                  "name": "Egg",
                  "category": "Protein",
                  "calories": 300,
                  "proteinGrams": 12,
                  "carbsGrams": 20,
                  "fatsGrams": 14,
                  "confidence": 0.81
                }
              ]
            }
            trailing text with braces {not json}
            ```
        """.trimIndent()

        val parseResult = GeminiNutritionSupport.parse(response)

        assertTrue(parseResult.toString(), parseResult is GeminiNutritionParseResult.Success)
        val success = parseResult as GeminiNutritionParseResult.Success
        assertEquals(300.0, success.result.totalCalories, 0.0)
        assertEquals("Egg", success.result.items.single().name)
    }

    @Test
    fun parse_rejectsNegativeNutritionValues() {
        val response = """
            {
              "totalCalories": -1,
              "totalProteinGrams": 10,
              "totalCarbsGrams": 10,
              "totalFatsGrams": 10,
              "items": [
                {
                  "name": "Soup",
                  "category": "Bowl",
                  "calories": 100,
                  "proteinGrams": 10,
                  "carbsGrams": 10,
                  "fatsGrams": 10,
                  "confidence": 0.5
                }
              ]
            }
        """.trimIndent()

        val parseResult = GeminiNutritionSupport.parse(response)

        assertTrue(parseResult.toString(), parseResult is GeminiNutritionParseResult.Failure)
        val failure = parseResult as GeminiNutritionParseResult.Failure
        assertTrue(failure.message, failure.message.contains("totalCalories"))
    }

    @Test
    fun parse_rejectsItemsWithoutNames() {
        val response = """
            {
              "totalCalories": 120,
              "totalProteinGrams": 5,
              "totalCarbsGrams": 10,
              "totalFatsGrams": 4,
              "items": [
                {
                  "name": "",
                  "category": "Snack",
                  "calories": 120,
                  "proteinGrams": 5,
                  "carbsGrams": 10,
                  "fatsGrams": 4,
                  "confidence": 0.7
                }
              ]
            }
        """.trimIndent()

        val parseResult = GeminiNutritionSupport.parse(response)

        assertTrue(parseResult.toString(), parseResult is GeminiNutritionParseResult.Failure)
        val failure = parseResult as GeminiNutritionParseResult.Failure
        assertTrue(failure.message, failure.message.contains("usable name"))
    }

    @Test
    fun validateDraft_recalculatesTotalsFromItems() {
        val draft = GeminiNutritionDraft(
            items = listOf(
                GeminiNutritionDraftItem(
                    name = "Chicken",
                    category = "Protein",
                    calories = "220",
                    proteinGrams = "30",
                    carbsGrams = "0",
                    fatsGrams = "8",
                    confidence = "0.9"
                ),
                GeminiNutritionDraftItem(
                    name = "Rice",
                    category = "Carb",
                    calories = "180",
                    proteinGrams = "4",
                    carbsGrams = "38",
                    fatsGrams = "1",
                    confidence = ""
                )
            ),
            notes = "Estimated bowl"
        )

        val validationResult = GeminiNutritionSupport.validateDraft(draft)

        assertTrue(validationResult.toString(), validationResult is GeminiNutritionDraftValidationResult.Success)
        val success = validationResult as GeminiNutritionDraftValidationResult.Success
        assertEquals(400.0, success.result.totalCalories, 0.0)
        assertEquals(34.0, success.result.totalProteinGrams, 0.0)
        assertEquals(38.0, success.result.totalCarbsGrams, 0.0)
        assertEquals(9.0, success.result.totalFatsGrams, 0.0)
        assertEquals("Estimated bowl", success.result.notes)
    }

    @Test
    fun validateDraft_rejectsConfidenceOutsideRange() {
        val draft = GeminiNutritionDraft(
            items = listOf(
                GeminiNutritionDraftItem(
                    name = "Soup",
                    category = "Bowl",
                    calories = "150",
                    proteinGrams = "8",
                    carbsGrams = "12",
                    fatsGrams = "7",
                    confidence = "1.4"
                )
            ),
            notes = ""
        )

        val validationResult = GeminiNutritionSupport.validateDraft(draft)

        assertTrue(validationResult.toString(), validationResult is GeminiNutritionDraftValidationResult.Failure)
        val failure = validationResult as GeminiNutritionDraftValidationResult.Failure
        assertTrue(failure.message, failure.message.contains("confidence"))
    }

    @Test
    fun buildRequest_omitsHintSectionWhenBlank() {
        val request = GeminiNutritionSupport.buildRequest("   ")

        assertEquals(null, request.sanitizedHint)
        assertTrue(request.prompt.contains("Analyze the meal in this image"))
        assertTrue(!request.prompt.contains("Optional user hint:"))
    }

    @Test
    fun buildRequest_includesHintSectionWhenProvided() {
        val request: GeminiAnalysisRequest = GeminiNutritionSupport.buildRequest(
            "Braised pork, egg, and vegetables"
        )

        assertEquals("Braised pork, egg, and vegetables", request.sanitizedHint)
        assertTrue(request.prompt.contains("Optional user hint:"))
        assertTrue(request.prompt.contains("Braised pork, egg, and vegetables"))
        assertTrue(request.prompt.contains("Use the hint only if it matches the visible image."))
        assertTrue(request.prompt.contains("Do not cite sources"))
    }
}
