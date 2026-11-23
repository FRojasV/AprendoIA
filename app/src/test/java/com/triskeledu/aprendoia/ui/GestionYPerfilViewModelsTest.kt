package com.triskeledu.aprendoia.ui

import androidx.lifecycle.SavedStateHandle
import com.triskeledu.aprendoia.DatosPrueba.admin
import com.triskeledu.aprendoia.DatosPrueba.ana
import com.triskeledu.aprendoia.DatosPrueba.capsula
import com.triskeledu.aprendoia.DatosPrueba.plaza
import com.triskeledu.aprendoia.ReglaCorrutinas
import com.triskeledu.aprendoia.data.local.relacion.ResumenGestion
import com.triskeledu.aprendoia.data.repository.GestionRepository
import com.triskeledu.aprendoia.data.repository.UsuarioRepository
import com.triskeledu.aprendoia.ui.editarjugador.EditarJugadorViewModel
import com.triskeledu.aprendoia.ui.gestion.viewmodel.PanelGestionViewModel
import com.triskeledu.aprendoia.ui.gestion.viewmodel.QrCapsulaViewModel
import com.triskeledu.aprendoia.ui.navigation.Rutas
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
import org.junit.Rule
import org.junit.Test

/** Pruebas del Panel de gestión, el QR de la cápsula y la edición de jugador. */
class GestionYPerfilViewModelsTest {

    @get:Rule val corrutinas = ReglaCorrutinas()

    private val usuarios = mockk<UsuarioRepository>(relaxed = true)
    private val gestion = mockk<GestionRepository>(relaxed = true)

    private fun editar(id: Long = 3) =
        EditarJugadorViewModel(usuarios, SavedStateHandle(mapOf(Rutas.ARG_USUARIO_ID to id)))

    private fun qr(id: Long = 1) =
        QrCapsulaViewModel(gestion, SavedStateHandle(mapOf(Rutas.ARG_CONTENIDO_ID to id)))

    @Test
    fun `panel combina usuario y resumen, y cierra la sesion`() {
        val resumen = ResumenGestion(puntos = 8, capsulas = 8, pendientes = 1, jugadores = 2)
        every { usuarios.usuarioActivo } returns flowOf(admin)
        every { gestion.observarResumen() } returns flowOf(resumen)

        val vm = PanelGestionViewModel(usuarios, gestion)
        assertEquals(admin, vm.uiState.value.usuario)
        assertEquals(resumen, vm.uiState.value.resumen)

        vm.cerrarSesion()

        coVerify { usuarios.cerrarSesion() }
        assertTrue(vm.uiState.value.sesionCerrada)
    }

    @Test
    fun `qr arma el contenido con los datos de la capsula`() {
        coEvery { gestion.obtenerCapsula(1) } returns capsula
        coEvery { gestion.obtenerPunto(1) } returns plaza

        val estado = qr().uiState.value

        assertFalse(estado.cargando)
        assertEquals("QR-PLAZA-ARMAS", estado.codigo)
        assertEquals("Plaza de Armas", estado.puntoNombre)
        assertTrue(estado.contenidoQr.contains("QR-PLAZA-ARMAS"))
    }

    @Test
    fun `qr avisa si la capsula no existe y limpia el mensaje`() {
        coEvery { gestion.obtenerCapsula(9) } returns null

        val vm = qr(9)
        assertEquals("La cápsula no existe", vm.uiState.value.mensaje)

        vm.mensajeMostrado()
        assertNull(vm.uiState.value.mensaje)
        vm.mostrarMensaje("QR guardado")
        assertEquals("QR guardado", vm.uiState.value.mensaje)
    }

    @Test
    fun `editar carga los datos del jugador`() {
        coEvery { usuarios.obtenerPorId(3) } returns ana

        val estado = editar().uiState.value

        assertFalse(estado.cargando)
        assertEquals("Ana González", estado.nombre)
        assertEquals("ana@email.com", estado.correo)
    }

    @Test
    fun `editar avisa si el jugador no existe y no guarda`() {
        coEvery { usuarios.obtenerPorId(9) } returns null

        val vm = editar(9)
        assertEquals("El jugador no existe", vm.uiState.value.mensajeError)

        vm.onGuardar()
        coVerify(exactly = 0) { usuarios.actualizar(any(), any()) }
        vm.onMensajeMostrado()
        assertNull(vm.uiState.value.mensajeError)
    }

    @Test
    fun `editar valida los campos y al corregir limpia el error`() {
        coEvery { usuarios.obtenerPorId(3) } returns ana
        val vm = editar()
        vm.onNombreChange("A")
        vm.onCorreoChange("correo-malo")
        vm.onTelefonoChange("12")
        vm.onPasswordChange("1")

        vm.onGuardar()

        val errores = vm.uiState.value.errores
        assertNotNull(errores.nombre)
        assertNotNull(errores.correo)
        assertNotNull(errores.telefono)
        assertNotNull(errores.password)
        coVerify(exactly = 0) { usuarios.actualizar(any(), any()) }

        vm.onNombreChange("Ana María")
        vm.onCorreoChange("ana.maria@email.com")
        vm.onTelefonoChange("+56 9 1234 5678")
        vm.onPasswordChange("")
        assertNull(vm.uiState.value.errores.nombre)
        assertNull(vm.uiState.value.errores.correo)
        assertNull(vm.uiState.value.errores.telefono)
        assertNull(vm.uiState.value.errores.password)
    }

    @Test
    fun `editar guarda los cambios y actualiza el alias`() {
        coEvery { usuarios.obtenerPorId(3) } returns ana
        coEvery { usuarios.actualizar(any(), any()) } returns Result.success(Unit)
        val vm = editar()
        vm.onNombreChange("  Ana María  ")
        vm.onDireccionChange(" Calle 123 ")
        vm.onAvatarSeleccionado("avatar_2")
        vm.onPasswordChange("clave1")

        vm.onGuardar()

        assertTrue(vm.uiState.value.guardado)
        assertFalse(vm.uiState.value.guardando)
        coVerify {
            usuarios.actualizar(
                match {
                    it.nombre == "Ana María" && it.alias == "Ana" &&
                        it.direccion == "Calle 123" && it.avatar == "avatar_2"
                },
                "clave1"
            )
        }
    }

    @Test
    fun `editar una cuenta del sistema no exige telefono y mantiene el alias`() {
        coEvery { usuarios.obtenerPorId(1) } returns admin
        coEvery { usuarios.actualizar(any(), any()) } returns Result.success(Unit)
        val vm = editar(1)

        vm.onGuardar()

        assertTrue(vm.uiState.value.guardado)
        coVerify { usuarios.actualizar(match { it.alias == "Admin" }, "") }
    }

    @Test
    fun `editar muestra el correo repetido bajo su campo`() {
        coEvery { usuarios.obtenerPorId(3) } returns ana
        coEvery { usuarios.actualizar(any(), any()) } returns
            Result.failure(IllegalArgumentException("El correo ya está registrado"))
        val vm = editar()

        vm.onGuardar()

        assertEquals("El correo ya está registrado", vm.uiState.value.errores.correo)
        assertFalse(vm.uiState.value.guardado)
    }

    @Test
    fun `editar muestra los errores del servidor como mensaje`() {
        coEvery { usuarios.obtenerPorId(3) } returns ana
        coEvery { usuarios.actualizar(any(), any()) } returns
            Result.failure(IllegalStateException("Servidor no disponible"))
        val vm = editar()

        vm.onGuardar()

        assertEquals("Servidor no disponible", vm.uiState.value.mensajeError)
        assertFalse(vm.uiState.value.guardando)
    }

    @Test
    fun `editar alterna la contrasena y los avatares`() {
        coEvery { usuarios.obtenerPorId(3) } returns ana
        val vm = editar()

        vm.onAlternarPassword()
        vm.onAlternarAvatares()

        assertTrue(vm.uiState.value.passwordVisible)
        // La lista de avatares parte desplegada: al alternar se contrae.
        assertFalse(vm.uiState.value.mostrarTodosAvatares)
    }
}
