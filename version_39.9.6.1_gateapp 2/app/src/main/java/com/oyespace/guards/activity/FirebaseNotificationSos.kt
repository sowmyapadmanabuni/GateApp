package com.oyespace.guards.com.oyespace.guards.activity

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceIdService
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage



class FirebaseNotificationSos: FirebaseMessagingService() {
    override fun onNewToken(s: String?) {
        super.onNewToken(s)
        Log.d("New Token",s)

    }

}