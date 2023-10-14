package nz.ac.uclive.ajs418.quickfire.fragments

import android.Manifest
import android.app.Dialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import nz.ac.uclive.ajs418.quickfire.MainActivity
import nz.ac.uclive.ajs418.quickfire.service.BluetoothClientService
import nz.ac.uclive.ajs418.quickfire.R
import nz.ac.uclive.ajs418.quickfire.viewmodel.UserViewModel


class ConnectFragment : Fragment() {
    private val REQUEST_BLUETOOTH_SCAN_PERMISSION = 1
    private val REQUEST_BLUETOOTH_PERMISSION = 2
    private val CONNECT_FRAGMENT_TEXT = "Connect Fragment"
    private lateinit var bluetoothClientService: BluetoothClientService
    private lateinit var userViewModel: UserViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        userViewModel = (requireActivity() as MainActivity).getUserViewModelInstance()
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
        bluetoothClientService = BluetoothClientService()

        val addButton = view.findViewById<Button>(R.id.addPersonButton)
        addButton.setOnClickListener {
            showPairedDevicesPopup()
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

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_BLUETOOTH_SCAN_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Something something something
                } else {
                    // Permission denied, handle this case (show a message, ask again, etc.)
                    Log.d(CONNECT_FRAGMENT_TEXT, "Permission Denied to Scan")
                    return
                }
            }
        }
    }

    private fun showPairedDevicesPopup() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.popup_layout)

        val listView = dialog.findViewById<ListView>(R.id.device_list)

        // BLUETOOTH PERMISSION
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED) {
            // BLUETOOTH permission is already granted, proceed with accessing paired devices
        } else {
            // BLUETOOTH permission is not granted, request it
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.BLUETOOTH), REQUEST_BLUETOOTH_PERMISSION)
        }

        // BLUETOOTH_CONNECT PERMISSION
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            // BLUETOOTH permission is already granted, proceed with accessing paired devices
        } else {
            // BLUETOOTH permission is not granted, request it
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.BLUETOOTH_CONNECT), REQUEST_BLUETOOTH_PERMISSION)
        }

        // Get the set of paired devices
        val bluetoothManager: BluetoothManager? = requireContext().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager?
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager?.adapter

        val pairedDevices = bluetoothAdapter?.bondedDevices

        // Convert the set of Bluetooth devices to a list of strings (device names)
        val deviceNames: Array<String> = pairedDevices?.mapNotNull { it.name }?.toTypedArray() ?: emptyArray()

        // Create an ArrayAdapter to populate the ListView
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, deviceNames)

        // Set the adapter for the ListView
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            // Get the selected device
            val selectedDevice: BluetoothDevice? = pairedDevices?.toList()?.getOrNull(position)

            // Check if a device was selected
            selectedDevice?.let {
                // Establish a connection with the selected device
                bluetoothClientService.connectToDevice(it, requireContext(), requireActivity())
                dialog.dismiss()
            }
        }

        dialog.show()
    }

}


