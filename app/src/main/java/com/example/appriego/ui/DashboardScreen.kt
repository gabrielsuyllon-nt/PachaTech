package com.example.appriego.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun DashboardScreen(
    onAbrirBitacora: () -> Unit,
    onEditarHuerto: () -> Unit,
    viewModel: BluetoothViewModel = viewModel()
) {
    val sensorData by viewModel.sensorData.collectAsState()
    val connectionState by viewModel.connectionState.collectAsState()

    val humedadSuelo = "${sensorData.humedadSuelo}%"
    val temperatura = "${sensorData.temperatura} °C"
    val humedadAire = "${sensorData.humedadAire}%"

    // TEXTOS FORMALES
    val estadoBomba = if (sensorData.estadoBomba == 1) "ACTIVA" else "APAGADA"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7F6)) // Un fondo apenas grisáceo para que el blanco resalte
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

                // INDICADOR LED PROFESIONAL
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(if (connectionState) Color(0xFF2E7D32) else Color(0xFFD32F2F), CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (connectionState) "Sincronizado en tiempo real" else "Sistema Desconectado",
                        fontSize = 14.sp,
                        color = if (connectionState) Color(0xFF2E7D32) else Color(0xFFD32F2F),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Button(
                onClick = onEditarHuerto,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF37474F)), // Gris azulado corporativo
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Text("Editar Huerto", fontSize = 12.sp, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            TarjetaSensor("Humedad Suelo", humedadSuelo, Color(0xFFE8F5E9), Color(0xFF1B5E20), Modifier.weight(1f))
            TarjetaSensor("Temperatura", temperatura, Color(0xFFFFF3E0), Color(0xFFE65100), Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            TarjetaSensor("Humedad Aire", humedadAire, Color(0xFFE3F2FD), Color(0xFF0D47A1), Modifier.weight(1f))
            TarjetaSensor("Bomba de Agua", estadoBomba, if (sensorData.estadoBomba == 1) Color(0xFFE1F5FE) else Color(0xFFECEFF1), if (sensorData.estadoBomba == 1) Color(0xFF0277BD) else Color(0xFF455A64), Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text("Control Manual del Sistema:", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF37474F))
        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { viewModel.encenderBomba() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                modifier = Modifier.weight(1f).height(45.dp),
                shape = RoundedCornerShape(8.dp)
            ) { Text("Prender", fontSize = 13.sp) }

            Button(
                onClick = { viewModel.apagarBomba() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                modifier = Modifier.weight(1f).height(45.dp),
                shape = RoundedCornerShape(8.dp)
            ) { Text("Apagar", fontSize = 13.sp) }

            Button(
                onClick = { viewModel.modoAutomatico() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF57C00)),
                modifier = Modifier.weight(1f).height(45.dp),
                shape = RoundedCornerShape(8.dp)
            ) { Text("Auto", fontSize = 13.sp) }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onAbrirBitacora,
            modifier = Modifier.fillMaxWidth().height(60.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20))
        ) {
            Text("Abrir Bitácora de Plantas", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun TarjetaSensor(titulo: String, valor: String, colorFondo: Color, colorTexto: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(120.dp),
        colors = CardDefaults.cardColors(containerColor = colorFondo),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp) // Diseño más plano y moderno
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = titulo, fontSize = 14.sp, color = Color(0xFF455A64), fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = valor, fontSize = 22.sp, color = colorTexto, fontWeight = FontWeight.ExtraBold)
        }
    }
}