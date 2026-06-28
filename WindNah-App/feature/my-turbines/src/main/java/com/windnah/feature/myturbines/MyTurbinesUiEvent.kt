package com.windnah.feature.myturbines

sealed interface MyTurbinesUiEvent {
    data class WindFarmClicked(val windFarmId: String) : MyTurbinesUiEvent
    data class RemoveFavoriteClicked(val windFarmId: String) : MyTurbinesUiEvent
    data object DiscoverClicked : MyTurbinesUiEvent
    data object BackClicked : MyTurbinesUiEvent
    data object RetryClicked : MyTurbinesUiEvent
}
