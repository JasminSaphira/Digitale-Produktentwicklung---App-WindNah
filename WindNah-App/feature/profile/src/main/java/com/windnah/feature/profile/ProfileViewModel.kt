package com.windnah.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.windnah.core.domain.repository.AuthErrorReason
import com.windnah.core.domain.repository.AuthRepository
import com.windnah.core.domain.repository.AuthResult
import com.windnah.core.domain.repository.FavoriteRepository
import com.windnah.core.domain.repository.RecentlyViewedRepository
import com.windnah.core.domain.repository.UserPreferencesRepository
import com.windnah.core.model.AuthUser
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditProfileUiState(
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
)

data class ProfileStatsUiState(
    val visitsCount: Int = 0,
    val favoritesCount: Int = 0,
    val memberDays: Int = 0,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    favoriteRepository: FavoriteRepository,
    recentlyViewedRepository: RecentlyViewedRepository,
) : ViewModel() {

    private val _currentUser = MutableStateFlow<AuthUser?>(null)
    val currentUser: StateFlow<AuthUser?> = _currentUser.asStateFlow()

    private val _editProfileUiState = MutableStateFlow(EditProfileUiState())
    val editProfileUiState: StateFlow<EditProfileUiState> = _editProfileUiState.asStateFlow()

    private val memberDays = _currentUser
        .flatMapLatest { user ->
            if (user == null) {
                flowOf(0)
            } else {
                userPreferencesRepository.observeMemberSinceEpochDay(user.id)
                    .map { memberSinceEpochDay ->
                        memberSinceEpochDay?.let { since ->
                            max(1L, currentEpochDay() - since + 1L).toInt()
                        } ?: 0
                    }
            }
        }

    val profileStats: StateFlow<ProfileStatsUiState> = combine(
        recentlyViewedRepository.observeRecentlyViewedIds(limit = Int.MAX_VALUE).map { it.size },
        favoriteRepository.observeFavoriteIds().map { it.size },
        memberDays,
    ) { visitsCount, favoritesCount, memberDays ->
        ProfileStatsUiState(
            visitsCount = visitsCount,
            favoritesCount = favoritesCount,
            memberDays = memberDays,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProfileStatsUiState(),
    )

    init {
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                if (user != null) {
                    userPreferencesRepository.ensureMemberSinceEpochDay(
                        userId = user.id,
                        epochDay = currentEpochDay(),
                    )
                }
                _currentUser.value = user
            }
        }
    }

    val isLocationUsageEnabled: StateFlow<Boolean> = userPreferencesRepository.isLocationUsageEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    val showLiveOutputMetric: StateFlow<Boolean> = userPreferencesRepository.showLiveOutputMetric
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = true,
        )

    val showCo2SavingsMetric: StateFlow<Boolean> = userPreferencesRepository.showCo2SavingsMetric
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = true,
        )

    val showHouseholdsMetric: StateFlow<Boolean> = userPreferencesRepository.showHouseholdsMetric
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = true,
        )

    fun setLocationUsageEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setLocationUsageEnabled(enabled)
        }
    }

    fun setShowLiveOutputMetric(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setShowLiveOutputMetric(enabled)
        }
    }

    fun setShowCo2SavingsMetric(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setShowCo2SavingsMetric(enabled)
        }
    }

    fun setShowHouseholdsMetric(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setShowHouseholdsMetric(enabled)
        }
    }

    fun updateProfile(
        displayName: String,
        email: String,
        onSuccess: () -> Unit,
    ) {
        viewModelScope.launch {
            _editProfileUiState.update { it.copy(isSaving = true, errorMessage = null) }
            when (val result = authRepository.updateProfile(displayName, email)) {
                is AuthResult.Success -> {
                    _currentUser.value = result.user
                    _editProfileUiState.value = EditProfileUiState()
                    onSuccess()
                }
                is AuthResult.Failure -> {
                    _editProfileUiState.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = result.reason.toEditProfileMessage(),
                        )
                    }
                }
            }
        }
    }

    fun clearEditProfileError() {
        _editProfileUiState.update { it.copy(errorMessage = null) }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }
}

private fun currentEpochDay(): Long =
    TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis())

private fun AuthErrorReason.toEditProfileMessage(): String =
    when (this) {
        AuthErrorReason.InvalidEmail ->
            "Bitte geben Sie eine g\u00fcltige E-Mail-Adresse ein."
        AuthErrorReason.EmailAlreadyInUse ->
            "Diese E-Mail-Adresse wird bereits verwendet."
        AuthErrorReason.RequiresRecentLogin ->
            "Bitte melden Sie sich erneut an und versuchen Sie es noch einmal."
        AuthErrorReason.Network ->
            "Keine Verbindung. Bitte pr\u00fcfen Sie Ihre Internetverbindung."
        AuthErrorReason.NotConfigured ->
            "Profilbearbeitung ist noch nicht eingerichtet. Bitte Firebase konfigurieren."
        AuthErrorReason.AccountNotFound ->
            "Dieses Konto konnte nicht gefunden werden. Bitte melden Sie sich erneut an."
        AuthErrorReason.InvalidCredentials,
        AuthErrorReason.WeakPassword,
        AuthErrorReason.Unknown ->
            "Profil konnte nicht gespeichert werden. Bitte versuchen Sie es erneut."
    }
