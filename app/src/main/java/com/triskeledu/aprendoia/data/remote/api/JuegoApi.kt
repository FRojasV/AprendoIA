package com.triskeledu.aprendoia.data.remote.api

import com.triskeledu.aprendoia.data.remote.dto.ActividadDto
import com.triskeledu.aprendoia.data.remote.dto.DescubrimientoDto
import com.triskeledu.aprendoia.data.remote.dto.EscaneoDto
import com.triskeledu.aprendoia.data.remote.dto.EscaneoRequestDto
import com.triskeledu.aprendoia.data.remote.dto.RankingDto
import com.triskeledu.aprendoia.data.remote.dto.SelfieDto
import com.triskeledu.aprendoia.data.remote.dto.SelfieRequestDto
import com.triskeledu.aprendoia.data.remote.dto.VistoRequestDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/** Endpoints del microservicio ms-juego (puerto 8083). */
interface JuegoApi {

    @POST("api/descubrimientos")
    suspend fun escanear(@Body datos: EscaneoRequestDto): EscaneoDto

    @GET("api/descubrimientos")
    suspend fun descubrimientos(@Query("usuarioId") usuarioId: Long): List<DescubrimientoDto>

    @PUT("api/descubrimientos/visto")
    suspend fun marcarVisto(@Body datos: VistoRequestDto): Map<String, Boolean>

    @POST("api/selfies")
    suspend fun registrarSelfie(@Body datos: SelfieRequestDto): SelfieDto

    @GET("api/selfies")
    suspend fun selfies(@Query("usuarioId") usuarioId: Long): List<SelfieDto>

    @GET("api/historial")
    suspend fun historial(@Query("usuarioId") usuarioId: Long): List<ActividadDto>

    @GET("api/ranking")
    suspend fun ranking(@Query("limite") limite: Int = 10): List<RankingDto>

    @DELETE("api/progreso/{usuarioId}")
    suspend fun eliminarProgreso(@Path("usuarioId") usuarioId: Long)
}
