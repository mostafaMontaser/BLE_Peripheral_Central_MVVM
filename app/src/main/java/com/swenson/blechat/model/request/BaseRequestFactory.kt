package com.swenson.blechat.model.request

import com.swenson.blechat.model.retrofit.Constant
import java.util.*
import kotlin.collections.HashMap


abstract class BaseRequestFactory {
    abstract var baseRequestParam: BaseRequestParam
    private var defaultProperties = HashMap<String, String>()

    init {
        defaultProperties["x-localization"] = Locale.getDefault().language
        defaultProperties["Accept"] = "application/json"
        defaultProperties["Connection"] = "close"
    }

    open fun getUrl() = Constant.BASE_URL + getEndPoint()
    abstract fun getEndPoint(): String?

    fun getHeaderParam(): HashMap<String, String> {
        val headers = HashMap<String, String>()
        if (baseRequestParam.account != null)
            defaultProperties["Authorization"] = "Bearer ${baseRequestParam.account?.token}"
        headers.putAll(defaultProperties)
        headers.putAll(getCustomHeaders())
        return headers
    }

    open fun getCustomHeaders(): HashMap<String, String> = HashMap()


}