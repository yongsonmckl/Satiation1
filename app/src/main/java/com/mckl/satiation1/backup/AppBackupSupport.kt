package com.mckl.satiation1.backup

import com.mckl.satiation1.database.AppSettings
import com.mckl.satiation1.database.MealItem
import com.mckl.satiation1.database.MealLog
import com.mckl.satiation1.database.MealWithItems
import com.mckl.satiation1.database.PresetFood
import com.mckl.satiation1.database.UserProfile
import com.mckl.satiation1.database.WeightLog
import org.json.JSONArray
import org.json.JSONObject

data class BackupSelection(
    val profile: Boolean = true,
    val settings: Boolean = true,
    val meals: Boolean = true,
    val weights: Boolean = true,
    val presetFoods: Boolean = true,
    val annotations: Boolean = true
) {
    fun anySelected(): Boolean {
        return profile || settings || meals || weights || presetFoods || annotations
    }
}

data class AppBackupPayload(
    val profile: UserProfile?,
    val settings: AppSettings?,
    val meals: List<MealWithItems>,
    val weights: List<WeightLog>,
    val presetFoods: List<PresetFood>,
    val annotations: Map<String, List<String>>
)

object AppBackupSupport {
    fun buildExportJson(payload: AppBackupPayload, selection: BackupSelection): String {
        val root = JSONObject()
        root.put("version", 1)
        root.put("exportedAtEpochMillis", System.currentTimeMillis())

        val categories = JSONObject().apply {
            if (selection.profile) {
                put("profile", payload.profile?.toJson() ?: JSONObject.NULL)
            }
            if (selection.settings) {
                put("settings", payload.settings?.toJson() ?: JSONObject.NULL)
            }
            if (selection.meals) {
                put("meals", JSONArray().apply {
                    payload.meals.forEach { put(it.toJson()) }
                })
            }
            if (selection.weights) {
                put("weights", JSONArray().apply {
                    payload.weights.forEach { put(it.toJson()) }
                })
            }
            if (selection.presetFoods) {
                put("presetFoods", JSONArray().apply {
                    payload.presetFoods.forEach { put(it.toJson()) }
                })
            }
            if (selection.annotations) {
                put("annotations", payload.annotations.toJson())
            }
        }

        root.put("categories", categories)
        return root.toString(2)
    }

    fun parseImportJson(rawJson: String): JSONObject {
        return JSONObject(rawJson)
    }

    fun readProfile(root: JSONObject): UserProfile? {
        val node = root.optJSONObject("profile") ?: return null
        return UserProfile(
            id = 1,
            name = node.optString("name"),
            pronouns = node.optString("pronouns"),
            heightCm = node.optDoubleOrNull("heightCm"),
            startWeightKg = node.optDouble("startWeightKg"),
            currentWeightKg = node.optDouble("currentWeightKg")
        )
    }

    fun readSettings(root: JSONObject): AppSettings? {
        val node = root.optJSONObject("settings") ?: return null
        return AppSettings(
            id = 1,
            geminiApiKey = node.optString("geminiApiKey").ifBlank { null },
            preferredUnits = node.optString("preferredUnits", "metric"),
            preferredDateFormat = node.optString("preferredDateFormat", "day_month_year"),
            calorieTarget = node.optDouble("calorieTarget", 2500.0),
            proteinTargetGrams = node.optDouble("proteinTargetGrams", 120.0),
            carbsTargetGrams = node.optDouble("carbsTargetGrams", 300.0),
            fatsTargetGrams = node.optDouble("fatsTargetGrams", 70.0),
            themePreference = node.optString("themePreference", "dark"),
            followSystemTheme = node.optBoolean("followSystemTheme", true),
            primaryAccentHex = node.optString("primaryAccentHex", "#BDE064"),
            secondaryAccentHex = node.optString("secondaryAccentHex", "#FF7D5A"),
            mealReminderEnabled = node.optBoolean("mealReminderEnabled", false),
            mealReminderHour = node.optInt("mealReminderHour", 12),
            mealReminderMinute = node.optInt("mealReminderMinute", 30),
            weightReminderEnabled = node.optBoolean("weightReminderEnabled", false),
            weightReminderHour = node.optInt("weightReminderHour", 8),
            weightReminderMinute = node.optInt("weightReminderMinute", 0),
            macroReminderEnabled = node.optBoolean("macroReminderEnabled", false),
            macroReminderHour = node.optInt("macroReminderHour", 20),
            macroReminderMinute = node.optInt("macroReminderMinute", 0)
        )
    }

    fun readMeals(root: JSONObject): List<MealWithItems> {
        val mealsArray = root.optJSONArray("meals") ?: return emptyList()
        return buildList {
            for (index in 0 until mealsArray.length()) {
                val mealWrapper = mealsArray.optJSONObject(index) ?: continue
                val mealObject = mealWrapper.optJSONObject("meal") ?: continue
                val itemsArray = mealWrapper.optJSONArray("items") ?: JSONArray()
                val meal = MealLog(
                    mealId = mealObject.optLong("mealId"),
                    loggedAtEpochMillis = mealObject.optLong("loggedAtEpochMillis"),
                    sourceType = mealObject.optString("sourceType"),
                    totalCalories = mealObject.optDouble("totalCalories"),
                    totalProteinGrams = mealObject.optDouble("totalProteinGrams"),
                    totalCarbsGrams = mealObject.optDouble("totalCarbsGrams"),
                    totalFatsGrams = mealObject.optDouble("totalFatsGrams"),
                    notes = mealObject.optString("notes").ifBlank { null }
                )
                val items = buildList {
                    for (itemIndex in 0 until itemsArray.length()) {
                        val itemObject = itemsArray.optJSONObject(itemIndex) ?: continue
                        add(
                            MealItem(
                                itemId = itemObject.optLong("itemId"),
                                mealId = itemObject.optLong("mealId"),
                                name = itemObject.optString("name"),
                                category = itemObject.optString("category").ifBlank { null },
                                calories = itemObject.optDouble("calories"),
                                proteinGrams = itemObject.optDouble("proteinGrams"),
                                carbsGrams = itemObject.optDouble("carbsGrams"),
                                fatsGrams = itemObject.optDouble("fatsGrams"),
                                confidence = itemObject.optDoubleOrNull("confidence")
                            )
                        )
                    }
                }
                add(MealWithItems(meal = meal, items = items))
            }
        }
    }

    fun readWeights(root: JSONObject): List<WeightLog> {
        val weightsArray = root.optJSONArray("weights") ?: return emptyList()
        return buildList {
            for (index in 0 until weightsArray.length()) {
                val weightObject = weightsArray.optJSONObject(index) ?: continue
                add(
                    WeightLog(
                        weightLogId = weightObject.optLong("weightLogId"),
                        loggedAtEpochMillis = weightObject.optLong("loggedAtEpochMillis"),
                        weightKg = weightObject.optDouble("weightKg")
                    )
                )
            }
        }
    }

    fun readPresetFoods(root: JSONObject): List<PresetFood> {
        val presetArray = root.optJSONArray("presetFoods") ?: return emptyList()
        return buildList {
            for (index in 0 until presetArray.length()) {
                val presetObject = presetArray.optJSONObject(index) ?: continue
                add(
                    PresetFood(
                        presetFoodId = presetObject.optLong("presetFoodId"),
                        name = presetObject.optString("name"),
                        category = presetObject.optString("category").ifBlank { null },
                        calories = presetObject.optDouble("calories"),
                        proteinGrams = presetObject.optDouble("proteinGrams"),
                        carbsGrams = presetObject.optDouble("carbsGrams"),
                        fatsGrams = presetObject.optDouble("fatsGrams"),
                        notes = presetObject.optString("notes").ifBlank { null },
                        updatedAtEpochMillis = presetObject.optLong("updatedAtEpochMillis")
                    )
                )
            }
        }
    }

    fun readAnnotations(root: JSONObject): Map<String, List<String>> {
        val annotationsObject = root.optJSONObject("annotations") ?: return emptyMap()
        return buildMap {
            val keys = annotationsObject.keys()
            while (keys.hasNext()) {
                val dayKey = keys.next()
                val notesArray = annotationsObject.optJSONArray(dayKey) ?: JSONArray()
                val notes = buildList {
                    for (index in 0 until notesArray.length()) {
                        notesArray.optString(index).trim().takeIf { it.isNotBlank() }?.let(::add)
                    }
                }
                if (notes.isNotEmpty()) {
                    put(dayKey, notes)
                }
            }
        }
    }
}

private fun UserProfile.toJson(): JSONObject {
    return JSONObject().apply {
        put("name", name)
        put("pronouns", pronouns)
        put("heightCm", heightCm ?: JSONObject.NULL)
        put("startWeightKg", startWeightKg)
        put("currentWeightKg", currentWeightKg)
    }
}

private fun AppSettings.toJson(): JSONObject {
    return JSONObject().apply {
        put("geminiApiKey", geminiApiKey ?: JSONObject.NULL)
        put("preferredUnits", preferredUnits)
        put("preferredDateFormat", preferredDateFormat)
        put("calorieTarget", calorieTarget)
        put("proteinTargetGrams", proteinTargetGrams)
        put("carbsTargetGrams", carbsTargetGrams)
        put("fatsTargetGrams", fatsTargetGrams)
        put("themePreference", themePreference)
        put("followSystemTheme", followSystemTheme)
        put("primaryAccentHex", primaryAccentHex)
        put("secondaryAccentHex", secondaryAccentHex)
        put("mealReminderEnabled", mealReminderEnabled)
        put("mealReminderHour", mealReminderHour)
        put("mealReminderMinute", mealReminderMinute)
        put("weightReminderEnabled", weightReminderEnabled)
        put("weightReminderHour", weightReminderHour)
        put("weightReminderMinute", weightReminderMinute)
        put("macroReminderEnabled", macroReminderEnabled)
        put("macroReminderHour", macroReminderHour)
        put("macroReminderMinute", macroReminderMinute)
    }
}

private fun MealWithItems.toJson(): JSONObject {
    return JSONObject().apply {
        put("meal", meal.toJson())
        put("items", JSONArray().apply {
            items.forEach { put(it.toJson()) }
        })
    }
}

private fun MealLog.toJson(): JSONObject {
    return JSONObject().apply {
        put("mealId", mealId)
        put("loggedAtEpochMillis", loggedAtEpochMillis)
        put("sourceType", sourceType)
        put("totalCalories", totalCalories)
        put("totalProteinGrams", totalProteinGrams)
        put("totalCarbsGrams", totalCarbsGrams)
        put("totalFatsGrams", totalFatsGrams)
        put("notes", notes ?: JSONObject.NULL)
    }
}

private fun MealItem.toJson(): JSONObject {
    return JSONObject().apply {
        put("itemId", itemId)
        put("mealId", mealId)
        put("name", name)
        put("category", category ?: JSONObject.NULL)
        put("calories", calories)
        put("proteinGrams", proteinGrams)
        put("carbsGrams", carbsGrams)
        put("fatsGrams", fatsGrams)
        put("confidence", confidence ?: JSONObject.NULL)
    }
}

private fun WeightLog.toJson(): JSONObject {
    return JSONObject().apply {
        put("weightLogId", weightLogId)
        put("loggedAtEpochMillis", loggedAtEpochMillis)
        put("weightKg", weightKg)
    }
}

private fun PresetFood.toJson(): JSONObject {
    return JSONObject().apply {
        put("presetFoodId", presetFoodId)
        put("name", name)
        put("category", category ?: JSONObject.NULL)
        put("calories", calories)
        put("proteinGrams", proteinGrams)
        put("carbsGrams", carbsGrams)
        put("fatsGrams", fatsGrams)
        put("notes", notes ?: JSONObject.NULL)
        put("updatedAtEpochMillis", updatedAtEpochMillis)
    }
}

private fun Map<String, List<String>>.toJson(): JSONObject {
    return JSONObject().apply {
        forEach { (dayKey, notes) ->
            put(dayKey, JSONArray().apply {
                notes.forEach { put(it) }
            })
        }
    }
}

private fun JSONObject.optDoubleOrNull(key: String): Double? {
    return if (isNull(key)) null else optDouble(key)
}
