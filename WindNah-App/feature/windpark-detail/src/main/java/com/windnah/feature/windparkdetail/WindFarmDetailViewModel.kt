package com.windnah.feature.windparkdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.windnah.core.domain.usecase.GetWindFarmDetailUseCase
import com.windnah.core.model.WindFarmDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

sealed interface WindFarmDetailUiState {
    data object Loading : WindFarmDetailUiState
    data class Success(val detail: WindFarmDetail) : WindFarmDetailUiState
    data object NotFound : WindFarmDetailUiState
}

@HiltViewModel
class WindFarmDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getWindFarmDetail: GetWindFarmDetailUseCase,
) : ViewModel() {

    private val windFarmId: String = checkNotNull(savedStateHandle["windFarmId"])

    val uiState: StateFlow<WindFarmDetailUiState> =
        getWindFarmDetail(windFarmId)
            .map { detail ->
                if (detail == null) WindFarmDetailUiState.NotFound
                else WindFarmDetailUiState.Success(detail)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = WindFarmDetailUiState.Loading,
            )
}
