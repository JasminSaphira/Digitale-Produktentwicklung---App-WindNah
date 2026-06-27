package com.windnah.core.database.cache

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Persisted individual turbine for offline display on the detail screen.
 * Indexed by [windFarmId] so turbines for a park can be loaded without a full scan.
 */
@Entity(
    tableName = "cached_turbines",
    indices = [Index("windFarmId")],
)
data class CachedTurbineEntity(
    @PrimaryKey val id: String,
    val windFarmId: String,
    val manufacturer: String?,
    val model: String?,
    val ratedPowerKw: Double,
    val rotorDiameterM: Double?,
    val hubHeightM: Double?,
    val commissioningYear: Int?,
    val status: String,
    val operator: String?,
)
