package com.windnah.core.model

data class WeatherData(
    val windSpeedMs: Double,
    val windDirectionDeg: Int,
    val temperatureCelsius: Double?,
    val timestamp: Long,
    val stationId: String,
)
