package com.example.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data.repository.AiRepository
import com.example.data.repository.AuthRepository
import com.example.data.repository.CampusRepository

class CampusViewModelFactory(
    private val authRepository: AuthRepository,
    private val campusRepository: CampusRepository,
    private val aiRepository: AiRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(authRepository) as T
            }
            modelClass.isAssignableFrom(CampusViewModel::class.java) -> {
                CampusViewModel(campusRepository) as T
            }
            modelClass.isAssignableFrom(AiViewModel::class.java) -> {
                AiViewModel(aiRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
