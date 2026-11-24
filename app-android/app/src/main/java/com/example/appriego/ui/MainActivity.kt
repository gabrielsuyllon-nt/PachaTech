package com.example.appriego.ui

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.appriego.data.AppDatabase
import com.example.appriego.ui.theme.AppRiegoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("PachaTechPrefs", MODE_PRIVATE)
        val sesionActiva = prefs.getBoolean("sesionActiva", false)
        val pantallaInicial = if (sesionActiva) "bluetooth" else "login"

        setContent {
            AppRiegoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // --- 🚀 MAGIA DE PERMISOS AL INICIAR LA APP ---
                    val permisosLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestMultiplePermissions()
                    ) { _ -> /* Ya el sistema los pidió, no bloqueamos la app si rechaza */ }

                    LaunchedEffect(Unit) {
                        val permisos = mutableListOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                        // Si el Android es 12 o superior, pide permisos especiales de Bluetooth
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            permisos.add(Manifest.permission.BLUETOOTH_CONNECT)
                            permisos.add(Manifest.permission.BLUETOOTH_SCAN)
                        }
                        permisosLauncher.launch(permisos.toTypedArray())
                    }
                    // ----------------------------------------------

                    var pantalla by remember { mutableStateOf(pantallaInicial) }
                    val context = LocalContext.current
                    val db = remember { AppDatabase(context) }

                    when (pantalla) {
                        "login" -> LoginScreen(
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
                            onDeviceConnected = { pantalla = "home" }
                        )
                    }
                }
            }
        }
    }
}