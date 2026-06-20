package com.example.appriego.ui

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Eco
import android.widget.Toast
import androidx.compose.material.icons.filled.LocalFlorist // Icono de una flor por si lo quieres para las macetitas
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appriego.R
import com.example.appriego.data.AppDatabase
import com.example.appriego.model.PlantaModelo
import androidx.compose.material.icons.filled.List

@Composable
fun HomeScreen(onVincularClick: () -> Unit) {
    val context = LocalContext.current
    val db = remember { AppDatabase(context) }

    // Recuperar el nombre del usuario (Simulado o desde SharedPreferences)
    val prefs = context.getSharedPreferences("PachaTechPrefs", Context.MODE_PRIVATE)
    val nombreUsuario = remember {
        mutableStateOf(prefs.getString("usuarioNombre", "Agricultor") ?: "Agricultor")
    } // Aquí podrías jalar el nombre real de la DB

    // ESTADOS CLAVE
    // REEMPLÁZALA POR ESTA:
    val nombreGuardado = prefs.getString("nombreHuertoActual", "") ?: ""
    var huertoActual by remember {
        mutableStateOf(
            if (nombreGuardado.isNotEmpty()) {
                db.obtenerHuerto().filter { it.nombre.startsWith(nombreGuardado) }
            } else {
                db.obtenerHuerto() // Si es la primera vez, trae lo que haya
            }
        )
    }
    var mostrarDialogoNuevo by remember { mutableStateOf(false) }
    var cantidadSeleccionada by remember { mutableStateOf(1) }
    var mostrarDialogoLista by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo con degradado sutil
        Box(modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f), Color.White))
        ))

        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {

            // SUPERIOR IZQUIERDA: BIENVENIDA
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Bienvenido,", fontSize = 16.sp, color = Color.Gray)
                    Text(nombreUsuario.value, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                }
                // Icono de perfil o logo pequeño
                Surface(modifier = Modifier.size(50.dp), shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                    Icon(Icons.Default.Eco, contentDescription = null, modifier = Modifier.padding(12.dp), tint = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // OPCIONES PRINCIPALES: NUEVO Y CAMBIAR
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                // Tarjeta Nuevo Huerto
                AccionCard(
                    titulo = "Nuevo Huerto",
                    icono = Icons.Default.Add,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f),
                    onClick = { mostrarDialogoNuevo = true }
                )
                // Tarjeta Cambiar Huerto
                AccionCard(
                    titulo = "Mis Huertos",
                    icono = Icons.Default.List, // Icono de lista para simular "Buscar tus huertos"
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f),
                    onClick = { mostrarDialogoLista = true } // Abre la lista de huertos
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // CUERPO CENTRAL: EL HUERTO
            Text("Estado de tu Huerto", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(16.dp))

            if (huertoActual.isEmpty()) {
                // MENSAJE CUANDO NO HAY NADA
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Text("Aún no tienes un huerto configurado.\nPresiona 'Nuevo Huerto' para iniciar.", color = Color.Gray, modifier = Modifier.padding(32.dp))
                }
            } else {
                // GRILLA DINÁMICA DE MACETITAS
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(huertoActual) { planta ->
                        MacetitaCard(planta)
                    }
                }
            }
        }

        // BOTÓN DE VINCULACIÓN FLOTANTE (O abajo)
        Button(
            onClick = onVincularClick,
            modifier = Modifier.align(Alignment.BottomCenter).padding(24.dp).fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Sincronizar Bluetooth", fontWeight = FontWeight.Bold)
        }
    }


    // DIÁLOGO PARA CREAR NUEVO HUERTO
    if (mostrarDialogoNuevo) {
        // Estado local para capturar el texto que escribe el usuario
        var nombreNuevoHuerto by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { mostrarDialogoNuevo = false },
            title = { Text("Configurar Nuevo Huerto", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Asigna un nombre a tu huerto:", fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))

                    // CAMPO DE TEXTO PARA EL NOMBRE
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

                    // SELECTOR DE CANTIDAD (1, 2, 4, 6)
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
                                nuevasPlantas.add(PlantaModelo(i, "$nombreFinalHuerto - P$i", 0))
                            }

                            // 1. Guardamos en la base de datos
                            db.guardarHuerto(nuevasPlantas)

                            // 2. Guardamos el nombre en sesión
                            prefs.edit().putString("nombreHuertoActual", nombreFinalHuerto).apply()

                            // 3. LA SOLUCIÓN: Creamos una lista NUEVA desde cero para obligar a Compose a redibujar
                            val todasLasPlantas = db.obtenerHuerto()
                            val listaFiltrada = todasLasPlantas.filter { it.nombre.startsWith(nombreFinalHuerto) }

                            // Al asignarle una lista completamente nueva empaquetada, la UI reacciona sí o sí
                            huertoActual = listOf() // Limpiamos un milisegundo
                            huertoActual = listaFiltrada // Le pasamos los datos reales

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
        // 1. Obtenemos todas las plantas y las agrupamos por el nombre del huerto para sacar la lista de huertos únicos
        val todasLasPlantas = db.obtenerHuerto() // Trae la lista completa de SQLite

        // Extraemos los nombres de los huertos (asumiendo que guardamos el nombre en el campo de la planta "NombreHuerto - P1")
        // Separamos por el guion "-" para obtener solo el nombre principal del huerto
        val listaHuertos = todasLasPlantas.map { planta ->
            planta.nombre.substringBefore(" - ")
        }.distinct() // .distinct() elimina los duplicados para que salga un nombre por huerto

        AlertDialog(
            onDismissRequest = { mostrarDialogoLista = false },
            title = { Text("Selecciona tu Huerto", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (listaHuertos.isEmpty()) {
                        Text("No tienes huertos registrados aún.", color = Color.Gray)
                    } else {
                        Text("Elige el huerto que deseas monitorear:", fontSize = 14.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Lista de los huertos encontrados
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            listaHuertos.forEach { nombreHuerto ->
                                OutlinedButton(
                                    onClick = {
                                        // Al seleccionar un huerto, filtramos las plantas que le pertenecen
                                        huertoActual = todasLasPlantas.filter { it.nombre.startsWith(nombreHuerto) }

                                        // Guardamos en sesión cuál es el huerto activo que se está viendo
                                        prefs.edit().putString("nombreHuertoActual", nombreHuerto).apply()

                                        Toast.makeText(context, "Cambiado a: $nombreHuerto", Toast.LENGTH_SHORT).show()
                                        mostrarDialogoLista = false // Cerramos el diálogo
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Start,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Eco, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(nombreHuerto, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { mostrarDialogoLista = false }) {
                    Text("Cerrar")
                }
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

@Composable
fun MacetitaCard(planta: PlantaModelo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {

            // CAMBIO: Quitamos la imagen que daba error y ponemos el icono de flor de la librería extendida
            Icon(
                imageVector = Icons.Default.LocalFlorist,
                contentDescription = null,
                modifier = Modifier.size(40.dp).padding(bottom = 8.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Text(planta.nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("Humedad: ${planta.nivelAgua}%", fontSize = 12.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(8.dp))

            // BARRA DE NIVEL DE AGUA
            LinearProgressIndicator(
                progress = planta.nivelAgua.toFloat() / 100f,
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                color = MaterialTheme.colorScheme.primary,
                trackColor = Color.LightGray
            )
        }
    }
}