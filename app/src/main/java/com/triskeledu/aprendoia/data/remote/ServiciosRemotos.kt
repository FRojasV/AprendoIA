package com.triskeledu.aprendoia.data.remote

import com.triskeledu.aprendoia.BuildConfig
import com.triskeledu.aprendoia.data.remote.api.ClimaApi
import com.triskeledu.aprendoia.data.remote.api.ContenidosApi
import com.triskeledu.aprendoia.data.remote.api.JuegoApi
import com.triskeledu.aprendoia.data.remote.api.UsuariosApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Crea UNA instancia de Retrofit por cada servidor:
 *  - 3 microservicios propios (Spring Boot)
 *  - 1 API externa (Open-Meteo)
 *
 * Las URL vienen de BuildConfig (se configuran en app/build.gradle.kts).
 * En el emulador, 10.0.2.2 es el "localhost" del PC donde corre Spring Boot.
 */
class ServiciosRemotos {

    /** Cliente HTTP compartido: tiempos de espera cortos y log de las peticiones en Logcat. */
    private val clienteHttp: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                else HttpLoggingInterceptor.Level.NONE
            }
        )
        .build()

    private fun retrofit(urlBase: String): Retrofit = Retrofit.Builder()
        .baseUrl(urlBase)
        .client(clienteHttp)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val usuarios: UsuariosApi = retrofit(BuildConfig.URL_MS_USUARIOS).create(UsuariosApi::class.java)
    val contenidos: ContenidosApi = retrofit(BuildConfig.URL_MS_CONTENIDOS).create(ContenidosApi::class.java)
    val juego: JuegoApi = retrofit(BuildConfig.URL_MS_JUEGO).create(JuegoApi::class.java)
    val clima: ClimaApi = retrofit(BuildConfig.URL_API_CLIMA).create(ClimaApi::class.java)
}
