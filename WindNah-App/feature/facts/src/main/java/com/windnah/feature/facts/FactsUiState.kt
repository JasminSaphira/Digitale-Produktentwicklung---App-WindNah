package com.windnah.feature.facts

import com.windnah.core.model.FactArticle
import com.windnah.core.model.FactCategory

data class FactsUiState(
    val isLoading: Boolean = true,
    val facts: List<FactArticle> = emptyList(),
    val selectedCategory: FactCategory? = null,
    val errorMessage: String? = null,
)
