package com.swenson.blechat.datasource.remote

import com.swenson.blechat.model.request.BaseRequestFactory
import com.swenson.blechat.model.response.BaseModel
import retrofit2.Response

interface BaseRemoteDataSource {
      suspend fun  fetchData(requestFactory: BaseRequestFactory): Response<out BaseModel>?
}