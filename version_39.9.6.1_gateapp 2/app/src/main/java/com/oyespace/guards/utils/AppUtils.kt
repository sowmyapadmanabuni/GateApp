package com.oyespace.guards.utils

import android.util.Log
import com.oyespace.guards.Myapp
import com.oyespace.guards.R


/**
 * Created by Kalyan on 04-Nov-17.
 */
class AppUtils {
    companion object {
        fun getPrice(price: Int?): String {
            val string = Myapp.getContext().getString(R.string.rupee)
            return string + " " + price
        }

        fun getPrice(price: String?): String {
            val string = Myapp.getContext().getString(R.string.rupee)
            return string + " " + price
        }

        fun getPrice(price: Double?): String {
            val string = Myapp.getContext().getString(R.string.rupee)
            return string + " " + price
        }

        fun intToString(price: Int?): String {
            return  ""+ price
        }



        fun calGeoLocationDiff(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Int {
            val earthRadius = 6371000.0 //meters
            val dLat = Math.toRadians(lat2 - lat1)
            val dLng = Math.toRadians(lng2 - lng1)
            val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                    Math.sin(dLng / 2) * Math.sin(dLng / 2)
            val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
            val dist = (earthRadius * c).toInt()

            Log.d("xgdssd Dgddfdf", "$lat1 $lng1 $lat2 $lng2 $dist")
            return dist
        }

    }




}