package com.oyespace.guards.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.speech.RecognizerIntent
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.oyespace.guards.Dashboard
import com.oyespace.guards.R
import com.oyespace.guards.constants.PrefKeys.LANGUAGE
import com.oyespace.guards.network.CommonDisposable
import com.oyespace.guards.network.RetrofitClinet
import com.oyespace.guards.pojo.UnitList
import com.oyespace.guards.pojo.UnitPojo
import com.oyespace.guards.utils.AppUtils
import com.oyespace.guards.utils.AppUtils.Companion.intToString
import com.oyespace.guards.utils.ConstantUtils
import com.oyespace.guards.utils.ConstantUtils.*
import com.oyespace.guards.utils.LocalDb
import com.oyespace.guards.utils.Prefs
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_unit_list.*
import java.util.*

class UnitListActivity : BaseKotlinActivity() , View.OnClickListener  {
    lateinit var txt_assn_name:TextView
    lateinit var txt_gate_name:TextView
    lateinit var txt_device_name:TextView
    var orderListAdapter:UnitListAdapter?=null
    var arrayList = ArrayList<UnitPojo>()
    private val REQUEST_CODE_SPEECH_INPUT = 100
    internal var unitNames = ""
    internal var unitId = 0
    internal var unitNumber1=""
    internal var unitNumber2=""
    internal var unitNumber3=""
    internal var unitNumber4=""
    internal var unitNumber5=""

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
            orderListAdapter = UnitListAdapter(arrayList as ArrayList<UnitPojo>, this@UnitListActivity)
            rv_unit.adapter = orderListAdapter
        }else {
            makeUnitLog()
        }

        rv_unit.setLayoutManager( GridLayoutManager(this@UnitListActivity, 2))

        btn_mic.setOnClickListener {
            Speak()
        }

    }

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.buttonNext ->{

                buttonNext.setEnabled(false)
                buttonNext.setClickable(false)
                if (arrayList?.size > 0) {
                    for (j in arrayList.indices) {
                        if (arrayList.get(j).isSelected) {
                            if((unitNames.length!=0)||(unitNumber1.length!=0)){
                                unitNames +=  ", "
                                unitNumber1+=", "
                                unitNumber2+=", "
                                unitNumber3+=", "
                                unitNumber4+=", "
                                unitNumber5+=", "
                            }
                            unitNames += arrayList.get(j).unUniName
                            unitId = arrayList.get(j).unUnitID
                            if(arrayList.get(j).tenant.size!=0){
                                try {
                                    unitNumber1 += arrayList.get(j).tenant[0].utMobile
                                    unitNumber2 += arrayList.get(j).tenant[0].utMobile1
                                  //0  Toast.makeText(this@UnitListActivity, unitNumber1, Toast.LENGTH_LONG).show()
                                }catch (e:IndexOutOfBoundsException){

                                }
                            }else {
                                if(arrayList.get(j).owner.size!=0){

                                    try{
                                        unitNumber1+=arrayList.get(j).owner[0].uoMobile
                                        unitNumber2+=arrayList.get(j).owner[0].uoMobile1
                                        unitNumber3+=arrayList.get(j).owner[0].uoMobile2
                                        unitNumber4+=arrayList.get(j).owner[0].uoMobile3
                                        unitNumber5+=arrayList.get(j).owner[0].uoMobile4

                                     //   Toast.makeText(this@UnitListActivity,unitNumber1,Toast.LENGTH_LONG).show()
                                    }catch (e:IndexOutOfBoundsException){

                                    }

                                }

                            }



                        }
                    }

                    if ( unitNames.length > 0) {

                        if(intent.getStringExtra(COMPANY_NAME).equals("Others")){
                            val d = Intent(this@UnitListActivity, PurposeScreen::class.java)
//                            Log.d( "intentdata MobileNumber", "buttonNext " + intent.getStringExtra(UNITNAME) +
// " " + intent.getStringExtra(UNITID) + " " + Ed_phoneNum.text + " " + countryCode );
                            d.putExtra(UNITID, intToString(unitId))
                            d.putExtra(UNITNAME, unitNames)
                            d.putExtra(FLOW_TYPE, intent.getStringExtra(FLOW_TYPE))
                            d.putExtra(VISITOR_TYPE, intent.getStringExtra(VISITOR_TYPE))
                            d.putExtra(COMPANY_NAME, intent.getStringExtra(COMPANY_NAME))
                            d.putExtra("RESIDENT_NUMBER",unitNumber1+", "+unitNumber2+", "+unitNumber3+", "+unitNumber4+", "+unitNumber5)

                            startActivity(d);
                            finish();
                        }else {

                            val d = Intent(this@UnitListActivity, MobileNumberScreen::class.java)
                            Log.d("intentdata NameEntr", "buttonNext " + getIntent().getStringExtra(UNITNAME) + " "
                                    + intent.getStringExtra(UNITID) + " " + getIntent().getStringExtra(MOBILENUMBER) + " "
                                    + getIntent().getStringExtra(COUNTRYCODE) + " ");
                            d.putExtra(UNITID, intToString(unitId))
                            d.putExtra(UNITNAME, unitNames)
                            d.putExtra(FLOW_TYPE, intent.getStringExtra(FLOW_TYPE))
                            d.putExtra(VISITOR_TYPE, intent.getStringExtra(VISITOR_TYPE))
                            d.putExtra(COMPANY_NAME, intent.getStringExtra(COMPANY_NAME))
                            //d.putExtra("RESIDENT_NUMBER",unitNumber1)
                            d.putExtra("RESIDENT_NUMBER",unitNumber1+", "+unitNumber2+", "+unitNumber3+", "+unitNumber4+", "+unitNumber5)
                            startActivity(d);
                            finish();
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
//        if(getIntent().getStringExtra(FLOW_TYPE).equals(DELIVERY)) {
//            val intent = Intent(this@UnitListActivity, ServiceProviderListActivity::class.java)
//            intent.putExtra(FLOW_TYPE, getIntent().getStringExtra(FLOW_TYPE))
//            intent.putExtra(VISITOR_TYPE, getIntent().getStringExtra(VISITOR_TYPE))
//            intent.putExtra(COMPANY_NAME, getIntent().getStringExtra(COMPANY_NAME))
//            startActivity(intent)
//        }else{
//            val intent = Intent(this@UnitListActivity, WorkersTypeList::class.java)
//            intent.putExtra(FLOW_TYPE, getIntent().getStringExtra(FLOW_TYPE))
//            intent.putExtra(VISITOR_TYPE, getIntent().getStringExtra(VISITOR_TYPE))
//            intent.putExtra(COMPANY_NAME, getIntent().getStringExtra(COMPANY_NAME))
//            startActivity(intent)
//        }
//    }

    private fun makeUnitLog() {

        RetrofitClinet.instance
            .unitList(CHAMPTOKEN, intToString( Prefs.getInt(ASSOCIATION_ID,0)))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CommonDisposable<UnitList<ArrayList<UnitPojo>>>() {

                override fun onSuccessResponse(UnitList: UnitList<ArrayList<UnitPojo>>) {

                    if (UnitList.success == true) {
                        Log.d("cdvd",UnitList.toString());

                        arrayList = UnitList.data.unit
//                        TODO save unit list
                        orderListAdapter = UnitListAdapter(arrayList as ArrayList<UnitPojo>, this@UnitListActivity)
                        rv_unit.adapter = orderListAdapter
//                        rv_unit.adapter = UnitAdapter(entries, this@UnitListActivity)

                    } else {
                        rv_unit.setEmptyAdapter("No items to show!", false, 0)
                    }
                }

                override fun onErrorResponse(e: Throwable) {
                    Log.d("cdvd",e.message);
                    Toast.makeText(this@UnitListActivity, "Error ", Toast.LENGTH_LONG).show()

                }

                override fun noNetowork() {
                    Toast.makeText(this@UnitListActivity, "No network call ", Toast.LENGTH_LONG).show()
                }
            })

    }

    private fun showSettingsDialog() {
        val builder = AlertDialog.Builder(this@UnitListActivity)
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
        intent.setData(uri)
        startActivityForResult(intent, 101)
    }


    class UnitListAdapter(private val listVistor: ArrayList<UnitPojo>, private val mcontext: Context) :
        RecyclerView.Adapter<UnitListAdapter.MenuHolder>() {

        private val mInflater: LayoutInflater


        init {
            mInflater = LayoutInflater.from(mcontext)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuHolder {
            val mainGroup = mInflater.inflate(R.layout.layout_unit_adapter_row, parent, false) as ViewGroup
            return MenuHolder(mainGroup)
        }


        override fun onBindViewHolder(holder: MenuHolder, position: Int) {
            val orderData = listVistor?.get(position)
            val vistordate = orderData?.asAssnID
            holder.apartmentNamee.text = orderData?.unUniName

            holder.cb_unit.setOnCheckedChangeListener {buttonView, isChecked ->
                // Toast.makeText(this,isChecked.toString(),Toast.LENGTH_SHORT).show()
                listVistor!!.get(position).isSelected=isChecked

            }
            //  Log.d("cdvd",orderData?.unUniName+" "+orderData.owner.uoisdCode+""+orderData.owner.uoMobile);

            holder.iv_unit.setOnClickListener {
                if (orderData!!.owner.size == 0 && orderData!!.tenant.size == 0) {
                    //  Log.d("cdvd 2", "" + orderData?.owner[0].uoisdCode + " " + orderData?.owner[0].uoMobile);

//if(orderData.owner[0].uoMobile!=null) {


                    val dialogBuilder = AlertDialog.Builder(mcontext)

                    // set message of alert dialog
                    dialogBuilder.setMessage("Not a Valid Mobile Number")
                        // if the dialog is cancelable
                        .setCancelable(false)
                        // positive button text and action
                        .setPositiveButton("Ok", DialogInterface.OnClickListener { dialog, id ->
                            dialog.cancel()


                        })
                    // negative button text and action
//                        .setNegativeButton("Cancel", DialogInterface.OnClickListener {
//                                dialog, id -> dialog.cancel()
//                        })

                    // create dialog box
                    val alert = dialogBuilder.create()
                    // set title for alert dialog box
                    // show alert dialog
                    alert.show()
                } else {
                    // Toast.makeText(mcontext,"Not a Valid Mobile Number",Toast.LENGTH_SHORT).show()

//                    val intent = Intent(Intent.ACTION_CALL);
//                    intent.data = Uri.parse("tel:" + orderData.owner[0].uoMobile)
//                    mcontext.startActivity(intent)


                    if (orderData!!.tenant.size != 0) {

                        val alertadd = android.support.v7.app.AlertDialog.Builder(mcontext)
                        val factory = LayoutInflater.from(mcontext)
                        val view = factory.inflate(R.layout.layout_phonenumber, null)
                        var tv_number1: TextView? = null
                        tv_number1 = view.findViewById(R.id.tv_number1)
                        var tv_number2: TextView? = null
                        tv_number2 = view.findViewById(R.id.tv_number2)


                        var iv_unit1: ImageView? = null
                        iv_unit1 = view.findViewById(R.id.iv_unit1)

                        var iv_unit2: ImageView? = null
                        iv_unit2 = view.findViewById(R.id.iv_unit2)

                        try {

                            if (orderData.tenant[0].utMobile.equals("")) {
                                iv_unit1.visibility = View.GONE
                                tv_number1.visibility = View.GONE
                            } else {

                                iv_unit1.visibility = View.VISIBLE
                                tv_number1.visibility = View.VISIBLE
                                tv_number1.setText(orderData.tenant[0].utMobile)
                            }

                            if (orderData.tenant[0].utMobile1.equals("")) {
                                iv_unit2.visibility = View.GONE
                                tv_number2.visibility = View.GONE
                            } else {

                                iv_unit2.visibility = View.VISIBLE
                                tv_number2.visibility = View.VISIBLE
                                tv_number2.setText(orderData.tenant[0].utMobile1)
                            }


                        } catch (e: IndexOutOfBoundsException) {

                        }

                        iv_unit1.setOnClickListener {

                            val intent = Intent(Intent.ACTION_CALL);
                            intent.data = Uri.parse("tel:" + orderData.tenant[0].utMobile)
                            mcontext.startActivity(intent)

                        }


                        iv_unit2.setOnClickListener {

                            val intent = Intent(Intent.ACTION_CALL);
                            intent.data = Uri.parse("tel:" + orderData.tenant[0].utMobile1)
                            mcontext.startActivity(intent)

                        }


                        alertadd.setView(view)
                        alertadd.show()

                    } else {

                        if (orderData!!.owner.size != 0) {

                            val alertadd = android.support.v7.app.AlertDialog.Builder(mcontext)
                            val factory = LayoutInflater.from(mcontext)
                            val view = factory.inflate(R.layout.layout_phonenumber, null)
                            var tv_number1: TextView? = null
                            tv_number1 = view.findViewById(R.id.tv_number1)
                            var tv_number2: TextView? = null
                            tv_number2 = view.findViewById(R.id.tv_number2)
                            var tv_number3: TextView? = null
                            tv_number3 = view.findViewById(R.id.tv_number3)
                            var tv_number4: TextView? = null
                            tv_number4 = view.findViewById(R.id.tv_number4)
                            var tv_number5: TextView? = null
                            tv_number5 = view.findViewById(R.id.tv_number5)


                            var iv_unit1: ImageView? = null
                            iv_unit1 = view.findViewById(R.id.iv_unit1)

                            var iv_unit2: ImageView? = null
                            iv_unit2 = view.findViewById(R.id.iv_unit2)

                            var iv_unit3: ImageView? = null
                            iv_unit3 = view.findViewById(R.id.iv_unit3)

                            var iv_unit4: ImageView? = null
                            iv_unit4 = view.findViewById(R.id.iv_unit4)

                            var iv_unit5: ImageView? = null
                            iv_unit5 = view.findViewById(R.id.iv_unit5)

                            try {

                                if (orderData.owner[0].uoMobile.equals("")) {
                                    iv_unit1.visibility = View.GONE
                                    tv_number1.visibility = View.GONE
                                } else {

                                    iv_unit1.visibility = View.VISIBLE
                                    tv_number1.visibility = View.VISIBLE
                                    tv_number1.setText(orderData.owner[0].uoMobile)
                                }

                                if (orderData.owner[0].uoMobile1.equals("")) {
                                    iv_unit2.visibility = View.GONE
                                    tv_number2.visibility = View.GONE
                                } else {
                                    iv_unit2.visibility = View.VISIBLE
                                    tv_number2.visibility = View.VISIBLE
                                    tv_number2.setText(orderData.owner[0].uoMobile1)
                                }

                                if (orderData.owner[0].uoMobile2.equals("")) {
                                    iv_unit3.visibility = View.GONE
                                    tv_number3.visibility = View.GONE
                                } else {
                                    iv_unit3.visibility = View.VISIBLE
                                    tv_number3.visibility = View.VISIBLE
                                    tv_number3.setText(orderData.owner[0].uoMobile2)
                                }

                                if (orderData.owner[0].uoMobile3.equals("")) {
                                    iv_unit4.visibility = View.GONE
                                    tv_number4.visibility = View.GONE
                                } else {
                                    iv_unit4.visibility = View.VISIBLE
                                    tv_number4.visibility = View.VISIBLE
                                    tv_number4.setText(orderData.owner[0].uoMobile3)
                                }
                                if (orderData.owner[0].uoMobile4.equals("")) {
                                    iv_unit5!!.visibility = View.GONE
                                    tv_number5.visibility = View.GONE
                                } else {
                                    iv_unit5!!.visibility = View.VISIBLE
                                    tv_number5.visibility = View.VISIBLE
                                    tv_number5.setText(orderData.owner[0].uoMobile4)
                                }
                            } catch (e: IndexOutOfBoundsException) {

                            }

                            iv_unit1.setOnClickListener {

                                val intent = Intent(Intent.ACTION_CALL);
                                intent.data = Uri.parse("tel:" + orderData.owner[0].uoMobile)
                                mcontext.startActivity(intent)

                            }

                            iv_unit2.setOnClickListener {

                                val intent = Intent(Intent.ACTION_CALL);
                                intent.data = Uri.parse("tel:" + orderData.owner[0].uoMobile1)
                                mcontext.startActivity(intent)

                            }

                            iv_unit3.setOnClickListener {

                                val intent = Intent(Intent.ACTION_CALL);
                                intent.data = Uri.parse("tel:" + orderData.owner[0].uoMobile2)
                                mcontext.startActivity(intent)

                            }
                            iv_unit4.setOnClickListener {

                                val intent = Intent(Intent.ACTION_CALL);
                                intent.data = Uri.parse("tel:" + orderData.owner[0].uoMobile3)
                                mcontext.startActivity(intent)

                            }
                            iv_unit5.setOnClickListener {

                                val intent = Intent(Intent.ACTION_CALL);
                                intent.data = Uri.parse("tel:" + orderData.owner[0].uoMobile4)
                                mcontext.startActivity(intent)

                            }




                            alertadd.setView(view)
                            alertadd.show()
                        } else {

                            val dialogBuilder = AlertDialog.Builder(mcontext)

                            // set message of alert dialog
                            dialogBuilder.setMessage("Not a Valid Mobile Number")
                                // if the dialog is cancelable
                                .setCancelable(false)
                                // positive button text and action
                                .setPositiveButton("Ok", DialogInterface.OnClickListener { dialog, id ->
                                    dialog.cancel()


                                })
                            // negative button text and action
//                        .setNegativeButton("Cancel", DialogInterface.OnClickListener {
//                                dialog, id -> dialog.cancel()
//                        })

                            // create dialog box
                            val alert = dialogBuilder.create()
                            // set title for alert dialog box
                            // show alert dialog
                            alert.show()

                        }

                    }

                }
            }
            holder.lv_itemrecyclerview.setOnClickListener({
                val mcontextintent = (mcontext as Activity).intent

                val intent = Intent(mcontext, MobileNumberScreen::class.java)
                intent.putExtra(FLOW_TYPE,mcontextintent.getStringExtra(FLOW_TYPE))
                intent.putExtra(VISITOR_TYPE,mcontextintent.getStringExtra(VISITOR_TYPE))
                intent.putExtra(COMPANY_NAME,mcontextintent.getStringExtra(COMPANY_NAME))
                intent.putExtra(UNITID, AppUtils.intToString(orderData?.unUnitID))
                intent.putExtra(UNITNAME, orderData?.unUniName)
//                mcontext.startActivity(intent)
//                (mcontext as Activity).finish()

                if( listVistor!!.get(position).isSelected){
                    listVistor!!.get(position).isSelected=false
                    holder.cb_unit.setChecked(false)
                }else{
                    listVistor!!.get(position).isSelected=true
                    holder.cb_unit.setChecked(true)
                }

            })
        }

        override fun getItemCount(): Int {
            return listVistor?.size ?: 0
        }

        inner class MenuHolder(private val view: View) : RecyclerView.ViewHolder(view) {

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
        val i_delivery = Intent(this@UnitListActivity, Dashboard::class.java)
        startActivity(i_delivery)
        finish()
    }
}