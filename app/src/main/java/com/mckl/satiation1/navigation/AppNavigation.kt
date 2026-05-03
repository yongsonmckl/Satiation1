package com.mckl.satiation1.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
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

    Box(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            enterTransition = { slideInHorizontally(animationSpec = tween(300), initialOffsetX = { it / 5 }) },
            exitTransition = { slideOutHorizontally(animationSpec = tween(300), targetOffsetX = { -it / 5 }) },
            popEnterTransition = { slideInHorizontally(animationSpec = tween(300), initialOffsetX = { -it / 5 }) },
            popExitTransition = { slideOutHorizontally(animationSpec = tween(300), targetOffsetX = { it / 5 }) }
        ) {
            // --- ONBOARDING ROUTES ---
            composable("splash") { SplashScreen(navController) }
            // Note: We now pass the ViewModel into these so they can hold temp memory!
            composable("name") { NameScreen(navController, sharedViewModel) }
            composable("weight") { WeightScreen(navController, sharedViewModel) }
            composable("gender") { GenderScreen(navController, sharedViewModel) }

            // --- MAIN APP ROUTES ---
            composable("main") { MainContainer(navController, sharedViewModel) }
            composable("settings") { SettingsScreen(navController, sharedViewModel) }
            composable("camera") { CameraScreen(navController, sharedViewModel) }
            composable("nutrition") { NutritionDetailScreen(navController, sharedViewModel) }
            composable("settings") { SettingsScreen(navController, sharedViewModel) }
            composable("edit_profile") { EditProfileScreen(navController, sharedViewModel) }
        }
    }
}