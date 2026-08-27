package com.example.ridepricematcher.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ridepricematcher.RidePriceMatcherApplication
import com.example.ridepricematcher.domain.model.AppError
import com.example.ridepricematcher.domain.model.AuditLog
import com.example.ridepricematcher.domain.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminViewModel : ViewModel() {

    private val adminRepo = RidePriceMatcherApplication.instance.adminRepository
    private val authRepo = RidePriceMatcherApplication.instance.authRepository

    private val _users = MutableStateFlow<List<UserProfile>>(emptyList())
    val users: StateFlow<List<UserProfile>> = _users.asStateFlow()

    private val _auditLogs = MutableStateFlow<List<AuditLog>>(emptyList())
    val auditLogs: StateFlow<List<AuditLog>> = _auditLogs.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _actionResult = MutableStateFlow<ActionResult?>(null)
    val actionResult: StateFlow<ActionResult?> = _actionResult.asStateFlow()

    init {
        loadUsers()
        loadAuditLogs()
    }

    fun loadUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            adminRepo.getAllUsers()
                .onSuccess { _users.value = it }
                .onFailure { _actionResult.value = ActionResult.Error(it as? AppError ?: AppError.Unknown("", "")) }
            _isLoading.value = false
        }
    }

    fun loadAuditLogs() {
        viewModelScope.launch {
            adminRepo.getAuditLogs()
                .onSuccess { _auditLogs.value = it }
        }
    }

    fun blockUser(userId: String) {
        performAdminAction { adminRepo.blockUser(userId, authRepo.currentUserId() ?: "") }
    }

    fun unblockUser(userId: String) {
        performAdminAction { adminRepo.unblockUser(userId, authRepo.currentUserId() ?: "") }
    }

    fun grantAdFree(userId: String) {
        performAdminAction { adminRepo.grantAdFree(userId, authRepo.currentUserId() ?: "") }
    }

    fun revokeAdFree(userId: String) {
        performAdminAction { adminRepo.revokeAdFree(userId, authRepo.currentUserId() ?: "") }
    }

    fun grantLifetime(userId: String) {
        performAdminAction { adminRepo.grantLifetime(userId, authRepo.currentUserId() ?: "") }
    }

    private fun performAdminAction(action: suspend () -> Result<Unit>) {
        viewModelScope.launch {
            _isLoading.value = true
            action()
                .onSuccess {
                    _actionResult.value = ActionResult.Success
                    loadUsers()
                    loadAuditLogs()
                }
                .onFailure {
                    _actionResult.value = ActionResult.Error(it as? AppError ?: AppError.Unknown("", ""))
                }
            _isLoading.value = false
        }
    }

    fun clearResult() { _actionResult.value = null }

    sealed class ActionResult {
        object Success : ActionResult()
        data class Error(val error: AppError) : ActionResult()
    }
}
