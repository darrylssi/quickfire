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
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import nz.ac.uclive.ajs418.quickfire.R
import nz.ac.uclive.ajs418.quickfire.dao.PartyDao
import nz.ac.uclive.ajs418.quickfire.dao.UserDao
import nz.ac.uclive.ajs418.quickfire.database.QuickfireDatabase
import nz.ac.uclive.ajs418.quickfire.entity.Party
import nz.ac.uclive.ajs418.quickfire.entity.User
import nz.ac.uclive.ajs418.quickfire.fragments.PlayFragment
import nz.ac.uclive.ajs418.quickfire.repository.PartyRepository
import nz.ac.uclive.ajs418.quickfire.repository.UserRepository
import nz.ac.uclive.ajs418.quickfire.viewmodel.PartyViewModel
import nz.ac.uclive.ajs418.quickfire.viewmodel.UserViewModel

class ConnectFragment : Fragment() {

    private lateinit var partyViewModel: PartyViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var currentUser: User
    private lateinit var startMatchButton: Button

    private val REQUEST_ENABLE_BT = 1
    private val REQUEST_BLUETOOTH_SCAN_PERMISSION = 2
    private val CONNECT_FRAGMENT_TEXT = "Connect Fragment"
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d("ConnectFragment", "test")
            when (intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    Log.d("ConnectFragment", "test2")
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
                    // val deviceName = device.name
                    val deviceAddress = device.address
                    Log.d("ConnectFragment", deviceAddress)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("ConnectFragment", "On View Created")


        // Set up Bluetooth receiver
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

        // Initialize user data and start match button
        initializeUserData(view)
    }

    private fun initializeUserData(view: View) {
        Log.e("ConnectFragment", "Initialize User Data")

        val partyDao: PartyDao = QuickfireDatabase.getDatabase(requireContext()).partyDao()
        val partyRepository: PartyRepository by lazy { PartyRepository(partyDao) }
        partyViewModel = PartyViewModel(partyRepository)

        val userDao: UserDao = QuickfireDatabase.getDatabase(requireContext()).userDao()
        val userRepository: UserRepository by lazy { UserRepository(userDao) }
        userViewModel = UserViewModel(userRepository)

        Log.e("ConnectFragment", "Parties 1 -> " + partyViewModel.parties)


        // Load or create the current user
        val usersLiveData: LiveData<List<User>> = userViewModel.users
        usersLiveData.observe(viewLifecycleOwner) { users ->
            if (users.isNotEmpty()) {
                currentUser = users[0]
            } else {
                // If no user exists, create a new one
                currentUser = User("Sahil", "light")
                userViewModel = UserViewModel(userRepository)
                userViewModel.addUser(currentUser)
            }

            startMatchButton = view.findViewById(R.id.startMatchButton)
            startMatchButton.setOnClickListener {
//                // Gets the existing party if exists
//                val partyByName = partyViewModel.getPartyByName(currentUser.name)
//
//                // Gets current parties (list)
//                val partiesLiveData: LiveData<List<Party>> = partyViewModel.parties
//                partiesLiveData.observe(viewLifecycleOwner) { parties ->
//                    Log.e("ConnectFragment", "Parties inside observe -> " + parties)
//
//                    // Checks if the party already exists
//                    if (parties.contains(partyByName)) {
//                        Log.e("ConnectFragment", "Party already exists, so instead create other named party")
//
//                        }
//                    }
//                }


                // Create a new party with the current user as the initiator
                val party = Party(
                    "Party Name", arrayListOf(currentUser.id),
                    arrayListOf()
                )

                // Start a coroutine to insert the party into the database
                GlobalScope.launch(Dispatchers.IO) {
                    val partyId = partyViewModel.addParty(party)

                    // Navigate to the PlayFragment with the party details
                    val fragmentTransaction = parentFragmentManager.beginTransaction()
                    val playFragment = PlayFragment()

                    // Pass the party details to the PlayFragment
                    val bundle = Bundle()
                    bundle.putParcelable("party", party)
                    playFragment.arguments = bundle

                    fragmentTransaction.replace(R.id.fragmentContainer, playFragment)
                        .commit()
                }
            }
        }
        Log.e("ConnectFragment", "Parties 2 -> " + partyViewModel.parties)
    }

    private fun disableButton(button: Button, view: View) {
        button.isEnabled = false
        button.isClickable = false
        button.setBackgroundColor(ContextCompat.getColor(view.context, R.color.grey))
        button.setTextColor(ContextCompat.getColor(view.context, R.color.white))
    }

    private fun initializeBluetoothConnection() {
        Log.d(CONNECT_FRAGMENT_TEXT, "Initializing Bluetooth Connection")
        val bluetoothManager: BluetoothManager? = requireContext().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager?
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager?.adapter

        // CHECK IF DEVICE SUPPORTS BLUETOOTH
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Log.d(CONNECT_FRAGMENT_TEXT, "Bluetooth Not Supported")
            return
        } else if (!bluetoothAdapter.isEnabled) {
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
