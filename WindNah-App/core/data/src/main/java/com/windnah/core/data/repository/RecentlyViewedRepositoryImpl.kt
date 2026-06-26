package com.windnah.core.data.repository

import com.windnah.core.database.recentlyviewed.RecentlyViewedDao
import com.windnah.core.database.recentlyviewed.RecentlyViewedEntity
import com.windnah.core.domain.repository.RecentlyViewedRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RecentlyViewedRepositoryImpl @Inject constructor(
    private val recentlyViewedDao: RecentlyViewedDao,
) : RecentlyViewedRepository {

    override fun observeRecentlyViewedIds(limit: Int): Flow<List<String>> =
        recentlyViewedDao.observeRecentlyViewedIds(limit)

    override suspend fun recordViewed(windFarmId: String) {
        recentlyViewedDao.upsertRecentlyViewed(
            RecentlyViewedEntity(
                windFarmId = windFarmId,
                viewedAtMillis = System.currentTimeMillis(),
            ),
        )
        recentlyViewedDao.trimToLimit(10)
    }
}
