package com.mckl.satiation1.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.mckl.satiation1.SatiationGreen
import com.mckl.satiation1.SatiationOrange
import com.mckl.satiation1.navigation.SatiationViewModel
import org.json.JSONObject

@Composable
fun CameraScreen(navController: NavController, viewModel: SatiationViewModel) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val colorScheme = MaterialTheme.colorScheme
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
    }

    LaunchedEffect(Unit) {
        if (!hasPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        if (hasPermission) {
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder()
                            .build()
                            .also { it.surfaceProvider = previewView.surfaceProvider }
                    try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                CameraSelector.DEFAULT_BACK_CAMERA,
                                preview
                            )
                        } catch (_: Exception) {
                        }
                    }, ContextCompat.getMainExecutor(ctx))
                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text(
                "Camera Permission Required",
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .statusBarsPadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(colorScheme.surface)
                .padding(32.dp)
        ) {
            Button(
                onClick = { navController.navigate("nutrition") },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SatiationGreen)
            ) {
                Text("Take Photo & Scan", color = colorScheme.onPrimary)
            }
        }
    }
}

@Composable
fun NutritionDetailScreen(
    navController: NavController,
    viewModel: SatiationViewModel,
    capturedImage: Bitmap? = null
) {
    val appSettings by viewModel.appSettings.collectAsState()
    val imageToScan = viewModel.capturedImage.value ?: capturedImage
    val apiKey = appSettings?.geminiApiKey?.trim().orEmpty()
    val colorScheme = MaterialTheme.colorScheme
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var totalCals by remember { mutableIntStateOf(0) }
    var itemsFound by remember { mutableStateOf("Waiting for image...") }

    val generativeModel = remember(apiKey) {
        apiKey
            .takeIf { it.isNotBlank() }
            ?.let {
                GenerativeModel(
                    modelName = "gemini-1.5-flash",
                    apiKey = it
                )
            }
    }

    val prompt = """
        Analyze this food. Return ONLY a raw JSON object with absolutely no markdown formatting, no backticks, and no conversational text.
        Format it exactly like this:
        {
            "total_calories": 450,
            "items": ["Chicken (Meat)", "Broccoli (Greens)", "Rice (Carbs)"]
        }
        Classify every item strictly as Meat, Greens, or Carbs in parentheses.
    """.trimIndent()

    LaunchedEffect(imageToScan, generativeModel) {
        when {
            generativeModel == null -> {
                errorMessage = "Add your Gemini API key in Settings before scanning."
            }
            imageToScan != null -> {
                errorMessage = null
                itemsFound = "Waiting for image..."
                totalCals = 0
                isLoading = true
                try {
                    val response = generativeModel.generateContent(
                        content {
                            image(imageToScan)
                            text(prompt)
                        }
                    )

                    val rawText = response.text ?: "{}"
                    val jsonResponse = JSONObject(rawText)
                    totalCals = jsonResponse.getInt("total_calories")
                    val itemsArray = jsonResponse.getJSONArray("items")
                    itemsFound = buildString {
                        for (index in 0 until itemsArray.length()) {
                            append("- ")
                            append(itemsArray.getString(index))
                            append('\n')
                        }
                    }
                } catch (exception: Exception) {
                    errorMessage = "AI Vision Failed: ${exception.message}"
                } finally {
                    isLoading = false
                }
            }
            else -> {
                errorMessage = "No image was passed from the camera!"
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(colorScheme.background).padding(24.dp)) {
        Text("AI Plate Analysis", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = colorScheme.onBackground)
        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = SatiationGreen)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Scanning macros...", color = colorScheme.onSurfaceVariant)
                }
            }
        } else if (errorMessage != null) {
            Text(errorMessage.orEmpty(), color = Color.Red, modifier = Modifier.weight(1f))
        } else {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "$totalCals Kcal",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = SatiationOrange
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text("Detected Items:", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colorScheme.onSurface)
                Spacer(modifier = Modifier.height(8.dp))
                Text(itemsFound, fontSize = 16.sp, color = colorScheme.onSurface)
            }
        }

        Button(
            onClick = { navController.popBackStack("main", inclusive = false) },
            modifier = Modifier.fillMaxWidth().padding(bottom = 30.dp).height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SatiationGreen),
            enabled = !isLoading
        ) {
            Text("Log this meal", color = colorScheme.onPrimary, fontWeight = FontWeight.Bold)
        }
    }
}
