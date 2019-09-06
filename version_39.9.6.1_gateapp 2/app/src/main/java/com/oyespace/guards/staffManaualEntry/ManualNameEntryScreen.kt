package com.oyespace.guards.staffManaualEntry

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.oyespace.guards.Dashboard
import com.oyespace.guards.R
import com.oyespace.guards.activity.BaseKotlinActivity
import com.oyespace.guards.camtest.AddCarFragment
import com.oyespace.guards.constants.PrefKeys.LANGUAGE
import com.oyespace.guards.staffManaualEntry.ManualAddCarFragment
import com.oyespace.guards.utils.ConstantUtils
import com.oyespace.guards.utils.ConstantUtils.*
import com.oyespace.guards.utils.LocalDb
import com.oyespace.guards.utils.Prefs
import kotlinx.android.synthetic.main.activity_mobile_number.*
import kotlinx.android.synthetic.main.activity_name_entry.*
import kotlinx.android.synthetic.main.activity_name_entry.buttonNext
import kotlinx.android.synthetic.main.activity_unit_list.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ManualNameEntryScreen : BaseKotlinActivity() , View.OnClickListener {
    var s_dob:String?=null
    var date:String?=null
    private var cal: Calendar? = null
    var oneWayTripDate:Date?=null
    var y:Int?=0
    var m:Int?=0
    var out= arrayOf(String())
    var d:Int?=0
    var inputt:SimpleDateFormat?=null
    var output:SimpleDateFormat?=null
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
                buttonNext.setEnabled(false)
                buttonNext.setClickable(false)
                if(Ed_Name.text.length<2) {
                    buttonNext.setEnabled(true)
                    buttonNext.setClickable(true)
                    Toast.makeText(this,"Enter Valid Name", Toast.LENGTH_SHORT).show()


                }
                else if((intent.getStringExtra(FLOW_TYPE).equals(STAFF_REGISTRATION))) {


                    if (ed_dob!!.length() == 0) {
                        buttonNext.setEnabled(true)
                        buttonNext.setClickable(true)
                        Toast.makeText(this, "Select DOB", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        val d = Intent(this@ManualNameEntryScreen, ManualAddCarFragment::class.java)
                        d.putExtra(UNITID,intent.getStringExtra(UNITID) )
                        d.putExtra(UNITNAME, intent.getStringExtra(UNITNAME))
                        d.putExtra(FLOW_TYPE,intent.getStringExtra(FLOW_TYPE))
                        d.putExtra(VISITOR_TYPE,intent.getStringExtra(VISITOR_TYPE))
                        d.putExtra(COMPANY_NAME,intent.getStringExtra(COMPANY_NAME))
                        d.putExtra(MOBILENUMBER, intent.getStringExtra(MOBILENUMBER))
                        d.putExtra(COUNTRYCODE, intent.getStringExtra(COUNTRYCODE))
                        d.putExtra(PERSONNAME, Ed_Name.getText().toString())
                        d.putExtra("DOB",s_dob.toString())
                        d.putExtra(ConstantUtils.UNIT_ACCOUNT_ID,intent.getStringExtra(ConstantUtils.UNIT_ACCOUNT_ID))
                        d.putExtra(BLOCK_ID,intent.getStringExtra(BLOCK_ID))
                     //   d.putExtra("FIRSTNAME", intent.getStringExtra("FIRSTNAME"))
                      //  d.putExtra("LASTNAME", intent.getStringExtra("LASTNAME"))
                        d.putExtra("DESIGNATION", intent.getStringExtra("DESIGNATION"))
                        d.putExtra("WORKTYPE", intent.getStringExtra("WORKTYPE"))
                        d.putExtra(WORKER_ID,  intent.getIntExtra(WORKER_ID,0))
                        startActivity(d);
                        finish();
                    }
                }
                else{
                    val d = Intent(this@ManualNameEntryScreen, ManualAddCarFragment::class.java)

                    d.putExtra(UNITID,intent.getStringExtra(UNITID) )
                    d.putExtra(UNITNAME, intent.getStringExtra(UNITNAME))
                    d.putExtra(FLOW_TYPE,intent.getStringExtra(FLOW_TYPE))
                    d.putExtra(VISITOR_TYPE,intent.getStringExtra(VISITOR_TYPE))
                    d.putExtra(COMPANY_NAME,intent.getStringExtra(COMPANY_NAME))
                    d.putExtra(MOBILENUMBER, intent.getStringExtra(MOBILENUMBER))
                    d.putExtra(COUNTRYCODE, intent.getStringExtra(COUNTRYCODE))
                    d.putExtra(PERSONNAME, Ed_Name.getText().toString())
                    d.putExtra("DOB",s_dob.toString())
                    d.putExtra(ConstantUtils.UNIT_ACCOUNT_ID,intent.getStringExtra(ConstantUtils.UNIT_ACCOUNT_ID))
                    d.putExtra(BLOCK_ID,intent.getStringExtra(BLOCK_ID))
                    //   d.putExtra("FIRSTNAME", intent.getStringExtra("FIRSTNAME"))
                    //  d.putExtra("LASTNAME", intent.getStringExtra("LASTNAME"))
                    d.putExtra("DESIGNATION", intent.getStringExtra("DESIGNATION"))
                    d.putExtra("WORKTYPE", intent.getStringExtra("WORKTYPE"))
                    d.putExtra(WORKER_ID,  intent.getIntExtra(WORKER_ID,0))
                    startActivity(d);
                    finish();

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

       if(getIntent().getStringExtra("FIRSTNAME")!=null) {
            Ed_Name.setText(getIntent().getStringExtra("FIRSTNAME"))
        }
        if (intent.getStringExtra(FLOW_TYPE).equals(STAFF_REGISTRATION)) {
            lyt_dob!!.visibility=View.VISIBLE
        }
            else{
            lyt_dob!!.visibility=View.GONE
            }


        val sDOB=intent.getStringExtra("BIRTHDAY")
        sDOB.substring(0,10);

        cal = Calendar.getInstance()
        y = cal!!.get(Calendar.YEAR)
        m = cal!!.get(Calendar.MONTH)
        d = cal!!.get(Calendar.DAY_OF_MONTH)


        date =sDOB.substring(0,10);
        s_dob=sDOB.substring(0,10);

        inputt =  SimpleDateFormat("yyyy-MM-dd");
        output =  SimpleDateFormat("dd-MM-yyyy");
        try {
            oneWayTripDate = inputt!!.parse(date);                 // parse input
            //0001-01-01

            if(output!!.format(oneWayTripDate).equals("01-01-0001")){
                ed_dob?.setHint(resources.getString(R.string.textselectdob));    // format output
            }else
            {
                ed_dob?.setText(output!!.format(oneWayTripDate));    // format output

                out = output!!.format(oneWayTripDate).split(",").toTypedArray();

                try {
                    System.out.println("Year = " + out[2]);
                    System.out.println("Month = " + out[0]);
                    System.out.println("Day = " + out[1]);
                    y = out[2].toInt()
                    m = out[0].toInt()
                    d = out[1].toInt()
                }catch (e:IndexOutOfBoundsException){

                }


            }

        } catch (e: ParseException) {
            e.printStackTrace();
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


             datepickerdialog = DatePickerDialog(this@ManualNameEntryScreen, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                // Display Selected date in textbox

                var userAge =  GregorianCalendar(year,month,day);
                var minAdultAge =  GregorianCalendar();
                minAdultAge.add(Calendar.YEAR, -18);
                if (minAdultAge.before(userAge)) {
                    Toast.makeText(this,"Age is below 18.",Toast.LENGTH_LONG).show();
                }
                else{
                    ed_dob!!.setText("" + dayOfMonth + "-" + (monthOfYear+1) + "-" + year)
                    s_dob=(""+year+"-"+(monthOfYear+1)+"-"+dayOfMonth)
                }
            }, y, m, d)
            try {
            datepickerdialog!!.getDatePicker().setMaxDate(calendar!!.getTimeInMillis());

            }
                    catch (e:KotlinNullPointerException){

            }

            datepickerdialog!!.show()
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
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN");
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