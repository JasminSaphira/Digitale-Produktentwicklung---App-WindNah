package com.windnah.core.database.favorite

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT windFarmId FROM favorites ORDER BY createdAtMillis DESC")
    fun observeFavoriteIds(): Flow<List<String>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE windFarmId = :windFarmId)")
    fun observeIsFavorite(windFarmId: String): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE windFarmId = :windFarmId)")
    suspend fun isFavorite(windFarmId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE windFarmId = :windFarmId")
    suspend fun deleteFavorite(windFarmId: String)
}
