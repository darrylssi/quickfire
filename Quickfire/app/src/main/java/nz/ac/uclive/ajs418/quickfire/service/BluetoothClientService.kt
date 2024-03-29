package nz.ac.uclive.ajs418.quickfire.service

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import nz.ac.uclive.ajs418.quickfire.entity.User
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

class BluetoothClientService {

    private var bluetoothSocket: BluetoothSocket? = null
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null
    private val REQUEST_BLUETOOTH_PERMISSIONS = 1
    val MY_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")
    private var callback: BluetoothServiceCallback? = null

    private fun setStreams(inStream: InputStream, outStream: OutputStream) {
        inputStream = inStream
        outputStream = outStream
    }

    fun setCallback(callback: BluetoothServiceCallback) {
        this.callback = callback
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun readData() {
        Log.d("BCS", "read")
        val buffer = ByteArray(1024)

        GlobalScope.launch(Dispatchers.IO) {
            try {
                while (true) {
                    val bytesRead = inputStream?.read(buffer)

                    if (bytesRead != null && bytesRead > 0) {
                        val receivedData = String(buffer, 0, bytesRead)
                        returnDataToFrag(receivedData)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                // Handle reading error
            }
        }
    }

    private suspend fun returnDataToFrag(string: String) {
        Log.d("BluetoothClientService", string)
        callback?.onDataReceived(string)

    }

    @OptIn(DelicateCoroutinesApi::class)
    fun writeData(data: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val bytes = data.toByteArray()
                outputStream?.write(bytes)
                // Data sent successfully
            } catch (e: IOException) {
                e.printStackTrace()
                // Handle writing error
            }
        }
    }

    fun connectToDevice(device: BluetoothDevice, context: Context, activity: Activity) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val bluetoothPermission = Manifest.permission.BLUETOOTH
                val bluetoothConnectPermission = Manifest.permission.BLUETOOTH_CONNECT

                if (ContextCompat.checkSelfPermission(context, bluetoothPermission) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, bluetoothConnectPermission) == PackageManager.PERMISSION_GRANTED) {
                    // BLUETOOTH and BLUETOOTH_CONNECT permissions are already granted, proceed with accessing paired devices
                } else {
                    // Request BLUETOOTH and BLUETOOTH_CONNECT permissions
                    ActivityCompat.requestPermissions(activity, arrayOf(bluetoothPermission, bluetoothConnectPermission), REQUEST_BLUETOOTH_PERMISSIONS)
                }

                val bluetoothSocket: BluetoothSocket? = device.createRfcommSocketToServiceRecord(MY_UUID)
                bluetoothSocket?.connect()

                // Create input and output streams
                if (bluetoothSocket != null) {
                    setStreams(bluetoothSocket.inputStream, bluetoothSocket.outputStream)
                }
                val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
                val deviceName = bluetoothAdapter?.name
                writeData("client_name:$deviceName")
                returnDataToFrag("client_name:$deviceName")
                // Start reading data
                readData()
            } catch (e: IOException) {
                e.printStackTrace()
                // Handle connection error
            }
        }
    }

    fun disconnect() {
        try {
            inputStream?.close()
            outputStream?.close()
            bluetoothSocket?.close()
        } catch (e: IOException) {
            e.printStackTrace()
            // Handle disconnection error
        }
    }
}
