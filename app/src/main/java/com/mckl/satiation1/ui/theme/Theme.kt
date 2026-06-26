package com.mckl.satiation1.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.mckl.satiation1.SatiationGreen
import com.mckl.satiation1.SatiationOrange

private val LightPrimaryGreen = Color(0xFFA1C247)

@Composable
fun Satiation1Theme(
    themePreference: String = "dark",
    followSystemTheme: Boolean = true,
    primaryAccentHex: String = "#BDE064",
    secondaryAccentHex: String = "#FF7D5A",
    content: @Composable () -> Unit
) {
    val darkTheme = when {
        followSystemTheme -> isSystemInDarkTheme()
        themePreference.lowercase() == "light" -> false
        else -> true
    }
    val primaryAccent = parseAccentColor(primaryAccentHex, if (darkTheme) Color(0xFFCDE986) else LightPrimaryGreen)
    val secondaryAccent = parseAccentColor(secondaryAccentHex, SatiationOrange)
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = primaryAccent,
            secondary = secondaryAccent,
            background = Color(0xFF11161B),
            surface = Color(0xFF1A2027),
            surfaceVariant = Color(0xFF252C34),
            onPrimary = Color(0xFF11161B),
            onSecondary = Color(0xFF11161B),
            onBackground = Color(0xFFF3F5F7),
            onSurface = Color(0xFFF3F5F7),
            onSurfaceVariant = Color(0xFFB6C0CA)
        )
    } else {
        lightColorScheme(
            primary = primaryAccent,
            secondary = secondaryAccent,
            background = Color(0xFFF6F3EC),
            surface = Color(0xFFFFFCF5),
            surfaceVariant = Color(0xFFF0E9DC),
            onPrimary = Color(0xFF1C252E),
            onSecondary = Color.White,
            onBackground = Color(0xFF26323D),
            onSurface = Color(0xFF26323D),
            onSurfaceVariant = Color(0xFF6D7782)
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

private fun parseAccentColor(rawHex: String, fallback: Color): Color {
    val normalized = rawHex.trim()
    return runCatching { Color(android.graphics.Color.parseColor(normalized)) }.getOrElse { fallback }
}
