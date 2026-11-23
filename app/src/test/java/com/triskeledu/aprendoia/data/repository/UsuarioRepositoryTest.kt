package com.triskeledu.aprendoia.data.repository

import com.triskeledu.aprendoia.DatosPrueba
import com.triskeledu.aprendoia.DatosPrueba.ana
import com.triskeledu.aprendoia.DatosPrueba.anaDto
import com.triskeledu.aprendoia.data.local.dao.UsuarioDao
import com.triskeledu.aprendoia.data.local.entity.UsuarioEntity
import com.triskeledu.aprendoia.data.remote.ErrorServidor
import com.triskeledu.aprendoia.data.remote.api.JuegoApi
import com.triskeledu.aprendoia.data.remote.api.UsuariosApi
import com.triskeledu.aprendoia.data.sesion.SesionManager
import com.triskeledu.aprendoia.util.Seguridad
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/** Pruebas del repositorio de usuarios: Retrofit (ms-usuarios) + Room + sesión. */
class UsuarioRepositoryTest {

    private val dao = mockk<UsuarioDao>(relaxed = true)
    private val sesion = mockk<SesionManager>(relaxed = true)
    private val api = mockk<UsuariosApi>()
    private val juegoApi = mockk<JuegoApi>(relaxed = true)
    private lateinit var repositorio: UsuarioRepository

    @Before
    fun preparar() {
        every { sesion.usuarioActivoId } returns flowOf(3L)
        repositorio = UsuarioRepository(dao, sesion, api, juegoApi)
    }

    // ---------------------------------------------------------------- Sincronización

    @Test
    fun `sincronizar guarda los usuarios del servidor conservando el hash local`() = runTest {
        coEvery { api.listar() } returns listOf(anaDto)
        coEvery { dao.obtenerTodos() } returns listOf(ana)
        val guardados = slot<List<UsuarioEntity>>()
        coEvery { dao.guardarTodos(capture(guardados)) } just Runs

        val r = repositorio.sincronizarUsuarios()

        assertTrue(r.isSuccess)
        assertEquals(ana.password, guardados.captured.single().password)
        coVerify { dao.eliminarExcepto(listOf(3L)) }
    }

    @Test
    fun `sincronizar sin conexion devuelve error`() = runTest {
        coEvery { api.listar() } throws DatosPrueba.sinConexion()
        assertTrue(repositorio.sincronizarUsuarios().isFailure)
    }

    // ---------------------------------------------------------------- Login

    @Test
    fun `login correcto en el servidor guarda el usuario e inicia sesion`() = runTest {
        coEvery { api.login(any()) } returns anaDto

        val r = repositorio.iniciarSesion(3, "1234")

        assertEquals("Ana", r.getOrNull()?.alias)
        coVerify { dao.guardar(match { it.password == Seguridad.hash("1234") }) }
        coVerify { sesion.iniciar(3) }
    }

    @Test
    fun `login con contrasena incorrecta muestra el mensaje del servidor`() = runTest {
        coEvery { api.login(any()) } throws DatosPrueba.errorHttp(401, "Contraseña incorrecta")

        val r = repositorio.iniciarSesion(3, "9999")

        assertTrue(r.exceptionOrNull() is IllegalArgumentException)
        assertEquals("Contraseña incorrecta", r.exceptionOrNull()?.message)
    }

    @Test
    fun `login sin conexion usa la contrasena guardada en el telefono`() = runTest {
        coEvery { api.login(any()) } throws DatosPrueba.sinConexion()
        coEvery { dao.obtenerPorId(3) } returns ana

        assertTrue(repositorio.iniciarSesion(3, "1234").isSuccess)
        assertTrue(repositorio.iniciarSesion(3, "0000").isFailure)
    }

    @Test
    fun `login sin conexion y sin hash local pide conexion`() = runTest {
        coEvery { api.login(any()) } throws DatosPrueba.sinConexion()
        coEvery { dao.obtenerPorId(3) } returns ana.copy(password = "")

        val r = repositorio.iniciarSesion(3, "1234")

        assertTrue(r.exceptionOrNull() is ErrorServidor)
    }

    @Test
    fun `login sin conexion de usuario inexistente falla`() = runTest {
        coEvery { api.login(any()) } throws DatosPrueba.sinConexion()
        coEvery { dao.obtenerPorId(99) } returns null

        assertTrue(repositorio.iniciarSesion(99, "1234").isFailure)
    }

    // ---------------------------------------------------------------- Registro

    @Test
    fun `registrar crea el jugador en el servidor y lo deja activo`() = runTest {
        coEvery { api.registrar(any()) } returns anaDto

        val r = repositorio.registrarCliente("Ana González", "ana@email.com", "+56 9 1234 5678", "1234", "", "avatar_1")

        assertEquals(3L, r.getOrNull())
        coVerify { sesion.iniciar(3) }
        coVerify { dao.guardar(any()) }
    }

    @Test
    fun `registrar con correo repetido devuelve error de campo`() = runTest {
        coEvery { api.registrar(any()) } throws DatosPrueba.errorHttp(409, "Ya existe un jugador con ese correo")

        val r = repositorio.registrarCliente("Ana González", "ana@email.com", "+56 9 1234 5678", "1234", "", "avatar_1")

        assertTrue(r.exceptionOrNull() is IllegalArgumentException)
        assertEquals("Ya existe un jugador con ese correo", r.exceptionOrNull()?.message)
    }

    @Test
    fun `registrar sin conexion devuelve error general`() = runTest {
        coEvery { api.registrar(any()) } throws DatosPrueba.sinConexion()

        val r = repositorio.registrarCliente("Ana González", "ana@email.com", "+56 9 1234 5678", "1234", "", "avatar_1")

        assertTrue(r.exceptionOrNull() is ErrorServidor)
    }

    // ---------------------------------------------------------------- Actualizar

    @Test
    fun `actualizar envia al servidor y guarda en Room`() = runTest {
        coEvery { dao.contarPorCorreo(any(), any()) } returns 0
        coEvery { api.actualizar(3, any()) } returns anaDto.copy(nombre = "Ana María")

        assertTrue(repositorio.actualizar(ana.copy(nombre = "Ana María")).isSuccess)
        coVerify { dao.guardar(match { it.nombre == "Ana María" }) }
    }

    @Test
    fun `actualizar sin conexion guarda solo en el telefono`() = runTest {
        coEvery { dao.contarPorCorreo(any(), any()) } returns 0
        coEvery { api.actualizar(3, any()) } throws DatosPrueba.sinConexion()

        assertTrue(repositorio.actualizar(ana, nuevaPassword = "5555").isSuccess)
        coVerify { dao.actualizar(match { it.password == Seguridad.hash("5555") }) }
    }

    @Test
    fun `actualizar con correo de otro jugador falla sin llamar al servidor`() = runTest {
        coEvery { dao.contarPorCorreo(any(), any()) } returns 1

        assertTrue(repositorio.actualizar(ana).isFailure)
        coVerify(exactly = 0) { api.actualizar(any(), any()) }
    }

    @Test
    fun `actualizar con error 409 del servidor devuelve error de campo`() = runTest {
        coEvery { dao.contarPorCorreo(any(), any()) } returns 0
        coEvery { api.actualizar(3, any()) } throws DatosPrueba.errorHttp(409, "Ese correo ya está en uso")

        assertTrue(repositorio.actualizar(ana).exceptionOrNull() is IllegalArgumentException)
    }

    // ---------------------------------------------------------------- Eliminar

    @Test
    fun `no se puede eliminar una cuenta del sistema`() = runTest {
        assertTrue(repositorio.eliminar(DatosPrueba.admin).isFailure)
        coVerify(exactly = 0) { api.eliminar(any()) }
    }

    @Test
    fun `eliminar borra en ms-usuarios, en ms-juego y en Room`() = runTest {
        coEvery { api.eliminar(3) } just Runs

        assertTrue(repositorio.eliminar(ana).isSuccess)
        coVerify { juegoApi.eliminarProgreso(3) }
        coVerify { dao.eliminar(ana) }
    }

    @Test
    fun `eliminar sin conexion no borra nada local`() = runTest {
        coEvery { api.eliminar(3) } throws DatosPrueba.sinConexion()

        assertTrue(repositorio.eliminar(ana).isFailure)
        coVerify(exactly = 0) { dao.eliminar(any()) }
    }

    // ---------------------------------------------------------------- Sesión

    @Test
    fun `usuario activo se obtiene desde la sesion`() = runTest {
        coEvery { dao.obtenerPorId(3) } returns ana
        every { dao.observarPorId(3) } returns flowOf(ana)

        assertEquals(ana, repositorio.obtenerUsuarioActivo())
        assertEquals(ana, repositorio.usuarioActivo.first())
        assertEquals(3L, repositorio.usuarioActivoId.first())
    }

    @Test
    fun `sin sesion el usuario activo es null`() = runTest {
        every { sesion.usuarioActivoId } returns flowOf(null)
        val sinSesion = UsuarioRepository(dao, sesion, api, juegoApi)

        assertNull(sinSesion.obtenerUsuarioActivo())
        assertNull(sinSesion.usuarioActivo.first())
    }

    @Test
    fun `cerrar sesion limpia la sesion`() = runTest {
        repositorio.cerrarSesion()
        coVerify { sesion.cerrar() }
    }
}
