package com.oyespace.guards.vehicle_others

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.oyespace.guards.R
import com.oyespace.guards.activity.BaseKotlinActivity
import com.oyespace.guards.constants.PrefKeys.LANGUAGE
import com.oyespace.guards.utils.ConstantUtils
import com.oyespace.guards.utils.ConstantUtils.*
import com.oyespace.guards.utils.LocalDb
import com.oyespace.guards.utils.Prefs
import kotlinx.android.synthetic.main.activity_purpose.*
import java.util.*

class VehicleOthersPurposeNameEntryScreen : BaseKotlinActivity(), View.OnClickListener {
    private val REQUEST_CODE_SPEECH_INPUT = 100
    lateinit var txt_assn_name: TextView
    lateinit var txt_gate_name: TextView
    lateinit var txt_device_name: TextView
    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.buttonNextt -> {
                buttonNextt.isEnabled = false
                buttonNextt.isClickable = false

                if (Ed_Name_purp.text.length > 2) {
//                    val d = Intent(this@NameEntryScreen, CameraActivity::class.java)
                    val d = Intent(this@VehicleOthersPurposeNameEntryScreen, VehicleOthersMobileNumberScreen::class.java)

//                    Log.d(
//                        "intentdata NameEntr",
//                        "buttonNext " + intent.getStringExtra(UNITNAME) + " " + intent.getStringExtra(UNITID)
//                                + " " + intent.getStringExtra(MOBILENUMBER) + " " + intent.getStringExtra(
//                            COUNTRYCODE
//                        ) + " " + Ed_Name_purp.text
//                    )
//                    d.putExtra(UNITID, intent.getStringExtra(UNITID))
//                    d.putExtra(UNITNAME, intent.getStringExtra(UNITNAME))
//                    d.putExtra(FLOW_TYPE, intent.getStringExtra(FLOW_TYPE))
//                    d.putExtra(VISITOR_TYPE, intent.getStringExtra(VISITOR_TYPE))
//                    d.putExtra(COMPANY_NAME, Ed_Name_purp.getText().toString())
//                    d.putExtra(MOBILENUMBER, intent.getStringExtra(MOBILENUMBER))
//                    d.putExtra(COUNTRYCODE, intent.getStringExtra(COUNTRYCODE))
//                    d.putExtra(PERSONNAME,  getIntent().getStringExtra(PERSONNAME))
//                    d.putExtra(VEHICLE_NUMBER, intent.getStringExtra(VEHICLE_NUMBER))


                    d.putExtra(FLOW_TYPE,intent.getStringExtra(FLOW_TYPE))
                    d.putExtra(VISITOR_TYPE,intent.getStringExtra(VISITOR_TYPE))
                    d.putExtra(COMPANY_NAME,intent.getStringExtra(COMPANY_NAME))
                    d.putExtra(UNITID, intent.getStringExtra(UNITID))
                    d.putExtra(UNITNAME, intent.getStringExtra(UNITNAME))
                   // d.putExtra(MOBILENUMBER, Ed_phoneNum.getText().toString())
                   // d.putExtra(COUNTRYCODE, countryCode)
                    d.putExtra(VEHICLE_NUMBER,intent.getStringExtra(VEHICLE_NUMBER))
                    d.putExtra(UNIT_ACCOUNT_ID,intent.getStringExtra(ConstantUtils.UNIT_ACCOUNT_ID))
                    d.putExtra(VISITOR_PURPOSE, Ed_Name_purp.text.toString())
                    startActivity(d)
                    finish()
                } else {
                    buttonNextt.isEnabled = true
                    buttonNextt.isClickable = true
                    Toast.makeText(this, "Enter Valid Purpose", Toast.LENGTH_SHORT).show()

                }
            }

        }
    }

    val entries: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocale(Prefs.getString(LANGUAGE, null))
        setContentView(R.layout.activity_purpose)
        txt_assn_name=findViewById(R.id.txt_assn_name)
        txt_gate_name=findViewById(R.id.txt_gate_name)
        txt_device_name=findViewById(R.id.txt_device_name)

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

        Btn_Mic.setOnClickListener {
            Speak()
        }

//        Log.d(
//            "intentdata NameEntr", "" + intent.getStringExtra(UNITNAME) + " " + intent.getStringExtra(UNITID)
//                    + " " + intent.getStringExtra(MOBILENUMBER) + " " + intent.getStringExtra(COUNTRYCODE)
//        )

    }

//    override fun onBackPressed() {
//        super.onBackPressed()
//
//        val d = Intent(this@VehicleOthersPurposeNameEntryScreen, VehicleOthersUnitScreen::class.java)
//        d.putExtra(FLOW_TYPE, intent.getStringExtra(FLOW_TYPE))
//        d.putExtra(VISITOR_TYPE, intent.getStringExtra(VISITOR_TYPE))
//        d.putExtra(COMPANY_NAME, intent.getStringExtra(COMPANY_NAME))
//        d.putExtra(UNITID, intent.getStringExtra(UNITID))
//        d.putExtra(UNITNAME, intent.getStringExtra(UNITNAME))
//        d.putExtra(MOBILENUMBER, getIntent().getStringExtra(MOBILENUMBER))
//        d.putExtra(COUNTRYCODE, getIntent().getStringExtra(COUNTRYCODE))
//        d.putExtra(VEHICLE_NUMBER, intent.getStringExtra(VEHICLE_NUMBER))
//
//        startActivity(d)
//
//    }

    fun Speak() {

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        //intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN")
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "say something")

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_SPEECH_INPUT -> {
                if (resultCode == Activity.RESULT_OK && null != data) {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    Ed_Name_purp.setText(result[0].replace(" ", "").trim())

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

    override fun onBackPressed() {
        super.onBackPressed()
//        val intent= Intent(this@VehicleOthersPurposeNameEntryScreen, Dashboard::class.java)
//        startActivity(intent)
        finish()
    }
}