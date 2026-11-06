package com.triskeledu.aprendoia.ui.mapa

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.triskeledu.aprendoia.aprendoApp
import com.triskeledu.aprendoia.data.repository.JuegoRepository
import com.triskeledu.aprendoia.data.repository.UsuarioRepository
import com.triskeledu.aprendoia.util.Coordenada
import com.triskeledu.aprendoia.util.Ubicacion
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


@OptIn(ExperimentalCoroutinesApi::class)
class MapaViewModel(
    private val usuarioRepository: UsuarioRepository,
    private val juegoRepository: JuegoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapaUiState())
    val uiState: StateFlow<MapaUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            usuarioRepository.usuarioActivoId
                .flatMapLatest { id ->
                    if (id == null) flowOf(emptyList()) else juegoRepository.observarPuntosConEstado(id)
                }
                .collect { puntos ->
                    _uiState.update { it.copy(cargando = false, puntos = puntos) }
                }
        }
    }

    fun onFiltroChange(filtro: FiltroMapa) = _uiState.update {
        // Si el punto seleccionado queda oculto por el filtro, se cierra la tarjeta.
        val nuevo = it.copy(filtro = filtro)
        if (nuevo.puntosVisibles.none { p -> p.punto.id == it.seleccionadoId }) nuevo.copy(seleccionadoId = null)
        else nuevo
    }

    fun onBusquedaChange(texto: String) =
        _uiState.update { it.copy(busqueda = texto, mostrarResultados = texto.isNotBlank()) }

    fun onLimpiarBusqueda() =
        _uiState.update { it.copy(busqueda = "", mostrarResultados = false) }

    /** Desde un marcador o desde un resultado de búsqueda. */
    fun onPuntoSeleccionado(puntoId: Long) = _uiState.update {
        // Si el punto no está visible con el filtro actual, se vuelve a "Todos".
        val filtro = if (it.puntosVisibles.any { p -> p.punto.id == puntoId }) it.filtro else FiltroMapa.TODOS
        it.copy(seleccionadoId = puntoId, mostrarResultados = false, filtro = filtro)
    }

    fun onCerrarSeleccion() = _uiState.update { it.copy(seleccionadoId = null) }

    /** Última posición en la que se revisaron puntos cercanos. */
    private var ultimaRevision: Coordenada? = null

    fun onUbicacion(coordenada: Coordenada) {
        if (_uiState.value.ubicacion != coordenada) {
            _uiState.update { it.copy(ubicacion = coordenada) }
        }
        // Se revisa la cercanía solo si el jugador se movió más de 25 m (ahorra consultas).
        val anterior = ultimaRevision
        if (anterior == null || Ubicacion.distanciaMetros(anterior, coordenada) > 25f) {
            ultimaRevision = coordenada
            viewModelScope.launch {
                usuarioRepository.usuarioActivoId.first()?.let { id ->
                    juegoRepository.verificarCercania(id, coordenada)
                }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val contenedor = aprendoApp().contenedor
                MapaViewModel(contenedor.usuarioRepository, contenedor.juegoRepository)
            }
        }
    }
}
