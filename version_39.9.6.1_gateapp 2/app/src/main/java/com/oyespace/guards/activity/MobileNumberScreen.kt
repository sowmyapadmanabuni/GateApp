package com.oyespace.guards.activity

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.speech.RecognizerIntent
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.text.TextUtils.substring
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.hbb20.CountryCodePicker
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.DexterError
import com.karumi.dexter.listener.PermissionRequestErrorListener
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.oyespace.guards.PojoClasses.GetLatestRecord
import com.oyespace.guards.R
import com.oyespace.guards.constants.PrefKeys
import com.oyespace.guards.listeners.PermissionCallback
import com.oyespace.guards.network.CommonDisposable
import com.oyespace.guards.network.RetrofitClinet
import com.oyespace.guards.pojo.*
import com.oyespace.guards.repo.VisitorLogRepo
import com.oyespace.guards.utils.ConstantUtils.*
import com.oyespace.guards.utils.LocalDb
import com.oyespace.guards.utils.Prefs
import com.oyespace.guards.utils.RandomUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_mobile_number.*
import java.util.*


class MobileNumberScreen : BaseKotlinActivity(), View.OnClickListener,
    CountryCodePicker.OnCountryChangeListener {
    var iv_torch:Button?=null
    var clickable1 = 0
    val workType: ArrayList<String> = ArrayList()
    private var ccp: CountryCodePicker? = null
    private var countryCode: String? = null
    private var countryName: String? = null
    lateinit var pDialog: ProgressDialog
    var phonenumber: String? = null
    lateinit var txt_assn_name: TextView
    lateinit var txt_gate_name: TextView
    lateinit var txt_device_name: TextView
    lateinit var timer: TextView
    val laststate: Int? = null
    var progressBar: ProgressBar? = null
    var ccd: String? = null
    lateinit var btn_nobalance: Button
    var mobileNumber: String? = null

    private val REQUEST_CODE_SPEECH_INPUT = 100

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.btn_nobalance -> {

                val d = Intent(this@MobileNumberScreen, MobileNumberScreenwithOTP::class.java)
                d.putExtra(FLOW_TYPE, DELIVERY)
                d.putExtra(VISITOR_TYPE, DELIVERY)
                startActivity(d)
                finish()

            }

            R.id.buttonNext -> {
                buttonNext.isEnabled = false
                buttonNext.isClickable = false

                if (useDummyValues) {
                    textview.text = "+91${dummyPhone}"
                    ccd = "+91"
                    mobileNumber = dummyPhone
                }


                if (textview.text.length == 13) {

                    mobileNumber = textview.text.toString()
                    val allowEntry = VisitorLogRepo.allowEntry("", mobileNumber)

                    if (!allowEntry) {

                        val builder = AlertDialog.Builder(this)

                        builder.setMessage("This number is being used by a person already in")
                        builder.setPositiveButton("Ok") { dialog, which ->
                            dialog.cancel()
                            finish()
                        }
                        builder.setCancelable(false)
                        builder.show()
                    }else {
                            Log.v("222", mobileNumber)
                            getAccountDetails(ccd.toString(), mobileNumber.toString())
                       }


                }else {
                        buttonNext.isEnabled = true
                        buttonNext.isClickable = true
                        Toast.makeText(this, "Invalid number captured", Toast.LENGTH_SHORT).show()

                    }


            }
        }
    }

//TODO sumeeth has reomoved this fucntion for crashing porpose

    val entries: ArrayList<String> = ArrayList()
    var receiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocale(Prefs.getString(PrefKeys.LANGUAGE, null))
        setContentView(R.layout.activity_mobile_number)

        iv_torch=findViewById(R.id.iv_torch)
        iv_torch!!.setOnClickListener {
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

        if (Build.VERSION.SDK_INT >= 28) {
            requestPermission(arrayOf(
                Manifest.permission.ANSWER_PHONE_CALLS
            ), 1, PermissionCallback { isGranted ->
                if (isGranted) {

                } else {

                }
            })
        }


        receiver = object : BroadcastReceiver() {

            override fun onReceive(context: Context?, intent: Intent?) {
                val telephony =
                    context?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                telephony.listen(object : PhoneStateListener() {

                    override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                        super.onCallStateChanged(state, phoneNumber)
                        if (state == TelephonyManager.CALL_STATE_RINGING) {

                            val bundle = intent?.extras
                            val number = bundle?.getString("incoming_number")

                            //  Toast.makeText(applicationContext, number, Toast.LENGTH_LONG).show();
                            if (textview != null && number != null) {
                                // textview.text = number.replace("+91", "")
                                textview.text = number

                                ccd = number.substring(0, 3)

                                try {
                                    mobileNumber = number.substring(3, 13)
                                }catch (e:StringIndexOutOfBoundsException){

                                }
                                // endCall(this@MobileNumberScreen)

                            }
                            LocalDb.disconnectCall(context)
                        }
                    }

                }, PhoneStateListener.LISTEN_CALL_STATE)

                //
            }
        }

        btn_nobalance = findViewById(R.id.btn_nobalance)
        progressBar = this.progressBar1
        timer = findViewById(R.id.timer)
        txt_assn_name = findViewById(R.id.txt_assn_name)
        txt_gate_name = findViewById(R.id.txt_gate_name)
        txt_device_name = findViewById(R.id.txt_device_name)
        if (Prefs.getString(PrefKeys.MODEL_NUMBER, null).equals("Nokia 1")) {
            txt_assn_name.textSize = 5 * resources.displayMetrics.density
        }
        txt_assn_name.text = "Society: " + LocalDb.getAssociation()!!.asAsnName
        txt_gate_name.text = "Gate No: " + Prefs.getString(GATE_NO, "")
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


        val timer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {



                var clock:String?=null
                val remainedSecs: Long = millisUntilFinished / 1000
                if((millisUntilFinished / 1000) % 60 < 10)
                    clock = " 0" + ((millisUntilFinished / 1000)/60)%60 + ":0" + ((millisUntilFinished / 1000)%60);
                else
                    clock = " 0" + ((millisUntilFinished / 1000)/60)%60 + ":" + ((millisUntilFinished / 1000)%60);
                // timer.text = ("0" + (remainedSecs / 60) + ":" + (remainedSecs % 60))// manage it accordign to you
                timer.text=clock
            }

            override fun onFinish() {
                finish()
            }
        }
        timer.start()


        btn_mic.setOnClickListener {
            Speak()
        }

            buttonSkip.visibility = View.INVISIBLE
            textview.visibility = View.VISIBLE

        val input = Prefs.getString(PrefKeys.MOBILE_NUMBER, "")
        val number = input.replaceFirst("(\\d{2})(\\d{4})(\\d{3})(\\d+)".toRegex(), "$1 $2 $3 $4")
        tv_guardnumber.text = resources.getString(R.string.textgivemissedcall) + " +" + number

        ccp = findViewById(R.id.country_code_picker)
        ccp!!.setOnCountryChangeListener(this)
        countryCode = ccp!!.selectedCountryCode

        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.PROCESS_OUTGOING_CALLS,
                Manifest.permission.CALL_PHONE
            )
            .withListener(object : MultiplePermissionsListener {

                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    // check if all permissions are granted
                    if (report.areAllPermissionsGranted()) {
//                        Toast.makeText(getApplicationContext(), "All permissions are granted!", Toast.LENGTH_SHORT)
//                            .show();
                    }

                    // check for permanent denial of any permission
                    if (report.isAnyPermissionPermanentlyDenied) {
                        // show alert dialog navigating to Settings
                        showSettingsDialog()
                    }
                }


                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    if (token != null) {
                        token.continuePermissionRequest()
                    }
                }
            }).withErrorListener(object : PermissionRequestErrorListener {

                override fun onError(error: DexterError) {
                    Toast.makeText(applicationContext, "Error occurred! ", Toast.LENGTH_SHORT).show()
                }
            })
            .onSameThread()
            .check()



    }

    private fun showSettingsDialog() {
        val builder = AlertDialog.Builder(this@MobileNumberScreen)
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

    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivityForResult(intent, 101)
    }

    fun getAccountDetails(isdCode: String, MobNumber: String) {
        progressBar?.visibility = View.VISIBLE


        val req = GetAccountDetailsByMobReq(isdCode, MobNumber.substring(3))
        Log.d("getAccountDetails", req.toString())
        compositeDisposable.add(
            RetrofitClinet.instance.GetAccountDetailsByMobCall(CHAMPTOKEN, req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : CommonDisposable<GetAccountDetailsByMobResp<AccountByMobile>>() {
                    override fun onSuccessResponse(globalApiObject: GetAccountDetailsByMobResp<AccountByMobile>) {
                        if (globalApiObject.data != null) {
                            progressBar?.visibility = View.GONE

                            Prefs.putString("Retake", "Yes")

                            getLatestRecordData(MobNumber.substring(3),globalApiObject.data.accountByMobile[0].acAccntID.toString(),isdCode)


                            }else{
                            Prefs.putString("PHOTO","CAPTURE")
                            val d = Intent(this@MobileNumberScreen, BlockTabsActivity::class.java)
                            d.putExtra(FLOW_TYPE, DELIVERY)
                            d.putExtra(VISITOR_TYPE, DELIVERY)
                            d.putExtra(MOBILENUMBER, MobNumber.substring(3))
                            d.putExtra(COUNTRYCODE, isdCode)
                            startActivity(d)
                            finish()
                        }


                    }

                    override fun onErrorResponse(e: Throwable) {
                        progressBar?.visibility = View.GONE
                        deliveryFlow_launchNameEntryScreen()

                    }

                    override fun noNetowork() {
                        progressBar?.visibility = View.GONE
                        deliveryFlow_launchNameEntryScreen()

                    }

                })
        )

    }

    fun deliveryFlow_launchNameEntryScreen() {
        val d = Intent(this@MobileNumberScreen, BlockTabsActivity::class.java)
        d.putExtra(FLOW_TYPE, intent.getStringExtra(FLOW_TYPE))
        d.putExtra(VISITOR_TYPE, intent.getStringExtra(VISITOR_TYPE))
        d.putExtra(MOBILENUMBER, mobileNumber)
        d.putExtra(COUNTRYCODE, ccd)
        startActivity(d)
        finish()
    }


    fun Speak() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "say something")

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)

        } catch (e: Exception) {

            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCountrySelected() {
        countryCode = ccp!!.selectedCountryCode
        countryName = ccp!!.selectedCountryName
        Toast.makeText(this, "Country Code " + countryCode, Toast.LENGTH_SHORT).show()
        Toast.makeText(this, "Country Name " + countryName, Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_SPEECH_INPUT -> {
                if (resultCode == Activity.RESULT_OK && null != data) {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    Ed_phoneNum.text = result[0].replace(" ", "").trim()
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


    override fun onPause() {

        unregisterReceiver(receiver)

        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        val action = "android.intent.action.PHONE_STATE"
        registerReceiver(receiver, IntentFilter(action))
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    fun getLatestRecordData(mobileNumber:String,accountId:String,isdCode: String) {
        RetrofitClinet.instance.getLatestRecord(OYE247TOKEN,mobileNumber)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CommonDisposable<GetLatestRecord>() {

                override fun onSuccessResponse(getdata: GetLatestRecord) {
                    if(getdata.data!=null) {

                        if(getdata.data.visitorLatestRecord.vlEntryImg==""){
                            Prefs.putString("PHOTO","CAPTURE")
                        }else{
                            Prefs.putString("PHOTO","DONTCAPTURE")
                        }

                        val d = Intent(this@MobileNumberScreen, BlockTabsActivity::class.java)

                        d.putExtra(FLOW_TYPE, DELIVERY)
                        d.putExtra(VISITOR_TYPE, DELIVERY)
                        d.putExtra(MOBILENUMBER, getdata.data.visitorLatestRecord.vlMobile.substring(3, 13))
                        d.putExtra(COUNTRYCODE, "+91")
                        d.putExtra(PERSONNAME, getdata.data.visitorLatestRecord.vlfName + " " + getdata.data.visitorLatestRecord.vllName)
                        d.putExtra(ACCOUNT_ID, accountId)
                        d.putExtra(COMPANY_NAME, getdata.data.visitorLatestRecord.vlComName)
                        startActivity(d)
                        finish()
                    }  else{
                        Prefs.putString("PHOTO","CAPTURE")
                        val d = Intent(this@MobileNumberScreen, BlockTabsActivity::class.java)
                        d.putExtra(FLOW_TYPE, DELIVERY)
                        d.putExtra(VISITOR_TYPE, DELIVERY)
                        d.putExtra(MOBILENUMBER, mobileNumber)
                        d.putExtra(COUNTRYCODE, isdCode)
                        startActivity(d)
                        finish()
                    }

                }

                override fun onErrorResponse(e: Throwable) {
                    // visitorLog(unitId, personName, mobileNumb, desgn, workerType, staffID, unitName,wkEntryImg)
                    //  visitorLogBiometric(unitId, personName, mobileNumb, desgn, workerType, staffID, unitName,wkEntryImg)


                }

                override fun noNetowork() {
                    Toast.makeText(this@MobileNumberScreen, "No network call ", Toast.LENGTH_LONG).show()
                }
            })


    }

}