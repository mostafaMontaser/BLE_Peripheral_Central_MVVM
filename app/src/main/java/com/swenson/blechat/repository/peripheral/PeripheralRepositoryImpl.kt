package com.swenson.blechat.repository.peripheral

import android.annotation.SuppressLint
import android.bluetooth.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.swenson.blechat.BLEChatApplication
import com.swenson.blechat.R
import com.swenson.blechat.constant.Constants
import com.swenson.blechat.datasource.remote.BaseRemoteDataSource
import com.swenson.blechat.datasource.room.RoomDataSource
import com.swenson.blechat.datasource.sharedpre.BaseSharedPrefDataSource

class PeripheralRepositoryImpl(
    private val mBluetoothManager: BluetoothManager?,
    override val sharedPrefDataSource: BaseSharedPrefDataSource,
    override val remoteRepo: BaseRemoteDataSource?,
    override val roomDataSource: RoomDataSource?
) : PeripheralRepository {
    private val _messageLiveData = MutableLiveData<String>()
    override val messageLiveData: LiveData<String>
        get() = _messageLiveData
    private var mSampleService: BluetoothGattService? = null
    private var mSampleCharacteristic: BluetoothGattCharacteristic? = null
    private var mGattServer: BluetoothGattServer? = null
    private var mBluetoothDevices: HashSet<BluetoothDevice>? = null
    private val mGattServerCallback: BluetoothGattServerCallback =
        object : BluetoothGattServerCallback() {
            override fun onConnectionStateChange(
                device: BluetoothDevice,
                status: Int,
                newState: Int
            ) {
                super.onConnectionStateChange(device, status, newState)
                val msg: String
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    if (newState == BluetoothGatt.STATE_CONNECTED) {
                        mBluetoothDevices!!.add(device)
                        msg = "Connected to device: " + device.address
                        _messageLiveData.postValue(msg)
                    } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                        mBluetoothDevices!!.remove(device)
                        msg = "Disconnected from device"
                        _messageLiveData.postValue(msg)
                    }
                } else {
                    mBluetoothDevices!!.remove(device)
                    msg =
                        BLEChatApplication.getContext()?.resources?.getString(R.string.status_error_when_connecting)
                            .toString() + ": " + status
                    _messageLiveData.postValue(msg)
                }
            }

            override fun onNotificationSent(device: BluetoothDevice, status: Int) {
                super.onNotificationSent(device, status)
            }

            @SuppressLint("MissingPermission")
            override fun onCharacteristicReadRequest(
                device: BluetoothDevice,
                requestId: Int,
                offset: Int,
                characteristic: BluetoothGattCharacteristic
            ) {
                super.onCharacteristicReadRequest(device, requestId, offset, characteristic)
                if (mGattServer == null) {
                    return
                }
                mGattServer!!.sendResponse(
                    device,
                    requestId,
                    BluetoothGatt.GATT_SUCCESS,
                    offset,
                    characteristic.value
                )
            }

            @SuppressLint("MissingPermission")
            override fun onCharacteristicWriteRequest(
                device: BluetoothDevice,
                requestId: Int,
                characteristic: BluetoothGattCharacteristic,
                preparedWrite: Boolean,
                responseNeeded: Boolean,
                offset: Int,
                value: ByteArray
            ) {
                super.onCharacteristicWriteRequest(
                    device,
                    requestId,
                    characteristic,
                    preparedWrite,
                    responseNeeded,
                    offset,
                    value
                )
                mSampleCharacteristic!!.value = value
                if (responseNeeded) {
                    mGattServer!!.sendResponse(
                        device,
                        requestId,
                        BluetoothGatt.GATT_SUCCESS,
                        0,
                        value
                    )
                }
            }

            @SuppressLint("MissingPermission")
            override fun onDescriptorReadRequest(
                device: BluetoothDevice,
                requestId: Int,
                offset: Int,
                descriptor: BluetoothGattDescriptor
            ) {
                super.onDescriptorReadRequest(device, requestId, offset, descriptor)
                if (mGattServer == null) {
                    return
                }
                mGattServer!!.sendResponse(
                    device,
                    requestId,
                    BluetoothGatt.GATT_SUCCESS,
                    offset,
                    descriptor.value
                )
            }

            override fun onDescriptorWriteRequest(
                device: BluetoothDevice,
                requestId: Int,
                descriptor: BluetoothGattDescriptor,
                preparedWrite: Boolean,
                responseNeeded: Boolean,
                offset: Int,
                value: ByteArray
            ) {
                super.onDescriptorWriteRequest(
                    device,
                    requestId,
                    descriptor,
                    preparedWrite,
                    responseNeeded,
                    offset,
                    value
                )
            }
        }

    @SuppressLint("MissingPermission")
    override fun notifyCharacteristicChanged() {
        /*
        done when the user clicks the notify button in the app.
        indicate - true for indication (acknowledge) and false for notification (un-acknowledge).
         */
        val indicate =
            (mSampleCharacteristic?.properties?.and(BluetoothGattCharacteristic.PROPERTY_INDICATE)) == BluetoothGattCharacteristic.PROPERTY_INDICATE
        if (mBluetoothDevices != null) {
            for (device in mBluetoothDevices!!) {
                mGattServer?.notifyCharacteristicChanged(device, mSampleCharacteristic, indicate)
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun setGattServer() {
        mBluetoothDevices = java.util.HashSet()
        if (mBluetoothManager != null) {
            mGattServer = mBluetoothManager.openGattServer(
                BLEChatApplication.getContext(),
                mGattServerCallback
            )
        } else {
            _messageLiveData.postValue(BLEChatApplication.getContext()?.resources?.getString(R.string.error_unknown))
        }
    }

    @SuppressLint("MissingPermission")
    override fun setBluetoothService() {

        // create the Service
        mSampleService =
            BluetoothGattService(
                Constants.HEART_RATE_SERVICE_UUID,
                BluetoothGattService.SERVICE_TYPE_PRIMARY
            )

        /*
        create the Characteristic.
        we need to grant to the Client permission to read (for when the user clicks the "Request Characteristic" button).
        no need for notify permission as this is an action the Server initiate.
         */
        mSampleCharacteristic = BluetoothGattCharacteristic(
            Constants.BODY_SENSOR_LOCATION_CHARACTERISTIC_UUID,
            BluetoothGattCharacteristic.PROPERTY_NOTIFY or BluetoothGattCharacteristic.PROPERTY_READ,
            BluetoothGattCharacteristic.PERMISSION_READ
        )
        // add the Characteristic to the Service
        mSampleService?.addCharacteristic(mSampleCharacteristic)

        // add the Service to the Server/Peripheral
        if (mGattServer != null) {
            mGattServer?.addService(mSampleService)
        }
    }

    override fun setCharacteristic(value: ByteArray) {
        mSampleCharacteristic?.setValue(value)
    }


}


