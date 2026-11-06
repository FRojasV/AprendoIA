package com.triskeledu.aprendoia.ui.escaner

/** Evento de navegación hacia la pantalla 8 (Resultado). */
data class IrAResultado(val contenidoId: Long, val puntosGanados: Int)

/** MODELO: estado de la pantalla 7 (Escáner QR). */
data class EscanerUiState(
    val procesando: Boolean = false,
    /** Texto de ayuda o error bajo el botón. */
    val mensaje: String = "Apunta la cámara al código QR del punto turístico.",
    val esError: Boolean = false,

    // Ingreso manual (útil en el emulador o si la cámara no enfoca)
    val mostrarIngresoManual: Boolean = false,
    val codigoManual: String = "",

    /** Evento: cuando no es null, la pantalla navega al resultado y lo limpia. */
    val irAResultado: IrAResultado? = null
)
