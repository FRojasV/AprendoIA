package com.triskeledu.aprendoia.ui.gestion.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.triskeledu.aprendoia.aprendoApp
import com.triskeledu.aprendoia.data.local.entity.ContenidoEntity
import com.triskeledu.aprendoia.data.local.entity.Rol
import com.triskeledu.aprendoia.data.remote.ErrorServidor
import com.triskeledu.aprendoia.data.repository.GestionRepository
import com.triskeledu.aprendoia.data.repository.UsuarioRepository
import com.triskeledu.aprendoia.ui.gestion.model.CampoFormulario
import com.triskeledu.aprendoia.ui.gestion.model.CapsulaFormUiState
import com.triskeledu.aprendoia.ui.navigation.Rutas
import com.triskeledu.aprendoia.util.LectorQr
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.Normalizer


class FormularioCapsulaViewModel(
    private val usuarioRepository: UsuarioRepository,
    private val gestionRepository: GestionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    /** -1 = cápsula nueva. */
    private val contenidoId: Long = savedStateHandle.get<Long>(Rutas.ARG_CONTENIDO_ID) ?: -1L
    private var original: ContenidoEntity? = null

    private val _uiState = MutableStateFlow(CapsulaFormUiState())
    val uiState: StateFlow<CapsulaFormUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val usuario = usuarioRepository.usuarioActivo.first()
            val esAdmin = usuario?.rol == Rol.ADMIN
            val puntos = gestionRepository.observarPuntos().first()
            val capsula = if (contenidoId > 0) gestionRepository.obtenerCapsula(contenidoId) else null
            original = capsula

            _uiState.update {
                val base = it.copy(cargando = false, esAdmin = esAdmin, puntosDisponibles = puntos)
                if (capsula == null) {
                    // Nueva: el autor por defecto es el usuario conectado.
                    base.copy(autor = CampoFormulario(usuario?.alias ?: ""), publicado = esAdmin)
                } else {
                    base.copy(
                        idEdicion = capsula.id,
                        titulo = CampoFormulario(capsula.titulo),
                        tema = CampoFormulario(capsula.tema),
                        autor = CampoFormulario(capsula.autor),
                        url = CampoFormulario(capsula.url),
                        duracion = CampoFormulario(capsula.duracion),
                        descripcion = CampoFormulario(capsula.descripcion),
                        codigoQr = CampoFormulario(capsula.codigoQr),
                        puntosQr = CampoFormulario(capsula.puntos.toString()),
                        puntoId = capsula.puntoId,
                        publicado = capsula.publicado,
                        estabaPublicado = capsula.publicado
                    )
                }
            }
        }
    }

    // --- EVENTOS DE ENTRADA ---

    fun cambiarTitulo(valor: String) = _uiState.update { it.copy(titulo = CampoFormulario(valor, validarTitulo(valor))) }

    fun cambiarTema(valor: String) = _uiState.update { it.copy(tema = CampoFormulario(valor, validarObligatorio(valor, "El tema"))) }

    fun cambiarAutor(valor: String) = _uiState.update { it.copy(autor = CampoFormulario(valor, validarObligatorio(valor, "El autor"))) }

    fun cambiarUrl(valor: String) = _uiState.update { it.copy(url = CampoFormulario(valor.trim(), validarUrl(valor.trim()))) }

    fun cambiarDuracion(valor: String) {
        // Solo dígitos y ":" (igual que "edad" en el ejemplo: lo demás se descarta).
        if (valor.all { it.isDigit() || it == ':' } && valor.length <= 5) {
            _uiState.update { it.copy(duracion = CampoFormulario(valor, validarDuracion(valor))) }
        }
    }

    fun cambiarDescripcion(valor: String) =
        _uiState.update { it.copy(descripcion = CampoFormulario(valor, validarDescripcion(valor))) }

    fun cambiarCodigo(valor: String) {
        val codigo = valor.uppercase().filter { it.isLetterOrDigit() || it == '-' }
        _uiState.update { it.copy(codigoQr = CampoFormulario(codigo, validarCodigo(codigo))) }
    }

    fun cambiarPuntosQr(valor: String) {
        if (valor.all(Char::isDigit) && valor.length <= 3) {
            _uiState.update { it.copy(puntosQr = CampoFormulario(valor, validarPuntos(valor))) }
        }
    }

    fun elegirPunto(puntoId: Long) {
        _uiState.update { it.copy(puntoId = puntoId, errorPunto = null) }
        // Si el código está vacío, se sugiere uno a partir del nombre del lugar.
        if (_uiState.value.codigoQr.valor.isBlank()) generarCodigo()
    }

    fun cambiarPublicado(valor: Boolean) {
        if (_uiState.value.esAdmin) _uiState.update { it.copy(publicado = valor) }
    }

    /** Propone un código único, por ejemplo "QR-CERRO-SANTA-LUCIA" o "...-2". */
    fun generarCodigo() {
        val punto = _uiState.value.puntoSeleccionado ?: run {
            _uiState.update { it.copy(errorPunto = "Elige primero el punto turístico") }
            return
        }
        viewModelScope.launch {
            val base = "QR-" + sinTildes(punto.nombre).uppercase()
                .replace(Regex("[^A-Z0-9]+"), "-").trim('-').take(22)
            var propuesta = base
            var n = 2
            while (!gestionRepository.codigoDisponible(propuesta, contenidoId)) {
                propuesta = "$base-$n"
                n++
            }
            cambiarCodigo(propuesta)
        }
    }

    fun enviar() {
        val actual = _uiState.value
        if (actual.guardando) return
        val validado = actual.copy(
            titulo = actual.titulo.copy(error = validarTitulo(actual.titulo.valor)),
            tema = actual.tema.copy(error = validarObligatorio(actual.tema.valor, "El tema")),
            autor = actual.autor.copy(error = validarObligatorio(actual.autor.valor, "El autor")),
            url = actual.url.copy(error = validarUrl(actual.url.valor)),
            duracion = actual.duracion.copy(error = validarDuracion(actual.duracion.valor)),
            descripcion = actual.descripcion.copy(error = validarDescripcion(actual.descripcion.valor)),
            codigoQr = actual.codigoQr.copy(error = validarCodigo(actual.codigoQr.valor)),
            puntosQr = actual.puntosQr.copy(error = validarPuntos(actual.puntosQr.valor)),
            errorPunto = if (actual.puntoId == null) "Elige el punto turístico" else null,
            intentoEnviar = true
        )
        _uiState.value = validado
        if (!validado.formularioValido) return

        _uiState.update { it.copy(guardando = true, mensajeError = null) }
        viewModelScope.launch {
            // Validación que necesita la base de datos: el código QR debe ser único.
            val codigo = LectorQr.normalizar(validado.codigoQr.valor)
            if (!gestionRepository.codigoDisponible(codigo, contenidoId)) {
                _uiState.update {
                    it.copy(guardando = false, codigoQr = it.codigoQr.copy(error = "Ese código ya está en uso"))
                }
                return@launch
            }
            val capsula = (original ?: ContenidoEntity(puntoId = 0, titulo = "", tema = "", autor = "", url = "", codigoQr = "")).copy(
                puntoId = validado.puntoId!!,
                titulo = validado.titulo.valor.trim(),
                tema = validado.tema.valor.trim(),
                autor = validado.autor.valor.trim(),
                url = validado.url.valor.trim(),
                duracion = validado.duracion.valor,
                descripcion = validado.descripcion.valor.trim(),
                codigoQr = codigo,
                puntos = validado.puntosQr.valor.toInt(),
                // El Creador nunca publica directamente.
                publicado = validado.esAdmin && validado.publicado
            )
            // Se guarda en ms-contenidos (POST o PUT) y luego en Room.
            gestionRepository.guardarCapsula(capsula, estabaPublicada = validado.estabaPublicado)
                .onSuccess { id -> _uiState.update { it.copy(guardando = false, guardadoId = id) } }
                .onFailure { error ->
                    _uiState.update {
                        // 409 = el servidor detectó el código QR repetido: se muestra bajo ese campo.
                        if ((error as? ErrorServidor)?.codigo == 409) {
                            it.copy(guardando = false, codigoQr = it.codigoQr.copy(error = error.message))
                        } else {
                            it.copy(guardando = false, mensajeError = error.message)
                        }
                    }
                }
        }
    }

    fun navegacionRealizada() = _uiState.update { it.copy(guardadoId = null) }

    // --- REGLAS DE NEGOCIO PRIVADAS (Validaciones) ---

    private fun validarTitulo(valor: String): String? = when {
        valor.isBlank() -> "El título es obligatorio"
        valor.trim().length < 3 -> "Escribe al menos 3 caracteres"
        valor.length > 80 -> "Máximo 80 caracteres"
        else -> null
    }

    private fun validarObligatorio(valor: String, nombre: String): String? =
        if (valor.isBlank()) "$nombre es obligatorio" else null

    /** La URL es opcional (aún no hay videos), pero si se escribe debe ser un link web. */
    private fun validarUrl(valor: String): String? = when {
        valor.isBlank() -> null
        !(valor.startsWith("https://") || valor.startsWith("http://")) -> "Debe comenzar con https://"
        valor.contains(' ') -> "El link no puede tener espacios"
        else -> null
    }

    private fun validarDuracion(valor: String): String? = when {
        valor.isBlank() -> null
        !Regex("^\\d{1,2}:[0-5]\\d$").matches(valor) -> "Usa el formato mm:ss, por ejemplo 2:30"
        else -> null
    }

    private fun validarDescripcion(valor: String): String? =
        if (valor.length > 300) "Máximo 300 caracteres" else null

    private fun validarCodigo(valor: String): String? = when {
        valor.isBlank() -> "El código QR es obligatorio"
        valor.length < 4 -> "Mínimo 4 caracteres"
        valor.length > 30 -> "Máximo 30 caracteres"
        else -> null
    }

    private fun validarPuntos(valor: String): String? {
        val numero = valor.toIntOrNull()
        return when {
            valor.isBlank() -> "Los puntos son obligatorios"
            numero == null || numero !in 1..100 -> "Entre 1 y 100 (el caso usa 10)"
            else -> null
        }
    }

    private fun sinTildes(texto: String): String =
        Normalizer.normalize(texto, Normalizer.Form.NFD).replace(Regex("\\p{Mn}+"), "")

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val c = aprendoApp().contenedor
                FormularioCapsulaViewModel(c.usuarioRepository, c.gestionRepository, createSavedStateHandle())
            }
        }
    }
}
