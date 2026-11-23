package com.triskeledu.aprendoia.ui.registro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.triskeledu.aprendoia.aprendoApp
import com.triskeledu.aprendoia.data.repository.JuegoRepository
import com.triskeledu.aprendoia.data.repository.UsuarioRepository
import com.triskeledu.aprendoia.util.ErroresJugador
import com.triskeledu.aprendoia.util.ValidadorJugador
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

//Usuario → Composable → ViewModel → MutableStateFlow → StateFlow → Compose → Pantalla

class RegistroViewModel(
    private val usuarioRepository: UsuarioRepository,
    private val juegoRepository: JuegoRepository
) : ViewModel() {

    //
    private val _uiState = MutableStateFlow(RegistroUiState())


    val uiState: StateFlow<RegistroUiState> = _uiState.asStateFlow()


    fun onNombreChange(valor: String) =
        _uiState.update { it.copy(nombre = valor, errores = it.errores.copy(nombre = null)) }

    fun onCorreoChange(valor: String) =
        _uiState.update { it.copy(correo = valor, errores = it.errores.copy(correo = null)) }

    fun onTelefonoChange(valor: String) =
        _uiState.update {
            it.copy(telefono = ValidadorJugador.limpiarTelefono(valor), errores = it.errores.copy(telefono = null))
        }

    fun onPasswordChange(valor: String) =
        _uiState.update { it.copy(password = valor, errores = it.errores.copy(password = null)) }

    fun onDireccionChange(valor: String) =
        _uiState.update { it.copy(direccion = valor) }

    fun onAvatarSeleccionado(clave: String) =
        _uiState.update { it.copy(avatar = clave) }

    fun onAlternarPassword() =
        _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }

    fun onAlternarAvatares() =
        _uiState.update { it.copy(mostrarTodosAvatares = !it.mostrarTodosAvatares) }

    fun onMensajeMostrado() =
        _uiState.update { it.copy(mensajeError = null) }

    // ---------- Acción principal ----------

    fun onCrearJugador() {
        val estado = _uiState.value
        if (estado.guardando) return

        val errores = ErroresJugador(
            nombre = ValidadorJugador.nombre(estado.nombre),
            correo = ValidadorJugador.correo(estado.correo),
            telefono = ValidadorJugador.telefono(estado.telefono),
            password = ValidadorJugador.password(estado.password)
        )
        if (errores.hayErrores) {
            _uiState.update { it.copy(errores = errores) }
            return
        }

        _uiState.update { it.copy(guardando = true, errores = ErroresJugador()) }

        viewModelScope.launch {
            usuarioRepository.registrarCliente(
                nombre = estado.nombre,
                correo = estado.correo,
                telefono = estado.telefono,
                password = estado.password,
                direccion = estado.direccion,
                avatar = estado.avatar
            ).onSuccess { nuevoId ->
                juegoRepository.crearBienvenida(nuevoId, estado.nombre.trim().substringBefore(" "))
                _uiState.update { it.copy(guardando = false, registroExitoso = true) }
            }.onFailure { error ->
                _uiState.update {
                    if (error is IllegalArgumentException) {
                        // Error de negocio (correo repetido): se muestra bajo el campo.
                        it.copy(guardando = false, errores = it.errores.copy(correo = error.message))
                    } else {
                        // Sin conexión u otro error del microservicio ms-usuarios.
                        it.copy(guardando = false, mensajeError = error.message ?: "No fue posible crear el jugador")
                    }
                }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val contenedor = aprendoApp().contenedor
                RegistroViewModel(contenedor.usuarioRepository, contenedor.juegoRepository)
            }
        }
    }
}
