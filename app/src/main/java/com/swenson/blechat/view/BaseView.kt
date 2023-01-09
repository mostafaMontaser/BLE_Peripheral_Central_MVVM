package com.swenson.blechat.view

import com.swenson.blechat.model.response.ErrorScreen


interface BaseView {

    fun showLoading()
    fun hideLoading()
    fun showError(error: ErrorScreen)

}