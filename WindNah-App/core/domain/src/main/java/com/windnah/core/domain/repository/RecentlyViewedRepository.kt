package com.windnah.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface RecentlyViewedRepository {
    fun observeRecentlyViewedIds(limit: Int = 10): Flow<List<String>>
    suspend fun recordViewed(windFarmId: String)
}
