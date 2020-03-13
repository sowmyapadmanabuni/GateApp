package com.oyespace.guards.activity

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
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

import android.os.Build
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.oyespace.guards.network.CommonDisposable
import com.oyespace.guards.network.RetrofitClinet
import com.oyespace.guards.pojo.VendorType
import com.oyespace.guards.pojo.VendorTypes
import com.oyespace.guards.utils.ConstantUtils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_service_provider_list.*
import kotlinx.android.synthetic.main.header_with_next.*


class ServiceProviderListActivity : BaseKotlinActivity() {

    var iv_torch: Button?=null
    var clickable1 = 0
    lateinit var txt_assn_name: TextView
    lateinit var txt_gate_name: TextView
    lateinit var txt_device_name: TextView

    lateinit var vendor_names: List<String>
           // = Arrays.asList(resources.getStringArray(R.array.vendor_list))


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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocale(Prefs.getString(LANGUAGE, null))

        setContentView(R.layout.activity_service_provider_list)

        buttonNext.visibility=View.GONE
        getVendorType()
       // vendor_names = Arrays.asList(resources.getStringArray(R.array.vendor_list))

        vendor_names= listOf(resources.getString(R.string.textzomato),
            resources.getString(R.string.textswiggy),
            resources.getString(R.string.textfoodpanda),
            resources.getString(R.string.textubereats),
            resources.getString(R.string.textdominos),
            resources.getString(R.string.textbluedart),
            resources.getString(R.string.textdtdc),
            resources.getString(R.string.textfedex),
            resources.getString(R.string.textjabong),
            resources.getString(R.string.textflipkart),
            resources.getString(R.string.textamazon),
            resources.getString(R.string.textbigbasket),
            resources.getString(R.string.textgrofers),
            resources.getString(R.string.textgodrej),
            resources.getString(R.string.textcarpenter),
            resources.getString(R.string.textplumber),
            resources.getString(R.string.textelectrician),
            resources.getString(R.string.textola),
            resources.getString(R.string.textuber),
            resources.getString(R.string.textmeru),
            resources.getString(R.string.textgascylinde),
            resources.getString(R.string.textothers))

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

        //  getServiceProviderList()
        val rv_serviceProvider = findViewById<RecyclerView>(R.id.rv_serviceProvider)
        rv_serviceProvider.setHasFixedSize(true)
        val gridLayoutManager = GridLayoutManager(this@ServiceProviderListActivity, 3)
        rv_serviceProvider.layoutManager = gridLayoutManager

//        val data: ArrayList<VendorPojo> = prepareData()
//        val adapter = CompanyItemRVAdapter( this@ServiceProviderListActivity,data,intent.getStringExtra(UNITID),intent.getStringExtra(UNITNAME),intent.getStringExtra(FLOW_TYPE),intent.getStringExtra(VISITOR_TYPE),intent.getStringExtra(MOBILENUMBER),"+91",intent.getStringExtra(UNIT_ACCOUNT_ID))
//        rv_serviceProvider.adapter = adapter

        //for loop for sections
        for (i in 1..4) {
            val itemArrayList: ArrayList<String> = ArrayList()
            //for loop for items
            for (j in 1..3) {
                itemArrayList.add("Item $j")
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
    private fun prepareData(): ArrayList<VendorPojo> {
        val vendordata: ArrayList<VendorPojo> = ArrayList()

        for (i in 0 until vendor_names.size) {
            val vendorPojo = VendorPojo()
            vendorPojo.vendor_names = vendor_names[i].toString()
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

    fun getVendorType() {
        RetrofitClinet.instance.getVendorTypes(
                ConstantUtils.OYE247TOKEN)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CommonDisposable<VendorTypes<ArrayList<VendorType>>>() {

                override fun onSuccessResponse(getdata: VendorTypes<ArrayList<VendorType>>) {

                    val adapter = CompanyItemRVAdapter( this@ServiceProviderListActivity,getdata.data.vendorTypes,intent.getStringExtra(UNITID),intent.getStringExtra(UNITNAME),intent.getStringExtra(FLOW_TYPE),intent.getStringExtra(VISITOR_TYPE),intent.getStringExtra(MOBILENUMBER),"+91",intent.getStringExtra(UNIT_ACCOUNT_ID))
                    rv_serviceProvider.adapter = adapter

                }

                override fun onErrorResponse(e: Throwable) {
                }

                override fun noNetowork() {
                    Toast.makeText(
                        this@ServiceProviderListActivity,
                        "No network call ",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })

    }

}