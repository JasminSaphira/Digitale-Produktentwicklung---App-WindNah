package com.windnah.core.network.dwd

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BrightSkyCurrentWeatherResponse(
    @SerialName("weather") val weather: BrightSkyWeatherDto? = null,
    @SerialName("sources") val sources: List<BrightSkySourceDto> = emptyList(),
)

@Serializable
data class BrightSkyWeatherDto(
    // BrightSky exposes wind as 10/30/60-minute means; *_10 is the most recent.
    // With units=si these are in m/s and degrees.
    @SerialName("wind_speed_10") val windSpeedMs: Double? = null,
    @SerialName("wind_direction_10") val windDirectionDeg: Double? = null,
    @SerialName("condition") val condition: String? = null,
    @SerialName("timestamp") val timestamp: String? = null,
)

@Serializable
data class BrightSkySourceDto(
    @SerialName("station_name") val stationName: String? = null,
    @SerialName("distance") val distanceM: Double? = null,
)
