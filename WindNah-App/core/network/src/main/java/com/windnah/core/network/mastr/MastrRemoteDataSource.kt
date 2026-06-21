package com.windnah.core.network.mastr

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MastrRemoteDataSource @Inject constructor(
    private val api: MastrApiService,
) {
    suspend fun getWindUnits(
        bundesland: String? = null,
        top: Int = 500,
        skip: Int = 0,
    ): List<MastrWindUnitDto> {
        val filter = buildFilter(bundesland)
        return api.getWindUnits(filter = filter, top = top, skip = skip).value
    }

    private fun buildFilter(bundesland: String?): String {
        val base = MastrApiService.FILTER_ACTIVE
        return if (bundesland != null) "$base and Bundesland eq '$bundesland'" else base
    }
}
