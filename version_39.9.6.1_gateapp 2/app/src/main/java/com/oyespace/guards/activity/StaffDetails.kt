package com.oyespace.guards.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.oyespace.guards.R
import com.oyespace.guards.constants.PrefKeys
import com.oyespace.guards.staff.StaffCaptureImageOcr
import com.oyespace.guards.utils.ConstantUtils
import com.oyespace.guards.utils.ConstantUtils.*
import com.oyespace.guards.utils.LocalDb
import com.oyespace.guards.utils.Prefs
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_final_registration.*
import kotlinx.android.synthetic.main.header_with_next.*
import java.util.*

class StaffDetails : BaseKotlinActivity() , View.OnClickListener  {

    var vehicleNumber:String?=null
        lateinit var mBitmap: Bitmap
    private val REQUEST_CODE_SPEECH_INPUT = 100
        override fun onClick(v: View?) {

            when (v?.id) {

                R.id.buttonNext ->{
                    buttonNext.isEnabled = false
                    buttonNext.isClickable = false
                    Log.d("button_done ","StaffEntry "+ ConstantUtils.FLOW_TYPE +" "+ ConstantUtils.STAFF_REGISTRATION +" "+ ConstantUtils.FLOW_TYPE.equals(ConstantUtils.STAFF_REGISTRATION,true))
                    //finish();
//                    val i_staff = Intent(this@StaffDetails, StaffListActivity::class.java)
//                    startActivity(i_staff)
                    finish()
                }

                R.id.profile_image ->{
                    val alertadd = AlertDialog.Builder(this@StaffDetails)
                    val factory = LayoutInflater.from(this@StaffDetails)
                    val view = factory.inflate(R.layout.dialog_big_image, null)
                    var dialog_imageview: ImageView? = null
                    dialog_imageview = view.findViewById(R.id.dialog_imageview)
                      dialog_imageview.background = profile_image.getDrawable()

                    alertadd.setView(view)
                    alertadd.show()

                }

            }
        }

        var minteger = 1
        val entries: ArrayList<String> = ArrayList()

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setLocale(Prefs.getString(PrefKeys.LANGUAGE, null))
            setContentView(R.layout.activity_final_registration)

            buttonNext.text=resources.getString(R.string.textdone)

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
            if(intent.getStringExtra(VEHICLE_NUMBER)!=null) {
                tv_vehiclenumber?.text = (intent.getStringExtra(VEHICLE_NUMBER))
            }
            if(!tv_vehiclenumber.text.equals("")){
                vehicleNumber=tv_vehiclenumber.text.toString()
            }
            else{
                vehicleNumber=""
            }
            iv_mike.setOnClickListener{
                Speak()
            }
            iv_scanner.setOnClickListener{
                val i_vehicle = Intent(this@StaffDetails, StaffCaptureImageOcr::class.java)
                intent.putExtra(WORKER_ID, intent.getStringExtra(WORKER_ID));
                intent.putExtra(PERSONNAME, intent.getStringExtra(PERSONNAME));
                intent.putExtra(UNITID, intent.getStringExtra(UNITID));
                intent.putExtra(UNITNAME, intent.getStringExtra(UNITNAME));
                intent.putExtra(FLOW_TYPE, intent.getStringExtra(FLOW_TYPE));
                intent.putExtra(VISITOR_TYPE, intent.getStringExtra(VISITOR_TYPE));
                intent.putExtra(COMPANY_NAME, intent.getStringExtra(COMPANY_NAME));
                intent.putExtra(MOBILENUMBER, intent.getStringExtra(MOBILENUMBER));
                intent.putExtra(COUNTRYCODE, intent.getStringExtra(COUNTRYCODE));
                startActivity(i_vehicle)
                finish()
            }

            Log.d("intentdata StaffEntry",""+intent.getStringExtra(ConstantUtils.UNITNAME)+" "+intent.getStringExtra(ConstantUtils.UNITID)
                    +" "+intent.getStringExtra(ConstantUtils.MOBILENUMBER)+" "+intent.getStringExtra(ConstantUtils.COUNTRYCODE)+" "
                    +intent.getStringExtra(ConstantUtils.PERSONNAME)+" "
                    +" "+intent.getStringExtra(ConstantUtils.FLOW_TYPE)+" "
                    + intent.getStringExtra(ConstantUtils.VISITOR_TYPE) + " " + intent.getStringExtra(
                ConstantUtils.COMPANY_NAME
            )
            )
            txt_header.text=resources.getString(R.string.textidcard)+" "+ LocalDb.getAssociation()!!.asAsnName

            tv_name.text =
                resources.getString(R.string.textname) + ": " + intent.getStringExtra(ConstantUtils.PERSONNAME)
            tv_mobilenumber.text =
                resources.getString(R.string.textmobile) + ": " + intent.getStringExtra(
                    ConstantUtils.COUNTRYCODE
                ) + "" + intent.getStringExtra(ConstantUtils.MOBILENUMBER)
            tv_for.text =
                resources.getString(R.string.textto) + intent.getStringExtra(ConstantUtils.UNITNAME)
            tv_totalperson.text = resources.getString(R.string.textperson)
            tv_from.text =
                resources.getString(R.string.textfrom) + intent.getStringExtra(ConstantUtils.COMPANY_NAME)

            menuAdd.setOnClickListener {
                minteger++
                menuCount.text = "" + minteger

            }

            menuRemove.setOnClickListener {
                if (minteger>1) {
                    minteger--
                    menuCount.text = "" + minteger

                }else{

                }
            }
            if (intent.getStringExtra(ConstantUtils.FLOW_TYPE) == ConstantUtils.STAFF_REGISTRATION) {
                tv_from.text = "Designation: " + intent.getStringExtra(ConstantUtils.COMPANY_NAME)
                itemLyt.visibility = View.GONE
            } else {

            }

            val imageAsBytes = android.util.Base64.decode(intent.getStringExtra("Base64"),android.util.Base64.DEFAULT);
            val decodedImage = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.size);
            profile_image.setImageBitmap(decodedImage)
//            Picasso.with(this)
//                    //.load(IMAGE_BASE_URL + "Images/PERSON" + intent.getIntExtra(ACCOUNT_ID, 0) + ".jpg")
//                .load(IMAGE_BASE_URL +"Images/PERSONAssociation"+Prefs.getInt(ConstantUtils.ASSOCIATION_ID,0)+"STAFF"+ intent.getIntExtra(WORKER_ID, 0)+".jpg")
//                .placeholder(R.drawable.placeholder_dark).error(R.drawable.placeholder_dark).into(profile_image)

            val mLayoutManager = GridLayoutManager(applicationContext, 2)
            rv_image.layoutManager = mLayoutManager

        }

//        override fun onBackPressed() {
//            super.onBackPressed()
//            val d = Intent(this@StaffDetails, Biometric::class.java)
//            d.putExtra(WORKER_ID, intent.getIntExtra(WORKER_ID, 0))
//            d.putExtra(PERSONNAME, intent.getStringExtra(PERSONNAME))
//            d.putExtra(UNITID, intent.getStringExtra(UNITID))
//            d.putExtra(UNITNAME, intent.getStringExtra(UNITNAME))
//            d.putExtra(FLOW_TYPE, intent.getStringExtra(FLOW_TYPE))
//            d.putExtra(VISITOR_TYPE, intent.getStringExtra(VISITOR_TYPE))
//            d.putExtra(COMPANY_NAME, intent.getStringExtra(COMPANY_NAME))
//            d.putExtra(MOBILENUMBER, intent.getStringExtra(MOBILENUMBER))
//            d.putExtra(COUNTRYCODE, intent.getStringExtra(COUNTRYCODE))
//            startActivity(d);
//            finish();
//        }

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
//        val i_delivery = Intent(this@StaffDetails, Dashboard::class.java)
//        startActivity(i_delivery)
        finish()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            REQUEST_CODE_SPEECH_INPUT ->{
                if (resultCode == Activity.RESULT_OK  && null!= data){
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    tv_vehiclenumber.setText(result[0].replace(" ", "").trim())

                }
            }
        }
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
    }