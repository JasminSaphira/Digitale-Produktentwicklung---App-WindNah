package com.windnah.feature.myturbines

data class MyTurbinesUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val favorites: List<MyTurbinesWindFarmItemUiModel> = emptyList(),
    val recentlyViewed: List<MyTurbinesWindFarmItemUiModel> = emptyList(),
)
