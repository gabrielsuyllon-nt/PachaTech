package com.example.appriego.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DashboardScreen(onAbrirBitacora: () -> Unit, onEditarHuerto: () -> Unit) {
    val humedadSuelo = "65%"
    val temperatura = "24 C"
    val estadoBomba = "Apagada"
    val nivelTanque = "80%"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Panel de Control", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                Text("Estado en tiempo real", fontSize = 14.sp, color = Color.Gray)
            }
            // AQUI ESTA EL NUEVO BOTON DE EDITAR HUERTO
            Button(
                onClick = onEditarHuerto,
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Text("Editar Huerto", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TarjetaSensor(
                titulo = "Humedad Suelo",
                valor = humedadSuelo,
                colorFondo = Color(0xFFE8F5E9),
                colorTexto = Color(0xFF2E7D32),
                modifier = Modifier.weight(1f)
            )
            TarjetaSensor(
                titulo = "Temperatura",
                valor = temperatura,
                colorFondo = Color(0xFFFFF3E0),
                colorTexto = Color(0xFFEF6C00),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TarjetaSensor(
                titulo = "Nivel de Tanque",
                valor = nivelTanque,
                colorFondo = Color(0xFFE3F2FD),
                colorTexto = Color(0xFF1565C0),
                modifier = Modifier.weight(1f)
            )
            TarjetaSensor(
                titulo = "Bomba de Agua",
                valor = estadoBomba,
                colorFondo = Color(0xFFECEFF1),
                colorTexto = Color(0xFF455A64),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onAbrirBitacora,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20))
        ) {
            Text("Abrir Bitacora de Plantas", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun TarjetaSensor(titulo: String, valor: String, colorFondo: Color, colorTexto: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(120.dp),
        colors = CardDefaults.cardColors(containerColor = colorFondo),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = titulo, fontSize = 14.sp, color = Color.DarkGray, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = valor, fontSize = 28.sp, color = colorTexto, fontWeight = FontWeight.Bold)
        }
    }
}