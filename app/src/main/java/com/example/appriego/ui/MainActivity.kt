// ui/MainActivity.kt
package com.example.appriego.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.appriego.ui.theme.AppRiegoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppRiegoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current

                    // Las 3 pantallas posibles
                    var pantalla by remember { mutableStateOf("splash") }

                    when (pantalla) {
                        "splash" -> SplashScreen(
                            onSplashFinished = {
                                // Si ya tiene nombre guardado, salta el perfil
                                val nombreGuardado = obtenerNombre(context)
                                pantalla = if (nombreGuardado != null) "home" else "perfil"
                            }
                        )
                        "perfil" -> ProfileScreen(
                            onProfileSaved = { pantalla = "home" }
                        )
                        "home" -> BluetoothMainScreen()
                    }
                    when (pantalla) {
                        "splash" -> SplashScreen(
                            onSplashFinished = {
                                val nombreGuardado = obtenerNombre(context)
                                pantalla = if (nombreGuardado != null) "home" else "perfil"
                            }
                        )
                        "perfil" -> ProfileScreen(
                            onProfileSaved = { pantalla = "home" }
                        )
                        "home" -> HomeScreen(
                            onVincularClick = { pantalla = "bluetooth" }
                        )
                        "bluetooth" -> BluetoothMainScreen()
                    }
                }
            }
        }
    }
}