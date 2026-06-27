package com.windnah.core.database.cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface CachedWindFarmDao {

    @Query("SELECT * FROM cached_wind_farms")
    fun observeWindFarms(): Flow<List<CachedWindFarmEntity>>

    @Query("SELECT * FROM cached_wind_farms WHERE id = :windFarmId LIMIT 1")
    suspend fun getWindFarm(windFarmId: String): CachedWindFarmEntity?

    @Query("SELECT * FROM cached_turbines WHERE windFarmId = :windFarmId")
    suspend fun getTurbines(windFarmId: String): List<CachedTurbineEntity>

    @Query("SELECT COUNT(*) FROM cached_wind_farms")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWindFarms(windFarms: List<CachedWindFarmEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTurbines(turbines: List<CachedTurbineEntity>)

    @Query("DELETE FROM cached_wind_farms")
    suspend fun clearWindFarms()

    @Query("DELETE FROM cached_turbines")
    suspend fun clearTurbines()

    /** Atomically replaces the whole cache so it never sits in a half-written state. */
    @Transaction
    suspend fun replaceAll(
        windFarms: List<CachedWindFarmEntity>,
        turbines: List<CachedTurbineEntity>,
    ) {
        clearWindFarms()
        clearTurbines()
        insertWindFarms(windFarms)
        insertTurbines(turbines)
    }
}
