package com.windnah.core.domain.usecase

import com.windnah.core.domain.repository.WeatherRepository
import com.windnah.core.domain.repository.WindFarmRepository
import com.windnah.core.model.EnergyMetrics
import com.windnah.core.model.WindFarmDetail
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetWindFarmDetailUseCase @Inject constructor(
    private val windFarmRepository: WindFarmRepository,
    private val weatherRepository: WeatherRepository,
    private val calculateCurrentOutputUseCase: CalculateCurrentOutputUseCase,
    private val calculateAnnualProductionUseCase: CalculateAnnualProductionUseCase,
    private val calculateHouseholdsSuppliedUseCase: CalculateHouseholdsSuppliedUseCase,
    private val calculateCo2SavingsUseCase: CalculateCo2SavingsUseCase,
    private val calculateLocalEnergyContributionUseCase: CalculateLocalEnergyContributionUseCase,
    private val calculateMunicipalRevenueUseCase: CalculateMunicipalRevenueUseCase,
    private val calculateNoiseEstimateUseCase: CalculateNoiseEstimateUseCase,
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
                val annualProductionKwh = calculateAnnualProductionUseCase(detail.windFarm.totalCapacityKw)
                val currentOutputKw = weather?.let {
                    calculateCurrentOutputUseCase(it, detail.turbines, detail.windFarm.totalCapacityKw)
                } ?: detail.energyMetrics.estimatedCurrentOutputKw
                val noiseEstimateDbA = weather?.let {
                    calculateNoiseEstimateUseCase(
                        weather = it,
                        turbines = detail.turbines,
                        installedCapacityKw = detail.windFarm.totalCapacityKw,
                        currentOutputKw = currentOutputKw,
                    )
                }

                detail.copy(
                    energyMetrics = detail.energyMetrics.copy(
                        estimatedCurrentOutputKw = currentOutputKw,
                        estimatedAnnualProductionKwh = annualProductionKwh,
                        householdsSupplied = calculateHouseholdsSuppliedUseCase(annualProductionKwh),
                        co2SavingsTonnesPerYear = calculateCo2SavingsUseCase(annualProductionKwh),
                        localEnergyContributionPercent = calculateLocalEnergyContributionUseCase(detail.windFarm),
                        municipalRevenueEurPerYear = calculateMunicipalRevenueUseCase(annualProductionKwh),
                        estimatedNoiseLevelDbA = noiseEstimateDbA,
                    ),
                    weather = weather,
                )
            }
        }
}
