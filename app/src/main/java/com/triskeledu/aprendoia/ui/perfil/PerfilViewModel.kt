package com.triskeledu.aprendoia.ui.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.triskeledu.aprendoia.aprendoApp
import com.triskeledu.aprendoia.data.local.relacion.EstadisticasJugador
import com.triskeledu.aprendoia.data.repository.JuegoRepository
import com.triskeledu.aprendoia.data.repository.UsuarioRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * VIEWMODEL del Perfil: muestra datos y estadísticas del jugador activo y
 * guarda en SQLite el avatar y la preferencia de notificaciones.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PerfilViewModel(
    private val usuarioRepository: UsuarioRepository,
    private val juegoRepository: JuegoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            usuarioRepository.usuarioActivo
                .flatMapLatest { usuario ->
                    if (usuario == null) flowOf(null to EstadisticasJugador(0, 0, 0, 0, 0, 0))
                    else juegoRepository.observarEstadisticas(usuario.id).map { usuario to it }
                }
                .collect { (usuario, estadisticas) ->
                    _uiState.update {
                        it.copy(cargando = false, usuario = usuario, estadisticas = estadisticas)
                    }
                }
        }
    }

    // ---------- Avatar ----------

    fun onCambiarAvatarClick() = _uiState.update {
        it.copy(mostrarDialogoAvatar = true, avatarTemporal = it.usuario?.avatar ?: "avatar_1")
    }

    fun onAvatarTemporal(clave: String) = _uiState.update { it.copy(avatarTemporal = clave) }

    fun onCancelarAvatar() = _uiState.update { it.copy(mostrarDialogoAvatar = false) }

    fun onGuardarAvatar() {
        val estado = _uiState.value
        val usuario = estado.usuario ?: return
        _uiState.update { it.copy(mostrarDialogoAvatar = false) }
        viewModelScope.launch {
            usuarioRepository.actualizar(usuario.copy(avatar = estado.avatarTemporal))
                .onSuccess { _uiState.update { it.copy(mensaje = "Avatar actualizado") } }
        }
    }

    // ---------- Notificaciones ----------

    fun onNotificacionesChange(activas: Boolean) {
        val usuario = _uiState.value.usuario ?: return
        viewModelScope.launch {
            usuarioRepository.actualizar(usuario.copy(notificacionesActivas = activas))
            _uiState.update {
                it.copy(mensaje = if (activas) "Notificaciones activadas" else "Notificaciones desactivadas")
            }
        }
    }

    fun onPermisoNotificacionDenegado() =
        _uiState.update { it.copy(mensaje = "Sin permiso no se pueden mostrar notificaciones") }

    // ---------- Diálogos simples ----------

    fun onMostrarPermisos(mostrar: Boolean) = _uiState.update { it.copy(mostrarDialogoPermisos = mostrar) }

    fun onMostrarAyuda(mostrar: Boolean) = _uiState.update { it.copy(mostrarAyuda = mostrar) }

    fun onMostrarConfirmarCierre(mostrar: Boolean) = _uiState.update { it.copy(mostrarConfirmarCierre = mostrar) }

    fun onMensajeMostrado() = _uiState.update { it.copy(mensaje = null) }

    // ---------- Cerrar sesión ----------

    fun onCerrarSesion() {
        _uiState.update { it.copy(mostrarConfirmarCierre = false) }
        viewModelScope.launch {
            usuarioRepository.cerrarSesion()
            _uiState.update { it.copy(sesionCerrada = true) }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val contenedor = aprendoApp().contenedor
                PerfilViewModel(contenedor.usuarioRepository, contenedor.juegoRepository)
            }
        }
    }
}
