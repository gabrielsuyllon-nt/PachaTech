// ui/ProfileScreen.kt
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

val GreenButton = Color(0xFF4CAF50)

@Composable
fun ProfileScreen(onProfileSaved: () -> Unit) {
    val context = LocalContext.current
    var nombre by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

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
            Text(text = "🧑‍🌾", fontSize = 80.sp)

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Crea tu perfil",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = GreenDark,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "¿Cómo te gustaría que te llame PachaTech?",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Campo NOMBRE — sin placeholder, solo label
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it; showError = false },
                label = { Text("Tu nombre — Ej. Gabriel") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                isError = showError && nombre.isBlank()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo CONTRASEÑA — visualTransformation siempre activo
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; showError = false },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                isError = showError && password.isBlank()
            )

            if (showError) {
                Spacer(modifier = Modifier.height(6.dp))
                Text("Completa todos los campos", color = Color.Red, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (nombre.isBlank() || password.isBlank()) {
                        showError = true
                    } else {
                        guardarCredenciales(context, nombre.trim(), password)
                        onProfileSaved()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = GreenButton)
            ) {
                Text("Comenzar", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

fun obtenerNombre(context: Context): String? {
    val prefs = context.getSharedPreferences("pachatech_prefs", Context.MODE_PRIVATE)
    return prefs.getString("user_name", null)
}