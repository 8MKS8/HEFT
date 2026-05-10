package com.heft.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heft.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// AuthViewModel — manages Login and Register screen state
class AuthViewModel : ViewModel() {

    // Repository for Firebase Auth operations
    private val repository = AuthRepository()

    // UI state for auth screens
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    // Check if user is already logged in
    val isLoggedIn: Boolean get() = repository.isLoggedIn

    // ── LOGIN ─────────────────────────────────────────────────────────────

    fun login(email: String, password: String) {
        // Validate inputs first
        if (email.isBlank()) {
            _authState.value = AuthState.Error("Please enter your email")
            return
        }
        if (password.isBlank()) {
            _authState.value = AuthState.Error("Please enter your password")
            return
        }

        // Perform login
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.login(email, password)
            result.onSuccess {
                _authState.value = AuthState.Success
            }
            result.onFailure { e ->
                _authState.value = AuthState.Error(
                    e.message ?: "Login failed. Please try again."
                )
            }
        }
    }

    // ── REGISTER ──────────────────────────────────────────────────────────

    fun register(email: String, password: String, displayName: String) {
        // Validate inputs
        if (displayName.isBlank()) {
            _authState.value = AuthState.Error("Please enter your name")
            return
        }
        if (email.isBlank()) {
            _authState.value = AuthState.Error("Please enter your email")
            return
        }
        if (password.length < 6) {
            _authState.value = AuthState.Error("Password must be at least 6 characters")
            return
        }

        // Perform registration
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.register(email, password, displayName)
            result.onSuccess {
                _authState.value = AuthState.Success
            }
            result.onFailure { e ->
                _authState.value = AuthState.Error(
                    e.message ?: "Registration failed. Please try again."
                )
            }
        }
    }

    // ── RESET ─────────────────────────────────────────────────────────────

    // Reset state back to idle
    fun resetState() {
        _authState.value = AuthState.Idle
    }
}

// All possible states for the auth screen
sealed class AuthState {
    object Idle    : AuthState()   // Default state
    object Loading : AuthState()   // Showing loading spinner
    object Success : AuthState()   // Login/Register successful
    data class Error(val message: String) : AuthState() // Error message
}