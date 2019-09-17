package com.oyespace.guards.vehicle_others

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.speech.RecognizerIntent
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.oyespace.guards.Dashboard
import kotlinx.android.synthetic.main.activity_unit_list.*
import com.oyespace.guards.R
import com.oyespace.guards.activity.BaseKotlinActivity
import com.oyespace.guards.activity.PurposeScreen
import com.oyespace.guards.constants.PrefKeys.LANGUAGE
import com.oyespace.guards.network.CommonDisposable
import com.oyespace.guards.network.RetrofitClinet
import com.oyespace.guards.pojo.UnitList
import com.oyespace.guards.pojo.UnitPojo
import com.oyespace.guards.utils.AppUtils
import com.oyespace.guards.utils.AppUtils.Companion.intToString
import com.oyespace.guards.utils.ConstantUtils.*
import com.oyespace.guards.utils.LocalDb
import com.oyespace.guards.utils.Prefs
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

class VehicleOthersUnitScreen : BaseKotlinActivity() , View.OnClickListener  {
    lateinit var txt_assn_name:TextView
    lateinit var txt_gate_name:TextView
    lateinit var txt_device_name:TextView
    var arrayList = ArrayList<UnitPojo>()
    var orderListAdapter:UnitListAdapter?=null
    private val REQUEST_CODE_SPEECH_INPUT = 100
    internal var unitNames = ""
    internal var unitId = ""
    internal var acAccntID=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocale(Prefs.getString(LANGUAGE, null))
        setContentView(R.layout.activity_unit_list)

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

        if(LocalDb.getUnitList()!=null){
            arrayList = LocalDb.getUnitList()
            orderListAdapter = UnitListAdapter(arrayList, this@VehicleOthersUnitScreen)
            rv_unit.adapter = orderListAdapter
        }else {
            makeUnitLog()
        }
        rv_unit.setLayoutManager(androidx.recyclerview.widget.GridLayoutManager(this@VehicleOthersUnitScreen, 2))

        btn_mic.setOnClickListener {
            Speak()
        }
    }

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.buttonNext ->{
                buttonNext.setEnabled(false)
                buttonNext.setClickable(false)
                if (arrayList.size > 0) {
                    for (j in arrayList.indices) {
                        if (arrayList.get(j).isSelected) {
                            if(unitNames.length!=0){
                                unitNames +=  ", "
                                unitId+= ", "
                                acAccntID+=", "
                            }
                            unitNames += arrayList.get(j).unUniName
                            unitId += arrayList.get(j).unUnitID
                            acAccntID+=arrayList.get(j).acAccntID

                        }
                    }

                    if ( unitNames.length > 0) {

                        if(intent.getStringExtra(COMPANY_NAME).equals("Others")){
                            val d = Intent(this@VehicleOthersUnitScreen, VehicleOthersPurposeNameEntryScreen::class.java)
//                            Log.d( "intentdata MobileNumber", "buttonNext " + intent.getStringExtra(UNITNAME) +
// " " + intent.getStringExtra(UNITID) + " " + Ed_phoneNum.text + " " + countryCode );
                            d.putExtra(UNITID, unitId)
                            d.putExtra(UNITNAME, unitNames)
                            d.putExtra(FLOW_TYPE, intent.getStringExtra(FLOW_TYPE))
                            d.putExtra(VISITOR_TYPE, intent.getStringExtra(VISITOR_TYPE))
                            d.putExtra(COMPANY_NAME, intent.getStringExtra(COMPANY_NAME))
                            d.putExtra(VEHICLE_NUMBER, intent.getStringExtra(VEHICLE_NUMBER))
                            d.putExtra(UNIT_ACCOUNT_ID,acAccntID)
                            startActivity(d)
                            finish()
                        }else {


                            val d = Intent(this@VehicleOthersUnitScreen, VehicleOthersMobileNumberScreen::class.java)
                            Log.d(
                                "intentdata NameEntr",
                                "buttonNext " + intent.getStringExtra(UNITNAME) + " " + intent.getStringExtra(
                                    UNITID
                                )
                                        + " " + intent.getStringExtra(MOBILENUMBER) + " " + intent.getStringExtra(
                                    COUNTRYCODE
                                ) + " "
                            )
                            d.putExtra(UNITID, unitId)
                            d.putExtra(UNITNAME, unitNames)
                            d.putExtra(FLOW_TYPE, VEHICLE_OTHERS)
                            d.putExtra(VISITOR_TYPE, intent.getStringExtra(VISITOR_TYPE))
                            d.putExtra(COMPANY_NAME, intent.getStringExtra(COMPANY_NAME))
                            d.putExtra(VEHICLE_NUMBER, intent.getStringExtra(VEHICLE_NUMBER))
                            d.putExtra(UNIT_ACCOUNT_ID,acAccntID)
                            startActivity(d)
                            finish()
                        }

                    } else {
                        buttonNext.setEnabled(true)
                        buttonNext.setClickable(true)
                        Toast.makeText(applicationContext, "Select Unit", Toast.LENGTH_SHORT).show()

                    }
                } else {
                    Toast.makeText(applicationContext, "No data", Toast.LENGTH_SHORT).show()
                }

            }

        }
    }

//    override fun onBackPressed() {
//        super.onBackPressed()
////        val intent = Intent(this@VehicleOthersUnitScreen, VehicleOthersServiceProviderListActivity::class.java)
////        intent.putExtra(FLOW_TYPE,getIntent().getStringExtra(FLOW_TYPE))
////        intent.putExtra(VISITOR_TYPE,getIntent().getStringExtra(VISITOR_TYPE))
////        intent.putExtra(COMPANY_NAME,getIntent().getStringExtra(COMPANY_NAME))
////        startActivity(intent)
//    }

    private fun makeUnitLog() {

        RetrofitClinet.instance
            .unitList("1FDF86AF-94D7-4EA9-8800-5FBCCFF8E5C1", intToString( Prefs.getInt(ASSOCIATION_ID,0)))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CommonDisposable<UnitList<ArrayList<UnitPojo>>>() {

                override fun onSuccessResponse(UnitList: UnitList<ArrayList<UnitPojo>>) {

                    if (UnitList.success == true) {
                        Log.d("cdvd",UnitList.toString())

                        arrayList = UnitList.data.unit
//                        TODO save unit list
                        orderListAdapter = UnitListAdapter(arrayList, this@VehicleOthersUnitScreen)

                        rv_unit.adapter = orderListAdapter
//                        rv_unit.adapter = UnitAdapter(entries, this@UnitListActivity)

                    } else {
                        rv_unit.setEmptyAdapter("No items to show!", false, 0)
                    }
                }

                override fun onErrorResponse(e: Throwable) {
                    Log.d("cdvd",e.message)

                    Toast.makeText(this@VehicleOthersUnitScreen, "Error 8", Toast.LENGTH_LONG)
                        .show()

                }

                override fun noNetowork() {
                    Toast.makeText(this@VehicleOthersUnitScreen, "No network call ", Toast.LENGTH_LONG)
                        .show()
                }
            })

    }

    private fun showSettingsDialog() {
        val builder = AlertDialog.Builder(this@VehicleOthersUnitScreen)
        builder.setTitle("Need Permissions")
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.")
        builder.setPositiveButton("GOTO SETTINGS") { dialog, which ->
            dialog.cancel()
            openSettings()
        }
        builder.setNegativeButton(
            "Cancel"
        ) { dialog, which -> dialog.cancel() }
        builder.show()
    }

    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivityForResult(intent, 101)
    }


    class UnitListAdapter(private val listVistor: ArrayList<UnitPojo>, private val mcontext: Context) :
        androidx.recyclerview.widget.RecyclerView.Adapter<UnitListAdapter.MenuHolder>() {

        private val mInflater: LayoutInflater

        init {
            mInflater = LayoutInflater.from(mcontext)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuHolder {
            val mainGroup = mInflater.inflate(R.layout.layout_unit_adapter_row, parent, false) as ViewGroup
            return MenuHolder(mainGroup)
        }

        override fun onBindViewHolder(holder: MenuHolder, position: Int) {
            val orderData = listVistor.get(position)
            val vistordate = orderData.asAssnID
            holder.apartmentNamee.text = orderData.unUniName

            holder.cb_unit.setOnCheckedChangeListener {buttonView, isChecked ->
                // Toast.makeText(this,isChecked.toString(),Toast.LENGTH_SHORT).show()
                listVistor.get(position).isSelected=isChecked

            }

            holder.iv_unit.setOnClickListener {
//                val intent = Intent(Intent.ACTION_CALL);
//                intent.data = Uri.parse("tel:"+orderData.owner.uoisdCode+orderData.owner.uoMobile)
//                mcontext.startActivity(intent)
                if(orderData.owner!=null) {
                    Log.d("cdvd 2", "" + orderData.owner[0].uoisdCode + " " + orderData.owner[0].uoMobile)
                    val intent = Intent(Intent.ACTION_CALL)
                    intent.data = Uri.parse("tel:"+ orderData.owner[0].uoMobile)
                    mcontext.startActivity(intent)
                }else{
                    Toast.makeText(mcontext,"Not a Valid Mobile Number",Toast.LENGTH_SHORT).show()

                }
            }
            holder.lv_itemrecyclerview.setOnClickListener({
                val mcontextintent = (mcontext as Activity).intent

                val intent = Intent(mcontext, VehicleOthersMobileNumberScreen::class.java)
                intent.putExtra(FLOW_TYPE,mcontextintent.getStringExtra(FLOW_TYPE))
                intent.putExtra(VISITOR_TYPE,mcontextintent.getStringExtra(VISITOR_TYPE))
                intent.putExtra(COMPANY_NAME,mcontextintent.getStringExtra(COMPANY_NAME))
                intent.putExtra(UNITID, AppUtils.intToString(orderData.unUnitID))
                intent.putExtra(UNITNAME, orderData.unUniName)
//                mcontext.startActivity(intent)
//                (mcontext as Activity).finish()
                if( listVistor.get(position).isSelected){
                    listVistor.get(position).isSelected=false
                    holder.cb_unit.isChecked = false
                }else{
                    listVistor.get(position).isSelected=true
                    holder.cb_unit.isChecked = true
                }

            })
        }

        override fun getItemCount(): Int {
            return listVistor.size
        }

        inner class MenuHolder(private val view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {

            val iv_unit: ImageView
            val cb_unit: CheckBox
            val apartmentNamee: TextView
            val lv_itemrecyclerview: RelativeLayout

            init {

                iv_unit = view.findViewById(R.id.iv_unit)
                cb_unit = view.findViewById(R.id.cb_unit)
                apartmentNamee = view.findViewById(R.id.tv_unit)
                lv_itemrecyclerview=view.findViewById(R.id.lv_itemrecyclerview)

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
                    edt_search_text1.setText(result[0] + "")

                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
//        val intent= Intent(this@VehicleOthersUnitScreen, Dashboard::class.java)
//        startActivity(intent)
        finish()
    }
}