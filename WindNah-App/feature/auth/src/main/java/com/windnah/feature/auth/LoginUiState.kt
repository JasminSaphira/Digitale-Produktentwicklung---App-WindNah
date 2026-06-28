package com.windnah.feature.auth

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val authErrorMessage: String? = null,
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val hasSubmitted: Boolean = false,
)
