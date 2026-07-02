package com.windnah.core.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import com.windnah.core.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : UserPreferencesRepository {

    override val hasCompletedOnboarding: Flow<Boolean> =
        dataStore.data.map { it[KEY_ONBOARDING_COMPLETED] ?: false }

    override val isLocationUsageEnabled: Flow<Boolean> =
        dataStore.data.map { it[KEY_LOCATION_USAGE_ENABLED] ?: false }

    override val showLiveOutputMetric: Flow<Boolean> =
        dataStore.data.map { it[KEY_SHOW_LIVE_OUTPUT_METRIC] ?: true }

    override val showCo2SavingsMetric: Flow<Boolean> =
        dataStore.data.map { it[KEY_SHOW_CO2_SAVINGS_METRIC] ?: true }

    override val showHouseholdsMetric: Flow<Boolean> =
        dataStore.data.map { it[KEY_SHOW_HOUSEHOLDS_METRIC] ?: true }

    override fun observeMemberSinceEpochDay(userId: String): Flow<Long?> =
        dataStore.data.map { it[memberSinceKey(userId)] }

    override suspend fun setOnboardingCompleted() {
        dataStore.edit { it[KEY_ONBOARDING_COMPLETED] = true }
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

    override suspend fun ensureMemberSinceEpochDay(userId: String, epochDay: Long) {
        val key = memberSinceKey(userId)
        dataStore.edit { preferences ->
            if (preferences[key] == null) {
                preferences[key] = epochDay
            }
        }
    }

    private fun memberSinceKey(userId: String): Preferences.Key<Long> =
        longPreferencesKey("member_since_epoch_day_${userId.toPreferenceKeySegment()}")

    private fun String.toPreferenceKeySegment(): String =
        map { char ->
            if (char.isLetterOrDigit() || char == '_' || char == '-') char else '_'
        }.joinToString(separator = "")

    companion object {
        private val KEY_ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        private val KEY_LOCATION_USAGE_ENABLED = booleanPreferencesKey("location_usage_enabled")
        private val KEY_SHOW_LIVE_OUTPUT_METRIC = booleanPreferencesKey("show_live_output_metric")
        private val KEY_SHOW_CO2_SAVINGS_METRIC = booleanPreferencesKey("show_co2_savings_metric")
        private val KEY_SHOW_HOUSEHOLDS_METRIC = booleanPreferencesKey("show_households_metric")
    }
}
