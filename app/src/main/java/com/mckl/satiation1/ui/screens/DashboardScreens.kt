package com.mckl.satiation1.ui.screens

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui. Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mckl.satiation1.DarkText
import com.mckl.satiation1.LightText
import com.mckl.satiation1.SatiationGreen
import com.mckl.satiation1.SatiationOrange
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.entryModelOf
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import com.mckl.satiation1.navigation.SatiationViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContainer(rootNavController: NavController, viewModel: SatiationViewModel) {
    var currentTab by remember { mutableStateOf("home") }
    val (showAddMenu, setShowAddMenu) = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Satiation", fontWeight = FontWeight.Bold, color = SatiationGreen) })
        },
        bottomBar = {
            Box(modifier = Modifier.fillMaxWidth().background(Color.White).padding(bottom = 15.dp)) {
                Row(modifier = Modifier.fillMaxWidth().padding(30.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { currentTab = "home" }) { Icon(Icons.Default.Home, "Home", tint = if (currentTab == "home") DarkText else LightText) }
                    IconButton(onClick = { currentTab = "checkmark" }) { Icon(Icons.Default.CheckCircle, "Tasks", tint = if (currentTab == "checkmark") DarkText else LightText) }

                    Box(modifier = Modifier.size(56.dp).clip(CircleShape).background(SatiationGreen).clickable { setShowAddMenu(true) }, contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Add, "Open Menu", tint = DarkText, modifier = Modifier.size(32.dp))
                    }

                    IconButton(onClick = { currentTab = "progress" }) { Icon(Icons.Default.DateRange, "Progress", tint = if (currentTab == "progress") DarkText else LightText) }
                    IconButton(onClick = { currentTab = "profile" }) { Icon(Icons.Default.Person, "Profile", tint = if (currentTab == "profile") DarkText else LightText) }
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = {
                    val tabOrder = listOf("home", "checkmark", "progress", "profile")
                    val isMovingRight = tabOrder.indexOf(targetState) > tabOrder.indexOf(initialState)
                    val slideDirection = if (isMovingRight) 1 else -1

                    (slideInHorizontally(animationSpec = tween(300)) { width -> width * slideDirection } + fadeIn()) togetherWith
                            (slideOutHorizontally(animationSpec = tween(300)) { width -> -width * slideDirection } + fadeOut())
                },
                label = "Tab Slide"
            ) { tab ->
                when (tab) {
                    "home" -> HomeScreen(onNavigateToCheckmark = { currentTab = "checkmark" }, onOpenMenu = { setShowAddMenu(true) })
                    "checkmark" -> CheckmarkScreen()
                    "progress" -> ProgressScreen()
                    "profile" -> ProfileScreen(rootNavController, viewModel)
                }
            }
        }
    }

    // Issue 4 Fix: Custom Overlay instead of ModalBottomSheet
    if (showAddMenu) {
        // Issue 2: Added AnimatedVisibility for smooth fade and slide
        AnimatedVisibility(
            visible = showAddMenu,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
        ) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)).clickable { setShowAddMenu(false) },
                contentAlignment = Alignment.BottomCenter
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)).background(Color.White).padding(24.dp).padding(bottom = 32.dp)
                ) {
                    Text("Add Entry", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
                    ListItem(headlineContent = { Text("Scan Food (Camera)") }, modifier = Modifier.clickable { setShowAddMenu(false); rootNavController.navigate("camera") })
                    // Issue 5: Hooked up the Manual button
                    ListItem(headlineContent = { Text("Manual Food Selection") }, modifier = Modifier.clickable { setShowAddMenu(false); rootNavController.navigate("manual_entry") })
                    ListItem(headlineContent = { Text("Log New Weight") }, modifier = Modifier.clickable { setShowAddMenu(false) })
                }
            }
        }
}

@Composable
fun HomeScreen(onNavigateToCheckmark: () -> Unit, onOpenMenu: () -> Unit) {
    val todayMeals = remember { mutableStateListOf<String>() }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("Today's Summary", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp).clickable { onNavigateToCheckmark() }, colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(4.dp)) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("0 Kcal", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = SatiationOrange)
                Text("Consumed Today", color = LightText)
            }
        }

        Text("Meals Eaten", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))

        if (todayMeals.isEmpty()) {
            // Issue 6: Clickable empty state to open the menu
            Box(modifier = Modifier.fillMaxWidth().height(120.dp).background(Color(0xFFF7F7F7), RoundedCornerShape(16.dp)).border(2.dp, Color(0xFFEBEBEB), RoundedCornerShape(16.dp)).clickable { onOpenMenu() }, contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = LightText)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Press to Log your First meal!", color = LightText, fontSize = 14.sp)
                }
            }
        } else {
            LazyColumn {
                items(todayMeals) { meal ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { onNavigateToCheckmark() }, colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        ListItem(headlineContent = { Text(meal) })
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileScreen(rootNavController: NavController, viewModel: SatiationViewModel) {
    val userProfile by viewModel.userProfile.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("Your Profile", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        if (userProfile == null) {
            Text("No profile found.")
        } else {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Name: ${userProfile!!.name}", fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Age: ${userProfile!!.age}", fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Starting Weight: ${userProfile!!.startWeight} kg", fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Current Weight: ${userProfile!!.currentWeight} kg", fontSize = 18.sp)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                // This now sends the user to the dedicated settings page
                onClick = { rootNavController.navigate("settings") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Edit Settings")
            }
        }
    }
}

@Composable
fun CheckmarkScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("Daily Targets", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 24.dp))
        MacroBar("Calories", 0f, 2500f, SatiationOrange)
        MacroBar("Protein", 0f, 120f, SatiationGreen)
        MacroBar("Fats", 0f, 70f, Color(0xFFD0C9FF))
        MacroBar("Carbs", 0f, 300f, Color(0xFFFFD56F))
    }
}

@Composable
fun ProgressScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("Progress", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Last 30 Days", fontWeight = FontWeight.Bold)
                Chart(chart = lineChart(), model = entryModelOf(67f, 66f, 66.5f, 65f, 64f, 62f), modifier = Modifier.height(200.dp))
            }
        }
    }
}

@Composable
fun SettingsScreen(navController: NavController, viewModel: SatiationViewModel) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp).statusBarsPadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
            }
            Text("Settings", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        // This button now opens the actual editing form
        ListItem(
            headlineContent = { Text("Edit Profile") },
            supportingContent = { Text("Name, Age, Pronouns, Weight") },
            modifier = Modifier.clickable { navController.navigate("edit_profile") }
        )

        ListItem(
            headlineContent = { Text("Edit Food Types") },
            supportingContent = { Text("Manage Custom Calorie Inputs") },
            modifier = Modifier.clickable { /* We will add this screen later! */ }
        )

        ListItem(
            headlineContent = { Text("Appearance") },
            supportingContent = { Text("Light/Dark Mode") },
            modifier = Modifier.clickable { /* To be added */ }
        )
    }
}

// This is the new screen where the user actually types their changes
@Composable
fun EditProfileScreen(navController: NavController, viewModel: SatiationViewModel) {
    val userProfile by viewModel.userProfile.collectAsState()

    var editName by remember { mutableStateOf(userProfile?.name ?: "") }
    var editAge by remember { mutableStateOf(userProfile?.age?.toString() ?: "") }
    var editWeight by remember { mutableStateOf(userProfile?.currentWeight?.toString() ?: "") }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp).statusBarsPadding()) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 24.dp)) {
            IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.offset(x = (-12).dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
            }
            Text("Edit Profile", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        }

        OutlinedTextField(value = editName, onValueChange = { editName = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = editAge, onValueChange = { editAge = it }, label = { Text("Age") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = editWeight, onValueChange = { editWeight = it }, label = { Text("Current Weight (kg)") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (userProfile != null) {
                    // Save to database
                    viewModel.saveProfile(
                        name = editName,
                        age = editAge.toIntOrNull() ?: userProfile!!.age,
                        startWeight = userProfile!!.startWeight,
                        currentWeight = editWeight.toIntOrNull() ?: userProfile!!.currentWeight,
                        pronouns = userProfile!!.pronouns
                    )
                }
                // Go back to the settings menu
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Save Changes")
        }
    }
}
