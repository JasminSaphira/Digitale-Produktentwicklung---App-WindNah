package com.example.windnah

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.windnah.navigation.ROUTE_DISCOVER
import com.example.windnah.navigation.ROUTE_ONBOARDING
import com.windnah.core.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    val startDestination: StateFlow<String?> = userPreferencesRepository.hasCompletedOnboarding
        .map { hasCompleted -> if (hasCompleted) ROUTE_DISCOVER else ROUTE_ONBOARDING }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    val darkModeEnabled: StateFlow<Boolean> = userPreferencesRepository.isDarkModeEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )
}
