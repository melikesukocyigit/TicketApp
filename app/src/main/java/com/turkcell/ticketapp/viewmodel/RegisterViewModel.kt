package com.turkcell.ticketapp.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.core.domain.auth.AuthRepository
import com.turkcell.ticketapp.util.toUserMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRegistered: Boolean = false
) {
    private val isEmailValid: Boolean
        get() = email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()

    private val isPasswordValid: Boolean
        get() = password.length in 8..128

    val canSubmit: Boolean get() = isEmailValid &&
            isPasswordValid &&
            password == confirmPassword &&
            !isLoading
}

class RegisterViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _state = MutableStateFlow(RegisterUiState())
    val state: StateFlow<RegisterUiState> = _state.asStateFlow()

    fun onEmailChange(value: String) = _state.update { it.copy(email = value, errorMessage = null) }
    fun onPasswordChange(value: String) = _state.update { it.copy(password = value, errorMessage = null) }
    fun onConfirmPasswordChange(value: String) = _state.update { it.copy(confirmPassword = value, errorMessage = null) }

    fun consumeError() = _state.update { it.copy(errorMessage = null) }
    fun submit() {
        val current = _state.value
        if (!current.canSubmit) return

        _state.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            authRepository.register(current.email, current.password)
                .onSuccess {
                    _state.update { it.copy(isLoading = false, isRegistered = true) }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, errorMessage = error.toUserMessage()) }
                }
        }
    }
}