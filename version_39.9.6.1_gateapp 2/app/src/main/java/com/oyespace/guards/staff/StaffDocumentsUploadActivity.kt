package com.oyespace.guards.staff

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.oyespace.guards.R
import com.oyespace.guards.utils.ConstantUtils
import com.oyespace.guards.utils.ConstantUtils.PERSONNAME
import com.oyespace.guards.utils.LocalDb
import com.oyespace.guards.utils.Prefs
import com.oyespace.guards.utils.RandomUtils
import java.util.*


class StaffDocumentsUploadActivity : AppCompatActivity() {
    var maxLengthofEditText:Int?=0
    var staffStatus:String?=null
    var encodedImage: String? = null
    var buttonNext:Button?=null
    lateinit var txt_assn_name: TextView
    lateinit var txt_gate_name: TextView
    lateinit var txt_device_name: TextView
    var month: Int = 0
    var day: Int = 0
    var s_date:String?=null
    private var calendar: Calendar? = null
    var datepickerdialog:DatePickerDialog?=null
    internal val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034
     var radioDocumentsGroup: RadioGroup? = null
     var rbDocument: RadioButton? = null
    var rbExpiry: RadioButton? = null
    var rbdl:RadioButton? = null
    var rg_expiry:RadioGroup?=null
     var ed_idproof:EditText?=null
    var iv_plus: ImageView?=null
    var ed_expirydate:EditText?=null
    internal var personPhoto: Bitmap? = null
    var iv_document:ImageView?=null
    var bt_active:Button?=null
    var bt_temporary:Button?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_documents_upload)

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
        buttonNext=findViewById(R.id.buttonNext)
        bt_temporary=findViewById(R.id.bt_temporary)
        bt_active=findViewById(R.id.bt_active)
        ed_expirydate=findViewById(R.id.ed_expirydate)
        rg_expiry=findViewById(R.id.rg_expiry)
        radioDocumentsGroup = findViewById(R.id.radiogroup);
        ed_idproof= findViewById(R.id.ed_idproof)
        iv_plus=findViewById(R.id.iv_plus)
        iv_document=findViewById(R.id.iv_document)
        rbdl=findViewById(R.id.rbdl)



        buttonNext!!.setOnClickListener(View.OnClickListener {

            //try {
                if ((radioDocumentsGroup!!.getCheckedRadioButtonId() == -1) )
                {
                  Toast.makeText(this@StaffDocumentsUploadActivity,"Select Document Type",Toast.LENGTH_LONG).show()
                }
                 else if(ed_idproof!!.text.toString().trim()==""){
                    Toast.makeText(this@StaffDocumentsUploadActivity,"Enter ID Proof Number",Toast.LENGTH_LONG).show()
                }else if(iv_document!!.getDrawable() == null){
                    Toast.makeText(this@StaffDocumentsUploadActivity,"Capture Document photo",Toast.LENGTH_LONG).show()
                }
                 else if(rg_expiry!!.getCheckedRadioButtonId() == -1){
                    Toast.makeText(this@StaffDocumentsUploadActivity,"Select Staff Expiry",Toast.LENGTH_LONG).show()
                }
                 else if(rbExpiry!!.text.equals("Yes")&&ed_expirydate!!.text.toString()== ""){
                    Toast.makeText(this@StaffDocumentsUploadActivity,"Select Expiry Date",Toast.LENGTH_LONG).show()
                }
                else if(staffStatus== null){
                    Toast.makeText(this@StaffDocumentsUploadActivity,"Select Staff Status",Toast.LENGTH_LONG).show()
                }
                else
                {
                    // one of the radio buttons is checked
                    val drawable = iv_document!!.getDrawable() as BitmapDrawable
                    val bitmap = drawable.bitmap
                    encodedImage = RandomUtils.encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 100)

                    val d = Intent(this@StaffDocumentsUploadActivity, StaffAddCarFragment::class.java)
                    d.putExtra(ConstantUtils.UNITID, intent.getStringExtra(ConstantUtils.UNITID))
                    d.putExtra(ConstantUtils.UNITNAME, intent.getStringExtra(ConstantUtils.UNITNAME))
                    d.putExtra(ConstantUtils.FLOW_TYPE, intent.getStringExtra(ConstantUtils.FLOW_TYPE))
                    d.putExtra(ConstantUtils.VISITOR_TYPE, intent.getStringExtra(ConstantUtils.VISITOR_TYPE))
                    d.putExtra(ConstantUtils.COMPANY_NAME, intent.getStringExtra(ConstantUtils.COMPANY_NAME))
                    d.putExtra(ConstantUtils.MOBILENUMBER, intent.getStringExtra(ConstantUtils.MOBILENUMBER))
                    d.putExtra(ConstantUtils.COUNTRYCODE, intent.getStringExtra(ConstantUtils.COUNTRYCODE))
                    d.putExtra(ConstantUtils.PERSONNAME, intent.getStringExtra(PERSONNAME))
                    d.putExtra("DOB", intent.getStringExtra("DOB"))
                    d.putExtra(ConstantUtils.UNIT_ACCOUNT_ID, intent.getStringExtra(ConstantUtils.UNIT_ACCOUNT_ID))
                    d.putExtra(ConstantUtils.BLOCK_ID, intent.getStringExtra(ConstantUtils.BLOCK_ID))
                    d.putExtra(ConstantUtils.VISITOR_PURPOSE, intent.getStringExtra(ConstantUtils.VISITOR_PURPOSE))
                    d.putExtra(ConstantUtils.UNITOCCUPANCYSTATUS, intent.getStringExtra(ConstantUtils.UNITOCCUPANCYSTATUS))
                    d.putExtra("DocumentType", rbDocument!!.text)
                    d.putExtra("DocumentImage", encodedImage)
                    d.putExtra("DocumentExpiry", rbExpiry!!.text)
                    d.putExtra("Status", staffStatus)
                    d.putExtra("DocumentExpiryDate", s_date)
                    d.putExtras(intent)
                    startActivity(d)
                    finish()
                }
//            }catch (e:KotlinNullPointerException){
//
//            }

        })

        iv_plus!!.setOnClickListener(View.OnClickListener {
            iv_document!!.visibility=View.VISIBLE
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
        })

        bt_active!!.setOnClickListener(View.OnClickListener {
            staffStatus= "Active"
            bt_active!!.setBackgroundColor(ContextCompat.getColor(this, R.color.orange));
            bt_temporary!!.setBackgroundColor(ContextCompat.getColor(this, R.color.grey));

        })

        bt_temporary!!.setOnClickListener(View.OnClickListener {
            staffStatus= "Temporary"
            bt_temporary!!.setBackgroundColor(ContextCompat.getColor(this, R.color.orange));
            bt_active!!.setBackgroundColor(ContextCompat.getColor(this, R.color.grey))

        })


        ed_idproof!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

               iv_plus!!.visibility=View.VISIBLE
                if (rbDocument!!.text.equals("Adhar")) {
                    maxLengthofEditText = 12
                    ed_idproof!!.setFilters(arrayOf<InputFilter>(LengthFilter(maxLengthofEditText!!)))
                    if(ed_idproof!!.getText().toString().length >12){
                        Toast.makeText(this@StaffDocumentsUploadActivity, "Enter valid details", Toast.LENGTH_SHORT).show()
                    }
                } else if (rbDocument!!.text.equals("PAN")) {
                    maxLengthofEditText = 10
                    ed_idproof!!.setFilters(arrayOf<InputFilter>(LengthFilter(maxLengthofEditText!!)))
                    if(ed_idproof!!.getText().toString().length >10){
                        Toast.makeText(this@StaffDocumentsUploadActivity, "Enter valid details", Toast.LENGTH_SHORT).show()
                    }
                }
                else if(rbDocument!!.text.equals("Voter ID")){
                    maxLengthofEditText = 10
                    ed_idproof!!.setFilters(arrayOf<InputFilter>(LengthFilter(maxLengthofEditText!!)))
                    if(ed_idproof!!.getText().toString().length >10){
                        Toast.makeText(this@StaffDocumentsUploadActivity, "Enter valid details", Toast.LENGTH_SHORT).show()

                    }
                }
                else if(rbDocument!!.text.equals("Driving Licence")){
                    maxLengthofEditText = 15
                    ed_idproof!!.setFilters(arrayOf<InputFilter>(LengthFilter(maxLengthofEditText!!)))
                    if(ed_idproof!!.getText().toString().length >15){
                        Toast.makeText(this@StaffDocumentsUploadActivity, "Enter valid details", Toast.LENGTH_SHORT).show()

                    }
                }
            }
        })

        radioDocumentsGroup!!.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            rbDocument =
                group.findViewById<View>(checkedId) as RadioButton
            if (null != rbDocument && checkedId > -1) {
            }
        })

        ed_expirydate!!.setOnClickListener {

            val cal = Calendar.getInstance()
            val y = cal.get(Calendar.YEAR)
            val m = cal.get(Calendar.MONTH)
            val d = cal.get(Calendar.DAY_OF_MONTH)


            datepickerdialog = DatePickerDialog(this@StaffDocumentsUploadActivity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                // Display Selected date in textbox

                var userAge = GregorianCalendar(year, month, day)
                var minAdultAge = GregorianCalendar()
                minAdultAge.add(Calendar.YEAR, -18)
//                if (minAdultAge.before(userAge)) {
//                    Toast.makeText(this, "Age is below 18.", Toast.LENGTH_LONG).show()
//                }
//                else{
                    ed_expirydate!!.setText("" + dayOfMonth + "-" + (monthOfYear+1) + "-" + year)
                    s_date=(""+year+"-"+(monthOfYear+1)+"-"+dayOfMonth)
               // }
            }, y, m, d)
            try {
                //datepickerdialog!!.datePicker.maxDate = calendar!!.timeInMillis
                datepickerdialog!!.datePicker.minDate=System. currentTimeMillis() - 1000

            }
            catch (e:KotlinNullPointerException){

            }

            datepickerdialog!!.show()
        }


        rg_expiry!!.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            rbExpiry = group.findViewById<View>(checkedId) as RadioButton
            if (null != rbExpiry && checkedId > -1) {

                if(rbExpiry!!.text.equals("Yes")){
                    ed_expirydate!!.visibility=View.VISIBLE
                }
                else{
                    ed_expirydate!!.visibility=View.GONE
                }
            }
        })
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {

            CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE -> if (resultCode == Activity.RESULT_OK) {
                val photo = data!!.extras!!.get("data") as Bitmap
                iv_document!!.setImageBitmap(photo)
                personPhoto = photo

            }
        }

    }
}
