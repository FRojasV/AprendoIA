package com.triskeledu.aprendoia.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Manejo de fotos para la selfie turística (pantalla 10).
 *
 * Flujo (igual que el ejemplo de Cámara del curso):
 *   1. crearUriTemporal()  → archivo vacío en la caché, compartido con FileProvider.
 *   2. La app de cámara del sistema guarda ahí la foto (contrato TakePicture).
 *   3. guardarEnGaleria()  → copia la foto a Imágenes/AprendoIA para que el
 *      jugador la vea en su galería (requisito del caso).
 */
class GaleriaFotos(private val context: Context) {

    /** Crea el archivo temporal y devuelve su Uri "content://" para la cámara. */
    fun crearUriTemporal(): Uri {
        val carpeta = File(context.cacheDir, "selfies").apply { mkdirs() }
        val archivo = File.createTempFile("selfie_", ".jpg", carpeta)
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)
    }

    /**
     * Copia la foto temporal a la galería pública del teléfono.
     * Devuelve la Uri definitiva (la que se guarda en SQLite) o null si falló.
     */
    suspend fun guardarEnGaleria(uriTemporal: Uri, nombrePunto: String): Uri? = withContext(Dispatchers.IO) {
        val marca = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val nombreArchivo = "AprendoIA_${nombrePunto.filter { it.isLetterOrDigit() }}_$marca.jpg"

        val valores = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, nombreArchivo)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/AprendoIA")
                put(MediaStore.Images.Media.IS_PENDING, 1) // Oculta mientras se copia
            }
        }

        val resolver = context.contentResolver
        val destino = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, valores)
            ?: return@withContext null

        try {
            resolver.openInputStream(uriTemporal).use { entrada ->
                resolver.openOutputStream(destino).use { salida ->
                    if (entrada == null || salida == null) error("No se pudo abrir la foto")
                    entrada.copyTo(salida)
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                valores.clear()
                valores.put(MediaStore.Images.Media.IS_PENDING, 0) // Ya visible en la galería
                resolver.update(destino, valores, null, null)
            }
            destino
        } catch (e: Exception) {
            resolver.delete(destino, null, null)
            null
        }
    }

    /** Borra los archivos temporales de la caché. */
    fun limpiarTemporales() {
        File(context.cacheDir, "selfies").listFiles()?.forEach { it.delete() }
    }

    /**
     * Carga una versión reducida de la foto para mostrarla en pantalla
     * (una foto de cámara completa puede pesar varios MB).
     */
    suspend fun cargarMiniatura(uri: Uri, ladoMaximo: Int = 1080): Bitmap? = withContext(Dispatchers.IO) {
        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                // ImageDecoder respeta la orientación de la foto (EXIF).
                val fuente = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(fuente) { decodificador, info, _ ->
                    val mayor = maxOf(info.size.width, info.size.height)
                    if (mayor > ladoMaximo) {
                        val escala = ladoMaximo.toFloat() / mayor
                        decodificador.setTargetSize(
                            (info.size.width * escala).toInt(),
                            (info.size.height * escala).toInt()
                        )
                    }
                }
            } else {
                val opciones = BitmapFactory.Options().apply { inSampleSize = 4 }
                context.contentResolver.openInputStream(uri).use { BitmapFactory.decodeStream(it, null, opciones) }
            }
        }.getOrNull()
    }
}
