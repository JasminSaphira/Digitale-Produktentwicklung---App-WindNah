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
class RegistrationViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistrationUiState())
    val uiState: StateFlow<RegistrationUiState> = _uiState.asStateFlow()

    fun onEvent(event: RegistrationUiEvent) {
        when (event) {
            is RegistrationUiEvent.NameChanged -> onNameChanged(event.name)
            is RegistrationUiEvent.EmailChanged -> onEmailChanged(event.email)
            is RegistrationUiEvent.PasswordChanged -> onPasswordChanged(event.password)
            is RegistrationUiEvent.ConfirmPasswordChanged -> onConfirmPasswordChanged(event.confirmPassword)
            is RegistrationUiEvent.TermsAcceptanceChanged -> onTermsAcceptanceChanged(event.hasAcceptedTerms)
            RegistrationUiEvent.TogglePasswordVisibilityClicked -> togglePasswordVisibility()
            RegistrationUiEvent.ToggleConfirmPasswordVisibilityClicked -> toggleConfirmPasswordVisibility()
            RegistrationUiEvent.SubmitClicked -> submit()
        }
    }

    private fun onNameChanged(name: String) {
        _uiState.update { state ->
            state.copy(
                name = name,
                authErrorMessage = null,
                isAuthenticated = false,
            )
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
                confirmPasswordError = if (state.hasSubmitted) {
                    validateConfirmPassword(password, state.confirmPassword)
                } else {
                    null
                },
                passwordRequirements = buildPasswordRequirements(password),
                authErrorMessage = null,
                isAuthenticated = false,
            )
        }
    }

    private fun onConfirmPasswordChanged(confirmPassword: String) {
        _uiState.update { state ->
            state.copy(
                confirmPassword = confirmPassword,
                confirmPasswordError = if (state.hasSubmitted) {
                    validateConfirmPassword(state.password, confirmPassword)
                } else {
                    null
                },
                authErrorMessage = null,
                isAuthenticated = false,
            )
        }
    }

    private fun onTermsAcceptanceChanged(hasAcceptedTerms: Boolean) {
        _uiState.update { state ->
            state.copy(
                hasAcceptedTerms = hasAcceptedTerms,
                termsError = if (state.hasSubmitted) validateTermsAccepted(hasAcceptedTerms) else null,
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

    private fun toggleConfirmPasswordVisibility() {
        _uiState.update { state ->
            state.copy(isConfirmPasswordVisible = !state.isConfirmPasswordVisible)
        }
    }

    private fun submit() {
        val state = _uiState.value
        val emailError = validateEmail(state.email)
        val passwordError = validatePassword(state.password)
        val confirmPasswordError = validateConfirmPassword(state.password, state.confirmPassword)
        val termsError = validateTermsAccepted(state.hasAcceptedTerms)

        _uiState.update {
            state.copy(
                hasSubmitted = true,
                emailError = emailError,
                passwordError = passwordError,
                confirmPasswordError = confirmPasswordError,
                termsError = termsError,
                passwordRequirements = buildPasswordRequirements(state.password),
                authErrorMessage = null,
                isAuthenticated = false,
            )
        }

        if (
            emailError != null ||
            passwordError != null ||
            confirmPasswordError != null ||
            termsError != null
        ) {
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, authErrorMessage = null) }
            when (
                val result = authRepository.registerWithEmail(
                    email = state.email,
                    password = state.password,
                    displayName = state.name,
                )
            ) {
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

    @Suppress("unused")
    private fun showAuthFailure(reason: AuthFailureReason) {
        _uiState.update { state ->
            state.copy(authErrorMessage = reason.toFriendlyMessage())
        }
    }
}
