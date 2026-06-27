package com.windnah.feature.myturbines

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.windnah.core.domain.repository.FavoriteRepository
import com.windnah.core.domain.repository.RecentlyViewedRepository
import com.windnah.core.domain.repository.WindFarmRepository
import com.windnah.core.model.WindFarmPreview
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MyTurbinesUiState(
    val favorites: List<WindFarmPreview> = emptyList(),
    val recentlyViewed: List<WindFarmPreview> = emptyList(),
)

@HiltViewModel
class MyTurbinesViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
    recentlyViewedRepository: RecentlyViewedRepository,
    windFarmRepository: WindFarmRepository,
) : ViewModel() {

    val uiState: StateFlow<MyTurbinesUiState> =
        combine(
            favoriteRepository.observeFavoriteIds(),
            recentlyViewedRepository.observeRecentlyViewedIds(limit = 10),
            windFarmRepository.observeWindFarmPreviews(),
        ) { favoriteIds, recentlyViewedIds, previewsResult ->
            val windFarmsById = previewsResult.previews.associateBy { it.windFarm.id }
            MyTurbinesUiState(
                favorites = favoriteIds.mapNotNull { windFarmsById[it] },
                recentlyViewed = recentlyViewedIds.mapNotNull { windFarmsById[it] },
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = MyTurbinesUiState(),
            )

    fun removeFavorite(windFarmId: String) {
        viewModelScope.launch {
            favoriteRepository.removeFavorite(windFarmId)
        }
    }
}
