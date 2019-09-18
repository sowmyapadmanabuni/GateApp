package com.oyespace.guards.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log


class NetworkAvailable {

     val instance = NetworkAvailable()
    var context: Context?=null
    var connectivityManager: ConnectivityManager?=null
    var wifiInfo: NetworkInfo? = null
    var mobileInfo:NetworkInfo? = null
    var connected = false

    fun getInstance(ctx: Context): NetworkAvailable {
        context = ctx.getApplicationContext()
        return instance
    }

    fun isOnline(): Boolean {
        try {
            connectivityManager = context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

            val networkInfo = connectivityManager!!.activeNetworkInfo
            connected = networkInfo != null && networkInfo.isAvailable &&
                    networkInfo.isConnected
            return connected


        } catch (e: Exception) {
            println("CheckConnectivity Exception: " + e.message)
            Log.v("connectivity", e.toString())
        }

        return connected
    }
}