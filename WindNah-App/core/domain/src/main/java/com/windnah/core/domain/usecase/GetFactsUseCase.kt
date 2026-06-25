package com.windnah.core.domain.usecase

import com.windnah.core.domain.repository.FactRepository
import com.windnah.core.model.FactArticle
import com.windnah.core.model.FactCategory
import javax.inject.Inject

class GetFactsUseCase @Inject constructor(
    private val factRepository: FactRepository,
) {
    suspend operator fun invoke(category: FactCategory? = null): List<FactArticle> =
        factRepository.getFacts()
            .filter { fact -> category == null || fact.category == category }
}
