package com.triskeledu.aprendoia.ui.progreso

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.triskeledu.aprendoia.aprendoApp
import com.triskeledu.aprendoia.data.repository.JuegoRepository
import com.triskeledu.aprendoia.data.repository.UsuarioRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

/** VIEWMODEL de Progreso: estadísticas, historial y totales del juego. */
@OptIn(ExperimentalCoroutinesApi::class)
class ProgresoViewModel(
    private val usuarioRepository: UsuarioRepository,
    private val juegoRepository: JuegoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProgresoUiState())
    val uiState: StateFlow<ProgresoUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            usuarioRepository.usuarioActivoId
                .flatMapLatest { id ->
                    if (id == null) {
                        flowOf(ProgresoUiState(cargando = false))
                    } else {
                        combine(
                            juegoRepository.observarEstadisticas(id),
                            juegoRepository.observarTotalLugares(),
                            juegoRepository.observarTotalCapsulas(),
                            juegoRepository.observarHistorial(id)
                        ) { estadisticas, lugares, capsulas, historial ->
                            ProgresoUiState(
                                cargando = false,
                                estadisticas = estadisticas,
                                totalLugares = lugares,
                                totalCapsulas = capsulas,
                                historial = historial
                            )
                        }
                    }
                }
                .collect { _uiState.value = it }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val contenedor = aprendoApp().contenedor
                ProgresoViewModel(contenedor.usuarioRepository, contenedor.juegoRepository)
            }
        }
    }
}
