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

    override val isLocationUsageEnabled: Flow<Boolean> =
        dataStore.data.map { it[KEY_LOCATION_USAGE_ENABLED] ?: false }

    override val showLiveOutputMetric: Flow<Boolean> =
        dataStore.data.map { it[KEY_SHOW_LIVE_OUTPUT_METRIC] ?: true }

    override val showCo2SavingsMetric: Flow<Boolean> =
        dataStore.data.map { it[KEY_SHOW_CO2_SAVINGS_METRIC] ?: true }

    override val showHouseholdsMetric: Flow<Boolean> =
        dataStore.data.map { it[KEY_SHOW_HOUSEHOLDS_METRIC] ?: true }

    override suspend fun setOnboardingCompleted() {
        dataStore.edit { it[KEY_ONBOARDING_COMPLETED] = true }
    }

    override suspend fun setDarkModeEnabled(enabled: Boolean) {
        dataStore.edit { it[KEY_DARK_MODE_ENABLED] = enabled }
    }

    override suspend fun setLocationUsageEnabled(enabled: Boolean) {
        dataStore.edit { it[KEY_LOCATION_USAGE_ENABLED] = enabled }
    }

    override suspend fun setShowLiveOutputMetric(enabled: Boolean) {
        dataStore.edit { it[KEY_SHOW_LIVE_OUTPUT_METRIC] = enabled }
    }

    override suspend fun setShowCo2SavingsMetric(enabled: Boolean) {
        dataStore.edit { it[KEY_SHOW_CO2_SAVINGS_METRIC] = enabled }
    }

    override suspend fun setShowHouseholdsMetric(enabled: Boolean) {
        dataStore.edit { it[KEY_SHOW_HOUSEHOLDS_METRIC] = enabled }
    }

    companion object {
        private val KEY_ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        private val KEY_DARK_MODE_ENABLED = booleanPreferencesKey("dark_mode_enabled")
        private val KEY_LOCATION_USAGE_ENABLED = booleanPreferencesKey("location_usage_enabled")
        private val KEY_SHOW_LIVE_OUTPUT_METRIC = booleanPreferencesKey("show_live_output_metric")
        private val KEY_SHOW_CO2_SAVINGS_METRIC = booleanPreferencesKey("show_co2_savings_metric")
        private val KEY_SHOW_HOUSEHOLDS_METRIC = booleanPreferencesKey("show_households_metric")
    }
}
