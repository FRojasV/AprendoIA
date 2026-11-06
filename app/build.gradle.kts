// =====================================================================
// app/build.gradle.kts  —  módulo de la aplicación "Aprendo IA"
// Compatible con AGP 9 (Kotlin integrado: no se usa el plugin kotlin-android).
// =====================================================================
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
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
    }

    buildTypes {
        release {
            isMinifyEnabled = false
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
}
