package com.windnah.core.domain.repository

import com.windnah.core.model.WeatherData

interface WeatherRepository {
    suspend fun getCurrentWeather(lat: Double, lon: Double): WeatherData?
}
