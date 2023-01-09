package com.swenson.blechat.view.central

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.swenson.blechat.R
import com.swenson.blechat.databinding.FragmentCentralBinding
import com.swenson.blechat.extention.gone
import com.swenson.blechat.extention.show
import com.swenson.blechat.view.BasicFragment
import com.swenson.blechat.viewmodel.BaseViewModel
import com.swenson.blechat.viewmodel.central.CentralViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class CentralFragment : BasicFragment(), DevicesAdapter.DevicesAdapterListener {
    private var deviceAdapter: DevicesAdapter = DevicesAdapter(this)
    private val viewModel: CentralViewModel by viewModel()
    private val requestScanPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            // PERMISSION NOT GRANTED
            showErrorText(R.string.bt_not_permit_scan)
        }
    }

    override fun getViewModel(): BaseViewModel = viewModel

    private lateinit var binding: FragmentCentralBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!::binding.isInitialized) {
            binding = FragmentCentralBinding.inflate(inflater, container, false)
        }
        return binding.root
    }

    override fun showLoading() {
        binding.layoutLoading.show()
    }

    override fun hideLoading() {
        binding.layoutLoading.gone()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        observeData()
    }

    private fun observeData() {
        viewModel.getTextLiveData().observe(viewLifecycleOwner) {
            showErrorText(it)
        }
        viewModel.getDeviceLiveData().observe(viewLifecycleOwner) {
            deviceAdapter.add(it)
        }
        viewModel.getDevicesLiveData().observe(viewLifecycleOwner) {
            deviceAdapter.add(it)
        }
    }

    private fun initUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestScanPermission.launch(
                    Manifest.permission.BLUETOOTH_SCAN, null
                )
            }
        }
        binding.btnScan.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.BLUETOOTH_SCAN
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    viewModel.startBLEScan()
                } else {
                    showErrorText(R.string.bt_not_permit_scan)
                }
            } else {
                viewModel.startBLEScan()
            }
        }
        binding.recyclerDevices.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = deviceAdapter
        }
    }

    private fun showErrorText(error: Int) {
        Snackbar.make(binding.root, error, Snackbar.LENGTH_LONG).show()
    }

    private fun showErrorText(error: String) {
        Snackbar.make(binding.root, error, Snackbar.LENGTH_LONG).show()
    }

    override fun onDeviceItemClick(deviceName: String?, deviceAddress: String?) {
        val action = CentralFragmentDirections.actionCentralFragmentToDeviceConnectFragment(
            deviceName,
            deviceAddress
        )
        findNavController().navigate(action)
    }
}