package com.swenson.blechat.repository.peripheral

import androidx.lifecycle.LiveData
import com.swenson.blechat.repository.BaseRepository

interface PeripheralRepository : BaseRepository {
    val messageLiveData: LiveData<String>
    fun notifyCharacteristicChanged()
    fun setCharacteristic(value: ByteArray)
    fun setBluetoothService()
    fun setGattServer()
}