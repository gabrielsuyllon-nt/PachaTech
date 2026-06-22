package com.example.appriego.ui

import android.content.Context
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appriego.data.AppDatabase
import com.example.appriego.model.PlantaModelo
import kotlinx.coroutines.delay

data class HistorialSemana(
    val semana: String,
    val saludPorcentaje: Float,
    val detalle: String,
    val colorFondo: Color,
    val colorTexto: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(onVolverClick: () -> Unit) {
    val context = LocalContext.current
    val db = remember { AppDatabase(context) }
    val prefs = context.getSharedPreferences("PachaTechPrefs", Context.MODE_PRIVATE)

    // Recuperar el huerto actual y sus plantas
    val nombreGuardado = prefs.getString("nombreHuertoActual", "") ?: ""
    val plantasGuardadas = remember {
        if (nombreGuardado.isNotEmpty()) db.obtenerMacetasPorHuerto(nombreGuardado).filter { it.nombre != "Vacío" }
        else listOf()
    }

    // Ver si venimos desde la bitácora con una planta pre-seleccionada
    val plantaIdInicial = prefs.getInt("planta_historial_id", -1)

    // Estado de la planta actual que estamos viendo en el historial
    var plantaSeleccionada by remember {
        mutableStateOf(plantasGuardadas.find { it.id == plantaIdInicial } ?: plantasGuardadas.firstOrNull())
    }

    // Cargar historial de la base de datos de SharedPreferences dinámicamente según la planta seleccionada
    val datosHistorial = remember(plantaSeleccionada) {
        if (plantaSeleccionada != null) {
            val historialString = prefs.getString("historial_planta_${plantaSeleccionada!!.id}", "") ?: ""
            if (historialString.isNotEmpty()) {
                historialString.split("||").map {
                    val partes = it.split("::")
                    val textoDetalle = partes[2]

                    val cFondo = when {
                        textoDetalle.contains("EFICIENTE") -> Color(0xFFE8F5E9)
                        textoDetalle.contains("PREVENTIVA") -> Color(0xFFFFF3E0)
                        textoDetalle.contains("PREVENTIVO") -> Color(0xFFF5F5F5)
                        else -> Color(0xFFFFEBEE)
                    }

                    val cTexto = when {
                        textoDetalle.contains("EFICIENTE") -> Color(0xFF1B5E20)
                        textoDetalle.contains("PREVENTIVA") -> Color(0xFFE65100)
                        textoDetalle.contains("PREVENTIVO") -> Color(0xFF424242)
                        else -> Color(0xFFC62828)
                    }

                    HistorialSemana(
                        semana = partes[0],
                        saludPorcentaje = partes[1].toFloat(),
                        detalle = textoDetalle,
                        colorFondo = cFondo,
                        colorTexto = cTexto
                    )
                }
            } else emptyList()
        } else emptyList()
    }

    var semanaSeleccionada by remember(plantaSeleccionada) { mutableStateOf(datosHistorial.lastOrNull()) }

    // Animación de las barras (se reinicia sola al cambiar de planta)
    var iniciarAnimacion by remember { mutableStateOf(false) }
    LaunchedEffect(plantaSeleccionada) {
        iniciarAnimacion = false
        delay(50)
        iniciarAnimacion = true
    }

    val multiplicadorAltura by animateFloatAsState(
        targetValue = if (iniciarAnimacion) 1f else 0f,
        animationSpec = tween(durationMillis = 800)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = listOf(Color(0xFFE8F5E9), Color(0xFFFFFFFF))))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- CABECERA ---
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onVolverClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = MaterialTheme.colorScheme.primary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Panel de Salud", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                    Text("Base de datos histórica", fontSize = 14.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- SELECTOR DE PLANTAS (INDEPENDIENTE) ---
            Text(
                text = "Consultar expediente de:",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (plantasGuardadas.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val filas = plantasGuardadas.chunked(3)

                    filas.forEachIndexed { indexFila, filaPlantas ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            filaPlantas.forEachIndexed { indexColumna, planta ->
                                val indexReal = indexFila * 3 + indexColumna
                                val esSeleccionada = plantaSeleccionada?.id == planta.id

                                Button(
                                    onClick = {
                                        plantaSeleccionada = planta
                                        semanaSeleccionada = null // Reinicia la selección
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (esSeleccionada) MaterialTheme.colorScheme.primary else Color(0xFFF5F5F5)
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1f) // Ancho uniforme
                                        .height(40.dp),
                                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                                ) {
                                    Text(
                                        text = "${indexReal + 1}. ${planta.nombre.split(" ").first()}",
                                        fontSize = 11.sp,
                                        color = if (esSeleccionada) Color.White else Color.DarkGray,
                                        fontWeight = if (esSeleccionada) FontWeight.Bold else FontWeight.Medium,
                                        maxLines = 1
                                    )
                                }
                            }
                            // Relleno para mantener el diseño cuadrado
                            repeat(3 - filaPlantas.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            } else {
                Text("No hay plantas registradas en este huerto.", color = Color.Gray, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- GRÁFICO HISTÓRICO ---
            if (plantaSeleccionada == null) {
                // Estado vacío si no seleccionó nada
            } else if (datosHistorial.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📂", fontSize = 40.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Esta planta no tiene bitácoras registradas.", color = Color.Gray, fontSize = 14.sp)
                    }
                }
            } else {
                // Si la semana seleccionada es nula (acaba de cambiar de planta), autoseleccionar la última
                if (semanaSeleccionada == null) {
                    semanaSeleccionada = datosHistorial.lastOrNull()
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(8.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {

                        Text("Evolución Semanal", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(24.dp))

                        // GRÁFICO ANIMADO CON SCROLL (CORREGIDO PARA QUE NO SE CORTE EL TEXTO)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp) // <-- Altura total aumentada
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(20.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            datosHistorial.forEach { dato ->
                                val esSeleccionada = dato.semana == semanaSeleccionada?.semana
                                val alturaBarra = (120.dp * (dato.saludPorcentaje / 100f)) * multiplicadorAltura // <-- Barra ligeramente más baja

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .clickable { semanaSeleccionada = dato }
                                        .padding(bottom = 8.dp) // <-- Margen de seguridad abajo
                                ) {
                                    if (esSeleccionada) {
                                        Text("${dato.saludPorcentaje.toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = dato.colorTexto)
                                        Spacer(modifier = Modifier.height(4.dp))
                                    } else {
                                        Spacer(modifier = Modifier.height(20.dp))
                                    }

                                    Box(
                                        modifier = Modifier
                                            .width(40.dp)
                                            .height(alturaBarra)
                                            .background(
                                                color = if (esSeleccionada) dato.colorTexto else dato.colorTexto.copy(alpha = 0.4f),
                                                shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                                            )
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(dato.semana, fontSize = 12.sp, fontWeight = if (esSeleccionada) FontWeight.ExtraBold else FontWeight.Medium, color = if (esSeleccionada) Color.Black else Color.Gray)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                        Divider(color = Color(0xFFF5F5F5), thickness = 2.dp)
                        Spacer(modifier = Modifier.height(24.dp))

                        // DETALLES DEL REPORTE
                        semanaSeleccionada?.let { semana ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = semana.colorFondo),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(0.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(10.dp).background(semana.colorTexto, CircleShape))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Reporte: ${semana.semana}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = semana.colorTexto)
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(semana.detalle, fontSize = 13.sp, color = Color.DarkGray, lineHeight = 18.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}