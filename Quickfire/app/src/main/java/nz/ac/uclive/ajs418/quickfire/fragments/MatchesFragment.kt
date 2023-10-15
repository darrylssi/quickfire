package nz.ac.uclive.ajs418.quickfire.fragments

import android.os.Bundle
import android.util.Log // Import the Log class
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import nz.ac.uclive.ajs418.quickfire.R
import nz.ac.uclive.ajs418.quickfire.adapters.PartyAdapter
import nz.ac.uclive.ajs418.quickfire.dao.PartyDao
import nz.ac.uclive.ajs418.quickfire.database.QuickfireDatabase
import nz.ac.uclive.ajs418.quickfire.entity.Party
import nz.ac.uclive.ajs418.quickfire.repository.PartyRepository
import nz.ac.uclive.ajs418.quickfire.viewmodel.PartyViewModel

// Fragment responsible for displaying a list of parties
class MatchesFragment : Fragment(), PartyAdapter.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var partyAdapter: PartyAdapter
    private lateinit var partyViewModel: PartyViewModel

    // Create simplified dummy data for parties
    private val parties = arrayOf<Party>(
        Party("Party 1", arrayListOf(1, 2, 3), arrayListOf(101, 102, 103)),
        Party("Party 2", arrayListOf(4, 5, 6), arrayListOf(104, 105, 106)),
        Party("Party 3", arrayListOf(7, 8, 9), arrayListOf(107, 108, 109)),
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the fragment's layout
        val view = inflater.inflate(R.layout.fragment_matches, container, false)

        // Gets parties from database
        val partyDao: PartyDao = QuickfireDatabase.getDatabase(requireContext()).partyDao()
        val partyRepository: PartyRepository by lazy { PartyRepository(partyDao) }
        partyViewModel = PartyViewModel(partyRepository)
//        parties = partyViewModel.parties  as Array<Party>

        // Initialize RecyclerView and its adapter
        recyclerView = view.findViewById(R.id.partyRecyclerView)
        partyAdapter = PartyAdapter(parties, this)

        // Set a LinearLayoutManager for the RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Set the adapter for the RecyclerView
        recyclerView.adapter = partyAdapter

        return view
    }

    override fun onItemClick(party: Party) {
        // Handle item click event and log party details
        Log.d("MatchesFragment", "Item clicked: $party")

        // Create a fragment transaction to navigate to PartyDetailsFragment
        val fragmentTransaction = parentFragmentManager.beginTransaction()
        val partyDetailsFragment = PartyDetailsFragment()

        // Pass the selected party's ID to the PartyDetailsFragment using a Bundle
        val bundle = Bundle()
        bundle.putLong("partyId", parties.indexOf(party).toLong())
        partyDetailsFragment.arguments = bundle

        // Replace the current fragment with PartyDetailsFragment and add to the back stack for navigation
        fragmentTransaction.replace(R.id.fragmentContainer, partyDetailsFragment)
            .addToBackStack(null)
            .commit()
    }
}
