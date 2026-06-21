package com.windnah.core.network.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.windnah.core.network.dwd.DwdApiService
import com.windnah.core.network.mastr.MastrApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BASIC
                }
            )
            .build()

    @Provides
    @Singleton
    @Named("mastr")
    fun provideMastrRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(MastrApiService.BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

    @Provides
    @Singleton
    @Named("dwd")
    fun provideDwdRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(DwdApiService.BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

    @Provides
    @Singleton
    fun provideMastrApiService(@Named("mastr") retrofit: Retrofit): MastrApiService =
        retrofit.create(MastrApiService::class.java)

    @Provides
    @Singleton
    fun provideDwdApiService(@Named("dwd") retrofit: Retrofit): DwdApiService =
        retrofit.create(DwdApiService::class.java)
}
