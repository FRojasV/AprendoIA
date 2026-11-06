package com.triskeledu.aprendoia.util

import java.security.MessageDigest

/** Utilidades simples de seguridad para las contraseñas locales. */
object Seguridad {

    /** Convierte la contraseña en un hash SHA-256 (texto hexadecimal). */
    fun hash(texto: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(texto.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }

    fun coincide(textoPlano: String, hashGuardado: String): Boolean =
        hash(textoPlano) == hashGuardado
}
