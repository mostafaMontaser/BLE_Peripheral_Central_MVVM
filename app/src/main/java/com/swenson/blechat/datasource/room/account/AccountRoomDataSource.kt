package com.swenson.blechat.datasource.room.account

import com.swenson.blechat.BLEChatApplication
import com.swenson.blechat.datasource.room.DeliveryDatabase
import com.swenson.blechat.datasource.room.RoomDataSource
import com.swenson.blechat.model.response.BaseModel
import com.swenson.blechat.model.signin.domainToRoom

class AccountRoomDataSource : RoomDataSource {
    override suspend fun saveObject(model: BaseModel?) {
        if (model is BaseModel) {
            DeliveryDatabase.getInstance(BLEChatApplication.getContext())?.accountDao?.clear()
           // DeliveryDatabase.getInstance(BLEChatApplication.getContext())?.accountDao?.insert(model.payload?.data?.domainToRoom())
        }
        }

    override suspend fun getCachedObject(): BaseModel? {
        return DeliveryDatabase.getInstance(BLEChatApplication.getContext())?.accountDao?.getAccount()
            ?.domainToModel()
    }
}