package com.windnah.feature.auth

data class RegistrationUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val hasAcceptedTerms: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val termsError: String? = null,
    val authErrorMessage: String? = null,
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val passwordRequirements: List<PasswordRequirementUiModel> = buildPasswordRequirements(""),
    val hasSubmitted: Boolean = false,
)
