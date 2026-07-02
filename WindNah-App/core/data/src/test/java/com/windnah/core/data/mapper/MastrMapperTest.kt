package com.windnah.core.data.mapper

import com.windnah.core.model.WindFarmStatus
import com.windnah.core.network.mastr.MastrWindUnitDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class MastrMapperTest {

    @Test
    fun `maStR units map to complete energy metrics`() {
        val units = listOf(
            MastrWindUnitDto(
                mastrNummer = "unit-1",
                windparkName = "Demo Park",
                gemeinde = "Prenzlau",
                bundesland = "Brandenburg",
                postleitzahl = "17291",
                breitengrad = 53.31,
                laengengrad = 13.86,
                nettonennleistungKw = 2_000.0,
                rotorDiameterM = 140.0,
                nabenhoeheM = 100.0,
                inbetriebnahmedatum = "2020-05-01",
                betriebsstatus = "InBetrieb",
                hersteller = "Enercon",
                typenbezeichnung = "E-126",
            ),
            MastrWindUnitDto(
                mastrNummer = "unit-2",
                windparkName = "Demo Park",
                gemeinde = "Prenzlau",
                bundesland = "Brandenburg",
                postleitzahl = "17291",
                breitengrad = 53.32,
                laengengrad = 13.87,
                nettonennleistungKw = 2_500.0,
                rotorDiameterM = 150.0,
                nabenhoeheM = 105.0,
                inbetriebnahmedatum = "2021-06-01",
                betriebsstatus = "InBetrieb",
                hersteller = "Vestas",
                typenbezeichnung = "V150",
            ),
        )

        val preview = units.toWindFarmPreviews().single()

        assertEquals("Demo Park", preview.windFarm.name)
        assertEquals(2, preview.windFarm.turbineCount)
        assertEquals(WindFarmStatus.IN_BETRIEB, preview.windFarm.status)
        assertEquals(9_000_000.0, preview.energyMetrics.estimatedAnnualProductionKwh, 0.0)
        assertEquals(2_571, preview.energyMetrics.householdsSupplied)
        assertEquals(3_186.0, preview.energyMetrics.co2SavingsTonnesPerYear, 0.0)
        assertNotNull(preview.energyMetrics.localEnergyContributionPercent)
        assertNotNull(preview.energyMetrics.municipalRevenueEurPerYear)
    }

    @Test
    fun `unnamed units in same municipality but far apart become separate parks`() {
        // Both in Prenzlau, but ~30 km apart and without a windpark name. The old grouping
        // (gemeinde+plz) collapsed these into one park; geo-clustering keeps them separate.
        val units = listOf(
            unnamedUnit(mastrNummer = "u1", lat = 53.31, lon = 13.86),
            unnamedUnit(mastrNummer = "u2", lat = 53.31, lon = 13.86), // same cell as u1
            unnamedUnit(mastrNummer = "u3", lat = 53.60, lon = 14.20), // far away
        )

        val previews = units.toWindFarmPreviews()

        assertEquals(2, previews.size)
    }

    @Test
    fun `detail lookup returns the same units that built the preview`() {
        val units = listOf(
            unnamedUnit(mastrNummer = "u1", lat = 53.31, lon = 13.86),
            unnamedUnit(mastrNummer = "u2", lat = 53.31, lon = 13.86),
            unnamedUnit(mastrNummer = "u3", lat = 53.60, lon = 14.20),
        )
        val preview = units.toWindFarmPreviews().first { it.windFarm.turbineCount == 2 }

        val matched = units.unitsForWindFarmId(preview.windFarm.id)

        assertEquals(2, matched.size)
        assertEquals(setOf("u1", "u2"), matched.map { it.mastrNummer }.toSet())
    }

    private fun unnamedUnit(mastrNummer: String, lat: Double, lon: Double) = MastrWindUnitDto(
        mastrNummer = mastrNummer,
        windparkName = null,
        gemeinde = "Prenzlau",
        bundesland = "Brandenburg",
        postleitzahl = "17291",
        breitengrad = lat,
        laengengrad = lon,
        nettonennleistungKw = 2_000.0,
        rotorDiameterM = 140.0,
        nabenhoeheM = 100.0,
        inbetriebnahmedatum = "2020-05-01",
        betriebsstatus = "InBetrieb",
        hersteller = "Enercon",
        typenbezeichnung = "E-126",
    )
}
