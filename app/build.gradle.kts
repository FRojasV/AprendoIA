// =====================================================================
// app/build.gradle.kts  —  módulo de la aplicación "Aprendo IA"
// Compatible con AGP 9 (Kotlin integrado: no se usa el plugin kotlin-android).
// =====================================================================
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
}

// ---------------------------------------------------------------------
// Dirección de los microservicios Spring Boot.
//  - Emulador de Android Studio: 10.0.2.2 es el "localhost" del PC.
//  - Teléfono real por Wi-Fi: cambia por la IP del PC (ej. "192.168.1.15").
// ---------------------------------------------------------------------
val hostBackend = "10.0.2.2"

// Firma de la APK: lee keystore.properties (en la raíz del proyecto) si existe.
val propiedadesFirma = Properties().apply {
    val archivo = rootProject.file("keystore.properties")
    if (archivo.exists()) archivo.inputStream().use { load(it) }
}

android {
    namespace = "com.triskeledu.aprendoia"
    // Las librerías de 2026 (Compose 1.12, Navigation 2.10, Core 1.19) exigen compilar con API 37.
    compileSdk = 37

    defaultConfig {
        applicationId = "com.triskeledu.aprendoia"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // URLs que usa Retrofit (se leen en el código como BuildConfig.URL_...)
        buildConfigField("String", "URL_MS_USUARIOS", "\"http://$hostBackend:8081/\"")
        buildConfigField("String", "URL_MS_CONTENIDOS", "\"http://$hostBackend:8082/\"")
        buildConfigField("String", "URL_MS_JUEGO", "\"http://$hostBackend:8083/\"")
        // API externa
        buildConfigField("String", "URL_API_CLIMA", "\"https://api.open-meteo.com/\"")
    }

    // ---------- Firma de la APK ----------
    signingConfigs {
        if (propiedadesFirma.containsKey("storeFile")) {
            create("release") {
                storeFile = rootProject.file(propiedadesFirma.getProperty("storeFile"))
                storePassword = propiedadesFirma.getProperty("storePassword")
                keyAlias = propiedadesFirma.getProperty("keyAlias")
                keyPassword = propiedadesFirma.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        debug {

            enableUnitTestCoverage = true
        }
        release {
            isMinifyEnabled = false
            signingConfigs.findByName("release")?.let { signingConfig = it }
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
        buildConfig = true // Para BuildConfig.URL_MS_...
    }

    testOptions {
        unitTests {
            // Los métodos de Android (Log, etc.) devuelven valores por defecto en pruebas JVM.
            isReturnDefaultValues = true
        }
    }
}

dependencies {
    // ---------- Base Android + Compose ----------
    implementation("androidx.core:core-ktx:1.19.1")
    implementation("androidx.activity:activity-compose:1.13.0")

    implementation(platform("androidx.compose:compose-bom:2026.09.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // Íconos (mapa, QR, colección, etc.)
    implementation("androidx.compose.material:material-icons-extended:1.7.8")

    // ---------- ViewModel + StateFlow en Compose ----------
    val lifecycleVersion = "2.11.0"
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:$lifecycleVersion")

    // ---------- Navegación entre pantallas ----------
    implementation("androidx.navigation:navigation-compose:2.10.2")

    // ---------- Room (SQLite) ----------
    val roomVersion = "2.8.5"
    implementation("androidx.room:room-runtime:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    // ---------- Sesión del jugador activo ----------
    implementation("androidx.datastore:datastore-preferences:1.2.1")

    // ---------- Escáner QR de Google (igual que el ejemplo "QR") ----------
    // Abre su propia pantalla de cámara: no hay que pedir permiso de cámara.
    implementation("com.google.android.gms:play-services-code-scanner:16.1.0")

    // ---------- Generador de códigos QR (panel del Administrador) ----------
    implementation("com.google.zxing:core:3.5.4")

    // ---------- Mapa OpenStreetMap ----------
    implementation("org.osmdroid:osmdroid-android:6.1.20")

    // ---------- Retrofit: microservicios propios + API externa (Open-Meteo) ----------
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // ---------- Pruebas unitarias (ViewModel y Repository) ----------
    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.14.5")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
    // JSONObject real (el de Android no funciona en pruebas JVM)
    testImplementation("org.json:json:20240303")
}
