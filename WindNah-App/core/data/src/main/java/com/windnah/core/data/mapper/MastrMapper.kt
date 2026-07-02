package com.windnah.core.data.mapper

import com.windnah.core.domain.usecase.WindFarmMetricCalculator
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

/**
 * Returns the units belonging to the given wind farm id, using the exact same grouping the
 * previews use. This keeps the detail view (turbines) consistent with the map (parks).
 */
fun List<MastrWindUnitDto>.unitsForWindFarmId(windFarmId: String): List<MastrWindUnitDto> =
    groupByWindPark().values.firstOrNull { units -> units.toWindFarmId() == windFarmId } ?: emptyList()

private fun List<MastrWindUnitDto>.toWindFarmId(): String {
    val (avgLat, avgLon) = averageCoords()
    val firstUnit = first()
    return buildWindFarmId(
        name = firstUnit.windparkName ?: "${firstUnit.gemeinde}_${firstUnit.postleitzahl}",
        federalState = firstUnit.bundesland ?: "",
        lat = avgLat,
        lon = avgLon,
    )
}

/** Mean of the units' coordinates, treating missing/empty values as 0.0. */
private fun List<MastrWindUnitDto>.averageCoords(): Pair<Double, Double> {
    val avgLat = mapNotNull { it.breitengrad }.average().let { if (it.isNaN()) 0.0 else it }
    val avgLon = mapNotNull { it.laengengrad }.average().let { if (it.isNaN()) 0.0 else it }
    return avgLat to avgLon
}

fun List<MastrWindUnitDto>.toWindTurbines(windFarmId: String): List<WindTurbine> =
    mapIndexed { index, dto ->
        WindTurbine(
            id = dto.mastrNummer,
            windFarmId = windFarmId,
            manufacturer = dto.hersteller,
            model = dto.typenbezeichnung,
            ratedPowerKw = dto.nettonennleistungKw ?: 0.0,
            rotorDiameterM = dto.rotorDiameterM,
            hubHeightM = dto.nabenhoeheM,
            commissioningYear = dto.inbetriebnahmedatum?.take(4)?.toIntOrNull(),
            status = dto.betriebsstatus.toWindFarmStatus(),
            operator = null,
        )
    }

/**
 * Grid size in degrees for clustering unnamed units by spatial proximity.
 * ~0.01° ≈ 1.1 km in latitude — units within the same grid cell are treated as one park.
 */
private const val GEO_CLUSTER_GRID_DEG = 0.01

private fun List<MastrWindUnitDto>.groupByWindPark(): Map<String, List<MastrWindUnitDto>> =
    groupBy { dto -> dto.windParkGroupKey() }

/**
 * Groups units into wind farms. Named parks group by their (normalized) name. Unnamed units
 * are clustered by spatial proximity (a coordinate grid) so that physically separate parks in
 * the same municipality stay separate instead of collapsing into one fake park.
 */
private fun MastrWindUnitDto.windParkGroupKey(): String {
    windparkName?.normalizeForId()?.let { return "name_$it" }

    val lat = breitengrad
    val lon = laengengrad
    if (lat != null && lon != null) {
        val latCell = Math.round(lat / GEO_CLUSTER_GRID_DEG)
        val lonCell = Math.round(lon / GEO_CLUSTER_GRID_DEG)
        return "geo_${latCell}_${lonCell}"
    }

    // No name and no coordinates — keep as a lone unit so it doesn't collapse into another park.
    return "id_$mastrNummer"
}

private fun List<MastrWindUnitDto>.toWindFarmPreview(): WindFarmPreview {
    val (avgLat, avgLon) = averageCoords()
    val totalKw = sumOf { it.nettonennleistungKw ?: 0.0 }
    val firstUnit = first()
    val status = map { it.betriebsstatus.toWindFarmStatus() }.aggregateStatus()
    val commYear = mapNotNull { it.inbetriebnahmedatum?.take(4)?.toIntOrNull() }.minOrNull()

    val windFarm = WindFarm(
        id = toWindFarmId(),
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
    )

    // Live current output is filled in later from DWD wind; here (no weather, no turbine detail)
    // buildEnergyMetrics produces the same annual/households/CO₂/local/municipal estimates the
    // detail view uses, keeping the calculation constants in one place (WindFarmMetricCalculator).
    return WindFarmPreview(
        windFarm = windFarm,
        energyMetrics = WindFarmMetricCalculator.buildEnergyMetrics(
            windFarm = windFarm,
            turbines = emptyList(),
            weather = null,
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

private val NON_ID_CHARS = Regex("[^a-z0-9äöüß]")

/** Lowercases and replaces every non-alphanumeric (German-aware) char with "_" for stable ids. */
private fun String.normalizeForId(): String =
    lowercase().trim().replace(NON_ID_CHARS, "_")

fun buildWindFarmId(name: String, federalState: String, lat: Double, lon: Double): String {
    val normalizedName = name.normalizeForId()
    val normalizedState = federalState.lowercase().trim().replace(" ", "_")
    val latStr = String.format("%.2f", lat).replace(".", "")
    val lonStr = String.format("%.2f", lon).replace(".", "")
    return "windfarm_${normalizedState}_${normalizedName}_${latStr}_${lonStr}"
}
