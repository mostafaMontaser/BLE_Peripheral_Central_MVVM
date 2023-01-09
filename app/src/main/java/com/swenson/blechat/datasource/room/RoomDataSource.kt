package com.swenson.blechat.datasource.room

import com.swenson.blechat.model.response.BaseModel


interface RoomDataSource {
    suspend fun saveObject(model: BaseModel?)
    suspend fun getCachedObject(): BaseModel?
}