package com.windnah.core.data.mapper

import com.windnah.core.model.EnergyMetrics
import com.windnah.core.model.WindFarm
import com.windnah.core.model.WindFarmPreview
import com.windnah.core.model.WindFarmStatus
import com.windnah.core.model.WindTurbine
import com.windnah.core.network.mastr.MastrWindUnitDto

fun List<MastrWindUnitDto>.toWindFarmPreviews(): List<WindFarmPreview> {
    return groupByWindPark()
        .map { (_, turbines) -> turbines.toWindFarmPreview() }
        .filter { it.windFarm.latitude != 0.0 && it.windFarm.longitude != 0.0 }
}

fun List<MastrWindUnitDto>.toWindTurbines(windFarmId: String): List<WindTurbine> =
    mapIndexed { index, dto ->
        WindTurbine(
            id = dto.mastrNummer,
            windFarmId = windFarmId,
            manufacturer = dto.hersteller,
            model = dto.typenbezeichnung,
            ratedPowerKw = dto.nettonennleistungKw ?: 0.0,
            rotorDiameterM = dto.rotorblattlaengeM?.let { it * 2 }, // blade length × 2 = diameter
            hubHeightM = dto.nabenhoeheM,
            commissioningYear = dto.inbetriebnahmedatum?.take(4)?.toIntOrNull(),
            status = dto.betriebsstatus.toWindFarmStatus(),
            operator = null,
        )
    }

private fun List<MastrWindUnitDto>.groupByWindPark(): Map<String, List<MastrWindUnitDto>> =
    groupBy { dto ->
        dto.windparkName?.normalizeWindparkName()
            ?: "${dto.gemeinde}_${dto.postleitzahl}"
    }

private fun List<MastrWindUnitDto>.toWindFarmPreview(): WindFarmPreview {
    val avgLat = mapNotNull { it.breitengrad }.average().let { if (it.isNaN()) 0.0 else it }
    val avgLon = mapNotNull { it.laengengrad }.average().let { if (it.isNaN()) 0.0 else it }
    val totalKw = sumOf { it.nettonennleistungKw ?: 0.0 }
    val firstUnit = first()
    val status = map { it.betriebsstatus.toWindFarmStatus() }.aggregateStatus()
    val commYear = mapNotNull { it.inbetriebnahmedatum?.take(4)?.toIntOrNull() }.minOrNull()

    val windFarmId = buildWindFarmId(
        name = firstUnit.windparkName ?: "${firstUnit.gemeinde}_${firstUnit.postleitzahl}",
        federalState = firstUnit.bundesland ?: "",
        lat = avgLat,
        lon = avgLon,
    )

    // Annual production estimate: totalKw × 2000 full-load hours (German onshore average)
    val annualKwh = totalKw * 2_000.0
    val householdsSupplied = (annualKwh / 3_500.0).toInt()
    val co2Savings = annualKwh * (363.0 - 9.0) / 1_000_000.0

    return WindFarmPreview(
        windFarm = WindFarm(
            id = windFarmId,
            name = firstUnit.windparkName?.trim() ?: "${firstUnit.gemeinde}, ${firstUnit.bundesland}",
            municipality = firstUnit.gemeinde?.trim() ?: "",
            federalState = firstUnit.bundesland?.trim() ?: "",
            latitude = avgLat,
            longitude = avgLon,
            status = status,
            turbineCount = size,
            totalCapacityKw = totalKw,
            commissioningYear = commYear,
            postalCode = firstUnit.postleitzahl,
        ),
        energyMetrics = EnergyMetrics(
            estimatedCurrentOutputKw = 0.0, // filled in by DWD-based calculation
            estimatedAnnualProductionKwh = annualKwh,
            householdsSupplied = householdsSupplied,
            co2SavingsTonnesPerYear = co2Savings,
            localEnergyContributionPercent = null,
            municipalRevenueEurPerYear = null,
        ),
    )
}

private fun String?.toWindFarmStatus(): WindFarmStatus = when (this) {
    "InBetrieb", "in Betrieb" -> WindFarmStatus.IN_BETRIEB
    "InPlanung", "in Planung", "Geplant" -> WindFarmStatus.IN_PLANUNG
    "Stillgelegt" -> WindFarmStatus.STILLGELEGT
    else -> WindFarmStatus.IN_BETRIEB
}

private fun List<WindFarmStatus>.aggregateStatus(): WindFarmStatus = when {
    all { it == WindFarmStatus.STILLGELEGT } -> WindFarmStatus.STILLGELEGT
    all { it == WindFarmStatus.IN_PLANUNG } -> WindFarmStatus.IN_PLANUNG
    any { it == WindFarmStatus.IN_WARTUNG } -> WindFarmStatus.IN_WARTUNG
    else -> WindFarmStatus.IN_BETRIEB
}

private fun String.normalizeWindparkName(): String =
    lowercase().trim().replace(Regex("[^a-z0-9äöüß]"), "_")

fun buildWindFarmId(name: String, federalState: String, lat: Double, lon: Double): String {
    val normalizedName = name.lowercase().trim().replace(Regex("[^a-z0-9äöüß]"), "_")
    val normalizedState = federalState.lowercase().trim().replace(" ", "_")
    val latStr = String.format("%.2f", lat).replace(".", "")
    val lonStr = String.format("%.2f", lon).replace(".", "")
    return "windfarm_${normalizedState}_${normalizedName}_${latStr}_${lonStr}"
}
