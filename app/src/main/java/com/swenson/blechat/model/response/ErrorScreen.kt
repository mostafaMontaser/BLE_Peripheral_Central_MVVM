package com.swenson.blechat.model.response

import com.swenson.blechat.model.request.BaseRequestParam


data class ErrorScreen(
    val title: String?,
    val message: String?,
    val responseCode: String?,
    val renderType: String,
    val endPoint: String?,
    val requestParam: BaseRequestParam
) {
    companion object {
        const val UNAUTHORIZED = "401"
    }

    constructor(
        title: String?,
        message: String?,
        responseCode: String?,
        endPoint: String?,
        requestParam: BaseRequestParam
    ) : this(title, message, responseCode, "", endPoint, requestParam)
}