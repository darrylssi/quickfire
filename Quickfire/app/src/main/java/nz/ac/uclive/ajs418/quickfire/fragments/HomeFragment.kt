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
        val soloPlayButton = view.findViewById<Button>(R.id.soloPlayButton)

        createPartyButton.setOnClickListener {
            replaceWithClientConnect(false)
        }

        joinPartyButton.setOnClickListener {
            replaceWithServerConnect(false)
        }

        soloPlayButton.setOnClickListener {
            replaceWithPlay()
        }
    }


    private fun replaceWithClientConnect(isMember: Boolean) {
        val bundle = Bundle()
        bundle.putBoolean("isMember", isMember)

        val fragmentTransaction = parentFragmentManager.beginTransaction()
        val clientConnectFragment = ClientConnectFragment()
        clientConnectFragment.arguments = bundle

        fragmentTransaction.replace(R.id.fragmentContainer, clientConnectFragment)
            .commit()
    }

    private fun replaceWithServerConnect(isMember: Boolean) {
        val bundle = Bundle()
        bundle.putBoolean("isMember", isMember)

        val fragmentTransaction = parentFragmentManager.beginTransaction()
        val serverConnectFragment = ServerConnectFragment()
       serverConnectFragment.arguments = bundle

        fragmentTransaction.replace(R.id.fragmentContainer, serverConnectFragment)
            .commit()
    }

    private fun replaceWithPlay() {
        val playFragment = PlayFragment()

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, playFragment)
            .commit()
    }

}