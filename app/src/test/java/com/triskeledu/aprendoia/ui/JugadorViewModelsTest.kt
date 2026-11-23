package com.triskeledu.aprendoia.ui

import com.triskeledu.aprendoia.DatosPrueba.ana
import com.triskeledu.aprendoia.ReglaCorrutinas
import com.triskeledu.aprendoia.data.local.entity.HistorialEntity
import com.triskeledu.aprendoia.data.local.entity.TipoActividad
import com.triskeledu.aprendoia.data.local.relacion.EstadisticasJugador
import com.triskeledu.aprendoia.data.repository.JuegoRepository
import com.triskeledu.aprendoia.data.repository.UsuarioRepository
import com.triskeledu.aprendoia.ui.historial.FiltroHistorial
import com.triskeledu.aprendoia.ui.historial.HistorialViewModel
import com.triskeledu.aprendoia.ui.perfil.PerfilViewModel
import com.triskeledu.aprendoia.ui.progreso.ProgresoViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** Pruebas de Historial, Progreso y Perfil (pantallas 11, 12 y 13). */
class JugadorViewModelsTest {

    @get:Rule val corrutinas = ReglaCorrutinas()

    private val usuarios = mockk<UsuarioRepository>()
    private val juego = mockk<JuegoRepository>(relaxed = true)
    private val stats = EstadisticasJugador(20, 100, 2, 1, 1, 2)
    private val qr = HistorialEntity(usuarioId = 3, tipo = TipoActividad.QR, titulo = "Plaza de Armas", puntos = 10)

    init {
        every { usuarios.usuarioActivoId } returns flowOf(3L)
        every { usuarios.usuarioActivo } returns flowOf(ana)
        every { juego.observarHistorial(3) } returns flowOf(listOf(qr))
        every { juego.observarEstadisticas(3) } returns flowOf(stats)
        every { juego.observarTotalLugares() } returns flowOf(8)
        every { juego.observarTotalCapsulas() } returns flowOf(8)
    }

    @Test
    fun `historial carga las actividades y cambia de filtro`() {
        val vm = HistorialViewModel(usuarios, juego)
        assertFalse(vm.uiState.value.cargando)
        assertEquals(10, vm.uiState.value.totalPuntos)
        vm.onFiltro(FiltroHistorial.SELFIES)
        assertEquals(0, vm.uiState.value.totalPuntos)
    }

    @Test
    fun `progreso combina estadisticas, totales e historial`() {
        val estado = ProgresoViewModel(usuarios, juego).uiState.value
        assertEquals(120, estado.puntaje)
        assertEquals(8, estado.totalCapsulas)
        assertEquals(listOf(qr), estado.historial)
    }

    @Test
    fun `sin sesion historial y progreso quedan vacios`() {
        every { usuarios.usuarioActivoId } returns flowOf(null)
        assertTrue(HistorialViewModel(usuarios, juego).uiState.value.actividades.isEmpty())
        assertEquals(0, ProgresoViewModel(usuarios, juego).uiState.value.puntaje)
    }

    @Test
    fun `perfil cambia el avatar y lo envia al servidor`() {
        coEvery { usuarios.actualizar(any(), any()) } returns Result.success(Unit)
        val vm = PerfilViewModel(usuarios, juego)

        vm.onCambiarAvatarClick()
        assertTrue(vm.uiState.value.mostrarDialogoAvatar)
        vm.onAvatarTemporal("avatar_4")
        vm.onGuardarAvatar()

        coVerify { usuarios.actualizar(match { it.avatar == "avatar_4" }, any()) }
        assertEquals("Avatar actualizado", vm.uiState.value.mensaje)
        vm.onMensajeMostrado()
        assertNull(vm.uiState.value.mensaje)
    }

    @Test
    fun `perfil activa notificaciones, muestra dialogos y cierra sesion`() {
        coEvery { usuarios.actualizar(any(), any()) } returns Result.success(Unit)
        coEvery { usuarios.cerrarSesion() } returns Unit
        val vm = PerfilViewModel(usuarios, juego)

        vm.onNotificacionesChange(false)
        assertEquals("Notificaciones desactivadas", vm.uiState.value.mensaje)
        vm.onPermisoNotificacionDenegado()
        assertEquals("Sin permiso no se pueden mostrar notificaciones", vm.uiState.value.mensaje)

        vm.onMostrarPermisos(true)
        vm.onMostrarAyuda(true)
        vm.onMostrarConfirmarCierre(true)
        vm.onCancelarAvatar()
        assertTrue(vm.uiState.value.mostrarDialogoPermisos && vm.uiState.value.mostrarAyuda)

        vm.onCerrarSesion()
        assertTrue(vm.uiState.value.sesionCerrada)
        assertFalse(vm.uiState.value.mostrarConfirmarCierre)
    }
}
