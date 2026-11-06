package com.triskeledu.aprendoia.ui.video

import com.triskeledu.aprendoia.data.local.entity.ContenidoEntity
import com.triskeledu.aprendoia.data.local.entity.PuntoEntity

/** MODELO: estado de la pantalla 9 (Video educativo). */
data class VideoUiState(
    val cargando: Boolean = true,
    val contenido: ContenidoEntity? = null,
    val punto: PuntoEntity? = null,
    /** El jugador escaneó el QR de esta cápsula (si no, queda bloqueada). */
    val desbloqueado: Boolean = false,
    val visto: Boolean = false,
    val mensaje: String? = null
) {
    /** Solo se consideran links web válidos (mismo criterio de seguridad del ejemplo QR). */
    val tieneVideo: Boolean
        get() = contenido?.url?.let { it.startsWith("https://") || it.startsWith("http://") } == true
}
