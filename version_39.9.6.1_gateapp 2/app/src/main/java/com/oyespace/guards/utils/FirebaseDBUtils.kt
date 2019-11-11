package com.oyespace.guards.utils

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.oyespace.guards.models.NotificationSyncModel

class FirebaseDBUtils {

    companion object {


        fun updateFirebaseColor(visitorId: Int, buttonColor: String = "#ffb81a") {

            val childName = "A_${Prefs.getInt(ConstantUtils.ASSOCIATION_ID, 0)}"
            Log.d("taaag", "push to firebase: $visitorId")
            val notificationSyncModel = NotificationSyncModel(visitorId, buttonColor)
            FirebaseDatabase.getInstance()
                .getReference("NotificationSync")
                .child(childName)
                .child(visitorId.toString()).setValue(notificationSyncModel)

        }

        fun removeFBNotificationSyncEntry(visitorId: Int) {

            val childName = "A_${Prefs.getInt(ConstantUtils.ASSOCIATION_ID, 0)}"
            Log.i("taaag", "remove from firebase: $visitorId")
            FirebaseDatabase.getInstance()
                .getReference("NotificationSync")
                .child(childName)
                .child(visitorId.toString())
                .removeValue()

        }

        fun addWalkieTalkieAudioFirebase(fileName: String) {
            val childName = "A_${Prefs.getInt(ConstantUtils.ASSOCIATION_ID, 0)}"
            FirebaseDatabase.getInstance()
                .getReference("wt_audio")
                .child(childName)
                .child("f")
                .setValue(fileName)
        }


        fun removeWalkieTalkieAudioFirebase() {
            val childName = "A_${Prefs.getInt(ConstantUtils.ASSOCIATION_ID, 0)}"
            FirebaseDatabase.getInstance()
                .getReference("wt_audio")
                .child(childName)
                .child("f")
                .removeValue()
        }

    }

}