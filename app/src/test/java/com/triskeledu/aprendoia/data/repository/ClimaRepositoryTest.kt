package com.triskeledu.aprendoia.data.repository

import com.triskeledu.aprendoia.DatosPrueba
import com.triskeledu.aprendoia.data.remote.api.ClimaApi
import com.triskeledu.aprendoia.data.remote.dto.ClimaActualDto
import com.triskeledu.aprendoia.data.remote.dto.ClimaRespuestaDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Pruebas de la API externa del clima (Open-Meteo). */
class ClimaRepositoryTest {

    private val api = mockk<ClimaApi>()
    private val repositorio = ClimaRepository(api)

    private fun respuesta(temperatura: Double, codigo: Int, dia: Int = 1) = ClimaRespuestaDto(
        ClimaActualDto("2026-11-23T12:00", temperatura, temperatura - 1, 40, 12.4, codigo, dia)
    )

    @Test
    fun `convierte la respuesta de Open-Meteo en un clima legible`() = runTest {
        coEvery { api.climaActual(-33.4378, -70.6505, any(), any()) } returns respuesta(23.6, 0)

        val clima = repositorio.climaActual(-33.4378, -70.6505).getOrThrow()

        assertEquals(24, clima.temperatura)
        assertEquals("Despejado", clima.descripcion)
        assertEquals("☀️", clima.emoji)
        assertEquals(12, clima.vientoKmH)
        assertEquals("Buen momento para buscar códigos QR.", clima.consejo)
    }

    @Test
    fun `consejos segun el clima`() = runTest {
        coEvery { api.climaActual(any(), any(), any(), any()) } returnsMany listOf(
            respuesta(15.0, 63), respuesta(31.0, 1), respuesta(5.0, 3)
        )
        assertEquals("Lleva paraguas para tu recorrido.", repositorio.climaActual(0.0, 0.0).getOrThrow().consejo)
        assertEquals("Hace calor: lleva agua y bloqueador.", repositorio.climaActual(0.0, 0.0).getOrThrow().consejo)
        assertEquals("Hace frío: abrígate antes de salir.", repositorio.climaActual(0.0, 0.0).getOrThrow().consejo)
    }

    @Test
    fun `respuesta sin datos o sin conexion devuelve error`() = runTest {
        coEvery { api.climaActual(1.0, 1.0, any(), any()) } returns ClimaRespuestaDto(null)
        coEvery { api.climaActual(2.0, 2.0, any(), any()) } throws DatosPrueba.sinConexion()

        assertTrue(repositorio.climaActual(1.0, 1.0).isFailure)
        assertTrue(repositorio.climaActual(2.0, 2.0).isFailure)
    }

    @Test
    fun `codigos WMO`() {
        assertEquals("🌙", ClimaRepository.describir(0, esDeDia = false).second)
        assertEquals("Neblina", ClimaRepository.describir(45).first)
        assertEquals("Llovizna", ClimaRepository.describir(53).first)
        assertEquals("Nieve", ClimaRepository.describir(71).first)
        assertEquals("Tormenta", ClimaRepository.describir(95).first)
        assertEquals("Sin información", ClimaRepository.describir(1234).first)
    }
}
