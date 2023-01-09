package com.swenson.blechat.model.signin

import com.swenson.blechat.datasource.room.account.AccountTable
import com.swenson.blechat.model.response.BaseModel


import java.io.Serializable

data class Account(var user: User, val token: String) : BaseModel()

data class User(
    val id: String,
) : Serializable

fun Account.domainToRoom(): AccountTable {
    return AccountTable(user.id, token)
}
