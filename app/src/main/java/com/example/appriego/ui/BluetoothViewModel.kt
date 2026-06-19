// com/example/appriego/ui/BluetoothViewModel.kt
package com.example.appriego.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appriego.model.BluetoothDeviceModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Ojo: Esto es un ejemplo, necesitaremos pasarle la lógica de datos real.
class BluetoothViewModel : ViewModel() {

    // Estado para la lista de dispositivos que vemos en la UI
    private val _devicesList = MutableStateFlow<List<BluetoothDeviceModel>>(emptyList())
    val devicesList: StateFlow<List<BluetoothDeviceModel>> = _devicesList.asStateFlow()

    // Estado para el dispositivo actualmente conectado
    private val _connectedDevice = MutableStateFlow<BluetoothDeviceModel?>(null)
    val connectedDevice: StateFlow<BluetoothDeviceModel?> = _connectedDevice.asStateFlow()

    init {
        // Al arrancar, simulamos una lista de dispositivos (esto se cambia luego por el real)
        cargarDispositivosSimulados()
    }

    private fun cargarDispositivosSimulados() {
        val lista = listOf(
            BluetoothDeviceModel("HC-05 Riego", "00:11:22:33:44:55", isPaired = true),
            BluetoothDeviceModel("Mi Audífono", "AA:BB:CC:DD:EE:FF", isPaired = false)
        )
        _devicesList.value = lista
    }

    fun vincularDispositivo(device: BluetoothDeviceModel) {
        // Aquí iría la lógica para vincular (petición al sistema Android)
        // Por ahora simulamos que funciona:
        val index = _devicesList.value.indexOf(device)
        if (index != -1) {
            val updatedList = _devicesList.value.toMutableList()
            updatedList[index] = device.copy(isPaired = true)
            _devicesList.value = updatedList
        }
    }

    fun conectarDispositivo(device: BluetoothDeviceModel) {
        // Aquí iría la lógica para conectar (crear el socket Bluetooth)
        // Simulamos conexión exitosa:
        val index = _devicesList.value.indexOf(device)
        if (index != -1) {
            val updatedList = _devicesList.value.toMutableList()
            updatedList[index] = device.copy(isConnected = true)
            _devicesList.value = updatedList
            _connectedDevice.value = device
        }
    }

    fun desconectarDispositivo() {
        // Lógica de desconexión real
        val connected = _connectedDevice.value
        if (connected != null) {
            _connectedDevice.value = null
            // Actualizamos la lista para que el botón cambie
            cargarDispositivosSimulados()
        }
    }
}