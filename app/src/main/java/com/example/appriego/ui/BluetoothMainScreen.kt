package com.example.appriego.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun BluetoothMainScreen(viewModel: BluetoothViewModel = viewModel()) {
    val devices by viewModel.devicesList.collectAsState()

    LazyColumn {
        items(devices) { device ->
            // AQUÍ LLAMAS AL COMPONENTE EXTERNO
            BluetoothDeviceItem(
                device = device,
                onConnectClick = { viewModel.conectarDispositivo(device) }
            )
        }
    }
}