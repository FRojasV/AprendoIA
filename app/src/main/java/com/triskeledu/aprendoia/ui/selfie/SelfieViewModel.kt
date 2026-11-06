package com.triskeledu.aprendoia.ui.selfie

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.triskeledu.aprendoia.aprendoApp
import com.triskeledu.aprendoia.data.repository.JuegoRepository
import com.triskeledu.aprendoia.data.repository.UsuarioRepository
import com.triskeledu.aprendoia.ui.navigation.Rutas
import com.triskeledu.aprendoia.util.GaleriaFotos
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * VIEWMODEL de la Selfie. Valida las reglas del caso (hito turístico +
 * QR escaneado + una sola selfie), guarda la foto en la galería y suma +100.
 * Abrir la cámara lo hace el Composable (necesita un "launcher" de Activity).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SelfieViewModel(
    private val usuarioRepository: UsuarioRepository,
    private val juegoRepository: JuegoRepository,
    private val galeria: GaleriaFotos,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val puntoId: Long = savedStateHandle.get<Long>(Rutas.ARG_PUNTO_ID) ?: 0L

    private val _uiState = MutableStateFlow(SelfieUiState())
    val uiState: StateFlow<SelfieUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val punto = juegoRepository.obtenerPunto(puntoId)
            // Se lee una sola vez si ya tenía selfie, para que al guardar no aparezca "bloqueado".
            val usuarioId = usuarioRepository.usuarioActivoId.first()
            val teniaSelfie = usuarioId?.let { juegoRepository.observarTieneSelfie(it, puntoId).first() } ?: false
            _uiState.update { it.copy(punto = punto, tieneSelfie = teniaSelfie) }

            usuarioRepository.usuarioActivoId
                .flatMapLatest { id ->
                    if (id == null) flowOf(false)
                    else juegoRepository.observarContenidosDePunto(puntoId, id).map { lista -> lista.any { c -> c.descubierto } }
                }
                .collect { escaneado -> _uiState.update { it.copy(cargando = false, qrEscaneado = escaneado) } }
        }
    }

    /** Prepara el archivo donde la cámara guardará la foto y devuelve su Uri. */
    fun prepararFoto(): Uri {
        val uri = galeria.crearUriTemporal()
        _uiState.update { it.copy(uriPendiente = uri.toString(), error = null) }
        return uri
    }

    /** Respuesta de la app de cámara: true si el usuario tomó la foto. */
    fun onResultadoCamara(exito: Boolean) = _uiState.update {
        if (exito) it.copy(fotoTomada = it.uriPendiente, uriPendiente = null)
        else it.copy(uriPendiente = null)
    }

    fun onPermisoDenegado() =
        _uiState.update { it.copy(error = "Necesitamos acceso a la cámara para la selfie.") }

    fun onDescartar() = _uiState.update { it.copy(fotoTomada = null) }

    fun onGuardar() {
        val estado = _uiState.value
        val foto = estado.fotoTomada ?: return
        val punto = estado.punto ?: return
        if (estado.guardando) return
        _uiState.update { it.copy(guardando = true, error = null) }

        viewModelScope.launch {
            val usuarioId = usuarioRepository.usuarioActivoId.first()
            if (usuarioId == null) {
                _uiState.update { it.copy(guardando = false, error = "No hay jugador activo") }
                return@launch
            }
            // 1) Copiar la foto a la galería del teléfono
            val uriGaleria = galeria.guardarEnGaleria(Uri.parse(foto), punto.nombre)
            if (uriGaleria == null) {
                _uiState.update { it.copy(guardando = false, error = "No se pudo guardar la foto en la galería") }
                return@launch
            }
            // 2) Registrar la selfie y los puntos en SQLite
            val puntos = juegoRepository.registrarSelfie(usuarioId, punto, uriGaleria.toString())
            galeria.limpiarTemporales()
            _uiState.update {
                it.copy(
                    guardando = false,
                    fotoTomada = uriGaleria.toString(),
                    puntosGanados = puntos ?: 0,
                    tieneSelfie = true
                )
            }
        }
    }

    suspend fun cargarMiniatura(uri: String): Bitmap? = galeria.cargarMiniatura(Uri.parse(uri))

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val contenedor = aprendoApp().contenedor
                SelfieViewModel(
                    usuarioRepository = contenedor.usuarioRepository,
                    juegoRepository = contenedor.juegoRepository,
                    galeria = contenedor.galeria,
                    savedStateHandle = createSavedStateHandle()
                )
            }
        }
    }
}
