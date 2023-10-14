package nz.ac.uclive.ajs418.quickfire.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import nz.ac.uclive.ajs418.quickfire.MainActivity
import nz.ac.uclive.ajs418.quickfire.R
import nz.ac.uclive.ajs418.quickfire.service.BluetoothServerService
import nz.ac.uclive.ajs418.quickfire.service.BluetoothServiceCallback
import nz.ac.uclive.ajs418.quickfire.viewmodel.PartyViewModel
import nz.ac.uclive.ajs418.quickfire.viewmodel.UserViewModel

class ServerConnectFragment : Fragment(), BluetoothServiceCallback {
    private val REQUEST_BLUETOOTH_PERMISSION = 1
    private lateinit var bluetoothServerService: BluetoothServerService
    private lateinit var userViewModel: UserViewModel
    private lateinit var partyViewModel: PartyViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        userViewModel = (requireActivity() as MainActivity).getUserViewModelInstance()
        partyViewModel = (requireActivity() as MainActivity).getPartyViewModelInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_connect_server, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bluetoothServerService = BluetoothServerService()
        bluetoothServerService.setCallback(this)

        val addButton = view.findViewById<Button>(R.id.enableDiscovery)
        addButton.setOnClickListener {
            enableDiscovery()
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

    private fun enableDiscovery() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED) {
            listenForIncomingConnections()
        } else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.BLUETOOTH), REQUEST_BLUETOOTH_PERMISSION)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_BLUETOOTH_PERMISSION && resultCode == Activity.RESULT_OK) {
            // Start listening for incoming connections
            listenForIncomingConnections()
        }
    }

    private fun listenForIncomingConnections() {
        bluetoothServerService.acceptConnections(requireContext(), requireActivity()) {
            // Do something after connection established
        }
    }

    override fun onDataReceived(string: String) {
        // Handle the received data here
        // If string starts with 'client_name:'
        // val user = client_name?.let { User(it, "CLIENT") }
        // lifecycleScope.launch { userViewModel.addUser(user) }
        // If string starts with 'server_name:'
        // val user = server_name?.let { User(it, "SERVER") }
        // lifecycleScope.launch { userViewModel.addUser(user) }
        // If string starts with 'party_name:'
        // val party = party_name?.let { Party(it, members, matches }
    }

    private fun sendData(data: String) {
        bluetoothServerService.writeData(data)
    }

    private fun switchToServerPlayFragment(bluetoothServerService: BluetoothServerService) {
        val serverPlayFragment = ServerPlayFragment()
        serverPlayFragment.setBluetoothServerService(bluetoothServerService)

        val fragmentTransaction = parentFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, serverPlayFragment)
            .addToBackStack(null) // Optional: Adds the transaction to the back stack
            .commit()
    }
}