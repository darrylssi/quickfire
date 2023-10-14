package nz.ac.uclive.ajs418.quickfire

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
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

class BluetoothServerService {

    private var serverSocket: BluetoothServerSocket? = null
    private var bluetoothSocket: BluetoothSocket? = null
    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null
    private val REQUEST_BLUETOOTH_PERMISSIONS = 1

    private val MY_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")
    private val APP_NAME = "nz.ac.uclive.ajs418.quickfire"

    fun acceptConnections(context: Context, activity: Activity, onSocketEstablished: () -> Unit) {
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
                // Show a toast message
                activity.runOnUiThread {
                    Toast.makeText(context, "Waiting for host to invite", Toast.LENGTH_SHORT).show()
                }
                val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
                serverSocket = bluetoothAdapter?.listenUsingRfcommWithServiceRecord(APP_NAME, MY_UUID)


                bluetoothSocket = serverSocket?.accept()
                inputStream = bluetoothSocket?.inputStream
                outputStream = bluetoothSocket?.outputStream

                // Start reading data
                writeData("Hello Client, I'm Server")
                val deviceName = bluetoothAdapter?.name
                val user = deviceName?.let { User(it, "SERVER") }
                writeData("Server Name: $deviceName")
                //start reading data
                readData()
                onSocketEstablished()
            } catch (e: IOException) {
                e.printStackTrace()
                // Handle connection failure
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun readData() {
        val buffer = ByteArray(1024)

        GlobalScope.launch(Dispatchers.IO) {
            try {
                while (true) {
                    val bytesRead = inputStream?.read(buffer)

                    if (bytesRead != null && bytesRead > 0) {
                        val receivedData = String(buffer, 0, bytesRead)
                        useReceivedData(receivedData)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                // Handle reading error
            }
        }
    }

    private fun useReceivedData(receivedData: String) {
        if (receivedData.startsWith("Client Name: ")) {
            val clientName = receivedData.substringAfter("Client Name: ")
        }
        Log.d("BluetoothServerService", receivedData)
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

    fun closeConnection() {
        try {
            inputStream?.close()
            outputStream?.close()
            bluetoothSocket?.close()
            serverSocket?.close()
        } catch (e: IOException) {
            e.printStackTrace()
            // Handle disconnection error
        }
    }
}
