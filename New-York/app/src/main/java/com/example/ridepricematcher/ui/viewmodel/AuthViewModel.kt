package com.example.ridepricematcher.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ridepricematcher.RidePriceMatcherApplication
import com.example.ridepricematcher.domain.model.AppError
import com.example.ridepricematcher.domain.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val authRepo = RidePriceMatcherApplication.instance.authRepository

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _currentUser = MutableStateFlow<UserProfile?>(null)
    val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()

    init {
        checkSession()
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error(AppError.Validation("Please fill all fields", "Empty fields"))
            return
        }
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            authRepo.signIn(email, password)
                .onSuccess { user ->
                    _currentUser.value = user
                    _uiState.value = AuthUiState.Authenticated(user)
                }
                .onFailure { error ->
                    _uiState.value = AuthUiState.Error(error as? AppError ?: AppError.Unknown("Login failed", error.message ?: ""))
                }
        }
    }

    fun signup(name: String, email: String, password: String, confirmPassword: String) {
        when {
            name.isBlank() || email.isBlank() || password.isBlank() ->
                _uiState.value = AuthUiState.Error(AppError.Validation("Please fill all fields", "Empty fields"))
            password.length < 6 ->
                _uiState.value = AuthUiState.Error(AppError.Validation("Password must be at least 6 characters", "Short password"))
            password != confirmPassword ->
                _uiState.value = AuthUiState.Error(AppError.Validation("Passwords do not match", "Mismatch"))
            else -> {
                _uiState.value = AuthUiState.Loading
                viewModelScope.launch {
                    authRepo.signUp(email, password, name)
                        .onSuccess { user ->
                            _currentUser.value = user
                            _uiState.value = AuthUiState.Authenticated(user)
                        }
                        .onFailure { error ->
                            _uiState.value = AuthUiState.Error(error as? AppError ?: AppError.Unknown("Signup failed", error.message ?: ""))
                        }
                }
            }
        }
    }

    fun forgotPassword(email: String) {
        if (email.isBlank()) {
            _uiState.value = AuthUiState.Error(AppError.Validation("Please enter your email", "Empty email"))
            return
        }
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            authRepo.sendPasswordReset(email)
                .onSuccess {
                    _uiState.value = AuthUiState.PasswordResetSent
                }
                .onFailure { error ->
                    _uiState.value = AuthUiState.Error(error as? AppError ?: AppError.Unknown("Request failed", error.message ?: ""))
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepo.signOut()
            _currentUser.value = null
            _uiState.value = AuthUiState.Idle
        }
    }

    private fun checkSession() {
        viewModelScope.launch {
            if (authRepo.isAuthenticated()) {
                authRepo.refreshSession()
                    .onSuccess {
                        // Session valid, user will be loaded by Home
                        _uiState.value = AuthUiState.SessionRestored
                    }
                    .onFailure {
                        authRepo.signOut()
                        _uiState.value = AuthUiState.Idle
                    }
            }
        }
    }

    fun clearError() {
        if (_uiState.value is AuthUiState.Error) {
            _uiState.value = AuthUiState.Idle
        }
    }

    sealed class AuthUiState {
        object Idle : AuthUiState()
        object Loading : AuthUiState()
        object SessionRestored : AuthUiState()
        object PasswordResetSent : AuthUiState()
        data class Authenticated(val user: UserProfile) : AuthUiState()
        data class Error(val error: AppError) : AuthUiState()
    }
}
