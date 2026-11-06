package com.triskeledu.aprendoia.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val localeChile: Locale = Locale.forLanguageTag("es-CL")

/** 1250 → "1.250" (separador de miles chileno). */
fun Int.conSeparador(): String = NumberFormat.getIntegerInstance(localeChile).format(this)

/** Fecha en milisegundos → "12/09/2026". */
fun Long.comoFecha(): String = SimpleDateFormat("dd/MM/yyyy", localeChile).format(Date(this))

/** Fecha en milisegundos → "16:30". */
fun Long.comoHora(): String = SimpleDateFormat("HH:mm", localeChile).format(Date(this))

/** Tiempo relativo: "Ahora", "Hace 5 min", "Hace 3 h", "Ayer", "12/10/2026". */
fun Long.haceCuanto(ahora: Long = System.currentTimeMillis()): String {
    val minutos = (ahora - this) / 60_000
    return when {
        minutos < 1 -> "Ahora"
        minutos < 60 -> "Hace $minutos min"
        minutos < 24 * 60 -> "Hace ${minutos / 60} h"
        minutos < 48 * 60 -> "Ayer"
        else -> comoFecha()
    }
}
