package com.windnah.feature.discover

import com.windnah.core.model.WindFarmPreview
import com.windnah.core.model.WindFarmStatus

data class DiscoverUiState(
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val selectedStatus: WindFarmStatus? = null,
    val selectedFederalState: String? = null,
    val windFarms: List<WindFarmPreview> = emptyList(),
    val selectedWindFarm: WindFarmPreview? = null,
    val statusFilters: List<WindFarmStatus> = WindFarmStatus.entries,
    val federalStateFilters: List<String> = GermanFederalStates,
    val errorMessage: String? = null,
    val hasLocationPermission: Boolean = false,
    val isResolvingCurrentLocation: Boolean = false,
    val pendingLocationRequestToken: Int = 0,
    val mapRecenterRequest: MapRecenterRequest? = null,
)

data class MapRecenterRequest(
    val latitude: Double,
    val longitude: Double,
    val zoom: Double,
    val requestToken: Int,
)

val GermanFederalStates = listOf(
    "Baden-Wuerttemberg",
    "Bayern",
    "Berlin",
    "Brandenburg",
    "Bremen",
    "Hamburg",
    "Hessen",
    "Mecklenburg-Vorpommern",
    "Niedersachsen",
    "Nordrhein-Westfalen",
    "Rheinland-Pfalz",
    "Saarland",
    "Sachsen",
    "Sachsen-Anhalt",
    "Schleswig-Holstein",
    "Thueringen",
)
