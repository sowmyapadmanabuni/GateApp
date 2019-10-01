package com.oyespace.guards.activity

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.oyespace.guards.R
import com.oyespace.guards.adapter.CompanyItemRVAdapter
import com.oyespace.guards.constants.PrefKeys.LANGUAGE
import com.oyespace.guards.pojo.VendorPojo
import com.oyespace.guards.utils.ConstantUtils
import com.oyespace.guards.utils.LocalDb
import com.oyespace.guards.utils.Prefs
import java.util.*


class ServiceProviderListActivity : BaseKotlinActivity() {


    lateinit var txt_assn_name: TextView
    lateinit var txt_gate_name: TextView
    lateinit var txt_device_name: TextView
    val myImageList = intArrayOf(
        R.drawable.zomoto,
        R.mipmap.sw,
        R.mipmap.foodpanda,
        R.drawable.ubereats,
        R.drawable.dominospizza,
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
        R.drawable.ola,
        R.drawable.uber,
        R.drawable.meru,
        R.drawable.gas_cylinder,
        R.drawable.others


    )
    val vendor_names = arrayOf(
        "Zomato",
        "Swiggy",
        "FoodPanda",
        "Uber Eats",
        "Dominos",
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
        "Electrician",
        "OLA",
        "Uber",
        "Meru",
        "Gas Cylinder",
        "Others"

    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocale(Prefs.getString(LANGUAGE, null))

        setContentView(R.layout.activity_service_provider_list)

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



        //  getServiceProviderList()
        val rv_serviceProvider = findViewById<RecyclerView>(R.id.rv_serviceProvider)
        rv_serviceProvider.setHasFixedSize(true)
        val gridLayoutManager = GridLayoutManager(this@ServiceProviderListActivity, 3)
        rv_serviceProvider.layoutManager = gridLayoutManager

        val data: ArrayList<VendorPojo> = prepareData()
        val adapter = CompanyItemRVAdapter( this@ServiceProviderListActivity,data)
        rv_serviceProvider.adapter = adapter

//        val itemArrayList: ArrayList<String> = ArrayList()
//        //for loop for items
//        // for (j in 1..3) {
//        //  itemArrayList.add("Item $j")
//        // }
//
//
//
//
//        itemArrayList.add("Zomato")
//        itemArrayList.add("Swiggy")
//        itemArrayList.add("FoodPanda")
//        itemArrayList.add("BlueDart")
//        itemArrayList.add("DTDC")
//        itemArrayList.add("Fedex")
//        itemArrayList.add("Jabong")
//        itemArrayList.add("Flipkart")
//        itemArrayList.add("Amazon")
//        itemArrayList.add("BigBasket")
//        itemArrayList.add("Grofers")
//        itemArrayList.add("Godrej")
//        itemArrayList.add("Carpenter")
//        itemArrayList.add("Plumber")
//        itemArrayList.add("electrician")
//        itemArrayList.add("OLA")
//        itemArrayList.add("Uber")
//        itemArrayList.add("Meru")
//        itemArrayList.add("Others")





        // val linearLayoutManager = LinearLayoutManager(this)
        //rv_serviceProvider!!.layoutManager = linearLayoutManager

        //  val sectionModelArrayList: ArrayList<SectionModel> = ArrayList()

        //for loop for sections
        for (i in 1..4) {
            val itemArrayList: ArrayList<String> = ArrayList()
            //for loop for items
            for (j in 1..3) {
                itemArrayList.add("Item $j")
            }

            //add the section and items to array list
            // sectionModelArrayList.add(SectionModel("Section $i", itemArrayList))
        }

        // val adapter = ServiceProviderAdapter(this, sectionModelArrayList)
        // rv_serviceProvider!!.adapter = adapter

    }


//    private fun getServiceProviderList() {
//
//
//        RetrofitClinet.instance
//            .serviceProviderList("7470AD35-D51C-42AC-BC21-F45685805BBE")
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribeWith(object : CommonDisposable<SeviceProviderListResponse>() {
//
//                override fun onSuccessResponse(serviceProviderlistresponse: SeviceProviderListResponse) {
//
//                    if (serviceProviderlistresponse.success == true) {
//
//                        Log.v("Service Provider data",serviceProviderlistresponse.data.toString())
//
//                        val arrayList = serviceProviderlistresponse.data.serviceProviders
//                        val adapter = CompanyItemRVAdapter( this@ServiceProviderListActivity,arrayList)
//                        rv_serviceProvider.adapter = adapter
//                    } else {
//                        // rv_serviceProvider.setEmptyAdapter("No items to show!", false, 0)
//
//                    }
//                }
//
//                override fun onErrorResponse(e: Throwable) {
//
//                    //rv_dashboard.setEmptyAdapter(getString(R.string.some_wrng), false, 0)
//                    Toast.makeText(this@ServiceProviderListActivity, e.toString(), Toast.LENGTH_LONG).show()
//                    Log.v("Error Service Provider",e.toString())
//
//                }
//
//                override fun noNetowork() {
//                    Toast.makeText(this@ServiceProviderListActivity, "No network call ", Toast.LENGTH_LONG).show()
//
//
//                }
//            })
//
//    }

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
            vendorPojo.vendor_names = vendor_names[i]
            vendorPojo.image_url = myImageList[i]
            vendordata.add(vendorPojo)
        }
        return vendordata
    }

    override fun onBackPressed() {
        super.onBackPressed()
//        val i_delivery = Intent(this@ServiceProviderListActivity, Dashboard::class.java)
//        startActivity(i_delivery)
        finish()

    }
}