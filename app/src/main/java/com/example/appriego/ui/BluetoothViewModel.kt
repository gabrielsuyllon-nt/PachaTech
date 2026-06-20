// com/example/appriego/ui/BluetoothViewModel.kt
package com.example.appriego.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appriego.model.BluetoothDeviceModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class BluetoothViewModel : ViewModel() {


    private val _devicesList = MutableStateFlow<List<BluetoothDeviceModel>>(emptyList())
    val devicesList: StateFlow<List<BluetoothDeviceModel>> = _devicesList.asStateFlow()


    private val _connectedDevice = MutableStateFlow<BluetoothDeviceModel?>(null)
    val connectedDevice: StateFlow<BluetoothDeviceModel?> = _connectedDevice.asStateFlow()

    init {

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

        val index = _devicesList.value.indexOf(device)
        if (index != -1) {
            val updatedList = _devicesList.value.toMutableList()
            updatedList[index] = device.copy(isPaired = true)
            _devicesList.value = updatedList
        }
    }

    fun conectarDispositivo(device: BluetoothDeviceModel) {

        val index = _devicesList.value.indexOf(device)
        if (index != -1) {
            val updatedList = _devicesList.value.toMutableList()
            updatedList[index] = device.copy(isConnected = true)
            _devicesList.value = updatedList
            _connectedDevice.value = device
        }
    }

    fun desconectarDispositivo() {

        val connected = _connectedDevice.value
        if (connected != null) {
            _connectedDevice.value = null

            cargarDispositivosSimulados()
        }
    }
}