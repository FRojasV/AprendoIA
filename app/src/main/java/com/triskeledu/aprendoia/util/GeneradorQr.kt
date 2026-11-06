package com.triskeledu.aprendoia.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Crea las imágenes de los códigos QR que el Administrador imprime y pega
 * en cada punto turístico. Usa ZXing (librería libre de Google para generar QR).
 * El escáner de la app (ejemplo QR del curso) los lee y entiende su JSON.
 */
object GeneradorQr {

    /** Solo la matriz del QR (cuadrado blanco y negro). */
    fun crearQr(texto: String, tamano: Int = 900): Bitmap {
        val pistas = mapOf(
            EncodeHintType.CHARACTER_SET to "UTF-8",
            EncodeHintType.MARGIN to 2,
            // Nivel M: el QR sigue funcionando aunque se dañe un poco al estar en la calle.
            EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.M
        )
        val matriz = QRCodeWriter().encode(texto, BarcodeFormat.QR_CODE, tamano, tamano, pistas)
        val pixeles = IntArray(tamano * tamano) { i ->
            if (matriz[i % tamano, i / tamano]) Color.BLACK else Color.WHITE
        }
        return Bitmap.createBitmap(pixeles, tamano, tamano, Bitmap.Config.ARGB_8888)
    }

    /**
     * Versión lista para imprimir: el QR con el nombre del lugar arriba
     * y el código y el título de la cápsula abajo.
     */
    fun crearCartel(texto: String, lugar: String, codigo: String, titulo: String): Bitmap {
        val qr = crearQr(texto)
        val ancho = qr.width + 100
        val alto = qr.height + 300
        val cartel = Bitmap.createBitmap(ancho, alto, Bitmap.Config.ARGB_8888)
        val lienzo = Canvas(cartel)
        lienzo.drawColor(Color.WHITE)

        val pintura = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT_BOLD
        }
        // Lugar (arriba)
        pintura.color = Color.rgb(11, 42, 91)
        pintura.textSize = 52f
        lienzo.drawText(lugar.take(30), ancho / 2f, 90f, pintura)
        // QR (centro)
        lienzo.drawBitmap(qr, 50f, 130f, null)
        // Código (abajo)
        pintura.color = Color.rgb(30, 111, 232)
        pintura.textSize = 48f
        lienzo.drawText(codigo, ancho / 2f, qr.height + 190f, pintura)
        // Título de la cápsula
        pintura.color = Color.DKGRAY
        pintura.typeface = Typeface.DEFAULT
        pintura.textSize = 34f
        lienzo.drawText(titulo.take(45), ancho / 2f, qr.height + 245f, pintura)
        return cartel
    }

    /** Abre el menú "Compartir" de Android (WhatsApp, correo, Drive, imprimir…). */
    suspend fun compartir(context: Context, imagen: Bitmap, codigo: String) {
        val uri = withContext(Dispatchers.IO) {
            val carpeta = File(context.cacheDir, "qr").apply { mkdirs() }
            val archivo = File(carpeta, "$codigo.png")
            archivo.outputStream().use { imagen.compress(Bitmap.CompressFormat.PNG, 100, it) }
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)
        }
        val intento = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Código QR $codigo – Aprendo IA")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intento, "Compartir código QR"))
    }

    /** Guarda el cartel en Galería › Imágenes › AprendoIA_QR. Devuelve true si funcionó. */
    suspend fun guardarEnGaleria(context: Context, imagen: Bitmap, codigo: String): Boolean =
        withContext(Dispatchers.IO) {
            val valores = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "$codigo.png")
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/AprendoIA_QR")
                }
            }
            runCatching {
                val resolver = context.contentResolver
                val destino: Uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, valores)
                    ?: error("No se pudo crear el archivo")
                resolver.openOutputStream(destino).use { salida ->
                    requireNotNull(salida)
                    imagen.compress(Bitmap.CompressFormat.PNG, 100, salida)
                }
            }.isSuccess
        }
}
