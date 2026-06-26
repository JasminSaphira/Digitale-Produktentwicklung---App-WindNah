package com.windnah.feature.discover

import com.windnah.core.model.WindFarmPreview
import com.windnah.core.model.WindFarmStatus

data class DiscoverUiState(
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val selectedStatuses: Set<WindFarmStatus> = emptySet(),
    val selectedFederalState: String? = null,
    val windFarms: List<WindFarmPreview> = emptyList(),
    val selectedWindFarm: WindFarmPreview? = null,
    val statusFilters: List<WindFarmStatus> = WindFarmStatus.entries,
    val federalStateFilters: List<String> = GermanFederalStates,
    val errorMessage: String? = null,
    val hasLocationPermission: Boolean = false,
    val isLocationUsageEnabled: Boolean = false,
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

data class FederalStateCenter(val latitude: Double, val longitude: Double, val zoom: Double)

val FederalStateCenters: Map<String, FederalStateCenter> = mapOf(
    "Baden-Wuerttemberg" to FederalStateCenter(48.6616, 9.3501, 7.5),
    "Bayern" to FederalStateCenter(48.9496, 11.3952, 7.2),
    "Berlin" to FederalStateCenter(52.5200, 13.4050, 11.0),
    "Brandenburg" to FederalStateCenter(52.4127, 12.5316, 7.5),
    "Bremen" to FederalStateCenter(53.0793, 8.8017, 12.0),
    "Hamburg" to FederalStateCenter(53.5753, 10.0153, 12.0),
    "Hessen" to FederalStateCenter(50.6521, 9.1624, 8.0),
    "Mecklenburg-Vorpommern" to FederalStateCenter(53.6127, 12.4296, 7.5),
    "Niedersachsen" to FederalStateCenter(52.6367, 9.8451, 7.5),
    "Nordrhein-Westfalen" to FederalStateCenter(51.4332, 7.6616, 8.0),
    "Rheinland-Pfalz" to FederalStateCenter(50.1183, 7.3080, 8.0),
    "Saarland" to FederalStateCenter(49.3964, 7.0228, 9.0),
    "Sachsen" to FederalStateCenter(51.1045, 13.2017, 8.5),
    "Sachsen-Anhalt" to FederalStateCenter(51.9503, 11.6923, 8.0),
    "Schleswig-Holstein" to FederalStateCenter(54.2194, 9.6961, 8.0),
    "Thueringen" to FederalStateCenter(50.8987, 11.0299, 8.0),
)

const val GermanyCenterLat = 51.1657
const val GermanyCenterLon = 10.4515
const val GermanyDefaultZoom = 6.0

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
