package com.example.appriego.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appriego.data.AppDatabase

@Composable
fun LoginScreen(onLoginExitoso: () -> Unit, onNavegarRegistro: () -> Unit) {
    val context = LocalContext.current
    val db = remember { AppDatabase(context) }

    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("PachaTech", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text("Sistema de Monitoreo", fontSize = 16.sp, color = MaterialTheme.colorScheme.secondary)

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo Electronico") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            label = { Text("Contrasena") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (correo.isNotEmpty() && contrasena.isNotEmpty()) {
                    val esValido = db.verificarLogin(correo, contrasena)
                    if (esValido) {
                        // AQUÍ GUARDAMOS LA SESIÓN PARA NO PEDIRLA OTRA VEZ
                        val prefs = context.getSharedPreferences("PachaTechPrefs", Context.MODE_PRIVATE)
                        prefs.edit().putBoolean("sesionActiva", true).apply()

                        Toast.makeText(context, "Bienvenido", Toast.LENGTH_SHORT).show()
                        onLoginExitoso()
                    } else {
                        Toast.makeText(context, "Correo o contrasena incorrectos", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Completa los campos", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar Sesion")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onNavegarRegistro) {
            Text("No tienes cuenta? Registrate aqui")
        }
    }
}