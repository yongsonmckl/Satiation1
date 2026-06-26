package com.mckl.satiation1.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DatePickerDialog
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
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mckl.satiation1.database.AppSettings
import com.mckl.satiation1.database.DailyMacroTotals
import com.mckl.satiation1.database.DailyMealSummary
import com.mckl.satiation1.database.FoodFrequencySummary
import com.mckl.satiation1.database.MealItem
import com.mckl.satiation1.database.MealLog
import com.mckl.satiation1.database.MealWithItems
import com.mckl.satiation1.database.PresetFood
import com.mckl.satiation1.database.WeightLog
import com.mckl.satiation1.DarkText
import com.mckl.satiation1.DATE_FORMAT_DAY_MONTH_YEAR
import com.mckl.satiation1.DisplayPreferences
import com.mckl.satiation1.LightText
import com.mckl.satiation1.SatiationGreen
import com.mckl.satiation1.SatiationOrange
import com.mckl.satiation1.UNIT_IMPERIAL
import com.mckl.satiation1.displayHeightToCm
import com.mckl.satiation1.displayWeightToKg
import com.mckl.satiation1.formatDateForPreference
import com.mckl.satiation1.formatHeightForDisplay
import com.mckl.satiation1.formatWholeHeightForDisplay
import com.mckl.satiation1.formatWholeWeightForDisplay
import com.mckl.satiation1.formatWeightForDisplay
import com.mckl.satiation1.heightInputLabel
import com.mckl.satiation1.heightCmToDisplayValue
import com.mckl.satiation1.navigation.SatiationViewModel
import com.mckl.satiation1.usesImperialUnits
import com.mckl.satiation1.weightInputLabel
import com.mckl.satiation1.weightKgToDisplayValue
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
    val openMealEditor: (MealWithItems) -> Unit = { meal ->
        viewModel.beginMealEdit(meal)
        rootNavController.navigate("manual_entry")
    }
    val deleteMealEntry: (MealWithItems) -> Unit = { meal ->
        viewModel.deleteMeal(meal.meal.mealId)
    }
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
                            color = appGreen()
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
                    .navigationBarsPadding()
                    .padding(bottom = 18.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp, vertical = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val selectedTabColor = colorScheme.primary
                    val unselectedTabColor = colorScheme.onSurfaceVariant
                    IconButton(onClick = { viewModel.currentMainTab = "home" }) {
                        Icon(
                            Icons.Default.Home,
                            contentDescription = "Home",
                            tint = if (currentTab == "home") selectedTabColor else unselectedTabColor
                        )
                    }
                    IconButton(onClick = { viewModel.currentMainTab = "checkmark" }) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Tasks",
                            tint = if (currentTab == "checkmark") selectedTabColor else unselectedTabColor
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(appGreen())
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

                    IconButton(onClick = { viewModel.openProgressRoot() }) {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = "Progress",
                            tint = if (currentTab == "progress") selectedTabColor else unselectedTabColor
                        )
                    }
                    IconButton(onClick = { viewModel.currentMainTab = "profile" }) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = if (currentTab == "profile") selectedTabColor else unselectedTabColor
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
                        onEditMeal = openMealEditor,
                        onDeleteMeal = deleteMealEntry,
                        onNavigateToCheckmark = { viewModel.currentMainTab = "checkmark" },
                        onOpenMenu = {
                            setIsAddMenuMounted(true)
                            setShowAddMenu(true)
                        }
                    )
                    "checkmark" -> CheckmarkScreen(
                        viewModel,
                        onEditMeal = openMealEditor,
                        onDeleteMeal = deleteMealEntry
                    )
                    "progress" -> ProgressScreen(
                        viewModel,
                        onEditMeal = openMealEditor,
                        onDeleteMeal = deleteMealEntry
                    )
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
                    .navigationBarsPadding()
                    .padding(bottom = 20.dp)
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
                            viewModel.openCameraForPreview()
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
                            viewModel.clearMealDraft()
                            rootNavController.navigate("manual_entry")
                        }
                    }
                )
                ListItem(
                    headlineContent = { Text("Log New Weight") },
                    modifier = Modifier.clickable {
                        coroutineScope.launch {
                            setShowAddMenu(false)
                            delay(240)
                            rootNavController.navigate("edit_weight")
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
    onEditMeal: (MealWithItems) -> Unit,
    onDeleteMeal: (MealWithItems) -> Unit,
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
                MealCard(
                    meal,
                    onEdit = { onEditMeal(meal) },
                    onDelete = { onDeleteMeal(meal) }
                )
            }
        }
    }
}

@Composable
fun SettingsHubScreen(rootNavController: NavController, viewModel: SatiationViewModel) {
    val userProfile by viewModel.userProfile.collectAsState()
    val currentBmi by viewModel.currentBmi.collectAsState()
    val profile = userProfile
    val appSettings by viewModel.appSettings.collectAsState()
    val preferredUnits = appSettings?.preferredUnits ?: DisplayPreferences.preferredUnits
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
                                ProfileDetailRow("Current Weight:", formatWeightForDisplay(profile.currentWeightKg, preferredUnits))
                                profile.heightCm?.let { heightCm ->
                                    ProfileDetailRow("Height:", formatHeightForDisplay(heightCm, preferredUnits))
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
                    title = "Gemini API Key",
                    description = "Store or replace the local scan key",
                    containerColor = profileCardColor,
                    onClick = { rootNavController.navigate("edit_api_key") }
                )
            }
            item {
                SettingsListEntry(
                    title = "Settings",
                    description = "Units, nutrients, appearance, history, reminders, and advanced tools",
                    containerColor = profileCardColor,
                    onClick = { rootNavController.navigate("settings_menu") }
                )
            }
        }
        PassiveScrollbar(listState = listState)
    }
}

@Composable
fun CheckmarkScreen(
    viewModel: SatiationViewModel,
    onEditMeal: (MealWithItems) -> Unit,
    onDeleteMeal: (MealWithItems) -> Unit
) {
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
                MacroBar("Protein", (todayTotal?.proteinGrams ?: 0.0).toFloat(), settings.proteinTargetGrams.toFloat(), appGreen())
            }
            item {
                MacroBar("Fats", (todayTotal?.fatsGrams ?: 0.0).toFloat(), settings.fatsTargetGrams.toFloat(), DarkFatsColor)
            }
            item {
                MacroBar("Carbs", (todayTotal?.carbsGrams ?: 0.0).toFloat(), settings.carbsTargetGrams.toFloat(), DarkCarbsColor)
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
                    MealCard(
                        meal,
                        onEdit = { onEditMeal(meal) },
                        onDelete = { onDeleteMeal(meal) }
                    )
                }
            }
        }
        PassiveScrollbar(listState = listState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    viewModel: SatiationViewModel,
    onEditMeal: (MealWithItems) -> Unit,
    onDeleteMeal: (MealWithItems) -> Unit
) {
    val todayStart = remember { startOfDayMillis(System.currentTimeMillis()) }
    var selectedDayStart by rememberSaveable { mutableStateOf(todayStart) }
    var rangeStartDay by rememberSaveable { mutableStateOf(addDays(todayStart, -6)) }
    var rangeEndDay by rememberSaveable { mutableStateOf(todayStart) }
    var progressDestination by rememberSaveable { mutableStateOf("overview") }
    var showCalendarDialog by remember { mutableStateOf(false) }
    var showAnnotationDialog by remember { mutableStateOf(false) }
    var pendingAnnotation by remember { mutableStateOf("") }
    val progressResetToken = viewModel.progressTabSelectionNonce
    val pageAnimationKey = remember(progressDestination, progressResetToken) {
        "$progressDestination-$progressResetToken"
    }

    var animatePageIn by remember { mutableStateOf(false) }
    val pageAlpha by animateFloatAsState(
        targetValue = if (animatePageIn) 1f else 0f,
        animationSpec = tween(220),
        label = "ProgressPageAlpha"
    )

    fun applyRange(startDay: Long, endDay: Long) {
        val normalizedStart = startOfDayMillis(minOf(startDay, endDay))
        val normalizedEnd = startOfDayMillis(minOf(maxOf(startDay, endDay), todayStart))
        rangeStartDay = normalizedStart
        rangeEndDay = normalizedEnd
        selectedDayStart = selectedDayStart.coerceIn(normalizedStart, normalizedEnd)
    }

    fun openCalendarDestination() {
        applyRange(addDays(todayStart, -6), todayStart)
        selectedDayStart = todayStart
        progressDestination = "calendar"
    }

    val rangeStart = rangeStartDay
    val rangeEnd = remember(rangeEndDay) { endOfDayMillis(rangeEndDay) }
    val rangeDayStarts = remember(rangeStartDay, rangeEndDay) { dayStartSeriesBetween(rangeStartDay, rangeEndDay) }

    val appSettings by viewModel.appSettings.collectAsState()
    val weightHistory by viewModel.weightHistory.collectAsState()
    val currentBmi by viewModel.currentBmi.collectAsState()
    val chartAnnotations by viewModel.chartAnnotations.collectAsState()
    val topFoods by viewModel.topFoods.collectAsState()
    val earliestMealLoggedAt by viewModel.earliestMealLoggedAt.collectAsState()
    val macroHistory by viewModel.getDailyMacroTotals(rangeStart, rangeEnd).collectAsState(initial = emptyList())
    val mealSummaries by viewModel.getDailyMealSummaries(rangeStart, rangeEnd).collectAsState(initial = emptyList())
    val selectedDayMeals by viewModel
        .getMealsForRange(selectedDayStart, endOfDayMillis(selectedDayStart))
        .collectAsState(initial = emptyList())

    val rangeSnapshots = remember(rangeDayStarts, macroHistory, mealSummaries, weightHistory) {
        buildProgressDaySnapshots(
            dayStarts = rangeDayStarts,
            macroHistory = macroHistory,
            mealSummaries = mealSummaries,
            weightHistory = weightHistory
        )
    }
    val snapshotsByStart = remember(rangeSnapshots) { rangeSnapshots.associateBy { it.startMillis } }
    val selectedDay = progressSnapshotForDay(
        dayStart = selectedDayStart,
        snapshotsByStart = snapshotsByStart,
        weightHistory = weightHistory
    )
    val selectedAnnotations = chartAnnotations[selectedDay.dayKey].orEmpty()
    val trackedDays = remember(rangeSnapshots) {
        rangeSnapshots.count { it.mealCount > 0 || it.totalCalories > 0.0 || it.weightKg != null }
    }
    val totalMeals = remember(rangeSnapshots) { rangeSnapshots.sumOf { it.mealCount } }
    val averageCalories = remember(rangeSnapshots) {
        if (rangeSnapshots.isEmpty()) 0.0 else rangeSnapshots.sumOf { it.totalCalories } / rangeSnapshots.size
    }
    val currentLoggingStreak = remember(rangeSnapshots) { currentLoggingStreak(rangeSnapshots) }
    val bestLoggingStreak = remember(rangeSnapshots) { longestLoggingStreak(rangeSnapshots) }
    val highestCalorieDay = remember(rangeSnapshots) {
        rangeSnapshots.filter { it.totalCalories > 0.0 }.maxByOrNull { it.totalCalories }
    }
    val rangeProtein = remember(rangeSnapshots) { rangeSnapshots.sumOf { it.proteinGrams } }
    val rangeCarbs = remember(rangeSnapshots) { rangeSnapshots.sumOf { it.carbsGrams } }
    val rangeFats = remember(rangeSnapshots) { rangeSnapshots.sumOf { it.fatsGrams } }
    val rangeWeightHistory = remember(weightHistory, rangeStart, rangeEnd) {
        weightHistory.filter { it.loggedAtEpochMillis in rangeStart..rangeEnd }
    }
    val rangeLatestWeight = rangeWeightHistory.lastOrNull()?.weightKg
    val rangeWeightDelta = remember(rangeWeightHistory) {
        if (rangeWeightHistory.size >= 2) {
            rangeWeightHistory.last().weightKg - rangeWeightHistory.first().weightKg
        } else {
            null
        }
    }
    val hasAnalyticsData = remember(rangeSnapshots, weightHistory, topFoods) {
        rangeSnapshots.any { it.totalCalories > 0.0 || it.mealCount > 0 } ||
            weightHistory.isNotEmpty() ||
            topFoods.isNotEmpty()
    }
    val weekdayMealPattern = remember(rangeSnapshots) { buildWeekdayMealPattern(rangeSnapshots) }
    val colorScheme = MaterialTheme.colorScheme
    val earliestTrackedDay = remember(earliestMealLoggedAt, weightHistory) {
        listOfNotNull(
            earliestMealLoggedAt?.let(::startOfDayMillis),
            weightHistory.minOfOrNull { startOfDayMillis(it.loggedAtEpochMillis) }
        ).minOrNull()
    }
    val canShowAllTime = earliestTrackedDay != null

    LaunchedEffect(pageAnimationKey) {
        animatePageIn = false
        delay(40)
        animatePageIn = true
    }

    LaunchedEffect(progressResetToken) {
        progressDestination = "overview"
    }

    BackHandler(enabled = progressDestination != "overview") {
        progressDestination = "overview"
    }

    when (progressDestination) {
        "overview" -> {
            val listState = rememberLazyListState()

            Box(modifier = Modifier.fillMaxSize().graphicsLayer(alpha = pageAlpha)) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    contentPadding = PaddingValues(top = 24.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        ProgressAnimatedSection(animationKey = pageAnimationKey, staggerIndex = 0) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("Progress", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = colorScheme.onBackground)
                                Text(
                                    "Choose the calendar view for date-based charts or the stats view for trends, weight, and favorite foods.",
                                    color = colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    if (!hasAnalyticsData) {
                        item {
                            ProgressAnimatedSection(animationKey = pageAnimationKey, staggerIndex = 1) {
                                AnalyticsEmptyStateCard(
                                    title = "No analytics yet",
                                    message = "Log a meal or add a weight entry to populate the calendar and stats pages."
                                )
                            }
                        }
                    }
                    item {
                        ProgressAnimatedSection(
                            animationKey = pageAnimationKey,
                            staggerIndex = if (hasAnalyticsData) 1 else 2
                        ) {
                            ProgressDestinationCard(
                                title = "Calendar",
                                description = "Open the calendar range view and inspect calories and macros by day.",
                                onClick = { openCalendarDestination() }
                            ) {
                                if (rangeSnapshots.any { it.totalCalories > 0.0 }) {
                                    CalorieRangeChart(
                                        daySnapshots = rangeSnapshots,
                                        selectedDay = selectedDay,
                                        interactive = false,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(172.dp)
                                    )
                                } else {
                                    SparseTrendState(
                                        title = "Calendar view is ready",
                                        message = "Calories, macro split, markers, and meals will appear here once data is logged."
                                    )
                                }
                            }
                        }
                    }
                    item {
                        ProgressAnimatedSection(
                            animationKey = pageAnimationKey,
                            staggerIndex = if (hasAnalyticsData) 2 else 3
                        ) {
                            ProgressDestinationCard(
                                title = "Stats",
                                description = "Open weight trends, favorite foods, and habit summaries that complement the calendar view.",
                                onClick = { progressDestination = "stats" }
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    TrendRow("Favorite Food", topFoods.firstOrNull()?.name ?: "No data yet")
                                    TrendRow("Latest Weight", rangeLatestWeight?.let(::formatWeightKg) ?: "No entries")
                                    TrendRow("Meals in Range", totalMeals.toString())
                                    TrendRow("Tracked Days", trackedDays.toString())
                                }
                            }
                        }
                    }
                }
                PassiveScrollbar(listState = listState)
            }
        }
        "calendar" -> {
            val listState = rememberLazyListState()

            Box(modifier = Modifier.fillMaxSize().graphicsLayer(alpha = pageAlpha)) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    contentPadding = PaddingValues(top = 24.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        ProgressAnimatedSection(animationKey = pageAnimationKey, staggerIndex = 0) {
                            ProgressPageHeader(
                                title = "Calendar",
                                subtitle = "Choose a live date range, then inspect calorie and macro charts by day.",
                                onBack = { progressDestination = "overview" }
                            )
                        }
                    }
                    item {
                        ProgressAnimatedSection(animationKey = pageAnimationKey, staggerIndex = 1) {
                            ProgressCalendarBrowserCard(
                                rangeStartDay = rangeStartDay,
                                rangeEndDay = rangeEndDay,
                                onOpenCalendar = { showCalendarDialog = true },
                                currentDayLabel = formatLongDayLabel(todayStart),
                                onQuickRangeSelected = { preset ->
                                    when (preset) {
                                        "week" -> applyRange(addDays(todayStart, -6), todayStart)
                                        "month" -> applyRange(addDays(todayStart, -29), todayStart)
                                        "all_time" -> earliestTrackedDay?.let { applyRange(it, todayStart) }
                                        "year" -> applyRange(addDays(todayStart, -364), todayStart)
                                    }
                                    selectedDayStart = todayStart
                                },
                                showAllTime = canShowAllTime
                            )
                        }
                    }
                    item {
                        ProgressAnimatedSection(animationKey = pageAnimationKey, staggerIndex = 2) {
                            CalorieTrendCard(
                                daySnapshots = rangeSnapshots,
                                selectedDay = selectedDay,
                                rangeLabel = formatDateRangeLabel(rangeStartDay, rangeEnd),
                                onSelectDay = { selectedDayStart = it.startMillis }
                            )
                        }
                    }
                    item {
                        ProgressAnimatedSection(animationKey = pageAnimationKey, staggerIndex = 3) {
                            MacroSplitCard(
                                proteinGrams = rangeProtein,
                                carbsGrams = rangeCarbs,
                                fatsGrams = rangeFats,
                                rangeLabel = formatDateRangeLabel(rangeStartDay, rangeEnd)
                            )
                        }
                    }
                    item {
                        ProgressAnimatedSection(animationKey = pageAnimationKey, staggerIndex = 4) {
                            SelectedDayOverviewCard(
                                selectedDay = selectedDay,
                                currentBmi = currentBmi,
                                calorieTarget = appSettings?.calorieTarget ?: 2500.0,
                                proteinTarget = appSettings?.proteinTargetGrams ?: 120.0,
                                carbsTarget = appSettings?.carbsTargetGrams ?: 300.0,
                                fatsTarget = appSettings?.fatsTargetGrams ?: 70.0
                            )
                        }
                    }
                    item {
                        ProgressAnimatedSection(animationKey = pageAnimationKey, staggerIndex = 5) {
                            AnnotationCard(
                                selectedDayLabel = selectedDay.longLabel,
                                annotations = selectedAnnotations,
                                onAddAnnotation = {
                                    pendingAnnotation = ""
                                    showAnnotationDialog = true
                                },
                                onDeleteAnnotation = { index ->
                                    viewModel.deleteChartAnnotation(selectedDay.dayKey, index)
                                }
                            )
                        }
                    }
                    item {
                        ProgressAnimatedSection(animationKey = pageAnimationKey, staggerIndex = 6) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Text("Meals for ${selectedDay.longLabel}", fontWeight = FontWeight.Bold, color = colorScheme.onSurface)
                                    Text(
                                        "Review everything logged on the selected day.",
                                        color = colorScheme.onSurfaceVariant,
                                        fontSize = 13.sp
                                    )
                                    if (selectedDayMeals.isEmpty()) {
                                        EmptyMealsCard()
                                    } else {
                                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                            selectedDayMeals.forEach { meal ->
                                                MealCard(
                                                    meal,
                                                    onEdit = { onEditMeal(meal) },
                                                    onDelete = { onDeleteMeal(meal) }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                PassiveScrollbar(listState = listState)
            }
        }
        "stats" -> {
            val listState = rememberLazyListState()

            Box(modifier = Modifier.fillMaxSize().graphicsLayer(alpha = pageAlpha)) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    contentPadding = PaddingValues(top = 24.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        ProgressAnimatedSection(animationKey = pageAnimationKey, staggerIndex = 0) {
                            ProgressPageHeader(
                                title = "Stats",
                                subtitle = "Weight, favorite foods, and broader patterns live here. Range-based charts follow the selected calendar dates.",
                                onBack = { progressDestination = "overview" }
                            )
                        }
                    }
                    if (!hasAnalyticsData) {
                        item {
                            ProgressAnimatedSection(animationKey = pageAnimationKey, staggerIndex = 1) {
                                AnalyticsEmptyStateCard(
                                    title = "No stats yet",
                                    message = "Log meals or weight entries to populate favorite foods, trends, and habit charts."
                                )
                            }
                        }
                    }
                    item {
                        ProgressAnimatedSection(
                            animationKey = pageAnimationKey,
                            staggerIndex = if (hasAnalyticsData) 1 else 2
                        ) {
                            ProgressHighlightsCard(
                                trackedDays = trackedDays,
                                totalMeals = totalMeals,
                                averageCalories = averageCalories,
                                currentLoggingStreak = currentLoggingStreak,
                                bestLoggingStreak = bestLoggingStreak,
                                latestWeight = rangeLatestWeight,
                                currentBmi = currentBmi,
                                favoriteFood = topFoods.firstOrNull()?.name,
                                highestCalorieDay = highestCalorieDay
                            )
                        }
                    }
                    item {
                        ProgressAnimatedSection(
                            animationKey = pageAnimationKey,
                            staggerIndex = if (hasAnalyticsData) 2 else 3
                        ) {
                            BmiGaugeCard(currentBmi = currentBmi)
                        }
                    }
                    item {
                        ProgressAnimatedSection(
                            animationKey = pageAnimationKey,
                            staggerIndex = if (hasAnalyticsData) 3 else 4
                        ) {
                            WeightTrendCard(
                                weightHistory = rangeWeightHistory,
                                rangeLabel = formatDateRangeLabel(rangeStartDay, rangeEnd),
                                latestWeight = rangeLatestWeight,
                                weightDelta = rangeWeightDelta
                            )
                        }
                    }
                    item {
                        ProgressAnimatedSection(
                            animationKey = pageAnimationKey,
                            staggerIndex = if (hasAnalyticsData) 4 else 5
                        ) {
                            FavoriteFoodsCard(topFoods = topFoods)
                        }
                    }
                    item {
                        ProgressAnimatedSection(
                            animationKey = pageAnimationKey,
                            staggerIndex = if (hasAnalyticsData) 5 else 6
                        ) {
                            WeekdayMealPatternCard(
                                pattern = weekdayMealPattern,
                                rangeLabel = formatDateRangeLabel(rangeStartDay, rangeEnd)
                            )
                        }
                    }
                }
                PassiveScrollbar(listState = listState)
            }
        }
    }

    if (showCalendarDialog) {
        val datePickerState = rememberDateRangePickerState(
            initialSelectedStartDateMillis = rangeStartDay,
            initialSelectedEndDateMillis = rangeEndDay
        )
        DatePickerDialog(
            onDismissRequest = { showCalendarDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedStartDateMillis?.let { pickedStart ->
                            val pickedEnd = datePickerState.selectedEndDateMillis ?: pickedStart
                            applyRange(startOfDayMillis(pickedStart), startOfDayMillis(pickedEnd))
                        }
                        showCalendarDialog = false
                    }
                ) {
                    Text("Use Range")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCalendarDialog = false }) {
                    Text("Cancel")
                }
            }
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    formatLiveRangeSelection(
                        datePickerState.selectedStartDateMillis,
                        datePickerState.selectedEndDateMillis
                    ),
                    modifier = Modifier.padding(horizontal = 24.dp),
                    color = colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
                DateRangePicker(state = datePickerState)
            }
        }
    }

    if (showAnnotationDialog) {
        AlertDialog(
            onDismissRequest = { showAnnotationDialog = false },
            title = { Text("Add Day Marker") },
            text = {
                OutlinedTextField(
                    value = pendingAnnotation,
                    onValueChange = { pendingAnnotation = it },
                    label = { Text("Marker note") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addChartAnnotation(selectedDay.dayKey, pendingAnnotation)
                        showAnnotationDialog = false
                    },
                    enabled = pendingAnnotation.isNotBlank()
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAnnotationDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun LegacyProgressScreen(viewModel: SatiationViewModel) {
    val lastThirtyDays = remember { trailingDayRangeMillis(30) }
    val dayStarts = remember { trailingDayStartMillis(30) }
    val appSettings by viewModel.appSettings.collectAsState()
    val macroHistory by viewModel.getDailyMacroTotals(lastThirtyDays.first, lastThirtyDays.second).collectAsState(initial = emptyList())
    val mealSummaries by viewModel.getDailyMealSummaries(lastThirtyDays.first, lastThirtyDays.second).collectAsState(initial = emptyList())
    val weightHistory by viewModel.weightHistory.collectAsState()
    val currentBmi by viewModel.currentBmi.collectAsState()
    val chartAnnotations by viewModel.chartAnnotations.collectAsState()

    val daySnapshots = remember(dayStarts, macroHistory, mealSummaries, weightHistory) {
        buildProgressDaySnapshots(
            dayStarts = dayStarts,
            macroHistory = macroHistory,
            mealSummaries = mealSummaries,
            weightHistory = weightHistory
        )
    }
    var selectedDayStart by rememberSaveable {
        mutableStateOf(startOfDayMillis(System.currentTimeMillis()))
    }
    LaunchedEffect(daySnapshots) {
        if (daySnapshots.none { it.startMillis == selectedDayStart }) {
            selectedDayStart = daySnapshots.lastOrNull()?.startMillis ?: startOfDayMillis(System.currentTimeMillis())
        }
    }
    val selectedDay = daySnapshots.firstOrNull { it.startMillis == selectedDayStart }
        ?: daySnapshots.last()
    val selectedDayRange = remember(selectedDay.startMillis) {
        selectedDay.startMillis to endOfDayMillis(selectedDay.startMillis)
    }
    val selectedDayMeals by viewModel
        .getMealsForRange(selectedDayRange.first, selectedDayRange.second)
        .collectAsState(initial = emptyList())
    val recentMacroDays = remember(daySnapshots) {
        daySnapshots
            .asReversed()
            .filter { it.mealCount > 0 || it.totalCalories > 0.0 }
            .take(7)
    }
    val calorieEntries = remember(daySnapshots) {
        daySnapshots.map { it.totalCalories.toFloat() }
    }
    val weightEntries = remember(weightHistory) {
        weightHistory.map { it.weightKg.toFloat() }
    }
    val hasMealData = remember(daySnapshots) {
        daySnapshots.any { it.mealCount > 0 || it.totalCalories > 0.0 }
    }
    val mealTrendDayCount = remember(daySnapshots) {
        daySnapshots.count { it.totalCalories > 0.0 }
    }
    val hasWeightData = weightHistory.isNotEmpty()
    val hasWeightTrendData = weightHistory.size >= 2
    val hasAnalyticsData = hasMealData || hasWeightData
    val weeklySnapshots = remember(daySnapshots) { daySnapshots.takeLast(7) }
    val weeklyAverageCalories = remember(weeklySnapshots) {
        weeklySnapshots.map { it.totalCalories }.average().takeIf { !it.isNaN() } ?: 0.0
    }
    val trackedDays = remember(daySnapshots) {
        daySnapshots.count { it.mealCount > 0 || it.totalCalories > 0.0 || it.weightKg != null }
    }
    val totalMeals = remember(daySnapshots) {
        daySnapshots.sumOf { it.mealCount }
    }
    val currentMealStreak = remember(daySnapshots) {
        currentLoggingStreak(daySnapshots)
    }
    val highestCalorieDay = remember(daySnapshots) {
        daySnapshots
            .filter { it.totalCalories > 0.0 }
            .maxByOrNull { it.totalCalories }
    }
    val latestWeight = weightHistory.lastOrNull()?.weightKg
    val weightDelta = remember(weightHistory) {
        if (weightHistory.size >= 2) {
            weightHistory.last().weightKg - weightHistory.first().weightKg
        } else {
            null
        }
    }
    val selectedAnnotations = chartAnnotations[selectedDay.dayKey].orEmpty()
    var showAnnotationDialog by remember { mutableStateOf(false) }
    var pendingAnnotation by remember { mutableStateOf("") }
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
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Progress", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = colorScheme.onBackground)
                    Text(
                        "Browse the last 30 days, inspect a specific date, and review meals, macros, weight, and BMI in one place.",
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }
            item {
                DateSelectorCard(
                    daySnapshots = daySnapshots,
                    selectedDayStart = selectedDay.startMillis,
                    onSelectDay = { selectedDayStart = it },
                    onJumpToToday = { selectedDayStart = startOfDayMillis(System.currentTimeMillis()) },
                    annotationCounts = chartAnnotations.mapValues { it.value.size }
                )
            }
            if (!hasAnalyticsData) {
                item {
                    AnalyticsEmptyStateCard(
                        title = "No analytics yet",
                        message = "Log a meal or add a weight entry to populate the dashboard. Once data exists, charts, comparisons, and selected-day details will appear here."
                    )
                }
            }
            item {
                SelectedDayOverviewCard(
                    selectedDay = selectedDay,
                    currentBmi = currentBmi,
                    calorieTarget = appSettings?.calorieTarget ?: 2500.0,
                    proteinTarget = appSettings?.proteinTargetGrams ?: 120.0,
                    carbsTarget = appSettings?.carbsTargetGrams ?: 300.0,
                    fatsTarget = appSettings?.fatsTargetGrams ?: 70.0
                )
            }
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Calorie Trend", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Daily total calories for the last 30 days.",
                            color = colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        if (mealTrendDayCount >= 2) {
                            Chart(
                                chart = lineChart(),
                                model = entryModelOf(*calorieEntries.toTypedArray()),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            ChartWindowCaption(
                                firstLabel = daySnapshots.first().compactLabel,
                                lastLabel = daySnapshots.last().compactLabel,
                                summary = "Selected day: ${selectedDay.longLabel} • ${formatCalories(selectedDay.totalCalories)}"
                            )
                        } else if (hasMealData) {
                            SparseTrendState(
                                title = formatCalories(selectedDay.totalCalories),
                                message = "Log meals across at least two days to draw a clearer trend line. The selected day summary is shown above and below."
                            )
                        } else {
                            Text("No meal history is available yet for the calorie trend.", color = colorScheme.onSurfaceVariant)
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
                        Text("Macronutrient Breakdown by Day", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Recent daily macro totals. The selected day is highlighted and repeated in the overview card above.",
                            color = colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        if (recentMacroDays.isEmpty()) {
                            Text("No macro history is available yet.", color = colorScheme.onSurfaceVariant)
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                recentMacroDays.forEach { day ->
                                    MacroDayBreakdownRow(
                                        day = day,
                                        isSelected = day.dayKey == selectedDay.dayKey
                                    )
                                }
                            }
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
                        Text("Meals for ${selectedDay.longLabel}", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Review the meals logged on the selected day.",
                            color = colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        if (selectedDayMeals.isEmpty()) {
                            EmptyMealsCard()
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                selectedDayMeals.forEach { meal ->
                                    MealCard(meal)
                                }
                            }
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
                        Text("Weight Trend", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Each point reflects a logged weight entry.",
                            color = colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        if (hasWeightTrendData) {
                            Chart(
                                chart = lineChart(),
                                model = entryModelOf(*weightEntries.toTypedArray()),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            TrendRow("Latest Weight", latestWeight?.let(::formatWeightKg) ?: "No entries")
                            TrendRow(
                                "Weight Change",
                                weightDelta?.let { formatSignedWeightChange(it) } ?: "Need at least two entries"
                            )
                        } else if (hasWeightData) {
                            SparseTrendState(
                                title = latestWeight?.let(::formatWeightKg) ?: "No entries",
                                message = "Log at least one more weight entry to draw a useful weight trend line."
                            )
                        } else {
                            Text("No weight entries yet.", color = colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
            item {
                AnnotationCard(
                    selectedDayLabel = selectedDay.longLabel,
                    annotations = selectedAnnotations,
                    onAddAnnotation = {
                        pendingAnnotation = ""
                        showAnnotationDialog = true
                    },
                    onDeleteAnnotation = { index ->
                        viewModel.deleteChartAnnotation(selectedDay.dayKey, index)
                    }
                )
            }
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Trends", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))
                        TrendRow("Tracked Days", trackedDays.toString())
                        TrendRow("Meals Logged", totalMeals.toString())
                        TrendRow("7-Day Avg Calories", formatCalories(weeklyAverageCalories))
                        TrendRow("Current Meal Streak", "$currentMealStreak day(s)")
                        TrendRow("Latest Weight", latestWeight?.let(::formatWeightKg) ?: "No entries")
                        TrendRow("Current BMI", currentBmi?.let { "%.1f".format(Locale.US, it) } ?: "Unavailable")
                        TrendRow(
                            "Highest Calorie Day",
                            highestCalorieDay?.let { "${it.compactLabel} • ${formatCalories(it.totalCalories)}" } ?: "No meal days yet"
                        )
                    }
                }
            }
        }
        PassiveScrollbar(listState = listState)
    }

    if (showAnnotationDialog) {
        AlertDialog(
            onDismissRequest = { showAnnotationDialog = false },
            title = { Text("Add Day Marker") },
            text = {
                OutlinedTextField(
                    value = pendingAnnotation,
                    onValueChange = { pendingAnnotation = it },
                    label = { Text("Marker note") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addChartAnnotation(selectedDay.dayKey, pendingAnnotation)
                        showAnnotationDialog = false
                    },
                    enabled = pendingAnnotation.isNotBlank()
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAnnotationDialog = false }) {
                    Text("Cancel")
                }
            }
        )
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
            title = "Change Name & Pronouns",
            description = "Edit your display name and how the app refers to you",
            containerColor = panelColor,
            onClick = { navController.navigate("edit_name") }
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
    var createPresetSeed by remember { mutableStateOf(viewModel.consumePresetDraft()) }

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
                    "Create reusable preset foods here or prefill one from a saved meal.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            item {
                PresetFoodEditorCard(
                    title = if (editingPreset == null) "Create Preset Food" else "Edit Preset Food",
                    submitLabel = if (editingPreset == null) "Save Preset" else "Update Preset",
                    initialName = editingPreset?.name ?: createPresetSeed?.name.orEmpty(),
                    initialCategory = editingPreset?.category ?: createPresetSeed?.category.orEmpty(),
                    initialCalories = editingPreset?.calories?.let(::formatNumberInput) ?: createPresetSeed?.calories.orEmpty(),
                    initialProtein = editingPreset?.proteinGrams?.let(::formatNumberInput) ?: createPresetSeed?.protein.orEmpty(),
                    initialCarbs = editingPreset?.carbsGrams?.let(::formatNumberInput) ?: createPresetSeed?.carbs.orEmpty(),
                    initialFats = editingPreset?.fatsGrams?.let(::formatNumberInput) ?: createPresetSeed?.fats.orEmpty(),
                    initialNotes = editingPreset?.notes ?: createPresetSeed?.notes.orEmpty(),
                    secondaryActionLabel = if (editingPreset == null) null else "Cancel Edit",
                    onSecondaryAction = {
                        editingPreset = null
                        createPresetSeed = null
                    },
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
                        createPresetSeed = null
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
        }
        PassiveScrollbar(listState = listState)
    }
}

@Composable
fun AppearanceScreen(navController: NavController, viewModel: SatiationViewModel) {
    val appSettings by viewModel.appSettings.collectAsState()
    val currentSettings = appSettings ?: AppSettings()
    val panelColor = settingsPanelColor()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ScreenHeader(title = "Appearance", onBack = { navController.popBackStack() })
        AppearanceAccentControls(
            currentSettings = currentSettings,
            onUpdate = viewModel::saveSettings
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = panelColor)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Use System Theme", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text(
                            "When enabled, the app follows the phone theme and manual light/dark switching is locked.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                    }
                    androidx.compose.material3.Switch(
                        checked = currentSettings.followSystemTheme,
                        onCheckedChange = { checked ->
                            viewModel.saveSettings(currentSettings.copy(followSystemTheme = checked))
                        }
                    )
                }
            }
        }
        AnimatedVisibility(
            visible = !currentSettings.followSystemTheme,
            enter = androidx.compose.animation.slideInVertically(initialOffsetY = { -it / 3 }) + fadeIn(),
            exit = androidx.compose.animation.slideOutVertically(targetOffsetY = { -it / 3 }) + fadeOut()
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = panelColor)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Theme Mode", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text(
                        "Choose the app theme manually once system theme is turned off.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                    ThemeModeToggle(
                        isDarkMode = currentSettings.themePreference != "light",
                        onToggle = { useDark ->
                            viewModel.saveSettings(
                                currentSettings.copy(themePreference = if (useDark) "dark" else "light")
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemeModeToggle(
    isDarkMode: Boolean,
    onToggle: (Boolean) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(colorScheme.surfaceVariant)
            .padding(4.dp)
    ) {
        ThemeModeToggleOption(
            label = "Light",
            selected = !isDarkMode,
            onClick = { onToggle(false) },
            modifier = Modifier.weight(1f)
        )
        ThemeModeToggleOption(
            label = "Dark",
            selected = isDarkMode,
            onClick = { onToggle(true) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ThemeModeToggleOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) colorScheme.primary else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            color = if (selected) colorScheme.onPrimary else colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun EditNameScreen(navController: NavController, viewModel: SatiationViewModel) {
    val userProfile by viewModel.userProfile.collectAsState()
    var name by remember { mutableStateOf(userProfile?.name.orEmpty()) }
    var pronouns by remember { mutableStateOf(userProfile?.pronouns.orEmpty()) }

    LaunchedEffect(userProfile?.name, userProfile?.pronouns) {
        name = userProfile?.name.orEmpty()
        pronouns = userProfile?.pronouns.orEmpty()
    }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp).statusBarsPadding()) {
        ScreenHeader(title = "Change Name & Pronouns", onBack = { navController.popBackStack() })
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = pronouns,
            onValueChange = { pronouns = it },
            label = { Text("Pronouns") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                userProfile?.let { profile ->
                    viewModel.saveProfile(
                        name = name,
                        startWeightKg = profile.startWeightKg,
                        currentWeightKg = profile.currentWeightKg,
                        pronouns = pronouns,
                        heightCm = profile.heightCm
                    )
                    navController.popBackStack()
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Save Changes")
        }
    }
}

@Composable
fun EditPronounsScreen(navController: NavController, viewModel: SatiationViewModel) {
    EditNameScreen(navController, viewModel)
}

@Composable
fun EditHeightScreen(navController: NavController, viewModel: SatiationViewModel) {
    val userProfile by viewModel.userProfile.collectAsState()
    val appSettings by viewModel.appSettings.collectAsState()
    val preferredUnits = appSettings?.preferredUnits ?: DisplayPreferences.preferredUnits
    var height by remember { mutableFloatStateOf(heightCmToDisplayValue(userProfile?.heightCm ?: 170.0, preferredUnits).toFloat()) }
    var isDialogOpen by remember { mutableStateOf(false) }
    val heightRange = if (usesImperialUnits(preferredUnits)) {
        minOf(47f, height)..maxOf(87f, height)
    } else {
        minOf(120f, height)..maxOf(220f, height)
    }

    LaunchedEffect(userProfile?.heightCm, preferredUnits) {
        height = heightCmToDisplayValue(userProfile?.heightCm ?: 170.0, preferredUnits).toFloat()
    }

    MetricSliderScreen(
        title = "Change Height",
        valueText = formatWholeHeightForDisplay(height.toInt(), preferredUnits),
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
                    heightCm = displayHeightToCm(height.toDouble(), preferredUnits)
                )
                navController.popBackStack()
            }
        }
    )

    if (isDialogOpen) {
        IntegerValueDialog(
            title = "Enter Height",
            label = heightInputLabel(preferredUnits),
            initialValue = height.toInt(),
            validRange = if (usesImperialUnits(preferredUnits)) 31..110 else 80..280,
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
    val appSettings by viewModel.appSettings.collectAsState()
    val preferredUnits = appSettings?.preferredUnits ?: DisplayPreferences.preferredUnits
    var weight by remember { mutableFloatStateOf(weightKgToDisplayValue(userProfile?.currentWeightKg ?: 67.0, preferredUnits).toFloat()) }
    var isDialogOpen by remember { mutableStateOf(false) }
    val weightRange = if (usesImperialUnits(preferredUnits)) {
        minOf(88f, weight)..maxOf(330f, weight)
    } else {
        minOf(40f, weight)..maxOf(150f, weight)
    }

    LaunchedEffect(userProfile?.currentWeightKg, preferredUnits) {
        weight = weightKgToDisplayValue(userProfile?.currentWeightKg ?: 67.0, preferredUnits).toFloat()
    }

    MetricSliderScreen(
        title = "Change Weight",
        valueText = formatWholeWeightForDisplay(weight.toInt(), preferredUnits),
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
            viewModel.logWeight(displayWeightToKg(weight.toDouble(), preferredUnits))
            navController.popBackStack()
        }
    )

    if (isDialogOpen) {
        IntegerValueDialog(
            title = "Enter Weight",
            label = weightInputLabel(preferredUnits),
            initialValue = weight.toInt(),
            validRange = if (usesImperialUnits(preferredUnits)) 44..880 else 20..400,
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
            .navigationBarsPadding()
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
fun ScreenHeader(
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
fun ProfileDetailRow(label: String, value: String) {
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
fun SettingsListEntry(
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
fun SettingsOptionRow(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualEntryPlaceholderScreen(navController: NavController, viewModel: SatiationViewModel) {
    val editingMeal = viewModel.editingMeal
    val colorScheme = MaterialTheme.colorScheme
    var mealName by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var fats by remember { mutableStateOf("") }
    val todayStart = remember { startOfDayMillis(System.currentTimeMillis()) }
    var useCurrentDate by rememberSaveable(editingMeal?.meal?.mealId) { mutableStateOf(true) }
    var selectedLogDay by rememberSaveable(editingMeal?.meal?.mealId) { mutableLongStateOf(todayStart) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showPresetSavePrompt by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    LaunchedEffect(editingMeal?.meal?.mealId) {
        val mealToEdit = editingMeal
        if (mealToEdit == null) {
            mealName = ""
            category = ""
            notes = ""
            calories = ""
            protein = ""
            carbs = ""
            fats = ""
            useCurrentDate = true
            selectedLogDay = todayStart
        } else {
            val primaryItem = mealToEdit.items.firstOrNull()
            mealName = primaryItem?.name ?: mealToEdit.items.joinToString { it.name }
            category = primaryItem?.category.orEmpty()
            notes = mealToEdit.meal.notes.orEmpty()
            calories = mealToEdit.meal.totalCalories.toInt().toString()
            protein = mealToEdit.meal.totalProteinGrams.toInt().toString()
            carbs = mealToEdit.meal.totalCarbsGrams.toInt().toString()
            fats = mealToEdit.meal.totalFatsGrams.toInt().toString()
            selectedLogDay = startOfDayMillis(mealToEdit.meal.loggedAtEpochMillis)
            useCurrentDate = selectedLogDay == todayStart
        }
    }

    BackHandler {
        viewModel.clearMealDraft()
        navController.popBackStack()
    }

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
                    IconButton(
                        onClick = {
                            viewModel.clearMealDraft()
                            navController.popBackStack()
                        },
                        modifier = Modifier.offset(x = (-12).dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = colorScheme.onBackground)
                    }
                    Text(
                        if (editingMeal == null) "Manual Entry" else "Edit Meal",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onBackground
                    )
                }
            }
            item {
                Text(
                    if (editingMeal == null) {
                        "Log a meal directly into Room, or reuse a saved preset food when offline or when scanning is unavailable."
                    } else {
                        "Update the meal details, macros, notes, and logging date for this entry."
                    },
                    color = colorScheme.onSurfaceVariant
                )
            }
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Logging Date", fontWeight = FontWeight.Bold, color = colorScheme.onSurface)
                                Text(
                                    if (useCurrentDate) {
                                        "This meal will be saved to today."
                                    } else {
                                        "Selected day: ${formatLongDayLabel(selectedLogDay)}"
                                    },
                                    color = colorScheme.onSurfaceVariant,
                                    fontSize = 13.sp
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = useCurrentDate,
                                    onCheckedChange = { checked ->
                                        useCurrentDate = checked
                                        if (checked) {
                                            selectedLogDay = todayStart
                                        }
                                    }
                                )
                                Text("Use Current Date", color = colorScheme.onSurfaceVariant, fontSize = 13.sp)
                            }
                        }
                        if (!useCurrentDate) {
                            OutlinedButton(onClick = { showDatePicker = true }) {
                                Text("Choose Date")
                            }
                        }
                    }
                }
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
                            val now = System.currentTimeMillis()
                            val referenceTime = editingMeal?.meal?.loggedAtEpochMillis ?: now
                            val referenceTimeOfDay = (referenceTime - startOfDayMillis(referenceTime))
                                .coerceIn(0L, 86_399_999L)
                            val loggedAt = if (useCurrentDate) {
                                now
                            } else {
                                selectedLogDay + referenceTimeOfDay
                            }
                            val mealLog = MealLog(
                                mealId = editingMeal?.meal?.mealId ?: 0,
                                loggedAtEpochMillis = loggedAt,
                                sourceType = "manual",
                                totalCalories = parsedCalories,
                                totalProteinGrams = parsedProtein,
                                totalCarbsGrams = parsedCarbs,
                                totalFatsGrams = parsedFats,
                                notes = notes.trim().ifEmpty { null }
                            )
                            val item = MealItem(
                                mealId = editingMeal?.meal?.mealId ?: 0,
                                name = trimmedMealName,
                                category = category.trim().ifEmpty { null },
                                calories = parsedCalories,
                                proteinGrams = parsedProtein,
                                carbsGrams = parsedCarbs,
                                fatsGrams = parsedFats
                            )
                            viewModel.saveMeal(mealLog, listOf(item))
                            showPresetSavePrompt = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text(if (editingMeal == null) "Save Meal" else "Update Meal")
                }
            }
            if (editingMeal != null) {
                item {
                    OutlinedButton(
                        onClick = {
                            val trimmedMealName = mealName.trim()
                            if (trimmedMealName.isNotBlank()) {
                                viewModel.seedPresetDraft(
                                    name = trimmedMealName,
                                    category = category.trim().ifEmpty { null },
                                    calories = calories.toDoubleOrNull() ?: 0.0,
                                    protein = protein.toDoubleOrNull() ?: 0.0,
                                    carbs = carbs.toDoubleOrNull() ?: 0.0,
                                    fats = fats.toDoubleOrNull() ?: 0.0,
                                    notes = notes.trim().ifEmpty { null }
                                )
                                navController.navigate("food_types")
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    ) {
                        Text("Save As Preset Meal")
                    }
                }
            }
        }
        PassiveScrollbar(listState = listState)
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedLogDay)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { selectedLogDay = startOfDayMillis(it) }
                        showDatePicker = false
                    }
                ) {
                    Text("Use Date")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showPresetSavePrompt) {
        AlertDialog(
            onDismissRequest = { showPresetSavePrompt = false },
            title = { Text("Save As Preset Meal?") },
            text = { Text("This meal has been saved. Do you also want to open the preset editor with this meal prefilled?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        val trimmedMealName = mealName.trim()
                        showPresetSavePrompt = false
                        if (trimmedMealName.isNotBlank()) {
                            viewModel.seedPresetDraft(
                                name = trimmedMealName,
                                category = category.trim().ifEmpty { null },
                                calories = calories.toDoubleOrNull() ?: 0.0,
                                protein = protein.toDoubleOrNull() ?: 0.0,
                                carbs = carbs.toDoubleOrNull() ?: 0.0,
                                fats = fats.toDoubleOrNull() ?: 0.0,
                                notes = notes.trim().ifEmpty { null }
                            )
                        }
                        viewModel.clearMealDraft()
                        navController.navigate("food_types")
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showPresetSavePrompt = false
                        viewModel.clearMealDraft()
                        navController.popBackStack()
                    }
                ) {
                    Text("No")
                }
            }
        )
    }
}

@Composable
private fun MealCard(
    meal: MealWithItems,
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null
) {
    val itemSummary = meal.items.joinToString { it.name }
    val title = itemSummary.ifBlank { "Meal" }
    val colorScheme = MaterialTheme.colorScheme
    var showDeleteConfirmation by remember(meal.meal.mealId) { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    title,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    onEdit?.let {
                        TextButton(onClick = it) {
                            Text("Edit")
                        }
                    }
                    onDelete?.let {
                        TextButton(onClick = { showDeleteConfirmation = true }) {
                            Text("Delete", color = Color(0xFFD64A4A))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(formatTime(meal.meal.loggedAtEpochMillis), color = colorScheme.onSurfaceVariant, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(formatCalories(meal.meal.totalCalories), color = SatiationOrange, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("P ${formatMacroValue(meal.meal.totalProteinGrams)}", color = appGreen(), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("C ${formatMacroValue(meal.meal.totalCarbsGrams)}", color = DarkCarbsColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("F ${formatMacroValue(meal.meal.totalFatsGrams)}", color = DarkFatsColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Delete Meal?") },
            text = {
                Text("This will remove this meal from the day. Do you want to continue?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                        onDelete?.invoke()
                    }
                ) {
                    Text("Delete", color = Color(0xFFD64A4A))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

private data class ProgressDaySnapshot(
    val startMillis: Long,
    val dayKey: String,
    val compactLabel: String,
    val longLabel: String,
    val mealCount: Int,
    val totalCalories: Double,
    val proteinGrams: Double,
    val carbsGrams: Double,
    val fatsGrams: Double,
    val weightKg: Double?,
    val isToday: Boolean
)

private data class WeekdayMealPattern(
    val label: String,
    val mealCount: Int
)

private data class ChartTooltipData(
    val title: String,
    val value: String
)

private fun progressSnapshotForDay(
    dayStart: Long,
    snapshotsByStart: Map<Long, ProgressDaySnapshot>,
    weightHistory: List<WeightLog>
): ProgressDaySnapshot {
    return snapshotsByStart[dayStart] ?: ProgressDaySnapshot(
        startMillis = dayStart,
        dayKey = dayKeyFromMillis(dayStart),
        compactLabel = formatCompactDayLabel(dayStart),
        longLabel = formatLongDayLabel(dayStart),
        mealCount = 0,
        totalCalories = 0.0,
        proteinGrams = 0.0,
        carbsGrams = 0.0,
        fatsGrams = 0.0,
        weightKg = weightHistory
            .filter { dayKeyFromMillis(it.loggedAtEpochMillis) == dayKeyFromMillis(dayStart) }
            .maxByOrNull { it.loggedAtEpochMillis }
            ?.weightKg,
        isToday = dayStart == startOfDayMillis(System.currentTimeMillis())
    )
}

@Composable
private fun ProgressDestinationCard(
    title: String,
    description: String,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(title, fontWeight = FontWeight.Bold, color = colorScheme.onSurface)
                    Text(description, color = colorScheme.onSurfaceVariant, fontSize = 13.sp)
                }
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = colorScheme.onSurfaceVariant
                )
            }
            content()
        }
    }
}

@Composable
private fun ProgressPageHeader(title: String, subtitle: String, onBack: () -> Unit) {
    val colorScheme = MaterialTheme.colorScheme

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack, modifier = Modifier.offset(x = (-12).dp)) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = colorScheme.onBackground
                )
            }
            Text(title, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = colorScheme.onBackground)
        }
        Text(subtitle, color = colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ProgressAnimatedSection(
    animationKey: Any,
    staggerIndex: Int,
    content: @Composable () -> Unit
) {
    var animateIn by remember(animationKey) { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (animateIn) 1f else 0f,
        animationSpec = tween(durationMillis = 180, delayMillis = staggerIndex * 55),
        label = "ProgressSectionAlpha$staggerIndex"
    )
    val scale by animateFloatAsState(
        targetValue = if (animateIn) 1f else 0.96f,
        animationSpec = tween(durationMillis = 210, delayMillis = staggerIndex * 55),
        label = "ProgressSectionScale$staggerIndex"
    )
    val offsetY by animateDpAsState(
        targetValue = if (animateIn) 0.dp else 18.dp,
        animationSpec = tween(durationMillis = 210, delayMillis = staggerIndex * 55),
        label = "ProgressSectionOffset$staggerIndex"
    )

    LaunchedEffect(animationKey) {
        animateIn = false
        delay(32)
        animateIn = true
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = offsetY)
            .graphicsLayer(
                alpha = alpha,
                scaleX = scale,
                scaleY = scale
            )
    ) {
        content()
    }
}

@Composable
private fun ProgressCalendarBrowserCard(
    rangeStartDay: Long,
    rangeEndDay: Long,
    onOpenCalendar: () -> Unit,
    currentDayLabel: String,
    onQuickRangeSelected: (String) -> Unit,
    showAllTime: Boolean
) {
    val colorScheme = MaterialTheme.colorScheme
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val quickLabelSize = when {
        screenWidth < 360 -> 9.sp
        screenWidth < 400 -> 10.sp
        else -> 11.sp
    }
    val todayStart = remember { startOfDayMillis(System.currentTimeMillis()) }
    val quickRangeSelection = remember(rangeStartDay, rangeEndDay, showAllTime, todayStart) {
        when {
            rangeEndDay != todayStart -> null
            rangeStartDay == addDays(todayStart, -6) -> "week"
            rangeStartDay == addDays(todayStart, -29) -> "month"
            showAllTime -> "all_time"
            rangeStartDay == addDays(todayStart, -364) -> "year"
            else -> null
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Calendar", fontWeight = FontWeight.Bold, color = colorScheme.onSurface, fontSize = 16.sp)
                    Text(
                        formatDateRangeLabel(rangeStartDay, endOfDayMillis(rangeEndDay)),
                        color = colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                    Text(
                        "Current Day: $currentDayLabel",
                        color = colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                }
                OutlinedButton(onClick = onOpenCalendar) {
                    Text("Choose Range", fontSize = 13.sp)
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    ProgressRangeButton(
                        label = "Past Week",
                        labelFontSize = quickLabelSize,
                        isSelected = quickRangeSelection == "week",
                        onClick = { onQuickRangeSelected("week") }
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    ProgressRangeButton(
                        label = "Past Month",
                        labelFontSize = quickLabelSize,
                        isSelected = quickRangeSelection == "month",
                        onClick = { onQuickRangeSelected("month") }
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    ProgressRangeButton(
                        label = if (showAllTime) "All Time" else "Past Year",
                        labelFontSize = quickLabelSize,
                        isSelected = quickRangeSelection == if (showAllTime) "all_time" else "year",
                        onClick = { onQuickRangeSelected(if (showAllTime) "all_time" else "year") }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProgressRangeButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    labelFontSize: TextUnit = 12.sp
) {
    if (isSelected) {
        Button(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
            Text(label, fontSize = labelFontSize, maxLines = 1)
        }
    } else {
        OutlinedButton(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
            Text(label, fontSize = labelFontSize, maxLines = 1)
        }
    }
}

@Composable
private fun ChartTooltipCard(
    tooltip: ChartTooltipData,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(tooltip.title, color = colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text(tooltip.value, color = colorScheme.onSurfaceVariant, fontSize = 11.sp)
        }
    }
}

@Composable
private fun AnimatedChartTooltip(
    tooltip: ChartTooltipData?,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = tooltip != null,
        enter = fadeIn(animationSpec = tween(180)),
        exit = fadeOut(animationSpec = tween(180)),
        modifier = modifier
    ) {
        tooltip?.let { ChartTooltipCard(tooltip = it) }
    }
}

@Composable
private fun RowScope.WeekDaySelectorPill(
    day: ProgressDaySnapshot,
    isSelected: Boolean,
    isEnabled: Boolean,
    hasMarker: Boolean,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1f)
            .clickable(enabled = isEnabled, onClick = onClick)
            .graphicsLayer { alpha = if (isEnabled) 1f else 0.5f },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) colorScheme.primary.copy(alpha = 0.16f) else colorScheme.surfaceVariant
        ),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(1.dp, colorScheme.primary.copy(alpha = 0.7f))
        } else {
            null
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (hasMarker) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 8.dp)
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(appGreen())
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = if (hasMarker) 14.dp else 10.dp, start = 2.dp, end = 2.dp, bottom = 2.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                Text(
                    formatWeekdayLabel(day.startMillis),
                    color = colorScheme.onSurfaceVariant,
                    fontSize = 8.sp,
                    maxLines = 1,
                    textAlign = TextAlign.Center
                )
                Text(
                    formatDayOfMonth(day.startMillis),
                    color = colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    maxLines = 1,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun CalorieTrendCard(
    daySnapshots: List<ProgressDaySnapshot>,
    selectedDay: ProgressDaySnapshot,
    rangeLabel: String,
    onSelectDay: (ProgressDaySnapshot) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Calories Logged", fontWeight = FontWeight.Bold, color = colorScheme.onSurface)
            Text(
                "Daily calories inside the selected range: $rangeLabel",
                color = colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )
            if (daySnapshots.any { it.totalCalories > 0.0 }) {
                CalorieRangeChart(
                    daySnapshots = daySnapshots,
                    selectedDay = selectedDay,
                    onSelectDay = onSelectDay,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                )
                CalorieChartAxisLabels(daySnapshots = daySnapshots)
            } else {
                SparseTrendState(
                    title = "No calories logged in this range",
                    message = "Adjust the range or log meals to populate the trend chart."
                )
            }
        }
    }
}

@Composable
private fun CalorieRangeChart(
    daySnapshots: List<ProgressDaySnapshot>,
    selectedDay: ProgressDaySnapshot,
    onSelectDay: ((ProgressDaySnapshot) -> Unit)? = null,
    interactive: Boolean = true,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val maxCalories = daySnapshots.maxOfOrNull { it.totalCalories }?.coerceAtLeast(1.0) ?: 1.0

    var tooltipDayKey by remember(daySnapshots) { mutableStateOf<String?>(null) }
    var chartSize by remember { mutableStateOf(IntSize.Zero) }
    val tapModifier = if (interactive) {
        Modifier.pointerInput(daySnapshots, chartSize) {
            detectTapGestures { tapOffset ->
                if (daySnapshots.isEmpty() || chartSize.width == 0) return@detectTapGestures
                val slotWidth = chartSize.width / daySnapshots.size.coerceAtLeast(1).toFloat()
                val baselineY = chartSize.height - 14.dp.toPx()
                val barWidth = (slotWidth * 0.62f).coerceAtLeast(4.dp.toPx())
                val chartHeight = baselineY - 16.dp.toPx()
                val tappedIndex = (tapOffset.x / slotWidth).toInt().coerceIn(0, daySnapshots.lastIndex)
                val day = daySnapshots[tappedIndex]
                if (day.totalCalories <= 0.0) {
                    tooltipDayKey = null
                    return@detectTapGestures
                }
                val left = (tappedIndex * slotWidth) + ((slotWidth - barWidth) / 2f)
                val barHeight = ((day.totalCalories / maxCalories).toFloat() * chartHeight).coerceAtLeast(0f)
                val top = baselineY - barHeight
                val tappedInsideBar = tapOffset.x in left..(left + barWidth) && tapOffset.y in top..baselineY
                if (!tappedInsideBar) {
                    tooltipDayKey = null
                    return@detectTapGestures
                }
                tooltipDayKey = if (tooltipDayKey == day.dayKey) null else day.dayKey
                onSelectDay?.invoke(day)
            }
        }
    } else {
        Modifier
    }

    Box(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { chartSize = it }
                .then(tapModifier)
        ) {
            val slotWidth = size.width / daySnapshots.size.coerceAtLeast(1)
            val baselineY = size.height - 14.dp.toPx()
            val barWidth = (slotWidth * 0.62f).coerceAtLeast(4.dp.toPx())

            drawLine(
                color = colorScheme.outlineVariant.copy(alpha = 0.55f),
                start = Offset(0f, baselineY),
                end = Offset(size.width, baselineY),
                strokeWidth = 2.dp.toPx()
            )

            daySnapshots.forEachIndexed { index, day ->
                val left = (index * slotWidth) + ((slotWidth - barWidth) / 2f)
                val barHeight = ((day.totalCalories / maxCalories).toFloat() * (baselineY - 16.dp.toPx())).coerceAtLeast(0f)
                val top = baselineY - barHeight
                val isTooltipSelected = tooltipDayKey == day.dayKey
                val isDaySelected = selectedDay.dayKey == day.dayKey
                val barColor = if (day.totalCalories <= 0.0) {
                    colorScheme.secondary.copy(alpha = 0.2f)
                } else if (isTooltipSelected) {
                    SatiationOrange.darken()
                } else {
                    SatiationOrange.copy(alpha = 0.68f)
                }

                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(left, top),
                    size = Size(barWidth, barHeight),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(10.dp.toPx(), 10.dp.toPx())
                )

                if (isDaySelected) {
                    val indicatorX = left + (barWidth / 2f)
                    drawLine(
                        color = colorScheme.onSurface.copy(alpha = 0.85f),
                        start = Offset(indicatorX, top - 10.dp.toPx()),
                        end = Offset(indicatorX, baselineY),
                        strokeWidth = 3.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
            }
        }

        AnimatedChartTooltip(
            tooltip = daySnapshots.firstOrNull { it.dayKey == tooltipDayKey }?.let { day ->
                ChartTooltipData(
                    title = day.longLabel,
                    value = formatCalories(day.totalCalories)
                )
            },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 8.dp)
        )
    }
}

@Composable
private fun CalorieChartAxisLabels(daySnapshots: List<ProgressDaySnapshot>) {
    if (daySnapshots.size > 14) {
        return
    }

    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        daySnapshots.forEach { day ->
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    formatWeekdayInitial(day.startMillis),
                    color = colorScheme.onSurfaceVariant,
                    fontSize = 9.sp
                )
                Text(
                    formatDayOfMonth(day.startMillis),
                    color = colorScheme.onSurfaceVariant,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
private fun ProgressHighlightsCard(
    trackedDays: Int,
    totalMeals: Int,
    averageCalories: Double,
    currentLoggingStreak: Int,
    bestLoggingStreak: Int,
    latestWeight: Double?,
    currentBmi: Double?,
    favoriteFood: String?,
    highestCalorieDay: ProgressDaySnapshot?
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Trends & Stats", fontWeight = FontWeight.Bold, color = colorScheme.onSurface)
            TrendRow("Tracked Days", trackedDays.toString())
            TrendRow("Meals Logged", totalMeals.toString())
            TrendRow("Average Calories", formatCalories(averageCalories))
            TrendRow("Current Logging Streak", "$currentLoggingStreak day(s)")
            TrendRow("Best Logging Streak", "$bestLoggingStreak day(s)")
            TrendRow("Favorite Food", favoriteFood ?: "No data yet")
            TrendRow("Latest Weight", latestWeight?.let(::formatWeightKg) ?: "No entries")
            TrendRow("Current BMI", currentBmi?.let { "%.1f".format(Locale.US, it) } ?: "Unavailable")
            TrendRow(
                "Highest Calorie Day",
                highestCalorieDay?.let { "${it.compactLabel} | ${formatCalories(it.totalCalories)}" } ?: "No meal days yet"
            )
        }
    }
}

@Composable
private fun BmiGaugeCard(currentBmi: Double?) {
    val colorScheme = MaterialTheme.colorScheme
    val healthyColor = colorScheme.primary
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("BMI Gauge", fontWeight = FontWeight.Bold, color = colorScheme.onSurface)
            Text(
                "A quick visual cue for underweight, healthy, overweight, and obesity ranges.",
                color = colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )
            if (currentBmi == null) {
                SparseTrendState(
                    title = "BMI unavailable",
                    message = "Add height and current weight in your profile to calculate BMI."
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val startAngle = 180f
                        val sweep = 180f
                        val strokeWidth = 22.dp.toPx()
                        val diameter = size.minDimension * 0.9f
                        val topLeft = Offset((size.width - diameter) / 2f, size.height * 0.1f)
                        val arcSize = Size(diameter, diameter)
                        val ranges = listOf(
                            Triple(0.0, 18.5, Color(0xFFF2C84B)),
                            Triple(18.5, 25.0, healthyColor),
                            Triple(25.0, 30.0, Color(0xFFF08A3E)),
                            Triple(30.0, 40.0, Color(0xFFD65555))
                        )
                        ranges.forEach { (from, to, color) ->
                            val sweepAngle = (((to - from) / 40.0) * sweep).toFloat()
                            val start = startAngle + (((from) / 40.0) * sweep).toFloat()
                            drawArc(
                                color = color,
                                startAngle = start,
                                sweepAngle = sweepAngle,
                                useCenter = false,
                                topLeft = topLeft,
                                size = arcSize,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                        }
                        val normalized = currentBmi.coerceIn(0.0, 40.0) / 40.0
                        val angle = Math.toRadians((180 + (normalized * 180)).toDouble())
                        val center = Offset(size.width / 2f, topLeft.y + (diameter / 2f))
                        val pointerLength = (diameter / 2f) - 18.dp.toPx()
                        val pointerEnd = Offset(
                            x = center.x + (kotlin.math.cos(angle) * pointerLength).toFloat(),
                            y = center.y + (kotlin.math.sin(angle) * pointerLength).toFloat()
                        )
                        drawLine(
                            color = colorScheme.onSurface,
                            start = center,
                            end = pointerEnd,
                            strokeWidth = 4.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                        drawCircle(color = colorScheme.onSurface, radius = 6.dp.toPx(), center = center)
                    }
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("%.1f".format(Locale.US, currentBmi), fontWeight = FontWeight.Bold, color = colorScheme.onSurface)
                        Text(
                            when {
                                currentBmi < 18.5 -> "Underweight"
                                currentBmi < 25.0 -> "Healthy"
                                currentBmi < 30.0 -> "Overweight"
                                else -> "Obesity"
                            },
                            color = colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MacroSplitCard(
    proteinGrams: Double,
    carbsGrams: Double,
    fatsGrams: Double,
    rangeLabel: String
) {
    val colorScheme = MaterialTheme.colorScheme
    val totalMacros = proteinGrams + carbsGrams + fatsGrams

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Nutritional Split", fontWeight = FontWeight.Bold, color = colorScheme.onSurface)
            Text(
                "Updates with the selected date range: $rangeLabel",
                color = colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )
            if (totalMacros <= 0.0) {
                SparseTrendState(
                    title = "No macros in this range",
                    message = "Log meals inside the selected range to populate the pie chart."
                )
            } else {
                MacroSplitPieChart(
                    proteinGrams = proteinGrams,
                    carbsGrams = carbsGrams,
                    fatsGrams = fatsGrams,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                )
                MacroLegendRow(label = "Protein", value = proteinGrams, color = appGreen())
                MacroLegendRow(label = "Carbs", value = carbsGrams, color = DarkCarbsColor)
                MacroLegendRow(label = "Fats", value = fatsGrams, color = DarkFatsColor)
            }
        }
    }
}

@Composable
private fun MacroSplitPieChart(
    proteinGrams: Double,
    carbsGrams: Double,
    fatsGrams: Double,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val slices = listOf(
        Triple("Protein", proteinGrams, appGreen()),
        Triple("Carbs", carbsGrams, DarkCarbsColor),
        Triple("Fats", fatsGrams, DarkFatsColor)
    )
    val total = slices.sumOf { it.second }.coerceAtLeast(1.0)
    var selectedSlice by remember(proteinGrams, carbsGrams, fatsGrams) { mutableStateOf<String?>(null) }
    var chartSize by remember { mutableStateOf(IntSize.Zero) }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .onSizeChanged { chartSize = it }
                .pointerInput(slices, chartSize) {
                    detectTapGestures { tapOffset ->
                        if (chartSize == IntSize.Zero) return@detectTapGestures
                        val width = chartSize.width.toFloat()
                        val height = chartSize.height.toFloat()
                        val diameter = minOf(width, height) * 0.82f
                        val radius = diameter / 2f
                        val center = Offset(width / 2f, height / 2f)
                        val dx = tapOffset.x - center.x
                        val dy = tapOffset.y - center.y
                        val distance = kotlin.math.sqrt((dx * dx) + (dy * dy))

                        if (distance > radius || distance < radius * 0.34f) {
                            selectedSlice = null
                            return@detectTapGestures
                        }

                        var angle = Math.toDegrees(kotlin.math.atan2(dy.toDouble(), dx.toDouble())).toFloat() + 90f
                        if (angle < 0f) angle += 360f

                        var runningAngle = 0f
                        slices.filter { it.second > 0.0 }.forEach { (label, value, _) ->
                            val sweepAngle = ((value / total) * 360.0).toFloat()
                            if (angle in runningAngle..(runningAngle + sweepAngle)) {
                                selectedSlice = if (selectedSlice == label) null else label
                                return@detectTapGestures
                            }
                            runningAngle += sweepAngle
                        }
                        selectedSlice = null
                    }
                }
        ) {
            val diameter = size.minDimension * 0.82f
            val topLeft = Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)
            var startAngle = -90f

            slices.filter { it.second > 0.0 }.forEach { (label, value, color) ->
                val sweepAngle = ((value / total) * 360.0).toFloat()
                drawArc(
                    color = if (selectedSlice == label) color.darken() else color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    topLeft = topLeft,
                    size = Size(diameter, diameter)
                )
                startAngle += sweepAngle
            }

            drawCircle(
                color = colorScheme.surface,
                radius = diameter * 0.28f,
                center = Offset(size.width / 2f, size.height / 2f)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("${total.toInt()}g", fontWeight = FontWeight.Bold, color = colorScheme.onSurface)
            Text("Total Macros", color = colorScheme.onSurfaceVariant, fontSize = 12.sp)
        }

        AnimatedChartTooltip(
            tooltip = slices.firstOrNull { it.first == selectedSlice }?.let { (label, value, _) ->
                val percentage = ((value / total) * 100.0)
                ChartTooltipData(
                    title = label,
                    value = "${formatMacroValue(value)} | ${"%.0f".format(Locale.US, percentage)}%"
                )
            },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 8.dp)
        )
    }
}

@Composable
private fun MacroLegendRow(label: String, value: Double, color: Color) {
    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Text(label, color = colorScheme.onSurface)
        }
        Text(formatMacroValue(value), color = colorScheme.onSurface, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun FavoriteFoodsCard(topFoods: List<FoodFrequencySummary>) {
    val colorScheme = MaterialTheme.colorScheme
    val highestCount = topFoods.maxOfOrNull { it.occurrences }?.coerceAtLeast(1) ?: 1
    var selectedFoodName by remember(topFoods) { mutableStateOf<String?>(null) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
    ) {
        Box {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Favorite Foods", fontWeight = FontWeight.Bold, color = colorScheme.onSurface)
                Text(
                    "All-time food frequency. This chart does not change with the selected date range.",
                    color = colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )
                if (topFoods.isEmpty()) {
                    SparseTrendState(
                        title = "No foods logged yet",
                        message = "Once meals exist, the most frequently eaten foods will appear here."
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        topFoods.forEach { food ->
                            val isSelected = selectedFoodName == food.name
                            Column(
                                modifier = Modifier.clickable {
                                    selectedFoodName = if (isSelected) null else food.name
                                },
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(food.name, color = colorScheme.onSurface, fontWeight = FontWeight.Medium)
                                    Text(
                                        "${food.occurrences} time(s)",
                                        color = colorScheme.onSurfaceVariant,
                                        fontSize = 12.sp
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(12.dp)
                                        .clip(RoundedCornerShape(999.dp))
                                        .background(colorScheme.surfaceVariant)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(food.occurrences / highestCount.toFloat())
                                            .fillMaxHeight()
                                            .background(if (isSelected) SatiationOrange.darken() else SatiationOrange)
                                    )
                                }
                                Text(
                                    "${formatCalories(food.totalCalories)} total",
                                    color = colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
            AnimatedChartTooltip(
                tooltip = topFoods.firstOrNull { it.name == selectedFoodName }?.let { food ->
                    ChartTooltipData(
                        title = food.name,
                        value = "${food.occurrences} time(s) | ${formatCalories(food.totalCalories)}"
                    )
                },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 12.dp)
            )
        }
    }
}

@Composable
private fun WeightTrendCard(
    weightHistory: List<WeightLog>,
    rangeLabel: String,
    latestWeight: Double?,
    weightDelta: Double?
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Weight Trend", fontWeight = FontWeight.Bold, color = colorScheme.onSurface)
            Text(
                "Weight entries inside the selected range: $rangeLabel",
                color = colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )
            if (weightHistory.size >= 2) {
                WeightTrendChart(
                    weightHistory = weightHistory,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
                TrendRow("Latest Weight", latestWeight?.let(::formatWeightKg) ?: "No entries")
                TrendRow(
                    "Weight Change",
                    weightDelta?.let(::formatSignedWeightChange) ?: "Need at least two entries"
                )
            } else if (weightHistory.isNotEmpty()) {
                SparseTrendState(
                    title = latestWeight?.let(::formatWeightKg) ?: "No entries",
                    message = "Log at least one more weight entry in this range to draw the line."
                )
            } else {
                SparseTrendState(
                    title = "No weight entries in this range",
                    message = "Use the plus menu to log a new weight or expand the range."
                )
            }
        }
    }
}

@Composable
private fun WeightTrendChart(weightHistory: List<WeightLog>, modifier: Modifier = Modifier) {
    val colorScheme = MaterialTheme.colorScheme
    val latestPointColor = appGreen()
    val minWeight = weightHistory.minOfOrNull { it.weightKg } ?: 0.0
    val maxWeight = weightHistory.maxOfOrNull { it.weightKg } ?: 1.0
    val spread = (maxWeight - minWeight).takeIf { it > 0.0 } ?: 1.0

    var selectedWeightLogId by remember(weightHistory) { mutableStateOf<Long?>(null) }
    var chartSize by remember { mutableStateOf(IntSize.Zero) }

    Box(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { chartSize = it }
                .pointerInput(weightHistory, chartSize) {
                    detectTapGestures { tapOffset ->
                        if (weightHistory.isEmpty() || chartSize.width == 0) return@detectTapGestures
                        val leftPadding = 8.dp.toPx()
                        val rightPadding = 8.dp.toPx()
                        val chartWidth = chartSize.width - leftPadding - rightPadding
                        val closestIndex = weightHistory.indices.minByOrNull { index ->
                            val pointX = if (weightHistory.size == 1) {
                                leftPadding + (chartWidth / 2f)
                            } else {
                                leftPadding + (index / weightHistory.lastIndex.toFloat()) * chartWidth
                            }
                            kotlin.math.abs(tapOffset.x - pointX)
                        } ?: return@detectTapGestures
                        val closestPointX = if (weightHistory.size == 1) {
                            leftPadding + (chartWidth / 2f)
                        } else {
                            leftPadding + (closestIndex / weightHistory.lastIndex.toFloat()) * chartWidth
                        }
                        val touchRadius = 20.dp.toPx()
                        if (kotlin.math.abs(tapOffset.x - closestPointX) > touchRadius) {
                            selectedWeightLogId = null
                            return@detectTapGestures
                        }
                        val entry = weightHistory[closestIndex]
                        selectedWeightLogId = if (selectedWeightLogId == entry.weightLogId) null else entry.weightLogId
                    }
                }
        ) {
            if (weightHistory.isEmpty()) return@Canvas

            val leftPadding = 8.dp.toPx()
            val rightPadding = 8.dp.toPx()
            val topPadding = 12.dp.toPx()
            val bottomPadding = 18.dp.toPx()
            val chartWidth = size.width - leftPadding - rightPadding
            val chartHeight = size.height - topPadding - bottomPadding

            drawLine(
                color = colorScheme.outlineVariant.copy(alpha = 0.55f),
                start = Offset(leftPadding, size.height - bottomPadding),
                end = Offset(size.width - rightPadding, size.height - bottomPadding),
                strokeWidth = 2.dp.toPx()
            )

            val points = weightHistory.mapIndexed { index, entry ->
                val x = if (weightHistory.size == 1) {
                    leftPadding + (chartWidth / 2f)
                } else {
                    leftPadding + (index / (weightHistory.lastIndex).toFloat()) * chartWidth
                }
                val yProgress = ((entry.weightKg - minWeight) / spread).toFloat()
                val y = size.height - bottomPadding - (yProgress * chartHeight)
                Offset(x, y)
            }

            points.zipWithNext().forEach { (start, end) ->
                drawLine(
                    color = SatiationOrange,
                    start = start,
                    end = end,
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }

            points.forEachIndexed { index, point ->
                val isLatest = index == points.lastIndex
                val isSelected = selectedWeightLogId == weightHistory[index].weightLogId
                drawCircle(
                    color = when {
                        isSelected -> SatiationOrange.darken()
                        isLatest -> latestPointColor
                        else -> colorScheme.surface
                    },
                    radius = if (isSelected || isLatest) 5.dp.toPx() else 4.dp.toPx(),
                    center = point
                )
                drawCircle(
                    color = when {
                        isSelected -> SatiationOrange.darken()
                        isLatest -> latestPointColor
                        else -> SatiationOrange
                    },
                    radius = if (isSelected || isLatest) 7.dp.toPx() else 6.dp.toPx(),
                    center = point,
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }

        AnimatedChartTooltip(
            tooltip = weightHistory.firstOrNull { it.weightLogId == selectedWeightLogId }?.let { entry ->
                ChartTooltipData(
                    title = formatLongDayLabel(entry.loggedAtEpochMillis),
                    value = formatWeightKg(entry.weightKg)
                )
            },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 8.dp)
        )
    }
}

@Composable
private fun WeekdayMealPatternCard(
    pattern: List<WeekdayMealPattern>,
    rangeLabel: String
) {
    val colorScheme = MaterialTheme.colorScheme
    val maxMeals = pattern.maxOfOrNull { it.mealCount }?.coerceAtLeast(1) ?: 1
    var selectedWeekday by remember(pattern) { mutableStateOf<String?>(null) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
    ) {
        Box {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Meal Logging Rhythm", fontWeight = FontWeight.Bold, color = colorScheme.onSurface)
                Text(
                    "Meals logged by weekday inside the selected range: $rangeLabel",
                    color = colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )
                if (pattern.any { it.mealCount > 0 }) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        pattern.forEach { day ->
                            val isSelected = selectedWeekday == day.label
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable(enabled = day.mealCount > 0) {
                                        selectedWeekday = if (isSelected) null else day.label
                                    },
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    day.mealCount.toString(),
                                    color = colorScheme.onSurface,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    contentAlignment = Alignment.BottomCenter
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .fillMaxHeight(day.mealCount / maxMeals.toFloat())
                                            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                                            .background(if (isSelected) appGreen().darken() else appGreen())
                                    )
                                }
                                Text(day.label, color = colorScheme.onSurfaceVariant, fontSize = 12.sp)
                            }
                        }
                    }
                } else {
                    SparseTrendState(
                        title = "No weekday pattern yet",
                        message = "Log meals across a few days to reveal which weekdays carry most of your entries."
                    )
                }
            }
            AnimatedChartTooltip(
                tooltip = pattern.firstOrNull { it.label == selectedWeekday }?.let { day ->
                    ChartTooltipData(
                        title = day.label,
                        value = if (day.mealCount == 1) "1 meal logged" else "${day.mealCount} meals logged"
                    )
                },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 12.dp)
            )
        }
    }
}

@Composable
private fun DateSelectorCard(
    daySnapshots: List<ProgressDaySnapshot>,
    selectedDayStart: Long,
    onSelectDay: (Long) -> Unit,
    onJumpToToday: () -> Unit,
    annotationCounts: Map<String, Int>
) {
    val colorScheme = MaterialTheme.colorScheme
    val scrollState = rememberScrollState()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Date Browser", fontWeight = FontWeight.Bold, color = colorScheme.onSurface)
                    Text("Tap a day to inspect meals and totals.", color = colorScheme.onSurfaceVariant, fontSize = 13.sp)
                }
                TextButton(onClick = onJumpToToday) {
                    Text("Today")
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                daySnapshots.asReversed().forEach { day ->
                    val isSelected = day.startMillis == selectedDayStart
                    val markerCount = annotationCounts[day.dayKey] ?: 0
                    Card(
                        modifier = Modifier
                            .width(96.dp)
                            .clickable { onSelectDay(day.startMillis) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) colorScheme.primary.copy(alpha = 0.18f) else colorScheme.surfaceVariant
                        ),
                        border = if (isSelected) {
                            androidx.compose.foundation.BorderStroke(1.dp, colorScheme.primary.copy(alpha = 0.75f))
                        } else {
                            null
                        }
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(day.compactLabel, fontWeight = FontWeight.Bold, color = colorScheme.onSurface)
                            Text(
                                if (day.isToday) "Today" else "${
                                    if (day.mealCount > 0) "${day.mealCount} meal(s)" else "No meals"
                                }",
                                color = colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                            if (markerCount > 0) {
                                Text(
                                    "$markerCount marker(s)",
                                    color = colorScheme.primary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AnalyticsEmptyStateCard(title: String, message: String) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(title, fontWeight = FontWeight.Bold, color = colorScheme.onSurface)
            Text(message, color = colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SelectedDayOverviewCard(
    selectedDay: ProgressDaySnapshot,
    currentBmi: Double?,
    calorieTarget: Double,
    proteinTarget: Double,
    carbsTarget: Double,
    fatsTarget: Double
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(selectedDay.longLabel, fontWeight = FontWeight.Bold, color = colorScheme.onSurface)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SummaryPill("Meals", selectedDay.mealCount.toString())
                SummaryPill("Calories", formatCalories(selectedDay.totalCalories))
                SummaryPill("Weight", selectedDay.weightKg?.let(::formatWeightKg) ?: "No entry")
            }
            TrendRow(
                "Selected Day Status",
                if (selectedDay.mealCount > 0 || selectedDay.totalCalories > 0.0) "Data available" else "No meals logged"
            )
            TrendRow("Current BMI", currentBmi?.let { "%.1f".format(Locale.US, it) } ?: "Unavailable")
            MacroBar("Protein", selectedDay.proteinGrams.toFloat(), proteinTarget.toFloat(), appGreen())
            MacroBar("Carbs", selectedDay.carbsGrams.toFloat(), carbsTarget.toFloat(), DarkCarbsColor)
            MacroBar("Fats", selectedDay.fatsGrams.toFloat(), fatsTarget.toFloat(), DarkFatsColor)
            TrendRow(
                "Calorie Target",
                "${formatCalories(selectedDay.totalCalories)} / ${formatCalories(calorieTarget)}"
            )
        }
    }
}

@Composable
private fun ChartWindowCaption(firstLabel: String, lastLabel: String, summary: String) {
    val colorScheme = MaterialTheme.colorScheme

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(firstLabel, color = colorScheme.onSurfaceVariant, fontSize = 12.sp)
            Text(lastLabel, color = colorScheme.onSurfaceVariant, fontSize = 12.sp)
        }
        Text(summary, color = colorScheme.onSurface, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun SparseTrendState(title: String, message: String) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceVariant.copy(alpha = 0.75f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(title, fontWeight = FontWeight.Bold, color = colorScheme.onSurface)
            Text(message, color = colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun MacroDayBreakdownRow(day: ProgressDaySnapshot, isSelected: Boolean) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) colorScheme.primary.copy(alpha = 0.12f) else colorScheme.surfaceVariant.copy(alpha = 0.75f)
        ),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(1.dp, colorScheme.primary.copy(alpha = 0.65f))
        } else {
            null
        }
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(day.longLabel, fontWeight = FontWeight.Bold, color = colorScheme.onSurface)
                Text(
                    if (day.mealCount > 0) "${day.mealCount} meal(s)" else "No meals",
                    color = colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MacroSummaryPill("P", day.proteinGrams)
                MacroSummaryPill("C", day.carbsGrams)
                MacroSummaryPill("F", day.fatsGrams)
                SummaryPill("Kcal", formatCalories(day.totalCalories))
            }
        }
    }
}

@Composable
private fun AnnotationCard(
    selectedDayLabel: String,
    annotations: List<String>,
    onAddAnnotation: () -> Unit,
    onDeleteAnnotation: (Int) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Markers & Notes", fontWeight = FontWeight.Bold, color = colorScheme.onSurface)
                    Text(selectedDayLabel, color = colorScheme.onSurfaceVariant, fontSize = 13.sp)
                }
                Button(onClick = onAddAnnotation) {
                    Text("Add Marker")
                }
            }

            if (annotations.isEmpty()) {
                Text("No markers added for this day yet.", color = colorScheme.onSurfaceVariant)
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    annotations.forEachIndexed { index, note ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceVariant.copy(alpha = 0.8f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(note, color = colorScheme.onSurface)
                                TextButton(
                                    onClick = { onDeleteAnnotation(index) },
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Text("Delete")
                                }
                            }
                        }
                    }
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
                    Text("P ${formatMacroValue(preset.proteinGrams)}", color = appGreen(), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("C ${formatMacroValue(preset.carbsGrams)}", color = DarkCarbsColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
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
fun EmptyStateCard(message: String) {
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
fun settingsPanelColor(): Color {
    val colorScheme = MaterialTheme.colorScheme
    return if (colorScheme.background.luminance() > 0.5f) {
        Color(0xFFFFFCF5)
    } else {
        colorScheme.surfaceVariant
    }
}

@Composable
private fun appGreen(): Color {
    return MaterialTheme.colorScheme.primary
}

@Composable
private fun PassiveScrollbar(listState: LazyListState) {
    return
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
private fun SummaryPill(label: String, value: String) {
    val colorScheme = MaterialTheme.colorScheme

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = colorScheme.onSurfaceVariant, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, fontWeight = FontWeight.Bold, color = colorScheme.onSurface)
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

fun formatWeightKg(weightKg: Double): String {
    return formatWeightForDisplay(weightKg)
}

fun formatHeightCm(heightCm: Double): String {
    return formatHeightForDisplay(heightCm)
}

fun formatCalories(calories: Double): String {
    return "${calories.toInt()} Kcal"
}

fun formatWholeWeightKg(weightKg: Int): String = formatWholeWeightForDisplay(weightKg)

fun formatWholeHeightCm(heightCm: Int): String = formatWholeHeightForDisplay(heightCm)

private val DarkFatsColor = Color(0xFF8A7AB4)
private val DarkCarbsColor = Color(0xFFB07A00)

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

fun formatTime(epochMillis: Long): String {
    return SimpleDateFormat("h:mm a", Locale.US).format(Date(epochMillis))
}

private fun currentDayRangeMillis(): Pair<Long, Long> {
    val start = startOfDayMillis(System.currentTimeMillis())
    return start to endOfDayMillis(start)
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

private fun startOfDayMillis(epochMillis: Long): Long {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = epochMillis
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return calendar.timeInMillis
}

private fun endOfDayMillis(epochMillis: Long): Long {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = startOfDayMillis(epochMillis)
        add(Calendar.DAY_OF_YEAR, 1)
        add(Calendar.MILLISECOND, -1)
    }
    return calendar.timeInMillis
}

private fun addDays(epochMillis: Long, days: Int): Long {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = startOfDayMillis(epochMillis)
        add(Calendar.DAY_OF_YEAR, days)
    }
    return calendar.timeInMillis
}

private fun startOfWeekMillis(epochMillis: Long): Long {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = startOfDayMillis(epochMillis)
    }
    val firstDayOfWeek = calendar.firstDayOfWeek
    while (calendar.get(Calendar.DAY_OF_WEEK) != firstDayOfWeek) {
        calendar.add(Calendar.DAY_OF_YEAR, -1)
    }
    return startOfDayMillis(calendar.timeInMillis)
}

private fun dayStartSeries(startMillis: Long, count: Int): List<Long> {
    return buildList {
        repeat(count) { index ->
            add(addDays(startMillis, index))
        }
    }
}

private fun dayStartSeriesBetween(startMillis: Long, endMillis: Long): List<Long> {
    val totalDays = daysBetweenInclusive(startMillis, endMillis)
    return dayStartSeries(startOfDayMillis(startMillis), totalDays)
}

private fun daysBetweenInclusive(startMillis: Long, endMillis: Long): Int {
    val safeStart = startOfDayMillis(minOf(startMillis, endMillis))
    val safeEnd = startOfDayMillis(maxOf(startMillis, endMillis))
    var count = 1
    var cursor = safeStart
    while (cursor < safeEnd) {
        cursor = addDays(cursor, 1)
        count += 1
    }
    return count
}

private fun trailingDayStartMillis(days: Int): List<Long> {
    val todayStart = startOfDayMillis(System.currentTimeMillis())
    val calendar = Calendar.getInstance().apply { timeInMillis = todayStart }
    repeat(days - 1) {
        calendar.add(Calendar.DAY_OF_YEAR, -1)
    }
    return buildList {
        repeat(days) {
            add(calendar.timeInMillis)
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
    }
}

private fun buildProgressDaySnapshots(
    dayStarts: List<Long>,
    macroHistory: List<DailyMacroTotals>,
    mealSummaries: List<DailyMealSummary>,
    weightHistory: List<WeightLog>
): List<ProgressDaySnapshot> {
    val macroByDay = macroHistory.associateBy { it.day }
    val mealSummaryByDay = mealSummaries.associateBy { it.day }
    val weightByDay = weightHistory
        .groupBy { dayKeyFromMillis(it.loggedAtEpochMillis) }
        .mapValues { (_, weights) -> weights.maxByOrNull { it.loggedAtEpochMillis }?.weightKg }
    val todayStart = startOfDayMillis(System.currentTimeMillis())

    return dayStarts.map { dayStart ->
        val dayKey = dayKeyFromMillis(dayStart)
        val macro = macroByDay[dayKey]
        val summary = mealSummaryByDay[dayKey]
        ProgressDaySnapshot(
            startMillis = dayStart,
            dayKey = dayKey,
            compactLabel = formatCompactDayLabel(dayStart),
            longLabel = formatLongDayLabel(dayStart),
            mealCount = summary?.mealCount ?: 0,
            totalCalories = macro?.calories ?: summary?.calories ?: 0.0,
            proteinGrams = macro?.proteinGrams ?: summary?.proteinGrams ?: 0.0,
            carbsGrams = macro?.carbsGrams ?: summary?.carbsGrams ?: 0.0,
            fatsGrams = macro?.fatsGrams ?: summary?.fatsGrams ?: 0.0,
            weightKg = weightByDay[dayKey],
            isToday = dayStart == todayStart
        )
    }
}

private fun currentLoggingStreak(daySnapshots: List<ProgressDaySnapshot>): Int {
    var streak = 0
    for (day in daySnapshots.asReversed()) {
        if (dayHasLoggedData(day)) {
            streak += 1
        } else if (streak > 0) {
            break
        }
    }
    return streak
}

private fun longestLoggingStreak(daySnapshots: List<ProgressDaySnapshot>): Int {
    var longest = 0
    var current = 0

    daySnapshots.forEach { day ->
        if (dayHasLoggedData(day)) {
            current += 1
            if (current > longest) {
                longest = current
            }
        } else {
            current = 0
        }
    }

    return longest
}

private fun dayHasLoggedData(day: ProgressDaySnapshot): Boolean {
    return day.mealCount > 0 || day.totalCalories > 0.0 || day.weightKg != null
}

private fun buildWeekdayMealPattern(daySnapshots: List<ProgressDaySnapshot>): List<WeekdayMealPattern> {
    val orderedWeekdays = dayStartSeries(startOfWeekMillis(System.currentTimeMillis()), 7)
        .map(::formatWeekdayLabel)
    return orderedWeekdays.map { label ->
        WeekdayMealPattern(
            label = label,
            mealCount = daySnapshots
                .filter { formatWeekdayLabel(it.startMillis) == label }
                .sumOf { it.mealCount }
        )
    }
}

private fun dayKeyFromMillis(epochMillis: Long): String {
    return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date(epochMillis))
}

private fun formatCompactDayLabel(epochMillis: Long): String {
    return SimpleDateFormat("EEE d", Locale.US).format(Date(epochMillis))
}

fun formatLongDayLabel(epochMillis: Long): String {
    return formatDateForPreference(epochMillis, withWeekday = true)
}

private fun formatWeekdayLabel(epochMillis: Long): String {
    return SimpleDateFormat("EEE", Locale.US).format(Date(epochMillis))
}

private fun formatWeekdayInitial(epochMillis: Long): String {
    return SimpleDateFormat("EEEEE", Locale.US).format(Date(epochMillis))
}

private fun formatDayOfMonth(epochMillis: Long): String {
    return SimpleDateFormat("d", Locale.US).format(Date(epochMillis))
}

fun formatDateRangeLabel(startMillis: Long, endMillis: Long): String {
    val start = formatDateForPreference(startMillis, withWeekday = false)
    val end = formatDateForPreference(endMillis, withWeekday = false)
    return "$start to $end"
}

private fun formatCompactCalorieLabel(calories: Double): String {
    return "${calories.toInt()} cal"
}

private fun formatLiveRangeSelection(startMillis: Long?, endMillis: Long?): String {
    val start = startMillis?.let(::startOfDayMillis)
    val end = endMillis?.let(::startOfDayMillis)
    return when {
        start == null -> "Choose a start and end date."
        end == null -> "Start: ${formatLongDayLabel(start)}. Tap the same day again or choose an end date."
        start == end -> "Showing ${formatLongDayLabel(start)} only."
        else -> "Showing ${formatDateRangeLabel(start, endOfDayMillis(end))}."
    }
}

private fun formatSignedWeightChange(weightDelta: Double): String {
    val prefix = if (weightDelta > 0) "+" else ""
    return prefix + formatWeightKg(weightDelta)
}

private fun Color.darken(multiplier: Float = 0.78f): Color {
    return Color(
        red = (red * multiplier).coerceIn(0f, 1f),
        green = (green * multiplier).coerceIn(0f, 1f),
        blue = (blue * multiplier).coerceIn(0f, 1f),
        alpha = alpha
    )
}
