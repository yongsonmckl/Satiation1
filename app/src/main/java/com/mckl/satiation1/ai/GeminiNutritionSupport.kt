package com.mckl.satiation1.ai

import org.json.JSONArray
import org.json.JSONObject

data class GeminiNutritionItem(
    val name: String,
    val category: String?,
    val calories: Double,
    val proteinGrams: Double,
    val carbsGrams: Double,
    val fatsGrams: Double,
    val confidence: Double?
)

data class GeminiNutritionResult(
    val totalCalories: Double,
    val totalProteinGrams: Double,
    val totalCarbsGrams: Double,
    val totalFatsGrams: Double,
    val items: List<GeminiNutritionItem>,
    val notes: String?
)

data class GeminiNutritionDraftItem(
    val name: String,
    val category: String,
    val calories: String,
    val proteinGrams: String,
    val carbsGrams: String,
    val fatsGrams: String,
    val confidence: String
)

data class GeminiNutritionDraft(
    val items: List<GeminiNutritionDraftItem>,
    val notes: String
)

data class GeminiAnalysisRequest(
    val prompt: String,
    val sanitizedHint: String?
)

data class GeminiNutritionAnalysis(
    val modelName: String,
    val rawText: String?,
    val sanitizedHint: String?,
    val result: GeminiNutritionResult
)

sealed interface GeminiNutritionParseResult {
    data class Success(val result: GeminiNutritionResult) : GeminiNutritionParseResult
    data class Failure(val message: String) : GeminiNutritionParseResult
}

sealed interface GeminiNutritionDraftValidationResult {
    data class Success(val result: GeminiNutritionResult) : GeminiNutritionDraftValidationResult
    data class Failure(val message: String) : GeminiNutritionDraftValidationResult
}

sealed interface NutritionScanUiState {
    data object Idle : NutritionScanUiState
    data object Loading : NutritionScanUiState
    data object Saving : NutritionScanUiState
    data object Saved : NutritionScanUiState
    data class MissingApiKey(val message: String) : NutritionScanUiState
    data class MissingImage(val message: String) : NutritionScanUiState
    data class ApiFailure(val message: String) : NutritionScanUiState
    data class InvalidModelOutput(val message: String) : NutritionScanUiState
    data class SaveFailure(val message: String, val draft: GeminiNutritionDraft) : NutritionScanUiState
    data class Review(val draft: GeminiNutritionDraft) : NutritionScanUiState
}

object GeminiNutritionSupport {
    const val preferredVisionModelName: String = "gemini-2.5-pro"
    const val fallbackVisionModelName: String = "gemini-2.5-flash"
    val visionModelCandidates: List<String> = listOf(
        preferredVisionModelName,
        fallbackVisionModelName
    )

    private val basePrompt: String = """
        Analyze the meal in this image and estimate its nutrition.
        Return ONLY a raw JSON object.
        Do not include markdown, code fences, backticks, comments, or explanation text.
        Use this exact shape:
        {
          "totalCalories": 0,
          "totalProteinGrams": 0,
          "totalCarbsGrams": 0,
          "totalFatsGrams": 0,
          "notes": "optional short assumptions or null",
          "items": [
            {
              "name": "Food name",
              "category": "category or null",
              "calories": 0,
              "proteinGrams": 0,
              "carbsGrams": 0,
              "fatsGrams": 0,
              "confidence": 0.0
            }
          ]
        }
        Every numeric field must be a number, not a string.
        Use confidence from 0.0 to 1.0 when possible, otherwise null.
        If multiple foods are present, include one item object per food.
        Do not cite sources, add references, mention books, or include any text outside the JSON object.
    """.trimIndent()

    fun buildRequest(userHint: String?): GeminiAnalysisRequest {
        val sanitizedHint = userHint
            ?.trim()
            ?.takeIf { it.isNotEmpty() }

        val prompt = buildString {
            append(basePrompt)
            if (sanitizedHint != null) {
                appendLine()
                appendLine()
                appendLine("Optional user hint:")
                appendLine(sanitizedHint)
                appendLine("Use the hint only if it matches the visible image.")
                append("Do not invent extra foods that are not visible.")
            }
        }

        return GeminiAnalysisRequest(
            prompt = prompt,
            sanitizedHint = sanitizedHint
        )
    }

    fun parse(rawText: String?): GeminiNutritionParseResult {
        if (rawText.isNullOrBlank()) {
            return GeminiNutritionParseResult.Failure("The AI returned an empty response.")
        }

        val jsonCandidate = extractJsonCandidate(rawText)
            ?: return GeminiNutritionParseResult.Failure(
                "The AI response was not valid JSON."
            )

        return runCatching {
            val root = JSONObject(jsonCandidate)
            val totalsContainer = root.optJSONObject("totals")

            val result = GeminiNutritionResult(
                totalCalories = readRequiredNumber(
                    root,
                    totalsContainer,
                    "totalCalories",
                    "total_calories"
                ),
                totalProteinGrams = readRequiredNumber(
                    root,
                    totalsContainer,
                    "totalProteinGrams",
                    "total_protein_grams"
                ),
                totalCarbsGrams = readRequiredNumber(
                    root,
                    totalsContainer,
                    "totalCarbsGrams",
                    "total_carbs_grams"
                ),
                totalFatsGrams = readRequiredNumber(
                    root,
                    totalsContainer,
                    "totalFatsGrams",
                    "total_fats_grams"
                ),
                items = parseItems(root.optJSONArray("items")),
                notes = root.optString("notes")
                    .trim()
                    .ifBlank { null }
                    .takeUnless { it.equals("null", ignoreCase = true) }
            )

            GeminiNutritionParseResult.Success(result)
        }.getOrElse { exception ->
            GeminiNutritionParseResult.Failure(
                exception.message ?: "The AI returned malformed nutrition data."
            )
        }
    }

    fun GeminiNutritionResult.toDraft(): GeminiNutritionDraft {
        return GeminiNutritionDraft(
            items = items.map { item ->
                GeminiNutritionDraftItem(
                    name = item.name,
                    category = item.category.orEmpty(),
                    calories = item.calories.toEditableNumber(),
                    proteinGrams = item.proteinGrams.toEditableNumber(),
                    carbsGrams = item.carbsGrams.toEditableNumber(),
                    fatsGrams = item.fatsGrams.toEditableNumber(),
                    confidence = item.confidence?.toEditableNumber().orEmpty()
                )
            },
            notes = notes.orEmpty()
        )
    }

    fun validateDraft(draft: GeminiNutritionDraft): GeminiNutritionDraftValidationResult {
        if (draft.items.isEmpty()) {
            return GeminiNutritionDraftValidationResult.Failure(
                "Add at least one food item before saving."
            )
        }

        return runCatching {
            val parsedItems = draft.items.mapIndexed { index, item ->
                val name = item.name.trim()
                if (name.isBlank()) {
                    error("Item ${index + 1} needs a food name.")
                }

                GeminiNutritionItem(
                    name = name,
                    category = item.category.trim().ifBlank { null },
                    calories = parseDraftNumber(item.calories, "Item ${index + 1} calories"),
                    proteinGrams = parseDraftNumber(item.proteinGrams, "Item ${index + 1} protein"),
                    carbsGrams = parseDraftNumber(item.carbsGrams, "Item ${index + 1} carbs"),
                    fatsGrams = parseDraftNumber(item.fatsGrams, "Item ${index + 1} fats"),
                    confidence = parseDraftConfidence(item.confidence, index)
                )
            }

            GeminiNutritionResult(
                totalCalories = parsedItems.sumOf { it.calories },
                totalProteinGrams = parsedItems.sumOf { it.proteinGrams },
                totalCarbsGrams = parsedItems.sumOf { it.carbsGrams },
                totalFatsGrams = parsedItems.sumOf { it.fatsGrams },
                items = parsedItems,
                notes = draft.notes.trim().ifBlank { null }
            )
        }.fold(
            onSuccess = { GeminiNutritionDraftValidationResult.Success(it) },
            onFailure = {
                GeminiNutritionDraftValidationResult.Failure(
                    it.message ?: "Review the AI meal values before saving."
                )
            }
        )
    }

    private fun extractJsonCandidate(rawText: String): String? {
        val stripped = stripMarkdownFences(rawText).trim()
        val firstBrace = stripped.indexOf('{')
        if (firstBrace == -1) {
            return null
        }

        var depth = 0
        var inString = false
        var isEscaped = false

        for (index in firstBrace until stripped.length) {
            val char = stripped[index]
            when {
                isEscaped -> isEscaped = false
                char == '\\' && inString -> isEscaped = true
                char == '"' -> inString = !inString
                !inString && char == '{' -> depth += 1
                !inString && char == '}' -> {
                    depth -= 1
                    if (depth == 0) {
                        return stripped.substring(firstBrace, index + 1)
                    }
                }
            }
        }

        return null
    }

    private fun stripMarkdownFences(rawText: String): String {
        return rawText
            .replace("```json", "", ignoreCase = true)
            .replace("```", "")
            .trim()
    }

    private fun parseItems(itemsArray: JSONArray?): List<GeminiNutritionItem> {
        if (itemsArray == null) {
            error("The AI response is missing the items list.")
        }

        val parsedItems = buildList {
            for (index in 0 until itemsArray.length()) {
                val itemObject = itemsArray.optJSONObject(index)
                    ?: error("Item ${index + 1} is not a valid object.")
                val name = itemObject.optString("name").trim()
                if (name.isBlank()) {
                    error("Item ${index + 1} is missing a usable name.")
                }

                add(
                    GeminiNutritionItem(
                        name = name,
                        category = itemObject.optString("category")
                            .trim()
                            .ifBlank { null }
                            .takeUnless { it.equals("null", ignoreCase = true) },
                        calories = readRequiredNumber(itemObject, null, "calories"),
                        proteinGrams = readRequiredNumber(itemObject, null, "proteinGrams", "protein_grams"),
                        carbsGrams = readRequiredNumber(itemObject, null, "carbsGrams", "carbs_grams"),
                        fatsGrams = readRequiredNumber(itemObject, null, "fatsGrams", "fats_grams"),
                        confidence = readOptionalConfidence(itemObject)
                    )
                )
            }
        }

        if (parsedItems.isEmpty()) {
            error("The AI response did not include any food items.")
        }

        return parsedItems
    }

    private fun readRequiredNumber(
        primary: JSONObject,
        secondary: JSONObject?,
        vararg keys: String
    ): Double {
        val value = keys.asSequence()
            .mapNotNull { key -> primary.opt(key).takeIf { it != null && it != JSONObject.NULL } }
            .firstOrNull()
            ?: keys.asSequence()
                .mapNotNull { key -> secondary?.opt(key).takeIf { it != null && it != JSONObject.NULL } }
                .firstOrNull()
            ?: error("Missing numeric field `${keys.first()}`.")

        val number = when (value) {
            is Number -> value.toDouble()
            is String -> value.toDoubleOrNull()
            else -> null
        } ?: error("Field `${keys.first()}` must be numeric.")

        if (!number.isFinite() || number < 0.0) {
            error("Field `${keys.first()}` must be a non-negative finite number.")
        }
        return number
    }

    private fun readOptionalConfidence(itemObject: JSONObject): Double? {
        val rawValue = sequenceOf("confidence", "confidenceScore", "confidence_score")
            .mapNotNull { key ->
                itemObject.opt(key).takeIf { it != null && it != JSONObject.NULL }
            }
            .firstOrNull() ?: return null

        val confidence = when (rawValue) {
            is Number -> rawValue.toDouble()
            is String -> rawValue.toDoubleOrNull()
            else -> null
        } ?: return null

        if (!confidence.isFinite()) {
            return null
        }

        return confidence.coerceIn(0.0, 1.0)
    }

    private fun parseDraftNumber(rawValue: String, label: String): Double {
        val number = rawValue.trim().toDoubleOrNull()
            ?: error("$label must be a valid number.")
        if (!number.isFinite() || number < 0.0) {
            error("$label must be a non-negative number.")
        }
        return number
    }

    private fun parseDraftConfidence(rawValue: String, index: Int): Double? {
        val trimmed = rawValue.trim()
        if (trimmed.isBlank()) {
            return null
        }
        val confidence = trimmed.toDoubleOrNull()
            ?: error("Item ${index + 1} confidence must be numeric.")
        if (!confidence.isFinite() || confidence < 0.0 || confidence > 1.0) {
            error("Item ${index + 1} confidence must be between 0 and 1.")
        }
        return confidence
    }

    private fun Double.toEditableNumber(): String {
        return if (this % 1.0 == 0.0) {
            toInt().toString()
        } else {
            String.format("%.1f", this)
        }
    }
}
