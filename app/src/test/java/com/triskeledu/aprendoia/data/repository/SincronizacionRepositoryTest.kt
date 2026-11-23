package com.triskeledu.aprendoia.data.repository

import com.triskeledu.aprendoia.DatosPrueba
import com.triskeledu.aprendoia.DatosPrueba.capsula
import com.triskeledu.aprendoia.DatosPrueba.capsulaDto
import com.triskeledu.aprendoia.DatosPrueba.plaza
import com.triskeledu.aprendoia.DatosPrueba.plazaDto
import com.triskeledu.aprendoia.data.local.AppDatabase
import com.triskeledu.aprendoia.data.local.dao.ContenidoDao
import com.triskeledu.aprendoia.data.local.dao.DescubrimientoDao
import com.triskeledu.aprendoia.data.local.dao.HistorialDao
import com.triskeledu.aprendoia.data.local.dao.PuntoDao
import com.triskeledu.aprendoia.data.local.dao.SelfieDao
import com.triskeledu.aprendoia.data.local.entity.DescubrimientoEntity
import com.triskeledu.aprendoia.data.local.entity.SelfieEntity
import com.triskeledu.aprendoia.data.remote.api.ContenidosApi
import com.triskeledu.aprendoia.data.remote.api.JuegoApi
import com.triskeledu.aprendoia.data.remote.dto.ActividadDto
import com.triskeledu.aprendoia.data.remote.dto.DescubrimientoDto
import com.triskeledu.aprendoia.data.remote.dto.SelfieDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/** Pruebas de la sincronización Room ↔ microservicios. */
class SincronizacionRepositoryTest {

    private val db = mockk<AppDatabase>()
    private val puntoDao = mockk<PuntoDao>(relaxed = true)
    private val contenidoDao = mockk<ContenidoDao>(relaxed = true)
    private val descubrimientoDao = mockk<DescubrimientoDao>(relaxed = true)
    private val selfieDao = mockk<SelfieDao>(relaxed = true)
    private val historialDao = mockk<HistorialDao>(relaxed = true)
    private val contenidosApi = mockk<ContenidosApi>()
    private val juegoApi = mockk<JuegoApi>(relaxed = true)
    private lateinit var repositorio: SincronizacionRepository

    @Before
    fun preparar() {
        every { db.puntoDao() } returns puntoDao
        every { db.contenidoDao() } returns contenidoDao
        every { db.descubrimientoDao() } returns descubrimientoDao
        every { db.selfieDao() } returns selfieDao
        every { db.historialDao() } returns historialDao
        repositorio = SincronizacionRepository(db, contenidosApi, juegoApi, DatosPrueba.transaccionDirecta)
    }

    @Test
    fun `al inicio el estado de conexion es desconocido`() {
        assertNull(repositorio.estado.value.conectado)
    }

    @Test
    fun `sincronizar contenidos guarda puntos y capsulas y quita los eliminados`() = runTest {
        coEvery { contenidosApi.listarPuntos() } returns listOf(plazaDto)
        coEvery { contenidosApi.listarCapsulas() } returns listOf(capsulaDto)

        assertTrue(repositorio.sincronizarContenidos().isSuccess)

        coVerify { puntoDao.guardarTodos(match { it.single().id == 1L }) }
        coVerify { contenidoDao.guardarTodos(match { it.single().codigoQr == "QR-PLAZA-ARMAS" }) }
        coVerify { contenidoDao.eliminarExcepto(listOf(1L)) }
        coVerify { puntoDao.eliminarExcepto(listOf(1L)) }
        assertTrue(repositorio.estado.value.conectado == true)
    }

    @Test
    fun `sin conexion se informa en el estado`() = runTest {
        coEvery { contenidosApi.listarPuntos() } throws DatosPrueba.sinConexion()

        assertTrue(repositorio.sincronizarContenidos().isFailure)
        assertFalse(repositorio.estado.value.conectado!!)
        coVerify(exactly = 0) { puntoDao.guardarTodos(any()) }
    }

    @Test
    fun `sincronizar progreso sube pendientes y baja lo del servidor`() = runTest {
        // En el teléfono: 1 descubrimiento y 1 selfie que el servidor aún no tiene.
        coEvery { descubrimientoDao.obtenerDeUsuario(3) } returns
            listOf(DescubrimientoEntity(usuarioId = 3, contenidoId = 1, puntos = 10))
        coEvery { selfieDao.obtenerDeUsuario(3) } returns
            listOf(SelfieEntity(usuarioId = 3, puntoId = 1, rutaFoto = "content://foto"))
        coEvery { contenidoDao.obtenerPorId(1) } returns capsula
        coEvery { contenidoDao.obtenerPorId(2) } returns capsula.copy(id = 2)
        coEvery { puntoDao.obtenerPorId(2) } returns plaza.copy(id = 2)
        coEvery { historialDao.contar(3) } returns 0

        // En el servidor: otra cápsula (vista) y otra selfie.
        coEvery { juegoApi.descubrimientos(3) } returns
            listOf(DescubrimientoDto(9, 3, 2, 2, 10, true, "2026-11-20T10:15:00", "2026-11-20T10:20:00"))
        coEvery { juegoApi.selfies(3) } returns listOf(SelfieDto(5, 3, 2, "demo://selfie", 100, "2026-11-20T10:30:00"))
        coEvery { juegoApi.historial(3) } returns
            listOf(ActividadDto(1, 3, "QR", "Escaneaste un QR", "IA en la Vida Diaria", 10, "2026-11-20T10:15:00"))

        assertTrue(repositorio.sincronizarProgreso(3).isSuccess)

        coVerify { juegoApi.escanear(match { it.codigoQr == "QR-PLAZA-ARMAS" }) }
        coVerify { juegoApi.registrarSelfie(match { it.puntoId == 1L }) }
        coVerify { descubrimientoDao.insertar(match { it.contenidoId == 2L }) }
        coVerify { descubrimientoDao.marcarVisto(3, 2, any()) }
        coVerify { selfieDao.insertar(match { it.puntoId == 2L }) }
        coVerify { historialDao.eliminarDeUsuario(3) }
        coVerify { historialDao.insertarVarios(match { it.size == 1 }) }
    }

    @Test
    fun `no se reemplaza el historial local si el del servidor esta incompleto`() = runTest {
        coEvery { juegoApi.historial(3) } returns emptyList()
        coEvery { historialDao.contar(3) } returns 4

        repositorio.sincronizarProgreso(3)

        coVerify(exactly = 0) { historialDao.eliminarDeUsuario(any()) }
    }

    @Test
    fun `registrar actualiza el mensaje de conexion`() {
        repositorio.registrar(Result.failure<Unit>(DatosPrueba.sinConexion()))
        assertEquals(false, repositorio.estado.value.conectado)

        repositorio.registrar(Result.success(Unit))
        assertEquals(true, repositorio.estado.value.conectado)
        assertNull(repositorio.estado.value.mensaje)
    }
}
