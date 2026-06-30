package com.example.appriego.ui

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appriego.model.BluetoothDeviceModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.UUID

@SuppressLint("MissingPermission")
class BluetoothViewModel : ViewModel() {

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    private val _plantasDelHuertoActual = MutableStateFlow<List<String>>(
        listOf("Tomate (Mucha Agua)", "Lechuga (Agua Media)", "Espinaca (Agua Media)") // Tus fijas por defecto
    )
    val plantasDelHuertoActual: StateFlow<List<String>> = _plantasDelHuertoActual.asStateFlow()

    fun actualizarPlantasDelHuerto(nuevasPlantas: List<String>) {
        _plantasDelHuertoActual.value = nuevasPlantas
    }

    private val _devicesList = MutableStateFlow<List<BluetoothDeviceModel>>(emptyList())
    val devicesList: StateFlow<List<BluetoothDeviceModel>> = _devicesList.asStateFlow()

    private val _connectedDevice = MutableStateFlow<BluetoothDeviceModel?>(null)
    val connectedDevice: StateFlow<BluetoothDeviceModel?> = _connectedDevice.asStateFlow()

    private var bluetoothSocket: BluetoothSocket? = null
    private val SERIAL_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    init {
        cargarDispositivosVinculados()
    }

    // Carga los dispositivos ya guardados en el teléfono
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

    // ESCANEO COMPLETO PARA LA FERIA: Jala vinculados de forma real
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

    fun conectarDispositivo(deviceModel: BluetoothDeviceModel) {
        if (bluetoothAdapter == null) return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val remoteDevice = bluetoothAdapter.getRemoteDevice(deviceModel.address)
                desconectarDispositivo()

                bluetoothSocket = remoteDevice.createRfcommSocketToServiceRecord(SERIAL_UUID)
                bluetoothAdapter.cancelDiscovery()
                bluetoothSocket?.connect()

                launch(Dispatchers.Main) {
                    _connectedDevice.value = deviceModel.copy(isConnected = true)
                    _devicesList.value = _devicesList.value.map {
                        if (it.address == deviceModel.address) it.copy(isConnected = true) else it
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                try { bluetoothSocket?.close() } catch (ex: Exception) {}
            }
        }
    }

    fun desconectarDispositivo() {
        try {
            bluetoothSocket?.close()
            bluetoothSocket = null
            _connectedDevice.value = null
            _devicesList.value = _devicesList.value.map { it.copy(isConnected = false) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

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

    // 🌟 NUEVA FUNCIÓN AGREGADA: Transmite el número por el canal Bluetooth real
    // Se llama 'enviarUmbralArduinio' con la 'i' para que encaje directo con tu UI
    fun enviarUmbralArduinio(porcentaje: Int) {
        if (bluetoothSocket == null || !bluetoothSocket!!.isConnected) return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val outputStream = bluetoothSocket!!.outputStream
                val trama = "$porcentaje\n" // Añade el salto de línea obligatorio para el Arduino
                outputStream.write(trama.toByteArray())
                outputStream.flush()
                println("LOG_FERIA: Umbral unificado transmitido -> $trama")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}