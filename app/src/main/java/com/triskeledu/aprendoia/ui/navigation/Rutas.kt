package com.triskeledu.aprendoia.ui.navigation

/**
 * Todas las rutas de navegación de la app en un solo lugar.
 * Las que reciben datos usan {argumento} y una función para armar la ruta real.
 */
object Rutas {
    // Acceso
    const val BIENVENIDA = "bienvenida"                 // 1
    const val REGISTRO = "registro"                     // 2
    const val SELECCION_BASE = "seleccion_jugador"      // 3
    const val SELECCION = "$SELECCION_BASE?modoAdmin={modoAdmin}"

    // Cliente (con barra inferior)
    const val DASHBOARD = "dashboard"                   // 4
    const val MAPA = "mapa"                             // 5
    const val COLECCION = "coleccion"                   // 11
    const val PERFIL = "perfil"                         // 15

    // Pantallas de detalle
    const val DETALLE_PUNTO = "detalle_punto/{puntoId}"                         // 6
    const val ESCANER = "escaner"                                               // 7
    const val RESULTADO_QR = "resultado_qr/{contenidoId}/{puntosGanados}"       // 8
    const val VIDEO = "video/{contenidoId}"                                     // 9
    const val SELFIE = "selfie/{puntoId}"                                       // 10
    const val PROGRESO = "progreso"                                             // 12
    const val HISTORIAL = "historial"                                           // 13
    const val NOTIFICACIONES = "notificaciones"                                 // 14

    // Administrador / Creador de Contenido (pantallas adicionales)
    const val PANEL_GESTION = "panel_gestion"
    const val EDITAR_JUGADOR = "editar_jugador/{usuarioId}"
    const val EDITAR_PERFIL = "editar_perfil/{usuarioId}"
    const val GESTION_PUNTOS = "gestion_puntos"
    const val FORM_PUNTO = "form_punto?puntoId={puntoId}"          // -1 = nuevo
    const val GESTION_CAPSULAS = "gestion_capsulas"
    const val FORM_CAPSULA = "form_capsula?contenidoId={contenidoId}" // -1 = nueva
    const val QR_CAPSULA = "qr_capsula/{contenidoId}"

    fun formPunto(puntoId: Long = -1) = "form_punto?puntoId=$puntoId"
    fun formCapsula(contenidoId: Long = -1) = "form_capsula?contenidoId=$contenidoId"
    fun qrCapsula(contenidoId: Long) = "qr_capsula/$contenidoId"

    const val ARG_PUNTO_ID = "puntoId"
    const val ARG_CONTENIDO_ID = "contenidoId"
    const val ARG_PUNTOS_GANADOS = "puntosGanados"
    const val ARG_USUARIO_ID = "usuarioId"
    const val ARG_MODO_ADMIN = "modoAdmin"

    fun seleccion(modoAdmin: Boolean = false) = "$SELECCION_BASE?modoAdmin=$modoAdmin"

    fun detallePunto(puntoId: Long) = "detalle_punto/$puntoId"
    fun resultadoQr(contenidoId: Long, puntosGanados: Int) = "resultado_qr/$contenidoId/$puntosGanados"
    fun video(contenidoId: Long) = "video/$contenidoId"
    fun selfie(puntoId: Long) = "selfie/$puntoId"
    fun editarJugador(usuarioId: Long) = "editar_jugador/$usuarioId"
    fun editarPerfil(usuarioId: Long) = "editar_perfil/$usuarioId"

    /** Pantallas donde se muestra la barra de navegación inferior. */
    val conBarraInferior = setOf(DASHBOARD, MAPA, COLECCION, PERFIL, PROGRESO)
}
