package com.windnah.core.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.windnah.core.data.repository.LocalMarkdownFactRepository
import com.windnah.core.data.repository.UserPreferencesRepositoryImpl
import com.windnah.core.data.repository.WeatherRepositoryImpl
import com.windnah.core.data.repository.WindFarmRepositoryImpl
import com.windnah.core.domain.repository.FactRepository
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

    companion object {
        @Provides
        @Singleton
        fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
            context.dataStore
    }
}
