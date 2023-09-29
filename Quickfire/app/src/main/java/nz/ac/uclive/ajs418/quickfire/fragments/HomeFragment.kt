package nz.ac.uclive.ajs418.quickfire.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import nz.ac.uclive.ajs418.quickfire.R

class HomeFragment : Fragment() {

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

        createPartyButton.setOnClickListener {
            replaceWithConnect(false)
        }

        joinPartyButton.setOnClickListener {
            replaceWithConnect(true)
        }
    }

    private fun replaceWithConnect(isMember: Boolean) {
        val bundle = Bundle()
        bundle.putBoolean("isMember", isMember)

        val fragmentTransaction = parentFragmentManager.beginTransaction()
        val connectFragment = ConnectFragment()
        connectFragment.arguments = bundle

        fragmentTransaction.replace(R.id.fragmentContainer, connectFragment)
            .commit()
    }

}