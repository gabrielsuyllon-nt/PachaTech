package com.example.appriego.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.appriego.data.AppDatabase
import com.example.appriego.ui.theme.AppRiegoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("PachaTechPrefs", MODE_PRIVATE)
        val sesionActiva = prefs.getBoolean("sesionActiva", false)
        val pantallaInicial = if (sesionActiva) "home" else "login"

        setContent {
            AppRiegoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var pantalla by remember { mutableStateOf(pantallaInicial) }
                    val context = androidx.compose.ui.platform.LocalContext.current
                    val db = remember { AppDatabase(context) }

                    when (pantalla) {
                        "login" -> LoginScreen(
                            onLoginExitoso = { pantalla = "home" },
                            onNavegarRegistro = { pantalla = "registro" }
                        )
                        "registro" -> RegistroScreen(
                            onRegistroExitoso = { pantalla = "login" },
                            onVolverLogin = { pantalla = "login" }
                        )
                        "home" -> HomeScreen(
                            onVincularClick = { pantalla = "bitacora" } // Modificado: Ahora te lleva a la Bitácora/Evaluación
                        )
                        "bitacora" -> BitacoraScreen(
                            onVolverClick = { pantalla = "home" } // Regresa al Panel de Control principal (HomeScreen)
                        )
                        // NOTA: Si vas a usar Bluetooth más adelante, puedes mantener la ruta aquí:
                        "bluetooth" -> BluetoothMainScreen(
                            onDeviceConnected = { pantalla = "home" }
                        )
                    }
                }
            }
        }
    }
}