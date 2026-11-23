package com.triskeledu.aprendoia.ui.gestion.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.triskeledu.aprendoia.aprendoApp
import com.triskeledu.aprendoia.data.local.entity.PuntoEntity
import com.triskeledu.aprendoia.data.repository.GestionRepository
import com.triskeledu.aprendoia.ui.gestion.model.CampoFormulario
import com.triskeledu.aprendoia.ui.gestion.model.PuntoFormUiState
import com.triskeledu.aprendoia.ui.navigation.Rutas
import com.triskeledu.aprendoia.util.Coordenada
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * VIEWMODEL del formulario de punto. Sigue el ejemplo "Formularios":
 * cada cambiarX() guarda el valor y lo valida al instante;
 * enviar() revalida todo y, si es válido, guarda en SQLite.
 */
class FormularioPuntoViewModel(
    private val gestionRepository: GestionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    /** -1 = punto nuevo. */
    private val puntoId: Long = savedStateHandle.get<Long>(Rutas.ARG_PUNTO_ID) ?: -1L
    private var original: PuntoEntity? = null

    private val _uiState = MutableStateFlow(PuntoFormUiState(cargando = puntoId > 0))
    val uiState: StateFlow<PuntoFormUiState> = _uiState.asStateFlow()

    init {
        if (puntoId > 0) {
            viewModelScope.launch {
                val punto = gestionRepository.obtenerPunto(puntoId)
                original = punto
                _uiState.update {
                    if (punto == null) it.copy(cargando = false)
                    else it.copy(
                        cargando = false,
                        idEdicion = punto.id,
                        nombre = CampoFormulario(punto.nombre),
                        descripcion = CampoFormulario(punto.descripcion),
                        infoTuristica = CampoFormulario(punto.infoTuristica),
                        latitud = CampoFormulario(formatear(punto.latitud)),
                        longitud = CampoFormulario(formatear(punto.longitud)),
                        esHitoTuristico = punto.esHitoTuristico
                    )
                }
            }
        }
    }

    // --- EVENTOS DE ENTRADA ---

    fun cambiarNombre(valor: String) = _uiState.update {
        it.copy(nombre = CampoFormulario(valor, validarNombre(valor)))
    }

    fun cambiarDescripcion(valor: String) = _uiState.update {
        it.copy(descripcion = CampoFormulario(valor, validarDescripcion(valor)))
    }

    fun cambiarInfoTuristica(valor: String) = _uiState.update {
        it.copy(infoTuristica = CampoFormulario(valor, validarInfo(valor)))
    }

    /** Solo acepta números, "-" y "." (la coma se cambia por punto). */
    fun cambiarLatitud(valor: String) {
        val limpio = limpiarNumero(valor) ?: return
        _uiState.update { it.copy(latitud = CampoFormulario(limpio, validarLatitud(limpio))) }
    }

    fun cambiarLongitud(valor: String) {
        val limpio = limpiarNumero(valor) ?: return
        _uiState.update { it.copy(longitud = CampoFormulario(limpio, validarLongitud(limpio))) }
    }

    /** Al tocar el mini mapa se completan latitud y longitud. */
    fun elegirEnMapa(coordenada: Coordenada) {
        cambiarLatitud(formatear(coordenada.latitud))
        cambiarLongitud(formatear(coordenada.longitud))
    }

    fun cambiarHito(valor: Boolean) = _uiState.update { it.copy(esHitoTuristico = valor) }

    fun enviar() {
        val actual = _uiState.value
        if (actual.guardando) return
        val validado = actual.copy(
            nombre = actual.nombre.copy(error = validarNombre(actual.nombre.valor)),
            descripcion = actual.descripcion.copy(error = validarDescripcion(actual.descripcion.valor)),
            infoTuristica = actual.infoTuristica.copy(error = validarInfo(actual.infoTuristica.valor)),
            latitud = actual.latitud.copy(error = validarLatitud(actual.latitud.valor)),
            longitud = actual.longitud.copy(error = validarLongitud(actual.longitud.valor)),
            intentoEnviar = true
        )
        _uiState.value = validado
        if (!validado.formularioValido) return

        _uiState.update { it.copy(guardando = true, mensajeError = null) }
        val punto = (original ?: PuntoEntity(nombre = "", descripcion = "", latitud = 0.0, longitud = 0.0)).copy(
            nombre = validado.nombre.valor.trim(),
            descripcion = validado.descripcion.valor.trim(),
            infoTuristica = validado.infoTuristica.valor.trim(),
            latitud = validado.latitud.valor.toDouble(),
            longitud = validado.longitud.valor.toDouble(),
            esHitoTuristico = validado.esHitoTuristico
        )
        viewModelScope.launch {
            // Se guarda en ms-contenidos (POST o PUT) y luego en Room.
            gestionRepository.guardarPunto(punto)
                .onSuccess { _uiState.update { it.copy(guardando = false, guardado = true) } }
                .onFailure { error -> _uiState.update { it.copy(guardando = false, mensajeError = error.message) } }
        }
    }

    // --- REGLAS DE NEGOCIO PRIVADAS (Validaciones) ---

    private fun validarNombre(valor: String): String? = when {
        valor.isBlank() -> "El nombre es obligatorio"
        valor.trim().length < 3 -> "Escribe al menos 3 caracteres"
        valor.length > 60 -> "Máximo 60 caracteres"
        else -> null
    }

    private fun validarDescripcion(valor: String): String? = when {
        valor.isBlank() -> "La descripción es obligatoria"
        valor.trim().length < 10 -> "Escribe al menos 10 caracteres"
        valor.length > 200 -> "Máximo 200 caracteres"
        else -> null
    }

    private fun validarInfo(valor: String): String? =
        if (valor.length > 500) "Máximo 500 caracteres" else null

    private fun validarLatitud(valor: String): String? {
        val numero = valor.toDoubleOrNull()
        return when {
            valor.isBlank() -> "La latitud es obligatoria"
            numero == null -> "Debe ser un número, por ejemplo -33.4378"
            numero !in -90.0..90.0 -> "Debe estar entre -90 y 90"
            else -> null
        }
    }

    private fun validarLongitud(valor: String): String? {
        val numero = valor.toDoubleOrNull()
        return when {
            valor.isBlank() -> "La longitud es obligatoria"
            numero == null -> "Debe ser un número, por ejemplo -70.6505"
            numero !in -180.0..180.0 -> "Debe estar entre -180 y 180"
            else -> null
        }
    }

    /** Devuelve null si el texto tiene caracteres no permitidos (se descarta, como "edad" en el ejemplo). */
    private fun limpiarNumero(valor: String): String? {
        val texto = valor.replace(',', '.')
        return if (texto.all { it.isDigit() || it == '-' || it == '.' } && texto.length <= 12) texto else null
    }

    private fun formatear(numero: Double): String = String.format(Locale.US, "%.6f", numero)

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                FormularioPuntoViewModel(aprendoApp().contenedor.gestionRepository, createSavedStateHandle())
            }
        }
    }
}
