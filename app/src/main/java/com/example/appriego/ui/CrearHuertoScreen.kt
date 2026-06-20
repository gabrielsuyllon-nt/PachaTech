package com.example.appriego.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appriego.model.PlantaModelo

@Composable
fun CrearHuertoScreen(onGuardarClick: (List<PlantaModelo>) -> Unit) {
    val catalogo = listOf(
        PlantaModelo(1, "Tomate", 3),
        PlantaModelo(2, "Lechuga", 3),
        PlantaModelo(3, "Menta", 3),
        PlantaModelo(4, "Zanahoria", 2),
        PlantaModelo(5, "Papa", 2),
        PlantaModelo(6, "Romero", 1),
        PlantaModelo(7, "Cactus", 1)
    )

    // Variables de estado
    var tamanoHuerto by remember { mutableIntStateOf(6) }
    var plantasSeleccionadas by remember { mutableStateOf(listOf<PlantaModelo>()) }

    // Lógica del algoritmo de acomodo
    val plantasOrdenadas = plantasSeleccionadas.sortedByDescending { it.nivelAgua }
    val distribucion = MutableList<PlantaModelo?>(tamanoHuerto) { null }

    // Orden de prioridad dependiendo del tamaño (El centro primero, luego bordes)
    val ordenPrioridad = when (tamanoHuerto) {
        1 -> listOf(0)
        2 -> listOf(0, 1)
        4 -> listOf(1, 2, 0, 3)
        6 -> listOf(1, 4, 0, 3, 2, 5)
        else -> listOf(0)
    }

    // Acomodamos las plantas en la distribución final
    plantasOrdenadas.forEachIndexed { index, planta ->
        if (index < tamanoHuerto) {
            distribucion[ordenPrioridad[index]] = planta
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Configuración de Huerto",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // 1. SELECTOR DE TAMAÑO
        Text("1. Elige el tamaño de tu zona de riego:", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf(1, 2, 4, 6).forEach { size ->
                Button(
                    onClick = {
                        tamanoHuerto = size
                        plantasSeleccionadas = emptyList() // Limpiamos si cambia de tamaño
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (tamanoHuerto == size) Color(0xFF2E7D32) else Color.LightGray
                    )
                ) {
                    Text("$size", color = if (tamanoHuerto == size) Color.White else Color.Black)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 2. CATÁLOGO DE PLANTAS
        Text(
            text = "2. Agrega tus plantas (${plantasSeleccionadas.size}/$tamanoHuerto)",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                catalogo.take(3).forEach { planta ->
                    Button(
                        onClick = {
                            if (plantasSeleccionadas.size < tamanoHuerto) {
                                plantasSeleccionadas = plantasSeleccionadas + planta
                            }
                        },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(text = planta.nombre, fontSize = 11.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                catalogo.drop(3).forEach { planta ->
                    Button(
                        onClick = {
                            if (plantasSeleccionadas.size < tamanoHuerto) {
                                plantasSeleccionadas = plantasSeleccionadas + planta
                            }
                        },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(text = planta.nombre, fontSize = 11.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para limpiar si se equivoca
        Button(
            onClick = { plantasSeleccionadas = emptyList() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
            modifier = Modifier.height(36.dp)
        ) {
            Text("Limpiar Tierra", fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. DIBUJO DINÁMICO DEL HUERTO
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF8D6E63), shape = RoundedCornerShape(12.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                when (tamanoHuerto) {
                    1 -> {
                        Row { CasillaTierra(distribucion[0]) }
                    }
                    2 -> {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            CasillaTierra(distribucion[0])
                            CasillaTierra(distribucion[1])
                        }
                    }
                    4 -> {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            CasillaTierra(distribucion[0])
                            CasillaTierra(distribucion[1])
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            CasillaTierra(distribucion[2])
                            CasillaTierra(distribucion[3])
                        }
                    }
                    6 -> {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            CasillaTierra(distribucion[0])
                            CasillaTierra(distribucion[1])
                            CasillaTierra(distribucion[2])
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            CasillaTierra(distribucion[3])
                            CasillaTierra(distribucion[4])
                            CasillaTierra(distribucion[5])
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // 4. GUARDAR
        Button(
            onClick = { onGuardarClick(plantasSeleccionadas) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = plantasSeleccionadas.isNotEmpty(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
        ) {
            Text(text = "Finalizar y Guardar Huerto", fontSize = 16.sp)
        }
    }
}

@Composable
fun CasillaTierra(planta: PlantaModelo?) {
    Card(
        modifier = Modifier
            .size(90.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF5D4037)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = planta != null,
                enter = fadeIn(animationSpec = tween(durationMillis = 800))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = planta?.nombre ?: "",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
            if (planta == null) {
                Text(text = "Tierra\nLibre", color = Color.LightGray, fontSize = 12.sp, textAlign = TextAlign.Center)
            }
        }
    }
}