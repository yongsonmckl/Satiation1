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

private val LightColorScheme = lightColorScheme(
    primary = LightPrimaryGreen,
    secondary = SatiationOrange,
    background = Color(0xFFF6F3EC),
    surface = Color(0xFFFFFCF5),
    surfaceVariant = Color(0xFFF0E9DC),
    onPrimary = Color(0xFF1C252E),
    onSecondary = Color.White,
    onBackground = Color(0xFF26323D),
    onSurface = Color(0xFF26323D),
    onSurfaceVariant = Color(0xFF6D7782)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFCDE986),
    secondary = Color(0xFFFFA081),
    background = Color(0xFF11161B),
    surface = Color(0xFF1A2027),
    surfaceVariant = Color(0xFF252C34),
    onPrimary = Color(0xFF11161B),
    onSecondary = Color(0xFF11161B),
    onBackground = Color(0xFFF3F5F7),
    onSurface = Color(0xFFF3F5F7),
    onSurfaceVariant = Color(0xFFB6C0CA)
)

@Composable
fun Satiation1Theme(
    themePreference: String = "dark",
    content: @Composable () -> Unit
) {
    val darkTheme = when (themePreference.lowercase()) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme()
    }

    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
