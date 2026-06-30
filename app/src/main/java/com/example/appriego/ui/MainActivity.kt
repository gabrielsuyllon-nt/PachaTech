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
import com.example.appriego.ui.HistorialScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("PachaTechPrefs", MODE_PRIVATE)
        val sesionActiva = prefs.getBoolean("sesionActiva", false)

        // --- CAMBIO CLAVE PARA HARDWARE ---
        // Si hay sesión activa, primero debe conectarse a la placa, luego ir al Home.
        val pantallaInicial = if (sesionActiva) "bluetooth" else "login"

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
                            // Al loguearse con éxito, lo mandamos a conectar el módulo
                            onLoginExitoso = { pantalla = "bluetooth" },
                            onNavegarRegistro = { pantalla = "registro" }
                        )
                        "registro" -> RegistroScreen(
                            onRegistroExitoso = { pantalla = "login" },
                            onVolverLogin = { pantalla = "login" }
                        )
                        "home" -> HomeScreen(
                            onVincularClick = { pantalla = "bluetooth" },
                            onBitacoraClick = { pantalla = "bitacora" },
                            onLogout = {
                                val editor = getSharedPreferences("PachaTechPrefs", MODE_PRIVATE).edit()
                                editor.putBoolean("sesionActiva", false).apply()
                                pantalla = "login"
                            },
                            onAddAccount = {
                                pantalla = "login"
                            }
                        )
                        "bitacora" -> BitacoraScreen(
                            onVolverClick = { pantalla = "home" },
                            onVerHistorialClick = { pantalla = "historial" }
                        )
                        "historial" -> HistorialScreen(
                            onVolverClick = { pantalla = "bitacora" }
                        )
                        "bluetooth" -> BluetoothMainScreen(
                            // Una vez emparejado y conectado, recién entramos al dashboard principal
                            onDeviceConnected = { pantalla = "home" }
                        )
                    }
                }
            }
        }
    }
}