package com.oyespace.guards.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.oyespace.guards.R
import com.oyespace.guards.constants.PrefKeys
import com.oyespace.guards.staffManaualEntry.ManualMobileNumberScreen
import com.oyespace.guards.utils.ConstantUtils
import com.oyespace.guards.utils.ConstantUtils.*
import com.oyespace.guards.utils.LocalDb
import com.oyespace.guards.utils.Prefs
import kotlinx.android.synthetic.main.activity_purpose.*
import kotlinx.android.synthetic.main.header_with_next.*
import java.util.*


class PurposeScreen : BaseKotlinActivity()  ,View.OnClickListener {
    private val REQUEST_CODE_SPEECH_INPUT = 100
    lateinit var txt_assn_name:TextView
    lateinit var txt_gate_name: TextView
    lateinit var txt_device_name:TextView
    internal var language: String? = ""
    var iv_torch: Button?=null
    var clickable1 = 0
    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.buttonNext -> {
                buttonNext.isEnabled = false
                buttonNext.isClickable = false
                if (Ed_Name_purp.text.length > 2) {

                   if (Prefs.getString(TYPE, "").equals("Create")) {
                        val d = Intent(this@PurposeScreen, MobileNumberScreen::class.java)
                        d.putExtra(ConstantUtils.UNITID, intent.getStringExtra(ConstantUtils.UNITID))
                        d.putExtra(ConstantUtils.UNITNAME, intent.getStringExtra(ConstantUtils.UNITNAME))
                        d.putExtra(ConstantUtils.FLOW_TYPE, intent.getStringExtra(ConstantUtils.FLOW_TYPE))
                        d.putExtra(ConstantUtils.VISITOR_TYPE, intent.getStringExtra(ConstantUtils.VISITOR_TYPE))
                        d.putExtra(ConstantUtils.COMPANY_NAME, intent.getStringExtra(ConstantUtils.COMPANY_NAME))
                        d.putExtra(ConstantUtils.MOBILENUMBER, intent.getStringExtra(ConstantUtils.MOBILENUMBER))
                        d.putExtra(ConstantUtils.COUNTRYCODE, intent.getStringExtra(ConstantUtils.COUNTRYCODE))
                        d.putExtra(ConstantUtils.UNIT_ACCOUNT_ID, intent.getStringExtra(UNIT_ACCOUNT_ID))
                        d.putExtra(BLOCK_ID, intent.getStringExtra(BLOCK_ID))
                        d.putExtra(VISITOR_PURPOSE, Ed_Name_purp.text.toString())
                        startActivity(d)
                        finish()
                    }else{

                        val d = Intent(
                            this@PurposeScreen, ManualMobileNumberScreen::class.java)
                        d.putExtra(ConstantUtils.UNITID, intent.getStringExtra(ConstantUtils.UNITID))
                        d.putExtra(ConstantUtils.UNITNAME, intent.getStringExtra(ConstantUtils.UNITNAME))
                        d.putExtra(FLOW_TYPE, intent.getStringExtra(FLOW_TYPE))
                        d.putExtra(VISITOR_TYPE, intent.getStringExtra(VISITOR_TYPE))
                        d.putExtra(COMPANY_NAME, intent.getStringExtra(COMPANY_NAME))
                        d.putExtra(ConstantUtils.UNIT_ACCOUNT_ID, intent.getStringExtra(UNIT_ACCOUNT_ID))
                        d.putExtra(BLOCK_ID, intent.getStringExtra(BLOCK_ID))
                        d.putExtra("FIRSTNAME", intent.getStringExtra("FIRSTNAME"))
                        d.putExtra("LASTNAME", intent.getStringExtra("LASTNAME"))
                        d.putExtra(MOBILENUMBER, intent.getStringExtra(MOBILENUMBER))
                        d.putExtra("DESIGNATION", intent.getStringExtra("DESIGNATION"))
                        d.putExtra("WORKTYPE", intent.getStringExtra("WORKTYPE"))
                        d.putExtra(WORKER_ID, intent.getIntExtra(WORKER_ID, 0))
                        d.putExtra("BIRTHDAY", intent.getStringExtra("BIRTHDAY"))
                        d.putExtra(VISITOR_PURPOSE, Ed_Name_purp.text.toString())



                        startActivity(d)
                        finish()
                    }
                } else {
                    buttonNext.isEnabled = true
                    buttonNext.isClickable = true
                    Toast.makeText(this, "Enter Valid Purpose", Toast.LENGTH_SHORT).show()

                }
            }

        }
    }

    val entries: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocale(Prefs.getString(PrefKeys.LANGUAGE, null))
        setContentView(R.layout.activity_purpose)
        txt_assn_name=findViewById(R.id.txt_assn_name)
        txt_gate_name=findViewById(R.id.txt_gate_name)
        txt_device_name=findViewById(R.id.txt_device_name)
        txt_assn_name.text = "Society: " + LocalDb.getAssociation()!!.asAsnName
        txt_gate_name.text = "Gate No: " + Prefs.getString(ConstantUtils.GATE_NO, "")
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

        Btn_Mic.setOnClickListener {
            Speak()
        }
//        supportActionBar!!.setTitle("Enter your Name")
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        Log.d(
            "intentdata NameEntr",
            "" + intent.getStringExtra(ConstantUtils.UNITNAME) + " " + intent.getStringExtra(
                ConstantUtils.UNITID
            )
                    + " " + intent.getStringExtra(ConstantUtils.MOBILENUMBER) + " " + intent.getStringExtra(
                ConstantUtils.COUNTRYCODE
            )
        )

    }
//
//    override fun onBackPressed() {
//        super.onBackPressed()
//
//        val d = Intent(this@PurposeScreen, UnitListActivity::class.java)
//        d.putExtra(ConstantUtils.FLOW_TYPE, intent.getStringExtra(ConstantUtils.FLOW_TYPE))
//        d.putExtra(ConstantUtils.VISITOR_TYPE, intent.getStringExtra(ConstantUtils.VISITOR_TYPE))
//        d.putExtra(ConstantUtils.COMPANY_NAME, intent.getStringExtra(ConstantUtils.COMPANY_NAME))
//        d.putExtra(ConstantUtils.UNITID, intent.getStringExtra(ConstantUtils.UNITID))
//        d.putExtra(ConstantUtils.UNITNAME, intent.getStringExtra(ConstantUtils.UNITNAME))
//        d.putExtra(ConstantUtils.MOBILENUMBER, getIntent().getStringExtra(ConstantUtils.MOBILENUMBER))
//        d.putExtra(ConstantUtils.COUNTRYCODE, getIntent().getStringExtra(ConstantUtils.COUNTRYCODE))
//        startActivity(d)
//
//    }

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
//        val i_delivery = Intent(this@PurposeScreen, Dashboard::class.java)
//        startActivity(i_delivery)
        finish()
    }
}