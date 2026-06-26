package com.windnah.core.model

data class EnergyMetrics(
    val estimatedCurrentOutputKw: Double,
    val estimatedAnnualProductionKwh: Double,
    val householdsSupplied: Int,
    val co2SavingsTonnesPerYear: Double,
    val localEnergyContributionPercent: Double?,
    val municipalRevenueEurPerYear: Double?,
    val estimatedNoiseLevelDbA: Double? = null,
)
