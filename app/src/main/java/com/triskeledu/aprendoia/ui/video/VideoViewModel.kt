package com.triskeledu.aprendoia.ui.video

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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * VIEWMODEL del Video. Carga la cápsula y su punto, y observa si el jugador
 * la desbloqueó y si ya la vio (se actualiza solo al marcarla como vista).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class VideoViewModel(
    private val usuarioRepository: UsuarioRepository,
    private val juegoRepository: JuegoRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val contenidoId: Long = savedStateHandle.get<Long>(Rutas.ARG_CONTENIDO_ID) ?: 0L

    private val _uiState = MutableStateFlow(VideoUiState())
    val uiState: StateFlow<VideoUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val contenido = juegoRepository.obtenerContenido(contenidoId)
            val punto = contenido?.let { juegoRepository.obtenerPunto(it.puntoId) }
            _uiState.update { it.copy(contenido = contenido, punto = punto) }

            usuarioRepository.usuarioActivoId
                .flatMapLatest { id ->
                    if (id == null) flowOf(null) else juegoRepository.observarDescubrimiento(id, contenidoId)
                }
                .collect { descubrimiento ->
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            desbloqueado = descubrimiento != null,
                            visto = descubrimiento?.visto == true
                        )
                    }
                }
        }
    }

    /** Se llama al abrir el video o al tocar "Marcar como vista". */
    fun onMarcarVisto() {
        val contenido = _uiState.value.contenido ?: return
        if (_uiState.value.visto || !_uiState.value.desbloqueado) return
        viewModelScope.launch {
            val usuarioId = usuarioRepository.usuarioActivoId.first() ?: return@launch
            if (juegoRepository.marcarVideoVisto(usuarioId, contenido)) {
                _uiState.update { it.copy(mensaje = "¡Cápsula completada! Se agregó a tu historial") }
            }
        }
    }

    fun onMensajeMostrado() = _uiState.update { it.copy(mensaje = null) }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val contenedor = aprendoApp().contenedor
                VideoViewModel(
                    usuarioRepository = contenedor.usuarioRepository,
                    juegoRepository = contenedor.juegoRepository,
                    savedStateHandle = createSavedStateHandle()
                )
            }
        }
    }
}
