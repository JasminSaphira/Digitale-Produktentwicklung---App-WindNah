package com.windnah.core.data.repository

import android.content.Context
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.UserProfileChangeRequest
import com.windnah.core.domain.repository.AuthErrorReason
import com.windnah.core.domain.repository.AuthRepository
import com.windnah.core.domain.repository.AuthResult
import com.windnah.core.model.AuthUser
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class FirebaseAuthRepository @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : AuthRepository {

    override val currentUser: Flow<AuthUser?> = callbackFlow {
        val auth = firebaseAuthOrNull()
        if (auth == null) {
            trySend(null)
            close()
            return@callbackFlow
        }

        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser?.toAuthUser())
        }
        auth.addAuthStateListener(listener)
        trySend(auth.currentUser?.toAuthUser())
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override suspend fun signInWithEmail(
        email: String,
        password: String,
    ): AuthResult {
        val auth = firebaseAuthOrNull() ?: return AuthResult.Failure(AuthErrorReason.NotConfigured)
        return authResultFromTask {
            auth.signInWithEmailAndPassword(email.trim(), password)
        }
    }

    override suspend fun registerWithEmail(
        email: String,
        password: String,
        displayName: String?,
    ): AuthResult {
        val auth = firebaseAuthOrNull() ?: return AuthResult.Failure(AuthErrorReason.NotConfigured)
        val result = authResultFromTask {
            auth.createUserWithEmailAndPassword(email.trim(), password)
        }

        val createdUser = (result as? AuthResult.Success)?.user
        if (createdUser != null && !displayName.isNullOrBlank()) {
            auth.currentUser?.updateProfile(
                UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName.trim())
                    .build(),
            )?.awaitCompletion()
        }

        return auth.currentUser?.toAuthUser()?.let(AuthResult::Success) ?: result
    }

    override suspend fun updateProfile(
        displayName: String,
        email: String,
    ): AuthResult {
        val auth = firebaseAuthOrNull() ?: return AuthResult.Failure(AuthErrorReason.NotConfigured)
        val user = auth.currentUser ?: return AuthResult.Failure(AuthErrorReason.AccountNotFound)

        user.updateProfile(
            UserProfileChangeRequest.Builder()
                .setDisplayName(displayName.trim().ifEmpty { null })
                .build(),
        ).awaitCompletion()?.let { reason ->
            return AuthResult.Failure(reason)
        }

        val trimmedEmail = email.trim()
        if (!user.email.equals(trimmedEmail, ignoreCase = true)) {
            @Suppress("DEPRECATION")
            user.updateEmail(trimmedEmail).awaitCompletion()?.let { reason ->
                return AuthResult.Failure(reason)
            }
        }

        return auth.currentUser?.toAuthUser()?.let(AuthResult::Success)
            ?: AuthResult.Failure(AuthErrorReason.Unknown)
    }

    override suspend fun signOut() {
        firebaseAuthOrNull()?.signOut()
    }

    private fun firebaseAuthOrNull(): FirebaseAuth? =
        if (FirebaseApp.getApps(context).isEmpty()) {
            null
        } else {
            FirebaseAuth.getInstance()
        }

    private suspend fun authResultFromTask(
        taskProvider: () -> com.google.android.gms.tasks.Task<com.google.firebase.auth.AuthResult>,
    ): AuthResult =
        suspendCancellableCoroutine { continuation ->
            taskProvider().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user?.toAuthUser()
                    continuation.resume(
                        if (user != null) {
                            AuthResult.Success(user)
                        } else {
                            AuthResult.Failure(AuthErrorReason.Unknown)
                        },
                    )
                } else {
                    continuation.resume(AuthResult.Failure(task.exception.toAuthErrorReason()))
                }
            }
        }

    private suspend fun com.google.android.gms.tasks.Task<Void>.awaitCompletion(): AuthErrorReason? {
        return suspendCancellableCoroutine { continuation ->
            addOnCompleteListener {
                continuation.resume(if (it.isSuccessful) null else it.exception.toAuthErrorReason())
            }
        }
    }
}

private fun com.google.firebase.auth.FirebaseUser.toAuthUser(): AuthUser =
    AuthUser(
        id = uid,
        email = email,
        displayName = displayName,
    )

private fun Throwable?.toAuthErrorReason(): AuthErrorReason =
    when (this) {
        is FirebaseNetworkException -> AuthErrorReason.Network
        is FirebaseAuthInvalidUserException -> AuthErrorReason.AccountNotFound
        is FirebaseAuthRecentLoginRequiredException -> AuthErrorReason.RequiresRecentLogin
        is FirebaseAuthInvalidCredentialsException -> AuthErrorReason.InvalidCredentials
        is FirebaseAuthUserCollisionException -> AuthErrorReason.EmailAlreadyInUse
        is FirebaseAuthWeakPasswordException -> AuthErrorReason.WeakPassword
        is FirebaseTooManyRequestsException -> AuthErrorReason.Network
        is FirebaseAuthException -> when (errorCode) {
            "ERROR_USER_NOT_FOUND" -> AuthErrorReason.AccountNotFound
            "ERROR_WRONG_PASSWORD",
            "ERROR_INVALID_CREDENTIAL" -> AuthErrorReason.InvalidCredentials
            "ERROR_INVALID_EMAIL" -> AuthErrorReason.InvalidEmail
            "ERROR_EMAIL_ALREADY_IN_USE" -> AuthErrorReason.EmailAlreadyInUse
            "ERROR_WEAK_PASSWORD" -> AuthErrorReason.WeakPassword
            "ERROR_REQUIRES_RECENT_LOGIN" -> AuthErrorReason.RequiresRecentLogin
            "ERROR_NETWORK_REQUEST_FAILED" -> AuthErrorReason.Network
            else -> AuthErrorReason.Unknown
        }
        else -> AuthErrorReason.Unknown
    }
