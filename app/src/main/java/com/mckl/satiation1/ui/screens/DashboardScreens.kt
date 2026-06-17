package com.mckl.satiation1.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mckl.satiation1.database.AppSettings
import com.mckl.satiation1.database.MealItem
import com.mckl.satiation1.database.MealLog
import com.mckl.satiation1.database.MealWithItems
import com.mckl.satiation1.database.PresetFood
import com.mckl.satiation1.DarkText
import com.mckl.satiation1.LightText
import com.mckl.satiation1.SatiationGreen
import com.mckl.satiation1.SatiationOrange
import com.mckl.satiation1.navigation.SatiationViewModel
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.entryModelOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun MainContainer(rootNavController: NavController, viewModel: SatiationViewModel) {
    val (showAddMenu, setShowAddMenu) = remember { mutableStateOf(false) }
    val (isAddMenuMounted, setIsAddMenuMounted) = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val userProfile by viewModel.userProfile.collectAsState()
    val currentTab = viewModel.currentMainTab
    val colorScheme = MaterialTheme.colorScheme
    val addMenuScrimAlpha by animateFloatAsState(
        targetValue = if (showAddMenu) 0.4f else 0f,
        animationSpec = tween(160),
        label = "AddMenuScrimAlpha"
    )
    val addMenuOffset: Dp by animateDpAsState(
        targetValue = if (showAddMenu) 0.dp else 320.dp,
        animationSpec = tween(durationMillis = if (showAddMenu) 280 else 220),
        label = "AddMenuOffset"
    )

    LaunchedEffect(showAddMenu) {
        if (!showAddMenu && isAddMenuMounted) {
            delay(220)
            setIsAddMenuMounted(false)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Satiation",
                            fontWeight = FontWeight.Bold,
                            color = SatiationGreen
                        )
                        Text(
                            "Hello, ${userProfile?.name?.takeIf { it.isNotBlank() } ?: "there"}!",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onSurface
                        )
                    }
                }
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorScheme.surface)
                    .padding(bottom = 15.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(30.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.currentMainTab = "home" }) {
                        Icon(
                            Icons.Default.Home,
                            contentDescription = "Home",
                            tint = if (currentTab == "home") DarkText else LightText
                        )
                    }
                    IconButton(onClick = { viewModel.currentMainTab = "checkmark" }) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Tasks",
                            tint = if (currentTab == "checkmark") DarkText else LightText
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(SatiationGreen)
                            .clickable {
                                setIsAddMenuMounted(true)
                                setShowAddMenu(true)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Open Menu",
                            tint = DarkText,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    IconButton(onClick = { viewModel.currentMainTab = "progress" }) {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = "Progress",
                            tint = if (currentTab == "progress") DarkText else LightText
                        )
                    }
                    IconButton(onClick = { viewModel.currentMainTab = "profile" }) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = if (currentTab == "profile") DarkText else LightText
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = {
                    fadeIn(animationSpec = tween(180)) togetherWith
                        fadeOut(animationSpec = tween(120))
                },
                label = "Tab Slide"
            ) { tab ->
                when (tab) {
                    "home" -> HomeScreen(
                        viewModel = viewModel,
                        onNavigateToCheckmark = { viewModel.currentMainTab = "checkmark" },
                        onOpenMenu = {
                            setIsAddMenuMounted(true)
                            setShowAddMenu(true)
                        }
                    )
                    "checkmark" -> CheckmarkScreen(viewModel)
                    "progress" -> ProgressScreen(viewModel)
                    "profile" -> SettingsHubScreen(rootNavController, viewModel)
                }
            }
        }
    }

    if (isAddMenuMounted || showAddMenu || addMenuScrimAlpha > 0f) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = addMenuScrimAlpha))
                .clickable { setShowAddMenu(false) },
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = addMenuOffset)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(colorScheme.surface)
                    .padding(24.dp)
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    "Add Entry",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                ListItem(
                    headlineContent = { Text("Scan Food (Camera)") },
                    modifier = Modifier.clickable {
                        coroutineScope.launch {
                            setShowAddMenu(false)
                            delay(240)
                            rootNavController.navigate("camera")
                        }
                    }
                )
                ListItem(
                    headlineContent = { Text("Manual Food Selection") },
                    modifier = Modifier.clickable {
                        coroutineScope.launch {
                            setShowAddMenu(false)
                            delay(240)
                            rootNavController.navigate("manual_entry")
                        }
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { setShowAddMenu(false) },
                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text("Back", fontWeight = FontWeight.Bold, color = colorScheme.onSurface)
                }
            }
        }
    }
}

@Composable
fun HomeScreen(
    viewModel: SatiationViewModel,
    onNavigateToCheckmark: () -> Unit,
    onOpenMenu: () -> Unit
) {
    val todayRange = remember { currentDayRangeMillis() }
    val todayMeals by viewModel.getMealsForRange(todayRange.first, todayRange.second).collectAsState(initial = emptyList())
    val todayMacros by viewModel.getDailyMacroTotals(todayRange.first, todayRange.second).collectAsState(initial = emptyList())
    val todayTotal = todayMacros.firstOrNull()
    val colorScheme = MaterialTheme.colorScheme

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
        contentPadding = PaddingValues(top = 24.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                "Today's Summary",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onBackground
            )
        }
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToCheckmark() },
                colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        formatCalories(todayTotal?.calories ?: 0.0),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = SatiationOrange
                    )
                    Text("Consumed Today", color = colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MacroSummaryPill("Protein", todayTotal?.proteinGrams ?: 0.0)
                        MacroSummaryPill("Carbs", todayTotal?.carbsGrams ?: 0.0)
                        MacroSummaryPill("Fats", todayTotal?.fatsGrams ?: 0.0)
                    }
                }
            }
        }
        item {
            Text(
                "Meals Eaten",
                fontWeight = FontWeight.Bold,
                color = colorScheme.onBackground,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        if (todayMeals.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
                        .border(2.dp, colorScheme.outlineVariant, RoundedCornerShape(16.dp))
                        .clickable { onOpenMenu() },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Press to log your first meal.", color = colorScheme.onSurfaceVariant, fontSize = 14.sp)
                    }
                }
            }
        } else {
            items(todayMeals) { meal ->
                MealCard(meal)
            }
        }
    }
}

@Composable
fun SettingsHubScreen(rootNavController: NavController, viewModel: SatiationViewModel) {
    val userProfile by viewModel.userProfile.collectAsState()
    val currentBmi by viewModel.currentBmi.collectAsState()
    val profile = userProfile
    val colorScheme = MaterialTheme.colorScheme
    val listState = rememberLazyListState()
    var animateCardIn by remember { mutableStateOf(false) }
    val cardScale by animateFloatAsState(
        targetValue = if (animateCardIn) 1f else 0.96f,
        animationSpec = tween(260),
        label = "SettingsCardScale"
    )
    val cardAlpha by animateFloatAsState(
        targetValue = if (animateCardIn) 1f else 0f,
        animationSpec = tween(220),
        label = "SettingsCardAlpha"
    )
    val cardOffset by animateDpAsState(
        targetValue = if (animateCardIn) 0.dp else 18.dp,
        animationSpec = tween(260),
        label = "SettingsCardOffset"
    )
    val profileCardColor = settingsPanelColor()

    LaunchedEffect(Unit) {
        animateCardIn = true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
            contentPadding = PaddingValues(top = 24.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Text("Settings", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = colorScheme.onBackground)
            }
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = cardOffset)
                        .graphicsLayer(
                            alpha = cardAlpha,
                            scaleX = cardScale,
                            scaleY = cardScale
                        )
                        .border(1.dp, colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = profileCardColor)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Profile", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(8.dp))
                        Column {
                            if (profile == null) {
                                Text("No profile found.", color = colorScheme.onSurfaceVariant)
                            } else {
                                ProfileDetailRow("Name:", profile.name)
                                ProfileDetailRow("Pronouns:", profile.pronouns)
                                ProfileDetailRow("Starting Weight:", formatWeightKg(profile.startWeightKg))
                                ProfileDetailRow("Current Weight:", formatWeightKg(profile.currentWeightKg))
                                profile.heightCm?.let { heightCm ->
                                    ProfileDetailRow("Height:", formatHeightCm(heightCm))
                                }
                                currentBmi?.let { bmi ->
                                    ProfileDetailRow("BMI:", "%.1f".format(Locale.US, bmi))
                                }
                            }
                        }
                    }
                }
            }
            item {
                SettingsListEntry(
                    title = "Edit Profile",
                    description = "Choose name, pronouns, height, or weight",
                    containerColor = profileCardColor,
                    onClick = {
                        viewModel.currentMainTab = "profile"
                        rootNavController.navigate("edit_profile")
                    }
                )
            }
            item {
                SettingsListEntry(
                    title = "Edit Nutrients",
                    description = "Edit your Daily Targets and Food Types",
                    containerColor = profileCardColor,
                    onClick = { rootNavController.navigate("edit_nutrients") }
                )
            }
            item {
                SettingsListEntry(
                    title = "Gemini API Key",
                    description = "Store or replace the local scan key",
                    containerColor = profileCardColor,
                    onClick = { rootNavController.navigate("edit_api_key") }
                )
            }
            item {
                SettingsListEntry(
                    title = "Appearance",
                    description = "Choose system, light, or dark mode",
                    containerColor = profileCardColor,
                    onClick = { rootNavController.navigate("appearance") }
                )
            }
        }
        PassiveScrollbar(listState = listState)
    }
}

@Composable
fun CheckmarkScreen(viewModel: SatiationViewModel) {
    val todayRange = remember { currentDayRangeMillis() }
    val todayMacros by viewModel.getDailyMacroTotals(todayRange.first, todayRange.second).collectAsState(initial = emptyList())
    val todayMeals by viewModel.getMealsForRange(todayRange.first, todayRange.second).collectAsState(initial = emptyList())
    val appSettings by viewModel.appSettings.collectAsState()
    val todayTotal = todayMacros.firstOrNull()
    val settings = appSettings ?: AppSettings()
    val colorScheme = MaterialTheme.colorScheme
    val listState = rememberLazyListState()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
            contentPadding = PaddingValues(top = 24.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    "Daily Targets",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onBackground
                )
            }
            item {
                MacroBar("Calories", (todayTotal?.calories ?: 0.0).toFloat(), settings.calorieTarget.toFloat(), SatiationOrange)
            }
            item {
                MacroBar("Protein", (todayTotal?.proteinGrams ?: 0.0).toFloat(), settings.proteinTargetGrams.toFloat(), SatiationGreen)
            }
            item {
                MacroBar("Fats", (todayTotal?.fatsGrams ?: 0.0).toFloat(), settings.fatsTargetGrams.toFloat(), DarkFatsColor)
            }
            item {
                MacroBar("Carbs", (todayTotal?.carbsGrams ?: 0.0).toFloat(), settings.carbsTargetGrams.toFloat(), Color(0xFFFFD56F))
            }
            item {
                Text("Meals Eaten", fontWeight = FontWeight.Bold, color = colorScheme.onBackground, modifier = Modifier.padding(top = 8.dp))
            }
            if (todayMeals.isEmpty()) {
                item {
                    EmptyMealsCard()
                }
            } else {
                items(todayMeals) { meal ->
                    MealCard(meal)
                }
            }
        }
        PassiveScrollbar(listState = listState)
    }
}

@Composable
fun ProgressScreen(viewModel: SatiationViewModel) {
    val lastThirtyDays = remember { trailingDayRangeMillis(30) }
    val macroHistory by viewModel.getDailyMacroTotals(lastThirtyDays.first, lastThirtyDays.second).collectAsState(initial = emptyList())
    val mealSummaries by viewModel.getDailyMealSummaries(lastThirtyDays.first, lastThirtyDays.second).collectAsState(initial = emptyList())
    val weightHistory by viewModel.weightHistory.collectAsState()
    val currentBmi by viewModel.currentBmi.collectAsState()

    val calorieEntries = remember(macroHistory) {
        macroHistory.map { it.calories.toFloat() }.ifEmpty { listOf(0f) }
    }
    val weightEntries = remember(weightHistory) {
        weightHistory.map { it.weightKg.toFloat() }.ifEmpty { listOf(0f) }
    }
    val averageCalories = remember(mealSummaries) {
        mealSummaries.map { it.calories }.average().takeIf { !it.isNaN() } ?: 0.0
    }
    val totalMeals = remember(mealSummaries) {
        mealSummaries.sumOf { it.mealCount }
    }
    val latestWeight = weightHistory.lastOrNull()?.weightKg
    val colorScheme = MaterialTheme.colorScheme

    val listState = rememberLazyListState()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize().padding(24.dp),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Progress", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = colorScheme.onBackground)
            }
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Calorie Trend", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))
                        Chart(
                            chart = lineChart(),
                            model = entryModelOf(*calorieEntries.toTypedArray()),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                    }
                }
            }
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Weight Trend", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))
                        if (weightHistory.isEmpty()) {
                            Text("No weight entries yet.", color = colorScheme.onSurfaceVariant)
                        } else {
                            Chart(
                                chart = lineChart(),
                                model = entryModelOf(*weightEntries.toTypedArray()),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            )
                        }
                    }
                }
            }
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Trends", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))
                        TrendRow("Tracked Days", mealSummaries.size.toString())
                        TrendRow("Meals Logged", totalMeals.toString())
                        TrendRow("Avg Calories / Day", formatCalories(averageCalories))
                        TrendRow("Latest Weight", latestWeight?.let(::formatWeightKg) ?: "No entries")
                        TrendRow("Current BMI", currentBmi?.let { "%.1f".format(Locale.US, it) } ?: "Unavailable")
                    }
                }
            }
        }
        PassiveScrollbar(listState = listState)
    }
}

@Composable
fun EditProfileScreen(navController: NavController, viewModel: SatiationViewModel) {
    LaunchedEffect(Unit) {
        viewModel.currentMainTab = "profile"
    }
    val panelColor = settingsPanelColor()
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).statusBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ScreenHeader(
            title = "Edit Profile",
            onBack = { navController.popBackStack() }
        )
        SettingsListEntry(
            title = "Change Name",
            description = "Edit your display name",
            containerColor = panelColor,
            onClick = { navController.navigate("edit_name") }
        )
        SettingsListEntry(
            title = "Change Pronouns",
            description = "Edit how the app refers to you",
            containerColor = panelColor,
            onClick = { navController.navigate("edit_pronouns") }
        )
        SettingsListEntry(
            title = "Change Height",
            description = "Use the same integer slider style as onboarding",
            containerColor = panelColor,
            onClick = { navController.navigate("edit_height") }
        )
        SettingsListEntry(
            title = "Change Weight",
            description = "Use the same integer slider style as onboarding",
            containerColor = panelColor,
            onClick = { navController.navigate("edit_weight") }
        )
    }
}

@Composable
fun EditNutrientsScreen(navController: NavController, viewModel: SatiationViewModel) {
    LaunchedEffect(Unit) {
        viewModel.currentMainTab = "profile"
    }
    val panelColor = settingsPanelColor()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).statusBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ScreenHeader(title = "Edit Nutrients", onBack = { navController.popBackStack() })
        SettingsListEntry(
            title = "Change Targets",
            description = "Adjust your calories, protein, carbs, and fats",
            containerColor = panelColor,
            onClick = { navController.navigate("edit_targets") }
        )
        SettingsListEntry(
            title = "Preset Foods",
            description = "Create and edit saved foods for quick manual logging",
            containerColor = panelColor,
            onClick = { navController.navigate("food_types") }
        )
    }
}

@Composable
fun EditTargetsScreen(navController: NavController, viewModel: SatiationViewModel) {
    val appSettings by viewModel.appSettings.collectAsState()
    var calories by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var fats by remember { mutableStateOf("") }

    LaunchedEffect(appSettings) {
        val settings = appSettings ?: AppSettings()
        calories = settings.calorieTarget.toInt().toString()
        protein = settings.proteinTargetGrams.toInt().toString()
        carbs = settings.carbsTargetGrams.toInt().toString()
        fats = settings.fatsTargetGrams.toInt().toString()
    }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp).statusBarsPadding()) {
        ScreenHeader(title = "Change Targets", onBack = { navController.popBackStack() })
        Text("Daily Targets", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = calories,
            onValueChange = { calories = it.filter(Char::isDigit) },
            label = { Text("Calories") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = protein,
            onValueChange = { protein = it.filter(Char::isDigit) },
            label = { Text("Protein (g)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = carbs,
            onValueChange = { carbs = it.filter(Char::isDigit) },
            label = { Text("Carbs (g)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = fats,
            onValueChange = { fats = it.filter(Char::isDigit) },
            label = { Text("Fats (g)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                val currentSettings = appSettings ?: AppSettings()
                viewModel.saveSettings(
                    currentSettings.copy(
                        calorieTarget = calories.toDoubleOrNull() ?: currentSettings.calorieTarget,
                        proteinTargetGrams = protein.toDoubleOrNull() ?: currentSettings.proteinTargetGrams,
                        carbsTargetGrams = carbs.toDoubleOrNull() ?: currentSettings.carbsTargetGrams,
                        fatsTargetGrams = fats.toDoubleOrNull() ?: currentSettings.fatsTargetGrams
                    )
                )
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Save Targets")
        }
    }
}

@Composable
fun FoodTypesScreen(navController: NavController, viewModel: SatiationViewModel) {
    val presetFoods by viewModel.presetFoods.collectAsState()
    val listState = rememberLazyListState()
    var editingPreset by remember { mutableStateOf<PresetFood?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .statusBarsPadding(),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                ScreenHeader(title = "Preset Foods", onBack = { navController.popBackStack() })
            }
            item {
                Text(
                    "Saved foods from here can be reused inside Preset Foods / Manual Entry.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            item {
                PresetFoodEditorCard(
                    title = if (editingPreset == null) "Create Preset Food" else "Edit Preset Food",
                    submitLabel = if (editingPreset == null) "Save Preset" else "Update Preset",
                    initialName = editingPreset?.name.orEmpty(),
                    initialCategory = editingPreset?.category.orEmpty(),
                    initialCalories = editingPreset?.calories?.let(::formatNumberInput).orEmpty(),
                    initialProtein = editingPreset?.proteinGrams?.let(::formatNumberInput).orEmpty(),
                    initialCarbs = editingPreset?.carbsGrams?.let(::formatNumberInput).orEmpty(),
                    initialFats = editingPreset?.fatsGrams?.let(::formatNumberInput).orEmpty(),
                    initialNotes = editingPreset?.notes.orEmpty(),
                    secondaryActionLabel = if (editingPreset == null) null else "Cancel Edit",
                    onSecondaryAction = { editingPreset = null },
                    onSubmit = { name, category, calories, protein, carbs, fats, notes ->
                        viewModel.savePresetFood(
                            presetFoodId = editingPreset?.presetFoodId ?: 0,
                            name = name,
                            category = category,
                            calories = calories,
                            proteinGrams = protein,
                            carbsGrams = carbs,
                            fatsGrams = fats,
                            notes = notes
                        )
                        editingPreset = null
                    }
                )
            }
            if (presetFoods.isEmpty()) {
                item {
                    EmptyStateCard("No preset foods saved yet.")
                }
            } else {
                items(presetFoods) { preset ->
                    PresetFoodCard(
                        preset = preset,
                        showUseButton = false,
                        onUse = {},
                        onEdit = { editingPreset = preset },
                        onDelete = {
                            viewModel.deletePresetFood(preset.presetFoodId)
                            if (editingPreset?.presetFoodId == preset.presetFoodId) {
                                editingPreset = null
                            }
                        }
                    )
                }
            }
            if (editingPreset != null) {
                item {
                    PresetFoodEditorCard(
                        title = "Edit Preset Food",
                        submitLabel = "Update Preset",
                        initialName = editingPreset?.name.orEmpty(),
                        initialCategory = editingPreset?.category.orEmpty(),
                        initialCalories = editingPreset?.calories?.let(::formatNumberInput).orEmpty(),
                        initialProtein = editingPreset?.proteinGrams?.let(::formatNumberInput).orEmpty(),
                        initialCarbs = editingPreset?.carbsGrams?.let(::formatNumberInput).orEmpty(),
                        initialFats = editingPreset?.fatsGrams?.let(::formatNumberInput).orEmpty(),
                        initialNotes = editingPreset?.notes.orEmpty(),
                        secondaryActionLabel = "Cancel Edit",
                        onSecondaryAction = { editingPreset = null },
                        onSubmit = { name, savedCategory, savedCalories, savedProtein, savedCarbs, savedFats, savedNotes ->
                            viewModel.savePresetFood(
                                presetFoodId = editingPreset?.presetFoodId ?: 0,
                                name = name,
                                category = savedCategory,
                                calories = savedCalories,
                                proteinGrams = savedProtein,
                                carbsGrams = savedCarbs,
                                fatsGrams = savedFats,
                                notes = savedNotes
                            )
                            editingPreset = null
                        }
                    )
                }
            }
        }
        PassiveScrollbar(listState = listState)
    }
}

@Composable
fun AppearanceScreen(navController: NavController, viewModel: SatiationViewModel) {
    val appSettings by viewModel.appSettings.collectAsState()
    val currentPreference = appSettings?.themePreference ?: "light"
    val panelColor = settingsPanelColor()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).statusBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ScreenHeader(title = "Appearance", onBack = { navController.popBackStack() })
        SettingsListEntry(
            title = "Light Mode",
            description = if (currentPreference == "light") "Selected" else "Always use the light theme",
            containerColor = panelColor,
            onClick = {
                val currentSettings = appSettings ?: AppSettings()
                viewModel.saveSettings(currentSettings.copy(themePreference = "light"))
            }
        )
        SettingsListEntry(
            title = "Dark Mode",
            description = if (currentPreference == "dark") "Selected" else "Always use the dark theme",
            containerColor = panelColor,
            onClick = {
                val currentSettings = appSettings ?: AppSettings()
                viewModel.saveSettings(currentSettings.copy(themePreference = "dark"))
            }
        )
    }
}

@Composable
fun EditNameScreen(navController: NavController, viewModel: SatiationViewModel) {
    val userProfile by viewModel.userProfile.collectAsState()
    var editValue by remember { mutableStateOf(userProfile?.name.orEmpty()) }

    LaunchedEffect(userProfile?.name) {
        editValue = userProfile?.name.orEmpty()
    }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp).statusBarsPadding()) {
        ScreenHeader(title = "Change Name", onBack = { navController.popBackStack() })
        OutlinedTextField(
            value = editValue,
            onValueChange = { editValue = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                userProfile?.let { profile ->
                    viewModel.saveProfile(
                        name = editValue,
                        startWeightKg = profile.startWeightKg,
                        currentWeightKg = profile.currentWeightKg,
                        pronouns = profile.pronouns,
                        heightCm = profile.heightCm
                    )
                    navController.popBackStack()
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Save Name")
        }
    }
}

@Composable
fun EditPronounsScreen(navController: NavController, viewModel: SatiationViewModel) {
    val userProfile by viewModel.userProfile.collectAsState()
    var editValue by remember { mutableStateOf(userProfile?.pronouns.orEmpty()) }

    LaunchedEffect(userProfile?.pronouns) {
        editValue = userProfile?.pronouns.orEmpty()
    }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp).statusBarsPadding()) {
        ScreenHeader(title = "Change Pronouns", onBack = { navController.popBackStack() })
        OutlinedTextField(
            value = editValue,
            onValueChange = { editValue = it },
            label = { Text("Pronouns") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                userProfile?.let { profile ->
                    viewModel.saveProfile(
                        name = profile.name,
                        startWeightKg = profile.startWeightKg,
                        currentWeightKg = profile.currentWeightKg,
                        pronouns = editValue,
                        heightCm = profile.heightCm
                    )
                    navController.popBackStack()
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Save Pronouns")
        }
    }
}

@Composable
fun EditHeightScreen(navController: NavController, viewModel: SatiationViewModel) {
    val userProfile by viewModel.userProfile.collectAsState()
    var height by remember { mutableFloatStateOf(userProfile?.heightCm?.toFloat() ?: 170.0f) }
    var isDialogOpen by remember { mutableStateOf(false) }
    val heightRange = minOf(120f, height)..maxOf(220f, height)

    LaunchedEffect(userProfile?.heightCm) {
        height = userProfile?.heightCm?.toFloat() ?: 170.0f
    }

    MetricSliderScreen(
        title = "Change Height",
        valueText = formatWholeHeightCm(height.toInt()),
        backgroundColor = SatiationGreen,
        onBack = { navController.popBackStack() },
        onSaveLabel = "Save Height",
        onValueClick = { isDialogOpen = true },
        slider = {
            Slider(
                value = height,
                onValueChange = { height = it.toInt().toFloat() },
                valueRange = heightRange,
                colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Color.White)
            )
        },
        onSave = {
            userProfile?.let { profile ->
                viewModel.saveProfile(
                    name = profile.name,
                    startWeightKg = profile.startWeightKg,
                    currentWeightKg = profile.currentWeightKg,
                    pronouns = profile.pronouns,
                    heightCm = height.toDouble()
                )
                navController.popBackStack()
            }
        }
    )

    if (isDialogOpen) {
        IntegerValueDialog(
            title = "Enter Height",
            label = "Height (cm)",
            initialValue = height.toInt(),
            validRange = 80..280,
            onDismiss = { isDialogOpen = false },
            onConfirm = {
                height = it.toFloat()
                isDialogOpen = false
            }
        )
    }
}

@Composable
fun EditWeightScreen(navController: NavController, viewModel: SatiationViewModel) {
    val userProfile by viewModel.userProfile.collectAsState()
    var weight by remember { mutableFloatStateOf(userProfile?.currentWeightKg?.toFloat() ?: 67.0f) }
    var isDialogOpen by remember { mutableStateOf(false) }
    val weightRange = minOf(40f, weight)..maxOf(150f, weight)

    LaunchedEffect(userProfile?.currentWeightKg) {
        weight = userProfile?.currentWeightKg?.toFloat() ?: 67.0f
    }

    MetricSliderScreen(
        title = "Change Weight",
        valueText = formatWholeWeightKg(weight.toInt()),
        backgroundColor = SatiationOrange,
        onBack = { navController.popBackStack() },
        onSaveLabel = "Save Weight",
        onValueClick = { isDialogOpen = true },
        slider = {
            Slider(
                value = weight,
                onValueChange = { weight = it.toInt().toFloat() },
                valueRange = weightRange,
                colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Color.White)
            )
        },
        onSave = {
            viewModel.logWeight(weight.toDouble())
            navController.popBackStack()
        }
    )

    if (isDialogOpen) {
        IntegerValueDialog(
            title = "Enter Weight",
            label = "Weight (kg)",
            initialValue = weight.toInt(),
            validRange = 20..400,
            onDismiss = { isDialogOpen = false },
            onConfirm = {
                weight = it.toFloat()
                isDialogOpen = false
            }
        )
    }
}

@Composable
fun EditApiKeyScreen(navController: NavController, viewModel: SatiationViewModel) {
    val appSettings by viewModel.appSettings.collectAsState()
    var apiKeyDraft by remember { mutableStateOf(appSettings?.geminiApiKey.orEmpty()) }
    val colorScheme = MaterialTheme.colorScheme

    LaunchedEffect(appSettings?.geminiApiKey) {
        apiKeyDraft = appSettings?.geminiApiKey.orEmpty()
    }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp).statusBarsPadding()) {
        ScreenHeader(title = "Gemini API Key", onBack = { navController.popBackStack() })
        Text("Stored locally on this device only.", color = colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = apiKeyDraft,
            onValueChange = { apiKeyDraft = it },
            label = { Text("API Key") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                val currentSettings = appSettings ?: AppSettings()
                viewModel.saveSettings(
                    currentSettings.copy(geminiApiKey = apiKeyDraft.trim().ifEmpty { null })
                )
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Save API Key")
        }
    }
}

@Composable
private fun MetricSliderScreen(
    title: String,
    valueText: String,
    backgroundColor: Color,
    onBack: () -> Unit,
    onSaveLabel: String,
    onValueClick: () -> Unit,
    slider: @Composable () -> Unit,
    onSave: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(24.dp)
            .statusBarsPadding()
    ) {
        ScreenHeader(title = title, onBack = onBack, lightContent = true)
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            valueText,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable { onValueClick() }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Tap the number to type your own value.",
            color = Color.White.copy(alpha = 0.9f),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))
        slider()
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = onSave,
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text(onSaveLabel, color = backgroundColor)
        }
    }
}

@Composable
private fun ScreenHeader(
    title: String,
    onBack: () -> Unit,
    lightContent: Boolean = false
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 24.dp)) {
        IconButton(onClick = onBack, modifier = Modifier.offset(x = (-12).dp)) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = if (lightContent) Color.White else MaterialTheme.colorScheme.onBackground
            )
        }
        Text(
            title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = if (lightContent) Color.White else MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun IntegerValueDialog(
    title: String,
    label: String,
    initialValue: Int,
    validRange: IntRange,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var draft by remember(initialValue) { mutableStateOf(initialValue.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = draft,
                onValueChange = { draft = it.filter(Char::isDigit) },
                label = { Text(label) },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    draft.toIntOrNull()
                        ?.takeIf { it in validRange }
                        ?.let(onConfirm)
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun ProfileDetailRow(label: String, value: String) {
    val colorScheme = MaterialTheme.colorScheme
    Text(
        buildAnnotatedString {
            withStyle(
                SpanStyle(
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    color = colorScheme.onSurface
                )
            ) {
                append("$label ")
            }
            withStyle(SpanStyle(color = colorScheme.onSurface)) {
                append(value)
            }
        },
        fontSize = 16.sp,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

@Composable
private fun SettingsListEntry(
    title: String,
    description: String,
    containerColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(description, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                ">",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SettingsOptionRow(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = settingsPanelColor()),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, color = colorScheme.onSurface, fontWeight = FontWeight.Medium)
            Text(
                if (selected) "Selected" else "",
                color = if (selected) colorScheme.secondary else colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
fun ManualEntryPlaceholderScreen(navController: NavController, viewModel: SatiationViewModel) {
    val presetFoods by viewModel.presetFoods.collectAsState()
    val colorScheme = MaterialTheme.colorScheme
    var editingPreset by remember { mutableStateOf<PresetFood?>(null) }
    var mealName by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var fats by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .statusBarsPadding()
    ) {
        LazyColumn(
            state = listState,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.offset(x = (-12).dp)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = colorScheme.onBackground)
                    }
                    Text("Preset Foods / Manual Entry", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = colorScheme.onBackground)
                }
            }
            item {
                Text(
                    "Log a meal directly into Room, or reuse a saved preset food when offline or when scanning is unavailable.",
                    color = colorScheme.onSurfaceVariant
                )
            }
            item {
                OutlinedTextField(
                    value = mealName,
                    onValueChange = { mealName = it },
                    label = { Text("Meal Name") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                MacroInputRow(
                    calories = calories,
                    onCaloriesChange = { calories = it },
                    protein = protein,
                    onProteinChange = { protein = it },
                    carbs = carbs,
                    onCarbsChange = { carbs = it },
                    fats = fats,
                    onFatsChange = { fats = it }
                )
            }
            item {
                Button(
                    onClick = {
                        val parsedCalories = calories.toDoubleOrNull() ?: 0.0
                        val parsedProtein = protein.toDoubleOrNull() ?: 0.0
                        val parsedCarbs = carbs.toDoubleOrNull() ?: 0.0
                        val parsedFats = fats.toDoubleOrNull() ?: 0.0
                        val trimmedMealName = mealName.trim()

                        if (trimmedMealName.isNotBlank()) {
                            val mealLog = MealLog(
                                loggedAtEpochMillis = System.currentTimeMillis(),
                                sourceType = "manual",
                                totalCalories = parsedCalories,
                                totalProteinGrams = parsedProtein,
                                totalCarbsGrams = parsedCarbs,
                                totalFatsGrams = parsedFats,
                                notes = notes.trim().ifEmpty { null }
                            )
                            val item = MealItem(
                                mealId = 0,
                                name = trimmedMealName,
                                category = category.trim().ifEmpty { null },
                                calories = parsedCalories,
                                proteinGrams = parsedProtein,
                                carbsGrams = parsedCarbs,
                                fatsGrams = parsedFats
                            )
                            viewModel.insertMeal(mealLog, listOf(item))
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text("Save Meal")
                }
            }
            item {
                Text("Preset Foods", fontWeight = FontWeight.Bold, color = colorScheme.onBackground)
            }
            if (presetFoods.isEmpty()) {
                item {
                    EmptyStateCard("No preset foods saved yet.")
                }
            } else {
                items(presetFoods) { preset ->
                    PresetFoodCard(
                        preset = preset,
                        onUse = {
                            editingPreset = null
                            mealName = preset.name
                            category = preset.category.orEmpty()
                            notes = preset.notes.orEmpty()
                            calories = preset.calories.toInt().toString()
                            protein = preset.proteinGrams.toInt().toString()
                            carbs = preset.carbsGrams.toInt().toString()
                            fats = preset.fatsGrams.toInt().toString()
                        },
                        onEdit = { editingPreset = preset },
                        onDelete = {
                            viewModel.deletePresetFood(preset.presetFoodId)
                            if (editingPreset?.presetFoodId == preset.presetFoodId) {
                                editingPreset = null
                            }
                        }
                    )
                }
            }
        }
        PassiveScrollbar(listState = listState)
    }
}

@Composable
private fun MealCard(meal: MealWithItems) {
    val itemSummary = meal.items.joinToString { it.name }
    val title = itemSummary.ifBlank { "Meal" }
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { },
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold, color = colorScheme.onSurface)
            Spacer(modifier = Modifier.height(6.dp))
            Text(formatTime(meal.meal.loggedAtEpochMillis), color = colorScheme.onSurfaceVariant, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(formatCalories(meal.meal.totalCalories), color = SatiationOrange, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("P ${formatMacroValue(meal.meal.totalProteinGrams)}", color = SatiationGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("C ${formatMacroValue(meal.meal.totalCarbsGrams)}", color = Color(0xFFFFD56F), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("F ${formatMacroValue(meal.meal.totalFatsGrams)}", color = DarkFatsColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun PresetFoodCard(
    preset: PresetFood,
    showUseButton: Boolean = true,
    onUse: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(preset.name, fontWeight = FontWeight.Bold, color = colorScheme.onSurface)
            preset.category?.takeIf { it.isNotBlank() }?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(it, color = colorScheme.onSurfaceVariant, fontSize = 13.sp)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(formatCalories(preset.calories), color = SatiationOrange, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("P ${formatMacroValue(preset.proteinGrams)}", color = SatiationGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("C ${formatMacroValue(preset.carbsGrams)}", color = Color(0xFFFFD56F), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("F ${formatMacroValue(preset.fatsGrams)}", color = DarkFatsColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            if (showUseButton) {
                Button(
                    onClick = onUse,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.surfaceVariant)
                ) {
                    Text("Use", color = colorScheme.onSurface, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary.copy(alpha = 0.14f))
                ) {
                    Text("Edit", color = colorScheme.onSurface, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.secondary.copy(alpha = 0.2f))
                ) {
                    Text("Delete", color = colorScheme.onSurface, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun PresetFoodEditorCard(
    title: String,
    submitLabel: String,
    initialName: String = "",
    initialCategory: String = "",
    initialCalories: String = "",
    initialProtein: String = "",
    initialCarbs: String = "",
    initialFats: String = "",
    initialNotes: String = "",
    secondaryActionLabel: String? = null,
    onSecondaryAction: (() -> Unit)? = null,
    onSubmit: (
        name: String,
        category: String?,
        calories: Double,
        protein: Double,
        carbs: Double,
        fats: Double,
        notes: String?
    ) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    var name by remember(initialName) { mutableStateOf(initialName) }
    var category by remember(initialCategory) { mutableStateOf(initialCategory) }
    var calories by remember(initialCalories) { mutableStateOf(initialCalories) }
    var protein by remember(initialProtein) { mutableStateOf(initialProtein) }
    var carbs by remember(initialCarbs) { mutableStateOf(initialCarbs) }
    var fats by remember(initialFats) { mutableStateOf(initialFats) }
    var notes by remember(initialNotes) { mutableStateOf(initialNotes) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, fontWeight = FontWeight.Bold, color = colorScheme.onSurface)
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Food Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Category (optional)") },
                modifier = Modifier.fillMaxWidth()
            )
            MacroInputRow(
                calories = calories,
                onCaloriesChange = { calories = it.filter { char -> char.isDigit() || char == '.' } },
                protein = protein,
                onProteinChange = { protein = it.filter { char -> char.isDigit() || char == '.' } },
                carbs = carbs,
                onCarbsChange = { carbs = it.filter { char -> char.isDigit() || char == '.' } },
                fats = fats,
                onFatsChange = { fats = it.filter { char -> char.isDigit() || char == '.' } }
            )
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (optional)") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    onSubmit(
                        name.trim(),
                        category.trim().ifEmpty { null },
                        calories.toDoubleOrNull() ?: 0.0,
                        protein.toDoubleOrNull() ?: 0.0,
                        carbs.toDoubleOrNull() ?: 0.0,
                        fats.toDoubleOrNull() ?: 0.0,
                        notes.trim().ifEmpty { null }
                    )
                },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text(submitLabel)
            }
            if (secondaryActionLabel != null && onSecondaryAction != null) {
                OutlinedButton(
                    onClick = onSecondaryAction,
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text(secondaryActionLabel)
                }
            }
        }
    }
}

private fun formatNumberInput(value: Double): String {
    return if (value % 1.0 == 0.0) {
        value.toInt().toString()
    } else {
        value.toString()
    }
}

@Composable
private fun EmptyStateCard(message: String) {
    val colorScheme = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
            .border(1.dp, colorScheme.outlineVariant, RoundedCornerShape(16.dp))
            .padding(18.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(message, color = colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun EmptyMealsCard() {
    EmptyStateCard("No meals logged for this day yet.")
}

@Composable
private fun settingsPanelColor(): Color {
    val colorScheme = MaterialTheme.colorScheme
    return if (colorScheme.background.luminance() > 0.5f) {
        Color(0xFFFFFCF5)
    } else {
        colorScheme.surfaceVariant
    }
}

@Composable
private fun PassiveScrollbar(listState: LazyListState) {
    val layoutInfo = listState.layoutInfo
    val totalItems = layoutInfo.totalItemsCount
    if (totalItems <= 0) return

    val visibleItems = layoutInfo.visibleItemsInfo
    if (visibleItems.isEmpty()) return

    val averageItemHeight = visibleItems.map { it.size }.average().toFloat().coerceAtLeast(1f)
    val estimatedContentHeight = averageItemHeight * totalItems
    val viewportHeight = (layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset).toFloat()
    if (estimatedContentHeight <= viewportHeight) return

    val firstVisibleItem = visibleItems.first()
    val scrollOffset = (firstVisibleItem.index * averageItemHeight) - firstVisibleItem.offset
    val scrollProgress = (scrollOffset / (estimatedContentHeight - viewportHeight)).coerceIn(0f, 1f)
    val density = LocalDensity.current

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp, bottom = 24.dp, end = 8.dp),
        contentAlignment = Alignment.TopEnd
    ) {
        val trackHeightPx = with(density) { maxHeight.toPx() }
        val minThumbPx = with(density) { 36.dp.toPx() }
        val thumbHeightPx = (trackHeightPx * (viewportHeight / estimatedContentHeight)).coerceIn(minThumbPx, trackHeightPx)
        val thumbOffsetPx = (trackHeightPx - thumbHeightPx) * scrollProgress

        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f), RoundedCornerShape(999.dp))
        )
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(with(density) { thumbHeightPx.toDp() })
                .offset(y = with(density) { thumbOffsetPx.toDp() })
                .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f), RoundedCornerShape(999.dp))
        )
    }
}

@Composable
private fun MacroSummaryPill(label: String, value: Double) {
    val colorScheme = MaterialTheme.colorScheme

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = colorScheme.onSurfaceVariant, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(formatMacroValue(value), fontWeight = FontWeight.Bold, color = colorScheme.onSurface)
    }
}

@Composable
private fun TrendRow(label: String, value: String) {
    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = colorScheme.onSurfaceVariant)
        Text(value, color = colorScheme.onSurface, fontWeight = FontWeight.Bold)
    }
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun MacroInputRow(
    calories: String,
    onCaloriesChange: (String) -> Unit,
    protein: String,
    onProteinChange: (String) -> Unit,
    carbs: String,
    onCarbsChange: (String) -> Unit,
    fats: String,
    onFatsChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = calories,
            onValueChange = onCaloriesChange,
            label = { Text("Calories") },
            modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = protein,
                onValueChange = onProteinChange,
                label = { Text("Protein") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = carbs,
                onValueChange = onCarbsChange,
                label = { Text("Carbs") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = fats,
                onValueChange = onFatsChange,
                label = { Text("Fats") },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun MacroBar(label: String, value: Float, max: Float, color: Color) {
    val colorScheme = MaterialTheme.colorScheme
    val progress = if (max > 0f) {
        (value / max).coerceIn(0f, 1f)
    } else {
        0f
    }

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontWeight = FontWeight.Bold, color = colorScheme.onSurface)
            Text("${formatMacroProgress(value)} / ${formatMacroProgress(max)}", color = colorScheme.onSurfaceVariant)
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp)),
            color = color,
            trackColor = colorScheme.surfaceVariant
        )
    }
}

private fun formatWeightKg(weightKg: Double): String {
    val display = if (weightKg % 1.0 == 0.0) {
        weightKg.toInt().toString()
    } else {
        "%.1f".format(Locale.US, weightKg)
    }
    return "$display kg"
}

private fun formatHeightCm(heightCm: Double): String {
    val display = if (heightCm % 1.0 == 0.0) {
        heightCm.toInt().toString()
    } else {
        "%.1f".format(Locale.US, heightCm)
    }
    return "$display cm"
}

private fun formatCalories(calories: Double): String {
    return "${calories.toInt()} Kcal"
}

private fun formatWholeWeightKg(weightKg: Int): String = "$weightKg kg"

private fun formatWholeHeightCm(heightCm: Int): String = "$heightCm cm"

private val DarkFatsColor = Color(0xFF8A7AB4)

private fun formatMacroValue(value: Double): String {
    return if (value % 1.0 == 0.0) {
        "${value.toInt()}g"
    } else {
        "${"%.1f".format(Locale.US, value)}g"
    }
}

private fun formatMacroProgress(value: Float): String {
    return if (value % 1f == 0f) {
        value.toInt().toString()
    } else {
        "%.1f".format(Locale.US, value)
    }
}

private fun formatTime(epochMillis: Long): String {
    return SimpleDateFormat("h:mm a", Locale.US).format(Date(epochMillis))
}

private fun currentDayRangeMillis(): Pair<Long, Long> {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val start = calendar.timeInMillis
    calendar.add(Calendar.DAY_OF_YEAR, 1)
    calendar.add(Calendar.MILLISECOND, -1)
    return start to calendar.timeInMillis
}

private fun trailingDayRangeMillis(days: Int): Pair<Long, Long> {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 59)
    calendar.set(Calendar.MILLISECOND, 999)
    val end = calendar.timeInMillis
    calendar.add(Calendar.DAY_OF_YEAR, -(days - 1))
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.timeInMillis to end
}
