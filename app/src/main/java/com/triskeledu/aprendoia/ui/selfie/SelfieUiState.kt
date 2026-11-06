package com.triskeledu.aprendoia.ui.selfie

import com.triskeledu.aprendoia.data.local.entity.PuntoEntity

/** MODELO: estado de la pantalla 10 (Selfie turística). */
data class SelfieUiState(
    val cargando: Boolean = true,
    val punto: PuntoEntity? = null,
    /** El jugador ya escaneó el QR de este punto. */
    val qrEscaneado: Boolean = false,
    /** El jugador ya tenía una selfie aquí (antes de entrar a la pantalla). */
    val tieneSelfie: Boolean = false,

    /** Uri del archivo donde la cámara está guardando la foto. */
    val uriPendiente: String? = null,
    /** Foto ya tomada, esperando que el jugador la confirme. */
    val fotoTomada: String? = null,

    val guardando: Boolean = false,
    val error: String? = null,
    /** Puntos ganados al guardar (null = aún no se guarda). */
    val puntosGanados: Int? = null
) {
    /** Motivo por el que no se puede tomar la selfie (null = se puede). */
    val motivoBloqueo: String?
        get() = when {
            punto == null -> "El punto no existe"
            !punto.esHitoTuristico -> "La bonificación de selfie solo existe en hitos turísticos."
            tieneSelfie && puntosGanados == null -> "Ya tomaste tu selfie en este lugar. ¡Busca otro hito!"
            !qrEscaneado -> "Primero escanea el código QR de este lugar."
            else -> null
        }
}
