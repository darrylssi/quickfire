package nz.ac.uclive.ajs418.quickfire.fragments

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import nz.ac.uclive.ajs418.quickfire.R
import java.io.IOException
import java.util.UUID

class HomeFragment : Fragment() {
    private val APP_NAME = "nz.ac.uclive.ajs418.quickfire"
    private val REQUEST_BLUETOOTH_PERMISSION = 2
    private val REQUEST_BLUETOOTH_DISCOVERABILITY = 3
    val MY_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val createPartyButton = view.findViewById<Button>(R.id.createPartyButton)
        val joinPartyButton = view.findViewById<Button>(R.id.joinPartyButton)
        val soloPlayButton = view.findViewById<Button>(R.id.soloPlayButton)

        createPartyButton.setOnClickListener {
            replaceWithConnect(false)
        }

        joinPartyButton.setOnClickListener {
            enableDiscovery()
            replaceWithConnect(true)
        }

        soloPlayButton.setOnClickListener {
            replaceWithPlay()
        }
    }

    private fun enableDiscovery() {
//        val bluetoothManager: BluetoothManager? = requireContext().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager?
//        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager?.adapter

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED) {
            // BLUETOOTH permission is already granted, proceed with accessing paired devices
            listenForIncomingConnections()
            // Ensure Bluetooth is enabled
//            if (bluetoothAdapter != null && bluetoothAdapter.isEnabled) {
//                // Make the device discoverable
//                val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
//                    putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
//                }
//                startActivityForResult(discoverableIntent, REQUEST_BLUETOOTH_DISCOVERABILITY)
//            }
        } else {
            // BLUETOOTH permission is not granted, request it
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.BLUETOOTH), REQUEST_BLUETOOTH_PERMISSION)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_BLUETOOTH_DISCOVERABILITY && resultCode == Activity.RESULT_OK) {
            // Start listening for incoming connections
            listenForIncomingConnections()
        }
    }

    private fun listenForIncomingConnections() {
        Log.d("Home Fragment", "TestA")
        val bluetoothManager: BluetoothManager? = requireContext().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager?
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager?.adapter

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED) {
            // BLUETOOTH permission is already granted, proceed with accessing paired devices
        } else {
            // BLUETOOTH permission is not granted, request it
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.BLUETOOTH), REQUEST_BLUETOOTH_PERMISSION)
        }
        Log.d("HomeFragment", "Listen")
        val serverSocket: BluetoothServerSocket? = bluetoothAdapter?.listenUsingRfcommWithServiceRecord(APP_NAME, MY_UUID)
        Log.d("HomeFragment", "ListenFinished?")

        serverSocket?.let {
            try {
                Log.d("Home Fragment", "Test")
                val socket: BluetoothSocket = it.accept() // Accepts incoming connections
                Log.d("Home Fragment", socket.toString())
                // Connection accepted, handle further logic here
                Log.d("Home Fragment", "Test")
            } catch (e: IOException) {
                e.printStackTrace()
                // Handle connection failure
            }
        }
    }


    private fun replaceWithConnect(isMember: Boolean) {
        val bundle = Bundle()
        bundle.putBoolean("isMember", isMember)

        val fragmentTransaction = parentFragmentManager.beginTransaction()
        val connectFragment = ConnectFragment()
        connectFragment.arguments = bundle

        fragmentTransaction.replace(R.id.fragmentContainer, connectFragment)
            .commit()
    }

    private fun replaceWithPlay() {
        val playFragment = PlayFragment()

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, playFragment)
            .commit()
    }

}