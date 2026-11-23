package com.triskeledu.aprendoia.data.local

import androidx.room.withTransaction

/**
 * Ejecuta un bloque "todo o nada" en la base de datos.
 * Es una interfaz para que las pruebas unitarias puedan reemplazarla por una
 * versión simple (sin Room real).
 */
interface Transaccion {
    suspend fun <R> ejecutar(bloque: suspend () -> R): R
}

/** Implementación real: usa withTransaction de Room. */
class TransaccionRoom(private val db: AppDatabase) : Transaccion {
    override suspend fun <R> ejecutar(bloque: suspend () -> R): R = db.withTransaction(bloque)
}
