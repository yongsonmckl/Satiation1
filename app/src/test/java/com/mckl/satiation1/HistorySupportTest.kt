package com.mckl.satiation1

import com.mckl.satiation1.database.MealItem
import com.mckl.satiation1.database.MealLog
import com.mckl.satiation1.database.MealWithItems
import com.mckl.satiation1.history.HistoryCalorieFilter
import com.mckl.satiation1.history.HistoryFilterState
import com.mckl.satiation1.history.HistoryRangeFilter
import com.mckl.satiation1.history.HistorySourceFilter
import com.mckl.satiation1.history.filterMeals
import org.junit.Assert.assertEquals
import org.junit.Test

class HistorySupportTest {
    @Test
    fun filtersBySearchSourceAndCalories() {
        val now = 1_750_000_000_000L
        val meals = listOf(
            meal(
                id = 1,
                loggedAt = now,
                source = "manual",
                calories = 450.0,
                name = "Chicken Rice",
                notes = "Lunch"
            ),
            meal(
                id = 2,
                loggedAt = now,
                source = "ai_scan",
                calories = 880.0,
                name = "Pork Bowl",
                notes = "Dinner"
            )
        )

        val filtered = filterMeals(
            meals = meals,
            filterState = HistoryFilterState(
                query = "pork",
                range = HistoryRangeFilter.ALL_TIME,
                source = HistorySourceFilter.AI_SCAN,
                calories = HistoryCalorieFilter.FROM_500_TO_1000
            ),
            nowMillis = now
        )

        assertEquals(listOf(2L), filtered.map { it.meal.mealId })
    }

    @Test
    fun filtersByPastWeekThreshold() {
        val now = 1_750_000_000_000L
        val meals = listOf(
            meal(id = 1, loggedAt = now, source = "manual", calories = 300.0, name = "Today Meal"),
            meal(id = 2, loggedAt = now - (10L * 24 * 60 * 60 * 1000), source = "manual", calories = 300.0, name = "Old Meal")
        )

        val filtered = filterMeals(
            meals = meals,
            filterState = HistoryFilterState(range = HistoryRangeFilter.PAST_WEEK),
            nowMillis = now
        )

        assertEquals(listOf(1L), filtered.map { it.meal.mealId })
    }

    private fun meal(
        id: Long,
        loggedAt: Long,
        source: String,
        calories: Double,
        name: String,
        notes: String? = null
    ): MealWithItems {
        val log = MealLog(
            mealId = id,
            loggedAtEpochMillis = loggedAt,
            sourceType = source,
            totalCalories = calories,
            totalProteinGrams = 20.0,
            totalCarbsGrams = 30.0,
            totalFatsGrams = 10.0,
            notes = notes
        )
        val item = MealItem(
            mealId = id,
            name = name,
            calories = calories,
            proteinGrams = 20.0,
            carbsGrams = 30.0,
            fatsGrams = 10.0
        )
        return MealWithItems(log, listOf(item))
    }
}
