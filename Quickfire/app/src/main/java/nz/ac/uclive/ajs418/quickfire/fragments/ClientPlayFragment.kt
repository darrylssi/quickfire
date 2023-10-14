package nz.ac.uclive.ajs418.quickfire.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import nz.ac.uclive.ajs418.quickfire.R
import nz.ac.uclive.ajs418.quickfire.service.BluetoothClientService

class ClientPlayFragment : Fragment() {
    private lateinit var bluetoothClientService: BluetoothClientService

    fun setBluetoothClientService(service: BluetoothClientService) {
        this.bluetoothClientService = service
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_play, container, false)
    }

}