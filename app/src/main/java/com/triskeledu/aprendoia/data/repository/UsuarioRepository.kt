package com.triskeledu.aprendoia.data.repository

import com.triskeledu.aprendoia.data.local.dao.UsuarioDao
import com.triskeledu.aprendoia.data.local.entity.Rol
import com.triskeledu.aprendoia.data.local.entity.UsuarioEntity
import com.triskeledu.aprendoia.data.local.relacion.JugadorResumen
import com.triskeledu.aprendoia.data.remote.ErrorServidor
import com.triskeledu.aprendoia.data.remote.ErroresRed
import com.triskeledu.aprendoia.data.remote.aEntidad
import com.triskeledu.aprendoia.data.remote.aRequest
import com.triskeledu.aprendoia.data.remote.api.JuegoApi
import com.triskeledu.aprendoia.data.remote.api.UsuariosApi
import com.triskeledu.aprendoia.data.remote.dto.LoginRequestDto
import com.triskeledu.aprendoia.data.remote.dto.UsuarioRequestDto
import com.triskeledu.aprendoia.data.sesion.SesionManager
import com.triskeledu.aprendoia.util.Seguridad
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

/**
 * Punto único de acceso a los usuarios y a la sesión.
 *
 * Los datos viven en el microservicio ms-usuarios; Room guarda una copia
 * para que la interfaz se actualice sola (Flow) y para funcionar sin conexión.
 * Los ViewModel hablan con el repositorio, nunca con Retrofit ni con Room.
 */
class UsuarioRepository(
    private val dao: UsuarioDao,
    private val sesion: SesionManager,
    private val api: UsuariosApi,
    private val juegoApi: JuegoApi
) {

    fun observarJugadores(): Flow<List<JugadorResumen>> = dao.observarJugadores()

    val usuarioActivoId: Flow<Long?> = sesion.usuarioActivoId

    /** Usuario activo completo; se actualiza solo si cambia la sesión o sus datos. */
    @OptIn(ExperimentalCoroutinesApi::class)
    val usuarioActivo: Flow<UsuarioEntity?> = sesion.usuarioActivoId.flatMapLatest { id ->
        if (id == null) flowOf(null) else dao.observarPorId(id)
    }

    suspend fun obtenerUsuarioActivo(): UsuarioEntity? =
        sesion.usuarioActivoId.first()?.let { dao.obtenerPorId(it) }

    suspend fun obtenerPorId(id: Long): UsuarioEntity? = dao.obtenerPorId(id)

    suspend fun cerrarSesion() = sesion.cerrar()

    /** GET /api/usuarios → copia local. Conserva el hash de contraseña ya guardado. */
    suspend fun sincronizarUsuarios(): Result<Unit> = runCatching {
        val remotos = api.listar()
        val locales = dao.obtenerTodos().associateBy { it.id }
        dao.guardarTodos(remotos.map { it.aEntidad(locales[it.id]?.password.orEmpty()) })
        dao.eliminarExcepto(remotos.map { it.id })
    }

    /**
     * POST /api/usuarios/login. Si el servidor no responde, se valida con el
     * hash guardado en el teléfono (solo si ese jugador ya inició sesión antes aquí).
     */
    suspend fun iniciarSesion(usuarioId: Long, password: String): Result<UsuarioEntity> {
        val remoto = runCatching { api.login(LoginRequestDto(usuarioId, password)) }
        return remoto.fold(
            onSuccess = { dto ->
                val usuario = dto.aEntidad(passwordLocal = Seguridad.hash(password))
                dao.guardar(usuario)
                sesion.iniciar(usuario.id)
                Result.success(usuario)
            },
            onFailure = { error ->
                if (ErroresRed.esSinConexion(error)) iniciarSesionLocal(usuarioId, password)
                else Result.failure(IllegalArgumentException(ErroresRed.mensaje(error)))
            }
        )
    }

    private suspend fun iniciarSesionLocal(usuarioId: Long, password: String): Result<UsuarioEntity> {
        val usuario = dao.obtenerPorId(usuarioId)
            ?: return Result.failure(IllegalArgumentException("El usuario no existe"))
        if (usuario.password.isBlank()) {
            return Result.failure(ErrorServidor(ErroresRed.SIN_CONEXION))
        }
        if (!Seguridad.coincide(password, usuario.password)) {
            return Result.failure(IllegalArgumentException("Contraseña incorrecta"))
        }
        sesion.iniciar(usuario.id)
        return Result.success(usuario)
    }

    /**
     * POST /api/usuarios: registra un Cliente (pantalla 2) y lo deja como jugador activo.
     * Fallas de negocio (correo repetido) → IllegalArgumentException, para mostrarla bajo el campo.
     */
    suspend fun registrarCliente(
        nombre: String,
        correo: String,
        telefono: String,
        password: String,
        direccion: String,
        avatar: String
    ): Result<Long> {
        val datos = UsuarioRequestDto(
            nombre = nombre.trim(),
            correo = correo.trim(),
            telefono = telefono.trim(),
            password = password,
            direccion = direccion.trim(),
            avatar = avatar,
            notificacionesActivas = true
        )
        return runCatching { api.registrar(datos) }.fold(
            onSuccess = { dto ->
                dao.guardar(dto.aEntidad(passwordLocal = Seguridad.hash(password)))
                sesion.iniciar(dto.id)
                Result.success(dto.id)
            },
            onFailure = { error -> Result.failure(traducir(error)) }
        )
    }

    /**
     * PUT /api/usuarios/{id}. Si nuevaPassword viene vacía se mantiene la actual.
     * Sin conexión, el cambio se guarda solo en el teléfono.
     */
    suspend fun actualizar(usuario: UsuarioEntity, nuevaPassword: String = ""): Result<Unit> {
        if (dao.contarPorCorreo(usuario.correo.trim(), excluirId = usuario.id) > 0) {
            return Result.failure(IllegalArgumentException("Ese correo ya está en uso"))
        }
        val hashLocal = if (nuevaPassword.isBlank()) usuario.password else Seguridad.hash(nuevaPassword)
        return runCatching { api.actualizar(usuario.id, usuario.aRequest(nuevaPassword)) }.fold(
            onSuccess = { dto ->
                dao.guardar(dto.aEntidad(passwordLocal = hashLocal))
                Result.success(Unit)
            },
            onFailure = { error ->
                if (ErroresRed.esSinConexion(error)) {
                    dao.actualizar(usuario.copy(password = hashLocal))
                    Result.success(Unit)
                } else {
                    Result.failure(traducir(error))
                }
            }
        )
    }

    /**
     * DELETE /api/usuarios/{id} y DELETE /api/progreso/{id} en ms-juego.
     * Solo se pueden eliminar clientes; las cuentas en duro se protegen.
     */
    suspend fun eliminar(usuario: UsuarioEntity): Result<Unit> {
        if (usuario.rol != Rol.CLIENTE) {
            return Result.failure(IllegalStateException("No se puede eliminar una cuenta del sistema"))
        }
        return runCatching { api.eliminar(usuario.id) }.fold(
            onSuccess = {
                runCatching { juegoApi.eliminarProgreso(usuario.id) }
                dao.eliminar(usuario)
                Result.success(Unit)
            },
            onFailure = { error -> Result.failure(ErrorServidor(ErroresRed.mensaje(error))) }
        )
    }

    /** 409 (dato repetido) → error de campo; lo demás → error general. */
    private fun traducir(error: Throwable): Exception =
        if (ErroresRed.codigoHttp(error) == 409) IllegalArgumentException(ErroresRed.mensaje(error))
        else ErrorServidor(ErroresRed.mensaje(error), ErroresRed.codigoHttp(error))
}
