package com.windnah.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.windnah.core.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

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
}
