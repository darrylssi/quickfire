package nz.ac.uclive.ajs418.quickfire.fragments

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nz.ac.uclive.ajs418.quickfire.MainActivity
import nz.ac.uclive.ajs418.quickfire.R
import nz.ac.uclive.ajs418.quickfire.database.QuickfireDatabase
import nz.ac.uclive.ajs418.quickfire.entity.Like
import nz.ac.uclive.ajs418.quickfire.entity.Media
import nz.ac.uclive.ajs418.quickfire.repository.MediaRepository
import nz.ac.uclive.ajs418.quickfire.service.BluetoothServerService
import nz.ac.uclive.ajs418.quickfire.service.BluetoothServiceCallback
import nz.ac.uclive.ajs418.quickfire.viewmodel.LikeViewModel
import nz.ac.uclive.ajs418.quickfire.viewmodel.MediaViewModel
import nz.ac.uclive.ajs418.quickfire.viewmodel.PartyViewModel
import nz.ac.uclive.ajs418.quickfire.viewmodel.UserViewModel

class ServerPlayFragment : Fragment(), BluetoothServiceCallback {
    private lateinit var bluetoothServerService: BluetoothServerService

    private lateinit var userViewModel: UserViewModel
    private lateinit var partyViewModel: PartyViewModel
    private lateinit var likeViewModel: LikeViewModel
    private lateinit var mediaViewModel: MediaViewModel

    // UI elements
    private lateinit var posterView: ImageView
    private lateinit var titleText: TextView
    private lateinit var yearText: TextView
    private lateinit var ratingBar: RatingBar
    private lateinit var typeText: TextView
    private lateinit var yesButton: Button
    private lateinit var noButton: Button

    private lateinit var mediaRepository: MediaRepository
    private var currentMedia: Media? = null
    private var currentPartyId: Long = 0L
    private var currentUserId: Long = 0L

    private lateinit var coroutineScope: CoroutineScope

    private var startX = 0f
    private var startY = 0f
    private val SWIPE_THRESHOLD = 100

    fun setBluetoothServerService(service: BluetoothServerService) {
        this.bluetoothServerService = service
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        userViewModel = (requireActivity() as MainActivity).getUserViewModelInstance()
        partyViewModel = (requireActivity() as MainActivity).getPartyViewModelInstance()
        likeViewModel = (requireActivity() as MainActivity).getLikeViewModelInstance()
        mediaViewModel = (requireActivity() as MainActivity).getMediaViewModelInstance()
        currentPartyId = partyViewModel.currentId
        currentUserId = userViewModel.currentId
        coroutineScope = CoroutineScope(Dispatchers.Main + Job())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_play, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bluetoothServerService.setCallback(this)

        // Initialize UI elements
        posterView = view.findViewById(R.id.posterView)
        titleText = view.findViewById(R.id.titleText)
        yearText = view.findViewById(R.id.yearText)
        ratingBar = view.findViewById(R.id.ratingBar)
        typeText = view.findViewById(R.id.typeText)
        yesButton = view.findViewById(R.id.yesButton)
        noButton = view.findViewById(R.id.noButton)

        // Initialize the MediaRepository with the MediaDao
        val mediaDao = QuickfireDatabase.getDatabase(requireContext()).mediaDao()
        mediaRepository = MediaRepository(mediaDao)

        // Load initial random media
        loadRandomMedia()

        // Handle the "Random" button click
        yesButton.setOnClickListener {
            lifecycleScope.launch {
                handleYesButtonClick()
            }
        }

        noButton.setOnClickListener {
            loadRandomMedia()
        }

        // Set up swipe gesture detection
        setupSwipeGestureDetection()
    }

    // Function to load a random media item
    private fun loadRandomMedia() {
        lifecycleScope.launch(Dispatchers.IO) {
            currentMedia = mediaRepository.getRandomMedia()
            displayMedia(currentMedia)
        }
    }

    // Function to set up swipe gesture detection for posterView
    private fun setupSwipeGestureDetection() {
        posterView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = event.x
                    startY = event.y
                    true
                }
                MotionEvent.ACTION_UP -> {
                    val endX = event.x
                    val endY = event.y
                    val deltaX = endX - startX
                    val deltaY = endY - startY

                    if (Math.abs(deltaX) > SWIPE_THRESHOLD) {
                        if (deltaX > 0) {
                            // Swipe right, perform the same action as clicking "Yes"
                            lifecycleScope.launch {
                                handleSwipeRight()
                            }
                        } else {
                            // Swipe left, perform the same action as clicking "No"
                            handleSwipeLeft()
                        }
                        true
                    } else if (Math.abs(deltaX) < 5 && Math.abs(deltaY) < 5) {
                        // A small movement, consider it a click
                        posterView.performClick()
                        true
                    } else {
                        // Not a swipe or a click
                        false
                    }
                }
                else -> false
            }
        }
    }

    private suspend fun handleYesButtonClick() {
        currentMedia?.let {
            addLike(it.id)
        }
        loadRandomMedia()
    }

    // Function to handle swipe right action
    private suspend fun handleSwipeRight() {
        currentMedia?.let {
            coroutineScope.launch {
                addLike(it.id)
            }
        }
        loadRandomMedia()
    }

    // Function to handle swipe left action
    private fun handleSwipeLeft() {
        loadRandomMedia()
    }

    private suspend fun addLike(currentMediaId: Long) {
        Log.d("SPF", "Adding like")
        val likeInstance =
            withContext(coroutineScope.coroutineContext + Dispatchers.IO) {
                getLikesByPartyAndMedia(currentPartyId, currentMediaId)
            }
        if (likeInstance != null) {
            // likeInstance is not null, you can use it here
            if (likeInstance.likedBy != currentUserId) {
                // Match found
                Log.d("SPF", "Match Found")
                partyViewModel.addMatchToParty(currentPartyId, currentMediaId)
                val toast = Toast.makeText(context, "MATCH FOUND 😍", Toast.LENGTH_LONG)
                toast.show()
                // send match
                sendData("MATCH: $currentMediaId")
            }
        } else {
            // likeInstance is null, create new like
            Log.d("SPF", "Create like")
            val like = Like(currentPartyId, currentMediaId, currentUserId)
            likeViewModel.addLike(like)
            sendData("LIKE: $currentMediaId, $currentUserId")
        }
    }

    // Define a suspend function to get likes by party and media
    // In your addLike function, replace the getLikesByPartyAndMedia call with this:
    private suspend fun getLikesByPartyAndMedia(partyId: Long, mediaId: Long): Like? {
        return likeViewModel.getLikesByPartyAndMedia(partyId, mediaId)
    }

    // Function to display media details in the UI
    private fun displayMedia(media: Media?) {
        activity?.runOnUiThread {
            if (media != null) {
                // Load the media poster using Picasso
                Picasso.get()
                    .load(media.imgUrl)
                    .fit()
                    .into(posterView)

                // Set the media details in UI elements
                titleText.text = media.title
                yearText.text = "Year: ${media.year}"
                typeText.text = "Type: ${media.type}"

                // Calculate and set the scaled rating in the RatingBar
                val scaledRating = when {
                    media.rating != null && media.rating.toString() != "" -> {
                        val ratingValue = media.rating!!.toFloat()
                        (ratingValue / 2f).coerceIn(0f, 5f)
                    }
                    else -> {
                        // If no rating is available, set it to 0
                        0f
                    }
                }
                ratingBar.rating = scaledRating
            }
        }
    }

    override fun onDataReceived(data: String) {
        Log.d("CPF", "DataReceived $data")
        val parts = data.split(":")
        if (parts.size >= 2) {
            val messageType = parts[0].trim()
            val messageContent = parts[1].trim()

            when (messageType) {
                "LIKE" -> handleLikeMessage(messageContent)
                "MATCH" -> handleMatchMessage(messageContent)
                else -> Log.d("ClientPlayFragment", "Unknown message type: $messageType")
            }
        } else {
            Log.d("ClientPlayFragment", "Invalid message format: $data")
        }
    }

    private fun handleLikeMessage(content: String) {
        val values = content.split(",")
        if (values.size == 2) {
            val mediaId = values[0].trim().toLong()
            val userId = values[1].trim().toLong()
            val like = Like(currentPartyId, mediaId, userId)
            likeViewModel.addLike(like)
        } else {
            Log.d("ClientPlayFragment", "Invalid LIKE message format: $content")
        }
    }

    private fun handleMatchMessage(content: String) {
        val mediaId = content.trim().toLong()
        partyViewModel.addMatchToParty(currentPartyId, mediaId)
        val toast = Toast.makeText(context, "MATCH FOUND 😍", Toast.LENGTH_LONG)
        toast.show()
    }

    private fun sendData(data: String) {
        bluetoothServerService.writeData(data)
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

}