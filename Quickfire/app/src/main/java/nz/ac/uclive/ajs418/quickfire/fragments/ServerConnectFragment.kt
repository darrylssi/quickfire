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
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nz.ac.uclive.ajs418.quickfire.MainActivity
import nz.ac.uclive.ajs418.quickfire.R
import nz.ac.uclive.ajs418.quickfire.entity.Party
import nz.ac.uclive.ajs418.quickfire.entity.User
import nz.ac.uclive.ajs418.quickfire.service.BluetoothServerService
import nz.ac.uclive.ajs418.quickfire.service.BluetoothServiceCallback
import nz.ac.uclive.ajs418.quickfire.viewmodel.LikeViewModel
import nz.ac.uclive.ajs418.quickfire.viewmodel.MediaViewModel
import nz.ac.uclive.ajs418.quickfire.viewmodel.PartyViewModel
import nz.ac.uclive.ajs418.quickfire.viewmodel.UserViewModel

class ServerConnectFragment : Fragment(), BluetoothServiceCallback {
    private val REQUEST_BLUETOOTH_PERMISSION = 1
    private lateinit var bluetoothServerService: BluetoothServerService

    private lateinit var userViewModel: UserViewModel
    private lateinit var partyViewModel: PartyViewModel
    private lateinit var likeViewModel: LikeViewModel
    private lateinit var mediaViewModel: MediaViewModel

    private var serverUser = User("", "")
    private var clientUser = User("", "")

    private var party = Party("", arrayListOf(), arrayListOf())

    private lateinit var coroutineScope: CoroutineScope

    override fun onAttach(context: Context) {
        super.onAttach(context)
        userViewModel = (requireActivity() as MainActivity).getUserViewModelInstance()
        partyViewModel = (requireActivity() as MainActivity).getPartyViewModelInstance()
        likeViewModel = (requireActivity() as MainActivity).getLikeViewModelInstance()
        mediaViewModel = (requireActivity() as MainActivity).getMediaViewModelInstance()
        coroutineScope = CoroutineScope(Dispatchers.Main + Job())
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

    override suspend fun onDataReceived(string: String) {
        Log.d("ServerConnectFragment", string)
        // Handle the received data here
        if (string.startsWith("client_name:")) {
            val username = string.substringAfter("client_name:")
            Log.d("SCF Client Name ODR", username)
            clientUser = User(username, "CLIENT")
            lifecycleScope.launch { userViewModel.addUser(clientUser) }
        }
        if (string.startsWith("server_name:")) {
            val username = string.substringAfter("server_name:")
            serverUser = User(username, "SERVER")
            lifecycleScope.launch { userViewModel.addUser(serverUser) }

        }
        if (string.startsWith("party_name:")) {
            val partyName = string.substringAfter("party_name:")
            Log.d("SCF: Client Username", clientUser.name)
            Log.d("SCF: Server Username", serverUser.name)
            lifecycleScope.launch {
                val client = getUserByName(clientUser.name)
                val server = getUserByName(serverUser.name)
                val clientID = client?.id
                val serverID = server?.id
                Log.d("CCF: Client ", "ID: $clientID")
                Log.d("CCF: Server ", "ID: $serverID")
                val partyMembers = ArrayList<Long>().apply {
                    if (client != null) {
                        add(client.id)
                    }
                    if (server != null) {
                        add(server.id)
                    }
                }
                party = Party(partyName, partyMembers, arrayListOf()) //  Matches is initially empty
                lifecycleScope.launch { partyViewModel.addParty(party) }
                lifecycleScope.launch { partyViewModel.setCurrentName(party.name) }
                switchToServerPlayFragment(bluetoothServerService)
            }
        }
    }

    private suspend fun getUserByName(name : String) : User? {
        return withContext(coroutineScope.coroutineContext + Dispatchers.IO) {
            userViewModel.getUserByName(name)
        }
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