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
import androidx.lifecycle.viewmodel.compose.viewModel // Importante para enlazar el cerebro

@Composable
fun DashboardScreen(
    onAbrirBitacora: () -> Unit,
    onEditarHuerto: () -> Unit,
    viewModel: BluetoothViewModel = viewModel() // <-- INYECTAMOS EL MOTOR AQUÍ
) {
    // 1. ABRIMOS LAS TUBERÍAS PARA ESCUCHAR AL ARDUINO
    val sensorData by viewModel.sensorData.collectAsState()
    val connectionState by viewModel.connectionState.collectAsState()

    // 2. REEMPLAZAMOS LOS TEXTOS FALSOS POR LOS DATOS REALES
    val humedadSuelo = "${sensorData.humedadSuelo}%"
    val temperatura = "${sensorData.temperatura} °C"
    val humedadAire = "${sensorData.humedadAire}%" // Cambiamos Tanque por Aire
    val estadoBomba = if (sensorData.estadoBomba == 1) "ENCENDIDA 💧" else "APAGADA 🛑"

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

                // Indicador visual de si el Bluetooth está conectado o no
                Text(
                    text = if (connectionState) "🟢 Conectado en tiempo real" else "🔴 Desconectado",
                    fontSize = 14.sp,
                    color = if (connectionState) Color(0xFF2E7D32) else Color.Red
                )
            }
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
                titulo = "Humedad Aire",
                valor = humedadAire,
                colorFondo = Color(0xFFE3F2FD),
                colorTexto = Color(0xFF1565C0),
                modifier = Modifier.weight(1f)
            )
            TarjetaSensor(
                titulo = "Bomba de Agua",
                valor = estadoBomba,
                colorFondo = if (sensorData.estadoBomba == 1) Color(0xFFE1F5FE) else Color(0xFFECEFF1),
                colorTexto = if (sensorData.estadoBomba == 1) Color(0xFF0277BD) else Color(0xFF455A64),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- LOS BOTONES DE CONTROL MANUAL (EL DISPARO) ---
        Text("Control Manual de Bomba:", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { viewModel.encenderBomba() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                modifier = Modifier.weight(1f).height(45.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Prender", fontSize = 13.sp)
            }
            Button(
                onClick = { viewModel.apagarBomba() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                modifier = Modifier.weight(1f).height(45.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Apagar", fontSize = 13.sp)
            }
            Button(
                onClick = { viewModel.modoAutomatico() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF57C00)),
                modifier = Modifier.weight(1f).height(45.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Auto", fontSize = 13.sp)
            }
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
            // Ajusté el tamaño de la letra a 22.sp para que la palabra "ENCENDIDA" no se corte
            Text(text = valor, fontSize = 22.sp, color = colorTexto, fontWeight = FontWeight.Bold)
        }
    }
}