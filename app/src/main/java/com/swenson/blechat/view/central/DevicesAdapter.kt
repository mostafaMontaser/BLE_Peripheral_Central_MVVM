package com.swenson.blechat.view.central

import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.swenson.blechat.databinding.ListItemDeviceListBinding

class DevicesAdapter(private val listener: DevicesAdapterListener?) :
    RecyclerView.Adapter<DevicesAdapter.ViewHolder?>() {
    private val mArrayList = ArrayList<ScanResult>()

    interface DevicesAdapterListener {
        fun onDeviceItemClick(deviceName: String?, deviceAddress: String?)
    }

    class ViewHolder(val bindingItem: ListItemDeviceListBinding) :
        RecyclerView.ViewHolder(bindingItem.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ListItemDeviceListBinding =
            ListItemDeviceListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("MissingPermission")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val scanResult = mArrayList[position]
        val deviceName = scanResult.device.name
        val deviceAddress = scanResult.device.address
        holder.bindingItem.deviceName.text = deviceName
        holder.bindingItem.deviceAddress.text = deviceAddress
        holder.itemView.setOnClickListener {
            if (!TextUtils.isEmpty(deviceName) && !TextUtils.isEmpty(deviceAddress)) {
                listener?.onDeviceItemClick(deviceName, deviceAddress)
            }
        }
    }

    override fun getItemCount() = mArrayList.size

    /**
     * Add a ScanResult item to the adapter if a result from that device isn't already present.
     * Otherwise updates the existing position with the new ScanResult.
     */
    fun add(scanResult: ScanResult?, notify: Boolean = true) {
        if (scanResult == null) {
            return
        }
        val existingPosition = getPosition(scanResult.device.address)
        if (existingPosition >= 0) {
            // Device is already in list, update its record.
            mArrayList[existingPosition] = scanResult
        } else {
            // Add new Device's ScanResult to list.
            mArrayList.add(scanResult)
        }
        if (notify) {
            notifyDataSetChanged()
        }
    }

    fun add(scanResults: List<ScanResult?>?) {
        if (scanResults != null) {
            for (scanResult in scanResults) {
                add(scanResult, false)
            }
            notifyDataSetChanged()
        }
    }

    /**
     * Search the adapter for an existing device address and return it, otherwise return -1.
     */
    private fun getPosition(address: String): Int {
        var position = -1
        for (i in mArrayList.indices) {
            if (mArrayList[i].device.address == address) {
                position = i
                break
            }
        }
        return position
    }

}
