package com.oyespace.guards.fcm

import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import android.telephony.TelephonyManager
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService

class FCMInstantIDService : FirebaseMessagingService() {

    private val TAG = "MyAndroidFCMIIDService"

    override fun onNewToken(token: String?) {
        super.onNewToken(token)
        val refreshedToken = FirebaseInstanceId.getInstance().getToken()
        //Log the token
        Log.d(TAG, "Refreshed token: " + refreshedToken!!)

        FirebaseMessaging.getInstance().subscribeToTopic("Gate")
    }

//    override fun onTokenRefresh() {
//        //Get hold of the registration token
//
//
//    }

}