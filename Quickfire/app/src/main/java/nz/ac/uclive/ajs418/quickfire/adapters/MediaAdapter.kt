package nz.ac.uclive.ajs418.quickfire.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import nz.ac.uclive.ajs418.quickfire.R
import nz.ac.uclive.ajs418.quickfire.entity.Media

// Adapter for the RecyclerView to display media items.
class MediaAdapter(private val mediaList: List<Media>) :
    RecyclerView.Adapter<MediaAdapter.MediaViewHolder>() {

    // Create a new ViewHolder for a media item view.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        // Inflate the layout for an individual media item view.
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_media, parent, false)
        return MediaViewHolder(view)
    }

    // Bind data to a ViewHolder.
    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        val media = mediaList[position]
        holder.bind(media)
    }

    // Return the total number of media items in the dataset.
    override fun getItemCount(): Int {
        return mediaList.size
    }

    // ViewHolder class for an individual media item view.
    inner class MediaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Views within the media item view.
        // private val mediaPosterImageView: ImageView = itemView.findViewById(R.id.media_poster)
        private val mediaTitleTextView: TextView = itemView.findViewById(R.id.media_name)
        private val mediaYearTextView: TextView = itemView.findViewById(R.id.media_year)
        private val mediaTypeTextView: TextView = itemView.findViewById(R.id.media_type)
        private val mediaImdbRatingTextView: TextView = itemView.findViewById(R.id.media_rating)

        // Bind data to the views within the media item view.
        fun bind(media: Media) {
            mediaTitleTextView.text = media.title
            mediaYearTextView.text = "Year: " + media.year.toString()
            mediaTypeTextView.text = "Type: " + media.type
            mediaImdbRatingTextView.text = "Imdb Rating: " + media.rating.toString()
        }
    }
}
