package com.mckl.satiation1.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.mckl.satiation1.SatiationGreen
import com.mckl.satiation1.SatiationOrange
import com.mckl.satiation1.ai.GeminiNutritionClient
import com.mckl.satiation1.ai.GeminiNutritionDraft
import com.mckl.satiation1.ai.GeminiNutritionDraftItem
import com.mckl.satiation1.ai.GeminiNutritionDraftValidationResult
import com.mckl.satiation1.ai.GeminiNutritionItem
import com.mckl.satiation1.ai.GeminiNutritionResult
import com.mckl.satiation1.ai.GeminiNutritionSupport
import com.mckl.satiation1.ai.NutritionScanUiState
import com.mckl.satiation1.ai.ScanImageLoader
import com.mckl.satiation1.navigation.SatiationViewModel
import java.io.File
import java.util.Locale

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
    var previewView by remember { mutableStateOf<PreviewView?>(null) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var isProcessingImage by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val pendingLaunchAction = remember { viewModel.consumeCameraLaunchAction() }

    val importImageLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        when {
            uri == null -> {
                if (viewModel.capturedImage.value == null) {
                    errorMessage = "No image was selected."
                }
            }
            else -> {
                isProcessingImage = true
                errorMessage = null
                runCatching { ScanImageLoader.loadBitmap(context, uri) }
                    .onSuccess { bitmap ->
                        viewModel.setCapturedImage(bitmap)
                        navController.navigate("nutrition")
                    }
                    .onFailure {
                        errorMessage = "Unable to read the selected image."
                    }
                isProcessingImage = false
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        if (!granted) {
            errorMessage = "Camera permission is required to take a photo. You can still import one."
        }
    }

    LaunchedEffect(Unit) {
        viewModel.clearCapturedImage()
        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
        if (pendingLaunchAction == SatiationViewModel.CameraLaunchAction.IMPORT) {
            importImageLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
    }

    LaunchedEffect(hasPermission, previewView, lifecycleOwner) {
        val activePreviewView = previewView ?: return@LaunchedEffect
        if (!hasPermission) {
            imageCapture = null
            return@LaunchedEffect
        }

        val cameraProvider = ProcessCameraProvider.getInstance(context).get()
        val preview = Preview.Builder()
            .build()
            .also { it.surfaceProvider = activePreviewView.surfaceProvider }
        val captureUseCase = ImageCapture.Builder()
            .setTargetRotation(activePreviewView.display.rotation)
            .build()

        runCatching {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                captureUseCase
            )
        }.onSuccess {
            imageCapture = captureUseCase
            errorMessage = null
        }.onFailure {
            imageCapture = null
            errorMessage = "Unable to start the camera preview."
        }
    }

    DisposableEffect(lifecycleOwner) {
        onDispose {
            runCatching {
                ProcessCameraProvider.getInstance(context).get().unbindAll()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (hasPermission) {
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        previewView = this
                    }
                },
                modifier = Modifier.fillMaxSize(),
                update = { previewView = it }
            )
        } else {
            Text(
                "Camera permission is unavailable. Import a photo to continue.",
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 32.dp)
            )
        }

        if (isProcessingImage) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Color.White)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Preparing image...", color = Color.White)
                }
            }
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
                .padding(24.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                errorMessage?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp
                    )
                }

                Button(
                    onClick = {
                        val captureUseCase = imageCapture ?: run {
                            errorMessage = "Camera is not ready yet."
                            return@Button
                        }
                        errorMessage = null
                        isProcessingImage = true
                        viewModel.clearCapturedImage()
                        captureUseCase.takePicture(
                            createOutputOptions(context),
                            ContextCompat.getMainExecutor(context),
                            object : ImageCapture.OnImageSavedCallback {
                                override fun onImageSaved(
                                    outputFileResults: ImageCapture.OutputFileResults
                                ) {
                                    val savedUri = outputFileResults.savedUri
                                    val outputFile = latestCaptureFile(context)
                                    val bitmap = runCatching {
                                        when {
                                            savedUri != null -> ScanImageLoader.loadBitmap(context, savedUri)
                                            outputFile != null -> BitmapFactory.decodeFile(outputFile.absolutePath)
                                            else -> null
                                        } ?: error("Capture completed without an image.")
                                    }

                                    bitmap.onSuccess {
                                        viewModel.setCapturedImage(it)
                                        isProcessingImage = false
                                        navController.navigate("nutrition")
                                    }.onFailure {
                                        errorMessage = "Unable to prepare the captured image."
                                        isProcessingImage = false
                                    }
                                }

                                override fun onError(exception: ImageCaptureException) {
                                    errorMessage = "Photo capture failed. Please try again."
                                    isProcessingImage = false
                                }
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SatiationGreen),
                    enabled = hasPermission && !isProcessingImage && imageCapture != null
                ) {
                    Text("Take Photo", color = colorScheme.onPrimary)
                }

                OutlinedButton(
                    onClick = {
                        errorMessage = null
                        viewModel.clearCapturedImage()
                        importImageLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !isProcessingImage
                ) {
                    Text("Import Photo", color = colorScheme.onSurface)
                }
            }
        }
    }
}

@Composable
fun NutritionDetailScreen(
    navController: NavController,
    viewModel: SatiationViewModel
) {
    val appSettings by viewModel.appSettings.collectAsState()
    val imageToScan = viewModel.capturedImage.value
    val apiKey = appSettings?.geminiApiKey?.trim().orEmpty()
    val colorScheme = MaterialTheme.colorScheme
    var uiState by remember { mutableStateOf<NutritionScanUiState>(NutritionScanUiState.Idle) }
    var analysisRequestNonce by remember { mutableIntStateOf(0) }
    var scanHint by rememberSaveable { mutableStateOf("") }
    var debugRawResponse by remember { mutableStateOf<String?>(null) }
    var analysisUsedModel by rememberSaveable { mutableStateOf<String?>(null) }
    var analysisUsedHint by rememberSaveable { mutableStateOf<String?>(null) }
    var showPresetSavePrompt by remember { mutableStateOf(false) }
    var savedResultForPreset by remember { mutableStateOf<GeminiNutritionResult?>(null) }

    LaunchedEffect(imageToScan, apiKey) {
        debugRawResponse = null
        analysisUsedModel = null
        analysisUsedHint = null
        uiState = when {
            apiKey.isBlank() -> NutritionScanUiState.MissingApiKey(
                "Add your Gemini API key in Settings before scanning."
            )
            imageToScan == null -> NutritionScanUiState.MissingImage(
                "No image is available. Take or import a photo first."
            )
            else -> NutritionScanUiState.Idle
        }
    }

    LaunchedEffect(analysisRequestNonce) {
        if (analysisRequestNonce == 0) {
            return@LaunchedEffect
        }

        when {
            apiKey.isBlank() -> {
                uiState = NutritionScanUiState.MissingApiKey(
                    "Add your Gemini API key in Settings before scanning."
                )
            }
            imageToScan == null -> {
                uiState = NutritionScanUiState.MissingImage(
                    "No image is available. Take or import a photo first."
                )
            }
            else -> {
                uiState = NutritionScanUiState.Loading
                try {
                    val analysis = GeminiNutritionClient.analyzeMeal(
                        apiKey = apiKey,
                        bitmap = imageToScan,
                        userHint = scanHint
                    )
                    analysisUsedHint = analysis.sanitizedHint
                    analysisUsedModel = analysis.modelName
                    debugRawResponse = analysis.rawText
                    uiState = NutritionScanUiState.Review(
                        GeminiNutritionSupport.run { analysis.result.toDraft() }
                    )
                } catch (exception: Exception) {
                    uiState = NutritionScanUiState.ApiFailure(
                        "AI analysis failed: ${exception.message}"
                    )
                }
            }
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is NutritionScanUiState.Saved && !showPresetSavePrompt) {
            viewModel.currentMainTab = "home"
            navController.popBackStack("main", inclusive = false)
        }
    }

    fun activeDraft(): GeminiNutritionDraft? {
        return when (val currentState = uiState) {
            is NutritionScanUiState.Review -> currentState.draft
            is NutritionScanUiState.SaveFailure -> currentState.draft
            else -> null
        }
    }

    fun updateDraft(transform: (GeminiNutritionDraft) -> GeminiNutritionDraft) {
        val currentDraft = activeDraft() ?: return
        uiState = NutritionScanUiState.Review(transform(currentDraft))
    }

    fun startAnalysis() {
        analysisRequestNonce += 1
    }

    fun openManualEntry() {
        viewModel.clearCapturedImage()
        viewModel.clearMealDraft()
        navController.navigate("manual_entry") {
            popUpTo("camera") {
                inclusive = true
            }
        }
    }

    val currentDraft = activeDraft()
    val validatedDraft = currentDraft?.let { GeminiNutritionSupport.validateDraft(it) }
    val reviewedResult = (validatedDraft as? GeminiNutritionDraftValidationResult.Success)?.result
    val validationError = (validatedDraft as? GeminiNutritionDraftValidationResult.Failure)?.message
    val lowConfidenceItems = reviewedResult?.items.orEmpty().filter { (it.confidence ?: 1.0) < LOW_CONFIDENCE_THRESHOLD }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .padding(24.dp)
    ) {
        Text(
            "AI Plate Analysis",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))

        imageToScan?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Selected meal photo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(20.dp))
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (val currentState = uiState) {
                NutritionScanUiState.Idle -> {
                    AnalysisControlCard(
                        scanHint = scanHint,
                        onHintChange = { scanHint = it },
                        buttonLabel = "Analyze Plate",
                        onAnalyze = ::startAnalysis,
                        enabled = imageToScan != null && apiKey.isNotBlank()
                    )
                }
                NutritionScanUiState.Loading,
                NutritionScanUiState.Saving,
                NutritionScanUiState.Saved -> {
                    val statusLabel = when (currentState) {
                        NutritionScanUiState.Loading -> "Analyzing plate..."
                        NutritionScanUiState.Saving -> "Saving AI meal..."
                        NutritionScanUiState.Saved -> "Meal saved."
                        else -> "Preparing scan..."
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = SatiationGreen)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(statusLabel, color = colorScheme.onSurfaceVariant)
                        }
                    }
                }
                is NutritionScanUiState.MissingApiKey -> {
                    ErrorStateContent(
                        message = currentState.message,
                        primaryLabel = "Edit API Key",
                        onPrimary = { navController.navigate("edit_api_key") },
                        secondaryLabel = "Use Manual Entry",
                        onSecondary = ::openManualEntry
                    )
                }
                is NutritionScanUiState.MissingImage -> {
                    ErrorStateContent(
                        message = currentState.message,
                        primaryLabel = "Back to Photo",
                        onPrimary = { navController.popBackStack() },
                        secondaryLabel = "Use Manual Entry",
                        onSecondary = ::openManualEntry
                    )
                }
                is NutritionScanUiState.ApiFailure -> {
                    ErrorStateContent(
                        message = currentState.message,
                        primaryLabel = "Retry Analysis",
                        onPrimary = ::startAnalysis,
                        secondaryLabel = "Use Manual Entry",
                        onSecondary = ::openManualEntry
                    )
                    AnalysisControlCard(
                        scanHint = scanHint,
                        onHintChange = { scanHint = it },
                        buttonLabel = "Retry With Current Hint",
                        onAnalyze = ::startAnalysis,
                        enabled = true
                    )
                }
                is NutritionScanUiState.InvalidModelOutput -> {
                    ErrorStateContent(
                        message = currentState.message,
                        primaryLabel = "Retry Analysis",
                        onPrimary = ::startAnalysis,
                        secondaryLabel = "Use Manual Entry",
                        onSecondary = ::openManualEntry
                    )
                    AnalysisControlCard(
                        scanHint = scanHint,
                        onHintChange = { scanHint = it },
                        buttonLabel = "Retry With Current Hint",
                        onAnalyze = ::startAnalysis,
                        enabled = true
                    )
                }
                is NutritionScanUiState.SaveFailure,
                is NutritionScanUiState.Review -> {
                    AnalysisControlCard(
                        scanHint = scanHint,
                        onHintChange = { scanHint = it },
                        buttonLabel = if (analysisUsedHint == null) "Analyze Again With Hint" else "Reanalyze Plate",
                        onAnalyze = ::startAnalysis,
                        enabled = uiState !is NutritionScanUiState.Saving
                    )

                    (currentState as? NutritionScanUiState.SaveFailure)?.let {
                        Text(it.message, color = colorScheme.error)
                    }
                    validationError?.let {
                        Text(it, color = colorScheme.error)
                    }

                    reviewedResult?.let { result ->
                        Text(
                            "${result.totalCalories.formatNutritionNumber()} Kcal",
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Bold,
                            color = SatiationOrange
                        )
                        Text(
                            "Protein ${result.totalProteinGrams.formatNutritionNumber()}g | " +
                                "Carbs ${result.totalCarbsGrams.formatNutritionNumber()}g | " +
                                "Fats ${result.totalFatsGrams.formatNutritionNumber()}g",
                            fontSize = 15.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                    }

                    AnalysisMetadata(
                        modelName = analysisUsedModel,
                        hint = analysisUsedHint
                    )

                    if (lowConfidenceItems.isNotEmpty()) {
                        LowConfidenceWarning(lowConfidenceItems)
                    }

                    Text(
                        "Review and edit the AI meal before saving. Totals update from the item list below.",
                        color = colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = currentDraft?.notes.orEmpty(),
                        onValueChange = { notes ->
                            updateDraft { draft -> draft.copy(notes = notes) }
                        },
                        label = { Text("AI Notes (optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        "Detected Items",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )

                    currentDraft?.items?.forEachIndexed { index, item ->
                        AiReviewItemEditor(
                            item = item,
                            index = index,
                            onChange = { updatedItem ->
                                updateDraft { draft ->
                                    draft.copy(
                                        items = draft.items.mapIndexed { itemIndex, existing ->
                                            if (itemIndex == index) updatedItem else existing
                                        }
                                    )
                                }
                            },
                            onRemove = if (currentDraft.items.size > 1) {
                                {
                                    updateDraft { draft ->
                                        draft.copy(
                                            items = draft.items.filterIndexed { itemIndex, _ ->
                                                itemIndex != index
                                            }
                                        )
                                    }
                                }
                            } else {
                                null
                            }
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            updateDraft { draft ->
                                draft.copy(
                                    items = draft.items + GeminiNutritionDraftItem(
                                        name = "",
                                        category = "",
                                        calories = "0",
                                        proteinGrams = "0",
                                        carbsGrams = "0",
                                        fatsGrams = "0",
                                        confidence = ""
                                    )
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Text("Add Item")
                    }

                    OutlinedButton(
                        onClick = ::openManualEntry,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Use Manual Entry Instead")
                    }
                }
            }

            if (!debugRawResponse.isNullOrBlank()) {
                DebugResponsePanel(debugRawResponse!!)
            }
        }

        when (uiState) {
            NutritionScanUiState.Idle -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 30.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                    ) {
                        Text("Back to Photo")
                    }
                    Button(
                        onClick = ::startAnalysis,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SatiationGreen),
                        enabled = imageToScan != null && apiKey.isNotBlank()
                    ) {
                        Text("Analyze Plate", color = colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
            is NutritionScanUiState.Review,
            is NutritionScanUiState.SaveFailure -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 30.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        enabled = uiState !is NutritionScanUiState.Loading &&
                            uiState !is NutritionScanUiState.Saving
                    ) {
                        Text("Retake Photo")
                    }

                    Button(
                        onClick = {
                            when (val draftResult = GeminiNutritionSupport.validateDraft(currentDraft!!)) {
                                is GeminiNutritionDraftValidationResult.Success -> {
                                    uiState = NutritionScanUiState.Saving
                                    viewModel.saveAiMeal(draftResult.result) { saveResult ->
                                        uiState = saveResult.fold(
                                            onSuccess = {
                                                savedResultForPreset = draftResult.result
                                                showPresetSavePrompt = true
                                                NutritionScanUiState.Saved
                                            },
                                            onFailure = {
                                                NutritionScanUiState.SaveFailure(
                                                    it.message ?: "Unable to save the AI meal.",
                                                    currentDraft
                                                )
                                            }
                                        )
                                    }
                                }
                                is GeminiNutritionDraftValidationResult.Failure -> {
                                    uiState = NutritionScanUiState.SaveFailure(
                                        draftResult.message,
                                        currentDraft
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SatiationGreen),
                        enabled = currentDraft != null &&
                            reviewedResult != null &&
                            uiState !is NutritionScanUiState.Loading &&
                            uiState !is NutritionScanUiState.Saving
                    ) {
                        Text("Save AI Meal", color = colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
            else -> {
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 30.dp)
                        .height(56.dp),
                    enabled = uiState !is NutritionScanUiState.Loading &&
                        uiState !is NutritionScanUiState.Saving
                ) {
                    Text("Back to Photo")
                }
            }
        }
    }

    if (showPresetSavePrompt && savedResultForPreset != null) {
        AlertDialog(
            onDismissRequest = { showPresetSavePrompt = false },
            title = { Text("Save As Preset Meal?") },
            text = { Text("This AI meal has been saved. Do you also want to open the preset editor with these values prefilled?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        val result = savedResultForPreset
                        showPresetSavePrompt = false
                        if (result != null) {
                            val primaryName = result.items.firstOrNull()?.name ?: "AI Meal"
                            val primaryCategory = result.items.firstOrNull()?.category
                            viewModel.seedPresetDraft(
                                name = primaryName,
                                category = primaryCategory,
                                calories = result.totalCalories,
                                protein = result.totalProteinGrams,
                                carbs = result.totalCarbsGrams,
                                fats = result.totalFatsGrams,
                                notes = result.notes
                            )
                        }
                        viewModel.currentMainTab = "profile"
                        navController.navigate("food_types") {
                            popUpTo("main") { inclusive = false }
                        }
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showPresetSavePrompt = false
                        viewModel.currentMainTab = "home"
                        navController.popBackStack("main", inclusive = false)
                    }
                ) {
                    Text("No")
                }
            }
        )
    }
}

@Composable
private fun AnalysisControlCard(
    scanHint: String,
    onHintChange: (String) -> Unit,
    buttonLabel: String,
    onAnalyze: () -> Unit,
    enabled: Boolean
) {
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(colorScheme.surface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            "Scan Hint (optional)",
            fontWeight = FontWeight.Bold,
            color = colorScheme.onSurface
        )
        Text(
            "Use this to confirm visible foods such as braised pork, egg, or leafy vegetables. The model should still rely on the image.",
            color = colorScheme.onSurfaceVariant,
            fontSize = 13.sp
        )
        OutlinedTextField(
            value = scanHint,
            onValueChange = onHintChange,
            label = { Text("Visible foods or context") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = onAnalyze,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = SatiationGreen),
            enabled = enabled
        ) {
            Text(buttonLabel, color = colorScheme.onPrimary)
        }
    }
}

@Composable
private fun ErrorStateContent(
    message: String,
    primaryLabel: String,
    onPrimary: () -> Unit,
    secondaryLabel: String? = null,
    onSecondary: (() -> Unit)? = null
) {
    val colorScheme = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(message, color = colorScheme.error)
        Button(
            onClick = onPrimary,
            colors = ButtonDefaults.buttonColors(containerColor = SatiationGreen)
        ) {
            Text(primaryLabel, color = colorScheme.onPrimary)
        }
        if (secondaryLabel != null && onSecondary != null) {
            OutlinedButton(onClick = onSecondary) {
                Text(secondaryLabel)
            }
        }
    }
}

@Composable
private fun AnalysisMetadata(
    modelName: String?,
    hint: String?
) {
    val colorScheme = MaterialTheme.colorScheme
    if (modelName == null && hint == null) {
        return
    }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        modelName?.let {
            Text(
                "Model: $it",
                color = colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )
        }
        Text(
            if (hint.isNullOrBlank()) {
                "Hint used: none"
            } else {
                "Hint used: $hint"
            },
            color = colorScheme.onSurfaceVariant,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun LowConfidenceWarning(items: List<GeminiNutritionItem>) {
    val colorScheme = MaterialTheme.colorScheme
    val names = items.joinToString(", ") { item ->
        val confidence = item.confidence?.let { "%.2f".format(Locale.US, it) } ?: "n/a"
        "${item.name} ($confidence)"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colorScheme.errorContainer)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            "Low-confidence items detected",
            color = colorScheme.onErrorContainer,
            fontWeight = FontWeight.Bold
        )
        Text(
            names,
            color = colorScheme.onErrorContainer
        )
        Text(
            "Double-check these foods before saving or use the scan hint to reanalyze.",
            color = colorScheme.onErrorContainer,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun DebugResponsePanel(rawResponse: String) {
    val colorScheme = MaterialTheme.colorScheme
    var expanded by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(colorScheme.surfaceVariant)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            "Debug Response",
            fontWeight = FontWeight.Bold,
            color = colorScheme.onSurface
        )
        HorizontalDivider()
        Text(
            if (expanded) rawResponse else rawResponse.take(DEBUG_PREVIEW_CHAR_LIMIT),
            color = colorScheme.onSurfaceVariant,
            fontSize = 12.sp
        )
        if (rawResponse.length > DEBUG_PREVIEW_CHAR_LIMIT) {
            OutlinedButton(onClick = { expanded = !expanded }) {
                Text(if (expanded) "Collapse Raw Response" else "Show Raw Response")
            }
        }
    }
}

@Composable
private fun AiReviewItemEditor(
    item: GeminiNutritionDraftItem,
    index: Int,
    onChange: (GeminiNutritionDraftItem) -> Unit,
    onRemove: (() -> Unit)?
) {
    val colorScheme = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(colorScheme.surface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Item ${index + 1}",
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )
            if (onRemove != null) {
                IconButton(onClick = onRemove) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Remove item",
                        tint = colorScheme.error
                    )
                }
            }
        }

        OutlinedTextField(
            value = item.name,
            onValueChange = { onChange(item.copy(name = it)) },
            label = { Text("Food Name") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = item.category,
            onValueChange = { onChange(item.copy(category = it)) },
            label = { Text("Category (optional)") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = item.calories,
            onValueChange = { onChange(item.copy(calories = filterDecimalInput(it))) },
            label = { Text("Calories") },
            modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = item.proteinGrams,
                onValueChange = { onChange(item.copy(proteinGrams = filterDecimalInput(it))) },
                label = { Text("Protein") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = item.carbsGrams,
                onValueChange = { onChange(item.copy(carbsGrams = filterDecimalInput(it))) },
                label = { Text("Carbs") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = item.fatsGrams,
                onValueChange = { onChange(item.copy(fatsGrams = filterDecimalInput(it))) },
                label = { Text("Fats") },
                modifier = Modifier.weight(1f)
            )
        }
        OutlinedTextField(
            value = item.confidence,
            onValueChange = { onChange(item.copy(confidence = filterDecimalInput(it))) },
            label = { Text("Confidence 0-1 (optional)") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

private fun createOutputOptions(context: Context): ImageCapture.OutputFileOptions {
    val imageFile = File(context.cacheDir, CAPTURED_IMAGE_NAME).apply {
        parentFile?.mkdirs()
        if (exists()) {
            delete()
        }
    }
    return ImageCapture.OutputFileOptions.Builder(imageFile).build()
}

private fun latestCaptureFile(context: Context): File? {
    val imageFile = File(context.cacheDir, CAPTURED_IMAGE_NAME)
    return imageFile.takeIf { it.exists() }
}

private const val CAPTURED_IMAGE_NAME = "phase4_scan_input.jpg"
private const val DEBUG_PREVIEW_CHAR_LIMIT = 320
private const val LOW_CONFIDENCE_THRESHOLD = 0.65

private fun Double.formatNutritionNumber(): String {
    return if (this % 1.0 == 0.0) {
        toInt().toString()
    } else {
        String.format(Locale.US, "%.1f", this)
    }
}

private fun filterDecimalInput(value: String): String {
    var seenDecimal = false
    return value.filter { char ->
        when {
            char.isDigit() -> true
            char == '.' && !seenDecimal -> {
                seenDecimal = true
                true
            }
            else -> false
        }
    }
}
