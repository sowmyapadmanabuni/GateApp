package com.oyespace.guards


import SecuGen.FDxSDKPro.*
import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.*
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.*
import android.provider.Settings
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.telephony.CellInfoLte
import android.telephony.TelephonyManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.oyespace.guards.activity.BaseKotlinActivity
import com.oyespace.guards.activity.ServiceProviderListActivity
import com.oyespace.guards.activity.StaffListActivity
import com.oyespace.guards.adapter.ChildEventListenerAdapter
import com.oyespace.guards.adapter.VisitorEntryListAdapter
import com.oyespace.guards.adapter.VistorOutListAdapter
import com.oyespace.guards.com.oyespace.guards.fcm.FRTDBService
import com.oyespace.guards.constants.PrefKeys
import com.oyespace.guards.constants.PrefKeys.*
import com.oyespace.guards.guest.GuestCustomViewFinderScannerActivity
import com.oyespace.guards.kidexit.KidExitBlockSelectionActivity
import com.oyespace.guards.models.*
import com.oyespace.guards.models.FingerPrint
import com.oyespace.guards.models.Worker
import com.oyespace.guards.network.*
import com.oyespace.guards.ocr.CaptureImageOcr
import com.oyespace.guards.pertroling.PScheduleListActivity
import com.oyespace.guards.pertroling.PatrollingActivitynew
import com.oyespace.guards.pojo.*
import com.oyespace.guards.realm.RealmDB
import com.oyespace.guards.repo.StaffRepo
import com.oyespace.guards.repo.VisitorLogRepo
import com.oyespace.guards.request.VisitorEntryReqJv
import com.oyespace.guards.resident.ResidentIdActivity
import com.oyespace.guards.residentidcard.ResidentIdActivity_OLD
import com.oyespace.guards.responce.VisitorLogCreateResp
import com.oyespace.guards.responce.VisitorLogExitResp
import com.oyespace.guards.services.SOSSirenService
import com.oyespace.guards.utils.*
import com.oyespace.guards.utils.ConstantUtils.*
import com.oyespace.guards.utils.DateTimeUtils.getCurrentTimeLocal
import com.oyespace.guards.utils.DateTimeUtils.getCurrentTimeLocalYMD
import com.oyespace.guards.utils.FirebaseDBUtils.Companion.addWalkieTalkieAudioFirebase
import com.oyespace.guards.utils.FirebaseDBUtils.Companion.removeWalkieTalkieAudioFirebase
import com.oyespace.guards.utils.FirebaseDBUtils.Companion.updateFirebaseColor
import com.oyespace.guards.utils.Utils.showToast
import com.oyespace.guards.zeotelapi.ZeotelRetrofitClinet
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.concurrent.fixedRateTimer

class Dashboard : BaseKotlinActivity(), View.OnClickListener, ResponseHandler, SGFingerPresentEvent {

var iv_torch:Button?=null

    private val REQUEST_CODE_SPEECH_INPUT = 100
    var unAccountID: String? = null
    private lateinit var tv: EditText
    var value: String? = ""
    var handler: Handler? = null
    var runnable: Runnable? = null
    private var arrayList: ArrayList<VisitorLogExitResp.Data.VisitorLog>? = null
    lateinit var cd: ConnectionDetector
    var timer: Timer? = null
    private val mInterval = 96000 // 5 seconds by default, can be changed later
    private var mHandlerr: Handler? = null
    var counter: Int? = 0
    //  internal var database: DBHelper?=null

    var audioclip: String? = null
    lateinit var mp: MediaPlayer

    internal var newAl: ArrayList<VisitorLog>? = ArrayList()
    var mHandler: Handler? = null
    lateinit var btn_in: Button
    lateinit var btn_out: Button
    lateinit var btn_mic: Button

    private var audiofile: File? = null
    var visitorEntryListAdapter: VisitorEntryListAdapter? = null
    var vistorOutListAdapter: VistorOutListAdapter? = null
    private var mFileName = ""
    private var myAudioRecorder: MediaRecorder? = null

    var iv_settings: ImageView? = null
    var iv_help: ImageView? = null
    lateinit var tv_nodata: TextView
    // LinearLayout lyt_settings;
    var clickable = 0
    var clickable1 = 0
    var re_kidexit:RelativeLayout?=null
    var re_resident: RelativeLayout? = null
    var re_vehicle: RelativeLayout? = null
    var re_staff: RelativeLayout? = null
    var re_guest: RelativeLayout? = null
    var re_delivery: RelativeLayout? = null
    var lyt_settings: RelativeLayout? = null
    var champApiInterface: ChampApiInterface? = null
    var rv_dashboard: RecyclerView? = null
    var tv_subscriptiondate: TextView? = null
    var tv_version: TextView? = null
    var tv_languagesettings: TextView? = null
    var txt_assn_name: TextView? = null
    var txt_device_name: TextView? = null
    var txt_gate_name: TextView? = null
    var subscriptionDate: String? = null
    internal var stringNumber: String? = null
    internal var stringCode: String? = null

    var showingOutLog = false

    internal var language: String? = ""
    internal var wvvalue: String? = ""
    var walk1: Button? = null
    var walk2: ImageView? = null
    internal var telMgr: TelephonyManager? = null
    internal var existInDB1 = BooleanArray(1)
    internal var existInDB2 = BooleanArray(1)
    internal var existInDB3 = BooleanArray(1)
    internal var tempFP1: ByteArray? = null
    internal var tempFP2: ByteArray? = null
    internal var tempFP3: ByteArray? = null
    internal var fingerPrints: List<FingerPrint> = arrayListOf(FingerPrint())
    internal var t1: TextToSpeech? = null
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

    var pkeyString = "-2"

    private var mLed: Boolean = false
    private var mAutoOnEnabled = true
    private var bSecuGenDeviceOpened: Boolean = false
    private var usbPermissionRequested: Boolean = false
    private var usbConnected = true
    private var sgfplib: JSGFPLib? = null
    var pTimer: Timer? = null
    var pTimerChecker: Timer? = null
    //a separate thread.
//    lateinit var notificationSyncFBRef: DatabaseReference
    lateinit var walkieAudioFBRef: DatabaseReference
    lateinit var fbdbAssocName: String


    var fingerDetectedHandler: Handler = object : Handler() {
        // @Override
        override fun handleMessage(msg: Message) {
            //Handle the message +sgfplib.DeviceInUse()
            Log.d("Dgddfdfhhjhj : ", "ff bf entrybywalk $autoooooo   $mAutoOnEnabled $usbConnected")

            if (mAutoOnEnabled) {

                Log.d("Dgddfdfhhjhj : ", "bf bf entrybywalk $autoooooo $nnnn  $mAutoOnEnabled $usbConnected")


                CaptureFingerPrint()

                Log.d("Dgddfdfhhjhj : ", "ff af entrybywalk $autoooooo $nnnn  $mAutoOnEnabled $usbConnected")
                mAutoOnEnabled = false
                val myRunnable = Runnable {
                    // your code here
                    mAutoOnEnabled = true
                    mLed=true
                }
                val myHandler = Handler()
                //final int TIME_TO_WAIT = 2000;

                myHandler.postDelayed(myRunnable, 4000)

            }
        }
    }
    private var mReceiver: BroadcastReceiver? = null
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action!!.equals(SYNC, ignoreCase = true)) {
                val message = intent.getStringExtra("message")

                if (message.equals(VISITOR_ENTRY_SYNC, ignoreCase = true)) {

                    loadEntryVisitorLog()

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
               // sgfplib!!.Close()
            }
        }
    }

    internal var rb_english: RadioButton? = null
    internal var rb_hindi: RadioButton? = null
    internal var rg_language: RadioGroup? = null
    internal var dialogs: Dialog? = null
    lateinit var textWatcher: TextWatcher


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel();
        setLocale(Prefs.getString(LANGUAGE, null))
        setContentView(R.layout.activity_dash_board)

        //   getSubscriptionData()

        cd = ConnectionDetector()
        cd.isConnectingToInternet(this@Dashboard)
        init()
        Prefs.putString("BUTTON", "IN")
        showingOutLog = false
        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

                try {
                    if (showingOutLog) {
                        if (vistorOutListAdapter != null) {
                            vistorOutListAdapter!!.applySearch(charSequence.toString())

                        }
                    } else {
                        if (visitorEntryListAdapter != null) {
                            visitorEntryListAdapter!!.applySearch(charSequence.toString())
                        }
                    }
                } catch (e: KotlinNullPointerException) {

                }
            }

            override fun afterTextChanged(editable: Editable) {
            }
        }
        tv.addTextChangedListener(textWatcher)
        language = Prefs.getString(LANGUAGE, null)
        if (language != null) {
            Log.v("language", language)
        } else {
            Prefs.putString(LANGUAGE, "en")
        }
//        if (!Prefs.getBoolean(BG_NOTIFICATION_ON, false)) {
//            startService(Intent(this@Dashboard, BGService::class.java))
//
//        }
//        getLatestSubscription()
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


        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        mReceiver = BatteryBroadcastReceiver()


        telMgr = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        sendAnalyticsData("SDDashB_Oncreate", "Start", Date().toString() + "")


        //        startService(new Intent(DashBoard.this, SGTrackingService.class));


        t1 = TextToSpeech(applicationContext, TextToSpeech.OnInitListener { status ->
            if (status != TextToSpeech.ERROR)
            // t1?.language = Locale.getDefault()
                t1?.language = Locale.getDefault()
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

       // if (Prefs.getString(PrefKeys.MODEL_NUMBER, null).equals("Nokia 2.1")) {

            registerReceiver(mUsbReceiver, filter)
      //  }


        sgfplib = JSGFPLib(getSystemService(Context.USB_SERVICE) as UsbManager)

        bSecuGenDeviceOpened = false
        usbPermissionRequested = false

        mLed = false
        mAutoOnEnabled = true
        autoOn = SGAutoOnEventNotifier(sgfplib, this)
        nCaptureModeN = 0

        txt_assn_name = findViewById(R.id.txt_assn_name)

        if (Prefs.getString(PrefKeys.MODEL_NUMBER, null).equals("Nokia 1")) {
            txt_assn_name!!.textSize = 5 * resources.displayMetrics.density
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
            txt_device_name?.text = "V: $appVersion${if (debug) "D" else ""}"

        } catch (ex: Exception) {
            ex.printStackTrace()
            txt_device_name?.text = " "

        }

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(receiver, IntentFilter("SYNC"))//constant


        fbdbAssocName = "A_${Prefs.getInt(ASSOCIATION_ID, 0)}"
        Log.d("taaag", "listening to $fbdbAssocName firebase object reference")
        walkieAudioFBRef = FirebaseDatabase.getInstance().getReference("wt_audio").child(fbdbAssocName)

        removeWalkieTalkieAudioFirebase()

        walkieAudioFBRef.addChildEventListener(object : ChildEventListenerAdapter() {

            override fun onChildChanged(ds: DataSnapshot, p1: String?) {
                onChildAdded(ds, p1)
            }

            override fun onChildAdded(ds: DataSnapshot, p1: String?) {
                val filename = ds.value.toString()

                if (filename != null && !filename.equals("null")) {
                    AppUtils.playWalkieTalkiAudio(this@Dashboard, filename)
                    removeWalkieTalkieAudioFirebase()
                }
            }

        })

    }


    @SuppressLint("NewApi")
    fun createNotificationChannel(){
        var notificationManager:NotificationManager =
            getSystemService(
                Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(resources.getString(R.string.gate_channel), resources.getString(R.string.gate_channel), importance)


        val sound:Uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.siren)
        val audioAttributes:AudioAttributes = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).build()

        channel.description = resources.getString(R.string.gate_channel)
        channel.enableLights(true)
        channel.lightColor = Color.RED
        channel.enableVibration(true)
        channel.setSound(sound,audioAttributes)
        channel.vibrationPattern =
            longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
        notificationManager?.createNotificationChannel(channel)
    }
    fun stopSiren() {
        val intent = Intent(this, SOSSirenService::class.java)
        this.stopService(intent)
    }

    @SuppressLint("MissingPermission")
    fun getCellAndWifiInfo() {
        val wifiManager = getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiManager.isWifiEnabled = true
        val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val cellLocation = telephonyManager.allCellInfo
        if (cellLocation != null && cellLocation.size > 0) {  //verify if is'nt null
            var lac = 0
            var cid = 0
            var mcc = 0
            var mnc = 0
            var str = 0
            var tim = 0

            var info = cellLocation[0]
            //for (info in cellLocation) {    // Loop for go through Muteablelist

            if (info is CellInfoLte) {       //verify if Network is LTE type
                val identityLte = info.cellIdentity     //get the cellIdentity data
                lac = identityLte.tac    //get the CI(CellIdentity) string
                cid = identityLte.ci
                mcc = identityLte.mcc
                mnc = identityLte.mnc
                str = info.cellSignalStrength.dbm
                tim = info.cellSignalStrength.timingAdvance

            }
            //}
            val wifircvr = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    val wifiList: List<ScanResult> = wifiManager.scanResults
                    if (wifiList.size > 0) {
                        val scanResult: ScanResult = wifiList[0]

                    }
                    Log.e("WIFILIST", "" + wifiList)
                    Log.e("CELLINFO", "LAC: " + lac + " - SID: " + cid + " - MCC: " + mcc + " - MNC: " + mnc + " - STRENGTH: " + str + " - TIMIN: " + tim)
                }
            }
//            registerReceiver(wifircvr, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
//            wifiManager.startScan()


        }
    }

    fun notifyPatrollingReminder() {
        val intentAction1 = Intent(this@Dashboard, BackgroundSyncReceiver::class.java)
        intentAction1.putExtra(BSR_Action, ConstantUtils.BGS_PATROLLING_ALARM)
        sendBroadcast(intentAction1)
    }


    fun runTimerCheck() {
        pTimerChecker = fixedRateTimer("patroll_timer_checker", false, 6000, 15000) {
            this@Dashboard.runOnUiThread {
                val activeAlert = Prefs.getBoolean("ACTIVE_ALERT", false)
                if (!activeAlert) {
                    if (pTimer == null) {
                        runPatrollingTimer()
                    }
                }
            }
        }
    }

    fun runPatrollingTimer() {
        pTimer = fixedRateTimer("patroll_timer", false, 1000, 60000) {
            this@Dashboard.runOnUiThread {
                notifyPatrollingReminder()
            }
        }
    }

    fun stopPatrollingTimer() {
        if (pTimer != null) {
            Log.e("PTIMER", "CANCELLED")
            pTimer!!.cancel()
            pTimer = null
        }
    }



    override fun onResume() {

        if (pTimerChecker == null) {
            runTimerCheck()
        }

        //registerReceiver(mUsbReceiver, filter)



        fixedRateTimer("timer", false, 0, 60000) {
            this@Dashboard.runOnUiThread {
                //  getSubscriptionData()
                //  notifyPatrollingReminder()
            }
        }

        try {

            Prefs.putBoolean("ACTIVE_SOS", false)
            stopSiren()
            getCellAndWifiInfo()
            //if(!LocalDb.isServiceRunning(FRTDBService::class.java,this)) {
            startService(Intent(this@Dashboard, FRTDBService::class.java))
            //}
            downloadBiometricData_Loop()
        } catch (e: NullPointerException) {

        }
        val ddc1 = Intent(this@Dashboard, BackgroundSyncReceiver::class.java)
        ddc1.putExtra(BSR_Action, SYNC_STAFF_LIST)
        sendBroadcast(ddc1)

        val updateHandler = Handler()


        updateHandler.postDelayed(runnable, 1000)
        //  stopRepeatingTask()

        super.onResume()

        dismissProgressrefresh()
        loadEntryVisitorLog()


        if (isTimeAutomatic(application)) {

        } else {
            val alertDialogBuilder = AlertDialog.Builder(this@Dashboard)
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
            val alertDialogBuilder = AlertDialog.Builder(this@Dashboard)
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



        bSecuGenDeviceOpened = false
        usbPermissionRequested = false

        mLed = false
        mAutoOnEnabled = true
        autoOn = SGAutoOnEventNotifier(sgfplib, this)
        nCaptureModeN = 0



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


    }

    private fun loadEntryVisitorLog(callback: () -> Unit = {}) {
        if (showingOutLog) {
            return
        }

        if (newAl == null || newAl!!.isEmpty()) {
            tv_nodata.text = "fetching data..."
            tv_nodata.visibility = View.VISIBLE
        }
        VisitorLogRepo.get_IN_VisitorLog(true, object : VisitorLogRepo.VisitorLogFetchListener {
            override fun onFetch(visitorLog: ArrayList<VisitorLog>?, errorMessage: String?) {

                // reset no data text
                rv_dashboard!!.visibility = View.GONE
                tv_nodata.text = "no data"
                tv_nodata.visibility = View.VISIBLE

                if (visitorLog != null) {
                    newAl = visitorLog

                    if (newAl!!.isEmpty()) {
                        rv_dashboard!!.visibility = View.GONE
                        tv_nodata.visibility = View.VISIBLE

                    } else {
                        rv_dashboard!!.visibility = View.VISIBLE
                        tv_nodata.visibility = View.GONE
                    }

                    if (visitorEntryListAdapter != null) {// it works second time and later
                        visitorEntryListAdapter!!.setVisitorLog(newAl)
                    } else {
                        visitorEntryListAdapter = VisitorEntryListAdapter(newAl!!, this@Dashboard)
                        rv_dashboard?.adapter = visitorEntryListAdapter
                    }

                    rv_dashboard?.smoothScrollToPosition(0)

                    val searchString = tv.text.toString()
                    if (!searchString.isEmpty()) {
                        visitorEntryListAdapter!!.applySearch(searchString)
                    }

                    callback()

                } else {
                    Toast.makeText(this@Dashboard, errorMessage, Toast.LENGTH_SHORT).show()
                }

            }

        })

    }

    private fun loadExitVisitorLog(pullFromBackend: Boolean) {

        if (!showingOutLog) {
            return
        }

        showProgress("loading exit data...")
        VisitorLogRepo.get_OUT_VisitorLog(pullFromBackend, object : VisitorLogRepo.ExitVisitorLogFetchListener {
            override fun onFetch(visitorLog: ArrayList<ExitVisitorLog>?, errorMessage: String?) {

                if (visitorLog != null) {


                    if (!visitorLog.isEmpty()) {
                        rv_dashboard!!.visibility = View.VISIBLE
                        tv_nodata.visibility = View.GONE
                    } else {
                        rv_dashboard!!.visibility = View.GONE
                        tv_nodata.visibility = View.VISIBLE
                    }

                    if (vistorOutListAdapter == null) {
                        vistorOutListAdapter = VistorOutListAdapter(visitorLog, this@Dashboard)
                    } else {
                        vistorOutListAdapter!!.setVisitorLog(visitorLog)
                    }
                    rv_dashboard?.adapter = vistorOutListAdapter

                    val searchString = tv.text.toString()
                    if (!searchString.isEmpty()) {
                        // force refresh images if pulled to refresh on searched list
                        vistorOutListAdapter!!.applySearch(searchString, true)
                    }

                } else {
                    Toast.makeText(this@Dashboard, errorMessage, Toast.LENGTH_SHORT).show()
                }
                dismissProgress()
            }

        })


    }

    override fun onFailure(e: Exception, urlId: Int) {

        showToast(this, e.message + " id " + urlId)
    }

    override fun onSuccess(responce: String, data: Any, urlId: Int, position: Int) {

        when (urlId) {
            URLData.URL_VISITOR_LOG.urlId -> {

                val loginDetailsResponce = data as VisitorLogCreateResp
                if (loginDetailsResponce != null) {

                    Log.d(
                        "str3",
                        "str3: " + urlId + " id " + position + " " + " " + " " + loginDetailsResponce.success.toString()
                    )
                    val loginDetailsResponceGson = Gson().fromJson(responce, GetVisitorEntryResponse::class.java)
                    if (loginDetailsResponce.success.equals("true", ignoreCase = true)) {
                        //   showToast(this, " Saved");
                        Log.e(
                            "str3",
                            "str3: " + loginDetailsResponceGson.data.visitorLog.vlVisLgID
                        )
                        //val loginDetailsResponce = Gson().fromJson(responce, CaptureFPResponse::class.java)
                        if (!pkeyString.equals("-2")) {
                            val pkey = pkeyString.toInt()
                            val visitor =
                                realm.where(VisitorLog::class.java).equalTo("vlVisLgID", pkey)
                                    .findFirst()
                            if (visitor != null) {
                                if (!realm.isInTransaction) {
                                    realm.beginTransaction()
                                }
                                val temp = realm.copyFromRealm(visitor)
                                temp.vlVisLgID = loginDetailsResponceGson.data.visitorLog.vlVisLgID
                                realm.copyToRealmOrUpdate(temp)
                                visitor.deleteFromRealm()
                                realm.commitTransaction()
                            }
                        }
//                        if (!realm.isInTransaction) {
//                            realm.beginTransaction()
//                        }
//                        visitor!!.deleteFromRealm()
//                        realm.commitTransaction()


                        visitorEntryLog(loginDetailsResponce.data.visitorLog.vlVisLgID)


                    } else {
                        showToast(this, "Visitor Data not saved ")

                    }

                } else {
                    showToast(this, "Something went wrong . please try again ")
                }
            }
            URLData.URL_VISITOR_MAKE_ENTRY.urlId -> {
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
            URLData.URL_VISITOR_MAKE_EXIT.urlId -> {
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


    }

    private fun visitorEntryLog(vlVisLgID: Int) {

        val restClient = RestClient.getInstance()

        val loginReq = VisitorEntryReqJv()

        loginReq.VLVisLgID = vlVisLgID
        loginReq.VLEntryT = getCurrentTimeLocal()
        loginReq.VLEntyWID = StaffRepo.getStaffList()!![0].wkWorkID

        Log.d("CreateVisitorLogResp", "StaffEntry $loginReq")
        //  showToast(this, "StaffEntry $loginReq");

        restClient.addHeader(OYE247KEY, OYE247TOKEN)
        restClient.post<Any>(
            this,
            loginReq,
            VisitorLogCreateResp::class.java,
            this,
            URLData.URL_VISITOR_MAKE_ENTRY
        )

    }

    public override fun onPause() {
        Log.e("DB_ONPAUSE", "ONPAUSE" + pTimer)
        if (pTimer != null) {
            Log.e("PTIMER", "CANCELLED")
            pTimer!!.cancel()
            pTimer = null
        }
        super.onPause()


    }

    public override fun onDestroy() {
        // clearApplicationData()

        stopPatrollingTimer()

        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
        if (bSecuGenDeviceOpened) {
            autoOn!!.stop()
            sgfplib!!.CloseDevice()
            bSecuGenDeviceOpened = false
        }
       // sgfplib!!.Close()


        mVerifyImage = null
        mVerifyTemplate = null
        //        sgfplib.Close();
        //if (Prefs.getString(PrefKeys.MODEL_NUMBER, null).equals("Nokia 2.1")) {
        unregisterReceiver(mUsbReceiver)
        //}
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

        if(usbConnected) {

            fingerDetectedHandler.sendMessage(Message())
        }
    }

    fun CaptureFingerPrint() {

        if (bSecuGenDeviceOpened == true) {

//                val fp: ByteArray = getFingerprintFromScanner() ?: return

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
              //  Log.d("taaag", "fp: ${fp.size}")
                val id = matchFingerprint(mVerifyTemplate!!)
                Log.d("taaag", "check result: $id")


                if (id > 0) {
                    val staff: VisitorLog? = VisitorLogRepo.get_IN_VisitorForId(id)

                    // if yes, then make exit call
                    if (staff != null) {
                        t1?.speak("Thank You " + staff.vlfName, TextToSpeech.QUEUE_FLUSH, null)
                        VisitorLogRepo.updateVisitorStatus(this, staff, EXITED)
                    } else {

                        // get staff for id
                        val worker: Worker? = StaffRepo.getStaffForId(id)

                        Log.d("taaag", "worker found: $worker")
                        if (worker == null) {
                            showToast(this, "No staff found")
                        } else {


//                            if (LocalDb.getVisitorEnteredLog() != null) {
//                                if (RandomUtils.contain(LocalDb.getVisitorEnteredLog(), intent.getIntExtra(ConstantUtils.WORKER_ID, 0))) {
//                                    Utils.showToast(
//                                        this@Dashboard,
//                                        "Duplicate Entry not allowed"
//                                    )
//                                    t1?.speak("Duplicate Entry", TextToSpeech.QUEUE_FLUSH, null)
//                                }
//                            }else {
//
//                                visitorLogBiometric(
//                                    worker.unUnitID,
//                                    "${worker.wkfName} ${worker.wklName}",
//                                    worker.wkMobile,
//                                    worker.wkDesgn,
//                                    worker.wkWrkType,
//                                    worker.wkWorkID,
//                                    worker.unUniName,
//                                    worker.wkEntryImg
//                                )
//                            }

                            val phone = worker.wkMobile

                            if (phone == null || phone.isEmpty()) {
                                getVisitorByWorkerId(
                                    Prefs.getInt(ASSOCIATION_ID, 0),
                                    worker.wkWorkID,
                                    worker.unUnitID,
                                    "${worker.wkfName} ${worker.wklName}",
                                    worker.wkMobile,
                                    worker.wkDesgn,
                                    worker.wkWrkType,
                                    worker.wkWorkID,
                                    worker.unUniName,
                                    worker.wkEntryImg
                                )
                            } else {

                                val allowEntry = VisitorLogRepo.allowEntry("", phone)

                                if (allowEntry) {
                                    getVisitorByWorkerId(
                                        Prefs.getInt(ASSOCIATION_ID, 0),
                                        worker.wkWorkID,
                                        worker.unUnitID,
                                        "${worker.wkfName} ${worker.wklName}",
                                        worker.wkMobile,
                                        worker.wkDesgn,
                                        worker.wkWrkType,
                                        worker.wkWorkID,
                                        worker.unUniName,
                                        worker.wkEntryImg
                                    )
                                } else {
                                    showToast(this, "Duplicate entries not allowed")
                                }
                            }


                        }


                    }


                } else {

                    t1?.speak("No Match Found", TextToSpeech.QUEUE_FLUSH, null)

                }

                mVerifyImage = null
                this.sgfplib!!.SetBrightness(100)
            } catch (ex: Exception) {
                sendExceptions("SGDBA_CptFingPt", ex.toString())
                Log.e("Biometric 1035", " $ex")
                ex.printStackTrace()
            }

        } else {
            Toast.makeText(applicationContext, "Biometric Device Not Attached", Toast.LENGTH_LONG)
                .show()
        }

    }

    fun matchFingerprint(fingerPrint: ByteArray): Int {

        val asscId = Prefs.getInt(ASSOCIATION_ID, 0)

        val fps = RealmDB.getRegularVisitorsFingerPrint(asscId)
        Log.v("taaag", "got ${fps.size} fingerprints from realm")
        val result = BooleanArray(1)
        for (fp in fps) {

            Log.v("taaag", "matching with ${fp.userName}'s fingerprint")

            sgfplib!!.MatchTemplate(fingerPrint, fp.FPImg1, SGFDxSecurityLevel.SL_HIGH, result)
            if (result[0]) return fp.userName.toInt()


            sgfplib!!.MatchTemplate(fingerPrint, fp.FPImg2, SGFDxSecurityLevel.SL_HIGH, result)
            if (result[0]) return fp.userName.toInt()

            sgfplib!!.MatchTemplate(fingerPrint, fp.FPImg3, SGFDxSecurityLevel.SL_HIGH, result)
            if (result[0]) return fp.userName.toInt()

        }

        return -1

    }

    override fun onStart() {
        Prefs.putBoolean("ACTIVE_SOS", false)
        Prefs.putBoolean("ACTIVE_ALERT", false)
        //if(!LocalDb.isServiceRunning(FRTDBService::class.java,this)) {
        startService(Intent(this@Dashboard, FRTDBService::class.java))
        registerReceiver(mReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

        getDeviceList(LocalDb.getAssociation()!!.asAssnID)

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
            R.id.iv_settings ->

                if (clickable == 0) {
                    clearSearchText()
                    lyt_settings?.visibility = View.VISIBLE
                    iv_settings?.setBackgroundResource(R.drawable.cancel)
                    clickable = 1
                } else if (clickable == 1) {
                    clearSearchText()
                    lyt_settings?.visibility = View.GONE
                    iv_settings?.setBackgroundResource(R.drawable.settings)
                    clickable = 0
                }
            R.id.tv_patrolling -> {
                //PatrollingLocActivity
                val i_vehicle = Intent(this@Dashboard, PScheduleListActivity::class.java)
                startActivity(i_vehicle)
            }

        }

    }

    internal fun onTabClicked(v: View) {
        Log.d("clcik", "view " + v.id)
        clearSearchText()
        when (v.id) {

            R.id.re_delivery -> {

                Prefs.putString(ConstantUtils.TYPE, "Create")
                val i_delivery = Intent(this@Dashboard, ServiceProviderListActivity::class.java)
                startActivity(i_delivery)


            }

            R.id.re_staff -> {
                val i_staff = Intent(this@Dashboard, StaffListActivity::class.java)
                startActivity(i_staff)

            }

            R.id.re_vehicle -> {

                val i_vehicle = Intent(this@Dashboard, CaptureImageOcr::class.java)
                startActivity(i_vehicle)

            }


            R.id.re_guest -> {
                val i_guest =
                    Intent(this@Dashboard, GuestCustomViewFinderScannerActivity::class.java)
                startActivity(i_guest)

            }

            R.id.re_resident -> {
                val i_staff = Intent(this@Dashboard, ResidentIdActivity_OLD::class.java)
                startActivity(i_staff)
            }
            R.id.re_kidexit->{

                val i_kidexit = Intent(this@Dashboard, KidExitBlockSelectionActivity::class.java)
                startActivity(i_kidexit)
            }
            R.id.iv_torch-> {


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    val camManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager;
                    var cameraId: String? = null
                    cameraId = camManager.getCameraIdList()[0];
                    if(clickable1==0){
                        try {
                            iv_torch!!.background=resources.getDrawable(R.drawable.torch_off)
                            camManager.setTorchMode(cameraId, true);   //Turn ON

                          //  iv_torch!!.text = "OFF"
                            clickable1=1
                        } catch (e: CameraAccessException) {
                            e.printStackTrace();
                        }
                    }
                    else if(clickable1==1){
                        camManager.setTorchMode(cameraId, false);
                       // iv_torch!!.text = "ON"
                        iv_torch!!.background=resources.getDrawable(R.drawable.torch_on)
                        clickable1=0

                    }
                }

            }
            R.id.tv_languagesettings ->

                showDialog()
        }

    }

    fun downloadBiometricData_Loop() {
        StaffRepo.getStaffList()!!.forEach {
            if (RealmDB.fingercount(it.wkWorkID) <= 3) {
                val ddc = Intent(applicationContext, BackgroundSyncReceiver::class.java)
                Log.d("btn_biometric", "af " + it.wkWorkID)
                ddc.putExtra(BSR_Action, SYNC_STAFF_BIOMETRIC)
                ddc.putExtra("ID", it.wkWorkID)
                sendBroadcast(ddc)

            }

        }
    }

    /// End Added by Rajesh

    internal fun init() {
        showProgressrefresh()
        mHandlerr = Handler()
        //startRepeatingTask()
        //database =  DBHelper(this);
        iv_torch=findViewById<Button>(R.id.iv_torch)
        iv_torch?.setOnClickListener ( this )
        tv = findViewById<EditText>(R.id.edt_search_text1)
        btn_mic = findViewById(R.id.btn_mic)
        btn_in = findViewById(R.id.btn_in)
        btn_out = findViewById(R.id.btn_out)
        btn_in.setBackgroundColor(resources.getColor(R.color.orange))
        btn_out.setBackgroundColor(resources.getColor(R.color.grey))

        walk1 = findViewById(R.id.walky)
        walk2 = findViewById(R.id.walky1)


        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        try {
            audiofile = File.createTempFile("AudioRecording", ".3gp", dir)
            Log.d("uploadAudio 43", audiofile.toString())
        } catch (e: IOException) {
            //            Log.e(TAG, "external storage access error");
            return
        }


        mFileName = audiofile!!.absolutePath
        Log.d("uploadAudio 51", mFileName)

        mp = MediaPlayer.create(this@Dashboard, R.raw.walkietalkiestart)

        walk1?.setOnTouchListener(View.OnTouchListener { v, event ->

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {

                    try {
                        if (mp.isPlaying) {
                            mp.stop()
                            mp.release()
                            mp = MediaPlayer.create(this@Dashboard, R.raw.walkietalkiestart)
                        }
                        mp.start()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    startRecording()
                    walk2?.visibility = View.VISIBLE
                    walk1?.visibility = View.GONE

                    return@OnTouchListener true
                }
                MotionEvent.ACTION_UP -> {
                    try {
                        if (mp.isPlaying) {
                            mp.stop()
                            mp.release()
                            mp = MediaPlayer.create(this@Dashboard, R.raw.walkietalkiestop)
                        }
                        mp.start()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    walk2?.visibility = View.GONE
                    walk1?.visibility = View.VISIBLE
                    stopRecording()
                }
            }
            false
        })



        this.mHandler = Handler()
        //   m_Runnable.run()


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
        iv_help = findViewById(R.id.iv_help)
        iv_settings?.setOnClickListener(this)
        lyt_settings = findViewById(R.id.lyt_settings)
        iv_settings?.setBackgroundResource(R.drawable.settings)
        lyt_settings?.visibility = View.GONE
        re_vehicle = findViewById(R.id.re_vehicle)
        re_vehicle?.setOnClickListener(this)
        re_guest = findViewById(R.id.re_guest)
        re_guest?.setOnClickListener(this)
        re_staff = findViewById(R.id.re_staff)
        re_resident = findViewById(R.id.re_resident)
        re_resident?.setOnClickListener(this)
        re_staff?.setOnClickListener(this)
        re_delivery = findViewById(R.id.re_delivery)
        re_delivery?.setOnClickListener(this)
        re_kidexit=findViewById(R.id.re_kidexit)
        re_kidexit?.setOnClickListener ( this )

        rv_dashboard = findViewById(R.id.rv_dashboard)
        rv_dashboard?.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )



        swipeContainer!!.setOnRefreshListener {


            if (!showingOutLog) {
                loadEntryVisitorLog()
            } else {
                loadExitVisitorLog(true)
            }
            swipeContainer!!.isRefreshing = false
        }

        Log.e("NOTIFICATION_SUB", "" + "AllGuards" + LocalDb.getAssociation()!!.asAssnID)
        FirebaseMessaging.getInstance()
            .subscribeToTopic("AllGuards" + LocalDb.getAssociation()!!.asAssnID)

        btn_mic.setOnClickListener(View.OnClickListener {
            Speak()

        })

        iv_help!!.setOnClickListener {
//            val intent = Intent(Intent.ACTION_CALL)
//            intent.data = Uri.parse("tel:" + "9343121121")
//            startActivity(intent)

            ZeotelRetrofitClinet.instance.getCall("KI_3t1wBwDQ2odmnvIclEdg-1391508276", "4000299","8431901841","AGENTNUMBER=8333833448","60","json")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : CommonDisposable<GetCallResponse>() {

                    override fun onSuccessResponse(getdata: GetCallResponse) {

                    }

                    override fun onErrorResponse(e: Throwable) {

                    }

                    override fun noNetowork() {
                        Toast.makeText(this@Dashboard, "No network call ", Toast.LENGTH_LONG).show()
                    }
                })


        }

        btn_in.setOnClickListener {

            showingOutLog = false
            clearSearchText()
            Prefs.putString("BUTTON", "IN")
            btn_in.setBackgroundColor(resources.getColor(R.color.orange))
            btn_out.setBackgroundColor(resources.getColor(R.color.grey))

            val visitorLog = VisitorLogRepo.get_IN_VisitorLog()
            if (visitorLog != null) {

                newAl = visitorLog
                // LocalDb.saveAllVisitorLog(newAl);
                if ((newAl)!!.isEmpty()) {
                    rv_dashboard!!.visibility = View.GONE
                    tv_nodata.visibility = View.VISIBLE
                    dismissProgressrefresh()
                } else {
                    rv_dashboard!!.visibility = View.VISIBLE
                    tv_nodata.visibility = View.GONE
                }

                if (vistorOutListAdapter == null) {
                    visitorEntryListAdapter = VisitorEntryListAdapter(newAl!!, this@Dashboard)
                } else {
                    visitorEntryListAdapter?.setVisitorLog(newAl)
                }
                rv_dashboard?.adapter = visitorEntryListAdapter
                btn_in.setBackgroundColor(resources.getColor(R.color.orange))
                btn_out.setBackgroundColor(resources.getColor(R.color.grey))
                dismissProgress()

            } else {
                rv_dashboard!!.visibility = View.GONE
                tv_nodata.visibility = View.VISIBLE
                dismissProgress()


            }


        }
        btn_out.setOnClickListener {

            showingOutLog = true
            clearSearchText()
            Prefs.putString("BUTTON", "OUT")
            btn_in.setBackgroundColor(resources.getColor(R.color.grey))
            btn_out.setBackgroundColor(resources.getColor(R.color.orange))
            loadExitVisitorLog(true)
        }




//        if (!Prefs.getBoolean(BG_NOTIFICATION_ON, false)) {
//            startService(Intent(this@Dashboard, BGService::class.java))
//        }


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

//    internal fun getLatestSubscription() {
//
//        val call = champApiInterface?.getLatestSubscription(Prefs.getInt(ASSOCIATION_ID, 0).toString())
//        call?.enqueue(object : Callback<SubscriptionResponse> {
//            override fun onResponse(call: Call<SubscriptionResponse>, response: Response<SubscriptionResponse>) {
//
//                try {
//                    if (response.body()!!.getSuccess() == true) {
//                        val dateFormat_DMY = SimpleDateFormat("dd-MM-yyyy")
//                        val CurrentString = response.body()!!.data.getSubscription().sueDate
//                        val separated =
//                            CurrentString.split("T".toRegex()).dropLastWhile { it.isEmpty() }
//                                .toTypedArray()
//                        subscriptionDate = separated[0]
//
//                        tv_subscriptiondate?.text = "Valid till: $subscriptionDate"
//                        //  if(PrefManager.getValidityDate().length()>0) {
//                        try {
//                            val dt_dwnld_date =
//                                dateFormat_DMY.parse(response.body()!!.data.getSubscription().sueDate)
//                            val c1 = Calendar.getInstance()
//                            c1.time = dt_dwnld_date
//
//                            val days =
//                                (c1.timeInMillis - System.currentTimeMillis()) / (24 * 60 * 60 * 1000) + 1
//
//                            if (0 < days && days <= 7) {
//                                val alertDialog =
//                                    android.app.AlertDialog.Builder(this@Dashboard)
//                                alertDialog.setTitle("Your Association Subscription Expires in $days days")
//                                alertDialog.setPositiveButton(
//                                    "Ok"
//                                ) { dialog, which -> dialog.cancel() }
//                                // Showing Alert Message
//                                if (!this@Dashboard.isFinishing) {
//                                    alertDialog.show()
//                                }
//                            }
//
//                        } catch (ex: Exception) {
//
//                        }
//
//                    } else {
//                    }
//                } catch (e: KotlinNullPointerException) {
//
//                }
//            }
//
//            override fun onFailure(call: Call<SubscriptionResponse>, t: Throwable) {
//                call.cancel()
//            }
//        })
//
//    }

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


    fun getDeviceList(AssnID: Int) {
        RetrofitClinet.instance.getDeviceListResponse(OYE247TOKEN, AppUtils.intToString(AssnID))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CommonDisposable<getDeviceList>() {

                override fun onSuccessResponse(deviceListResponse: getDeviceList) {


                    Log.e("WORKEESS", deviceListResponse.data.toString())
                    if (deviceListResponse.data.deviceListByAssocID != null) {
                        Prefs.putInt("TOTAL_GUARDS", deviceListResponse.data.deviceListByAssocID.size)


                        val arrayList = deviceListResponse.data.deviceListByAssocID

                        if (arrayList.size == 1 || arrayList.size == 0) {
                            Prefs.putString(WALKIETALKIE, "OFF")
                            Log.e("Device List", arrayList.size.toString())
                        } else {
                            Prefs.putString(WALKIETALKIE, "ON")
                            Log.e("Device List", arrayList.size.toString())
                        }


                    } else {
                        Prefs.putInt("TOTAL_GUARDS", 1)

                    }
                }

                override fun onErrorResponse(e: Throwable) {


                    Log.d("Error WorkerList", e.toString())

                }

                override fun noNetowork() {

                }
            })
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
            return true
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
            return true
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

        audioclip = audiofile.toString()

        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), audiofile)
        val body = MultipartBody.Part.createFormData("Test", audioclip, requestFile)
        val apiService = ImageApiClient.getImageClient().create(ImageApiInterface::class.java)
        val call = apiService.updateImageProfile(body)

        call.enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: retrofit2.Response<Any>) {
                try {
                    addWalkieTalkieAudioFirebase(response.body().toString())

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

    private fun startRecording() {

        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        audiofile = File.createTempFile("AudioRecording", ".3gp", dir)

        myAudioRecorder = MediaRecorder()
        myAudioRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        myAudioRecorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        myAudioRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        myAudioRecorder?.setOutputFile(audiofile?.absolutePath)
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
            myAudioRecorder?.setMaxDuration(50 * 1000)
            myAudioRecorder!!.release()
            // Toast.makeText(applicationContext, "Recording Stopped", Toast.LENGTH_LONG).show()
            uploadAudio()
            myAudioRecorder = null
        }

        //record.isEnabled = true
        // }, 5000)
        try {
            if (mp.isPlaying) {
                mp.stop()
                mp.release()
                mp = MediaPlayer.create(this@Dashboard, R.raw.walkietalkiestop)
            }
            mp.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        walk2?.visibility = View.GONE
        walk1?.visibility = View.VISIBLE

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
                DialogInterface.OnClickListener { dialogInterface: DialogInterface, i: Int ->
                    fun onClick(dialog: DialogInterface, which: Int) {
                        // do want you want to do here
                        Toast.makeText(this@Dashboard, "Coming", Toast.LENGTH_LONG).show()
                        onDestroy()

                    }
                })
            .setNegativeButton(android.R.string.no,
                DialogInterface.OnClickListener { dialogInterface: DialogInterface, i: Int ->
                    fun onClick(
                        dialog: DialogInterface,
                        which: Int
                    ) {
                    }
                }).show()

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
    fun getVisitorByWorkerId(
        assnID: Int,
        workerID: Int,
        unitId: String,
        personName: String,
        mobileNumb: String,
        desgn: String,
        workerType: String,
        staffID: Int,
        unitName: String,
        wkEntryImg: String
    ) {

        // showToast(this@Dashboard,assnID.toString()+".."+workerID+"..."+personName)
        RetrofitClinet.instance.getVisitorByWorkerId(OYE247TOKEN, workerID, assnID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CommonDisposable<getVisitorDataByWorker>() {

                override fun onSuccessResponse(getdata: getVisitorDataByWorker) {

                    if (getdata.success == true) {

                        t1?.speak("No Match Found", TextToSpeech.QUEUE_FLUSH, null)

                    }
                }

                override fun onErrorResponse(e: Throwable) {

                    // showToast(this@Dashboard,"false")
                    //showToast(this@Dashboard,workerID.toString()+"-"+unitId+"-"+personName+"-"+mobileNumb+"-"+desgn+"-"+workerType+"-"+staffID+"-"+unitName)
                    Log.e("onErrorResponse", "" + personName)
                    visitorLogBiometric(
                        unitId,
                        personName,
                        mobileNumb,
                        desgn,
                        workerType,
                        staffID,
                        unitName,
                        wkEntryImg
                    )


                }

                override fun noNetowork() {
                    Toast.makeText(this@Dashboard, "No network call ", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun visitorLogBiometric(
        unitId: String, personName: String, mobileNumb: String, desgn: String,
        workerType: String, staffID: Int, unitName: String, wkEntryImg: String
    ) {

        val req = CreateVisitorLogReq(
            Prefs.getInt(ASSOCIATION_ID, 0), staffID.toInt(), unitName,
            unitId, desgn, personName,
            LocalDb.getAssociation()!!.asAsnName, 0, "", mobileNumb, "1", "", "", "",
            1, "Staff Biometric Entry", "", "", "", "", ""
            , "", "", "", "", "", "", wkEntryImg, Prefs.getString(GATE_NO, ""), getCurrentTimeLocal(),
            "", "", "", "", "", "", "", "", "", "",""
        )

        Log.d("CreateVisitorLogResp", "StaffEntry destination " + req.toString())

        compositeDisposable.add(
            RetrofitClinet.instance.createVisitorLogCall(OYE247TOKEN, req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : CommonDisposable<CreateVisitorLogResp<VLRData>>() {
                    override fun onSuccessResponse(globalApiObject: CreateVisitorLogResp<VLRData>) {
                        if (globalApiObject.success == true) {

                            t1?.speak("Welcome $personName", TextToSpeech.QUEUE_FLUSH, null)

                            val visitorLogID = globalApiObject.data.visitorLog.vlVisLgID

                            updateFirebaseColor(visitorLogID, "#f0f0f0")
                            loadEntryVisitorLog()

                            if (unitId.contains(",")) {

                                var unitname_dataList: Array<String>
                                var unitid_dataList: Array<String>

                                unitname_dataList =
                                    unitName.split(",".toRegex())
                                        .dropLastWhile({ it.isEmpty() })
                                        .toTypedArray()
                                unitid_dataList =
                                    unitId.split(",".toRegex())
                                        .dropLastWhile({ it.isEmpty() })
                                        .toTypedArray()
                                // unitAccountId_dataList=intent.getStringExtra(UNIT_ACCOUNT_ID).split(",".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                                if (unitid_dataList.isNotEmpty()) {
                                    for (i in 0 until unitid_dataList.size) {

                                        val ddc = Intent(
                                            this@Dashboard,
                                            BackgroundSyncReceiver::class.java
                                        )
                                        ddc.putExtra(
                                            ConstantUtils.BSR_Action,
                                            ConstantUtils.VisitorEntryFCM
                                        )
                                        ddc.putExtra(
                                            "msg",
                                            "$personName $desgn " + " is coming to your home" + "(" + unitname_dataList.get(
                                                i
                                            ).replace(" ", "") + ")"
                                        )
                                        ddc.putExtra("mobNum", mobileNumb)
                                        ddc.putExtra("name", personName)
                                        ddc.putExtra("nr_id", visitorLogID.toString())
                                        ddc.putExtra(
                                            "unitname",
                                            unitname_dataList.get(i).replace(" ", "")
                                        )
                                        ddc.putExtra("memType", "Owner")
                                        ddc.putExtra(
                                            UNITID,
                                            unitid_dataList.get(i).replace(" ", "")
                                        )
                                        ddc.putExtra(COMPANY_NAME, "Staff")
                                        ddc.putExtra(UNIT_ACCOUNT_ID, unAccountID)
                                        ddc.putExtra("VLVisLgID", visitorLogID)
                                        ddc.putExtra(VISITOR_TYPE, desgn)
                                        sendBroadcast(ddc)
                                    }
                                }
                            } else {
                                val ddc = Intent(this@Dashboard, BackgroundSyncReceiver::class.java)
                                ddc.putExtra(
                                    ConstantUtils.BSR_Action,
                                    ConstantUtils.VisitorEntryFCM
                                )
                                ddc.putExtra(
                                    "msg",
                                    "$personName" + " is coming to your home" + "(" + unitName + ")"
                                )
                                ddc.putExtra("mobNum", mobileNumb)
                                ddc.putExtra("name", personName)
                                ddc.putExtra("nr_id", visitorLogID.toString())
                                ddc.putExtra("unitname", unitName)
                                ddc.putExtra("memType", "Owner")
                                ddc.putExtra(UNITID, unitId)
                                ddc.putExtra(COMPANY_NAME, "Staff")
                                ddc.putExtra(UNIT_ACCOUNT_ID, unAccountID)
                                ddc.putExtra("VLVisLgID", visitorLogID)
                                ddc.putExtra(VISITOR_TYPE, desgn)
                                sendBroadcast(ddc)
                            }

                            val intentAction1 =
                                Intent(applicationContext, BackgroundSyncReceiver::class.java)
                            intentAction1.putExtra(BSR_Action, SENDFCM_toSYNC_VISITORENTRY)
                            sendBroadcast(intentAction1)


                        } else {
                            Utils.showToast(applicationContext, globalApiObject.apiVersion)
                        }
                    }

                    override fun onErrorResponse(e: Throwable) {
                        Utils.showToast(applicationContext, getString(R.string.some_wrng))
                    }

                    override fun noNetowork() {
                        Utils.showToast(applicationContext, getString(R.string.no_internet))
                    }

                    override fun onShowProgress() {

                    }

                    override fun onDismissProgress() {

                    }
                })
        )
    }

    private fun getUnitLog(
        unitId: Int, personName: String, mobileNumb: String, desgn: String,
        workerType: String, staffID: Int, unitName: String, vlVisLgID: Int
    ) {

        RetrofitClinet.instance
            .getUnitListbyUnitId("1FDF86AF-94D7-4EA9-8800-5FBCCFF8E5C1", unitId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CommonDisposable<UnitlistbyUnitID>() {

                override fun onSuccessResponse(UnitList: UnitlistbyUnitID) {

                    if (UnitList.success == true) {

                        if (UnitList.data.unit.unOcStat.equals("Sold Owner Occupied Unit")) {
                            unAccountID = UnitList.data.unit.owner[0].acAccntID.toString()
                        } else if (UnitList.data.unit.unOcStat.equals("Sold Tenant Occupied Unit")) {

                            unAccountID = UnitList.data.unit.tenant[0].acAccntID.toString()

                        } else if (UnitList.data.unit.unOcStat.equals("UnSold Tenant Occupied Unit")) {

                            unAccountID = UnitList.data.unit.tenant[0].acAccntID.toString()

                        } else if (UnitList.data.unit.unOcStat.equals("UnSold Vacant Unit")) {
                            unAccountID = "0"

                        } else if (UnitList.data.unit.unOcStat.equals("Sold Vacant Unit")) {
                            unAccountID = UnitList.data.unit.owner[0].acAccntID.toString()
                        } else {

                        }

                        Toast.makeText(this@Dashboard, unAccountID, Toast.LENGTH_LONG).show()


                        val ddc = Intent(this@Dashboard, BackgroundSyncReceiver::class.java)
                        ddc.putExtra(ConstantUtils.BSR_Action, ConstantUtils.VisitorEntryFCM)
                        ddc.putExtra(
                            "msg",
                            "$personName" + " is coming to your home" + "(" + unitName + ")"
                        )
                        ddc.putExtra("mobNum", mobileNumb)
                        ddc.putExtra("name", personName)
                        ddc.putExtra("nr_id", vlVisLgID.toString())
                        ddc.putExtra("unitname", unitName)
                        ddc.putExtra("memType", "Owner")
                        ddc.putExtra(UNITID, unitId.toString())
                        ddc.putExtra(COMPANY_NAME, desgn)
                        ddc.putExtra(UNIT_ACCOUNT_ID, unAccountID)
                        ddc.putExtra("VLVisLgID", vlVisLgID)
//                        intent.getStringExtra("msg"),intent.getStringExtra("mobNum"),
//                        intent.getStringExtra("name"),intent.getStringExtra("nr_id"),
//                        intent.getStringExtra("unitname"),intent.getStringExtra("memType")
                        sendBroadcast(ddc)


//                        val d  =  Intent(this@Dashboard,BackgroundSyncReceiver::class.java)
//                        d.putExtra(BSR_Action, VisitorEntryFCM)
//                        d.putExtra("msg", "$personName $desgn "+" is coming to your home"+"("+unitName+")")
//                        d.putExtra("mobNum", mobileNumb)
//                        d.putExtra("name", personName)
//                        d.putExtra("nr_id", "0")
//                        d.putExtra("unitname",unitName)
//                        d.putExtra("memType", "Owner")
//                        d.putExtra(UNITID,unitId.toString())
//                        d.putExtra(COMPANY_NAME,"Staff")
//                        d.putExtra(UNIT_ACCOUNT_ID,"0")
//                        d.putExtra("VLVisLgID",0)
//                        sendBroadcast(d);


                    } else {
                    }
                }

                override fun onErrorResponse(e: Throwable) {
                    Log.d("cdvd", e.message)


                }

                override fun noNetowork() {

                }
            })

    }

    private fun clearSearchText() {

        tv.removeTextChangedListener(textWatcher)
        tv.setText("")
        tv.addTextChangedListener(textWatcher)

    }

    fun Speak() {


        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "say something")

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_SPEECH_INPUT -> {
                if (resultCode == Activity.RESULT_OK && null != data) {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    tv.setText(result[0].replace(" ", "").trim())


                }
            }
        }
    }

    class LogOutTimerTask : TimerTask() {
        val context: Context? = null

        override fun run() {

            var i: Intent = context!!.packageManager
                .getLaunchIntentForPackage(context.packageName)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(i)


        }
    }

    fun getSubscriptionData() {
        RetrofitClinet.instance.getSubscriptionData(OYE247TOKEN, LocalDb.getAssociation()!!.asAssnID.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CommonDisposable<SubscriptionResp>() {

                override fun onSuccessResponse(getdata: SubscriptionResp) {

                    //   CheckDates(getdata.data.subscription.sueDate,getCurrentTimeLocal(),this@Dashboard)


                }

                override fun onErrorResponse(e: Throwable) {
                    // visitorLog(unitId, personName, mobileNumb, desgn, workerType, staffID, unitName,wkEntryImg)
                    //  visitorLogBiometric(unitId, personName, mobileNumb, desgn, workerType, staffID, unitName,wkEntryImg)


                }

                override fun noNetowork() {
                    Toast.makeText(this@Dashboard, "No network call ", Toast.LENGTH_LONG).show()
                }
            })


    }

}




