package com.triskeledu.aprendoia.ui

import com.triskeledu.aprendoia.DatosPrueba.forestal
import com.triskeledu.aprendoia.DatosPrueba.plaza
import com.triskeledu.aprendoia.ReglaCorrutinas
import com.triskeledu.aprendoia.data.local.relacion.PuntoConEstado
import com.triskeledu.aprendoia.data.repository.JuegoRepository
import com.triskeledu.aprendoia.data.repository.UsuarioRepository
import com.triskeledu.aprendoia.ui.mapa.FiltroMapa
import com.triskeledu.aprendoia.ui.mapa.MapaViewModel
import com.triskeledu.aprendoia.util.Coordenada
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/** Pruebas del mapa (pantalla 5): filtros, búsqueda, selección y cercanía. */
class MapaViewModelTest {

    @get:Rule val corrutinas = ReglaCorrutinas()

    private val usuarios = mockk<UsuarioRepository>()
    private val juego = mockk<JuegoRepository>(relaxed = true)
    private lateinit var vm: MapaViewModel

    private val completado = PuntoConEstado(plaza, descubierto = true)
    private val pendiente = PuntoConEstado(forestal, descubierto = false)

    @Before
    fun preparar() {
        every { usuarios.usuarioActivoId } returns flowOf(3L)
        every { juego.observarPuntosConEstado(3) } returns flowOf(listOf(completado, pendiente))
        vm = MapaViewModel(usuarios, juego)
    }

    @Test
    fun `carga los puntos del jugador con su estado`() {
        val estado = vm.uiState.value
        assertFalse(estado.cargando)
        assertEquals(1, estado.totalCompletados)
        assertEquals(1, estado.totalPendientes)
        assertEquals(2, estado.contar(FiltroMapa.TODOS))
    }

    @Test
    fun `filtros pendientes y completados`() {
        vm.onFiltroChange(FiltroMapa.PENDIENTES)
        assertEquals(listOf(pendiente), vm.uiState.value.puntosVisibles)

        vm.onFiltroChange(FiltroMapa.COMPLETADOS)
        assertEquals(listOf(completado), vm.uiState.value.puntosVisibles)
    }

    @Test
    fun `filtrar cierra la tarjeta si el punto elegido queda oculto`() {
        vm.onPuntoSeleccionado(plaza.id)
        vm.onFiltroChange(FiltroMapa.PENDIENTES)
        assertNull(vm.uiState.value.seleccionadoId)
    }

    @Test
    fun `busqueda por nombre y seleccion de un resultado`() {
        vm.onBusquedaChange("forestal")
        assertTrue(vm.uiState.value.mostrarResultados)
        assertEquals(listOf(pendiente), vm.uiState.value.resultadosBusqueda)

        vm.onPuntoSeleccionado(forestal.id)
        assertEquals(pendiente, vm.uiState.value.seleccionado)
        assertFalse(vm.uiState.value.mostrarResultados)

        vm.onLimpiarBusqueda()
        assertEquals("", vm.uiState.value.busqueda)
        vm.onCerrarSeleccion()
        assertNull(vm.uiState.value.seleccionado)
    }

    @Test
    fun `seleccionar un punto oculto vuelve al filtro Todos`() {
        vm.onFiltroChange(FiltroMapa.PENDIENTES)
        vm.onPuntoSeleccionado(plaza.id)
        assertEquals(FiltroMapa.TODOS, vm.uiState.value.filtro)
    }

    @Test
    fun `la ubicacion calcula distancias y revisa puntos cercanos`() {
        val aqui = Coordenada(plaza.latitud, plaza.longitud)
        vm.onUbicacion(aqui)

        assertEquals(aqui, vm.uiState.value.ubicacion)
        assertNotNull(vm.uiState.value.distanciaA(pendiente))
        coVerify { juego.verificarCercania(3, aqui) }
    }
}
