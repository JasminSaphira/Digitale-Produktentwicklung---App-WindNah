package com.windnah.feature.myturbines

import com.windnah.core.common.format.formatCompactNumber
import com.windnah.core.common.format.formatKilotonnes
import com.windnah.core.common.format.formatMegawatts
import com.windnah.core.model.WindFarmPreview
import com.windnah.core.model.WindFarmStatus

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

internal fun WindFarmStatus.toMyTurbinesStatusUiModel(): MyTurbinesStatusUiModel = when (this) {
    WindFarmStatus.IN_BETRIEB -> MyTurbinesStatusUiModel.InOperation
    WindFarmStatus.IN_WARTUNG -> MyTurbinesStatusUiModel.Maintenance
    WindFarmStatus.IN_PLANUNG -> MyTurbinesStatusUiModel.Planned
    WindFarmStatus.STILLGELEGT -> MyTurbinesStatusUiModel.Decommissioned
}
