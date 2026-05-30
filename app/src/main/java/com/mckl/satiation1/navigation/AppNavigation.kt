package com.mckl.satiation1.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mckl.satiation1.ui.screens.CameraScreen
import com.mckl.satiation1.ui.screens.EditProfileScreen
import com.mckl.satiation1.ui.screens.GenderScreen
import com.mckl.satiation1.ui.screens.MainContainer
import com.mckl.satiation1.ui.screens.ManualEntryPlaceholderScreen
import com.mckl.satiation1.ui.screens.NameScreen
import com.mckl.satiation1.ui.screens.NutritionDetailScreen
import com.mckl.satiation1.ui.screens.SettingsScreen
import com.mckl.satiation1.ui.screens.SplashScreen
import com.mckl.satiation1.ui.screens.WeightScreen

@Composable
fun SatiationApp() {
    val navController = rememberNavController()

    // 1. Initialize the ViewModel here at the top
    val sharedViewModel: SatiationViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

    // 2. Read the user profile directly from the Room Database
    val userProfile by sharedViewModel.userProfile.collectAsState()

    // 3. If the database is empty (null), they haven't set up yet. Go to Splash.
    //    If it has data, they are a returning user. Go to Main.
    val startDestination = if (userProfile == null) "splash" else "main"

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            enterTransition = { fadeIn(animationSpec = tween(180)) },
            exitTransition = { fadeOut(animationSpec = tween(120)) },
            popEnterTransition = { fadeIn(animationSpec = tween(180)) },
            popExitTransition = { fadeOut(animationSpec = tween(120)) }
        ) {
            // --- ONBOARDING ROUTES ---
            composable("splash") { SplashScreen(navController) }
            // The onboarding screens share one ViewModel for temporary setup state.
            composable("name") { NameScreen(navController, sharedViewModel) }
            composable("weight") { WeightScreen(navController, sharedViewModel) }
            composable("gender") { GenderScreen(navController, sharedViewModel) }

            // --- MAIN APP ROUTES ---
            composable("main") { MainContainer(navController, sharedViewModel) }
            composable("settings") { SettingsScreen(navController, sharedViewModel) }
            composable(
                route = "camera",
                enterTransition = {
                    fadeIn(animationSpec = tween(140)) + slideInVertically(
                        animationSpec = tween(240),
                        initialOffsetY = { it }
                    )
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(140)) + slideOutVertically(
                        animationSpec = tween(320),
                        targetOffsetY = { it }
                    )
                },
                popEnterTransition = {
                    fadeIn(animationSpec = tween(140))
                },
                popExitTransition = {
                    fadeOut(animationSpec = tween(140)) + slideOutVertically(
                        animationSpec = tween(320),
                        targetOffsetY = { it }
                    )
                }
            ) { CameraScreen(navController, sharedViewModel) }
            composable("nutrition") { NutritionDetailScreen(navController, sharedViewModel) }
            composable(
                route = "manual_entry",
                enterTransition = {
                    fadeIn(animationSpec = tween(140)) + slideInVertically(
                        animationSpec = tween(240),
                        initialOffsetY = { it }
                    )
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(100)) + slideOutVertically(
                        animationSpec = tween(200),
                        targetOffsetY = { it }
                    )
                },
                popEnterTransition = {
                    fadeIn(animationSpec = tween(140))
                },
                popExitTransition = {
                    fadeOut(animationSpec = tween(100)) + slideOutVertically(
                        animationSpec = tween(200),
                        targetOffsetY = { it }
                    )
                }
            ) { ManualEntryPlaceholderScreen(navController) }
            composable("edit_profile") { EditProfileScreen(navController, sharedViewModel) }
        }
    }
}
