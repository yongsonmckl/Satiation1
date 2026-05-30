package com.mckl.satiation1.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mckl.satiation1.ui.screens.CameraScreen
import com.mckl.satiation1.ui.screens.EditApiKeyScreen
import com.mckl.satiation1.ui.screens.EditHeightScreen
import com.mckl.satiation1.ui.screens.EditNameScreen
import com.mckl.satiation1.ui.screens.EditNutrientsScreen
import com.mckl.satiation1.ui.screens.EditProfileScreen
import com.mckl.satiation1.ui.screens.EditPronounsScreen
import com.mckl.satiation1.ui.screens.EditTargetsScreen
import com.mckl.satiation1.ui.screens.EditWeightScreen
import com.mckl.satiation1.ui.screens.AppearanceScreen
import com.mckl.satiation1.ui.screens.FoodTypesScreen
import com.mckl.satiation1.ui.screens.MainContainer
import com.mckl.satiation1.ui.screens.ManualEntryPlaceholderScreen
import com.mckl.satiation1.ui.screens.NameScreen
import com.mckl.satiation1.ui.screens.NutritionDetailScreen
import com.mckl.satiation1.ui.screens.SplashScreen
import com.mckl.satiation1.ui.screens.WeightScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun SatiationApp(sharedViewModel: SatiationViewModel) {
    val navController = rememberNavController()
    val userProfile by sharedViewModel.userProfile.collectAsState()
    val isUserProfileLoaded by sharedViewModel.isUserProfileLoaded.collectAsState()

    if (!isUserProfileLoaded) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

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
            composable("name") { NameScreen(navController, sharedViewModel) }
            composable("weight") { WeightScreen(navController, sharedViewModel) }

            // --- MAIN APP ROUTES ---
            composable("main") { MainContainer(navController, sharedViewModel) }
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
            ) { ManualEntryPlaceholderScreen(navController, sharedViewModel) }
            composable("edit_profile") { EditProfileScreen(navController, sharedViewModel) }
            composable("edit_name") { EditNameScreen(navController, sharedViewModel) }
            composable("edit_pronouns") { EditPronounsScreen(navController, sharedViewModel) }
            composable("edit_height") { EditHeightScreen(navController, sharedViewModel) }
            composable("edit_weight") { EditWeightScreen(navController, sharedViewModel) }
            composable("edit_nutrients") { EditNutrientsScreen(navController, sharedViewModel) }
            composable("edit_targets") { EditTargetsScreen(navController, sharedViewModel) }
            composable("food_types") { FoodTypesScreen(navController, sharedViewModel) }
            composable("appearance") { AppearanceScreen(navController, sharedViewModel) }
            composable("edit_api_key") { EditApiKeyScreen(navController, sharedViewModel) }
        }
    }
}
