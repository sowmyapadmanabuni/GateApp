package com.oyespace.guards.activity

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.CallLog
import android.provider.Settings
import android.speech.RecognizerIntent
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
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
import com.oyespace.guards.BackgroundSyncReceiver
import com.oyespace.guards.R
import com.oyespace.guards.camtest.AddCarFragment
import com.oyespace.guards.constants.PrefKeys
import com.oyespace.guards.listeners.PermissionCallback
import com.oyespace.guards.network.CommonDisposable
import com.oyespace.guards.network.ResponseHandler
import com.oyespace.guards.network.RetrofitClinet
import com.oyespace.guards.pojo.*
import com.oyespace.guards.repo.VisitorLogRepo
import com.oyespace.guards.staffManaualEntry.ManulBlockSelectionActivity
import com.oyespace.guards.utils.*
import com.oyespace.guards.utils.ConstantUtils.*
import com.oyespace.guards.utils.Utils.showToast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_mobile_number.*
import java.util.*


class MobileNumberforEntryScreen : BaseKotlinActivity(), View.OnClickListener, ResponseHandler, CountryCodePicker.OnCountryChangeListener {
    override fun onSuccess(response: String?, data: Any?, urlId: Int, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onFailure(e: java.lang.Exception?, urlId: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    var receiver: BroadcastReceiver? = null
    val workType: ArrayList<String> = ArrayList()
    private var ccp: CountryCodePicker? = null
    private var countryCode: String? = null
    private var countryName: String? = null
    lateinit var pDialog: ProgressDialog
    var phonenumber: String? = null
    lateinit var txt_assn_name: TextView
    lateinit var txt_gate_name: TextView
    lateinit var txt_device_name: TextView
    val laststate: Int? = null
    var buttonSkip: Button? = null
    var btn_manualentry: Button? = null
    var lytt:LinearLayout?=null
    var ccd: String? = null
    var mobileNumber: String? = null
    lateinit var btn_nobalance: Button
    lateinit var timer: TextView


    // private var Ed_phoneNum:String?=null

    private val REQUEST_CODE_SPEECH_INPUT = 100

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.btn_nobalance -> {

                val d = Intent(this@MobileNumberforEntryScreen, MobileNumberEntryScreenwithOTP::class.java)
                d.putExtra(UNITID, intent.getStringExtra(UNITID))
                d.putExtra("FIRSTNAME", intent.getStringExtra("FIRSTNAME"))
                d.putExtra("LASTNAME", intent.getStringExtra("LASTNAME"))
                d.putExtra(MOBILENUMBER, intent.getStringExtra(MOBILENUMBER))
                d.putExtra("DESIGNATION", intent.getStringExtra("DESIGNATION"))
                d.putExtra("WORKTYPE", intent.getStringExtra("WORKTYPE"))
                d.putExtra(WORKER_ID, intent.getIntExtra(WORKER_ID, 0))
                d.putExtra(UNITNAME, intent.getStringExtra(UNITNAME))
                d.putExtra("Image", intent.getStringExtra("Image"))
                startActivity(d)
                finish()

            }


            R.id.buttonNext -> {

                if (useDummyValues) {
                    textview.text = "+91${dummyPhone}"
                    ccd = "+91"
                    mobileNumber = dummyPhone
                }

                if (textview.text.isNotEmpty()) {

                    Log.v("NUMBER MATCH", intent.getStringExtra(MOBILENUMBER) + ".." + textview.text)
                    val phoneNumber: String = textview.text.toString()
                    if (phoneNumber.contains(intent.getStringExtra(MOBILENUMBER))) {

                        val allowEntry = VisitorLogRepo.allowEntry("", phoneNumber)

                        if (!allowEntry) {
                            Toast.makeText(this, "Duplicate Entry not allowed", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            getVisitorByWorkerId(Prefs.getInt(ASSOCIATION_ID, 0), intent.getIntExtra(ConstantUtils.WORKER_ID, 0), intent.getStringExtra(UNITID), intent.getStringExtra("FIRSTNAME"), intent.getStringExtra(MOBILENUMBER), intent.getStringExtra("DESIGNATION"), intent.getStringExtra("WORKTYPE"), intent.getIntExtra(ConstantUtils.WORKER_ID, 0), intent.getStringExtra(UNITNAME), intent.getStringExtra("Image"))
                        }


                    } else {
                        buttonNext.isEnabled = true
                        buttonNext.isClickable = true
                        textview.text = ""
                        Toast.makeText(this, "Enter valid staff number", Toast.LENGTH_SHORT).show()
                    }

                }


            }
        }
    }

//TODO sumeeth has reomoved this fucntion for crashing porpose

    val entries: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocale(Prefs.getString(PrefKeys.LANGUAGE, null))
        setContentView(R.layout.activity_mobilenumberforentry)
        btn_nobalance = findViewById(R.id.btn_nobalance)
        btn_manualentry = findViewById(R.id.btn_manualentry)
        lytt=findViewById(R.id.lytt)
        timer = findViewById(R.id.timer)

        if(intent.getStringExtra(MOBILENUMBER).isEmpty()){
            lytt?.visibility=View.INVISIBLE
            btn_nobalance.visibility=View.INVISIBLE
            tv_guardnumber.visibility=View.INVISIBLE
            timertext.visibility=View.GONE
            timer.visibility= View.GONE



        }
        else{
            lytt?.visibility=View.VISIBLE
            btn_nobalance.visibility=View.VISIBLE
            tv_guardnumber.visibility=View.VISIBLE
            timertext.visibility=View.VISIBLE
            timer.visibility= View.VISIBLE
        }

        btn_manualentry!!.setOnClickListener {


            Prefs.putString(TYPE, "Entry")
            val d = Intent(this@MobileNumberforEntryScreen, ManulBlockSelectionActivity::class.java)
            d.putExtra(UNITID, intent.getStringExtra(UNITID))
            d.putExtra("FIRSTNAME", intent.getStringExtra("FIRSTNAME"))
            d.putExtra("LASTNAME", intent.getStringExtra("LASTNAME"))
            d.putExtra(MOBILENUMBER, intent.getStringExtra(MOBILENUMBER))
            d.putExtra("DESIGNATION", intent.getStringExtra("DESIGNATION"))
            d.putExtra("WORKTYPE", intent.getStringExtra("WORKTYPE"))
            d.putExtra(WORKER_ID, intent.getIntExtra(WORKER_ID, 0))
            d.putExtra(UNITNAME, intent.getStringExtra(UNITNAME))
            d.putExtra("BIRTHDAY", intent.getStringExtra("BIRTHDAY"))
            d.putExtra(FLOW_TYPE, STAFF_REGISTRATION)
            d.putExtra(VISITOR_TYPE, "STAFF")
            d.putExtras(intent)
            startActivity(d)
            finish()

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
                val telephony = context?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                telephony.listen(object : PhoneStateListener() {

                    override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                        super.onCallStateChanged(state, phoneNumber)
                        if (state == TelephonyManager.CALL_STATE_RINGING) {

                            val bundle = intent?.extras
                            val number = bundle?.getString("incoming_number")

                            //   Toast.makeText(applicationContext, number, Toast.LENGTH_LONG).show();
                            if (textview != null && number != null) {
                                // textview.text = number.replace("+91", "")
                                textview.text = number

                                ccd = number.substring(0, 3)

                                mobileNumber = number.substring(3, 13)

                                // GetWorkersListByMobileNumberAndAssocID(ccd.toString()+mobileNumber.toString(),Prefs.getInt(ASSOCIATION_ID, 0))
                            }
                            LocalDb.disconnectCall(context)
                        }
                    }

                }, PhoneStateListener.LISTEN_CALL_STATE)

                //
            }
        }

        buttonSkip = findViewById(R.id.buttonSkip)
        buttonSkip?.visibility = View.INVISIBLE
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

                val remainedSecs: Long = millisUntilFinished / 1000
                timer.text = ("0" + (remainedSecs / 60) + ":" + (remainedSecs % 60))// manage it accordign to you
            }

            override fun onFinish() {
                finish()
            }
        }
        timer.start()


        val mobilePHONEDATA: String = Prefs.getString(PrefKeys.MOBILE_NUMBER, "")


        val input = Prefs.getString(PrefKeys.MOBILE_NUMBER, "")
        val countrycode = Prefs.getString(PrefKeys.COUNTRY_CODE, "")

        val number = input.replaceFirst("(\\d{3})(\\d{3})(\\d+)".toRegex(), "$1 $2 $3")



        tv_guardnumber.text = resources.getString(R.string.textgivemissedcall) + " " + "+" + countrycode + " " + number

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
                    Toast.makeText(applicationContext, "Error occurred! ", Toast.LENGTH_SHORT)
                        .show()
                }
            })
            .onSameThread()
            .check()

    }

    private fun showSettingsDialog() {
        val builder = AlertDialog.Builder(this@MobileNumberforEntryScreen)
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

        val req = GetAccountDetailsByMobReq("+" + isdCode, MobNumber)
        Log.d("getAccountDetails", req.toString())
        compositeDisposable.add(
            RetrofitClinet.instance.GetAccountDetailsByMobCall(CHAMPTOKEN, req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : CommonDisposable<GetAccountDetailsByMobResp<AccountByMobile>>() {
                    override fun onSuccessResponse(globalApiObject: GetAccountDetailsByMobResp<AccountByMobile>) {
                        if (globalApiObject.data != null) {
                            Log.d("getAccountDetails", globalApiObject.data.toString())
                            Log.d("getAccountDetails", globalApiObject.data.accountByMobile.toString())
                            val d = Intent(this@MobileNumberforEntryScreen, AddCarFragment::class.java)

                            Log.d(
                                "intentdata NameEntr",
                                "buttonNext " + intent.getStringExtra(UNITNAME) + " " + intent.getStringExtra(
                                    UNITID
                                )
                                        + " " + intent.getStringExtra(MOBILENUMBER) + " " + intent.getStringExtra(
                                    COUNTRYCODE
                                ) + " "
                                        + globalApiObject.data.accountByMobile[0].acfName + " " + globalApiObject.data.accountByMobile[0].aclName
                            )
                            d.putExtra(UNITID, intent.getStringExtra(UNITID))
                            d.putExtra(UNITNAME, intent.getStringExtra(UNITNAME))
                            d.putExtra(FLOW_TYPE, intent.getStringExtra(FLOW_TYPE))
                            d.putExtra(VISITOR_TYPE, intent.getStringExtra(VISITOR_TYPE))
                            d.putExtra(COMPANY_NAME, intent.getStringExtra(COMPANY_NAME))
                            d.putExtra(MOBILENUMBER, MobNumber)
                            d.putExtra(COUNTRYCODE, isdCode)
                            d.putExtra(
                                PERSONNAME,
                                globalApiObject.data.accountByMobile[0].acfName + " " + globalApiObject.data.accountByMobile[0].aclName
                            )
                            d.putExtra(ACCOUNT_ID, globalApiObject.data.accountByMobile[0].acAccntID)

                            startActivity(d)
                            finish()

                        } else {
                            deliveryFlow_launchNameEntryScreen()
                        }
                    }

                    override fun onErrorResponse(e: Throwable) {
                        deliveryFlow_launchNameEntryScreen()

                    }

                    override fun noNetowork() {
                        deliveryFlow_launchNameEntryScreen()

                    }

                })
        )

    }

    fun deliveryFlow_launchNameEntryScreen() {
        val d = Intent(this@MobileNumberforEntryScreen, NameEntryScreen::class.java)
        Log.d(
            "intentdata MobileNumber",
            "buttonNext " + intent.getStringExtra(UNITNAME) + " " + intent.getStringExtra(UNITID)
                    + " " + textview.text + " " + countryCode
        )
        d.putExtra(FLOW_TYPE, intent.getStringExtra(FLOW_TYPE))
        d.putExtra(VISITOR_TYPE, intent.getStringExtra(VISITOR_TYPE))
        d.putExtra(COMPANY_NAME, intent.getStringExtra(COMPANY_NAME))
        d.putExtra(UNITID, intent.getStringExtra(UNITID))
        d.putExtra(UNITNAME, intent.getStringExtra(UNITNAME))
        d.putExtra(MOBILENUMBER, textview.text.toString())
        d.putExtra(COUNTRYCODE, countryCode)

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

//        val telephony = this@MobileNumberforEntryScreen.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
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
//                           // Toast.makeText(this@MobileNumberScreen,phonenumber?.substring(3, 13),Toast.LENGTH_LONG).show()
//                            textview.text = phonenumber?.substring(3, 13)
//
////                            ccp.setCountryForPhoneCode()
//
//                            //TODO romove the data
//
//                        } else if (phonenumber?.length == 10) {
//                            textview.text = phonenumber
//
////                        }else if(phonenumber.contains("+91")){
////                            textview.text=phonenumber?.replace("+91","")
//                        } else {
//                           // textview.text = phonenumber)
//                            if(textview.text.toString().length==0){
//                                val i=Intent(this@MobileNumberforEntryScreen,Dashboard::class.java)
//                                startActivity(i)
//
//                            }
//                        }
//
//                    }
//TelephonyManager.CALL_STATE_IDLE->{
//    if (laststate==TelephonyManager.CALL_STATE_RINGING){
//        if(textview.text.toString().equals(null)){
//            val i=Intent(this@MobileNumberforEntryScreen,Dashboard::class.java)
//            startActivity(i)
//
//        }
//    }
//
//}
//
//                }
//
//
//
//            }
//        }, PhoneStateListener.LISTEN_CALL_STATE)
//
////        if(textview.text.toString().length==0){
////            val i=Intent(this@MobileNumberScreen,DashBoard::class.java)
////            startActivity(i)
////
////        }
    }

    private fun visitorLog(
        unitId: String, personName: String, mobileNumb: String, desgn: String,
        workerType: String, staffID: Int, unitName: String
    ) {


        var memID: Int = 410
        if (BASE_URL.contains("dev", true)) {
            memID = 64
        } else if (BASE_URL.contains("uat", true)) {
            memID = 64
        }
//        var memID:Int=64;
//        if(!BASE_URL.contains("dev",true)){
//            memID=410;
//        }
        var SPPrdImg1=""
        var SPPrdImg2=""
        var SPPrdImg3=""
        var SPPrdImg4=""
        var SPPrdImg5=""
        var SPPrdImg6=""
        var SPPrdImg7=""
        var SPPrdImg8=""
        var SPPrdImg9=""
        var SPPrdImg10=""
        val req = CreateVisitorLogReq(Prefs.getInt(ASSOCIATION_ID,0), staffID,
            unitName, unitId, desgn,
            personName,LocalDb.getAssociation()!!.asAsnName,0,"",mobileNumb,
            "","","","",
            1,
            "Staff Missed call Entry",
            SPPrdImg1,
            SPPrdImg2,
            SPPrdImg3,
            SPPrdImg4,
            SPPrdImg5
            , SPPrdImg6, SPPrdImg7, SPPrdImg8, SPPrdImg9, SPPrdImg10,"",intent.getStringExtra("Image"),Prefs.getString(ConstantUtils.GATE_NO, ""),
            DateTimeUtils.getCurrentTimeLocal(),"","","","","","","","",""
            , ""
        )
        Log.d("CreateVisitorLogResp","StaffEntry "+req.toString())

        CompositeDisposable().add(
            RetrofitClinet.instance.createVisitorLogCall(OYE247TOKEN, req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : CommonDisposable<CreateVisitorLogResp<VLRData>>() {
                    override fun onSuccessResponse(globalApiObject: CreateVisitorLogResp<VLRData>) {
                        if (globalApiObject.success == true) {
                            // Utils.showToast(applicationContext, intToString(globalApiObject.data.visitorLog.vlVisLgID))

                            FirebaseDBUtils.updateFirebaseColor(globalApiObject.data.visitorLog.vlVisLgID, "#f0f0f0")


                            if (unitId.contains(",")) {

                                var unitname_dataList: Array<String>
                                var unitid_dataList: Array<String>

                                unitname_dataList =
                                    unitName.split(",".toRegex()).dropLastWhile({ it.isEmpty() })
                                        .toTypedArray()
                                unitid_dataList =
                                    unitId.split(",".toRegex()).dropLastWhile({ it.isEmpty() })
                                        .toTypedArray()
                                // unitAccountId_dataList=intent.getStringExtra(UNIT_ACCOUNT_ID).split(",".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                                if (unitid_dataList.size > 0) {
                                    for (i in 0 until unitid_dataList.size) {

                                        val ddc =
                                            Intent(
                                                this@MobileNumberforEntryScreen,
                                                BackgroundSyncReceiver::class.java
                                            )
                                        ddc.putExtra(
                                            ConstantUtils.BSR_Action,
                                            ConstantUtils.VisitorEntryFCM
                                        )
                                        ddc.putExtra(
                                            "msg",
                                            personName + " " + desgn + " is coming to your home" + "(" + "(" + unitname_dataList.get(
                                                i
                                            ).replace(" ", "") + ")" + ")"
                                        )
                                        ddc.putExtra("mobNum", mobileNumb)
                                        ddc.putExtra("name", personName)
                                        ddc.putExtra(
                                            "nr_id",
                                            globalApiObject.data.visitorLog.vlVisLgID.toString()
                                        )
                                        ddc.putExtra(
                                            "unitname",
                                            unitname_dataList.get(i).replace(" ", "")
                                        )
                                        ddc.putExtra("memType", "Owner")
                                        ddc.putExtra(
                                            UNITID,
                                            unitid_dataList.get(i).replace(" ", "")
                                        )
                                        ddc.putExtra(
                                            COMPANY_NAME,
                                            intent.getStringExtra(COMPANY_NAME)
                                        )
                                        //     ddc.putExtra(UNIT_ACCOUNT_ID,UnitList.data.unit.acAccntID.toString())
                                        ddc.putExtra(
                                            "VLVisLgID",
                                            globalApiObject.data.visitorLog.vlVisLgID
                                        )
                                        ddc.putExtra(VISITOR_TYPE, "Staff")
//                        intent.getStringExtra("msg"),intent.getStringExtra("mobNum"),
//                        intent.getStringExtra("name"),intent.getStringExtra("nr_id"),
//                        intent.getStringExtra("unitname"),intent.getStringExtra("memType")
                                        this@MobileNumberforEntryScreen.sendBroadcast(ddc)

                                    }
                                }
                            } else {
                                val ddc =
                                    Intent(
                                        this@MobileNumberforEntryScreen,
                                        BackgroundSyncReceiver::class.java
                                    )
                                ddc.putExtra(
                                    ConstantUtils.BSR_Action,
                                    ConstantUtils.VisitorEntryFCM
                                )
                                ddc.putExtra(
                                    "msg",
                                    personName + " " + desgn + " is coming to your home" + "(" + unitName + ")"
                                )
                                ddc.putExtra("mobNum", mobileNumb)
                                ddc.putExtra("name", personName)
                                ddc.putExtra(
                                    "nr_id",
                                    globalApiObject.data.visitorLog.vlVisLgID.toString()
                                )
                                ddc.putExtra("unitname", unitName)
                                ddc.putExtra("memType", "Owner")
                                ddc.putExtra(UNITID, unitId.toString())
                                ddc.putExtra(COMPANY_NAME, intent.getStringExtra(COMPANY_NAME))
                                //     ddc.putExtra(UNIT_ACCOUNT_ID,UnitList.data.unit.acAccntID.toString())
                                ddc.putExtra("VLVisLgID", globalApiObject.data.visitorLog.vlVisLgID)
                                ddc.putExtra(VISITOR_TYPE, "Staff")
//                        intent.getStringExtra("msg"),intent.getStringExtra("mobNum"),
//                        intent.getStringExtra("name"),intent.getStringExtra("nr_id"),
//                        intent.getStringExtra("unitname"),intent.getStringExtra("memType")
                                this@MobileNumberforEntryScreen.sendBroadcast(ddc)

                            }


//                            getUnitLog(intent.getStringExtra("UNITID").toInt(), intent.getStringExtra("FIRSTNAME") + " " +intent.getStringExtra("LASTNAME"),
//                                intent.getStringExtra(MOBILENUMBER),intent.getStringExtra("DESIGNATION"), intent.getStringExtra("WORKTYPE"),intent.getIntExtra("WORKERID",0), intent.getStringExtra("UNITNAME"),globalApiObject.data.visitorLog.vlVisLgID)
//


                            Log.d("CreateVisitorLogResp", "StaffEntry " + globalApiObject.data.toString())
                        } else {
                            Log.d("CreateVisitorLogResp", "StaffEntry " + globalApiObject.toString())

                            Utils.showToast(this@MobileNumberforEntryScreen, "Entry not Saved" + globalApiObject.toString())
                        }

                        finish()

                    }

                    override fun onErrorResponse(e: Throwable) {
                        Log.d("onErrorResponse", "StaffEntry " + e.toString())

                        Utils.showToast(this@MobileNumberforEntryScreen, "Something went wrong")
//                    dismissProgress()
                    }

                    override fun noNetowork() {
                        Utils.showToast(this@MobileNumberforEntryScreen, resources.getString(R.string.no_internet))
                    }

                    override fun onShowProgress() {
//                    showProgress()
                    }

                    override fun onDismissProgress() {
//                    dismissProgress()
                    }
                })
        )
    }

    override fun onBackPressed() {
        super.onBackPressed()
//        val i_delivery = Intent(this@MobileNumberforEntryScreen, Dashboard::class.java)
//        startActivity(i_delivery)
        finish()
    }

//    private fun GetWorkersListByMobileNumberAndAssocID(WKMobile: String, ASAssnID: Int) {
//
//
//        val req = GetWorkersListByMobileNumberReq(WKMobile, ASAssnID)
//
//        compositeDisposable.add(
//            RetrofitClinet.instance.GetWorkersListByMobileNumberAndAssocID(ConstantUtils.OYE247TOKEN, req)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeWith(object : CommonDisposable<GetWorkersListByMobileNumberResp>() {
//                    override fun onSuccessResponse(globalApiObject: GetWorkersListByMobileNumberResp) {
//                        if (globalApiObject.success == true) {
//
//                            if (globalApiObject.data.message.equals("Invalid MobileNumber")) {
//
//                                // getAccountDetails(ccd.toString(), mobileNumber.toString());
//
//
//                            } else {
//
//
//                                val builder = AlertDialog.Builder(this@MobileNumberforEntryScreen)
//                                // builder.setTitle("Vendor Entry already done")
//                                builder.setMessage(globalApiObject.data.message + ". Please Try again")
//                                builder.setPositiveButton("Ok") { dialog, which ->
//
//
//                                    dialog.cancel()
//                                    textview!!.text = ""
//
////                                    val d = Intent(this@MobileNumberScreen, Dashboard::class.java)
////                                    startActivity(d)
////                                    finish()
//                                }
//                                builder.setCancelable(false)
//                                builder.show()
//                                // Toast.makeText(this@EditStaffActivity,globalApiObject.data.message,Toast.LENGTH_LONG).show()
//
//                            }
//                        }
//                    }
//
//                    override fun onErrorResponse(e: Throwable) {
////                    Utils.showToast(applicationContext, getString(R.string.some_wrng))
//                        Log.d("CreateVisitorLogResp", "onErrorResponse  " + e.toString())
//                    }
//
//                    override fun noNetowork() {
////                    Utils.showToast(applicationContext, getString(R.string.no_internet))
//                    }
//
//                    override fun onShowProgress() {
//                    }
//
//                    override fun onDismissProgress() {
//                    }
//                })
//        )
//    }

//    private fun getUnitLog(
//        unitId: Int, personName: String, mobileNumb: String, desgn: String,
//        workerType: String, staffID: Int, unitName: String, vlVisLgID: Int
//    ) {
//
//        RetrofitClinet.instance
//            .getUnitListbyUnitId("1FDF86AF-94D7-4EA9-8800-5FBCCFF8E5C1", unitId)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribeWith(object : CommonDisposable<UnitlistbyUnitID>() {
//
//                override fun onSuccessResponse(UnitList: UnitlistbyUnitID) {
//
//                    if (UnitList.success == true) {
//
//                        val ddc  =  Intent(this@MobileNumberforEntryScreen, BackgroundSyncReceiver::class.java)
//                        ddc.putExtra(ConstantUtils.BSR_Action, ConstantUtils.VisitorEntryFCM)
//                        ddc.putExtra(
//                            "msg",
//                            personName + " " + desgn + " is coming to your home" + "(" + unitName + ")"
//                        )
//                        ddc.putExtra("mobNum", mobileNumb)
//                        ddc.putExtra("name", personName)
//                        ddc.putExtra("nr_id", vlVisLgID.toString())
//                        ddc.putExtra("unitname", unitName)
//                        ddc.putExtra("memType", "Owner")
//                        ddc.putExtra(UNITID, unitId.toString())
//                        ddc.putExtra(COMPANY_NAME, intent.getStringExtra(COMPANY_NAME))
//                        // ddc.putExtra(UNIT_ACCOUNT_ID,UnitList.data.unit.acAccntID.toString())
//                        ddc.putExtra("VLVisLgID", vlVisLgID)
//                        ddc.putExtra(VISITOR_TYPE, "Staff")
////                        intent.getStringExtra("msg"),intent.getStringExtra("mobNum"),
////                        intent.getStringExtra("name"),intent.getStringExtra("nr_id"),
////                        intent.getStringExtra("unitname"),intent.getStringExtra("memType")
//                        this@MobileNumberforEntryScreen.sendBroadcast(ddc)
//
//
//                    } else {
//                    }
//                }
//
//                override fun onErrorResponse(e: Throwable) {
//                    Log.d("cdvd", e.message)
//
//
//                }
//
//                override fun noNetowork() {
//
//                }
//            })
//
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
            .subscribeWith(object : CommonDisposable<getVisitorDataByWorker>() {

                override fun onSuccessResponse(getdata: getVisitorDataByWorker) {

                    if (getdata.success == true) {
                        showToast(this@MobileNumberforEntryScreen, "Duplicate Entry not allowed")
                        //  showToast(this@Dashboard,workerID.toString())

                    }
                }

                override fun onErrorResponse(e: Throwable) {


                    visitorLog(
                        intent.getStringExtra(UNITID),
                        intent.getStringExtra("FIRSTNAME") + " " + intent.getStringExtra("LASTNAME"),
                        intent.getStringExtra(MOBILENUMBER),
                        intent.getStringExtra("DESIGNATION"),
                        intent.getStringExtra("WORKTYPE"),
                        workerID.toInt(),
                        intent.getStringExtra(UNITNAME)
                    )
                    //   }

//                        visitorLog(intent.getIntExtra("UNITID",0), intent.getStringExtra("FIRSTNAME") + " " +intent.getStringExtra("LASTNAME"),
//                            intent.getStringExtra(MOBILENUMBER),intent.getStringExtra("DESIGNATION"), intent.getStringExtra("WORKTYPE"),intent.getIntExtra("WORKERID",0), intent.getStringExtra("UNITNAME")
//                        );
                    buttonNext.isEnabled = false
                    buttonNext.isClickable = false
                }

                override fun noNetowork() {
                    Toast.makeText(this@MobileNumberforEntryScreen, "No network call ", Toast.LENGTH_LONG)
                        .show()
                }
            })
    }


}