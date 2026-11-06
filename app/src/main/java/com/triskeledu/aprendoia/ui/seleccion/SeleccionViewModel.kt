package com.triskeledu.aprendoia.ui.seleccion

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.triskeledu.aprendoia.aprendoApp
import com.triskeledu.aprendoia.data.local.entity.Rol
import com.triskeledu.aprendoia.data.local.relacion.JugadorResumen
import com.triskeledu.aprendoia.data.repository.UsuarioRepository
import com.triskeledu.aprendoia.ui.navigation.Rutas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * VIEWMODEL de la pantalla 3. Escucha la lista de jugadores desde SQLite
 * (se actualiza sola al registrar, editar o eliminar) y valida contraseñas.
 */
class SeleccionViewModel(
    private val usuarioRepository: UsuarioRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val modoAdminSolicitado: Boolean =
        savedStateHandle.get<Boolean>(Rutas.ARG_MODO_ADMIN) ?: false

    private val _uiState = MutableStateFlow(SeleccionUiState())
    val uiState: StateFlow<SeleccionUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                usuarioRepository.observarJugadores(),
                usuarioRepository.usuarioActivoId
            ) { jugadores, activoId -> jugadores to activoId }
                .collect { (jugadores, activoId) ->
                    val activoEsAdmin = jugadores.any {
                        it.usuario.id == activoId && it.usuario.rol == Rol.ADMIN
                    }
                    val modoAdmin = modoAdminSolicitado && activoEsAdmin
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            // En modo administrador solo se gestionan Clientes.
                            jugadores = if (modoAdmin) jugadores.filter { j -> j.usuario.rol == Rol.CLIENTE }
                            else jugadores,
                            usuarioActivoId = activoId,
                            modoAdmin = modoAdmin
                        )
                    }
                }
        }
    }

    // ---------- Login ----------

    fun onJugadorSeleccionado(jugador: JugadorResumen) = _uiState.update {
        it.copy(jugadorParaLogin = jugador, password = "", errorPassword = null, passwordVisible = false)
    }

    fun onPasswordChange(valor: String) =
        _uiState.update { it.copy(password = valor, errorPassword = null) }

    fun onAlternarPassword() =
        _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }

    fun onCancelarLogin() =
        _uiState.update { it.copy(jugadorParaLogin = null, password = "", errorPassword = null) }

    fun onIngresar() {
        val estado = _uiState.value
        val jugador = estado.jugadorParaLogin ?: return
        if (estado.validando) return
        if (estado.password.isBlank()) {
            _uiState.update { it.copy(errorPassword = "Ingresa la contraseña") }
            return
        }
        _uiState.update { it.copy(validando = true) }

        viewModelScope.launch {
            usuarioRepository.iniciarSesion(jugador.usuario.id, estado.password)
                .onSuccess { usuario ->
                    _uiState.update {
                        it.copy(validando = false, jugadorParaLogin = null, password = "", rolIngresado = usuario.rol)
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(validando = false, errorPassword = error.message ?: "No fue posible ingresar")
                    }
                }
        }
    }

    /** La pantalla avisa que ya navegó, para no repetir la navegación. */
    fun onNavegacionRealizada() = _uiState.update { it.copy(rolIngresado = null) }

    // ---------- Eliminación (Administrador) ----------

    fun onSolicitarEliminar(jugador: JugadorResumen) =
        _uiState.update { it.copy(jugadorParaEliminar = jugador) }

    fun onCancelarEliminar() =
        _uiState.update { it.copy(jugadorParaEliminar = null) }

    fun onConfirmarEliminar() {
        val jugador = _uiState.value.jugadorParaEliminar ?: return
        _uiState.update { it.copy(jugadorParaEliminar = null) }
        viewModelScope.launch {
            usuarioRepository.eliminar(jugador.usuario)
                .onSuccess { _uiState.update { it.copy(mensaje = "Se eliminó a ${jugador.usuario.nombre}") } }
                .onFailure { e -> _uiState.update { it.copy(mensaje = e.message ?: "No fue posible eliminar") } }
        }
    }

    fun onMensajeMostrado() = _uiState.update { it.copy(mensaje = null) }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SeleccionViewModel(
                    usuarioRepository = aprendoApp().contenedor.usuarioRepository,
                    savedStateHandle = createSavedStateHandle()
                )
            }
        }
    }
}
