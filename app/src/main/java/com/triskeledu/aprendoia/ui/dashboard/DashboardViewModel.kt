package com.triskeledu.aprendoia.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.triskeledu.aprendoia.aprendoApp
import com.triskeledu.aprendoia.data.repository.JuegoRepository
import com.triskeledu.aprendoia.data.repository.UsuarioRepository
import com.triskeledu.aprendoia.data.repository.ClimaRepository
import com.triskeledu.aprendoia.data.repository.SincronizacionRepository
import com.triskeledu.aprendoia.ui.components.EstadoClima
import com.triskeledu.aprendoia.util.CENTRO_SANTIAGO
import com.triskeledu.aprendoia.util.Coordenada
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch


@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModel(
    private val usuarioRepository: UsuarioRepository,
    private val juegoRepository: JuegoRepository,
    private val sincronizacionRepository: SincronizacionRepository,
    private val climaRepository: ClimaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            usuarioRepository.usuarioActivo
                .flatMapLatest { usuario ->
                    if (usuario == null) {
                        flowOf(DashboardUiState(cargando = false, sinSesion = true))
                    } else {
                        combine(
                            juegoRepository.observarEstadisticas(usuario.id),
                            juegoRepository.observarTotalLugares(),
                            juegoRepository.observarDesafioActual(usuario.id),
                            juegoRepository.observarNotificacionesNoLeidas(usuario.id)
                        ) { estadisticas, totalLugares, desafio, noLeidas ->
                            DashboardUiState(
                                cargando = false,
                                usuario = usuario,
                                estadisticas = estadisticas,
                                totalLugares = totalLugares,
                                desafio = desafio,
                                notificacionesNoLeidas = noLeidas
                            )
                        }
                    }
                }
                // Se conservan el clima y el aviso de conexión (vienen de otras fuentes).
                .collect { nuevoEstado ->
                    _uiState.update { nuevoEstado.copy(clima = it.clima, avisoConexion = it.avisoConexion) }
                }
        }
        // Estado de la conexión con los microservicios.
        viewModelScope.launch {
            sincronizacionRepository.estado.collect { conexion ->
                _uiState.update { it.copy(avisoConexion = if (conexion.conectado == false) conexion.mensaje else null) }
            }
        }
        sincronizar()
        cargarClima()
    }

    /** Trae del servidor contenidos y progreso del jugador (y sube lo hecho sin conexión). */
    fun sincronizar() {
        viewModelScope.launch {
            sincronizacionRepository.sincronizarContenidos()
            usuarioRepository.usuarioActivoId.first()?.let { sincronizacionRepository.sincronizarProgreso(it) }
        }
    }

    /** API externa: clima actual en el centro de Santiago (Plaza de Armas). */
    fun cargarClima() {
        _uiState.update { it.copy(clima = EstadoClima(cargando = true)) }
        viewModelScope.launch {
            climaRepository.climaActual(CENTRO_SANTIAGO.latitud, CENTRO_SANTIAGO.longitud)
                .onSuccess { clima -> _uiState.update { it.copy(clima = EstadoClima(clima = clima)) } }
                .onFailure { _uiState.update { it.copy(clima = EstadoClima(error = "No se pudo obtener el clima")) } }
        }
    }

    /** Al abrir el Dashboard se revisa si hay un punto pendiente cerca. */
    fun onUbicacion(coordenada: Coordenada) {
        val usuario = _uiState.value.usuario ?: return
        viewModelScope.launch { juegoRepository.verificarCercania(usuario.id, coordenada) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val contenedor = aprendoApp().contenedor
                DashboardViewModel(
                    contenedor.usuarioRepository,
                    contenedor.juegoRepository,
                    contenedor.sincronizacionRepository,
                    contenedor.climaRepository
                )
            }
        }
    }
}
