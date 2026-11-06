package com.triskeledu.aprendoia.ui.gestion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.triskeledu.aprendoia.aprendoApp
import com.triskeledu.aprendoia.data.repository.GestionRepository
import com.triskeledu.aprendoia.data.repository.UsuarioRepository
import com.triskeledu.aprendoia.ui.gestion.model.PanelUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** VIEWMODEL del panel principal de gestión. */
class PanelGestionViewModel(
    private val usuarioRepository: UsuarioRepository,
    gestionRepository: GestionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PanelUiState())
    val uiState: StateFlow<PanelUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(usuarioRepository.usuarioActivo, gestionRepository.observarResumen()) { usuario, resumen ->
                usuario to resumen
            }.collect { (usuario, resumen) ->
                _uiState.update { it.copy(usuario = usuario, resumen = resumen) }
            }
        }
    }

    fun cerrarSesion() {
        viewModelScope.launch {
            usuarioRepository.cerrarSesion()
            _uiState.update { it.copy(sesionCerrada = true) }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val c = aprendoApp().contenedor
                PanelGestionViewModel(c.usuarioRepository, c.gestionRepository)
            }
        }
    }
}
