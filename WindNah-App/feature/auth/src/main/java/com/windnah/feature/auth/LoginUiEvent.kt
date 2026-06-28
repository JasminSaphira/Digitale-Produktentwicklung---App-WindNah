package com.windnah.feature.auth

sealed interface LoginUiEvent {
    data class EmailChanged(val email: String) : LoginUiEvent
    data class PasswordChanged(val password: String) : LoginUiEvent
    data object TogglePasswordVisibilityClicked : LoginUiEvent
    data object SubmitClicked : LoginUiEvent
}
