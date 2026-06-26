package com.windnah.core.data.repository

import com.windnah.core.domain.repository.WindFarmRepository
import com.windnah.core.domain.usecase.WindFarmMetricCalculator
import com.windnah.core.model.EnergyMetrics
import com.windnah.core.model.WindFarm
import com.windnah.core.model.WindFarmDetail
import com.windnah.core.model.WindFarmPreview
import com.windnah.core.model.WindFarmStatus
import com.windnah.core.model.WindTurbine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeWindFarmRepository @Inject constructor() : WindFarmRepository {

    override fun observeWindFarmPreviews(): Flow<List<WindFarmPreview>> =
        flowOf(mockWindFarms)

    override fun observeWindFarmDetail(windFarmId: String): Flow<WindFarmDetail?> {
        val preview = mockWindFarms.firstOrNull { it.windFarm.id == windFarmId }
            ?: return flowOf(null)
        val turbines = mockTurbines[windFarmId] ?: emptyList()
        return flowOf(WindFarmDetail(preview.windFarm, preview.energyMetrics, turbines))
    }

    companion object {
        fun mockTurbinesFor(preview: WindFarmPreview): List<WindTurbine> {
            val existing = mockTurbines[preview.windFarm.id]
            if (existing != null) return existing
            val count = preview.windFarm.turbineCount.coerceAtLeast(1)
            val kwPerTurbine = if (count > 0) preview.windFarm.totalCapacityKw / count else 2000.0
            return (1..count).map { i ->
                WindTurbine(
                    id = "${preview.windFarm.id}-t$i",
                    windFarmId = preview.windFarm.id,
                    manufacturer = null,
                    model = null,
                    ratedPowerKw = kwPerTurbine,
                    rotorDiameterM = null,
                    hubHeightM = null,
                    commissioningYear = preview.windFarm.commissioningYear,
                    status = preview.windFarm.status,
                    operator = null,
                )
            }
        }

        val mockPreviews: List<WindFarmPreview> get() = mockWindFarms

        private fun estimatedLocalEnergyContributionPercent(
            totalCapacityKw: Double,
            turbineCount: Int,
        ): Double = WindFarmMetricCalculator.calculateLocalEnergyContributionPercent(
            WindFarm(
                id = "estimated",
                name = "Estimated",
                municipality = "Estimated",
                federalState = "Estimated",
                latitude = 0.0,
                longitude = 0.0,
                status = WindFarmStatus.IN_BETRIEB,
                turbineCount = turbineCount,
                totalCapacityKw = totalCapacityKw,
                commissioningYear = null,
            ),
        )

        private fun estimatedMunicipalRevenueEur(annualKwh: Double): Double =
            WindFarmMetricCalculator.calculateMunicipalRevenueEur(annualKwh)

        private val mockTurbines: Map<String, List<WindTurbine>> = mapOf(
            "windpark-uckermark" to (1..14).map { i ->
                WindTurbine(
                    id = "uckermark-t$i",
                    windFarmId = "windpark-uckermark",
                    manufacturer = "Enercon",
                    model = "E-126",
                    ratedPowerKw = 3_000.0,
                    rotorDiameterM = 127.0,
                    hubHeightM = 135.0,
                    commissioningYear = 2019,
                    status = WindFarmStatus.IN_BETRIEB,
                    operator = "WindNorth GmbH",
                )
            },
            "windpark-nordfriesland" to (1..9).map { i ->
                WindTurbine(
                    id = "nordfriesland-t$i",
                    windFarmId = "windpark-nordfriesland",
                    manufacturer = "Vestas",
                    model = "V150",
                    ratedPowerKw = 3_500.0,
                    rotorDiameterM = 150.0,
                    hubHeightM = 105.0,
                    commissioningYear = 2017,
                    status = if (i <= 3) WindFarmStatus.IN_WARTUNG else WindFarmStatus.IN_BETRIEB,
                    operator = "NordWind AG",
                )
            },
            "windpark-hunsrueck" to (1..11).map { i ->
                WindTurbine(
                    id = "hunsrueck-t$i",
                    windFarmId = "windpark-hunsrueck",
                    manufacturer = "Siemens Gamesa",
                    model = "SG 3.4-132",
                    ratedPowerKw = 3_600.0,
                    rotorDiameterM = 132.0,
                    hubHeightM = 110.0,
                    commissioningYear = 2021,
                    status = WindFarmStatus.IN_BETRIEB,
                    operator = "RheinWind GmbH & Co. KG",
                )
            },
            "windpark-altmark" to (1..7).map { i ->
                WindTurbine(
                    id = "altmark-t$i",
                    windFarmId = "windpark-altmark",
                    manufacturer = "Nordex",
                    model = "N163/5.X",
                    ratedPowerKw = 5_000.0,
                    rotorDiameterM = 163.0,
                    hubHeightM = 164.0,
                    commissioningYear = null,
                    status = WindFarmStatus.IN_PLANUNG,
                    operator = "AltmarkWind GmbH",
                )
            },
            "windpark-oberbayern" to (1..3).map { i ->
                WindTurbine(
                    id = "oberbayern-t$i",
                    windFarmId = "windpark-oberbayern",
                    manufacturer = "Enercon",
                    model = "E-66",
                    ratedPowerKw = 1_500.0,
                    rotorDiameterM = 66.0,
                    hubHeightM = 65.0,
                    commissioningYear = 2002,
                    status = WindFarmStatus.STILLGELEGT,
                    operator = "BayernWind GmbH",
                )
            },
        )

        // Mock discovery data until MaStR/DWD-backed repositories are implemented.
        private val mockWindFarms = listOf(
            WindFarmPreview(
                windFarm = WindFarm(
                    id = "windpark-uckermark",
                    name = "Windpark Uckermark",
                    municipality = "Prenzlau",
                    federalState = "Brandenburg",
                    latitude = 53.3137,
                    longitude = 13.8627,
                    status = WindFarmStatus.IN_BETRIEB,
                    turbineCount = 14,
                    totalCapacityKw = 42_000.0,
                    commissioningYear = 2019,
                    postalCode = "17291",
                ),
                energyMetrics = EnergyMetrics(
                    estimatedCurrentOutputKw = 18_500.0,
                    estimatedAnnualProductionKwh = 92_000_000.0,
                    householdsSupplied = 26_000,
                    co2SavingsTonnesPerYear = 38_600.0,
                    localEnergyContributionPercent = estimatedLocalEnergyContributionPercent(42_000.0, 14),
                    municipalRevenueEurPerYear = estimatedMunicipalRevenueEur(92_000_000.0),
                ),
            ),
            WindFarmPreview(
                windFarm = WindFarm(
                    id = "windpark-nordfriesland",
                    name = "Windpark Nordfriesland",
                    municipality = "Husum",
                    federalState = "Schleswig-Holstein",
                    latitude = 54.4858,
                    longitude = 9.0524,
                    status = WindFarmStatus.IN_WARTUNG,
                    turbineCount = 9,
                    totalCapacityKw = 31_500.0,
                    commissioningYear = 2017,
                    postalCode = "25813",
                ),
                energyMetrics = EnergyMetrics(
                    estimatedCurrentOutputKw = 7_800.0,
                    estimatedAnnualProductionKwh = 74_000_000.0,
                    householdsSupplied = 20_900,
                    co2SavingsTonnesPerYear = 31_100.0,
                    localEnergyContributionPercent = estimatedLocalEnergyContributionPercent(31_500.0, 9),
                    municipalRevenueEurPerYear = estimatedMunicipalRevenueEur(74_000_000.0),
                ),
            ),
            WindFarmPreview(
                windFarm = WindFarm(
                    id = "windpark-hunsrueck",
                    name = "Windpark Hunsrueck",
                    municipality = "Kirchberg",
                    federalState = "Rheinland-Pfalz",
                    latitude = 49.9435,
                    longitude = 7.4079,
                    status = WindFarmStatus.IN_BETRIEB,
                    turbineCount = 11,
                    totalCapacityKw = 39_600.0,
                    commissioningYear = 2021,
                    postalCode = "55481",
                ),
                energyMetrics = EnergyMetrics(
                    estimatedCurrentOutputKw = 15_100.0,
                    estimatedAnnualProductionKwh = 86_500_000.0,
                    householdsSupplied = 24_400,
                    co2SavingsTonnesPerYear = 36_300.0,
                    localEnergyContributionPercent = estimatedLocalEnergyContributionPercent(39_600.0, 11),
                    municipalRevenueEurPerYear = estimatedMunicipalRevenueEur(86_500_000.0),
                ),
            ),
            WindFarmPreview(
                windFarm = WindFarm(
                    id = "windpark-altmark",
                    name = "Windpark Altmark",
                    municipality = "Stendal",
                    federalState = "Sachsen-Anhalt",
                    latitude = 52.6069,
                    longitude = 11.8587,
                    status = WindFarmStatus.IN_PLANUNG,
                    turbineCount = 7,
                    totalCapacityKw = 28_000.0,
                    commissioningYear = null,
                    postalCode = "39576",
                ),
                energyMetrics = EnergyMetrics(
                    estimatedCurrentOutputKw = 0.0,
                    estimatedAnnualProductionKwh = 61_000_000.0,
                    householdsSupplied = 17_200,
                    co2SavingsTonnesPerYear = 25_600.0,
                    localEnergyContributionPercent = estimatedLocalEnergyContributionPercent(28_000.0, 7),
                    municipalRevenueEurPerYear = estimatedMunicipalRevenueEur(61_000_000.0),
                ),
            ),
            WindFarmPreview(
                windFarm = WindFarm(
                    id = "windpark-oberbayern",
                    name = "Windpark Oberbayern",
                    municipality = "Ebersberg",
                    federalState = "Bayern",
                    latitude = 48.0786,
                    longitude = 11.9708,
                    status = WindFarmStatus.STILLGELEGT,
                    turbineCount = 3,
                    totalCapacityKw = 4_500.0,
                    commissioningYear = 2002,
                    postalCode = "85560",
                ),
                energyMetrics = EnergyMetrics(
                    estimatedCurrentOutputKw = 0.0,
                    estimatedAnnualProductionKwh = 0.0,
                    householdsSupplied = 0,
                    co2SavingsTonnesPerYear = 0.0,
                    localEnergyContributionPercent = estimatedLocalEnergyContributionPercent(4_500.0, 3),
                    municipalRevenueEurPerYear = estimatedMunicipalRevenueEur(0.0),
                ),
            ),
        )
    }
}
