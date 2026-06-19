package com.windnah.core.domain.usecase

import com.windnah.core.domain.repository.WindFarmRepository
import com.windnah.core.model.WindFarmPreview
import com.windnah.core.model.WindFarmStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetDiscoverWindFarmsUseCase @Inject constructor(
    private val windFarmRepository: WindFarmRepository,
) {
    operator fun invoke(
        searchQuery: String,
        selectedStatus: WindFarmStatus?,
        selectedFederalState: String?,
    ): Flow<List<WindFarmPreview>> =
        windFarmRepository.observeWindFarmPreviews()
            .map { previews ->
                previews.filter { preview ->
                    preview.matchesSearch(searchQuery) &&
                        preview.matchesStatus(selectedStatus) &&
                        preview.matchesFederalState(selectedFederalState)
                }
            }

    private fun WindFarmPreview.matchesSearch(query: String): Boolean {
        val normalizedQuery = query.trim().normalizeSearchValue()
        if (normalizedQuery.isEmpty()) return true

        return listOf(
            windFarm.name,
            windFarm.municipality,
            windFarm.federalState,
            windFarm.id,
            windFarm.postalCode.orEmpty(),
        ).any { value ->
            value.normalizeSearchValue().contains(normalizedQuery)
        }
    }

    private fun WindFarmPreview.matchesStatus(status: WindFarmStatus?): Boolean =
        status == null || windFarm.status == status

    private fun WindFarmPreview.matchesFederalState(federalState: String?): Boolean =
        federalState == null || windFarm.federalState == federalState

    private fun String.normalizeSearchValue(): String =
        lowercase()
            .replace("ä", "ae")
            .replace("ö", "oe")
            .replace("ü", "ue")
            .replace("ß", "ss")
            .replace("-", " ")
            .trim()
}
