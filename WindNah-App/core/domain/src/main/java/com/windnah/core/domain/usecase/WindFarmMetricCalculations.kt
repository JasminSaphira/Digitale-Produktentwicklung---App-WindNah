package com.windnah.core.domain.usecase

import com.windnah.core.model.EnergyMetrics
import com.windnah.core.model.WeatherData
import com.windnah.core.model.WindFarm
import com.windnah.core.model.WindFarmStatus
import com.windnah.core.model.WindTurbine
import kotlin.math.log10
import kotlin.math.pow

private const val HELLMANN_EXPONENT = 0.14
private const val MEASUREMENT_HEIGHT_M = 10.0
private const val CUT_IN_MS = 3.0
private const val RATED_MS = 12.0
private const val CUT_OUT_MS = 25.0
private const val WAKE_EFFICIENCY = 0.85
private const val DEFAULT_HUB_HEIGHT_M = 100.0

private const val ANNUAL_FULL_LOAD_HOURS = 2_000.0
private const val LOCAL_REFERENCE_FULL_LOAD_HOURS = 1_900.0
private const val AVERAGE_HOUSEHOLD_KWH_PER_YEAR = 3_500.0
private const val AVERAGE_ELECTRICITY_MIX_CO2_G_PER_KWH = 363.0
private const val WIND_LIFECYCLE_CO2_G_PER_KWH = 9.0
private const val MUNICIPAL_REVENUE_EUR_PER_KWH = 0.002
private const val MUNICIPAL_KWH_PER_PERSON_PER_YEAR = 5_500.0

private const val MIN_ESTIMATED_POPULATION = 4_500.0
private const val POPULATION_PER_KW_FACTOR = 0.75
private const val POPULATION_PER_TURBINE_FACTOR = 1_100.0

private const val NOISE_REFERENCE_DISTANCE_M = 500.0
private const val NOISE_MIN_DB = 30.0
private const val NOISE_MAX_DB = 90.0

object WindFarmMetricCalculator {
    fun calculateCurrentOutput(
        weather: WeatherData,
        turbines: List<WindTurbine>,
        installedCapacityKw: Double,
    ): Double {
        if (turbines.isEmpty()) {
            return calculateFallbackCurrentOutput(
                windSpeedMs = weather.windSpeedMs,
                installedCapacityKw = installedCapacityKw,
                hubHeightM = DEFAULT_HUB_HEIGHT_M,
            )
        }

        val activeTurbines = turbines.filter { it.status == WindFarmStatus.IN_BETRIEB }
        if (activeTurbines.isEmpty()) {
            return 0.0
        }

        return activeTurbines
            .sumOf { turbine ->
                calculateTurbineOutput(
                    windSpeedMs = weather.windSpeedMs,
                    hubHeightM = turbine.hubHeightM ?: DEFAULT_HUB_HEIGHT_M,
                    ratedPowerKw = turbine.ratedPowerKw,
                )
            }
            .times(WAKE_EFFICIENCY)
    }

    fun calculateAnnualProductionKwh(installedCapacityKw: Double): Double =
        installedCapacityKw * ANNUAL_FULL_LOAD_HOURS

    fun calculateHouseholdsSupplied(annualProductionKwh: Double): Int =
        (annualProductionKwh / AVERAGE_HOUSEHOLD_KWH_PER_YEAR).toInt()

    fun calculateCo2SavingsTonnes(annualProductionKwh: Double): Double =
        annualProductionKwh * (AVERAGE_ELECTRICITY_MIX_CO2_G_PER_KWH - WIND_LIFECYCLE_CO2_G_PER_KWH) / 1_000_000.0

    fun calculateLocalEnergyContributionPercent(windFarm: WindFarm): Double {
        val annualProductionForLocalEstimate = windFarm.totalCapacityKw * LOCAL_REFERENCE_FULL_LOAD_HOURS
        val estimatedPopulation = estimateMunicipalityPopulation(windFarm)
        val municipalConsumptionKwh = estimatedPopulation * MUNICIPAL_KWH_PER_PERSON_PER_YEAR
        if (municipalConsumptionKwh <= 0.0) return 0.0
        return annualProductionForLocalEstimate / municipalConsumptionKwh * 100.0
    }

    fun calculateMunicipalRevenueEur(annualProductionKwh: Double): Double =
        annualProductionKwh * MUNICIPAL_REVENUE_EUR_PER_KWH

    fun calculateNoiseEstimateDbA(
        weather: WeatherData,
        turbines: List<WindTurbine>,
        installedCapacityKw: Double,
        currentOutputKw: Double,
    ): Double? {
        val activeTurbines = turbines.count { it.status == WindFarmStatus.IN_BETRIEB }
        if (activeTurbines == 0) return null

        val rotorDiameters = turbines.mapNotNull { it.rotorDiameterM }
        val averageRotorDiameterM = rotorDiameters.takeIf { it.isNotEmpty() }?.average() ?: 120.0
        val baseNoiseDb = when {
            averageRotorDiameterM < 100.0 -> 60.0
            averageRotorDiameterM <= 130.0 -> 63.0
            else -> 66.0
        }

        val utilization = if (installedCapacityKw > 0.0) {
            (currentOutputKw / installedCapacityKw).coerceIn(0.0, 1.0)
        } else {
            0.0
        }

        val utilizationAdjustmentDb = utilization * 6.0
        val windAdjustmentDb = ((weather.windSpeedMs - 6.0) * 0.7).coerceIn(-2.0, 6.0)
        val distanceAttenuationDb = 20.0 * log10(NOISE_REFERENCE_DISTANCE_M / 100.0)
        val turbineAggregationDb = if (activeTurbines > 1) {
            10.0 * log10(activeTurbines.toDouble())
        } else {
            0.0
        }

        return (baseNoiseDb + utilizationAdjustmentDb + windAdjustmentDb + turbineAggregationDb - distanceAttenuationDb)
            .coerceIn(NOISE_MIN_DB, NOISE_MAX_DB)
    }

    /**
     * Extrapolates a reference dB(A) value (given at [NOISE_REFERENCE_DISTANCE_M]) to another
     * distance using inverse-distance sound attenuation (20·log10(ref/distance)), clamped to the
     * plausible [NOISE_MIN_DB]..[NOISE_MAX_DB] range. Used by the detail-screen distance slider.
     */
    fun estimateNoiseForDistanceDbA(referenceNoiseDbA: Double, distanceM: Int): Double =
        (referenceNoiseDbA + 20.0 * log10(NOISE_REFERENCE_DISTANCE_M / distanceM.toDouble()))
            .coerceIn(NOISE_MIN_DB, NOISE_MAX_DB)

    fun buildEnergyMetrics(
        windFarm: WindFarm,
        turbines: List<WindTurbine>,
        weather: WeatherData? = null,
        currentOutputOverrideKw: Double? = null,
    ): EnergyMetrics {
        val annualProductionKwh = calculateAnnualProductionKwh(windFarm.totalCapacityKw)
        val currentOutputKw = currentOutputOverrideKw
            ?: weather?.let { calculateCurrentOutput(it, turbines, windFarm.totalCapacityKw) }
            ?: 0.0

        return EnergyMetrics(
            estimatedCurrentOutputKw = currentOutputKw,
            estimatedAnnualProductionKwh = annualProductionKwh,
            householdsSupplied = calculateHouseholdsSupplied(annualProductionKwh),
            co2SavingsTonnesPerYear = calculateCo2SavingsTonnes(annualProductionKwh),
            localEnergyContributionPercent = calculateLocalEnergyContributionPercent(windFarm),
            municipalRevenueEurPerYear = calculateMunicipalRevenueEur(annualProductionKwh),
            estimatedNoiseLevelDbA = weather?.let {
                calculateNoiseEstimateDbA(
                    weather = it,
                    turbines = turbines,
                    installedCapacityKw = windFarm.totalCapacityKw,
                    currentOutputKw = currentOutputKw,
                )
            },
        )
    }

    private fun calculateFallbackCurrentOutput(
        windSpeedMs: Double,
        installedCapacityKw: Double,
        hubHeightM: Double,
    ): Double {
        val hubWindSpeedMs = windSpeedAtHubHeight(windSpeedMs, hubHeightM)
        val ratio = when {
            hubWindSpeedMs < CUT_IN_MS -> 0.0
            hubWindSpeedMs <= RATED_MS -> ((hubWindSpeedMs - CUT_IN_MS) / (RATED_MS - CUT_IN_MS)).pow3()
            hubWindSpeedMs <= CUT_OUT_MS -> 1.0
            else -> 0.0
        }
        return installedCapacityKw * ratio * WAKE_EFFICIENCY
    }

    private fun calculateTurbineOutput(
        windSpeedMs: Double,
        hubHeightM: Double,
        ratedPowerKw: Double,
    ): Double {
        val hubWindSpeedMs = windSpeedAtHubHeight(windSpeedMs, hubHeightM)
        val ratio = when {
            hubWindSpeedMs < CUT_IN_MS -> 0.0
            hubWindSpeedMs <= RATED_MS -> ((hubWindSpeedMs - CUT_IN_MS) / (RATED_MS - CUT_IN_MS)).pow3()
            hubWindSpeedMs <= CUT_OUT_MS -> 1.0
            else -> 0.0
        }
        return ratedPowerKw * ratio
    }

    private fun windSpeedAtHubHeight(windSpeedMs: Double, hubHeightM: Double): Double =
        windSpeedMs * (hubHeightM / MEASUREMENT_HEIGHT_M).pow(HELLMANN_EXPONENT)

    private fun Double.pow3(): Double = this * this * this

    private fun estimateMunicipalityPopulation(windFarm: WindFarm): Double =
        maxOf(
            MIN_ESTIMATED_POPULATION,
            windFarm.totalCapacityKw * POPULATION_PER_KW_FACTOR + windFarm.turbineCount * POPULATION_PER_TURBINE_FACTOR,
        )
}

