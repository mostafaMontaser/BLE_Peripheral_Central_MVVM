package com.swenson.blechat.view.dashboard

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.swenson.blechat.R
import com.swenson.blechat.databinding.FragmentDashboardBinding
import com.swenson.blechat.extention.gone
import com.swenson.blechat.extention.show
import com.swenson.blechat.view.BasicFragment
import com.swenson.blechat.viewmodel.BaseViewModel
import com.swenson.blechat.viewmodel.dsshboard.DashboardViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class DashboardFragment : BasicFragment() {
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            enableNavigation()
        } else {
            // PERMISSION NOT GRANTED
            showErrorText(R.string.bt_not_permit_coarse)
        }
    }
    private val requestMultiplePermissions = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            enableBT()
        } else {
            showErrorText(R.string.bt_not_permit_connect)
        }
    }
    private lateinit var binding: FragmentDashboardBinding
    private val viewModel: DashboardViewModel by viewModel()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!::binding.isInitialized) {
            binding = FragmentDashboardBinding.inflate(inflater, container, false)
        }
        return binding.root
    }

    override fun getViewModel(): BaseViewModel = viewModel

    override fun showLoading() {
        binding.layoutLoading.show()
    }

    override fun hideLoading() {
        binding.layoutLoading.gone()
    }


    @SuppressLint("InlinedApi")
    private fun initBT() {
        val bluetoothService =
            context?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager?

        if (bluetoothService != null) {

            mBluetoothAdapter = bluetoothService.adapter

            // Is Bluetooth supported on this device?
            if (mBluetoothAdapter != null) {
                // Is Bluetooth turned on?
                if (!mBluetoothAdapter!!.isEnabled) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                        if (ActivityCompat.checkSelfPermission(
                                requireContext(),
                                Manifest.permission.BLUETOOTH_CONNECT
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            requestMultiplePermissions.launch(
                                Manifest.permission.BLUETOOTH_CONNECT, null
                            )
                        } else {
                            enableBT()
                        }
                    } else {
                        enableBT()
                    }
                } else {
                    // Are Bluetooth Advertisements supported on this device?
                    if (mBluetoothAdapter!!.isMultipleAdvertisementSupported) {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (ActivityCompat.checkSelfPermission(
                                    requireContext(),
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {

                                requestPermissionLauncher.launch(
                                    Manifest.permission.ACCESS_COARSE_LOCATION, null
                                )

                            } else {
                                // Everything is supported and enabled.
                                enableNavigation()
                            }
                        } else {
                            // Everything is supported and enabled.
                            enableNavigation()
                        }
                    } else {
                        // Bluetooth Advertisements are not supported.
                        showErrorText(R.string.bt_ads_not_supported)
                    }
                }
            } else {
                // Bluetooth is not supported.
                showErrorText(R.string.bt_not_supported)

            }
        }
    }

    private fun enableNavigation() {
        binding.btnBtServer.isEnabled = true
        binding.btnBtClient.isEnabled = true
    }

    private fun disableNavigation() {
        binding.btnBtServer.isEnabled = false
        binding.btnBtClient.isEnabled = false
    }


    private fun showErrorText(error: Int) {
        Snackbar.make(binding.root, error, Snackbar.LENGTH_LONG).show()
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                initBT()
            } else {
                Toast.makeText(
                    requireContext(),
                    R.string.bt_not_enabled,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private fun enableBT() {
        // Prompt user to turn on Bluetooth (logic continues in onActivityResult()).
        val enableBtIntent =
            Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        resultLauncher.launch(enableBtIntent)
    }

    override fun onResume() {
        super.onResume()
        disableNavigation()
        initBT()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    @SuppressLint("InlinedApi")
    private fun initUI() {
        binding.btnBtClient.setOnClickListener {
            findNavController().navigate(
                DashboardFragmentDirections.actionDestinationToCentralFragment()
            )
        }
        binding.btnBtServer.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                showErrorText(R.string.bt_not_permit_connect)
                    requestMultiplePermissions.launch(
                        Manifest.permission.BLUETOOTH_CONNECT, null
                    )
                }else{
                findNavController().navigate(
                    DashboardFragmentDirections.actionDestinationToPeripheralFragment())
                }

        }else{
                findNavController().navigate(
                    DashboardFragmentDirections.actionDestinationToPeripheralFragment())
            }
        }
    }
}