package com.swenson.blechat.viewmodel.central

import androidx.lifecycle.viewModelScope
import com.swenson.blechat.repository.BaseRepository
import com.swenson.blechat.repository.central.CentralRepository
import com.swenson.blechat.viewmodel.BaseViewModel

class CentralViewModel(private val repository: CentralRepository) : BaseViewModel() {
    override fun getRepository(): BaseRepository = repository
    fun startBLEScan() {
        repository.startBLEScan(viewModelScope)
    }

    fun getTextLiveData() = repository.messageLiveData
    fun getDeviceLiveData() = repository.deviceLiveData
    fun getDevicesLiveData() = repository.devicesLiveData
}