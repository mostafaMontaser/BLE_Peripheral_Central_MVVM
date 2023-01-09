package com.swenson.blechat.view

import android.os.Bundle

import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.navOptions
import com.swenson.blechat.model.response.ErrorScreen
import com.swenson.blechat.util.network.NetworkUtil
import com.swenson.blechat.viewmodel.BaseViewModel


abstract class BasicFragment : BaseFragment(), BaseView {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getViewModel()?.showFullLoading?.observe(this, showFullLoading)
        getViewModel()?.errorDialog?.observe(this, showErrorUi)
    }

    override fun showError(
        error: ErrorScreen
    ) {

        showDefaultErrorDialog(error)


    }

    fun showDefaultErrorDialog(
        error: ErrorScreen,
    ) {

        if (NetworkUtil.NO_INTERNET_CONNECTION_CODE == error.responseCode) {
            Toast.makeText(
                requireContext(),
                error.message,
                Toast.LENGTH_LONG
            ).show()
        } else {
            Toast.makeText(
                requireContext(),
                error.message,
                Toast.LENGTH_LONG
            ).show()

        }
    }

    abstract fun getViewModel(): BaseViewModel?

    private val showFullLoading = Observer<Boolean> {
        if (it)
            showLoading()
        else
            hideLoading()
    }

    private val showErrorUi = Observer<ErrorScreen> { showError(it) }


}