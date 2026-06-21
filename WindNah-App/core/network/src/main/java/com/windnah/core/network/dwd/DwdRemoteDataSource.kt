package com.windnah.core.network.dwd

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DwdRemoteDataSource @Inject constructor(
    private val api: DwdApiService,
) {
    suspend fun getCurrentWeather(lat: Double, lon: Double): BrightSkyWeatherDto? =
        api.getCurrentWeather(lat, lon).weather
}
