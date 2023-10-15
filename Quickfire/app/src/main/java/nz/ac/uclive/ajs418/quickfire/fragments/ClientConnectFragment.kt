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
import android.widget.EditText
import android.widget.ListView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import nz.ac.uclive.ajs418.quickfire.MainActivity
import nz.ac.uclive.ajs418.quickfire.service.BluetoothClientService
import nz.ac.uclive.ajs418.quickfire.R
import nz.ac.uclive.ajs418.quickfire.entity.Party
import nz.ac.uclive.ajs418.quickfire.entity.User
import nz.ac.uclive.ajs418.quickfire.service.BluetoothServiceCallback
import nz.ac.uclive.ajs418.quickfire.viewmodel.LikeViewModel
import nz.ac.uclive.ajs418.quickfire.viewmodel.PartyViewModel
import nz.ac.uclive.ajs418.quickfire.viewmodel.UserViewModel


class ClientConnectFragment : Fragment(), BluetoothServiceCallback {
    private val REQUEST_BLUETOOTH_PERMISSION = 1
    private lateinit var bluetoothClientService: BluetoothClientService
    private lateinit var userViewModel: UserViewModel
    private lateinit var partyViewModel: PartyViewModel
    private lateinit var likeViewModel: LikeViewModel
    private var serverUser = User("", "")
    private var clientUser = User("", "")
    private var party = Party("", arrayListOf(), arrayListOf())


    override fun onAttach(context: Context) {
        super.onAttach(context)
        userViewModel = (requireActivity() as MainActivity).getUserViewModelInstance()
        partyViewModel = (requireActivity() as MainActivity).getPartyViewModelInstance()
        //likeViewModel = (requireActivity() as MainActivity).getLikeViewModelInstance()
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
        bluetoothClientService.setCallback(this)

        val addButton = view.findViewById<Button>(R.id.addPersonButton)
        addButton.setOnClickListener {
            showPairedDevicesPopup()
            enableStartButton(view)
        }

        val startButton = view.findViewById<Button>(R.id.startMatchButton)

        startButton.setOnClickListener {
            val partyName = view.findViewById<EditText>(R.id.partyNameField)
            val partNameText = partyName.text.toString()
            sendData("party_name:$partNameText")
            Log.d("CCF: Client Username", clientUser.name)
            Log.d("CCF: Server Username", serverUser.name)
            val partyMembers = ArrayList<Long>().apply {
                add(clientUser.id)
                add(serverUser.id)
            }
            party = Party(partNameText, partyMembers, arrayListOf()) //  Matches is initially empty
            lifecycleScope.launch { partyViewModel.addParty(party) }
            lifecycleScope.launch { partyViewModel.setCurrentParty(party.id) }
            switchToClientPlayFragment(bluetoothClientService)
        }
    }

    private fun enableStartButton( view: View) {
        val startButton = view.findViewById<Button>(R.id.startMatchButton)
        startButton.isEnabled = true
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_BLUETOOTH_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Something something something
                } else {
                    // Permission denied, handle this case (show a message, ask again, etc.)
                    Log.d("Connect Fragment", "Permission Denied to Scan")
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

    override fun onDataReceived(string: String) {
        Log.d("ClientConnectFragment", string)
        // Handle the received data here
        if (string.startsWith("client_name:")) {
            val username = string.substringAfter("client_name:")
            clientUser = User(username, "CLIENT")
            lifecycleScope.launch { userViewModel.addUser(clientUser) }
            lifecycleScope.launch { userViewModel.setId(clientUser.id) }
        }
        if (string.startsWith("server_name:")) {
            val username = string.substringAfter("server_name:")
            serverUser = User(username, "SERVER")
            lifecycleScope.launch { userViewModel.addUser(serverUser) }
        }
    }

    private fun sendData(data: String) {
        bluetoothClientService.writeData(data)
    }

    private fun switchToClientPlayFragment(bluetoothClientService: BluetoothClientService) {
        val clientPlayFragment = ClientPlayFragment()
        clientPlayFragment.setBluetoothClientService(bluetoothClientService)

        val fragmentTransaction = parentFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, clientPlayFragment)
            .addToBackStack(null) // Optional: Adds the transaction to the back stack
            .commit()
    }

}


