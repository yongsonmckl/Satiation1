package com.mckl.satiation1.navigation

import android.app.Application
import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mckl.satiation1.database.SatiationDatabase
import com.mckl.satiation1.database.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class SatiationViewModel(application: Application) : AndroidViewModel(application) {

    var capturedImage = mutableStateOf<Bitmap?>(null)

    // Temporary memory for Onboarding setup
    var setupName = ""
    var setupAge = 0
    var setupWeight = 0
    var setupPronouns = ""

    private val userDao = SatiationDatabase.getDatabase(application).userDao()

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    init {
        // The second the app opens, load the user's profile from the database
        viewModelScope.launch {
            userDao.getUserProfile().collect { profile ->
                _userProfile.value = profile
            }
        }
    }

    // 4. Function to save edits back into the database
    fun saveProfile(
        name: String,
        age: Int,
        startWeight: Int,
        currentWeight: Int,
        pronouns: String
    ) {
        // We add Dispatchers.IO to manually push this to a background thread!
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            val newProfile = UserProfile(
                id = 1,
                name = name,
                age = age,
                startWeight = startWeight,
                currentWeight = currentWeight,
                pronouns = pronouns
            )
            userDao.insertOrUpdateProfile(newProfile)
        }
    }
}