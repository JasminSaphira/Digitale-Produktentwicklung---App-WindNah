package com.windnah.feature.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.windnah.core.domain.usecase.GetDiscoverWindFarmsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val LOCATION_UNAVAILABLE_MESSAGE = "Standort aktuell nicht verfuegbar."
private const val WIND_FARMS_UNAVAILABLE_MESSAGE = "Windparks koennen aktuell nicht geladen werden."

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val getDiscoverWindFarmsUseCase: GetDiscoverWindFarmsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DiscoverUiState())
    val uiState: StateFlow<DiscoverUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadWindFarms()
    }

    fun onEvent(event: DiscoverUiEvent) {
        when (event) {
            is DiscoverUiEvent.SearchQueryChanged -> {
                _uiState.update { it.copy(searchQuery = event.query) }
                loadWindFarms()
            }

            is DiscoverUiEvent.StatusFilterSelected -> {
                _uiState.update { state ->
                    state.copy(
                        selectedStatus = if (state.selectedStatus == event.status) null else event.status,
                    )
                }
                loadWindFarms()
            }

            is DiscoverUiEvent.FederalStateFilterSelected -> {
                _uiState.update { it.copy(selectedFederalState = event.federalState) }
                loadWindFarms()
            }

            is DiscoverUiEvent.WindFarmSelected -> {
                _uiState.update { state ->
                    state.copy(selectedWindFarm = state.windFarms.firstOrNull { it.windFarm.id == event.windFarmId })
                }
            }

            is DiscoverUiEvent.LocationPermissionUpdated -> {
                _uiState.update { state ->
                    state.copy(
                        hasLocationPermission = event.granted,
                        isResolvingCurrentLocation = if (event.granted) state.isResolvingCurrentLocation else false,
                    )
                }
            }

            DiscoverUiEvent.RecenterRequested -> {
                _uiState.update { state ->
                    if (!state.hasLocationPermission) {
                        state
                    } else {
                        state.copy(
                            isResolvingCurrentLocation = true,
                            pendingLocationRequestToken = state.pendingLocationRequestToken + 1,
                            errorMessage = state.errorMessage
                                ?.takeUnless { it == LOCATION_UNAVAILABLE_MESSAGE },
                        )
                    }
                }
            }

            is DiscoverUiEvent.CurrentLocationResolved -> {
                _uiState.update { state ->
                    state.copy(
                        isResolvingCurrentLocation = false,
                        mapRecenterRequest = MapRecenterRequest(
                            latitude = event.latitude,
                            longitude = event.longitude,
                            zoom = 10.5,
                            requestToken = state.pendingLocationRequestToken,
                        ),
                        errorMessage = state.errorMessage
                            ?.takeUnless { it == LOCATION_UNAVAILABLE_MESSAGE },
                    )
                }
            }

            DiscoverUiEvent.CurrentLocationUnavailable -> {
                _uiState.update { state ->
                    state.copy(
                        isResolvingCurrentLocation = false,
                        errorMessage = LOCATION_UNAVAILABLE_MESSAGE,
                    )
                }
            }

            DiscoverUiEvent.ClearFiltersClicked -> {
                _uiState.update { state ->
                    state.copy(
                        searchQuery = "",
                        selectedStatus = null,
                        selectedFederalState = null,
                    )
                }
                loadWindFarms()
            }

            DiscoverUiEvent.WindFarmSelectionCleared -> {
                _uiState.update { it.copy(selectedWindFarm = null) }
            }
        }
    }

    private fun loadWindFarms() {
        loadJob?.cancel()
        val state = _uiState.value
        _uiState.update { currentState ->
            currentState.copy(
                isLoading = true,
                errorMessage = currentState.errorMessage
                    ?.takeIf { it == LOCATION_UNAVAILABLE_MESSAGE },
            )
        }
        loadJob = viewModelScope.launch {
            getDiscoverWindFarmsUseCase(
                searchQuery = state.searchQuery,
                selectedStatus = state.selectedStatus,
                selectedFederalState = state.selectedFederalState,
            )
                .catch {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            errorMessage = WIND_FARMS_UNAVAILABLE_MESSAGE,
                        )
                    }
                }
                .collect { windFarms ->
                    _uiState.update { currentState ->
                        val selectedWindFarm = currentState.selectedWindFarm
                            ?.takeIf { selected -> windFarms.any { it.windFarm.id == selected.windFarm.id } }

                        currentState.copy(
                            isLoading = false,
                            windFarms = windFarms,
                            selectedWindFarm = selectedWindFarm,
                            errorMessage = currentState.errorMessage
                                ?.takeIf { it == LOCATION_UNAVAILABLE_MESSAGE },
                        )
                    }
                }
        }
    }
}
