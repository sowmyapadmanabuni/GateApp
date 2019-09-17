package com.oyespace.guards.activity

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import android.util.Log
import android.view.View
import com.oyespace.guards.Dashboard
import com.oyespace.guards.constants.PrefKeys
import com.oyespace.guards.utils.*
import kotlinx.android.synthetic.main.activity_final_registration.*
import java.util.*
import com.oyespace.guards.R
import com.oyespace.guards.utils.ConstantUtils.*
import com.oyespace.guards.utils.LocalDb
import com.oyespace.guards.utils.Prefs
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_final_registration.txt_assn_name
import kotlinx.android.synthetic.main.activity_final_registration.txt_device_name
import kotlinx.android.synthetic.main.activity_final_registration.txt_gate_name
import kotlinx.android.synthetic.main.activity_unit_list.*

class StaffDetails : BaseKotlinActivity() , View.OnClickListener  {

        lateinit var mBitmap: Bitmap

        override fun onClick(v: View?) {

            when (v?.id) {

                R.id.button_done ->{
                    button_done.setEnabled(false)
                    button_done.setClickable(false)
                    Log.d("button_done ","StaffEntry "+ ConstantUtils.FLOW_TYPE +" "+ ConstantUtils.STAFF_REGISTRATION +" "+ ConstantUtils.FLOW_TYPE.equals(ConstantUtils.STAFF_REGISTRATION,true))
                    //finish();
                    val i_staff = Intent(this@StaffDetails, StaffListActivity::class.java)
                    startActivity(i_staff)
                    finish()
                }

                R.id.profile_image ->{
                    Log.d("button_done ","StaffEntry "+ ConstantUtils.FLOW_TYPE +" "+ ConstantUtils.STAFF_REGISTRATION +" "+ ConstantUtils.FLOW_TYPE.equals(ConstantUtils.STAFF_REGISTRATION,true))
                        val d = Intent(this@StaffDetails, ImgView::class.java)
                        d.putExtra("URL_IMAGE", IMAGE_BASE_URL +"Images/PERSONAssociation"+Prefs.getInt(ConstantUtils.ASSOCIATION_ID,0)+"STAFF"+ intent.getIntExtra(WORKER_ID, 0)+".jpg")
                        startActivity(d)

                }

            }
        }

        var minteger = 1
        val entries: ArrayList<String> = ArrayList()

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setLocale(Prefs.getString(PrefKeys.LANGUAGE, null))
            setContentView(R.layout.activity_final_registration)

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


            Log.d("intentdata StaffEntry",""+intent.getStringExtra(ConstantUtils.UNITNAME)+" "+intent.getStringExtra(ConstantUtils.UNITID)
                    +" "+intent.getStringExtra(ConstantUtils.MOBILENUMBER)+" "+intent.getStringExtra(ConstantUtils.COUNTRYCODE)+" "
                    +intent.getStringExtra(ConstantUtils.PERSONNAME)+" "
                    +" "+intent.getStringExtra(ConstantUtils.FLOW_TYPE)+" "
                    +intent.getStringExtra(ConstantUtils.VISITOR_TYPE)+" "+intent.getStringExtra(ConstantUtils.COMPANY_NAME));
            txt_header.text=resources.getString(R.string.textidcard)+" "+ LocalDb.getAssociation()!!.asAsnName

            tv_name.setText(resources.getString(R.string.textname)+": "+intent.getStringExtra(ConstantUtils.PERSONNAME))
            tv_mobilenumber.setText(resources.getString(R.string.textmobile)+": "+intent.getStringExtra(ConstantUtils.COUNTRYCODE)+""+intent.getStringExtra(ConstantUtils.MOBILENUMBER))
            tv_for.setText(resources.getString(R.string.textto) +intent.getStringExtra(ConstantUtils.UNITNAME))
            tv_totalperson.setText(resources.getString(R.string.textperson))
            tv_from.setText(resources.getString(R.string.textfrom) +intent.getStringExtra(ConstantUtils.COMPANY_NAME))

            menuAdd.setOnClickListener {
                minteger++
                menuCount.setText(""+minteger)

            }

            menuRemove.setOnClickListener {
                if (minteger>1) {
                    minteger--
                    menuCount.setText("" + minteger)

                }else{

                }
            }
            if (intent.getStringExtra(ConstantUtils.FLOW_TYPE) == ConstantUtils.STAFF_REGISTRATION) {
                tv_from.setText("Designation: "+intent.getStringExtra(ConstantUtils.COMPANY_NAME))
                itemLyt.setVisibility(View.GONE)
            } else {

            }

            Picasso.with(this)
                    //.load(IMAGE_BASE_URL + "Images/PERSON" + intent.getIntExtra(ACCOUNT_ID, 0) + ".jpg")
                .load(IMAGE_BASE_URL +"Images/PERSONAssociation"+Prefs.getInt(ConstantUtils.ASSOCIATION_ID,0)+"STAFF"+ intent.getIntExtra(WORKER_ID, 0)+".jpg")
                .placeholder(R.drawable.placeholder_dark).error(R.drawable.placeholder_dark).into(profile_image)

            val mLayoutManager =
                androidx.recyclerview.widget.GridLayoutManager(applicationContext, 2)
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
    }