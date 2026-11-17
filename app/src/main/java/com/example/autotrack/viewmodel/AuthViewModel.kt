package com.example.autotrack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autotrack.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository = AuthRepository()) : ViewModel() {

    val currentUser: FirebaseUser?
        get() = authRepository.getCurrentUser()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun login(email: String, pass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                authRepository.login(email, pass)
                onSuccess()
            } catch (e: Exception) {
                val message = e.localizedMessage ?: ""
                _errorMessage.value = when {
                    message.contains("password", ignoreCase = true) || 
                    message.contains("credential", ignoreCase = true) || 
                    message.contains("no user", ignoreCase = true) ||
                    message.contains("user-not-found", ignoreCase = true) ||
                    message.contains("invalid-credential", ignoreCase = true) -> 
                        "Invalid email or password."
                    message.contains("format", ignoreCase = true) || 
                    message.contains("address", ignoreCase = true) ||
                    message.contains("invalid-email", ignoreCase = true) -> 
                        "Please enter a valid email address."
                    else -> "Login failed. Please check your details."
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun register(email: String, pass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                authRepository.register(email, pass)
                onSuccess()
            } catch (e: Exception) {
                val message = e.localizedMessage ?: ""
                _errorMessage.value = when {
                    message.contains("password", ignoreCase = true) || 
                    message.contains("weak-password", ignoreCase = true) -> 
                        "Password should be at least 6 characters."
                    message.contains("already in use", ignoreCase = true) || 
                    message.contains("email-already-in-use", ignoreCase = true) -> 
                        "Email is already registered."
                    message.contains("format", ignoreCase = true) || 
                    message.contains("address", ignoreCase = true) ||
                    message.contains("invalid-email", ignoreCase = true) -> 
                        "Please enter a valid email address."
                    else -> "Registration failed. Please try again."
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        authRepository.logout()
        onSuccess()
    }
}
