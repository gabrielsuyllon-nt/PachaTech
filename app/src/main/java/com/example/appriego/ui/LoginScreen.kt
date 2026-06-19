// ui/LoginScreen.kt
package com.example.appriego.ui

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    val context = LocalContext.current
    var usuario by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "🌱", fontSize = 72.sp)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "PachaTech",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = GreenDark
            )

            Text(
                text = "Bienvenido de vuelta",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Campo usuario
            OutlinedTextField(
                value = usuario,
                onValueChange = { usuario = it; errorMsg = "" },
                label = { Text("Usuario") },
                placeholder = { Text("Ej. Morgan") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; errorMsg = "" },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )

            // Mensaje de error
            if (errorMsg.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMsg,
                    color = Color.Red,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Botón Ingresar
            Button(
                onClick = {
                    val resultado = verificarCredenciales(context, usuario.trim(), password)
                    if (resultado) {
                        onLoginSuccess()
                    } else {
                        errorMsg = "Usuario o contraseña incorrectos"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = GreenButton)
            ) {
                Text("Ingresar", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ── Helpers SharedPreferences ──────────────────────────────

fun guardarCredenciales(context: Context, usuario: String, password: String) {
    val prefs = context.getSharedPreferences("pachatech_prefs", Context.MODE_PRIVATE)
    prefs.edit()
        .putString("user_name", usuario)
        .putString("user_password", password)
        .apply()
}

fun verificarCredenciales(context: Context, usuario: String, password: String): Boolean {
    val prefs = context.getSharedPreferences("pachatech_prefs", Context.MODE_PRIVATE)
    val savedUser = prefs.getString("user_name", null)
    val savedPass = prefs.getString("user_password", null)
    return usuario == savedUser && password == savedPass
}

fun tieneCuenta(context: Context): Boolean {
    val prefs = context.getSharedPreferences("pachatech_prefs", Context.MODE_PRIVATE)
    return prefs.getString("user_password", null) != null
}