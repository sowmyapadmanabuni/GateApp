package com.oyespace.guards.staff

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.View
import android.widget.*
import com.oyespace.guards.R
import com.oyespace.guards.activity.BaseKotlinActivity
import com.oyespace.guards.camtest.AddCarFragment
import com.oyespace.guards.constants.PrefKeys.LANGUAGE
import com.oyespace.guards.utils.ConstantUtils
import com.oyespace.guards.utils.ConstantUtils.*
import com.oyespace.guards.utils.LocalDb
import com.oyespace.guards.utils.Prefs
import kotlinx.android.synthetic.main.activity_name_entry.*
import kotlinx.android.synthetic.main.header_with_next.*
import java.util.*

class StaffNameEntryScreen : BaseKotlinActivity() , View.OnClickListener {
    var iv_torch: Button?=null
    var clickable1 = 0
    var s_dob:String?=null
    private val REQUEST_CODE_SPEECH_INPUT = 100
    internal var language: String? = ""
    lateinit var txt_assn_name: TextView
    lateinit var txt_gate_name: TextView
    lateinit var txt_device_name: TextView
    var lyt_dob:LinearLayout?=null
    var datepickerdialog:DatePickerDialog?=null
    var ed_dob:EditText?=null
    private var calendar: Calendar? = null
    private var year: Int = 0
    var month: Int = 0
    var day: Int = 0
    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.buttonNext ->{
                buttonNext.isEnabled = false
                buttonNext.isClickable = false
                if(Ed_Name.text.length<2) {
                    buttonNext.isEnabled = true
                    buttonNext.isClickable = true
                    Toast.makeText(this,"Enter Valid Name", Toast.LENGTH_SHORT).show()


                }
                else if((intent.getStringExtra(FLOW_TYPE).equals(STAFF_REGISTRATION))) {


                    if (ed_dob!!.length() == 0) {
                        buttonNext.isEnabled = true
                        buttonNext.isClickable = true
                        Toast.makeText(this, "Select DOB", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        val d = Intent(this@StaffNameEntryScreen,   StaffDocumentsUploadActivity::class.java)
                        d.putExtra(UNITID,intent.getStringExtra(UNITID) )
                        d.putExtra(UNITNAME, intent.getStringExtra(UNITNAME))
                        d.putExtra(FLOW_TYPE,intent.getStringExtra(FLOW_TYPE))
                        d.putExtra(VISITOR_TYPE,intent.getStringExtra(VISITOR_TYPE))
                        d.putExtra(COMPANY_NAME,intent.getStringExtra(COMPANY_NAME))
                        d.putExtra(MOBILENUMBER, intent.getStringExtra(MOBILENUMBER))
                        d.putExtra(COUNTRYCODE, intent.getStringExtra(COUNTRYCODE))
                        d.putExtra(PERSONNAME, Ed_Name.text.toString())
                        d.putExtra("DOB",s_dob.toString())
                        d.putExtra(ConstantUtils.UNIT_ACCOUNT_ID,intent.getStringExtra(ConstantUtils.UNIT_ACCOUNT_ID))
                        d.putExtra(BLOCK_ID, intent.getStringExtra(BLOCK_ID))
                        d.putExtra(VISITOR_PURPOSE,intent.getStringExtra(VISITOR_PURPOSE))
                        d.putExtra(UNITOCCUPANCYSTATUS,intent.getStringExtra(UNITOCCUPANCYSTATUS))
                        d.putExtras(intent)
                        startActivity(d)
                        finish()
                    }
                }
                else{
                    val d = Intent(this@StaffNameEntryScreen, StaffAddCarFragment::class.java)

//                    Log.d("intentdata NameEntr","buttonNext "+getIntent().getStringExtra(UNITNAME)+" "+intent.getStringExtra(UNITID)
//                            +" "+getIntent().getStringExtra(MOBILENUMBER)+" "+getIntent().getStringExtra(COUNTRYCODE)+" "+Ed_Name.text);
                    d.putExtra(UNITID,intent.getStringExtra(UNITID) )
                    d.putExtra(UNITNAME, intent.getStringExtra(UNITNAME))
                    d.putExtra(FLOW_TYPE,intent.getStringExtra(FLOW_TYPE))
                    d.putExtra(VISITOR_TYPE,intent.getStringExtra(VISITOR_TYPE))
                    d.putExtra(COMPANY_NAME,intent.getStringExtra(COMPANY_NAME))
                    d.putExtra(MOBILENUMBER, intent.getStringExtra(MOBILENUMBER))
                    d.putExtra(COUNTRYCODE, intent.getStringExtra(COUNTRYCODE))
                    d.putExtra(PERSONNAME, Ed_Name.text.toString())
                   // d.putExtra("DOB",ed_dob!!.getText().toString())
                    d.putExtra("DOB",s_dob.toString())
                    d.putExtra(ConstantUtils.UNIT_ACCOUNT_ID,intent.getStringExtra(ConstantUtils.UNIT_ACCOUNT_ID))
                    d.putExtra(BLOCK_ID, intent.getStringExtra(BLOCK_ID))
                    d.putExtra(VISITOR_PURPOSE,intent.getStringExtra(VISITOR_PURPOSE))
                    d.putExtra(UNITOCCUPANCYSTATUS,intent.getStringExtra(UNITOCCUPANCYSTATUS))
                    d.putExtras(intent)
                    startActivity(d)
                    finish()

                }
            }

        }
    }

    val entries: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocale(Prefs.getString(LANGUAGE, null))
        setContentView(R.layout.activity_name_entry)
        ed_dob=findViewById(R.id.ed_dob)
        lyt_dob=findViewById(R.id.lyt_dob)

        if (intent.getStringExtra(PERSONNAME) != null) {
            Ed_Name.setText(intent.getStringExtra("FIRSTNAME"))
        }
        if (intent.getStringExtra(FLOW_TYPE).equals(STAFF_REGISTRATION)) {
            lyt_dob!!.visibility=View.VISIBLE
        }
            else{
            lyt_dob!!.visibility=View.GONE
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

        ed_dob!!.setOnClickListener {

            val cal = Calendar.getInstance()
            val y = cal.get(Calendar.YEAR)
            val m = cal.get(Calendar.MONTH)
            val d = cal.get(Calendar.DAY_OF_MONTH)


             datepickerdialog = DatePickerDialog(this@StaffNameEntryScreen, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                // Display Selected date in textbox

                 var userAge = GregorianCalendar(year, month, day)
                 var minAdultAge = GregorianCalendar()
                 minAdultAge.add(Calendar.YEAR, -18)
                 if (minAdultAge.before(userAge)) {
                     Toast.makeText(this, "Age is below 18.", Toast.LENGTH_LONG).show()
                }
                else{
                    ed_dob!!.setText("" + dayOfMonth + "-" + (monthOfYear+1) + "-" + year)
                    s_dob=(""+year+"-"+(monthOfYear+1)+"-"+dayOfMonth)
                }
            }, y, m, d)
            try {
                datepickerdialog!!.datePicker.maxDate = calendar!!.timeInMillis

            }
                    catch (e:KotlinNullPointerException){

            }

            datepickerdialog!!.show()
        }

        Btn_Mic.setOnClickListener{
            Speak()
        }
//        supportActionBar!!.setTitle("Enter your Name")
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

//        Log.d("intentdata NameEntr",""+getIntent().getStringExtra(UNITNAME)+" "+intent.getStringExtra(UNITID)
//        +" "+getIntent().getStringExtra(MOBILENUMBER)+" "+getIntent().getStringExtra(COUNTRYCODE));

    }

//    override fun onBackPressed() {
//        super.onBackPressed()
//
//        val d = Intent(this@NameEntryScreen, MobileNumberScreen::class.java)
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
        if (language=="en" || language=="hi") {
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
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN")
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
                    Ed_Name.setText(result[0].replace(" ", "").trim())

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

//    override fun onCreateDialog(id: Int): Dialog? {
//        // TODO Auto-generated method stub
//        return if (id == 999) {
//            DatePickerDialog(
//                this,
//                myDateListener, year, month, day
//            )
//        } else null
//    }

    override fun onBackPressed() {
        super.onBackPressed()
//        val i_delivery = Intent(this@NameEntryScreen, Dashboard::class.java)
//        startActivity(i_delivery)
        finish()
    }
//    fun setDate() {
//        showDialog(999)
////        Toast.makeText(
////            applicationContext, "ca",
////            Toast.LENGTH_SHORT
////        )
////            .show()
//    }
//    private fun showDate(year: Int, month: Int, day: Int) {
//        ed_dob!!.setText(
//            StringBuilder().append(day).append("-")
//                .append(month).append("-").append(year)
//        )
//    }
//    private val myDateListener = DatePickerDialog.OnDateSetListener { arg0, arg1, arg2, arg3 ->
//        // TODO Auto-generated method stub
//        // arg1 = year
//        // arg2 = month
//        // arg3 = day
//        showDate(arg1, arg2 + 1, arg3)
//    }

}