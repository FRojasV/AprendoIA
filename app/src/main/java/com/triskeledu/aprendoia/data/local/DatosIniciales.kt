package com.triskeledu.aprendoia.data.local

import com.triskeledu.aprendoia.data.local.entity.ContenidoEntity
import com.triskeledu.aprendoia.data.local.entity.PuntoEntity
import com.triskeledu.aprendoia.data.local.entity.Rol
import com.triskeledu.aprendoia.data.local.entity.UsuarioEntity
import com.triskeledu.aprendoia.util.Seguridad

/**
 * Datos que la app necesita desde el primer arranque:
 *  - Cuentas en duro de Administrador y Creador de Contenido (exigidas por el caso).
 *  - Puntos de ejemplo en Santiago y sus cápsulas de IA, mientras no exista el backend.
 */
object DatosIniciales {

    const val ID_ADMIN = 1L
    const val ID_CREADOR = 2L

    suspend fun poblar(db: AppDatabase) {
        // Las cuentas en duro se insertan siempre con IGNORE: si existen, no pasa nada.
        db.usuarioDao().insertarSiNoExisten(usuariosEnDuro())

        // Puntos y contenidos solo la primera vez, para que si el Administrador
        // elimina uno, no vuelva a aparecer al reiniciar la app.
        if (db.puntoDao().contar() == 0) {
            db.puntoDao().insertarSiNoExisten(puntos)
            db.contenidoDao().insertarSiNoExisten(contenidos)
        }
    }

    private fun usuariosEnDuro() = listOf(
        UsuarioEntity(
            id = ID_ADMIN,
            nombre = "Administrador",
            alias = "Admin",
            correo = "admin@triskeledu.cl",
            password = Seguridad.hash("123"),
            avatar = "avatar_admin",
            rol = Rol.ADMIN
        ),
        UsuarioEntity(
            id = ID_CREADOR,
            nombre = "Contenido",
            alias = "GenCont",
            correo = "contenido@triskeledu.cl",
            password = Seguridad.hash("123"),
            avatar = "avatar_creador",
            rol = Rol.CREADOR
        )
    )

    private val puntos = listOf(
        PuntoEntity(
            id = 1, nombre = "Plaza de Armas",
            descripcion = "Centro histórico y cívico de Santiago.",
            infoTuristica = "Fundada en 1541 por Pedro de Valdivia. La rodean la Catedral Metropolitana, el Correo Central y el Museo Histórico Nacional.",
            latitud = -33.4378, longitud = -70.6505, esHitoTuristico = true
        ),
        PuntoEntity(
            id = 2, nombre = "Cerro Santa Lucía",
            descripcion = "Cerro urbano con terrazas, fuentes y miradores.",
            infoTuristica = "Lugar donde se fundó la ciudad. Desde el Castillo Hidalgo hay una de las mejores vistas del centro.",
            latitud = -33.4405, longitud = -70.6440, esHitoTuristico = true
        ),
        PuntoEntity(
            id = 3, nombre = "Parque Forestal",
            descripcion = "Parque lineal junto al río Mapocho.",
            latitud = -33.4362, longitud = -70.6425, esHitoTuristico = false
        ),
        PuntoEntity(
            id = 4, nombre = "Museo Nacional de Bellas Artes",
            descripcion = "Principal museo de artes visuales del país.",
            infoTuristica = "Edificio inaugurado en 1910 para el Centenario, inspirado en el Petit Palais de París.",
            latitud = -33.4343, longitud = -70.6433, esHitoTuristico = true
        ),
        PuntoEntity(
            id = 5, nombre = "Barrio Lastarria",
            descripcion = "Barrio bohemio con cafés, librerías y centros culturales.",
            latitud = -33.4386, longitud = -70.6392, esHitoTuristico = false
        ),
        PuntoEntity(
            id = 6, nombre = "Palacio de La Moneda",
            descripcion = "Sede del Presidente de la República.",
            infoTuristica = "Construido entre 1784 y 1805 como casa de moneda. Bajo la plaza se encuentra el Centro Cultural La Moneda.",
            latitud = -33.4429, longitud = -70.6540, esHitoTuristico = true
        ),
        PuntoEntity(
            id = 7, nombre = "Cerro San Cristóbal",
            descripcion = "Parque Metropolitano con vista panorámica de la ciudad.",
            infoTuristica = "En su cumbre está el Santuario de la Inmaculada Concepción. Se puede subir en funicular o teleférico.",
            latitud = -33.4253, longitud = -70.6330, esHitoTuristico = true
        ),
        PuntoEntity(
            id = 8, nombre = "Biblioteca Nacional",
            descripcion = "Una de las bibliotecas más antiguas de Sudamérica.",
            latitud = -33.4421, longitud = -70.6456, esHitoTuristico = false
        )
    )

    // Sin videos por ahora: el Administrador o el Creador agregarán la URL de cada cápsula.
    // Mientras la URL esté vacía, la pantalla de video muestra "Video aún no disponible".
    private const val VIDEO_DEMO_1 = ""
    private const val VIDEO_DEMO_2 = ""

    private val contenidos = listOf(
        ContenidoEntity(
            id = 1, puntoId = 1, titulo = "Introducción a la Inteligencia Artificial",
            tema = "Fundamentos de IA", autor = "Triskeledu", url = VIDEO_DEMO_1, duracion = "2:15",
            descripcion = "Qué es la IA, cómo funciona y ejemplos de su uso en la vida diaria.",
            codigoQr = "QR-PLAZA-ARMAS"
        ),
        ContenidoEntity(
            id = 2, puntoId = 2, titulo = "IA en la Vida Diaria",
            tema = "Aplicaciones", autor = "Triskeledu", url = VIDEO_DEMO_2, duracion = "1:50",
            descripcion = "Asistentes de voz, recomendaciones y mapas: la IA que usas sin notarlo.",
            codigoQr = "QR-SANTA-LUCIA"
        ),
        ContenidoEntity(
            id = 3, puntoId = 3, titulo = "Robótica e IA",
            tema = "Robótica", autor = "Triskeledu", url = VIDEO_DEMO_1, duracion = "2:30",
            descripcion = "Cómo los robots perciben el entorno y toman decisiones.",
            codigoQr = "QR-PARQUE-FORESTAL"
        ),
        ContenidoEntity(
            id = 4, puntoId = 4, titulo = "IA y el Medio Ambiente",
            tema = "Sostenibilidad", autor = "Triskeledu", url = VIDEO_DEMO_2, duracion = "2:05",
            descripcion = "Modelos que predicen el clima y ayudan a cuidar los recursos naturales.",
            codigoQr = "QR-BELLAS-ARTES"
        ),
        ContenidoEntity(
            id = 5, puntoId = 5, titulo = "Futuro de la IA",
            tema = "Tendencias", autor = "Triskeledu", url = VIDEO_DEMO_1, duracion = "2:40",
            descripcion = "Hacia dónde avanza la IA y qué habilidades serán clave.",
            codigoQr = "QR-LASTARRIA"
        ),
        ContenidoEntity(
            id = 6, puntoId = 6, titulo = "Machine Learning en 2 minutos",
            tema = "Aprendizaje automático", autor = "Triskeledu", url = VIDEO_DEMO_2, duracion = "2:00",
            descripcion = "Cómo una máquina aprende a partir de ejemplos.",
            codigoQr = "QR-LA-MONEDA"
        ),
        ContenidoEntity(
            id = 7, puntoId = 7, titulo = "Redes Neuronales",
            tema = "Deep Learning", autor = "Triskeledu", url = VIDEO_DEMO_1, duracion = "2:20",
            descripcion = "Neuronas artificiales, capas y entrenamiento explicados con ejemplos simples.",
            codigoQr = "QR-SAN-CRISTOBAL"
        ),
        ContenidoEntity(
            id = 8, puntoId = 8, titulo = "IA Generativa",
            tema = "IA Generativa", autor = "Triskeledu", url = VIDEO_DEMO_2, duracion = "2:10",
            descripcion = "Modelos que crean texto, imágenes y música. Usos responsables.",
            codigoQr = "QR-BIBLIOTECA"
        )
    )
}
