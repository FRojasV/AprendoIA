package com.triskeledu.aprendoia.ui

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import com.triskeledu.aprendoia.DatosPrueba.capsula
import com.triskeledu.aprendoia.DatosPrueba.plaza
import com.triskeledu.aprendoia.ReglaCorrutinas
import com.triskeledu.aprendoia.data.local.entity.DescubrimientoEntity
import com.triskeledu.aprendoia.data.local.relacion.ContenidoConEstado
import com.triskeledu.aprendoia.data.local.relacion.DescubrimientoDetalle
import com.triskeledu.aprendoia.data.local.relacion.EstadisticasJugador
import com.triskeledu.aprendoia.data.local.relacion.SelfieDetalle
import com.triskeledu.aprendoia.data.repository.Clima
import com.triskeledu.aprendoia.data.repository.ClimaRepository
import com.triskeledu.aprendoia.data.repository.JuegoRepository
import com.triskeledu.aprendoia.data.repository.UsuarioRepository
import com.triskeledu.aprendoia.ui.coleccion.ColeccionViewModel
import com.triskeledu.aprendoia.ui.coleccion.FiltroCapsulas
import com.triskeledu.aprendoia.ui.coleccion.PestanaColeccion
import com.triskeledu.aprendoia.ui.detallepunto.DetallePuntoViewModel
import com.triskeledu.aprendoia.ui.navigation.Rutas
import com.triskeledu.aprendoia.ui.resultadoqr.ResultadoQrViewModel
import com.triskeledu.aprendoia.ui.selfie.SelfieViewModel
import com.triskeledu.aprendoia.ui.video.VideoViewModel
import com.triskeledu.aprendoia.util.Coordenada
import com.triskeledu.aprendoia.util.GaleriaFotos
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/** Pruebas de Resultado QR, Detalle del punto, Video, Selfie y Colección. */
class ExperienciaJugadorViewModelsTest {

    @get:Rule val corrutinas = ReglaCorrutinas()

    private val usuarios = mockk<UsuarioRepository>(relaxed = true)
    private val juego = mockk<JuegoRepository>(relaxed = true)
    private val climaRepo = mockk<ClimaRepository>()
    private val galeria = mockk<GaleriaFotos>(relaxed = true)

    private val uriTemporal = uriFalsa("content://temporal")
    private val uriGaleria = uriFalsa("content://galeria")
    private val conEstado = ContenidoConEstado(capsula, descubierto = true, visto = false)
    private val clima = Clima(20, 19, 40, 10, "Despejado", "☀️", true)

    private fun uriFalsa(texto: String): Uri = mockk<Uri>().also { every { it.toString() } returns texto }

    @Before
    fun preparar() {
        mockkStatic(Uri::class)
        every { Uri.parse("content://temporal") } returns uriTemporal
        every { Uri.parse("content://galeria") } returns uriGaleria
        every { usuarios.usuarioActivoId } returns flowOf(3L)
        coEvery { juego.obtenerContenido(1) } returns capsula
        coEvery { juego.obtenerPunto(1) } returns plaza
        every { juego.observarPunto(1) } returns flowOf(plaza)
        every { juego.observarTieneSelfie(3, 1) } returns flowOf(false)
        every { juego.observarContenidosDePunto(1, 3) } returns flowOf(listOf(conEstado))
        every { galeria.crearUriTemporal() } returns uriTemporal
    }

    @After
    fun limpiar() = unmockkStatic(Uri::class)

    private fun resultado(id: Long = 1) = ResultadoQrViewModel(
        usuarios, juego,
        SavedStateHandle(mapOf<String, Any>(Rutas.ARG_CONTENIDO_ID to id, Rutas.ARG_PUNTOS_GANADOS to 10))
    )

    private fun detalle(id: Long = 1) =
        DetallePuntoViewModel(usuarios, juego, climaRepo, SavedStateHandle(mapOf(Rutas.ARG_PUNTO_ID to id)))

    private fun video() = VideoViewModel(usuarios, juego, SavedStateHandle(mapOf(Rutas.ARG_CONTENIDO_ID to 1L)))

    private fun selfie() = SelfieViewModel(usuarios, juego, galeria, SavedStateHandle(mapOf(Rutas.ARG_PUNTO_ID to 1L)))

    private fun selfieConFoto() = selfie().apply {
        prepararFoto()
        onResultadoCamara(true)
    }

    // ---------- Resultado del QR ----------

    @Test
    fun `resultado carga capsula, punto, puntaje y selfie`() {
        every { juego.observarEstadisticas(3) } returns flowOf(EstadisticasJugador(20, 100, 2, 1, 1, 2))
        every { juego.observarTieneSelfie(3, 1) } returns flowOf(true)

        val estado = resultado().uiState.value

        assertFalse(estado.cargando)
        assertEquals(10, estado.puntosGanados)
        assertEquals(capsula, estado.contenido)
        assertEquals(plaza, estado.punto)
        assertEquals(120, estado.puntajeTotal)
        assertTrue(estado.tieneSelfie)
    }

    @Test
    fun `resultado sin sesion ni capsula queda en cero`() {
        every { usuarios.usuarioActivoId } returns flowOf(null)
        coEvery { juego.obtenerContenido(9) } returns null

        val estado = resultado(9).uiState.value

        assertFalse(estado.cargando)
        assertNull(estado.punto)
        assertEquals(0, estado.puntajeTotal)
        assertFalse(estado.tieneSelfie)
    }

    // ---------- Detalle del punto ----------

    @Test
    fun `detalle combina punto, capsulas, selfie y clima`() {
        coEvery { climaRepo.climaActual(plaza.latitud, plaza.longitud) } returns Result.success(clima)

        val vm = detalle()

        assertFalse(vm.uiState.value.cargando)
        assertEquals(plaza, vm.uiState.value.punto)
        assertEquals(listOf(conEstado), vm.uiState.value.contenidos)
        assertEquals(clima, vm.uiState.value.clima.clima)

        val ubicacion = mockk<Coordenada>()
        vm.onUbicacion(ubicacion)
        assertSame(ubicacion, vm.uiState.value.ubicacion)
    }

    @Test
    fun `detalle sigue funcionando si falla el clima`() {
        coEvery { climaRepo.climaActual(any(), any()) } returns Result.failure(RuntimeException("sin red"))

        val estado = detalle().uiState.value

        assertEquals("No se pudo obtener el clima", estado.clima.error)
        assertEquals(plaza, estado.punto)
    }

    @Test
    fun `detalle indica cuando el punto no existe`() {
        coEvery { juego.obtenerPunto(9) } returns null
        every { juego.observarPunto(9) } returns flowOf(null)
        every { juego.observarContenidosDePunto(9, 3) } returns flowOf(emptyList())
        every { juego.observarTieneSelfie(3, 9) } returns flowOf(false)

        val estado = detalle(9).uiState.value

        assertTrue(estado.noExiste)
        assertEquals("Punto no encontrado", estado.clima.error)
    }

    @Test
    fun `detalle sin sesion no muestra capsulas`() {
        every { usuarios.usuarioActivoId } returns flowOf(null)
        coEvery { climaRepo.climaActual(any(), any()) } returns Result.success(clima)

        val estado = detalle().uiState.value

        assertTrue(estado.contenidos.isEmpty())
        assertFalse(estado.tieneSelfie)
    }

    // ---------- Video ----------

    @Test
    fun `video desbloqueado se marca como visto`() {
        every { juego.observarDescubrimiento(3, 1) } returns
            flowOf(DescubrimientoEntity(usuarioId = 3, contenidoId = 1, puntos = 10))
        coEvery { juego.marcarVideoVisto(3, capsula) } returns true
        val vm = video()
        assertTrue(vm.uiState.value.desbloqueado)
        assertFalse(vm.uiState.value.visto)

        vm.onMarcarVisto()

        assertEquals("¡Cápsula completada! Se agregó a tu historial", vm.uiState.value.mensaje)
        vm.onMensajeMostrado()
        assertNull(vm.uiState.value.mensaje)
    }

    @Test
    fun `video ya visto no se vuelve a marcar`() {
        every { juego.observarDescubrimiento(3, 1) } returns
            flowOf(DescubrimientoEntity(usuarioId = 3, contenidoId = 1, puntos = 10, visto = true))
        val vm = video()

        vm.onMarcarVisto()

        assertTrue(vm.uiState.value.visto)
        coVerify(exactly = 0) { juego.marcarVideoVisto(any(), any()) }
    }

    @Test
    fun `video bloqueado no se puede marcar`() {
        every { juego.observarDescubrimiento(3, 1) } returns flowOf(null)
        val vm = video()

        vm.onMarcarVisto()

        assertFalse(vm.uiState.value.desbloqueado)
        coVerify(exactly = 0) { juego.marcarVideoVisto(any(), any()) }
    }

    @Test
    fun `video sin sesion queda bloqueado`() {
        every { usuarios.usuarioActivoId } returns flowOf(null)

        val vm = video()

        assertFalse(vm.uiState.value.cargando)
        assertFalse(vm.uiState.value.desbloqueado)
    }

    // ---------- Selfie ----------

    @Test
    fun `selfie carga el punto y detecta el QR escaneado`() {
        val estado = selfie().uiState.value

        assertFalse(estado.cargando)
        assertEquals(plaza, estado.punto)
        assertTrue(estado.qrEscaneado)
        assertFalse(estado.tieneSelfie)
    }

    @Test
    fun `selfie sin sesion no tiene QR escaneado`() {
        every { usuarios.usuarioActivoId } returns flowOf(null)

        assertFalse(selfie().uiState.value.qrEscaneado)
    }

    @Test
    fun `selfie maneja foto tomada, cancelada y descartada`() {
        val vm = selfie()

        assertSame(uriTemporal, vm.prepararFoto())
        assertEquals("content://temporal", vm.uiState.value.uriPendiente)

        vm.onResultadoCamara(false)
        assertNull(vm.uiState.value.uriPendiente)
        assertNull(vm.uiState.value.fotoTomada)

        vm.prepararFoto()
        vm.onResultadoCamara(true)
        assertEquals("content://temporal", vm.uiState.value.fotoTomada)

        vm.onDescartar()
        assertNull(vm.uiState.value.fotoTomada)
    }

    @Test
    fun `selfie avisa si se niega el permiso de camara`() {
        val vm = selfie()

        vm.onPermisoDenegado()

        assertNotNull(vm.uiState.value.error)
    }

    @Test
    fun `selfie sin foto no se guarda`() {
        val vm = selfie()

        vm.onGuardar()

        coVerify(exactly = 0) { juego.registrarSelfie(any(), any(), any()) }
    }

    @Test
    fun `selfie se guarda en galeria y suma 100 puntos`() {
        coEvery { galeria.guardarEnGaleria(uriTemporal, "Plaza de Armas") } returns uriGaleria
        coEvery { juego.registrarSelfie(3, plaza, "content://galeria") } returns 100
        val vm = selfieConFoto()

        vm.onGuardar()

        val estado = vm.uiState.value
        assertFalse(estado.guardando)
        assertEquals(100, estado.puntosGanados)
        assertEquals("content://galeria", estado.fotoTomada)
        assertTrue(estado.tieneSelfie)
        verify { galeria.limpiarTemporales() }
    }

    @Test
    fun `selfie avisa si no se pudo guardar en galeria`() {
        coEvery { galeria.guardarEnGaleria(any(), any()) } returns null
        val vm = selfieConFoto()

        vm.onGuardar()

        assertEquals("No se pudo guardar la foto en la galería", vm.uiState.value.error)
        coVerify(exactly = 0) { juego.registrarSelfie(any(), any(), any()) }
    }

    @Test
    fun `selfie exige un jugador activo al guardar`() {
        val vm = selfieConFoto()
        every { usuarios.usuarioActivoId } returns flowOf(null)

        vm.onGuardar()

        assertEquals("No hay jugador activo", vm.uiState.value.error)
        assertFalse(vm.uiState.value.guardando)
    }

    @Test
    fun `selfie carga la miniatura desde la galeria`() = runTest {
        val miniatura = mockk<Bitmap>()
        coEvery { galeria.cargarMiniatura(uriGaleria, any()) } returns miniatura

        assertSame(miniatura, selfie().cargarMiniatura("content://galeria"))
    }

    // ---------- Colección ----------

    private val detalleVisto = DescubrimientoDetalle(
        descubrimientoId = 1, contenidoId = 1, titulo = "Introducción a la IA", tema = "Fundamentos", url = "",
        puntoId = 1, puntoNombre = "Plaza de Armas", esHitoTuristico = true, imagenUrl = null,
        puntos = 10, fecha = 1L, visto = true
    )
    private val detallePendiente = detalleVisto.copy(descubrimientoId = 2, contenidoId = 2, visto = false)
    private val selfieDetalle = SelfieDetalle(1, 1, "Plaza de Armas", "content://galeria", 100, 1L)

    @Test
    fun `coleccion carga capsulas y selfies, y filtra`() {
        every { juego.observarColeccion(3) } returns flowOf(listOf(detalleVisto, detallePendiente))
        every { juego.observarSelfies(3) } returns flowOf(listOf(selfieDetalle))
        every { juego.observarTotalCapsulas() } returns flowOf(8)

        val vm = ColeccionViewModel(usuarios, juego, galeria)
        assertFalse(vm.uiState.value.cargando)
        assertEquals(8, vm.uiState.value.totalCapsulas)
        assertEquals(listOf(selfieDetalle), vm.uiState.value.selfies)

        vm.onFiltro(FiltroCapsulas.POR_VER)
        assertEquals(listOf(detallePendiente), vm.uiState.value.capsulasVisibles)
        vm.onFiltro(FiltroCapsulas.VISTAS)
        assertEquals(listOf(detalleVisto), vm.uiState.value.capsulasVisibles)
        vm.onPestana(PestanaColeccion.SELFIES)
        assertEquals(PestanaColeccion.SELFIES, vm.uiState.value.pestana)
    }

    @Test
    fun `coleccion sin sesion queda vacia`() {
        every { usuarios.usuarioActivoId } returns flowOf(null)

        val estado = ColeccionViewModel(usuarios, juego, galeria).uiState.value

        assertFalse(estado.cargando)
        assertTrue(estado.capsulas.isEmpty())
        assertTrue(estado.selfies.isEmpty())
    }

    @Test
    fun `coleccion pide la miniatura con lado 400`() = runTest {
        every { usuarios.usuarioActivoId } returns flowOf(null)
        val miniatura = mockk<Bitmap>()
        coEvery { galeria.cargarMiniatura(uriGaleria, 400) } returns miniatura

        assertSame(miniatura, ColeccionViewModel(usuarios, juego, galeria).cargarMiniatura("content://galeria"))
    }
}
