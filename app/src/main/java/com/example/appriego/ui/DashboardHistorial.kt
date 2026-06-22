package com.example.appriego.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

// --- CLASE DE DATOS PARA EL HISTORIAL ---
data class HistorialSemana(
    val semana: String,
    val saludPorcentaje: Float, // De 0 a 100 para el gráfico
    val estado: String,
    val detalle: String,
    val colorPrincipal: Color,
    val colorFondo: Color
)

@Composable
fun DashboardHistorialSemanal(onDismiss: () -> Unit) {
    // Datos simulados (Mock) para impresionar en la presentación
    val datosHistorial = listOf(
        HistorialSemana("Sem 1", 35f, "CRÍTICO", "Suelo compactado y deficiencia hídrica detectada. Se recomendó aireación inmediata.", Color(0xFFD32F2F), Color(0xFFFFEBEE)),
        HistorialSemana("Sem 2", 60f, "PREVENTIVO", "Mejora en infiltración. Leve clorosis por falta de nutrientes. Se aplicó abono orgánico.", Color(0xFFF57C00), Color(0xFFFFF3E0)),
        HistorialSemana("Sem 3", 85f, "MEJORANDO", "Recuperación de turgencia. Crecimiento de nuevos brotes visible.", Color(0xFF7CB342), Color(0xFFF1F8E9)),
        HistorialSemana("Sem 4", 100f, "EFICIENTE", "Desarrollo vigoroso. Algoritmo de riego sincronizado perfectamente con las necesidades de la planta.", Color(0xFF1B5E20), Color(0xFFE8F5E9))
    )

    var semanaSeleccionada by remember { mutableStateOf(datosHistorial.last()) }

    // Disparador de la animación cuando se abre el cuadro
    var iniciarAnimacion by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { iniciarAnimacion = true }

    // Animación de crecimiento de las barras (Tarda 1 segundo)
    val multiplicadorAltura by animateFloatAsState(
        targetValue = if (iniciarAnimacion) 1f else 0f,
        animationSpec = tween(durationMillis = 1000)
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false) // Para hacerlo más ancho
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Título del Dashboard
                Text(
                    text = "Tendencia de Salud",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Últimas 4 semanas de monitoreo",
                    fontSize = 13.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(32.dp))

                // GRÁFICO DE BARRAS ANIMADO
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp), // Altura máxima del gráfico
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    datosHistorial.forEach { dato ->
                        val esSeleccionada = dato.semana == semanaSeleccionada.semana
                        val alturaBarra = (120.dp * (dato.saludPorcentaje / 100f)) * multiplicadorAltura

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { semanaSeleccionada = dato }
                                .padding(horizontal = 8.dp)
                        ) {
                            // Porcentaje arriba de la barra (aparece solo si está seleccionada)
                            if (esSeleccionada) {
                                Text(
                                    text = "${dato.saludPorcentaje.toInt()}%",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = dato.colorPrincipal
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                            } else {
                                Spacer(modifier = Modifier.height(20.dp)) // Espacio vacío para mantener alineación
                            }

                            // La Barra
                            Box(
                                modifier = Modifier
                                    .width(36.dp)
                                    .height(alturaBarra)
                                    .background(
                                        color = if (esSeleccionada) dato.colorPrincipal else dato.colorPrincipal.copy(alpha = 0.4f),
                                        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                                    )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Etiqueta del eje X (Sem 1, Sem 2...)
                            Text(
                                text = dato.semana,
                                fontSize = 12.sp,
                                fontWeight = if (esSeleccionada) FontWeight.ExtraBold else FontWeight.Medium,
                                color = if (esSeleccionada) Color.Black else Color.Gray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
                Divider(color = Color(0xFFF5F5F5), thickness = 2.dp)
                Spacer(modifier = Modifier.height(24.dp))

                // TARJETA DE DETALLES DE LA SEMANA SELECCIONADA
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = semanaSeleccionada.colorFondo),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(semanaSeleccionada.colorPrincipal, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Reporte: ${semanaSeleccionada.semana}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = semanaSeleccionada.colorPrincipal
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = semanaSeleccionada.detalle,
                            fontSize = 13.sp,
                            color = Color.DarkGray,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botón de Cerrar
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEEEEEE))
                ) {
                    Text("Cerrar Panel", color = Color.DarkGray, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}