package com.triskeledu.aprendoia.data.remote

import com.triskeledu.aprendoia.data.local.entity.ContenidoEntity
import com.triskeledu.aprendoia.data.local.entity.HistorialEntity
import com.triskeledu.aprendoia.data.local.entity.PuntoEntity
import com.triskeledu.aprendoia.data.local.entity.Rol
import com.triskeledu.aprendoia.data.local.entity.TipoActividad
import com.triskeledu.aprendoia.data.local.entity.UsuarioEntity
import com.triskeledu.aprendoia.data.remote.dto.ActividadDto
import com.triskeledu.aprendoia.data.remote.dto.CapsulaDto
import com.triskeledu.aprendoia.data.remote.dto.CapsulaRequestDto
import com.triskeledu.aprendoia.data.remote.dto.PuntoDto
import com.triskeledu.aprendoia.data.remote.dto.PuntoRequestDto
import com.triskeledu.aprendoia.data.remote.dto.UsuarioDto
import com.triskeledu.aprendoia.data.remote.dto.UsuarioRequestDto
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Conversión entre lo que llega del servidor (DTO) y lo que se guarda en Room (Entity).
 * Mantenerlo en un solo archivo evita repetir la misma lógica en cada repositorio.
 */

// ---------------------------------------------------------------- Usuarios

/** El servidor nunca envía la contraseña: se conserva el hash local si existe. */
fun UsuarioDto.aEntidad(passwordLocal: String = ""): UsuarioEntity = UsuarioEntity(
    id = id,
    nombre = nombre,
    alias = alias,
    correo = correo,
    telefono = telefono.orEmpty(),
    password = passwordLocal,
    direccion = direccion.orEmpty(),
    avatar = avatar ?: "avatar_1",
    rol = runCatching { Rol.valueOf(rol) }.getOrDefault(Rol.CLIENTE),
    notificacionesActivas = notificacionesActivas
)

fun UsuarioEntity.aRequest(password: String = ""): UsuarioRequestDto = UsuarioRequestDto(
    nombre = nombre,
    correo = correo,
    telefono = telefono,
    password = password,
    direccion = direccion,
    avatar = avatar,
    notificacionesActivas = notificacionesActivas
)

// ---------------------------------------------------------------- Puntos

fun PuntoDto.aEntidad(): PuntoEntity = PuntoEntity(
    id = id,
    nombre = nombre,
    descripcion = descripcion,
    infoTuristica = infoTuristica.orEmpty(),
    latitud = latitud,
    longitud = longitud,
    esHitoTuristico = esHitoTuristico,
    imagenUrl = imagenUrl
)

fun PuntoEntity.aRequest(): PuntoRequestDto = PuntoRequestDto(
    nombre = nombre,
    descripcion = descripcion,
    infoTuristica = infoTuristica,
    latitud = latitud,
    longitud = longitud,
    esHitoTuristico = esHitoTuristico,
    imagenUrl = imagenUrl
)

// ---------------------------------------------------------------- Cápsulas

fun CapsulaDto.aEntidad(): ContenidoEntity = ContenidoEntity(
    id = id,
    puntoId = puntoId,
    titulo = titulo,
    tema = tema,
    autor = autor,
    url = url.orEmpty(),
    duracion = duracion.orEmpty(),
    descripcion = descripcion.orEmpty(),
    codigoQr = codigoQr,
    puntos = puntos,
    publicado = publicado
)

fun ContenidoEntity.aRequest(): CapsulaRequestDto = CapsulaRequestDto(
    puntoId = puntoId,
    titulo = titulo,
    tema = tema,
    autor = autor,
    url = url,
    duracion = duracion,
    descripcion = descripcion,
    codigoQr = codigoQr,
    puntos = puntos,
    publicado = publicado
)

// ---------------------------------------------------------------- Historial

fun ActividadDto.aEntidad(): HistorialEntity = HistorialEntity(
    usuarioId = usuarioId,
    tipo = runCatching { TipoActividad.valueOf(tipo) }.getOrDefault(TipoActividad.QR),
    titulo = titulo,
    detalle = detalle.orEmpty(),
    puntos = puntos,
    fecha = fechaIsoAMillis(fecha)
)

/** "2026-11-20T10:15:00" (hora de Santiago) → milisegundos. Si no se entiende, la hora actual. */
fun fechaIsoAMillis(texto: String?, zona: ZoneId = ZoneId.of("America/Santiago")): Long =
    runCatching { LocalDateTime.parse(texto).atZone(zona).toInstant().toEpochMilli() }
        .getOrDefault(System.currentTimeMillis())
