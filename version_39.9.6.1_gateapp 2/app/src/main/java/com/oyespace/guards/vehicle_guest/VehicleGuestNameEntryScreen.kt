package com.oyespace.guards.vehicle_guest

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.oyespace.guards.Dashboard
import com.oyespace.guards.R
import com.oyespace.guards.activity.BaseKotlinActivity
import com.oyespace.guards.constants.PrefKeys.LANGUAGE
import com.oyespace.guards.utils.ConstantUtils

import com.oyespace.guards.utils.ConstantUtils.*
import com.oyespace.guards.utils.LocalDb
import com.oyespace.guards.utils.Prefs
import kotlinx.android.synthetic.main.activity_name_entry.*
import java.util.*

class VehicleGuestNameEntryScreen : BaseKotlinActivity() , View.OnClickListener {
    private val REQUEST_CODE_SPEECH_INPUT = 100
    internal var language: String? = ""
    lateinit var txt_assn_name:TextView
    lateinit var txt_gate_name:TextView
    lateinit var txt_device_name: TextView
    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.buttonNext ->{
                buttonNext.setEnabled(false)
                buttonNext.setClickable(false)
                if(Ed_Name.text.length>2) {
//                    val d = Intent(this@NameEntryScreen, CameraActivity::class.java)
                    val d = Intent(this@VehicleGuestNameEntryScreen, VehicleGuestAddCarFragment::class.java)

//                    Log.d("intentdata NameEntr","buttonNext "+getIntent().getStringExtra(UNITNAME)+" "+intent.getStringExtra(UNITID)
//                            +" "+getIntent().getStringExtra(MOBILENUMBER)+" "+getIntent().getStringExtra(COUNTRYCODE)+" "+Ed_Name.text);
                    d.putExtra(UNITID,intent.getStringExtra(UNITID) )
                    d.putExtra(UNITNAME, intent.getStringExtra(UNITNAME))
                    d.putExtra(FLOW_TYPE,intent.getStringExtra(FLOW_TYPE))
                    d.putExtra(VISITOR_TYPE,intent.getStringExtra(VISITOR_TYPE))
                    d.putExtra(COMPANY_NAME,intent.getStringExtra(COMPANY_NAME))
                    d.putExtra(MOBILENUMBER, intent.getStringExtra(MOBILENUMBER))
                    d.putExtra(COUNTRYCODE, intent.getStringExtra(COUNTRYCODE))
                    d.putExtra(PERSONNAME, Ed_Name.getText().toString())
                    d.putExtra(UNIT_ACCOUNT_ID,intent.getStringExtra(ConstantUtils.UNIT_ACCOUNT_ID))
                    d.putExtra(BLOCK_ID,intent.getStringExtra(BLOCK_ID))
                    startActivity(d);
                    finish();
                }else{
                    buttonNext.setEnabled(true)
                    buttonNext.setClickable(true)
                    Toast.makeText(this,"Enter Valid Name", Toast.LENGTH_SHORT).show()

                }
            }

        }
    }

    val entries: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocale(Prefs.getString(LANGUAGE, null))
        setContentView(R.layout.activity_name_entry)
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

        Btn_Mic.setOnClickListener{
            Speak();
        }
//        supportActionBar!!.setTitle("Enter your Name")
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

//        Log.d("intentdata NameEntr",""+getIntent().getStringExtra(UNITNAME)+" "+intent.getStringExtra(UNITID)
//        +" "+getIntent().getStringExtra(MOBILENUMBER)+" "+getIntent().getStringExtra(COUNTRYCODE));

    }

//    override fun onBackPressed() {
//        super.onBackPressed()
//
//        val d = Intent(this@VehicleGuestNameEntryScreen, VehicleGuestMobileNumberScreen::class.java)
//        d.putExtra(FLOW_TYPE,intent.getStringExtra(FLOW_TYPE))
//        d.putExtra(VISITOR_TYPE,intent.getStringExtra(VISITOR_TYPE))
//        d.putExtra(COMPANY_NAME,intent.getStringExtra(COMPANY_NAME))
//        d.putExtra(UNITID, intent.getStringExtra(UNITID))
//        d.putExtra(UNITNAME, intent.getStringExtra(UNITNAME))
//        d.putExtra(MOBILENUMBER, getIntent().getStringExtra(MOBILENUMBER))
//        d.putExtra(COUNTRYCODE, getIntent().getStringExtra(COUNTRYCODE))
//        startActivity(d)
//
//    }

    fun Speak() {

        language = Prefs.getString(LANGUAGE, null)
        if (language=="en") {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, Locale.getDefault())
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "say something")

            try {
                startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
            } catch (e: Exception) {
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
        } else if(language=="hi") {

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
//        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, Locale.getDefault())
//       // intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN");
//        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"say something")
//
//        try {
//            startActivityForResult(intent,REQUEST_CODE_SPEECH_INPUT)
//        }catch (e: Exception){
//            Toast.makeText(this,e.message, Toast.LENGTH_SHORT).show()
//        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            REQUEST_CODE_SPEECH_INPUT ->{
                if (resultCode == Activity.RESULT_OK  && null!= data){
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    Ed_Name.setText(result[0]+"")

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
//        val intent= Intent(this@VehicleGuestNameEntryScreen, Dashboard::class.java)
//        startActivity(intent)
        finish()
    }
}