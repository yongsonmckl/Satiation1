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
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mckl.satiation1.navigation.SatiationApp
import com.mckl.satiation1.navigation.SatiationViewModel
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
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())

        setContent {
            val sharedViewModel: SatiationViewModel = viewModel()
            val appSettings by sharedViewModel.appSettings.collectAsState()
            val themePreference = appSettings?.themePreference ?: "system"
            val isDarkTheme = when (themePreference.lowercase()) {
                "dark" -> true
                "light" -> false
                else -> isSystemInDarkTheme()
            }

            Satiation1Theme(themePreference = themePreference) {
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
