package com.triskeledu.aprendoia.data.remote.api

import com.triskeledu.aprendoia.data.remote.dto.CapsulaDto
import com.triskeledu.aprendoia.data.remote.dto.CapsulaRequestDto
import com.triskeledu.aprendoia.data.remote.dto.PuntoDto
import com.triskeledu.aprendoia.data.remote.dto.PuntoRequestDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/** Endpoints del microservicio ms-contenidos (puerto 8082). */
interface ContenidosApi {

    // ---------- Puntos ----------
    @GET("api/puntos")
    suspend fun listarPuntos(): List<PuntoDto>

    @POST("api/puntos")
    suspend fun crearPunto(@Body datos: PuntoRequestDto): PuntoDto

    @PUT("api/puntos/{id}")
    suspend fun actualizarPunto(@Path("id") id: Long, @Body datos: PuntoRequestDto): PuntoDto

    @DELETE("api/puntos/{id}")
    suspend fun eliminarPunto(@Path("id") id: Long)

    // ---------- Cápsulas ----------
    /** Todas (publicadas y pendientes): la app filtra según el rol. */
    @GET("api/capsulas")
    suspend fun listarCapsulas(): List<CapsulaDto>

    @POST("api/capsulas")
    suspend fun crearCapsula(@Body datos: CapsulaRequestDto): CapsulaDto

    @PUT("api/capsulas/{id}")
    suspend fun actualizarCapsula(@Path("id") id: Long, @Body datos: CapsulaRequestDto): CapsulaDto

    @PATCH("api/capsulas/{id}/publicar")
    suspend fun publicarCapsula(@Path("id") id: Long): CapsulaDto

    @DELETE("api/capsulas/{id}")
    suspend fun eliminarCapsula(@Path("id") id: Long)
}
