package com.swenson.blechat.view.peripheral

import android.annotation.SuppressLint
import android.app.Service
import android.bluetooth.BluetoothManager
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.ParcelUuid
import com.swenson.blechat.constant.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class PeripheralAdvertiseService : Service() {
    private var mBluetoothLeAdvertiser: BluetoothLeAdvertiser? = null
    private var mAdvertiseCallback: AdvertiseCallback? = null

    /**
     * Length of time to allow advertising before automatically shutting off. (10 minutes)
     */
    private val TIMEOUT = TimeUnit.MILLISECONDS.convert(10, TimeUnit.MINUTES)
    override fun onCreate() {
        running = true
        initialize()
        startAdvertising()
        setTimeout()
        super.onCreate()
    }

    override fun onDestroy() {
        /**
         * Note that onDestroy is not guaranteed to be called quickly or at all. Services exist at
         * the whim of the system, and onDestroy can be delayed or skipped entirely if memory need
         * is critical.
         */
        running = false
        stopAdvertising()
        stopForeground(true)
        super.onDestroy()
    }

    /**
     * Required for extending service, but this will be a Started Service only, so no need for
     * binding.
     */
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    /**
     * Get references to system Bluetooth objects if we don't have them already.
     */
    private fun initialize() {
        if (mBluetoothLeAdvertiser == null) {
            val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
            val bluetoothAdapter = bluetoothManager.adapter
            if (bluetoothAdapter != null) {
                mBluetoothLeAdvertiser = bluetoothAdapter.bluetoothLeAdvertiser
            }
        }
    }

    /**
     * Starts a delayed Runnable that will cause the BLE Advertising to timeout and stop after a
     * set amount of time.
     */
    private fun setTimeout() {
        CoroutineScope(Dispatchers.Default).launch {
            delay(TIMEOUT)
            stopSelf()
        }
    }

    /**
     * Starts BLE Advertising.
     */
    @SuppressLint("MissingPermission")
    private fun startAdvertising() {
        if (mAdvertiseCallback == null) {
            val settings = buildAdvertiseSettings()
            val data = buildAdvertiseData()
            mAdvertiseCallback = SampleAdvertiseCallback()
            if (mBluetoothLeAdvertiser != null) {
                mBluetoothLeAdvertiser!!.startAdvertising(settings, data, mAdvertiseCallback)
            }
        }
    }

    /**
     * Stops BLE Advertising.
     */
    @SuppressLint("MissingPermission")
    private fun stopAdvertising() {
        if (mBluetoothLeAdvertiser != null) {
            mBluetoothLeAdvertiser!!.stopAdvertising(mAdvertiseCallback)
            mAdvertiseCallback = null
        }
    }

    /**
     * Returns an AdvertiseData object which includes the Service UUID and Device Name.
     */
    private fun buildAdvertiseData(): AdvertiseData {
        /**
         * Note: There is a strict limit of 31 Bytes on packets sent over BLE Advertisements.
         * This includes everything put into AdvertiseData including UUIDs, device info, &
         * arbitrary service or manufacturer data.
         * Attempting to send packets over this limit will result in a failure with error code
         * AdvertiseCallback.ADVERTISE_FAILED_DATA_TOO_LARGE. Catch this error in the
         * onStartFailure() method of an AdvertiseCallback implementation.
         */
        val dataBuilder = AdvertiseData.Builder()
        //dataBuilder.addServiceUuid(Constants.SERVICE_UUID);
        dataBuilder.addServiceUuid(ParcelUuid.fromString(Constants.HEART_RATE_SERVICE_UUID.toString()))
        dataBuilder.setIncludeDeviceName(true)

        /* For example - this will cause advertising to fail (exceeds size limit) */
        //String failureData = "asdghkajsghalkxcjhfa;sghtalksjcfhalskfjhasldkjfhdskf";
        //dataBuilder.addServiceData(Constants.SERVICE_UUID, failureData.getBytes());
        return dataBuilder.build()
    }

    /**
     * Returns an AdvertiseSettings object set to use low power (to help preserve battery life)
     * and disable the built-in timeout since this code uses its own timeout runnable.
     */
    private fun buildAdvertiseSettings(): AdvertiseSettings {
        val settingsBuilder = AdvertiseSettings.Builder()
        settingsBuilder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER)
        settingsBuilder.setTimeout(0)
        return settingsBuilder.build()
    }

    /**
     * Custom callback after Advertising succeeds or fails to start. Broadcasts the error code
     * in an Intent to be picked up by AdvertiserFragment and stops this Service.
     */
    private inner class SampleAdvertiseCallback : AdvertiseCallback() {
        override fun onStartFailure(errorCode: Int) {
            super.onStartFailure(errorCode)
            stopSelf()
        }

        override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
            super.onStartSuccess(settingsInEffect)
        }
    }

    companion object {
        /**
         * A global variable to let AdvertiserFragment check if the Service is running without needing
         * to start or bind to it.
         * This is the best practice method as defined here:
         * https://groups.google.com/forum/#!topic/android-developers/jEvXMWgbgzE
         */
        var running = false
    }
}

