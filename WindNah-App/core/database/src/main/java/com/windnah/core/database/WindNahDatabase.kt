package com.windnah.core.database

import androidx.room.Database
import androidx.room.migration.Migration
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.windnah.core.database.cache.CachedTurbineEntity
import com.windnah.core.database.cache.CachedWindFarmDao
import com.windnah.core.database.cache.CachedWindFarmEntity
import com.windnah.core.database.favorite.FavoriteDao
import com.windnah.core.database.favorite.FavoriteEntity
import com.windnah.core.database.recentlyviewed.RecentlyViewedDao
import com.windnah.core.database.recentlyviewed.RecentlyViewedEntity

@Database(
    entities = [
        FavoriteEntity::class,
        RecentlyViewedEntity::class,
        CachedWindFarmEntity::class,
        CachedTurbineEntity::class,
    ],
    version = 3,
    exportSchema = false,
)
abstract class WindNahDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
    abstract fun recentlyViewedDao(): RecentlyViewedDao
    abstract fun cachedWindFarmDao(): CachedWindFarmDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS recently_viewed_wind_farms (
                        windFarmId TEXT NOT NULL PRIMARY KEY,
                        viewedAtMillis INTEGER NOT NULL
                    )
                    """.trimIndent(),
                )
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS cached_wind_farms (
                        id TEXT NOT NULL PRIMARY KEY,
                        name TEXT NOT NULL,
                        municipality TEXT NOT NULL,
                        federalState TEXT NOT NULL,
                        latitude REAL NOT NULL,
                        longitude REAL NOT NULL,
                        status TEXT NOT NULL,
                        turbineCount INTEGER NOT NULL,
                        totalCapacityKw REAL NOT NULL,
                        commissioningYear INTEGER,
                        postalCode TEXT,
                        estimatedCurrentOutputKw REAL NOT NULL,
                        estimatedAnnualProductionKwh REAL NOT NULL,
                        householdsSupplied INTEGER NOT NULL,
                        co2SavingsTonnesPerYear REAL NOT NULL,
                        localEnergyContributionPercent REAL,
                        municipalRevenueEurPerYear REAL,
                        estimatedNoiseLevelDbA REAL,
                        cachedAtMillis INTEGER NOT NULL
                    )
                    """.trimIndent(),
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS cached_turbines (
                        id TEXT NOT NULL PRIMARY KEY,
                        windFarmId TEXT NOT NULL,
                        manufacturer TEXT,
                        model TEXT,
                        ratedPowerKw REAL NOT NULL,
                        rotorDiameterM REAL,
                        hubHeightM REAL,
                        commissioningYear INTEGER,
                        status TEXT NOT NULL,
                        operator TEXT
                    )
                    """.trimIndent(),
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_cached_turbines_windFarmId ON cached_turbines (windFarmId)",
                )
            }
        }
    }
}
