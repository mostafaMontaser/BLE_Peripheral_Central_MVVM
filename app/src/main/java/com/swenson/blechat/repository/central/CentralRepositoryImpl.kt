package com.swenson.blechat.repository.central

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.swenson.blechat.BLEChatApplication
import com.swenson.blechat.R
import com.swenson.blechat.datasource.remote.BaseRemoteDataSource
import com.swenson.blechat.datasource.room.RoomDataSource
import com.swenson.blechat.datasource.sharedpre.BaseSharedPrefDataSource
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

class CentralRepositoryImpl(
    private val bluetoothAdapter: BluetoothAdapter?,
    override val sharedPrefDataSource: BaseSharedPrefDataSource,
    override val remoteRepo: BaseRemoteDataSource?,
    override val roomDataSource: RoomDataSource?
) : CentralRepository {
    private val _messageLiveData = MutableLiveData<String>()
    override val messageLiveData: LiveData<String>
        get() = _messageLiveData
    private val _deviceLiveData = MutableLiveData<ScanResult>()
    override val deviceLiveData: LiveData<ScanResult>
        get() = _deviceLiveData
    private val _devicesLiveData = MutableLiveData<List<ScanResult>>()
    override val devicesLiveData: LiveData<List<ScanResult>>
        get() = _devicesLiveData
    private var mScanCallback: ScanCallback? = null
    private val SCAN_PERIOD: Long = 30000

    @SuppressLint("MissingPermission")
    override fun startBLEScan(scope: CoroutineScope) {

        /*
        better to request each time as BluetoothAdapter state might change (connection lost, etc...)
         */if (bluetoothAdapter != null) {
            val bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
            if (bluetoothLeScanner != null) {
                if (mScanCallback == null) {

                    // Will stop the scanning after a set time.
                    scope.launch(Dispatchers.Default) {
                        delay(SCAN_PERIOD) // Task will be performed after the delay
                        stopScanning()
                    }


                    // Kick off a new scan.
                    mScanCallback = SampleScanCallback()
                    bluetoothLeScanner.startScan(
                        buildScanFilters(),
                        buildScanSettings(),
                        mScanCallback
                    )
                    val toastText = "${BLEChatApplication.getContext()?.resources?.getString(R.string.scan_start_toast)} ${TimeUnit.SECONDS.convert(SCAN_PERIOD, TimeUnit.MILLISECONDS)} ${BLEChatApplication.getContext()?.resources?.getString(R.string.seconds)}"
                    _messageLiveData.postValue(toastText)
                } else {
                    _messageLiveData.postValue(BLEChatApplication.getContext()?.resources?.getString(R.string.already_scanning))
                }
                return
            }
        }
        _messageLiveData.postValue(BLEChatApplication.getContext()?.resources?.getString(R.string.error_unknown))
    }


    /**
     * Return a List of [ScanFilter] objects to filter by Service UUID.
     */
    private fun buildScanFilters(): List<ScanFilter> {
        val scanFilters: MutableList<ScanFilter> = ArrayList()
        val builder = ScanFilter.Builder()
        // Comment out the below line to see all BLE devices around you
        //builder.setServiceUuid(Constants.SERVICE_UUID);
        scanFilters.add(builder.build())
        return scanFilters
    }

    /**
     * Return a [ScanSettings] object set to use low power (to preserve battery life).
     */
    private fun buildScanSettings(): ScanSettings? {
        val builder = ScanSettings.Builder()
        builder.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
        return builder.build()
    }


    /**
     * Stop scanning for BLE Advertisements.
     */
    @SuppressLint("MissingPermission")
    private fun stopScanning() {
        /*
        better to request each time as BluetoothAdapter state might change (connection lost, etc...)
         */
        if (bluetoothAdapter != null) {
            val bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
            if (bluetoothLeScanner != null) {
                bluetoothLeScanner.stopScan(mScanCallback)
                mScanCallback = null

                // Even if no new results, update 'last seen' times.
                //mDevicesAdapter.notifyDataSetChanged()
                return
            }
        }
        _messageLiveData.postValue(BLEChatApplication.getContext()?.resources?.getString((R.string.error_unknown)))
    }
    private inner class SampleScanCallback : ScanCallback() {
        override fun onBatchScanResults(results: List<ScanResult>) {
            super.onBatchScanResults(results)
            _devicesLiveData.postValue(results)
        }

        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            if(result.device.name!=null)
            _deviceLiveData.postValue(result)
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
           _messageLiveData.postValue("Scan failed with error: $errorCode")
        }

    }

}


