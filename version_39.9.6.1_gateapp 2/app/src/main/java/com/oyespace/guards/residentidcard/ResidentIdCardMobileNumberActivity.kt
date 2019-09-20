package com.oyespace.guards.residentidcard

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.hbb20.CountryCodePicker
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.DexterError
import com.karumi.dexter.listener.PermissionRequestErrorListener
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.oyespace.guards.Dashboard
import com.oyespace.guards.R
import com.oyespace.guards.activity.BaseKotlinActivity
import com.oyespace.guards.camtest.AddCarFragment
import com.oyespace.guards.constants.PrefKeys
import com.oyespace.guards.guest.GuestUnitScreen
import com.oyespace.guards.network.CommonDisposable
import com.oyespace.guards.network.RetrofitClinet
import com.oyespace.guards.pojo.*
import com.oyespace.guards.request.ResidentValidationRequest
import com.oyespace.guards.responce.ResidentValidationResponse
import com.oyespace.guards.responce.VisitorLogExitResp
import com.oyespace.guards.utils.*
import com.oyespace.guards.utils.ConstantUtils.*
import com.oyespace.guards.utils.RandomUtils.entryExists
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_mobile_number.*
import kotlinx.android.synthetic.main.activity_mobile_number.btn_mic
import kotlinx.android.synthetic.main.activity_mobile_number.buttonNext
import kotlinx.android.synthetic.main.activity_unit_list.*
import java.lang.reflect.Method
import java.util.*


class ResidentIdCardMobileNumberActivity : BaseKotlinActivity(), View.OnClickListener, CountryCodePicker.OnCountryChangeListener {
    val workType: ArrayList<String> = ArrayList();
    private var ccp: CountryCodePicker? = null
    private var countryCode: String? = null
    private var countryName: String? = null
    lateinit var pDialog: ProgressDialog
    var phonenumber: String? = null
    lateinit var txt_assn_name:TextView
    lateinit var txt_gate_name:TextView
    lateinit var txt_device_name: TextView
    lateinit var timer:TextView
    val laststate:Int?=null
    var progressBar: ProgressBar?=null
    var ccd:String?=null
    lateinit var btn_nobalance: Button
    var mobileNumber:String?=null
    //internal lateinit var alertDialog: android.support.v7.app.AlertDialog
    // private var Ed_phoneNum:String?=null

    private val REQUEST_CODE_SPEECH_INPUT = 100;

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.Btn_SendOtp ->
            {

                Toast.makeText(this@ResidentIdCardMobileNumberActivity, "Coming soon", Toast.LENGTH_LONG)
                    .show()
            }

            R.id.buttonSkip -> {


            }

            R.id.btn_nobalance->{



            }

            R.id.buttonNext -> {
                buttonNext.setEnabled(false)
                buttonNext.setClickable(false)

                if (textview.text.length == 13) {

                         //  getAccountDetails(ccd.toString(), mobileNumber.toString());
                   // getResidentValidation(ccd.toString(),mobileNumber.toString())

                }

                else {
                    buttonNext.setEnabled(true)
                    buttonNext.setClickable(true)
                    Toast.makeText(this, "Invalid number captured", Toast.LENGTH_SHORT).show()

                }
            }

        }
    }

//TODO sumeeth has reomoved this fucntion for crashing porpose

    val entries: ArrayList<String> = ArrayList()
    var receiver:BroadcastReceiver?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocale(Prefs.getString(PrefKeys.LANGUAGE, null))
        setContentView(R.layout.activity_mobile_number)

        //  Toast.makeText(applicationContext, "coming", Toast.LENGTH_LONG).show();
      //  Toast.makeText(this@MobileNumberScreen,intent.getStringExtra( "RESIDENT_NUMBER"),Toast.LENGTH_LONG).show()
        buttonNext.setText(resources.getString(R.string.textdone))
        buttonSkip.visibility=View.GONE

        receiver =  object : BroadcastReceiver() {

            override fun onReceive(context: Context?, intent: Intent?) {
                val telephony = context?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                telephony.listen(object : PhoneStateListener() {

                    override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                        super.onCallStateChanged(state, phoneNumber)
                        if (state == TelephonyManager.CALL_STATE_RINGING) {

                            val bundle = intent?.getExtras();
                            val number = bundle?.getString("incoming_number");

                          //  Toast.makeText(applicationContext, number, Toast.LENGTH_LONG).show();
                            if (textview != null && number != null) {
                               // textview.text = number.replace("+91", "")
                                textview.text = number

                                ccd= number.substring(0,3)

                                mobileNumber=number.substring(3,13)
                               // endCall(this@MobileNumberScreen)

                            }
                        }
                    }

                }, PhoneStateListener.LISTEN_CALL_STATE);

                //
            }
        };


        btn_nobalance=findViewById(R.id.btn_nobalance)
        progressBar = this.progressBar1
        timer=findViewById(R.id.timer)
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


        val timer = object: CountDownTimer (60000,1000){
            override fun onTick(millisUntilFinished: Long) {

                val remainedSecs: Long  = millisUntilFinished / 1000;
                timer.text=("0" + (remainedSecs / 60) + ":" + (remainedSecs % 60));// manage it accordign to you
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
            "" + getIntent().getStringExtra(UNITNAME) + " " + intent.getStringExtra(UNITID)
        );

        btn_mic.setOnClickListener {
            Speak()
        }

//        if (intent.getStringExtra(FLOW_TYPE).equals(STAFF_REGISTRATION)) {
//           // buttonSkip.setVisibility(View.VISIBLE)
//           // if (Prefs.getString(PrefKeys.MODEL_NUMBER, null) == "Nokia 2.1") {
//            if(workType.contains(intent.getStringExtra(COMPANY_NAME))){
//                buttonSkip.visibility=View.INVISIBLE
//            }
//            else{
//                buttonSkip.visibility=View.VISIBLE
//            }
//           // }
////        else{
////                buttonSkip.visibility=View.INVISIBLE
////            }
//            img_logo.visibility=View.VISIBLE
////            Ed_phoneNum.setVisibility(View.VISIBLE)
////            textview.visibility = View.GONE
//            Ed_phoneNum.setVisibility(View.GONE)
//            textview.visibility = View.VISIBLE
//        } else {
//            buttonSkip.setVisibility(View.INVISIBLE)
//            textview.visibility = View.VISIBLE
//        }

        val mobilePHONEDATA:String = Prefs.getString(PrefKeys.MOBILE_NUMBER,"")



        val countrycode = Prefs.getString(PrefKeys.COUNTRY_CODE,"")

        val input =Prefs.getString(PrefKeys.MOBILE_NUMBER,"")
       // val number = input.replaceFirst("(\\d{3})(\\d{3})(\\d+)".toRegex(), "$1 $2 $3")
        val number = input.replaceFirst("(\\d{2})(\\d{4})(\\d{3})(\\d+)".toRegex(), "$1 $2 $3 $4")
        tv_guardnumber.setText(resources.getString(R.string.textgivemissedcall)+" +"+number)

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

                pDialog = ProgressDialog(this@ResidentIdCardMobileNumberActivity)
                pDialog!!.setMessage("Loading..")
                pDialog!!.setCancelable(false)
                pDialog!!.isIndeterminate = false
                pDialog!!.show()
            }

            if (TextUtils.isEmpty(Ed_phoneNum.text.toString()) || countryCode!!.startsWith("+91")) {
                Toast.makeText(this, "Enter your phone number", Toast.LENGTH_SHORT).show()
                val maxLength = 10
                Ed_phoneNum.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength)))

            } else if (countryCode!!.startsWith("+91") && Ed_phoneNum.length() <= 10) {
                Toast.makeText(this, "number should be 10 digits", Toast.LENGTH_LONG).show()
            } else {
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



    }

    private fun showSettingsDialog() {
        val builder = AlertDialog.Builder(this@ResidentIdCardMobileNumberActivity)
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




    fun getAccountDetails(isdCode: String, MobNumber: String) {
        progressBar?.visibility = View.VISIBLE


        val req = GetAccountDetailsByMobReq( isdCode, MobNumber)
        Log.d("getAccountDetails", req.toString())
        compositeDisposable.add(
            RetrofitClinet.instance.GetAccountDetailsByMobCall(CHAMPTOKEN, req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : CommonDisposable<GetAccountDetailsByMobResp<AccountByMobile>>() {
                    override fun onSuccessResponse(globalApiObject: GetAccountDetailsByMobResp<AccountByMobile>) {
                        if (globalApiObject.data != null) {
                            progressBar?.visibility = View.GONE

                            //makeUnitLog(isdCode,mobileNumber.toString())


                        } else {
                            progressBar?.visibility = View.GONE
                        }
                    }

                    override fun onErrorResponse(e: Throwable) {
                        progressBar?.visibility = View.GONE

                    }

                    override fun noNetowork() {
                        progressBar?.visibility = View.GONE

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

//
//
//    private fun getResidentValidation(ccd:String,mobilenumber: String) {
//
//        val req = ResidentValidationRequest(ccd+mobilenumber.toString(), LocalDb.getAssociation()!!.asAssnID
//        )
//        CompositeDisposable().add(
//            RetrofitClinet.instance.residentValidation("7470AD35-D51C-42AC-BC21-F45685805BBE", req)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeWith(object : CommonDisposable<ResidentValidationResponse>() {
//                    override fun onSuccessResponse(globalApiObject: ResidentValidationResponse) {
//                        if (globalApiObject.success == true) {
//                            val viewGroup = findViewById<ViewGroup>(android.R.id.content)
//
//                            val dialogView = LayoutInflater.from(this@ResidentIdCardMobileNumberActivity)
//                                .inflate(R.layout.layout_qrcodedailog, viewGroup, false)
//
//
//                            val builder =
//                                android.support.v7.app.AlertDialog.Builder(this@ResidentIdCardMobileNumberActivity)
//
//                            val dialog_imageview = dialogView.findViewById<ImageView>(R.id.dialog_imageview)
//                            val tv_msg = dialogView.findViewById<TextView>(R.id.tv_msg)
//                            tv_msg.setText("Valid")
//                            val drawable = resources.getDrawable(R.drawable.valid_invi)
//                            dialog_imageview.setImageDrawable(drawable)
//                            val btn_ok = dialogView.findViewById<Button>(R.id.btn_ok)
//                            btn_ok.setOnClickListener(View.OnClickListener {
//                                alertDialog.dismiss()
//
//                                finish()
//                            })
//
//                            builder.setView(dialogView)
//
//                            //finally creating the alert dialog and displaying it
//                            alertDialog = builder.create()
//
//                            alertDialog.show()                        } else {
//                            Utils.showToast(this@ResidentIdCardMobileNumberActivity, "Failed")
//                        }
//                    }
//
//                    override fun onErrorResponse(e: Throwable) {
//                        Utils.showToast(this@ResidentIdCardMobileNumberActivity, "Not a resident")
//                        Utils.showToast(this@ResidentIdCardMobileNumberActivity, "Error visitor exit")
//                    }
//
//                    override fun noNetowork() {
//                        Utils.showToast(this@ResidentIdCardMobileNumberActivity, "no_internet visitor exit")
//                    }
//
//                    override fun onShowProgress() {
////                        showProgress()
//                    }
//
//                    override fun onDismissProgress() {
////                        dismissProgress()
//                    }
//                })
//        )
//
//
//    }
//


}