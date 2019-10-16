package com.oyespace.guards.vehicle_others

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.speech.RecognizerIntent
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.text.InputFilter
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
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
import com.oyespace.guards.R
import com.oyespace.guards.activity.BaseKotlinActivity
import com.oyespace.guards.constants.PrefKeys
import com.oyespace.guards.constants.PrefKeys.LANGUAGE
import com.oyespace.guards.network.CommonDisposable
import com.oyespace.guards.network.RetrofitClinet
import com.oyespace.guards.pojo.*
import com.oyespace.guards.utils.ConstantUtils
import com.oyespace.guards.utils.ConstantUtils.*
import com.oyespace.guards.utils.LocalDb
import com.oyespace.guards.utils.Prefs
import com.oyespace.guards.utils.RandomUtils.entryExists
import com.oyespace.guards.utils.Utils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_mobile_number.*
import java.util.*

class VehicleOthersMobileNumberScreen : BaseKotlinActivity() , View.OnClickListener, CountryCodePicker.OnCountryChangeListener{

    private var ccp: CountryCodePicker? = null
    private var countryCode: String? = null
    private var countryName: String? = null
    lateinit var pDialog: ProgressDialog
    var phonenumber:String?=null
    lateinit var timer:TextView
    lateinit var txt_assn_name:TextView
    lateinit var txt_gate_name:TextView
    lateinit var txt_device_name: TextView
    var ccd:String?=null
    var mobileNumber:String?=null
    lateinit var btn_nobalance: Button


    private val REQUEST_CODE_SPEECH_INPUT = 100

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.Btn_SendOtp ->{
                Toast.makeText(this@VehicleOthersMobileNumberScreen, "Coming soon", Toast.LENGTH_LONG)
                    .show()
            }
            R.id.btn_nobalance->{

                val d = Intent(this@VehicleOthersMobileNumberScreen, Vehicle_Others_MobileNumberScreenwithOTP::class.java)
                d.putExtra(UNITID, intent.getStringExtra(UNITID))
                d.putExtra(UNITNAME, intent.getStringExtra(UNITNAME))
                d.putExtra(FLOW_TYPE, intent.getStringExtra(FLOW_TYPE))
                d.putExtra(VISITOR_TYPE, intent.getStringExtra(VISITOR_TYPE))
                d.putExtra(COMPANY_NAME, intent.getStringExtra(COMPANY_NAME))
                d.putExtra(VEHICLE_NUMBER, intent.getStringExtra(VEHICLE_NUMBER))
                d.putExtra(UNIT_ACCOUNT_ID,intent.getStringExtra(ConstantUtils.UNIT_ACCOUNT_ID))
                d.putExtra(BLOCK_ID, intent.getStringExtra(BLOCK_ID))
                startActivity(d)
                finish()

            }

            R.id.buttonNext ->{
                buttonNext.isEnabled = false
                buttonNext.isClickable = false
                if(textview.text.length==13) {
//                    val d = Intent(this@VehicleOthersMobileNumberScreen, VehicleOthersNameEntryScreen::class.java)
////                    Log.d("intentdata MobileNumber","buttonNext "+intent.getStringExtra(UNITNAME)+" "+intent.getStringExtra(UNITID)
//                           // +" "+textview.text+" "+countryCode)
//                    d.putExtra(FLOW_TYPE,intent.getStringExtra(FLOW_TYPE))
//                    d.putExtra(VISITOR_TYPE,intent.getStringExtra(VISITOR_TYPE))
//                    d.putExtra(COMPANY_NAME,intent.getStringExtra(COMPANY_NAME))
//                    d.putExtra(UNITID, intent.getStringExtra(UNITID))
//                    d.putExtra(UNITNAME, intent.getStringExtra(UNITNAME))
//                    d.putExtra(MOBILENUMBER, textview.text.toString())
//                    d.putExtra(UNIT_ACCOUNT_ID,intent.getStringExtra(ConstantUtils.UNIT_ACCOUNT_ID))
                   // d.putExtra(COUNTRYCODE, countryCode)

//                    startActivity(d);
//                    finish();
                   // deliveryFlow_launchNameEntryScreen()

                    if(entryExists(ccd,mobileNumber)) {
//                        Toast.makeText(this,"Mobile Number already used for Visitor Entry", Toast.LENGTH_SHORT).show()
                        val builder = AlertDialog.Builder(this@VehicleOthersMobileNumberScreen)
                        //  builder.setTitle("Vendor Entry already done")
                        builder.setMessage("This number is being used by a person already in")
                        builder.setPositiveButton("Ok") { dialog, which ->

//                            val d = Intent(this@VehicleOthersMobileNumberScreen, Dashboard::class.java)
//                            startActivity(d)
                            finish()
                            dialog.dismiss()
                        }
                        builder.setCancelable(false)
                        builder.show()
                    }else{
                        getAccountDetails(ccd.toString(), mobileNumber.toString())

                    }

                }else if(Ed_phoneNum.text.length==13) {
                    val d = Intent(this@VehicleOthersMobileNumberScreen, VehicleOthersNameEntryScreen::class.java)
//                    Log.d("intentdata MobileNumber","buttonNext "+intent.getStringExtra(UNITNAME)+" "+intent.getStringExtra(UNITID)
//                            +" "+Ed_phoneNum.text+" "+countryCode)
                    d.putExtra(FLOW_TYPE,intent.getStringExtra(FLOW_TYPE))
                    d.putExtra(VISITOR_TYPE,intent.getStringExtra(VISITOR_TYPE))
                    d.putExtra(COMPANY_NAME,intent.getStringExtra(COMPANY_NAME))
                    d.putExtra(UNITID, intent.getStringExtra(UNITID))
                    d.putExtra(UNITNAME, intent.getStringExtra(UNITNAME))
                    d.putExtra(MOBILENUMBER, Ed_phoneNum.text.toString())
                  //  d.putExtra(COUNTRYCODE, countryCode)
                    d.putExtra(VEHICLE_NUMBER,intent.getStringExtra(VEHICLE_NUMBER))
                    d.putExtra(UNIT_ACCOUNT_ID,intent.getStringExtra(ConstantUtils.UNIT_ACCOUNT_ID))

                    startActivity(d)
                    finish()
//                    getAccountDetails("+"+countryCode.toString(),textview.getText().toString());
                }else {
                    buttonNext.isEnabled = true
                    buttonNext.isClickable = true
                    Toast.makeText(this,"Invalid number captured", Toast.LENGTH_SHORT).show()

                }
            }

        }
    }

    val entries: ArrayList<String> = ArrayList()
    var receiver: BroadcastReceiver?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocale(Prefs.getString(LANGUAGE, null))



        setContentView(R.layout.activity_mobile_number)

        receiver =  object : BroadcastReceiver() {
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
                              //  textview.text = number.replace("+91", "")
                                textview.text = number

                                ccd= number.substring(0,3)

                                mobileNumber=number.substring(3,13)
                            }
                        }
                        LocalDb.disconnectCall(context)
                    }

                }, PhoneStateListener.LISTEN_CALL_STATE)

                //
            }
        }
        btn_nobalance=findViewById(R.id.btn_nobalance)
        timer=findViewById(R.id.timer)
        txt_assn_name=findViewById(R.id.txt_assn_name)
        txt_gate_name=findViewById(R.id.txt_gate_name)
        txt_device_name=findViewById(R.id.txt_device_name)
        if(Prefs.getString(PrefKeys.MODEL_NUMBER,null).equals("Nokia 1")) {
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
        val timer = object: CountDownTimer(60000,1000){
            override fun onTick(millisUntilFinished: Long) {

                val remainedSecs: Long = millisUntilFinished / 1000
                timer.text =
                    ("0" + (remainedSecs / 60) + ":" + (remainedSecs % 60))// manage it accordign to you
            }

            override fun onFinish() {
//                val i_delivery = Intent(this@VehicleOthersMobileNumberScreen, Dashboard::class.java)
//                startActivity(i_delivery)
                finish()
            }
        }
        timer.start()
        Log.d("intentdata MobileNumber",""+ intent.getStringExtra(UNITNAME)+" "+intent.getStringExtra(UNITID))

        btn_mic.setOnClickListener {
            Speak()
        }

        val input =Prefs.getString(PrefKeys.MOBILE_NUMBER,"")
        val number = input.replaceFirst("(\\d{2})(\\d{4})(\\d{3})(\\d+)".toRegex(), "$1 $2 $3 $4")
        tv_guardnumber.text = resources.getString(R.string.textgivemissedcall) + " +" + number

//        tv_guardnumber.text =
//            resources.getString(R.string.textgivemissedcall)+" "+ Prefs.getString(PrefKeys.MOBILE_NUMBER,"")


        if(intent.getStringExtra(FLOW_TYPE).equals(VEHICLE_OTHERS)){
            buttonSkip.visibility = View.INVISIBLE
//            Ed_phoneNum.setVisibility(View.VISIBLE)
//            textview.visibility=View.GONE
//            btn_mic.setVisibility(View.VISIBLE)
            Ed_phoneNum.visibility = View.GONE
            textview.visibility = View.VISIBLE

        }else{
            buttonSkip.visibility = View.INVISIBLE
        }

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

                pDialog = ProgressDialog(this@VehicleOthersMobileNumberScreen)
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
            }else{
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

//        val telephony = this@VehicleOthersMobileNumberScreen.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
//        telephony.listen(object : PhoneStateListener() {
//            override fun onCallStateChanged(state: Int, incomingNumber: String) {
//                super.onCallStateChanged(state, incomingNumber)
//                when (state) {
//
//                    // not getting incoming number in latest version of android
//                    TelephonyManager.CALL_STATE_RINGING-> {
//                        Log.e("Shalini Pareek", "incomingNumber: $incomingNumber")
//                        phonenumber= "$incomingNumber"
//                        if(phonenumber?.length==13){
//                            Log.d("Shalini length", "length: "+phonenumber?.substring(3,13))
//                            textview.text=phonenumber?.substring(3,13)
////                            ccp.setCountryForPhoneCode()
////                            if (entryExists(countryCode.toString(), textview.getText().toString())) {
//////                        Toast.makeText(this,"Mobile Number already used for Visitor Entry", Toast.LENGTH_SHORT).show()
////                                val builder = AlertDialog.Builder(this@VehicleOthersMobileNumberScreen)
////                                builder.setTitle("Vendor Entry already done")
////                                builder.setMessage("No Duplicates allowed")
////                                builder.setPositiveButton("Ok") { dialog, which ->
////                                    dialog.cancel()
//////                                }
////                                builder.setCancelable(false);
////                                builder.show()
////                            } else {
////                               // getAccountDetails(countryCode.toString(), textview.getText().toString());
////
////                            }
//                        }else if(phonenumber?.length==10){
//                            textview.text=phonenumber
////                        }else if(phonenumber.contains("+91")){
////                            textview.text=phonenumber?.replace("+91","")
//                        }else{
//                            textview.text=phonenumber
//                        }
//
//                    }
//
//                }
//            }
//        }, PhoneStateListener.LISTEN_CALL_STATE)

    }

    private fun showSettingsDialog() {
        val builder = AlertDialog.Builder(this@VehicleOthersMobileNumberScreen)
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
//        val intent = Intent(this@VehicleOthersMobileNumberScreen, VehicleOthersUnitScreen::class.java)
//        intent.putExtra(FLOW_TYPE,getIntent().getStringExtra(FLOW_TYPE))
//        intent.putExtra(VISITOR_TYPE,getIntent().getStringExtra(VISITOR_TYPE))
//        intent.putExtra(COMPANY_NAME,getIntent().getStringExtra(COMPANY_NAME))
//        intent.putExtra(UNITID, getIntent().getStringExtra(UNITID))
//        intent.putExtra(UNITNAME,getIntent().getStringExtra(UNITNAME))
//        intent.putExtra(VEHICLE_NUMBER,intent.getStringExtra(VEHICLE_NUMBER))
//
//        startActivity(intent)
//
//    }

    fun getAccountDetails(isdCode:String, MobNumber:String){

        val req = GetAccountDetailsByMobReq(isdCode,MobNumber)
        Log.d("getAccountDetails",req.toString())
        compositeDisposable.add(
            RetrofitClinet.instance.GetAccountDetailsByMobCall(CHAMPTOKEN,req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : CommonDisposable<GetAccountDetailsByMobResp<AccountByMobile>>() {
                    override fun onSuccessResponse(globalApiObject: GetAccountDetailsByMobResp<AccountByMobile>) {
                        if (globalApiObject.data!=null) {
                            Log.d("getAccountDetails",globalApiObject.data.toString())
                            Log.d("getAccountDetails",globalApiObject.data.accountByMobile.toString())
                            val d = Intent(this@VehicleOthersMobileNumberScreen, VehicleOthersAddCarFragment
                            ::class.java)

//                            Log.d("intentdata NameEntr","buttonNext "+ intent.getStringExtra(UNITNAME)+" "+intent.getStringExtra(UNITID)
//                                    +" "+ intent.getStringExtra(MOBILENUMBER)+" "+ intent.getStringExtra(COUNTRYCODE)+" "
//                                    +globalApiObject.data.accountByMobile[0].acfName+" "+globalApiObject.data.accountByMobile[0].aclName)
                            d.putExtra(UNITID,intent.getStringExtra(UNITID) )
                            d.putExtra(UNITNAME, intent.getStringExtra(UNITNAME))
                            d.putExtra(FLOW_TYPE,intent.getStringExtra(FLOW_TYPE))
                            d.putExtra(VISITOR_TYPE,intent.getStringExtra(VISITOR_TYPE))
                            d.putExtra(COMPANY_NAME,intent.getStringExtra(COMPANY_NAME))
                            d.putExtra(MOBILENUMBER, MobNumber)
                            d.putExtra(COUNTRYCODE, isdCode)
                            d.putExtra(VEHICLE_NUMBER,intent.getStringExtra(VEHICLE_NUMBER))
                            d.putExtra(PERSONNAME, globalApiObject.data.accountByMobile[0].acfName+" "+globalApiObject.data.accountByMobile[0].aclName)
                            d.putExtra(ACCOUNT_ID, globalApiObject.data.accountByMobile[0].acAccntID)
                            d.putExtra(UNIT_ACCOUNT_ID,intent.getStringExtra(ConstantUtils.UNIT_ACCOUNT_ID))
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

                }))


    }

    fun deliveryFlow_launchNameEntryScreen(){
        val d = Intent(this@VehicleOthersMobileNumberScreen, VehicleOthersNameEntryScreen::class.java)
//        Log.d("intentdata MobileNumber","buttonNext "+intent.getStringExtra(UNITNAME)+" "+intent.getStringExtra(UNITID)
//                +" "+textview.text+" "+countryCode)
        d.putExtra(FLOW_TYPE,intent.getStringExtra(FLOW_TYPE))
        d.putExtra(VISITOR_TYPE,intent.getStringExtra(VISITOR_TYPE))
        d.putExtra(COMPANY_NAME,intent.getStringExtra(COMPANY_NAME))
        d.putExtra(UNITID, intent.getStringExtra(UNITID))
        d.putExtra(UNITNAME, intent.getStringExtra(UNITNAME))
        d.putExtra(MOBILENUMBER, mobileNumber)
        d.putExtra(COUNTRYCODE, ccd)
        d.putExtra(VEHICLE_NUMBER,intent.getStringExtra(VEHICLE_NUMBER))
        d.putExtra(UNIT_ACCOUNT_ID,intent.getStringExtra(ConstantUtils.UNIT_ACCOUNT_ID))
                    startActivity(d)
        finish()
    }

    fun sendotp(){

       ""
        Ed_phoneNum.toString()

            val req = GetOTPReq("", Ed_phoneNum.toString())
            compositeDisposable.add(
                RetrofitClinet.instance.getOTPCall(CHAMPTOKEN,req)
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
                    }))


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
                    Ed_phoneNum.text = result[0].trim() + ""
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


//    override fun onResume() {
//        super.onResume()
//        val telephony = this@VehicleOthersMobileNumberScreen.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
//        telephony.listen(object : PhoneStateListener() {
//            override fun onCallStateChanged(state: Int, incomingNumber: String) {
//                super.onCallStateChanged(state, incomingNumber)
//                when (state) {
//
//                    // not getting incoming number in latest version of android
//                    TelephonyManager.CALL_STATE_RINGING-> {
//                        Log.e("Shalini Pareek", "incomingNumber: $incomingNumber")
//                        phonenumber= "$incomingNumber"
//                        if(phonenumber?.length==13){
//                            Log.d("Shalini length", "length: "+phonenumber?.substring(3,13))
//                            textview.text=phonenumber?.substring(3,13)
////                            ccp.setCountryForPhoneCode()
////                            if (entryExists(countryCode.toString(), textview.getText().toString())) {
//////                        Toast.makeText(this,"Mobile Number already used for Visitor Entry", Toast.LENGTH_SHORT).show()
////                                val builder = AlertDialog.Builder(this@VehicleOthersMobileNumberScreen)
////                                builder.setTitle("Vendor Entry already done")
////                                builder.setMessage("No Duplicates allowed")
////                                builder.setPositiveButton("Ok") { dialog, which ->
////                                    dialog.cancel()
//////                                }
////                                builder.setCancelable(false);
////                                builder.show()
////                            } else {
////                               // getAccountDetails(countryCode.toString(), textview.getText().toString());
////
////                            }
//                        }else if(phonenumber?.length==10){
//                            textview.text=phonenumber
////                        }else if(phonenumber.contains("+91")){
////                            textview.text=phonenumber?.replace("+91","")
//                        }else{
//                            textview.text=phonenumber
//                        }
//
//                    }
//
//                }
//            }
//        }, PhoneStateListener.LISTEN_CALL_STATE)
//    }


    override fun onBackPressed() {
        super.onBackPressed()
//        val intent= Intent(this@VehicleOthersMobileNumberScreen, Dashboard::class.java)
//        startActivity(intent)
        finish()
    }
}