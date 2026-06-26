package com.windnah.core.database.recentlyviewed

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recently_viewed_wind_farms")
data class RecentlyViewedEntity(
    @PrimaryKey val windFarmId: String,
    val viewedAtMillis: Long,
)
