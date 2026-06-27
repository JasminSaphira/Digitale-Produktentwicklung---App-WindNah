package com.windnah.core.database.cache

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Persisted wind farm aggregate (one row per park) for offline display on the map and preview.
 * Energy metrics are flattened into columns; nullable metrics map to nullable columns.
 */
@Entity(tableName = "cached_wind_farms")
data class CachedWindFarmEntity(
    @PrimaryKey val id: String,
    val name: String,
    val municipality: String,
    val federalState: String,
    val latitude: Double,
    val longitude: Double,
    val status: String,
    val turbineCount: Int,
    val totalCapacityKw: Double,
    val commissioningYear: Int?,
    val postalCode: String?,
    val estimatedCurrentOutputKw: Double,
    val estimatedAnnualProductionKwh: Double,
    val householdsSupplied: Int,
    val co2SavingsTonnesPerYear: Double,
    val localEnergyContributionPercent: Double?,
    val municipalRevenueEurPerYear: Double?,
    val estimatedNoiseLevelDbA: Double?,
    val cachedAtMillis: Long,
)
