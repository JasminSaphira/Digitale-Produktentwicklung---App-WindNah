package com.windnah.core.domain.usecase

import com.windnah.core.domain.repository.WeatherRepository
import com.windnah.core.domain.repository.WindFarmRepository
import com.windnah.core.model.WeatherData
import com.windnah.core.model.WindFarmDetail
import com.windnah.core.model.WindTurbine
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val HELLMANN_EXPONENT = 0.14
private const val MEASUREMENT_HEIGHT_M = 10.0
private const val CUT_IN_MS = 3.0
private const val RATED_MS = 12.0
private const val CUT_OUT_MS = 25.0
private const val WAKE_EFFICIENCY = 0.85
private const val DEFAULT_HUB_HEIGHT_M = 100.0

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
                val currentOutputKw = weather?.let {
                    calculateCurrentOutput(it, detail.turbines, detail.windFarm.totalCapacityKw)
                } ?: detail.energyMetrics.estimatedCurrentOutputKw

                detail.copy(
                    energyMetrics = detail.energyMetrics.copy(
                        estimatedCurrentOutputKw = currentOutputKw,
                    ),
                    weather = weather,
                )
            }
        }

    private fun calculateCurrentOutput(
        weather: WeatherData,
        turbines: List<WindTurbine>,
        totalCapacityKw: Double,
    ): Double {
        if (turbines.isEmpty()) {
            return estimateFromCapacity(weather.windSpeedMs, totalCapacityKw, DEFAULT_HUB_HEIGHT_M)
        }
        return turbines
            .filter { it.status.name == "IN_BETRIEB" }
            .sumOf { turbine ->
                val hubHeight = turbine.hubHeightM ?: DEFAULT_HUB_HEIGHT_M
                turbineOutputKw(weather.windSpeedMs, hubHeight, turbine.ratedPowerKw)
            }
            .times(WAKE_EFFICIENCY)
    }

    private fun estimateFromCapacity(windSpeedMs: Double, capacityKw: Double, hubHeightM: Double): Double {
        val vHub = windAtHubHeight(windSpeedMs, hubHeightM)
        val ratio = when {
            vHub < CUT_IN_MS -> 0.0
            vHub <= RATED_MS -> ((vHub - CUT_IN_MS) / (RATED_MS - CUT_IN_MS)).pow3()
            vHub <= CUT_OUT_MS -> 1.0
            else -> 0.0
        }
        return capacityKw * ratio * WAKE_EFFICIENCY
    }

    private fun turbineOutputKw(windSpeedMs: Double, hubHeightM: Double, ratedPowerKw: Double): Double {
        val vHub = windAtHubHeight(windSpeedMs, hubHeightM)
        val ratio = when {
            vHub < CUT_IN_MS -> 0.0
            vHub <= RATED_MS -> ((vHub - CUT_IN_MS) / (RATED_MS - CUT_IN_MS)).pow3()
            vHub <= CUT_OUT_MS -> 1.0
            else -> 0.0
        }
        return ratedPowerKw * ratio
    }

    private fun windAtHubHeight(v10m: Double, hubHeightM: Double): Double =
        v10m * Math.pow(hubHeightM / MEASUREMENT_HEIGHT_M, HELLMANN_EXPONENT)

    private fun Double.pow3(): Double = this * this * this
}
