package com.oyespace.guards.activity

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.CallLog
import android.provider.Settings
import android.speech.RecognizerIntent
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
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
import com.oyespace.guards.getOTPbyCall.OTPbyCallRetrofitClinet
import com.oyespace.guards.network.CommonDisposable
import com.oyespace.guards.network.RetrofitClinet
import com.oyespace.guards.pojo.*
import com.oyespace.guards.repo.VisitorLogRepo
import com.oyespace.guards.utils.*
import com.oyespace.guards.utils.ConstantUtils.*
import com.oyespace.guards.utils.FirebaseDBUtils.Companion.updateFirebaseColor
import com.yarolegovich.lovelydialog.LovelyStandardDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_mobile_number.*
import java.util.*


class MobileNumberEntryScreenwithOTP : BaseKotlinActivity(), View.OnClickListener, CountryCodePicker.OnCountryChangeListener {
    val workType: ArrayList<String> = ArrayList()
    private var ccp: CountryCodePicker? = null
    private var countryCode: String? = null
    private var countryName: String? = null
    lateinit var pDialog: ProgressDialog
    var phonenumber: String? = null
    lateinit var txt_assn_name:TextView
    lateinit var txt_gate_name:TextView
    lateinit var txt_device_name: TextView
   // lateinit var timer:TextView
    val laststate:Int?=null
    var progressBar: ProgressBar?=null
    var mobilenumber:String?=null
    var otpnumber: String? = null
    var phone:String?=null
    var dialogs:Dialog?=null

    // private var Ed_phoneNum:String?=null

    private val REQUEST_CODE_SPEECH_INPUT = 100

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.Btn_SendOtp ->
            {

                Toast.makeText(this@MobileNumberEntryScreenwithOTP, "Coming soon", Toast.LENGTH_LONG)
                    .show()
            }

        }
    }

//TODO sumeeth has reomoved this fucntion for crashing porpose

    val entries: ArrayList<String> = ArrayList()
    var receiver:BroadcastReceiver?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocale(Prefs.getString(PrefKeys.LANGUAGE, null))


        setContentView(R.layout.layout_mobilenumber_otp)

        addEntries()
        progressBar = this.progressBar1
       // timer=findViewById(R.id.timer)
        txt_assn_name=findViewById(R.id.txt_assn_name)
        txt_gate_name=findViewById(R.id.txt_gate_name)
        txt_device_name=findViewById(R.id.txt_device_name)
        //txt_assn_name.text = "Society: " + LocalDb.getAssociation()!!.asAsnName
        txt_assn_name.text =  LocalDb.getAssociation()!!.asAsnName
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

        Log.d(
            "intentdata MobileNumber",
            "" + intent.getStringExtra(UNITNAME) + " " + intent.getStringExtra(UNITID)
        )

        btn_mic.setOnClickListener {
            Speak()
        }

        val mobilePHONEDATA:String = Prefs.getString(PrefKeys.MOBILE_NUMBER,"")



        val countrycode = Prefs.getString(PrefKeys.COUNTRY_CODE,"")

        val input =Prefs.getString(PrefKeys.MOBILE_NUMBER,"")
       // val number = input.replaceFirst("(\\d{3})(\\d{3})(\\d+)".toRegex(), "$1 $2 $3")
        val number = input.replaceFirst("(\\d{2})(\\d{4})(\\d{3})(\\d+)".toRegex(), "$1 $2 $3 $4")

        ccp = findViewById(R.id.country_code_picker)
        ccp!!.setOnCountryChangeListener(this)
        countryCode = ccp!!.selectedCountryCode

        Btn_SendOtp.setOnClickListener {

            mobilenumber= phone
            phone = Ed_phoneNum.text.toString().replace(" ", "")

            if (phone != null) {

                Log.v("NUMBER MATCH", intent.getStringExtra(MOBILENUMBER) + ".." + textview.text)
                val phoneNumber = "" + phone
                if (intent.getStringExtra(MOBILENUMBER).contains(phoneNumber) || debug) {

                    val allowEntry = VisitorLogRepo.allowEntry(countryCode, phone)

                    if (!allowEntry) {
                        Toast.makeText(this, "Duplicate Entry not allowed", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        sendotp()
                    }

                } else {
                    Toast.makeText(this, "Enter valid staff number", Toast.LENGTH_SHORT).show()
                }
            }

        }

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
        val builder = AlertDialog.Builder(this@MobileNumberEntryScreenwithOTP)
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
        progressBar?.visibility = View.VISIBLE


        val req = GetAccountDetailsByMobReq("+" + isdCode, MobNumber)
        Log.d("getAccountDetails", req.toString())
        compositeDisposable.add(
            RetrofitClinet.instance.GetAccountDetailsByMobCall(CHAMPTOKEN, req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : CommonDisposable<GetAccountDetailsByMobResp<AccountByMobile>>() {
                    override fun onSuccessResponse(globalApiObject: GetAccountDetailsByMobResp<AccountByMobile>) {
                        if (globalApiObject.data != null) {
                            progressBar?.visibility = View.GONE

                            Log.d("getAccountDetails", globalApiObject.data.toString())
                            Log.d("getAccountDetails", globalApiObject.data.accountByMobile.toString())
                            val d = Intent(this@MobileNumberEntryScreenwithOTP, AddCarFragment::class.java)

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
        val d = Intent(this@MobileNumberEntryScreenwithOTP, NameEntryScreen::class.java)
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
        phone

        val req = GetOTPReq(countryCode.toString(), phone.toString())
        compositeDisposable.add(
            RetrofitClinet.instance.getOTPCall(CHAMPTOKEN, req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : CommonDisposable<GetOTPResp>() {
                    override fun onSuccessResponse(globalApiObject: GetOTPResp) {
                        if (globalApiObject.success == true) {
                            showDialog("Verify OTP")
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

                    phone = Ed_phoneNum.text.toString().replace(" ", "")


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

    fun addEntries() {
        workType.add("Security Guard")
        workType.add("Security Supervisor")
        workType.add("Manager")
        workType.add("Assistant Manager")
        workType.add("Security Officer")
        workType.add("Assistant Security Officer")
        workType.add("Senior Supervisor")
        workType.add("Head Guard")
        workType.add("Senior Security")
        workType.add("Lady Supervisor")
        workType.add("Lady Head Guard")
        workType.add("Lady Senior Security Guard")
        workType.add("Lady Security Guard")

    }

    private fun showDialog(title: String) {
         dialogs = Dialog(this@MobileNumberEntryScreenwithOTP)

        dialogs!!.setCancelable(false)
        dialogs!!.setContentView(R.layout.layout_otp_dialog)
        val ed_otp = dialogs!!.findViewById(R.id.ed_otp) as EditText
        val btn_otpbycall = dialogs!!.findViewById(R.id.btn_otpbycall) as Button
        otpnumber = ed_otp.text.toString()
        val btn_verifyotp = dialogs!!.findViewById(R.id.btn_verifyotp) as Button
        val btn_cancel = dialogs!!.findViewById(R.id.btn_cancel) as Button

        btn_otpbycall.setOnClickListener {
            Toast.makeText(this@MobileNumberEntryScreenwithOTP, "coming", Toast.LENGTH_LONG).show()
            sendOTPbyCallReq(
                "261622AtznpKYJ5c5ab60e", countryCode.toString()
                        + phone, "voice"
            )
        }

        btn_cancel.setOnClickListener {
            dialogs!!.dismiss()
        }

        btn_verifyotp.setOnClickListener {


            if (TextUtils.isEmpty(ed_otp.text.toString())) {

                // Toast.makeText(this, "Enter OTP ", Toast.LENGTH_SHORT).show()

                LovelyStandardDialog(this@MobileNumberEntryScreenwithOTP, LovelyStandardDialog.ButtonLayout.VERTICAL)
                    .setTopColorRes(R.color.google_red)
                    .setIcon(R.drawable.ic_info_black_24dp)
                    //This will add Don't show again checkbox to the dialog. You can pass any ID as argument
                    .setTitle("invalid")
                    .setTitleGravity(Gravity.CENTER)
                    .setMessage("Enter OTP")
                    .setMessageGravity(Gravity.CENTER)
                    .setPositiveButton(android.R.string.ok) {

                    }

                    .show()

            } else {
                //   Toast.makeText(this, ed_otp.text.toString(), Toast.LENGTH_SHORT).show()
                verifyOTP(ed_otp.text.toString())
            }

        }

        dialogs!!.show()

    }
    fun verifyOTP(number: String) {

        countryCode.toString()
        phone

        Log.d("sdssds", "Verify otp " + countryCode.toString() + " " + Ed_phoneNum.text.trim().toString() + " " + number)
        val req = GetVerifyOTPRequest(countryCode.toString(), phone.toString(), number)
        Log.d("sdssds", "Verify otp " + req.toString() + " " + Ed_phoneNum.text.toString() + " " + number)

        compositeDisposable.add(
            RetrofitClinet.instance.getVerifyOTP(ConstantUtils.CHAMPTOKEN, req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : CommonDisposable<GetVerifyOTPResponse>() {
                    override fun onSuccessResponse(globalApiObject: GetVerifyOTPResponse) {
                        if (globalApiObject.success) {
                            dialogs!!.dismiss()

                           // getAccountDetails(countryCode.toString(), phone.toString());

                            visitorLog(
                                intent.getStringExtra(UNITID),
                                intent.getStringExtra("FIRSTNAME") + " " + intent.getStringExtra("LASTNAME"),
                                intent.getStringExtra(MOBILENUMBER),
                                intent.getStringExtra("DESIGNATION"),
                                intent.getStringExtra("WORKTYPE"),
                                intent.getIntExtra("WORKERID", 0),
                                intent.getStringExtra(UNITNAME)
                            )

                            buttonNext.isEnabled = false
                            buttonNext.isClickable = false



                        } else {


                            //Utils.showToast(applicationContext, globalApiObject.apiVersion)


                        }
                    }

                    override fun onErrorResponse(e: Throwable) {

                        println("Verify otp " + e.toString())

                        LovelyStandardDialog(this@MobileNumberEntryScreenwithOTP, LovelyStandardDialog.ButtonLayout.VERTICAL)
                            .setTopColorRes(R.color.google_red)
                            .setIcon(R.drawable.ic_info_black_24dp)
                            //This will add Don't show again checkbox to the dialog. You can pass any ID as argument
                            .setTitle("invalid")
                            .setTitleGravity(Gravity.CENTER)
                            //  .setMessage("Our Machine are not talking to each other please wait,Humans are Fixing it ")
                            .setMessage("Enter valid OTP")
                            .setMessageGravity(Gravity.CENTER)
                            .setPositiveButton(android.R.string.ok) {

                            }

                            .show()

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

    private fun visitorLog(
        unitId: String, personName: String, mobileNumb: String, desgn: String,
        workerType: String, staffID: Int, unitName: String
    ) {


        var memID: Int = 410
        if(BASE_URL.contains("dev",true)){
            memID = 64
        }
        else if(BASE_URL.contains("uat",true)){
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
            unitName,unitId ,desgn,
            personName,"",0,"+",mobileNumb,
            "","","","",
            1, workerType.toLowerCase().capitalize(), SPPrdImg1, SPPrdImg2, SPPrdImg3, SPPrdImg4, SPPrdImg5
            ,
            SPPrdImg6,
            SPPrdImg7,
            SPPrdImg8,
            SPPrdImg9,
            SPPrdImg10,
            "",
            intent.getStringExtra("Image"),
            Prefs.getString(ConstantUtils.GATE_NO, ""),
            DateTimeUtils.getCurrentTimeLocal(),
            "" ,"","","","","","","","",""
        )
        Log.d("CreateVisitorLogResp","StaffEntry "+req.toString())

        CompositeDisposable().add(
            RetrofitClinet.instance.createVisitorLogCall(OYE247TOKEN,req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : CommonDisposable<CreateVisitorLogResp<VLRData>>() {
                    override fun onSuccessResponse(globalApiObject: CreateVisitorLogResp<VLRData>) {
                        if (globalApiObject.success == true) {

                            val id = globalApiObject.data.visitorLog.vlVisLgID
                            updateFirebaseColor(id, "#f0f0f0")

                            val ddc  =  Intent(this@MobileNumberEntryScreenwithOTP, BackgroundSyncReceiver::class.java)
                            ddc.putExtra(ConstantUtils.BSR_Action, ConstantUtils.SENDFCM_toSYNC_VISITORENTRY)
                            ddc.putExtra("msg", personName+" "+desgn +" is coming to your home")
                            ddc.putExtra("mobNum", mobileNumb)
                            ddc.putExtra("name", personName)
                            ddc.putExtra("nr_id", AppUtils.intToString(globalApiObject.data.visitorLog.vlVisLgID))
                            ddc.putExtra("unitname", unitName)
                            ddc.putExtra("memType", "Owner")
                            ddc.putExtra(COMPANY_NAME,intent.getStringExtra(COMPANY_NAME))
                            this@MobileNumberEntryScreenwithOTP.sendBroadcast(ddc)

                            Log.d("CreateVisitorLogResp","StaffEntry "+globalApiObject.data.toString())
                        } else {
                            Log.d("CreateVisitorLogResp","StaffEntry "+globalApiObject.toString())

                            Utils.showToast(this@MobileNumberEntryScreenwithOTP, "Entry not Saved"+globalApiObject.toString())
                        }
                        finish()
                    }

                    override fun onErrorResponse(e: Throwable) {
                        Log.d("onErrorResponse", "StaffEntry " + e.toString())

                        Utils.showToast(this@MobileNumberEntryScreenwithOTP, "Something went wrong")
//                    dismissProgress()
                    }

                    override fun noNetowork() {
                        Utils.showToast(this@MobileNumberEntryScreenwithOTP, "No Internet")
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
//        val i_delivery = Intent(this@MobileNumberEntryScreenwithOTP, Dashboard::class.java)
//        startActivity(i_delivery)
        finish()
    }

    private fun sendOTPbyCallReq(authkey: String, mobile: String, retrytype: String) {

        val dataReq = GetOTPbyCallReq(authkey, mobile, retrytype)


        OTPbyCallRetrofitClinet.instance
            .sendOTPbyCallReq(dataReq)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CommonDisposable<OTPbyCallResponse>() {

                override fun onSuccessResponse(otPbyCallResponse: OTPbyCallResponse) {

                }

                override fun onErrorResponse(e: Throwable) {
                    Log.d("Error WorkerList", e.toString())
                }

                override fun noNetowork() {

                }
            })
    }

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
                        Utils.showToast(
                            this@MobileNumberEntryScreenwithOTP,
                            "Duplicate Entry not allowed"
                        )
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
                        intent.getIntExtra("WORKERID", 0),
                        intent.getStringExtra(UNITNAME)
                    )

                    buttonNext.isEnabled = false
                    buttonNext.isClickable = false
                }

                override fun noNetowork() {
                    Toast.makeText(
                        this@MobileNumberEntryScreenwithOTP,
                        "No network call ",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            })
    }



}

