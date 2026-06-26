package com.windnah.core.domain.usecase

import com.windnah.core.model.WeatherData
import com.windnah.core.model.WindFarm
import com.windnah.core.model.WindFarmStatus
import com.windnah.core.model.WindTurbine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class WindFarmMetricCalculatorTest {

    @Test
    fun `annual production households co2 and revenue follow the documented formulas`() {
        val annualProduction = WindFarmMetricCalculator.calculateAnnualProductionKwh(42_000.0)
        val households = WindFarmMetricCalculator.calculateHouseholdsSupplied(annualProduction)
        val co2Savings = WindFarmMetricCalculator.calculateCo2SavingsTonnes(annualProduction)
        val municipalRevenue = WindFarmMetricCalculator.calculateMunicipalRevenueEur(annualProduction)

        assertEquals(84_000_000.0, annualProduction, 0.0)
        assertEquals(24_000, households)
        assertEquals(29_736.0, co2Savings, 0.0)
        assertEquals(168_000.0, municipalRevenue, 0.0)
    }

    @Test
    fun `local energy contribution uses the deterministic estimate model`() {
        val windFarm = WindFarm(
            id = "windfarm-brandenburg-demo",
            name = "Demo",
            municipality = "Prenzlau",
            federalState = "Brandenburg",
            latitude = 0.0,
            longitude = 0.0,
            status = WindFarmStatus.IN_BETRIEB,
            turbineCount = 14,
            totalCapacityKw = 42_000.0,
            commissioningYear = 2019,
        )

        val contribution = WindFarmMetricCalculator.calculateLocalEnergyContributionPercent(windFarm)

        assertEquals(30.936227951, contribution, 0.000001)
    }

    @Test
    fun `current output and noise estimates stay within expected bounds`() {
        val weather = WeatherData(
            windSpeedMs = 8.0,
            windDirectionDeg = 270,
            temperatureCelsius = null,
            timestamp = 0L,
            stationId = "test",
        )
        val turbines = listOf(
            WindTurbine(
                id = "t-1",
                windFarmId = "windfarm-test",
                manufacturer = "Enercon",
                model = "E-126",
                ratedPowerKw = 1_000.0,
                rotorDiameterM = 126.0,
                hubHeightM = 100.0,
                commissioningYear = 2020,
                status = WindFarmStatus.IN_BETRIEB,
                operator = null,
            ),
        )

        val currentOutput = WindFarmMetricCalculator.calculateCurrentOutput(
            weather = weather,
            turbines = turbines,
            installedCapacityKw = 1_000.0,
        )
        val noise = WindFarmMetricCalculator.calculateNoiseEstimateDbA(
            weather = weather,
            turbines = turbines,
            installedCapacityKw = 1_000.0,
            currentOutputKw = currentOutput,
        )

        assertEquals(606.677110479, currentOutput, 0.000001)
        assertNotNull(noise)
        assertTrue(noise!! in 40.0..70.0)
    }
}
