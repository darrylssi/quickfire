package nz.ac.uclive.ajs418.quickfire.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import nz.ac.uclive.ajs418.quickfire.R
import nz.ac.uclive.ajs418.quickfire.entity.Party

// Adapter for the RecyclerView to display party items.
class PartyAdapter(
    private val parties: Array<Party>,
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<PartyAdapter.PartyViewHolder>() {

    // Interface to handle item click events within the RecyclerView.
    interface OnItemClickListener {
        fun onItemClick(party: Party)
    }

    // Create a new ViewHolder for a party item view.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartyViewHolder {
        // Inflate the layout for an individual party item view.
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_party, parent, false)
        return PartyViewHolder(view)
    }

    // Bind data to a ViewHolder.
    override fun onBindViewHolder(holder: PartyViewHolder, position: Int) {
        val party = parties[position]
        holder.bind(party)
    }

    // Return the total number of party items in the dataset.
    override fun getItemCount(): Int {
        return parties.size
    }

    // ViewHolder class for an individual party item view.
    inner class PartyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // View within the party item view.
        private val partyNameTextView: TextView = itemView.findViewById(R.id.party_name)

        // Bind data to the view within the party item view.
        fun bind(party: Party) {
            partyNameTextView.text = party.name

            // Set a click listener for the party item view.
            itemView.setOnClickListener {
                onItemClickListener.onItemClick(party)
            }
        }
    }
}
