package nz.ac.uclive.ajs418.quickfire.fragments

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import nz.ac.uclive.ajs418.quickfire.R


class ConnectFragment : Fragment() {
    private val REQUEST_ENABLE_BT = 1
    private val REQUEST_DISCOVERABLE = 2
    private val CONNECT_FRAGMENT_TEXT = "Connect Fragment"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_connect, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

    private fun initializeBluetoothConnection() {
        Log.d("Connect Fragment", "Initializing Bluetooth Connection")
        val bluetoothManager: BluetoothManager? = requireContext().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager?
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager?.adapter

        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Log.d(CONNECT_FRAGMENT_TEXT, "Bluetooth Not Supported")
            return
        } else if (!bluetoothAdapter.isEnabled) {
            Log.d(CONNECT_FRAGMENT_TEXT, "Bluetooth Disabled - trying to enable")
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }

        // Bluetooth is enabled, proceed with device discovery
        val discoveryIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
        startActivityForResult(discoveryIntent, REQUEST_DISCOVERABLE)
    }

}
