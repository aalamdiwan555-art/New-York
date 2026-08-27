package com.example.ridepricematcher.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OnboardingViewModel : ViewModel() {

    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    private val _totalPages = MutableStateFlow(9)
    val totalPages: StateFlow<Int> = _totalPages.asStateFlow()

    private val _completed = MutableStateFlow(false)
    val completed: StateFlow<Boolean> = _completed.asStateFlow()

    fun nextPage() {
        if (_currentPage.value < _totalPages.value - 1) {
            _currentPage.value++
        } else {
            _completed.value = true
        }
    }

    fun previousPage() {
        if (_currentPage.value > 0) {
            _currentPage.value--
        }
    }

    fun skipToEnd() {
        _completed.value = true
    }
}
