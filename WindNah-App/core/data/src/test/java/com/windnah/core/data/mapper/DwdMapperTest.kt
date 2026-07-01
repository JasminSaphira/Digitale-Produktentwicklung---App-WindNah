package com.windnah.core.data.mapper

import com.windnah.core.network.dwd.BrightSkyWeatherDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DwdMapperTest {

    @Test
    fun `missing wind speed does not create weather data`() {
        val weather = BrightSkyWeatherDto(
            windSpeedMs = null,
            windDirectionDeg = 240.0,
        ).toWeatherData()

        assertNull(weather)
    }

    @Test
    fun `maps wind speed when present`() {
        val weather = BrightSkyWeatherDto(
            windSpeedMs = 8.0,
            windDirectionDeg = 240.0,
        ).toWeatherData()

        requireNotNull(weather)
        assertEquals(8.0, weather.windSpeedMs, 0.0)
        assertEquals(240, weather.windDirectionDeg)
    }
}
