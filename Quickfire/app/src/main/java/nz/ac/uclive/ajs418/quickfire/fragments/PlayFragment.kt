package nz.ac.uclive.ajs418.quickfire.fragments

import nz.ac.uclive.ajs418.quickfire.R
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import nz.ac.uclive.ajs418.quickfire.entity.Media
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.Charset
import java.util.*
import com.squareup.picasso.Picasso

class PlayFragment : Fragment() {

    // Initialize random generator
    private val random = Random()

    // List to store media data
    private var mediaList: List<Media>? = null

    // UI elements
    private lateinit var posterView: ImageView
    private lateinit var titleText: TextView
    private lateinit var yearText: TextView
    private lateinit var ratingBar: RatingBar
    private lateinit var typeText: TextView
    private lateinit var yesButton: Button
    private lateinit var noButton: Button

    // Current media index
    private var currentIndex = -1

    // Variables for swipe gesture detection
    private var startX = 0f
    private var startY = 0f
    private val SWIPE_THRESHOLD = 100 // Threshold for swipe gesture detection

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_play, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        posterView = view.findViewById(R.id.posterView)
        titleText = view.findViewById(R.id.titleText)
        yearText = view.findViewById(R.id.yearText)
        ratingBar = view.findViewById(R.id.ratingBar)
        typeText = view.findViewById(R.id.typeText)
        yesButton = view.findViewById(R.id.yesButton)
        noButton = view.findViewById(R.id.noButton)

        // Load media data when the fragment is created
        loadMediaData(requireContext()) { success ->
            if (success) {
                // Set initial random media
                val initialRandomMedia = selectRandomMedia()
                displayMedia(initialRandomMedia)
            }
        }

        // Handle the "Random" button click
        yesButton.setOnClickListener {
            val randomMedia = selectRandomMedia()
            displayMedia(randomMedia)
        }

        noButton.setOnClickListener {
            val randomMedia = selectRandomMedia()
            displayMedia(randomMedia)
        }

        // Set up swipe gesture detection
        setupSwipeGestureDetection()
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
                        // Swipe detected, check direction
                        if (deltaX > 0) {
                            // Swipe right, perform the same action as clicking "Yes"
                            handleSwipeRight()
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

    // Function to handle swipe right action
    private fun handleSwipeRight() {
        val randomMedia = selectRandomMedia()
        displayMedia(randomMedia)
    }

    // Function to handle swipe left action
    private fun handleSwipeLeft() {
        val randomMedia = selectRandomMedia()
        displayMedia(randomMedia)
    }

    // Function to load media data from JSON files
    private fun loadMediaData(context: Context, callback: (Boolean) -> Unit) {
        // Load media data from JSON files using coroutines
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val tvShowsJson = loadJsonFromAssets(context, "json/tv_shows.json")
                val moviesJson = loadJsonFromAssets(context, "json/movies.json")

                val tvShows = parseMediaJson(tvShowsJson)
                val movies = parseMediaJson(moviesJson)

                mediaList = tvShows + movies // Combine TV shows and movies
                callback(true) // Notify that initialization is complete
            } catch (e: IOException) {
                // Handle the exception (e.g., file not found)
                callback(false)
            }
        }
    }

    // Function to load JSON data from assets
    private fun loadJsonFromAssets(context: Context, fileName: String): String {
        return try {
            val inputStream = context.assets.open(fileName)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, Charset.defaultCharset())
        } catch (e: IOException) {
            ""
        }
    }

    // Function to parse JSON data into a list of Media objects
    private fun parseMediaJson(json: String): List<Media> {
        val mediaList = mutableListOf<Media>()
        val jsonArray = JSONArray(json)
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val title = jsonObject.getString("title")
            val year = jsonObject.getString("year").toInt()
            val type = jsonObject.getString("type")
            val rating = jsonObject.optDouble("rating", 0.0).toString().toFloat()
            val synopsis = jsonObject.getString("synopsis")
            val imgUrl = jsonObject.optString("img", null)
            val media = Media(title, year.toLong(), type, rating, synopsis, imgUrl)
            mediaList.add(media)
        }
        return mediaList
    }

    // Function to select a random media item from the list
    private fun selectRandomMedia(): Media? {
        return if (mediaList?.isNotEmpty() == true) {
            val randomIndex = random.nextInt(mediaList!!.size)
            mediaList!![randomIndex]
        } else {
            null
        }
    }

    // Function to display media details in the UI
    private fun displayMedia(media: Media?) {
        activity?.runOnUiThread {
            // Update your UI to display the selected media
            if (media != null) {
                Picasso.get()
                    .load(media.imgUrl)
                    .fit()
                    .into(posterView)
                // Make the image slightly transparent (adjust alpha)
                posterView.alpha = 0.6f

                titleText.text = media.title
                yearText.text = "Year: " + media.year.toString()
                typeText.text = "Type: " + media.type

                // Check if the rating is available and not empty or null
                val scaledRating = when {
                    media.rating != null && media.rating.toString() != "" -> {
                        // Convert the rating to a float and scale it down to 0-5 range
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
}
