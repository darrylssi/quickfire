package nz.ac.uclive.ajs418.quickfire.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import nz.ac.uclive.ajs418.quickfire.R
import nz.ac.uclive.ajs418.quickfire.service.BluetoothClientService
import nz.ac.uclive.ajs418.quickfire.service.BluetoothServiceCallback

class ClientPlayFragment : Fragment(), BluetoothServiceCallback {
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

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bluetoothClientService.setCallback(this)
    }

    override fun onDataReceived(data: String) {
        // Handle the received data here
    }

    private fun sendData(data: String) {
        bluetoothClientService.writeData(data)
    }

}