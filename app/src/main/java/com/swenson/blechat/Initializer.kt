package com.swenson.blechat

import android.content.Context
import com.swenson.blechat.di.appComponents
import com.swenson.blechat.di.networkComponent
import org.koin.android.ext.koin.androidContext

import org.koin.core.context.startKoin



class Initializer(private val context: Context) {

    fun initKoin() {
        startKoin {
            androidContext(context)
            modules(
                appComponents +
                        networkComponent
            )
        }
    }
}