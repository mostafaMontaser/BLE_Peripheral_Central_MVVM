package com.swenson.blechat.util.network

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

object MultiPartUtil {

    fun createPartFromString(string: String): RequestBody {
        return string.let {
            it.toRequestBody("text/plain".toMediaTypeOrNull())
        }
    }
}