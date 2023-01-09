package com.swenson.blechat.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swenson.blechat.model.request.BaseRequestFactory
import com.swenson.blechat.model.response.BaseModel
import com.swenson.blechat.model.response.ErrorScreen
import com.swenson.blechat.model.response.ResponseException
import com.swenson.blechat.repository.BaseRepository
import kotlinx.coroutines.*
import java.lang.reflect.Type

abstract class BaseViewModel : ViewModel() {

    private var _errorDialog: MutableLiveData<ErrorScreen> = MutableLiveData()
    val errorDialog: LiveData<ErrorScreen>
        get() = _errorDialog
    var _showFullLoading: MutableLiveData<Boolean> = MutableLiveData()
    val showFullLoading: LiveData<Boolean>
        get() = _showFullLoading

    var _showSuccessDialog: MutableLiveData<Boolean> = MutableLiveData()
    val showSuccessDialog: LiveData<Boolean>
        get() = _showSuccessDialog


    fun fetchData(
        cash: Boolean, type: Type, requestFactory: BaseRequestFactory, showLoading: Boolean = true,
        showSuccess: Boolean = false, showError: Boolean = true,
        proceedResponse: (
            t: Any?
        ) -> Unit
    ) {


        val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
            if (throwable is ResponseException) {
                onError(
                    ErrorScreen(
                        title = throwable.endPoint,
                        message = throwable.message,
                        responseCode = throwable.responseCode,
                        endPoint = requestFactory.getEndPoint(),
                        requestParam = requestFactory.baseRequestParam
                    ), showError
                )
            }
        }
        fetchLogic(
            showLoading,
            showError,
            exceptionHandler,
            cash,
            type,
            requestFactory,
            proceedResponse
        )
    }

    private fun fetchLogic(
        showLoading: Boolean,
        shouldConnect: Boolean,
        exceptionHandler: CoroutineExceptionHandler,
        cash: Boolean,
        type: Type,
        requestFactory: BaseRequestFactory,
        proceedResponse: (t: Any?) -> Unit
    ) {

        if (showLoading)
            _showFullLoading.value = true
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
                val response = getRepository()?.fetchData(shouldConnect, cash, requestFactory)
                withContext(Dispatchers.Main) {
                    proceedResponse(response)
                    if (showLoading)
                        _showFullLoading.value = false
                    if (response is BaseModel) {
                        _showSuccessDialog.value = true
                    }
            }
        }
    }

    private fun onError(errorScreen: ErrorScreen, showError: Boolean = true) {
        if (showError)
            _errorDialog.postValue(errorScreen)
        _showFullLoading.postValue(false)
    }

    abstract fun getRepository(): BaseRepository?
}
