package com.triskeledu.aprendoia.ui.mapa

import com.triskeledu.aprendoia.data.local.relacion.PuntoConEstado
import com.triskeledu.aprendoia.util.Coordenada
import com.triskeledu.aprendoia.util.Ubicacion


enum class FiltroMapa(val etiqueta: String) {
    TODOS("Todos"),
    PENDIENTES("Pendientes"),
    COMPLETADOS("Completados")
}

/** MODELO: estado de la pantalla 5 (Mapa de Santiago). */
data class MapaUiState(
    val cargando: Boolean = true,
    /** Todos los puntos con su estado para el jugador activo. */
    val puntos: List<PuntoConEstado> = emptyList(),
    val filtro: FiltroMapa = FiltroMapa.TODOS,
    val busqueda: String = "",
    val mostrarResultados: Boolean = false,
    val seleccionadoId: Long? = null,
    /**  (null = sin permiso o sin GPS todavía). */
    val ubicacion: Coordenada? = null
) {
    /** Puntos que se dibujan en el mapa según el filtro elegido. */
    val puntosVisibles: List<PuntoConEstado>
        get() = when (filtro) {
            FiltroMapa.TODOS -> puntos
            FiltroMapa.PENDIENTES -> puntos.filter { !it.descubierto }
            FiltroMapa.COMPLETADOS -> puntos.filter { it.descubierto }
        }

    /** Coincidencias del buscador (por nombre o descripción). */
    val resultadosBusqueda: List<PuntoConEstado>
        get() {
            val texto = busqueda.trim()
            if (texto.isEmpty()) return emptyList()
            return puntos.filter {
                it.punto.nombre.contains(texto, ignoreCase = true) ||
                    it.punto.descripcion.contains(texto, ignoreCase = true)
            }
        }

    val seleccionado: PuntoConEstado?
        get() = puntos.firstOrNull { it.punto.id == seleccionadoId }

    val totalCompletados: Int get() = puntos.count { it.descubierto }
    val totalPendientes: Int get() = puntos.size - totalCompletados

    fun contar(filtro: FiltroMapa): Int = when (filtro) {
        FiltroMapa.TODOS -> puntos.size
        FiltroMapa.PENDIENTES -> totalPendientes
        FiltroMapa.COMPLETADOS -> totalCompletados
    }

    /** "a 350 m" desde el jugador hasta el punto, o null si no hay ubicación. */
    fun distanciaA(punto: PuntoConEstado): String? = ubicacion?.let {
        Ubicacion.formatearDistancia(
            Ubicacion.distanciaMetros(it, Coordenada(punto.punto.latitud, punto.punto.longitud))
        )
    }
}
