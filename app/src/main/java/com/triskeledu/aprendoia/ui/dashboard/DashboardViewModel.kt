package com.triskeledu.aprendoia.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.triskeledu.aprendoia.aprendoApp
import com.triskeledu.aprendoia.data.repository.JuegoRepository
import com.triskeledu.aprendoia.data.repository.UsuarioRepository
import com.triskeledu.aprendoia.util.Coordenada
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
    private val juegoRepository: JuegoRepository
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
                .collect { nuevoEstado -> _uiState.value = nuevoEstado }
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
                DashboardViewModel(contenedor.usuarioRepository, contenedor.juegoRepository)
            }
        }
    }
}
