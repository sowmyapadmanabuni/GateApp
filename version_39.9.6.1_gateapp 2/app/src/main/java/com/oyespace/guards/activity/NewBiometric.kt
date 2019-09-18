package com.oyespace.guards.activity

import SecuGen.Driver.Constant
import SecuGen.FDxSDKPro.*
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.*
import android.graphics.*
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.oyespace.guards.R
import java.io.IOException
import java.nio.ByteBuffer

class NewBiometric : AppCompatActivity(), View.OnClickListener, java.lang.Runnable, SGFingerPresentEvent {
    private val TAG = "SecuGen USB"

  //  private var mButtonCapture: Button? = null
     var mButtonRegister: Button? = null
   // private var mButtonMatch: Button? = null
  //  private var mButtonLed: Button? = null
    private var mSDKTest: Button? = null
    private var mEditLog: EditText? = null
    private var mTextViewResult: android.widget.TextView? = null
   // private var mCheckBoxMatched: android.widget.CheckBox? = null
 //   private var mToggleButtonSmartCapture: android.widget.ToggleButton? = null
    private var mToggleButtonCaptureModeN: android.widget.ToggleButton? = null
    private var mToggleButtonAutoOn: android.widget.ToggleButton? = null
    private var mToggleButtonNFIQ: android.widget.ToggleButton? = null
    private var mToggleButtonUSBBulkMode64: android.widget.ToggleButton? = null
    private var mPermissionIntent: PendingIntent? = null
  //  private var mImageViewFingerprint: ImageView? = null
    private var mImageViewRegister: ImageView? = null
  //  private var mImageViewVerify: ImageView? = null
    private var mRegisterImage: ByteArray? = null
    private var mVerifyImage: ByteArray? = null
    private var mRegisterTemplate: ByteArray? = null
    private var mVerifyTemplate: ByteArray? = null
    private var mMaxTemplateSize: IntArray? = null
    private var mImageWidth: Int = 0
    private var mImageHeight: Int = 0
    private var mImageDPI: Int = 0
    private var grayBuffer: IntArray? = null
    private var grayBitmap: Bitmap? = null
    private var filter: IntentFilter? = null //2014-04-11
    private var autoOn: SGAutoOnEventNotifier? = null
    private var mLed: Boolean = false
    private var mAutoOnEnabled: Boolean = false
    private var nCaptureModeN: Int = 0
   // private var mButtonSetBrightness0: Button? = null
  //  private var mButtonSetBrightness100: Button? = null
    private var mButtonReadSN: Button? = null
    private var bSecuGenDeviceOpened: Boolean = false
    private var sgfplib: JSGFPLib? = null
    private var usbPermissionRequested: Boolean = false


    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    private fun debugMessage(message: String) {
        this.mEditLog!!.append(message)
        this.mEditLog!!.invalidate() //TODO trying to get Edit log to update after each line written
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    //This broadcast receiver is necessary to get user permissions to access the attached USB device
    private val ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION"
    private val mUsbReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            //Log.d(TAG,"Enter mUsbReceiver.onReceive()");
            if (ACTION_USB_PERMISSION == action) {
                synchronized(this) {
                    val device = intent.getParcelableExtra<Parcelable>(UsbManager.EXTRA_DEVICE) as UsbDevice
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            //DEBUG Log.d(TAG, "Vendor ID : " + device.getVendorId() + "\n");
                            //DEBUG Log.d(TAG, "Product ID: " + device.getProductId() + "\n");
                            debugMessage("USB BroadcastReceiver VID : " + device.vendorId + "\n")
                            debugMessage("USB BroadcastReceiver PID: " + device.productId + "\n")
                        } else
                            Log.e(TAG, "mUsbReceiver.onReceive() Device is null")
                    } else
                        Log.e(TAG, "mUsbReceiver.onReceive() permission denied for device $device")
                }
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    //This message handler is used to access local resources not
    //accessible by SGFingerPresentCallback() because it is called by
    //a separate thread.
    var fingerDetectedHandler: Handler = object : Handler() {
        // @Override
        override fun handleMessage(msg: Message) {
            //Handle the message
            CaptureFingerPrint()
            if (mAutoOnEnabled) {
                mToggleButtonAutoOn!!.toggle()
                EnableControls()
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    fun EnableControls() {
//        this.mButtonCapture!!.setClickable(true)
//        this.mButtonCapture!!.setTextColor(getResources().getColor(android.R.color.white))
        this.mButtonRegister!!.setClickable(true)
        this.mButtonRegister!!.setTextColor(getResources().getColor(android.R.color.white))
//        this.mButtonMatch!!.setClickable(true)
//        this.mButtonMatch!!.setTextColor(getResources().getColor(android.R.color.white))
    //    mButtonSetBrightness0!!.isClickable = true
     //   mButtonSetBrightness100!!.isClickable = true
//        mButtonReadSN!!.isClickable = true
//        this.mButtonLed!!.setClickable(true)
//        this.mButtonLed!!.setTextColor(getResources().getColor(android.R.color.white))
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    fun DisableControls() {
//        this.mButtonCapture!!.setClickable(false)
//        this.mButtonCapture!!.setTextColor(getResources().getColor(android.R.color.black))
        this.mButtonRegister!!.setClickable(false)
        this.mButtonRegister!!.setTextColor(getResources().getColor(android.R.color.black))
//        this.mButtonMatch!!.setClickable(false)
      //  this.mButtonMatch!!.setTextColor(getResources().getColor(android.R.color.black))
//        mButtonSetBrightness0!!.isClickable = false
      //  mButtonSetBrightness100!!.isClickable = false
       // mButtonReadSN!!.isClickable = false
//        this.mButtonLed!!.setClickable(false)
//        this.mButtonLed!!.setTextColor(getResources().getColor(android.R.color.black))
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_biometric)
//        mButtonCapture = findViewById(R.id.buttonCapture) as Button
//        mButtonCapture!!.setOnClickListener(this)
        mButtonRegister = findViewById(R.id.buttonRegister1) as Button
        mButtonRegister!!.setOnClickListener(this)
//        mButtonMatch = findViewById(R.id.buttonMatch) as Button
//        mButtonMatch!!.setOnClickListener(this)
//        mButtonLed = findViewById(R.id.buttonLedOn) as Button
//        mButtonLed!!.setOnClickListener(this)
//        mSDKTest = findViewById(R.id.buttonSDKTest) as Button
//        mSDKTest!!.setOnClickListener(this)
        mEditLog = findViewById(R.id.editLog) as EditText
        mTextViewResult = findViewById(R.id.textViewResult) as android.widget.TextView
//        mCheckBoxMatched = findViewById(R.id.checkBoxMatched) as android.widget.CheckBox
//        mToggleButtonSmartCapture = findViewById(R.id.toggleButtonSmartCapture) as android.widget.ToggleButton
//        mToggleButtonSmartCapture!!.setOnClickListener(this)
//        mToggleButtonCaptureModeN = findViewById(R.id.toggleButtonCaptureModeN) as android.widget.ToggleButton
//        mToggleButtonCaptureModeN!!.setOnClickListener(this)
//        mToggleButtonAutoOn = findViewById(R.id.toggleButtonAutoOn) as android.widget.ToggleButton
//        mToggleButtonAutoOn!!.setOnClickListener(this)
//        mToggleButtonNFIQ = findViewById(R.id.toggleButtonNFIQ) as android.widget.ToggleButton
//        mToggleButtonNFIQ!!.setOnClickListener(this)
//        mToggleButtonUSBBulkMode64 = findViewById(R.id.ToggleButtonUSBBulkMode64) as android.widget.ToggleButton
//        mToggleButtonUSBBulkMode64!!.setOnClickListener(this)
//        mImageViewFingerprint = findViewById(R.id.imageViewFingerprint) as ImageView
        mImageViewRegister = findViewById(R.id.imageFingerprint1) as ImageView
//        mImageViewVerify = findViewById(R.id.imageViewVerify) as ImageView
//        mButtonSetBrightness0 = findViewById(R.id.buttonSetBrightness0) as Button
//        mButtonSetBrightness0!!.setOnClickListener(this)
//        mButtonSetBrightness100 = findViewById(R.id.buttonSetBrightness100) as Button
//        mButtonSetBrightness100!!.setOnClickListener(this)
//        mButtonSetBrightness0!!.isClickable = false
//        mButtonSetBrightness100!!.isClickable = false
//        mButtonSetBrightness0!!.setTextColor(getResources().getColor(android.R.color.black))
//        mButtonSetBrightness100!!.setTextColor(getResources().getColor(android.R.color.black))
//        mButtonReadSN = findViewById(R.id.buttonReadSN) as Button
    //    mButtonReadSN!!.setOnClickListener(this)

        grayBuffer = IntArray(JSGFPLib.MAX_IMAGE_WIDTH_ALL_DEVICES * JSGFPLib.MAX_IMAGE_HEIGHT_ALL_DEVICES)
        for (i in grayBuffer!!.indices)
            grayBuffer!![i] = android.graphics.Color.GRAY
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
      //  mImageViewFingerprint!!.setImageBitmap(grayBitmap)

        val sintbuffer =
            IntArray(JSGFPLib.MAX_IMAGE_WIDTH_ALL_DEVICES / 2 * (JSGFPLib.MAX_IMAGE_HEIGHT_ALL_DEVICES / 2))
        for (i in sintbuffer.indices)
            sintbuffer[i] = android.graphics.Color.GRAY
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
        mImageViewRegister!!.setImageBitmap(grayBitmap)
       // mImageViewVerify!!.setImageBitmap(grayBitmap)
        mMaxTemplateSize = IntArray(1)

        //USB Permissions
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, Intent(ACTION_USB_PERMISSION), 0)
        filter = IntentFilter(ACTION_USB_PERMISSION)
        //       	registerReceiver(mUsbReceiver, filter);
        sgfplib = JSGFPLib(getSystemService(Context.USB_SERVICE) as UsbManager)
     //   this.mToggleButtonSmartCapture!!.toggle()
        bSecuGenDeviceOpened = false
        usbPermissionRequested = false

        debugMessage("Starting Activity\n")
        debugMessage("jnisgfplib version: " + Integer.toHexString(sgfplib!!.Version().toInt()) + "\n")
        mLed = false
        mAutoOnEnabled = false
        autoOn = SGAutoOnEventNotifier(sgfplib, this)
        nCaptureModeN = 0

    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    override fun onPause() {
        //Log.d(TAG, "onPause()");
        if (bSecuGenDeviceOpened) {
            autoOn!!.stop()
            EnableControls()
            sgfplib!!.CloseDevice()
            bSecuGenDeviceOpened = false
        }
        unregisterReceiver(mUsbReceiver)
        mRegisterImage = null
        mVerifyImage = null
        mRegisterTemplate = null
        mVerifyTemplate = null
        //mImageViewFingerprint!!.setImageBitmap(grayBitmap)
        mImageViewRegister!!.setImageBitmap(grayBitmap)
        //mImageViewVerify!!.setImageBitmap(grayBitmap)
        super.onPause()
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    override fun onResume() {
        //Log.d(TAG, "onResume()");
        super.onResume()
        DisableControls()
        registerReceiver(mUsbReceiver, filter)
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
                    finish()
                    return@OnClickListener
                }
            )
            dlgAlert.setCancelable(false)
            dlgAlert.create().show()
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
                        debugMessage("Requesting USB Permission\n")
                        //Log.d(TAG, "Call GetUsbManager().requestPermission()");
                        usbPermissionRequested = true
                        sgfplib!!.GetUsbManager().requestPermission(usbDevice, mPermissionIntent)
                    } else {
                        //wait up to 20 seconds for the system to grant USB permission
                        hasPermission = sgfplib!!.GetUsbManager().hasPermission(usbDevice)
                        debugMessage("Waiting for USB Permission\n")
                        var i = 0
                        while (hasPermission == false && i <= 40) {
                            ++i
                            hasPermission = sgfplib!!.GetUsbManager().hasPermission(usbDevice)
                            try {
                                Thread.sleep(500)
                            } catch (e: InterruptedException) {
                                e.printStackTrace()
                            }

                            //Log.d(TAG, "Waited " + i*50 + " milliseconds for USB permission");
                        }
                    }
                }
                if (hasPermission) {
                    debugMessage("Opening SecuGen Device\n")
                    error = sgfplib!!.OpenDevice(0)
                    debugMessage("OpenDevice() ret: $error\n")
                    if (error == SGFDxErrorCode.SGFDX_ERROR_NONE) {
                        bSecuGenDeviceOpened = true
                        val deviceInfo = SecuGen.FDxSDKPro.SGDeviceInfoParam()
                        error = sgfplib!!.GetDeviceInfo(deviceInfo)
                        debugMessage("GetDeviceInfo() ret: $error\n")
                        mImageWidth = deviceInfo.imageWidth
                        mImageHeight = deviceInfo.imageHeight
                        mImageDPI = deviceInfo.imageDPI
                        debugMessage("Image width: $mImageWidth\n")
                        debugMessage("Image height: $mImageHeight\n")
                        debugMessage("Image resolution: $mImageDPI\n")
                        debugMessage("Serial Number: " + String(deviceInfo.deviceSN()) + "\n")
                        sgfplib!!.SetTemplateFormat(SGFDxTemplateFormat.TEMPLATE_FORMAT_SG400)
                        sgfplib!!.GetMaxTemplateSize(mMaxTemplateSize)
                        debugMessage("TEMPLATE_FORMAT_SG400 SIZE: " + mMaxTemplateSize!![0] + "\n")
                        mRegisterTemplate = ByteArray(mMaxTemplateSize!![0])
                        mVerifyTemplate = ByteArray(mMaxTemplateSize!![0])
                        EnableControls()
             //           val smartCaptureEnabled = this.mToggleButtonSmartCapture!!.isChecked()
//                        if (smartCaptureEnabled)
//                            sgfplib!!.WriteData(SGFDxConstant.WRITEDATA_COMMAND_ENABLE_SMART_CAPTURE, 1.toByte())
//                        else
//                            sgfplib!!.WriteData(SGFDxConstant.WRITEDATA_COMMAND_ENABLE_SMART_CAPTURE, 0.toByte())
//                        if (mAutoOnEnabled) {
//                            autoOn!!.start()
//                            DisableControls()
//                        }
                    } else {
                        debugMessage("Waiting for USB Permission\n")
                    }
                }
                //Thread thread = new Thread(this);
                //thread.start();
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    override fun onDestroy() {
        //Log.d(TAG, "onDestroy()");
        sgfplib!!.CloseDevice()
        mRegisterImage = null
        mVerifyImage = null
        mRegisterTemplate = null
        mVerifyTemplate = null
        sgfplib!!.Close()
        //    	unregisterReceiver(mUsbReceiver);
        super.onDestroy()
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    //Converts image to grayscale (NEW)
    fun toGrayscale(mImageBuffer: ByteArray, width: Int, height: Int): Bitmap {
        val Bits = ByteArray(mImageBuffer.size * 4)
        for (i in mImageBuffer.indices) {
            Bits[i * 4 + 2] = mImageBuffer[i]
            Bits[i * 4 + 1] = Bits[i * 4 + 2]
            Bits[i * 4] = Bits[i * 4 + 1] // Invert the source bits
            Bits[i * 4 + 3] = -1// 0xff, that's the alpha.
        }

        val bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        //Bitmap bm contains the fingerprint img
        bmpGrayscale.copyPixelsFromBuffer(ByteBuffer.wrap(Bits))
        return bmpGrayscale
    }


    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    //Converts image to grayscale (NEW)
    fun toGrayscale(mImageBuffer: ByteArray): Bitmap {
        val Bits = ByteArray(mImageBuffer.size * 4)
        for (i in mImageBuffer.indices) {
            Bits[i * 4 + 2] = mImageBuffer[i]
            Bits[i * 4 + 1] = Bits[i * 4 + 2]
            Bits[i * 4] = Bits[i * 4 + 1] // Invert the source bits
            Bits[i * 4 + 3] = -1// 0xff, that's the alpha.
        }

        val bmpGrayscale = Bitmap.createBitmap(mImageWidth, mImageHeight, Bitmap.Config.ARGB_8888)
        //Bitmap bm contains the fingerprint img
        bmpGrayscale.copyPixelsFromBuffer(ByteBuffer.wrap(Bits))
        return bmpGrayscale
    }


    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    //Converts image to grayscale (NEW)
    fun toGrayscale(bmpOriginal: Bitmap): Bitmap {
        val width: Int
        val height: Int
        height = bmpOriginal.height
        width = bmpOriginal.width
        val bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        for (y in 0 until height) {
            for (x in 0 until width) {
                var color = bmpOriginal.getPixel(x, y)
                val r = color shr 16 and 0xFF
                val g = color shr 8 and 0xFF
                val b = color and 0xFF
                val gray = (r + g + b) / 3
                color = Color.rgb(gray, gray, gray)
                //color = Color.rgb(r/3, g/3, b/3);
                bmpGrayscale.setPixel(x, y, color)
            }
        }
        return bmpGrayscale
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    //Converts image to binary (OLD)
    fun toBinary(bmpOriginal: Bitmap): Bitmap {
        val width: Int
        val height: Int
        height = bmpOriginal.height
        width = bmpOriginal.width
        val bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        val c = Canvas(bmpGrayscale)
        val paint = Paint()
        val cm = ColorMatrix()
        cm.setSaturation(0f)
        val f = ColorMatrixColorFilter(cm)
        paint.colorFilter = f
        c.drawBitmap(bmpOriginal, 0f, 0f, paint)
        return bmpGrayscale
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    fun DumpFile(fileName: String, buffer: ByteArray) {
        //Uncomment section below to dump images and templates to SD card
        /*
        try {
            File myFile = new File("/sdcard/Download/" + fileName);
            myFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(myFile);
            fOut.write(buffer,0,buffer.length);
            fOut.close();
        } catch (Exception e) {
            debugMessage("Exception when writing file" + fileName);
        }
       */
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    override fun SGFingerPresentCallback() {
        autoOn!!.stop()
        fingerDetectedHandler.sendMessage(Message())
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    fun CaptureFingerPrint() {
        var dwTimeStart: Long = 0
        var dwTimeEnd: Long = 0
        var dwTimeElapsed: Long = 0
      //  this.mCheckBoxMatched!!.setChecked(false)
        var buffer: ByteArray? = ByteArray(mImageWidth * mImageHeight)
        dwTimeStart = System.currentTimeMillis()
        //long result = sgfplib.GetImage(buffer);
        val result = sgfplib!!.GetImageEx(buffer, 10000, 50)
        val NFIQString: String
        if (this.mToggleButtonNFIQ!!.isChecked()) {
            val nfiq = sgfplib!!.ComputeNFIQ(buffer, mImageWidth.toLong(), mImageHeight.toLong())
            //long nfiq = sgfplib.ComputeNFIQEx(buffer, mImageWidth, mImageHeight,500);
            NFIQString = ("NFIQ=$nfiq")
        } else
            NFIQString = ""
        DumpFile("capture2016.raw", buffer!!)
        dwTimeEnd = System.currentTimeMillis()
        dwTimeElapsed = dwTimeEnd - dwTimeStart
        debugMessage("getImageEx(10000,50) ret:" + result + " [" + dwTimeElapsed + "ms]" + NFIQString + "\n")
        mTextViewResult!!.text =
            "getImageEx(10000,50) ret: " + result + " [" + dwTimeElapsed + "ms] " + NFIQString + "\n"
      //  mImageViewFingerprint!!.setImageBitmap(this.toGrayscale(buffer))

        buffer = null
    }


    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    override fun onClick(v: View) {
        var dwTimeStart: Long = 0
        var dwTimeEnd: Long = 0
        var dwTimeElapsed: Long = 0
//        if (v === mToggleButtonSmartCapture) {
//            if (mToggleButtonSmartCapture!!.isChecked) {
//                sgfplib!!.WriteData(
//                    SGFDxConstant.WRITEDATA_COMMAND_ENABLE_SMART_CAPTURE,
//                    1.toByte()
//                ) //Enable Smart Capture
////                this.mButtonSetBrightness0!!.setClickable(false)
////           //     this.mButtonSetBrightness100!!.setClickable(false)
////                this.mButtonSetBrightness0!!.setTextColor(getResources().getColor(android.R.color.black))
////                this.mButtonSetBrightness100!!.setTextColor(getResources().getColor(android.R.color.black))
//            } else {
//                sgfplib!!.WriteData(
//                    SGFDxConstant.WRITEDATA_COMMAND_ENABLE_SMART_CAPTURE,
//                    0.toByte()
//                ) //Disable Smart Capture
////                this.mButtonSetBrightness0!!.setClickable(true)
////                this.mButtonSetBrightness100!!.setClickable(true)
////                this.mButtonSetBrightness0!!.setTextColor(getResources().getColor(android.R.color.white))
////                this.mButtonSetBrightness100!!.setTextColor(getResources().getColor(android.R.color.white))
//            }
//        }
        if (v === mToggleButtonCaptureModeN) {
            if (mToggleButtonCaptureModeN!!.isChecked)
                sgfplib!!.WriteData(0.toByte(), 0.toByte()) //Enable Mode N
            else
                sgfplib!!.WriteData(0.toByte(), 1.toByte()) //Disable Mode N
        }
        if (v === mToggleButtonUSBBulkMode64) {
            if (mToggleButtonUSBBulkMode64!!.isChecked)
                sgfplib!!.WriteData(
                    Constant.WRITEDATA_COMMAND_ENABLE_USB_MODE_64,
                    1.toByte()
                ) //Enable 64byte USB bulk mode
            else
                sgfplib!!.WriteData(
                    Constant.WRITEDATA_COMMAND_ENABLE_USB_MODE_64,
                    0.toByte()
                ) //Enable 4096byte USB bulk mode
        }
        if (v === this.mButtonReadSN) {
            //Read Serial number
            var szSerialNumber: ByteArray? = ByteArray(15)
            val result = sgfplib!!.ReadSerialNumber(szSerialNumber)
            debugMessage("ReadSerialNumber() ret: " + result + " [" + (szSerialNumber) + "]\n")
            //Increment last byte and Write serial number
            //szSerialNumber[14] += 1;
            //error = sgfplib.WriteSerialNumber(szSerialNumber);
            szSerialNumber = null
        }
//        if (v === mButtonCapture) {
//            //DEBUG Log.d(TAG, "Pressed CAPTURE");
//            CaptureFingerPrint()
//        }
        if (v === mToggleButtonAutoOn) {
            if (mToggleButtonAutoOn!!.isChecked) {
                mAutoOnEnabled = true
                autoOn!!.start() //Enable Auto On
                DisableControls()
            } else {
                mAutoOnEnabled = false
                autoOn!!.stop() //Disable Auto On
                EnableControls()
            }

        }
//        if (v === mButtonLed) {
//            this.mCheckBoxMatched!!.setChecked(false)
//            mLed = !mLed
//            dwTimeStart = System.currentTimeMillis()
//            val result = sgfplib!!.SetLedOn(mLed)
//            dwTimeEnd = System.currentTimeMillis()
//            dwTimeElapsed = dwTimeEnd - dwTimeStart
//            debugMessage("setLedOn(" + mLed + ") ret:" + result + " [" + dwTimeElapsed + "ms]\n")
//            mTextViewResult!!.text = "setLedOn(" + mLed + ") ret: " + result + " [" + dwTimeElapsed + "ms]\n"
//        }
        if (v === mSDKTest) {
            SDKTest()
        }
        if (v === this.mButtonRegister) {

            try {

                //DEBUG Log.d(TAG, "Clicked REGISTER");
                debugMessage("Clicked REGISTER\n")
                if (mRegisterImage != null)
                    mRegisterImage = null
                mRegisterImage = ByteArray(mImageWidth * mImageHeight)

                //     this.mCheckBoxMatched!!.setChecked(false)
                dwTimeStart = System.currentTimeMillis()

                var result = sgfplib!!.GetImage(mRegisterImage)

                DumpFile("register.raw", mRegisterImage!!)
                dwTimeEnd = System.currentTimeMillis()
                dwTimeElapsed = dwTimeEnd - dwTimeStart
                debugMessage("GetImage() ret:" + result + " [" + dwTimeElapsed + "ms]\n")
                // mImageViewFingerprint!!.setImageBitmap(this.toGrayscale(mRegisterImage!!))
                dwTimeStart = System.currentTimeMillis()
                result = sgfplib!!.SetTemplateFormat(SecuGen.FDxSDKPro.SGFDxTemplateFormat.TEMPLATE_FORMAT_SG400)
                dwTimeEnd = System.currentTimeMillis()
                dwTimeElapsed = dwTimeEnd - dwTimeStart
                debugMessage("SetTemplateFormat(SG400) ret:" + result + " [" + dwTimeElapsed + "ms]\n")
                var fpInfo: SGFingerInfo? = SGFingerInfo()
                for (i in mRegisterTemplate!!.indices)
                    mRegisterTemplate!![i] = 0
                dwTimeStart = System.currentTimeMillis()
                result = sgfplib!!.CreateTemplate(fpInfo, mRegisterImage, mRegisterTemplate)
                DumpFile("register.min", mRegisterTemplate!!)
                dwTimeEnd = System.currentTimeMillis()
                dwTimeElapsed = dwTimeEnd - dwTimeStart
                debugMessage("CreateTemplate() ret:" + result + " [" + dwTimeElapsed + "ms]\n")
                mImageViewRegister!!.setImageBitmap(this.toGrayscale(mRegisterImage!!))
                mTextViewResult!!.text = "Click Verify"
                mRegisterImage = null
                fpInfo = null
            }catch (e:NullPointerException){
                Toast.makeText(this@NewBiometric, "HIII", Toast.LENGTH_LONG).show()

            }
        }
//        if (v === this.mButtonMatch) {
//            //DEBUG Log.d(TAG, "Clicked MATCH");
//            debugMessage("Clicked MATCH\n")
//            if (mVerifyImage != null)
//                mVerifyImage = null
//            mVerifyImage = ByteArray(mImageWidth * mImageHeight)
//            dwTimeStart = System.currentTimeMillis()
//            var result = sgfplib!!.GetImage(mVerifyImage)
//            DumpFile("verify.raw", mVerifyImage!!)
//            dwTimeEnd = System.currentTimeMillis()
//            dwTimeElapsed = dwTimeEnd - dwTimeStart
//            debugMessage("GetImage() ret:" + result + " [" + dwTimeElapsed + "ms]\n")
//            mImageViewFingerprint!!.setImageBitmap(this.toGrayscale(mVerifyImage!!))
//            mImageViewVerify!!.setImageBitmap(this.toGrayscale(mVerifyImage!!))
//            dwTimeStart = System.currentTimeMillis()
//            result = sgfplib!!.SetTemplateFormat(SecuGen.FDxSDKPro.SGFDxTemplateFormat.TEMPLATE_FORMAT_SG400)
//            dwTimeEnd = System.currentTimeMillis()
//            dwTimeElapsed = dwTimeEnd - dwTimeStart
//            debugMessage("SetTemplateFormat(SG400) ret:" + result + " [" + dwTimeElapsed + "ms]\n")
//            var fpInfo: SGFingerInfo? = SGFingerInfo()
//            for (i in mVerifyTemplate!!.indices)
//                mVerifyTemplate!![i] = 0
//            dwTimeStart = System.currentTimeMillis()
//            result = sgfplib!!.CreateTemplate(fpInfo, mVerifyImage, mVerifyTemplate)
//            DumpFile("verify.min", mVerifyTemplate!!)
//            dwTimeEnd = System.currentTimeMillis()
//            dwTimeElapsed = dwTimeEnd - dwTimeStart
//            debugMessage("CreateTemplate() ret:" + result + " [" + dwTimeElapsed + "ms]\n")
//            var matched: BooleanArray? = BooleanArray(1)
//            dwTimeStart = System.currentTimeMillis()
//            result = sgfplib!!.MatchTemplate(mRegisterTemplate, mVerifyTemplate, SGFDxSecurityLevel.SL_NORMAL, matched)
//            dwTimeEnd = System.currentTimeMillis()
//            dwTimeElapsed = dwTimeEnd - dwTimeStart
//            debugMessage("MatchTemplate() ret:" + result + " [" + dwTimeElapsed + "ms]\n")
//            if (matched!![0]) {
//                mTextViewResult!!.text = "MATCHED!!\n"
//                this.mCheckBoxMatched!!.setChecked(true)
//                debugMessage("MATCHED!!\n")
//            } else {
//                mTextViewResult!!.text = "NOT MATCHED!!"
//                this.mCheckBoxMatched!!.setChecked(false)
//                debugMessage("NOT MATCHED!!\n")
//            }
//            mVerifyImage = null
//            fpInfo = null
//            matched = null
//        }
//        if (v === this.mButtonSetBrightness0) {
//            this.sgfplib!!.SetBrightness(0)
//            debugMessage("SetBrightness(0)\n")
//        }
//        if (v === this.mButtonSetBrightness100) {
//            this.sgfplib!!.SetBrightness(100)
//            debugMessage("SetBrightness(100)\n")
//        }
    }


    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    private fun SDKTest() {
        mTextViewResult!!.text = ""
        debugMessage("\n###############\n")
        debugMessage("### SDK Test  ###\n")
        debugMessage("###############\n")

        val X_SIZE = 248
        val Y_SIZE = 292

        var error: Long = 0
        val sgTemplate1: ByteArray
        val sgTemplate2: ByteArray
        val sgTemplate3: ByteArray
        val ansiTemplate1: ByteArray
        val ansiTemplate2: ByteArray
        val isoTemplate1: ByteArray
        val isoTemplate2: ByteArray
        val ansiTemplate1Windows: ByteArray
        val ansiTemplate2Windows: ByteArray
        val ansiTemplate3Windows: ByteArray

        val size = IntArray(1)
        val score = IntArray(1)
        val quality1 = IntArray(1)
        val quality2 = IntArray(1)
        val quality3 = IntArray(1)
        val numOfMinutiae = IntArray(1)
        val nfiq1: Long
        val nfiq2: Long
        val nfiq3: Long
        val matched = BooleanArray(1)

        val finger1 = ByteArray(X_SIZE * Y_SIZE)
        val finger2 = ByteArray(X_SIZE * Y_SIZE)
        val finger3 = ByteArray(X_SIZE * Y_SIZE)

        var dwTimeStart: Long = 0
        var dwTimeEnd: Long = 0
        var dwTimeElapsed: Long = 0

        try {
            val fileInputStream = getResources().openRawResource(R.raw.finger_0_10_3)
            error = fileInputStream.read(finger1).toLong()
            fileInputStream.close()
        } catch (ex: IOException) {
            debugMessage("Error: Unable to find fingerprint image R.raw.finger_0_10_3.\n")
            return
        }

        try {
            val fileInputStream = getResources().openRawResource(R.raw.finger_1_10_3)
            error = fileInputStream.read(finger2).toLong()
            fileInputStream.close()
        } catch (ex: IOException) {
            debugMessage("Error: Unable to find fingerprint image R.raw.finger_1_10_3.\n")
            return
        }

        try {
            val fileInputStream = getResources().openRawResource(R.raw.finger_2_10_3)
            error = fileInputStream.read(finger3).toLong()
            fileInputStream.close()
        } catch (ex: IOException) {
            debugMessage("Error: Unable to find fingerprint image R.raw.finger_2_10_3.\n")
            return
        }

        try {
            val fileInputStream = getResources().openRawResource(R.raw.ansi378_0_10_3_windows)
            val length = fileInputStream.available()
            debugMessage("ansi378_0_10_3_windows.ansi378 \n\ttemplate length is: $length\n")
            ansiTemplate1Windows = ByteArray(length)
            error = fileInputStream.read(ansiTemplate1Windows).toLong()
            debugMessage("\tRead: " + error + "bytes\n")
            fileInputStream.close()
        } catch (ex: IOException) {
            debugMessage("Error: Unable to find fingerprint image R.raw.ansi378_0_10_3_windows.ansi378.\n")
            return
        }

        try {
            val fileInputStream = getResources().openRawResource(R.raw.ansi378_1_10_3_windows)
            val length = fileInputStream.available()
            debugMessage("ansi378_1_10_3_windows.ansi378 \n\ttemplate length is: $length\n")
            ansiTemplate2Windows = ByteArray(length)
            error = fileInputStream.read(ansiTemplate2Windows).toLong()
            debugMessage("\tRead: " + error + "bytes\n")
            fileInputStream.close()
        } catch (ex: IOException) {
            debugMessage("Error: Unable to find fingerprint image R.raw.ansi378_1_10_3_windows.ansi378.\n")
            return
        }

        try {
            val fileInputStream = getResources().openRawResource(R.raw.ansi378_2_10_3_windows)
            val length = fileInputStream.available()
            debugMessage("ansi378_2_10_3_windows.ansi378 \n\ttemplate length is: $length\n")
            ansiTemplate3Windows = ByteArray(length)
            error = fileInputStream.read(ansiTemplate3Windows).toLong()
            debugMessage("\tRead: " + error + "bytes\n")
            fileInputStream.close()
        } catch (ex: IOException) {
            debugMessage("Error: Unable to find fingerprint image R.raw.ansi378_2_10_3_windows.ansi378.\n")
            return
        }

        val sgFplibSDKTest = JSGFPLib(getSystemService(Context.USB_SERVICE) as UsbManager)

        error = sgFplibSDKTest.InitEx(X_SIZE.toLong(), Y_SIZE.toLong(), 500)
        debugMessage("InitEx($X_SIZE,$Y_SIZE,500) ret:$error\n")
        if (error != SGFDxErrorCode.SGFDX_ERROR_NONE)
            return

        val fpInfo1 = SGFingerInfo()
        val fpInfo2 = SGFingerInfo()
        val fpInfo3 = SGFingerInfo()

        error = sgFplibSDKTest.GetImageQuality(X_SIZE.toLong(), Y_SIZE.toLong(), finger1, quality1)

        debugMessage("GetImageQuality(R.raw.finger_0_10_3) ret:" + error + "\n\tFinger quality=" + quality1[0] + "\n")
        error = sgFplibSDKTest.GetImageQuality(X_SIZE.toLong(), Y_SIZE.toLong(), finger2, quality2)
        debugMessage("GetImageQuality(R.raw.finger_1_10_3) ret:" + error + "\n\tFinger quality=" + quality2[0] + "\n")
        error = sgFplibSDKTest.GetImageQuality(X_SIZE.toLong(), Y_SIZE.toLong(), finger3, quality3)
        debugMessage("GetImageQuality(R.raw.finger_2_10_3) ret:" + error + "\n\tFinger quality=" + quality3[0] + "\n")

        dwTimeStart = System.currentTimeMillis()
        nfiq1 = sgFplibSDKTest.ComputeNFIQ(finger1, X_SIZE.toLong(), Y_SIZE.toLong())
        dwTimeEnd = System.currentTimeMillis()
        dwTimeElapsed = dwTimeEnd - dwTimeStart
        debugMessage("ComputeNFIQ(R.raw.finger_0_10_3)\n\tNFIQ=$nfiq1\n")
        if (nfiq1 == 2L)
            debugMessage("\t+++PASS\n")
        else
            debugMessage("\t+++FAIL!!!!!!!!!!!!!!!!!!\n")
        debugMessage("\t$dwTimeElapsed milliseconds\n")

        dwTimeStart = System.currentTimeMillis()
        nfiq2 = sgFplibSDKTest.ComputeNFIQ(finger2, X_SIZE.toLong(), Y_SIZE.toLong())
        dwTimeEnd = System.currentTimeMillis()
        dwTimeElapsed = dwTimeEnd - dwTimeStart
        debugMessage("ComputeNFIQ(R.raw.finger_1_10_3)\n\tNFIQ=$nfiq2\n")
        if (nfiq2 == 3L)
            debugMessage("\t+++PASS\n")
        else
            debugMessage("\t+++FAIL!!!!!!!!!!!!!!!!!!\n")
        debugMessage("\t$dwTimeElapsed milliseconds\n")

        dwTimeStart = System.currentTimeMillis()
        nfiq3 = sgFplibSDKTest.ComputeNFIQ(finger3, X_SIZE.toLong(), Y_SIZE.toLong())
        dwTimeEnd = System.currentTimeMillis()
        dwTimeElapsed = dwTimeEnd - dwTimeStart
        debugMessage("ComputeNFIQ(R.raw.finger_2_10_3)\n\tNFIQ=$nfiq3\n")
        if (nfiq3 == 2L)
            debugMessage("\t+++PASS\n")
        else
            debugMessage("\t+++FAIL!!!!!!!!!!!!!!!!!!\n")
        debugMessage("\t$dwTimeElapsed milliseconds\n")

        fpInfo1.FingerNumber = 1
        fpInfo1.ImageQuality = quality1[0]
        fpInfo1.ImpressionType = SGImpressionType.SG_IMPTYPE_LP
        fpInfo1.ViewNumber = 1

        fpInfo2.FingerNumber = 1
        fpInfo2.ImageQuality = quality2[0]
        fpInfo2.ImpressionType = SGImpressionType.SG_IMPTYPE_LP
        fpInfo2.ViewNumber = 2

        fpInfo3.FingerNumber = 1
        fpInfo3.ImageQuality = quality3[0]
        fpInfo3.ImpressionType = SGImpressionType.SG_IMPTYPE_LP
        fpInfo3.ViewNumber = 3


        ///////////////////////////////////////////////////////////////////////////////////////////////
        //TEST SG400
        debugMessage("#######################\n")
        debugMessage("TEST SG400\n")
        debugMessage("###\n###\n")
        error = sgFplibSDKTest.SetTemplateFormat(SecuGen.FDxSDKPro.SGFDxTemplateFormat.TEMPLATE_FORMAT_SG400)
        debugMessage("SetTemplateFormat(SG400) ret:$error\n")
        error = sgFplibSDKTest.GetMaxTemplateSize(size)
        debugMessage("GetMaxTemplateSize() ret:" + error + " SG400_MAX_SIZE=" + size[0] + "\n")

        sgTemplate1 = ByteArray(size[0])
        sgTemplate2 = ByteArray(size[0])
        sgTemplate3 = ByteArray(size[0])

        //////////////////////////////////////////////////////////////////////////////////////////////
        //TEST DeviceInfo
        error = sgFplibSDKTest.CreateTemplate(null, finger1, sgTemplate1)
        debugMessage("CreateTemplate(finger3) ret:$error\n")
        error = sgFplibSDKTest.GetTemplateSize(sgTemplate1, size)
        debugMessage("GetTemplateSize() ret:" + error + " size=" + size[0] + "\n")
        error = sgFplibSDKTest.GetNumOfMinutiae(
            SecuGen.FDxSDKPro.SGFDxTemplateFormat.TEMPLATE_FORMAT_SG400,
            sgTemplate1,
            numOfMinutiae
        )
        debugMessage("GetNumOfMinutiae() ret:" + error + " minutiae=" + numOfMinutiae[0] + "\n")

        error = sgFplibSDKTest.CreateTemplate(null, finger2, sgTemplate2)
        debugMessage("CreateTemplate(finger2) ret:$error\n")
        error = sgFplibSDKTest.GetTemplateSize(sgTemplate2, size)
        debugMessage("GetTemplateSize() ret:" + error + " size=" + size[0] + "\n")
        error = sgFplibSDKTest.GetNumOfMinutiae(
            SecuGen.FDxSDKPro.SGFDxTemplateFormat.TEMPLATE_FORMAT_SG400,
            sgTemplate2,
            numOfMinutiae
        )
        debugMessage("GetNumOfMinutiae() ret:" + error + " minutiae=" + numOfMinutiae[0] + "\n")

        error = sgFplibSDKTest.CreateTemplate(null, finger3, sgTemplate3)
        debugMessage("CreateTemplate(finger3) ret:$error\n")
        error = sgFplibSDKTest.GetTemplateSize(sgTemplate3, size)
        debugMessage("GetTemplateSize() ret:" + error + " size=" + size[0] + "\n")
        error = sgFplibSDKTest.GetNumOfMinutiae(
            SecuGen.FDxSDKPro.SGFDxTemplateFormat.TEMPLATE_FORMAT_SG400,
            sgTemplate3,
            numOfMinutiae
        )
        debugMessage("GetNumOfMinutiae() ret:" + error + " minutiae=" + numOfMinutiae[0] + "\n")

        ///////////////////////////////////////////////////////////////////////////////////////////////
        error = sgFplibSDKTest.MatchTemplate(sgTemplate1, sgTemplate2, SGFDxSecurityLevel.SL_NORMAL, matched)
        debugMessage("MatchTemplate(sgTemplate1,sgTemplate2) \n\tret:$error\n")
        if (matched[0])
            debugMessage("\tMATCHED!!\n")
        else
            debugMessage("\tNOT MATCHED!!\n")

        error = sgFplibSDKTest.GetMatchingScore(sgTemplate1, sgTemplate2, score)
        debugMessage("GetMatchingScore(sgTemplate1, sgTemplate2) \n\tret:" + error + ". \n\tScore:" + score[0] + "\n")


        ///////////////////////////////////////////////////////////////////////////////////////////////
        error = sgFplibSDKTest.MatchTemplate(sgTemplate1, sgTemplate3, SGFDxSecurityLevel.SL_NORMAL, matched)
        debugMessage("MatchTemplate(sgTemplate1,sgTemplate3) \n\tret:$error\n")
        if (matched[0])
            debugMessage("\tMATCHED!!\n")
        else
            debugMessage("\tNOT MATCHED!!\n")

        error = sgFplibSDKTest.GetMatchingScore(sgTemplate1, sgTemplate3, score)
        debugMessage("GetMatchingScore(sgTemplate1, sgTemplate3) \n\tret:" + error + ". \n\tScore:" + score[0] + "\n")


        ///////////////////////////////////////////////////////////////////////////////////////////////
        error = sgFplibSDKTest.MatchTemplate(sgTemplate2, sgTemplate3, SGFDxSecurityLevel.SL_NORMAL, matched)
        debugMessage("MatchTemplate(sgTemplate2,sgTemplate3) \n\tret:$error\n")
        if (matched[0])
            debugMessage("\tMATCHED!!\n")
        else
            debugMessage("\tNOT MATCHED!!\n")

        error = sgFplibSDKTest.GetMatchingScore(sgTemplate2, sgTemplate3, score)
        debugMessage("GetMatchingScore(sgTemplate2, sgTemplate3) \n\tret:" + error + ". \n\tScore:" + score[0] + "\n")


        ///////////////////////////////////////////////////////////////////////////////////////////////
        //TEST ANSI378
        debugMessage("#######################\n")
        debugMessage("TEST ANSI378\n")
        debugMessage("###\n###\n")
        error = sgFplibSDKTest.SetTemplateFormat(SecuGen.FDxSDKPro.SGFDxTemplateFormat.TEMPLATE_FORMAT_ANSI378)
        debugMessage("SetTemplateFormat(ANSI378) ret:$error\n")
        error = sgFplibSDKTest.GetMaxTemplateSize(size)
        debugMessage("GetMaxTemplateSize() ret:" + error + "\n\tANSI378_MAX_SIZE=" + size[0] + "\n")

        ansiTemplate1 = ByteArray(size[0])
        ansiTemplate2 = ByteArray(size[0])

        error = sgFplibSDKTest.CreateTemplate(fpInfo1, finger1, ansiTemplate1)
        debugMessage("CreateTemplate(finger1) ret:$error\n")
        error = sgFplibSDKTest.GetTemplateSize(ansiTemplate1, size)
        debugMessage("GetTemplateSize(ansi) ret:" + error + " size=" + size[0] + "\n")
        error = sgFplibSDKTest.GetNumOfMinutiae(
            SecuGen.FDxSDKPro.SGFDxTemplateFormat.TEMPLATE_FORMAT_ANSI378,
            ansiTemplate1,
            numOfMinutiae
        )
        debugMessage("GetNumOfMinutiae(ansi) ret:" + error + " minutiae=" + numOfMinutiae[0] + "\n")

        error = sgFplibSDKTest.CreateTemplate(fpInfo2, finger2, ansiTemplate2)
        debugMessage("CreateTemplate(finger2) ret:$error\n")
        error = sgFplibSDKTest.GetTemplateSize(ansiTemplate2, size)
        debugMessage("GetTemplateSize(ansi) ret:" + error + " size=" + size[0] + "\n")
        error = sgFplibSDKTest.GetNumOfMinutiae(
            SecuGen.FDxSDKPro.SGFDxTemplateFormat.TEMPLATE_FORMAT_ANSI378,
            ansiTemplate2,
            numOfMinutiae
        )
        debugMessage("GetNumOfMinutiae(ansi) ret:" + error + " minutiae=" + numOfMinutiae[0] + "\n")

        error = sgFplibSDKTest.MatchTemplate(ansiTemplate1, ansiTemplate2, SGFDxSecurityLevel.SL_NORMAL, matched)
        debugMessage("MatchTemplate(ansi) ret:$error\n")
        if (matched[0])
            debugMessage("\tMATCHED!!\n")
        else
            debugMessage("\tNOT MATCHED!!\n")

        error = sgFplibSDKTest.GetMatchingScore(ansiTemplate1, ansiTemplate2, score)
        debugMessage("GetMatchingScore(ansi) ret:" + error + ". \n\tScore:" + score[0] + "\n")

        error = sgFplibSDKTest.GetTemplateSizeAfterMerge(ansiTemplate1, ansiTemplate2, size)
        debugMessage("GetTemplateSizeAfterMerge(ansi) ret:" + error + ". \n\tSize:" + size[0] + "\n")

        val mergedAnsiTemplate1 = ByteArray(size[0])
        error = sgFplibSDKTest.MergeAnsiTemplate(ansiTemplate1, ansiTemplate2, mergedAnsiTemplate1)
        debugMessage("MergeAnsiTemplate() ret:$error\n")

        error = sgFplibSDKTest.MatchAnsiTemplate(
            ansiTemplate1,
            0,
            mergedAnsiTemplate1,
            0,
            SGFDxSecurityLevel.SL_NORMAL,
            matched
        )
        debugMessage("MatchAnsiTemplate(0,0) ret:$error\n")
        if (matched[0])
            debugMessage("\tMATCHED!!\n")
        else
            debugMessage("\tNOT MATCHED!!\n")

        error = sgFplibSDKTest.MatchAnsiTemplate(
            ansiTemplate1,
            0,
            mergedAnsiTemplate1,
            1,
            SGFDxSecurityLevel.SL_NORMAL,
            matched
        )
        debugMessage("MatchAnsiTemplate(0,1) ret:$error\n")
        if (matched[0])
            debugMessage("\tMATCHED!!\n")
        else
            debugMessage("\tNOT MATCHED!!\n")

        error = sgFplibSDKTest.GetAnsiMatchingScore(ansiTemplate1, 0, mergedAnsiTemplate1, 0, score)
        debugMessage("GetAnsiMatchingScore(0,0) ret:" + error + ". \n\tScore:" + score[0] + "\n")

        error = sgFplibSDKTest.GetAnsiMatchingScore(ansiTemplate1, 0, mergedAnsiTemplate1, 1, score)
        debugMessage("GetAnsiMatchingScore(0,1) ret:" + error + ". \n\tScore:" + score[0] + "\n")

        val ansiTemplateInfo = SGANSITemplateInfo()
        error = sgFplibSDKTest.GetAnsiTemplateInfo(ansiTemplate1, ansiTemplateInfo)
        debugMessage("GetAnsiTemplateInfo(ansiTemplate1) ret:$error\n")
        debugMessage("   TotalSamples=" + ansiTemplateInfo.TotalSamples + "\n")
        for (i in 0 until ansiTemplateInfo.TotalSamples) {
            debugMessage("   Sample[" + i + "].FingerNumber=" + ansiTemplateInfo.SampleInfo[i].FingerNumber + "\n")
            debugMessage("   Sample[" + i + "].ImageQuality=" + ansiTemplateInfo.SampleInfo[i].ImageQuality + "\n")
            debugMessage("   Sample[" + i + "].ImpressionType=" + ansiTemplateInfo.SampleInfo[i].ImpressionType + "\n")
            debugMessage("   Sample[" + i + "].ViewNumber=" + ansiTemplateInfo.SampleInfo[i].ViewNumber + "\n")
        }

        error = sgFplibSDKTest.GetAnsiTemplateInfo(mergedAnsiTemplate1, ansiTemplateInfo)
        debugMessage("GetAnsiTemplateInfo(mergedAnsiTemplate1) ret:$error\n")
        debugMessage("   TotalSamples=" + ansiTemplateInfo.TotalSamples + "\n")

        for (i in 0 until ansiTemplateInfo.TotalSamples) {
            debugMessage("   Sample[" + i + "].FingerNumber=" + ansiTemplateInfo.SampleInfo[i].FingerNumber + "\n")
            debugMessage("   Sample[" + i + "].ImageQuality=" + ansiTemplateInfo.SampleInfo[i].ImageQuality + "\n")
            debugMessage("   Sample[" + i + "].ImpressionType=" + ansiTemplateInfo.SampleInfo[i].ImpressionType + "\n")
            debugMessage("   Sample[" + i + "].ViewNumber=" + ansiTemplateInfo.SampleInfo[i].ViewNumber + "\n")
        }


        ///////////////////////////////////////////////////////////////////////////////////////////////
        //ALGORITHM COMPATIBILITY TEST
        val compatible: Boolean
        debugMessage("#######################\n")
        debugMessage("TEST ANSI378 Compatibility\n")
        debugMessage("###\n###\n")
        ///
        error = sgFplibSDKTest.GetMatchingScore(ansiTemplate1, ansiTemplate1Windows, score)

        debugMessage("0_10_3.raw <> 0_10_3.ansiw:" + score[0] + "\n")
        if (score[0] == 199)
            debugMessage("\t+++PASS\n")
        else
            debugMessage("\t+++FAIL!!!!!!!!!!!!!!!!!!\n")

        ///
        error = sgFplibSDKTest.GetMatchingScore(ansiTemplate1, ansiTemplate2Windows, score)
        debugMessage("0_10_3.raw <> 1_10_3.ansiw:" + score[0] + "\n")
        if (score[0] == 199)
            debugMessage("\t+++PASS\n")
        else
            debugMessage("\t+++FAIL!!!!!!!!!!!!!!!!!!\n")
        ///
        error = sgFplibSDKTest.GetMatchingScore(ansiTemplate1, ansiTemplate3Windows, score)
        debugMessage("0_10_3.raw <> 2_10_3.ansiw:" + score[0] + "\n")
        if (score[0] == 176)
            debugMessage("\t+++PASS\n")
        else
            debugMessage("\t+++FAIL!!!!!!!!!!!!!!!!!!\n")
        ///
        error = sgFplibSDKTest.GetMatchingScore(ansiTemplate2, ansiTemplate3Windows, score)
        if (score[0] == 192)
            compatible = true
        else
            compatible = false
        debugMessage("1_10_3.raw <> 2_10_3.ansiw:" + score[0] + "\n\tCompatible:" + compatible + "\n")

        ///////////////////////////////////////////////////////////////////////////////////////////////
        //TEST ISO19794-2
        debugMessage("#######################\n")
        debugMessage("TEST ISO19794-2\n")
        debugMessage("###\n###\n")
        error = sgFplibSDKTest.SetTemplateFormat(SecuGen.FDxSDKPro.SGFDxTemplateFormat.TEMPLATE_FORMAT_ISO19794)
        debugMessage("SetTemplateFormat(ISO19794) ret:$error\n")
        error = sgFplibSDKTest.GetMaxTemplateSize(size)
        debugMessage("GetMaxTemplateSize() ret:" + error + " ISO19794_MAX_SIZE=" + size[0] + "\n")

        isoTemplate1 = ByteArray(size[0])
        isoTemplate2 = ByteArray(size[0])

        error = sgFplibSDKTest.CreateTemplate(fpInfo1, finger1, isoTemplate1)
        debugMessage("CreateTemplate(finger1) ret:$error\n")
        error = sgFplibSDKTest.GetTemplateSize(isoTemplate1, size)
        debugMessage("GetTemplateSize(iso) ret:" + error + " \n\tsize=" + size[0] + "\n")
        error = sgFplibSDKTest.GetNumOfMinutiae(
            SecuGen.FDxSDKPro.SGFDxTemplateFormat.TEMPLATE_FORMAT_ISO19794,
            isoTemplate1,
            numOfMinutiae
        )
        debugMessage("GetNumOfMinutiae(iso) ret:" + error + " minutiae=" + numOfMinutiae[0] + "\n")

        error = sgFplibSDKTest.CreateTemplate(fpInfo2, finger2, isoTemplate2)
        debugMessage("CreateTemplate(finger2) ret:$error\n")
        error = sgFplibSDKTest.GetTemplateSize(isoTemplate2, size)
        debugMessage("GetTemplateSize(iso) ret:" + error + " \n\tsize=" + size[0] + "\n")
        error = sgFplibSDKTest.GetNumOfMinutiae(
            SecuGen.FDxSDKPro.SGFDxTemplateFormat.TEMPLATE_FORMAT_ISO19794,
            isoTemplate2,
            numOfMinutiae
        )
        debugMessage("GetNumOfMinutiae(iso) ret:" + error + " minutiae=" + numOfMinutiae[0] + "\n")

        error = sgFplibSDKTest.MatchTemplate(isoTemplate1, isoTemplate2, SGFDxSecurityLevel.SL_NORMAL, matched)
        debugMessage("MatchTemplate(iso) ret:$error\n")
        if (matched[0])
            debugMessage("\tMATCHED!!\n")
        else
            debugMessage("\tNOT MATCHED!!\n")

        error = sgFplibSDKTest.GetMatchingScore(isoTemplate1, isoTemplate2, score)
        debugMessage("GetMatchingScore(iso) ret:" + error + ". \n\tScore:" + score[0] + "\n")

        error = sgFplibSDKTest.GetIsoTemplateSizeAfterMerge(isoTemplate1, isoTemplate2, size)
        debugMessage("GetIsoTemplateSizeAfterMerge() ret:" + error + ". \n\tSize:" + size[0] + "\n")


        val mergedIsoTemplate1 = ByteArray(size[0])
        error = sgFplibSDKTest.MergeIsoTemplate(isoTemplate1, isoTemplate2, mergedIsoTemplate1)
        debugMessage("MergeIsoTemplate() ret:$error\n")

        error = sgFplibSDKTest.MatchIsoTemplate(
            isoTemplate1,
            0,
            mergedIsoTemplate1,
            0,
            SGFDxSecurityLevel.SL_NORMAL,
            matched
        )
        debugMessage("MatchIsoTemplate(0,0) ret:$error\n")
        if (matched[0])
            debugMessage("\tMATCHED!!\n")
        else
            debugMessage("\tNOT MATCHED!!\n")

        error = sgFplibSDKTest.MatchIsoTemplate(
            isoTemplate1,
            0,
            mergedIsoTemplate1,
            1,
            SGFDxSecurityLevel.SL_NORMAL,
            matched
        )
        debugMessage("MatchIsoTemplate(0,1) ret:$error\n")
        if (matched[0])
            debugMessage("\tMATCHED!!\n")
        else
            debugMessage("\tNOT MATCHED!!\n")

        error = sgFplibSDKTest.GetIsoMatchingScore(isoTemplate1, 0, mergedIsoTemplate1, 0, score)
        debugMessage("GetIsoMatchingScore(0,0) ret:" + error + ". \n\tScore:" + score[0] + "\n")

        error = sgFplibSDKTest.GetIsoMatchingScore(isoTemplate1, 0, mergedIsoTemplate1, 1, score)
        debugMessage("GetIsoMatchingScore(0,1) ret:" + error + ". \n\tScore:" + score[0] + "\n")

        val isoTemplateInfo = SGISOTemplateInfo()
        error = sgFplibSDKTest.GetIsoTemplateInfo(isoTemplate1, isoTemplateInfo)
        debugMessage("GetIsoTemplateInfo(isoTemplate1) \n\tret:$error\n")
        debugMessage("\tTotalSamples=" + isoTemplateInfo.TotalSamples + "\n")
        for (i in 0 until isoTemplateInfo.TotalSamples) {
            debugMessage("\tSample[" + i + "].FingerNumber=" + isoTemplateInfo.SampleInfo[i].FingerNumber + "\n")
            debugMessage("\tSample[" + i + "].ImageQuality=" + isoTemplateInfo.SampleInfo[i].ImageQuality + "\n")
            debugMessage("\tSample[" + i + "].ImpressionType=" + isoTemplateInfo.SampleInfo[i].ImpressionType + "\n")
            debugMessage("\tSample[" + i + "].ViewNumber=" + isoTemplateInfo.SampleInfo[i].ViewNumber + "\n")
        }

        error = sgFplibSDKTest.GetIsoTemplateInfo(mergedIsoTemplate1, isoTemplateInfo)
        debugMessage("GetIsoTemplateInfo(mergedIsoTemplate1) \n\tret:$error\n")
        debugMessage("\tTotalSamples=" + isoTemplateInfo.TotalSamples + "\n")
        for (i in 0 until isoTemplateInfo.TotalSamples) {
            debugMessage("\tSample[" + i + "].FingerNumber=" + isoTemplateInfo.SampleInfo[i].FingerNumber + "\n")
            debugMessage("\tSample[" + i + "].ImageQuality=" + isoTemplateInfo.SampleInfo[i].ImageQuality + "\n")
            debugMessage("\tSample[" + i + "].ImpressionType=" + isoTemplateInfo.SampleInfo[i].ImpressionType + "\n")
            debugMessage("\tSample[" + i + "].ViewNumber=" + isoTemplateInfo.SampleInfo[i].ViewNumber + "\n")
        }

        //Reset extractor/matcher for attached device opened in resume() method
        error = sgFplibSDKTest.InitEx(mImageWidth.toLong(), mImageHeight.toLong(), 500)
        debugMessage("InitEx($mImageWidth,$mImageHeight,500) ret:$error\n")

        ///////////////////////////////////////////////////////////////////////////////////////////////
        //Test WSQ Processing
        debugMessage("#######################\n")
        debugMessage("TEST WSQ COMPRESSION\n")
        debugMessage("###\n###\n")
        val wsqfinger1: ByteArray
        val wsqLen: Int
        try {
            val fileInputStream = getResources().openRawResource(R.raw.wsq2raw_finger)
            wsqLen = fileInputStream.available()
            debugMessage("WSQ file length is: $wsqLen\n")
            wsqfinger1 = ByteArray(wsqLen)
            error = fileInputStream.read(wsqfinger1).toLong()
            debugMessage("Read: " + error + "bytes\n")
            fileInputStream.close()
        } catch (ex: IOException) {
            debugMessage("Error: Unable to find fingerprint image R.raw.wsq2raw_finger.\n")
            return
        }


        val fingerImageOutSize = IntArray(1)
        dwTimeStart = System.currentTimeMillis()
        error = sgFplibSDKTest.WSQGetDecodedImageSize(fingerImageOutSize, wsqfinger1, wsqLen)
        dwTimeEnd = System.currentTimeMillis()
        dwTimeElapsed = dwTimeEnd - dwTimeStart
        debugMessage("WSQGetDecodedImageSize() ret:$error\n")
        debugMessage("\tRAW Image size is: " + fingerImageOutSize[0] + "\n")
        debugMessage("\t$dwTimeElapsed milliseconds\n")
        //      debugMessage("Byte 0:"+ String.format("%02X",wsqfinger1[0]) + "\n");
        //      debugMessage("Byte 1:"+ String.format("%02X",wsqfinger1[1]) + "\n");
        //      debugMessage("Byte 201:"+ String.format("%02X",wsqfinger1[201]) + "\n");
        //      debugMessage("Byte 1566:"+ String.format("%02X",wsqfinger1[1566]) + "\n");
        //      debugMessage("Byte 7001:"+ String.format("%02X",wsqfinger1[7001]) + "\n");
        //      debugMessage("Byte 7291:"+ String.format("%02X",wsqfinger1[7291]) + "\n");

        val rawfinger1ImageOut = ByteArray(fingerImageOutSize[0])
        val decodeWidth = IntArray(1)
        val decodeHeight = IntArray(1)
        val decodePixelDepth = IntArray(1)
        val decodePPI = IntArray(1)
        val decodeLossyFlag = IntArray(1)
        debugMessage("Decode WSQ File\n")
        dwTimeStart = System.currentTimeMillis()
        error = sgFplibSDKTest.WSQDecode(
            rawfinger1ImageOut,
            decodeWidth,
            decodeHeight,
            decodePixelDepth,
            decodePPI,
            decodeLossyFlag,
            wsqfinger1,
            wsqLen
        )
        dwTimeEnd = System.currentTimeMillis()
        dwTimeElapsed = dwTimeEnd - dwTimeStart
        debugMessage("\tret:\t\t\t$error\n")
        debugMessage("\twidth:\t\t" + decodeWidth[0] + "\n")
        debugMessage("\theight:\t\t" + decodeHeight[0] + "\n")
        debugMessage("\tdepth:\t\t" + decodePixelDepth[0] + "\n")
        debugMessage("\tPPI:\t\t\t" + decodePPI[0] + "\n")
        debugMessage("\tLossy Flag\t" + decodeLossyFlag[0] + "\n")
        if (decodeWidth[0] == 258 && decodeHeight[0] == 336)
            debugMessage("\t+++PASS\n")
        else
            debugMessage("\t+++FAIL!!!!!!!!!!!!!!!!!!\n")
        debugMessage("\t$dwTimeElapsed milliseconds\n")

      //  mImageViewFingerprint!!.setImageBitmap(this.toGrayscale(rawfinger1ImageOut, decodeWidth[0], decodeHeight[0]))


        val rawfinger1: ByteArray
        val encodeWidth = 258
        val encodeHeight = 336
        val encodePixelDepth = 8
        val encodePPI = 500

        val rawLen: Int
        try {
            val fileInputStream = getResources().openRawResource(R.raw.raw2wsq_finger)
            rawLen = fileInputStream.available()
            debugMessage("RAW file length is: $rawLen\n")
            rawfinger1 = ByteArray(rawLen)
            error = fileInputStream.read(rawfinger1).toLong()
            debugMessage("Read: " + error + "bytes\n")
            fileInputStream.close()
        } catch (ex: IOException) {
            debugMessage("Error: Unable to find fingerprint image R.raw.raw2wsq_finger.\n")
            return
        }

        val wsqImageOutSize = IntArray(1)
        dwTimeStart = System.currentTimeMillis()
        error = sgFplibSDKTest.WSQGetEncodedImageSize(
            wsqImageOutSize,
            SGWSQLib.BITRATE_5_TO_1,
            rawfinger1,
            encodeWidth,
            encodeHeight,
            encodePixelDepth,
            encodePPI
        )
        dwTimeEnd = System.currentTimeMillis()
        dwTimeElapsed = dwTimeEnd - dwTimeStart
        debugMessage("WSQGetEncodedImageSize() ret:$error\n")
        debugMessage("WSQ Image size is: " + wsqImageOutSize[0] + "\n")
        if (wsqImageOutSize[0] == 20200)
            debugMessage("\t+++PASS\n")
        else
            debugMessage("\t+++FAIL!!!!!!!!!!!!!!!!!!\n")
        debugMessage("\t$dwTimeElapsed milliseconds\n")

        val wsqfinger1ImageOut = ByteArray(wsqImageOutSize[0])
        dwTimeStart = System.currentTimeMillis()
        error = sgFplibSDKTest.WSQEncode(
            wsqfinger1ImageOut,
            SGWSQLib.BITRATE_5_TO_1,
            rawfinger1,
            encodeWidth,
            encodeHeight,
            encodePixelDepth,
            encodePPI
        )
        dwTimeEnd = System.currentTimeMillis()
        dwTimeElapsed = dwTimeEnd - dwTimeStart
        debugMessage("WSQEncode() ret:$error\n")
        debugMessage("\t$dwTimeElapsed milliseconds\n")

        dwTimeStart = System.currentTimeMillis()
        error = sgFplibSDKTest.WSQGetDecodedImageSize(fingerImageOutSize, wsqfinger1ImageOut, wsqImageOutSize[0])
        dwTimeEnd = System.currentTimeMillis()
        dwTimeElapsed = dwTimeEnd - dwTimeStart
        debugMessage("WSQGetDecodedImageSize() ret:$error\n")
        debugMessage("RAW Image size is: " + fingerImageOutSize[0] + "\n")
        debugMessage("\t$dwTimeElapsed milliseconds\n")

        val rawfinger2ImageOut = ByteArray(fingerImageOutSize[0])
        dwTimeStart = System.currentTimeMillis()
        error = sgFplibSDKTest.WSQDecode(
            rawfinger2ImageOut,
            decodeWidth,
            decodeHeight,
            decodePixelDepth,
            decodePPI,
            decodeLossyFlag,
            wsqfinger1,
            wsqLen
        )
        dwTimeEnd = System.currentTimeMillis()
        dwTimeElapsed = dwTimeEnd - dwTimeStart
        debugMessage("WSQDecode() ret:$error\n")
        debugMessage("\tret:\t\t\t$error\n")
        debugMessage("\twidth:\t\t" + decodeWidth[0] + "\n")
        debugMessage("\theight:\t\t" + decodeHeight[0] + "\n")
        debugMessage("\tdepth:\t\t" + decodePixelDepth[0] + "\n")
        debugMessage("\tPPI:\t\t\t" + decodePPI[0] + "\n")
        debugMessage("\tLossy Flag\t" + decodeLossyFlag[0] + "\n")
        if (decodeWidth[0] == 258 && decodeHeight[0] == 336)
            debugMessage("\t+++PASS\n")
        else
            debugMessage("\t+++FAIL!!!!!!!!!!!!!!!!!!\n")
     //   mImageViewFingerprint!!.setImageBitmap(this.toGrayscale(rawfinger2ImageOut, decodeWidth[0], decodeHeight[0]))
        debugMessage("\t$dwTimeElapsed milliseconds\n")

        debugMessage("\n## END SDK TEST ##\n")
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////
    override fun run() {

        //Log.d(TAG, "Enter run()");
        //ByteBuffer buffer = ByteBuffer.allocate(1);
        //UsbRequest request = new UsbRequest();
        //request.initialize(mSGUsbInterface.getConnection(), mEndpointBulk);
        //byte status = -1;
        while (true) {


            // queue a request on the interrupt endpoint
            //request.queue(buffer, 1);
            // send poll status command
            //  sendCommand(COMMAND_STATUS);
            // wait for status event
            /*
            if (mSGUsbInterface.getConnection().requestWait() == request) {
                byte newStatus = buffer.get(0);
                if (newStatus != status) {
                    Log.d(TAG, "got status " + newStatus);
                    status = newStatus;
                    if ((status & COMMAND_FIRE) != 0) {
                        // stop firing
                        sendCommand(COMMAND_STOP);
                    }
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
            } else {
                Log.e(TAG, "requestWait failed, exiting");
                break;
            }
            */
        }
    }
}