package com.oyespace.guards.fcm

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Handler
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.util.Log
import android.widget.Toast
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.oyespace.guards.BackgroundSyncReceiver
import com.oyespace.guards.DataBaseHelper
import com.oyespace.guards.R
import com.oyespace.guards.activity.TicketingDetailsActivity
import com.oyespace.guards.cloudfunctios.CloudFunctionRetrofitClinet
import com.oyespace.guards.constants.PrefKeys.EMERGENCY_SOUND_ON
import com.oyespace.guards.network.CommonDisposable
import com.oyespace.guards.network.ImageApiClient
import com.oyespace.guards.network.ImageApiInterface
import com.oyespace.guards.network.RetrofitClinet
import com.oyespace.guards.pojo.CloudFunctionNotificationReq
import com.oyespace.guards.pojo.GetTicketingResponsesRes
import com.oyespace.guards.pojo.SendGateAppNotificationRequest
import com.oyespace.guards.pojo.TicketingResponseData
import com.oyespace.guards.utils.ConstantUtils
import com.oyespace.guards.utils.ConstantUtils.*
import com.oyespace.guards.utils.LocalDb
import com.oyespace.guards.utils.Prefs
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.internal.util.HalfSerializer.onError
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.*

class FCMMessagingService : FirebaseMessagingService(){
    internal var msg = ""
    lateinit var mp:  MediaPlayer
    internal var handler = Handler()
    internal lateinit var mediaPlayer: MediaPlayer

//    private val firebaseDatabase = FirebaseDatabase.getInstance()
//    private val mRootReference = firebaseDatabase.getReference()
//    private var mChildReference:DatabaseReference? = null

    //   private val TAG = "MyFirebaseToken"//FirebaseInstanceId.getInstance().token
    private val TAG = FirebaseInstanceId.getInstance().token
    private lateinit var notificationManager: NotificationManager
    private val ADMIN_CHANNEL_ID = "GateApp"

    override fun onNewToken(token: String?) {
        super.onNewToken(token)
        Log.i(TAG, token)
    }
    lateinit var dbh:DataBaseHelper;

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        remoteMessage?.let { message ->
            //  Log.i(TAG, message.getData().get("message"))

            notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            //Setting up Notification channels for android O and above
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                setupNotificationChannels()
            }
            val notificationId = Random().nextInt(60000)

            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            val notificationBuilder = NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
                .setSmallIcon(R.drawable.oyespace_launcher)  //a resource for your custom small icon
                .setContentTitle(message.data["title"]) //the "title" value you sent in your notification
                .setContentText(message.data["message"]) //ditto
                .setAutoCancel(true)  //dismisses the notification on click
                .setSound(defaultSoundUri)

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.notify(notificationId /* ID of notification */, notificationBuilder.build())

        }


        Log.d("JSON s", "From:  " + remoteMessage!!.from)
//        getNotification(Prefs.getInt(ASSOCIATION_ID,0),LocalDb.getAssociation()!!.asAsnName,"Oyespace","Gate App",
//            "gate_app","Gate",Prefs.getInt(DEVICE_ID,0))

        try
        {
            dbh = DataBaseHelper(applicationContext)
            Log.d("Dgddfdf", "fcm:msg " + remoteMessage!!.data["activt"])
        }
        catch (ex:Exception) {
            ex.printStackTrace()
            Log.d("JSON exbg", "Dgddfdfeemer  " + ex.toString() )

            Log.d("JSON exbg", "Notification  " + " data " + remoteMessage!!.data)
        }

        Log.d("JSON s", "data: " + remoteMessage!!.data)

        try
        {
            if (remoteMessage!!.data["activt"].equals("visitorEntryApproval", ignoreCase = true))
            {

                //TODO only notification and open respective activity
                Log.d("JSON in", "childExitApproved: " + remoteMessage!!.data["name"] + " " + remoteMessage!!.data["entry_type"])

//                val intentAction = Intent(applicationContext, DownloadResDataReceiver::class.java)
//                intentAction.putExtra("action", DAILY_HELP)
//                sendBroadcast(intentAction)

                Log.d("JSON in", "visitorEntryApproval: " + remoteMessage!!.data["name"] + " " + remoteMessage!!.data["entry_type"])
            }
            else if (remoteMessage!!.data["activt"].equals("childExitApproval", ignoreCase = true))
            {

//                dbh.insertSecurityNotificationTable(prefManager.getAssociationId(),
//                    remoteMessage!!.data["activt"],
//                    remoteMessage!!.data["name"], "Child Exit", Integer.valueOf(remoteMessage!!.data["nr_id"]), remoteMessage!!.data["mobile"].trim { it <= ' ' })

//                childExitNotification("Child Exit Permission", remoteMessage!!.data["name"],
//                    remoteMessage!!.data["nr_id"], remoteMessage!!.data["mobile"].trim { it <= ' ' })

                Log.d("JSON in", "Child_Exit: " + remoteMessage!!.data["name"] + " " + remoteMessage!!.data["entry_type"])

            }
            else if (remoteMessage!!.data["activt"].equals("childExitApproved", ignoreCase = true))
            {

                //TODO notification and open respective activity
                Log.d("JSON in", "childExitApproved: " + remoteMessage!!.data["name"] + " " + remoteMessage!!.data["entry_type"])

//                dbh.updateVisitorEntryRequest(Integer.valueOf(remoteMessage!!.data["nr_id"]), dateFormat_YMD_hms.format(Date()), prefManager.getGuardID())
//                makeAutoEntry(Integer.valueOf(remoteMessage!!.data["nr_id"]))

//                val intentAction = Intent(applicationContext, DownloadResDataReceiver::class.java)
//                intentAction.putExtra("action", NONREGULAR)
//                sendBroadcast(intentAction)

                Log.d("JSON in", "visitorEntryApproval: " + remoteMessage!!.data["name"] + " " + remoteMessage!!.data["entry_type"])
            }
            else if (remoteMessage!!.data["activt"].equals("CourierEntryApproval", ignoreCase = true))
            {

//                courierEntryReject(remoteMessage!!.data["name"] + " " + remoteMessage!!.data["entry_type"],
//                    remoteMessage!!.data["mobile"], remoteMessage!!.data["nr_id"]
//                )

//                dbh.insertSecurityNotificationTable(prefManager.getAssociationId(),
//                    remoteMessage!!.data["activt"],
//                    remoteMessage!!.data["name"], "Courier Entry", Integer.valueOf(remoteMessage!!.data["nr_id"]), remoteMessage!!.data["mobile"].trim { it <= ' ' })

//                val intentAction = Intent(applicationContext, DownloadResDataReceiver::class.java)
//                intentAction.putExtra("action", DAILY_HELP)
//                sendBroadcast(intentAction)

                Log.d("JSON in", "visitorEntryApproval: " + remoteMessage!!.data["name"] + " " + remoteMessage!!.data["entry_type"])

            }
            else if (remoteMessage!!.data["activt"].equals("setFirebaseWelcomeMsg", ignoreCase = true))
            {

//                mChildReference = mRootReference.child("message" + prefManager.getAssociationId())
//                mChildReference!!.setValue(remoteMessage!!.data["name"])
//                prefManager.setWelcomeMessage(remoteMessage!!.data["name"])

            }
            else if (remoteMessage!!.data["activt"].equals("getFirebaseWelcomeMsg", ignoreCase = true))
            {

//                prefManager = PrefManager(applicationContext)
//                mChildReference = mRootReference.child("message" + prefManager.getAssociationId())
//
//                mChildReference!!.addValueEventListener(object:ValueEventListener() {
//                    fun onDataChange(dataSnapshot:DataSnapshot) {
//                        if (dataSnapshot.getValue(String::class.java) == null)
//                        {
//                            prefManager.setWelcomeMessage("Welcome")
//                            sendFCM_welcomeMsg(prefManager.getWelcomeMessage())
//                        }
//                        else
//                        {
//                            prefManager.setWelcomeMessage(dataSnapshot.getValue(String::class.java))
//                            sendFCM_welcomeMsg(prefManager.getWelcomeMessage())
//                        }
//                        Log.d("Message", "A" + prefManager.getWelcomeMessage())
//                    }
//
//                    fun onCancelled(databaseError:DatabaseError) {
//
//                    }
//                })

            }
            else if (remoteMessage!!.data["activt"].equals("CourierReply", ignoreCase = true))
            {

//                val intentAction = Intent(applicationContext, DownloadResDataReceiver::class.java)
//                intentAction.putExtra("action", "COURIER")
//                intentAction.putExtra("nrid", remoteMessage!!.data["nr_id"])
//                sendBroadcast(intentAction)

//                if (prefManager.getAttendanceID() === 0)
//                {
//
//                }
//                else
//                {
//                    val i = Intent(this, CourierNotification::class.java)
//                    i.putExtra("action", "COURIER")
//                    i.putExtra("nrid", remoteMessage!!.data["nr_id"])
//                    i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                    startActivity(i)
//                }

                Log.d("JSON in", "CourierReply: " + remoteMessage!!.data["name"] + " " + remoteMessage!!.data["entry_type"] + " " + remoteMessage!!.data["nr_id"])

            }
            else if (remoteMessage!!.data["activt"].equals("permissionStatus", ignoreCase = true))
            {

                //TODO only notification
                Log.d("JSON in", "permissionStatus: " + remoteMessage!!.data["name"] + " " + remoteMessage!!.data["entry_type"])

            }
            else if (remoteMessage!!.data["activt"].equals(Emergency, ignoreCase = true))
            {
                Log.d("Dgddfdfe fcm in emer", remoteMessage!!.data["incidentId"] + " " + remoteMessage!!.data["mob"]
                        + " " + remoteMessage!!.data["incidentId"])

                Log.d("Dgddfdfeemer ",   " 205 " )
                //val intent = Intent(this, SplashActivity::class.java)
                getEmerResponses(
                    remoteMessage!!.data["name"].toString(), remoteMessage!!.data["mob"].toString(),
                    remoteMessage!!.data["incidentId"].toString(), true, remoteMessage!!.data["gps"].toString()
                )

            }
            else if (remoteMessage!!.data["activt"].equals("emergencyAttend", ignoreCase = true))
            {
                Log.d("Dgddfdfe fcm in attend", remoteMessage!!.data["incidentId"] + " " + remoteMessage!!.data["mob"]
                        + " " + remoteMessage!!.data["incidentId"])
                Prefs.putBoolean(EMERGENCY_SOUND_ON,false);

            }
            else if (remoteMessage!!.data["activt"].equals("incident", ignoreCase = true))
            {
//                dbh.insertSecurityNotificationTable(prefManager.getAssociationId(),
//                    remoteMessage!!.data["activt"], remoteMessage!!.data["name"], remoteMessage!!.data["gps"],
//                    Integer.valueOf(remoteMessage!!.data["incidentId"]), remoteMessage!!.data["mob"].trim { it <= ' ' })
//
//                if (!prefManager.getEmergency() && prefManager.getIncidentID() === 0)
//                {
//                    getEmerResponses( remoteMessage!!.data["name"], remoteMessage!!.data["mob"],
//                        remoteMessage!!.data["incidentId"], false, remoteMessage!!.data["gps"]        )
//
//                }

            }
            else if (remoteMessage!!.data["activt"].equals(ConstantUtils.BACKGROUND_SYNC, ignoreCase = true))
            {
                Log.d("Dgddfdf", "fcm:msg " + remoteMessage!!.data["activt"])
                val intentAction1 = Intent(applicationContext, BackgroundSyncReceiver::class.java)
                intentAction1.putExtra(ConstantUtils.BSR_Action, ConstantUtils.VISITOR_ENTRY_SYNC)
                sendBroadcast(intentAction1)

            }
//            else if (remoteMessage!!.data["activt"].equals(NONREGULAR_BYID, ignoreCase = true))
//            {
//                Log.d("Dgddfdf", "fcm:msg " + remoteMessage!!.data["activt"] + " " + remoteMessage!!.data["nr_id"])
//                val intentAction1 = Intent(applicationContext, DownloadResDataReceiver::class.java)
//                intentAction1.putExtra(action, NONREGULAR_BYID)
//                intentAction1.putExtra(OYENonRegularVisitorID, remoteMessage!!.data["nr_id"])
//                sendBroadcast(intentAction1)

//            }
//            else if (remoteMessage!!.data["activt"].equals(Attendance, ignoreCase = true))
//            {
//                val intentAction1 = Intent(applicationContext, DownloadResDataReceiver::class.java)
//                intentAction1.putExtra(action, GUARD + Attendance)
//                sendBroadcast(intentAction1)
            //   }
            else if (remoteMessage!!.data["activt"].equals("PatrollingNotDone", ignoreCase = true))
            {

                //TODO only notification and open respective activity
                Log.d("JSON in", "PatrollingNotDone: " + remoteMessage!!.data["name"] + " " + remoteMessage!!.data["entry_type"])

            }
            else if (remoteMessage!!.data["activt"].equals("SyncMemberDNDstatus", ignoreCase = true))
            {

                dbh.updateDNDlocal(Integer.valueOf(remoteMessage!!.data["nr_id"]),
                    remoteMessage!!.data["name"], remoteMessage!!.data["mobile"]
                )

                Log.d("JSON in", "SyncMemberDND: " + remoteMessage!!.data["name"] + " " + remoteMessage!!.data["entry_type"])
            }
            else if (remoteMessage!!.data["activt"].equals("SyncOtpPhotostatus", ignoreCase = true))
            {

                dbh.updateOtpStatus_photoStatus(Integer.valueOf(remoteMessage!!.data["nr_id"]),
                    remoteMessage!!.data["name"], remoteMessage!!.data["mobile"]
                )

                Log.d("JSON in", "SyncOtpPhotostatus: " + remoteMessage!!.data["name"] + " " + remoteMessage!!.data["entry_type"])
            }
            else if (remoteMessage!!.data["activt"].equals("SyncAssociationSettings", ignoreCase = true))
            {

                dbh.updatesettingStatusAssociation(Integer.valueOf(remoteMessage!!.data["nr_id"]),
                    remoteMessage!!.data["name"], remoteMessage!!.data["mobile"]
                )

                Log.d("JSON in", "SyncOtpPhotostatus: " + remoteMessage!!.data["name"] + " " + remoteMessage!!.data["entry_type"])

            }
            else if (remoteMessage!!.data["activt"].equals("emergencyResponse", ignoreCase = true))
            {

                //TODO only notification and open respective activity
                Log.d("JSON in", "emergencyResponse: " + remoteMessage!!.data["name"] + " " + remoteMessage!!.data["entry_type"])

            }
            else if (remoteMessage!!.data["activt"].equals("AssignedTask", ignoreCase = true))
            {

                //TODO notification and open respective activity
                Log.d("JSON in", "AssignedTask: " + remoteMessage!!.data["name"] + " " + remoteMessage!!.data["entry_type"])

//                dbh.insertSecurityNotificationTable(prefManager.getAssociationId(), remoteMessage!!.data["activt"],
//                    remoteMessage!!.data["name"], "Assigned Task", dbh.getAdminMemberID(prefManager.getAssociationId()), remoteMessage!!.data["mob"].trim { it <= ' ' })

//                val intentAction = Intent(applicationContext, DownloadResDataReceiver::class.java)
//                intentAction.putExtra("action", "MyTask")
//                sendBroadcast(intentAction)

                Log.d("JSON in", "visitorEntryApproval: " + remoteMessage!!.data["name"] + " " + remoteMessage!!.data["entry_type"])

            }
            else if (remoteMessage!!.data["activt"].equals("meeting", ignoreCase = true))
            {

                //TODO notification and open respective activity
                Log.d("JSON in", "meeting: " + remoteMessage!!.data["name"] + " " + remoteMessage!!.data["entry_type"])

//                dbh.insertSecurityNotificationTable(prefManager.getAssociationId(), remoteMessage!!.data["activt"],
//                    remoteMessage!!.data["name"], "meeting", dbh.getAdminMemberID(prefManager.getAssociationId()), remoteMessage!!.data["mob"].trim { it <= ' ' })

            }
            else if (remoteMessage!!.data["activt"].equals("GroupMember", ignoreCase = true))
            {
                //TODO notification and open respective activity
                Log.d("JSON in", "GroupMember: " + remoteMessage!!.data["name"] + " " + remoteMessage!!.data["entry_type"])

            }
            else if (remoteMessage!!.data["activt"].equals("audiomessage", ignoreCase = true))
            {

                getAudio(remoteMessage!!.data["entry_type"].toString())
                //TODO notification and open respective activity
                Log.d("JSON in", "audiomessage: " + remoteMessage!!.data["name"] + " " + remoteMessage!!.data["entry_type"])

            }
            else
            {

                //TODO Default notification
                Log.d("JSON in", "Default : " + remoteMessage!!.data["name"] + " " + remoteMessage!!.data["entry_type"])
            }
        }
        catch (ex:Exception) {

            Log.d("JSON ex3 858", "Notification  " + ex.toString() + " data " + remoteMessage!!.data)
        }

        Log.d("JSON 1", "Notification Message Body: " + " data " + remoteMessage!!.data)

    }

    private fun sendFCM_welcomeMsg(welcomeMessage:String) {
//        val apiService = FCMApiClient.getClient().create(FCMApiInterface::class.java)
//
//        val payloadData = EntryPermissionPayload("getFirebaseWelcomeMsgReply",
//            welcomeMessage, 1,
//            entry_type[3], GlobalVariables.getGlobal_mobilenumber(), prefManager.getAssociationId())
//        val sendOTPRequest = SendEntryPermissionRequest(payloadData, "/topics/Admin" + prefManager.getAssociationId())
//        val call = apiService.sendEntryPermission(sendOTPRequest)
//
//        call.enqueue(object: Callback<SendFCMResponse> {
//            override fun onResponse(call: Call<SendFCMResponse>, response: Response<SendFCMResponse>) {
//                Log.d("Dgddfdf", "fcm: " + response.body()!!.getMessage_id())
//
//            }
//
//            override fun onFailure(call: Call<SendFCMResponse>, t:Throwable) {
//                Log.d("TAG", t.toString())
//            }
//        })
    }

    internal fun courierEntryReject(title:String, mobileNumber:String, nrid:String) {

//        try
//        {
//            val intentAction_approve = Intent(applicationContext, NotificationReceiver::class.java)
//            intentAction_approve.putExtra("action", "Approve")
//            intentAction_approve.putExtra("mob", mobileNumber)
//            intentAction_approve.putExtra("nr_id", nrid)
//
//            val intentAction_deny = Intent(applicationContext, NotificationReceiver::class.java)
//            intentAction_deny.putExtra("action", "Deny")
//            intentAction_deny.putExtra("mob", mobileNumber)
//            intentAction_deny.putExtra("nr_id", nrid)
//
//            val intentAction_leaveatguard = Intent(applicationContext, NotificationReceiver::class.java)
//            intentAction_leaveatguard.putExtra("action", "LeaveAtGuard")
//            intentAction_leaveatguard.putExtra("mob", mobileNumber)
//            intentAction_leaveatguard.putExtra("nr_id", nrid)
//
//            val notificationView = RemoteViews(packageName, R.layout.activity_entryapproval_notification)
//
//            val notifyMgr = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//            val notificationId = 1
//            val channelId = "channel-61"
//            val channelName = "Channel CourierEntryApproval"
//            val importance = NotificationManager.IMPORTANCE_HIGH
//
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
//            {
//                val mChannel = NotificationChannel(
//                    channelId, channelName, importance)
//
//                assert(notifyMgr != null)
//                mChannel.description = "no sound"
//                mChannel.setSound(null, null)
//                mChannel.enableLights(false)
//                mChannel.lightColor = Color.BLUE
//                mChannel.enableVibration(false)
//
//                notifyMgr!!.createNotificationChannel(mChannel)
//            }
//
//            val pIntent_approve = PendingIntent.getBroadcast(applicationContext, 71, intentAction_approve, PendingIntent.FLAG_UPDATE_CURRENT)
//            val pIntent_deny = PendingIntent.getBroadcast(applicationContext, 72, intentAction_deny, PendingIntent.FLAG_UPDATE_CURRENT)
//            val pIntent_leaveatguard = PendingIntent.getBroadcast(
//                applicationContext, 73,
//                intentAction_leaveatguard, PendingIntent.FLAG_UPDATE_CURRENT)
//            notificationView.setTextViewText(R.id.txt_title, title)
//            notificationView.setOnClickPendingIntent(R.id.btn_approve, pIntent_approve)
//            notificationView.setOnClickPendingIntent(R.id.btn_reject, pIntent_deny)
//            notificationView.setOnClickPendingIntent(R.id.btn_leaveatgate, pIntent_leaveatguard)
//
//            val intent = Intent(this, SplashActivity::class.java)
//            val resultIntent = PendingIntent.getActivity(this, 990, intent,
//                PendingIntent.FLAG_ONE_SHOT)
//
//            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//
//            val drivingNotifBldr = NotificationCompat.Builder(applicationContext, channelId)
//                .setSmallIcon(R.drawable.oye247)
//                .setContent(notificationView)
//                .setAutoCancel(true)
//                .setSound(defaultSoundUri)
//                .setContentIntent(pIntent_approve)
//                .setContentIntent(resultIntent)
//                .setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_SOUND)
//                .setPriority(Notification.PRIORITY_MAX)
//
//            val stackBuilder = TaskStackBuilder.create(applicationContext)
//            stackBuilder.addNextIntent(intent)
//            val resultPendingIntent = stackBuilder.getPendingIntent(
//                Emergency_Notification_ID,
//                PendingIntent.FLAG_UPDATE_CURRENT
//            )
//            drivingNotifBldr.setContentIntent(resultPendingIntent)
//
//            notifyMgr!!.notify(Emergency_Notification_ID, drivingNotifBldr.build())
//
//            Log.d("Dgddfdfe check bf emer", " " + prefManager.getGuardID() + " " + prefManager.getAttendanceID())
//        }
//        catch (ex:Exception) {
//
//            Toast.makeText(applicationContext, "Toast", Toast.LENGTH_SHORT).show()
//        }

    }

    internal fun yesNoNotification_25(title:String, mobileNumber:String, emergencyID:String, emergencyB:Boolean, gps:String) {

        val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
//        am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0)
        val i = Intent(applicationContext, TicketingDetailsActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(i)

//                //to play sound external sound files
//                // final MediaPlayer mediaPlayer;
        mediaPlayer = MediaPlayer.create(this, R.raw.emergency)
        mediaPlayer.start()
        Log.d("Dgddfdfeemer ",   " 547 " )

        Prefs.putBoolean(EMERGENCY_SOUND_ON,true);

        playMediaFile()

        val r = Runnable {
            Prefs.putBoolean(EMERGENCY_SOUND_ON,false);
//                    Log.d("Dgddfdfe run in stop", " " + "runnable ")
        }
        Log.d("Dgddfdfe run else emer", " " + "runnable ffff ")
        handler.postDelayed(r, 60000)

    }

    internal fun childExitNotification(title:String, name:String, nrv_id:String, unit_id:String) {

//        try
//        {
//            val intentAction = Intent(applicationContext, ChildNotificationReceiver::class.java)
//            intentAction.putExtra("action", Attend)
//            intentAction.putExtra("det", name)
//            intentAction.putExtra("nrv_id", nrv_id)
//            intentAction.putExtra("unit_id", unit_id)
//            Log.d("Child_Exit gps", unit_id)
//
//            val intentActionselected = Intent(applicationContext, ChildNotificationReceiver::class.java)
//            intentActionselected.putExtra("action", Pass)
//            intentActionselected.putExtra("det", name)
//            intentActionselected.putExtra("nrv_id", nrv_id)
//            intentActionselected.putExtra("unit_id", unit_id)
//            Log.d("Child_Exit gps", unit_id)
//
//            val notificationView = RemoteViews(packageName, R.layout.activity_child_exit_notification)
//            val notifyMgr = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//            val notificationId = 1
//            val channelId = "channel-01"
//            val channelName = "Channel Name"
//            val importance = NotificationManager.IMPORTANCE_HIGH
//
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
//            {
//                val mChannel = NotificationChannel(
//                    channelId, channelName, importance)
//
//                assert(notifyMgr != null)
//                mChannel.description = "no sound"
//                mChannel.setSound(null, null)
//                mChannel.enableLights(false)
//                mChannel.lightColor = Color.BLUE
//                mChannel.enableVibration(false)
//
//                notifyMgr!!.createNotificationChannel(mChannel)
//            }
//
//            val pIntentlogin = PendingIntent.getBroadcast(applicationContext, 81, intentAction, PendingIntent.FLAG_UPDATE_CURRENT)
//            val pIntentselected = PendingIntent.getBroadcast(
//                applicationContext, 82,
//                intentActionselected, PendingIntent.FLAG_UPDATE_CURRENT)
//            notificationView.setTextViewText(R.id.txt_title, title)
//            notificationView.setOnClickPendingIntent(R.id.btn_allow, pIntentlogin)
//            notificationView.setOnClickPendingIntent(R.id.btn_pass, pIntentselected)
//
//            val intent = Intent(this, SplashActivity::class.java)
//            val resultIntent = PendingIntent.getActivity(this, 990, intent,
//                PendingIntent.FLAG_ONE_SHOT)
//
//            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//
//            val drivingNotifBldr = NotificationCompat.Builder(applicationContext, channelId)
//                .setSmallIcon(R.drawable.oye247)
//                .setContent(notificationView)
//                .setAutoCancel(true)
//                .setSound(defaultSoundUri)
//                .setContentIntent(pIntentlogin)
//                .setContentIntent(resultIntent)
//                .setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_SOUND)
//                .setPriority(Notification.PRIORITY_MAX)
//
//
//            val stackBuilder = TaskStackBuilder.create(applicationContext)
//            stackBuilder.addNextIntent(intent)
//            val resultPendingIntent = stackBuilder.getPendingIntent(
//                Emergency_Notification_ID,
//                PendingIntent.FLAG_UPDATE_CURRENT
//            )
//            drivingNotifBldr.setContentIntent(resultPendingIntent)
//
//            //            notifyMgr.notify(Emergency_Notification_ID, drivingNotifBldr.build());
//
//            Log.d("Dgddfdfe Child_Exit", "$title| $name| $nrv_id| $unit_id")
//            val unit_id_new = Integer.parseInt(unit_id)
//
//            val curPref = dbh.getMyMember_byUnitID(unit_id_new)
//            if (curPref.count > 0)
//            {
//                curPref.moveToFirst()
//                prefManager.setAssociationId(curPref.getInt(curPref.getColumnIndex("AssociationID")))
//                prefManager.setMemUnitID(curPref.getInt(curPref.getColumnIndex("OYEUnitID")))
//                prefManager.setMemRoleID(curPref.getInt(curPref.getColumnIndex("OYEMemberRoleID")))
//                prefManager.setMemberID(curPref.getInt(curPref.getColumnIndex("OYEMemberID")))
//
//                Log.d("Dgddfdf in if", "Child_Exit 1" + dbh.getUnitName(prefManager.getMemUnitID()) + " " + prefManager.getMemUnitID()
//                        + " " + prefManager.getMemRoleID() + " " + prefManager.getAssociationId()
//                )
//            }
//
//            val i = Intent(applicationContext, ViewChildExitList::class.java)
//            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//            startActivity(i)
//            Log.d("Dgddfdf af", "Child_Exit 1" + dbh.getUnitName(prefManager.getMemUnitID()) + " " + prefManager.getMemUnitID()
//                    + " " + prefManager.getMemRoleID() + " " + prefManager.getAssociationId()
//            )
//
//        }
//        catch (ex:Exception) {
//            Log.d("Dgddfdf in ex", "Child_Exit exexex" + ex.toString() + " " + prefManager.getMemUnitID()
//                    + " " + prefManager.getMemRoleID() + " " + prefManager.getAssociationId()
//            )
//
//            Toast.makeText(applicationContext, "Toast", Toast.LENGTH_SHORT).show()
//        }

    }

    private fun getEmerResponses(title:String, mobileNumber:String, emergencyId:String, emergencyB:Boolean, gps:String) {
        RetrofitClinet.instance
            .getTicketingResponses(OYE247TOKEN,emergencyId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CommonDisposable<GetTicketingResponsesRes<TicketingResponseData>>() {

                override fun onSuccessResponse(visitorList: GetTicketingResponsesRes<TicketingResponseData>) {
                    Log.d("getEmerResponses","in emergencyId "+emergencyId)
                    Log.d("getEmerResponses","Dgddfdfeemer in 417"+visitorList.toString())

                    if (visitorList.data.ticketingResponse !=null) {
                        Log.d("getEmerResponses",visitorList.toString());

                    } else {
                        Log.d("getEmerResponses","Dgddfdfeemer in 437")
                        yesNoNotification_25(title, mobileNumber, emergencyId, emergencyB, gps)
                        dbh.insertSecurityNotificationTable(LocalDb.getAssociation().asAssnID,
                            Emergency, title, gps, Integer.valueOf(emergencyId), mobileNumber)

                    }
                }

                override fun onErrorResponse(e: Throwable) {
                    Log.d("getEmerResponses",e.message);
                    Log.d("getEmerResponses","Dgddfdfeemer in 441")
                    yesNoNotification_25(title, mobileNumber, emergencyId, emergencyB, gps)
                    dbh.insertSecurityNotificationTable(LocalDb.getAssociation().asAssnID,
                        Emergency, title, gps, Integer.valueOf(emergencyId), mobileNumber)


                }

                override fun noNetowork() {

                }
            })

    }

    fun makeAutoEntry(NRVisitorID:Int) {

//        object: AsyncTask<String, String, String>() {
//
//            override fun doInBackground(vararg params:String):String? {
//                var response:String? = null
//
//                Log.d("TAG", "Number of movies received: " + prefManager.getAssociationId() + " " +
//                        NRVisitorID + " " + " " + " "
//                )
//
//                response = postApprovedVisitorEntryRequest(
//                    "{\"VLEntryT\":\"" + GMT_YMD_CurrentTime_only() + "\"," +
//                            "\"VLVisLgID\":" + NRVisitorID + "," +
//                            "\"VLEntyWID\":" + prefManager.getGuardID() +
//                            " }"
//                )
//                Log.d("Dgddfdf s", response!! + "")
//                return response
//
//            }
//
//            override fun onPostExecute(s:String?) {
//                super.onPostExecute(s)
//
//                if (s != null)
//                {
//                    try
//                    {
//                        Log.d("Dgddfdf", s!! + "")
//                        val jsonObj = JSONObject(s)
//                        val successb = jsonObj.getBoolean(success)
//
//                    }
//                    catch (e: JSONException) {
//                        Log.d("Dgddfdf 2", "Json parsing error: " + e.message)
//                    }
//
//                }
//                else
//                {
//                    Log.d("Dgddfdf 3", "Couldn't get json from server.")
//                }
//
//            }
//        }.execute("")
    }

    fun playMediaFile() {
        Log.d("Dgddfdfeemer ",   " 787 " )

        val r = Runnable {

            if (Prefs.getBoolean(EMERGENCY_SOUND_ON,false)) {
                playMediaFile()
                mediaPlayer = MediaPlayer.create(applicationContext, R.raw.emergency)
                mediaPlayer.start()
            }
        }

        handler.postDelayed(r, 4000)

    }

    fun getAudio(filename:String) {



//Define Notification Manager

//Define sound URI



        mp = MediaPlayer.create(applicationContext, R.raw.walkietalkiestart);

        try {
            if (mp.isPlaying()) {
                mp.stop()
                mp.release()
                //getAudio(remoteMessage!!.data["entry_type"].toString())
                mp = MediaPlayer.create(applicationContext, R.raw.walkietalkieinterference)
            }

            mp.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val mediaPlayer:MediaPlayer

        val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0)
        mediaPlayer =  MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setDataSource("http://mediaupload.oyespace.com/"+filename);
        Log.v("uploadAudio 714",filename)
        mediaPlayer.prepare();

        // Start playing audio from http url
        mediaPlayer.start();

        Log.v("uploadAudio 720","PLAYYY")

//mediaPlayer.setOnErrorListener( MediaPlayer.OnErrorListener() {
//     boolean onError(mp:MediaPlayer , int what, int extra) {
//        mp.reset();
//        return false;
//    }
//});






//           // val body = MultipartBody.Part.createFormData("Test", audioclip, requestFile)
//            val apiService = ImageApiClient.getImageClient().create(ImageApiInterface::class.java)
//            val call = apiService.getAudioFile()
//
//            call.enqueue(object : Callback<Any> {
//                override fun onResponse(call: Call<Any>, response: retrofit2.Response<Any>) {
//                    try {
//
//
//                        Log.d("PLAYAudio1", "response:" + response.body()!!)
//
//                    } catch (ex: Exception) {
//                        Log.d("PLAYAudio2", "errr:" + ex.toString())
//
//                    }
//
//                }
//
//                override fun onFailure(call: Call<Any>, t: Throwable) {
//                    Log.d("PLAYAudio3", t.toString())
//
//                }
//            })

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun setupNotificationChannels() {
        val adminChannelName = "Oyespace"
        val adminChannelDescription = "Security"

        val adminChannel: NotificationChannel
        adminChannel = NotificationChannel(ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_LOW)
        adminChannel.description = adminChannelDescription
        adminChannel.enableLights(true)
        adminChannel.lightColor = Color.RED
        adminChannel.enableVibration(true)
        notificationManager.createNotificationChannel(adminChannel)
    }
    private fun getNotification(associationID: Int, associationName: String, ntDesc: String, ntTitle: String, ntType: String, sbSubID: String, userID: Int) {

        val dataReq = SendGateAppNotificationRequest(associationID,associationName,ntDesc,ntTitle,ntType,sbSubID,userID )


        CloudFunctionRetrofitClinet.instance
            .getNotification(dataReq)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CommonDisposable<Any>() {

                override fun onSuccessResponse(any: Any) {

//                    if (workerListResponse.data.checkPointListByAssocID !=null) {
//                        Log.d("WorkerList success",workerListResponse.data.toString())
//                        var arrayList: ArrayList<CheckPointByAssocID>? = null
//                        arrayList=ArrayList()
//                        arrayList = workerListResponse.data.checkPointListByAssocID
//
//                        Collections.sort(arrayList, object : Comparator<CheckPointByAssocID>{
//                            override  fun compare(lhs: CheckPointByAssocID, rhs: CheckPointByAssocID): Int {
//                                return lhs.cpCkPName.compareTo(rhs.cpCkPName)
//                            }
//                        })
//
//                        LocalDb.saveCheckPointList(arrayList);
//
//                    } else {
//
//                    }
                }

                override fun onErrorResponse(e: Throwable) {
                    Log.d("Error WorkerList",e.toString())
                }

                override fun noNetowork() {

                }
            })
    }


}



