package nz.ac.uclive.ajs418.quickfire.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import nz.ac.uclive.ajs418.quickfire.R
import nz.ac.uclive.ajs418.quickfire.adapters.MediaAdapter
import nz.ac.uclive.ajs418.quickfire.dao.MediaDao
import nz.ac.uclive.ajs418.quickfire.dao.PartyDao
import nz.ac.uclive.ajs418.quickfire.database.QuickfireDatabase
import nz.ac.uclive.ajs418.quickfire.entity.Media
import nz.ac.uclive.ajs418.quickfire.entity.Party
import nz.ac.uclive.ajs418.quickfire.repository.MediaRepository
import nz.ac.uclive.ajs418.quickfire.repository.PartyRepository
import nz.ac.uclive.ajs418.quickfire.viewmodel.MediaViewModel
import nz.ac.uclive.ajs418.quickfire.viewmodel.PartyViewModel

// Fragment responsible for displaying media details for a selected party
class PartyDetailsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var mediaAdapter: MediaAdapter
    private lateinit var mediaViewModel: MediaViewModel


    // Hardcoded dummy media for the selected party
    private val medias = arrayOf(
        Media("Movie 1", 2022, "Movie", 7.5f, "Movie synopsis 1", null),
        Media("Movie 2", 2020, "Movie", 8.0f, "Movie synopsis 2", null),
        Media("TV Show 1", 2021, "TV Show", 8.5f, "TV show synopsis 1", null),
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_party_details, container, false)

        // Gets medias at given party id from database
        val mediaDao: MediaDao = QuickfireDatabase.getDatabase(requireContext()).mediaDao()
        val mediaRepository: MediaRepository by lazy { MediaRepository(mediaDao) }
        mediaViewModel = MediaViewModel(mediaRepository)
//        medias = mediaViewModel.mediaList  as Array<Media>

        // Initialize RecyclerView and its adapter for media
        recyclerView = view.findViewById(R.id.mediaRecyclerView)
        mediaAdapter = MediaAdapter(medias)

        // Set a LinearLayoutManager for the RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Set the adapter for the RecyclerView
        recyclerView.adapter = mediaAdapter

        return view
    }
}
