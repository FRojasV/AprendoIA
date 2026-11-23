package com.triskeledu.aprendoia.data.remote.api

import com.triskeledu.aprendoia.data.remote.dto.LoginRequestDto
import com.triskeledu.aprendoia.data.remote.dto.UsuarioDto
import com.triskeledu.aprendoia.data.remote.dto.UsuarioRequestDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/** Endpoints del microservicio ms-usuarios (puerto 8081). Retrofit genera la implementación. */
interface UsuariosApi {

    @GET("api/usuarios")
    suspend fun listar(): List<UsuarioDto>

    @GET("api/usuarios/{id}")
    suspend fun obtener(@Path("id") id: Long): UsuarioDto

    @POST("api/usuarios")
    suspend fun registrar(@Body datos: UsuarioRequestDto): UsuarioDto

    @PUT("api/usuarios/{id}")
    suspend fun actualizar(@Path("id") id: Long, @Body datos: UsuarioRequestDto): UsuarioDto

    @DELETE("api/usuarios/{id}")
    suspend fun eliminar(@Path("id") id: Long)

    @POST("api/usuarios/login")
    suspend fun login(@Body datos: LoginRequestDto): UsuarioDto
}
