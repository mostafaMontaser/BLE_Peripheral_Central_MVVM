package com.swenson.blechat.model.response

import java.io.Serializable

open class BaseModel : Serializable {
    val message: String? = null
    var error: Int = 0
    open class Payload : Serializable
}