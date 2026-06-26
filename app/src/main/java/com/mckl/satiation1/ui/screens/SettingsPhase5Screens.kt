package com.mckl.satiation1.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mckl.satiation1.formatDateForPreference
import com.mckl.satiation1.DATE_FORMAT_DAY_MONTH_YEAR
import com.mckl.satiation1.DATE_FORMAT_ISO
import com.mckl.satiation1.DATE_FORMAT_MONTH_DAY_YEAR
import com.mckl.satiation1.SatiationOrange
import com.mckl.satiation1.UNIT_IMPERIAL
import com.mckl.satiation1.UNIT_METRIC
import com.mckl.satiation1.backup.BackupSelection
import com.mckl.satiation1.database.AppSettings
import com.mckl.satiation1.database.MealWithItems
import com.mckl.satiation1.history.HistoryCalorieFilter
import com.mckl.satiation1.history.HistoryFilterState
import com.mckl.satiation1.history.HistoryRangeFilter
import com.mckl.satiation1.history.HistorySourceFilter
import com.mckl.satiation1.history.filterMeals
import com.mckl.satiation1.navigation.SatiationViewModel
import java.io.BufferedReader
import java.io.InputStreamReader

@Composable
fun SettingsMenuScreen(navController: NavController, viewModel: SatiationViewModel) {
    val panelColor = settingsPanelColor()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ScreenHeader(title = "Settings", onBack = { navController.popBackStack() })
        SettingsListEntry(
            title = "Display Units",
            description = "Choose metric or imperial measurements",
            containerColor = panelColor,
            onClick = { navController.navigate("display_units") }
        )
        SettingsListEntry(
            title = "Edit Nutrients",
            description = "Targets and preset foods",
            containerColor = panelColor,
            onClick = { navController.navigate("edit_nutrients") }
        )
        SettingsListEntry(
            title = "Appearance",
            description = "Theme selection for the app",
            containerColor = panelColor,
            onClick = { navController.navigate("appearance") }
        )
        SettingsListEntry(
            title = "Meal History",
            description = "Review, edit, or delete logged meals",
            containerColor = panelColor,
            onClick = { navController.navigate("history") }
        )
        SettingsListEntry(
            title = "Notifications",
            description = "Daily reminders for meals, weight, and macros",
            containerColor = panelColor,
            onClick = { navController.navigate("notifications") }
        )
        SettingsListEntry(
            title = "Advanced",
            description = "Import, export, clear data, and date formatting",
            containerColor = panelColor,
            onClick = { navController.navigate("advanced_settings") }
        )
    }
}

@Composable
fun DisplayUnitsScreen(navController: NavController, viewModel: SatiationViewModel) {
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
        ScreenHeader(title = "Display Units", onBack = { navController.popBackStack() })
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = panelColor)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Display Mode", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(
                    "Switch between metric and imperial units for profile, onboarding, and weight-related screens.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )
                AnimatedVisibility(
                    visible = true,
                    enter = slideInVertically(initialOffsetY = { -it / 3 }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { -it / 3 }) + fadeOut()
                ) {
                    UnitModeToggle(
                        useImperial = currentSettings.preferredUnits == UNIT_IMPERIAL,
                        onToggle = { useImperial ->
                            viewModel.saveSettings(
                                currentSettings.copy(
                                    preferredUnits = if (useImperial) UNIT_IMPERIAL else UNIT_METRIC
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun UnitModeToggle(
    useImperial: Boolean,
    onToggle: (Boolean) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, colorScheme.outlineVariant, RoundedCornerShape(18.dp))
            .background(colorScheme.surfaceVariant, RoundedCornerShape(18.dp))
            .padding(4.dp)
    ) {
        UnitModeToggleOption(
            label = "Metric",
            description = "kg / cm",
            selected = !useImperial,
            onClick = { onToggle(false) },
            modifier = Modifier.weight(1f)
        )
        UnitModeToggleOption(
            label = "Imperial",
            description = "lb / ft-in",
            selected = useImperial,
            onClick = { onToggle(true) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun UnitModeToggleOption(
    label: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    Column(
        modifier = modifier
            .background(
                color = if (selected) colorScheme.primary else androidx.compose.ui.graphics.Color.Transparent,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            color = if (selected) colorScheme.onPrimary else colorScheme.onSurfaceVariant
        )
        Text(
            text = description,
            fontSize = 12.sp,
            color = if (selected) colorScheme.onPrimary.copy(alpha = 0.88f) else colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun HistoryScreen(navController: NavController, viewModel: SatiationViewModel) {
    val listState = rememberLazyListState()
    val meals by viewModel.getAllMeals().collectAsState(initial = null)
    var filters by remember { mutableStateOf(HistoryFilterState()) }
    var selectedMeal by remember { mutableStateOf<MealWithItems?>(null) }
    val filteredMeals = remember(meals, filters) {
        meals?.let { filterMeals(it, filters) }.orEmpty()
    }

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
                ScreenHeader(title = "Meal History", onBack = { navController.popBackStack() })
            }
            item {
                Text(
                    "Search meals by name or notes, narrow the date range, filter by source, and reuse the same edit/delete flow used elsewhere in the app.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            item {
                OutlinedTextField(
                    value = filters.query,
                    onValueChange = { filters = filters.copy(query = it) },
                    label = { Text("Search meals or notes") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                HistoryFilterRow(
                    title = "Date Range",
                    options = listOf(
                        "Past Week" to (filters.range == HistoryRangeFilter.PAST_WEEK),
                        "Past Month" to (filters.range == HistoryRangeFilter.PAST_MONTH),
                        "All Time" to (filters.range == HistoryRangeFilter.ALL_TIME)
                    ),
                    onSelect = { label ->
                        filters = filters.copy(
                            range = when (label) {
                                "Past Week" -> HistoryRangeFilter.PAST_WEEK
                                "All Time" -> HistoryRangeFilter.ALL_TIME
                                else -> HistoryRangeFilter.PAST_MONTH
                            }
                        )
                    }
                )
            }
            item {
                HistoryFilterRow(
                    title = "Source",
                    options = listOf(
                        "All" to (filters.source == HistorySourceFilter.ALL),
                        "Manual" to (filters.source == HistorySourceFilter.MANUAL),
                        "AI Scan" to (filters.source == HistorySourceFilter.AI_SCAN)
                    ),
                    onSelect = { label ->
                        filters = filters.copy(
                            source = when (label) {
                                "Manual" -> HistorySourceFilter.MANUAL
                                "AI Scan" -> HistorySourceFilter.AI_SCAN
                                else -> HistorySourceFilter.ALL
                            }
                        )
                    }
                )
            }
            item {
                HistoryFilterRow(
                    title = "Calories",
                    options = listOf(
                        "All" to (filters.calories == HistoryCalorieFilter.ALL),
                        "<500" to (filters.calories == HistoryCalorieFilter.UNDER_500),
                        "500-1000" to (filters.calories == HistoryCalorieFilter.FROM_500_TO_1000),
                        "1000+" to (filters.calories == HistoryCalorieFilter.OVER_1000)
                    ),
                    onSelect = { label ->
                        filters = filters.copy(
                            calories = when (label) {
                                "<500" -> HistoryCalorieFilter.UNDER_500
                                "500-1000" -> HistoryCalorieFilter.FROM_500_TO_1000
                                "1000+" -> HistoryCalorieFilter.OVER_1000
                                else -> HistoryCalorieFilter.ALL
                            }
                        )
                    }
                )
            }
            if (meals == null) {
                item {
                    EmptyStateCard("Loading meal history...")
                }
            } else if (filteredMeals.isEmpty() && meals.orEmpty().isNotEmpty()) {
                item {
                    EmptyStateCard("No meals match the current filters.")
                }
            } else if (meals.orEmpty().isEmpty()) {
                item {
                    EmptyStateCard("No meals logged yet.")
                }
            } else {
                items(filteredMeals, key = { it.meal.mealId }) { meal ->
                    HistoryMealCard(
                        meal = meal,
                        onOpen = { selectedMeal = meal },
                        onEdit = {
                            viewModel.beginMealEdit(meal)
                            navController.navigate("manual_entry")
                        },
                        onDelete = { viewModel.deleteMeal(meal.meal.mealId) }
                    )
                }
            }
        }
    }

    selectedMeal?.let { meal ->
        AlertDialog(
            onDismissRequest = { selectedMeal = null },
            title = {
                Text(meal.items.firstOrNull()?.name ?: "Meal Details")
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "${formatDateForPreference(meal.meal.loggedAtEpochMillis, withWeekday = true)} • ${formatTime(meal.meal.loggedAtEpochMillis)}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text("Source: ${if (meal.meal.sourceType == "ai_scan") "AI Scan" else "Manual"}")
                    Text("Calories: ${formatCalories(meal.meal.totalCalories)}")
                    Text(
                        "Protein ${"%.0f".format(meal.meal.totalProteinGrams)}g | Carbs ${"%.0f".format(meal.meal.totalCarbsGrams)}g | Fats ${"%.0f".format(meal.meal.totalFatsGrams)}g",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    meal.meal.notes?.takeIf { it.isNotBlank() }?.let {
                        Text("Notes: $it")
                    }
                    Text("Items", fontWeight = FontWeight.Bold)
                    meal.items.forEach { item ->
                        Text(
                            "• ${item.name} (${item.category ?: "General"})",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = { selectedMeal = null }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun AdvancedSettingsScreen(navController: NavController, viewModel: SatiationViewModel) {
    val context = LocalContext.current
    val appSettings by viewModel.appSettings.collectAsState()
    val currentSettings = appSettings ?: AppSettings()
    var selection by remember { mutableStateOf(BackupSelection()) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var pendingAction by remember { mutableStateOf<AdvancedAction?>(null) }
    var pendingExportJson by remember { mutableStateOf<String?>(null) }

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        val exportJson = pendingExportJson
        if (uri == null || exportJson == null) {
            pendingExportJson = null
            return@rememberLauncherForActivityResult
        }
        runCatching {
            context.contentResolver.openOutputStream(uri)?.bufferedWriter()?.use { writer ->
                writer.write(exportJson)
            } ?: error("Unable to open export file.")
        }.onSuccess {
            statusMessage = "Exported the selected data successfully."
        }.onFailure {
            statusMessage = it.message ?: "Export failed."
        }
        pendingExportJson = null
    }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri == null) {
            return@rememberLauncherForActivityResult
        }
        val rawJson = runCatching {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).readText()
            } ?: error("Unable to open import file.")
        }

        rawJson.onSuccess { text ->
            viewModel.importAppData(text, selection) { result ->
                statusMessage = result.fold(
                    onSuccess = { "Imported the selected data successfully." },
                    onFailure = { it.message ?: "Import failed." }
                )
            }
        }.onFailure {
            statusMessage = it.message ?: "Import failed."
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ScreenHeader(title = "Advanced", onBack = { navController.popBackStack() })
        statusMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        SelectionCard(
            title = "Data Categories",
            description = "Choose which groups are affected by export, import, or clear actions.",
            selection = selection,
            onSelectionChange = { selection = it }
        )
        DateFormatCard(
            currentFormat = currentSettings.preferredDateFormat,
            onSelect = { formatKey ->
                viewModel.saveSettings(currentSettings.copy(preferredDateFormat = formatKey))
                statusMessage = "Saved the preferred date format."
            }
        )
        SettingsListEntry(
            title = "Export Selected Data",
            description = "Create a JSON backup of the selected categories",
            containerColor = settingsPanelColor(),
            onClick = { pendingAction = AdvancedAction.Export }
        )
        SettingsListEntry(
            title = "Import Selected Data",
            description = "Replace selected categories from a JSON backup file",
            containerColor = settingsPanelColor(),
            onClick = { pendingAction = AdvancedAction.Import }
        )
        SettingsListEntry(
            title = "Clear Selected Data",
            description = "Delete the selected categories from local storage",
            containerColor = settingsPanelColor(),
            onClick = { pendingAction = AdvancedAction.Clear }
        )
    }

    pendingAction?.let { action ->
        AlertDialog(
            onDismissRequest = { pendingAction = null },
            title = {
                Text(
                    when (action) {
                        AdvancedAction.Export -> "Export Selected Data?"
                        AdvancedAction.Import -> "Import Selected Data?"
                        AdvancedAction.Clear -> "Clear Selected Data?"
                    }
                )
            },
            text = {
                Text(
                    when (action) {
                        AdvancedAction.Export -> "The selected categories will be written to a JSON file."
                        AdvancedAction.Import -> "The selected categories will be replaced with data from the chosen JSON file."
                        AdvancedAction.Clear -> "The selected categories will be deleted from this device."
                    }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        pendingAction = null
                        when (action) {
                            AdvancedAction.Export -> {
                                viewModel.exportAppData(selection) { result ->
                                    result.onSuccess { exportJson ->
                                        pendingExportJson = exportJson
                                        exportLauncher.launch("satiation-backup.json")
                                    }.onFailure {
                                        statusMessage = it.message ?: "Export failed."
                                    }
                                }
                            }
                            AdvancedAction.Import -> importLauncher.launch(arrayOf("application/json"))
                            AdvancedAction.Clear -> {
                                viewModel.clearSelectedData(selection) { result ->
                                    statusMessage = result.fold(
                                        onSuccess = { "Cleared the selected data successfully." },
                                        onFailure = { it.message ?: "Unable to clear selected data." }
                                    )
                                }
                            }
                        }
                    }
                ) {
                    Text(
                        when (action) {
                            AdvancedAction.Clear -> "Confirm"
                            else -> "Continue"
                        }
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingAction = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

private enum class AdvancedAction {
    Export,
    Import,
    Clear
}

@Composable
private fun SelectionCard(
    title: String,
    description: String,
    selection: BackupSelection,
    onSelectionChange: (BackupSelection) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = settingsPanelColor())
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(description, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
            BackupSelectionRow("Profile", selection.profile) {
                onSelectionChange(selection.copy(profile = !selection.profile))
            }
            BackupSelectionRow("Settings", selection.settings) {
                onSelectionChange(selection.copy(settings = !selection.settings))
            }
            BackupSelectionRow("Meals", selection.meals) {
                onSelectionChange(selection.copy(meals = !selection.meals))
            }
            BackupSelectionRow("Weight Logs", selection.weights) {
                onSelectionChange(selection.copy(weights = !selection.weights))
            }
            BackupSelectionRow("Preset Foods", selection.presetFoods) {
                onSelectionChange(selection.copy(presetFoods = !selection.presetFoods))
            }
            BackupSelectionRow("Annotations", selection.annotations) {
                onSelectionChange(selection.copy(annotations = !selection.annotations))
            }
        }
    }
}

@Composable
private fun BackupSelectionRow(
    label: String,
    checked: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurface)
        Checkbox(checked = checked, onCheckedChange = { onToggle() })
    }
}

@Composable
private fun DateFormatCard(
    currentFormat: String,
    onSelect: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = settingsPanelColor())
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("Date Format", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            DateFormatOption("Day Month Year", "26 Jun 2026", currentFormat == DATE_FORMAT_DAY_MONTH_YEAR) {
                onSelect(DATE_FORMAT_DAY_MONTH_YEAR)
            }
            DateFormatOption("Month Day Year", "Jun 26 2026", currentFormat == DATE_FORMAT_MONTH_DAY_YEAR) {
                onSelect(DATE_FORMAT_MONTH_DAY_YEAR)
            }
            DateFormatOption("ISO", "2026-06-26", currentFormat == DATE_FORMAT_ISO) {
                onSelect(DATE_FORMAT_ISO)
            }
        }
    }
}

@Composable
private fun DateFormatOption(
    title: String,
    preview: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(title, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            Text(preview, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
        }
    }
}

@Composable
private fun HistoryMealCard(
    meal: MealWithItems,
    onOpen: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    var showDeleteConfirmation by remember(meal.meal.mealId) { mutableStateOf(false) }
    val mealTitle = meal.items.joinToString { it.name }.ifBlank { "Meal" }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(mealTitle, fontWeight = FontWeight.Bold, color = colorScheme.onSurface)
            Text(formatDateForHistory(meal.meal.loggedAtEpochMillis), color = colorScheme.onSurfaceVariant, fontSize = 13.sp)
            meal.meal.notes?.takeIf { it.isNotBlank() }?.let {
                Text(it, color = colorScheme.onSurfaceVariant, fontSize = 13.sp)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(formatCalories(meal.meal.totalCalories), color = SatiationOrange, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = onEdit) {
                        Text("Edit")
                    }
                    TextButton(onClick = { showDeleteConfirmation = true }) {
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Delete Meal?") },
            text = { Text("This will remove the selected meal from history.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                        onDelete()
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
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

@Composable
private fun HistoryFilterRow(
    title: String,
    options: List<Pair<String, Boolean>>,
    onSelect: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            options.forEach { (label, selected) ->
                TextButton(
                    onClick = { onSelect(label) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        label,
                        fontSize = 12.sp,
                        color = if (selected) SatiationOrange else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

private fun formatDateForHistory(epochMillis: Long): String {
    return "${formatLongDayLabel(epochMillis)} • ${formatTime(epochMillis)}"
}
