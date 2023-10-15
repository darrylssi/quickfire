package nz.ac.uclive.ajs418.quickfire.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import nz.ac.uclive.ajs418.quickfire.MainActivity
import nz.ac.uclive.ajs418.quickfire.R
import nz.ac.uclive.ajs418.quickfire.adapters.PartyAdapter
import nz.ac.uclive.ajs418.quickfire.entity.Party
import nz.ac.uclive.ajs418.quickfire.viewmodel.PartyViewModel


class MatchesFragment : Fragment(), PartyAdapter.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var testAdapter: PartyAdapter

    private lateinit var partyViewModel: PartyViewModel

    private var partyArray : Array<Party>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the fragment's layout
        val view = inflater.inflate(R.layout.fragment_matches, container, false)

        partyViewModel = (requireActivity() as MainActivity).getPartyViewModelInstance()

        partyViewModel.parties.observe(viewLifecycleOwner) { parties ->
            // Initialize RecyclerView and its adapter
            recyclerView = view.findViewById(R.id.partyRecyclerView)
            testAdapter = PartyAdapter(parties, this)

            // Set a LinearLayoutManager for the RecyclerView
            recyclerView.layoutManager = LinearLayoutManager(requireContext())

            // Set the adapter for the RecyclerView
            recyclerView.adapter = testAdapter
        }

        return view
    }


    override fun onItemClick(party: Party) {
        // Handle item click event and log party details
        Log.d("MatchesFragment", "Item clicked: $party")

        // Create a fragment transaction to navigate to PartyDetailsFragment
        val fragmentTransaction = parentFragmentManager.beginTransaction()
        val partyDetailsFragment = PartyDetailsFragment(party)

        // Pass the selected party's ID to the PartyDetailsFragment using a Bundle
        val bundle = Bundle()
        partyArray?.indexOf(party)?.let { bundle.putLong("partyId", it.toLong()) }
        partyDetailsFragment.arguments = bundle

        // Replace the current fragment with PartyDetailsFragment and add to the back stack for navigation
        fragmentTransaction.replace(R.id.fragmentContainer, partyDetailsFragment)
            .addToBackStack(null)
            .commit()
    }

}