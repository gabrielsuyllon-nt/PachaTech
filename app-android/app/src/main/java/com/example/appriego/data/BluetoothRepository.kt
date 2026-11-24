// data/BluetoothRepository.kt
package com.example.appriego.data

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice

class BluetoothRepository {

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    @SuppressLint("MissingPermission") // Usaremos esto para saltar la alerta temporalmente
    fun getPairedDevices(): List<com.example.appriego.model.BluetoothDeviceModel> {
        // Aquí obtendremos los dispositivos reales vinculados en tu Android
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        return pairedDevices?.map {
            com.example.appriego.model.BluetoothDeviceModel(
                name = it.name ?: "Desconocido",
                address = it.address,
                isPaired = true
            )
        } ?: emptyList()
    }
}