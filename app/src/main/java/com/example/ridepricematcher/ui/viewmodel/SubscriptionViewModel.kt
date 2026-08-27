package com.example.ridepricematcher.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ridepricematcher.RidePriceMatcherApplication
import com.example.ridepricematcher.domain.model.AppError
import com.example.ridepricematcher.domain.model.Entitlement
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SubscriptionViewModel : ViewModel() {

    private val authRepo = RidePriceMatcherApplication.instance.authRepository
    private val entitlementRepo = RidePriceMatcherApplication.instance.entitlementRepository

    private val _entitlement = MutableStateFlow<Entitlement?>(null)
    val entitlement: StateFlow<Entitlement?> = _entitlement.asStateFlow()

    private val _adProgress = MutableStateFlow(0)
    val adProgress: StateFlow<Int> = _adProgress.asStateFlow()

    private val _adState = MutableStateFlow<AdState>(AdState.Idle)
    val adState: StateFlow<AdState> = _adState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            val userId = authRepo.currentUserId() ?: return@launch
            entitlementRepo.getEntitlement(userId)
                .onSuccess { _entitlement.value = it }
            entitlementRepo.getAdRewardCount(userId)
                .onSuccess { _adProgress.value = it % 20 }
        }
    }

    fun onAdRewarded(provider: String, rewardId: String) {
        viewModelScope.launch {
            val userId = authRepo.currentUserId() ?: return@launch
            _adState.value = AdState.Processing
            entitlementRepo.recordAdReward(userId, provider, rewardId)
                .onSuccess {
                    _adProgress.value = (_adProgress.value + 1) % 20
                    _adState.value = AdState.Rewarded
                    loadData() // Refresh entitlement
                }
                .onFailure {
                    _adState.value = AdState.Error(it as? AppError ?: AppError.Unknown("Reward failed", ""))
                }
        }
    }

    fun resetAdState() { _adState.value = AdState.Idle }

    sealed class AdState {
        object Idle : AdState()
        object Processing : AdState()
        object Rewarded : AdState()
        data class Error(val error: AppError) : AdState()
    }
}
