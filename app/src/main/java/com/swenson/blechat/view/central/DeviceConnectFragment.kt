package com.swenson.blechat.view.central

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.content.*
import android.graphics.Color
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.swenson.blechat.R
import com.swenson.blechat.constant.Constants
import com.swenson.blechat.databinding.FragmentDeviceConnectBinding
import com.swenson.blechat.view.BaseFragment

class DeviceConnectFragment : BaseFragment() {
    private var mBluetoothLeService: CentralService? = null
    private var mDeviceServices: ArrayList<ArrayList<BluetoothGattCharacteristic>>? = null
    private var mCharacteristic: BluetoothGattCharacteristic? = null
    private var mDeviceName: String? = null
    private var mDeviceAddress: String? = null
    private lateinit var binding: FragmentDeviceConnectBinding
    val args: DeviceConnectFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!::binding.isInitialized) {
            binding = FragmentDeviceConnectBinding.inflate(inflater, container, false)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        abstractData()
        initUI()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val gattServiceIntent = Intent(requireContext(), CentralService::class.java)
        requireContext().bindService(
            gattServiceIntent,
            mServiceConnection,
            Context.BIND_AUTO_CREATE
        )
    }
    private fun abstractData() {
        mDeviceAddress = args.deviceAddress
        mDeviceName = args.deviceName
    }

    private fun initUI() {
        binding.btnRequestReadCharacteristic.setOnClickListener {
            requestReadCharacteristic()
        }
        binding.txtConnectedDeviceName.text = mDeviceName
    }

    // Code to manage Service lifecycle.
    private val mServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, service: IBinder) {
            mBluetoothLeService = (service as CentralService.LocalBinder).service
            if (mBluetoothLeService != null && !mBluetoothLeService!!.initialize()) {
                requireActivity().onBackPressed()
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService?.connect(mDeviceAddress)
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            mBluetoothLeService = null
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().registerReceiver(
            mGattUpdateReceiver,
            makeGattUpdateIntentFilter()
        )
    }

    override fun onPause() {
        super.onPause()
        requireContext().unregisterReceiver(mGattUpdateReceiver)
    }


    override fun onDestroy() {
        super.onDestroy()
        requireContext().unbindService(mServiceConnection)
        mBluetoothLeService = null
    }

    private fun makeGattUpdateIntentFilter(): IntentFilter {
        val intentFilter = IntentFilter()
        intentFilter.addAction(CentralService.ACTION_GATT_CONNECTED)
        intentFilter.addAction(CentralService.ACTION_GATT_DISCONNECTED)
        intentFilter.addAction(CentralService.ACTION_GATT_SERVICES_DISCOVERED)
        intentFilter.addAction(CentralService.ACTION_DATA_AVAILABLE)
        return intentFilter
    }

    private val mGattUpdateReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            intent.action ?: return
            when (intent.action) {
                CentralService.ACTION_GATT_CONNECTED -> {
                    updateConnectionState(R.string.connected)
                    binding.btnRequestReadCharacteristic.isEnabled = true
                }
                CentralService.ACTION_GATT_DISCONNECTED -> {
                    updateConnectionState(R.string.disconnected)
                    binding.btnRequestReadCharacteristic.isEnabled = false
                }
                CentralService.ACTION_GATT_SERVICES_DISCOVERED -> {
                    // set all the supported services and characteristics on the user interface.
                    setGattServices(mBluetoothLeService?.supportedGattServices)
                    registerCharacteristic()
                }
                CentralService.ACTION_DATA_AVAILABLE -> {
                    val msg = intent.getIntExtra(CentralService.EXTRA_DATA, -1)
                    updateInputFromServer(msg)
                }
            }
        }
    }

    private fun setGattServices(gattServices: List<BluetoothGattService>?) {
        if (gattServices == null) {
            return
        }
        mDeviceServices = ArrayList()

        // Loops through available GATT Services from the connected device
        for (gattService in gattServices) {
            val characteristic = java.util.ArrayList<BluetoothGattCharacteristic>()
            characteristic.addAll(gattService.characteristics) // each GATT Service can have multiple characteristic
            mDeviceServices?.add(characteristic)
        }
    }

    private fun registerCharacteristic() {
        var characteristic: BluetoothGattCharacteristic? = null
        if (mDeviceServices != null) {

            /* iterate all the Services the connected device offer.
            a Service is a collection of Characteristic.
             */
            for (service in mDeviceServices!!) {
                // iterate all the Characteristic of the Service
                for (serviceCharacteristic in service) {

                    /* check this characteristic belongs to the Service defined in
                    PeripheralAdvertiseService.buildAdvertiseData() method
                     */
                    if (serviceCharacteristic.service.uuid == Constants.HEART_RATE_SERVICE_UUID) {
                        if (serviceCharacteristic.uuid == Constants.BODY_SENSOR_LOCATION_CHARACTERISTIC_UUID) {
                            characteristic = serviceCharacteristic
                            mCharacteristic = characteristic
                        }
                    }
                }
            }

            /*
            int charaProp = characteristic.getProperties();
            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
            */if (characteristic != null) {
                mBluetoothLeService!!.readCharacteristic(characteristic)
                mBluetoothLeService!!.setCharacteristicNotification(characteristic, true)
            }
        }
    }


    private fun updateConnectionState(resourceId: Int) {
        binding.txtConnectionStatus.setText(resourceId)
    }

    private fun updateInputFromServer(msg: Int) {
        val color: String = when (msg) {
            Constants.SERVER_MSG_FIRST_STATE -> "#AD1457"
            Constants.SERVER_MSG_SECOND_STATE -> "#6A1B9A"
            else -> "#FFFFFF"
        }
        binding.txtServerCharacteristicValue.setBackgroundColor(Color.parseColor(color))
        showMsgText(java.lang.String.format(getString(R.string.characteristic_value_received), msg))
    }

    private fun showMsgText(string: String) {
        Snackbar.make(binding.root, string, Snackbar.LENGTH_LONG).show()
    }

    private fun showMsgText(string: Int) {
        Snackbar.make(binding.root, string, Snackbar.LENGTH_LONG).show()
    }

    private fun requestReadCharacteristic() {
        if (mBluetoothLeService != null && mCharacteristic != null) {
            mBluetoothLeService!!.readCharacteristic(mCharacteristic)
        } else {
            showMsgText(R.string.error_unknown)
        }
    }
}