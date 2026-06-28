package com.windnah.core.domain.repository

import com.windnah.core.model.AuthUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<AuthUser?>

    suspend fun signInWithEmail(
        email: String,
        password: String,
    ): AuthResult

    suspend fun registerWithEmail(
        email: String,
        password: String,
        displayName: String?,
    ): AuthResult

    suspend fun signOut()
}

sealed interface AuthResult {
    data class Success(val user: AuthUser) : AuthResult
    data class Failure(val reason: AuthErrorReason) : AuthResult
}

enum class AuthErrorReason {
    InvalidCredentials,
    AccountNotFound,
    EmailAlreadyInUse,
    WeakPassword,
    Network,
    NotConfigured,
    Unknown,
}
