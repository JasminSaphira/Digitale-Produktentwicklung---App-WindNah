package com.windnah.core.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.windnah.core.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : UserPreferencesRepository {

    override val hasCompletedOnboarding: Flow<Boolean> =
        dataStore.data.map { it[KEY_ONBOARDING_COMPLETED] ?: false }

    override val isDarkModeEnabled: Flow<Boolean> =
        dataStore.data.map { it[KEY_DARK_MODE_ENABLED] ?: false }

    override suspend fun setOnboardingCompleted() {
        dataStore.edit { it[KEY_ONBOARDING_COMPLETED] = true }
    }

    override suspend fun setDarkModeEnabled(enabled: Boolean) {
        dataStore.edit { it[KEY_DARK_MODE_ENABLED] = enabled }
    }

    companion object {
        private val KEY_ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        private val KEY_DARK_MODE_ENABLED = booleanPreferencesKey("dark_mode_enabled")
    }
}
