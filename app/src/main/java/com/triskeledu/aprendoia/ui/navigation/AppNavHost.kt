package com.triskeledu.aprendoia.ui.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import com.triskeledu.aprendoia.ui.notificaciones.PantallaNotificaciones
import com.triskeledu.aprendoia.ui.gestion.pantallas.PantallaFormularioCapsula
import com.triskeledu.aprendoia.ui.gestion.pantallas.PantallaFormularioPunto
import com.triskeledu.aprendoia.ui.gestion.pantallas.PantallaListaCapsulas
import com.triskeledu.aprendoia.ui.gestion.pantallas.PantallaListaPuntos
import com.triskeledu.aprendoia.ui.gestion.pantallas.PantallaPanelGestion
import com.triskeledu.aprendoia.ui.gestion.pantallas.PantallaQrCapsula
import com.triskeledu.aprendoia.util.Notificador
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.triskeledu.aprendoia.ui.bienvenida.PantallaBienvenida
import com.triskeledu.aprendoia.ui.coleccion.PantallaColeccion
import com.triskeledu.aprendoia.ui.dashboard.PantallaDashboard
import com.triskeledu.aprendoia.ui.historial.PantallaHistorial
import com.triskeledu.aprendoia.ui.progreso.PantallaProgreso
import com.triskeledu.aprendoia.ui.detallepunto.PantallaDetallePunto
import com.triskeledu.aprendoia.ui.escaner.PantallaEscaner
import com.triskeledu.aprendoia.ui.mapa.PantallaMapa
import com.triskeledu.aprendoia.ui.resultadoqr.PantallaResultadoQr
import com.triskeledu.aprendoia.ui.selfie.PantallaSelfie
import com.triskeledu.aprendoia.ui.video.PantallaVideo
import com.triskeledu.aprendoia.ui.perfil.PantallaPerfil
import com.triskeledu.aprendoia.ui.editarjugador.PantallaEditarJugador
import com.triskeledu.aprendoia.ui.registro.PantallaRegistro
import com.triskeledu.aprendoia.ui.seleccion.PantallaSeleccion

/**
 * Grafo de navegación con las 15 pantallas del caso.
 * [destinoExterno]: pantalla pedida desde una notificación del sistema.
 */
@Composable
fun AppNavHost(
    destinoExterno: String? = null,
    onDestinoConsumido: () -> Unit = {},
    navController: NavHostController = rememberNavController()
) {
    val entradaActual by navController.currentBackStackEntryAsState()
    val rutaActual = entradaActual?.destination?.route

    // Al tocar una notificación del teléfono: Dashboard → Notificaciones.
    LaunchedEffect(destinoExterno) {
        if (destinoExterno == Notificador.DESTINO_NOTIFICACIONES) {
            navController.irAlInicioDeJugador()
            navController.navigate(Rutas.NOTIFICACIONES) { launchSingleTop = true }
            onDestinoConsumido()
        }
    }

    Scaffold(
        // Cada pantalla maneja sus propios márgenes del sistema (pantalla completa).
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (rutaActual in Rutas.conBarraInferior) {
                BarraInferior(rutaActual) { ruta -> navController.navegarDesdeBarra(ruta) }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Rutas.BIENVENIDA,
            modifier = Modifier
                .padding(padding)
                .consumeWindowInsets(padding)
        ) {
            // ---------- 1. Bienvenida ----------
            composable(Rutas.BIENVENIDA) {
                PantallaBienvenida(
                    onComenzar = { navController.navigate(Rutas.seleccion()) },
                    onTengoCuenta = { navController.navigate(Rutas.seleccion()) }
                )
            }

            // ---------- 2. Registro ----------
            composable(Rutas.REGISTRO) {
                PantallaRegistro(
                    onVolver = { navController.popBackStack() },
                    onRegistrado = { navController.irAlInicioDeJugador() }
                )
            }

            // ---------- 3. Selección de jugador ----------
            composable(
                route = Rutas.SELECCION,
                arguments = listOf(
                    navArgument(Rutas.ARG_MODO_ADMIN) {
                        type = NavType.BoolType
                        defaultValue = false
                    }
                )
            ) {
                PantallaSeleccion(
                    onIngresoCliente = { navController.irAlInicioDeJugador() },
                    onIngresoGestion = { navController.irAlPanelGestion() },
                    onCrearJugador = { navController.navigate(Rutas.REGISTRO) },
                    onEditarJugador = { id -> navController.navigate(Rutas.editarJugador(id)) },
                    onVolver = { navController.popBackStack() }
                )
            }

            // ---------- Edición de jugador (Administrador) ----------
            composable(
                route = Rutas.EDITAR_JUGADOR,
                arguments = listOf(navArgument(Rutas.ARG_USUARIO_ID) { type = NavType.LongType })
            ) {
                PantallaEditarJugador(onVolver = { navController.popBackStack() })
            }

            // ---------- Edición del propio perfil (desde pantalla 15) ----------
            composable(
                route = Rutas.EDITAR_PERFIL,
                arguments = listOf(navArgument(Rutas.ARG_USUARIO_ID) { type = NavType.LongType })
            ) {
                PantallaEditarJugador(
                    titulo = "Editar perfil",
                    onVolver = { navController.popBackStack() }
                )
            }

            // ---------- 4. Dashboard ----------
            composable(Rutas.DASHBOARD) {
                PantallaDashboard(
                    onIrMapa = { navController.navegarDesdeBarra(Rutas.MAPA) },
                    onIrEscaner = { navController.navegarDesdeBarra(Rutas.ESCANER) },
                    onIrColeccion = { navController.navegarDesdeBarra(Rutas.COLECCION) },
                    onIrProgreso = { navController.navigate(Rutas.PROGRESO) },
                    onIrHistorial = { navController.navigate(Rutas.HISTORIAL) },
                    onIrNotificaciones = { navController.navigate(Rutas.NOTIFICACIONES) },
                    onIrPerfil = { navController.navegarDesdeBarra(Rutas.PERFIL) },
                    onIrDetallePunto = { id -> navController.navigate(Rutas.detallePunto(id)) },
                    onSinSesion = { navController.volverASeleccion() }
                )
            }

            // ---------- 5. Mapa ----------
            composable(Rutas.MAPA) {
                PantallaMapa(
                    onVerDetalle = { id -> navController.navigate(Rutas.detallePunto(id)) }
                )
            }

            // ---------- 6. Detalle del punto ----------
            composable(
                route = Rutas.DETALLE_PUNTO,
                arguments = listOf(navArgument(Rutas.ARG_PUNTO_ID) { type = NavType.LongType })
            ) {
                PantallaDetallePunto(
                    onVolver = { navController.popBackStack() },
                    onEscanear = { navController.navigate(Rutas.ESCANER) },
                    onVerVideo = { id -> navController.navigate(Rutas.video(id)) },
                    onTomarSelfie = { id -> navController.navigate(Rutas.selfie(id)) }
                )
            }

            // ---------- 7. Escáner QR ----------
            composable(Rutas.ESCANER) {
                PantallaEscaner(
                    onVolver = { navController.popBackStack() },
                    onResultado = { contenidoId, puntos ->
                        // El escáner se quita de la pila: "atrás" desde el resultado no vuelve a él.
                        navController.navigate(Rutas.resultadoQr(contenidoId, puntos)) {
                            popUpTo(Rutas.ESCANER) { inclusive = true }
                        }
                    }
                )
            }

            // ---------- 8. Resultado del QR ----------
            composable(
                route = Rutas.RESULTADO_QR,
                arguments = listOf(
                    navArgument(Rutas.ARG_CONTENIDO_ID) { type = NavType.LongType },
                    navArgument(Rutas.ARG_PUNTOS_GANADOS) { type = NavType.IntType }
                )
            ) {
                PantallaResultadoQr(
                    onVerVideo = { id -> navController.navigate(Rutas.video(id)) },
                    onTomarSelfie = { id -> navController.navigate(Rutas.selfie(id)) },
                    onEscanearOtro = {
                        navController.navigate(Rutas.ESCANER) {
                            popUpTo(Rutas.RESULTADO_QR) { inclusive = true }
                        }
                    },
                    onVolverMapa = { navController.navegarDesdeBarra(Rutas.MAPA) }
                )
            }

            // ---------- 9. Video ----------
            composable(
                route = Rutas.VIDEO,
                arguments = listOf(navArgument(Rutas.ARG_CONTENIDO_ID) { type = NavType.LongType })
            ) {
                PantallaVideo(
                    onVolver = { navController.popBackStack() },
                    onIrAlPunto = { id -> navController.navigate(Rutas.detallePunto(id)) }
                )
            }

            // ---------- 10. Selfie ----------
            composable(
                route = Rutas.SELFIE,
                arguments = listOf(navArgument(Rutas.ARG_PUNTO_ID) { type = NavType.LongType })
            ) {
                PantallaSelfie(onVolver = { navController.popBackStack() })
            }

            // ---------- 11. Colección ----------
            composable(Rutas.COLECCION) {
                PantallaColeccion(
                    onAbrirCapsula = { id -> navController.navigate(Rutas.video(id)) },
                    onAbrirPunto = { id -> navController.navigate(Rutas.detallePunto(id)) },
                    onIrMapa = { navController.navegarDesdeBarra(Rutas.MAPA) }
                )
            }

            // ---------- 12. Progreso ----------
            composable(Rutas.PROGRESO) {
                PantallaProgreso(
                    onVolver = { navController.popBackStack() },
                    onVerHistorial = { navController.navigate(Rutas.HISTORIAL) }
                )
            }

            // ---------- 13. Historial ----------
            composable(Rutas.HISTORIAL) {
                PantallaHistorial(onVolver = { navController.popBackStack() })
            }

            // ---------- 14. Notificaciones ----------
            composable(Rutas.NOTIFICACIONES) {
                PantallaNotificaciones(
                    onVolver = { navController.popBackStack() },
                    onNavegar = { destino -> navController.abrirDestinoNotificacion(destino) }
                )
            }

            // ---------- 15. Perfil ----------
            composable(Rutas.PERFIL) {
                PantallaPerfil(
                    onVolver = { navController.navegarDesdeBarra(Rutas.DASHBOARD) },
                    onEditarPerfil = { id -> navController.navigate(Rutas.editarPerfil(id)) },
                    onSesionCerrada = { navController.volverASeleccion() }
                )
            }

            // ---------- Panel de gestión (Administrador / Creador) ----------
            composable(Rutas.PANEL_GESTION) {
                PantallaPanelGestion(
                    onPuntos = { navController.navigate(Rutas.GESTION_PUNTOS) },
                    onCapsulas = { navController.navigate(Rutas.GESTION_CAPSULAS) },
                    onJugadores = { navController.navigate(Rutas.seleccion(modoAdmin = true)) },
                    onVerMapa = { navController.navigate(Rutas.MAPA) },
                    onSesionCerrada = { navController.volverASeleccion() }
                )
            }

            composable(Rutas.GESTION_PUNTOS) {
                PantallaListaPuntos(
                    onVolver = { navController.popBackStack() },
                    onNuevo = { navController.navigate(Rutas.formPunto()) },
                    onEditar = { id -> navController.navigate(Rutas.formPunto(id)) }
                )
            }

            composable(
                route = Rutas.FORM_PUNTO,
                arguments = listOf(navArgument(Rutas.ARG_PUNTO_ID) { type = NavType.LongType; defaultValue = -1L })
            ) {
                PantallaFormularioPunto(onVolver = { navController.popBackStack() })
            }

            composable(Rutas.GESTION_CAPSULAS) {
                PantallaListaCapsulas(
                    onVolver = { navController.popBackStack() },
                    onNueva = { navController.navigate(Rutas.formCapsula()) },
                    onEditar = { id -> navController.navigate(Rutas.formCapsula(id)) },
                    onVerQr = { id -> navController.navigate(Rutas.qrCapsula(id)) }
                )
            }

            composable(
                route = Rutas.FORM_CAPSULA,
                arguments = listOf(navArgument(Rutas.ARG_CONTENIDO_ID) { type = NavType.LongType; defaultValue = -1L })
            ) {
                PantallaFormularioCapsula(
                    onVolver = { navController.popBackStack() },
                    // Al guardar se reemplaza el formulario por la pantalla del QR.
                    onGuardada = { id ->
                        navController.navigate(Rutas.qrCapsula(id)) {
                            popUpTo(Rutas.FORM_CAPSULA) { inclusive = true }
                        }
                    }
                )
            }

            composable(
                route = Rutas.QR_CAPSULA,
                arguments = listOf(navArgument(Rutas.ARG_CONTENIDO_ID) { type = NavType.LongType })
            ) {
                PantallaQrCapsula(onVolver = { navController.popBackStack() })
            }
        }
    }
}

/* ---------------- Funciones de ayuda para navegar ---------------- */

/** Navegación de la barra inferior: no apila pantallas repetidas y conserva su estado. */
fun NavHostController.navegarDesdeBarra(ruta: String) {
    if (ruta == Rutas.ESCANER) {
        navigate(Rutas.ESCANER) { launchSingleTop = true }
        return
    }
    navigate(ruta) {
        popUpTo(Rutas.DASHBOARD) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

/** Tras iniciar sesión o registrarse: va al Dashboard y borra el acceso del historial. */
fun NavHostController.irAlInicioDeJugador() {
    navigate(Rutas.DASHBOARD) {
        popUpTo(graph.id) { inclusive = true }
        launchSingleTop = true
    }
}

/**
 * Abre la pantalla guardada en el campo "destino" de una notificación
 * ("mapa", "video/3", "selfie/1", "detalle_punto/2"...).
 */
fun NavHostController.abrirDestinoNotificacion(destino: String) {
    if (destino == Rutas.MAPA) {
        navegarDesdeBarra(Rutas.MAPA)
        return
    }
    try {
        navigate(destino)
    } catch (e: IllegalArgumentException) {
        // Ruta desconocida (por ejemplo, de una versión anterior): se ignora.
    }
}

/** Tras ingresar como Administrador o Creador de Contenido. */
fun NavHostController.irAlPanelGestion() {
    navigate(Rutas.PANEL_GESTION) {
        popUpTo(graph.id) { inclusive = true }
        launchSingleTop = true
    }
}

/** Cambiar de jugador: vuelve a la selección limpiando la pila de pantallas. */
fun NavHostController.volverASeleccion() {
    navigate(Rutas.seleccion()) {
        popUpTo(graph.id) { inclusive = true }
        launchSingleTop = true
    }
}
