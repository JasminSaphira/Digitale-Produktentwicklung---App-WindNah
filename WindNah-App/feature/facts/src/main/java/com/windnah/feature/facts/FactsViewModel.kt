package com.windnah.feature.facts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.windnah.core.domain.usecase.GetFactsUseCase
import com.windnah.core.model.FactCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val FACTS_UNAVAILABLE_MESSAGE = "Fakten können aktuell nicht geladen werden."

@HiltViewModel
class FactsViewModel @Inject constructor(
    private val getFactsUseCase: GetFactsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FactsUiState())
    val uiState: StateFlow<FactsUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadFacts()
    }

    fun onCategorySelected(category: FactCategory?) {
        _uiState.update { state -> state.copy(selectedCategory = category) }
        loadFacts(category)
    }

    fun retry() {
        loadFacts(_uiState.value.selectedCategory)
    }

    private fun loadFacts(category: FactCategory? = _uiState.value.selectedCategory) {
        loadJob?.cancel()
        _uiState.update { state ->
            state.copy(
                isLoading = true,
                errorMessage = null,
            )
        }
        loadJob = viewModelScope.launch {
            runCatching { getFactsUseCase(category) }
                .onSuccess { facts ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            facts = facts,
                            errorMessage = null,
                        )
                    }
                }
                .onFailure {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            facts = emptyList(),
                            errorMessage = FACTS_UNAVAILABLE_MESSAGE,
                        )
                    }
                }
        }
    }
}
