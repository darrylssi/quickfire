package nz.ac.uclive.ajs418.quickfire.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import nz.ac.uclive.ajs418.quickfire.R


class ConnectFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_connect, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val arguments = arguments
        if (arguments != null && arguments.getBoolean("isMember")) {
            val startButton = view.findViewById<Button>(R.id.startMatchButton)
            val addButton = view.findViewById<Button>(R.id.addPersonButton)
            disableButton(startButton, view)
            disableButton(addButton, view)
        }
    }

    private fun disableButton(button: Button, view: View) {
        button.isEnabled = false
        button.isClickable = false
        button.setBackgroundColor(ContextCompat.getColor(view.context, R.color.grey))
        button.setTextColor(ContextCompat.getColor(view.context, R.color.white))
    }

}
