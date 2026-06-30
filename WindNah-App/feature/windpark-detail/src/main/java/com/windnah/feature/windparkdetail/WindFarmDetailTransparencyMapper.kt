package com.windnah.feature.windparkdetail

import com.windnah.core.designsystem.components.TransparencyInfoUiModel
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
            meaning = "Wie viel Strom der Windpark gerade ungefaehr erzeugt - in Echtzeit.",
            calculation = "Die App kennt die Windgeschwindigkeit am Standort und weiss, wie viele Turbinen gerade aktiv sind. Daraus wird grob berechnet, wie viel Strom die Anlagen bei diesem Wind typischerweise liefern.",
            dataUsed = "Live-Wetterdaten von DWD/BrightSky und Anlagendaten aus dem Marktstammdatenregister (MaStR).",
            sources = listOf("DWD/BrightSky Live-Wetterdaten", "MaStR Anlagendaten"),
        )

        WindFarmDetailMetric.WindSpeed -> TransparencyInfoUiModel(
            title = "Windgeschwindigkeit",
            value = weather?.let { "${formatDecimal(it.windSpeedMs, 1)} m/s" } ?: "Keine Live-Daten",
            meaning = "Wie stark der Wind gerade am Standort des Windparks weht.",
            calculation = "Der Wert wird direkt von der naechstgelegenen Wetterstation abgerufen - ohne Umrechnung oder Schaetzung. Er ist gleichzeitig die Grundlage fuer die Leistungsberechnung.",
            dataUsed = "Live-Wetterdaten von DWD/BrightSky.",
            sources = listOf("DWD/BrightSky Live-Wetterdaten"),
        )

        WindFarmDetailMetric.AnnualProduction -> TransparencyInfoUiModel(
            title = "Jahresproduktion",
            value = "${formatGigawattHours(metrics.estimatedAnnualProductionKwh)} GWh",
            meaning = "Wie viel Strom der Windpark in einem durchschnittlichen Jahr voraussichtlich erzeugt.",
            calculation = "In Deutschland laufen Windraeder im Modell rund 2.000 Stunden pro Jahr auf voller Leistung. Die installierte Gesamtleistung des Parks wird mit diesem Wert multipliziert.",
            dataUsed = "Installierte Leistung aus MaStR und Berechnungsmodell von WindNah.",
            sources = listOf("MaStR installierte Leistung", "WindNah Berechnungsmodell"),
        )

        WindFarmDetailMetric.Households -> TransparencyInfoUiModel(
            title = "Haushalte versorgt",
            value = formatNumber(metrics.householdsSupplied),
            meaning = "Wie viele Haushalte der Windpark mit seinem Jahresstrom theoretisch versorgen koennte - als greifbare Alltagszahl.",
            calculation = "Ein typischer 2-Personen-Haushalt verbraucht rund 3.500 kWh Strom pro Jahr. Die Jahresproduktion des Parks wird durch diesen Wert geteilt.",
            dataUsed = "Berechnete Jahresproduktion und Verbrauchsdaten des Statistischen Bundesamts.",
            sources = listOf("Berechnete Jahresproduktion", "Statistisches Bundesamt Haushaltsverbrauch"),
        )

        WindFarmDetailMetric.Co2Savings -> TransparencyInfoUiModel(
            title = "CO2-Einsparung",
            value = "${formatNumber(metrics.co2SavingsTonnesPerYear.roundToInt())} t",
            meaning = "Wie viel CO2 rechnerisch eingespart wird - verglichen mit Strom aus dem normalen Stromnetz.",
            calculation = "Die Differenz zwischen durchschnittlichem Netzstrom und Windstrom wird mit der Jahresproduktion multipliziert. So entsteht die rechnerisch eingesparte CO2-Menge.",
            dataUsed = "Berechnete Jahresproduktion sowie Emissionsfaktoren fuer Strommix und Windenergie vom Umweltbundesamt.",
            sources = listOf("Berechnete Jahresproduktion", "Umweltbundesamt Emissionsfaktoren", "WindNah Berechnungsmodell"),
        )

        WindFarmDetailMetric.LocalEnergyContribution -> TransparencyInfoUiModel(
            title = "Lokaler Energiebeitrag",
            value = metrics.localEnergyContributionPercent?.let { "${it.roundToInt()} %" } ?: "Nicht verfuegbar",
            meaning = "Wie viel des lokalen Strombedarfs der Windpark rechnerisch abdecken koennte - als grobe Einordnung fuer die Region.",
            calculation = "Die geschaetzte Jahresproduktion des Windparks wird mit einem modellierten Strombedarf der umliegenden Kommune verglichen. Das ist eine Naeherung: Der Strom fliesst ins allgemeine Netz, nicht direkt in die Gemeinde.",
            dataUsed = "Anlagendaten aus MaStR und ein vereinfachtes kommunales Verbrauchsmodell von WindNah.",
            sources = listOf("MaStR Anlagendaten", "WindNah Gemeinde-Verbrauchsmodell"),
        )

        WindFarmDetailMetric.MunicipalRevenue -> TransparencyInfoUiModel(
            title = "Kommunale Einnahmen",
            value = metrics.municipalRevenueEurPerYear?.let { "${formatNumber(it.roundToInt())} EUR" } ?: "Nicht verfuegbar",
            meaning = "Wie viel Geld die Gemeinde durch den Windpark jaehrlich einnehmen koennte - als grobe Orientierung.",
            calculation = "Kommunen koennen nach EEG an Windparks auf ihrem Gebiet beteiligt werden. Die Jahresproduktion des Parks wird mit dem gesetzlichen Richtwert pro Kilowattstunde multipliziert.",
            dataUsed = "Berechnete Jahresproduktion und kommunaler EEG-Beteiligungswert nach Paragraph 6 EEG 2023.",
            sources = listOf("Berechnete Jahresproduktion", "EEG Paragraph 6 Beteiligungswert", "WindNah Berechnungsmodell"),
        )

        WindFarmDetailMetric.NoiseEstimate -> TransparencyInfoUiModel(
            title = "Laermschaetzung",
            value = metrics.estimatedNoiseLevelDbA?.let { "${it.roundToInt()} dB(A)" } ?: "Nicht verfuegbar",
            meaning = "Wie laut die Windraeder am gewaehlten Standort ungefaehr wahrnehmbar sein koennten.",
            calculation = "Die App berechnet einen Naeherungswert aus Abstand, aktueller Auslastung und typischen Schallwerten fuer diesen Anlagentyp. Der Wert ist keine offizielle Laermmessung oder behoerdliche Bewertung.",
            dataUsed = "Anlagendaten aus MaStR und vereinfachte Annahmen zur Schallausbreitung.",
            sources = listOf("MaStR Anlagendaten", "WindNah Schallmodell"),
        )

        WindFarmDetailMetric.SizeComparison -> TransparencyInfoUiModel(
            title = "Groessenvergleich",
            value = averageComparisonHeight(detail)?.let { "ca. ${it.roundToInt()} m" } ?: "Nicht verfuegbar",
            meaning = "Wie hoch die Windraeder wirklich sind - verglichen mit bekannten Bauwerken.",
            calculation = "Die App nutzt Nabenhoehe und Rotordurchmesser aus den technischen Anlagendaten. Daraus ergibt sich die Gesamthoehe von der Bodenplatte bis zur Rotorspitze. Fehlen einzelne Werte, wird mit den vorhandenen Angaben gerechnet.",
            dataUsed = "Nabenhoehe und Rotordurchmesser aus den Turbinendaten im MaStR.",
            sources = listOf("MaStR Turbinenstammdaten", "Referenzhoehen bekannter Bauwerke"),
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
