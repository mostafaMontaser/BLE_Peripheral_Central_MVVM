package com.swenson.blechat.di

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import com.swenson.blechat.datasource.room.account.AccountRoomDataSource
import com.swenson.blechat.datasource.sharedpre.BaseSharedPrefDataSource
import com.swenson.blechat.datasource.sharedpre.BaseSharedPrefDataSourceImpl
import com.swenson.blechat.model.retrofit.Service
import com.swenson.blechat.repository.central.CentralRepository
import com.swenson.blechat.repository.central.CentralRepositoryImpl
import com.swenson.blechat.repository.dashboard.DashboardRepository
import com.swenson.blechat.repository.dashboard.DashboardRepositoryImpl
import com.swenson.blechat.repository.peripheral.PeripheralRepository
import com.swenson.blechat.repository.peripheral.PeripheralRepositoryImpl
import com.swenson.blechat.viewmodel.central.CentralViewModel
import com.swenson.blechat.viewmodel.dsshboard.DashboardViewModel
import com.swenson.blechat.viewmodel.peripheral.PeripheralViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val networkModule = module {
    single { Service.getService() }
}
val accountRoomDataSource = module {
    single { AccountRoomDataSource() }
}
val baseSharedPrefDataSource = module {
    single<BaseSharedPrefDataSource> { BaseSharedPrefDataSourceImpl() }
}

val dashboard = module {
    viewModel { DashboardViewModel(get()) }
    factory<DashboardRepository> { DashboardRepositoryImpl(get(), get(), null, null) }
    factory<BluetoothAdapter> { (androidContext().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter }
}
val central = module {
    viewModel { CentralViewModel(get()) }
    factory<CentralRepository> { CentralRepositoryImpl(get(), get(), null, null) }
    factory<BluetoothAdapter> { (androidContext().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter }
}
val peripheral = module {
    viewModel { PeripheralViewModel(get()) }
    factory<PeripheralRepository> { PeripheralRepositoryImpl(get(), get(), null, null) }
    factory<BluetoothManager> { (androidContext().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager) }
}
