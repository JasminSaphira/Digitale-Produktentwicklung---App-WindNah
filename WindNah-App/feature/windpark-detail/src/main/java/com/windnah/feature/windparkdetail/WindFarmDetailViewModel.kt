package com.windnah.feature.windparkdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.windnah.core.domain.repository.FavoriteRepository
import com.windnah.core.domain.repository.RecentlyViewedRepository
import com.windnah.core.domain.repository.UserPreferencesRepository
import com.windnah.core.domain.usecase.GetWindFarmDetailUseCase
import com.windnah.core.model.WindFarmDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface WindFarmDetailUiState {
    data object Loading : WindFarmDetailUiState
    data class Success(
        val detail: WindFarmDetail,
        val metricVisibility: MetricVisibilityPreferences,
        val isFavorite: Boolean,
    ) : WindFarmDetailUiState
    data object NotFound : WindFarmDetailUiState
}

data class MetricVisibilityPreferences(
    val showLiveOutput: Boolean = true,
    val showCo2Savings: Boolean = true,
    val showHouseholds: Boolean = true,
)

@HiltViewModel
class WindFarmDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getWindFarmDetail: GetWindFarmDetailUseCase,
    userPreferencesRepository: UserPreferencesRepository,
    private val favoriteRepository: FavoriteRepository,
    private val recentlyViewedRepository: RecentlyViewedRepository,
) : ViewModel() {

    private val windFarmId: String = checkNotNull(savedStateHandle["windFarmId"])

    init {
        viewModelScope.launch {
            recentlyViewedRepository.recordViewed(windFarmId)
        }
    }

    private val metricVisibility =
        combine(
            userPreferencesRepository.showLiveOutputMetric,
            userPreferencesRepository.showCo2SavingsMetric,
            userPreferencesRepository.showHouseholdsMetric,
        ) { showLiveOutput, showCo2Savings, showHouseholds ->
            MetricVisibilityPreferences(
                showLiveOutput = showLiveOutput,
                showCo2Savings = showCo2Savings,
                showHouseholds = showHouseholds,
            )
        }

    val uiState: StateFlow<WindFarmDetailUiState> =
        combine(
            getWindFarmDetail(windFarmId),
            metricVisibility,
            favoriteRepository.observeIsFavorite(windFarmId),
        ) { detail, metricVisibility, isFavorite ->
            if (detail == null) WindFarmDetailUiState.NotFound
            else WindFarmDetailUiState.Success(
                detail = detail,
                metricVisibility = metricVisibility,
                isFavorite = isFavorite,
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = WindFarmDetailUiState.Loading,
            )

    fun toggleFavorite() {
        viewModelScope.launch {
            favoriteRepository.toggleFavorite(windFarmId)
        }
    }
}
