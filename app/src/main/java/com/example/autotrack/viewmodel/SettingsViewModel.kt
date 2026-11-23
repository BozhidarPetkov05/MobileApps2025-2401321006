package com.example.autotrack.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.autotrack.data.repository.AuthRepository
import com.example.autotrack.datastore.PreferencesManager
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository = AuthRepository()
    private val preferencesManager = PreferencesManager(application)

    val currentUser: FirebaseUser?
        get() = authRepository.getCurrentUser()

    val themeMode: StateFlow<String> = preferencesManager.themeMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "SYSTEM")

    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            preferencesManager.saveThemeMode(mode)
        }
    }

    fun logout(onLogoutSuccess: () -> Unit) {
        authRepository.logout()
        onLogoutSuccess()
    }
}
