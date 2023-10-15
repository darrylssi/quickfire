package nz.ac.uclive.ajs418.quickfire.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import nz.ac.uclive.ajs418.quickfire.R
import nz.ac.uclive.ajs418.quickfire.dao.PartyDao
import nz.ac.uclive.ajs418.quickfire.dao.UserDao
import nz.ac.uclive.ajs418.quickfire.database.QuickfireDatabase
import nz.ac.uclive.ajs418.quickfire.entity.Party
import nz.ac.uclive.ajs418.quickfire.entity.User
import nz.ac.uclive.ajs418.quickfire.repository.PartyRepository
import nz.ac.uclive.ajs418.quickfire.repository.UserRepository
import nz.ac.uclive.ajs418.quickfire.viewmodel.PartyViewModel
import nz.ac.uclive.ajs418.quickfire.viewmodel.UserViewModel

class HomeFragment : Fragment() {

    private lateinit var partyViewModel: PartyViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var currentUser: User
    private lateinit var soloPlayButton: Button

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

        initializeUserData(view)
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

    private fun initializeUserData(view: View) {
        Log.e("HomeFragment", "Initialize User Data")

        val partyDao: PartyDao = QuickfireDatabase.getDatabase(requireContext()).partyDao()
        val partyRepository: PartyRepository by lazy { PartyRepository(partyDao) }
        partyViewModel = PartyViewModel(partyRepository)

        val userDao: UserDao = QuickfireDatabase.getDatabase(requireContext()).userDao()
        val userRepository: UserRepository by lazy { UserRepository(userDao) }
        userViewModel = UserViewModel(userRepository)

        Log.e("HomeFragment", "Parties 1 -> " + partyViewModel.parties)


        // Load or create the current user
        val usersLiveData: LiveData<List<User>> = userViewModel.users
        usersLiveData.observe(viewLifecycleOwner) { users ->
            // If no user exists, create a new one
            currentUser = User("My", "light")
            userViewModel = UserViewModel(userRepository)
            userViewModel.addUser(currentUser)

            Log.e("HomeFragment", "Curr User -> " + currentUser)

            soloPlayButton = view.findViewById(R.id.soloPlayButton)
            soloPlayButton.setOnClickListener {
//                // Gets the existing solo party if exists
//                val partyByName = partyViewModel.getPartyByName(currentUser.name)
//
//                // Gets current parties (list)
//                val partiesLiveData: LiveData<List<Party>> = partyViewModel.parties
//                partiesLiveData.observe(viewLifecycleOwner) { parties ->
//                    Log.e("HomeFragment", "Parties inside observe -> " + parties)
//
//                    // Checks if the party already exists
//                    if (parties.contains(partyByName)) {
//                        Log.e("HomeFragment", "Yes, it is here")
//
//                        // Start a coroutine to insert the party into the database
//                        GlobalScope.launch(Dispatchers.IO) {
//                            val partyId = partyViewModel.getPartyByName(currentUser.name)
//
//                            // Navigate to the PlayFragment with the party details
//                            val fragmentTransaction = parentFragmentManager.beginTransaction()
//                            val playFragment = PlayFragment()
//
//                            // Pass the party details to the PlayFragment
//                            val bundle = Bundle()
//                            bundle.putParcelable("party", party)
//                            playFragment.arguments = bundle
//
//                            fragmentTransaction.replace(R.id.fragmentContainer, playFragment)
//                                .commit()
//                        }
//                    }
//                }

                // Create a new party with the current user as the initiator
                val party = Party(
                    currentUser.name, arrayListOf(currentUser.id),
                    arrayListOf()
                )
                Log.e("HomeFragment", "Party -> " + party)

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
        Log.e("HomeFragment", "Parties 2 -> " + partyViewModel.parties)
    }

}