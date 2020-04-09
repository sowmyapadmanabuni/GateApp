package com.oyespace.guards.utils

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.oyespace.guards.models.NotificationSyncModel
import com.oyespace.guards.models.VisitorLog
import com.oyespace.guards.utils.ConstantUtils.ENTRYPENDING
import com.oyespace.guards.utils.ConstantUtils.EXITPENDING

class FirebaseDBUtils {

    companion object {


        fun updateFirebaseColor(visitorId: Int, buttonColor: String = "#ffb81a",status:String=ENTRYPENDING) {

            val childName = "A_${Prefs.getInt(ConstantUtils.ASSOCIATION_ID, 0)}"
            Log.e("taaag_firebase", "push to firebase: $visitorId")
            val notificationSyncModel = NotificationSyncModel(visitorId, buttonColor,status,"","","",false)
            FirebaseDatabase.getInstance().getReference("NotificationSync").child(childName).child(visitorId.toString()).setValue(notificationSyncModel)

        }

        fun updateVisitorLog(visitorId: Int, visitorLog: VisitorLog, accountId:String,visitorJSON:String, buttonColor: String = "#ffb81a",status:String=ENTRYPENDING) {

            val childName = "A_${Prefs.getInt(ConstantUtils.ASSOCIATION_ID, 0)}"
            Log.e("taaag_firebase", "push to firebase: $visitorId")
            val notificationSyncModel = NotificationSyncModel(visitorId, buttonColor,status,visitorLog.unUnitID,accountId,visitorJSON,false)
            FirebaseDatabase.getInstance().getReference("NotificationSync").child(childName).child(visitorId.toString()).setValue(notificationSyncModel)

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
        fun updateFirebaseColorforExit(visitorId: Int,visitorLog: VisitorLog,visitorJSON:String, buttonColor: String = "#ffb81a",status:String=EXITPENDING) {

            val childName = "A_${Prefs.getInt(ConstantUtils.ASSOCIATION_ID, 0)}"
            Log.d("taaag", "push to firebase: $visitorId")
            val notificationSyncModel = NotificationSyncModel(visitorId, buttonColor,status,visitorLog.unUnitID,"",visitorJSON,false)
            FirebaseDatabase.getInstance()
                .getReference("NotificationSync")
                .child(childName)
                .child(visitorId.toString()).setValue(notificationSyncModel)

        }

        fun updateKidExitFirebaseColor(visitorId: Int, unitId:String,visitorJSON: String, buttonColor: String = "#ffb81a",status:String=EXITPENDING) {

            val childName = "A_${Prefs.getInt(ConstantUtils.ASSOCIATION_ID, 0)}"
            Log.d("taaag", "push to firebase: $visitorId")
            val notificationSyncModel = NotificationSyncModel(visitorId, buttonColor,status,"","","",true)
            FirebaseDatabase.getInstance()
                .getReference("NotificationSync")
                .child(childName)
                .child(visitorId.toString()).setValue(notificationSyncModel)

        }

    }


}