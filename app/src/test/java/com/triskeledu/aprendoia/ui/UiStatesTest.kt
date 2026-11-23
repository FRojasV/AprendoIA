package com.triskeledu.aprendoia.ui

import com.triskeledu.aprendoia.data.local.entity.HistorialEntity
import com.triskeledu.aprendoia.data.local.entity.TipoActividad
import com.triskeledu.aprendoia.data.local.relacion.EstadisticasJugador
import com.triskeledu.aprendoia.data.repository.ClimaRepository
import com.triskeledu.aprendoia.ui.historial.FiltroHistorial
import com.triskeledu.aprendoia.ui.historial.HistorialUiState
import com.triskeledu.aprendoia.ui.progreso.Nivel
import com.triskeledu.aprendoia.ui.progreso.ProgresoUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Pruebas de la lógica que vive en los UiState (niveles, logros, gráfico semanal, historial). */
class UiStatesTest {

    private val ahora = System.currentTimeMillis()
    private val unDia = 24L * 60 * 60 * 1000

    private fun actividad(tipo: TipoActividad, puntos: Int, fecha: Long = ahora) =
        HistorialEntity(usuarioId = 3, tipo = tipo, titulo = tipo.name, puntos = puntos, fecha = fecha)

    // ---------------------------------------------------------------- Progreso

    @Test
    fun `niveles segun el puntaje`() {
        val novato = ProgresoUiState(estadisticas = EstadisticasJugador(50, 0, 5, 0, 0, 5))
        assertEquals(Nivel.NOVATO, novato.nivel)
        assertEquals(Nivel.EXPLORADOR, novato.siguienteNivel)
        assertEquals(0.5f, novato.avanceNivel)

        val maestro = ProgresoUiState(estadisticas = EstadisticasJugador(5_000, 1_000, 0, 0, 0, 0))
        assertEquals(Nivel.MAESTRO, maestro.nivel)
        assertNull(maestro.siguienteNivel)
        assertEquals(1f, maestro.avanceNivel)
        assertTrue(maestro.avanceTotal in 0f..1f)
    }

    @Test
    fun `logros se desbloquean con las estadisticas`() {
        val estado = ProgresoUiState(
            estadisticas = EstadisticasJugador(80, 300, 8, 3, 3, 8),
            totalLugares = 8
        )
        assertTrue(estado.logros.all { it.desbloqueado })
        assertFalse(ProgresoUiState().logros.any { it.desbloqueado })
    }

    @Test
    fun `grafico semanal suma los puntos de cada dia`() {
        val estado = ProgresoUiState(
            historial = listOf(
                actividad(TipoActividad.QR, 10),
                actividad(TipoActividad.SELFIE, 100),
                actividad(TipoActividad.QR, 10, ahora - 30 * unDia) // fuera de la semana
            )
        )
        val dias = estado.puntosUltimos7Dias
        assertEquals(7, dias.size)
        assertEquals("Hoy", dias.last().etiqueta)
        assertEquals(110, dias.last().puntos)
        assertEquals(110, dias.sumOf { it.puntos })
    }

    // ---------------------------------------------------------------- Historial

    @Test
    fun `historial agrupa por dia y filtra por tipo`() {
        val estado = HistorialUiState(
            actividades = listOf(
                actividad(TipoActividad.QR, 10),
                actividad(TipoActividad.VIDEO, 0),
                actividad(TipoActividad.SELFIE, 100, ahora - 10 * unDia)
            )
        )
        assertEquals(listOf("Hoy"), estado.grupos.take(1).map { it.titulo })
        assertEquals(2, estado.grupos.size)
        assertEquals(110, estado.totalPuntos)

        val soloSelfies = estado.copy(filtro = FiltroHistorial.SELFIES)
        assertEquals(1, soloSelfies.grupos.size)
        assertEquals(100, soloSelfies.totalPuntos)
    }

    // ---------------------------------------------------------------- Clima (códigos WMO)

    @Test
    fun `codigos WMO de Open-Meteo y consejos`() {
        assertEquals("Despejado" to "🌙", ClimaRepository.describir(0, esDeDia = false))
        assertEquals("Lluvia", ClimaRepository.describir(63).first)
        assertEquals("Tormenta", ClimaRepository.describir(95).first)
        assertEquals("Sin información", ClimaRepository.describir(999).first)
    }
}
