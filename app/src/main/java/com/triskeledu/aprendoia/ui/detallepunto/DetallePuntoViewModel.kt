package com.triskeledu.aprendoia.ui.detallepunto

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.triskeledu.aprendoia.aprendoApp
import com.triskeledu.aprendoia.data.repository.ClimaRepository
import com.triskeledu.aprendoia.data.repository.JuegoRepository
import com.triskeledu.aprendoia.ui.components.EstadoClima
import com.triskeledu.aprendoia.data.repository.UsuarioRepository
import com.triskeledu.aprendoia.ui.navigation.Rutas
import com.triskeledu.aprendoia.util.Coordenada
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * VIEWMODEL del Detalle. Recibe el id por la ruta "detalle_punto/{puntoId}"
 * y combina: datos del punto + cápsulas con su estado + si ya tiene selfie.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class DetallePuntoViewModel(
    private val usuarioRepository: UsuarioRepository,
    private val juegoRepository: JuegoRepository,
    private val climaRepository: ClimaRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val puntoId: Long = savedStateHandle.get<Long>(Rutas.ARG_PUNTO_ID) ?: 0L

    private val _uiState = MutableStateFlow(DetallePuntoUiState())
    val uiState: StateFlow<DetallePuntoUiState> = _uiState.asStateFlow()

    init {
        cargarClima()
        viewModelScope.launch {
            usuarioRepository.usuarioActivoId
                .flatMapLatest { usuarioId ->
                    val id = usuarioId ?: 0L
                    combine(
                        juegoRepository.observarPunto(puntoId),
                        if (usuarioId == null) flowOf(emptyList())
                        else juegoRepository.observarContenidosDePunto(puntoId, id),
                        if (usuarioId == null) flowOf(false)
                        else juegoRepository.observarTieneSelfie(id, puntoId)
                    ) { punto, contenidos, tieneSelfie -> Triple(punto, contenidos, tieneSelfie) }
                }
                .collect { (punto, contenidos, tieneSelfie) ->
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            noExiste = punto == null,
                            punto = punto,
                            contenidos = contenidos,
                            tieneSelfie = tieneSelfie
                        )
                    }
                }
        }
    }

    fun onUbicacion(coordenada: Coordenada) = _uiState.update { it.copy(ubicacion = coordenada) }

    /** API externa (Open-Meteo): clima actual en las coordenadas del punto. */
    fun cargarClima() {
        _uiState.update { it.copy(clima = EstadoClima(cargando = true)) }
        viewModelScope.launch {
            val punto = juegoRepository.obtenerPunto(puntoId)
            if (punto == null) {
                _uiState.update { it.copy(clima = EstadoClima(error = "Punto no encontrado")) }
                return@launch
            }
            climaRepository.climaActual(punto.latitud, punto.longitud)
                .onSuccess { clima -> _uiState.update { it.copy(clima = EstadoClima(clima = clima)) } }
                .onFailure { _uiState.update { it.copy(clima = EstadoClima(error = "No se pudo obtener el clima")) } }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val contenedor = aprendoApp().contenedor
                DetallePuntoViewModel(
                    usuarioRepository = contenedor.usuarioRepository,
                    juegoRepository = contenedor.juegoRepository,
                    climaRepository = contenedor.climaRepository,
                    savedStateHandle = createSavedStateHandle()
                )
            }
        }
    }
}
