package com.triskeledu.aprendoia.data.repository

import com.triskeledu.aprendoia.DatosPrueba
import com.triskeledu.aprendoia.DatosPrueba.capsula
import com.triskeledu.aprendoia.DatosPrueba.capsulaDto
import com.triskeledu.aprendoia.DatosPrueba.plaza
import com.triskeledu.aprendoia.DatosPrueba.plazaDto
import com.triskeledu.aprendoia.data.local.AppDatabase
import com.triskeledu.aprendoia.data.local.dao.ContenidoDao
import com.triskeledu.aprendoia.data.local.dao.NotificacionDao
import com.triskeledu.aprendoia.data.local.dao.PuntoDao
import com.triskeledu.aprendoia.data.local.dao.UsuarioDao
import com.triskeledu.aprendoia.data.remote.ErrorServidor
import com.triskeledu.aprendoia.data.remote.ErroresRed
import com.triskeledu.aprendoia.data.remote.api.ContenidosApi
import com.triskeledu.aprendoia.util.Notificador
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/** Pruebas del CRUD de gestión contra ms-contenidos (Administrador / Creador). */
class GestionRepositoryTest {

    private val db = mockk<AppDatabase>()
    private val puntoDao = mockk<PuntoDao>(relaxed = true)
    private val contenidoDao = mockk<ContenidoDao>(relaxed = true)
    private val usuarioDao = mockk<UsuarioDao>(relaxed = true)
    private val notificacionDao = mockk<NotificacionDao>(relaxed = true)
    private val notificador = mockk<Notificador>(relaxed = true)
    private val api = mockk<ContenidosApi>()
    private val sincronizacion = mockk<SincronizacionRepository>(relaxed = true)
    private lateinit var repositorio: GestionRepository

    @Before
    fun preparar() {
        every { db.puntoDao() } returns puntoDao
        every { db.contenidoDao() } returns contenidoDao
        every { db.usuarioDao() } returns usuarioDao
        every { db.notificacionDao() } returns notificacionDao
        coEvery { puntoDao.obtenerPorId(1) } returns plaza
        coEvery { usuarioDao.obtenerIdsClientes() } returns listOf(3L, 4L)
        repositorio = GestionRepository(db, notificador, api, sincronizacion)
    }

    @Test
    fun `crear punto hace POST y guarda en Room`() = runTest {
        coEvery { api.crearPunto(any()) } returns plazaDto

        val r = repositorio.guardarPunto(plaza.copy(id = 0))

        assertEquals(1L, r.getOrNull())
        coVerify { puntoDao.guardar(match { it.id == 1L }) }
    }

    @Test
    fun `editar punto hace PUT`() = runTest {
        coEvery { api.actualizarPunto(1, any()) } returns plazaDto.copy(nombre = "Plaza de Armas de Santiago")

        assertTrue(repositorio.guardarPunto(plaza).isSuccess)
        coVerify { puntoDao.guardar(match { it.nombre == "Plaza de Armas de Santiago" }) }
    }

    @Test
    fun `sin conexion el punto no se guarda y se informa el error`() = runTest {
        coEvery { api.crearPunto(any()) } throws DatosPrueba.sinConexion()

        val error = repositorio.guardarPunto(plaza.copy(id = 0)).exceptionOrNull()

        assertTrue(error is ErrorServidor)
        assertEquals(ErroresRed.SIN_CONEXION, error?.message)
        coVerify(exactly = 0) { puntoDao.guardar(any()) }
    }

    @Test
    fun `eliminar punto borra en el servidor y en Room`() = runTest {
        coEvery { api.eliminarPunto(1) } just Runs

        assertTrue(repositorio.eliminarPunto(plaza).isSuccess)
        coVerify { puntoDao.eliminarPorId(1) }
    }

    @Test
    fun `crear capsula publicada avisa a todos los jugadores`() = runTest {
        coEvery { api.crearCapsula(any()) } returns capsulaDto

        assertEquals(1L, repositorio.guardarCapsula(capsula.copy(id = 0), estabaPublicada = false).getOrNull())
        coVerify { notificacionDao.insertarVarias(match { it.size == 2 }) }
        verify { notificador.mostrar(any(), any()) }
    }

    @Test
    fun `editar capsula ya publicada no repite el aviso`() = runTest {
        coEvery { api.actualizarCapsula(1, any()) } returns capsulaDto

        assertTrue(repositorio.guardarCapsula(capsula, estabaPublicada = true).isSuccess)
        coVerify(exactly = 0) { notificacionDao.insertarVarias(any()) }
    }

    @Test
    fun `codigo QR repetido devuelve 409 con el mensaje del servidor`() = runTest {
        coEvery { api.crearCapsula(any()) } throws DatosPrueba.errorHttp(409, "Ese código QR ya está en uso")

        val error = repositorio.guardarCapsula(capsula.copy(id = 0), false).exceptionOrNull() as ErrorServidor

        assertEquals(409, error.codigo)
        assertEquals("Ese código QR ya está en uso", error.message)
    }

    @Test
    fun `publicar capsula pendiente la publica y avisa`() = runTest {
        coEvery { api.publicarCapsula(1) } returns capsulaDto

        assertTrue(repositorio.publicar(capsula.copy(publicado = false)).isSuccess)
        coVerify { contenidoDao.guardar(match { it.publicado }) }
        coVerify { notificacionDao.insertarVarias(any()) }
    }

    @Test
    fun `publicar capsula ya publicada no llama al servidor`() = runTest {
        assertTrue(repositorio.publicar(capsula).isSuccess)
        coVerify(exactly = 0) { api.publicarCapsula(any()) }
    }

    @Test
    fun `eliminar capsula`() = runTest {
        coEvery { api.eliminarCapsula(1) } just Runs

        assertTrue(repositorio.eliminarCapsula(capsula).isSuccess)
        coVerify { contenidoDao.eliminarPorId(1) }
    }

    @Test
    fun `codigo disponible consulta Room y resumen combina contadores`() = runTest {
        coEvery { contenidoDao.contarPorCodigoQr("QR-NUEVO", 0) } returns 0
        coEvery { contenidoDao.contarPorCodigoQr("QR-PLAZA-ARMAS", 0) } returns 1
        every { puntoDao.observarTotal() } returns flowOf(8)
        every { contenidoDao.observarTotal() } returns flowOf(9)
        every { contenidoDao.observarPendientes() } returns flowOf(1)
        every { usuarioDao.observarCantidadClientes() } returns flowOf(2)

        assertTrue(repositorio.codigoDisponible("QR-NUEVO", 0))
        assertFalse(repositorio.codigoDisponible("QR-PLAZA-ARMAS", 0))
        val resumen = repositorio.observarResumen().first()
        assertEquals(8, resumen.puntos)
        assertEquals(1, resumen.pendientes)
        assertEquals(2, resumen.jugadores)
    }
}
