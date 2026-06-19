package com.windnah.core.data.repository

import com.windnah.core.domain.repository.WindFarmRepository
import com.windnah.core.model.EnergyMetrics
import com.windnah.core.model.WindFarm
import com.windnah.core.model.WindFarmPreview
import com.windnah.core.model.WindFarmStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeWindFarmRepository @Inject constructor() : WindFarmRepository {

    override fun observeWindFarmPreviews(): Flow<List<WindFarmPreview>> =
        flowOf(mockWindFarms)

    companion object {
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
                    localEnergyContributionPercent = null,
                    municipalRevenueEurPerYear = null,
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
                    localEnergyContributionPercent = null,
                    municipalRevenueEurPerYear = null,
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
                    localEnergyContributionPercent = null,
                    municipalRevenueEurPerYear = null,
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
                    localEnergyContributionPercent = null,
                    municipalRevenueEurPerYear = null,
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
                    localEnergyContributionPercent = null,
                    municipalRevenueEurPerYear = null,
                ),
            ),
        )
    }
}
