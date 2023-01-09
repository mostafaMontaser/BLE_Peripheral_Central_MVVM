package com.swenson.blechat

import android.content.Context
import androidx.multidex.MultiDexApplication
import java.lang.ref.WeakReference

class BLEChatApplication : MultiDexApplication() {

    companion object {
        private var context: WeakReference<Context>? = null
        fun getContext(): Context? = context?.get()
    }

    private lateinit var initializer: Initializer

    override fun onCreate() {
        super.onCreate()
        context = WeakReference(applicationContext)
        initializer = Initializer(this)
        initializer.initKoin()
    }
}

