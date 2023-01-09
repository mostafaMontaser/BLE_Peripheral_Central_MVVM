package com.swenson.blechat.util.network

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket

import java.net.URI

import java.net.URISyntaxException

object SocketHelper  {

    lateinit var mSocket: Socket

    @Synchronized
    fun initSocket(){
        try {
            val options = IO.Options()
            options.timeout=(60000)
            options.transports=(arrayOf(io.socket.engineio.client.transports.WebSocket.NAME))
            options.secure=(true)
            options.reconnection =true
            options.upgrade=false



            mSocket = IO.socket(URI.create("https://dev.kamashka.com:6001"),options)
        }catch (e:URISyntaxException){
            Log.d("ssssss", "initSocket: e ")
        }
    }

    @Synchronized
    fun getSocket() = mSocket
}