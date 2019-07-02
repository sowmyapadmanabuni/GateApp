package com.oyespace.guards.activity

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.InputFilter
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.hbb20.CountryCodePicker
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.oyespace.guards.R
import com.oyespace.guards.constants.PrefKeys
import com.oyespace.guards.constants.PrefKeys.*
import com.oyespace.guards.network.CommonDisposable
import com.oyespace.guards.network.RetrofitClinet
import com.oyespace.guards.pojo.*
import com.oyespace.guards.utils.ConstantUtils
import com.oyespace.guards.utils.Prefs
import com.oyespace.guards.utils.Utils
import com.yarolegovich.lovelydialog.LovelyStandardDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_mobile_number.*
import java.util.*

class LoginActivity : BaseKotlinActivity(), View.OnClickListener, CountryCodePicker.OnCountryChangeListener {

    private var ccp: CountryCodePicker? = null
    private var countryCode: String? = null
    private var countryName: String? = null
    private val REQUEST_CODE_SPEECH_INPUT = 100;
    var otpnumber: String? = null
    var mobilenumber:String?=null
    var phone:String?=null
    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.btn_mic -> {
                Speak()
            }
            R.id.Btn_SendOtp -> {

                mobilenumber= phone.toString()

                if (TextUtils.isEmpty(Ed_phoneNum.text.toString())) {

                    LovelyStandardDialog(this, LovelyStandardDialog.ButtonLayout.VERTICAL)
                        .setTopColorRes(R.color.google_red)
                        .setIcon(R.drawable.ic_info_black_24dp)
                        //This will add Don't show again checkbox to the dialog. You can pass any ID as argument
                        .setTitle("invalid")
                        .setTitleGravity(Gravity.CENTER)
                        .setMessage("Enter your phone number")
                        .setMessageGravity(Gravity.CENTER)
                        .setPositiveButton(android.R.string.ok) {

                        }

                        .show()

                    val maxLength = 10
                    Ed_phoneNum.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength)))

                } else if (countryCode!!.startsWith("91") && Ed_phoneNum.text.toString().length < 10) {

                    LovelyStandardDialog(this, LovelyStandardDialog.ButtonLayout.VERTICAL)
                        .setTopColorRes(R.color.google_red)
                        .setIcon(R.drawable.ic_info_black_24dp)
                        //This will add Don't show again checkbox to the dialog. You can pass any ID as argument
                        .setTitle("invalid")
                        .setTitleGravity(Gravity.CENTER)
                        .setMessage("Mobile Number Should be 10 Digit")
                        .setMessageGravity(Gravity.CENTER)
                        .setPositiveButton(android.R.string.ok) {

                        }

                        .show()

                } else if (Ed_phoneNum.text.toString().startsWith("1", true)
                    || Ed_phoneNum.text.toString().startsWith("2", true)
                    || Ed_phoneNum.text.toString().startsWith("3", true)
                    || Ed_phoneNum.text.toString().startsWith("4", true)
                ) {


                 //   Toast.makeText(this, "Mobile Number is invalid", Toast.LENGTH_LONG).show()

                    LovelyStandardDialog(this, LovelyStandardDialog.ButtonLayout.VERTICAL)
                        .setTopColorRes(R.color.google_red)
                        .setIcon(R.drawable.ic_info_black_24dp)
                        //This will add Don't show again checkbox to the dialog. You can pass any ID as argument
                        .setTitle("invalid")
                        .setTitleGravity(Gravity.CENTER)
                        .setMessage("Mobile Number is invalid")
                        .setMessageGravity(Gravity.CENTER)
                        .setPositiveButton(android.R.string.ok) {

                        }

                        .show()


                } else {
                   // verifyNumber(Ed_phoneNum.text.toString())

                   // Prefs.putString(COUNTRY_CODE, countryCode.toString())
                    sendotp()

                    // TODO sumeeth have changed the function .   sendotp()

                }

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocale(Prefs.getString(LANGUAGE, null))
        setContentView(R.layout.activity_login)

        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_SMS,
                Manifest.permission.RECEIVE_SMS
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    // check if all permissions are granted
                    if (report.areAllPermissionsGranted()) {
                        // do you work now
                    }

                    // check for permanent denial of any permission
                    if (report.isAnyPermissionPermanentlyDenied) {
                        // permission is denied permenantly, navigate user to app settings
                        //showSettingsDialog()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            })
            .onSameThread()
            .check()


        ccp = findViewById(R.id.country_code_picker)
        ccp!!.setOnCountryChangeListener(this)
        countryCode = ccp!!.selectedCountryCode
    }

    fun Speak() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "say something")

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)

        } catch (e: Exception) {

        //    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCountrySelected() {
        countryCode = ccp!!.selectedCountryCode
        countryName = ccp!!.selectedCountryName
      //  Toast.makeText(this, "Country Code " + countryCode, Toast.LENGTH_SHORT).show()
        // Toast.makeText(this, "Country Name " + countryName, Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_SPEECH_INPUT -> {
                if (resultCode == Activity.RESULT_OK && null != data) {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    Ed_phoneNum.setText(result[0].trim() + "")
                    phone = Ed_phoneNum.text.toString().replace(" ","");
                }
            }
        }
    }


    fun sendotp() {

        countryCode.toString()
        phone.toString()
        Log.d("sdssds", countryCode.toString() + " " + Ed_phoneNum.text.toString())

        val req = GetOTPReq(countryCode.toString(), phone.toString())
        compositeDisposable.add(
            RetrofitClinet.instance.getOTPCall(ConstantUtils.CHAMPTOKEN, req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : CommonDisposable<GetOTPResp>() {
                    override fun onSuccessResponse(globalApiObject: GetOTPResp) {
                        if (globalApiObject.success == true) {
                          //  Utils.showToast(applicationContext, "OTP Sent")
                            showDialog("Verify OTP")
                        } else {
                          //  Utils.showToast(applicationContext, globalApiObject.apiVersion)
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

    fun verifyOTP(number: String) {

        countryCode.toString()
        phone.toString()

        Log.d("sdssds", "Verify otp " + countryCode.toString() + " " + phone + " " + number)
        val req = GetVerifyOTPRequest(countryCode.toString(), phone.toString(), number)
        Log.d("sdssds", "Verify otp " + req.toString() + " " + phone.toString() + " " + number)

        compositeDisposable.add(
            RetrofitClinet.instance.getVerifyOTP(ConstantUtils.CHAMPTOKEN, req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : CommonDisposable<GetVerifyOTPResponse>() {
                    override fun onSuccessResponse(globalApiObject: GetVerifyOTPResponse) {
                        if (globalApiObject.success == true) {

                            Prefs.putString(PrefKeys.MOBILE_NUMBER,countryCode.toString()+phone)

                            val mainIntent = Intent(this@LoginActivity, MyRoleScreen::class.java)
                            mainIntent.putExtra("MOBIELNUMBER",phone.toString())
                            startActivity(mainIntent)
                            finish()
                            println("Verify otp " + globalApiObject.success)
                        } else {


                            //Utils.showToast(applicationContext, globalApiObject.apiVersion)


                        }
                    }

                    override fun onErrorResponse(e: Throwable) {

                        println("Verify otp " + e.toString())

                        LovelyStandardDialog(this@LoginActivity, LovelyStandardDialog.ButtonLayout.VERTICAL)
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


    private fun showDialog(title: String) {
        var dialogs = Dialog(this@LoginActivity)

        dialogs!!.setCancelable(false)
        dialogs!!.setContentView(R.layout.layout_otp_dialog)
        val ed_otp = dialogs!!.findViewById(R.id.ed_otp) as EditText
        otpnumber = ed_otp.text.toString()
        val btn_cancel= dialogs!!.findViewById(R.id.btn_cancel) as Button
        val btn_verifyotp = dialogs!!.findViewById(R.id.btn_verifyotp) as Button
        btn_verifyotp.setOnClickListener {


            if (TextUtils.isEmpty(ed_otp.text.toString())) {

               // Toast.makeText(this, "Enter OTP ", Toast.LENGTH_SHORT).show()

                LovelyStandardDialog(this@LoginActivity, LovelyStandardDialog.ButtonLayout.VERTICAL)
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
        dialogs.show()

    }


    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action!!.equals("otp", ignoreCase = true)) {
                val message = intent.getStringExtra("message")
                val sender = intent.getStringExtra("sender")
                if (sender.contains("OyeSpc")) {
                    val otp_number = message.replace("[^0-9]".toRegex(), "")
                    Log.d("rere", "$message $otp_number")

                    try {
                        java.lang.Long.parseLong(otp_number)

                        verifyOTP(otp_number)
                    } catch (ex: Exception) {
                        Log.d("rere ex", "$ex $otp_number")
                    }

                }

            }
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        finish()
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
}
