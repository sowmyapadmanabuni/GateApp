package com.oyespace.guards.activity

//For registering finger print. Wired

import SecuGen.FDxSDKPro.*
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.*
import android.content.res.Resources
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.PowerManager
import android.speech.tts.TextToSpeech
import android.support.v7.app.AppCompatActivity
import android.util.Base64
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.*
import com.oyespace.guards.*
import com.oyespace.guards.constants.PrefKeys
import com.oyespace.guards.network.ResponseHandler
import com.oyespace.guards.network.RestClient
import com.oyespace.guards.network.URLData
import com.oyespace.guards.request.FingerPrintCreateReq
import com.oyespace.guards.responce.FingerPrintCreateResp
import com.oyespace.guards.utils.ConstantUtils
import com.oyespace.guards.utils.ConstantUtils.*
import com.oyespace.guards.utils.LocalDb
import com.oyespace.guards.utils.Prefs
import com.oyespace.guards.utils.Utils.isEmpty
import com.oyespace.guards.utils.Utils.showToast
import kotlinx.android.synthetic.main.layout_viewpager_iem.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.URL
import java.nio.ByteBuffer
import java.sql.Blob
import java.util.*
import kotlin.math.absoluteValue


class Biometric : AppCompatActivity(), ResponseHandler, View.OnClickListener, Runnable, SGFingerPresentEvent {

    var result:Long ?= null
    lateinit var txt_assn_name:TextView
    lateinit var txt_gate_name:TextView
    lateinit var txt_device_name:TextView
    internal var finger_type = ""
    internal var MemberType = "Regular"
    internal var key_left_thumb = "left_thumb"
    internal var key_left_index = "left_index"
    internal var key_left_centre = "left_centre"
    internal var key_left_ring = "left_ring"
    internal var key_left_small = "left_little"

    internal var key_right_thumb = "right_thumb"
    internal var key_right_index = "right_index"
    internal var key_right_centre = "right_centre"
    internal var key_right_ring = "right_ring"
    internal var key_right_small = "right_little"

    //  private var mButtonRegister: Button? = null
    private var mButtonRegister1: Button? = null
    private var mButtonRegister2: Button? = null
    private var mButtonRegister3: Button? = null
    private var save: Button? = null
    private var next: Button? = null
    private var change: Button? = null
    private var previous: Button? = null
    private var buttonDone: Button? = null
    private var mTextViewResult: android.widget.TextView? = null
    private var mToggleButtonAutoOn: android.widget.ToggleButton? = null
    private var mPermissionIntent: PendingIntent? = null
    private var mImageFingerprint1: ImageView? = null
    private var mImageFingerprint2: ImageView? = null
    private var mImageFingerprint3: ImageView? = null
    private var relLayout1: RelativeLayout? = null
    private var relLayout2: RelativeLayout? = null
    private var relLayout3: RelativeLayout? = null

    private var copy1 = false
    private var copy2 = false
    private var copy3 = false
    private var mLed: Boolean = false
    private var mAutoOnEnabled: Boolean = false
    private var bSecuGenDeviceOpened: Boolean = false
    private var usbPermissionRequested: Boolean = false

    private lateinit var mFingerprint1Template: ByteArray
    private  lateinit var mFingerprint2Template: ByteArray
    private lateinit var mFingerprint3Template: ByteArray
    private var mRegisterImage: ByteArray? = null
    private  var mRegisterTemplate: ByteArray? =null
    private var mMaxTemplateSize: IntArray? = null
    private var mImageWidth: Int = 0
    private var mImageHeight: Int = 0
    private var mImageDPI: Int = 0
    private  lateinit var grayBuffer: IntArray
    private var grayBitmap: Bitmap? = null
    private var filter: IntentFilter? = null //2014-04-11
    private var autoOn: SGAutoOnEventNotifier? = null
    private var nCaptureModeN: Int = 0
    private var fingerId = 0
    private var memId = 0
    lateinit var dbh: DataBaseHelper

    lateinit var t1: TextToSpeech
    // private var sgfplib: JSGFPLib? = null
    lateinit var sgfplib: JSGFPLib
    internal var existInDB = BooleanArray(1)
    internal var tempFP: ByteArray? = null


    lateinit var fingerDetails: TextView//080 42074082
    lateinit var left_thumb: ImageView
    lateinit var left_index: ImageView
    lateinit var left_middle: ImageView
    lateinit var left_ring: ImageView
    lateinit var left_small: ImageView
    lateinit var right_thumb: ImageView
    lateinit var right_index: ImageView
    lateinit var right_middle: ImageView
    lateinit var right_ring: ImageView
    lateinit var right_small: ImageView
    lateinit var btn_left_thumb: Button
    lateinit var btn_left_index: Button
    lateinit var btn_left_middle: Button
    lateinit var btn_left_ring: Button
    lateinit var btn_left_small: Button
    lateinit var btn_right_thumb: Button
    lateinit var btn_right_index: Button
    lateinit var btn_right_middle: Button
    lateinit var btn_right_ring: Button
    lateinit var btn_right_small: Button

    internal var bl_left_thumb = false
    internal var bl_left_index = false
    internal var bl_left_middle = false
    internal var bl_left_ring = false
    internal var bl_left_little = false
    internal var bl_right_thumb = false
    internal var bl_right_index = false
    internal var bl_right_middle = false
    internal var bl_right_ring = false
    internal var bl_right_little = false
    private val mUsbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            //Log.d(TAG,"Enter mUsbReceiver.onReceive()");
            if (ACTION_USB_PERMISSION == action) {
                synchronized(this) {
                    val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                        } else
                            Log.e(TAG, "mUsbReceiver.onReceive() Device is null")
                    } else
                        Log.e(TAG, "mUsbReceiver.onReceive() permission denied for device " + device!!)
                }
            }

            if (UsbManager.ACTION_USB_DEVICE_ATTACHED == action) {
                // Log.d("p22g","connectUsb");
                onResume()
                sgfplib = JSGFPLib(getSystemService(Context.USB_SERVICE) as UsbManager)
                bSecuGenDeviceOpened = false
                usbPermissionRequested = false

                //        // debugMessage("Starting Activity\n");
                // debugMessage("jnisgfplib version: " + Integer.toHexString((int)sgfplib.Version()) + "\n");
                mLed = false
                mAutoOnEnabled = false
                // autoOn = new SGAutoOnEventNotifier (sgfplib, this);
                nCaptureModeN = 0
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED == action) {
                DisableControls()
            }

//            Toast.makeText(
//                applicationContext,
//                "Inside " + action!!, Toast.LENGTH_SHORT
//            ).show()
        }


    }

    var fingerDetectedHandler: Handler = object : Handler() {
        // @Override
        override fun handleMessage(msg: Message) {
            Toast.makeText(this@Biometric, "capture finger" , Toast.LENGTH_LONG).show()

            //Handle the message
            CaptureFingerPrint()
            if (mAutoOnEnabled) {
                mToggleButtonAutoOn!!.toggle()
                EnableControls()
            }
        }
    }

    internal var i = 0

    fun EnableControls() {
        //this.mButtonRegister!!.isClickable = true
        //this.mButtonRegister!!.setTextColor(resources.getColor(android.R.color.white))
        this.mButtonRegister1!!.isClickable = true
        this.mButtonRegister1!!.setTextColor(resources.getColor(android.R.color.white))
        this.mButtonRegister2!!.isClickable = true
        this.mButtonRegister2!!.setTextColor(resources.getColor(android.R.color.white))
        this.mButtonRegister3!!.isClickable = true
        this.mButtonRegister3!!.setTextColor(resources.getColor(android.R.color.white))
    }

    fun DisableControls() {
        // this.mButtonRegister!!.visibility = View.INVISIBLE
        // this.mButtonRegister!!.setTextColor(resources.getColor(android.R.color.black))
        this.mButtonRegister1!!.visibility = View.INVISIBLE
        this.mButtonRegister1!!.setTextColor(resources.getColor(android.R.color.black))
        this.mButtonRegister2!!.visibility = View.INVISIBLE
        this.mButtonRegister2!!.setTextColor(resources.getColor(android.R.color.black))
        this.mButtonRegister3!!.visibility = View.INVISIBLE
        this.mButtonRegister3!!.setTextColor(resources.getColor(android.R.color.black))
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("btn_biometric", "bf setContentView")
        setLocale(Prefs.getString(PrefKeys.LANGUAGE, null))

        setContentView(R.layout.activity_register_finger_print)
        Log.d("btn_biometric", "af setContentView")
        txt_assn_name=findViewById(R.id.txt_assn_name)
        txt_gate_name=findViewById(R.id.txt_gate_name)
        txt_device_name=findViewById(R.id.txt_device_name)

        dbh = DataBaseHelper(applicationContext)
        txt_assn_name.text = "Society: " + LocalDb.getAssociation()!!.asAsnName
        txt_gate_name.text = "Gate No: " + Prefs.getString(ConstantUtils.GATE_NO, "")
        try {
            var appVersion = ""
            val manager = baseContext.packageManager
            val info = manager.getPackageInfo(baseContext.packageName, 0)
            appVersion = info.versionName
            Log.d("tag", "app " + appVersion + " " + info.versionName)
            txt_device_name.text = "V: $appVersion"

        } catch (ex: Exception) {
            ex.printStackTrace()
            txt_device_name.text = " "

        }
        //        prefManager=new PrefManager(getApplicationContext());

        //        getSupportActionBar().setTitle("Finger Print Registration");
        //        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        left_thumb = findViewById(R.id.left_thumb)
        left_index = findViewById(R.id.left_index)
        left_middle = findViewById(R.id.left_centre)
        left_ring = findViewById(R.id.left_ring)
        left_small = findViewById(R.id.left_small)

        right_thumb = findViewById(R.id.right_thumb)
        right_index = findViewById(R.id.right_index)
        right_middle = findViewById(R.id.right_centre)
        right_ring = findViewById(R.id.right_ring)
        right_small = findViewById(R.id.right_small)


        btn_left_thumb = findViewById(R.id.btn_left_thumb)
        btn_left_index = findViewById(R.id.btn_left_index)
        btn_left_middle = findViewById(R.id.btn_left_middle)
        btn_left_ring = findViewById(R.id.btn_left_ring)
        btn_left_small = findViewById(R.id.btn_left_small)

        btn_right_thumb = findViewById(R.id.btn_right_thumb)
        btn_right_index = findViewById(R.id.btn_right_index)
        btn_right_middle = findViewById(R.id.btn_right_centre)
        btn_right_ring = findViewById(R.id.btn_right_ring)
        btn_right_small = findViewById(R.id.btn_right_little)

        save = findViewById(R.id.buttonSaveToDB)
        save!!.visibility = View.INVISIBLE
        next = findViewById(R.id.buttonNext)
        previous = findViewById(R.id.buttonPrevious)
        buttonDone = findViewById(R.id.buttonDone)


        t1 = TextToSpeech(applicationContext, TextToSpeech.OnInitListener { status ->
            if (status != TextToSpeech.ERROR)
                t1.language = Locale.getDefault()
        })

        memId = intent.getIntExtra(WORKER_ID, 0)
        val ddc = Intent(this@Biometric, BackgroundSyncReceiver::class.java)
        Log.d("btn_biometric", "memId $memId")

        ddc.putExtra(BSR_Action, SYNC_STAFF_BIOMETRIC)
        ddc.putExtra("ID", memId)
        sendBroadcast(ddc)

        fingerDetails = findViewById(R.id.txt_member)
        // MemberType=getIntent().getStringExtra("memberType");

        if (intent.getIntExtra("memId", 0) != 0) {
            fingerDetails.text = "Register" + intent.getStringExtra(PERSONNAME)
            // +getIntent().getStringExtra("finger"));

        } else {
            //            finish();
            //            ((TextView) findViewById(R.id.txt_member)).setText("memId");
        }

        //        showToast(this,  "create fmid " + memId);

        //mButtonRegister = findViewById(R.id.buttonRegister)
        mButtonRegister1 = findViewById(R.id.buttonRegister1)
        mButtonRegister2 = findViewById(R.id.buttonRegister2)
        mButtonRegister3 = findViewById(R.id.buttonRegister3)

        if (dbh.fingercount(memId) == 9) {
            //
            next!!.visibility = View.INVISIBLE
            //  mButtonRegister!!.visibility = View.INVISIBLE
            mButtonRegister1!!.visibility = View.INVISIBLE
            mButtonRegister2!!.visibility = View.INVISIBLE
            mButtonRegister3!!.visibility = View.INVISIBLE
            Toast.makeText(this, "All fingers are registerd", Toast.LENGTH_SHORT).show()
            highlightFingers()
        } else {
            selectedFinger()
        }

        //        selectedFinger();

        //  mButtonRegister!!.setOnClickListener(this)
        mButtonRegister1!!.setOnClickListener(this)
        mButtonRegister2!!.setOnClickListener(this)
        mButtonRegister3!!.setOnClickListener(this)
        mTextViewResult = findViewById(R.id.textViewResult)

        mToggleButtonAutoOn = findViewById(R.id.toggleButtonAutoOn)
        mToggleButtonAutoOn!!.setOnClickListener(this)

        mImageFingerprint1 = findViewById(R.id.imageFingerprint1)
        mImageFingerprint2 = findViewById(R.id.imageFingerprint2)
        mImageFingerprint3 = findViewById(R.id.imageFingerprint3)

        relLayout1 = findViewById(R.id.layout_fp1_done)
        relLayout2 = findViewById(R.id.layout_fp2_done)
        relLayout3 = findViewById(R.id.layout_fp3_done)

        grayBuffer = IntArray(JSGFPLib.MAX_IMAGE_WIDTH_ALL_DEVICES * JSGFPLib.MAX_IMAGE_HEIGHT_ALL_DEVICES)
        for (i in grayBuffer!!.indices)
            grayBuffer[i] = Color.GRAY//getResources().getColor(R.color.google_light);
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

        mImageFingerprint1!!.setImageBitmap(grayBitmap)
        mImageFingerprint2!!.setImageBitmap(grayBitmap)
        mImageFingerprint3!!.setImageBitmap(grayBitmap)

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
        registerReceiver(mUsbReceiver, filter)

        //       	registerReceiver(mUsbReceiver, filter);
        sgfplib = JSGFPLib(getSystemService(Context.USB_SERVICE) as UsbManager)
        bSecuGenDeviceOpened = false
        usbPermissionRequested = false

        //        // debugMessage("Starting Activity\n");
        // debugMessage("jnisgfplib version: " + Integer.toHexString((int)sgfplib.Version()) + "\n");
        mLed = false
        mAutoOnEnabled = false
        autoOn = SGAutoOnEventNotifier(sgfplib, this)
        nCaptureModeN = 0

    }

    override fun onBackPressed() {
        /*
        if(dbh.fingercount(memId)>=4) {
*/

        super.onBackPressed()


//        if(::mFingerprint1Template.isInitialized and ::mFingerprint2Template.isInitialized and :: mFingerprint3Template.isInitialized){
//            mFingerprint1Template
//            mFingerprint2Template
//            mFingerprint3Template
//            mImageFingerprint1!!.setImageBitmap(grayBitmap)
//            mImageFingerprint2!!.setImageBitmap(grayBitmap)
//            mImageFingerprint3!!.setImageBitmap(grayBitmap)
//
//        }

        val d = Intent(this@Biometric, Dashboard::class.java)
        startActivity(d)
        finish()

        /*}
        else {
            if (bSecuGenDeviceOpened == true) {
                new android.support.v7.app.AlertDialog.Builder(RegisterFingerPrint.this)
                        .setTitle("Finger Print Registration")
                        .setMessage("Please register four finger")
                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })

                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                //open activity
                            }
                        }).create().show();
                Toast.makeText(this, "Please register four finger", Toast.LENGTH_SHORT).show();
            }
            else{
                new android.support.v7.app.AlertDialog.Builder(RegisterFingerPrint.this)
                        .setTitle("Finger Print Registration")
                        .setMessage("Please connect biometric device 1")
                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })

                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                //open activity
                            }
                        }).create().show();
                Toast.makeText(this, "Please connect biometric device3", Toast.LENGTH_SHORT).show();
            }

        }*/

    }


    public override fun onPause() {


        if (bSecuGenDeviceOpened) {
            autoOn!!.stop()
            EnableControls()
            sgfplib!!.CloseDevice()
            bSecuGenDeviceOpened = false
        }
        unregisterReceiver(mUsbReceiver)
        if(::mFingerprint1Template.isInitialized and ::mFingerprint2Template.isInitialized and :: mFingerprint3Template.isInitialized){
            mFingerprint1Template
            mFingerprint2Template
            mFingerprint3Template
            mImageFingerprint1!!.setImageBitmap(grayBitmap)
            mImageFingerprint2!!.setImageBitmap(grayBitmap)
            mImageFingerprint3!!.setImageBitmap(grayBitmap)

        }
        mRegisterImage
        mRegisterTemplate

        //        mImageViewFingerprint.setImageBitmap(grayBitmap);
//        mImageFingerprint1!!.setImageBitmap(grayBitmap)
//        mImageFingerprint2!!.setImageBitmap(grayBitmap)
//        mImageFingerprint3!!.setImageBitmap(grayBitmap)
        //        mImageViewRegister.setImageBitmap(grayBitmap);

        super.onPause()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    public override fun onResume() {
        //Log.d(TAG, "onResume()");
        super.onResume()
        DisableControls()
        registerReceiver(mUsbReceiver, filter)
        try {
            var error = sgfplib!!.Init(SGFDxDeviceName.SG_DEV_AUTO)
            if (error != SGFDxErrorCode.SGFDX_ERROR_NONE) {
                val dlgAlert = AlertDialog.Builder(this)
                if (error == SGFDxErrorCode.SGFDX_ERROR_DEVICE_NOT_FOUND)
                    dlgAlert.setMessage("The attached fingerprint device is not supported on Android")
                else
                    dlgAlert.setMessage("Fingerprint device initialization failed!")
                dlgAlert.setTitle("SecuGen Fingerprint SDK")
                dlgAlert.setPositiveButton("OK",
                    DialogInterface.OnClickListener { dialog, whichButton ->
                        //                            finish();
                        return@OnClickListener
                    }
                )
                dlgAlert.setCancelable(false)
                //            dlgAlert.create().show();
                android.support.v7.app.AlertDialog.Builder(this@Biometric)
                    .setTitle("Finger Print Registration")
                    .setMessage("Please connect biometric device2")
                    .setNeutralButton("OK") { dialog, which -> dialog.dismiss() }

                    .setOnDismissListener {
                        //open activity
                    }.create().show()

            } else {
                val usbDevice = sgfplib!!.GetUsbDevice()
                if (usbDevice == null) {
                    val dlgAlert = AlertDialog.Builder(this)
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
                    if (!hasPermission) {
                        if (!usbPermissionRequested) {
                            // debugMessage("Requesting USB Permission\n");
                            //Log.d(TAG, "Call GetUsbManager().requestPermission()");
                            usbPermissionRequested = true
                            sgfplib!!.GetUsbManager().requestPermission(usbDevice, mPermissionIntent)
                        } else {
                            //wait up to 20 seconds for the system to grant USB permission
                            hasPermission = sgfplib!!.GetUsbManager().hasPermission(usbDevice)
                            // debugMessage("Waiting for USB Permission\n");
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
                        // debugMessage("Opening SecuGen Device\n");
                        error = sgfplib!!.OpenDevice(0)
                        // debugMessage("OpenDevice() ret: " + error + "\n");
                        if (error == SGFDxErrorCode.SGFDX_ERROR_NONE) {
                            bSecuGenDeviceOpened = true
                            val deviceInfo = SecuGen.FDxSDKPro.SGDeviceInfoParam()
                            error = sgfplib!!.GetDeviceInfo(deviceInfo)
                            // debugMessage("GetDeviceInfo() ret: " + error + "\n");
                            mImageWidth = deviceInfo.imageWidth
                            mImageHeight = deviceInfo.imageHeight
                            mImageDPI = deviceInfo.imageDPI
                            // debugMessage("Image width: " + mImageWidth + "\n");
                            // debugMessage("Image height: " + mImageHeight + "\n");
                            // debugMessage("Image resolution: " + mImageDPI + "\n");
                            // debugMessage("Serial Number: " + new String(deviceInfo.deviceSN()) + "\n");
                            sgfplib!!.SetTemplateFormat(SGFDxTemplateFormat.TEMPLATE_FORMAT_SG400)
                            sgfplib!!.GetMaxTemplateSize(mMaxTemplateSize)
                            // debugMessage("TEMPLATE_FORMAT_SG400 SIZE: " + mMaxTemplateSize[0] + "\n");
                            mRegisterTemplate = ByteArray(mMaxTemplateSize!![0])
                            mFingerprint1Template = ByteArray(mMaxTemplateSize!![0])
                            mFingerprint2Template = ByteArray(mMaxTemplateSize!![0])
                            mFingerprint3Template = ByteArray(mMaxTemplateSize!![0])

                            EnableControls()
                            sgfplib!!.WriteData(SGFDxConstant.WRITEDATA_COMMAND_ENABLE_SMART_CAPTURE, 1.toByte())
                            if (mAutoOnEnabled) {
                                autoOn!!.start()
                                DisableControls()
                            }
                        } else {
                            // debugMessage("Waiting for USB Permission\n");
                        }
                    }
                    //Thread thread = new Thread(this);
                    //thread.start();
                    //   mButtonRegister.setVisibility(View.VISIBLE);
                    mButtonRegister1!!.visibility = View.VISIBLE
                    mButtonRegister2!!.visibility = View.VISIBLE
                    mButtonRegister3!!.visibility = View.VISIBLE
                }
            }
        } catch (ex: Exception) {
            Toast.makeText(applicationContext, "Connect Secugen Correctly", Toast.LENGTH_SHORT).show()
        }

    }

    public override fun onDestroy() {
        //Log.d(TAG, "onDestroy()");

        if (bSecuGenDeviceOpened) {
            autoOn!!.stop()
            //            EnableControls();
            sgfplib!!.CloseDevice()
            bSecuGenDeviceOpened = false
        }
        // sgfplib.CloseDevice();

        if(::mFingerprint1Template.isInitialized and ::mFingerprint2Template.isInitialized and :: mFingerprint3Template.isInitialized){
            mFingerprint1Template
            mFingerprint2Template
            mFingerprint3Template
            mImageFingerprint1!!.setImageBitmap(grayBitmap)
            mImageFingerprint2!!.setImageBitmap(grayBitmap)
            mImageFingerprint3!!.setImageBitmap(grayBitmap)

        }
        mRegisterImage
        mRegisterTemplate
//        mFingerprint1Template
//        mFingerprint2Template
//        mFingerprint3Template
        // sgfplib.Close();
        //    	unregisterReceiver(mUsbReceiver);
        super.onDestroy()
    }

    //Converts image to grayscale (NEW)
    fun toGrayscale(mImageBuffer: ByteArray?): Bitmap {
        val Bits = ByteArray(mImageBuffer!!.size * 4)
        for (i in mImageBuffer.indices) {
            Bits[i * 4 + 2] = mImageBuffer[i]
            Bits[i * 4 + 1] = Bits[i * 4 + 2]
            Bits[i * 4] = Bits[i * 4 + 1] // Invert the source bits
            Bits[i * 4 + 1] = mImageBuffer[i]
            Bits[i * 4] = Bits[i * 4 + 1]
            Bits[i * 4 + 2] = -1 // Invert the source bits //trial 4
            //            Bits[i * 4+1] = Bits[i * 4 + 2]  = mImageBuffer[i]; Bits[i * 4 ] = -1; // Invert the source bits //trial 2
            //            Bits[i * 4] = Bits[i * 4 + 2]  = mImageBuffer[i]; Bits[i * 4 + 1] = -1; // Invert the source bits //trial 3
            Bits[i * 4 + 3] = -1// 0xff, that's the alpha.

        }

        val bmpGrayscale = Bitmap.createBitmap(mImageWidth, mImageHeight, Bitmap.Config.ARGB_4444)
        //Bitmap bm contains the fingerprint img
        bmpGrayscale.copyPixelsFromBuffer(ByteBuffer.wrap(Bits))
        return bmpGrayscale
    }

    override fun SGFingerPresentCallback() {
        autoOn!!.stop()
        fingerDetectedHandler.sendMessage(Message())
    }

    fun CaptureFingerPrint() {
        //        this.mCheckBoxMatched.setChecked(false);

        var buffer: ByteArray? = ByteArray(mImageWidth * mImageHeight)

        //commented val result = sgfplib!!.GetImage(buffer)
        //        if (this.mToggleButtonNFIQ.isChecked()) {
        //            long nfiq = sgfplib.ComputeNFIQ(buffer, mImageWidth, mImageHeight);

        //        mImageViewFingerprint.setImageBitmap(this.toGrayscale(buffer));

        buffer = null

    }



    private fun imageToBitmap(image: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 90, stream)

        return stream.toByteArray()
    }
    override fun onClick(v: View) {

        try {
            if (v === this.mButtonRegister1) {
                if (mRegisterImage != null) {
                    mRegisterImage = null
                    Toast.makeText(this@Biometric, "mRegisterImageNull" + mRegisterImage, Toast.LENGTH_LONG).show()
                }

                mRegisterImage = ByteArray(mImageWidth * mImageHeight)
                previous = findViewById(R.id.buttonPrevious)
                previous!!.visibility = View.INVISIBLE
                next!!.visibility = View.INVISIBLE
                save!!.visibility = View.VISIBLE




//            try {
//
////               var res: Resources = getResources();
////                var drawable: Drawable = res.getDrawable(R.drawable.ic_launcher_background);
////               var  bitmap:Bitmap  = ((BitmapDrawable)drawable).getBitmap();
//
////                var url: URL = URL("https://via.placeholder.com/300/09f/fff.png");
////                val image: Bitmap  = BitmapFactory.decodeStream(url.openConnection().getInputStream());
////                mRegisterImage = byteArrayOf(81, 80, 79, 0x00, 79, 0x80);//imageToBitmap(bitmap)
//            } catch(e: IOException) {
//                System.out.println(e);
//            }

                // sgfplib.SetLedOn(true)


                result = sgfplib!!.GetImageEx(mRegisterImage, 10000, 50)

                if (result.toString() == "52" || result.toString() == "0" || result.toString().equals(0)) {
                     // Toast.makeText(this@Biometric, " ElseD: " + result.toString(), Toast.LENGTH_LONG).show()
                }

                if(result.toString().equals("54"))
                {
                    //  Toast.makeText(this@Biometric, " Blue: " + result.toString(), Toast.LENGTH_LONG).show()

                    //  t1.speak("Try Again", TextToSpeech.QUEUE_FLUSH, null)

//                    mImageFingerprint1!!.visibility = View.INVISIBLE
//                    mImageFingerprint1!!.visibility = View.VISIBLE
//                    mButtonRegister1!!.visibility = View.VISIBLE
                }

                var fpInfo: SGFingerInfo? = SGFingerInfo()


                for (i in mRegisterTemplate!!.indices)
                    mRegisterTemplate!![i] = 0
                //            result = sgfplib.CreateTemplate(fpInfo, mRegisterImage, mRegisterTemplate);

                if (copy1 == false) {
                    mImageFingerprint1!!.setImageBitmap(this.toGrayscale(mRegisterImage))
                    for (i in mFingerprint1Template!!.indices)
                        mFingerprint1Template[i] = 0
                    result = sgfplib!!.CreateTemplate(fpInfo, mRegisterImage, mFingerprint1Template)

                    relLayout1!!.visibility = View.VISIBLE
                    change = findViewById(R.id.btn_delete_fp1)
                    change!!.visibility = View.VISIBLE
                    change = findViewById(R.id.btn_delete_fp2)
                    change!!.visibility = View.INVISIBLE
                    change = findViewById(R.id.btn_delete_fp3)
                    change!!.visibility = View.INVISIBLE
                    mButtonRegister1!!.visibility = View.INVISIBLE
                    t1.speak("Thank You", TextToSpeech.QUEUE_FLUSH, null)

                    copy1 = true
                }

                Log.d("size  3", result.toString() + " " + mRegisterImage!!.size)
                Log.d("size  4", result.toString() + " " + mRegisterTemplate!!.size)

                mRegisterImage = null
                fpInfo = null

            }
        }
        catch (e:NullPointerException){
            //  Toast.makeText(this@Biometric, " Value: " + e, Toast.LENGTH_LONG).show()
           // Toast.makeText(this@Biometric, " Catch: " + result.toString(), Toast.LENGTH_LONG).show()

            val d = Intent(this, Biometric::class.java)
            intent.getIntExtra(WORKER_ID, 0)
            d.putExtra(PERSONNAME, getIntent().getStringExtra(PERSONNAME))
            d.putExtra(UNITID, getIntent().getStringExtra(UNITID))
            d.putExtra(UNITNAME, getIntent().getStringExtra(UNITNAME))
            d.putExtra(FLOW_TYPE, getIntent().getStringExtra(FLOW_TYPE))
            d.putExtra(VISITOR_TYPE, getIntent().getStringExtra(VISITOR_TYPE))
            d.putExtra(COMPANY_NAME, getIntent().getStringExtra(COMPANY_NAME))
            d.putExtra(MOBILENUMBER, getIntent().getStringExtra(MOBILENUMBER))
            d.putExtra(COUNTRYCODE, getIntent().getStringExtra(COUNTRYCODE))
            startActivity(d)

            //  Relaunching the biometric page
//                   val intent  = Intent(this, Biometric::class.java)
//                    startActivity(intent);
//                    finish()


            // Restart the APP
//            var i:Intent  = getBaseContext().getPackageManager()
//                         .getLaunchIntentForPackage( getBaseContext().getPackageName() );
//            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(i);


// To Reboot
//          var pm: PowerManager = this.getApplicationContext().getSystemService(Context.POWER_SERVICE) as PowerManager;
//pm.reboot(null);
        }

        try {
            if (v === this.mButtonRegister2) {
                if (mRegisterImage != null)
                    mRegisterImage = null
                mRegisterImage = ByteArray(mImageWidth * mImageHeight)
                previous = findViewById(R.id.buttonPrevious)
                previous!!.visibility = View.INVISIBLE
                next!!.visibility = View.INVISIBLE
                save!!.visibility = View.VISIBLE

                var result = sgfplib!!.GetImageEx(mRegisterImage,10000,50)


                  Toast.makeText(this@Biometric, " D: " + sgfplib.SetLedOn(false), Toast.LENGTH_LONG).show()

                if(result.toString() == "52" || result.toString() == "" || result.toString().equals("0")||result.toString()=="0")
                {
                    // Toast.makeText(this@Biometric, " ElseD: " + sgfplib.SetLedOn(false), Toast.LENGTH_LONG).show()
                    //  t1.speak("Try Again", TextToSpeech.QUEUE_FLUSH, null)

                }
                Log.d("size  1", result.toString() + " " + mRegisterImage!!.size)

                //            result = sgfplib.SetTemplateFormat(SecuGen.FDxSDKPro.SGFDxTemplateFormat.TEMPLATE_FORMAT_SG400);
                Log.d("size  2", result.toString() + " " + mRegisterImage!!.size)

                var fpInfo: SGFingerInfo? = SGFingerInfo()
                for (i in mRegisterTemplate!!.indices)
                    mRegisterTemplate!![i] = 0
                //            result = sgfplib.CreateTemplate(fpInfo, mRegisterImage, mRegisterTemplate);

                if (copy2 == false) {

                    for (i in mFingerprint2Template!!.indices)
                        mFingerprint2Template[i] = 0
                    result = sgfplib!!.CreateTemplate(fpInfo, mRegisterImage, mFingerprint2Template)

                    var existInDB1 = BooleanArray(1)
                    existInDB1 = BooleanArray(1)
                    val res: Long
                    res = sgfplib!!.MatchTemplate(
                        mFingerprint1Template,
                        mFingerprint2Template,
                        SGFDxSecurityLevel.SL_LOWEST,
                        existInDB1
                    )

                    if (existInDB1[0]) {
                        t1.speak("Please change finger angle and retry", TextToSpeech.QUEUE_FLUSH, null)
                    } else {
                        mTextViewResult!!.text = "MATCHED!!\n"
                        //                    this.mCheckBoxMatched.setChecked(true);
                        mImageFingerprint2!!.setImageBitmap(this.toGrayscale(mRegisterImage))
                        //                    Bitmap waterMarkedPhoto1 = BitmapFactory.decodeByteArray(mFingerprint2Template, 0, mFingerprint2Template.length);
                        //                    mImageFingerprint1.setImageBitmap(waterMarkedPhoto1);

                        relLayout2!!.visibility = View.VISIBLE
                        t1.speak("Thank You", TextToSpeech.QUEUE_FLUSH, null)
                        change = findViewById(R.id.btn_delete_fp2)
                        change!!.visibility = View.VISIBLE
                        change = findViewById(R.id.btn_delete_fp1)
                        change!!.visibility = View.INVISIBLE
                        change = findViewById(R.id.btn_delete_fp3)
                        change!!.visibility = View.INVISIBLE
                        mButtonRegister2!!.visibility = View.INVISIBLE
                        copy2 = true
                    }
//                } else {
//                    t1.speak(" Not match", TextToSpeech.QUEUE_FLUSH, null)
//                }

                }

                Log.d("size  3", result.toString() + " " + mRegisterImage!!.size)
                Log.d("size  4", result.toString() + " " + mRegisterTemplate!!.size)

                mRegisterImage = null
                fpInfo = null

            }
        }
        catch (e:Exception)

        {
            e.printStackTrace()
            //FingerImage 2
            //  Toast.makeText(this@Biometric, " Value: " + e, Toast.LENGTH_LONG).show()
            //t1.speak("Try Again", TextToSpeech.QUEUE_FLUSH, null)
        }

        try {
            if (v === this.mButtonRegister3) {
                if (mRegisterImage != null)
                    mRegisterImage = null
                mRegisterImage = ByteArray(mImageWidth * mImageHeight)
                previous = findViewById(R.id.buttonPrevious)
                previous!!.visibility = View.INVISIBLE
                next!!.visibility = View.INVISIBLE
                save!!.visibility = View.VISIBLE

                var result = sgfplib!!.GetImageEx(mRegisterImage,10000,50)

              //  Toast.makeText(this@Biometric, " D: " + sgfplib.SetLedOn(true), Toast.LENGTH_LONG).show()


                if(result.toString() == "52"||result.toString()=="0"||result.toString()==""||result.toString().equals(0))
                {
                    // Toast.makeText(this@Biometric, " ElseD: " + sgfplib.SetLedOn(false), Toast.LENGTH_LONG).show()
                    // t1.speak("Try Again", TextToSpeech.QUEUE_FLUSH, null)

                }
                Log.d("size  1", result.toString() + " " + mRegisterImage!!.size)
                Log.d("size  2", result.toString() + " " + mRegisterImage!!.size)

                var fpInfo: SGFingerInfo? = SGFingerInfo()
                for (i in mRegisterTemplate!!.indices)
                    mRegisterTemplate!![i] = 0

                if (copy3 == false) {

                    for (i in mFingerprint3Template!!.indices)
                        mFingerprint3Template[i] = 0
                    result = sgfplib!!.CreateTemplate(fpInfo, mRegisterImage, mFingerprint3Template)



                    val existInDB1 = BooleanArray(1)
                    val res: Long
                    res = sgfplib!!.MatchTemplate(
                        mFingerprint2Template,
                        mFingerprint3Template,
                        SGFDxSecurityLevel.SL_LOWEST,
                        existInDB1
                    )
                    if (existInDB1[0]) {
                        t1.speak("Please change finger angle and retry", TextToSpeech.QUEUE_FLUSH, null)
                    } else {


                        mTextViewResult!!.text = "MATCHED!!\n"
                        //                    this.mCheckBoxMatched.setChecked(true);
                        mImageFingerprint3!!.setImageBitmap(this.toGrayscale(mRegisterImage))
                        relLayout3!!.visibility = View.VISIBLE
                        t1.speak("Thank You", TextToSpeech.QUEUE_FLUSH, null)
                        change = findViewById(R.id.btn_delete_fp3)
                        change!!.visibility = View.VISIBLE
                        change = findViewById(R.id.btn_delete_fp1)
                        change!!.visibility = View.INVISIBLE
                        change = findViewById(R.id.btn_delete_fp2)
                        change!!.visibility = View.INVISIBLE
                        //relLayout3.setVisibility(View.INVISIBLE);
                        mButtonRegister3!!.visibility = View.INVISIBLE
                        copy3 = true
                    }
                    // } else {
                    // t1.speak(" Not match", TextToSpeech.QUEUE_FLUSH, null)
                    // }

                }

                Log.d("size  3", result.toString() + " " + mRegisterImage!!.size)
                Log.d("size  4", result.toString() + " " + mRegisterTemplate!!.size)

                mRegisterImage = null
                fpInfo = null

            }
        }catch (e:Exception)
        {
            //Finger Image 3
            //  Toast.makeText(this@Biometric, " Value: " + e, Toast.LENGTH_LONG).show()
          //  t1.speak("Try Again", TextToSpeech.QUEUE_FLUSH, null)
        }

        if (v.id == R.id.btn_delete_fp1) {
            mImageFingerprint1!!.setImageBitmap(grayBitmap)
            for (i in mFingerprint1Template!!.indices)
                mFingerprint1Template[i] = 0
            // next=findViewById(R.id.buttonNext);
            next!!.visibility = View.VISIBLE
            previous!!.visibility = View.VISIBLE
            save!!.visibility = View.INVISIBLE

            relLayout1!!.visibility = View.INVISIBLE
            mButtonRegister1!!.visibility = View.VISIBLE

            copy1 = false
        } else if (v.id == R.id.btn_delete_fp2) {
            mImageFingerprint2!!.setImageBitmap(grayBitmap)
            for (i in mFingerprint2Template!!.indices)
                mFingerprint2Template[i] = 0

            relLayout2!!.visibility = View.INVISIBLE
            copy2 = false
            change = findViewById(R.id.btn_delete_fp1)
            change!!.visibility = View.VISIBLE
            mButtonRegister2!!.visibility = View.VISIBLE

        } else if (v.id == R.id.btn_delete_fp3) {
            mImageFingerprint3!!.setImageBitmap(grayBitmap)
            for (i in mFingerprint3Template!!.indices)
                mFingerprint3Template[i] = 0
            relLayout3!!.visibility = View.INVISIBLE
            copy3 = false
            change = findViewById(R.id.btn_delete_fp1)
            change!!.visibility = View.INVISIBLE
            change = findViewById(R.id.btn_delete_fp2)
            change!!.visibility = View.VISIBLE
            mButtonRegister3!!.visibility = View.VISIBLE

        }

        if (v.id == R.id.buttonSaveToDB) {
            if (copy1 == false) {
                Toast.makeText(applicationContext, "Take 1st finger print", Toast.LENGTH_SHORT).show()
                t1.speak("take 1st finger print", TextToSpeech.QUEUE_FLUSH, null)
            } else if (copy2 == false) {
                Toast.makeText(applicationContext, "Take 2nd finger print", Toast.LENGTH_SHORT).show()
                t1.speak("take 2nd finger print", TextToSpeech.QUEUE_FLUSH, null)
            } else if (copy3 == false) {
                Toast.makeText(applicationContext, "Take 3rd finger print", Toast.LENGTH_SHORT).show()
                t1.speak("take 3rd finger print", TextToSpeech.QUEUE_FLUSH, null)

            } else {

                t1.speak("Finger print data saved", TextToSpeech.QUEUE_FLUSH, null)
                //  Toast.makeText(this@Biometric,dbh.fingercount(memId).toString()+".."+fingerId.toString()+".."+mFingerprint1Template.toString()+".."+mFingerprint2Template+".."+mFingerprint3Template,Toast.LENGTH_LONG).show()

                uploadFingerPrint(mFingerprint1Template, mFingerprint2Template, mFingerprint3Template)

                next = findViewById(R.id.buttonNext)
                next!!.visibility = View.VISIBLE
                previous!!.visibility = View.VISIBLE
                save!!.visibility = View.INVISIBLE
                mButtonRegister1!!.visibility = View.VISIBLE
                mButtonRegister2!!.visibility = View.VISIBLE
                mButtonRegister3!!.visibility = View.VISIBLE



            }
        }

        if (v.id == R.id.buttonDone) {


            val d = Intent(this@Biometric, StaffDetails::class.java)
            d.putExtra(WORKER_ID, intent.getIntExtra(WORKER_ID, 0))
            d.putExtra(PERSONNAME, intent.getStringExtra(PERSONNAME))
            d.putExtra(UNITID, intent.getStringExtra(UNITID))
            d.putExtra(UNITNAME, intent.getStringExtra(UNITNAME))
            d.putExtra(FLOW_TYPE, intent.getStringExtra(FLOW_TYPE))
            d.putExtra(VISITOR_TYPE, intent.getStringExtra(VISITOR_TYPE))
            d.putExtra(COMPANY_NAME, intent.getStringExtra(COMPANY_NAME))
            d.putExtra(MOBILENUMBER, intent.getStringExtra(MOBILENUMBER))
            d.putExtra(COUNTRYCODE, intent.getStringExtra(COUNTRYCODE))
            startActivity(d)
            finish()
        }
        if (v.id == R.id.buttonNext) {
            if (copy1 == false && copy2 == false && copy3 == false) {
                fingerId++
                selectedFinger()

            } else {
                Toast.makeText(applicationContext, "Complete the Current selected Registration", Toast.LENGTH_SHORT)
                    .show()
                t1.speak("Complete the Current selected Registration", TextToSpeech.QUEUE_FLUSH, null)
            }
        }

        if (v.id == R.id.buttonPrevious) {
            if (copy1 == false && copy2 == false && copy3 == false) {
                fingerId--
                selectedFinger()

            } else {
                Toast.makeText(applicationContext, "Complete the Current selected Registration", Toast.LENGTH_SHORT)
                    .show()
                t1.speak("Complete the Current selected Registration", TextToSpeech.QUEUE_FLUSH, null)
            }
        }


    }


    private fun resetCapures() {
        mImageFingerprint1!!.setImageBitmap(grayBitmap)
        for (i in mFingerprint1Template!!.indices)
            mFingerprint1Template[i] = 0

        relLayout1!!.visibility = View.INVISIBLE
        copy1 = false

        mImageFingerprint2!!.setImageBitmap(grayBitmap)
        for (i in mFingerprint2Template!!.indices)
            mFingerprint2Template[i] = 0

        relLayout2!!.visibility = View.INVISIBLE
        copy2 = false

        mImageFingerprint3!!.setImageBitmap(grayBitmap)
        for (i in mFingerprint3Template!!.indices)
            mFingerprint3Template[i] = 0

        relLayout3!!.visibility = View.INVISIBLE
        copy3 = false

    }

    override fun onFailure(e: Exception, urlId: Int) {

        showToast(this, e.message + " id " + urlId)
    }

    override fun onSuccess(responce: String, data: Any, urlId: Int, position: Int) {
        try {
            if (urlId == URLData.URL_SAVE_FINGERPRINT.urlId) {
                val loginDetailsResponce = data as FingerPrintCreateResp
                if (loginDetailsResponce != null) {
                    Log.d(
                        "str3",
                        "str3: " + urlId + " id " + position + " " + memId + " " + MemberType + " " + loginDetailsResponce.success.toString()
                    )
                    if (loginDetailsResponce.success.equals("true", ignoreCase = true)) {
                        showToast(this, "Fingerprint Saved")
                        dbh.insertFingerPrints(
                            loginDetailsResponce.FingerPrint().fpid.toInt(),
                            memId.toString() + "",
                            finger_type,
                            mFingerprint1Template,
                            mFingerprint2Template,
                            mFingerprint3Template,
                            MemberType,
                            Prefs.getInt(ASSOCIATION_ID, 0)

                        )
                        selectedFinger()
                        resetCapures()
                    } else {
                        showToast(this, "Fingerprint not saved ")
                    }

                } else {
                    showToast(this, "Something went wrong . please try again ")
                }

            }
        }catch (e:java.lang.Exception){
            e.printStackTrace()
        }
        //  showToast(this, urlId+" id "+position+" "+memId+" "+MemberType+" ");

        //        finish();
    }

    fun uploadFingerPrint(byteArray_fp1: ByteArray, byteArray_fp2: ByteArray?, byteArray_fp3: ByteArray?) {

        val str1 = Base64.encodeToString(
            byteArray_fp1,
            Base64.DEFAULT
        )
        val str2 = Base64.encodeToString(
            byteArray_fp2,
            Base64.DEFAULT
        )
        val str3 = Base64.encodeToString(
            byteArray_fp3,
            Base64.DEFAULT
        )

        val restClient = RestClient.getInstance()
        val loginReq = FingerPrintCreateReq()
        loginReq.ASAssnID = Prefs.getInt(ASSOCIATION_ID, 0).toString() + ""
        loginReq.FMID = memId.toString() + ""
        loginReq.FPImg1 = str1
        loginReq.FPFngName = finger_type
        loginReq.FPImg2 = str2
        loginReq.FPImg3 = str3
        loginReq.FPMemType = MemberType

        Log.d("str3", "str3: $str3")



        restClient.addHeader(OYE247KEY, OYE247TOKEN)

        restClient.post<Any>(this, loginReq, FingerPrintCreateResp::class.java, this, URLData.URL_SAVE_FINGERPRINT)
        val response=FingerPrintCreateResp()
        // Toast.makeText(this@Biometric,response.toString(),Toast.LENGTH_LONG).show()

    }

    fun selectedFinger() {

        highlightFingers()

        right_thumb.visibility = View.INVISIBLE
        right_index.visibility = View.INVISIBLE
        right_middle.visibility = View.INVISIBLE
        right_ring.visibility = View.INVISIBLE
        right_small.visibility = View.INVISIBLE

        left_thumb.visibility = View.INVISIBLE
        left_index.visibility = View.INVISIBLE
        left_middle.visibility = View.INVISIBLE
        left_ring.visibility = View.INVISIBLE
        left_small.visibility = View.INVISIBLE

        if (fingerId % 10 == 0) {
            right_thumb.visibility = View.VISIBLE
            finger_type = key_right_thumb
            if (bl_right_thumb) {
                fingerId++
                // Toast.makeText(this@Biometric,"Finger Id: "+fingerId,Toast.LENGTH_LONG).show()

                selectedFinger()
            }
        } else if (fingerId % 10 == 1 || fingerId % 10 == -1) {
            right_index.visibility = View.VISIBLE
            finger_type = key_right_index
            if (bl_right_index) {
                fingerId++
                // Toast.makeText(this@Biometric,"Finger Id: "+fingerId,Toast.LENGTH_LONG).show()

                selectedFinger()
            }
        } else if (fingerId % 10 == 2 || fingerId % 10 == -2) {
            right_middle.visibility = View.VISIBLE
            finger_type = key_right_centre
            if (bl_right_middle) {
                fingerId++
                // Toast.makeText(this@Biometric,"Finger Id: "+fingerId,Toast.LENGTH_LONG).show()

                selectedFinger()
            }
        } else if (fingerId % 10 == 3 || fingerId % 10 == -3) {
            right_ring.visibility = View.VISIBLE
            finger_type = key_right_ring
            if (bl_right_ring) {
                fingerId++
                // Toast.makeText(this@Biometric,"Finger Id: "+fingerId,Toast.LENGTH_LONG).show()

                selectedFinger()
            }
        } else if (fingerId % 10 == 4 || fingerId % 10 == -4) {
            right_small.visibility = View.VISIBLE
            finger_type = key_right_small
            if (bl_right_little) {
                fingerId++
                //  Toast.makeText(this@Biometric,"Finger Id: "+fingerId,Toast.LENGTH_LONG).show()

                selectedFinger()
            }
        } else if (fingerId % 10 == 5 || fingerId % 10 == -5) {
            left_thumb.visibility = View.VISIBLE
            finger_type = key_left_thumb
            if (bl_left_thumb) {
                fingerId++
                //   Toast.makeText(this@Biometric,"Finger Id: "+fingerId,Toast.LENGTH_LONG).show()

                selectedFinger()
            }
        } else if (fingerId % 10 == 6 || fingerId % 10 == -6) {
            left_index.visibility = View.VISIBLE
            finger_type = key_left_index
            if (bl_left_index) {
                fingerId++
                // Toast.makeText(this@Biometric,"Finger Id: "+fingerId,Toast.LENGTH_LONG).show()

                selectedFinger()
            }
        } else if (fingerId % 10 == 7 || fingerId % 10 == -7) {
            left_middle.visibility = View.VISIBLE
            finger_type = key_left_centre
            if (bl_left_middle) {
                fingerId++
                // Toast.makeText(this@Biometric,"Finger Id: "+fingerId,Toast.LENGTH_LONG).show()

                selectedFinger()
            }
        } else if (fingerId % 10 == 8 || fingerId % 10 == -8) {
            left_ring.visibility = View.VISIBLE
            finger_type = key_left_ring
            if (bl_left_ring) {
                fingerId++
                // Toast.makeText(this@Biometric,"Finger Id: "+fingerId,Toast.LENGTH_LONG).show()

                selectedFinger()
            }
        } else if (fingerId % 10 == 9 || fingerId % 10 == -9) {
            left_small.visibility = View.VISIBLE
            finger_type = key_left_small
            if (bl_left_little) {
                fingerId++
                // Toast.makeText(this@Biometric,"Finger Id: "+fingerId,Toast.LENGTH_LONG).show()

                selectedFinger()
            }

        }
        var stFinger = ""
        if (!finger_type.contains("thumb")) {
            stFinger = " finger"
        }
        fingerDetails.text =
            "Register " + intent.getStringExtra(PERSONNAME) + "'s " + finger_type.replace("_", " ") + stFinger

    }

    fun highlightFingers() {

        bl_left_thumb = dbh.getMemberFingerExists(memId.toString() + "", "left_thumb")
        bl_left_index = dbh.getMemberFingerExists(memId.toString() + "", "left_index")
        bl_left_middle = dbh.getMemberFingerExists(memId.toString() + "", "left_centre")
        bl_left_ring = dbh.getMemberFingerExists(memId.toString() + "", "left_ring")
        bl_left_little = dbh.getMemberFingerExists(memId.toString() + "", "left_little")

        bl_right_thumb = dbh.getMemberFingerExists(memId.toString() + "", "right_thumb")
        bl_right_index = dbh.getMemberFingerExists(memId.toString() + "", "right_index")
        bl_right_middle = dbh.getMemberFingerExists(memId.toString() + "", "right_centre")
        bl_right_ring = dbh.getMemberFingerExists(memId.toString() + "", "right_ring")
        bl_right_little = dbh.getMemberFingerExists(memId.toString() + "", "right_little")

        if (bl_left_thumb)
            btn_left_thumb.alpha = 0.3.toFloat()
        if (bl_left_index)
            btn_left_index.alpha = 0.3.toFloat()
        if (bl_left_middle)
            btn_left_middle.alpha = 0.3.toFloat()
        if (bl_left_ring)
            btn_left_ring.alpha = 0.3.toFloat()
        if (bl_left_little)
            btn_left_small.alpha = 0.3.toFloat()

        if (bl_right_thumb)
            btn_right_thumb.alpha = 0.3.toFloat()
        if (bl_right_index)
            btn_right_index.alpha = 0.3.toFloat()
        if (bl_right_middle)
            btn_right_middle.alpha = 0.3.toFloat()
        if (bl_right_ring)
            btn_right_ring.alpha = 0.3.toFloat()
        if (bl_right_little)
            btn_right_small.alpha = 0.3.toFloat()

        // Toast.makeText(getApplicationContext(),"Hi there"+dbh.fingercount(memId),Toast.LENGTH_SHORT).show();
        // Toast.makeText(this@Biometric,dbh.fingercount(memId),Toast.LENGTH_LONG).show()

        if (dbh.fingercount(memId) > 1) {

            buttonDone!!.visibility = View.VISIBLE
        } else {
            buttonDone!!.visibility = View.GONE

        }

    }

    override fun run() {
        i++
        //Toast.makeText(getApplicationContext(),"Hi there"+i,Toast.LENGTH_SHORT).show();
    }

    fun clicked(view: View) {
        right_thumb.visibility = View.INVISIBLE
        right_index.visibility = View.INVISIBLE
        right_middle.visibility = View.INVISIBLE
        right_ring.visibility = View.INVISIBLE
        right_small.visibility = View.INVISIBLE

        left_thumb.visibility = View.INVISIBLE
        left_index.visibility = View.INVISIBLE
        left_middle.visibility = View.INVISIBLE
        left_ring.visibility = View.INVISIBLE
        left_small.visibility = View.INVISIBLE

        if (copy1 == false) {
            when (view.id) {

                R.id.btn_left_small -> {

                    left_small.visibility = View.VISIBLE
                    fingerId = 9
                    finger_type = key_left_small
                    if (bl_left_little) {
                        selectedFinger()

                    }
                }
                R.id.btn_left_ring -> {

                    left_ring.visibility = View.VISIBLE
                    fingerId = 8
                    finger_type = key_left_ring
                    if (bl_left_ring) {
                        selectedFinger()
                    }
                }
                R.id.btn_left_middle -> {

                    left_middle.visibility = View.VISIBLE
                    fingerId = 7
                    finger_type = key_left_centre
                    if (bl_left_middle) {
                        selectedFinger()
                    }
                }
                R.id.btn_left_index -> {

                    left_index.visibility = View.VISIBLE
                    fingerId = 6
                    finger_type = key_left_index
                    if (bl_left_index) {
                        selectedFinger()
                    }
                }
                R.id.btn_left_thumb -> {

                    left_thumb.visibility = View.VISIBLE
                    fingerId = 5
                    finger_type = key_left_thumb
                    if (bl_left_thumb) {
                        selectedFinger()
                    }
                }
                R.id.btn_right_thumb -> {

                    right_thumb.visibility = View.VISIBLE
                    fingerId = 0
                    finger_type = key_right_thumb
                    if (bl_right_thumb) {
                        selectedFinger()
                    }
                }
                R.id.btn_right_index -> {

                    right_index.visibility = View.VISIBLE
                    fingerId = 1
                    finger_type = key_right_index
                    if (bl_right_index) {
                        selectedFinger()
                    }
                }
                R.id.btn_right_centre -> {

                    right_middle.visibility = View.VISIBLE
                    fingerId = 2
                    finger_type = key_right_centre
                    if (bl_right_middle) {
                        selectedFinger()
                    }
                }
                R.id.btn_right_ring -> {

                    right_ring.visibility = View.VISIBLE
                    fingerId = 3
                    finger_type = key_right_ring
                    if (bl_right_ring) {
                        selectedFinger()
                    }
                }
                R.id.btn_right_little -> {

                    right_small.visibility = View.VISIBLE
                    fingerId = 4
                    finger_type = key_right_small
                    if (bl_right_little) {
                        selectedFinger()
                    }
                }
            }
        }
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

    companion object {

        private val TAG = "SecuGen USB"
        private val ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION"
    }



}
