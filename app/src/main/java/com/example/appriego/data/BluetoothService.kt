package com.example.appriego.data

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import java.util.UUID

class BluetoothService {
    // El HC-05 siempre usa este UUID para conexiones seriales
    private val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    fun connect(deviceAddress: String) {
        // Aquí meteremos el BluetoothSocket y el hilo para no congelar la pantalla
        println("Conectando con el HC-05 en la dirección: $deviceAddress")
    }

    fun sendData(message: String) {
        // Aquí irá el OutputStream para mandar los comandos a tu Arduino
    }
}