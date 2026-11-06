package com.triskeledu.aprendoia.ui.progreso

import com.triskeledu.aprendoia.data.local.entity.HistorialEntity
import com.triskeledu.aprendoia.data.local.relacion.EstadisticasJugador
import com.triskeledu.aprendoia.data.local.relacion.PUNTAJE_MAXIMO
import com.triskeledu.aprendoia.data.local.relacion.puntajeTotal
import java.util.Calendar

/** Niveles del jugador según su puntaje. */
enum class Nivel(val nombre: String, val desde: Int) {
    NOVATO("Novato", 0),
    EXPLORADOR("Explorador", 100),
    AVENTURERO("Aventurero", 500),
    EXPERTO("Experto en IA", 2_000),
    MAESTRO("Maestro de Santiago", 5_000)
}

/** Insignia que se desbloquea al cumplir una meta. */
data class Logro(
    val titulo: String,
    val descripcion: String,
    val desbloqueado: Boolean
)

/** Una barra del gráfico semanal. */
data class PuntosDia(val etiqueta: String, val puntos: Int)

/** MODELO: estado de la pantalla 12 (Puntaje y Progreso). */
data class ProgresoUiState(
    val cargando: Boolean = true,
    val estadisticas: EstadisticasJugador = EstadisticasJugador(0, 0, 0, 0, 0, 0),
    val totalLugares: Int = 0,
    val totalCapsulas: Int = 0,
    val historial: List<HistorialEntity> = emptyList()
) {
    val puntaje: Int get() = estadisticas.puntajeTotal

    /** Avance hacia los 10.000 puntos que propone el caso (0..1). */
    val avanceTotal: Float get() = (puntaje.toFloat() / PUNTAJE_MAXIMO).coerceIn(0f, 1f)

    val nivel: Nivel get() = Nivel.entries.last { puntaje >= it.desde }

    val siguienteNivel: Nivel? get() = Nivel.entries.firstOrNull { it.desde > puntaje }

    /** Avance dentro del nivel actual (0..1). */
    val avanceNivel: Float
        get() {
            val siguiente = siguienteNivel ?: return 1f
            val rango = (siguiente.desde - nivel.desde).toFloat()
            return ((puntaje - nivel.desde) / rango).coerceIn(0f, 1f)
        }

    /** Puntos ganados cada uno de los últimos 7 días (de más antiguo a hoy). */
    val puntosUltimos7Dias: List<PuntosDia>
        get() {
            val letras = mapOf(
                Calendar.MONDAY to "L", Calendar.TUESDAY to "M", Calendar.WEDNESDAY to "X",
                Calendar.THURSDAY to "J", Calendar.FRIDAY to "V", Calendar.SATURDAY to "S", Calendar.SUNDAY to "D"
            )
            return (6 downTo 0).map { diasAtras ->
                val inicio = inicioDelDia(diasAtras)
                val fin = inicio + 24L * 60 * 60 * 1000
                val dia = Calendar.getInstance().apply { timeInMillis = inicio }
                PuntosDia(
                    etiqueta = if (diasAtras == 0) "Hoy" else letras[dia.get(Calendar.DAY_OF_WEEK)] ?: "",
                    puntos = historial.filter { it.fecha in inicio until fin }.sumOf { it.puntos }
                )
            }
        }

    val logros: List<Logro>
        get() = listOf(
            Logro("Primer QR", "Escanea tu primer código", estadisticas.qrEncontrados >= 1),
            Logro("Coleccionista", "Descubre 5 cápsulas", estadisticas.qrEncontrados >= 5),
            Logro("Estudiante", "Mira 3 cápsulas de IA", estadisticas.videosVistos >= 3),
            Logro("Fotógrafo", "Toma tu primera selfie", estadisticas.selfies >= 1),
            Logro("Turista experto", "Toma 3 selfies en hitos", estadisticas.selfies >= 3),
            Logro(
                "Santiago completo", "Visita todos los lugares",
                totalLugares > 0 && estadisticas.lugaresVisitados >= totalLugares
            )
        )

    private fun inicioDelDia(diasAtras: Int): Long = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, -diasAtras)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}
