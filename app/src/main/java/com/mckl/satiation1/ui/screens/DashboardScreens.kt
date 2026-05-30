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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun MainContainer(rootNavController: NavController, viewModel: SatiationViewModel) {
    val (showAddMenu, setShowAddMenu) = remember { mutableStateOf(false) }
    val (isAddMenuMounted, setIsAddMenuMounted) = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val userProfile by viewModel.userProfile.collectAsState()
    val currentTab = viewModel.currentMainTab
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
                            color = DarkText
                        )
                    }
                }
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
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
                        onNavigateToCheckmark = { viewModel.currentMainTab = "checkmark" },
                        onOpenMenu = { setShowAddMenu(true) }
                    )
                    "checkmark" -> CheckmarkScreen()
                    "progress" -> ProgressScreen()
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
                    .background(Color.White)
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
                ListItem(
                    headlineContent = { Text("Log New Weight") },
                    modifier = Modifier.clickable { setShowAddMenu(false) }
                )
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
fun SettingsHubScreen(rootNavController: NavController, viewModel: SatiationViewModel) {
    val userProfile by viewModel.userProfile.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("Settings", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color(0xFFE2E2E2), RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Profile", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                if (userProfile == null) {
                    Text("No profile found.", color = LightText)
                } else {
                    Text("Name: ${userProfile!!.name}", fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Pronouns: ${userProfile!!.pronouns}", fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Starting Weight: ${userProfile!!.startWeight} kg", fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Current Weight: ${userProfile!!.currentWeight} kg", fontSize = 16.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        ListItem(
            headlineContent = { Text("Edit Profile") },
            supportingContent = { Text("Name, pronouns, and weight") },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .clickable {
                    viewModel.currentMainTab = "profile"
                    rootNavController.navigate("edit_profile")
                }
        )

        ListItem(
            headlineContent = { Text("Food Types") },
            supportingContent = { Text("Manage custom calorie inputs here later") },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
        )

        ListItem(
            headlineContent = { Text("Appearance") },
            supportingContent = { Text("Light and dark mode options will live here") },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
        )
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
    viewModel.currentMainTab = "profile"

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
            supportingContent = { Text("Name, Pronouns, Weight") },
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
    viewModel.currentMainTab = "profile"
    val userProfile by viewModel.userProfile.collectAsState()

    var editName by remember { mutableStateOf(userProfile?.name ?: "") }
    var editPronouns by remember { mutableStateOf(userProfile?.pronouns ?: "") }
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
        OutlinedTextField(value = editPronouns, onValueChange = { editPronouns = it }, label = { Text("Pronouns") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = editWeight, onValueChange = { editWeight = it }, label = { Text("Current Weight (kg)") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (userProfile != null) {
                    // Save to database
                    viewModel.saveProfile(
                        name = editName,
                        startWeight = userProfile!!.startWeight,
                        currentWeight = editWeight.toIntOrNull() ?: userProfile!!.currentWeight,
                        pronouns = editPronouns
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

@Composable
fun ManualEntryPlaceholderScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .statusBarsPadding()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.offset(x = (-12).dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text("Manual Entry", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        }

        Text(
            "Manual logging is not implemented yet. Phase 2 will replace this placeholder with the preset foods and manual entry flow.",
            color = LightText
        )
    }
}
