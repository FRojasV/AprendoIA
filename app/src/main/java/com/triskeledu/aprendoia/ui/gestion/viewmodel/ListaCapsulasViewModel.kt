package com.triskeledu.aprendoia.ui.gestion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.triskeledu.aprendoia.aprendoApp
import com.triskeledu.aprendoia.data.local.entity.Rol
import com.triskeledu.aprendoia.data.local.relacion.CapsulaGestion
import com.triskeledu.aprendoia.data.repository.GestionRepository
import com.triskeledu.aprendoia.data.repository.UsuarioRepository
import com.triskeledu.aprendoia.ui.gestion.model.FiltroCapsulasGestion
import com.triskeledu.aprendoia.ui.gestion.model.ListaCapsulasUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * VIEWMODEL de la lista de cápsulas.
 * Administrador: publica, edita y elimina. Creador: crea y edita sus pendientes.
 */
class ListaCapsulasViewModel(
    usuarioRepository: UsuarioRepository,
    private val gestionRepository: GestionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ListaCapsulasUiState())
    val uiState: StateFlow<ListaCapsulasUiState> = _uiState.asStateFlow()

    init {
        // Al abrir la lista se piden al servidor las cápsulas más recientes.
        viewModelScope.launch {
            gestionRepository.refrescar()
                .onFailure { error -> _uiState.update { it.copy(mensaje = error.message) } }
        }
        viewModelScope.launch {
            combine(usuarioRepository.usuarioActivo, gestionRepository.observarCapsulas()) { usuario, capsulas ->
                (usuario?.rol == Rol.ADMIN) to capsulas
            }.collect { (esAdmin, capsulas) ->
                _uiState.update { it.copy(cargando = false, esAdmin = esAdmin, capsulas = capsulas) }
            }
        }
    }

    fun cambiarFiltro(filtro: FiltroCapsulasGestion) = _uiState.update { it.copy(filtro = filtro) }

    fun publicar(capsula: CapsulaGestion) {
        if (!_uiState.value.esAdmin) return
        viewModelScope.launch {
            gestionRepository.publicar(capsula.contenido)
                .onSuccess { _uiState.update { it.copy(mensaje = "Publicada. Se avisó a los jugadores 🎉") } }
                .onFailure { error -> _uiState.update { it.copy(mensaje = error.message) } }
        }
    }

    fun solicitarEliminar(capsula: CapsulaGestion) = _uiState.update { it.copy(capsulaParaEliminar = capsula) }

    fun cancelarEliminar() = _uiState.update { it.copy(capsulaParaEliminar = null) }

    fun confirmarEliminar() {
        val capsula = _uiState.value.capsulaParaEliminar ?: return
        _uiState.update { it.copy(capsulaParaEliminar = null) }
        viewModelScope.launch {
            gestionRepository.eliminarCapsula(capsula.contenido)
                .onSuccess { _uiState.update { it.copy(mensaje = "Se eliminó \"${capsula.contenido.titulo}\"") } }
                .onFailure { error -> _uiState.update { it.copy(mensaje = error.message) } }
        }
    }

    fun mensajeMostrado() = _uiState.update { it.copy(mensaje = null) }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val c = aprendoApp().contenedor
                ListaCapsulasViewModel(c.usuarioRepository, c.gestionRepository)
            }
        }
    }
}
