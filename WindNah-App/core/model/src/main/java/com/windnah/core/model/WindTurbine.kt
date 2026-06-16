package com.windnah.core.model

data class WindTurbine(
    val id: String,
    val windFarmId: String,
    val manufacturer: String?,
    val model: String?,
    val ratedPowerKw: Double,
    val rotorDiameterM: Double?,
    val hubHeightM: Double?,
    val commissioningYear: Int?,
    val status: WindFarmStatus,
    val operator: String?,
)
