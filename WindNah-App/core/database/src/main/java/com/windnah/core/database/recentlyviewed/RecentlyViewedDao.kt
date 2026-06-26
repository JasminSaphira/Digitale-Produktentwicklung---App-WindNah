package com.windnah.core.database.recentlyviewed

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentlyViewedDao {
    @Query("SELECT windFarmId FROM recently_viewed_wind_farms ORDER BY viewedAtMillis DESC LIMIT :limit")
    fun observeRecentlyViewedIds(limit: Int): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRecentlyViewed(entity: RecentlyViewedEntity)

    @Query(
        """
        DELETE FROM recently_viewed_wind_farms
        WHERE windFarmId NOT IN (
            SELECT windFarmId
            FROM recently_viewed_wind_farms
            ORDER BY viewedAtMillis DESC
            LIMIT :limit
        )
        """,
    )
    suspend fun trimToLimit(limit: Int)
}
