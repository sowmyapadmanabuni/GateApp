package com.oyespace.guards.kidexit

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.os.*
import android.provider.CallLog
import android.provider.Settings
import android.speech.RecognizerIntent
import android.telecom.TelecomManager
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.text.InputFilter
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.hbb20.CountryCodePicker
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.DexterError
import com.karumi.dexter.listener.PermissionRequestErrorListener
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.oyespace.guards.R
import com.oyespace.guards.activity.BaseKotlinActivity
import com.oyespace.guards.constants.PrefKeys
import com.oyespace.guards.listeners.PermissionCallback
import com.oyespace.guards.network.CommonDisposable
import com.oyespace.guards.network.RetrofitClinet
import com.oyespace.guards.pojo.*
import com.oyespace.guards.repo.StaffRepo
import com.oyespace.guards.repo.VisitorLogRepo
import com.oyespace.guards.utils.ConstantUtils
import com.oyespace.guards.utils.ConstantUtils.*
import com.oyespace.guards.utils.LocalDb
import com.oyespace.guards.utils.Prefs
import com.oyespace.guards.utils.Utils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_mobile_number.*
import java.util.*


class KidExitMobileNumberScreen : BaseKotlinActivity(), View.OnClickListener,
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

    // private var Ed_phoneNum:String?=null

    private val REQUEST_CODE_SPEECH_INPUT = 100

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.Btn_SendOtp -> {

                Toast.makeText(this@KidExitMobileNumberScreen, "Coming soon", Toast.LENGTH_LONG)
                    .show()
            }

            R.id.buttonSkip -> {
                val d = Intent(this@KidExitMobileNumberScreen, KidExitNameEntryScreen::class.java)
                Log.d(
                    "intentdata MobileNumber",
                    "buttonNext " + intent.getStringExtra(UNITNAME) + " " + intent.getStringExtra(
                        UNITID
                    )
                            + " " + textview.text + " " + countryCode
                )
                d.putExtra(FLOW_TYPE, intent.getStringExtra(FLOW_TYPE))
                d.putExtra(VISITOR_TYPE, intent.getStringExtra(VISITOR_TYPE))
                d.putExtra(COMPANY_NAME, intent.getStringExtra(COMPANY_NAME))
                d.putExtra(UNITID, intent.getStringExtra(UNITID))
                d.putExtra(UNITNAME, intent.getStringExtra(UNITNAME))
                d.putExtra(MOBILENUMBER, "")
                d.putExtra(COUNTRYCODE, "")
                d.putExtra(UNIT_ACCOUNT_ID, intent.getStringExtra(ConstantUtils.UNIT_ACCOUNT_ID))
                d.putExtra(BLOCK_ID, intent.getStringExtra(BLOCK_ID))
                d.putExtra(VISITOR_PURPOSE,intent.getStringExtra(VISITOR_PURPOSE))
                startActivity(d)
                finish()

            }

            R.id.btn_nobalance -> {

                val d = Intent(this@KidExitMobileNumberScreen, KidExit_MobileNumberScreenwithOTP::class.java)
                d.putExtra(UNITID, intent.getStringExtra(UNITID))
                d.putExtra(UNITNAME, intent.getStringExtra(UNITNAME))
                d.putExtra(FLOW_TYPE, intent.getStringExtra(FLOW_TYPE))
                d.putExtra(VISITOR_TYPE, intent.getStringExtra(VISITOR_TYPE))
                d.putExtra(COMPANY_NAME, intent.getStringExtra(COMPANY_NAME))
                d.putExtra(UNIT_ACCOUNT_ID, intent.getStringExtra(ConstantUtils.UNIT_ACCOUNT_ID))
                d.putExtra(BLOCK_ID, intent.getStringExtra(BLOCK_ID))
                d.putExtra(VISITOR_PURPOSE,intent.getStringExtra(VISITOR_PURPOSE))
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

                    val flow = intent.getStringExtra(FLOW_TYPE)
                    val allowEntry = when (flow) {
                        STAFF_REGISTRATION -> !StaffRepo.checkExistingStaffForPhone(mobileNumber!!)
                        else -> VisitorLogRepo.allowEntry("", mobileNumber)
                    }

                    if (!allowEntry) {
                        //                        Toast.makeText(this,"Mobile Number already used for Visitor Entry", Toast.LENGTH_SHORT).show()
                        val builder = AlertDialog.Builder(this@KidExitMobileNumberScreen)
                        // builder.setTitle("Vendor Entry already done")
                        builder.setMessage(
                            when (flow) {
                                STAFF_REGISTRATION -> "Staff already registered"
                                else -> "This number is being used by a person already in"

                            }
                        )
                        builder.setPositiveButton("Ok") { dialog, which ->


                            dialog.cancel()
//                            val d = Intent(this@MobileNumberScreen, Dashboard::class.java)
//                            startActivity(d)
                            finish()
                        }
                        builder.setCancelable(false)
                        builder.show()

                    } else {

                        if ((intent.getStringExtra(FLOW_TYPE).equals(STAFF_REGISTRATION))) {

                            GetWorkersListByMobileNumberAndAssocID(
                                ccd.toString() + mobileNumber.toString(),
                                Prefs.getInt(ASSOCIATION_ID, 0)
                            )
                        } else {
                            getAccountDetails(ccd.toString(), mobileNumber.toString())

                        }

                    }

                } else {
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

        //  Toast.makeText(applicationContext, "coming", Toast.LENGTH_LONG).show();
        //  Toast.makeText(this@MobileNumberScreen,intent.getStringExtra( "RESIDENT_NUMBER"),Toast.LENGTH_LONG).show()


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
                                try {

                                    ccd = number.substring(0, 3)

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

        addEntries()
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
//                val i_delivery = Intent(this@MobileNumberScreen, Dashboard::class.java)
//                startActivity(i_delivery)
                finish()
            }
        }
        timer.start()


        Log.d(
            "intentdata MobileNumber",
            "" + intent.getStringExtra(UNITNAME) + " " + intent.getStringExtra(UNITID)
        )

        btn_mic.setOnClickListener {
            Speak()
        }

        if (intent.getStringExtra(FLOW_TYPE).equals(STAFF_REGISTRATION)) {
            // buttonSkip.setVisibility(View.VISIBLE)
            // if (Prefs.getString(PrefKeys.MODEL_NUMBER, null) == "Nokia 2.1") {
            if (workType.contains(intent.getStringExtra(COMPANY_NAME))) {
                buttonSkip.visibility = View.VISIBLE
            } else {
                buttonSkip.visibility = View.INVISIBLE
            }
            // }
//        else{
//                buttonSkip.visibility=View.INVISIBLE
//            }
            img_logo.visibility = View.VISIBLE
//            Ed_phoneNum.setVisibility(View.VISIBLE)
//            textview.visibility = View.GONE
            Ed_phoneNum.visibility = View.GONE
            textview.visibility = View.VISIBLE
        } else {
            buttonSkip.visibility = View.INVISIBLE
            textview.visibility = View.VISIBLE
        }

        val mobilePHONEDATA: String = Prefs.getString(PrefKeys.MOBILE_NUMBER, "")


        val countrycode = Prefs.getString(PrefKeys.COUNTRY_CODE, "")

        val input = Prefs.getString(PrefKeys.MOBILE_NUMBER, "")
        // val number = input.replaceFirst("(\\d{3})(\\d{3})(\\d+)".toRegex(), "$1 $2 $3")
        val number = input.replaceFirst("(\\d{2})(\\d{4})(\\d{3})(\\d+)".toRegex(), "$1 $2 $3 $4")
        tv_guardnumber.text = resources.getString(R.string.textgivemissedcall) + " +" + number

        // tv_guardnumber.setText(resources.getString(R.string.textgivemissedcall)+" "+"+"+countrycode+" "+number)


//        Ed_phoneNum.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(s: Editable) {
//                val x = s.toString()
//                if (x.startsWith("0") || x.startsWith("1") || x.startsWith("2") || x.startsWith("3") || x.startsWith("4")
//                ) {
//                    //your stuff here
//                    Btn_SendOtp.setEnabled(false)
//                    Btn_SendOtp.setVisibility(View.INVISIBLE)
//
//                } else if (x.startsWith("5") ||x.startsWith("6") || x.startsWith("7") || x.startsWith("8") || x.startsWith("9")) {
//                    Btn_SendOtp.setEnabled(true)
//                    val maxLength = 10
//                    Ed_phoneNum.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength)))
//                  //  Btn_SendOtp.setVisibility(View.VISIBLE)
//                }
//
//            }
//
//            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
//
//            }
//
//            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//
//            }
//
//        });

//        Ed_phoneNum.setText(getCallDetails(this@MobileNumberScreen))

        //to set default country code as India
        // ccp!!.setDefaultCountryUsingNameCode("+91")

        ccp = findViewById(R.id.country_code_picker)
        ccp!!.setOnCountryChangeListener(this)
        countryCode = ccp!!.selectedCountryCode

        Btn_SendOtp.setOnClickListener {

            fun DisplayProgressDialog() {

                pDialog = ProgressDialog(this@KidExitMobileNumberScreen)
                pDialog.setMessage("Loading..")
                pDialog.setCancelable(false)
                pDialog.isIndeterminate = false
                pDialog.show()
            }

            if (TextUtils.isEmpty(Ed_phoneNum.text.toString()) || countryCode!!.startsWith("+91")) {
                Toast.makeText(this, "Enter your phone number", Toast.LENGTH_SHORT).show()
                val maxLength = 10
                Ed_phoneNum.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength))

            } else if (countryCode!!.startsWith("+91") && Ed_phoneNum.length() <= 10) {
                Toast.makeText(this, "number should be 10 digits", Toast.LENGTH_LONG).show()
            } else {
                sendotp()
            }

        }

//           // Ed_phoneNum.text.toString()
//            if(TextUtils.isEmpty(Ed_phoneNum.text.toString())||countryCode !!.startsWith("+91")) {
//                Toast.makeText(this, "Enter your phone number", Toast.LENGTH_SHORT).show()
//            }
//            else {
//                val intent = Intent(this, verify_oyp::class.java)
//                intent.putExtra("countryCode", countryCode)
//                intent.putExtra("countryname", countryName)
//                intent.putExtra("phonenumber", Ed_phoneNum.text.toString())
//                startActivity(intent)
//            }
//
//        }

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

//        val telephony = this@MobileNumberScreen.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
//        telephony.listen(object : PhoneStateListener() {
//            override fun onCallStateChanged(state: Int, incomingNumber: String) {
//                super.onCallStateChanged(state, incomingNumber)
//                when (state) {
//
//                    // not getting incoming number in latest version of android
//                    TelephonyManager.CALL_STATE_RINGING -> {
//                        Log.e("Shalini Pareek", "incomingNumber: $incomingNumber")
//                        phonenumber = "$incomingNumber"
//                        if (phonenumber?.length == 13) {
//                            Log.d("Shalini length", "length: " + phonenumber?.substring(3, 13))
//                            textview.text = phonenumber?.substring(3, 13)
////                            ccp.setCountryForPhoneCode()
//
//                            //TODO romove the data
//
//                        } else if (phonenumber?.length == 10) {
//                            textview.text = phonenumber
////                        }else if(phonenumber.contains("+91")){
////                            textview.text=phonenumber?.replace("+91","")
//                        } else {
//                            textview.text = phonenumber
//                        }
//
//                    }
//
//                }
//            }
//        }, PhoneStateListener.LISTEN_CALL_STATE)

    }

    private fun showSettingsDialog() {
        val builder = AlertDialog.Builder(this@KidExitMobileNumberScreen)
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

//    override fun onBackPressed() {
//        super.onBackPressed()
//        if(intent.getStringExtra(COMPANY_NAME).equals("Others")){
//            val intent = Intent(this@MobileNumberScreen, PurposeScreen::class.java)
//            intent.putExtra(FLOW_TYPE, getIntent().getStringExtra(FLOW_TYPE))
//            intent.putExtra(VISITOR_TYPE, getIntent().getStringExtra(VISITOR_TYPE))
//            intent.putExtra(COMPANY_NAME, getIntent().getStringExtra(COMPANY_NAME))
//            intent.putExtra(UNITID, getIntent().getStringExtra(UNITID))
//            intent.putExtra(UNITNAME, getIntent().getStringExtra(UNITNAME))
//            startActivity(intent)
//        }
//        else{
//            val intent = Intent(this@MobileNumberScreen, UnitListActivity::class.java)
//            intent.putExtra(FLOW_TYPE, getIntent().getStringExtra(FLOW_TYPE))
//            intent.putExtra(VISITOR_TYPE, getIntent().getStringExtra(VISITOR_TYPE))
//            intent.putExtra(COMPANY_NAME, getIntent().getStringExtra(COMPANY_NAME))
//            intent.putExtra(UNITID, getIntent().getStringExtra(UNITID))
//            intent.putExtra(UNITNAME, getIntent().getStringExtra(UNITNAME))
//            startActivity(intent)
//        }
//
//    }

    fun getCallDetails(context: Context): String {

        val sb = StringBuffer()
        var phNumber = "k"
        //  Cursor managedCursor = managedQuery(CallLog.Calls.CONTENT_URI, null, null, null, null);
        val managedCursor = context.contentResolver.query(CallLog.Calls.CONTENT_URI, null, null, null, null)
        val number = managedCursor!!.getColumnIndex(CallLog.Calls.NUMBER)
        Log.d("CallNom", number.toString())// call date

        val type = managedCursor.getColumnIndex(CallLog.Calls.TYPE)
        val date = managedCursor.getColumnIndex(CallLog.Calls.DATE)
        val duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION)
        sb.append("Call Details :")
        while (managedCursor.moveToNext()) {
            phNumber = managedCursor.getString(number) // mobile number
            val callType = managedCursor.getString(type) // call type
            val callDate = managedCursor.getString(date)
            Log.d("CallTime2", phNumber)// call date
            val callDayTime = Date(java.lang.Long.valueOf(callDate))
            val callDuration = managedCursor.getString(duration)
            var dir: String? = null
            val dircode = Integer.parseInt(callType)
            when (dircode) {
                CallLog.Calls.OUTGOING_TYPE -> dir = "OUTGOING"

                CallLog.Calls.INCOMING_TYPE -> dir = "INCOMING"

                CallLog.Calls.MISSED_TYPE -> dir = "MISSED"
            }
            Log.d(
                "CallTime1 ",
                callDayTime.toString() + "Phone Number:--- $phNumber \\nCall Type:--- $dir \\nCall Date:--- $callDayTime \\nCall duration in sec :--- $callDuration"
            )

            sb.append("\nPhone Number:--- $phNumber \nCall Type:--- $dir \nCall Date:--- $callDayTime \nCall duration in sec :--- $callDuration")
            sb.append("\n----------------------------------")
        }
        managedCursor.close()
        phNumber = phNumber.replace("[-+^() ]*".toRegex(), "")
        val i = phNumber.length
        return if (i > 10) {

            phNumber.substring(i - 10)
        } else {
            //contactName.setText(name);
            phNumber
            //contactEmail.setText(email);
        }

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

                            if ((intent.getStringExtra(FLOW_TYPE).equals(STAFF_REGISTRATION))) {
                                val d = Intent(this@KidExitMobileNumberScreen, KidExitNameEntryScreen::class.java)
                                d.putExtra(FLOW_TYPE, intent.getStringExtra(FLOW_TYPE))
                                d.putExtra(VISITOR_TYPE, intent.getStringExtra(VISITOR_TYPE))
                                d.putExtra(COMPANY_NAME, intent.getStringExtra(COMPANY_NAME))
                                d.putExtra(UNITID, intent.getStringExtra(UNITID))
                                d.putExtra(UNITNAME, intent.getStringExtra(UNITNAME))
                                d.putExtra(MOBILENUMBER, mobileNumber)
                                d.putExtra(COUNTRYCODE, ccd)
                                d.putExtra(
                                    UNIT_ACCOUNT_ID,
                                    intent.getStringExtra(ConstantUtils.UNIT_ACCOUNT_ID)
                                )
                                d.putExtra(BLOCK_ID, intent.getStringExtra(BLOCK_ID))
                                d.putExtra(
                                    PERSONNAME,
                                    globalApiObject.data.accountByMobile[0].acfName + " " + globalApiObject.data.accountByMobile[0].aclName
                                )
                                d.putExtra(VISITOR_PURPOSE,intent.getStringExtra(VISITOR_PURPOSE))
                                startActivity(d)
                                finish()
                            } else {
                                val d = Intent(this@KidExitMobileNumberScreen, KidExitNameEntryScreen::class.java)
                                d.putExtra(UNITID, intent.getStringExtra(UNITID))
                                d.putExtra(UNITNAME, intent.getStringExtra(UNITNAME))
                                d.putExtra(FLOW_TYPE, intent.getStringExtra(FLOW_TYPE))
                                d.putExtra(VISITOR_TYPE, intent.getStringExtra(VISITOR_TYPE))
                                d.putExtra(COMPANY_NAME, intent.getStringExtra(COMPANY_NAME))
                                d.putExtra(MOBILENUMBER, MobNumber)
                                d.putExtra(COUNTRYCODE, isdCode)
                                d.putExtra(PERSONNAME, globalApiObject.data.accountByMobile[0].acfName + " " + globalApiObject.data.accountByMobile[0].aclName)
                                d.putExtra(ACCOUNT_ID, globalApiObject.data.accountByMobile[0].acAccntID)
                                d.putExtra(UNIT_ACCOUNT_ID, intent.getStringExtra(ConstantUtils.UNIT_ACCOUNT_ID))
                                d.putExtra(BLOCK_ID, intent.getStringExtra(BLOCK_ID))
                                d.putExtra(VISITOR_PURPOSE,intent.getStringExtra(VISITOR_PURPOSE))
                                startActivity(d)
                                finish()
                            }

                        } else {
                            progressBar?.visibility = View.GONE
                            deliveryFlow_launchNameEntryScreen()
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
        val d = Intent(this@KidExitMobileNumberScreen, KidExitNameEntryScreen::class.java)
        d.putExtra(FLOW_TYPE, intent.getStringExtra(FLOW_TYPE))
        d.putExtra(VISITOR_TYPE, intent.getStringExtra(VISITOR_TYPE))
        d.putExtra(COMPANY_NAME, intent.getStringExtra(COMPANY_NAME))
        d.putExtra(UNITID, intent.getStringExtra(UNITID))
        d.putExtra(UNITNAME, intent.getStringExtra(UNITNAME))
        d.putExtra(MOBILENUMBER, mobileNumber)
        d.putExtra(COUNTRYCODE, ccd)
        d.putExtra(UNIT_ACCOUNT_ID, intent.getStringExtra(ConstantUtils.UNIT_ACCOUNT_ID))
        d.putExtra(BLOCK_ID, intent.getStringExtra(BLOCK_ID))
        d.putExtra(VISITOR_PURPOSE,intent.getStringExtra(VISITOR_PURPOSE))
        startActivity(d)
        finish()
    }

    fun sendotp() {

        countryCode.toString()
        Ed_phoneNum.toString()

        val req = GetOTPReq(countryCode.toString(), Ed_phoneNum.toString())
        compositeDisposable.add(
            RetrofitClinet.instance.getOTPCall(CHAMPTOKEN, req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : CommonDisposable<GetOTPResp>() {
                    override fun onSuccessResponse(globalApiObject: GetOTPResp) {
                        if (globalApiObject.success == true) {
                            Utils.showToast(applicationContext, "OTP Sent")
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
                        showProgress()
                    }

                    override fun onDismissProgress() {
                        dismissProgress()
                    }
                })
        )


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
        // Toast.makeText(this@MobileNumberScreen,"Coming1",Toast.LENGTH_LONG).show()
        super.onResume()
        val action = "android.intent.action.PHONE_STATE"
        registerReceiver(receiver, IntentFilter(action))
    }

    fun addEntries() {
        workType.add("Sweeper")
        workType.add("Maid")
        workType.add("HouseKeeping")
        workType.add("Cook")
        workType.add("Gardener")

    }

    override fun onBackPressed() {
        super.onBackPressed()
//        val i_delivery = Intent(this@MobileNumberScreen, Dashboard::class.java)
//        startActivity(i_delivery)
        finish()
    }

    private fun GetWorkersListByMobileNumberAndAssocID(WKMobile: String, ASAssnID: Int) {


        val req = GetWorkersListByMobileNumberReq(WKMobile, ASAssnID)

        compositeDisposable.add(
            RetrofitClinet.instance.GetWorkersListByMobileNumberAndAssocID(ConstantUtils.OYE247TOKEN, req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : CommonDisposable<GetWorkersListByMobileNumberResp>() {
                    override fun onSuccessResponse(globalApiObject: GetWorkersListByMobileNumberResp) {
                        if (globalApiObject.success == true) {

                            if (globalApiObject.data.message.equals("Invalid MobileNumber")) {

                                getAccountDetails(ccd.toString(), mobileNumber.toString())


                            } else {


                                val builder = AlertDialog.Builder(this@KidExitMobileNumberScreen)
                                // builder.setTitle("Vendor Entry already done")
                                builder.setMessage(globalApiObject.data.message + ". Please Try again")
                                builder.setPositiveButton("Ok") { dialog, which ->


                                    dialog.cancel()
                                    textview!!.text = ""

//                                    val d = Intent(this@MobileNumberScreen, Dashboard::class.java)
//                                    startActivity(d)
//                                    finish()
                                }
                                builder.setCancelable(false)
                                builder.show()
                                // Toast.makeText(this@EditStaffActivity,globalApiObject.data.message,Toast.LENGTH_LONG).show()

                            }
                        }
                    }

                    override fun onErrorResponse(e: Throwable) {
//                    Utils.showToast(applicationContext, getString(R.string.some_wrng))
                        Log.d("CreateVisitorLogResp", "onErrorResponse  " + e.toString())
                    }

                    override fun noNetowork() {
//                    Utils.showToast(applicationContext, getString(R.string.no_internet))
                    }

                    override fun onShowProgress() {
                    }

                    override fun onDismissProgress() {
                    }
                })
        )
    }

    @SuppressLint("PrivateApi")
    fun endCall(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ANSWER_PHONE_CALLS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                telecomManager.endCall()
                return true
            }
            return false
        }
        //use unofficial API for older Android versions, as written here: https://stackoverflow.com/a/8380418/878126
        try {
            val telephonyClass = Class.forName("com.android.internal.telephony.ITelephony")
            val telephonyStubClass = telephonyClass.classes[0]
            val serviceManagerClass = Class.forName("android.os.ServiceManager")
            val serviceManagerNativeClass = Class.forName("android.os.ServiceManagerNative")
            val getService = serviceManagerClass.getMethod("getService", String::class.java)
            val tempInterfaceMethod =
                serviceManagerNativeClass.getMethod("asInterface", IBinder::class.java)
            val tmpBinder = Binder()
            tmpBinder.attachInterface(null, "fake")
            val serviceManagerObject = tempInterfaceMethod.invoke(null, tmpBinder)
            val retbinder = getService.invoke(serviceManagerObject, "phone") as IBinder
            val serviceMethod = telephonyStubClass.getMethod("asInterface", IBinder::class.java)
            val telephonyObject = serviceMethod.invoke(null, retbinder)
            val telephonyEndCall = telephonyClass.getMethod("endCall")
            telephonyEndCall.invoke(telephonyObject)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

}