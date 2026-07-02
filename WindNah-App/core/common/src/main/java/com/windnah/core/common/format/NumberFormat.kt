package com.windnah.core.common.format

import java.util.Locale
import kotlin.math.roundToInt

/**
 * Canonical German-locale number formatting shared across all features.
 *
 * German convention: "." groups thousands, "," is the decimal separator
 * (e.g. 1.234,5). Using [Locale.GERMANY] keeps this consistent everywhere
 * instead of hand-rolled `String.format(...).replace('.', ',')` per screen.
 */

/** Integer with grouped thousands, e.g. 1234 -> "1.234". */
fun formatInt(value: Int): String =
    String.format(Locale.GERMANY, "%,d", value)

/** Decimal with a fixed number of fraction digits, e.g. 1.5 -> "1,5". */
fun formatDecimal(value: Double, decimals: Int): String =
    String.format(Locale.GERMANY, "%,.${decimals}f", value)

/** Kilowatts -> megawatts string with unit, e.g. 29800.0 -> "29,8 MW". */
fun formatMegawatts(kw: Double): String =
    "${formatDecimal(kw / 1_000.0, 1)} MW"

/** Kilowatt-hours -> gigawatt-hours value (no unit), e.g. 92_000_000 -> "92,0". */
fun formatGigawattHours(kwh: Double): String =
    formatDecimal(kwh / 1_000_000.0, 1)

/** Kilowatt-hours -> megawatt-hours integer, e.g. 20_000_000 -> "20.000". */
fun formatMegawattHours(kwh: Double): String =
    formatInt((kwh / 1_000.0).roundToInt())

/** Euro integer with grouped thousands, e.g. 40000 -> "40.000". */
fun formatEur(value: Int): String =
    formatInt(value)

/** Tonnes -> kilotonnes with one decimal, e.g. 18_200 -> "18,2". */
fun formatKilotonnes(tonnes: Double): String =
    formatDecimal(tonnes / 1_000.0, 1)

/** Compact count: >= 1000 shown as "N,Nk", else the plain number. */
fun formatCompactNumber(value: Int): String =
    if (value >= 1_000) {
        "${formatDecimal(value / 1_000.0, 1)}k"
    } else {
        value.toString()
    }
