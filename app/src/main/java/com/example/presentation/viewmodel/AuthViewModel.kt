package com.example.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.entity.UserEntity
import com.example.data.model.UserRole
import com.example.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    val currentUser: StateFlow<UserEntity?> = authRepository.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _infoMessage = MutableStateFlow<String?>(null)
    val infoMessage: StateFlow<String?> = _infoMessage.asStateFlow()

    val isFirebaseReady: Boolean
        get() = authRepository.isFirebaseReady

    init {
        viewModelScope.launch {
            authRepository.checkExistingSession()
        }
    }

    fun login(
        email: String,
        pass: String,
        role: UserRole? = null,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _infoMessage.value = null
            val result = authRepository.login(email, pass, role)
            _isLoading.value = false
            result.onSuccess {
                onSuccess()
            }.onFailure {
                _errorMessage.value = it.message ?: "Authentication failed."
            }
        }
    }

    fun quickLoginAs(role: UserRole, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _infoMessage.value = null
            val result = authRepository.quickLoginRole(role)
            _isLoading.value = false
            result.onSuccess {
                onSuccess()
            }.onFailure {
                _errorMessage.value = it.message ?: "Failed to switch role."
            }
        }
    }

    fun register(
        name: String,
        email: String,
        pass: String = "password123",
        role: UserRole,
        department: String,
        refId: String,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _infoMessage.value = null
            val result = authRepository.registerUser(name, email, pass, role, department, refId)
            _isLoading.value = false
            result.onSuccess {
                onSuccess()
            }.onFailure {
                _errorMessage.value = it.message ?: "Registration failed."
            }
        }
    }

    fun sendPasswordReset(email: String) {
        viewModelScope.launch {
            if (email.isBlank()) {
                _errorMessage.value = "Please enter your college email first."
                return@launch
            }
            _isLoading.value = true
            val result = authRepository.sendPasswordReset(email)
            _isLoading.value = false
            result.onSuccess {
                _infoMessage.value = "Password reset instructions sent to $email"
            }.onFailure {
                _errorMessage.value = it.message ?: "Could not send password reset email."
            }
        }
    }

    fun logout() {
        authRepository.logout()
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun clearInfo() {
        _infoMessage.value = null
    }
}
