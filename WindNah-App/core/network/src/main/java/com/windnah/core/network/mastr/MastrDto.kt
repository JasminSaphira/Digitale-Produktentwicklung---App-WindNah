package com.windnah.core.network.mastr

data class MastrWindUnitDto(
    val mastrNummer: String,
    val windparkName: String? = null,
    val gemeinde: String? = null,
    val bundesland: String? = null,
    val postleitzahl: String? = null,
    val breitengrad: Double? = null,
    val laengengrad: Double? = null,
    val nettonennleistungKw: Double? = null,
    val rotorDiameterM: Double? = null,
    val nabenhoeheM: Double? = null,
    val inbetriebnahmedatum: String? = null,
    val betriebsstatus: String? = null,
    val hersteller: String? = null,
    val typenbezeichnung: String? = null,
)
