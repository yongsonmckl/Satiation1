package com.mckl.satiation1.reminders

import java.util.Calendar

enum class ReminderType(
    val requestCode: Int,
    val channelId: String,
    val title: String,
    val body: String
) {
    MEAL_LOGGING(
        requestCode = 4101,
        channelId = "meal_logging_reminders",
        title = "Log your meals",
        body = "Capture today's meals so your calorie and macro history stays accurate."
    ),
    WEIGHT_LOGGING(
        requestCode = 4102,
        channelId = "weight_logging_reminders",
        title = "Log your weight",
        body = "Record a weight entry to keep your progress charts current."
    ),
    MACRO_CHECK_IN(
        requestCode = 4103,
        channelId = "macro_check_in_reminders",
        title = "Macro check-in",
        body = "Review today's calories and macros before the day ends."
    )
}

fun nextReminderTriggerAt(hourOfDay: Int, minute: Int, nowMillis: Long = System.currentTimeMillis()): Long {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = nowMillis
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        set(Calendar.HOUR_OF_DAY, hourOfDay.coerceIn(0, 23))
        set(Calendar.MINUTE, minute.coerceIn(0, 59))
    }
    if (calendar.timeInMillis <= nowMillis) {
        calendar.add(Calendar.DAY_OF_YEAR, 1)
    }
    return calendar.timeInMillis
}
