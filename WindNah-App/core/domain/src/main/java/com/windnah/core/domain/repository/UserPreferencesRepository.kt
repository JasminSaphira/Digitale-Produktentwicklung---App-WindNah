package com.windnah.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val hasCompletedOnboarding: Flow<Boolean>
    val isLocationUsageEnabled: Flow<Boolean>
    val showLiveOutputMetric: Flow<Boolean>
    val showCo2SavingsMetric: Flow<Boolean>
    val showHouseholdsMetric: Flow<Boolean>
    suspend fun setOnboardingCompleted()
    suspend fun setLocationUsageEnabled(enabled: Boolean)
    suspend fun setShowLiveOutputMetric(enabled: Boolean)
    suspend fun setShowCo2SavingsMetric(enabled: Boolean)
    suspend fun setShowHouseholdsMetric(enabled: Boolean)
}
