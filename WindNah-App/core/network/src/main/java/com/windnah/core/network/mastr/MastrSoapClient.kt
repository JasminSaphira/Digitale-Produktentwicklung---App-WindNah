package com.windnah.core.network.mastr

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory

private const val SOAP_ENDPOINT_ANLAGE =
    "https://www.marktstammdatenregister.de/MaStRApi/Api.svc/Soap11/Anlage"
private const val NS_M = "https://www.marktstammdatenregister.de/Services/Public/1_2/Modelle"
private const val NS_A =
    "https://www.marktstammdatenregister.de/Services/Public/1_2/Modelle/Anlage"
private const val NS_ENV = "http://schemas.xmlsoap.org/soap/envelope/"

internal const val MASTR_API_KEY =
    "x7uopppDQpagcZgOHGyWKzjVjR5blJYxZgcjqkXxE5wl6uYFqMv3iYWeTDoceqPa/" +
    "V21xMzwb4PcxPj+u24JWbWHvN3ycsWBYe/YRvlB/SYaj6Pj79FWkAp0LIZnrdgFi8ITjmBwJ9XKLwjibsuVkeDWjwfs8n" +
    "tY3qyxN6h64VytW1+AqFk0g9JpGouAyDNP+bin6pvwuYopMrNonygvnBuA+esxDSXRVfCcLLjLAn+9tl0u2O8JITLwZtW+" +
    "Gw21sOGAt9ScuBWA0t1ituv32WZkHHlpvkzlLoyrulCkxI+j9Vk5fdMCxkDkvl8xvdaJagXCK2HLTvbRYslAeq6xt4aBiC" +
    "j1kxUbfmP5Y5Mzz/2POGsFIpJ5kdW1BqkdZj+hESauayHT2o/h3vzpmyTJYX/pYcKLVT2GOzRnVPrr/UfCgb5cDR+AwoKC" +
    "IDjX3RrutDdtNy/aY1cE3fW7ICpcOzvTrjFSOWiUwL+q8SNwKt2VKDmR/p5g+NsfsOwOP3RcF1Ab0CzrNsj1qgn3H0h6kl6" +
    "WbJE="
internal const val MASTR_MARKTAKTEUR = "SOM961179242694"

private const val MAX_LIST_PAGES = 1
private const val MAX_DETAIL_REQUESTS = 100

class MastrSoapClient(private val okHttpClient: OkHttpClient) {

    /**
     * Fetches wind turbines via two-step SOAP:
     * 1. GetGefilterteListeStromErzeuger(Wind) — paginates to collect EinheitMastrNummern
     * 2. GetEinheitWind per number — fetches full data (coords, park name, specs)
     */
    fun getWindEinheiten(): List<MastrWindUnitDto> {
        val mastrNummern = fetchWindEinheitNummern().take(MAX_DETAIL_REQUESTS)
        return runBlocking(Dispatchers.IO) {
            mastrNummern
                .chunked(20)
                .flatMap { chunk ->
                    chunk.map { async { fetchEinheitWind(it) } }.awaitAll()
                }
                .filterNotNull()
        }
    }

    private fun fetchWindEinheitNummern(): List<String> {
        val result = mutableListOf<String>()
        var startAb = 1
        var page = 0
        do {
            val response = executeListRequest(startAb)
            result.addAll(response.mastrNummern)
            startAb += response.mastrNummern.size
            page++
        } while (response.hasMore && page < MAX_LIST_PAGES)
        return result
    }

    private fun fetchEinheitWind(einheitMastrNummer: String): MastrWindUnitDto? {
        val body = """
            <?xml version="1.0" encoding="utf-8"?>
            <s:Envelope xmlns:s="$NS_ENV" xmlns:a="$NS_A" xmlns:m="$NS_M">
              <s:Body>
                <a:GetEinheitWindRequest>
                  <m:apiKey>$MASTR_API_KEY</m:apiKey>
                  <m:marktakteurMastrNummer>$MASTR_MARKTAKTEUR</m:marktakteurMastrNummer>
                  <m:einheitMastrNummer>$einheitMastrNummer</m:einheitMastrNummer>
                </a:GetEinheitWindRequest>
              </s:Body>
            </s:Envelope>
        """.trimIndent()

        val xml = executeSoap(body, "GetEinheitWind")
        return parseEinheitWindResponse(xml, einheitMastrNummer)
    }

    private fun executeListRequest(startAb: Int): ListResponse {
        val body = """
            <?xml version="1.0" encoding="utf-8"?>
            <s:Envelope xmlns:s="$NS_ENV" xmlns:a="$NS_A" xmlns:m="$NS_M">
              <s:Body>
                <a:GetGefilterteListeStromErzeugerRequest>
                  <m:apiKey>$MASTR_API_KEY</m:apiKey>
                  <m:marktakteurMastrNummer>$MASTR_MARKTAKTEUR</m:marktakteurMastrNummer>
                  <m:startAb>$startAb</m:startAb>
                  <m:energietraeger>Wind</m:energietraeger>
                </a:GetGefilterteListeStromErzeugerRequest>
              </s:Body>
            </s:Envelope>
        """.trimIndent()

        val xml = executeSoap(body, "GetGefilterteListeStromErzeuger")
        return parseListResponse(xml)
    }

    private fun executeSoap(soapBody: String, action: String): String {
        val requestBody = soapBody.toRequestBody("text/xml; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url(SOAP_ENDPOINT_ANLAGE)
            .post(requestBody)
            .header("SOAPAction", action)
            .header("Content-Type", "text/xml; charset=utf-8")
            .build()
        val response = okHttpClient.newCall(request).execute()
        return response.body?.string() ?: throw IllegalStateException("Empty SOAP response for $action")
    }

    private fun parseListResponse(xml: String): ListResponse {
        val parser = newParser(xml)
        val nummern = mutableListOf<String>()
        var ergebniscode: String? = null
        var currentTag: String? = null

        forEachEvent(parser) { eventType ->
            when (eventType) {
                XmlPullParser.START_TAG -> currentTag = parser.localName()
                XmlPullParser.TEXT -> {
                    val text = parser.text?.trim() ?: return@forEachEvent
                    if (text.isEmpty()) return@forEachEvent
                    when (currentTag) {
                        "Ergebniscode" -> ergebniscode = text
                        "EinheitMastrNummer" -> nummern.add(text)
                    }
                }
                XmlPullParser.END_TAG -> currentTag = null
            }
        }
        return ListResponse(nummern, ergebniscode == "OkWeitereDatenVorhanden")
    }

    private fun parseEinheitWindResponse(xml: String, fallbackNummer: String): MastrWindUnitDto? {
        val parser = newParser(xml)
        val fields = mutableMapOf<String, String>()
        var currentTag: String? = null
        var inHersteller = false

        forEachEvent(parser) { eventType ->
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    val tag = parser.localName()
                    inHersteller = inHersteller || tag == "Hersteller"
                    currentTag = tag
                }
                XmlPullParser.TEXT -> {
                    val text = parser.text?.trim() ?: return@forEachEvent
                    if (text.isEmpty() || currentTag == null) return@forEachEvent
                    val tag = currentTag!!
                    // Inside <Hersteller><Wert>NAME</Wert></Hersteller>
                    if (inHersteller && tag == "Wert") {
                        fields["Hersteller"] = text
                    } else if (!inHersteller) {
                        fields[tag] = text
                    }
                }
                XmlPullParser.END_TAG -> {
                    if (parser.localName() == "Hersteller") inHersteller = false
                    currentTag = null
                }
            }
        }

        val ergebniscode = fields["Ergebniscode"] ?: return null
        if (ergebniscode != "OK") return null

        return MastrWindUnitDto(
            mastrNummer = fields["EinheitMastrNummer"] ?: fallbackNummer,
            windparkName = fields["NameWindpark"],
            gemeinde = fields["Gemeinde"] ?: fields["Ort"],
            bundesland = fields["Bundesland"],
            postleitzahl = fields["Postleitzahl"],
            breitengrad = fields["Breitengrad"]?.toDoubleOrNull(),
            laengengrad = fields["Laengengrad"]?.toDoubleOrNull(),
            nettonennleistungKw = fields["Nettonennleistung"]?.toDoubleOrNull()
                ?: fields["Bruttoleistung"]?.toDoubleOrNull(),
            rotorblattlaengeM = fields["Rotordurchmesser"]?.toDoubleOrNull()?.let { it / 2 },
            nabenhoeheM = fields["Nabenhoehe"]?.toDoubleOrNull(),
            inbetriebnahmedatum = fields["Inbetriebnahmedatum"],
            betriebsstatus = fields["EinheitBetriebsstatus"],
            hersteller = fields["Hersteller"],
            typenbezeichnung = fields["Typenbezeichnung"],
        )
    }

    private fun newParser(xml: String): XmlPullParser {
        val factory = XmlPullParserFactory.newInstance().apply { isNamespaceAware = false }
        return factory.newPullParser().apply { setInput(xml.reader()) }
    }

    private fun XmlPullParser.localName(): String = name?.substringAfterLast(':') ?: name ?: ""

    private inline fun forEachEvent(parser: XmlPullParser, block: (Int) -> Unit) {
        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            block(eventType)
            eventType = parser.next()
        }
    }

    private data class ListResponse(val mastrNummern: List<String>, val hasMore: Boolean)
}
