plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp") // Habilita KSP a nivel de proyecto
//    kotlin("plugin.serialization") version "2.1.21" // Habilita la serialización de Kotlin
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.21"
    alias(libs.plugins.kotlin.compose) // Habilita la serialización de Kotlin
    id("kotlin-parcelize") // Habilita la serialización de Kotlin
}

android {
    namespace = "com.example.mypokecompanion"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.mypokecompanion"
        minSdk = 25
        targetSdk = 35
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
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true // Habilita Jetpack Compose
        viewBinding = false // Habilita View Binding
    }
}

dependencies {


// Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0") // O la última versión estable

    // Moshi - Para parsear JSON
    implementation("com.squareup.moshi:moshi-kotlin:1.15.0") // O la última versión estable
    // kapt("com.squareup.moshi:moshi-kotlin-codegen:1.15.0") // Necesario si usas @JsonClass y quieres generación de código en tiempo de compilación
    // Si usas `KotlinJsonAdapterFactory` en runtime,
    // `moshi-kotlin-codegen` y `kapt` no son estrictamente necesarios,
    // pero `KotlinJsonAdapterFactory` usa reflexión, lo cual puede ser más lento.
    // Para este proyecto, `KotlinJsonAdapterFactory` es suficiente por simplicidad.

    // Retrofit Moshi Converter - Para que Retrofit use Moshi
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0") // O la última versión estable que coincida con tu versión de Retrofit

    // OkHttp (Retrofit lo necesita, usualmente ya lo tienes si usas Retrofit)
    implementation("com.squareup.okhttp3:okhttp:4.12.0") // O la última versión estable
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0") // Útil para debugging de red
    implementation ("androidx.paging:paging-runtime-ktx:3.3.0") // o la última versión
    implementation ("androidx.paging:paging-compose:3.3.0")    // Para integración con Jetpack Compose
    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation(libs.androidx.core.ktx) // https://developer.android.com/kotlin/ktx: Jetpack, Kotlin, and Android
    implementation(libs.androidx.appcompat) // https://developer.android.com/jetpack/androidx/releases/appcompat
    implementation(libs.material) // https://developer.android.com/jetpack/androidx/releases/material
    implementation(libs.androidx.activity) // https://developer.android.com/jetpack/androidx/releases/activity
    implementation(libs.androidx.constraintlayout) // https://developer.android.com/jetpack/androidx/releases/constraintlayout

    // https://mvnrepository.com/artifact/androidx.compose.material3/material3
//    implementation("androidx.compose.material3:material3") // Material 3
    implementation(libs.androidx.material3)

    /******************************************************************
     * Dependencias de Jetpack Compose
     * https://developer.android.com/develop/ui/compose/setup#kotlin
     ******************************************************************/
    val composeBom = platform("androidx.compose:compose-bom:2025.05.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)
//    implementation("androidx.compose.ui:ui-tooling-preview") // Preview de UI
    implementation(libs.androidx.ui.tooling.preview)
//    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation(libs.androidx.ui.tooling) // Herramientas de depuración
    // UI Tests
//    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation(libs.androidx.ui.test.junit4)
//    debugImplementation("androidx.compose.ui:ui-test-manifest")
    debugImplementation(libs.androidx.ui.test.manifest)
    // Opciona, Incluido automáticamente por material, solo añadir cuando necesites
    // los iconos pero no la librería de material (e.g. cuando usas Material3 o un
    // sistema de diseño personalizado basado en Foundation)
//    implementation("androidx.compose.material:material-icons-core")
    implementation(libs.androidx.material.icons.core)
    // Opcional, pero recomendado, para usar Material Design 3 icons
//    implementation("androidx.compose.material:material-icons-extended")
    implementation(libs.material.icons.extended)
    // Opcional, añade utilidades de tamaño de ventana
//    implementation("androidx.compose.material3.adaptive:adaptive")
    implementation(libs.androidx.adaptive)
    // Opcional, integración con activity
    // https://mvnrepository.com/artifact/androidx.activity/activity-compose
//    implementation("androidx.activity:activity-compose:1.10.1")
    implementation(libs.androidx.activity.compose)
//    implementation("androidx.compose.runtime:runtime-livedata")
    implementation(libs.androidx.runtime.livedata) // LiveData en Compose



    /******************************************************************
     * Dependencias Room
     * https://developer.android.com/training/data-storage/room
     ******************************************************************* */
//     https://mvnrepository.com/artifact/androidx.room/room-runtime
//    implementation("androidx.room:room-runtime:2.7.1") // Obligatorio
    implementation(libs.androidx.room.runtime) // Obligatorio
//    ksp("androidx.room:room-compiler:2.7.1") // Debes añadir KSP para la generación de código
    ksp(libs.androidx.room.compiler) // Debes añadir KSP para la generación de código
//     https://mvnrepository.com/artifact/androidx.room/room-ktx
//    implementation("androidx.room:room-ktx:2.7.1")
    implementation(libs.androidx.room.ktx) // Opcional, para kotlin y coroutines

    /******************************************************************
     * Dependencias de Navigation
     *  https://developer.android.com/jetpack/androidx/releases/navigation
     * ***************************************************** */
//     https://mvnrepository.com/artifact/androidx.navigation/navigation-compose
//    implementation("androidx.navigation:navigation-compose:2.9.0") // Para navegación en Compose
    implementation(libs.androidx.navigation.compose) // Para navegación en Compose
    // https://mvnrepository.com/artifact/androidx.navigation/navigation-fragment-ktx
//    implementation("androidx.navigation:navigation-fragment-ktx:2.9.0")
    implementation(libs.androidx.navigation.fragment.ktx) // Para navegación en Fragments
    // https://mvnrepository.com/artifact/androidx.navigation/navigation-ui-ktx
//    implementation("androidx.navigation:navigation-ui-ktx:2.9.0")
    implementation(libs.androidx.navigation.ui.ktx)
    // https://mvnrepository.com/artifact/androidx.navigation/navigation-testing
//    implementation("androidx.navigation:navigation-testing:2.9.0")
    implementation(libs.androidx.navigation.testing) // testing de navegación

    // Serialización: puede precisarse para la serialización de datos en alguna otra biblitoeca,
    // como Retrofit, Kator o navegación en Compose
    // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-serialization-json
//    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
    implementation(libs.kotlinx.serialization.json) // Serialización de JSON

    /******************************************************************
     * Dependencias de Ciclo de vida y ViewModel
     * https://developer.android.com/jetpack/androidx/releases/lifecycle
     * ********************************************************************/
    // https://mvnrepository.com/artifact/androidx.lifecycle/lifecycle-runtime-ktx
//    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.0") // Ciclo de vida
    implementation(libs.androidx.lifecycle.lifecycle.runtime.ktx) // Ciclo de vida
    // https://mvnrepository.com/artifact/androidx.lifecycle/lifecycle-runtime-compose
//    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.0") // Utilidades de ciclo de vida en Compose
    implementation(libs.androidx.lifecycle.runtime.compose) // Utilidades de ciclo de vida en Compose
    // https://mvnrepository.com/artifact/androidx.lifecycle/lifecycle-viewmodel-ktx
//    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.0") // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.ktx) // ViewModel
    // https://mvnrepository.com/artifact/androidx.lifecycle/lifecycle-viewmodel-compose
//    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.0") // En compose
    implementation(libs.androidx.lifecycle.viewmodel.compose) // En compose
    // https://mvnrepository.com/artifact/androidx.lifecycle/lifecycle-livedata-ktx
//    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.9.0") // LiveData
    implementation(libs.androidx.lifecycle.livedata.ktx) // LiveData
    // https://mvnrepository.com/artifact/androidx.lifecycle/lifecycle-viewmodel-savedstate
//    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:2.9.0") // Guardar estado en Compose
    implementation(libs.androidx.lifecycle.viewmodel.savedstate) // Guardar estado en Compose
    // https://mvnrepository.com/artifact/androidx.lifecycle/lifecycle-runtime-testing
//    implementation("androidx.lifecycle:lifecycle-runtime-testing:2.9.0") // Testing de ciclo de vida
    implementation(libs.androidx.lifecycle.runtime.testing) // Testing de ciclo de vida


    /******************************************************************
     * Dependencias de Coroutines
     * https://developer.android.com/kotlin/coroutines
     *****************************************************/
    // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-coroutines-android
//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2") // https://developer.android.com/kotlin/coroutines
    implementation(libs.kotlinx.coroutines.android) // https://developer.android.com/kotlin/coroutines
    // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-coroutines-core
//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2") // https://developer.android.com/kotlin/coroutines
    implementation(libs.org.jetbrains.kotlinx.kotlinx.coroutines.core) // https://developer.android.com/kotlin/coroutines

    /******************************************************************
     * Dependencias de RecyclerView
     * https://developer.android.com/jetpack/androidx/releases/recyclerview
     *****************************************************/
    // https://mvnrepository.com/artifact/androidx.recyclerview/recyclerview
    // https://mvnrepository.com/artifact/androidx.recyclerview/recyclerview
//    implementation("androidx.recyclerview:recyclerview:1.4.0")
    implementation(libs.androidx.recyclerview)
    // https://mvnrepository.com/artifact/androidx.recyclerview/recyclerview-selection
    // https://mvnrepository.com/artifact/androidx.recyclerview/recyclerview-selection
//    implementation("androidx.recyclerview:recyclerview-selection:1.1.0") // Selección de elementos
    implementation(libs.androidx.recyclerview.selection) // Selección de elementos
    // https://mvnrepository.com/artifact/jp.wasabeef/recyclerview-animators
    // https://mvnrepository.com/artifact/jp.wasabeef/recyclerview-animators
//    implementation("jp.wasabeef:recyclerview-animators:4.0.2") // Animaciones de RecyclerView
    implementation(libs.recyclerview.animators)

    /******************************************************************
     * Dependencias de Coil
     *  https://github.com/coil-kt/coil
     *****************************************************/
    // https://mvnrepository.com/artifact/io.coil-kt/coil
//    implementation("io.coil-kt:coil:2.7.0")
    implementation(libs.coil)

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}