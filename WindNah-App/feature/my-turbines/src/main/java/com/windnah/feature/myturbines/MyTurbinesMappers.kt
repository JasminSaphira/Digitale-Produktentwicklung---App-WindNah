package com.windnah.feature.myturbines

import com.windnah.core.model.WindFarmPreview
import com.windnah.core.model.WindFarmStatus
import java.util.Locale

data class MyTurbinesWindFarmItemUiModel(
    val id: String,
    val name: String,
    val location: String,
    val statusLabel: String,
    val status: MyTurbinesStatusUiModel,
    val co2Savings: String,
    val households: String,
    val capacity: String,
)

enum class MyTurbinesStatusUiModel {
    InOperation,
    Maintenance,
    Planned,
    Decommissioned,
}

internal fun mapMyTurbinesUiState(
    favoriteIds: List<String>,
    recentlyViewedIds: List<String>,
    previews: List<WindFarmPreview>,
): MyTurbinesUiState {
    val previewsById = previews.associateBy { it.windFarm.id }

    return MyTurbinesUiState(
        favorites = favoriteIds.mapNotNull { previewId ->
            previewsById[previewId]?.toMyTurbinesWindFarmItemUiModel()
        },
        recentlyViewed = recentlyViewedIds.mapNotNull { previewId ->
            previewsById[previewId]?.toMyTurbinesWindFarmItemUiModel()
        },
    )
}

internal fun WindFarmPreview.toMyTurbinesWindFarmItemUiModel(): MyTurbinesWindFarmItemUiModel {
    val farm = windFarm

    return MyTurbinesWindFarmItemUiModel(
        id = farm.id,
        name = farm.name,
        location = "${farm.municipality}, ${farm.federalState}",
        statusLabel = farm.status.label,
        status = farm.status.toMyTurbinesStatusUiModel(),
        co2Savings = "${formatKilotonnes(energyMetrics.co2SavingsTonnesPerYear)} kt/a",
        households = formatCompactNumber(energyMetrics.householdsSupplied),
        capacity = formatMegawatts(farm.totalCapacityKw),
    )
}

internal fun formatKilotonnes(tonnes: Double): String =
    String.format(Locale.GERMANY, "%.1f", tonnes / 1_000.0)

internal fun formatCompactNumber(value: Int): String =
    if (value >= 1_000) {
        String.format(Locale.GERMANY, "%.1fk", value / 1_000.0)
    } else {
        value.toString()
    }

internal fun formatMegawatts(totalCapacityKw: Double): String =
    String.format(Locale.GERMANY, "%.1f MW", totalCapacityKw / 1_000.0)

internal fun WindFarmStatus.toMyTurbinesStatusUiModel(): MyTurbinesStatusUiModel = when (this) {
    WindFarmStatus.IN_BETRIEB -> MyTurbinesStatusUiModel.InOperation
    WindFarmStatus.IN_WARTUNG -> MyTurbinesStatusUiModel.Maintenance
    WindFarmStatus.IN_PLANUNG -> MyTurbinesStatusUiModel.Planned
    WindFarmStatus.STILLGELEGT -> MyTurbinesStatusUiModel.Decommissioned
}

internal val WindFarmStatus.label: String
    get() = when (this) {
        WindFarmStatus.IN_BETRIEB -> "In Betrieb"
        WindFarmStatus.IN_WARTUNG -> "In Wartung"
        WindFarmStatus.IN_PLANUNG -> "In Planung"
        WindFarmStatus.STILLGELEGT -> "Stillgelegt"
    }
