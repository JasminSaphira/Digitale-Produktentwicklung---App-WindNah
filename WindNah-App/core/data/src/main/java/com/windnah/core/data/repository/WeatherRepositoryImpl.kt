package com.windnah.core.data.repository

import com.windnah.core.data.mapper.toWeatherData
import com.windnah.core.domain.repository.WeatherRepository
import com.windnah.core.model.WeatherData
import com.windnah.core.network.dwd.DwdRemoteDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val dwd: DwdRemoteDataSource,
) : WeatherRepository {

    override suspend fun getCurrentWeather(lat: Double, lon: Double): WeatherData? =
        runCatching { dwd.getCurrentWeather(lat, lon)?.toWeatherData() }.getOrNull()
}
