package com.windnah.feature.auth

sealed interface RegistrationUiEvent {
    data class NameChanged(val name: String) : RegistrationUiEvent
    data class EmailChanged(val email: String) : RegistrationUiEvent
    data class PasswordChanged(val password: String) : RegistrationUiEvent
    data class ConfirmPasswordChanged(val confirmPassword: String) : RegistrationUiEvent
    data class TermsAcceptanceChanged(val hasAcceptedTerms: Boolean) : RegistrationUiEvent
    data object TogglePasswordVisibilityClicked : RegistrationUiEvent
    data object ToggleConfirmPasswordVisibilityClicked : RegistrationUiEvent
    data object SubmitClicked : RegistrationUiEvent
}
