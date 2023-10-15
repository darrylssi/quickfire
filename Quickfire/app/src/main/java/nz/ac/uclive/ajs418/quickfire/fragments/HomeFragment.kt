package nz.ac.uclive.ajs418.quickfire.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nz.ac.uclive.ajs418.quickfire.MainActivity
import nz.ac.uclive.ajs418.quickfire.R
import nz.ac.uclive.ajs418.quickfire.entity.Party
import nz.ac.uclive.ajs418.quickfire.entity.User
import nz.ac.uclive.ajs418.quickfire.viewmodel.PartyViewModel
import nz.ac.uclive.ajs418.quickfire.viewmodel.UserViewModel

class HomeFragment : Fragment() {

    private lateinit var partyViewModel: PartyViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var soloPlayButton: Button

    private lateinit var coroutineScope: CoroutineScope

    override fun onAttach(context: Context) {
        super.onAttach(context)
        coroutineScope = CoroutineScope(Dispatchers.Main + Job())
        userViewModel = (requireActivity() as MainActivity).getUserViewModelInstance()
        partyViewModel = (requireActivity() as MainActivity).getPartyViewModelInstance()
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

        createPartyButton.setOnClickListener {
            replaceWithClientConnect()
        }

        joinPartyButton.setOnClickListener {
            replaceWithServerConnect()
        }

        lifecycleScope.launch {
            initializeSoloPlay(view)
        }
    }

    private fun replaceWithClientConnect() {
        val fragmentTransaction = parentFragmentManager.beginTransaction()
        val clientConnectFragment = ClientConnectFragment()

        fragmentTransaction.replace(R.id.fragmentContainer, clientConnectFragment)
            .commit()
    }

    private fun replaceWithServerConnect() {
        val fragmentTransaction = parentFragmentManager.beginTransaction()
        val serverConnectFragment = ServerConnectFragment()

        fragmentTransaction.replace(R.id.fragmentContainer, serverConnectFragment)
            .commit()
    }

    private suspend fun initializeSoloPlay(view: View) {
        Log.e("HomeFragment", "Initialize User Data")

        // Load or create the current user
        val currUserCheck: User? = withContext(coroutineScope.coroutineContext + Dispatchers.IO) {
            userViewModel.getUserByName("Me")
        }

        // Load or create the current party
        val currPartyCheck: Party? = withContext(coroutineScope.coroutineContext + Dispatchers.IO) {
            partyViewModel.getPartyByName("My Party")
        }

        // Observe users and parties LiveData
        val usersLiveData: LiveData<List<User>> = userViewModel.users
        val partiesLiveData: LiveData<List<Party>> = partyViewModel.parties

        usersLiveData.observe(viewLifecycleOwner) { users ->
            if (users.isNotEmpty()) {
                if (users.contains(currUserCheck)) {
                    userViewModel.setId(currUserCheck!!.id)
                } else {
                    // If no user exists, create a new one
                    val currentUser = User("Me", "light")
                    userViewModel.addUser(currentUser)
                    userViewModel.setId(currentUser.id)
                }
                Log.e("HomeFragment", "User 1 -> " + userViewModel.currentId)
            }
        }

        partiesLiveData.observe(viewLifecycleOwner) { parties ->
            if (parties.isNotEmpty()) {
                if (currPartyCheck != null && parties.contains(currPartyCheck)) {
                    partyViewModel.setCurrentParty(currPartyCheck!!.id)
                } else {
                    // If no party exists, create a new one
                    val currentParty = Party("My Party", arrayListOf(userViewModel.currentId), arrayListOf())
                    partyViewModel.addParty(currentParty)
                    partyViewModel.setCurrentParty(currentParty.id)
                }
                Log.e("HomeFragment", "Party 1 -> " + partyViewModel.currentPartyId)
            }
        }

        soloPlayButton = view.findViewById(R.id.soloPlayButton)
        soloPlayButton.setOnClickListener {
            // Start a coroutine to insert the party into the database
            GlobalScope.launch(Dispatchers.IO) {
                // Navigate to the PlayFragment
                val fragmentTransaction = parentFragmentManager.beginTransaction()
                val playFragment = PlayFragment()

                fragmentTransaction.replace(R.id.fragmentContainer, playFragment)
                    .commit()
            }
            Log.e("HomeFragment", "Parties 2 -> " + partyViewModel.parties)
        }
    }




}