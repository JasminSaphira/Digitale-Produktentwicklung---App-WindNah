package com.windnah.core.database.favorite

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val windFarmId: String,
    val createdAtMillis: Long,
)
