package com.swenson.blechat.viewmodel.dsshboard

import android.bluetooth.BluetoothAdapter
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class DashboardFactory (
    private val bluetoothAdapter: BluetoothAdapter,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            //return DashboardViewModel(bluetoothAdapter) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
