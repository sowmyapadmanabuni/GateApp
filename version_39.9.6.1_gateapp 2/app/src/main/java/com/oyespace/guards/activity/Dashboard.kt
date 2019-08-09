package com.oyespace.guards
import SecuGen.FDxSDKPro.*
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.PendingIntent
import android.content.*
import android.content.res.Configuration
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Color
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.speech.tts.TextToSpeech
import android.support.v4.content.ContextCompat.startActivity
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.telephony.TelephonyManager
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.*
import com.google.firebase.messaging.FirebaseMessaging
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.malinskiy.superrecyclerview.SuperRecyclerView
import com.oyespace.guards.activity.*
import com.oyespace.guards.adapter.VistorEntryListAdapter
import com.oyespace.guards.adapter.VistorListAdapter
import com.oyespace.guards.com.oyespace.guards.fcm.FRTDBService
import com.oyespace.guards.com.oyespace.guards.utils.ConnectionDetector
import com.oyespace.guards.constants.PrefKeys
import com.oyespace.guards.guest.GuestCustomViewFinderScannerActivity
import com.oyespace.guards.network.*
import com.oyespace.guards.ocr.*
import com.oyespace.guards.pertroling.PatrollingActivitynew
import com.oyespace.guards.request.VisitorEntryReqJv
import com.oyespace.guards.request.VisitorExitReqJv
import com.oyespace.guards.responce.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

import com.oyespace.guards.constants.PrefKeys.BG_NOTIFICATION_ON
import com.oyespace.guards.constants.PrefKeys.LANGUAGE
import com.oyespace.guards.constants.PrefKeys.PATROLLING_ID
import com.oyespace.guards.models.VisitorLog
import com.oyespace.guards.models.Worker
import com.oyespace.guards.pojo.getDeviceList
import com.oyespace.guards.pojo.getVisitorDataByWorker
import com.oyespace.guards.utils.*
import com.oyespace.guards.utils.ConstantUtils.*
import com.oyespace.guards.utils.DateTimeUtils.getCurrentTimeLocal
import com.oyespace.guards.utils.DateTimeUtils.getCurrentTimeLocalYMD
import com.oyespace.guards.utils.Utils.showToast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.kotlin.createObject
import kotlinx.android.synthetic.main.activity_final_registration.*
import kotlinx.android.synthetic.main.activity_walkie_talkie.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.lang.NullPointerException
import kotlin.concurrent.fixedRateTimer

class Dashboard : BaseKotlinActivity(), AdapterView.OnItemSelectedListener, View.OnClickListener,ResponseHandler, Runnable,
    SGFingerPresentEvent {
    lateinit var cd: ConnectionDetector
    var timer:Timer?=null
    private val mInterval = 96000 // 5 seconds by default, can be changed later
    private var mHandlerr: Handler? = null
    var counter:Int?=0
    internal var database: DBHelper?=null

    var audioclip: String? = null
    lateinit var mp:  MediaPlayer

    internal var newAl: ArrayList<VisitorLog>? = ArrayList()
     var mHandler: Handler?=null
    lateinit var btn_in:Button
    lateinit var btn_out:Button

    private var audiofile: File? = null
    var vistorEntryListAdapter: VistorEntryListAdapter?=null
    // var spinner: Spinner?=null
    private var mFileName = ""
    private var myAudioRecorder: MediaRecorder? = null
    // var record: ImageView?=null
    var iv_settings: ImageView?=null
    lateinit var tv_nodata: TextView
    // LinearLayout lyt_settings;
    var clickable = 0
    var clickable1 = 0
    var re_vehicle: RelativeLayout?=null
    var re_staff: RelativeLayout?=null
    var re_guest: RelativeLayout?=null
    var re_delivery: RelativeLayout?=null
    var lyt_settings: RelativeLayout?=null
    var champApiInterface: ChampApiInterface?=null
    var rv_dashboard: RecyclerView?=null
    var tv_subscriptiondate: TextView?=null
    var tv_version: TextView?=null
    var tv_languagesettings: TextView?=null
    var txt_assn_name: TextView?=null
    var txt_device_name: TextView?=null
    var txt_gate_name: TextView?=null
    var subscriptionDate: String?=null
    internal var stringNumber: String? = null
    internal var stringCode: String? = null
    internal var dbh: DataBaseHelper?=null
    internal var language: String? = ""
    internal var wvvalue:String?=""
    var walk1: Button ?=null
    var walk2: ImageView?=null
    internal var telMgr: TelephonyManager?=null
    internal var existInDB1 = BooleanArray(1)
    internal var existInDB2 = BooleanArray(1)
    internal var existInDB3 = BooleanArray(1)
    internal var tempFP1: ByteArray?=null
    internal var tempFP2: ByteArray?=null
    internal var tempFP3: ByteArray?=null
    internal var curData: Cursor? = null
    internal var t1: TextToSpeech?=null
    internal var memName = ""
    internal var nnnn = 0
    internal var autoooooo = 0
    private var swipeContainer: SwipeRefreshLayout? = null
    ///Start Added by Rajesh
    private val imageUri: Uri? = null
    private var mPermissionIntent: PendingIntent? = null
    private var mVerifyImage: ByteArray? = null
    private var mVerifyTemplate: ByteArray? = null
    private var mMaxTemplateSize: IntArray? = null
    private var grayBuffer: IntArray? = null
    private var mImageWidth: Int = 0
    private var mImageHeight: Int = 0
    private var mImageDPI: Int = 0
    private var nCaptureModeN: Int = 0
    private var grayBitmap: Bitmap? = null
    private var filter: IntentFilter? = null //2014-04-11
    private var autoOn: SGAutoOnEventNotifier? = null


    //    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    //    private DatabaseReference mRootReference =firebaseDatabase.getReference();
    //    private DatabaseReference mChildReference;
    private var mLed: Boolean = false
    private var mAutoOnEnabled = true
    private var bSecuGenDeviceOpened: Boolean = false
    private var usbPermissionRequested: Boolean = false
    private var usbConnected = true
    private var sgfplib: JSGFPLib? = null
    //a separate thread.
    var fingerDetectedHandler: Handler = object : Handler() {
        // @Override
        override fun handleMessage(msg: Message) {
            //Handle the message +sgfplib.DeviceInUse()
            Log.d("Dgddfdfhhjhj : ", "ff bf entrybywalk $autoooooo   $mAutoOnEnabled $usbConnected")

            if (mAutoOnEnabled) {

                Log.d("Dgddfdfhhjhj : ", "bf bf entrybywalk $autoooooo $nnnn  $mAutoOnEnabled $usbConnected")
                if (usbConnected) {
                    CaptureFingerPrint()
                }
                Log.d("Dgddfdfhhjhj : ", "ff af entrybywalk $autoooooo $nnnn  $mAutoOnEnabled $usbConnected")
                mAutoOnEnabled = false
                val myRunnable = Runnable {
                    // your code here
                    mAutoOnEnabled = true
                }

                val myHandler = Handler()
                //final int TIME_TO_WAIT = 2000;

                myHandler.postDelayed(myRunnable, 3000)

            }
        }
    }
    private var mReceiver: BroadcastReceiver? = null
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action!!.equals(SYNC, ignoreCase = true)) {
                val message = intent.getStringExtra("message")

                if (message.equals(VISITOR_ENTRY_SYNC, ignoreCase = true)) {
                    Log.e("VISITOR_ENTRY_SYNC","VISITOR_ENTRY_SYNC");
                    var newAl: ArrayList<VisitorLog>? = ArrayList()
                    if (LocalDb.getVisitorEnteredLog() != null) {
                        newAl = LocalDb.getVisitorEnteredLog()
                        // LocalDb.saveAllVisitorLog(newAl);

                        if((newAl)!!.isEmpty()){
                            rv_dashboard!!.setVisibility(View.GONE)
                            tv_nodata!!.setVisibility(View.VISIBLE)
                        }else {
                            rv_dashboard!!.setVisibility(View.VISIBLE)
                            tv_nodata!!.setVisibility(View.GONE)
                        }
                        vistorEntryListAdapter = VistorEntryListAdapter(newAl!!, this@Dashboard)
                        rv_dashboard?.adapter = vistorEntryListAdapter
                        dismissProgressrefresh()
                        btn_in.setBackgroundColor(resources.getColor(R.color.orange))
                        btn_out.setBackgroundColor(resources.getColor(R.color.grey))
                    } else {
                        dismissProgress()
                        rv_dashboard!!.setVisibility(View.GONE)
                        tv_nodata!!.setVisibility(View.VISIBLE)
                        vistorEntryListAdapter = VistorEntryListAdapter(newAl!!, this@Dashboard)
                        rv_dashboard?.adapter = vistorEntryListAdapter
                        btn_in.setBackgroundColor(resources.getColor(R.color.orange))
                        btn_out.setBackgroundColor(resources.getColor(R.color.grey))

                    }
                }

            }
        }
    }

    private val mUsbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            //Log.d(TAG,"Enter mUsbReceiver.onReceive()");
            if (ACTION_USB_PERMISSION == action) {
                usbConnected = true
                synchronized(this) {
                    val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                        } else
                            Log.e("TAG", "mUsbReceiver.onReceive() Device is null")
                    } else
                        Log.e("TAG", "mUsbReceiver.onReceive() permission denied for device " + device!!)
                }

            }
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED == action) {
                onResume()
                sgfplib = JSGFPLib(getSystemService(Context.USB_SERVICE) as UsbManager)
                bSecuGenDeviceOpened = false
                usbPermissionRequested = false

                //        // debugMessage("Starting Activity\n");
                mLed = false
                //                mAutoOnEnabled = false;
                // autoOn = new SGAutoOnEventNotifier (sgfplib, this);
                nCaptureModeN = 0
                usbConnected = true
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED == action) {
                usbConnected = false
            }
        }
    }

    internal var rb_english: RadioButton?=null
    internal var rb_hindi: RadioButton?=null
    internal var rg_language: RadioGroup?=null
    internal var dialogs: Dialog?=null

    //public void refreshAdapter(){
    //    final Handler handler = new Handler();
    //    handler.postDelayed( new Runnable() {
    //
    //        @Override
    //        public void run() {
    //            refreshAdapter();
    //             vistorEntryListAdapter.notifyDataSetChanged();
    //            //handler.postDelayed( this, 60 * 1000 );
    //        }
    //    }, 30 * 1000 );
    //}

//    private val m_Runnable = object : Runnable {
//        override fun run() {
//            var i:Intent  = getBaseContext().getPackageManager()
//                         .getLaunchIntentForPackage( getBaseContext().getPackageName() );
//            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(i);
//            this@Dashboard.mHandler?.postDelayed(this, 20000)
//        }
//
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocale(Prefs.getString(LANGUAGE, null))
        setContentView(R.layout.activity_dash_board)


        cd = ConnectionDetector()
        cd.isConnectingToInternet(this@Dashboard)
        init()

//        wvvalue=Prefs.getString(WALKIETALKIE,null)
//        if (wvvalue != null) {
//            Log.v("wvvalue", wvvalue)
//
//        }

        language = Prefs.getString(LANGUAGE, null)
        if (language != null) {
            Log.v("language", language)
        } else {
            Prefs.putString(LANGUAGE, "en")
        }
        if (!Prefs.getBoolean(BG_NOTIFICATION_ON, false)) {
            startService(Intent(this@Dashboard, BGService::class.java))

        }
        getLatestSubscription()
        println("Shalini" + getCurrentTimeLocalYMD())
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.RECORD_AUDIO
            )

            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    // check if all permissions are granted
                    if (report.areAllPermissionsGranted()) {
                        // do you work now
                    }

                    // check for permanent denial of any permission
                    if (report.isAnyPermissionPermanentlyDenied) {
                        // permission is denied permenantly, navigate user to app settings
                        showSettingsDialog()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            })
            .onSameThread()
            .check()

        //        prefManager=new PrefManager(getApplicationContext());
        //        mChildReference = mRootReference.child("message"+prefManager.getAssociationId());
        //
        //        mChildReference.addValueEventListener(new ValueEventListener() {
        //            @Override
        //            public void onDataChange(DataSnapshot dataSnapshot) {
        //                if(dataSnapshot.getValue(String.class)==null)
        //                {
        //                    prefManager.setWelcomeMessage("Welcome");
        //                }else {
        //                    prefManager.setWelcomeMessage(dataSnapshot.getValue(String.class));
        //                    sendFCM_welcomeMsg(prefManager.getWelcomeMessage());
        //                }
        //                Log.d("Message","A"+prefManager.getWelcomeMessage());
        //            }
        //
        //            @Override
        //            public void onCancelled(DatabaseError databaseError) {
        //
        //            }
        //        });

        //        setLocale(prefManager.getLanguage());
        //Todo setContentView here for Language
        //        language=prefManager.getLanguage();

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        mReceiver = BatteryBroadcastReceiver()

        dbh = DataBaseHelper(applicationContext)
        telMgr = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        sendAnalyticsData("SDDashB_Oncreate", "Start", Date().toString() + "")

        //        imageView= (ImageView) findViewById(leftpalm.id.imageViewV);

        //        if(prefManager.getMemRoleID()==ROLE_GUARD) {
        //            FirebaseMessaging.getInstance().subscribeToTopic("AllGuards" + prefManager.getAssociationId());
        //            FirebaseMessaging.getInstance().subscribeToTopic("Guard" + prefManager.getGuardID());
        //        }
        //        if(prefManager.getMemRoleID()==ROLE_SUPERVISOR ){
        //            FirebaseMessaging.getInstance().subscribeToTopic("AllGuards" + prefManager.getAssociationId());
        //            FirebaseMessaging.getInstance().subscribeToTopic("AllSupervisor" + prefManager.getAssociationId());
        //            FirebaseMessaging.getInstance().subscribeToTopic("Guard" + prefManager.getGuardID());
        //        }

        //        startService(new Intent(DashBoard.this, SGTrackingService.class));

        //        curData=dbh.getRegularVisitorsFinger(GMT_YMD_CurrentTime());
        curData = dbh?.getRegularVisitorsFingerPrint(Prefs.getInt(ASSOCIATION_ID,0))
        if (curData != null) {
            curData!!.moveToFirst()
        }
        t1 = TextToSpeech(applicationContext, TextToSpeech.OnInitListener { status ->
            if (status != TextToSpeech.ERROR)
            // t1?.language = Locale.getDefault()
                t1?.language=Locale.getDefault()
        })

        grayBuffer = IntArray(JSGFPLib.MAX_IMAGE_WIDTH_ALL_DEVICES * JSGFPLib.MAX_IMAGE_HEIGHT_ALL_DEVICES)
        for (i in grayBuffer!!.indices)
            grayBuffer!![i] = Color.GRAY
        grayBitmap = Bitmap.createBitmap(
            JSGFPLib.MAX_IMAGE_WIDTH_ALL_DEVICES,
            JSGFPLib.MAX_IMAGE_HEIGHT_ALL_DEVICES,
            Bitmap.Config.ARGB_8888
        )
        grayBitmap!!.setPixels(
            grayBuffer,
            0,
            JSGFPLib.MAX_IMAGE_WIDTH_ALL_DEVICES,
            0,
            0,
            JSGFPLib.MAX_IMAGE_WIDTH_ALL_DEVICES,
            JSGFPLib.MAX_IMAGE_HEIGHT_ALL_DEVICES
        )

        val sintbuffer =
            IntArray(JSGFPLib.MAX_IMAGE_WIDTH_ALL_DEVICES / 2 * (JSGFPLib.MAX_IMAGE_HEIGHT_ALL_DEVICES / 2))
        for (i in sintbuffer.indices)
            sintbuffer[i] = Color.GRAY
        val sb = Bitmap.createBitmap(
            JSGFPLib.MAX_IMAGE_WIDTH_ALL_DEVICES / 2,
            JSGFPLib.MAX_IMAGE_HEIGHT_ALL_DEVICES / 2,
            Bitmap.Config.ARGB_8888
        )
        sb.setPixels(
            sintbuffer,
            0,
            JSGFPLib.MAX_IMAGE_WIDTH_ALL_DEVICES / 2,
            0,
            0,
            JSGFPLib.MAX_IMAGE_WIDTH_ALL_DEVICES / 2,
            JSGFPLib.MAX_IMAGE_HEIGHT_ALL_DEVICES / 2
        )
        mMaxTemplateSize = IntArray(1)

        //USB Permissions
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, Intent(ACTION_USB_PERMISSION), 0)
        filter = IntentFilter(ACTION_USB_PERMISSION)
        filter!!.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
        filter!!.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        filter!!.addAction(UsbManager.EXTRA_PERMISSION_GRANTED)
        filter!!.addAction(ACTION_USB_PERMISSION)

        if(Prefs.getString(PrefKeys.MODEL_NUMBER,null).equals("Nokia 2.1")){
            registerReceiver(mUsbReceiver, filter)
        }



        sgfplib = JSGFPLib(getSystemService(Context.USB_SERVICE) as UsbManager)

        bSecuGenDeviceOpened = false
        usbPermissionRequested = false

        mLed = false
        mAutoOnEnabled = true
        autoOn = SGAutoOnEventNotifier(sgfplib, this)
        nCaptureModeN = 0

        txt_assn_name = findViewById(R.id.txt_assn_name)

        if(Prefs.getString(PrefKeys.MODEL_NUMBER,null).equals("Nokia 1")) {
            txt_assn_name!!.setTextSize(5 * getResources().getDisplayMetrics().density);
        }

        txt_device_name = findViewById(R.id.txt_device_name)

        txt_assn_name?.text = "Society: " + LocalDb.getAssociation()!!.asAsnName
        txt_gate_name?.text = "Gate No: " + Prefs.getString(GATE_NO, null)
        //        txt_device_name.setText("Gate: "+Prefs.getInt(ASSOCIATION_ID,0) );
        try {
            var appVersion = ""
            val manager = baseContext.packageManager
            val info = manager.getPackageInfo(baseContext.packageName, 0)
            appVersion = info.versionName
            Log.d("tag", "app " + appVersion + " " + info.versionName)
            txt_device_name?.text = "V: $appVersion"

        } catch (ex: Exception) {
            ex.printStackTrace()
            txt_device_name?.text = " "

        }
//        if(intent.getStringExtra("STAFF")!=null) {
//            if (intent.getStringExtra("STAFF").equals("Available")) {
//                downloadBiometricData_Loop()
//            } else {
//                Log.e("Value", "Not Available")
//            }
//        }


    }

    private fun sendFCM_welcomeMsg(welcomeMessage: String) {
        //        FCMApiInterface apiService =
        //                FCMApiClient.getClient().create(FCMApiInterface.class);
        //
        //        EntryPermissionPayload payloadData = new EntryPermissionPayload("getFirebaseWelcomeMsgReply",
        //                welcomeMessage,1,
        //                entry_type[3], GlobalVariables.getGlobal_mobilenumber(),prefManager.getAssociationId());
        //        SendEntryPermissionRequest sendOTPRequest = new SendEntryPermissionRequest(payloadData, "/topics/Admin" +prefManager.getAssociationId());
        //        Call<SendFCMResponse> call = apiService.sendEntryPermission(sendOTPRequest);
        //
        //        call.enqueue(new Callback<SendFCMResponse>() {
        //            @Override
        //            public void onResponse(Call<SendFCMResponse> call, Response<SendFCMResponse> response) {
        //                Log.d("Dgddfdf", "fcm: " + response.body().getMessage_id());
        //                if (response.body().getMessage_id() != null) {
        //
        //                } else {
        //
        //                }
        //            }
        //
        //            @Override
        //            public void onFailure(Call<SendFCMResponse> call, Throwable t) {
        //                 Log error here since request failed
        //                Log.d("TAG", t.toString());
        //                sendExceptions("SGDash","FCM fail 4522"+t.toString());
        //            }
        //        });
    }

    override fun onResume() {



        timer = Timer()
        Log.i("Main", "Invoking logout timer")
        val logoutTimeTask = LogOutTimerTask()
        timer!!.schedule(logoutTimeTask, 300000)

        if (timer != null) {
            timer!!.cancel();
            Log.i("Main", "cancel timer");
            timer = null;
        }

        try {

            downloadBiometricData_Loop()
        }catch (e:NullPointerException){

        }

        try {

            //  Dashboard..notifyDatasetChanged()
        } catch (e: Throwable) {
            //error occured. Probably null
        }


        val updateHandler = Handler()

        val runnable = Runnable {
            // openAlert() // some action(s)
        }

        updateHandler.postDelayed(runnable, 1000)
        //  stopRepeatingTask()

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, IntentFilter("SYNC"))//constant
        super.onResume()


        if (LocalDb.getVisitorEnteredLog() != null) {
            dismissProgressrefresh()
            newAl = LocalDb.getVisitorEnteredLog()
            // LocalDb.saveAllVisitorLog(newAl);

            if((newAl)!!.isEmpty()){
                rv_dashboard!!.setVisibility(View.GONE)
                tv_nodata!!.setVisibility(View.VISIBLE)
                dismissProgressrefresh()

            }else {
                rv_dashboard!!.setVisibility(View.VISIBLE)
                tv_nodata!!.setVisibility(View.GONE)
                dismissProgressrefresh()
            }
            if (vistorEntryListAdapter != null) {// it works second time and later
                vistorEntryListAdapter!!.notifyDataSetChanged();
                btn_in.setBackgroundColor(resources.getColor(R.color.orange))
                btn_out.setBackgroundColor(resources.getColor(R.color.grey))
            }
            else {
                dismissProgressrefresh()
                vistorEntryListAdapter = VistorEntryListAdapter(newAl!!, this@Dashboard)
                rv_dashboard?.adapter = vistorEntryListAdapter
                btn_in.setBackgroundColor(resources.getColor(R.color.orange))
                btn_out.setBackgroundColor(resources.getColor(R.color.grey))
            }
        }
        val intentAction1 = Intent(applicationContext, BackgroundSyncReceiver::class.java)
        intentAction1.putExtra(BSR_Action, VISITOR_ENTRY_SYNC)
        sendBroadcast(intentAction1)
        //Toast.makeText(DashBoard.this,"NO data",Toast.LENGTH_LONG).show();
        //}

        if (isTimeAutomatic(application)) {

        } else {
            val alertDialogBuilder = android.support.v7.app.AlertDialog.Builder(this@Dashboard)
            alertDialogBuilder.setTitle("Time settings")

            // Setting Dialog Message
            alertDialogBuilder.setMessage("Time is not automatic. Please select automatic time")

            // On pressing Settings button
            alertDialogBuilder.setPositiveButton(
                "Settings"
            ) { dialog, which ->
                val intent = Intent(Settings.ACTION_DATE_SETTINGS)
                startActivity(intent)
            }
            alertDialogBuilder.setCancelable(false)

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()

        }


        if (isTimeZoneAutomatic(application)) {

        } else {
            val alertDialogBuilder = android.support.v7.app.AlertDialog.Builder(this@Dashboard)
            alertDialogBuilder.setTitle("Time settings")

            // Setting Dialog Message
            alertDialogBuilder.setMessage("Timezone is not automatic. Please select automatic timezone")

            // On pressing Settings button
            alertDialogBuilder.setPositiveButton(
                "Settings"
            ) { dialog, which ->
                val intent = Intent(Settings.ACTION_DATE_SETTINGS)
                startActivity(intent)
            }
            alertDialogBuilder.setCancelable(false)

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()

        }

        //        Intent intentAction3 = new Intent(getApplicationContext(), DownloadResDataReceiver.class);
        //        intentAction3.putExtra(action, UPLOAD_IMAGES);
        //        sendBroadcast(intentAction3);

        //        Log.d("Count_image","ll"+idb.pending_getImages_toUpload());
        //        sendAnalyticsData("SDDashB_OnResume ", "Image Count "+dbh.getAssociationName(prefManager.getAssociationId()), "Count: "+idb.pending_getImages_toUpload());

        bSecuGenDeviceOpened = false
        usbPermissionRequested = false

        mLed = false
        mAutoOnEnabled = true
        autoOn = SGAutoOnEventNotifier(sgfplib, this)
        nCaptureModeN = 0

        //        prefManager.setOnForeground(true);

        //        Log.d("AppVersionValidity"," onresume "+prefManager.getAppVersionValidity()+" ");

        //        Intent intentAction = new Intent(getApplicationContext(), DownloadResDataReceiver.class);
        //        intentAction.putExtra(action, DAILY_HELP);
        //         sendBroadcast(intentAction);
        //        Log.d("stfdhi ",prefManager.getGuardStartTime().equalsIgnoreCase("0001-01-01T00:00:00")+" ");

        try {
            var error = sgfplib!!.Init(SGFDxDeviceName.SG_DEV_AUTO)
            Log.d("onResume", "onResume( )$nnnn")
            if (error != SGFDxErrorCode.SGFDX_ERROR_NONE) {
                val dlgAlert = android.app.AlertDialog.Builder(this)
                if (error == SGFDxErrorCode.SGFDX_ERROR_DEVICE_NOT_FOUND)
                //                Toast.makeText(this, "connect secugen", Toast.LENGTH_LONG).show();
                    dlgAlert.setMessage("The attached fingerprint device is not supported on Android")
            } else {
                val usbDevice = sgfplib!!.GetUsbDevice()
                if (usbDevice == null) {
                    val dlgAlert = android.app.AlertDialog.Builder(this)
                    dlgAlert.setMessage("SecuGen fingerprint sensor not found!")
                    dlgAlert.setTitle("SecuGen Fingerprint SDK")
                    dlgAlert.setPositiveButton("OK",
                        DialogInterface.OnClickListener { dialog, whichButton ->
                            finish()
                            return@OnClickListener
                        }
                    )
                    dlgAlert.setCancelable(false)
                    dlgAlert.create().show()
                } else {
                    var hasPermission = sgfplib!!.GetUsbManager().hasPermission(usbDevice)
                    Log.d("onResume", "$nnnn o $hasPermission")
                    if (!hasPermission) {
                        if (!usbPermissionRequested) {
                            //Log.d(TAG, "Call GetUsbManager().requestPermission()");
                            usbPermissionRequested = true
                            sgfplib!!.GetUsbManager().requestPermission(usbDevice, mPermissionIntent)
                        } else {
                            //wait up to 20 seconds for the system to grant USB permission
                            hasPermission = sgfplib!!.GetUsbManager().hasPermission(usbDevice)
                            var i = 0
                            while (hasPermission == false && i <= 40) {
                                ++i
                                hasPermission = sgfplib!!.GetUsbManager().hasPermission(usbDevice)
                                try {
                                    Thread.sleep(50)
                                } catch (e: InterruptedException) {
                                    e.printStackTrace()
                                }

                                //Log.d(TAG, "Waited " + i*50 + " milliseconds for USB permission");
                            }
                        }
                    }
                    if (hasPermission) {
                        error = sgfplib!!.OpenDevice(0)
                        if (error == SGFDxErrorCode.SGFDX_ERROR_NONE) {
                            bSecuGenDeviceOpened = true
                            val deviceInfo = SecuGen.FDxSDKPro.SGDeviceInfoParam()
                            error = sgfplib!!.GetDeviceInfo(deviceInfo)
                            mImageWidth = deviceInfo.imageWidth
                            mImageHeight = deviceInfo.imageHeight
                            mImageDPI = deviceInfo.imageDPI

                            sgfplib!!.SetTemplateFormat(SGFDxTemplateFormat.TEMPLATE_FORMAT_SG400)
                            sgfplib!!.GetMaxTemplateSize(mMaxTemplateSize)

                            mVerifyTemplate = ByteArray(mMaxTemplateSize!![0])

                            sgfplib!!.WriteData(SGFDxConstant.WRITEDATA_COMMAND_ENABLE_SMART_CAPTURE, 1.toByte())
                            autoOn!!.start()
                        } else {

                        }
                    }

                }
            }
        } catch (ex: Exception) {
            Toast.makeText(applicationContext, "Connect Secugen Correctly", Toast.LENGTH_SHORT).show()
        }


//        handler =  Handler();
//
//
//        r = Runnable() {
//            run() {
//                Toast.makeText(
//                    this@Dashboard, "user Is Idle from last 5 minutes",
//                    Toast.LENGTH_SHORT
//                ).show();
//            }
//        }

//        r =  Runnable() {
//
//            @Override
//           fun run() {
//                // TODO Auto-generated method stub
//                Toast.makeText(this@Dashboard, "user Is Idle from last 5 minutes",
//                    Toast.LENGTH_SHORT).show();
//            }
//        };
//        startHandler();


    }

    override fun onBackPressed() {
        super.onBackPressed()
        //  dbh?.residentsLogVehicles
        //            prefManager.setOnForeground(false);
        //            startService(new Intent(DashBoard.this, ByteDownloaderService.class));
        //            Intent intentAction1 = new Intent(getApplicationContext(), DownloadResDataReceiver.class);
        //            intentAction1.putExtra(action, NONREGULAR);
        //            sendBroadcast(intentAction1);
        //            Intent intentAction3 = new Intent(getApplicationContext(), DownloadResDataReceiver.class);
        //            intentAction3.putExtra(action, UPLOAD_IMAGES);
        //            sendBroadcast(intentAction3);
        //            Intent intentAction22 = new Intent(getApplicationContext(), DownloadResDataReceiver.class);
        //            intentAction22.putExtra(action, DAILY_HELP);
        //            sendBroadcast(intentAction22);
        //            Intent intentAction23 = new Intent(getApplicationContext(), DownloadResDataReceiver.class);
        //            intentAction23.putExtra(action, PATROLLING);
        //            sendBroadcast(intentAction23);
        //            Intent intentAction24 = new Intent(getApplicationContext(), DownloadResDataReceiver.class);
        //            intentAction24.putExtra(action, "SYNC_ASSOCIATIONS");
        //            sendBroadcast(intentAction24);
        //            Cursor cur=dbh.getMemberByAssnID_date(prefManager.getAssociationId(),IConstant.dateFormat_YMD.format(new Date()));
        //            Cursor cur1=dbh.getMemberByAssnID(prefManager.getAssociationId());

        //            if(cur.getCount()>0 || cur1.getCount()<1) {
        //                Intent intentAction2 = new Intent(getApplicationContext(), DownloadResDataReceiver.class);
        //                intentAction2.putExtra(action, OYE_MEMBER);
        //                sendBroadcast(intentAction2);
        //            }

        //            startService(new Intent(DashBoard.this, ImageUploadService.class));

    }

    override fun onFailure(e: Exception, urlId: Int) {

        showToast(this, e.message + " id " + urlId)
    }

    override fun onSuccess(responce: String, data: Any, urlId: Int, position: Int) {

        when (urlId){
            URLData.URL_VISITOR_LOG.urlId->{

                val loginDetailsResponce = data as VisitorLogCreateResp
                if (loginDetailsResponce != null) {

                    Log.d(
                        "str3",
                        "str3: " + urlId + " id " + position + " " + " " + " " + loginDetailsResponce.success.toString()
                    )
                    if (loginDetailsResponce.success.equals("true", ignoreCase = true)) {
                        //   showToast(this, " Saved");
                        visitorEntryLog(loginDetailsResponce.data.visitorLog.vlVisLgID)


                    } else {
                        showToast(this, "Visitor Data not saved ")

                    }

                } else {
                    showToast(this, "Something went wrong . please try again ")
                }
            }
            URLData.URL_VISITOR_MAKE_ENTRY.urlId->{
                val loginDetailsResponce = data as VisitorLogCreateResp
                if (loginDetailsResponce != null) {
                    Log.d(
                        "str3",
                        "str3: " + urlId + " id " + position + " " + " " + " " + loginDetailsResponce.success.toString()
                    )
                    if (loginDetailsResponce.success.equals("true", ignoreCase = true)) {
                        // showToast(this, " Welcome")

                        val intentAction1 = Intent(applicationContext, BackgroundSyncReceiver::class.java)
                        intentAction1.putExtra(BSR_Action, SENDFCM_toSYNC_VISITORENTRY)
                        sendBroadcast(intentAction1)

                    } else {
                        showToast(this, "Visitor Details not saved ")
                    }

                } else {


                    showToast(this, "Something went wrong . please try again ")
                }
            }
            URLData.URL_VISITOR_MAKE_EXIT.urlId->{
                val loginDetailsResponce = data as VisitorLogCreateResp
                if (loginDetailsResponce != null) {

                    Log.d(
                        "str3",
                        "str3: " + urlId + " id " + position + " " + " " + " " + loginDetailsResponce.success.toString()
                    )
                    if (loginDetailsResponce.success.equals("true", ignoreCase = true)) {
                        //showToast(this, " Thank You")
                        // rv_dashboard.setVisibility(View.VISIBLE);
                        // tv_nodata.setVisibility(View.GONE);
                        val intentAction1 = Intent(applicationContext, BackgroundSyncReceiver::class.java)
                        intentAction1.putExtra(BSR_Action, SENDFCM_toSYNC_VISITORENTRY)
                        sendBroadcast(intentAction1)

                    } else {
                        showToast(this, "Exit Details not saved ")
                    }

                } else {
                    showToast(this, "Something went wrong . please try again ")


                }

            }

        }



//        if (urlId == URLData.URL_VISITOR_LOG.urlId) {
//            val loginDetailsResponce = data as VisitorLogCreateResp
//            if (loginDetailsResponce != null) {
//                Log.d(
//                    "str3",
//                    "str3: " + urlId + " id " + position + " " + " " + " " + loginDetailsResponce.success.toString()
//                )
//                if (loginDetailsResponce.success.equals("true", ignoreCase = true)) {
//                    //                    showToast(this, " Saved");
//                    visitorEntryLog(loginDetailsResponce.data.visitorLog.vlVisLgID)
//
//                } else {
//                    showToast(this, "Visitor Data not saved ")
//                }
//
//            } else {
//                showToast(this, "Something went wrong . please try again ")
//            }
//
//        }
//        else
//            if (urlId == URLData.URL_VISITOR_MAKE_ENTRY.urlId) {
//
//            val loginDetailsResponce = data as VisitorLogCreateResp
//            if (loginDetailsResponce != null) {
//                Log.d(
//                    "str3",
//                    "str3: " + urlId + " id " + position + " " + " " + " " + loginDetailsResponce.success.toString()
//                )
//                if (loginDetailsResponce.success.equals("true", ignoreCase = true)) {
//                    showToast(this, " Welcome")
//
//                    val intentAction1 = Intent(applicationContext, BackgroundSyncReceiver::class.java)
//                    intentAction1.putExtra(BSR_Action, SENDFCM_toSYNC_VISITORENTRY)
//                    sendBroadcast(intentAction1)
//
//                } else {
//                    showToast(this, "Visitor Details not saved ")
//                }
//
//            } else {
//
//
//                showToast(this, "Something went wrong . please try again ")
//            }
//        }
//        else if (urlId == URLData.URL_VISITOR_MAKE_EXIT.urlId) {
//
//            val loginDetailsResponce = data as VisitorLogCreateResp
//            if (loginDetailsResponce != null) {
//
//                Log.d(
//                    "str3",
//                    "str3: " + urlId + " id " + position + " " + " " + " " + loginDetailsResponce.success.toString()
//                )
//                if (loginDetailsResponce.success.equals("true", ignoreCase = true)) {
//                    showToast(this, " Thank You")
//                    // rv_dashboard.setVisibility(View.VISIBLE);
//                    // tv_nodata.setVisibility(View.GONE);
//                    val intentAction1 = Intent(applicationContext, BackgroundSyncReceiver::class.java)
//                    intentAction1.putExtra(BSR_Action, SENDFCM_toSYNC_VISITORENTRY)
//                    sendBroadcast(intentAction1)
//
//                } else {
//                    showToast(this, "Exit Details not saved ")
//                }
//
//            } else {
//                showToast(this, "Something went wrong . please try again ")
//
//
//            }
//
//        }

        //  showToast(this, urlId+" id "+position+" "+memId+" "+MemberType+" ");

        //  finish();
    }

    private fun visitorEntryLog(vlVisLgID: Int) {

        val restClient = RestClient.getInstance()

        val loginReq = VisitorEntryReqJv()

        loginReq.VLVisLgID = vlVisLgID
        loginReq.VLEntryT = getCurrentTimeLocal()
        loginReq.VLEntyWID = LocalDb.getStaffs(realm)!![0].wkWorkID.toInt()

        Log.d("CreateVisitorLogResp", "StaffEntry $loginReq")
        //  showToast(this, "StaffEntry $loginReq");

        restClient.addHeader(OYE247KEY, OYE247TOKEN)
        restClient.post<Any>(this, loginReq, VisitorLogCreateResp::class.java, this, URLData.URL_VISITOR_MAKE_ENTRY)

    }

    private fun visitorLog(
        unitId: Int, personName: String, mobileNumb: String, desgn: String,
        workerType: String, staffID: Int, unitName: String,wkEntryImg:String
    ) {

        val restClient = RestClient.getInstance()

        val loginReq = RequestDTO()

//        var memID = 64
//        if (!BASE_URL.contains("dev")) {
//            memID = 410
//        }


        var memID=410;
        if(BASE_URL.contains("dev",true)){
            memID=64;
        }
        else if(BASE_URL.contains("uat",true)){
            memID=64;
        }
        loginReq.aSAssnID = LocalDb.getAssociation()!!.asAssnID
        loginReq.mEMemID = memID
        loginReq.rERgVisID = staffID
        loginReq.uNUnitID = unitId
        loginReq.vLFName = personName
        loginReq.vLMobile = mobileNumb
        loginReq.vLComName = desgn
        loginReq.vLVisType = workerType
        loginReq.uNUniName = unitName
        loginReq.vLVisCnt = 1
        loginReq.VLEntryImg=wkEntryImg


        realm.executeTransaction {
            val vlog = it.createObject<VisitorLog>();
            vlog.asAssnID  = LocalDb.getAssociation()!!.asAssnID
            vlog.mEMemID = memID
            vlog.reRgVisID = staffID
            vlog.uNUnitID = unitId
            vlog.vlfName = personName
            vlog.vlMobile = mobileNumb
            vlog.vlComName = desgn
            vlog.vlVisType = workerType
            vlog.unUniName = unitName
            vlog.vlVisCnt = 1
            vlog.vlEntryT = getCurrentTimeLocal()
        }
        //val workerCount = realm.where(VisitorLog::class.java).findAll()

//        dbh!!.insertStaffWorker(LocalDb.getAssociation()!!.asAssnID,memID,staffID,unitId,personName,mobileNumb,desgn,workerType,unitName,1,
//            getCurrentTimeLocal(),"")
//getCurrentTimeLocal()
        Log.d("CreateVisitorLogResp", "StaffEntry $loginReq")
        //  showToast(this, loginReq.ASAssnID + " fmid " + loginReq.FMID+" "+loginReq.FPImg1);

        restClient.addHeader(OYE247KEY, OYE247TOKEN)
        restClient.post<Any>(this, loginReq, VisitorLogCreateResp::class.java, this, URLData.URL_VISITOR_LOG)
          t1?.speak("Welcome $personName", TextToSpeech.QUEUE_FLUSH, null)
        Prefs.putString(BIOMETRICPERSONNAME,personName)
        if(database!!.insertContact(personName)){
//            Toast.makeText(getApplicationContext(), "done",
//                Toast.LENGTH_SHORT).show();
        } else{
//            Toast.makeText(getApplicationContext(), "not done",
//                Toast.LENGTH_SHORT).show();
        }


//            Toast.makeText(this@Dashboard,counter.toString(),Toast.LENGTH_LONG).show()
//            val dialogBuilder = AlertDialog.Builder(this@Dashboard)
//
//            // set message of alert dialog
//            dialogBuilder.setMessage("")
//                // if the dialog is cancelable
//                .setCancelable(false)
//                // positive button text and action
//                .setPositiveButton("Proceed", DialogInterface.OnClickListener {
//                        dialog, id ->
//                    counter=0
//
//
//                })
//                // negative button text and action
//                .setNegativeButton("Cancel", DialogInterface.OnClickListener {
//                        dialog, id -> dialog.cancel()
//                })
//
//            // create dialog box
//            val alert = dialogBuilder.create()
//            // set title for alert dialog box
//            // show alert dialog
//            alert.show()
//
//        }else{
//            Toast.makeText(this@Dashboard,counter.toString(),Toast.LENGTH_LONG).show()
//
//        }

//        dbh!!.insertStaffWorker(LocalDb.getAssociation()!!.asAssnID,memID,staffID,unitId,personName,mobileNumb,desgn,workerType,unitName,1,
//            getCurrentTimeLocal(),"")


        var id: Long  =  dbh!!.insertVisitorData(unitName,LocalDb.getAssociation()!!.asAssnID.toString(),personName,memID,staffID,
            unitId,mobileNumb,"Staff",workerType,1,getCurrentTimeLocal(),"" )


        if(id<=0)
        {
            //  Toast.makeText(this@Dashboard,"Insertion Unsuccessful",Toast.LENGTH_LONG).show()
        } else
        {
            // Toast.makeText(this@Dashboard,"Insertion Successful",Toast.LENGTH_LONG).show()

        }



//        d.putExtra(BSR_Action, VisitorEntryFCM)
//        d.putExtra("msg", intent.getStringExtra(PERSONNAME)+" from "+intent.getStringExtra(COMPANY_NAME)+" is coming to your home")
//        d.putExtra("mobNum", intent.getStringExtra(MOBILENUMBER))
//        d.putExtra("name", intent.getStringExtra(PERSONNAME))
//        d.putExtra("nr_id", AppUtils.intToString(globalApiObject.data.visitorLog.vlVisLgID))
//        d.putExtra("unitname", intent.getStringExtra(UNITNAME))
//        d.putExtra("memType", "Owner")
//        d.putExtra(UNITID,intent.getStringExtra(UNITID))
//        d.putExtra(COMPANY_NAME,intent.getStringExtra(COMPANY_NAME))



        val d  =  Intent(this@Dashboard,BackgroundSyncReceiver::class.java)
        d.putExtra(BSR_Action, VisitorEntryFCM)
        d.putExtra("msg", "$personName $desgn "+" is coming to your home")
        d.putExtra("mobNum", mobileNumb)
        d.putExtra("name", personName)
        d.putExtra("nr_id", "0")
        d.putExtra("unitname",unitName)
        d.putExtra("memType", "Owner")
        d.putExtra(UNITID,unitId.toString())
        d.putExtra(COMPANY_NAME,"Staff")
        d.putExtra(UNIT_ACCOUNT_ID,"0")
        d.putExtra("VLVisLgID",0)
        sendBroadcast(d);




//        val ddc = Intent(this@Dashboard, BackgroundSyncReceiver::class.java)
//        ddc.putExtra(BSR_Action, SENDFCM_toSYNC_VISITORENTRY)
////        ddc.putExtra("msg", "$personName $desgn is coming to your home")
////        ddc.putExtra("mobNum", mobileNumb)
////        ddc.putExtra("name", personName)
////        ddc.putExtra("nr_id", "0")
////        ddc.putExtra("unitname", unitName)
////        ddc.putExtra("memType", "Owner")
//        sendBroadcast(ddc)


    }

    fun VisitorExit(vlVisLgID: Int,vlfName:String) {
        val restClient = RestClient.getInstance()

        val loginReq = VisitorExitReqJv()

        loginReq.VLVisLgID = vlVisLgID
        loginReq.VLExitT = getCurrentTimeLocal()
        loginReq.VLExitWID = LocalDb.getStaffs(realm)!![0].wkWorkID.toInt()

        Log.d("CreateVisitorLogResp", "StaffEntry $loginReq")
        //  showToast(this, loginReq.ASAssnID + " fmid " + loginReq.FMID+" "+loginReq.FPImg1);

        restClient.addHeader(OYE247KEY, OYE247TOKEN)
        restClient.post<Any>(this, loginReq, VisitorLogCreateResp::class.java, this, URLData.URL_VISITOR_MAKE_EXIT)
        // t1?.speak("Thank You " + vlfName, TextToSpeech.QUEUE_FLUSH, null)



    }

    public override fun onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
        super.onPause()


    }

    public override fun onDestroy() {
        // clearApplicationData()


        if (bSecuGenDeviceOpened) {
            autoOn!!.stop()
            sgfplib!!.CloseDevice()
            bSecuGenDeviceOpened = false
        }
        //        sgfplib.CloseDevice();



        mVerifyImage = null
        mVerifyTemplate = null
        //        sgfplib.Close();
        if(Prefs.getString(PrefKeys.MODEL_NUMBER,null).equals("Nokia 2.1")) {
            unregisterReceiver(mUsbReceiver)
        }
        val ddc2 = Intent(this@Dashboard, BackgroundSyncReceiver::class.java)
        Log.d("SYNC_UNIT_LIST", "af ")
        ddc2.putExtra(BSR_Action, SYNC_UNIT_LIST)
        sendBroadcast(ddc2)
        val ddc1 = Intent(this@Dashboard, BackgroundSyncReceiver::class.java)
        Log.d("SYNC_STAFF_LIST", "af ")
        ddc1.putExtra(BSR_Action, SYNC_STAFF_LIST)
        sendBroadcast(ddc1)

        super.onDestroy()

//        try {
//            trimCache(this)
//        } catch (e: Exception) {
//            // TODO Auto-generated catch block
//            e.printStackTrace()
//        }
    }

    fun setLocale(lang: String?) {
        var lang = lang
        if (lang == null) {
            lang = "en"
        } else {
        }
        val myLocale = Locale(lang)
        val res = resources
        val dm = res.displayMetrics
        val conf = res.configuration
        conf.locale = myLocale
        res.updateConfiguration(conf, dm)
    }

    private fun restartInLocale(lang: String?) {
        var lang = lang
        if (lang == null) {
            lang = "en"
        } else {
        }
        val myLocale = Locale(lang)
        val config = Configuration()
        config.locale = myLocale
        val resources = resources
        resources.updateConfiguration(config, resources.displayMetrics)
        recreate()
    }

    override fun SGFingerPresentCallback() {
        autoooooo++
        if (usbConnected) {
            fingerDetectedHandler.sendMessage(Message())
        }
    }

    override fun run() {

    }

    fun CaptureFingerPrint() {

        Log.d("abcdef", "7172")
        if (bSecuGenDeviceOpened == true) {
            //DEBUG Log.d(TAG, "Clicked MATCH");
            if (mVerifyImage != null)
                mVerifyImage = null
            mVerifyImage = ByteArray(mImageWidth * mImageHeight)

            try {
                var result = sgfplib!!.GetImage(mVerifyImage)
                Log.d("match  1", result.toString() + " " + mVerifyImage!!.size)

                result = sgfplib!!.SetTemplateFormat(SecuGen.FDxSDKPro.SGFDxTemplateFormat.TEMPLATE_FORMAT_SG400)
                Log.d("match  2", result.toString() + " " + mVerifyImage!!.size)

                var fpInfo: SGFingerInfo? = SGFingerInfo()
                for (i in mVerifyTemplate!!.indices)
                    mVerifyTemplate!![i] = 0

                result = sgfplib!!.CreateTemplate(fpInfo, mVerifyImage, mVerifyTemplate)
                Log.d("match  3", result.toString() + " " + mVerifyTemplate!!.size)

                var matched: BooleanArray? = BooleanArray(1)

                Log.d("match  5", result.toString() + " " + mVerifyTemplate!!.size + " " + matched!![0])
                Log.d(
                    "Dgddfdfhhjhj : ",
                    "CaptureFingerPrint entrybywalk " + mVerifyTemplate!!.size + " " + autoooooo + " " + nnnn + " " + " " + mAutoOnEnabled + " " + usbConnected
                )
//                if (matched[0]) {
//                    //                                Toast.makeText(getApplicationContext(), "MATCHED!!\n ", Toast.LENGTH_SHORT).show();
//                } else {
//                    //                                Toast.makeText(getApplicationContext(), "NOT MATCHED!! ", Toast.LENGTH_SHORT).show();
//                }

                //                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                //                am.setStreamVolume(AudioManager.STREAM_SYSTEM, am.getStreamMaxVolume(AudioManager.STREAM_SYSTEM), 0);

                val tempNumber = checkFingerPrint(mVerifyTemplate!!)
                Log.d("Biometric 953", " ")
                if (tempNumber == 0) {
                    t1?.speak("No Match Found", TextToSpeech.QUEUE_FLUSH, null)


                } else if (tempNumber > 0 && tempNumber < 4) {

                    val enteredStaff = ArrayList<VisitorLog>()
                    Log.d("Biometric 973", " " + (LocalDb.getVisitorEnteredLog() != null))
                    //looping through existing elements
                    if (LocalDb.getVisitorEnteredLog() != null) {
                        for (s in LocalDb.getVisitorEnteredLog()!!) {
                            //if the existing elements contains the search input
                            if (s.reRgVisID == Integer.parseInt(memName)) {
                                //adding the element to filtered list
                                enteredStaff.add(s)
                            } else {

                            }
                        }
                    }
                    Log.d("Biometric 983", " ")

                    if (enteredStaff.size > 0) {

                        t1?.speak("Thank You " + enteredStaff[0].vlfName, TextToSpeech.QUEUE_FLUSH, null)

                        Log.d("check 79 ", "bio")
                        VisitorExit(enteredStaff[0].vlVisLgID,enteredStaff[0].vlfName)

                    } else {

                        //                                t1.speak("Welcome " + dbh.getMemName(Integer.parseInt(memName)), TextToSpeech.QUEUE_FLUSH, null);

                        //LocalDb.getStaffs(realm)
                        val filterdNames = ArrayList<Worker>()

                        //looping through existing elements
                        for (s in LocalDb.getStaffs(realm)!!) {
                            //if the existing elements contains the search input
                            if (s.wkWorkID.toInt() == Integer.parseInt(memName)) {
                                //adding the element to filtered list
                                filterdNames.add(s)
                            }
                        }
                        if (filterdNames.size > 0) {


                        //    val (_, _, unUnitID, unUniName, _, wkDesgn, _, _, wkMobile, wkWorkID, wkWrkType, _, _, wkfName, _, _, wklName) = filterdNames[0]
                            var worker:Worker = filterdNames[0];

                        getVisitorByWorkerId(Prefs.getInt(ASSOCIATION_ID,0),worker.wkWorkID.toInt(),worker.unUnitID.toInt(),"${worker.wkfName} ${worker.wklName}",worker.wkMobile, worker.wkDesgn, worker.wkWrkType,worker.wkWorkID.toInt(), worker.unUniName, worker.wkEntryImg)
                           // t1?.speak("Welcome $wkfName$wklName", TextToSpeech.QUEUE_FLUSH, null)
                          // showToast(this@Dashboard,"came")
//                            visitorLog(
//                                unUnitID, "$wkfName $wklName",
//                                wkMobile, wkDesgn, wkWrkType,
//                                wkWorkID, unUniName
//                            )
//
//                         //   showToast(this@Dashboard,wkWorkID.toString())
//                            Log.d("check 78 ", "bio")
                          // t1?.speak("Welcome $wkfName", TextToSpeech.QUEUE_FLUSH, null)
                        } else {
                            Toast.makeText(applicationContext, "No Data", Toast.LENGTH_SHORT).show()

                        }
                    }

                    //                                Toast.makeText(getApplicationContext(), "" + tempNumber, Toast.LENGTH_SHORT).show();
                } else {
                    //                                Toast.makeText(getApplicationContext(), "" + tempNumber, Toast.LENGTH_LONG).show();
                }
                Log.d("Biometric 1030", " ")

                mVerifyImage = null
                fpInfo = null
                matched = null
                this.sgfplib!!.SetBrightness(100)
            } catch (ex: Exception) {
                sendExceptions("SGDBA_CptFingPt", ex.toString())
                Log.d("Biometric 1035", " $ex")
                // Toast.makeText(applicationContext, "Biometric not attached correctly ", Toast.LENGTH_LONG).show()
            }

        } else {
            // Toast.makeText(applicationContext, "Biometric Device Not Attached", Toast.LENGTH_LONG).show()
        }

        var buffer: ByteArray? = ByteArray(mImageWidth * mImageHeight)
        //        long result = sgfplib.GetImageEx(buffer, 100,50);

        buffer = null
    }

    fun checkFingerPrint(template: ByteArray): Int {
        nnnn++
        val exists = false
        var number = 0
        var itera = 0
        //        curData=dbh.getRegularVisitorsFinger(GMT_YMD_CurrentTime());
        curData = dbh?.getRegularVisitorsFingerPrint(Prefs.getInt(ASSOCIATION_ID,0))
        if (curData != null) {
            curData!!.moveToFirst()
        }
        if (curData != null) {
            if (curData!!.moveToFirst()) {
                existInDB1 = BooleanArray(1)
                existInDB2 = BooleanArray(1)
                existInDB3 = BooleanArray(1)
                memName = ""
                do {
                    itera++
                    tempFP1 = ByteArray(mImageWidth * mImageHeight)
                    for (j in tempFP1!!.indices)
                        tempFP1!![j] = 0



                    if (curData!!.getBlob(3) != null) {


                        Log.d(
                            "Dgddfdf hhjhj : ",
                            "ff in entrybywalk " + autoooooo + " " + nnnn + " " + curData!!.getString(1) + " " + itera + " " + memName
                        )
                        tempFP1 = curData!!.getBlob(3)
                        val res: Long
                        res = sgfplib!!.MatchTemplate(template, tempFP1, SGFDxSecurityLevel.SL_HIGH, existInDB1)
                        if (existInDB1[0]) {
                            number++
                            //  Log.d("Dgddfdf string hhjhj", "ff  entrybywalk "+curData.getString(3).toString()+" "+" ");
                            //                            Toast.makeText(getApplicationContext(), " MATCHED!! " + curData.getString(1) + " " + curData.getString(2), Toast.LENGTH_SHORT).show();
                            memName = curData!!.getString(1)
                        }
                        Log.d("data", curData!!.getString(0) + " ")
                    }
                    else if (curData!!.getBlob(4) != null) {

                        tempFP2 = ByteArray(mImageWidth * mImageHeight)
                        for (j in tempFP2!!.indices)
                            tempFP2!![j] = 0

                        tempFP2 = curData!!.getBlob(4)
                        val res2: Long
                        res2 = sgfplib!!.MatchTemplate(template, tempFP2, SGFDxSecurityLevel.SL_HIGH, existInDB2)
                        if (existInDB2[0]) {
                            number++

                            //                            Toast.makeText(getApplicationContext(), " MATCHED!! " + curData.getString(1) + " " + curData.getString(2), Toast.LENGTH_SHORT).show();
                            memName = curData!!.getString(1)
                        }
                        Log.d("data", curData!!.getString(0) + " ")
                    } else if (curData!!.getBlob(5) != null) {

                        tempFP3 = ByteArray(mImageWidth * mImageHeight)
                        for (j in tempFP3!!.indices)
                            tempFP3!![j] = 0
                        tempFP3 = curData!!.getBlob(5)
                        val res3: Long
                        res3 = sgfplib!!.MatchTemplate(template, tempFP3, SGFDxSecurityLevel.SL_HIGH, existInDB3)
                        if (existInDB3[0]) {
                            number++

                            //                            Toast.makeText(getApplicationContext(), " MATCHED!! " + curData.getString(1) + " " + curData.getString(2), Toast.LENGTH_SHORT).show();
                            memName = curData!!.getString(1)
                        }
                    }
                    if (number > 0) {
                        return number
                    }

                } while (curData!!.moveToNext())
            }
        }
        return number
    }

    override fun onStart() {
        registerReceiver(mReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

        super.onStart()

    }

    override fun onStop() {
        unregisterReceiver(mReceiver)

        super.onStop()
    }

    fun sendExceptions(id: String, execeptionString: String) {

    }

    fun sendAnalyticsData(_Activity: String, id: String, execeptionString: String) {


    }

    override fun onClick(v: View) {
        Log.d("clcik", "view onClick" + v.id)

        onTabClicked(v)
        when (v.id) {
            R.id.iv_settings -> if (clickable == 0) {
                lyt_settings?.visibility = View.VISIBLE
                iv_settings?.setBackgroundResource(R.drawable.cancel)
                clickable = 1
            } else if (clickable == 1) {
                lyt_settings?.visibility = View.GONE
                iv_settings?.setBackgroundResource(R.drawable.settings)
                clickable = 0
            }
            R.id.tv_patrolling -> {
                //PatrollingActivitynew
                val i_vehicle = Intent(this@Dashboard, PatrollingActivitynew::class.java)
                startActivity(i_vehicle)
            }
            R.id.tv_emergency -> {
                val i_emer = Intent(this@Dashboard, TicketingDetailsActivity::class.java)
                startActivity(i_emer)
            }
        }//            case R.id.tv_filter:
        //                if (clickable1 == 0) {
        //                    lyt_settings.setVisibility(View.VISIBLE);
        //                    iv_settings.setBackgroundResource(R.drawable.cancel);
        //                    clickable1 = 1;
        //                } else if (clickable1 == 1) {
        //                    lyt_settings.setVisibility(View.GONE);
        //                    iv_settings.setBackgroundResource(R.drawable.settings);
        //                    clickable1 = 0;
        //                }
        //                break;

    }

    internal fun onTabClicked(v: View) {
        Log.d("clcik", "view " + v.id)
        when (v.id) {

            R.id.re_vehicle -> {
//                re_vehicle!!.setEnabled(false)
//                re_vehicle!!.setClickable(false)


               val i_vehicle = Intent(this@Dashboard, CaptureImageOcr::class.java)
               startActivity(i_vehicle)
                //finish()

            }

            R.id.re_delivery -> {
//                re_delivery!!.setEnabled(false)
//                re_delivery!!.setClickable(false)
                val i_delivery = Intent(this@Dashboard, ServiceProviderListActivity::class.java)
                startActivity(i_delivery)

            }

            R.id.re_guest -> {
//                re_guest!!.setEnabled(false)
//                re_guest!!.setClickable(false)
                val i_guest = Intent(this@Dashboard, GuestCustomViewFinderScannerActivity::class.java)
                startActivity(i_guest)

            }

            R.id.re_staff -> {
//                re_staff!!.setEnabled(false)
//                re_staff!!.setClickable(false)
                val i_staff = Intent(this@Dashboard, StaffListActivity::class.java)
                startActivity(i_staff)

            }

            R.id.tv_subscriptiondate -> {
            }
            R.id.tv_languagesettings ->

                showDialog()
//            R.id.record -> {
//
//                val i = Intent(this@Dashboard, WalkieTalkieActivity::class.java)
//                startActivity(i)
//            }
        }//                myAudioRecorder = new MediaRecorder();
        //                myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //                myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        //                myAudioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        //                myAudioRecorder.setOutputFile(mFileName);
        //
        //
        //                try {
        //                    myAudioRecorder.prepare();
        //                    myAudioRecorder.start();
        //                } catch (IllegalStateException e) {
        //                    e.printStackTrace();
        //                } catch (IOException e) {
        //                    e.printStackTrace();
        //                }
        //// Started to stop after 5 sec automatically
        //                new Handler().postDelayed(new Runnable() {
        //                    @Override
        //                    public void run() {
        //                        if(myAudioRecorder!= null)
        //                        {
        //                            myAudioRecorder.reset();
        //                            myAudioRecorder.release();
        //                            Toast.makeText(getApplicationContext(),"Recording Stopped",Toast.LENGTH_LONG).show();
        //                            myAudioRecorder = null;
        //                        }
        //
        //                        record.setEnabled(true);
        //
        //                    }
        //                }, 5000);
        //
        //                // To stop after 5 sec automatically end
        //
        //                Toast.makeText(getApplicationContext(), "Recording Started", Toast.LENGTH_LONG).show();
    }

    fun downloadBiometricData_Loop() {
//        for ((_, _, _, _, _, _, _, _, _, wkWorkID) in LocalDb.getStaffs(realm)!!) {
//            //if the existing elements contains the search input
//            if (dbh!!.fingercount(wkWorkID) > 3) {
//
//            } else {
//                val ddc = Intent(applicationContext, BackgroundSyncReceiver::class.java)
//                Log.d("btn_biometric", "af $wkWorkID")
//                ddc.putExtra(BSR_Action, SYNC_STAFF_BIOMETRIC)
//                ddc.putExtra("ID", wkWorkID)
//                sendBroadcast(ddc)
//
//            }
//
//        }
    }

    /// End Added by Rajesh

    internal fun init() {

        Prefs.putBoolean("ACTIVE_SOS",false);
        startService(Intent(this@Dashboard, FRTDBService::class.java))

        showProgressrefresh()
        initRealm()
//        realm.executeTransaction { realm ->
//            realm.deleteAll()
//        }
        mHandlerr = Handler()
        //startRepeatingTask()
        database =  DBHelper(this);


        btn_in=findViewById(R.id.btn_in)
        btn_out=findViewById(R.id.btn_out)
        btn_in.setBackgroundColor(resources.getColor(R.color.orange))
        btn_out.setBackgroundColor(resources.getColor(R.color.grey))

        walk1=findViewById(R.id.walky)
        walk2=findViewById(R.id.walky1)
        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        try {
            audiofile = File.createTempFile("AudioRecording", ".3gp", dir)
            Log.d("uploadAudio 43",audiofile.toString() )
        } catch (e: IOException) {
            //            Log.e(TAG, "external storage access error");
            return
        }


        mFileName = audiofile!!.getAbsolutePath()
        Log.d("uploadAudio 51",mFileName )

        mp = MediaPlayer.create(this@Dashboard, R.raw.walkietalkiestart);

        walk1?.setOnTouchListener(View.OnTouchListener { v, event ->
            // TODO Auto-generated method stub
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {

                    try {
                        if (mp.isPlaying()) {
                            mp.stop()
                            mp.release()
                            mp = MediaPlayer.create(this@Dashboard, R.raw.walkietalkiestart)
                        }
                        mp.start()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    startRecording()
                    walk2?.visibility=View.VISIBLE
                    walk1?.visibility=View.GONE

                    return@OnTouchListener true
                }
                MotionEvent.ACTION_UP -> {
                    try {
                        if (mp.isPlaying()) {
                            mp.stop()
                            mp.release()
                            mp = MediaPlayer.create(this@Dashboard, R.raw.walkietalkiestop)
                        }
                        mp.start()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    walk2?.visibility=View.GONE
                    walk1?.visibility=View.VISIBLE
                    stopRecording()
                }
            }
            false
        })


        this.mHandler = Handler()
        tv_nodata = findViewById(R.id.tv_nodata)
        swipeContainer = findViewById<View>(R.id.swipeContainer) as SwipeRefreshLayout
        champApiInterface = ChampApiClient.getClient().create(ChampApiInterface::class.java)
        tv_languagesettings = findViewById(R.id.tv_languagesettings)
        tv_languagesettings?.setOnClickListener(this)
        tv_version = findViewById(R.id.tv_version)
        tv_version?.setOnClickListener(this)
        tv_subscriptiondate = findViewById(R.id.tv_subscriptiondate)
        txt_gate_name = findViewById(R.id.txt_gate_name)
        tv_subscriptiondate?.setOnClickListener(this)
        iv_settings = findViewById(R.id.iv_settings)
        iv_settings?.setOnClickListener(this)
        lyt_settings = findViewById(R.id.lyt_settings)
        iv_settings?.setBackgroundResource(R.drawable.settings)
        lyt_settings?.visibility = View.GONE
        re_vehicle = findViewById(R.id.re_vehicle)
        re_vehicle?.setOnClickListener(this)
        re_guest = findViewById(R.id.re_guest)
        re_guest?.setOnClickListener(this)
        re_staff = findViewById(R.id.re_staff)
        re_staff?.setOnClickListener(this)
        re_delivery = findViewById(R.id.re_delivery)
        re_delivery?.setOnClickListener(this)
        rv_dashboard = findViewById(R.id.rv_dashboard)
        rv_dashboard?.setLayoutManager(LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false))



        swipeContainer!!.setOnRefreshListener {
            // Your code to refresh the list here.
            // Make sure you call swipeContainer.setRefreshing(false)
            // once the network request has completed successfully.

            fetchTimelineAsync(0)
            swipeContainer!!.isRefreshing = false
        }

        FirebaseMessaging.getInstance().subscribeToTopic("AllGuards" + LocalDb.getAssociation()!!.asAssnID)
            .addOnCompleteListener { task ->
                var msg = "SUCCESS"
                if (!task.isSuccessful) {
                    msg = "FAILED"
                }
                Log.e("SUBSCRIPTION", msg)

            }


        btn_in.setOnClickListener{
            btn_in.setBackgroundColor(resources.getColor(R.color.orange))
            btn_out.setBackgroundColor(resources.getColor(R.color.grey))

            if (LocalDb.getVisitorEnteredLog() != null) {

                newAl = LocalDb.getVisitorEnteredLog()
                // LocalDb.saveAllVisitorLog(newAl);
                if((newAl)!!.isEmpty()){
                    rv_dashboard!!.setVisibility(View.GONE)
                    tv_nodata!!.setVisibility(View.VISIBLE)
                    dismissProgressrefresh()
                }else {
                    rv_dashboard!!.setVisibility(View.VISIBLE)
                    tv_nodata!!.setVisibility(View.GONE)
                }


                vistorEntryListAdapter = VistorEntryListAdapter(newAl!!, this@Dashboard)
                rv_dashboard?.adapter = vistorEntryListAdapter
                btn_in.setBackgroundColor(resources.getColor(R.color.orange))
                btn_out.setBackgroundColor(resources.getColor(R.color.grey))
                dismissProgress()

            } else {
                rv_dashboard!!.setVisibility(View.GONE)
                tv_nodata!!.setVisibility(View.VISIBLE)
                dismissProgress()


                //                        AlertDialog.Builder builder = new AlertDialog.Builder(DashBoard.this);
                //                        // builder.setTitle("Need Permissions");
                //                        builder.setMessage("No data");
                //                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                //                            @Override
                //                            public void onClick(DialogInterface dialog, int which) {
                //                                dialog.cancel();
                //                                //openSettings();
                //                            }
                //                        });
                ////                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                ////                            @Override
                ////                            public void onClick(DialogInterface dialog, int which) {
                ////                                dialog.cancel();
                ////                            }
                ////                        });
                //                        builder.show();
            }

        }
        btn_out.setOnClickListener{

            //refresh ( 1000 )


            btn_in.setBackgroundColor(resources.getColor(R.color.grey))
            btn_out.setBackgroundColor(resources.getColor(R.color.orange))
            getExitVisitorLog()
        }


        if (!Prefs.getBoolean(BG_NOTIFICATION_ON, false)) {
            startService(Intent(this@Dashboard, BGService::class.java))
        }


        if (Prefs.getInt(PATROLLING_ID, 0) != 0) {
            startService(Intent(this@Dashboard, SGPatrollingService::class.java))
            val builder = AlertDialog.Builder(this@Dashboard)
            builder.setTitle("Patrolling Not Completed")
            builder.setMessage("Complete Now")
            builder.setPositiveButton("GOTO Patrolling") { dialog, which ->
                val i_vehicle = Intent(this@Dashboard, PatrollingActivitynew::class.java)
                startActivity(i_vehicle)
                dialog.cancel()
            }
            builder.setNegativeButton(
                "Cancel"
            ) { dialog, which -> dialog.cancel() }
            builder.show()
        }
    }

    private fun showSettingsDialog() {
        val builder = AlertDialog.Builder(this@Dashboard)
        builder.setTitle("Need Permissions")
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.")
        builder.setPositiveButton("GOTO SETTINGS") { dialog, which ->
            dialog.cancel()
            openSettings()
        }
        builder.setNegativeButton(
            "Cancel"
        ) { dialog, which -> dialog.cancel() }
        builder.show()

    }

    // navigating user to app settings
    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivityForResult(intent, 101)
    }

    fun getExitVisitorLog() {
        showProgress()
        val call = champApiInterface?.getVisitorLogExitList(LocalDb.getAssociation()!!.asAssnID.toString() + "")
        Log.d(
            "button_done ",
            "visitorlogbydate " + LocalDb.getAssociation()!!.asAssnID + " " + getCurrentTimeLocalYMD()
        )

        call?.enqueue(object : Callback<VisitorLogExitResp> {
            override fun onResponse(call: Call<VisitorLogExitResp>, response: Response<VisitorLogExitResp>) {
                dismissProgress()
                if (response.body()!!.success == true) {

                    if (response.body()!!.data.visitorLog != null) {
                        tv_nodata?.visibility = View.GONE

                        rv_dashboard?.visibility = View.VISIBLE

                        val nonExitedSort = ArrayList<VisitorLogExitResp.Data.VisitorLog>()
                        val exitedSort = ArrayList<VisitorLogExitResp.Data.VisitorLog>()

                        val arrayList = response.body()!!.data.visitorLog

                        for (s in arrayList) {
                            //if the existing elements contains the search input
                            Log.d(
                                "button_done ",
                                "visitorlogbydate " + s.vlExitT + " " + (s.vlExitT == "0001-01-01T00:00:00")
                            )

                        }

                        LocalDb.saveEnteredVisitorLog_old(nonExitedSort)

                        var newAl = ArrayList<VisitorLogExitResp.Data.VisitorLog>()

                        newAl = RandomUtils.getSortedVisitorLog_old(response.body()!!.data.visitorLog)
                        LocalDb.saveAllVisitorLog(newAl)

                        //  VistorListAdapter vistorListAdapter = new VistorListAdapter(newAl, DashBoard.this);
                        val vistorListAdapter = VistorListAdapter(response.body()!!.data.visitorLog, this@Dashboard)
                        rv_dashboard?.adapter = vistorListAdapter

                        if (arrayList.size == 0) {
                            Toast.makeText(this@Dashboard, "No items", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // rv_dashboard.setEmptyAdapter("No items to show!", false, 0);
                        tv_nodata?.visibility = View.VISIBLE

                        rv_dashboard?.visibility = View.GONE

                    }
                }

            }

            override fun onFailure(call: Call<VisitorLogExitResp>, t: Throwable) {
                call.cancel()
                dismissProgress()
                Log.d("button_done ", "visitorlogbydate " + t.message + " " + getCurrentTimeLocalYMD())

            }
        })

    }

    internal fun getLatestSubscription() {

        val call = champApiInterface?.getLatestSubscription(Prefs.getInt(ASSOCIATION_ID, 0).toString())
        call?.enqueue(object : Callback<SubscriptionResponse> {
            override fun onResponse(call: Call<SubscriptionResponse>, response: Response<SubscriptionResponse>) {

try {
    if (response.body()!!.getSuccess() == true) {
        val dateFormat_DMY = SimpleDateFormat("dd-MM-yyyy")
        val CurrentString = response.body()!!.data.getSubscription().sueDate
        val separated = CurrentString.split("T".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        subscriptionDate = separated[0]

        tv_subscriptiondate?.text = "Valid till: $subscriptionDate"
        //  if(PrefManager.getValidityDate().length()>0) {
        try {
            val dt_dwnld_date = dateFormat_DMY.parse(response.body()!!.data.getSubscription().sueDate)
            val c1 = Calendar.getInstance()
            c1.time = dt_dwnld_date

            val days = (c1.timeInMillis - System.currentTimeMillis()) / (24 * 60 * 60 * 1000) + 1

            if (0 < days && days <= 7) {
                val alertDialog = android.app.AlertDialog.Builder(this@Dashboard)
                alertDialog.setTitle("Your Association Subscription Expires in $days days")
                alertDialog.setPositiveButton(
                    "Ok"
                ) { dialog, which -> dialog.cancel() }
                // Showing Alert Message
                if (!this@Dashboard.isFinishing) {
                    alertDialog.show()
                }
            }

        } catch (ex: Exception) {

        }

    } else {
    }
}catch (e:KotlinNullPointerException){

}
            }

            override fun onFailure(call: Call<SubscriptionResponse>, t: Throwable) {
                call.cancel()
            }
        })

    }

    fun fetchTimelineAsync(page: Int) {
        onResume()

    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

    }

    override fun onNothingSelected(parent: AdapterView<*>) {

    }

    private inner class BatteryBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
            // mBatteryLevelText.setText(getString(R.string.battery_level) + " " + level);
            // mBatteryLevelProgress.setProgress(level);
            val ifilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val batteryStatus = context.registerReceiver(null, ifilter)
            val status = batteryStatus!!.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            val isCharging =
                status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL

            var status_level = 0
            Log.d("action 3964 charging", "$isCharging Level:$status_level")
            if (level == 50) {
                Log.d("battery", level.toString())
                if (isCharging) {
                    Log.d("battery charging", level.toString())

                } else {
                    if (status_level > 3) {

                    } else {
                        status_level++
                        //                        t1.speak("Battery 50%. Connect to  charger", TextToSpeech.QUEUE_FLUSH, null);

                        val builder = android.app.AlertDialog.Builder(context)
                        builder.setMessage("Battery 50%. Connect to  charger")
                            .setCancelable(false)
                            .setPositiveButton("OK") { dialog, id ->
                                //do things

                                dialog.cancel()
                            }
                        val alert = builder.create()
                        if (!this@Dashboard.isFinishing) {
                            //                            alert.show();
                        }
                    }
                }
            } else if (level == 30) {
                if (isCharging) {

                } else {
                    t1?.speak("Battery 30%. Connect to  charger", TextToSpeech.QUEUE_FLUSH, null)

                    val builder = android.app.AlertDialog.Builder(context)
                    builder.setMessage("Battery 30%. Connect to  charger")
                        .setCancelable(false)
                        .setPositiveButton("OK") { dialog, id ->
                            //do things

                            dialog.cancel()
                        }
                    val alert = builder.create()
                    if (!this@Dashboard.isFinishing) {
                        alert.show()
                    }

                }
                Log.d("battery", level.toString())

            } else if (level == 20) {

                val builder = android.app.AlertDialog.Builder(context)
                val alert = builder.create()
                Log.d("battery", level.toString())
                if (isCharging) {
                    alert.dismiss()
                    Log.d("checkit 4022", "hi")
                } else {
                    t1?.speak("Battery low Connect to  charger", TextToSpeech.QUEUE_FLUSH, null)
                    //                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Battery 20%. Connect to charger")
                        .setCancelable(false)
                        .setPositiveButton("OK") { dialog, id ->
                            //do things
                            dialog.cancel()
                        }
                    //                    AlertDialog alert = builder.create();
                    if (!this@Dashboard.isFinishing) {
                        alert.show()
                    }

                }

            } else if (level == 10) {
                if (isCharging) {
                } else {
                    Log.d("battery", level.toString())
                    t1?.speak("Battery 10%. Connect to  charger", TextToSpeech.QUEUE_FLUSH, null)

                    val builder = android.app.AlertDialog.Builder(context)
                    builder.setMessage("Battery 10%. Connect to charger")
                        .setCancelable(false)
                        .setPositiveButton("OK") { dialog, id ->
                            //do things
                            dialog.cancel()
                        }
                    val alert = builder.create()
                    if (!this@Dashboard.isFinishing) {
                        alert.show()
                    }

                }

            } else if (level == 5) {

                if (isCharging) {
                } else {
                    //                    sendFCM_battery_alert();
                    Log.d("battery", level.toString())
                    t1?.speak("Battery critical. Connect to charger", TextToSpeech.QUEUE_FLUSH, null)

                    val builder = android.app.AlertDialog.Builder(context)
                    builder.setMessage("Battery critical. Connect to  charger")
                        .setCancelable(false)
                        .setPositiveButton("OK") { dialog, id ->
                            //do things
                            dialog.cancel()
                        }
                    val alert = builder.create()
                    if (!this@Dashboard.isFinishing) {
                        alert.show()
                    }
                }

            }
        }
    }

    private fun showDialog() {

        dialogs = Dialog(this@Dashboard)
        //dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogs?.setCancelable(true)
        dialogs?.setContentView(R.layout.layout_language_dailog)
        rg_language = dialogs?.findViewById(R.id.rg_language)
        rb_english = dialogs?.findViewById(R.id.rb_english)
        rb_hindi = dialogs?.findViewById(R.id.rb_hindi)
        if (Prefs.getString(LANGUAGE, null).equals("en", ignoreCase = true)) {
            rb_english?.isChecked = true
        } else if (Prefs.getString(LANGUAGE, null).equals("hi", ignoreCase = true)) {
            rb_hindi?.isChecked = true
        }


        dialogs?.show()

    }

    fun onRadioButtonClicked(v: View) {
        val checked = (v as RadioButton).isChecked
        var str = ""
        // Check which radio button was clicked
        when (v.getId()) {
            R.id.rb_hindi -> {
                if (checked)
                    str = "Hindi"
                restartInLocale("hi")
                dialogs?.dismiss()
                Prefs.putString(LANGUAGE, "hi")
            }
            R.id.rb_english -> {
                if (checked)
                    str = "English"
                Prefs.putString(LANGUAGE, "en")
                restartInLocale("en")
                dialogs?.dismiss()
            }
        }
    }

    companion object {
        private val ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION"

        fun isTimeAutomatic(c: Context): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                Settings.Global.getInt(c.contentResolver, Settings.Global.AUTO_TIME, 0) == 1
            } else {
                android.provider.Settings.System.getInt(
                    c.contentResolver,
                    android.provider.Settings.System.AUTO_TIME,
                    0
                ) == 1
            }
        }

        fun isTimeZoneAutomatic(c: Context): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                Settings.Global.getInt(c.contentResolver, Settings.Global.AUTO_TIME_ZONE, 0) == 1
            } else {
                android.provider.Settings.System.getInt(
                    c.contentResolver,
                    android.provider.Settings.System.AUTO_TIME,
                    0
                ) == 1
            }
        }

        fun trimCache(context: Context) {
            try {
                val dir = context.cacheDir
                if (dir != null && dir.isDirectory) {
                    deleteDir1(dir)
                }
            } catch (e: Exception) {
                // TODO: handle exception
            }

        }

        fun deleteDir1(dir: File?): Boolean {
            if (dir != null && dir.isDirectory) {
                val children = dir.list()
                for (i in children!!.indices) {
                    val success = deleteDir(File(dir, children[i]))
                    if (!success) {
                        return false
                    }
                }
            }

            // The directory is now empty so delete it
            return dir!!.delete()
        }

        fun deleteDir(dir: File?): Boolean {
            if (dir != null && dir.isDirectory) {
                val children = dir.list()
                for (i in children!!.indices) {
                    val success = deleteDir(File(dir, children[i]))
                    if (!success) {
                        return false
                    }
                }
                return dir.delete()
            } else return if (dir != null && dir.isFile) {
                dir.delete()
            } else {
                false
            }
        }
    }
    fun uploadAudio() {


        val Audio = audiofile
        audioclip = Audio.toString()

        val file = File(audiofile.toString())
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val body = MultipartBody.Part.createFormData("Test", audioclip, requestFile)
        val apiService = ImageApiClient.getImageClient().create(ImageApiInterface::class.java)
        val call = apiService.updateImageProfile(body)

        call.enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: retrofit2.Response<Any>) {
                try {
                    val intentAction1 = Intent(applicationContext, BackgroundSyncReceiver::class.java)
                    intentAction1.putExtra(BSR_Action, ConstantUtils.SENDAUDIO)
                    intentAction1.putExtra("FILENAME", response.body().toString())
                    sendBroadcast(intentAction1)
                    Log.d("uploadAudio 110", "response:" + response.body()!!)
                    Log.d("uploadAudio 112", file.toString())


                } catch (ex: Exception) {
                    Log.d("uploadAudio 113", "errr:" + ex.toString())
                }

            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                // Log error here since request failed
                Log.d("uploadAudio 121", t.toString())

            }
        })

    }

//    fun setEmptyView(emptyView: View) {
//        this.emptyView = emptyView
//    }

    private fun startRecording() {




        myAudioRecorder = MediaRecorder()
        myAudioRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        myAudioRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        myAudioRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        myAudioRecorder?.setOutputFile(mFileName)
        // recorder.setOnErrorListener(errorListener)
        // recorder.setOnInfoListener(infoListener)

        try {
            myAudioRecorder?.prepare()
            myAudioRecorder?.start()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }
    fun stopRecording() {



        //  Handler().postDelayed({
        if (myAudioRecorder != null) {
            myAudioRecorder!!.reset()
            myAudioRecorder?.setMaxDuration(50*1000)
            myAudioRecorder!!.release()
            // Toast.makeText(applicationContext, "Recording Stopped", Toast.LENGTH_LONG).show()
            uploadAudio()
            myAudioRecorder = null
        }

                    //record.isEnabled = true
               // }, 5000)
                try {
                    if (mp.isPlaying()) {
                        mp.stop()
                        mp.release()
                        mp = MediaPlayer.create(this@Dashboard, R.raw.walkietalkiestop)
                    }
                    mp.start()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                walk2?.visibility=View.GONE
                walk1?.visibility=View.VISIBLE

//        }
    }

    fun clearApplicationData(context: Context) {
        val cache = context.cacheDir
        val appDir = File(cache.parent)
        if (appDir.exists()) {
            val children = appDir.list()
            for (s in children!!) {
                if (s != "lib") {
                    if (s == "cache") {
                        deleteDir(File(appDir, s))
                        Log.i(
                            "EEEEEERRRRRROOOOOOORRRR",
                            "**************** File /data/data/APP_PACKAGE/$s DELETED *******************"
                        )
                    }
                }
            }
        }
    }

    fun deleteDir(dir: File?): Boolean {
        if (dir != null && dir.isDirectory) {
            val children = dir.list()
            var i = 0
            while (i < children!!.size) {
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
                i++
            }
        }

        assert(dir != null)
        return dir!!.delete()
    }
    fun openAlert() {
        AlertDialog.Builder(this@Dashboard)
            .setTitle("SignOut")
            .setMessage("TYPE YOUR MESSAGE HERE")
            .setPositiveButton(android.R.string.yes,
                DialogInterface.OnClickListener() { dialogInterface: DialogInterface, i: Int ->
                    fun onClick(dialog:DialogInterface , which:Int) {
                        // do want you want to do here
                        Toast.makeText(this@Dashboard,"Coming",Toast.LENGTH_LONG).show()
                        onDestroy()

                    }
                })
            .setNegativeButton(android.R.string.no,
                DialogInterface.OnClickListener() { dialogInterface: DialogInterface, i: Int ->
                    fun onClick(dialog:DialogInterface ,
                                which:Int) {
                    }
                }).show();

    }

    var mStatusChecker: Runnable = object : Runnable {
        override fun run() {
            try {
               // openAlert()
              // clearApplicationData(this@Dashboard)
                //trimCache(this@Dashboard)
//                finish();
//                startActivity(getIntent());
              //  updateStatus() //this function can change value of mInterval.
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandlerr!!.postDelayed(this, mInterval.toLong())
            }
        }
    }
//
//    fun startRepeatingTask() {
//        mStatusChecker.run()
//    }
//
//    fun stopRepeatingTask() {
//        mHandlerr!!.removeCallbacks(mStatusChecker)
//    }
//    private fun refresh( milliseconds:Long) {
//        val handler: Handler  =  Handler()
//        val runnable:Runnable = Runnable(){
//            run(){
//                content()
//            }
//        }
//
//        handler.postDelayed(runnable, milliseconds);
//
//    }

    //    private fun content() {
//        refresh(1000)
//    }
    fun getVisitorByWorkerId(assnID: Int,workerID:Int, unitId: Int, personName: String, mobileNumb: String, desgn: String,
                             workerType: String, staffID: Int, unitName: String,wkEntryImg:String){

        // showToast(this@Dashboard,assnID.toString()+".."+workerID+"..."+personName)
        RetrofitClinet.instance.getVisitorByWorkerId(OYE247TOKEN, workerID,assnID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CommonDisposable<getVisitorDataByWorker>() {

                override fun onSuccessResponse(getdata: getVisitorDataByWorker) {

                    if (getdata.success == true) {
                        // showToast(this@Dashboard,"already entered")
                        //  showToast(this@Dashboard,workerID.toString())

                    }
                }

                override fun onErrorResponse(e: Throwable) {

                    // showToast(this@Dashboard,"false")
                    //showToast(this@Dashboard,workerID.toString()+"-"+unitId+"-"+personName+"-"+mobileNumb+"-"+desgn+"-"+workerType+"-"+staffID+"-"+unitName)

                    visitorLog(
                        unitId, personName,
                        mobileNumb, desgn, workerType,
                        staffID, unitName,wkEntryImg
                    )

                    // Log.d("check 78 ", "bio")
                  //  t1?.speak("Welcome"+personName, TextToSpeech.QUEUE_FLUSH, null)
//                    showToast(this@Dashboard,personName)
                    //Log.d("Error WorkerList",e.toString())

                }

                override fun noNetowork() {
                    Toast.makeText(this@Dashboard, "No network call ", Toast.LENGTH_LONG)
                        .show()
                }
            })
    }

//    override fun onUserInteraction() {
//        super.onUserInteraction()
//        stopHandler();//stop first and then start
//        startHandler();
//    }
//   fun stopHandler() {
//    handler!!.removeCallbacks(r);
//}
//    fun startHandler() {
//    handler!!.postDelayed(r, 1*60*1000);
//}
//
//
//    val myHandler =  Handler();
//   val myRunnable =  Runnable() {
//
//       Toast.makeText(this@Dashboard,"Hii",Toast.LENGTH_LONG).show()
//       var i:Intent  = getBaseContext().getPackageManager()
//                         .getLaunchIntentForPackage( getBaseContext().getPackageName() );
//            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(i);
//       finish()
//
//
//    };
//
//    override fun onUserInteraction() {
//        super.onUserInteraction()
//        myHandler.removeCallbacks(myRunnable);
//        myHandler.postDelayed(myRunnable,2000);
//    }
//
//    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
//        Toast.makeText(this@Dashboard,"gjgj",Toast.LENGTH_LONG).show()
//  //  val timestamp = System.getCurrentTimeMilis();
//    val timestamp = 2000;
//    return super.dispatchTouchEvent(ev)
//    }


    class LogOutTimerTask : TimerTask() {
        val context:Context?=null

        override fun run() {

Toast.makeText(context,"Hii",Toast.LENGTH_LONG).show()
            var i:Intent  = context!!.getPackageManager()
                .getLaunchIntentForPackage( context.getPackageName() );
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context!!.startActivity(i);


       }
    }




}
