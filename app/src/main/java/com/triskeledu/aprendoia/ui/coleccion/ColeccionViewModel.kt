package com.triskeledu.aprendoia.ui.coleccion

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.triskeledu.aprendoia.aprendoApp
import com.triskeledu.aprendoia.data.local.relacion.DescubrimientoDetalle
import com.triskeledu.aprendoia.data.local.relacion.SelfieDetalle
import com.triskeledu.aprendoia.data.repository.JuegoRepository
import com.triskeledu.aprendoia.data.repository.UsuarioRepository
import com.triskeledu.aprendoia.util.GaleriaFotos
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** VIEWMODEL de la Colección: cápsulas descubiertas y selfies del jugador activo. */
@OptIn(ExperimentalCoroutinesApi::class)
class ColeccionViewModel(
    private val usuarioRepository: UsuarioRepository,
    private val juegoRepository: JuegoRepository,
    private val galeria: GaleriaFotos
) : ViewModel() {

    private val _uiState = MutableStateFlow(ColeccionUiState())
    val uiState: StateFlow<ColeccionUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            usuarioRepository.usuarioActivoId
                .flatMapLatest { id ->
                    if (id == null) {
                        flowOf(Triple(emptyList<DescubrimientoDetalle>(), emptyList<SelfieDetalle>(), 0))
                    } else {
                        combine(
                            juegoRepository.observarColeccion(id),
                            juegoRepository.observarSelfies(id),
                            juegoRepository.observarTotalCapsulas()
                        ) { capsulas, selfies, total -> Triple(capsulas, selfies, total) }
                    }
                }
                .collect { (capsulas, selfies, total) ->
                    _uiState.update {
                        it.copy(cargando = false, capsulas = capsulas, selfies = selfies, totalCapsulas = total)
                    }
                }
        }
    }

    fun onPestana(pestana: PestanaColeccion) = _uiState.update { it.copy(pestana = pestana) }

    fun onFiltro(filtro: FiltroCapsulas) = _uiState.update { it.copy(filtro = filtro) }

    suspend fun cargarMiniatura(uri: String): Bitmap? = galeria.cargarMiniatura(Uri.parse(uri), ladoMaximo = 400)

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val contenedor = aprendoApp().contenedor
                ColeccionViewModel(contenedor.usuarioRepository, contenedor.juegoRepository, contenedor.galeria)
            }
        }
    }
}
