package com.example.appriego.data

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

// Creamos un "molde" para guardar los 4 datos del Arduino ordenaditos
data class SensorData(
    val temperatura: Float,
    val humedadAire: Float,
    val humedadSuelo: Int,
    val estadoBomba: Int
)

@SuppressLint("MissingPermission")
class BluetoothService(private val bluetoothAdapter: BluetoothAdapter?) {

    // El "canal secreto" oficial para hablar con el módulo HC-05 (SPP)
    private val MY_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    private var bluetoothSocket: BluetoothSocket? = null
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null

    // La tubería de datos que va a estar escuchando tu interfaz gráfica (Compose)
    private val _sensorData = MutableStateFlow(SensorData(0.0f, 0.0f, 0, 0))
    val sensorData: StateFlow<SensorData> = _sensorData

    private val _connectionState = MutableStateFlow(false)
    val connectionState: StateFlow<Boolean> = _connectionState

    // 1. FUNCIÓN PARA CONECTARSE AL BLUETOOTH
    suspend fun connectToDevice(deviceAddress: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val device: BluetoothDevice? = bluetoothAdapter?.getRemoteDevice(deviceAddress)
                bluetoothSocket = device?.createRfcommSocketToServiceRecord(MY_UUID)

                bluetoothAdapter?.cancelDiscovery() // Cancelamos búsqueda para no gastar batería
                bluetoothSocket?.connect()

                inputStream = bluetoothSocket?.inputStream
                outputStream = bluetoothSocket?.outputStream

                _connectionState.value = true
                Log.d("BluetoothService", "¡Conectado como unos cracks!")
                true
            } catch (e: Exception) {
                Log.e("BluetoothService", "Error de conexión: ${e.message}")
                disconnect()
                false
            }
        }
    }

    // 2. FUNCIÓN PARA ESCUCHAR AL ARDUINO INFINITAMENTE
    suspend fun listenForData() {
        withContext(Dispatchers.IO) {
            val buffer = ByteArray(1024)
            var bytes: Int
            var readMessage = ""

            while (isActive && _connectionState.value) {
                try {
                    bytes = inputStream?.read(buffer) ?: 0
                    if (bytes > 0) {
                        val text = String(buffer, 0, bytes)
                        readMessage += text

                        // ¿Te acuerdas del 'println' en Arduino? Aquí buscamos ese salto de línea '\n'
                        if (readMessage.contains("\n")) {
                            val messages = readMessage.split("\n")
                            for (i in 0 until messages.size - 1) {
                                val cleanMessage = messages[i].trim()
                                if (cleanMessage.isNotEmpty()) {
                                    parseData(cleanMessage) // Mandamos a cortar por comas
                                }
                            }
                            readMessage = messages.last() // Guardamos el pedacito que sobre para la próxima
                        }
                    }
                } catch (e: Exception) {
                    Log.e("BluetoothService", "Se perdió la conexión bro :c")
                    disconnect()
                    break
                }
            }
        }
    }

    // 3. CORTAR EL MENSAJE Y ASIGNARLO (EL TRUCO DE LA COMA)
    private fun parseData(data: String) {
        try {
            val parts = data.split(",") // Cortamos por la coma
            if (parts.size == 4) { // Si llegaron los 4 datos completos...
                val temp = parts[0].toFloat()
                val humAire = parts[1].toFloat()
                val humSuelo = parts[2].toInt()
                val estadoBomba = parts[3].toInt()

                // Actualizamos nuestro molde en tiempo real
                _sensorData.value = SensorData(temp, humAire, humSuelo, estadoBomba)
                Log.d("BluetoothService", "Llegó: Temp $temp | Tierra $humSuelo | Bomba $estadoBomba")
            }
        } catch (e: Exception) {
            Log.e("BluetoothService", "Basura recibida (es normal al inicio): $data")
        }
    }

    // 4. FUNCIÓN PARA PRENDER LA BOMBA DESDE LA APP
    fun sendCommand(command: String) {
        try {
            // Le enviamos el '1' o el '0' al Arduino
            outputStream?.write(command.toByteArray())
            Log.d("BluetoothService", "¡Disparo de comando!: $command")
        } catch (e: Exception) {
            Log.e("BluetoothService", "Error al disparar el comando", e)
        }
    }

    // 5. APAGAR TODO
    fun disconnect() {
        try {
            inputStream?.close()
            outputStream?.close()
            bluetoothSocket?.close()
        } catch (e: Exception) {
            Log.e("BluetoothService", "Error cerrando", e)
        } finally {
            _connectionState.value = false
        }
    }
}