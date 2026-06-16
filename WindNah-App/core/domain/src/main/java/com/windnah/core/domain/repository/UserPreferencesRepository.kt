package com.windnah.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val hasCompletedOnboarding: Flow<Boolean>
    suspend fun setOnboardingCompleted()
}
