package com.example.appriego.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun BluetoothMainScreen(
    viewModel: BluetoothViewModel = viewModel(),
    onDeviceConnected: () -> Unit
) {
    val devices by viewModel.devicesList.collectAsState()
    val connectedDevice by viewModel.connectedDevice.collectAsState()

    LaunchedEffect(connectedDevice) {
        if (connectedDevice != null) {
            onDeviceConnected()
        }
    }

    LazyColumn {
        items(devices) { device ->
            BluetoothDeviceItem(
                device = device,
                onConnectClick = { viewModel.conectarDispositivo(device) }
            )
        }
    }
}