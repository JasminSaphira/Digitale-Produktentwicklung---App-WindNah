package com.windnah.feature.windparkdetail

import com.windnah.core.common.format.formatDecimal
import com.windnah.core.common.format.formatGigawattHours
import com.windnah.core.common.format.formatInt
import com.windnah.core.common.format.formatMegawatts
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
    val weather = detail.weather

    return when (this) {
        WindFarmDetailMetric.CurrentOutput -> TransparencyInfoUiModel(
            title = "Aktuelle Leistung",
            value = weather?.let { formatMegawatts(metrics.estimatedCurrentOutputKw) } ?: "Keine Live-Daten",
            meaning = "Wie viel Strom der Windpark gerade ungefähr erzeugt – in Echtzeit.",
            calculation = "Die App kennt die Windgeschwindigkeit am Standort und weiß, wie viele Turbinen gerade aktiv sind. Daraus wird grob berechnet, wie viel Strom die Anlagen bei diesem Wind typischerweise liefern.",
            sources = listOf(
                "Live-Wetterdaten (DWD / BrightSky)",
                "Anlagendaten aus dem Marktstammdatenregister (MaStR)",
            ),
        )

        WindFarmDetailMetric.WindSpeed -> TransparencyInfoUiModel(
            title = "Windgeschwindigkeit",
            value = weather?.let { "${formatDecimal(it.windSpeedMs, 1)} m/s" } ?: "Keine Live-Daten",
            meaning = "Wie stark der Wind gerade am Standort des Windparks weht.",
            calculation = "Der Wert wird direkt von der nächstgelegenen Wetterstation abgerufen – ohne Umrechnung oder Schätzung. Er ist gleichzeitig die Grundlage für die Leistungsberechnung.",
            sources = listOf("Live-Wetterdaten (DWD / BrightSky)"),
        )

        WindFarmDetailMetric.AnnualProduction -> TransparencyInfoUiModel(
            title = "Jahresproduktion",
            value = "${formatGigawattHours(metrics.estimatedAnnualProductionKwh)} GWh",
            meaning = "Wie viel Strom der Windpark in einem durchschnittlichen Jahr voraussichtlich erzeugt.",
            calculation = "In Deutschland laufen Windräder im Schnitt rund 2.000 Stunden pro Jahr auf voller Leistung. Die installierte Gesamtleistung des Parks wird einfach mit diesem Wert multipliziert.",
            example = "Ein Park mit 10 MW × 2.000 Stunden = 20.000 MWh pro Jahr",
            sources = listOf(
                "Installierte Leistung aus MaStR",
                "Berechnungsmodell von WindNah",
            ),
        )

        WindFarmDetailMetric.Households -> TransparencyInfoUiModel(
            title = "Haushalte versorgt",
            value = formatInt(metrics.householdsSupplied),
            meaning = "Wie viele Haushalte der Windpark mit seinem Jahresstrom theoretisch versorgen könnte – als greifbare Alltagszahl.",
            calculation = "Ein typischer 2-Personen-Haushalt verbraucht laut Statistischem Bundesamt rund 3.500 kWh Strom pro Jahr. Die Jahresproduktion des Parks wird durch diesen Wert geteilt.",
            example = "20.000 MWh ÷ 3,5 MWh = ca. 5.700 Haushalte",
            sources = listOf(
                "Berechnete Jahresproduktion",
                "Verbrauchsdaten Statistisches Bundesamt (Stand 2023)",
            ),
        )

        WindFarmDetailMetric.Co2Savings -> TransparencyInfoUiModel(
            title = "CO₂-Einsparung",
            value = "${formatInt(metrics.co2SavingsTonnesPerYear.roundToInt())} t",
            meaning = "Wie viel CO₂ rechnerisch eingespart wird – verglichen mit dem Strom, der sonst aus dem normalen Stromnetz käme.",
            calculation = "Normaler Netzstrom kommt zu einem großen Teil aus Gas- und Kohlekraftwerken und erzeugt dabei CO₂. Windstrom dagegen so gut wie keins. Die Differenz zwischen beiden Werten wird mit der Jahresproduktion multipliziert – so entsteht die eingesparte CO₂-Menge.",
            example = "Netzstrom erzeugt ~363 g CO₂ pro kWh, Windstrom ~9 g → Ersparnis von ~354 g pro kWh × Jahresproduktion",
            sources = listOf(
                "Berechnete Jahresproduktion",
                "Emissionsfaktoren für Strom-Mix und Windenergie vom Umweltbundesamt",
            ),
        )

        WindFarmDetailMetric.LocalEnergyContribution -> TransparencyInfoUiModel(
            title = "Lokaler Energiebeitrag",
            value = metrics.localEnergyContributionPercent?.let { "${it.roundToInt()} %" } ?: "Nicht verfügbar",
            meaning = "Wie viel des lokalen Strombedarfs der Windpark rechnerisch abdecken könnte – als grobe Einordnung für die Region.",
            calculation = "Die geschätzte Jahresproduktion des Windparks wird mit dem modellierten Strombedarf der umliegenden Kommune verglichen. So entsteht ein Prozentwert, der zeigt: Welchen Anteil könnte dieser Park theoretisch beisteuern?\n\nDies ist eine Näherung – der Strom fließt ins allgemeine Netz, nicht direkt in die Gemeinde.",
            sources = listOf(
                "Anlagendaten aus MaStR",
                "Vereinfachtes kommunales Verbrauchsmodell von WindNah",
            ),
        )

        WindFarmDetailMetric.MunicipalRevenue -> TransparencyInfoUiModel(
            title = "Kommunale Einnahmen",
            value = metrics.municipalRevenueEurPerYear?.let { "${formatInt(it.roundToInt())} €" } ?: "Nicht verfügbar",
            meaning = "Wie viel Geld die Gemeinde durch den Windpark jährlich einnehmen könnte – als grobe Orientierung.",
            calculation = "Seit 2023 dürfen Kommunen per Gesetz (EEG) an Windparks auf ihrem Gebiet finanziell beteiligt werden. Die Jahresproduktion des Parks wird mit dem gesetzlich festgelegten Richtwert pro Kilowattstunde multipliziert.",
            example = "20.000 MWh × 0,2 ct/kWh = ca. 40.000 € pro Jahr für die Kommune",
            sources = listOf(
                "Berechnete Jahresproduktion",
                "Kommunaler EEG-Beteiligungswert (§ 6 EEG 2023)",
            ),
        )

        WindFarmDetailMetric.NoiseEstimate -> TransparencyInfoUiModel(
            title = "Lärmschätzung",
            value = metrics.estimatedNoiseLevelDbA?.let { "${it.roundToInt()} dB(A)" } ?: "Nicht verfügbar",
            meaning = "Wie laut die Windräder am gewählten Standort ungefähr wahrnehmbar sein könnten.",
            calculation = "Die App berechnet einen Näherungswert aus dem Abstand zum Windrad, der aktuellen Auslastung und typischen Schallwerten für diesen Anlagentyp. Je weiter weg, desto leiser – ähnlich wie bei jeder anderen Schallquelle.\n\nDies ist keine offizielle Lärmmessung oder behördliche Bewertung – sondern eine grobe technische Schätzung zur Orientierung.",
            sources = listOf(
                "Anlagendaten aus MaStR",
                "Vereinfachte Annahmen zur Schallausbreitung",
            ),
        )

        WindFarmDetailMetric.SizeComparison -> TransparencyInfoUiModel(
            title = "Größenvergleich",
            value = averageComparisonHeight(detail)?.let { "ca. ${it.roundToInt()} m" } ?: "Nicht verfügbar",
            meaning = "Wie hoch die Windräder wirklich sind – verglichen mit bekannten Bauwerken, die man sich besser vorstellen kann.",
            calculation = "Die App nutzt die Nabenhöhe (Mittelpunkt des Rotors) und den Rotordurchmesser aus den technischen Anlagendaten. Daraus ergibt sich die Gesamthöhe – also von der Bodenplatte bis zur Rotorspitze. Fehlen einzelne Werte, wird mit den vorhandenen Angaben gerechnet.",
            example = "Nabenhöhe 140 m + halber Rotor 80 m = 220 m Gesamthöhe",
            sources = listOf("Nabenhöhe & Rotordurchmesser aus den Turbinendaten (MaStR)"),
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
