package com.triskeledu.aprendoia.ui.gestion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.triskeledu.aprendoia.aprendoApp
import com.triskeledu.aprendoia.data.local.entity.PuntoEntity
import com.triskeledu.aprendoia.data.repository.GestionRepository
import com.triskeledu.aprendoia.ui.gestion.model.ListaPuntosUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** VIEWMODEL de la lista de puntos (Administrador). */
class ListaPuntosViewModel(
    private val gestionRepository: GestionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ListaPuntosUiState())
    val uiState: StateFlow<ListaPuntosUiState> = _uiState.asStateFlow()

    init {
        // Al abrir la lista se piden al servidor los puntos más recientes.
        viewModelScope.launch {
            gestionRepository.refrescar()
                .onFailure { error -> _uiState.update { it.copy(mensaje = error.message) } }
        }
        viewModelScope.launch {
            gestionRepository.observarPuntos().collect { puntos ->
                _uiState.update { it.copy(cargando = false, puntos = puntos) }
            }
        }
    }

    fun solicitarEliminar(punto: PuntoEntity) = _uiState.update { it.copy(puntoParaEliminar = punto) }

    fun cancelarEliminar() = _uiState.update { it.copy(puntoParaEliminar = null) }

    fun confirmarEliminar() {
        val punto = _uiState.value.puntoParaEliminar ?: return
        _uiState.update { it.copy(puntoParaEliminar = null) }
        viewModelScope.launch {
            gestionRepository.eliminarPunto(punto)
                .onSuccess { _uiState.update { it.copy(mensaje = "Se eliminó \"${punto.nombre}\"") } }
                .onFailure { error -> _uiState.update { it.copy(mensaje = error.message) } }
        }
    }

    fun mensajeMostrado() = _uiState.update { it.copy(mensaje = null) }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { ListaPuntosViewModel(aprendoApp().contenedor.gestionRepository) }
        }
    }
}
