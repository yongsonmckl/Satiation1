package com.mckl.satiation1.navigation

import android.app.Application
import android.graphics.Bitmap
import androidx.room.withTransaction
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mckl.satiation1.database.AppSettings
import com.mckl.satiation1.database.MealItem
import com.mckl.satiation1.database.MealLog
import com.mckl.satiation1.database.PresetFood
import com.mckl.satiation1.database.SatiationDatabase
import com.mckl.satiation1.database.UserProfile
import com.mckl.satiation1.database.WeightLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SatiationViewModel(application: Application) : AndroidViewModel(application) {

    var capturedImage = mutableStateOf<Bitmap?>(null)
    var currentMainTab by mutableStateOf("home")

    // Temporary memory for onboarding setup.
    var setupName = ""
    var setupWeightKg = 0.0
    var setupHeightCm = 0.0
    var setupPronouns = ""

    private val database = SatiationDatabase.getDatabase(application)
    private val userProfileDao = database.userProfileDao()
    private val appSettingsDao = database.appSettingsDao()
    private val mealDao = database.mealDao()
    private val weightLogDao = database.weightLogDao()
    private val presetFoodDao = database.presetFoodDao()
    private val _isUserProfileLoaded = MutableStateFlow(false)
    val isUserProfileLoaded: StateFlow<Boolean> = _isUserProfileLoaded.asStateFlow()

    val userProfile: StateFlow<UserProfile?> = userProfileDao
        .getUserProfile()
        .onEach { _isUserProfileLoaded.value = true }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val appSettings: StateFlow<AppSettings?> = appSettingsDao.getSettings().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    val currentBmi: StateFlow<Double?> = userProfileDao.getCurrentBmi().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    val weightHistory: StateFlow<List<WeightLog>> = weightLogDao.getWeightHistory().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val presetFoods: StateFlow<List<PresetFood>> = presetFoodDao.getPresetFoods().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    init {
        viewModelScope.launch(Dispatchers.IO) {
            if (appSettingsDao.getSettingsOnce() == null) {
                appSettingsDao.insertOrUpdateSettings(AppSettings())
            }
        }
    }

    fun saveProfile(
        name: String,
        startWeightKg: Double,
        currentWeightKg: Double,
        pronouns: String,
        heightCm: Double? = userProfile.value?.heightCm
    ) {
        val trimmedName = name.trim()
        val trimmedPronouns = pronouns.trim()
        if (trimmedName.isBlank() || startWeightKg <= 0.0 || currentWeightKg <= 0.0) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val existingProfile = userProfile.value
            val sanitizedHeight = heightCm?.takeIf { it > 0.0 }
            val newProfile = UserProfile(
                id = 1,
                name = trimmedName,
                pronouns = trimmedPronouns,
                heightCm = sanitizedHeight,
                startWeightKg = startWeightKg,
                currentWeightKg = currentWeightKg
            )
            userProfileDao.insertOrUpdateProfile(newProfile)

            val shouldAppendWeightLog = existingProfile == null ||
                existingProfile.currentWeightKg != currentWeightKg
            if (shouldAppendWeightLog) {
                weightLogDao.insertWeightLog(
                    WeightLog(
                        loggedAtEpochMillis = System.currentTimeMillis(),
                        weightKg = currentWeightKg
                    )
                )
            }
        }
    }

    fun saveSettings(settings: AppSettings) {
        viewModelScope.launch(Dispatchers.IO) {
            appSettingsDao.insertOrUpdateSettings(settings.copy(id = 1))
        }
    }

    fun logWeight(weightKg: Double, timestampMillis: Long = System.currentTimeMillis()) {
        if (weightKg <= 0.0) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            weightLogDao.insertWeightLog(
                WeightLog(
                    loggedAtEpochMillis = timestampMillis,
                    weightKg = weightKg
                )
            )

            userProfile.value?.let { profile ->
                userProfileDao.insertOrUpdateProfile(profile.copy(currentWeightKg = weightKg))
            }
        }
    }

    fun insertMeal(
        mealLog: MealLog,
        items: List<MealItem>
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            database.withTransaction {
                val mealId = mealDao.insertMealLog(mealLog)
                if (items.isNotEmpty()) {
                    mealDao.insertMealItems(items.map { it.copy(mealId = mealId) })
                }
            }
        }
    }

    fun getMealsForRange(startInclusive: Long, endInclusive: Long) =
        mealDao.getMealsBetween(startInclusive, endInclusive)

    fun getDailyMacroTotals(startInclusive: Long, endInclusive: Long) =
        mealDao.getDailyMacroTotals(startInclusive, endInclusive)

    fun getDailyMealSummaries(startInclusive: Long, endInclusive: Long) =
        mealDao.getDailyMealSummaries(startInclusive, endInclusive)

    fun savePresetFood(
        presetFoodId: Long = 0,
        name: String,
        category: String?,
        calories: Double,
        proteinGrams: Double,
        carbsGrams: Double,
        fatsGrams: Double,
        notes: String?
    ) {
        val trimmedName = name.trim()
        if (trimmedName.isBlank()) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            presetFoodDao.insertOrUpdatePresetFood(
                PresetFood(
                    presetFoodId = presetFoodId,
                    name = trimmedName,
                    category = category?.trim()?.ifEmpty { null },
                    calories = calories,
                    proteinGrams = proteinGrams,
                    carbsGrams = carbsGrams,
                    fatsGrams = fatsGrams,
                    notes = notes?.trim()?.ifEmpty { null },
                    updatedAtEpochMillis = System.currentTimeMillis()
                )
            )
        }
    }

    fun deletePresetFood(presetFoodId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            presetFoodDao.deletePresetFoodById(presetFoodId)
        }
    }
}
