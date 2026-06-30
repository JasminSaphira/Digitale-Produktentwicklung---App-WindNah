package com.windnah.feature.windparkdetail

import com.windnah.core.model.EnergyMetrics
import com.windnah.core.model.WeatherData
import com.windnah.core.model.WindFarm
import com.windnah.core.model.WindFarmDetail
import com.windnah.core.model.WindFarmStatus
import com.windnah.core.model.WindTurbine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WindFarmDetailTransparencyMapperTest {

    @Test
    fun `creates transparency info for every metric`() {
        val detail = sampleDetail()

        WindFarmDetailMetric.entries.forEach { metric ->
            val info = metric.toTransparencyInfoUiModel(detail)

            assertTrue(info.title.isNotBlank())
            assertTrue(info.value.isNotBlank())
            assertTrue(info.meaning.isNotBlank())
            assertTrue(info.calculation.isNotBlank())
            assertTrue(info.dataUsed.isNotBlank())
            assertFalse(info.sources.isEmpty())
        }
    }

    @Test
    fun `uses fallback text when wind data is missing`() {
        val detail = sampleDetail(weather = null)

        val info = WindFarmDetailMetric.WindSpeed.toTransparencyInfoUiModel(detail)

        assertEquals("Keine Live-Daten", info.value)
    }

    @Test
    fun `uses fallback text when noise cannot be estimated`() {
        val detail = sampleDetail(
            metrics = sampleMetrics().copy(estimatedNoiseLevelDbA = null),
        )

        val info = WindFarmDetailMetric.NoiseEstimate.toTransparencyInfoUiModel(detail)

        assertEquals("Nicht verfuegbar", info.value)
    }

    @Test
    fun `size comparison uses turbine dimensions`() {
        val info = WindFarmDetailMetric.SizeComparison.toTransparencyInfoUiModel(sampleDetail())

        assertEquals("ca. 175 m", info.value)
        assertTrue(info.sources.any { it.contains("MaStR") })
    }

    private fun sampleDetail(
        metrics: EnergyMetrics = sampleMetrics(),
        weather: WeatherData? = sampleWeather(),
    ): WindFarmDetail =
        WindFarmDetail(
            windFarm = WindFarm(
                id = "windpark-1",
                name = "Windpark Test",
                municipality = "Musterstadt",
                federalState = "Niedersachsen",
                latitude = 52.0,
                longitude = 9.0,
                status = WindFarmStatus.IN_BETRIEB,
                turbineCount = 2,
                totalCapacityKw = 10_000.0,
                commissioningYear = 2020,
            ),
            energyMetrics = metrics,
            turbines = listOf(
                WindTurbine(
                    id = "turbine-1",
                    windFarmId = "windpark-1",
                    manufacturer = "Vestas",
                    model = "V150",
                    ratedPowerKw = 5_000.0,
                    rotorDiameterM = 150.0,
                    hubHeightM = 100.0,
                    commissioningYear = 2020,
                    status = WindFarmStatus.IN_BETRIEB,
                    operator = "WindNah",
                ),
            ),
            weather = weather,
        )

    private fun sampleMetrics(): EnergyMetrics =
        EnergyMetrics(
            estimatedCurrentOutputKw = 4_200.0,
            estimatedAnnualProductionKwh = 20_000_000.0,
            householdsSupplied = 5_714,
            co2SavingsTonnesPerYear = 7_080.0,
            localEnergyContributionPercent = 32.4,
            municipalRevenueEurPerYear = 40_000.0,
            estimatedNoiseLevelDbA = 43.2,
        )

    private fun sampleWeather(): WeatherData =
        WeatherData(
            windSpeedMs = 6.3,
            windDirectionDeg = 240,
            temperatureCelsius = 12.0,
            timestamp = 1_700_000_000L,
            stationId = "station-1",
        )
}
