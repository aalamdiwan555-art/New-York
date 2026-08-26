package com.example.ridepricematcher.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ridepricematcher.RidePriceMatcherApplication
import com.example.ridepricematcher.domain.model.AppError
import com.example.ridepricematcher.domain.model.PriceRule
import com.example.ridepricematcher.domain.model.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {

    private val prefRepo = RidePriceMatcherApplication.instance.userPreferenceRepository
    private val authRepo = RidePriceMatcherApplication.instance.authRepository

    private val _preferences = MutableStateFlow<UserPreferences?>(null)
    val preferences: StateFlow<UserPreferences?> = _preferences.asStateFlow()

    private val _priceRule = MutableStateFlow(PriceRule())
    val priceRule: StateFlow<PriceRule> = _priceRule.asStateFlow()

    private val _saveState = MutableStateFlow<SaveState>(SaveState.Idle)
    val saveState: StateFlow<SaveState> = _saveState.asStateFlow()

    init {
        loadPreferences()
    }

    private fun loadPreferences() {
        viewModelScope.launch {
            val userId = authRepo.currentUserId() ?: return@launch
            prefRepo.getPreferences(userId)
                .onSuccess { prefs ->
                    _preferences.value = prefs
                    _priceRule.value = PriceRule(
                        minimumFare = prefs.minimumPrice,
                        maximumFare = prefs.maximumPrice,
                        currency = "INR"
                    )
                }
        }
    }

    fun updatePriceRule(rule: PriceRule) {
        _priceRule.value = rule
    }

    fun updateSelectedLanguages(languages: List<String>) {
        _preferences.value = _preferences.value?.copy(selectedLanguages = languages)
    }

    fun toggleMatching(enabled: Boolean) {
        _preferences.value = _preferences.value?.copy(matchingEnabled = enabled)
    }

    fun savePreferences() {
        viewModelScope.launch {
            val userId = authRepo.currentUserId() ?: return@launch
            val current = _preferences.value ?: UserPreferences(userId = userId)
            val updated = current.copy(
                minimumPrice = _priceRule.value.minimumFare,
                maximumPrice = _priceRule.value.maximumFare,
                matchingEnabled = current.matchingEnabled
            )
            _saveState.value = SaveState.Saving
            prefRepo.savePreferences(updated)
                .onSuccess { _saveState.value = SaveState.Saved }
                .onFailure { _saveState.value = SaveState.Error(it as? AppError ?: AppError.Unknown("Save failed", "")) }
        }
    }

    sealed class SaveState {
        object Idle : SaveState()
        object Saving : SaveState()
        object Saved : SaveState()
        data class Error(val error: AppError) : SaveState()
    }
}
