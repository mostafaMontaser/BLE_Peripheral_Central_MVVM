package com.swenson.blechat.model.response


data class ResponseException(
    override var message: String? = "",
    var responseCode: String? = "",
    var endPoint: String? = ""
) : Exception()