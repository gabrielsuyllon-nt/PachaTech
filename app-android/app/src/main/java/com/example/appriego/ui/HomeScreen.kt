package com.example.appriego.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appriego.data.AppDatabase
import com.example.appriego.model.PlantaModelo
import com.example.appriego.model.CATALOGO_PLANTAS

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    bluetoothViewModel: BluetoothViewModel = viewModel(),
    onVincularClick: () -> Unit,
    onBitacoraClick: () -> Unit,
    onLogout: () -> Unit,
    onAddAccount: () -> Unit
) {
    val context = LocalContext.current
    val db = remember { AppDatabase(context) }

    val prefs = context.getSharedPreferences("PachaTechPrefs", Context.MODE_PRIVATE)
    val nombreUsuario = remember {
        mutableStateOf(prefs.getString("usuarioNombre", "Agricultor") ?: "Agricultor")
    }

    val nombreGuardado = prefs.getString("nombreHuertoActual", "") ?: ""
    var huertoActual by remember {
        mutableStateOf(if (nombreGuardado.isNotEmpty()) db.obtenerMacetasPorHuerto(nombreGuardado) else listOf())
    }
    var nombreHuertoActivo by remember { mutableStateOf(if (nombreGuardado.isNotEmpty()) nombreGuardado else "Sin configurar") }
    var mostrarDialogoNuevo by remember { mutableStateOf(false) }
    var cantidadSeleccionada by remember { mutableStateOf(1) }
    var mostrarDialogoLista by remember { mutableStateOf(false) }
    var macetaSeleccionadaIndice by remember { mutableStateOf<Int?>(null) }
    var mostrarDialogoPlantas by remember { mutableStateOf(false) }
    var mostrarConfirmarBorrar by remember { mutableStateOf(false) }
    var huertoABorrar by remember { mutableStateOf("") }

    // COLORES FUERTES PARA ASEGURAR CONTRASTE
    val verdeTexto = Color(0xFF1B5E20)
    val negroTexto = Color(0xFF212121)

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color(0xFFE8F5E9), Color(0xFFFFFFFF))))
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
                    Text("Bienvenido,", fontSize = 16.sp, color = Color.DarkGray)
                    Text(nombreUsuario.value, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = verdeTexto)
                }

                var menuExpandido by remember { mutableStateOf(false) }
                Box {
                    Surface(
                        modifier = Modifier
                            .size(50.dp)
                            .clickable { menuExpandido = true },
                        shape = CircleShape,
                        color = Color(0xFFC8E6C9)
                    ) {
                        Icon(
                            painter = painterResource(id = com.example.appriego.R.drawable.ic_user),
                            contentDescription = "Opciones",
                            modifier = Modifier.padding(12.dp),
                            tint = verdeTexto
                        )
                    }

                    DropdownMenu(expanded = menuExpandido, onDismissRequest = { menuExpandido = false }, modifier = Modifier.background(Color.White)) {
                        DropdownMenuItem(text = { Text("Añadir otra cuenta", color = negroTexto) }, onClick = { menuExpandido = false; onAddAccount() })
                        DropdownMenuItem(text = { Text("Cerrar sesión", color = negroTexto) }, onClick = { menuExpandido = false; onLogout() })
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                AccionCard("Nuevo Huerto", Icons.Default.Add, verdeTexto, Modifier.weight(1f)) { mostrarDialogoNuevo = true }
                AccionCard("Mis Huertos", Icons.Default.List, Color(0xFF0D47A1), Modifier.weight(1f)) { mostrarDialogoLista = true }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text("Huerto: $nombreHuertoActivo", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = verdeTexto)
            Spacer(modifier = Modifier.height(16.dp))

            if (huertoActual.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Text("Aún no tienes un huerto configurado.\nPresiona 'Nuevo Huerto' para iniciar.", color = Color.DarkGray, modifier = Modifier.padding(32.dp))
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
                        MacetitaCard(planta, indice, huertoActual.size) {
                            macetaSeleccionadaIndice = indice
                            mostrarDialogoPlantas = true
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = onBitacoraClick,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.List, contentDescription = null, tint = verdeTexto)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Abrir Bitácora del Huerto", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = verdeTexto)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {
                    val plantasNombres = huertoActual.map { it.nombre }.filter { it != "Vacío" }
                    bluetoothViewModel.actualizarPlantasDelHuerto(plantasNombres)
                    onVincularClick()
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = verdeTexto)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Bluetooth, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sincronizar Bluetooth", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                }
            }
        }
    }

    // --- 🪄 DIÁLOGOS ANIMADOS Y MODERNOS ---

    // 1. DIÁLOGO CREAR HUERTO
    if (mostrarDialogoNuevo) {
        var nombreNuevoHuerto by remember { mutableStateOf("") }
        DialogoAnimado(onDismiss = { mostrarDialogoNuevo = false }) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Configurar Nuevo Huerto", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = verdeTexto)
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = nombreNuevoHuerto,
                    onValueChange = { nombreNuevoHuerto = it },
                    label = { Text("Ej. Mi Huerto Tech", color = Color.DarkGray) },
                    textStyle = LocalTextStyle.current.copy(color = negroTexto),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(20.dp))

                Text("¿Cuántas plantas vas a monitorear?", fontSize = 14.sp, color = Color.DarkGray)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    listOf(1, 2, 4, 6).forEach { num ->
                        FilterChip(
                            selected = cantidadSeleccionada == num,
                            onClick = { cantidadSeleccionada = num },
                            label = { Text(num.toString(), fontWeight = FontWeight.Bold, color = negroTexto) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = { mostrarDialogoNuevo = false }) { Text("Cancelar", color = Color.DarkGray) }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = verdeTexto),
                        onClick = {
                            if (nombreNuevoHuerto.trim().isNotEmpty()) {
                                val nombreFinalHuerto = nombreNuevoHuerto.trim()
                                val nuevasPlantas = mutableListOf<PlantaModelo>()
                                for (i in 1..cantidadSeleccionada) nuevasPlantas.add(PlantaModelo(i, "Vacío", 0))

                                db.guardarHuertoCompleto(nombreFinalHuerto, nuevasPlantas)
                                prefs.edit().putString("nombreHuertoActual", nombreFinalHuerto).apply()
                                huertoActual = db.obtenerMacetasPorHuerto(nombreFinalHuerto)
                                nombreHuertoActivo = nombreFinalHuerto
                                mostrarDialogoNuevo = false
                                bluetoothViewModel.notificarCambioHuerto()
                            } else {
                                Toast.makeText(context, "Escribe un nombre", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) { Text("Crear", color = Color.White) }
                }
            }
        }
    }

    // 2. DIÁLOGO MIS HUERTOS (LISTA Y BORRAR)
    if (mostrarDialogoLista) {
        val listaHuertos = db.obtenerListaDeHuertosUnicos()
        DialogoAnimado(onDismiss = { mostrarDialogoLista = false }) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Selecciona tu Huerto", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = verdeTexto)
                Spacer(modifier = Modifier.height(16.dp))

                if (listaHuertos.isEmpty()) {
                    Text("No tienes huertos registrados aún.", color = Color.DarkGray)
                } else {
                    Column(modifier = Modifier.fillMaxWidth().heightIn(max = 300.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        listaHuertos.forEach { nombreHuerto ->
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                OutlinedButton(
                                    onClick = {
                                        huertoActual = db.obtenerMacetasPorHuerto(nombreHuerto)
                                        prefs.edit().putString("nombreHuertoActual", nombreHuerto).apply()
                                        nombreHuertoActivo = nombreHuerto
                                        mostrarDialogoLista = false
                                        bluetoothViewModel.notificarCambioHuerto()
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Eco, contentDescription = null, modifier = Modifier.size(18.dp), tint = verdeTexto)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(nombreHuerto, fontWeight = FontWeight.Bold, color = negroTexto)
                                }
                                IconButton(onClick = { huertoABorrar = nombreHuerto; mostrarDialogoLista = false; mostrarConfirmarBorrar = true }) {
                                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFD32F2F))
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(onClick = { mostrarDialogoLista = false }, modifier = Modifier.align(Alignment.End)) { Text("Cerrar", color = Color.DarkGray) }
            }
        }
    }

    // 3. DIÁLOGO ELIMINAR HUERTO
    if (mostrarConfirmarBorrar) {
        DialogoAnimado(onDismiss = { mostrarConfirmarBorrar = false; mostrarDialogoLista = true }) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("¿Eliminar huerto?", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFD32F2F))
                Spacer(modifier = Modifier.height(16.dp))
                Text("¿Estás seguro de que deseas eliminar el huerto \"$huertoABorrar\"? Esta acción no se puede deshacer.", fontSize = 14.sp, color = negroTexto)
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = { mostrarConfirmarBorrar = false; mostrarDialogoLista = true }) { Text("Cancelar", color = Color.DarkGray) }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                        onClick = {
                            db.eliminarHuertoPorNombre(huertoABorrar)
                            if (nombreHuertoActivo == huertoABorrar) {
                                prefs.edit().putString("nombreHuertoActual", "").apply()
                                nombreHuertoActivo = "Sin configurar"
                                huertoActual = listOf()
                            }
                            mostrarConfirmarBorrar = false
                        }
                    ) { Text("Eliminar", color = Color.White) }
                }
            }
        }
    }

    // 4. DIÁLOGO SELECCIONAR PLANTAS (EL QUE TIENE EL SCROLL MÁGICO)
    if (mostrarDialogoPlantas && macetaSeleccionadaIndice != null) {
        DialogoAnimado(onDismiss = { mostrarDialogoPlantas = false }) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Sembrar Planta", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = verdeTexto)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Selecciona una planta. Se ordenarán automáticamente por cercanía a la bomba física.", fontSize = 13.sp, color = Color.DarkGray)
                Spacer(modifier = Modifier.height(16.dp))

                // ✨ AQUI ESTÁ EL SCROLL SOLUCIONADO ✨
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 350.dp) // Limita el alto para que nunca se desborde de la pantalla
                        .verticalScroll(rememberScrollState()), // Activa el deslizamiento infinito
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CATALOGO_PLANTAS.forEach { tipo ->
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable {
                                val nombreLocal = prefs.getString("nombreHuertoActual", "") ?: ""
                                val listaModificada = huertoActual.toMutableList()
                                listaModificada[macetaSeleccionadaIndice!!] = PlantaModelo(
                                    id = listaModificada[macetaSeleccionadaIndice!!].id,
                                    nombre = tipo.nombre,
                                    nivelAgua = tipo.nivelAguaIdeal
                                )
                                val listaOrdenada = organizarPlantasPorBomba(listaModificada)
                                if (nombreLocal.isNotEmpty()) {
                                    db.guardarHuertoCompleto(nombreLocal, listaOrdenada)
                                    huertoActual = db.obtenerMacetasPorHuerto(nombreLocal)
                                }
                                mostrarDialogoPlantas = false
                                bluetoothViewModel.notificarCambioHuerto()
                            },
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9))
                        ) {
                            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    Text(tipo.nombre, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = negroTexto)
                                    Text(if (tipo.nivelAguaIdeal >= 75) "Mucha agua" else if (tipo.nivelAguaIdeal >= 50) "Agua moderada" else "Poca agua", fontSize = 12.sp, color = Color.DarkGray)
                                }
                                Text("${tipo.nivelAguaIdeal}% H2O", color = verdeTexto, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                TextButton(onClick = { mostrarDialogoPlantas = false }, modifier = Modifier.align(Alignment.End)) { Text("Cancelar", color = Color.DarkGray) }
            }
        }
    }
}

// 🪄 COMPONENTE REUTILIZABLE DE ANIMACIÓN
@Composable
fun DialogoAnimado(onDismiss: () -> Unit, content: @Composable () -> Unit) {
    var animScale by remember { mutableFloatStateOf(0.8f) }
    var animAlpha by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        androidx.compose.animation.core.animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(400, easing = FastOutSlowInEasing)
        ) { value, _ ->
            animScale = 0.8f + (0.2f * value)
            animAlpha = value
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().scale(animScale).alpha(animAlpha),
            shape = RoundedCornerShape(24.dp), // Bordes mucho más redondos y modernos
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(12.dp)
        ) {
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccionCard(titulo: String, icono: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = modifier.height(100.dp), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.15f))) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icono, contentDescription = null, tint = color, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(titulo, color = color, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
        }
    }
}

@Composable
fun MacetitaCard(planta: PlantaModelo, indice: Int, totalMacetas: Int, onClick: () -> Unit) {
    val esCentro = (totalMacetas == 4 && (indice == 1 || indice == 2)) || (totalMacetas == 6 && (indice == 2 || indice == 3))

    // --- MAGIA DE ANIMACIÓN CORREGIDA ---
    var progresoActual by remember { mutableFloatStateOf(0f) }

    // Usamos 'Unit' para que esto pase siempre que se dibuje la tarjeta
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(200) // Le damos 0.2 segundos de espera para que tus ojos lo noten
        progresoActual = planta.nivelAgua.toFloat() / 100f
    }

    val progresoAnimado by animateFloatAsState(
        targetValue = progresoActual,
        animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
        label = "animacion_agua"
    )
    // -------------------------------------

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (planta.nombre == "Vacío") Color(0xFFF5F5F5) else Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = Icons.Default.LocalFlorist, contentDescription = null, modifier = Modifier.size(40.dp).padding(bottom = 8.dp), tint = if (planta.nombre == "Vacío") Color.LightGray else Color(0xFF1B5E20))
            Text("Macetita ${indice + 1}", fontSize = 12.sp, color = Color.DarkGray)
            Text(planta.nombre, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF212121))
            Text("Humedad req: ${planta.nivelAgua}%", fontSize = 12.sp, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(8.dp))

            // Aquí usamos la variable animada en vez de planta.nivelAgua
            LinearProgressIndicator(
                progress = progresoAnimado,
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                color = Color(0xFF1B5E20),
                trackColor = Color.LightGray
            )

            if (esCentro && planta.nombre != "Vacío") {
                Spacer(modifier = Modifier.height(6.dp))
                Text("Ubicación: Bomba", fontSize = 11.sp, color = Color(0xFF1976D2), fontWeight = FontWeight.ExtraBold)
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