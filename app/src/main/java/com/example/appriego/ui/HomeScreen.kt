package com.example.appriego.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothSearching
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appriego.data.AppDatabase
import com.example.appriego.model.PlantaModelo

data class TipoPlanta(val nombre: String, val nivelAguaIdeal: Int)

val CATALOGO_PLANTAS = listOf(
    TipoPlanta("Tomate (Mucha Agua)", 80),
    TipoPlanta("Lechuga (Agua Media)", 50),
    TipoPlanta("Espinaca (Agua Media)", 45),
    TipoPlanta("Cactus (Poca Agua)", 15),
    TipoPlanta("Romero (Poca Agua)", 20)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onVincularClick: () -> Unit) {
    val context = LocalContext.current
    val db = remember { AppDatabase(context) }

    val prefs = context.getSharedPreferences("PachaTechPrefs", Context.MODE_PRIVATE)
    val nombreUsuario = remember {
        mutableStateOf(prefs.getString("usuarioNombre", "Agricultor") ?: "Agricultor")
    }

    val nombreGuardado = prefs.getString("nombreHuertoActual", "") ?: ""
    var huertoActual by remember {
        mutableStateOf(
            if (nombreGuardado.isNotEmpty()) {
                db.obtenerMacetasPorHuerto(nombreGuardado)
            } else {
                listOf()
            }
        )
    }
    var nombreHuertoActivo by remember { mutableStateOf(if (nombreGuardado.isNotEmpty()) nombreGuardado else "Sin configurar") }
    var mostrarDialogoNuevo by remember { mutableStateOf(false) }
    var cantidadSeleccionada by remember { mutableStateOf(1) }
    var mostrarDialogoLista by remember { mutableStateOf(false) }
    var macetaSeleccionadaIndice by remember { mutableStateOf<Int?>(null) }
    var mostrarDialogoPlantas by remember { mutableStateOf(false) }
    var mostrarConfirmarBorrar by remember { mutableStateOf(false) }
    var huertoABorrar by remember { mutableStateOf("") }

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
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Bienvenido,", fontSize = 16.sp, color = Color.Gray)
                    Text(nombreUsuario.value, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                }
                Surface(modifier = Modifier.size(50.dp), shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                    Icon(Icons.Default.Eco, contentDescription = null, modifier = Modifier.padding(12.dp), tint = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                AccionCard(
                    titulo = "Nuevo Huerto",
                    icono = Icons.Default.Add,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f),
                    onClick = { mostrarDialogoNuevo = true }
                )
                AccionCard(
                    titulo = "Mis Huertos",
                    icono = Icons.Default.List,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f),
                    onClick = { mostrarDialogoLista = true }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Huerto: $nombreHuertoActivo",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (huertoActual.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Text("Aún no tienes un huerto configurado.\nPresiona 'Nuevo Huerto' para iniciar.", color = Color.Gray, modifier = Modifier.padding(32.dp))
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    itemsIndexed(huertoActual) { indice, planta ->
                        MacetitaCard(
                            planta = planta,
                            indice = indice,
                            totalMacetas = huertoActual.size, // 🌟 Pasamos el tamaño dinámico real
                            onClick = {
                                macetaSeleccionadaIndice = indice
                                mostrarDialogoPlantas = true
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 🌟 CORRECCIÓN EXCELENTE: El botón ahora vive aquí abajo, dentro de la columna segura
            Button(
                onClick = { onVincularClick() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Bluetooth, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sincronizar Bluetooth", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }

    // DIÁLOGO PARA CREAR NUEVO HUERTO
    if (mostrarDialogoNuevo) {
        var nombreNuevoHuerto by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { mostrarDialogoNuevo = false },
            title = { Text("Configurar Nuevo Huerto", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Asigna un nombre a tu huerto:", fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = nombreNuevoHuerto,
                        onValueChange = { nombreNuevoHuerto = it },
                        label = { Text("Ej. Mi Huerto Tech") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text("¿Cuántas plantas vas a monitorear?:", fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        listOf(1, 2, 4, 6).forEach { num ->
                            FilterChip(
                                selected = cantidadSeleccionada == num,
                                onClick = { cantidadSeleccionada = num },
                                label = { Text(num.toString(), fontWeight = FontWeight.Medium) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nombreNuevoHuerto.trim().isNotEmpty()) {
                            val nombreFinalHuerto = nombreNuevoHuerto.trim()
                            val nuevasPlantas = mutableListOf<PlantaModelo>()

                            for (i in 1..cantidadSeleccionada) {
                                nuevasPlantas.add(PlantaModelo(i, "Vacío", 0))
                            }

                            db.guardarHuertoCompleto(nombreFinalHuerto, nuevasPlantas)
                            prefs.edit().putString("nombreHuertoActual", nombreFinalHuerto).apply()

                            huertoActual = db.obtenerMacetasPorHuerto(nombreFinalHuerto)
                            nombreHuertoActivo = nombreFinalHuerto
                            mostrarDialogoNuevo = false
                        } else {
                            Toast.makeText(context, "Por favor, escribe un nombre para el huerto", Toast.LENGTH_SHORT).show()
                        }
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Crear")
                }
            }
        )
    }

    // DIÁLOGO PARA BUSCAR Y CAMBIAR ENTRE HUERTOS
    if (mostrarDialogoLista) {
        val listaHuertos = db.obtenerListaDeHuertosUnicos()

        AlertDialog(
            onDismissRequest = { mostrarDialogoLista = false },
            title = { Text("Selecciona tu Huerto", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (listaHuertos.isEmpty()) {
                        Text("No tienes huertos registrados aún.", color = Color.Gray)
                    } else {
                        Text("Elige un huerto o elimina los que ya no uses:", fontSize = 14.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(12.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            listaHuertos.forEach { nombreHuerto ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            huertoActual = db.obtenerMacetasPorHuerto(nombreHuerto)
                                            prefs.edit().putString("nombreHuertoActual", nombreHuerto).apply()
                                            nombreHuertoActivo = nombreHuerto
                                            Toast.makeText(context, "Cambiado a: $nombreHuerto", Toast.LENGTH_SHORT).show()
                                            mostrarDialogoLista = false
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.Start,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.Eco, contentDescription = null, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(nombreHuerto, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                        }
                                    }

                                    IconButton(
                                        onClick = {
                                            huertoABorrar = nombreHuerto
                                            mostrarDialogoLista = false
                                            mostrarConfirmarBorrar = true
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Eliminar Huerto",
                                            tint = Color(0xFFD32F2F)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { mostrarDialogoLista = false }) { Text("Cerrar") }
            }
        )
    }

    if (mostrarConfirmarBorrar) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmarBorrar = false },
            title = { Text("¿Eliminar huerto?", fontWeight = FontWeight.Bold) },
            text = { Text("¿Estás seguro de que deseas eliminar el huerto \"$huertoABorrar\"? Esta acción borrará todas sus macetas y plantas.") },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    onClick = {
                        db.eliminarHuertoPorNombre(huertoABorrar)
                        Toast.makeText(context, "Huerto eliminado correctamente", Toast.LENGTH_SHORT).show()

                        if (nombreHuertoActivo == huertoABorrar) {
                            prefs.edit().putString("nombreHuertoActual", "").apply()
                            nombreHuertoActivo = "Sin configurar"
                            huertoActual = listOf()
                        }

                        mostrarConfirmarBorrar = false
                        mostrarDialogoLista = false
                    }
                ) {
                    Text("Confirmar", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarConfirmarBorrar = false
                        mostrarDialogoLista = true
                    }
                ) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        )
    }

    // ASIGNAR PLANTA Y AUTO-ORDENAR POR PRIORIDAD DE BOMBA FÍSICA
    if (mostrarDialogoPlantas && macetaSeleccionadaIndice != null) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoPlantas = false },
            title = { Text("Sembrar Planta", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Selecciona una planta. Se ordenarán automáticamente por cercanía a la bomba física del centro.", fontSize = 13.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(12.dp))

                    CATALOGO_PLANTAS.forEach { tipo ->
                        TextButton(
                            onClick = {
                                val nombreHuertoActivo = prefs.getString("nombreHuertoActual", "") ?: ""
                                val listaModificada = huertoActual.toMutableList()

                                listaModificada[macetaSeleccionadaIndice!!] = PlantaModelo(
                                    id = listaModificada[macetaSeleccionadaIndice!!].id,
                                    nombre = tipo.nombre,
                                    nivelAgua = tipo.nivelAguaIdeal
                                )

                                val listaOrdenada = organizarPlantasPorBomba(listaModificada)

                                if (nombreHuertoActivo.isNotEmpty()) {
                                    db.guardarHuertoCompleto(nombreHuertoActivo, listaOrdenada)
                                    huertoActual = db.obtenerMacetasPorHuerto(nombreHuertoActivo)
                                }

                                mostrarDialogoPlantas = false
                                Toast.makeText(context, "¡Posiciones optimizadas en base a la bomba!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = tipo.nombre,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = when {
                                            tipo.nivelAguaIdeal >= 75 -> "Mucha agua"
                                            tipo.nivelAguaIdeal >= 50 -> "Agua moderada"
                                            else -> "Poca agua"
                                        },
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Normal
                                    )
                                }

                                Text(
                                    text = "${tipo.nivelAguaIdeal}% H2O",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { mostrarDialogoPlantas = false }) { Text("Cancelar") }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccionCard(titulo: String, icono: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icono, contentDescription = null, tint = color)
            Text(titulo, color = color, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}

// CORREGIDO: Ahora recibe el total de macetas dinámicamente para pintar el centro exacto
@Composable
fun MacetitaCard(planta: PlantaModelo, indice: Int, totalMacetas: Int, onClick: () -> Unit) {
    val esCentro = (totalMacetas == 4 && (indice == 1 || indice == 2)) ||
            (totalMacetas == 6 && (indice == 2 || indice == 3))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (planta.nombre == "Vacío") Color(0xFFF5F5F5) else Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {

            Icon(
                imageVector = Icons.Default.LocalFlorist,
                contentDescription = null,
                modifier = Modifier.size(40.dp).padding(bottom = 8.dp),
                tint = if (planta.nombre == "Vacío") Color.LightGray else MaterialTheme.colorScheme.primary
            )

            Text("Macetita ${indice + 1}", fontSize = 12.sp, color = Color.Gray)
            Text(planta.nombre, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.DarkGray)
            Text("Humedad req: ${planta.nivelAgua}%", fontSize = 12.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = planta.nivelAgua.toFloat() / 100f,
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                color = MaterialTheme.colorScheme.primary,
                trackColor = Color.LightGray
            )

            if (esCentro && planta.nombre != "Vacío") {
                Spacer(modifier = Modifier.height(4.dp))
                Text("💧 Centro (Bomba)", fontSize = 11.sp, color = Color(0xFF1976D2), fontWeight = FontWeight.Bold)
            }
        }
    }
}

fun organizarPlantasPorBomba(plantas: List<PlantaModelo>): List<PlantaModelo> {
    if (plantas.isEmpty()) return plantas

    val ordenadasPorAgua = plantas.sortedByDescending { it.nivelAgua }
    val macetasOrdenadas = MutableList(plantas.size) { PlantaModelo(0, "Vacío", 0) }

    val mapaPosiciones = when (plantas.size) {
        4 -> listOf(2, 1, 0, 3)
        6 -> listOf(3, 2, 4, 1, 5, 0)
        else -> plantas.indices.toList()
    }

    for (i in ordenadasPorAgua.indices) {
        if (i < mapaPosiciones.size) {
            val posicionFisica = mapaPosiciones[i]
            macetasOrdenadas[posicionFisica] = ordenadasPorAgua[i]
        }
    }

    return macetasOrdenadas
}