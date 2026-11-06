package com.triskeledu.aprendoia.ui.coleccion

import com.triskeledu.aprendoia.data.local.relacion.DescubrimientoDetalle
import com.triskeledu.aprendoia.data.local.relacion.SelfieDetalle

/** Pestañas de la colección. Un enum, como "Pantalla" en el ejemplo de Barras. */
enum class PestanaColeccion(val titulo: String) { CAPSULAS("Cápsulas"), SELFIES("Selfies") }

/** Filtro de cápsulas. */
enum class FiltroCapsulas(val etiqueta: String) { TODAS("Todas"), POR_VER("Por ver"), VISTAS("Vistas") }

/** MODELO: estado de la pantalla 11 (Colección de descubrimientos). */
data class ColeccionUiState(
    val cargando: Boolean = true,
    val capsulas: List<DescubrimientoDetalle> = emptyList(),
    val selfies: List<SelfieDetalle> = emptyList(),
    val totalCapsulas: Int = 0,
    val pestana: PestanaColeccion = PestanaColeccion.CAPSULAS,
    val filtro: FiltroCapsulas = FiltroCapsulas.TODAS
) {
    val capsulasVisibles: List<DescubrimientoDetalle>
        get() = when (filtro) {
            FiltroCapsulas.TODAS -> capsulas
            FiltroCapsulas.POR_VER -> capsulas.filter { !it.visto }
            FiltroCapsulas.VISTAS -> capsulas.filter { it.visto }
        }

    /** Avance de la colección entre 0 y 1. */
    val avance: Float
        get() = if (totalCapsulas == 0) 0f else (capsulas.size.toFloat() / totalCapsulas).coerceIn(0f, 1f)
}
