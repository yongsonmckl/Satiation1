package com.mckl.satiation1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RippleConfiguration
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mckl.satiation1.DisplayPreferences
import com.mckl.satiation1.navigation.SatiationApp
import com.mckl.satiation1.navigation.SatiationViewModel
import com.mckl.satiation1.reminders.ReminderScheduler
import com.mckl.satiation1.ui.theme.Satiation1Theme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
        )

        val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
        ReminderScheduler.ensureNotificationChannels(this)

        setContent {
            val sharedViewModel: SatiationViewModel = viewModel()
            val appSettings by sharedViewModel.appSettings.collectAsState()
            DisplayPreferences.syncFromSettings(appSettings)
            val themePreference = appSettings?.themePreference ?: "dark"
            val followSystemTheme = appSettings?.followSystemTheme ?: true
            val primaryAccentHex = appSettings?.primaryAccentHex ?: "#BDE064"
            val secondaryAccentHex = appSettings?.secondaryAccentHex ?: "#FF7D5A"
            val isDarkTheme = when {
                followSystemTheme -> isSystemInDarkTheme()
                themePreference.lowercase() == "light" -> false
                else -> true
            }

            LaunchedEffect(appSettings) {
                ReminderScheduler.syncAllReminders(this@MainActivity, appSettings)
            }

            Satiation1Theme(
                themePreference = themePreference,
                followSystemTheme = followSystemTheme,
                primaryAccentHex = primaryAccentHex,
                secondaryAccentHex = secondaryAccentHex
            ) {
                val navigationBarColor = if (isDarkTheme) {
                    MaterialTheme.colorScheme.surface.toArgb()
                } else {
                    Color.White.toArgb()
                }
                SideEffect {
                    windowInsetsController.isAppearanceLightStatusBars = !isDarkTheme
                    windowInsetsController.isAppearanceLightNavigationBars = !isDarkTheme
                    window.statusBarColor = Color.Transparent.toArgb()
                    window.navigationBarColor = navigationBarColor
                }

                CompositionLocalProvider(
                    LocalRippleConfiguration provides RippleConfiguration(
                        color = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Surface(color = MaterialTheme.colorScheme.background) {
                        SatiationApp(sharedViewModel = sharedViewModel)
                    }
                }
            }
        }
    }
}
