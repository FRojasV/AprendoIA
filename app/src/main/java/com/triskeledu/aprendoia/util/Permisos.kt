package com.triskeledu.aprendoia.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat

/** Estado actual de los permisos que usa la app. */
data class EstadoPermisos(
    val camara: Boolean,
    val ubicacion: Boolean,
    val notificaciones: Boolean
)

/**
 * Ayudas para consultar permisos. La solicitud en sí se hace desde los
 * Composable con rememberLauncherForActivityResult.
 */
object Permisos {

    const val CAMARA = Manifest.permission.CAMERA
    val UBICACION = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    /** POST_NOTIFICATIONS solo existe desde Android 13 (API 33). */
    val requiereNotificaciones: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    fun tiene(context: Context, permiso: String): Boolean =
        ContextCompat.checkSelfPermission(context, permiso) == PackageManager.PERMISSION_GRANTED

    fun tieneUbicacion(context: Context): Boolean = UBICACION.any { tiene(context, it) }

    fun tieneNotificaciones(context: Context): Boolean =
        !requiereNotificaciones || tiene(context, Manifest.permission.POST_NOTIFICATIONS)

    fun leer(context: Context) = EstadoPermisos(
        camara = tiene(context, CAMARA),
        ubicacion = tieneUbicacion(context),
        notificaciones = tieneNotificaciones(context)
    )

    /** Lista de permisos que aún faltan, para pedirlos todos juntos. */
    fun pendientes(context: Context): Array<String> = buildList {
        if (!tiene(context, CAMARA)) add(CAMARA)
        if (!tieneUbicacion(context)) addAll(UBICACION)
        if (requiereNotificaciones && !tieneNotificaciones(context)) {
            add(Manifest.permission.POST_NOTIFICATIONS)
        }
    }.toTypedArray()

    /** Abre la pantalla de Ajustes de la app (cuando el usuario negó un permiso). */
    fun abrirAjustes(context: Context) {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context.packageName, null)
        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}
