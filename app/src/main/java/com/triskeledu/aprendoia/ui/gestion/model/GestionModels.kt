package com.triskeledu.aprendoia.ui.gestion.model

import com.triskeledu.aprendoia.data.local.entity.PuntoEntity
import com.triskeledu.aprendoia.data.local.entity.Rol
import com.triskeledu.aprendoia.data.local.entity.UsuarioEntity
import com.triskeledu.aprendoia.data.local.relacion.CapsulaGestion
import com.triskeledu.aprendoia.data.local.relacion.ResumenGestion
import com.triskeledu.aprendoia.util.Coordenada

// =====================================================================
// GestionModels.kt
// Mismo modelo del ejemplo "Formularios" del curso:
//   CampoFormulario  → valor + error de UN campo
//   ...FormUiState   → todos los campos + intentoEnviar + formularioValido
// =====================================================================

/**
 * Modelo de datos atómico para un campo individual.
 * Combina el texto ingresado con el mensaje de error correspondiente.
 */
data class CampoFormulario(
    val valor: String = "",
    val error: String? = null
) {
    val esValido: Boolean get() = error == null
}

// ---------------------------------------------------------------------
// Panel principal
// ---------------------------------------------------------------------

data class PanelUiState(
    val usuario: UsuarioEntity? = null,
    val resumen: ResumenGestion = ResumenGestion(),
    val sesionCerrada: Boolean = false
) {
    val esAdmin: Boolean get() = usuario?.rol == Rol.ADMIN
}

// ---------------------------------------------------------------------
// Puntos georreferenciados (solo Administrador)
// ---------------------------------------------------------------------

data class ListaPuntosUiState(
    val cargando: Boolean = true,
    val puntos: List<PuntoEntity> = emptyList(),
    val puntoParaEliminar: PuntoEntity? = null,
    val mensaje: String? = null
)

/** Estado del formulario de punto (crear o editar). */
data class PuntoFormUiState(
    val cargando: Boolean = false,
    /** null = punto nuevo; con valor = se está editando ese id. */
    val idEdicion: Long? = null,
    val nombre: CampoFormulario = CampoFormulario(),
    val descripcion: CampoFormulario = CampoFormulario(),
    val infoTuristica: CampoFormulario = CampoFormulario(),
    val latitud: CampoFormulario = CampoFormulario(),
    val longitud: CampoFormulario = CampoFormulario(),
    val esHitoTuristico: Boolean = false,
    val intentoEnviar: Boolean = false,
    val guardando: Boolean = false,
    val guardado: Boolean = false,
    /** Error devuelto por el microservicio (por ejemplo, sin conexión). */
    val mensajeError: String? = null
) {
    val esEdicion: Boolean get() = idEdicion != null

    /** Coordenada escrita (si ambos números son válidos) para mostrarla en el mapa. */
    val coordenada: Coordenada?
        get() {
            val lat = latitud.valor.toDoubleOrNull() ?: return null
            val lon = longitud.valor.toDoubleOrNull() ?: return null
            return Coordenada(lat, lon)
        }

    val formularioValido: Boolean
        get() = nombre.valor.isNotBlank() && descripcion.valor.isNotBlank() &&
            latitud.valor.isNotBlank() && longitud.valor.isNotBlank() &&
            nombre.esValido && descripcion.esValido && latitud.esValido && longitud.esValido
}

// ---------------------------------------------------------------------
// Cápsulas de IA (Administrador y Creador de Contenido)
// ---------------------------------------------------------------------

enum class FiltroCapsulasGestion(val etiqueta: String) {
    TODAS("Todas"), PENDIENTES("Pendientes"), PUBLICADAS("Publicadas")
}

data class ListaCapsulasUiState(
    val cargando: Boolean = true,
    val esAdmin: Boolean = false,
    val capsulas: List<CapsulaGestion> = emptyList(),
    val filtro: FiltroCapsulasGestion = FiltroCapsulasGestion.TODAS,
    val capsulaParaEliminar: CapsulaGestion? = null,
    val mensaje: String? = null
) {
    val visibles: List<CapsulaGestion>
        get() = when (filtro) {
            FiltroCapsulasGestion.TODAS -> capsulas
            FiltroCapsulasGestion.PENDIENTES -> capsulas.filter { !it.contenido.publicado }
            FiltroCapsulasGestion.PUBLICADAS -> capsulas.filter { it.contenido.publicado }
        }
}

/** Estado del formulario de cápsula (crear o editar). */
data class CapsulaFormUiState(
    val cargando: Boolean = true,
    val idEdicion: Long? = null,
    val esAdmin: Boolean = false,
    val puntosDisponibles: List<PuntoEntity> = emptyList(),

    val titulo: CampoFormulario = CampoFormulario(),
    val tema: CampoFormulario = CampoFormulario(),
    val autor: CampoFormulario = CampoFormulario(),
    val url: CampoFormulario = CampoFormulario(),
    val duracion: CampoFormulario = CampoFormulario(),
    val descripcion: CampoFormulario = CampoFormulario(),
    val codigoQr: CampoFormulario = CampoFormulario(),
    val puntosQr: CampoFormulario = CampoFormulario("10"),
    val puntoId: Long? = null,
    val errorPunto: String? = null,
    val publicado: Boolean = false,
    /** Para saber si al guardar hay que avisar a los jugadores. */
    val estabaPublicado: Boolean = false,

    val intentoEnviar: Boolean = false,
    val guardando: Boolean = false,
    /** Evento: id de la cápsula guardada (para abrir su QR). */
    val guardadoId: Long? = null,
    /** Error devuelto por el microservicio (por ejemplo, código QR repetido). */
    val mensajeError: String? = null
) {
    val esEdicion: Boolean get() = idEdicion != null

    val puntoSeleccionado: PuntoEntity? get() = puntosDisponibles.firstOrNull { it.id == puntoId }

    val formularioValido: Boolean
        get() = titulo.valor.isNotBlank() && tema.valor.isNotBlank() && autor.valor.isNotBlank() &&
            codigoQr.valor.isNotBlank() && puntoId != null &&
            listOf(titulo, tema, autor, url, duracion, descripcion, codigoQr, puntosQr).all { it.esValido }
}

// ---------------------------------------------------------------------
// Código QR de una cápsula
// ---------------------------------------------------------------------

data class QrCapsulaUiState(
    val cargando: Boolean = true,
    val titulo: String = "",
    val codigo: String = "",
    val puntoNombre: String = "",
    /** Texto JSON que va dentro del QR (mismo formato que lee el escáner). */
    val contenidoQr: String = "",
    val mensaje: String? = null
)
