package com.example.appriego.model

data class BluetoothDeviceModel(
    val name: String,        // El nombre que muestra el HC-05 (ej: "HC-05 Riego")
    val address: String,     // La dirección MAC única (ej: "00:11:22:33:44:55")
    val isPaired: Boolean,   // ¿Ya está vinculado en los ajustes del cel?
    val isConnected: Boolean = false // ¿Está conectado ahora mismo?
)