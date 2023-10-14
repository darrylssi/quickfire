package nz.ac.uclive.ajs418.quickfire.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import nz.ac.uclive.ajs418.quickfire.MainActivity
import nz.ac.uclive.ajs418.quickfire.service.BluetoothServerService
import nz.ac.uclive.ajs418.quickfire.R
import nz.ac.uclive.ajs418.quickfire.service.BluetoothServiceCallback
import nz.ac.uclive.ajs418.quickfire.viewmodel.UserViewModel

class HomeFragment : Fragment(), BluetoothServiceCallback {
    private val REQUEST_BLUETOOTH_PERMISSION = 1
    private lateinit var bluetoothServerService: BluetoothServerService
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
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val createPartyButton = view.findViewById<Button>(R.id.createPartyButton)
        val joinPartyButton = view.findViewById<Button>(R.id.joinPartyButton)
        val soloPlayButton = view.findViewById<Button>(R.id.soloPlayButton)
        bluetoothServerService = BluetoothServerService()
        bluetoothServerService.setCallback(this)

        createPartyButton.setOnClickListener {
            replaceWithConnect(false)
        }

        joinPartyButton.setOnClickListener {
            enableDiscovery()
        }

        soloPlayButton.setOnClickListener {
            replaceWithPlay()
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
            replaceWithConnect(true)
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

    override fun onDataReceived(data: String) {
        // Handle the received data here
    }

}