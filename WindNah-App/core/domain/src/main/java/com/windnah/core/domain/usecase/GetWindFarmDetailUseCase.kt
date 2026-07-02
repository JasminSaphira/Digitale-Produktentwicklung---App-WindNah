package com.windnah.core.domain.usecase

import com.windnah.core.domain.repository.WeatherRepository
import com.windnah.core.domain.repository.WindFarmRepository
import com.windnah.core.model.WindFarmDetail
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetWindFarmDetailUseCase @Inject constructor(
    private val windFarmRepository: WindFarmRepository,
    private val weatherRepository: WeatherRepository,
) {
    operator fun invoke(windFarmId: String): Flow<WindFarmDetail?> =
        windFarmRepository.observeWindFarmDetail(windFarmId).map { detail ->
            detail ?: return@map null
            coroutineScope {
                val weatherDeferred = async {
                    runCatching {
                        weatherRepository.getCurrentWeather(
                            lat = detail.windFarm.latitude,
                            lon = detail.windFarm.longitude,
                        )
                    }.getOrNull()
                }
                val weather = weatherDeferred.await()

                detail.copy(
                    energyMetrics = WindFarmMetricCalculator.buildEnergyMetrics(
                        windFarm = detail.windFarm,
                        turbines = detail.turbines,
                        weather = weather,
                    ),
                    weather = weather,
                )
            }
        }
}
