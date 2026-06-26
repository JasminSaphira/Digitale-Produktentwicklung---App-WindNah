package com.windnah.feature.discover

import com.windnah.core.model.WindFarmStatus

sealed interface DiscoverUiEvent {
    data class SearchQueryChanged(val query: String) : DiscoverUiEvent
    data class StatusFilterToggled(val status: WindFarmStatus) : DiscoverUiEvent
    data class FederalStateFilterSelected(val federalState: String?) : DiscoverUiEvent
    data class WindFarmSelected(val windFarmId: String) : DiscoverUiEvent
    data class LocationPermissionUpdated(val granted: Boolean) : DiscoverUiEvent
    data object RecenterRequested : DiscoverUiEvent
    data class CurrentLocationResolved(val latitude: Double, val longitude: Double) : DiscoverUiEvent
    data object CurrentLocationUnavailable : DiscoverUiEvent
    data object ClearFiltersClicked : DiscoverUiEvent
    data object WindFarmSelectionCleared : DiscoverUiEvent
    data object RetryClicked : DiscoverUiEvent
}
