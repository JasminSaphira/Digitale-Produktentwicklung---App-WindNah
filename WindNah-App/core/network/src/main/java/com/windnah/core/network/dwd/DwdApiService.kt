package com.windnah.core.network.dwd

import retrofit2.http.GET
import retrofit2.http.Query

// BrightSky is a free DWD-backed open weather API (https://brightsky.dev)
interface DwdApiService {

    @GET("current_weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
    ): BrightSkyCurrentWeatherResponse

    companion object {
        const val BASE_URL = "https://api.brightsky.dev/"
    }
}
