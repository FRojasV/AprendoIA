package com.triskeledu.aprendoia.ui.resultadoqr

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.triskeledu.aprendoia.aprendoApp
import com.triskeledu.aprendoia.data.local.relacion.puntajeTotal
import com.triskeledu.aprendoia.data.repository.JuegoRepository
import com.triskeledu.aprendoia.data.repository.UsuarioRepository
import com.triskeledu.aprendoia.ui.navigation.Rutas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * VIEWMODEL del Resultado. Recibe por la ruta el id de la cápsula y los
 * puntos ganados, y carga el punto turístico y el puntaje total actualizado.
 */
class ResultadoQrViewModel(
    private val usuarioRepository: UsuarioRepository,
    private val juegoRepository: JuegoRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val contenidoId: Long = savedStateHandle.get<Long>(Rutas.ARG_CONTENIDO_ID) ?: 0L
    private val puntosGanados: Int = savedStateHandle.get<Int>(Rutas.ARG_PUNTOS_GANADOS) ?: 0

    private val _uiState = MutableStateFlow(ResultadoQrUiState(puntosGanados = puntosGanados))
    val uiState: StateFlow<ResultadoQrUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val contenido = juegoRepository.obtenerContenido(contenidoId)
            val punto = contenido?.let { juegoRepository.obtenerPunto(it.puntoId) }
            val usuarioId = usuarioRepository.usuarioActivoId.first()

            val total = usuarioId?.let { juegoRepository.observarEstadisticas(it).first().puntajeTotal } ?: 0
            val tieneSelfie = if (usuarioId != null && punto != null) {
                juegoRepository.observarTieneSelfie(usuarioId, punto.id).first()
            } else false

            _uiState.update {
                it.copy(
                    cargando = false,
                    contenido = contenido,
                    punto = punto,
                    puntajeTotal = total,
                    tieneSelfie = tieneSelfie
                )
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val contenedor = aprendoApp().contenedor
                ResultadoQrViewModel(
                    usuarioRepository = contenedor.usuarioRepository,
                    juegoRepository = contenedor.juegoRepository,
                    savedStateHandle = createSavedStateHandle()
                )
            }
        }
    }
}
