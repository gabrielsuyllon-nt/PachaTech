package com.example.appriego.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.WaterDrop
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
import com.example.appriego.model.TipoPlanta
import com.example.appriego.model.CATALOGO_PLANTAS

@Composable
fun CrearHuertoScreen(onGuardarClick: (List<PlantaModelo>) -> Unit) {
    var tamanoHuerto by remember { mutableIntStateOf(6) }
    var plantasSeleccionadas by remember { mutableStateOf(listOf<PlantaModelo>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Diseña tu Huerto",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF2E7D32)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- 1. SELECCIÓN DE TAMAÑO ---
        Text("1. Elige el número de macetas:", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf(1, 2, 4, 6).forEach { size ->
                Button(
                    onClick = {
                        tamanoHuerto = size
                        plantasSeleccionadas = emptyList()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (tamanoHuerto == size) Color(0xFF2E7D32) else Color.LightGray
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.size(70.dp)
                ) {
                    Text(text = "$size", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- 2. CATÁLOGO DE PLANTAS ---
        Text(
            text = "2. Selecciona tus plantas (${plantasSeleccionadas.size}/$tamanoHuerto):",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            contentPadding = PaddingValues(4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(CATALOGO_PLANTAS) { tipo ->
                Card(
                    modifier = Modifier
                        .clickable {
                            if (plantasSeleccionadas.size < tamanoHuerto) {
                                val nuevoId = plantasSeleccionadas.size + 1
                                val nuevaPlanta = PlantaModelo(nuevoId, tipo.nombre, tipo.nivelAguaIdeal)
                                plantasSeleccionadas = plantasSeleccionadas + nuevaPlanta
                            }
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = obtenerEmojiPlanta(tipo.nombre),
                            fontSize = 28.sp
                        )
                        Text(
                            text = tipo.nombre.substringBefore(" ("),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color.DarkGray,
                            textAlign = TextAlign.Center
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.WaterDrop,
                                contentDescription = null,
                                tint = Color(0xFF1976D2),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "${tipo.nivelAguaIdeal}%",
                                fontSize = 11.sp,
                                color = Color(0xFF1976D2)
                            )
                        }
                    }
                }
            }
        }

        if (plantasSeleccionadas.isNotEmpty()) {
            TextButton(
                onClick = { plantasSeleccionadas = emptyList() },
                colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFD32F2F))
            ) {
                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Limpiar selección")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- 3. VISTA PREVIA DEL HUERTO ---
        Text(
            text = "3. Vista previa del huerto:",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF8D6E63), shape = RoundedCornerShape(12.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            when (tamanoHuerto) {
                1 -> VistaMacetas(listOf(1), plantasSeleccionadas)
                2 -> VistaMacetas(listOf(1, 2), plantasSeleccionadas)
                4 -> VistaMacetas(listOf(1, 2, 3, 4), plantasSeleccionadas)
                6 -> VistaMacetas(listOf(1, 2, 3, 4, 5, 6), plantasSeleccionadas)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { onGuardarClick(plantasSeleccionadas) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = plantasSeleccionadas.size == tamanoHuerto,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
        ) {
            Text("Guardar Huerto", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun VistaMacetas(ids: List<Int>, plantasSeleccionadas: List<PlantaModelo>) {
    val filas = when (ids.size) {
        1 -> listOf(listOf(1))
        2 -> listOf(listOf(1, 2))
        4 -> listOf(listOf(1, 2), listOf(3, 4))
        6 -> listOf(listOf(1, 2, 3), listOf(4, 5, 6))
        else -> listOf(ids)
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        filas.forEach { fila ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                fila.forEach { id ->
                    val planta = plantasSeleccionadas.getOrNull(id - 1)
                    MacetaPreview(id, planta)
                }
            }
        }
    }
}

@Composable
fun MacetaPreview(id: Int, planta: PlantaModelo?) {
    Card(
        modifier = Modifier.size(80.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (planta != null) Color(0xFFA5D6A7) else Color(0xFFD7CCC8)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Maceta $id",
                fontSize = 10.sp,
                color = Color.DarkGray
            )
            if (planta != null) {
                Text(
                    text = obtenerEmojiPlanta(planta.nombre),
                    fontSize = 24.sp
                )
                Text(
                    text = planta.nombre.substringBefore(" "),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            } else {
                Text(
                    text = "🌱",
                    fontSize = 20.sp
                )
                Text("Vacía", fontSize = 10.sp, color = Color.Gray)
            }
        }
    }
}

fun obtenerEmojiPlanta(nombre: String): String {
    return when {
        nombre.contains("Tomate") -> "🍅"
        nombre.contains("Lechuga") -> "🥬"
        nombre.contains("Espinaca") -> "🌿"
        nombre.contains("Cactus") -> "🌵"
        nombre.contains("Romero") -> "🌿"
        nombre.contains("Pimiento") -> "🫑"
        nombre.contains("Fresa") -> "🍓"
        nombre.contains("Albahaca") -> "🌱"
        nombre.contains("Zanahoria") -> "🥕"
        nombre.contains("Papa") -> "🥔"
        else -> "🌱"
    }
}