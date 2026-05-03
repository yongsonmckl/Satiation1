package com.mckl.satiation1.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.mckl.satiation1.DarkText
import com.mckl.satiation1.LightText
import com.mckl.satiation1.SatiationGreen
import com.mckl.satiation1.SatiationOrange
import com.mckl.satiation1.navigation.SatiationViewModel
import android.graphics.Bitmap
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import org.json.JSONObject
@Composable
fun CameraScreen(navController: NavController, viewModel: SatiationViewModel) {
    val context = LocalContext.current
    val mainExecutor = androidx.core.content.ContextCompat.getMainExecutor(LocalContext.current)
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    var hasPermission by remember { mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted -> hasPermission = granted }
    LaunchedEffect(Unit) { if (!hasPermission) launcher.launch(Manifest.permission.CAMERA) }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        if (hasPermission) {
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx).apply { layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT) }
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also { it.surfaceProvider = previewView.surfaceProvider}
                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview)
                        } catch (e: Exception) { e.printStackTrace() }
                    }, ContextCompat.getMainExecutor(ctx))
                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text("Camera Permission Required", color = Color.White, modifier = Modifier.align(Alignment.Center))
        }
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp).  statusBarsPadding(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), CircleShape)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
        }

        Box(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)).background(Color.White).padding(32.dp)) {
            Button(onClick = { navController.navigate("nutrition")}, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = SatiationGreen)) {
                Text("Take Photo & Scan", color = DarkText)
            }
        }
    }
}

@SuppressLint("SecretInSource")
@Composable
fun NutritionDetailScreen(navController: NavController, viewModel: SatiationViewModel, capturedImage: Bitmap? = null) {
    val imageToScan = viewModel.capturedImage.value
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // State variables to hold the AI's math
    var totalCals by remember { mutableIntStateOf(0) }
    var itemsFound by remember { mutableStateOf("Waiting for image...") }

    // THE AI BRAIN
    val generativeModel = remember {
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = "AIzaSyBFx2gtWjUVSzOJfEBMDUDerQ0k2FgmpoA"
        )
    }

    // The ruthless prompt that forces Gemini to output strict JSON
    val prompt = """
        Analyze this food. Return ONLY a raw JSON object with absolutely no markdown formatting, no backticks, and no conversational text.
        Format it exactly like this:
        {
            "total_calories": 450,
            "items": ["Chicken (Meat)", "Broccoli (Greens)", "Rice (Carbs)"]
        }
        Classify every item strictly as Meat, Greens, or Carbs in parentheses.
    """.trimIndent()

    // Trigger the AI the second this screen opens if we have an image
    LaunchedEffect(capturedImage) {
        if (capturedImage != null) {
            isLoading = true
            try {
                val response = generativeModel.generateContent(
                    content {
                        image(capturedImage)
                        text(prompt)
                    }
                )

                // Parse the JSON string Android-style
                val rawText = response.text ?: "{}"
                val jsonResponse = JSONObject(rawText)

                totalCals = jsonResponse.getInt("total_calories")
                val itemsArray = jsonResponse.getJSONArray("items")

                var itemList = ""
                for (i in 0 until itemsArray.length()) {
                    itemList += "• ${itemsArray.getString(i)}\n"
                }
                itemsFound = itemList

            } catch (e: Exception) {
                errorMessage = "AI Vision Failed: ${e.message}"
            } finally {
                isLoading = false
            }
        } else {
            errorMessage = "No image was passed from the camera!"
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White).padding(24.dp)) {
        Text("AI Plate Analysis", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = SatiationGreen)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Scanning macros...", color = LightText)
                }
            }
        } else if (errorMessage != null) {
            Text(errorMessage!!, color = Color.Red, modifier = Modifier.weight(1f))
        } else {
            Column(modifier = Modifier.weight(1f)) {
                Text("$totalCals Kcal", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = SatiationOrange)
                Spacer(modifier = Modifier.height(24.dp))
                Text("Detected Items:", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(itemsFound, fontSize = 16.sp, color = DarkText)
            }
        }

        Button(
            onClick = { navController.popBackStack("main", inclusive = false) },
            modifier = Modifier.fillMaxWidth().padding(bottom = 30.dp).height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SatiationGreen),
            enabled = !isLoading
        ) {
            Text("Log this meal", color = DarkText, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun MacroBar(label: String, value: Float, max: Float, color: Color) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, fontWeight = FontWeight.Bold)
            Text("${value.toInt()} / ${max.toInt()}", color = LightText)
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(progress = { value / max }, modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(6.dp)), color = color, trackColor = Color(0xFFF0F0F0))
    }
}