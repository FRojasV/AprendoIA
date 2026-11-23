package com.triskeledu.aprendoia.ui

import androidx.lifecycle.SavedStateHandle
import com.triskeledu.aprendoia.DatosPrueba.admin
import com.triskeledu.aprendoia.DatosPrueba.capsula
import com.triskeledu.aprendoia.DatosPrueba.plaza
import com.triskeledu.aprendoia.ReglaCorrutinas
import com.triskeledu.aprendoia.data.local.entity.Rol
import com.triskeledu.aprendoia.data.remote.ErrorServidor
import com.triskeledu.aprendoia.data.repository.GestionRepository
import com.triskeledu.aprendoia.data.repository.UsuarioRepository
import com.triskeledu.aprendoia.ui.gestion.viewmodel.FormularioCapsulaViewModel
import com.triskeledu.aprendoia.ui.gestion.viewmodel.FormularioPuntoViewModel
import com.triskeledu.aprendoia.ui.navigation.Rutas
import com.triskeledu.aprendoia.util.Coordenada
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

/** Pruebas de los formularios del panel de gestión (patrón CampoFormulario del curso). */
class FormulariosGestionViewModelTest {

    @get:Rule val corrutinas = ReglaCorrutinas()

    private val gestion = mockk<GestionRepository>()
    private val usuarios = mockk<UsuarioRepository>()

    // ================================================================ Punto

    private fun formularioPunto(id: Long = -1) =
        FormularioPuntoViewModel(gestion, SavedStateHandle(mapOf(Rutas.ARG_PUNTO_ID to id)))

    @Test
    fun `punto - validaciones en vivo y al enviar`() {
        val vm = formularioPunto()
        vm.cambiarNombre("Pl")
        assertEquals("Escribe al menos 3 caracteres", vm.uiState.value.nombre.error)

        vm.cambiarLatitud("-95")
        assertEquals("Debe estar entre -90 y 90", vm.uiState.value.latitud.error)
        vm.cambiarLongitud("abc") // se descarta: no es número
        assertEquals("", vm.uiState.value.longitud.valor)

        vm.enviar()
        assertTrue(vm.uiState.value.intentoEnviar)
        assertFalse(vm.uiState.value.formularioValido)
        coVerify(exactly = 0) { gestion.guardarPunto(any()) }
    }

    @Test
    fun `punto - elegir en el mapa completa las coordenadas y guarda`() {
        coEvery { gestion.guardarPunto(any()) } returns Result.success(20L)
        val vm = formularioPunto()
        vm.cambiarNombre("Cerro Huelén")
        vm.cambiarDescripcion("Mirador del cerro Santa Lucía")
        vm.cambiarInfoTuristica("Antiguo nombre del cerro")
        vm.elegirEnMapa(Coordenada(-33.44051, -70.64402))
        vm.cambiarHito(true)

        assertEquals("-33.440510", vm.uiState.value.latitud.valor)
        assertNotNull(vm.uiState.value.coordenada)
        vm.enviar()

        assertTrue(vm.uiState.value.guardado)
        coVerify { gestion.guardarPunto(match { it.esHitoTuristico && it.latitud == -33.44051 }) }
    }

    @Test
    fun `punto - editar carga los datos y muestra error del servidor`() {
        coEvery { gestion.obtenerPunto(1) } returns plaza
        coEvery { gestion.guardarPunto(any()) } returns Result.failure(ErrorServidor("Sin conexión con el servidor"))
        val vm = formularioPunto(1)

        assertTrue(vm.uiState.value.esEdicion)
        assertEquals("Plaza de Armas", vm.uiState.value.nombre.valor)
        vm.enviar()

        assertEquals("Sin conexión con el servidor", vm.uiState.value.mensajeError)
        assertFalse(vm.uiState.value.guardado)
    }

    // ================================================================ Cápsula

    private fun formularioCapsula(id: Long = -1): FormularioCapsulaViewModel {
        every { usuarios.usuarioActivo } returns flowOf(admin)
        every { gestion.observarPuntos() } returns flowOf(listOf(plaza))
        coEvery { gestion.codigoDisponible(any(), any()) } returns true
        return FormularioCapsulaViewModel(usuarios, gestion, SavedStateHandle(mapOf(Rutas.ARG_CONTENIDO_ID to id)))
    }

    @Test
    fun `capsula - nueva del administrador viene publicada y con autor`() {
        val vm = formularioCapsula()
        assertFalse(vm.uiState.value.cargando)
        assertTrue(vm.uiState.value.esAdmin)
        assertTrue(vm.uiState.value.publicado)
        assertEquals("Admin", vm.uiState.value.autor.valor)
    }

    @Test
    fun `capsula - elegir punto propone un codigo QR unico`() {
        val vm = formularioCapsula()
        // Van después de formularioCapsula(), que deja todos los códigos como disponibles.
        coEvery { gestion.codigoDisponible("QR-PLAZA-DE-ARMAS", -1) } returns false
        coEvery { gestion.codigoDisponible("QR-PLAZA-DE-ARMAS-2", -1) } returns true

        vm.elegirPunto(1)

        assertEquals("QR-PLAZA-DE-ARMAS-2", vm.uiState.value.codigoQr.valor)
    }

    @Test
    fun `capsula - validaciones de url duracion y puntos`() {
        val vm = formularioCapsula()
        vm.cambiarUrl("youtube.com/video")
        vm.cambiarDuracion("2:75")
        vm.cambiarPuntosQr("500")
        vm.cambiarDuracion("2:x0") // se descarta

        assertEquals("Debe comenzar con https://", vm.uiState.value.url.error)
        assertNotNull(vm.uiState.value.duracion.error)
        assertNotNull(vm.uiState.value.puntosQr.error)

        vm.enviar()
        assertEquals("Elige el punto turístico", vm.uiState.value.errorPunto)
    }

    @Test
    fun `capsula - enviar valida y guarda en el servidor`() {
        coEvery { gestion.guardarCapsula(any(), any()) } returns Result.success(30L)
        val vm = formularioCapsula()
        vm.elegirPunto(1)
        vm.cambiarTitulo("Visión por computador")
        vm.cambiarTema("Computer Vision")
        vm.cambiarAutor("Triskeledu")
        vm.cambiarDescripcion("Cómo una cámara reconoce objetos")
        vm.cambiarDuracion("2:30")
        vm.cambiarUrl("https://youtu.be/demo")
        vm.cambiarPublicado(false)

        vm.enviar()

        assertEquals(30L, vm.uiState.value.guardadoId)
        coVerify { gestion.guardarCapsula(match { !it.publicado && it.puntos == 10 }, estabaPublicada = false) }
        vm.navegacionRealizada()
        assertNull(vm.uiState.value.guardadoId)
    }

    @Test
    fun `capsula - codigo repetido en el servidor se muestra bajo el campo`() {
        coEvery { gestion.guardarCapsula(any(), any()) } returns
            Result.failure(ErrorServidor("Ese código QR ya está en uso", 409))
        val vm = formularioCapsula()
        vm.elegirPunto(1)
        vm.cambiarTitulo("Visión por computador")
        vm.cambiarTema("Computer Vision")
        vm.cambiarCodigo("qr-plaza-armas")

        vm.enviar()

        assertEquals("Ese código QR ya está en uso", vm.uiState.value.codigoQr.error)
    }

    @Test
    fun `capsula - el creador no puede publicar`() {
        every { usuarios.usuarioActivo } returns flowOf(admin.copy(id = 2, alias = "GenCont", rol = Rol.CREADOR))
        every { gestion.observarPuntos() } returns flowOf(listOf(plaza))
        coEvery { gestion.obtenerCapsula(1) } returns capsula.copy(publicado = false)
        val vm = FormularioCapsulaViewModel(usuarios, gestion, SavedStateHandle(mapOf(Rutas.ARG_CONTENIDO_ID to 1L)))

        vm.cambiarPublicado(true)

        assertTrue(vm.uiState.value.esEdicion)
        assertFalse(vm.uiState.value.publicado)
    }
}
