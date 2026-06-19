package com.windnah.core.model

data class WindFarm(
    val id: String,
    val name: String,
    val municipality: String,
    val federalState: String,
    val latitude: Double,
    val longitude: Double,
    val status: WindFarmStatus,
    val turbineCount: Int,
    val totalCapacityKw: Double,
    val commissioningYear: Int?,
    val postalCode: String? = null,
)
