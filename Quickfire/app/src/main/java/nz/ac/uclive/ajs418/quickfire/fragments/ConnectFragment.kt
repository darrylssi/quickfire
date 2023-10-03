package nz.ac.uclive.ajs418.quickfire.fragments

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import nz.ac.uclive.ajs418.quickfire.R


class ConnectFragment : Fragment() {
    private val REQUEST_ENABLE_BT = 1
    private val REQUEST_BLUETOOTH_SCAN_PERMISSION = 2
    private val CONNECT_FRAGMENT_TEXT = "Connect Fragment"

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d("ConnectFragment","test")
            when(intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    Log.d("ConnectFragment","test2")
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
                    // val deviceName = device.name
                    val deviceAddress = device.address
                    Log.d("ConnectFragment",deviceAddress)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_connect, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up bluetooth receiver
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        requireActivity().registerReceiver(receiver, filter)

        val addButton = view.findViewById<Button>(R.id.addPersonButton)
        addButton.setOnClickListener {
            initializeBluetoothConnection()
        }

        val arguments = arguments
        if (arguments != null && arguments.getBoolean("isMember")) {
            val startButton = view.findViewById<Button>(R.id.startMatchButton)
            disableButton(startButton, view)
        }
    }

    private fun disableButton(button: Button, view: View) {
        button.isEnabled = false
        button.isClickable = false
        button.setBackgroundColor(ContextCompat.getColor(view.context, R.color.grey))
        button.setTextColor(ContextCompat.getColor(view.context, R.color.white))
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun initializeBluetoothConnection() {
        Log.d(CONNECT_FRAGMENT_TEXT, "Initializing Bluetooth Connection")
        val bluetoothManager: BluetoothManager? = requireContext().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager?
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager?.adapter

        // CHECK IF DEVICE SUPPORTS BLUETOOTH
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Log.d(CONNECT_FRAGMENT_TEXT, "Bluetooth Not Supported")
            return
        }
        // CHECK IF BLUETOOTH IS OFF AND TURN ON
        else if (!bluetoothAdapter.isEnabled) {
            Log.d(CONNECT_FRAGMENT_TEXT, "Bluetooth Disabled - trying to enable")
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }

        // REQUEST PERMISSION TO CONDUCT BLUETOOTH SCAN
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
            // BLUETOOTH_SCAN permission is already granted, proceed with Bluetooth operations
        } else {
            // BLUETOOTH_SCAN permission is not granted, request it
            Log.d(CONNECT_FRAGMENT_TEXT, "Ask for permission")
            requestPermissions(arrayOf(Manifest.permission.BLUETOOTH_SCAN), REQUEST_BLUETOOTH_SCAN_PERMISSION)
        }

        Log.d("ConnectFragment", "Begin Scan")
        // START BLUETOOTH SCAN
        val discoveryStarted = bluetoothAdapter.startDiscovery()

    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_BLUETOOTH_SCAN_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // BLUETOOTH_SCAN permission granted, proceed with Bluetooth operations
                } else {
                    // Permission denied, handle this case (show a message, ask again, etc.)
                    Log.d(CONNECT_FRAGMENT_TEXT, "Permission Denied to Scan")
                    return
                }
            }
        }
    }

}


