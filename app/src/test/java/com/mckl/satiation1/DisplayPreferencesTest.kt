package com.mckl.satiation1

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DisplayPreferencesTest {

    @Test
    fun imperialWeightConversion_roundTripsCleanly() {
        val weightKg = 84.0
        val displayPounds = weightKgToDisplayValue(weightKg, UNIT_IMPERIAL)

        assertEquals(185.1879, displayPounds, 0.01)
        assertEquals(weightKg, displayWeightToKg(displayPounds, UNIT_IMPERIAL), 0.01)
    }

    @Test
    fun imperialHeightFormatting_usesFeetAndInches() {
        val formatted = formatHeightForDisplay(180.0, UNIT_IMPERIAL)

        assertEquals("5 ft 11 in", formatted)
    }

    @Test
    fun preferredDateFormatting_changesPattern() {
        val epochMillis = 1782432000000L

        val monthDay = formatDateForPreference(epochMillis, preferredDateFormat = DATE_FORMAT_MONTH_DAY_YEAR)
        val iso = formatDateForPreference(epochMillis, preferredDateFormat = DATE_FORMAT_ISO)

        assertTrue(monthDay.contains("Jun"))
        assertEquals("2026-06-26", iso)
    }
}
