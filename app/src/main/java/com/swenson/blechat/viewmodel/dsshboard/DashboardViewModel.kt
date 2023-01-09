package com.swenson.blechat.viewmodel.dsshboard

import com.swenson.blechat.repository.BaseRepository
import com.swenson.blechat.repository.central.CentralRepository
import com.swenson.blechat.repository.dashboard.DashboardRepository
import com.swenson.blechat.viewmodel.BaseViewModel

class DashboardViewModel(private val dashboardRepository: DashboardRepository) : BaseViewModel() {
    override fun getRepository(): BaseRepository = dashboardRepository


}