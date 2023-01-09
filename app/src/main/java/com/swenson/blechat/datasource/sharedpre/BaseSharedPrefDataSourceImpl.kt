package com.swenson.blechat.datasource.sharedpre

import com.swenson.blechat.model.response.CacheEntry
import com.swenson.blechat.util.parsing.ParsingHelper
import com.swenson.blechat.extention.convertToModel
import java.lang.reflect.Type

class BaseSharedPrefDataSourceImpl : BaseSharedPrefDataSource {
    override fun getCachedObject(type: Type): Any? {
        val entry: CacheEntry<Any>? = SecureSharedPref.getObject(type)
        val cachedObject: Any? = entry?.obj
        return if (cachedObject != null) {
            val mCachedObject: Any? =
                ParsingHelper.gson?.toJson(cachedObject)?.convertToModel(type)
            mCachedObject
        } else null
    }


    override fun <T> saveObject(instance: T, type: Type, lastModifiedDate: Long) =
        SecureSharedPref.edit().putObject(CacheEntry(instance, lastModifiedDate), type).apply()

    override fun clearAllCaches() {
        SecureSharedPref.edit().clear().apply()
    }

}