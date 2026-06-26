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
                rotorblattlaengeM = 70.0,
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
                rotorblattlaengeM = 75.0,
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
}
