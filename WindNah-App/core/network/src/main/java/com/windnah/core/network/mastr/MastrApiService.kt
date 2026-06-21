package com.windnah.core.network.mastr

import retrofit2.http.GET
import retrofit2.http.Query

interface MastrApiService {

    @GET("EinheitWind")
    suspend fun getWindUnits(
        @Query("\$select") select: String = WIND_UNIT_FIELDS,
        @Query("\$filter") filter: String? = null,
        @Query("\$top") top: Int = 100,
        @Query("\$skip") skip: Int = 0,
        @Query("\$orderby") orderby: String = "NameWindpark asc",
    ): MastrWindUnitResponse

    companion object {
        const val BASE_URL = "https://www.marktstammdatenregister.de/MaStRAPI/wapi/mastr/"

        // Only fetch fields we actually use to minimize payload
        const val WIND_UNIT_FIELDS = "EinheitMastrNummer,NameWindpark,Gemeinde,Bundesland," +
            "Postleitzahl,Breitengrad,Laengengrad,Nettonennleistung,RotorblattlaengeM," +
            "Nabenhoehe,Inbetriebnahmedatum,EinheitBetriebsstatus,Hersteller,Typenbezeichnung"

        // Filter: only wind turbines with coordinates
        const val FILTER_ACTIVE = "EinheitBetriebsstatus ne null and Breitengrad ne null and Laengengrad ne null"
    }
}
