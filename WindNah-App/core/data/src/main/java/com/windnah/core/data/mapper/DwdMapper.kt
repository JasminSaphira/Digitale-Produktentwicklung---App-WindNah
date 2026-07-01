package com.windnah.core.data.mapper

import com.windnah.core.model.WeatherData
import com.windnah.core.network.dwd.BrightSkyWeatherDto

fun BrightSkyWeatherDto.toWeatherData(): WeatherData? {
    val windSpeed = windSpeedMs ?: return null

    return WeatherData(
        windSpeedMs = windSpeed,
        windDirectionDeg = windDirectionDeg?.toInt() ?: 0,
        temperatureCelsius = null,
        timestamp = System.currentTimeMillis(),
        stationId = "brightsky",
    )
}
