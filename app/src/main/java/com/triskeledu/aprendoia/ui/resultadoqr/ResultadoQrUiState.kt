package com.triskeledu.aprendoia.ui.resultadoqr

import com.triskeledu.aprendoia.data.local.entity.ContenidoEntity
import com.triskeledu.aprendoia.data.local.entity.PuntoEntity

/** MODELO: estado de la pantalla 8 (Resultado del QR). */
data class ResultadoQrUiState(
    val cargando: Boolean = true,
    val contenido: ContenidoEntity? = null,
    val punto: PuntoEntity? = null,
    /** 0 = el QR ya había sido escaneado antes. */
    val puntosGanados: Int = 0,
    val puntajeTotal: Int = 0,
    val tieneSelfie: Boolean = false
) {
    val esNuevo: Boolean get() = puntosGanados > 0
    val ofrecerSelfie: Boolean get() = punto?.esHitoTuristico == true && !tieneSelfie
}
