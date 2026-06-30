package com.example.appriego.ui

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothSearching
import androidx.compose.material.icons.filled.CheckCircle
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
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BluetoothMainScreen(
    viewModel: BluetoothViewModel = viewModel(),
    onDeviceConnected: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val devices by viewModel.devicesList.collectAsState()
    val connectedDevice by viewModel.connectedDevice.collectAsState()

    // --- AQUÍ ESCUCHAMOS AL ARDUINO DE VERDAD ---
    val sensorData by viewModel.sensorData.collectAsState()

    var buscandoDispositivos by remember { mutableStateOf(false) }

    val launcherPermisos = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permisos ->
        val conexionOk = permisos[Manifest.permission.BLUETOOTH_CONNECT] ?: false
        if (conexionOk) {
            viewModel.cargarDispositivosVinculados()
        } else {
            Toast.makeText(context, "Se necesitan los permisos para conectar el Arduino", Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val tieneConnect = ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
            if (!tieneConnect) {
                launcherPermisos.launch(arrayOf(Manifest.permission.BLUETOOTH_CONNECT))
            } else {
                viewModel.cargarDispositivosVinculados()
            }
        } else {
            viewModel.cargarDispositivosVinculados()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFE8F5E9), Color(0xFFFFFFFF))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(24.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onDeviceConnected() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.Black)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Conexión Bluetooth",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = "Vincula la app con el circuito físico de la bomba",
                fontSize = 14.sp,
                color = Color.DarkGray,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 48.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

                    if (bluetoothAdapter == null) {
                        Toast.makeText(context, "Este dispositivo no soporta Bluetooth", Toast.LENGTH_SHORT).show()
                    } else if (!bluetoothAdapter.isEnabled) {
                        Toast.makeText(context, "Por favor, enciende el Bluetooth de tu celular", Toast.LENGTH_LONG).show()
                    } else {
                        scope.launch {
                            buscandoDispositivos = true
                            viewModel.simularEscaneoReal()
                            delay(1500)
                            buscandoDispositivos = false
                            Toast.makeText(context, "Lista de dispositivos actualizada", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !buscandoDispositivos,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (buscandoDispositivos) Icons.Default.BluetoothSearching else Icons.Default.Bluetooth,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (buscandoDispositivos) "Buscando cambios..." else "Buscar / Actualizar Lista",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }

            if (buscandoDispositivos) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.primary)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Dispositivos del Entorno:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // Esto hace que la lista ocupe el espacio disponible
            ) {
                if (devices.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No se encontraron dispositivos.\nPresiona 'Buscar / Actualizar Lista'.",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(devices) { device ->
                            val esElConectado = connectedDevice?.address == device.address

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = if (esElConectado) androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF2E7D32)) else null,
                                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            modifier = Modifier.weight(1f),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Surface(
                                                modifier = Modifier.size(42.dp),
                                                shape = CircleShape,
                                                color = if (esElConectado) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.primaryContainer
                                            ) {
                                                Icon(
                                                    Icons.Default.Bluetooth,
                                                    contentDescription = null,
                                                    tint = if (esElConectado) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.padding(10.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(16.dp))
                                            Column(modifier = Modifier.weight(1f, fill = false)) {
                                                Text(
                                                    text = device.name ?: "Dispositivo Guardado",
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontSize = 16.sp,
                                                    color = Color.Black,
                                                    maxLines = 1
                                                )
                                                Text(
                                                    text = "Dirección MAC: ${device.address}",
                                                    fontSize = 13.sp,
                                                    color = Color.DarkGray
                                                )
                                            }
                                        }
                                        if (esElConectado) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = null,
                                                    tint = Color(0xFF2E7D32),
                                                    modifier = Modifier.size(24.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Listo", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (device.isPaired) {
                                            TextButton(
                                                onClick = { viewModel.desvincularDispositivoReal(device) },
                                                colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFD32F2F))
                                            ) {
                                                Text("Desvincular", fontWeight = FontWeight.Bold)
                                            }

                                            Spacer(modifier = Modifier.width(8.dp))

                                            Button(
                                                onClick = { viewModel.conectarDispositivo(device) },
                                                enabled = !esElConectado,
                                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Text(if (esElConectado) "Conectado" else "Conectar")
                                            }
                                        } else {
                                            Button(
                                                onClick = {
                                                    Toast.makeText(context, "Emparejando con ${device.name}...", Toast.LENGTH_SHORT).show()
                                                    viewModel.vincularDispositivoReal(device)
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                                            ) {
                                                Text("Vincular", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- NUEVA TARJETA: LECTURAS REALES Y CONTROL TOTAL ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Monitoreo y Control en Vivo",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF2E7D32)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Humedad Tierra: ${sensorData.humedadSuelo}", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("Temp Ambiente: ${sensorData.temperatura} °C", fontSize = 14.sp)
                            Text("Humedad Aire: ${sensorData.humedadAire}%", fontSize = 14.sp)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Estado Bomba", fontSize = 12.sp, color = Color.Gray)
                            Text(
                                text = if (sensorData.estadoBomba == 1) "ACTIVA 💧" else "APAGADA 🛑",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (sensorData.estadoBomba == 1) Color(0xFF1976D2) else Color(0xFFD32F2F)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = Color.LightGray)
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Acciones Manuales (Relé):", fontSize = 13.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.encenderBomba()
                                Toast.makeText(context, "Orden enviada: ENCENDER BOMBA", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2)),
                            modifier = Modifier.weight(1f).height(40.dp),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Prender", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                viewModel.apagarBomba()
                                Toast.makeText(context, "Orden enviada: APAGAR BOMBA", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                            modifier = Modifier.weight(1f).height(40.dp),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Apagar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                viewModel.modoAutomatico()
                                Toast.makeText(context, "Orden enviada: MODO AUTOMÁTICO", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF57C00)),
                            modifier = Modifier.weight(1f).height(40.dp),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Auto", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}