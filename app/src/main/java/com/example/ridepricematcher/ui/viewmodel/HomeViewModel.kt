package com.example.ridepricematcher.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ridepricematcher.AppModule
import com.example.ridepricematcher.ads.AdPolicy
import com.example.ridepricematcher.domain.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val authRepo = AppModule.authRepository
    private val entitlementRepo = RidePriceMatcherApplication.instance.entitlementRepository
    private val prefRepo = RidePriceMatcherApplication.instance.userPreferenceRepository
    private val langRepo = RidePriceMatcherApplication.instance.languageRepository

    private val _profile = MutableStateFlow<UserProfile?>(null)
    val profile: StateFlow<UserProfile?> = _profile.asStateFlow()

    private val _entitlement = MutableStateFlow<Entitlement?>(null)
    val entitlement: StateFlow<Entitlement?> = _entitlement.asStateFlow()

    private val _preferences = MutableStateFlow<UserPreferences?>(null)
    val preferences: StateFlow<UserPreferences?> = _preferences.asStateFlow()

    private val _languages = MutableStateFlow<List<LanguageConfig>>(emptyList())
    val languages: StateFlow<List<LanguageConfig>> = _languages.asStateFlow()

    private val _adProgress = MutableStateFlow(0)
    val adProgress: StateFlow<Int> = _adProgress.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<AppError?>(null)
    val error: StateFlow<AppError?> = _error.asStateFlow()

    init {
        loadHomeData()
        // FIX #1: Start language collection here, not inside loadHomeData
        viewModelScope.launch {
            langRepo.getEnabledLanguages().collect { list ->
                _languages.value = list
            }
        }
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _isLoading.value = true
            val userId = authRepo.currentUserId() ?: run {
                _isLoading.value = false
                return@launch
            }

            authRepo.getCurrentProfile()
                .onSuccess { _profile.value = it }
                .onFailure { _error.value = it as? AppError }

            // FIX #2: Handle refresh error
            authRepo.refreshSession()
                .onFailure { _error.value = it as? AppError }

            entitlementRepo.getEntitlement(userId)
                .onSuccess {
                    _entitlement.value = it
                    _profile.value?.let { profile ->
                        AdPolicy.setUser(RidePriceMatcherApplication.instance, profile, it)
                    }
                }
                .onFailure { _error.value = it as? AppError }

            prefRepo.getPreferences(userId)
                .onSuccess { _preferences.value = it }

            entitlementRepo.getAdRewardCount(userId)
                .onSuccess { _adProgress.value = it % 20 }

            // FIX #3: Just sync, don't collect here
            langRepo.syncLanguages()

            _isLoading.value = false
        }
    }

    fun refreshEntitlement() {
        viewModelScope.launch {
            val userId = authRepo.currentUserId() ?: return@launch
            entitlementRepo.getEntitlement(userId)
                .onSuccess { _entitlement.value = it }
        }
    }

    fun clearError() { _error.value = null }
}
