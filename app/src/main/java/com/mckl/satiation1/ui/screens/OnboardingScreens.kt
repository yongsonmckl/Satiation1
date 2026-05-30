package com.mckl.satiation1.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mckl.satiation1.DarkText
import com.mckl.satiation1.SatiationGreen
import com.mckl.satiation1.SatiationOrange
import com.mckl.satiation1.GENDERS_LIST
import com.mckl.satiation1.navigation.SatiationViewModel


@Composable
fun SplashScreen(navController: NavController) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Spacer(modifier = Modifier.weight(1f))
        Text("Satiation", fontSize = 40.sp, fontWeight = FontWeight.Bold, color = SatiationGreen)
        Box(modifier = Modifier.size(200.dp).padding(32.dp)) {
            Box(modifier = Modifier.fillMaxSize().clip(CircleShape).background(Color(0xFFF0F0F0)))
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(SatiationOrange).align(Alignment.TopEnd))
        }
        Text("Track Your Nutrition,\nTransform Your Health", fontSize = 24.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = { navController.navigate("name") },
            colors = ButtonDefaults.buttonColors(containerColor = SatiationGreen),
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Start Your Journey", color = DarkText, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun NameScreen(navController: NavController, viewModel: SatiationViewModel) {
    var name by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("What is your name?", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(40.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {
                val cleanName = name.replace(Regex("[^A-Za-z ]"), "").replace(Regex("\\s+"), " ").trim()
                if (cleanName.isNotEmpty()) {
                    // Save to short-term memory
                    viewModel.setupName = cleanName
                    navController.navigate("weight")
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = SatiationGreen),
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Continue", color = DarkText)
        }
    }
}

@Composable
fun WeightScreen(navController: NavController, viewModel: SatiationViewModel) {
    var weight by remember { mutableIntStateOf(67) }

    Column(modifier = Modifier.fillMaxSize().background(SatiationGreen), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(80.dp))
        Text("What's your current\nweight right now?", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.weight(1f))
        Text("$weight Kg", fontSize = 56.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Slider(
            value = weight.toFloat(),
            onValueChange = { weight = it.toInt() },
            valueRange = 40f..150f,
            modifier = Modifier.padding(horizontal = 32.dp),
            colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Color.White)
        )
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {
                // Save to short-term memory
                viewModel.setupWeight = weight
                navController.navigate("gender")
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().padding(32.dp).height(56.dp)
        ) {
            Text("Continue", color = SatiationGreen)
        }
    }
}

@Composable
fun GenderScreen(navController: NavController, viewModel: SatiationViewModel) {
    var selectedGender by remember { mutableStateOf<String?>(null) }
    var pronouns by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("About you", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = DarkText)
        Spacer(modifier = Modifier.height(24.dp))

        // Gender Selection
        GENDERS_LIST.forEach { gender ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    .border(1.dp, if (selectedGender == gender) SatiationOrange else Color.LightGray, RoundedCornerShape(16.dp))
                    .clickable { selectedGender = gender }.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(gender, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                RadioButton(
                    selected = selectedGender == gender,
                    onClick = { selectedGender = gender },
                    colors = RadioButtonDefaults.colors(selectedColor = SatiationOrange)
                )
            }
        }

        AnimatedVisibility(visible = selectedGender != null) {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                OutlinedTextField(
                    value = pronouns,
                    onValueChange = { pronouns = it },
                    label = { Text("What do we call you?") },
                    placeholder = { Text(when(selectedGender) { "Male" -> "He/Him"; "Female" -> "She/Her"; else -> "They/Them" }) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {
                val finalPronouns = pronouns.ifEmpty {
                    when(selectedGender) { "Male" -> "He/Him"; "Female" -> "She/Her"; else -> "They/Them" }
                }

                // Save to short-term memory
                viewModel.setupPronouns = finalPronouns

                // THIS IS THE FINAL STEP! Save to the permanent database
                viewModel.saveProfile(
                    name = viewModel.setupName,
                    startWeight = viewModel.setupWeight,
                    currentWeight = viewModel.setupWeight,
                    pronouns = viewModel.setupPronouns
                )

                // Go to home screen and delete the setup history
                navController.navigate("main") { popUpTo("splash") { inclusive = true } }
            },
            colors = ButtonDefaults.buttonColors(containerColor = SatiationOrange),
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Complete Setup", color = Color.White)
        }
    }
}
