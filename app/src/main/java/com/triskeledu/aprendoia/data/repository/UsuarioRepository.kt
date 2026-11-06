package com.triskeledu.aprendoia.data.repository

import com.triskeledu.aprendoia.data.local.dao.UsuarioDao
import com.triskeledu.aprendoia.data.local.entity.Rol
import com.triskeledu.aprendoia.data.local.entity.UsuarioEntity
import com.triskeledu.aprendoia.data.local.relacion.JugadorResumen
import com.triskeledu.aprendoia.data.sesion.SesionManager
import com.triskeledu.aprendoia.util.Seguridad
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

/**
 * Punto único de acceso a los datos de usuarios y a la sesión.
 * Los ViewModel hablan con el repositorio, nunca directamente con Room.
 */
class UsuarioRepository(
    private val dao: UsuarioDao,
    private val sesion: SesionManager
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

    /** Valida la contraseña y, si es correcta, deja al usuario como activo. */
    suspend fun iniciarSesion(usuarioId: Long, password: String): Result<UsuarioEntity> {
        val usuario = dao.obtenerPorId(usuarioId)
            ?: return Result.failure(IllegalArgumentException("El usuario no existe"))
        if (!Seguridad.coincide(password, usuario.password)) {
            return Result.failure(IllegalArgumentException("Contraseña incorrecta"))
        }
        sesion.iniciar(usuario.id)
        return Result.success(usuario)
    }

    suspend fun cerrarSesion() = sesion.cerrar()

    /** Registra un nuevo Cliente (pantalla 2) y lo deja como jugador activo. */
    suspend fun registrarCliente(
        nombre: String,
        correo: String,
        telefono: String,
        password: String,
        direccion: String,
        avatar: String
    ): Result<Long> {
        if (dao.contarPorCorreo(correo.trim()) > 0) {
            return Result.failure(IllegalArgumentException("Ya existe un jugador con ese correo"))
        }
        val id = dao.insertar(
            UsuarioEntity(
                nombre = nombre.trim(),
                alias = nombre.trim().substringBefore(" "),
                correo = correo.trim(),
                telefono = telefono.trim(),
                password = Seguridad.hash(password),
                direccion = direccion.trim(),
                avatar = avatar,
                rol = Rol.CLIENTE
            )
        )
        sesion.iniciar(id)
        return Result.success(id)
    }

    /** Actualiza datos del perfil. Si nuevaPassword viene vacía, se mantiene la actual. */
    suspend fun actualizar(usuario: UsuarioEntity, nuevaPassword: String = ""): Result<Unit> {
        if (dao.contarPorCorreo(usuario.correo.trim(), excluirId = usuario.id) > 0) {
            return Result.failure(IllegalArgumentException("Ese correo ya está en uso"))
        }
        val actualizado = if (nuevaPassword.isBlank()) usuario
        else usuario.copy(password = Seguridad.hash(nuevaPassword))
        dao.actualizar(actualizado)
        return Result.success(Unit)
    }

    suspend fun obtenerPorId(id: Long): UsuarioEntity? = dao.obtenerPorId(id)

    /** Solo se pueden eliminar clientes; las cuentas en duro se protegen. */
    suspend fun eliminar(usuario: UsuarioEntity): Result<Unit> {
        if (usuario.rol != Rol.CLIENTE) {
            return Result.failure(IllegalStateException("No se puede eliminar una cuenta del sistema"))
        }
        dao.eliminar(usuario)
        return Result.success(Unit)
    }
}
