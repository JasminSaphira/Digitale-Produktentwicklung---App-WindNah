package com.windnah.feature.auth

import com.windnah.core.domain.repository.AuthErrorReason

private const val MinPasswordLength = 8
private val EmailPattern = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

internal const val EmptyEmailMessage = "Bitte geben Sie Ihre E-Mail-Adresse ein."
internal const val InvalidEmailMessage = "Bitte geben Sie eine g\u00fcltige E-Mail-Adresse ein."
internal const val EmptyPasswordMessage = "Bitte geben Sie Ihr Passwort ein."
internal const val ShortPasswordMessage = "Das Passwort muss mindestens 8 Zeichen lang sein."
internal const val EmptyConfirmPasswordMessage = "Bitte best\u00e4tigen Sie Ihr Passwort."
internal const val PasswordMismatchMessage = "Die Passw\u00f6rter stimmen nicht \u00fcberein."
internal const val TermsNotAcceptedMessage =
    "Bitte stimmen Sie den Datenschutzbestimmungen und Nutzungsbedingungen zu."

data class PasswordRequirementUiModel(
    val label: String,
    val isMet: Boolean,
)

enum class AuthFailureReason {
    InvalidCredentials,
    AccountNotFound,
    Network,
}

internal fun AuthFailureReason.toFriendlyMessage(): String =
    when (this) {
        AuthFailureReason.InvalidCredentials ->
            "E-Mail oder Passwort stimmt nicht. Bitte pr\u00fcfen Sie Ihre Eingaben."
        AuthFailureReason.AccountNotFound ->
            "Zu dieser E-Mail-Adresse existiert noch kein Konto. M\u00f6chten Sie sich registrieren?"
        AuthFailureReason.Network ->
            "Keine Verbindung. Bitte pr\u00fcfen Sie Ihre Internetverbindung."
    }

internal fun AuthErrorReason.toFriendlyAuthMessage(): String =
    when (this) {
        AuthErrorReason.InvalidCredentials ->
            "E-Mail oder Passwort stimmt nicht. Bitte pr\u00fcfen Sie Ihre Eingaben."
        AuthErrorReason.InvalidEmail ->
            InvalidEmailMessage
        AuthErrorReason.AccountNotFound ->
            "Zu dieser E-Mail-Adresse existiert noch kein Konto. M\u00f6chten Sie sich registrieren?"
        AuthErrorReason.EmailAlreadyInUse ->
            "Zu dieser E-Mail-Adresse existiert bereits ein Konto. Bitte melden Sie sich an."
        AuthErrorReason.WeakPassword ->
            "Das Passwort ist zu schwach. Bitte w\u00e4hlen Sie ein st\u00e4rkeres Passwort."
        AuthErrorReason.RequiresRecentLogin ->
            "Bitte melden Sie sich erneut an und versuchen Sie es noch einmal."
        AuthErrorReason.Network ->
            "Keine Verbindung. Bitte pr\u00fcfen Sie Ihre Internetverbindung."
        AuthErrorReason.NotConfigured ->
            "Anmeldung ist noch nicht eingerichtet. Bitte Firebase konfigurieren."
        AuthErrorReason.Unknown ->
            "Anmeldung fehlgeschlagen. Bitte versuchen Sie es erneut."
    }

internal fun validateEmail(email: String): String? {
    val trimmedEmail = email.trim()
    return when {
        trimmedEmail.isEmpty() -> EmptyEmailMessage
        !EmailPattern.matches(trimmedEmail) -> InvalidEmailMessage
        else -> null
    }
}

internal fun validatePassword(password: String): String? =
    when {
        password.isEmpty() -> EmptyPasswordMessage
        password.length < MinPasswordLength -> ShortPasswordMessage
        else -> null
    }

internal fun validateConfirmPassword(password: String, confirmPassword: String): String? =
    when {
        confirmPassword.isEmpty() -> EmptyConfirmPasswordMessage
        confirmPassword != password -> PasswordMismatchMessage
        else -> null
    }

internal fun validateTermsAccepted(hasAcceptedTerms: Boolean): String? =
    if (hasAcceptedTerms) null else TermsNotAcceptedMessage

internal fun buildPasswordRequirements(password: String): List<PasswordRequirementUiModel> =
    listOf(
        PasswordRequirementUiModel(
            label = "Mindestens 8 Zeichen",
            isMet = password.length >= MinPasswordLength,
        ),
        PasswordRequirementUiModel(
            label = "Ein Gro\u00dfbuchstabe",
            isMet = password.any { it.isUpperCase() },
        ),
        PasswordRequirementUiModel(
            label = "Eine Zahl",
            isMet = password.any { it.isDigit() },
        ),
        PasswordRequirementUiModel(
            label = "Ein Sonderzeichen",
            isMet = password.any { !it.isLetterOrDigit() },
        ),
    )
