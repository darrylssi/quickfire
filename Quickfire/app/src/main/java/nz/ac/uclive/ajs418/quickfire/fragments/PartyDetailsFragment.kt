package nz.ac.uclive.ajs418.quickfire.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import nz.ac.uclive.ajs418.quickfire.MainActivity
import nz.ac.uclive.ajs418.quickfire.R
import nz.ac.uclive.ajs418.quickfire.adapters.MediaAdapter
import nz.ac.uclive.ajs418.quickfire.entity.Media
import nz.ac.uclive.ajs418.quickfire.entity.Party
import nz.ac.uclive.ajs418.quickfire.viewmodel.MediaViewModel
import nz.ac.uclive.ajs418.quickfire.viewmodel.PartyViewModel

// Fragment responsible for displaying media details for a selected party
class PartyDetailsFragment(selectedParty : Party) : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var mediaAdapter: MediaAdapter

    private lateinit var partyViewModel: PartyViewModel
    private lateinit var mediaViewModel: MediaViewModel

    // Hardcoded dummy media for the selected party
    private val dummyMedia = listOf(
        Media("Movie 1", 2022, "Movie", 7.5f, "Movie synopsis 1", null),
        Media("Movie 2", 2020, "Movie", 8.0f, "Movie synopsis 2", null),
        Media("TV Show 1", 2021, "TV Show", 8.5f, "TV show synopsis 1", null),
    )
    private val party = selectedParty
    private val mediaIds = party.matches

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_party_details, container, false)

        partyViewModel = (requireActivity() as MainActivity).getPartyViewModelInstance()
        mediaViewModel = (requireActivity() as MainActivity).getMediaViewModelInstance()
        //var matches = getMedia(mediaIds)
        var matches = dummyMedia
        var id = party.id
        Log.d("PartyDetailsFrag", "Media Ids: $mediaIds")
        Log.d("PartyDetailsFrag", "Party ID: $id")

        // Initialize RecyclerView and its adapter for media
        recyclerView = view.findViewById(R.id.mediaRecyclerView)
        mediaAdapter = MediaAdapter(matches)

        // Set a LinearLayoutManager for the RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Set the adapter for the RecyclerView
        recyclerView.adapter = mediaAdapter

        return view
    }

//    private fun getMedia(mediaIds: ArrayList<String>): List<Media> {
//        Log.d("PDF", "GetMedia")
//        val medias = ArrayList<Media>()
//        Log.d("PDF", "Test")
//        for (mediaIdString in mediaIds) {
//            var mediaId = mediaIdString
//            Log.d("PDF", "Test2")
//            Log.d("PDF", "MediaId: $mediaId")
//            val media = mediaViewModel.getById(mediaId)
//            media?.let {
//                medias.add(it)
//            }
//        }
//        return medias
//    }
}
