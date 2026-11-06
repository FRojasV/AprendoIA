package com.triskeledu.aprendoia.ui.gestion.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.triskeledu.aprendoia.aprendoApp
import com.triskeledu.aprendoia.data.repository.GestionRepository
import com.triskeledu.aprendoia.ui.gestion.model.QrCapsulaUiState
import com.triskeledu.aprendoia.ui.navigation.Rutas
import com.triskeledu.aprendoia.util.LectorQr
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** VIEWMODEL que arma el JSON del QR de una cápsula (mismo formato que lee el escáner). */
class QrCapsulaViewModel(
    gestionRepository: GestionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val contenidoId: Long = savedStateHandle.get<Long>(Rutas.ARG_CONTENIDO_ID) ?: 0L

    private val _uiState = MutableStateFlow(QrCapsulaUiState())
    val uiState: StateFlow<QrCapsulaUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val capsula = gestionRepository.obtenerCapsula(contenidoId)
            val punto = capsula?.let { gestionRepository.obtenerPunto(it.puntoId) }
            _uiState.update {
                if (capsula == null) it.copy(cargando = false, mensaje = "La cápsula no existe")
                else it.copy(
                    cargando = false,
                    titulo = capsula.titulo,
                    codigo = capsula.codigoQr,
                    puntoNombre = punto?.nombre ?: "",
                    contenidoQr = LectorQr.crearJson(
                        codigo = capsula.codigoQr,
                        titulo = capsula.titulo,
                        descripcion = capsula.descripcion,
                        url = capsula.url
                    )
                )
            }
        }
    }

    fun mostrarMensaje(texto: String) = _uiState.update { it.copy(mensaje = texto) }

    fun mensajeMostrado() = _uiState.update { it.copy(mensaje = null) }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { QrCapsulaViewModel(aprendoApp().contenedor.gestionRepository, createSavedStateHandle()) }
        }
    }
}
