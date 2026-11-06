package com.triskeledu.aprendoia.data.sesion

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Archivo de preferencias donde se guarda la sesión. */
private val Context.dataStoreSesion by preferencesDataStore(name = "sesion")

/**
 * Recuerda qué jugador está activo. Todas las pantallas leen este id para
 * mostrar solo la información de ese perfil.
 */
class SesionManager(private val context: Context) {

    val usuarioActivoId: Flow<Long?> =
        context.dataStoreSesion.data.map { preferencias -> preferencias[CLAVE_USUARIO] }

    suspend fun iniciar(usuarioId: Long) {
        context.dataStoreSesion.edit { it[CLAVE_USUARIO] = usuarioId }
    }

    suspend fun cerrar() {
        context.dataStoreSesion.edit { it.remove(CLAVE_USUARIO) }
    }

    private companion object {
        val CLAVE_USUARIO = longPreferencesKey("usuario_activo_id")
    }
}
