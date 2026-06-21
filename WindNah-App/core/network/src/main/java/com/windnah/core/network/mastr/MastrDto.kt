package com.windnah.core.network.mastr

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MastrWindUnitResponse(
    @SerialName("value") val value: List<MastrWindUnitDto> = emptyList(),
    @SerialName("@odata.nextLink") val nextLink: String? = null,
)

@Serializable
data class MastrWindUnitDto(
    @SerialName("EinheitMastrNummer") val mastrNummer: String,
    @SerialName("NameWindpark") val windparkName: String? = null,
    @SerialName("Gemeinde") val gemeinde: String? = null,
    @SerialName("Bundesland") val bundesland: String? = null,
    @SerialName("Postleitzahl") val postleitzahl: String? = null,
    @SerialName("Breitengrad") val breitengrad: Double? = null,
    @SerialName("Laengengrad") val laengengrad: Double? = null,
    @SerialName("Nettonennleistung") val nettonennleistungKw: Double? = null,
    @SerialName("RotorblattlaengeM") val rotorblattlaengeM: Double? = null,
    @SerialName("Nabenhoehe") val nabenhoeheM: Double? = null,
    @SerialName("Inbetriebnahmedatum") val inbetriebnahmedatum: String? = null,
    @SerialName("EinheitBetriebsstatus") val betriebsstatus: String? = null,
    @SerialName("AnlagenbetreiberMastrNummer") val betreiberMastrNummer: String? = null,
    @SerialName("Hersteller") val hersteller: String? = null,
    @SerialName("Typenbezeichnung") val typenbezeichnung: String? = null,
)
