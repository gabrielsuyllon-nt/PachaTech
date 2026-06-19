// ui/HomeScreen.kt
package com.example.appriego.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(onVincularClick: () -> Unit) {
    val context = LocalContext.current
    val nombre = obtenerNombre(context) ?: "Agricultor"

    // Fondo blanco limpio, sin Scaffold
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Header
        Text(
            text = "PachaTech",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = GreenDark
        )
        Text(
            text = "Sistema Inteligente de Riego",
            fontSize = 13.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Tarjeta bienvenida
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "¡Hola, $nombre! 👋",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Para comenzar a monitorear tus plantas, primero debes sincronizar tu aplicación con el módulo físico de tu huerto.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    lineHeight = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Ilustración
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "📱", fontSize = 52.sp)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "⇄", fontSize = 28.sp, color = Color.LightGray)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "🪵", fontSize = 52.sp)
        }

        Spacer(modifier = Modifier.weight(1f))

        // Botón
        Button(
            onClick = onVincularClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = GreenButton)
        ) {
            Text(
                text = "Vincular Huerto",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}