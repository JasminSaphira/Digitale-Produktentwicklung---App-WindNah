package com.windnah.core.domain.usecase

import com.windnah.core.domain.repository.WeatherRepository
import com.windnah.core.domain.repository.WindFarmRepository
import com.windnah.core.model.EnergyMetrics
import com.windnah.core.model.WeatherData
import com.windnah.core.model.WindFarm
import com.windnah.core.model.WindFarmDetail
import com.windnah.core.model.WindFarmPreview
import com.windnah.core.model.WindFarmStatus
import com.windnah.core.model.WindTurbine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GetWindFarmDetailUseCaseTest {

    @Test
    fun `detail use case enriches metrics with weather driven calculations`() = runBlocking {
        val windFarm = WindFarm(
            id = "windfarm-test",
            name = "Testpark",
            municipality = "Taucha",
            federalState = "Sachsen",
            latitude = 51.0,
            longitude = 12.0,
            status = WindFarmStatus.IN_BETRIEB,
            turbineCount = 2,
            totalCapacityKw = 4_000.0,
            commissioningYear = 2020,
        )
        val turbines = listOf(
            WindTurbine(
                id = "t-1",
                windFarmId = windFarm.id,
                manufacturer = "Enercon",
                model = "E-126",
                ratedPowerKw = 2_000.0,
                rotorDiameterM = 126.0,
                hubHeightM = 100.0,
                commissioningYear = 2020,
                status = WindFarmStatus.IN_BETRIEB,
                operator = null,
            ),
            WindTurbine(
                id = "t-2",
                windFarmId = windFarm.id,
                manufacturer = "Enercon",
                model = "E-126",
                ratedPowerKw = 2_000.0,
                rotorDiameterM = 126.0,
                hubHeightM = 100.0,
                commissioningYear = 2020,
                status = WindFarmStatus.IN_BETRIEB,
                operator = null,
            ),
        )
        val preview = WindFarmPreview(
            windFarm = windFarm,
            energyMetrics = EnergyMetrics(
                estimatedCurrentOutputKw = 0.0,
                estimatedAnnualProductionKwh = 8_000_000.0,
                householdsSupplied = 2_285,
                co2SavingsTonnesPerYear = 2_832.0,
                localEnergyContributionPercent = null,
                municipalRevenueEurPerYear = null,
            ),
        )

        val useCase = GetWindFarmDetailUseCase(
            windFarmRepository = object : WindFarmRepository {
                override fun observeWindFarmPreviews(): Flow<List<WindFarmPreview>> = flowOf(listOf(preview))
                override fun observeWindFarmDetail(windFarmId: String): Flow<WindFarmDetail?> =
                    flowOf(WindFarmDetail(windFarm = windFarm, energyMetrics = preview.energyMetrics, turbines = turbines))
            },
            weatherRepository = object : WeatherRepository {
                override suspend fun getCurrentWeather(lat: Double, lon: Double): WeatherData? =
                    WeatherData(
                        windSpeedMs = 8.0,
                        windDirectionDeg = 250,
                        temperatureCelsius = 13.0,
                        timestamp = 1L,
                        stationId = "station",
                    )
            },
            calculateCurrentOutputUseCase = CalculateCurrentOutputUseCase(),
            calculateAnnualProductionUseCase = CalculateAnnualProductionUseCase(),
            calculateHouseholdsSuppliedUseCase = CalculateHouseholdsSuppliedUseCase(),
            calculateCo2SavingsUseCase = CalculateCo2SavingsUseCase(),
            calculateLocalEnergyContributionUseCase = CalculateLocalEnergyContributionUseCase(),
            calculateMunicipalRevenueUseCase = CalculateMunicipalRevenueUseCase(),
            calculateNoiseEstimateUseCase = CalculateNoiseEstimateUseCase(),
        )

        val detail = requireNotNull(useCase(windFarm.id).first())

        assertEquals(8_000_000.0, detail.energyMetrics.estimatedAnnualProductionKwh, 0.0)
        assertTrue(detail.energyMetrics.estimatedCurrentOutputKw > 0.0)
        assertNotNull(detail.energyMetrics.estimatedNoiseLevelDbA)
        assertEquals(2_832.0, detail.energyMetrics.co2SavingsTonnesPerYear, 0.0)
    }
}
