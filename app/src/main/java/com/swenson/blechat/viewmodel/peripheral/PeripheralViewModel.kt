package com.swenson.blechat.viewmodel.peripheral

import com.swenson.blechat.repository.BaseRepository
import com.swenson.blechat.repository.peripheral.PeripheralRepository
import com.swenson.blechat.viewmodel.BaseViewModel

class PeripheralViewModel(private val repository: PeripheralRepository) : BaseViewModel() {
    override fun getRepository(): BaseRepository = repository

    fun getTextLiveData() = repository.messageLiveData

    fun notifyCharacteristicChanged() {
        repository.notifyCharacteristicChanged()
    }

    fun setCharacteristic(value: ByteArray) {
        repository.setCharacteristic(value)
    }

    fun setBluetoothService() {
        repository.setBluetoothService()
    }

    fun setGattServer() {
        repository.setGattServer()
    }
}