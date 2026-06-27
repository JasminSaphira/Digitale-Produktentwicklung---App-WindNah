package com.windnah.core.data.mapper

import com.windnah.core.database.cache.CachedTurbineEntity
import com.windnah.core.database.cache.CachedWindFarmEntity
import com.windnah.core.model.EnergyMetrics
import com.windnah.core.model.WindFarm
import com.windnah.core.model.WindFarmPreview
import com.windnah.core.model.WindFarmStatus
import com.windnah.core.model.WindTurbine

fun WindFarmPreview.toCachedEntity(cachedAtMillis: Long): CachedWindFarmEntity =
    CachedWindFarmEntity(
        id = windFarm.id,
        name = windFarm.name,
        municipality = windFarm.municipality,
        federalState = windFarm.federalState,
        latitude = windFarm.latitude,
        longitude = windFarm.longitude,
        status = windFarm.status.name,
        turbineCount = windFarm.turbineCount,
        totalCapacityKw = windFarm.totalCapacityKw,
        commissioningYear = windFarm.commissioningYear,
        postalCode = windFarm.postalCode,
        estimatedCurrentOutputKw = energyMetrics.estimatedCurrentOutputKw,
        estimatedAnnualProductionKwh = energyMetrics.estimatedAnnualProductionKwh,
        householdsSupplied = energyMetrics.householdsSupplied,
        co2SavingsTonnesPerYear = energyMetrics.co2SavingsTonnesPerYear,
        localEnergyContributionPercent = energyMetrics.localEnergyContributionPercent,
        municipalRevenueEurPerYear = energyMetrics.municipalRevenueEurPerYear,
        estimatedNoiseLevelDbA = energyMetrics.estimatedNoiseLevelDbA,
        cachedAtMillis = cachedAtMillis,
    )

fun CachedWindFarmEntity.toWindFarmPreview(): WindFarmPreview =
    WindFarmPreview(
        windFarm = WindFarm(
            id = id,
            name = name,
            municipality = municipality,
            federalState = federalState,
            latitude = latitude,
            longitude = longitude,
            status = status.toWindFarmStatusOrDefault(),
            turbineCount = turbineCount,
            totalCapacityKw = totalCapacityKw,
            commissioningYear = commissioningYear,
            postalCode = postalCode,
        ),
        energyMetrics = EnergyMetrics(
            estimatedCurrentOutputKw = estimatedCurrentOutputKw,
            estimatedAnnualProductionKwh = estimatedAnnualProductionKwh,
            householdsSupplied = householdsSupplied,
            co2SavingsTonnesPerYear = co2SavingsTonnesPerYear,
            localEnergyContributionPercent = localEnergyContributionPercent,
            municipalRevenueEurPerYear = municipalRevenueEurPerYear,
            estimatedNoiseLevelDbA = estimatedNoiseLevelDbA,
        ),
    )

fun WindTurbine.toCachedEntity(): CachedTurbineEntity =
    CachedTurbineEntity(
        id = id,
        windFarmId = windFarmId,
        manufacturer = manufacturer,
        model = model,
        ratedPowerKw = ratedPowerKw,
        rotorDiameterM = rotorDiameterM,
        hubHeightM = hubHeightM,
        commissioningYear = commissioningYear,
        status = status.name,
        operator = operator,
    )

fun CachedTurbineEntity.toWindTurbine(): WindTurbine =
    WindTurbine(
        id = id,
        windFarmId = windFarmId,
        manufacturer = manufacturer,
        model = model,
        ratedPowerKw = ratedPowerKw,
        rotorDiameterM = rotorDiameterM,
        hubHeightM = hubHeightM,
        commissioningYear = commissioningYear,
        status = status.toWindFarmStatusOrDefault(),
        operator = operator,
    )

private fun String.toWindFarmStatusOrDefault(): WindFarmStatus =
    runCatching { WindFarmStatus.valueOf(this) }.getOrDefault(WindFarmStatus.IN_BETRIEB)
