package com.example.appriego.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appriego.model.BluetoothDeviceModel

@Composable
fun BluetoothDeviceItem(device: BluetoothDeviceModel, onConnectClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = device.name, style = MaterialTheme.typography.titleMedium)
            Text(text = device.address, style = MaterialTheme.typography.bodySmall)

            Button(onClick = onConnectClick) {
                Text(if (device.isConnected) "Desconectar" else "Conectar")
            }
        }
    }
}