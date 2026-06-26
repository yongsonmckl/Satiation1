package com.mckl.satiation1

import com.mckl.satiation1.reminders.nextReminderTriggerAt
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class ReminderSupportTest {
    @Test
    fun nextReminderStaysTodayWhenTimeIsAhead() {
        val now = Calendar.getInstance().apply {
            set(2026, Calendar.JUNE, 26, 8, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val trigger = nextReminderTriggerAt(20, 15, now)
        val calendar = Calendar.getInstance().apply { timeInMillis = trigger }

        assertEquals(26, calendar.get(Calendar.DAY_OF_MONTH))
        assertEquals(20, calendar.get(Calendar.HOUR_OF_DAY))
        assertEquals(15, calendar.get(Calendar.MINUTE))
    }

    @Test
    fun nextReminderRollsToTomorrowWhenTimePassed() {
        val now = Calendar.getInstance().apply {
            set(2026, Calendar.JUNE, 26, 21, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val trigger = nextReminderTriggerAt(20, 15, now)
        val calendar = Calendar.getInstance().apply { timeInMillis = trigger }

        assertTrue(trigger > now)
        assertEquals(27, calendar.get(Calendar.DAY_OF_MONTH))
        assertEquals(20, calendar.get(Calendar.HOUR_OF_DAY))
        assertEquals(15, calendar.get(Calendar.MINUTE))
    }
}
