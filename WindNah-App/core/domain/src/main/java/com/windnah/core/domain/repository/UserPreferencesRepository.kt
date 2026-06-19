package com.windnah.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val hasCompletedOnboarding: Flow<Boolean>
    val isDarkModeEnabled: Flow<Boolean>
    suspend fun setOnboardingCompleted()
    suspend fun setDarkModeEnabled(enabled: Boolean)
}
