package com.windnah.core.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.windnah.core.database.WindNahDatabase
import com.windnah.core.database.cache.CachedWindFarmDao
import com.windnah.core.database.favorite.FavoriteDao
import com.windnah.core.database.recentlyviewed.RecentlyViewedDao
import com.windnah.core.data.repository.FavoriteRepositoryImpl
import com.windnah.core.data.repository.LocalMarkdownFactRepository
import com.windnah.core.data.repository.RecentlyViewedRepositoryImpl
import com.windnah.core.data.repository.UserPreferencesRepositoryImpl
import com.windnah.core.data.repository.WeatherRepositoryImpl
import com.windnah.core.data.repository.WindFarmRepositoryImpl
import com.windnah.core.domain.repository.FavoriteRepository
import com.windnah.core.domain.repository.FactRepository
import com.windnah.core.domain.repository.RecentlyViewedRepository
import com.windnah.core.domain.repository.UserPreferencesRepository
import com.windnah.core.domain.repository.WeatherRepository
import com.windnah.core.domain.repository.WindFarmRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "windnah_preferences")

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(
        impl: UserPreferencesRepositoryImpl,
    ): UserPreferencesRepository

    @Binds
    @Singleton
    abstract fun bindWindFarmRepository(
        impl: WindFarmRepositoryImpl,
    ): WindFarmRepository

    @Binds
    @Singleton
    abstract fun bindWeatherRepository(
        impl: WeatherRepositoryImpl,
    ): WeatherRepository

    @Binds
    @Singleton
    abstract fun bindFactRepository(
        impl: LocalMarkdownFactRepository,
    ): FactRepository

    @Binds
    @Singleton
    abstract fun bindFavoriteRepository(
        impl: FavoriteRepositoryImpl,
    ): FavoriteRepository

    @Binds
    @Singleton
    abstract fun bindRecentlyViewedRepository(
        impl: RecentlyViewedRepositoryImpl,
    ): RecentlyViewedRepository

    companion object {
        @Provides
        @Singleton
        fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
            context.dataStore

        @Provides
        @Singleton
        fun provideWindNahDatabase(@ApplicationContext context: Context): WindNahDatabase =
            Room.databaseBuilder(
                context,
                WindNahDatabase::class.java,
                "windnah.db",
            )
                .addMigrations(
                    WindNahDatabase.MIGRATION_1_2,
                    WindNahDatabase.MIGRATION_2_3,
                )
                .build()

        @Provides
        @Singleton
        fun provideFavoriteDao(database: WindNahDatabase): FavoriteDao =
            database.favoriteDao()

        @Provides
        @Singleton
        fun provideRecentlyViewedDao(database: WindNahDatabase): RecentlyViewedDao =
            database.recentlyViewedDao()

        @Provides
        @Singleton
        fun provideCachedWindFarmDao(database: WindNahDatabase): CachedWindFarmDao =
            database.cachedWindFarmDao()
    }
}
