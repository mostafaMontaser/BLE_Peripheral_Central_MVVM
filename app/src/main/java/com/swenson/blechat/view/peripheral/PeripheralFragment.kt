package com.swenson.blechat.view.peripheral

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.Snackbar
import com.swenson.blechat.R
import com.swenson.blechat.constant.Constants
import com.swenson.blechat.databinding.FragmentPeripheralBinding
import com.swenson.blechat.extention.gone
import com.swenson.blechat.extention.show
import com.swenson.blechat.view.BasicFragment
import com.swenson.blechat.viewmodel.peripheral.PeripheralViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PeripheralFragment : BasicFragment() {
    private val requestAdvertisePermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            // PERMISSION NOT GRANTED
            showErrorText(R.string.bt_not_permit_advertise)
        }
    }
    private lateinit var binding: FragmentPeripheralBinding
    private val peripheralViewModel: PeripheralViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!::binding.isInitialized) {
            binding = FragmentPeripheralBinding.inflate(inflater, container, false)
        }
        return binding.root
    }

    override fun getViewModel(): PeripheralViewModel = peripheralViewModel

    override fun showLoading() {
        binding.layoutLoading.show()
    }

    override fun hideLoading() {
        binding.layoutLoading.gone()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        peripheralViewModel.setGattServer()
        peripheralViewModel.setBluetoothService()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        observeData()
    }

    private fun observeData() {
        peripheralViewModel.getTextLiveData().observe(viewLifecycleOwner) {
            showErrorText(it)
        }
    }

    @SuppressLint("InlinedApi")
    private fun initUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.BLUETOOTH_ADVERTISE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestAdvertisePermission.launch(
                    Manifest.permission.BLUETOOTH_ADVERTISE, null
                )
            }
        }
        binding.switchColor.setOnCheckedChangeListener { _, checkedId ->
            setCharacteristic(checkedId)
        }
        binding.switchAdvertise.setOnCheckedChangeListener { _, switchToggle ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.BLUETOOTH_ADVERTISE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    beginAdvertise(switchToggle)
                } else {
                    showErrorText(R.string.bt_not_permit_advertise)
                }
            } else {
                beginAdvertise(switchToggle)
            }

        }

        binding.btnNotify.setOnClickListener {
            peripheralViewModel.notifyCharacteristicChanged()
        }
        setCharacteristic()
    }

    private fun beginAdvertise(switchToggle: Boolean) {
        if (switchToggle) {
            // TODO bluetooth - maybe bindService? what happens when closing app?
            requireContext().startService(getServiceIntent(requireContext()))

        } else {
            requireContext().stopService(getServiceIntent(requireContext()))
            binding.switchAdvertise.isChecked = false
        }
    }

    private fun setCharacteristic(checkedId: Int = R.id.color_option_1) {
        /*
        done each time the user changes a value of a Characteristic
         */
        val value: Int =
            if (checkedId == R.id.color_option_1) Constants.SERVER_MSG_FIRST_STATE else Constants.SERVER_MSG_SECOND_STATE
        peripheralViewModel.setCharacteristic(getValue(value))
    }

    private fun getValue(value: Int) = byteArrayOf(value.toByte())
    private fun getServiceIntent(context: Context) =
        Intent(context, PeripheralAdvertiseService::class.java)

    private fun showErrorText(error: Int) {
        Snackbar.make(binding.root, error, Snackbar.LENGTH_LONG).show()
    }

    private fun showErrorText(error: String) {
        Snackbar.make(binding.root, error, Snackbar.LENGTH_LONG).show()
    }
}
