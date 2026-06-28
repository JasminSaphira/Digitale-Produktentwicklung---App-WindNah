package com.windnah.feature.windparkdetail

import com.windnah.core.designsystem.components.TransparencyInfoUiModel
import com.windnah.core.domain.usecase.WindFarmMetricTransparency
import com.windnah.core.model.WindFarmDetail
import kotlin.math.roundToInt

enum class WindFarmDetailMetric {
    CurrentOutput,
    WindSpeed,
    AnnualProduction,
    Households,
    Co2Savings,
    LocalEnergyContribution,
    MunicipalRevenue,
    NoiseEstimate,
    SizeComparison,
}

internal fun WindFarmDetailMetric.toTransparencyInfoUiModel(
    detail: WindFarmDetail,
): TransparencyInfoUiModel {
    val metrics = detail.energyMetrics
    val windFarm = detail.windFarm
    val weather = detail.weather

    return when (this) {
        WindFarmDetailMetric.CurrentOutput -> TransparencyInfoUiModel(
            title = "Aktuelle Leistung",
            value = formatMegawatts(metrics.estimatedCurrentOutputKw),
            meaning = "Zeigt, wie viel Strom der Windpark im Moment voraussichtlich erzeugt.",
            calculation = "Aus aktueller Windgeschwindigkeit, Turbinenparametern und installierter Leistung wird eine geschaetzte Leistung berechnet.",
            sources = listOf("DWD/BrightSky Wetterdaten", "MaStR Anlagendaten"),
            updateFrequency = "alle 15 Minuten",
            assumptions = WindFarmMetricTransparency.CURRENT_OUTPUT,
        )

        WindFarmDetailMetric.WindSpeed -> TransparencyInfoUiModel(
            title = "Windgeschwindigkeit",
            value = weather?.let { "${formatDecimal(it.windSpeedMs, 1)} m/s" } ?: "Keine Live-Daten",
            meaning = "Beschreibt die aktuell gemeldete Windgeschwindigkeit am Standort.",
            calculation = "Der Wert kommt aus Wetterdaten nahe des Windparks und wird fuer die aktuelle Leistungsabschaetzung verwendet.",
            sources = listOf("DWD/BrightSky Livedaten"),
            updateFrequency = "alle 15 Minuten",
            assumptions = if (weather == null) "Wenn keine Live-Daten vorliegen, zeigt die App einen Ersatztext statt eines Messwerts." else null,
        )

        WindFarmDetailMetric.AnnualProduction -> TransparencyInfoUiModel(
            title = "Jahresproduktion",
            value = "${formatGigawattHours(metrics.estimatedAnnualProductionKwh)} GWh",
            meaning = "Schaetzt, wie viel Strom der Windpark in einem durchschnittlichen Jahr erzeugen kann.",
            calculation = "Installierte Leistung mal 2.000 Volllaststunden pro Jahr.",
            sources = listOf("MaStR installierte Leistung", "WindNah Berechnungsmodell"),
            updateFrequency = "taeglich",
            assumptions = WindFarmMetricTransparency.ANNUAL_PRODUCTION,
        )

        WindFarmDetailMetric.Households -> TransparencyInfoUiModel(
            title = "Haushalte versorgt",
            value = formatNumber(metrics.householdsSupplied),
            meaning = "Uebersetzt die Jahresproduktion in eine alltagsnahe Groesse.",
            calculation = "Jahresproduktion geteilt durch 3.500 kWh durchschnittlichen Jahresverbrauch je 2-Personen-Haushalt.",
            sources = listOf("Statistisches Bundesamt Haushaltsverbrauch", "MaStR Anlagendaten"),
            updateFrequency = "taeglich",
            assumptions = WindFarmMetricTransparency.HOUSEHOLDS_SUPPLIED,
        )

        WindFarmDetailMetric.Co2Savings -> TransparencyInfoUiModel(
            title = "CO2-Einsparung",
            value = "${formatNumber(metrics.co2SavingsTonnesPerYear.roundToInt())} t",
            meaning = "Zeigt, wie viel CO2 gegenueber dem durchschnittlichen Strommix rechnerisch vermieden wird.",
            calculation = "Jahresproduktion mal Differenz aus deutschem Strommix und Windkraft-Lebenszykluswert.",
            sources = listOf("Umweltbundesamt Strommix", "Windkraft Lebenszykluswert", "WindNah Berechnungsmodell"),
            updateFrequency = "taeglich",
            assumptions = WindFarmMetricTransparency.CO2_SAVINGS,
        )

        WindFarmDetailMetric.LocalEnergyContribution -> TransparencyInfoUiModel(
            title = "Lokaler Energiebeitrag",
            value = metrics.localEnergyContributionPercent?.let { "${it.roundToInt()} %" } ?: "Nicht verfuegbar",
            meaning = "Ordnet die Windpark-Erzeugung grob im Verhaeltnis zum lokalen Strombedarf ein.",
            calculation = "Geschaetzte Jahresproduktion wird mit einem modellierten kommunalen Verbrauch verglichen.",
            sources = listOf("MaStR Anlagendaten", "WindNah Gemeinde-Verbrauchsmodell"),
            updateFrequency = "monatlich",
            assumptions = WindFarmMetricTransparency.LOCAL_ENERGY,
        )

        WindFarmDetailMetric.MunicipalRevenue -> TransparencyInfoUiModel(
            title = "Kommunale Einnahmen",
            value = metrics.municipalRevenueEurPerYear?.let { "${formatNumber(it.roundToInt())} EUR" } ?: "Nicht verfuegbar",
            meaning = "Schaetzt, welcher finanzielle Beitrag fuer die Kommune pro Jahr entstehen kann.",
            calculation = "Jahresproduktion mal EEG-Paragraf-6-Richtwert von 0,2 ct/kWh.",
            sources = listOf("EEG Paragraf 6 Richtwert", "WindNah Berechnungsmodell"),
            updateFrequency = "monatlich",
            assumptions = WindFarmMetricTransparency.MUNICIPAL_REVENUE,
        )

        WindFarmDetailMetric.NoiseEstimate -> TransparencyInfoUiModel(
            title = "Laermschaetzung",
            value = metrics.estimatedNoiseLevelDbA?.let { "${it.roundToInt()} dB(A)" } ?: "Nicht verfuegbar",
            meaning = "Gibt eine bildungsorientierte Einschaetzung der Schallimmission in Referenzentfernung.",
            calculation = "Aus Turbinenanzahl, Rotordurchmesser, Auslastung, Wind und Entfernung wird ein grober dB(A)-Wert abgeleitet.",
            sources = listOf("MaStR Turbinendaten", "DWD/BrightSky Wetterdaten", "WindNah Schallmodell"),
            updateFrequency = "alle 15 Minuten",
            assumptions = metrics.estimatedNoiseLevelDbA?.let { WindFarmMetricTransparency.NOISE }
                ?: "Ohne aktive Turbinen oder Wetterdaten kann keine Laermschaetzung berechnet werden.",
        )

        WindFarmDetailMetric.SizeComparison -> TransparencyInfoUiModel(
            title = "Groessenvergleich",
            value = averageComparisonHeight(detail)?.let { "ca. ${it.roundToInt()} m" } ?: "Nicht verfuegbar",
            meaning = "Vergleicht die Hoehe der Windraeder mit bekannten Bauwerken.",
            calculation = "Wenn vorhanden: Nabenhoehe plus halber Rotordurchmesser. Sonst wird mit verfuegbaren Teilwerten gearbeitet.",
            sources = listOf("MaStR Turbinenstammdaten", "Referenzhoehen bekannter Bauwerke"),
            updateFrequency = "bei Aktualisierung der Anlagendaten",
            assumptions = "Fehlende Nabenhoehen oder Rotordurchmesser fuehren zu Naeherungswerten.",
        )
    }
}

private fun averageComparisonHeight(detail: WindFarmDetail): Double? {
    val heights = detail.turbines.mapNotNull { turbine ->
        val hubHeight = turbine.hubHeightM
        val rotorDiameter = turbine.rotorDiameterM
        when {
            hubHeight != null && rotorDiameter != null -> hubHeight + rotorDiameter / 2.0
            hubHeight != null -> hubHeight
            rotorDiameter != null -> rotorDiameter / 2.0
            else -> null
        }
    }

    return heights.takeIf { it.isNotEmpty() }?.average()
}

private fun formatMegawatts(kw: Double): String =
    "${formatDecimal(kw / 1_000.0, 1)} MW"

private fun formatGigawattHours(kwh: Double): String =
    formatDecimal(kwh / 1_000_000.0, 1)

private fun formatDecimal(value: Double, decimals: Int): String =
    String.format("%.${decimals}f", value).replace('.', ',')

private fun formatNumber(value: Int): String =
    String.format("%,d", value).replace(',', '.')
