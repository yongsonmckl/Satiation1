package com.mckl.satiation1.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mckl.satiation1.GENDERS_LIST
import com.mckl.satiation1.SatiationGreen
import com.mckl.satiation1.SatiationOrange
import com.mckl.satiation1.navigation.SatiationViewModel

@Composable
fun SplashScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Text(
            "Satiation",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = SatiationGreen
        )
        Box(modifier = Modifier.size(200.dp).padding(32.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(SatiationOrange)
                    .align(Alignment.TopEnd)
            )
        }
        Text(
            "Track Your Nutrition,\nTransform Your Health",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = { navController.navigate("name") },
            colors = ButtonDefaults.buttonColors(containerColor = SatiationGreen),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                "Start Your Journey",
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun NameScreen(navController: NavController, viewModel: SatiationViewModel) {
    var name by remember { mutableStateOf(viewModel.setupName) }
    var selectedGender by remember { mutableStateOf<String?>(null) }
    var pronouns by remember { mutableStateOf(viewModel.setupPronouns) }
    var stage by remember {
        mutableIntStateOf(
            when {
                viewModel.setupPronouns.isNotBlank() -> 2
                viewModel.setupName.isNotBlank() -> 1
                else -> 0
            }
        )
    }
    val scrollState = rememberScrollState()

    fun cleanName(): String {
        return name
            .replace(Regex("[^A-Za-z' -]"), "")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    fun defaultPronouns(gender: String?): String {
        return when (gender) {
            "Male" -> "He/Him"
            "Female" -> "She/Her"
            else -> "They/Them"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 24.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Tell us about you",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    val sanitizedName = cleanName()
                    if (sanitizedName.isNotBlank()) {
                        name = sanitizedName
                        viewModel.setupName = sanitizedName
                        stage = 1
                    }
                }
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))

        AnimatedVisibility(
            visible = stage >= 1,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column {
                Text(
                    "Gender",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                GENDERS_LIST.forEach { gender ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .border(
                                1.dp,
                                if (selectedGender == gender) SatiationOrange else MaterialTheme.colorScheme.outlineVariant,
                                RoundedCornerShape(16.dp)
                            )
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { selectedGender = gender }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            gender,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        RadioButton(
                            selected = selectedGender == gender,
                            onClick = { selectedGender = gender },
                            colors = RadioButtonDefaults.colors(selectedColor = SatiationOrange)
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = stage >= 2,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = pronouns,
                    onValueChange = { pronouns = it },
                    label = { Text("Pronouns") },
                    placeholder = { Text(defaultPronouns(selectedGender)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            val sanitizedName = cleanName()
                            if (sanitizedName.isNotBlank() && selectedGender != null) {
                                viewModel.setupName = sanitizedName
                                viewModel.setupPronouns =
                                    pronouns.trim().ifBlank { defaultPronouns(selectedGender) }
                                navController.navigate("weight")
                            }
                        }
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                val sanitizedName = cleanName()
                when {
                    stage == 0 && sanitizedName.isNotBlank() -> {
                        name = sanitizedName
                        viewModel.setupName = sanitizedName
                        stage = 1
                    }
                    stage == 1 && selectedGender != null -> {
                        val defaultPronouns = defaultPronouns(selectedGender)
                        if (pronouns.isBlank()) {
                            pronouns = defaultPronouns
                        }
                        stage = 2
                    }
                    stage >= 2 && sanitizedName.isNotBlank() && selectedGender != null -> {
                        viewModel.setupName = sanitizedName
                        viewModel.setupPronouns = pronouns.trim().ifBlank { defaultPronouns(selectedGender) }
                        navController.navigate("weight")
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = SatiationGreen),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                when {
                    stage == 0 -> "Next"
                    stage == 1 -> "Next"
                    else -> "Continue"
                },
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }

    LaunchedEffect(name) {
        if (stage > 0 && cleanName().isBlank()) {
            stage = 0
            selectedGender = null
            pronouns = ""
        }
    }
}

@Composable
fun WeightScreen(navController: NavController, viewModel: SatiationViewModel) {
    var weight by remember {
        mutableFloatStateOf(
            viewModel.setupWeightKg.takeIf { it > 0.0 }?.toFloat() ?: 67f
        )
    }
    var height by remember {
        mutableFloatStateOf(
            viewModel.setupHeightCm.takeIf { it > 0.0 }?.toFloat() ?: 170f
        )
    }
    var editingMetric by remember { mutableStateOf<String?>(null) }
    val scrollState = rememberScrollState()
    val weightRange = minOf(40f, weight)..maxOf(150f, weight)
    val heightRange = minOf(120f, height)..maxOf(220f, height)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SatiationGreen)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Your body metrics",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))

        Text("Current Weight", fontSize = 18.sp, color = Color.White)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            formatOnboardingWeightKg(weight.toInt()),
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.clickable { editingMetric = "weight" }
        )
        Text(
            "Tap the number to type your own value.",
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 14.sp
        )
        Slider(
            value = weight,
            onValueChange = { weight = it.toInt().toFloat() },
            valueRange = weightRange,
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text("Height", fontSize = 18.sp, color = Color.White)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            formatOnboardingHeightCm(height.toInt()),
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.clickable { editingMetric = "height" }
        )
        Text(
            "Tap the number to type your own value.",
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 14.sp
        )
        Slider(
            value = height,
            onValueChange = { height = it.toInt().toFloat() },
            valueRange = heightRange,
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = {
                viewModel.setupWeightKg = weight.toDouble()
                viewModel.setupHeightCm = height.toDouble()
                viewModel.saveProfile(
                    name = viewModel.setupName,
                    startWeightKg = viewModel.setupWeightKg,
                    currentWeightKg = viewModel.setupWeightKg,
                    pronouns = viewModel.setupPronouns,
                    heightCm = viewModel.setupHeightCm
                )
                navController.navigate("main") {
                    popUpTo("splash") { inclusive = true }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Complete Setup", color = SatiationGreen)
        }
    }

    if (editingMetric == "weight") {
        IntegerValueDialog(
            title = "Enter Weight",
            label = "Weight (kg)",
            initialValue = weight.toInt(),
            validRange = 20..400,
            onDismiss = { editingMetric = null },
            onConfirm = {
                weight = it.toFloat()
                editingMetric = null
            }
        )
    }

    if (editingMetric == "height") {
        IntegerValueDialog(
            title = "Enter Height",
            label = "Height (cm)",
            initialValue = height.toInt(),
            validRange = 80..280,
            onDismiss = { editingMetric = null },
            onConfirm = {
                height = it.toFloat()
                editingMetric = null
            }
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
                onValueChange = { value ->
                    draft = value.filter { it.isDigit() }
                },
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

private fun formatOnboardingWeightKg(weightKg: Int): String = "$weightKg kg"

private fun formatOnboardingHeightCm(heightCm: Int): String = "$heightCm cm"
