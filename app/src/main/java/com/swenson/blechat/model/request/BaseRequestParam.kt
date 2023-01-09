package com.swenson.blechat.model.request

import com.swenson.blechat.model.signin.Account
import java.io.Serializable

open class BaseRequestParam(var account: Account?=null) : Serializable
