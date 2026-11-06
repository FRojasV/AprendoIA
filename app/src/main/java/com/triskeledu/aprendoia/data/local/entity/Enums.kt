package com.triskeledu.aprendoia.data.local.entity

/**
 * Enumeraciones del dominio. Room (2.3+) guarda los enum como texto
 * automáticamente, por ejemplo "ADMIN", "CLIENTE", etc.
 */

/** Roles definidos en el caso Triskeledu. */
enum class Rol { ADMIN, CREADOR, CLIENTE }

/** Tipos de actividad que se registran en el historial (pantalla 13). */
enum class TipoActividad { QR, VIDEO, SELFIE, VISITA }

/** Tipos de notificación (pantalla 14). */
enum class TipoNotificacion { PUNTO_CERCANO, DESAFIO, NUEVO_VIDEO, LOGRO, EVENTO }
