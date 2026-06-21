package com.windnah.core.model

data class WindFarmDetail(
    val windFarm: WindFarm,
    val energyMetrics: EnergyMetrics,
    val turbines: List<WindTurbine>,
)
