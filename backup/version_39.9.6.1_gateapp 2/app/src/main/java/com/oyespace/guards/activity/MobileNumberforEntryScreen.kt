package com.oyespace.guards.activity

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.CallLog
import android.provider.Settings
import android.speech.RecognizerIntent
import android.support.v7.app.AlertDialog
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.text.InputFilter
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.hbb20.CountryCodePicker
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.DexterError
import com.karumi.dexter.listener.PermissionRequestErrorListener
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.oyespace.guards.BackgroundSyncReceiver
import com.oyespace.guards.Dashboard
import com.oyespace.guards.R
import com.oyespace.guards.camtest.AddCarFragment
import com.oyespace.guards.constants.PrefKeys
import com.oyespace.guards.network.CommonDisposable
import com.oyespace.guards.network.ResponseHandler
import com.oyespace.guards.network.RetrofitClinet
import com.oyespace.guards.pojo.*
import com.oyespace.guards.utils.*
import com.oyespace.guards.utils.ConstantUtils.*
import com.oyespace.guards.utils.RandomUtils.entryExists
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_mobile_number.*
import java.util.*


class MobileNumberforEntryScreen : BaseKotlinActivity(), View.OnClickListener, ResponseHandler,CountryCodePicker.OnCountryChangeListener {
    override fun onSuccess(response: String?, data: Any?, urlId: Int, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onFailure(e: java.lang.Exception?, urlId: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    val workType: ArrayList<String> = ArrayList();
    private var ccp: CountryCodePicker? = null
    private var countryCode: String? = null
    private var countryName: String? = null
    lateinit var pDialog: ProgressDialog
    var phonenumber: String? = null
    lateinit var txt_assn_name:TextView
    lateinit var txt_gate_name:TextView
    lateinit var txt_device_name: TextView
    val laststate:Int?=null
    var buttonSkip:Button?=null

    // private var Ed_phoneNum:String?=null

    private val REQUEST_CODE_SPEECH_INPUT = 100;

    override fun onClick(v: View?) {

        when (v?.id) {


            R.id.buttonNext -> {
                if (textview.text.length > 0) {

                    Log.v("NUMBER MATCH",intent.getStringExtra(MOBILENUMBER)+".."+textview.text)
                    if (intent.getStringExtra(MOBILENUMBER).equals("+91"+textview.text)) {



                        visitorLog(intent.getIntExtra("UNITID",0), intent.getStringExtra("FIRSTNAME") + " " +intent.getStringExtra("LASTNAME"),
                            intent.getStringExtra(MOBILENUMBER),intent.getStringExtra("DESIGNATION"), intent.getStringExtra("WORKTYPE"),intent.getIntExtra("WORKERID",0), intent.getStringExtra("UNITNAME")
                        );
                        buttonNext.setEnabled(false)
                        buttonNext.setClickable(false)
                    }
                    else {
                        buttonNext.setEnabled(true)
                        buttonNext.setClickable(true)
                        textview.text=""
                      Toast.makeText(this, "Enter valid staff number", Toast.LENGTH_SHORT).show()
                   }

//                    val d = Intent(this@MobileNumberforEntryScreen, NameEntryScreen::class.java)
//                    Log.d(
//                        "intentdata MobileNumber",
//                        "buttonNext " + intent.getStringExtra(UNITNAME) + " " + intent.getStringExtra(UNITID)
//                                + " " + textview.text + " " + countryCode
//                    );
//                    d.putExtra(FLOW_TYPE, intent.getStringExtra(FLOW_TYPE))
//                    d.putExtra(VISITOR_TYPE, intent.getStringExtra(VISITOR_TYPE))
//                    d.putExtra(COMPANY_NAME, intent.getStringExtra(COMPANY_NAME))
//                    d.putExtra(UNITID, intent.getStringExtra(UNITID))
//                    d.putExtra(UNITNAME, intent.getStringExtra(UNITNAME))
//                    d.putExtra(MOBILENUMBER, textview.getText().toString())
//                    d.putExtra(COUNTRYCODE, countryCode)
//                    startActivity(d);
//                  finish();

//                     deliveryFlow_launchNameEntryScreen()

//                   if (entryExists(countryCode.toString(), textview.getText().toString())) {
////                        Toast.makeText(this,"Mobile Number already used for Visitor Entry", Toast.LENGTH_SHORT).show()
//                        val builder = AlertDialog.Builder(this@MobileNumberforEntryScreen)
//                       // builder.setTitle("Vendor Entry already done")
//                        builder.setMessage("Number is already registered")
//                        builder.setPositiveButton("Ok") { dialog, which ->
//
//
//                            dialog.cancel()
//                            val d = Intent(this@MobileNumberforEntryScreen, DashBoard::class.java)
//                            startActivity(d)
//                            finish()
//                        }
//                        builder.setCancelable(false);
//                        builder.show()
//                    } else {
//                        getAccountDetails(countryCode.toString(), textview.getText().toString());
//
//                    }
//
//                } else if(Ed_phoneNum.text.length == 10) {
//                    val d = Intent(this@MobileNumberforEntryScreen, NameEntryScreen::class.java)
//                    Log.d(
//                        "intentdata MobileNumber",
//                        "buttonNext " + intent.getStringExtra(UNITNAME) + " " + intent.getStringExtra(UNITID)
//                                + " " + Ed_phoneNum.text + " " + countryCode
//                    );
//                    d.putExtra(FLOW_TYPE, intent.getStringExtra(FLOW_TYPE))
//                    d.putExtra(VISITOR_TYPE, intent.getStringExtra(VISITOR_TYPE))
//                    d.putExtra(COMPANY_NAME, intent.getStringExtra(COMPANY_NAME))
//                    d.putExtra(UNITID, intent.getStringExtra(UNITID))
//                    d.putExtra(UNITNAME, intent.getStringExtra(UNITNAME))
//                    d.putExtra(MOBILENUMBER, Ed_phoneNum.getText().toString())
//                    d.putExtra(COUNTRYCODE, countryCode)
//
//                    startActivity(d);
//                    finish();
////                    getAccountDetails("+"+countryCode.toString(),textview.getText().toString());
//                }
//                    else {
//                        Toast.makeText(this, "Enter Valid Number", Toast.LENGTH_SHORT).show()
//
//                    }
                }

            }
        }
    }

//TODO sumeeth has reomoved this fucntion for crashing porpose

    val entries: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocale(Prefs.getString(PrefKeys.LANGUAGE, null))


       // buttonSkip.setVisibility(View.INVISIBLE)


        setContentView(R.layout.activity_mobilenumberforentry)

        buttonSkip=findViewById(R.id.buttonSkip)
        buttonSkip?.visibility=View.INVISIBLE
        txt_assn_name=findViewById(R.id.txt_assn_name)
        txt_gate_name=findViewById(R.id.txt_gate_name)
        txt_device_name=findViewById(R.id.txt_device_name)
        if(Prefs.getString(PrefKeys.MODEL_NUMBER,null).equals("Nokia 1")) {
            txt_assn_name!!.setTextSize(5 * getResources().getDisplayMetrics().density);
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





        val mobilePHONEDATA:String = Prefs.getString(PrefKeys.MOBILE_NUMBER,"")



        val input =Prefs.getString(PrefKeys.MOBILE_NUMBER,"")
        val countrycode = Prefs.getString(PrefKeys.COUNTRY_CODE,"")

        val number = input.replaceFirst("(\\d{3})(\\d{3})(\\d+)".toRegex(), "$1 $2 $3")



        tv_guardnumber.setText(resources.getString(R.string.textgivemissedcall)+" "+"+"+countrycode+" "+number)

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
                    if (report.isAnyPermissionPermanentlyDenied()) {
                        // show alert dialog navigating to Settings
                        showSettingsDialog();
                    }
                }


                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    if (token != null) {
                        token.continuePermissionRequest()
                    };
                }
            }).withErrorListener(object : PermissionRequestErrorListener {

                override fun onError(error: DexterError) {
                    Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                }
            })
            .onSameThread()
            .check();

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
        intent.setData(uri)
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
                                "buttonNext " + getIntent().getStringExtra(UNITNAME) + " " + intent.getStringExtra(
                                    UNITID
                                )
                                        + " " + getIntent().getStringExtra(MOBILENUMBER) + " " + getIntent().getStringExtra(
                                    COUNTRYCODE
                                ) + " "
                                        + globalApiObject.data.accountByMobile[0].acfName + " " + globalApiObject.data.accountByMobile[0].aclName
                            );
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

                            startActivity(d);
                            finish();

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
        );
        d.putExtra(FLOW_TYPE, intent.getStringExtra(FLOW_TYPE))
        d.putExtra(VISITOR_TYPE, intent.getStringExtra(VISITOR_TYPE))
        d.putExtra(COMPANY_NAME, intent.getStringExtra(COMPANY_NAME))
        d.putExtra(UNITID, intent.getStringExtra(UNITID))
        d.putExtra(UNITNAME, intent.getStringExtra(UNITNAME))
        d.putExtra(MOBILENUMBER, textview.getText().toString())
        d.putExtra(COUNTRYCODE, countryCode)

        startActivity(d);
        finish();
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
                    Ed_phoneNum.setText(result[0].trim() + "")
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

    override fun onResume() {
        super.onResume()

        val telephony = this@MobileNumberforEntryScreen.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        telephony.listen(object : PhoneStateListener() {
            override fun onCallStateChanged(state: Int, incomingNumber: String) {
                super.onCallStateChanged(state, incomingNumber)
                when (state) {

                    // not getting incoming number in latest version of android
                    TelephonyManager.CALL_STATE_RINGING -> {
                        Log.e("Shalini Pareek", "incomingNumber: $incomingNumber")
                        phonenumber = "$incomingNumber"
                        if (phonenumber?.length == 13) {
                            Log.d("Shalini length", "length: " + phonenumber?.substring(3, 13))
                           // Toast.makeText(this@MobileNumberScreen,phonenumber?.substring(3, 13),Toast.LENGTH_LONG).show()
                            textview.text = phonenumber?.substring(3, 13)

//                            ccp.setCountryForPhoneCode()

                            //TODO romove the data

                        } else if (phonenumber?.length == 10) {
                            textview.text = phonenumber

//                        }else if(phonenumber.contains("+91")){
//                            textview.text=phonenumber?.replace("+91","")
                        } else {
                           // textview.text = phonenumber)
                            if(textview.text.toString().length==0){
                                val i=Intent(this@MobileNumberforEntryScreen,Dashboard::class.java)
                                startActivity(i)

                            }
                        }

                    }
TelephonyManager.CALL_STATE_IDLE->{
    if (laststate==TelephonyManager.CALL_STATE_RINGING){
        if(textview.text.toString().equals(null)){
            val i=Intent(this@MobileNumberforEntryScreen,Dashboard::class.java)
            startActivity(i)

        }
    }

}

                }



            }
        }, PhoneStateListener.LISTEN_CALL_STATE)

//        if(textview.text.toString().length==0){
//            val i=Intent(this@MobileNumberScreen,DashBoard::class.java)
//            startActivity(i)
//
//        }
    }

    private fun visitorLog(unitId:Int,personName:String,mobileNumb:String, desgn:String,
                           workerType:String,staffID:Int,unitName:String) {


        var memID:Int=410;
        if(BASE_URL.contains("dev",true)){
            memID=64;
        }
        else if(BASE_URL.contains("uat",true)){
            memID=64;
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
        val req = CreateVisitorLogReq(Prefs.getInt(ASSOCIATION_ID,0), memID, staffID,
            unitName,unitId ,desgn,
            personName,"",0,"+",mobileNumb,
            "","","","",
            1,workerType,SPPrdImg1, SPPrdImg2, SPPrdImg3, SPPrdImg4, SPPrdImg5
            , SPPrdImg6, SPPrdImg7, SPPrdImg8, SPPrdImg9, SPPrdImg10,"","");
        Log.d("CreateVisitorLogResp","StaffEntry "+req.toString())

        CompositeDisposable().add(
            RetrofitClinet.instance.createVisitorLogCall(OYE247TOKEN,req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : CommonDisposable<CreateVisitorLogResp<VLRData>>() {
                    override fun onSuccessResponse(globalApiObject: CreateVisitorLogResp<VLRData>) {
                        if (globalApiObject.success == true) {
                            // Utils.showToast(applicationContext, intToString(globalApiObject.data.visitorLog.vlVisLgID))
                            visitorEntryLog(globalApiObject.data.visitorLog.vlVisLgID)

                            val ddc  =  Intent(this@MobileNumberforEntryScreen, BackgroundSyncReceiver::class.java)
                            ddc.putExtra(ConstantUtils.BSR_Action, ConstantUtils.SENDFCM_toSYNC_VISITORENTRY)
                            ddc.putExtra("msg", personName+" "+desgn +" is coming to your home")
                            ddc.putExtra("mobNum", mobileNumb)
                            ddc.putExtra("name", personName)
                            ddc.putExtra("nr_id", AppUtils.intToString(globalApiObject.data.visitorLog.vlVisLgID))
                            ddc.putExtra("unitname", unitName)
                            ddc.putExtra("memType", "Owner")
                            ddc.putExtra(COMPANY_NAME,intent.getStringExtra(COMPANY_NAME))
//                        intent.getStringExtra("msg"),intent.getStringExtra("mobNum"),
//                        intent.getStringExtra("name"),intent.getStringExtra("nr_id"),
//                        intent.getStringExtra("unitname"),intent.getStringExtra("memType")
                            this@MobileNumberforEntryScreen.sendBroadcast(ddc);

                            Log.d("CreateVisitorLogResp","StaffEntry "+globalApiObject.data.toString())
                        } else {
                            Log.d("CreateVisitorLogResp","StaffEntry "+globalApiObject.toString())

                            Utils.showToast(this@MobileNumberforEntryScreen, "Entry not Saved"+globalApiObject.toString())
                        }
                    }

                    override fun onErrorResponse(e: Throwable) {
                        Log.d("onErrorResponse","StaffEntry "+e.toString())

                        Utils.showToast(this@MobileNumberforEntryScreen, "Something went wrong")
//                    dismissProgress()
                    }

                    override fun noNetowork() {
                        Utils.showToast(this@MobileNumberforEntryScreen, "No Internet")
                    }

                    override fun onShowProgress() {
//                    showProgress()
                    }

                    override fun onDismissProgress() {
//                    dismissProgress()
                    }
                }))
    }

    private fun visitorEntryLog( visitorLogID: Int) {
//        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
//        val currentDate = sdf.format(Date())
//        System.out.println(" C DATE is  "+currentDate)

        val req = VisitorEntryReq(DateTimeUtils.getCurrentTimeLocal(), LocalDb.getStaffList()[0].wkWorkID, visitorLogID)
        Log.d("CreateVisitorLogResp","StaffEntry "+req.toString())

        CompositeDisposable().add(RetrofitClinet.instance.visitorEntryCall(OYE247TOKEN,req)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CommonDisposable<VisitorExitResp>() {
                override fun onSuccessResponse(globalApiObject: VisitorExitResp) {
                    if (globalApiObject.success == true) {
//                        Log.d("VisitorEntryReq","StaffEntry "+globalApiObject.data.toString())
                        finish()
                    } else {
                        Utils.showToast(this@MobileNumberforEntryScreen, globalApiObject.apiVersion)
                    }
                }

                override fun onErrorResponse(e: Throwable) {
                    Utils.showToast(this@MobileNumberforEntryScreen, "Something went wrong")
//                    dismissProgress()
                }

                override fun noNetowork() {
                    Utils.showToast(this@MobileNumberforEntryScreen, "No Internet")
                }

                override fun onShowProgress() {
//                    showProgress()
                }

                override fun onDismissProgress() {
//                    dismissProgress()
                }
            }))
    }

}