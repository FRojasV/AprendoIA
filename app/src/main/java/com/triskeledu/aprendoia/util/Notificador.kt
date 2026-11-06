package com.triskeledu.aprendoia.util

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.triskeledu.aprendoia.MainActivity
import com.triskeledu.aprendoia.R

/**
 * Notificaciones LOCALES (sin Firebase ni servidor).
 * Se muestran en la barra del teléfono y, además, cada aviso se guarda en
 * la tabla "notificaciones" para la pantalla 14 (eso lo hace el repositorio).
 */
class Notificador(private val context: Context) {

    init {
        crearCanal()
    }

    /** Desde Android 8 toda notificación necesita un "canal". */
    private fun crearCanal() {
        val canal = NotificationChannel(
            CANAL_JUEGO,
            "Juego y desafíos",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Puntos ganados, desafíos y puntos cercanos"
        }
        context.getSystemService(NotificationManager::class.java)?.createNotificationChannel(canal)
    }

    /**
     * Muestra la notificación solo si el sistema tiene el permiso.
     * (Si el jugador las desactivó en su Perfil, el repositorio ni siquiera llama aquí.)
     */
    @SuppressLint("MissingPermission")
    fun mostrar(titulo: String, mensaje: String) {
        if (!Permisos.tieneNotificaciones(context)) return

        // Al tocar la notificación se abre la app directamente en la bandeja (pantalla 14).
        val abrirApp = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java)
                .putExtra(EXTRA_ABRIR, DESTINO_NOTIFICACIONES)
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificacion = NotificationCompat.Builder(context, CANAL_JUEGO)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setStyle(NotificationCompat.BigTextStyle().bigText(mensaje))
            .setContentIntent(abrirApp)
            .setAutoCancel(true)
            .build()

        // Un id distinto por notificación para que no se reemplacen entre sí.
        NotificationManagerCompat.from(context).notify(System.currentTimeMillis().toInt(), notificacion)
    }

    companion object {
        const val CANAL_JUEGO = "canal_juego"
        const val EXTRA_ABRIR = "abrir_pantalla"
        const val DESTINO_NOTIFICACIONES = "notificaciones"
    }
}
