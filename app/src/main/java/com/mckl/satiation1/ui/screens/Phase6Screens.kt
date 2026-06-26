package com.mckl.satiation1.ui.screens

import android.Manifest
import android.graphics.Bitmap
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Image
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.mckl.satiation1.SatiationGreen
import com.mckl.satiation1.database.AppSettings
import com.mckl.satiation1.navigation.SatiationViewModel
import com.mckl.satiation1.reminders.ReminderType

@Composable
fun NotificationSettingsScreen(navController: NavController, viewModel: SatiationViewModel) {
    val context = LocalContext.current
    val appSettings by viewModel.appSettings.collectAsState()
    val currentSettings = appSettings ?: AppSettings()
    var editingReminder by remember { mutableStateOf<ReminderType?>(null) }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        statusMessage = if (granted) {
            "Notification permission granted."
        } else {
            "Notification permission was not granted. Reminders can be configured, but alerts will stay silent until permission is allowed."
        }
    }

    fun notificationsGranted(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    }

    fun updateSettings(transform: (AppSettings) -> AppSettings) {
        val updated = transform(currentSettings)
        viewModel.saveSettings(updated)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ScreenHeader(title = "Notifications", onBack = { navController.popBackStack() })
        Text(
            "Configure daily reminders for meal logging, weight entries, and a final macro check-in.",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (!notificationsGranted()) {
            SettingsListEntry(
                title = "Allow Notifications",
                description = "Required on newer Android versions before reminders can alert you.",
                containerColor = settingsPanelColor(),
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            )
        }
        statusMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
        }
        ReminderSettingCard(
            title = "Meal Logging Reminder",
            description = "Daily reminder to log breakfast, lunch, dinner, or anything you ate.",
            enabled = currentSettings.mealReminderEnabled,
            timeLabel = formatReminderTime(currentSettings.mealReminderHour, currentSettings.mealReminderMinute),
            onToggle = { enabled ->
                if (enabled && !notificationsGranted() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                updateSettings { it.copy(mealReminderEnabled = enabled) }
            },
            onEditTime = { editingReminder = ReminderType.MEAL_LOGGING }
        )
        ReminderSettingCard(
            title = "Weight Reminder",
            description = "Prompt yourself to log a weight entry at a consistent time.",
            enabled = currentSettings.weightReminderEnabled,
            timeLabel = formatReminderTime(currentSettings.weightReminderHour, currentSettings.weightReminderMinute),
            onToggle = { enabled ->
                if (enabled && !notificationsGranted() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                updateSettings { it.copy(weightReminderEnabled = enabled) }
            },
            onEditTime = { editingReminder = ReminderType.WEIGHT_LOGGING }
        )
        ReminderSettingCard(
            title = "Macro Check-In Reminder",
            description = "One last prompt to check calories and macros before the day ends.",
            enabled = currentSettings.macroReminderEnabled,
            timeLabel = formatReminderTime(currentSettings.macroReminderHour, currentSettings.macroReminderMinute),
            onToggle = { enabled ->
                if (enabled && !notificationsGranted() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                updateSettings { it.copy(macroReminderEnabled = enabled) }
            },
            onEditTime = { editingReminder = ReminderType.MACRO_CHECK_IN }
        )
    }

    editingReminder?.let { type ->
        val initialHour = when (type) {
            ReminderType.MEAL_LOGGING -> currentSettings.mealReminderHour
            ReminderType.WEIGHT_LOGGING -> currentSettings.weightReminderHour
            ReminderType.MACRO_CHECK_IN -> currentSettings.macroReminderHour
        }
        val initialMinute = when (type) {
            ReminderType.MEAL_LOGGING -> currentSettings.mealReminderMinute
            ReminderType.WEIGHT_LOGGING -> currentSettings.weightReminderMinute
            ReminderType.MACRO_CHECK_IN -> currentSettings.macroReminderMinute
        }
        ReminderTimeDialog(
            title = when (type) {
                ReminderType.MEAL_LOGGING -> "Meal Reminder Time"
                ReminderType.WEIGHT_LOGGING -> "Weight Reminder Time"
                ReminderType.MACRO_CHECK_IN -> "Macro Reminder Time"
            },
            initialHour = initialHour,
            initialMinute = initialMinute,
            onDismiss = { editingReminder = null },
            onConfirm = { hour, minute ->
                updateSettings {
                    when (type) {
                        ReminderType.MEAL_LOGGING -> it.copy(mealReminderHour = hour, mealReminderMinute = minute)
                        ReminderType.WEIGHT_LOGGING -> it.copy(weightReminderHour = hour, weightReminderMinute = minute)
                        ReminderType.MACRO_CHECK_IN -> it.copy(macroReminderHour = hour, macroReminderMinute = minute)
                    }
                }
                editingReminder = null
            }
        )
    }
}

@Composable
fun OnboardingApiKeyScreen(navController: NavController, viewModel: SatiationViewModel) {
    val appSettings by viewModel.appSettings.collectAsState()
    val currentSettings = appSettings ?: AppSettings()
    var apiKey by remember { mutableStateOf(currentSettings.geminiApiKey.orEmpty()) }
    var showSkipConfirmation by remember { mutableStateOf(false) }

    LaunchedEffect(currentSettings.geminiApiKey) {
        apiKey = currentSettings.geminiApiKey.orEmpty()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SatiationGreen)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text("Gemini API Key", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(
            "Add your Gemini API key now so AI plate analysis is ready on first use. This field stays visible and is not censored.",
            color = Color.White.copy(alpha = 0.92f)
        )
        OutlinedTextField(
            value = apiKey,
            onValueChange = { apiKey = it },
            label = { Text("Gemini API Key") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        Text(
            "You can leave this blank and continue, then come back later from Settings.",
            color = Color.White.copy(alpha = 0.86f),
            fontSize = 13.sp
        )
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {
                viewModel.saveSettings(currentSettings.copy(geminiApiKey = apiKey.ifBlank { null }))
                navController.navigate("onboarding_guide")
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Save And Continue", color = SatiationGreen, fontWeight = FontWeight.Bold)
        }
        OutlinedButton(
            onClick = { showSkipConfirmation = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Skip For Now", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }

    if (showSkipConfirmation) {
        AlertDialog(
            onDismissRequest = { showSkipConfirmation = false },
            title = { Text("Skip API Key Setup?") },
            text = { Text("You can still log meals manually. AI plate analysis will stay unavailable until you add the Gemini API key in Settings.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSkipConfirmation = false
                        navController.navigate("onboarding_guide")
                    }
                ) {
                    Text("Skip")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSkipConfirmation = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun OnboardingGuideScreen(navController: NavController, viewModel: SatiationViewModel) {
    AppGuideContent(
        title = "Quick Guide",
        actionLabel = "Finish Setup",
        onAction = {
            viewModel.completeOnboardingProfile()
            navController.navigate("main") {
                popUpTo("splash") { inclusive = true }
            }
        },
        onBack = { navController.popBackStack() }
    )
}

@Composable
fun AppGuideScreen(navController: NavController) {
    AppGuideContent(
        title = "App Guide",
        actionLabel = "Back To Settings",
        onAction = { navController.popBackStack() },
        onBack = { navController.popBackStack() }
    )
}

@Composable
private fun AppGuideContent(
    title: String,
    actionLabel: String,
    onAction: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ScreenHeader(title = title, onBack = onBack)
        GuideCard("Manual Logging", "Use the plus menu to create manual meals, edit them later, or save them as preset meals.")
        GuideCard("Camera & Gemini", "Scan a meal photo, review the AI output, then save it or convert it into a reusable preset.")
        GuideCard("Progress", "Use Calendar and Stats to inspect calorie history, macro splits, weight trends, BMI, and top foods.")
        GuideCard("Settings & API Key", "Profile changes, appearance, reminders, data export/import, and your Gemini API key all live under Settings.")
        Button(
            onClick = onAction,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(actionLabel)
        }
    }
}

@Composable
private fun GuideCard(title: String, description: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = settingsPanelColor())
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(description, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun ReminderSettingCard(
    title: String,
    description: String,
    enabled: Boolean,
    timeLabel: String,
    onToggle: (Boolean) -> Unit,
    onEditTime: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = settingsPanelColor())
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text(description, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                }
                Switch(checked = enabled, onCheckedChange = onToggle)
            }
            OutlinedButton(
                onClick = onEditTime,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Time: $timeLabel")
            }
        }
    }
}

@Composable
private fun ReminderTimeDialog(
    title: String,
    initialHour: Int,
    initialMinute: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    var hour by remember(initialHour) { mutableStateOf(initialHour.toString().padStart(2, '0')) }
    var minute by remember(initialMinute) { mutableStateOf(initialMinute.toString().padStart(2, '0')) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = hour,
                    onValueChange = { hour = it.filter(Char::isDigit).take(2) },
                    label = { Text("Hour (0-23)") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = minute,
                    onValueChange = { minute = it.filter(Char::isDigit).take(2) },
                    label = { Text("Minute (0-59)") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val parsedHour = hour.toIntOrNull()
                    val parsedMinute = minute.toIntOrNull()
                    if (parsedHour != null && parsedHour in 0..23 && parsedMinute != null && parsedMinute in 0..59) {
                        onConfirm(parsedHour, parsedMinute)
                    }
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
private fun AccentChoiceRow(
    title: String,
    selectedHex: String,
    onSelect: (String) -> Unit,
    onCustomClick: () -> Unit
) {
    val palette = listOf("#BDE064", "#FF7D5A", "#2AA7A1", "#4C7EFF", "#F4B942", "#E35D8F")
    val selectedColor = parseHexColor(selectedHex, Color.Gray)
    val isCustomSelection = palette.none { it.equals(selectedHex, ignoreCase = true) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            palette.forEach { hex ->
                val selected = selectedHex.equals(hex, ignoreCase = true)
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .border(
                            width = if (selected) 3.dp else 1.dp,
                            color = if (selected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outlineVariant,
                            shape = CircleShape
                        )
                        .padding(3.dp)
                        .clip(CircleShape)
                        .background(runCatching { Color(android.graphics.Color.parseColor(hex)) }.getOrDefault(Color.Gray))
                        .clickable { onSelect(hex) }
                )
            }
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .border(
                        width = if (isCustomSelection) 3.dp else 1.dp,
                        color = if (isCustomSelection) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outlineVariant,
                        shape = CircleShape
                    )
                    .padding(3.dp)
                    .clip(CircleShape)
                    .background(selectedColor)
                    .clickable { onCustomClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "HEX",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedColor.luminance() > 0.55f) Color.Black else Color.White
                )
            }
        }
    }
}

internal fun formatReminderTime(hour: Int, minute: Int): String {
    val normalizedHour = hour.coerceIn(0, 23)
    val normalizedMinute = minute.coerceIn(0, 59)
    val suffix = if (normalizedHour >= 12) "PM" else "AM"
    val hour12 = when (val value = normalizedHour % 12) {
        0 -> 12
        else -> value
    }
    return "%d:%02d %s".format(hour12, normalizedMinute, suffix)
}

@Composable
internal fun AppearanceAccentControls(
    currentSettings: AppSettings,
    onUpdate: (AppSettings) -> Unit
){
    var customTarget by remember { mutableStateOf<AccentTarget?>(null) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = settingsPanelColor())
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("Accent Colors", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(
                "Lower-priority polish: choose the primary and secondary accents used by the app theme.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )
            AccentChoiceRow(
                title = "Primary Accent",
                selectedHex = currentSettings.primaryAccentHex,
                onSelect = { onUpdate(currentSettings.copy(primaryAccentHex = it)) },
                onCustomClick = { customTarget = AccentTarget.PRIMARY }
            )
            AccentChoiceRow(
                title = "Secondary Accent",
                selectedHex = currentSettings.secondaryAccentHex,
                onSelect = { onUpdate(currentSettings.copy(secondaryAccentHex = it)) },
                onCustomClick = { customTarget = AccentTarget.SECONDARY }
            )
        }
    }
    customTarget?.let { target ->
        CustomAccentDialog(
            title = if (target == AccentTarget.PRIMARY) "Custom Primary Accent" else "Custom Secondary Accent",
            initialHex = if (target == AccentTarget.PRIMARY) currentSettings.primaryAccentHex else currentSettings.secondaryAccentHex,
            onDismiss = { customTarget = null },
            onConfirm = { hex ->
                customTarget = null
                if (target == AccentTarget.PRIMARY) {
                    onUpdate(currentSettings.copy(primaryAccentHex = hex))
                } else {
                    onUpdate(currentSettings.copy(secondaryAccentHex = hex))
                }
            }
        )
    }
}

private enum class AccentTarget {
    PRIMARY,
    SECONDARY
}

@Composable
private fun CustomAccentDialog(
    title: String,
    initialHex: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val initialColor = parseHexColor(initialHex, Color(0xFFBDE064))
    val initialHsv = remember(initialHex) { colorToHsv(initialColor) }
    var hue by remember(initialHex) { mutableStateOf(initialHsv[0]) }
    var saturation by remember(initialHex) { mutableStateOf(initialHsv[1]) }
    var value by remember(initialHex) { mutableStateOf(initialHsv[2]) }
    var hexInput by remember(initialHex) { mutableStateOf(colorToHex(initialColor)) }

    val previewColor = remember(hue, saturation, value) { colorFromHsv(hue, saturation, value) }

    LaunchedEffect(hue, saturation, value) {
        hexInput = colorToHex(previewColor)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                ColorWheelPicker(
                    hue = hue,
                    saturation = saturation,
                    value = value,
                    onChange = { newHue, newSaturation ->
                        hue = newHue
                        saturation = newSaturation
                    }
                )
                Text("Brightness", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Slider(
                    value = value,
                    onValueChange = { value = it },
                    valueRange = 0f..1f
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(previewColor)
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                    )
                    OutlinedTextField(
                        value = hexInput,
                        onValueChange = {
                            hexInput = it.uppercase()
                            parseHexColorOrNull(it)?.let { parsed ->
                                val hsv = colorToHsv(parsed)
                                hue = hsv[0]
                                saturation = hsv[1]
                                value = hsv[2]
                            }
                        },
                        label = { Text("Hex Color") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(colorToHex(previewColor)) }) {
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
private fun ColorWheelPicker(
    hue: Float,
    saturation: Float,
    value: Float,
    onChange: (Float, Float) -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp),
        contentAlignment = Alignment.Center
    ) {
        val density = LocalDensity.current
        val wheelSizeDp = minOf(maxWidth, 220.dp)
        val wheelSizePx = with(density) { wheelSizeDp.roundToPx().coerceAtLeast(160) }
        val wheelBitmap = remember(wheelSizePx, value) { buildColorWheelBitmap(wheelSizePx, value) }
        val markerOffset = remember(hue, saturation) {
            val radians = Math.toRadians(hue.toDouble())
            val radiusFraction = saturation.coerceIn(0f, 1f)
            val x = (Math.cos(radians) * radiusFraction).toFloat()
            val y = (Math.sin(radians) * radiusFraction).toFloat()
            x to y
        }

        Box(
            modifier = Modifier
                .size(wheelSizeDp)
                .pointerInput(value) {
                    detectTapGestures { offset ->
                        updateWheelSelection(offset.x, offset.y, size.width.toFloat(), size.height.toFloat(), onChange)
                    }
                }
                .pointerInput(value) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            updateWheelSelection(offset.x, offset.y, size.width.toFloat(), size.height.toFloat(), onChange)
                        },
                        onDrag = { change, _ ->
                            updateWheelSelection(change.position.x, change.position.y, size.width.toFloat(), size.height.toFloat(), onChange)
                        }
                    )
                }
        ) {
            Image(
                bitmap = wheelBitmap,
                contentDescription = "Accent color wheel",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(Color.Transparent)
                    .border(2.dp, Color.White, CircleShape)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(20.dp)
                    .graphicsLayer {
                        translationX = markerOffset.first * (wheelSizePx / 2f)
                        translationY = markerOffset.second * (wheelSizePx / 2f)
                    }
                    .clip(CircleShape)
                    .background(Color.Transparent)
                    .border(3.dp, Color.White, CircleShape)
            )
        }
    }
}

private fun updateWheelSelection(
    pointerX: Float,
    pointerY: Float,
    width: Float,
    height: Float,
    onChange: (Float, Float) -> Unit
) {
    val centerX = width / 2f
    val centerY = height / 2f
    val dx = pointerX - centerX
    val dy = pointerY - centerY
    val radius = minOf(width, height) / 2f
    val distance = kotlin.math.sqrt(dx * dx + dy * dy).coerceAtMost(radius)
    val saturation = (distance / radius).coerceIn(0f, 1f)
    val rawHue = Math.toDegrees(kotlin.math.atan2(dy.toDouble(), dx.toDouble())).toFloat()
    val hue = if (rawHue < 0f) rawHue + 360f else rawHue
    onChange(hue, saturation)
}

private fun buildColorWheelBitmap(sizePx: Int, value: Float) = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888).apply {
    val radius = sizePx / 2f
    for (x in 0 until sizePx) {
        for (y in 0 until sizePx) {
            val dx = x - radius
            val dy = y - radius
            val distance = kotlin.math.sqrt(dx * dx + dy * dy)
            if (distance > radius) {
                setPixel(x, y, android.graphics.Color.TRANSPARENT)
            } else {
                val saturation = (distance / radius).coerceIn(0f, 1f)
                val hue = ((Math.toDegrees(kotlin.math.atan2(dy.toDouble(), dx.toDouble())) + 360.0) % 360.0).toFloat()
                setPixel(x, y, android.graphics.Color.HSVToColor(floatArrayOf(hue, saturation, value.coerceIn(0f, 1f))))
            }
        }
    }
}.asImageBitmap()

private fun parseHexColor(rawHex: String, fallback: Color): Color {
    return parseHexColorOrNull(rawHex) ?: fallback
}

private fun parseHexColorOrNull(rawHex: String): Color? {
    val normalized = rawHex.trim().let {
        when {
            it.isEmpty() -> return null
            it.startsWith("#") -> it
            else -> "#$it"
        }
    }
    return runCatching { Color(android.graphics.Color.parseColor(normalized)) }.getOrNull()
}

private fun colorToHex(color: Color): String {
    return "#%02X%02X%02X".format(
        (color.red * 255).toInt().coerceIn(0, 255),
        (color.green * 255).toInt().coerceIn(0, 255),
        (color.blue * 255).toInt().coerceIn(0, 255)
    )
}

private fun colorToHsv(color: Color): FloatArray {
    val hsv = FloatArray(3)
    android.graphics.Color.colorToHSV(
        android.graphics.Color.rgb(
            (color.red * 255).toInt().coerceIn(0, 255),
            (color.green * 255).toInt().coerceIn(0, 255),
            (color.blue * 255).toInt().coerceIn(0, 255)
        ),
        hsv
    )
    return hsv
}

private fun colorFromHsv(hue: Float, saturation: Float, value: Float): Color {
    return Color(android.graphics.Color.HSVToColor(floatArrayOf(hue, saturation, value)))
}
