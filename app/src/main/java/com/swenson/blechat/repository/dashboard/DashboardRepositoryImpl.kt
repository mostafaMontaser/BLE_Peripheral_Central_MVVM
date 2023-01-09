package com.swenson.blechat.repository.dashboard

import android.bluetooth.BluetoothAdapter
import com.swenson.blechat.datasource.remote.BaseRemoteDataSource
import com.swenson.blechat.datasource.room.RoomDataSource
import com.swenson.blechat.datasource.sharedpre.BaseSharedPrefDataSource

class DashboardRepositoryImpl(val bluetoothAdapter: BluetoothAdapter,
                              override val sharedPrefDataSource: BaseSharedPrefDataSource,
                              override val remoteRepo: BaseRemoteDataSource?,
                              override val roomDataSource: RoomDataSource?
) : DashboardRepository {




}