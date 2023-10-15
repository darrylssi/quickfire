package nz.ac.uclive.ajs418.quickfire.service

interface BluetoothServiceCallback {
    suspend fun onDataReceived(data: String)
}