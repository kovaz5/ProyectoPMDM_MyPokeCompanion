package com.example.mypokecompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.mypokecompanion.navigation.AppNavigation
import com.example.mypokecompanion.ui.theme.MyPokeCompanionTheme

/**
 * Actividad principal y punto de entrada de la aplicación.
 * Configura el tema de la aplicación y establece el contenido de la UI
 * utilizando Jetpack Compose, iniciando la navegación con [AppNavigation].
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyPokeCompanionTheme { // Aplica el tema personalizado de la aplicación.
                // Surface es un contenedor que utiliza el color de fondo del tema.
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Inicia la estructura de navegación de la aplicación.
                    AppNavigation()
                }
            }
        }
    }
}