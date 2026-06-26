package com.windnah.core.database

import androidx.room.Database
import androidx.room.migration.Migration
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.windnah.core.database.favorite.FavoriteDao
import com.windnah.core.database.favorite.FavoriteEntity
import com.windnah.core.database.recentlyviewed.RecentlyViewedDao
import com.windnah.core.database.recentlyviewed.RecentlyViewedEntity

@Database(
    entities = [
        FavoriteEntity::class,
        RecentlyViewedEntity::class,
    ],
    version = 2,
    exportSchema = false,
)
abstract class WindNahDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
    abstract fun recentlyViewedDao(): RecentlyViewedDao

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
    }
}
