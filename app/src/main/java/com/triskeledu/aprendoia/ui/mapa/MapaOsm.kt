package com.triskeledu.aprendoia.ui.mapa

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.triskeledu.aprendoia.util.CENTRO_SANTIAGO
import com.triskeledu.aprendoia.util.Coordenada
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay



/**
 * Crea el MapView una sola vez y lo conecta al ciclo de vida de la pantalla:
 * onResume/onPause descargan o pausan los mosaicos del mapa.
 */
@Composable
fun rememberMapView(): MapView {
    val context = LocalContext.current
    val mapa = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)                       // Zoom con dos dedos
            zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
            isTilesScaledToDpi = true                          // Texto del mapa legible
            minZoomLevel = 5.0
            maxZoomLevel = 19.0
            controller.setZoom(14.5)
            controller.setCenter(CENTRO_SANTIAGO.aGeoPoint())
        }
    }

    val ciclo = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(ciclo) {
        val observador = LifecycleEventObserver { _, evento ->
            when (evento) {
                Lifecycle.Event.ON_RESUME -> mapa.onResume()
                Lifecycle.Event.ON_PAUSE -> mapa.onPause()
                else -> Unit
            }
        }
        ciclo.addObserver(observador)
        onDispose {
            ciclo.removeObserver(observador)
            mapa.onDetach() // Libera memoria al salir de la pantalla
        }
    }
    return mapa
}

/** Muestra el MapView dentro de Compose. */
@Composable
fun VistaMapa(mapView: MapView, modifier: Modifier = Modifier) {
    AndroidView(factory = { mapView }, modifier = modifier)
}

/** Capa con el punto azul de tu posición. Se enciende con enableMyLocation(). */
fun MapView.crearCapaMiUbicacion(): MyLocationNewOverlay {
    val capa = MyLocationNewOverlay(GpsMyLocationProvider(context), this)
    overlays.add(capa)
    return capa
}

/** Detecta toques en zonas vacías del mapa (para cerrar la tarjeta inferior). */
fun MapView.alTocarMapa(accion: () -> Unit) {
    val receptor = object : MapEventsReceiver {
        override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
            accion()
            return true
        }

        override fun longPressHelper(p: GeoPoint?): Boolean = false
    }
    // Índice 0: queda "debajo" de los marcadores, así los toques a un marcador no lo activan.
    overlays.add(0, MapEventsOverlay(receptor))
}

/** Vuela con animación hasta [punto]. */
fun MapView.irA(punto: GeoPoint, zoom: Double = 16.5) {
    controller.animateTo(punto, zoom, 800L)
}

fun Coordenada.aGeoPoint() = GeoPoint(latitud, longitud)

fun GeoPoint.aCoordenada() = Coordenada(latitude, longitude)

/**
 * Dibuja un pin de color con borde blanco (sin imágenes externas).
 * [color] es un color Android (Int), por ejemplo Color(...).toArgb().
 */
fun crearIconoPin(context: Context, color: Int, tamanoDp: Float = 34f): Drawable {
    val densidad = context.resources.displayMetrics.density
    val ancho = (tamanoDp * densidad).toInt()
    val alto = (tamanoDp * 1.35f * densidad).toInt()
    val radio = ancho / 2f

    val bitmap = Bitmap.createBitmap(ancho, alto, Bitmap.Config.ARGB_8888)
    val lienzo = Canvas(bitmap)
    val pintura = Paint(Paint.ANTI_ALIAS_FLAG)

    // Punta del pin
    val punta = Path().apply {
        moveTo(radio, alto.toFloat())
        lineTo(radio - radio * 0.62f, radio * 1.45f)
        lineTo(radio + radio * 0.62f, radio * 1.45f)
        close()
    }
    pintura.color = color
    lienzo.drawPath(punta, pintura)

    // Círculo con borde blanco
    pintura.color = android.graphics.Color.WHITE
    lienzo.drawCircle(radio, radio, radio * 0.98f, pintura)
    pintura.color = color
    lienzo.drawCircle(radio, radio, radio * 0.80f, pintura)

    // Punto blanco central
    pintura.color = android.graphics.Color.WHITE
    lienzo.drawCircle(radio, radio, radio * 0.32f, pintura)

    return BitmapDrawable(context.resources, bitmap)
}
