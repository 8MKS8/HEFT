package com.heft.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.heft.data.model.User
import com.heft.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ProfileViewModel – manages the state for the Profile screen.
 * Loads and saves user profile data to Firestore.
 */
class ProfileViewModel : ViewModel() {

    // Repository for Firebase operations
    private val repository = AuthRepository()

    // Current user from Firebase Auth
    private val currentUser = FirebaseAuth.getInstance().currentUser

    // UI state
    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val profileState: StateFlow<ProfileState> = _profileState

    // User data fields
    val userEmail = currentUser?.email ?: ""
    val userId    = currentUser?.uid ?: ""

    init {
        // Load profile when ViewModel is created
        loadProfile()
    }

    /**
     * Load user profile from Firestore.
     */
    fun loadProfile() {
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading
            // For now show the current auth user data
            _profileState.value = ProfileState.Success(
                User(
                    uid   = userId,
                    email = userEmail
                )
            )
        }
    }

    /**
     * Save updated profile to Firestore.
     */
    fun saveProfile(displayName: String, fitnessGoal: String, weeklyTarget: Int) {
        viewModelScope.launch {
            _profileState.value = ProfileState.Saving

            // Validate inputs
            if (displayName.isBlank()) {
                _profileState.value = ProfileState.Error("Please enter your name")
                return@launch
            }

            if (weeklyTarget < 1 || weeklyTarget > 14) {
                _profileState.value = ProfileState.Error("Weekly target must be between 1 and 14")
                return@launch
            }

            // TODO: Save to Firestore
            _profileState.value = ProfileState.Saved
        }
    }

    /**
     * Reset state back to success.
     */
    fun resetState() {
        loadProfile()
    }
}

/**
 * ProfileState – all possible UI states for the Profile screen.
 */
sealed class ProfileState {
    object Loading : ProfileState()
    object Saving  : ProfileState()
    object Saved   : ProfileState()
    data class Success(val user: User) : ProfileState()
    data class Error(val message: String) : ProfileState()
}