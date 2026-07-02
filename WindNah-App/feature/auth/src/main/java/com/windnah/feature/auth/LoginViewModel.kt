package com.windnah.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.windnah.core.domain.repository.AuthRepository
import com.windnah.core.domain.repository.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEvent(event: LoginUiEvent) {
        when (event) {
            is LoginUiEvent.EmailChanged -> onEmailChanged(event.email)
            is LoginUiEvent.PasswordChanged -> onPasswordChanged(event.password)
            LoginUiEvent.TogglePasswordVisibilityClicked -> togglePasswordVisibility()
            LoginUiEvent.SubmitClicked -> submit()
        }
    }

    private fun onEmailChanged(email: String) {
        _uiState.update { state ->
            state.copy(
                email = email,
                emailError = if (state.hasSubmitted) validateEmail(email) else null,
                authErrorMessage = null,
                isAuthenticated = false,
            )
        }
    }

    private fun onPasswordChanged(password: String) {
        _uiState.update { state ->
            state.copy(
                password = password,
                passwordError = if (state.hasSubmitted) validatePassword(password) else null,
                authErrorMessage = null,
                isAuthenticated = false,
            )
        }
    }

    private fun togglePasswordVisibility() {
        _uiState.update { state ->
            state.copy(isPasswordVisible = !state.isPasswordVisible)
        }
    }

    private fun submit() {
        val state = _uiState.value
        val emailError = validateEmail(state.email)
        val passwordError = validatePassword(state.password)

        _uiState.update {
            state.copy(
                hasSubmitted = true,
                emailError = emailError,
                passwordError = passwordError,
                authErrorMessage = null,
                isAuthenticated = false,
            )
        }

        if (emailError != null || passwordError != null) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, authErrorMessage = null) }
            when (val result = authRepository.signInWithEmail(state.email, state.password)) {
                is AuthResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isAuthenticated = true,
                            authErrorMessage = null,
                        )
                    }
                }
                is AuthResult.Failure -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isAuthenticated = false,
                            authErrorMessage = result.reason.toFriendlyAuthMessage(),
                        )
                    }
                }
            }
        }
    }
}
