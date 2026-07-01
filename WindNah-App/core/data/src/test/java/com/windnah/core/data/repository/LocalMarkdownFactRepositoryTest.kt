package com.windnah.core.data.repository

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class LocalMarkdownFactRepositoryTest {

    @Test
    fun `parseFactSources parses markdown source links`() {
        val sources = parseFactSources("[UBA, 2023](https://example.org)")

        assertEquals(1, sources.size)
        assertEquals("UBA, 2023", sources.first().label)
        assertEquals("https://example.org", sources.first().url)
    }

    @Test
    fun `parseFactSources keeps plain text sources without url`() {
        val sources = parseFactSources("UBA, 2023")

        assertEquals(1, sources.size)
        assertEquals("UBA, 2023", sources.first().label)
        assertNull(sources.first().url)
    }

    @Test
    fun `parseFactSources supports mixed source separators`() {
        val sources = parseFactSources(
            "EEG §6 | [Umweltbundesamt, 2023](https://example.org); Fraunhofer ISE, 2024",
        )

        assertEquals(3, sources.size)
        assertEquals("EEG §6", sources[0].label)
        assertNull(sources[0].url)
        assertEquals("Umweltbundesamt, 2023", sources[1].label)
        assertEquals("https://example.org", sources[1].url)
        assertEquals("Fraunhofer ISE, 2024", sources[2].label)
        assertNull(sources[2].url)
    }
}
