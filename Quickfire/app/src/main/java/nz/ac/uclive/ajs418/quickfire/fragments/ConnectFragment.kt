import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
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
import nz.ac.uclive.ajs418.quickfire.viewmodel.UserViewModel

class ConnectFragment : Fragment() {

    private lateinit var partyRepository: PartyRepository
    private lateinit var userViewModel: UserViewModel
    private lateinit var currentUser: User
    private lateinit var startMatchButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_connect, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userDao: UserDao = QuickfireDatabase.getDatabase(requireContext()).userDao()
        val userRepository: UserRepository by lazy { UserRepository(userDao) }
        partyRepository = PartyRepository(QuickfireDatabase.getDatabase(requireContext()).partyDao())

        // Load or create the current user
        val usersLiveData: LiveData<List<User>> = userRepository.users
        usersLiveData.observe(viewLifecycleOwner, { users ->
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
                // Create a new party with the current user as the initiator
                val party = Party("Party Name", listOf(currentUser.id), emptyList())

                // Start a coroutine to insert the party into the database
                GlobalScope.launch(Dispatchers.IO) {
                    val partyId = partyRepository.insert(party)

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
        })
    }
}
