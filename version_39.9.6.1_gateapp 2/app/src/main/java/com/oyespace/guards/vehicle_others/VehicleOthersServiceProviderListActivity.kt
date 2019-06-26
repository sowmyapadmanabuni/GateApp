package com.oyespace.guards.vehicle_others

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.oyespace.guards.Dashboard
import com.oyespace.guards.R
import com.oyespace.guards.activity.BaseKotlinActivity
import com.oyespace.guards.constants.PrefKeys.LANGUAGE
import com.oyespace.guards.pojo.VendorPojo
import com.oyespace.guards.utils.ConstantUtils
import com.oyespace.guards.utils.LocalDb
import com.oyespace.guards.utils.Prefs
import kotlinx.android.synthetic.main.activity_service_provider_list.*
import kotlinx.android.synthetic.main.activity_unit_list.*

import java.util.*

class VehicleOthersServiceProviderListActivity : BaseKotlinActivity() {
    lateinit var txt_assn_name: TextView
    lateinit var txt_gate_name: TextView
    lateinit var txt_device_name: TextView
    val vendor_names = arrayOf(
        "OLA",
        "Uber",
        "Meru",
        "Zomato",
        "Swiggy",
        "FoodPanda",
        "BlueDart",
        "DTDC",
        "Fedex",
        "Jabong",
        "Flipkart",
        "Amazon",
        "BigBasket",
        "Grofers",
        "Godrej",
        "Carpenter",
        "Plumber",
        "electrician",
        "Others"

    )
    val myImageList = intArrayOf(
        R.drawable.ola,
        R.drawable.uber,
        R.drawable.meru,
        R.drawable.zomoto,
        R.mipmap.sw,
        R.mipmap.foodpanda,
        R.mipmap.bluedart,
        R.mipmap.dtdc,
        R.mipmap.fedex,
        R.mipmap.jabong,
        R.mipmap.flipkart,
        R.mipmap.amazon,
        R.mipmap.bb,
        R.mipmap.grofers,
        R.drawable.godrej,
        R.drawable.carpenter,
        R.drawable.plumber,
        R.drawable.electrician,
        R.drawable.others


    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocale(Prefs.getString(LANGUAGE, null))

        setContentView(R.layout.activity_service_provider_list)
        tv_selectdelivery.text=""
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

        val rv_serviceProvider = findViewById<RecyclerView>(R.id.rv_serviceProvider)
        rv_serviceProvider.setHasFixedSize(true)
        val gridLayoutManager = GridLayoutManager(this@VehicleOthersServiceProviderListActivity, 3)
        rv_serviceProvider.layoutManager = gridLayoutManager

        //  val sectionModelArrayList: ArrayList<SectionModel> = ArrayList()

        //for loop for sections
        //  for (i in 1..4) {


        val itemArrayList: ArrayList<String> = ArrayList()
        //for loop for items
        // for (j in 1..3) {
        //  itemArrayList.add("Item $j")
        // }

        itemArrayList.add("OLA")
        itemArrayList.add("Uber")
        itemArrayList.add("Meru")
        itemArrayList.add("Zomato")
        itemArrayList.add("Swiggy")
        itemArrayList.add("FoodPanda")
        itemArrayList.add("BlueDart")
        itemArrayList.add("DTDC")
        itemArrayList.add("Fedex")
        itemArrayList.add("Jabong")
        itemArrayList.add("Flipkart")
        itemArrayList.add("Amazon")
        itemArrayList.add("BigBasket")
        itemArrayList.add("Grofers")
        itemArrayList.add("Godrej")
        itemArrayList.add("Carpenter")
        itemArrayList.add("Plumber")
        itemArrayList.add("electrician")
        itemArrayList.add("Others")


        val data: ArrayList<VendorPojo> = prepareData()

        val adapter = VehicleOthersCompanyItemRVAdapter(this@VehicleOthersServiceProviderListActivity, data)
        rv_serviceProvider.adapter = adapter

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
    private fun prepareData(): ArrayList<VendorPojo> {
        val vendordata: ArrayList<VendorPojo> = ArrayList()

        for (i in 0 until vendor_names.size) {
            val vendorPojo = VendorPojo()
            vendorPojo.setVendor_names(vendor_names[i])
            vendorPojo.setImage_url(myImageList[i])
            vendordata.add(vendorPojo)
        }
        return vendordata
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent= Intent(this@VehicleOthersServiceProviderListActivity, Dashboard::class.java)
        startActivity(intent)
        finish()
    }
}

