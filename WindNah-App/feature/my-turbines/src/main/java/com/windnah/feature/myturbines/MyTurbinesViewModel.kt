package com.windnah.feature.myturbines

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.windnah.core.domain.repository.FavoriteRepository
import com.windnah.core.domain.repository.RecentlyViewedRepository
import com.windnah.core.domain.repository.WindFarmRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.concurrent.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val MY_TURBINES_ERROR_MESSAGE = "Windparks können aktuell nicht geladen werden."

@HiltViewModel
class MyTurbinesViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
    private val recentlyViewedRepository: RecentlyViewedRepository,
    private val windFarmRepository: WindFarmRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyTurbinesUiState(isLoading = true))
    val uiState: StateFlow<MyTurbinesUiState> = _uiState.asStateFlow()

    private var observeJob: Job? = null

    init {
        observeUiState()
    }

    fun onEvent(event: MyTurbinesUiEvent) {
        when (event) {
            is MyTurbinesUiEvent.RemoveFavoriteClicked -> removeFavorite(event.windFarmId)
            MyTurbinesUiEvent.RetryClicked -> observeUiState()
            MyTurbinesUiEvent.DiscoverClicked,
            MyTurbinesUiEvent.BackClicked,
            is MyTurbinesUiEvent.WindFarmClicked -> Unit
        }
    }

    private fun observeUiState() {
        observeJob?.cancel()
        _uiState.update { state ->
            state.copy(
                isLoading = true,
                errorMessage = null,
            )
        }

        observeJob = viewModelScope.launch {
            try {
                combine(
                    favoriteRepository.observeFavoriteIds(),
                    recentlyViewedRepository.observeRecentlyViewedIds(limit = 10),
                    windFarmRepository.observeWindFarmPreviews(),
                ) { favoriteIds, recentlyViewedIds, previewsResult ->
                    mapMyTurbinesUiState(
                        favoriteIds = favoriteIds,
                        recentlyViewedIds = recentlyViewedIds,
                        previews = previewsResult.previews,
                    )
                }.collect { state ->
                    _uiState.value = state.copy(
                        isLoading = false,
                        errorMessage = null,
                    )
                }
            } catch (error: Throwable) {
                if (error is CancellationException) throw error
                _uiState.value = MyTurbinesUiState(
                    isLoading = false,
                    errorMessage = MY_TURBINES_ERROR_MESSAGE,
                )
            }
        }
    }

    private fun removeFavorite(windFarmId: String) {
        viewModelScope.launch {
            favoriteRepository.removeFavorite(windFarmId)
        }
    }
}
