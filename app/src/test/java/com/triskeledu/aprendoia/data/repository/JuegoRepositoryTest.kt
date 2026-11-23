package com.triskeledu.aprendoia.data.repository

import com.triskeledu.aprendoia.DatosPrueba
import com.triskeledu.aprendoia.DatosPrueba.ana
import com.triskeledu.aprendoia.DatosPrueba.capsula
import com.triskeledu.aprendoia.DatosPrueba.plaza
import com.triskeledu.aprendoia.data.local.AppDatabase
import com.triskeledu.aprendoia.data.local.dao.ContenidoDao
import com.triskeledu.aprendoia.data.local.dao.DescubrimientoDao
import com.triskeledu.aprendoia.data.local.dao.HistorialDao
import com.triskeledu.aprendoia.data.local.dao.NotificacionDao
import com.triskeledu.aprendoia.data.local.dao.PuntoDao
import com.triskeledu.aprendoia.data.local.dao.SelfieDao
import com.triskeledu.aprendoia.data.local.dao.UsuarioDao
import com.triskeledu.aprendoia.data.local.entity.TipoActividad
import com.triskeledu.aprendoia.data.local.entity.TipoNotificacion
import com.triskeledu.aprendoia.data.local.relacion.PuntoConEstado
import com.triskeledu.aprendoia.data.remote.api.JuegoApi
import com.triskeledu.aprendoia.data.remote.dto.EscaneoDto
import com.triskeledu.aprendoia.util.Coordenada
import com.triskeledu.aprendoia.util.Notificador
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/** Pruebas de las reglas del juego en la app (offline-first + envío a ms-juego). */
class JuegoRepositoryTest {

    private val db = mockk<AppDatabase>()
    private val usuarioDao = mockk<UsuarioDao>(relaxed = true)
    private val puntoDao = mockk<PuntoDao>(relaxed = true)
    private val contenidoDao = mockk<ContenidoDao>(relaxed = true)
    private val descubrimientoDao = mockk<DescubrimientoDao>(relaxed = true)
    private val selfieDao = mockk<SelfieDao>(relaxed = true)
    private val historialDao = mockk<HistorialDao>(relaxed = true)
    private val notificacionDao = mockk<NotificacionDao>(relaxed = true)
    private val notificador = mockk<Notificador>(relaxed = true)
    private val api = mockk<JuegoApi>(relaxed = true)
    private val sincronizacion = mockk<SincronizacionRepository>(relaxed = true)

    private lateinit var repositorio: JuegoRepository

    private val escaneoOk = EscaneoDto(true, 10, 50, 1, 1, "Introducción a la IA", "Plaza de Armas", true)

    @Before
    fun preparar() {
        every { db.usuarioDao() } returns usuarioDao
        every { db.puntoDao() } returns puntoDao
        every { db.contenidoDao() } returns contenidoDao
        every { db.descubrimientoDao() } returns descubrimientoDao
        every { db.selfieDao() } returns selfieDao
        every { db.historialDao() } returns historialDao
        every { db.notificacionDao() } returns notificacionDao
        coEvery { usuarioDao.obtenerPorId(3) } returns ana
        coEvery { puntoDao.obtenerPorId(1) } returns plaza
        repositorio = JuegoRepository(db, notificador, api, sincronizacion, DatosPrueba.transaccionDirecta)
    }

    // ---------------------------------------------------------------- Escaneo de QR

    @Test
    fun `sin jugador activo no se puede escanear`() = runTest {
        assertEquals(ResultadoEscaneo.SinSesion, repositorio.registrarEscaneo(null, "QR-PLAZA-ARMAS"))
    }

    @Test
    fun `QR nuevo suma puntos, guarda historial y avisa el desafio de selfie`() = runTest {
        coEvery { api.escanear(any()) } returns escaneoOk
        coEvery { contenidoDao.buscarPorCodigoQr("QR-PLAZA-ARMAS") } returns capsula
        coEvery { descubrimientoDao.insertar(any()) } returns 50L
        coEvery { selfieDao.existe(3, 1) } returns false

        val r = repositorio.registrarEscaneo(3, "QR-PLAZA-ARMAS")

        assertEquals(ResultadoEscaneo.Nuevo(capsula, 10), r)
        coVerify { historialDao.insertar(match { it.tipo == TipoActividad.QR && it.puntos == 10 }) }
        coVerify { notificacionDao.insertar(match { it.tipo == TipoNotificacion.DESAFIO }) }
        verify { notificador.mostrar(any(), any()) }
    }

    @Test
    fun `QR repetido no suma puntos`() = runTest {
        coEvery { api.escanear(any()) } returns escaneoOk.copy(nuevo = false, puntosGanados = 0)
        coEvery { contenidoDao.buscarPorCodigoQr("QR-PLAZA-ARMAS") } returns capsula
        coEvery { descubrimientoDao.insertar(any()) } returns -1L

        assertEquals(ResultadoEscaneo.Repetido(capsula), repositorio.registrarEscaneo(3, "QR-PLAZA-ARMAS"))
        coVerify(exactly = 0) { historialDao.insertar(any()) }
    }

    @Test
    fun `codigo que el servidor no conoce devuelve NoEncontrado`() = runTest {
        coEvery { api.escanear(any()) } throws DatosPrueba.errorHttp(404, "No existe")

        assertEquals(ResultadoEscaneo.NoEncontrado, repositorio.registrarEscaneo(3, "QR-FALSO"))
        coVerify(exactly = 0) { descubrimientoDao.insertar(any()) }
    }

    @Test
    fun `sin conexion el escaneo funciona con los datos locales`() = runTest {
        coEvery { api.escanear(any()) } throws DatosPrueba.sinConexion()
        coEvery { contenidoDao.buscarPorCodigoQr("QR-PLAZA-ARMAS") } returns capsula
        coEvery { descubrimientoDao.insertar(any()) } returns 7L
        coEvery { selfieDao.existe(3, 1) } returns true

        assertTrue(repositorio.registrarEscaneo(3, "QR-PLAZA-ARMAS") is ResultadoEscaneo.Nuevo)
        coVerify { sincronizacion.registrar(any()) }
    }

    @Test
    fun `sin conexion y codigo desconocido devuelve NoEncontrado`() = runTest {
        coEvery { api.escanear(any()) } throws DatosPrueba.sinConexion()
        coEvery { contenidoDao.buscarPorCodigoQr(any()) } returns null

        assertEquals(ResultadoEscaneo.NoEncontrado, repositorio.registrarEscaneo(3, "QR-NUEVO"))
        coVerify(exactly = 0) { sincronizacion.sincronizarContenidos() }
    }

    @Test
    fun `si el servidor conoce una capsula nueva se descargan los contenidos`() = runTest {
        coEvery { api.escanear(any()) } returns escaneoOk
        coEvery { contenidoDao.buscarPorCodigoQr("QR-PLAZA-ARMAS") } returnsMany listOf(null, capsula)
        coEvery { sincronizacion.sincronizarContenidos() } returns Result.success(Unit)
        coEvery { descubrimientoDao.insertar(any()) } returns 9L

        assertTrue(repositorio.registrarEscaneo(3, "QR-PLAZA-ARMAS") is ResultadoEscaneo.Nuevo)
        coVerify { sincronizacion.sincronizarContenidos() }
    }

    // ---------------------------------------------------------------- Video

    @Test
    fun `marcar video visto la primera vez registra historial y avisa al servidor`() = runTest {
        coEvery { descubrimientoDao.marcarVisto(3, 1, any()) } returns 1

        assertTrue(repositorio.marcarVideoVisto(3, capsula))
        coVerify { api.marcarVisto(match { it.capsulaId == 1L }) }
        coVerify { historialDao.insertar(match { it.tipo == TipoActividad.VIDEO }) }
    }

    @Test
    fun `marcar video ya visto no repite el historial`() = runTest {
        coEvery { descubrimientoDao.marcarVisto(3, 1, any()) } returns 0

        assertFalse(repositorio.marcarVideoVisto(3, capsula))
        coVerify(exactly = 0) { historialDao.insertar(any()) }
    }

    // ---------------------------------------------------------------- Selfie

    @Test
    fun `selfie nueva suma 100 y se envia a ms-juego`() = runTest {
        coEvery { selfieDao.insertar(any()) } returns 4L

        assertEquals(100, repositorio.registrarSelfie(3, plaza, "content://foto"))
        coVerify { api.registrarSelfie(match { it.puntoId == 1L }) }
        coVerify { historialDao.insertar(match { it.tipo == TipoActividad.SELFIE }) }
        verify { notificador.mostrar(any(), any()) }
    }

    @Test
    fun `selfie repetida no suma`() = runTest {
        coEvery { selfieDao.insertar(any()) } returns -1L

        assertNull(repositorio.registrarSelfie(3, plaza, "content://foto"))
        coVerify(exactly = 0) { api.registrarSelfie(any()) }
    }

    // ---------------------------------------------------------------- Notificaciones

    @Test
    fun `punto pendiente cercano genera un solo aviso`() = runTest {
        every { puntoDao.observarConEstado(3) } returns flowOf(listOf(PuntoConEstado(plaza, descubierto = false)))
        coEvery { notificacionDao.existe(3, TipoNotificacion.PUNTO_CERCANO, "detalle_punto/1") } returnsMany listOf(false, true)

        repositorio.verificarCercania(3, Coordenada(plaza.latitud, plaza.longitud))
        repositorio.verificarCercania(3, Coordenada(plaza.latitud, plaza.longitud))

        coVerify(exactly = 1) { notificacionDao.insertar(match { it.tipo == TipoNotificacion.PUNTO_CERCANO }) }
    }

    @Test
    fun `puntos ya descubiertos no generan aviso de cercania`() = runTest {
        every { puntoDao.observarConEstado(3) } returns flowOf(listOf(PuntoConEstado(plaza, descubierto = true)))

        repositorio.verificarCercania(3, Coordenada(plaza.latitud, plaza.longitud))

        coVerify(exactly = 0) { notificacionDao.insertar(any()) }
    }

    @Test
    fun `bienvenida y bandeja de notificaciones`() = runTest {
        repositorio.crearBienvenida(3, "Ana")
        repositorio.marcarNotificacionLeida(8)
        repositorio.marcarTodasLeidas(3)
        repositorio.eliminarNotificacion(8)

        coVerify { notificacionDao.insertar(match { it.tipo == TipoNotificacion.EVENTO && it.destino == "mapa" }) }
        coVerify { notificacionDao.marcarLeida(8) }
        coVerify { notificacionDao.marcarTodasLeidas(3) }
        coVerify { notificacionDao.eliminar(8) }
    }

    // ---------------------------------------------------------------- Consultas (delegan en Room)

    @Test
    fun `las consultas observan los DAO correspondientes`() = runTest {
        every { descubrimientoDao.observarEstadisticas(3) } returns flowOf(DatosPrueba.estadisticasVacias)
        every { puntoDao.observarTotal() } returns flowOf(8)
        every { contenidoDao.observarTotalPublicados() } returns flowOf(8)
        every { selfieDao.observarExiste(3, 1) } returns flowOf(true)
        coEvery { contenidoDao.obtenerPorId(1) } returns capsula

        assertEquals(DatosPrueba.estadisticasVacias, repositorio.observarEstadisticas(3).first())
        assertEquals(8, repositorio.observarTotalLugares().first())
        assertEquals(8, repositorio.observarTotalCapsulas().first())
        assertTrue(repositorio.observarTieneSelfie(3, 1).first())
        assertEquals(capsula, repositorio.obtenerContenido(1))
        assertEquals(plaza, repositorio.obtenerPunto(1))
    }
}
