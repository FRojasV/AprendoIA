package com.triskeledu.aprendoia.util

import android.content.Context
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import org.json.JSONException
import org.json.JSONObject

/**
 * Lectura de códigos QR, igual que en el ejemplo "QR" del curso:
 *  - El escáner de Google abre su propia cámara (no se pide permiso de cámara).
 *  - El QR trae un JSON; lo convertimos a un objeto Kotlin con org.json.
 *
 * Formato del QR de Triskeledu:
 *   {"app":"triskeledu","codigo":"QR-PLAZA-ARMAS","titulo":"...","descripcion":"...","url":"..."}
 * Lo único obligatorio es "codigo": con él se busca la cápsula en SQLite.
 */
object LectorQr {

    /** Datos que vienen dentro del QR. */
    data class DatosQr(
        val codigo: String,
        val titulo: String,
        val descripcion: String,
        val url: String
    )

    /**
     * Abre el escáner de Google y entrega el resultado mediante dos funciones.
     * @param alLeer   recibe el TEXTO del QR.
     * @param alFallar recibe un mensaje si se canceló o hubo un error.
     */
    fun escanear(
        context: Context,
        alLeer: (String) -> Unit,
        alFallar: (String) -> Unit
    ) {
        val opciones = GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .enableAutoZoom() // Acerca la cámara si el QR está lejos
            .build()

        GmsBarcodeScanning.getClient(context, opciones)
            .startScan()
            .addOnSuccessListener { codigo -> alLeer(codigo.rawValue ?: "") }
            .addOnCanceledListener { alFallar("Escaneo cancelado.") }
            .addOnFailureListener { error -> alFallar("No se pudo escanear: ${error.message}") }
    }


    fun interpretar(texto: String): DatosQr? {
        val limpio = texto.trim()
        if (limpio.isEmpty()) return null

        if (limpio.startsWith("{")) {
            return try {
                val json = JSONObject(limpio)
                val codigo = json.optString("codigo", "").trim()
                if (codigo.isEmpty()) null
                else DatosQr(
                    codigo = normalizar(codigo),
                    titulo = json.optString("titulo", ""),
                    descripcion = json.optString("descripcion", ""),
                    url = json.optString("url", "")
                )
            } catch (e: JSONException) {
                null
            }
        }
        return DatosQr(codigo = normalizar(limpio), titulo = "", descripcion = "", url = "")
    }

    /** "qr-plaza-armas " → "QR-PLAZA-ARMAS" (así se guarda en la base). */
    fun normalizar(codigo: String): String = codigo.trim().uppercase()

    /** Arma el JSON que el Administrador imprimirá en el QR (se usa en la etapa de gestión). */
    fun crearJson(codigo: String, titulo: String, descripcion: String, url: String): String =
        JSONObject()
            .put("app", "triskeledu")
            .put("codigo", codigo)
            .put("titulo", titulo)
            .put("descripcion", descripcion)
            .put("url", url)
            .toString()
}
