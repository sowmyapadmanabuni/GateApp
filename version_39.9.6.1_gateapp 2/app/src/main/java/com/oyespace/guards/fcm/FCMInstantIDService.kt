package com.oyespace.guards.fcm

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import android.telephony.TelephonyManager
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import com.google.firebase.messaging.FirebaseMessaging

class FCMInstantIDService : FirebaseInstanceIdService() {

    private val TAG = "MyAndroidFCMIIDService"

    override fun onTokenRefresh() {
        //Get hold of the registration token
        val refreshedToken = FirebaseInstanceId.getInstance().token
        //Log the token
        Log.d(TAG, "Refreshed token: " + refreshedToken!!)

        FirebaseMessaging.getInstance().subscribeToTopic("Gate")

    }




}