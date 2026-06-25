package com.windnah.core.model

data class FactArticle(
    val id: String,
    val title: String,
    val myth: String,
    val category: FactCategory,
    val explanation: String,
    val sources: List<String>,
)
