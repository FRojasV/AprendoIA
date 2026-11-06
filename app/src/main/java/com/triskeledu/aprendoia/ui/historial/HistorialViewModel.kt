package com.triskeledu.aprendoia.ui.historial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.triskeledu.aprendoia.aprendoApp
import com.triskeledu.aprendoia.data.local.entity.HistorialEntity
import com.triskeledu.aprendoia.data.repository.JuegoRepository
import com.triskeledu.aprendoia.data.repository.UsuarioRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** VIEWMODEL del Historial: actividades del jugador activo desde SQLite. */
@OptIn(ExperimentalCoroutinesApi::class)
class HistorialViewModel(
    private val usuarioRepository: UsuarioRepository,
    private val juegoRepository: JuegoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistorialUiState())
    val uiState: StateFlow<HistorialUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            usuarioRepository.usuarioActivoId
                .flatMapLatest { id ->
                    if (id == null) flowOf(emptyList<HistorialEntity>()) else juegoRepository.observarHistorial(id)
                }
                .collect { lista -> _uiState.update { it.copy(cargando = false, actividades = lista) } }
        }
    }

    fun onFiltro(filtro: FiltroHistorial) = _uiState.update { it.copy(filtro = filtro) }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val contenedor = aprendoApp().contenedor
                HistorialViewModel(contenedor.usuarioRepository, contenedor.juegoRepository)
            }
        }
    }
}
