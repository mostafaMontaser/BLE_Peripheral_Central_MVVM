package com.swenson.blechat.model.response

import com.google.gson.annotations.SerializedName

data class ErrorModel(
    @SerializedName("payload")
    val errorPayload: BaseModel.Payload,
    val error :Int,
    val message :String
)