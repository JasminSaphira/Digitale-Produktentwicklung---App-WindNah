package com.windnah.feature.myturbines

import com.windnah.core.model.EnergyMetrics
import com.windnah.core.model.WindFarm
import com.windnah.core.model.WindFarmPreview
import com.windnah.core.model.WindFarmStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MyTurbinesMappersTest {

    @Test
    fun `maps preview to ui model`() {
        val preview = samplePreview()

        val item = preview.toMyTurbinesWindFarmItemUiModel()

        assertEquals("wf-1", item.id)
        assertEquals("Windpark Alpha", item.name)
        assertEquals("Musterstadt, Niedersachsen", item.location)
        assertEquals("In Betrieb", item.statusLabel)
        assertEquals(MyTurbinesStatusUiModel.InOperation, item.status)
        assertEquals("1,5 kt/a", item.co2Savings)
        assertEquals("1,4k", item.households)
        assertEquals("2,5 MW", item.capacity)
    }

    @Test
    fun `maps turbine state from ids and previews`() {
        val preview = samplePreview()

        val state = mapMyTurbinesUiState(
            favoriteIds = listOf("wf-1", "missing"),
            recentlyViewedIds = listOf("wf-1"),
            previews = listOf(preview),
        )

        assertEquals(1, state.favorites.size)
        assertEquals(1, state.recentlyViewed.size)
        assertEquals("wf-1", state.favorites.single().id)
        assertEquals("wf-1", state.recentlyViewed.single().id)
    }

    @Test
    fun `formats compact values`() {
        assertEquals("999", formatCompactNumber(999))
        assertEquals("1,0k", formatCompactNumber(1_000))
        assertEquals("2,5k", formatCompactNumber(2_500))
        assertEquals("1,2", formatKilotonnes(1_234.0))
        assertEquals("3,4 MW", formatMegawatts(3_450.0))
    }

    @Test
    fun `maps all wind farm statuses`() {
        assertEquals(MyTurbinesStatusUiModel.InOperation, WindFarmStatus.IN_BETRIEB.toMyTurbinesStatusUiModel())
        assertEquals(MyTurbinesStatusUiModel.Maintenance, WindFarmStatus.IN_WARTUNG.toMyTurbinesStatusUiModel())
        assertEquals(MyTurbinesStatusUiModel.Planned, WindFarmStatus.IN_PLANUNG.toMyTurbinesStatusUiModel())
        assertEquals(MyTurbinesStatusUiModel.Decommissioned, WindFarmStatus.STILLGELEGT.toMyTurbinesStatusUiModel())
    }

    @Test
    fun `formats wind farm status labels`() {
        assertEquals("In Betrieb", WindFarmStatus.IN_BETRIEB.label)
        assertEquals("In Wartung", WindFarmStatus.IN_WARTUNG.label)
        assertEquals("In Planung", WindFarmStatus.IN_PLANUNG.label)
        assertEquals("Stillgelegt", WindFarmStatus.STILLGELEGT.label)
    }

    @Test
    fun `exposes loading and error state defaults`() {
        val state = MyTurbinesUiState()

        assertFalse(state.isLoading)
        assertTrue(state.favorites.isEmpty())
        assertTrue(state.recentlyViewed.isEmpty())
        assertEquals(null, state.errorMessage)
    }

    private fun samplePreview(): WindFarmPreview =
        WindFarmPreview(
            windFarm = WindFarm(
                id = "wf-1",
                name = "Windpark Alpha",
                municipality = "Musterstadt",
                federalState = "Niedersachsen",
                latitude = 0.0,
                longitude = 0.0,
                status = WindFarmStatus.IN_BETRIEB,
                turbineCount = 6,
                totalCapacityKw = 2_500.0,
                commissioningYear = 2020,
            ),
            energyMetrics = EnergyMetrics(
                householdsSupplied = 1_400,
                co2SavingsTonnesPerYear = 1_500.0,
            ),
        )
}
