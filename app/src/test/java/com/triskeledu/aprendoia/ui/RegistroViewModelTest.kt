package com.triskeledu.aprendoia.ui

import com.triskeledu.aprendoia.ReglaCorrutinas
import com.triskeledu.aprendoia.data.remote.ErrorServidor
import com.triskeledu.aprendoia.data.repository.JuegoRepository
import com.triskeledu.aprendoia.data.repository.UsuarioRepository
import com.triskeledu.aprendoia.ui.registro.RegistroViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** Pruebas del formulario de registro (pantalla 2): validación + creación en ms-usuarios. */
class RegistroViewModelTest {

    @get:Rule val corrutinas = ReglaCorrutinas()

    private val usuarios = mockk<UsuarioRepository>()
    private val juego = mockk<JuegoRepository>(relaxed = true)
    private val vm = RegistroViewModel(usuarios, juego)

    private fun completarFormulario() {
        vm.onNombreChange("Ana González")
        vm.onCorreoChange("ana@email.com")
        vm.onTelefonoChange("+56 9 1234 5678")
        vm.onPasswordChange("1234")
        vm.onDireccionChange("Providencia")
        vm.onAvatarSeleccionado("avatar_3")
    }

    @Test
    fun `formulario vacio muestra errores en todos los campos obligatorios`() {
        vm.onCrearJugador()

        val errores = vm.uiState.value.errores
        assertNotNull(errores.nombre)
        assertNotNull(errores.correo)
        assertNotNull(errores.telefono)
        assertNotNull(errores.password)
        coVerify(exactly = 0) { usuarios.registrarCliente(any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `correo invalido y telefono con letras`() {
        completarFormulario()
        vm.onCorreoChange("ana@")
        vm.onTelefonoChange("abc123")

        vm.onCrearJugador()

        assertEquals("El correo no es válido", vm.uiState.value.errores.correo)
        assertEquals("123", vm.uiState.value.telefono) // las letras se descartan al escribir
    }

    @Test
    fun `al escribir se borra el error del campo`() {
        vm.onCrearJugador()
        vm.onNombreChange("Ana")
        assertNull(vm.uiState.value.errores.nombre)
    }

    @Test
    fun `registro correcto crea la bienvenida y avisa a la pantalla`() {
        completarFormulario()
        coEvery { usuarios.registrarCliente(any(), any(), any(), any(), any(), any()) } returns Result.success(10L)

        vm.onCrearJugador()

        assertTrue(vm.uiState.value.registroExitoso)
        assertFalse(vm.uiState.value.guardando)
        coVerify { juego.crearBienvenida(10L, "Ana") }
        coVerify { usuarios.registrarCliente("Ana González", "ana@email.com", "+56 9 1234 5678", "1234", "Providencia", "avatar_3") }
    }

    @Test
    fun `correo repetido en el servidor se muestra bajo el campo`() {
        completarFormulario()
        coEvery { usuarios.registrarCliente(any(), any(), any(), any(), any(), any()) } returns
            Result.failure(IllegalArgumentException("Ya existe un jugador con ese correo"))

        vm.onCrearJugador()

        assertEquals("Ya existe un jugador con ese correo", vm.uiState.value.errores.correo)
    }

    @Test
    fun `sin conexion se muestra un mensaje general`() {
        completarFormulario()
        coEvery { usuarios.registrarCliente(any(), any(), any(), any(), any(), any()) } returns
            Result.failure(ErrorServidor("Sin conexión con el servidor"))

        vm.onCrearJugador()

        assertEquals("Sin conexión con el servidor", vm.uiState.value.mensajeError)
        vm.onMensajeMostrado()
        assertNull(vm.uiState.value.mensajeError)
    }

    @Test
    fun `alternar password y avatares`() {
        vm.onAlternarPassword()
        vm.onAlternarAvatares()
        assertTrue(vm.uiState.value.passwordVisible)
        assertTrue(vm.uiState.value.mostrarTodosAvatares)
    }
}
