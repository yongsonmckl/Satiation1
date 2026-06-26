package com.mckl.satiation1.history

import com.mckl.satiation1.database.MealWithItems
import java.util.Calendar
import java.util.Locale

enum class HistoryRangeFilter {
    PAST_WEEK,
    PAST_MONTH,
    ALL_TIME
}

enum class HistorySourceFilter(val sourceType: String?) {
    ALL(null),
    MANUAL("manual"),
    AI_SCAN("ai_scan")
}

enum class HistoryCalorieFilter(
    val minInclusive: Double?,
    val maxInclusive: Double?
) {
    ALL(null, null),
    UNDER_500(null, 499.99),
    FROM_500_TO_1000(500.0, 1000.0),
    OVER_1000(1000.01, null)
}

data class HistoryFilterState(
    val query: String = "",
    val range: HistoryRangeFilter = HistoryRangeFilter.PAST_MONTH,
    val source: HistorySourceFilter = HistorySourceFilter.ALL,
    val calories: HistoryCalorieFilter = HistoryCalorieFilter.ALL
)

fun filterMeals(
    meals: List<MealWithItems>,
    filterState: HistoryFilterState,
    nowMillis: Long = System.currentTimeMillis()
): List<MealWithItems> {
    val threshold = thresholdForRange(filterState.range, nowMillis)
    val query = filterState.query.trim().lowercase(Locale.US)

    return meals.filter { meal ->
        val inRange = threshold == null || meal.meal.loggedAtEpochMillis >= threshold
        val sourceMatches = filterState.source.sourceType == null || meal.meal.sourceType == filterState.source.sourceType
        val caloriesMatches = matchesCalories(meal.meal.totalCalories, filterState.calories)
        val textMatches = if (query.isBlank()) {
            true
        } else {
            meal.items.any { it.name.lowercase(Locale.US).contains(query) || it.category.orEmpty().lowercase(Locale.US).contains(query) } ||
                meal.meal.notes.orEmpty().lowercase(Locale.US).contains(query)
        }
        inRange && sourceMatches && caloriesMatches && textMatches
    }
}

private fun matchesCalories(totalCalories: Double, filter: HistoryCalorieFilter): Boolean {
    val minOkay = filter.minInclusive?.let { totalCalories >= it } ?: true
    val maxOkay = filter.maxInclusive?.let { totalCalories <= it } ?: true
    return minOkay && maxOkay
}

private fun thresholdForRange(range: HistoryRangeFilter, nowMillis: Long): Long? {
    if (range == HistoryRangeFilter.ALL_TIME) return null
    val calendar = Calendar.getInstance().apply {
        timeInMillis = nowMillis
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    when (range) {
        HistoryRangeFilter.PAST_WEEK -> calendar.add(Calendar.DAY_OF_YEAR, -6)
        HistoryRangeFilter.PAST_MONTH -> calendar.add(Calendar.DAY_OF_YEAR, -29)
        HistoryRangeFilter.ALL_TIME -> return null
    }
    return calendar.timeInMillis
}
