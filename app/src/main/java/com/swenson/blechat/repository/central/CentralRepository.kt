package com.swenson.blechat.repository.central

import android.bluetooth.le.ScanResult
import androidx.lifecycle.LiveData
import com.swenson.blechat.repository.BaseRepository
import kotlinx.coroutines.CoroutineScope

interface CentralRepository : BaseRepository {
    val messageLiveData: LiveData<String>
    val deviceLiveData: LiveData<ScanResult>
    val devicesLiveData: LiveData<List<ScanResult>>
    fun startBLEScan(scope: CoroutineScope)

}