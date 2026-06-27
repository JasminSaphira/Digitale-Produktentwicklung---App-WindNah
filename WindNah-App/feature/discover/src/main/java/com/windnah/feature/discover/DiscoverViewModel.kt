package com.windnah.feature.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.windnah.core.domain.repository.UserPreferencesRepository
import com.windnah.core.domain.usecase.GetDiscoverWindFarmsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val LOCATION_UNAVAILABLE_MESSAGE = "Standort aktuell nicht verfuegbar."
private const val WIND_FARMS_UNAVAILABLE_MESSAGE = "Windparks koennen aktuell nicht geladen werden."
private const val SEARCH_DEBOUNCE_MS = 300L

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val getDiscoverWindFarmsUseCase: GetDiscoverWindFarmsUseCase,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DiscoverUiState())
    val uiState: StateFlow<DiscoverUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        _uiState
            .map { it.searchQuery }
            .distinctUntilChanged()
            .drop(1) // skip the initial empty query — the explicit loadWindFarms() below handles startup
            .debounce(SEARCH_DEBOUNCE_MS)
            .onEach { loadWindFarms() }
            .launchIn(viewModelScope)

        userPreferencesRepository.isLocationUsageEnabled
            .onEach { enabled ->
                _uiState.update { state ->
                    state.copy(
                        isLocationUsageEnabled = enabled,
                        isResolvingCurrentLocation = if (enabled) state.isResolvingCurrentLocation else false,
                    )
                }
            }
            .launchIn(viewModelScope)

        loadWindFarms()
    }

    fun onEvent(event: DiscoverUiEvent) {
        when (event) {
            is DiscoverUiEvent.SearchQueryChanged -> {
                _uiState.update { it.copy(searchQuery = event.query) }
                // loadWindFarms() is triggered via the debounced searchQuery flow in init
            }

            is DiscoverUiEvent.StatusFilterToggled -> {
                _uiState.update { state ->
                    val current = state.selectedStatuses
                    state.copy(
                        selectedStatuses = if (event.status in current) {
                            current - event.status
                        } else {
                            current + event.status
                        },
                    )
                }
                loadWindFarms()
            }

            is DiscoverUiEvent.FederalStateFilterSelected -> {
                _uiState.update { state ->
                    val nextToken = (state.mapRecenterRequest?.requestToken ?: 0) + 1
                    val recenter = if (event.federalState != null) {
                        FederalStateCenters[event.federalState]?.let { center ->
                            MapRecenterRequest(center.latitude, center.longitude, center.zoom, nextToken)
                        }
                    } else {
                        MapRecenterRequest(GermanyCenterLat, GermanyCenterLon, GermanyDefaultZoom, nextToken)
                    }
                    state.copy(
                        selectedFederalState = event.federalState,
                        mapRecenterRequest = recenter ?: state.mapRecenterRequest,
                    )
                }
                loadWindFarms()
            }

            is DiscoverUiEvent.WindFarmSelected -> {
                _uiState.update { state ->
                    state.copy(selectedWindFarm = state.windFarms.firstOrNull { it.windFarm.id == event.windFarmId })
                }
            }

            is DiscoverUiEvent.LocationPermissionUpdated -> {
                if (!event.granted) {
                    viewModelScope.launch {
                        userPreferencesRepository.setLocationUsageEnabled(false)
                    }
                }
                _uiState.update { state ->
                    state.copy(
                        hasLocationPermission = event.granted,
                        isResolvingCurrentLocation = if (event.granted) state.isResolvingCurrentLocation else false,
                    )
                }
            }

            DiscoverUiEvent.RecenterRequested -> {
                _uiState.update { state ->
                    if (!state.hasLocationPermission || !state.isLocationUsageEnabled) {
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
                        selectedStatuses = emptySet(),
                        selectedFederalState = null,
                    )
                }
                loadWindFarms()
            }

            DiscoverUiEvent.WindFarmSelectionCleared -> {
                _uiState.update { it.copy(selectedWindFarm = null) }
            }

            DiscoverUiEvent.RetryClicked -> {
                loadWindFarms()
            }
        }
    }

    fun setLocationUsageEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setLocationUsageEnabled(enabled)
        }
    }

    fun enableLocationUsageAndRecenter() {
        _uiState.update { it.copy(isLocationUsageEnabled = true) }
        viewModelScope.launch {
            userPreferencesRepository.setLocationUsageEnabled(true)
        }
        onEvent(DiscoverUiEvent.RecenterRequested)
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
                selectedStatuses = state.selectedStatuses,
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
