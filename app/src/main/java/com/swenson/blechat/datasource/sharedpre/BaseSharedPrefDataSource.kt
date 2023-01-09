package com.swenson.blechat.datasource.sharedpre

import java.lang.reflect.Type
import java.util.*

interface BaseSharedPrefDataSource {
    fun getCachedObject(type: Type): Any?
    fun <T>saveObject(instance: T, type: Type, lastModifiedDate: Long = Date().time)
    fun clearAllCaches()
}