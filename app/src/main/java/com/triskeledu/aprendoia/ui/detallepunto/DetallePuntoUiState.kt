package com.triskeledu.aprendoia.ui.detallepunto

import com.triskeledu.aprendoia.data.local.entity.PuntoEntity
import com.triskeledu.aprendoia.data.local.relacion.ContenidoConEstado
import com.triskeledu.aprendoia.util.Coordenada
import com.triskeledu.aprendoia.util.Ubicacion

/** MODELO: estado de la pantalla 6 (Detalle del punto). */
data class DetallePuntoUiState(
    val cargando: Boolean = true,
    val noExiste: Boolean = false,
    val punto: PuntoEntity? = null,
    val contenidos: List<ContenidoConEstado> = emptyList(),
    val tieneSelfie: Boolean = false,
    val ubicacion: Coordenada? = null
) {
    /** El punto se considera completado si el jugador escaneó al menos un QR. */
    val completado: Boolean get() = contenidos.any { it.descubierto }

    /** La selfie (+100) se habilita en hitos turísticos después de escanear el QR. */
    val puedeTomarSelfie: Boolean
        get() = punto?.esHitoTuristico == true && completado && !tieneSelfie

    val distancia: String?
        get() {
            val p = punto ?: return null
            val desde = ubicacion ?: return null
            return Ubicacion.formatearDistancia(
                Ubicacion.distanciaMetros(desde, Coordenada(p.latitud, p.longitud))
            )
        }
}
