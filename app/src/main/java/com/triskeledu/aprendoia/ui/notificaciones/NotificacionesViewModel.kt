package com.triskeledu.aprendoia.ui.notificaciones

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.triskeledu.aprendoia.aprendoApp
import com.triskeledu.aprendoia.data.local.entity.NotificacionEntity
import com.triskeledu.aprendoia.data.repository.JuegoRepository
import com.triskeledu.aprendoia.data.repository.UsuarioRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** VIEWMODEL de la bandeja de notificaciones del jugador activo. */
@OptIn(ExperimentalCoroutinesApi::class)
class NotificacionesViewModel(
    private val usuarioRepository: UsuarioRepository,
    private val juegoRepository: JuegoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificacionesUiState())
    val uiState: StateFlow<NotificacionesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            usuarioRepository.usuarioActivoId
                .flatMapLatest { id ->
                    if (id == null) flowOf(emptyList<NotificacionEntity>())
                    else juegoRepository.observarNotificaciones(id)
                }
                .collect { lista -> _uiState.update { it.copy(cargando = false, notificaciones = lista) } }
        }
    }

    fun onFiltro(filtro: FiltroNotificaciones) = _uiState.update { it.copy(filtro = filtro) }

    /** Al tocar un aviso: se marca como leído y, si tiene destino, se navega. */
    fun onAbrir(notificacion: NotificacionEntity) {
        viewModelScope.launch {
            if (!notificacion.leida) juegoRepository.marcarNotificacionLeida(notificacion.id)
            notificacion.destino?.let { destino -> _uiState.update { it.copy(irA = destino) } }
        }
    }

    fun onNavegacionRealizada() = _uiState.update { it.copy(irA = null) }

    fun onMarcarTodas() {
        viewModelScope.launch {
            usuarioRepository.usuarioActivoId.first()?.let { juegoRepository.marcarTodasLeidas(it) }
        }
    }

    fun onEliminar(notificacion: NotificacionEntity) {
        viewModelScope.launch { juegoRepository.eliminarNotificacion(notificacion.id) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val contenedor = aprendoApp().contenedor
                NotificacionesViewModel(contenedor.usuarioRepository, contenedor.juegoRepository)
            }
        }
    }
}
