package com.example.appriego.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appriego.data.AppDatabase
import com.example.appriego.model.PlantaModelo

@Composable
fun BitacoraScreen(onVolverClick: () -> Unit) {
    val context = LocalContext.current
    val db = remember { AppDatabase(context) }

    // 1. SOLUCIÓN: Recuperamos cuál es el huerto que está activo actualmente
    val prefs = context.getSharedPreferences("PachaTechPrefs", Context.MODE_PRIVATE)
    val nombreHuertoActivo = prefs.getString("nombreHuertoActual", "") ?: ""

    // 2. SOLUCIÓN: Jalamos solo las plantas que pertenecen a ese huerto activo y filtramos las que no estén "Vacías"
    val plantasGuardadas = remember(nombreHuertoActivo) {
        if (nombreHuertoActivo.isNotEmpty()) {
            db.obtenerMacetasPorHuerto(nombreHuertoActivo).filter { it.nombre != "Vacío" }
        } else {
            listOf()
        }
    }

    var plantaSeleccionada by remember { mutableStateOf<PlantaModelo?>(null) }

    var crecimiento by remember { mutableStateOf("") }
    var hojasAmarillas by remember { mutableStateOf("") }
    var tierraSeca by remember { mutableStateOf("") }
    var resultadoDiagnostico by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Evaluación de Riego",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1B5E20)
        )

        // Muestra una pequeña etiqueta con el huerto que se está evaluando
        if (nombreHuertoActivo.isNotEmpty()) {
            Text(
                text = "Huerto actual: $nombreHuertoActivo",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "1. Selecciona la planta a evaluar:",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (plantasGuardadas.isEmpty()) {
            // Si no hay plantas sembradas en este huerto
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay plantas sembradas en este huerto para evaluar.", fontSize = 12.sp, color = Color.Gray)
            }
        } else {
            // Fila de botones para elegir planta
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                plantasGuardadas.forEach { planta ->
                    val esSeleccionada = plantaSeleccionada?.id == planta.id
                    Button(
                        onClick = {
                            plantaSeleccionada = planta
                            // Limpiamos el diagnóstico anterior al cambiar de planta
                            resultadoDiagnostico = ""
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (esSeleccionada) Color(0xFF2E7D32) else Color.LightGray
                        ),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                    ) {
                        // Cortamos el texto largo por si acaso para que quepa en el botón
                        Text(
                            text = planta.nombre.substringBefore(" ("),
                            fontSize = 10.sp,
                            color = if (esSeleccionada) Color.White else Color.Black
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "2. Cuestionario de Estado:",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("¿Ha notado algún crecimiento en los últimos días?", fontSize = 14.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = crecimiento == "Si", onClick = { crecimiento = "Si" })
                        Text("Sí")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = crecimiento == "No", onClick = { crecimiento = "No" })
                        Text("No")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("¿Las hojas presentan manchas o color amarillento?", fontSize = 14.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = hojasAmarillas == "Si", onClick = { hojasAmarillas = "Si" })
                        Text("Sí")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = hojasAmarillas == "No", onClick = { hojasAmarillas = "No" })
                        Text("No")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("¿La tierra se siente excesivamente seca al tacto?", fontSize = 14.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = tierraSeca == "Si", onClick = { tierraSeca = "Si" })
                        Text("Sí")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = tierraSeca == "No", onClick = { tierraSeca = "No" })
                        Text("No")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (plantaSeleccionada == null) {
                    Toast.makeText(context, "Selecciona una planta primero", Toast.LENGTH_SHORT).show()
                } else if (crecimiento.isEmpty() || hojasAmarillas.isEmpty() || tierraSeca.isEmpty()) {
                    Toast.makeText(context, "Responde todas las preguntas", Toast.LENGTH_SHORT).show()
                } else {
                    resultadoDiagnostico = if (crecimiento == "Si" && hojasAmarillas == "No" && tierraSeca == "No") {
                        "Análisis: El sistema de riego está funcionando de manera óptima para ${plantaSeleccionada?.nombre}. Siga con la configuración actual."
                    } else if (tierraSeca == "Si") {
                        "Análisis: El suministro hídrico es insuficiente para ${plantaSeleccionada?.nombre}. Se recomienda incrementar el tiempo de apertura de la electroválvula en el circuito físico."
                    } else if (hojasAmarillas == "Si") {
                        "Análisis: Alerta de posible exceso de humedad en ${plantaSeleccionada?.nombre}. Reduzca la frecuencia de riego en el controlador."
                    } else {
                        "Análisis: Datos registrados para ${plantaSeleccionada?.nombre}. Monitoree el comportamiento los próximos dos días para evaluar la evolución."
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20))
        ) {
            Text("Procesar y Analizar Datos")
        }

        if (resultadoDiagnostico.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = resultadoDiagnostico,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1B5E20),
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(onClick = onVolverClick) {
            Text("Volver al Panel de Control", color = Color.Gray)
        }
    }
}