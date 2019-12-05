package com.oyespace.guards.resident

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
import android.text.InputFilter
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
import com.oyespace.guards.Dashboard
import com.oyespace.guards.R
import com.oyespace.guards.activity.BaseKotlinActivity
import com.oyespace.guards.camtest.AddCarFragment
import com.oyespace.guards.com.oyespace.guards.resident.ResidentChecker
import com.oyespace.guards.constants.PrefKeys
import com.oyespace.guards.network.CommonDisposable
import com.oyespace.guards.network.RetrofitClinet
import com.oyespace.guards.pojo.*
import com.oyespace.guards.repo.StaffRepo
import com.oyespace.guards.repo.VisitorLogRepo
import com.oyespace.guards.utils.ConstantUtils
import com.oyespace.guards.utils.ConstantUtils.*
import com.oyespace.guards.utils.LocalDb
import com.oyespace.guards.utils.Prefs
import com.oyespace.guards.utils.RandomUtils.entryExists
import com.oyespace.guards.utils.Utils
import com.yarolegovich.lovelydialog.LovelyStandardDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_mobile_number.*
import java.util.*


class ResidentMobileNumberScreenwithOTP : BaseKotlinActivity(), View.OnClickListener, CountryCodePicker.OnCountryChangeListener {
    val workType: ArrayList<String> = ArrayList()
    private var ccp: CountryCodePicker? = null
    private var countryCode: String? = null
    private var countryName: String? = null
    lateinit var pDialog: ProgressDialog
    var phonenumber: String? = null
    lateinit var txt_assn_name: TextView
    lateinit var txt_gate_name: TextView
    lateinit var txt_device_name: TextView
    // lateinit var timer:TextView
    val laststate: Int? = null
    var progressBar: ProgressBar? = null
    var mobilenumber: String? = null
    var otpnumber: String? = null
    var phone: String? = null
    var dialogs: Dialog? = null

    // private var Ed_phoneNum:String?=null

    private val REQUEST_CODE_SPEECH_INPUT = 100

    override fun onClick(v: View?) {

    }

//TODO sumeeth has reomoved this fucntion for crashing porpose

    val entries: ArrayList<String> = ArrayList()
    var receiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocale(Prefs.getString(PrefKeys.LANGUAGE, null))


        setContentView(R.layout.layout_mobilenumber_otp)


        progressBar = this.progressBar1
        // timer=findViewById(R.id.timer)
        txt_assn_name = findViewById(R.id.txt_assn_name)
        txt_gate_name = findViewById(R.id.txt_gate_name)
        txt_device_name = findViewById(R.id.txt_device_name)
        //txt_assn_name.text = "Society: " + LocalDb.getAssociation()!!.asAsnName
        txt_assn_name.text = LocalDb.getAssociation()!!.asAsnName
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

            // buttonSkip.setVisibility(View.VISIBLE)
//            if (Prefs.getString(PrefKeys.MODEL_NUMBER, null) == "Nokia 2.1") {
//                if (workType.contains(intent.getStringExtra(COMPANY_NAME))) {
//                    buttonSkip.visibility = View.INVISIBLE
//                } else {
//                    buttonSkip.visibility = View.VISIBLE
//                }
//            } else {
//                buttonSkip.visibility = View.INVISIBLE
//            }
            img_logo.visibility = View.VISIBLE
            Ed_phoneNum.visibility = View.VISIBLE
            textview.visibility = View.GONE



        ccp = findViewById(R.id.country_code_picker)
        ccp!!.setOnCountryChangeListener(this)
        countryCode = ccp!!.selectedCountryCode

        Btn_SendOtp.setOnClickListener {

            mobilenumber = phone
            phone = Ed_phoneNum.text.toString().replace(" ", "")
            if (TextUtils.isEmpty(Ed_phoneNum.text.trim().toString()) || countryCode!!.startsWith("+91")) {
                Toast.makeText(this, "Enter your phone number", Toast.LENGTH_SHORT).show()
                val maxLength = 10
                Ed_phoneNum.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength))

            } else if (phone!!.length < 10) {
                Toast.makeText(this, "Enter valid mobile number", Toast.LENGTH_LONG).show()
            } else {

                val flow = intent.getStringExtra(FLOW_TYPE)

                val allowEntry = when (flow) {
                    STAFF_REGISTRATION -> !StaffRepo.checkExistingStaffForPhone(phone!!)
                    FULL_MANUAL_STAFF_ENTRY -> !VisitorLogRepo.check_IN_StaffVisitorByPhone(phone)
                    else -> VisitorLogRepo.allowEntry(countryCode.toString(), phone)
                }

                if (!allowEntry) {
//                        Toast.makeText(this,"Mobile Number already used for Visitor Entry", Toast.LENGTH_SHORT).show()
                    val builder = AlertDialog.Builder(this@ResidentMobileNumberScreenwithOTP)
                    // builder.setTitle("Vendor Entry already done")
                    builder.setMessage(
                        if (flow == STAFF_REGISTRATION) {
                            "Number is already registered"
                        } else {
                            "Duplicate entry not allowed"
                        }
                    )
                    builder.setPositiveButton("Ok") { dialog, which ->


                        dialog.cancel()

                        finish()
                    }
                    builder.setCancelable(false)
                    builder.show()
                } else {
                    sendotp()

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
        val builder = AlertDialog.Builder(this@ResidentMobileNumberScreenwithOTP)
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
                    //  Toast.makeText(this@MobileNumberScreenwithOTP,phone,Toast.LENGTH_LONG).show()


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



    private fun showDialog(title: String) {
        dialogs = Dialog(this@ResidentMobileNumberScreenwithOTP)

        dialogs!!.setCancelable(false)
        dialogs!!.setContentView(R.layout.layout_otp_dialog)
        val ed_otp = dialogs!!.findViewById(R.id.ed_otp) as EditText
        otpnumber = ed_otp.text.toString()
        val btn_verifyotp = dialogs!!.findViewById(R.id.btn_verifyotp) as Button
        val btn_cancel = dialogs!!.findViewById(R.id.btn_cancel) as Button
        btn_verifyotp.setOnClickListener {


            if (TextUtils.isEmpty(ed_otp.text.toString())) {

                // Toast.makeText(this, "Enter OTP ", Toast.LENGTH_SHORT).show()

                LovelyStandardDialog(this@ResidentMobileNumberScreenwithOTP, LovelyStandardDialog.ButtonLayout.VERTICAL)
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
        btn_cancel.setOnClickListener {
            dialogs!!.dismiss()
        }

        dialogs!!.show()

    }

    fun verifyOTP(number: String) {

        countryCode.toString()
        phone

        Log.d("sdssds", "Verify otp " + countryCode.toString() + " " + Ed_phoneNum.text.trim().toString() + " " + number)
        val req = GetVerifyOTPRequest(countryCode.toString(), phone.toString(), number)

        compositeDisposable.add(
            RetrofitClinet.instance.getVerifyOTP(ConstantUtils.CHAMPTOKEN, req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : CommonDisposable<GetVerifyOTPResponse>() {
                    override fun onSuccessResponse(globalApiObject: GetVerifyOTPResponse) {
                        if (globalApiObject.success == true) {
                            dialogs!!.dismiss()

                            val ascId = LocalDb.getAssociation().asAssnID

                            ResidentChecker().isResident(
                                "+"+countryCode.toString()+phone.toString(),
                                ascId,
                                object : ResidentChecker.ResponseListener {

                                    override fun onResult(isResident: Boolean) {

                                        if (isResident) {
                                            Utils.getAlertDialog(this@ResidentMobileNumberScreenwithOTP, getString(R.string.valid), -1) {
                                                finish()
                                            }.show()
                                        } else {
                                            Utils.getAlertDialog(this@ResidentMobileNumberScreenwithOTP, getString(R.string.invalid), R.drawable.invalid_invi) {
                                                finish()
                                            }.show()
                                        }
                                        Log.d("check response", "resident: " + isResident)

                                    }

                                    override fun onError(error: String) {

                                        Utils.getAlertDialog(
                                            this@ResidentMobileNumberScreenwithOTP,
                                            getString(R.string.invalid), R.drawable.invalid_invi
                                        ) {
                                            finish()
                                        }.show()

                                    }

                                })                        } else {




                        }
                    }

                    override fun onErrorResponse(e: Throwable) {

                        println("Verify otp " + e.toString())

                        LovelyStandardDialog(this@ResidentMobileNumberScreenwithOTP, LovelyStandardDialog.ButtonLayout.VERTICAL)
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

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}