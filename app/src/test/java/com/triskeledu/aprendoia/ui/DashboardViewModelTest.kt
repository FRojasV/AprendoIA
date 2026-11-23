package com.triskeledu.aprendoia.ui

import com.triskeledu.aprendoia.DatosPrueba.ana
import com.triskeledu.aprendoia.DatosPrueba.plaza
import com.triskeledu.aprendoia.ReglaCorrutinas
import com.triskeledu.aprendoia.data.local.entity.UsuarioEntity
import com.triskeledu.aprendoia.data.local.relacion.EstadisticasJugador
import com.triskeledu.aprendoia.data.repository.Clima
import com.triskeledu.aprendoia.data.repository.ClimaRepository
import com.triskeledu.aprendoia.data.repository.EstadoConexion
import com.triskeledu.aprendoia.data.repository.JuegoRepository
import com.triskeledu.aprendoia.data.repository.SincronizacionRepository
import com.triskeledu.aprendoia.data.repository.UsuarioRepository
import com.triskeledu.aprendoia.ui.dashboard.DashboardViewModel
import com.triskeledu.aprendoia.util.Coordenada
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** Pruebas del Dashboard: datos del jugador + microservicios + API externa de clima. */
class DashboardViewModelTest {

    @get:Rule val corrutinas = ReglaCorrutinas()

    private val usuarios = mockk<UsuarioRepository>()
    private val juego = mockk<JuegoRepository>(relaxed = true)
    private val sincronizacion = mockk<SincronizacionRepository>()
    private val clima = mockk<ClimaRepository>()
    private val conexion = MutableStateFlow(EstadoConexion())

    private val soleado = Clima(24, 23, 40, 8, "Despejado", "☀️", esDeDia = true)

    private fun crear(usuario: UsuarioEntity? = ana, climaOk: Boolean = true): DashboardViewModel {
        every { usuarios.usuarioActivo } returns flowOf(usuario)
        every { usuarios.usuarioActivoId } returns flowOf(usuario?.id)
        every { juego.observarEstadisticas(3) } returns flowOf(EstadisticasJugador(20, 100, 2, 1, 1, 2))
        every { juego.observarTotalLugares() } returns flowOf(8)
        every { juego.observarDesafioActual(3) } returns flowOf(plaza)
        every { juego.observarNotificacionesNoLeidas(3) } returns flowOf(4)
        every { sincronizacion.estado } returns conexion
        coEvery { sincronizacion.sincronizarContenidos() } returns Result.success(Unit)
        coEvery { sincronizacion.sincronizarProgreso(any()) } returns Result.success(Unit)
        coEvery { clima.climaActual(any(), any()) } returns
            if (climaOk) Result.success(soleado) else Result.failure(RuntimeException("timeout"))
        return DashboardViewModel(usuarios, juego, sincronizacion, clima)
    }

    @Test
    fun `muestra puntaje, progreso, desafio y avisos del jugador`() {
        val estado = crear().uiState.value

        assertFalse(estado.cargando)
        assertEquals(120, estado.puntaje)
        assertEquals(0.25f, estado.progresoLugares)
        assertEquals(plaza, estado.desafio)
        assertEquals(4, estado.notificacionesNoLeidas)
    }

    @Test
    fun `al abrir sincroniza contenidos y progreso con los microservicios`() {
        crear()
        coVerify { sincronizacion.sincronizarContenidos() }
        coVerify { sincronizacion.sincronizarProgreso(3) }
    }

    @Test
    fun `sin sesion lo indica y no pide progreso`() {
        val vm = crear(usuario = null)
        assertTrue(vm.uiState.value.sinSesion)
        assertEquals(0f, vm.uiState.value.progresoLugares)
        coVerify(exactly = 0) { sincronizacion.sincronizarProgreso(any()) }
    }

    @Test
    fun `clima de la API externa se muestra en la tarjeta`() {
        val estado = crear().uiState.value.clima
        assertEquals(soleado, estado.clima)
        assertFalse(estado.cargando)
        assertEquals("Buen momento para buscar códigos QR.", soleado.consejo)
    }

    @Test
    fun `si falla el clima el resto del dashboard sigue funcionando`() {
        val vm = crear(climaOk = false)
        assertEquals("No se pudo obtener el clima", vm.uiState.value.clima.error)
        assertEquals(120, vm.uiState.value.puntaje)

        coEvery { clima.climaActual(any(), any()) } returns Result.success(soleado)
        vm.cargarClima()
        assertEquals(soleado, vm.uiState.value.clima.clima)
    }

    @Test
    fun `aviso de conexion aparece y desaparece segun los microservicios`() {
        val vm = crear()
        assertNull(vm.uiState.value.avisoConexion)

        conexion.value = EstadoConexion(conectado = false, mensaje = "Sin conexión con el servidor")
        assertEquals("Sin conexión con el servidor", vm.uiState.value.avisoConexion)

        conexion.value = EstadoConexion(conectado = true)
        assertNull(vm.uiState.value.avisoConexion)
    }

    @Test
    fun `la ubicacion revisa si hay un punto pendiente cerca`() {
        val vm = crear()
        val aqui = Coordenada(-33.4378, -70.6505)
        vm.onUbicacion(aqui)
        coVerify { juego.verificarCercania(3, aqui) }
    }
}
