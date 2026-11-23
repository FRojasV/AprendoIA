package com.triskeledu.aprendoia.ui

import androidx.lifecycle.SavedStateHandle
import com.triskeledu.aprendoia.DatosPrueba.admin
import com.triskeledu.aprendoia.DatosPrueba.ana
import com.triskeledu.aprendoia.ReglaCorrutinas
import com.triskeledu.aprendoia.data.local.entity.Rol
import com.triskeledu.aprendoia.data.local.relacion.JugadorResumen
import com.triskeledu.aprendoia.data.repository.UsuarioRepository
import com.triskeledu.aprendoia.ui.navigation.Rutas
import com.triskeledu.aprendoia.ui.seleccion.SeleccionViewModel
import io.mockk.coEvery
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

/** Pruebas de la selección de jugador (pantalla 3): lista, login y modo administrador. */
class SeleccionViewModelTest {

    @get:Rule val corrutinas = ReglaCorrutinas()

    private val usuarios = mockk<UsuarioRepository>()
    private val jugadorAna = JugadorResumen(ana, puntaje = 120, qrEncontrados = 2)
    private val jugadorAdmin = JugadorResumen(admin, puntaje = 0, qrEncontrados = 0)

    @Before
    fun preparar() {
        every { usuarios.observarJugadores() } returns flowOf(listOf(jugadorAna, jugadorAdmin))
        every { usuarios.usuarioActivoId } returns flowOf(1L)
        coEvery { usuarios.sincronizarUsuarios() } returns Result.success(Unit)
    }

    private fun crear(modoAdmin: Boolean = false) =
        SeleccionViewModel(usuarios, SavedStateHandle(mapOf(Rutas.ARG_MODO_ADMIN to modoAdmin)))

    @Test
    fun `al abrir sincroniza y muestra todos los jugadores`() {
        val vm = crear()

        assertFalse(vm.uiState.value.cargando)
        assertEquals(2, vm.uiState.value.jugadores.size)
        coVerify { usuarios.sincronizarUsuarios() }
    }

    @Test
    fun `sin conexion avisa que se muestran los datos guardados`() {
        coEvery { usuarios.sincronizarUsuarios() } returns Result.failure(Exception("sin red"))

        val vm = crear()

        assertNotNull(vm.uiState.value.mensaje)
        vm.onMensajeMostrado()
        assertNull(vm.uiState.value.mensaje)
    }

    @Test
    fun `modo administrador solo muestra clientes`() {
        val vm = crear(modoAdmin = true)

        assertTrue(vm.uiState.value.modoAdmin)
        assertEquals(listOf(jugadorAna), vm.uiState.value.jugadores)
    }

    @Test
    fun `login sin contrasena pide escribirla`() {
        val vm = crear()
        vm.onJugadorSeleccionado(jugadorAna)

        vm.onIngresar()

        assertEquals("Ingresa la contraseña", vm.uiState.value.errorPassword)
    }

    @Test
    fun `login correcto entrega el rol para navegar`() {
        coEvery { usuarios.iniciarSesion(3, "1234") } returns Result.success(ana)
        val vm = crear()
        vm.onJugadorSeleccionado(jugadorAna)
        vm.onPasswordChange("1234")
        vm.onAlternarPassword()

        vm.onIngresar()

        assertEquals(Rol.CLIENTE, vm.uiState.value.rolIngresado)
        assertNull(vm.uiState.value.jugadorParaLogin)
        vm.onNavegacionRealizada()
        assertNull(vm.uiState.value.rolIngresado)
    }

    @Test
    fun `login incorrecto muestra el error`() {
        coEvery { usuarios.iniciarSesion(3, "9999") } returns Result.failure(IllegalArgumentException("Contraseña incorrecta"))
        val vm = crear()
        vm.onJugadorSeleccionado(jugadorAna)
        vm.onPasswordChange("9999")

        vm.onIngresar()

        assertEquals("Contraseña incorrecta", vm.uiState.value.errorPassword)
        vm.onCancelarLogin()
        assertNull(vm.uiState.value.jugadorParaLogin)
    }

    @Test
    fun `eliminar jugador confirmado`() {
        coEvery { usuarios.eliminar(ana) } returns Result.success(Unit)
        val vm = crear(modoAdmin = true)

        vm.onSolicitarEliminar(jugadorAna)
        assertEquals(jugadorAna, vm.uiState.value.jugadorParaEliminar)
        vm.onConfirmarEliminar()

        assertEquals("Se eliminó a Ana González", vm.uiState.value.mensaje)
        assertNull(vm.uiState.value.jugadorParaEliminar)
    }

    @Test
    fun `eliminar con error y cancelar`() {
        coEvery { usuarios.eliminar(ana) } returns Result.failure(Exception("Sin conexión"))
        val vm = crear(modoAdmin = true)

        vm.onSolicitarEliminar(jugadorAna)
        vm.onCancelarEliminar()
        assertNull(vm.uiState.value.jugadorParaEliminar)

        vm.onSolicitarEliminar(jugadorAna)
        vm.onConfirmarEliminar()
        assertEquals("Sin conexión", vm.uiState.value.mensaje)
    }
}
