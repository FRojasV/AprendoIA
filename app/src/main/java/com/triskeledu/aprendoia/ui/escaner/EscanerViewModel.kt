package com.triskeledu.aprendoia.ui.escaner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.triskeledu.aprendoia.aprendoApp
import com.triskeledu.aprendoia.data.repository.JuegoRepository
import com.triskeledu.aprendoia.data.repository.ResultadoEscaneo
import com.triskeledu.aprendoia.data.repository.UsuarioRepository
import com.triskeledu.aprendoia.util.LectorQr
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * VIEWMODEL del escáner. Recibe el texto leído (desde la cámara o escrito a
 * mano), lo interpreta como JSON y lo registra en SQLite.
 * La cámara en sí la abre el Composable, porque necesita el Context.
 */
class EscanerViewModel(
    private val usuarioRepository: UsuarioRepository,
    private val juegoRepository: JuegoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EscanerUiState())
    val uiState: StateFlow<EscanerUiState> = _uiState.asStateFlow()

    /** Texto recibido desde el escáner de Google. */
    fun onTextoLeido(texto: String) {
        val datos = LectorQr.interpretar(texto)
        if (datos == null) {
            mostrarError("Se leyó el QR, pero no es un código de Aprendo IA.")
            return
        }
        procesarCodigo(datos.codigo)
    }

    fun onEscaneoFallido(mensaje: String) = mostrarError(mensaje)

    // ---------- Ingreso manual ----------

    fun onAlternarIngresoManual() =
        _uiState.update { it.copy(mostrarIngresoManual = !it.mostrarIngresoManual) }

    fun onCodigoManualChange(valor: String) =
        _uiState.update { it.copy(codigoManual = valor.uppercase()) }

    fun onConfirmarCodigoManual() {
        val codigo = _uiState.value.codigoManual
        if (codigo.isBlank()) {
            mostrarError("Escribe el código impreso bajo el QR.")
            return
        }
        procesarCodigo(LectorQr.normalizar(codigo))
    }

    // ---------- Lógica común ----------

    private fun procesarCodigo(codigo: String) {
        if (_uiState.value.procesando) return
        _uiState.update { it.copy(procesando = true, esError = false, mensaje = "Validando código…") }

        viewModelScope.launch {
            val usuarioId = usuarioRepository.usuarioActivoId.first()
            when (val resultado = juegoRepository.registrarEscaneo(usuarioId, codigo)) {
                is ResultadoEscaneo.Nuevo -> navegar(resultado.contenido.id, resultado.puntosGanados)
                is ResultadoEscaneo.Repetido -> navegar(resultado.contenido.id, 0)
                ResultadoEscaneo.NoEncontrado -> mostrarError("El código \"$codigo\" no existe o aún no está publicado.")
                ResultadoEscaneo.SinSesion -> mostrarError("Selecciona un jugador antes de escanear.")
            }
        }
    }

    private fun navegar(contenidoId: Long, puntos: Int) = _uiState.update {
        it.copy(
            procesando = false,
            codigoManual = "",
            mostrarIngresoManual = false,
            irAResultado = IrAResultado(contenidoId, puntos)
        )
    }

    private fun mostrarError(mensaje: String) =
        _uiState.update { it.copy(procesando = false, esError = true, mensaje = mensaje) }

    fun onNavegacionRealizada() = _uiState.update {
        it.copy(irAResultado = null, esError = false, mensaje = EscanerUiState().mensaje)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val contenedor = aprendoApp().contenedor
                EscanerViewModel(contenedor.usuarioRepository, contenedor.juegoRepository)
            }
        }
    }
}
