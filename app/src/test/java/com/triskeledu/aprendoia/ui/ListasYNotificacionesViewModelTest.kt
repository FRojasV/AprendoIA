package com.triskeledu.aprendoia.ui

import com.triskeledu.aprendoia.DatosPrueba.admin
import com.triskeledu.aprendoia.DatosPrueba.ana
import com.triskeledu.aprendoia.DatosPrueba.capsula
import com.triskeledu.aprendoia.DatosPrueba.forestal
import com.triskeledu.aprendoia.DatosPrueba.plaza
import com.triskeledu.aprendoia.ReglaCorrutinas
import com.triskeledu.aprendoia.data.local.entity.NotificacionEntity
import com.triskeledu.aprendoia.data.local.entity.TipoNotificacion
import com.triskeledu.aprendoia.data.local.relacion.CapsulaGestion
import com.triskeledu.aprendoia.data.remote.ErrorServidor
import com.triskeledu.aprendoia.data.repository.GestionRepository
import com.triskeledu.aprendoia.data.repository.JuegoRepository
import com.triskeledu.aprendoia.data.repository.UsuarioRepository
import com.triskeledu.aprendoia.ui.gestion.model.FiltroCapsulasGestion
import com.triskeledu.aprendoia.ui.gestion.viewmodel.ListaCapsulasViewModel
import com.triskeledu.aprendoia.ui.gestion.viewmodel.ListaPuntosViewModel
import com.triskeledu.aprendoia.ui.notificaciones.FiltroNotificaciones
import com.triskeledu.aprendoia.ui.notificaciones.NotificacionesViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** Pruebas de las listas del panel de gestión (CRUD contra ms-contenidos) y de la bandeja de avisos. */
class ListasYNotificacionesViewModelTest {

    @get:Rule val corrutinas = ReglaCorrutinas()

    private val gestion = mockk<GestionRepository>()
    private val usuarios = mockk<UsuarioRepository>()
    private val juego = mockk<JuegoRepository>(relaxed = true)

    private val publicada = CapsulaGestion(capsula, "Plaza de Armas")
    private val pendiente = CapsulaGestion(capsula.copy(id = 2, codigoQr = "QR-NUEVA", publicado = false), "Parque Forestal")

    // ================================================================ Lista de puntos

    @Test
    fun `lista de puntos refresca del servidor y elimina con confirmacion`() {
        coEvery { gestion.refrescar() } returns Result.success(Unit)
        every { gestion.observarPuntos() } returns flowOf(listOf(plaza, forestal))
        coEvery { gestion.eliminarPunto(forestal) } returns Result.success(Unit)
        val vm = ListaPuntosViewModel(gestion)

        assertEquals(2, vm.uiState.value.puntos.size)
        vm.solicitarEliminar(forestal)
        assertEquals(forestal, vm.uiState.value.puntoParaEliminar)
        vm.confirmarEliminar()

        assertNull(vm.uiState.value.puntoParaEliminar)
        assertEquals("Se eliminó \"Parque Forestal\"", vm.uiState.value.mensaje)
        vm.mensajeMostrado()
        assertNull(vm.uiState.value.mensaje)
    }

    @Test
    fun `lista de puntos sin servidor muestra el error y permite cancelar`() {
        coEvery { gestion.refrescar() } returns Result.failure(ErrorServidor("Sin conexión con el servidor"))
        every { gestion.observarPuntos() } returns flowOf(listOf(plaza))
        val vm = ListaPuntosViewModel(gestion)

        assertEquals("Sin conexión con el servidor", vm.uiState.value.mensaje)
        vm.solicitarEliminar(plaza)
        vm.cancelarEliminar()
        vm.confirmarEliminar() // no hay nada seleccionado: no hace nada
        coVerify(exactly = 0) { gestion.eliminarPunto(any()) }
    }

    // ================================================================ Lista de cápsulas

    private fun listaCapsulas(esAdmin: Boolean = true): ListaCapsulasViewModel {
        coEvery { gestion.refrescar() } returns Result.success(Unit)
        every { usuarios.usuarioActivo } returns flowOf(if (esAdmin) admin else ana)
        every { gestion.observarCapsulas() } returns flowOf(listOf(publicada, pendiente))
        return ListaCapsulasViewModel(usuarios, gestion)
    }

    @Test
    fun `filtros de capsulas pendientes y publicadas`() {
        val vm = listaCapsulas()
        assertEquals(2, vm.uiState.value.visibles.size)
        vm.cambiarFiltro(FiltroCapsulasGestion.PENDIENTES)
        assertEquals(listOf(pendiente), vm.uiState.value.visibles)
        vm.cambiarFiltro(FiltroCapsulasGestion.PUBLICADAS)
        assertEquals(listOf(publicada), vm.uiState.value.visibles)
    }

    @Test
    fun `el administrador publica y elimina capsulas`() {
        coEvery { gestion.publicar(any()) } returns Result.success(Unit)
        coEvery { gestion.eliminarCapsula(any()) } returns Result.failure(ErrorServidor("No se pudo eliminar"))
        val vm = listaCapsulas()

        vm.publicar(pendiente)
        assertEquals("Publicada. Se avisó a los jugadores 🎉", vm.uiState.value.mensaje)

        vm.solicitarEliminar(publicada)
        vm.confirmarEliminar()
        assertEquals("No se pudo eliminar", vm.uiState.value.mensaje)
        vm.cancelarEliminar()
        assertNull(vm.uiState.value.capsulaParaEliminar)
    }

    @Test
    fun `quien no es administrador no puede publicar`() {
        val vm = listaCapsulas(esAdmin = false)
        vm.publicar(pendiente)
        coVerify(exactly = 0) { gestion.publicar(any()) }
    }

    // ================================================================ Notificaciones

    private val bienvenida = NotificacionEntity(
        id = 1, usuarioId = 3, titulo = "¡Bienvenida!", mensaje = "Explora el mapa",
        tipo = TipoNotificacion.EVENTO, destino = "mapa"
    )
    private val leida = bienvenida.copy(id = 2, destino = null, leida = true)

    private fun notificaciones(): NotificacionesViewModel {
        every { usuarios.usuarioActivoId } returns flowOf(3L)
        every { juego.observarNotificaciones(3) } returns flowOf(listOf(bienvenida, leida))
        return NotificacionesViewModel(usuarios, juego)
    }

    @Test
    fun `bandeja cuenta y filtra los avisos no leidos`() {
        val vm = notificaciones()
        assertEquals(1, vm.uiState.value.noLeidas)
        vm.onFiltro(FiltroNotificaciones.NO_LEIDAS)
        assertEquals(listOf(bienvenida), vm.uiState.value.visibles)
    }

    @Test
    fun `abrir un aviso lo marca leido y navega a su destino`() {
        val vm = notificaciones()
        vm.onAbrir(bienvenida)
        coVerify { juego.marcarNotificacionLeida(1) }
        assertEquals("mapa", vm.uiState.value.irA)
        vm.onNavegacionRealizada()
        assertNull(vm.uiState.value.irA)

        vm.onAbrir(leida) // ya leído y sin destino
        coVerify(exactly = 0) { juego.marcarNotificacionLeida(2) }
        assertNull(vm.uiState.value.irA)
    }

    @Test
    fun `marcar todas y eliminar delegan en el repositorio`() {
        val vm = notificaciones()
        vm.onMarcarTodas()
        vm.onEliminar(leida)
        coVerify { juego.marcarTodasLeidas(3) }
        coVerify { juego.eliminarNotificacion(2) }
    }

    @Test
    fun `sin sesion la bandeja queda vacia`() {
        every { usuarios.usuarioActivoId } returns flowOf(null)
        val vm = NotificacionesViewModel(usuarios, juego)
        assertTrue(vm.uiState.value.notificaciones.isEmpty())
    }
}
