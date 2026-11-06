package com.triskeledu.aprendoia.ui.historial

import com.triskeledu.aprendoia.data.local.entity.HistorialEntity
import com.triskeledu.aprendoia.data.local.entity.TipoActividad
import com.triskeledu.aprendoia.util.comoFecha

/** Filtros del historial (null = todos). */
enum class FiltroHistorial(val etiqueta: String, val tipo: TipoActividad?) {
    TODOS("Todos", null),
    QR("QR", TipoActividad.QR),
    VIDEOS("Videos", TipoActividad.VIDEO),
    SELFIES("Selfies", TipoActividad.SELFIE)
}

/** Grupo de actividades de un mismo día ("Hoy", "Ayer", "12/10/2026"). */
data class GrupoDia(val titulo: String, val actividades: List<HistorialEntity>)

/** MODELO: estado de la pantalla 13 (Historial de actividades). */
data class HistorialUiState(
    val cargando: Boolean = true,
    val actividades: List<HistorialEntity> = emptyList(),
    val filtro: FiltroHistorial = FiltroHistorial.TODOS
) {
    private val filtradas: List<HistorialEntity>
        get() = filtro.tipo?.let { tipo -> actividades.filter { it.tipo == tipo } } ?: actividades

    /** Actividades agrupadas por día, de la más reciente a la más antigua. */
    val grupos: List<GrupoDia>
        get() {
            val hoy = System.currentTimeMillis().comoFecha()
            val ayer = (System.currentTimeMillis() - 24L * 60 * 60 * 1000).comoFecha()
            return filtradas
                .groupBy { it.fecha.comoFecha() }
                .map { (fecha, lista) ->
                    val titulo = when (fecha) {
                        hoy -> "Hoy"
                        ayer -> "Ayer"
                        else -> fecha
                    }
                    GrupoDia(titulo, lista)
                }
        }

    val totalPuntos: Int get() = filtradas.sumOf { it.puntos }
}
