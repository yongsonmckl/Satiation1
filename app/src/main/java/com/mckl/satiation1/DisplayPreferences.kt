package com.mckl.satiation1

import com.mckl.satiation1.database.AppSettings
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

const val UNIT_METRIC = "metric"
const val UNIT_IMPERIAL = "imperial"

const val DATE_FORMAT_DAY_MONTH_YEAR = "day_month_year"
const val DATE_FORMAT_MONTH_DAY_YEAR = "month_day_year"
const val DATE_FORMAT_ISO = "iso"

object DisplayPreferences {
    @Volatile
    var preferredUnits: String = UNIT_METRIC

    @Volatile
    var preferredDateFormat: String = DATE_FORMAT_DAY_MONTH_YEAR

    fun syncFromSettings(settings: AppSettings?) {
        preferredUnits = settings?.preferredUnits ?: UNIT_METRIC
        preferredDateFormat = settings?.preferredDateFormat ?: DATE_FORMAT_DAY_MONTH_YEAR
    }
}

fun usesImperialUnits(preferredUnits: String = DisplayPreferences.preferredUnits): Boolean {
    return preferredUnits == UNIT_IMPERIAL
}

fun weightKgToDisplayValue(weightKg: Double, preferredUnits: String = DisplayPreferences.preferredUnits): Double {
    return if (usesImperialUnits(preferredUnits)) weightKg * 2.20462 else weightKg
}

fun displayWeightToKg(weightDisplay: Double, preferredUnits: String = DisplayPreferences.preferredUnits): Double {
    return if (usesImperialUnits(preferredUnits)) weightDisplay / 2.20462 else weightDisplay
}

fun heightCmToDisplayValue(heightCm: Double, preferredUnits: String = DisplayPreferences.preferredUnits): Double {
    return if (usesImperialUnits(preferredUnits)) heightCm / 2.54 else heightCm
}

fun displayHeightToCm(heightDisplay: Double, preferredUnits: String = DisplayPreferences.preferredUnits): Double {
    return if (usesImperialUnits(preferredUnits)) heightDisplay * 2.54 else heightDisplay
}

fun formatWeightForDisplay(weightKg: Double, preferredUnits: String = DisplayPreferences.preferredUnits): String {
    val displayValue = weightKgToDisplayValue(weightKg, preferredUnits)
    val rounded = if (displayValue % 1.0 == 0.0) {
        displayValue.roundToInt().toString()
    } else {
        "%.1f".format(Locale.US, displayValue)
    }
    return if (usesImperialUnits(preferredUnits)) "$rounded lb" else "$rounded kg"
}

fun formatWholeWeightForDisplay(weightValue: Int, preferredUnits: String = DisplayPreferences.preferredUnits): String {
    return if (usesImperialUnits(preferredUnits)) "$weightValue lb" else "$weightValue kg"
}

fun weightInputLabel(preferredUnits: String = DisplayPreferences.preferredUnits): String {
    return if (usesImperialUnits(preferredUnits)) "Weight (lb)" else "Weight (kg)"
}

fun formatHeightForDisplay(heightCm: Double, preferredUnits: String = DisplayPreferences.preferredUnits): String {
    if (!usesImperialUnits(preferredUnits)) {
        val rounded = if (heightCm % 1.0 == 0.0) {
            heightCm.roundToInt().toString()
        } else {
            "%.1f".format(Locale.US, heightCm)
        }
        return "$rounded cm"
    }

    return formatHeightInchesForDisplay(heightCmToDisplayValue(heightCm, preferredUnits).roundToInt())
}

fun formatWholeHeightForDisplay(heightValue: Int, preferredUnits: String = DisplayPreferences.preferredUnits): String {
    return if (usesImperialUnits(preferredUnits)) {
        formatHeightInchesForDisplay(heightValue)
    } else {
        "$heightValue cm"
    }
}

fun heightInputLabel(preferredUnits: String = DisplayPreferences.preferredUnits): String {
    return if (usesImperialUnits(preferredUnits)) "Height (in)" else "Height (cm)"
}

private fun formatHeightInchesForDisplay(totalInches: Int): String {
    val feet = totalInches / 12
    val inches = totalInches % 12
    return "$feet ft $inches in"
}

fun formatDateForPreference(
    epochMillis: Long,
    withWeekday: Boolean = false,
    preferredDateFormat: String = DisplayPreferences.preferredDateFormat
): String {
    val pattern = when (preferredDateFormat) {
        DATE_FORMAT_MONTH_DAY_YEAR -> if (withWeekday) "EEEE, MMM d yyyy" else "MMM d yyyy"
        DATE_FORMAT_ISO -> if (withWeekday) "EEEE, yyyy-MM-dd" else "yyyy-MM-dd"
        else -> if (withWeekday) "EEEE, d MMM yyyy" else "d MMM yyyy"
    }
    return SimpleDateFormat(pattern, Locale.US).format(Date(epochMillis))
}

