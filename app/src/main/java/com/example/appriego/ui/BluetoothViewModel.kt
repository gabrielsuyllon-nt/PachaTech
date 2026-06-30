package com.example.appriego.ui

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appriego.data.BluetoothService
import com.example.appriego.data.SensorData
import com.example.appriego.model.BluetoothDeviceModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
class BluetoothViewModel : ViewModel() {

    // --- 1. MOTOR BLUETOOTH (EL QUE CREAMOS HOY) ---
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val bluetoothService = BluetoothService(bluetoothAdapter)

    // Datos en tiempo real que vienen del Arduino
    val sensorData: StateFlow<SensorData> = bluetoothService.sensorData
    val connectionState: StateFlow<Boolean> = bluetoothService.connectionState

    // --- 2. LÓGICA DE LAS PLANTAS (EL TRABAJO DE TUS AMIGOS) ---
    private val _plantasDelHuertoActual = MutableStateFlow<List<String>>(
        listOf("Tomate (Mucha Agua)", "Lechuga (Agua Media)", "Espinaca (Agua Media)")
    )
    val plantasDelHuertoActual: StateFlow<List<String>> = _plantasDelHuertoActual.asStateFlow()

    fun actualizarPlantasDelHuerto(nuevasPlantas: List<String>) {
        _plantasDelHuertoActual.value = nuevasPlantas
    }

    // --- 3. GESTIÓN DE DISPOSITIVOS VISUALES (EL TRABAJO DE TUS AMIGOS) ---
    private val _devicesList = MutableStateFlow<List<BluetoothDeviceModel>>(emptyList())
    val devicesList: StateFlow<List<BluetoothDeviceModel>> = _devicesList.asStateFlow()

    private val _connectedDevice = MutableStateFlow<BluetoothDeviceModel?>(null)
    val connectedDevice: StateFlow<BluetoothDeviceModel?> = _connectedDevice.asStateFlow()

    init {
        cargarDispositivosVinculados()
    }

    fun cargarDispositivosVinculados() {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) return

        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter.bondedDevices
        val listaVinculados = pairedDevices?.map { device ->
            BluetoothDeviceModel(
                name = device.name ?: "Dispositivo Desconocido",
                address = device.address,
                isPaired = true
            )
        } ?: emptyList()

        _devicesList.value = listaVinculados
    }

    fun simularEscaneoReal() {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) return
        if (bluetoothAdapter.isDiscovering) {
            bluetoothAdapter.cancelDiscovery()
        }
        bluetoothAdapter.startDiscovery()
        cargarDispositivosVinculados()
    }
    fun vincularDispositivoReal(deviceModel: BluetoothDeviceModel) {
        if (bluetoothAdapter == null) return
        try {
            val remoteDevice = bluetoothAdapter.getRemoteDevice(deviceModel.address)
            remoteDevice.createBond()

            _devicesList.value = _devicesList.value.map {
                if (it.address == deviceModel.address) it.copy(isPaired = true) else it
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun desvincularDispositivoReal(deviceModel: BluetoothDeviceModel) {
        if (bluetoothAdapter == null) return
        try {
            val remoteDevice = bluetoothAdapter.getRemoteDevice(deviceModel.address)
            val method = remoteDevice.javaClass.getMethod("removeBond")
            method.invoke(remoteDevice)

            if (_connectedDevice.value?.address == deviceModel.address) {
                desconectarDispositivo()
            }

            _devicesList.value = _devicesList.value.map {
                if (it.address == deviceModel.address) it.copy(isPaired = false, isConnected = false) else it
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // --- 4. CONEXIÓN REAL AL ARDUINO (NUESTRA MEZCLA) ---
    fun conectarDispositivo(deviceModel: BluetoothDeviceModel) {
        viewModelScope.launch {
            // Usamos nuestro motor nuevo para conectar a la MAC address
            val isConnected = bluetoothService.connectToDevice(deviceModel.address)

            if (isConnected) {
                // Si conecta, actualizamos la UI de tus amigos
                _connectedDevice.value = deviceModel.copy(isConnected = true)
                _devicesList.value = _devicesList.value.map {
                    if (it.address == deviceModel.address) it.copy(isConnected = true) else it
                }
                val plantas = plantasDelHuertoActual.value
                val umbral = if (plantas.isNotEmpty()) calcularHumedadNecesaria(plantas) else 50
                enviarUmbralArduinio(umbral)

                // Y ponemos al motor a escuchar datos infinitamente
                bluetoothService.listenForData()
            }
        }
    }

    fun desconectarDispositivo() {
        bluetoothService.disconnect()
        _connectedDevice.value = null
        _devicesList.value = _devicesList.value.map { it.copy(isConnected = false) }
    }

    // --- 5. ACCIONES DE LA BOMBA (EL QUE CREAMOS HOY) ---
    fun encenderBomba() {
        bluetoothService.sendCommand("1")
    }

    fun apagarBomba() {
        bluetoothService.sendCommand("0")
    }

    fun modoAutomatico() {
        bluetoothService.sendCommand("2")
    }

    // --- 6. CÁLCULO DE HUMEDAD Y ENVÍO AL ARDUINO (DE TUS AMIGOS) ---
    fun calcularHumedadNecesaria(plantasSeleccionadas: List<String>): Int {
        if (plantasSeleccionadas.isEmpty()) return 0

        val requerimientos = mapOf(
            "Tomate (Mucha Agua)" to 80,
            "Lechuga (Agua Media)" to 50,
            "Espinaca (Agua Media)" to 45,
            "Cactus (Poca Agua)" to 15,
            "Romero (Poca Agua)" to 20
        )

        var sumaHumedades = 0
        var plantasValidas = 0

        for (planta in plantasSeleccionadas) {
            requerimientos[planta]?.let { humedad ->
                sumaHumedades += humedad
                plantasValidas++
            }
        }
        return if (plantasValidas > 0) sumaHumedades / plantasValidas else 50
    }

    fun enviarUmbralArduinio(porcentaje: Int) {
        bluetoothService.sendCommand("U$porcentaje\n")
        println("LOG_FERIA: Umbral unificado transmitido -> $porcentaje%")
    }
}