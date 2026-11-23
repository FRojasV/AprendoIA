package com.triskeledu.aprendoia.ui

import com.triskeledu.aprendoia.DatosPrueba.capsula
import com.triskeledu.aprendoia.ReglaCorrutinas
import com.triskeledu.aprendoia.data.repository.JuegoRepository
import com.triskeledu.aprendoia.data.repository.ResultadoEscaneo
import com.triskeledu.aprendoia.data.repository.UsuarioRepository
import com.triskeledu.aprendoia.ui.escaner.EscanerViewModel
import com.triskeledu.aprendoia.ui.escaner.IrAResultado
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/** Pruebas del escáner QR (pantalla 7). */
class EscanerViewModelTest {

    @get:Rule val corrutinas = ReglaCorrutinas()

    private val usuarios = mockk<UsuarioRepository>()
    private val juego = mockk<JuegoRepository>()
    private lateinit var vm: EscanerViewModel

    @Before
    fun preparar() {
        every { usuarios.usuarioActivoId } returns flowOf(3L)
        vm = EscanerViewModel(usuarios, juego)
    }

    @Test
    fun `QR nuevo navega al resultado con los puntos ganados`() {
        coEvery { juego.registrarEscaneo(3, "QR-PLAZA-ARMAS") } returns ResultadoEscaneo.Nuevo(capsula, 10)

        vm.onTextoLeido("qr-plaza-armas")

        assertEquals(IrAResultado(1, 10), vm.uiState.value.irAResultado)
        vm.onNavegacionRealizada()
        assertNull(vm.uiState.value.irAResultado)
    }

    @Test
    fun `QR repetido navega con cero puntos`() {
        coEvery { juego.registrarEscaneo(3, "QR-PLAZA-ARMAS") } returns ResultadoEscaneo.Repetido(capsula)

        vm.onTextoLeido("QR-PLAZA-ARMAS")

        assertEquals(IrAResultado(1, 0), vm.uiState.value.irAResultado)
    }

    @Test
    fun `codigo inexistente muestra error`() {
        coEvery { juego.registrarEscaneo(3, "QR-FALSO") } returns ResultadoEscaneo.NoEncontrado

        vm.onTextoLeido("QR-FALSO")

        assertTrue(vm.uiState.value.esError)
        assertFalse(vm.uiState.value.procesando)
    }

    @Test
    fun `sin sesion muestra error`() {
        coEvery { juego.registrarEscaneo(3, "QR-PLAZA-ARMAS") } returns ResultadoEscaneo.SinSesion

        vm.onTextoLeido("QR-PLAZA-ARMAS")

        assertEquals("Selecciona un jugador antes de escanear.", vm.uiState.value.mensaje)
    }

    @Test
    fun `texto vacio o escaneo cancelado muestran error sin llamar al repositorio`() {
        vm.onTextoLeido("   ")
        assertTrue(vm.uiState.value.esError)

        vm.onEscaneoFallido("Escaneo cancelado.")
        assertEquals("Escaneo cancelado.", vm.uiState.value.mensaje)
        coVerify(exactly = 0) { juego.registrarEscaneo(any(), any()) }
    }

    @Test
    fun `ingreso manual del codigo`() {
        coEvery { juego.registrarEscaneo(3, "QR-LASTARRIA") } returns ResultadoEscaneo.Nuevo(capsula.copy(id = 5), 10)

        vm.onAlternarIngresoManual()
        assertTrue(vm.uiState.value.mostrarIngresoManual)
        vm.onConfirmarCodigoManual()
        assertTrue(vm.uiState.value.esError) // vacío

        vm.onCodigoManualChange("qr-lastarria")
        assertEquals("QR-LASTARRIA", vm.uiState.value.codigoManual)
        vm.onConfirmarCodigoManual()

        assertEquals(IrAResultado(5, 10), vm.uiState.value.irAResultado)
        assertFalse(vm.uiState.value.mostrarIngresoManual)
    }
}
