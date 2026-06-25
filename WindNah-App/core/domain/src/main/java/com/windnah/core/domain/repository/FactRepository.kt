package com.windnah.core.domain.repository

import com.windnah.core.model.FactArticle

interface FactRepository {
    suspend fun getFacts(): List<FactArticle>

    suspend fun getFact(id: String): FactArticle?
}
