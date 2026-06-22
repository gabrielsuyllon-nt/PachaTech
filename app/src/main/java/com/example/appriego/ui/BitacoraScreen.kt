package com.example.appriego.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material.icons.filled.List

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BitacoraScreen(onVolverClick: () -> Unit, onVerHistorialClick: () -> Unit) {
    val context = LocalContext.current
    val db = remember { AppDatabase(context) }

    val prefs = context.getSharedPreferences("PachaTechPrefs", Context.MODE_PRIVATE)
    val nombreGuardado = prefs.getString("nombreHuertoActual", "") ?: ""
    val plantasGuardadas = remember {
        if (nombreGuardado.isNotEmpty()) db.obtenerMacetasPorHuerto(nombreGuardado).filter { it.nombre != "Vacío" }
        else listOf()
    }

    var plantaSeleccionada by remember { mutableStateOf<PlantaModelo?>(null) }

    var q1Crecimiento by remember { mutableStateOf("") }
    var q2Marchitamiento by remember { mutableStateOf("") }
    var q3Color by remember { mutableStateOf("") }
    var q4Tacto by remember { mutableStateOf("") }
    var q5Costra by remember { mutableStateOf("") }
    var q6Moho by remember { mutableStateOf("") }

    var resultadoDiagnostico by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFE8F5E9),
                            Color(0xFFFFFFFF)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- CABECERA ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Evaluación de Riego",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Huerto actual: $nombreGuardado",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                // BOTÓN DE HISTORIAL ACTUALIZADO (ACCESO LIBRE)
                IconButton(
                    onClick = {
                        if (plantaSeleccionada != null) {
                            prefs.edit()
                                .putInt("planta_historial_id", plantaSeleccionada!!.id)
                                .putString("planta_historial_nombre", plantaSeleccionada!!.nombre)
                                .apply()
                        } else {
                            prefs.edit().putInt("planta_historial_id", -1).apply()
                        }

                        onVerHistorialClick() // Viaja a la nueva pantalla SIN RESTRICCIONES
                    },
                    modifier = Modifier.background(Color(0xFFE8F5E9), shape = CircleShape)
                ) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.List,
                        contentDescription = "Historial",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- SECCIÓN 1: SELECCIÓN DE PLANTA ---
            Text(
                text = "1. Selecciona la planta a evaluar:",
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (plantasGuardadas.isEmpty()) {
                Text("No hay plantas registradas en este huerto.", color = Color.Gray, fontSize = 14.sp)
            } else {
                // Dividimos la lista automáticamente en grupos de 3 para crear filas hacia abajo
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
                                        resultadoDiagnostico = "" // Limpia si cambia
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (esSeleccionada) MaterialTheme.colorScheme.primary else Color(0xFFF5F5F5)
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1f) // Para que tengan el mismo ancho
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
                            // Truco UX: Rellenar espacios vacíos si la fila tiene menos de 3 plantas
                            repeat(3 - filaPlantas.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- SECCIÓN 2: CUESTIONARIO ---
            Text(
                text = "2. Cuestionario de Estado:",
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    Text("¿Se evidencia la aparición de hojas nuevas o alargamiento del tallo?", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = q1Crecimiento == "Sí", onClick = { q1Crecimiento = "Sí" }, colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary))
                        Text("Sí", fontSize = 14.sp, color = Color.DarkGray)
                        Spacer(modifier = Modifier.width(24.dp))
                        RadioButton(selected = q1Crecimiento == "No", onClick = { q1Crecimiento = "No" }, colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary))
                        Text("No", fontSize = 14.sp, color = Color.DarkGray)
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("¿Observa pérdida de firmeza o marchitamiento durante el día?", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = q2Marchitamiento == "Sí", onClick = { q2Marchitamiento = "Sí" }, colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary))
                        Text("Sí", fontSize = 14.sp, color = Color.DarkGray)
                        Spacer(modifier = Modifier.width(24.dp))
                        RadioButton(selected = q2Marchitamiento == "No", onClick = { q2Marchitamiento = "No" }, colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary))
                        Text("No", fontSize = 14.sp, color = Color.DarkGray)
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("¿Las hojas presentan un color amarillento inusual o bordes secos?", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = q3Color == "Sí", onClick = { q3Color = "Sí" }, colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary))
                        Text("Sí", fontSize = 14.sp, color = Color.DarkGray)
                        Spacer(modifier = Modifier.width(24.dp))
                        RadioButton(selected = q3Color == "No", onClick = { q3Color = "No" }, colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary))
                        Text("No", fontSize = 14.sp, color = Color.DarkGray)
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Divider(color = Color(0xFFF5F5F5), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Al tacto (2 cm), ¿la tierra se siente anormalmente seca o empapada?", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = q4Tacto == "Sí", onClick = { q4Tacto = "Sí" }, colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary))
                        Text("Sí", fontSize = 14.sp, color = Color.DarkGray)
                        Spacer(modifier = Modifier.width(24.dp))
                        RadioButton(selected = q4Tacto == "No (Normal)", onClick = { q4Tacto = "No (Normal)" }, colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary))
                        Text("No (Normal)", fontSize = 14.sp, color = Color.DarkGray)
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("¿La superficie de la tierra presenta una costra dura o grietas?", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = q5Costra == "Sí", onClick = { q5Costra = "Sí" }, colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary))
                        Text("Sí", fontSize = 14.sp, color = Color.DarkGray)
                        Spacer(modifier = Modifier.width(24.dp))
                        RadioButton(selected = q5Costra == "No", onClick = { q5Costra = "No" }, colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary))
                        Text("No", fontSize = 14.sp, color = Color.DarkGray)
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("¿Observa coloración verdosa (algas) o moho en la tierra?", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = q6Moho == "Sí", onClick = { q6Moho = "Sí" }, colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary))
                        Text("Sí", fontSize = 14.sp, color = Color.DarkGray)
                        Spacer(modifier = Modifier.width(24.dp))
                        RadioButton(selected = q6Moho == "No", onClick = { q6Moho = "No" }, colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary))
                        Text("No", fontSize = 14.sp, color = Color.DarkGray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- SECCIÓN 3: RESULTADOS Y GUARDADO ---
            Button(
                onClick = {
                    if (plantaSeleccionada == null) {
                        Toast.makeText(context, "Selecciona una planta primero", Toast.LENGTH_SHORT).show()
                    } else if (q1Crecimiento.isEmpty() || q2Marchitamiento.isEmpty() || q3Color.isEmpty() || q4Tacto.isEmpty() || q5Costra.isEmpty() || q6Moho.isEmpty()) {
                        Toast.makeText(context, "Responde todas las preguntas para el diagnóstico", Toast.LENGTH_SHORT).show()
                    } else {
                        val plantaSana = q1Crecimiento == "Sí" && q2Marchitamiento == "No" && q3Color == "No"
                        val sueloOk = q4Tacto == "No (Normal)" && q5Costra == "No" && q6Moho == "No"

                        resultadoDiagnostico = when {
                            plantaSana && sueloOk -> "SISTEMA EFICIENTE:\nLa telemetría coincide con el desarrollo vigoroso. El algoritmo de riego automático opera con parámetros óptimos."
                            plantaSana && q5Costra == "Sí" -> "ACCIÓN PREVENTIVA (Aireación):\nLa planta y el riego están bien, pero la tierra empieza a compactarse. Remueva ligeramente la superficie para prevenir futuros problemas de infiltración."
                            plantaSana && q6Moho == "Sí" -> "ACCIÓN PREVENTIVA (Drenaje):\nLa planta se desarrolla bien, pero hay indicios de moho. Verifique que los agujeros de la maceta no estén tapados para evitar acumulación futura."
                            q2Marchitamiento == "Sí" && (q5Costra == "Sí" || q4Tacto == "Sí") -> "ALERTA CRÍTICA DE SUELO:\nLa planta presenta marchitamiento porque la costra o sequedad extrema impide que el riego automático llegue a las raíces. ¡Remueva la tierra de inmediato!"
                            (q3Color == "Sí" || q2Marchitamiento == "Sí") && q6Moho == "Sí" -> "MEJORA DE VENTILACIÓN (Exceso retenido):\nLa planta presenta síntomas de asfixia (clorosis/moho). El sustrato no está logrando evaporar el agua del riego automático. Reubique la maceta temporalmente a una zona con mayor luz solar y corriente de aire."
                            q1Crecimiento == "No" && q3Color == "Sí" && sueloOk -> "ALERTA DE NUTRIENTES:\nEl suministro de agua y la tierra son correctos, pero la planta no crece y pierde color. Se requiere aplicar ABONO ORGÁNICO."
                            else -> "DIAGNÓSTICO PREVENTIVO:\nEl sistema detecta anomalías leves. Se recomienda limpieza de hojas y verificar la ubicación de la planta respecto a la luz solar."
                        }

                        val historialAnterior = prefs.getString("historial_planta_${plantaSeleccionada!!.id}", "") ?: ""
                        val numSemana = if (historialAnterior.isEmpty()) 1 else historialAnterior.split("||").size + 1

                        val porcentaje = when {
                            resultadoDiagnostico.contains("EFICIENTE") -> 100f
                            resultadoDiagnostico.contains("PREVENTIVA") -> 75f
                            resultadoDiagnostico.contains("PREVENTIVO") -> 50f
                            else -> 25f
                        }

                        val nuevoRegistro = "Semana $numSemana::$porcentaje::$resultadoDiagnostico"
                        val historialActualizado = if (historialAnterior.isEmpty()) nuevoRegistro else "$historialAnterior||$nuevoRegistro"

                        prefs.edit().putString("historial_planta_${plantaSeleccionada!!.id}", historialActualizado).apply()

                        Toast.makeText(context, "Evaluación guardada con éxito: Semana $numSemana", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Procesar y Guardar Evaluación", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            if (resultadoDiagnostico.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))

                val colorFondo = when {
                    resultadoDiagnostico.contains("SISTEMA EFICIENTE") -> Color(0xFFE8F5E9)
                    resultadoDiagnostico.contains("ACCIÓN PREVENTIVA") -> Color(0xFFFFF3E0)
                    resultadoDiagnostico.contains("PREVENTIVO") -> Color(0xFFF5F5F5)
                    else -> Color(0xFFFFEBEE)
                }

                val colorTexto = when {
                    resultadoDiagnostico.contains("SISTEMA EFICIENTE") -> Color(0xFF1B5E20)
                    resultadoDiagnostico.contains("ACCIÓN PREVENTIVA") -> Color(0xFFE65100)
                    resultadoDiagnostico.contains("PREVENTIVO") -> Color(0xFF424242)
                    else -> Color(0xFFC62828)
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = colorFondo),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Text(text = resultadoDiagnostico, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = colorTexto, modifier = Modifier.padding(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            TextButton(onClick = onVolverClick) {
                Text("Volver al Menú Principal", color = Color.Gray, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}