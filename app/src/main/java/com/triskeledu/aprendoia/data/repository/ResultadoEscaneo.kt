package com.triskeledu.aprendoia.data.repository

import com.triskeledu.aprendoia.data.local.entity.ContenidoEntity

/**
 * Posibles resultados al registrar un QR.
 * Una "sealed class" obliga a manejar todos los casos en un when.
 */
sealed class ResultadoEscaneo {
    /** Primera vez que este jugador escanea el QR: se suman puntos. */
    data class Nuevo(val contenido: ContenidoEntity, val puntosGanados: Int) : ResultadoEscaneo()

    /** El jugador ya lo había escaneado: no se suman puntos de nuevo. */
    data class Repetido(val contenido: ContenidoEntity) : ResultadoEscaneo()

    /** El código no existe en la base o no está publicado. */
    data object NoEncontrado : ResultadoEscaneo()

    /** No hay jugador activo. */
    data object SinSesion : ResultadoEscaneo()
}
