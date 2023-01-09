package com.swenson.blechat.repository

import com.swenson.blechat.datasource.remote.BaseRemoteDataSource
import com.swenson.blechat.datasource.room.RoomDataSource
import com.swenson.blechat.datasource.sharedpre.BaseSharedPrefDataSource
import com.swenson.blechat.model.request.BaseRequestFactory
import com.swenson.blechat.model.response.BaseModel
import com.swenson.blechat.model.response.ResponseException
import com.swenson.blechat.util.network.NetworkUtil
import java.lang.reflect.Type
import java.util.*

interface BaseRepository {

    val sharedPrefDataSource: BaseSharedPrefDataSource
    val remoteRepo: BaseRemoteDataSource?
    val roomDataSource: RoomDataSource?


    suspend fun fetchData(
        shouldConnected: Boolean = true,
        cash: Boolean,
        requestFactory: BaseRequestFactory
    ): Any? {
        var errorMessage: String? = null
        var responseCode: String? = null
        val response = try {
            val isNetworkConnected = NetworkUtil.isNetworkAvailable()
            if (isNetworkConnected) {
                //todo add token to header
//                val loginResponseObj =
//                    (getCashedObject(LoginResponse::class.java) as LoginResponse?)
//
//                requestFactory.baseRequestParam.token = loginResponseObj?.payload?.account?.token

                remoteRepo?.fetchData(requestFactory)
            } else {
                errorMessage = NetworkUtil.NETWORK_ERROR_MSG
                responseCode = NetworkUtil.NO_INTERNET_CONNECTION_CODE
                null
            }
        } catch (ex: Exception) {
            errorMessage = NetworkUtil.CLIENT_ERROR_MSG
            responseCode = ""
            ex.printStackTrace()
            null
        }
        if (response != null && response.isSuccessful) {
            val body = response.body()

            if (body is BaseModel) {
                val cache = response.body()
                if (cash) {
                    roomDataSource?.saveObject(body)
                }
                return cache
            } else {
                errorMessage = body?.message
            }
        } else if (cash && !shouldConnected) {
            val cashed = roomDataSource?.getCachedObject()
            if (cashed != null)
                return cashed
            if (errorMessage == null) {
                errorMessage = NetworkUtil.SERVER_ERROR_MSG
                responseCode = "" + response?.code()
            }
        } else {
            if (errorMessage == null) {
                errorMessage = NetworkUtil.SERVER_ERROR_MSG
                responseCode = "" + response?.code()
            }
        }

        throw ResponseException(
            message = errorMessage,
            responseCode = responseCode,
            endPoint = requestFactory.getEndPoint()
        )
    }

    fun getCashedObjectSharedPref(type: Type): Any? = sharedPrefDataSource.getCachedObject(type)

    fun saveObjectSharedPref(instance: BaseModel?, type: Type, lastModifiedDate: Long = Date().time) =
        sharedPrefDataSource.saveObject(instance, type, lastModifiedDate)

}
