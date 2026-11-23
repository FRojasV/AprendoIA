package com.triskeledu.aprendoia.ui.editarjugador

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.triskeledu.aprendoia.aprendoApp
import com.triskeledu.aprendoia.data.local.entity.Rol
import com.triskeledu.aprendoia.data.repository.UsuarioRepository
import com.triskeledu.aprendoia.ui.navigation.Rutas
import com.triskeledu.aprendoia.util.ErroresJugador
import com.triskeledu.aprendoia.util.ValidadorJugador
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * VIEWMODEL de edición. Recibe el id del jugador por la ruta
 * "editar_jugador/{usuarioId}", carga sus datos y guarda los cambios en SQLite.
 */
class EditarJugadorViewModel(
    private val usuarioRepository: UsuarioRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val usuarioId: Long = savedStateHandle.get<Long>(Rutas.ARG_USUARIO_ID) ?: 0L

    private val _uiState = MutableStateFlow(EditarJugadorUiState())
    val uiState: StateFlow<EditarJugadorUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val usuario = usuarioRepository.obtenerPorId(usuarioId)
            _uiState.update {
                if (usuario == null) {
                    it.copy(cargando = false, mensajeError = "El jugador no existe")
                } else {
                    it.copy(
                        cargando = false,
                        original = usuario,
                        nombre = usuario.nombre,
                        correo = usuario.correo,
                        telefono = usuario.telefono,
                        direccion = usuario.direccion,
                        avatar = usuario.avatar
                    )
                }
            }
        }
    }

    fun onNombreChange(valor: String) =
        _uiState.update { it.copy(nombre = valor, errores = it.errores.copy(nombre = null)) }

    fun onCorreoChange(valor: String) =
        _uiState.update { it.copy(correo = valor, errores = it.errores.copy(correo = null)) }

    fun onTelefonoChange(valor: String) =
        _uiState.update {
            it.copy(telefono = ValidadorJugador.limpiarTelefono(valor), errores = it.errores.copy(telefono = null))
        }

    fun onDireccionChange(valor: String) = _uiState.update { it.copy(direccion = valor) }

    fun onPasswordChange(valor: String) =
        _uiState.update { it.copy(nuevaPassword = valor, errores = it.errores.copy(password = null)) }

    fun onAvatarSeleccionado(clave: String) = _uiState.update { it.copy(avatar = clave) }

    fun onAlternarPassword() = _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }

    fun onAlternarAvatares() = _uiState.update { it.copy(mostrarTodosAvatares = !it.mostrarTodosAvatares) }

    fun onMensajeMostrado() = _uiState.update { it.copy(mensajeError = null) }

    fun onGuardar() {
        val estado = _uiState.value
        val original = estado.original ?: return
        if (estado.guardando) return

        val esCliente = original.rol == Rol.CLIENTE
        val errores = ErroresJugador(
            nombre = ValidadorJugador.nombre(estado.nombre),
            correo = ValidadorJugador.correo(estado.correo),
            // Las cuentas en duro no tienen teléfono obligatorio.
            telefono = if (esCliente) ValidadorJugador.telefono(estado.telefono) else null,
            password = ValidadorJugador.password(estado.nuevaPassword, obligatoria = false)
        )
        if (errores.hayErrores) {
            _uiState.update { it.copy(errores = errores) }
            return
        }

        _uiState.update { it.copy(guardando = true) }

        val actualizado = original.copy(
            nombre = estado.nombre.trim(),
            alias = if (esCliente) estado.nombre.trim().substringBefore(" ") else original.alias,
            correo = estado.correo.trim(),
            telefono = estado.telefono.trim(),
            direccion = estado.direccion.trim(),
            avatar = estado.avatar
        )

        viewModelScope.launch {
            usuarioRepository.actualizar(actualizado, estado.nuevaPassword)
                .onSuccess { _uiState.update { it.copy(guardando = false, guardado = true) } }
                .onFailure { error ->
                    _uiState.update {
                        if (error is IllegalArgumentException) {
                            it.copy(guardando = false, errores = it.errores.copy(correo = error.message))
                        } else {
                            it.copy(guardando = false, mensajeError = error.message)
                        }
                    }
                }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                EditarJugadorViewModel(
                    usuarioRepository = aprendoApp().contenedor.usuarioRepository,
                    savedStateHandle = createSavedStateHandle()
                )
            }
        }
    }
}
