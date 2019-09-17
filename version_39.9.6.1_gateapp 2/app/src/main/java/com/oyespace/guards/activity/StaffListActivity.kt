package com.oyespace.guards.activity

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.speech.RecognizerIntent
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.SearchView
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import com.oyespace.guards.adapter.StaffAdapter
import com.oyespace.guards.network.CommonDisposable
import com.oyespace.guards.network.RetrofitClinet
import com.oyespace.guards.pojo.GetWorkerListbyAssnIDResp
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_staff_list.*
import com.oyespace.guards.R
import com.oyespace.guards.constants.PrefKeys
import com.oyespace.guards.pojo.WorkerDetails
import com.oyespace.guards.pojo.WorkerListbyAssnIDData
import com.oyespace.guards.utils.AppUtils.Companion.intToString
import com.oyespace.guards.utils.LocalDb
import com.oyespace.guards.utils.Prefs
import com.yarolegovich.lovelydialog.LovelyStandardDialog
import kotlinx.android.synthetic.main.activity_name_entry.*
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.oyespace.guards.DataBaseHelper
import com.oyespace.guards.models.GetWorkersResponse
import com.oyespace.guards.models.Worker
import com.oyespace.guards.models.WorkersList
import com.oyespace.guards.utils.ConstantUtils
import kotlinx.android.synthetic.main.activity_final_registration.*
import java.io.File
import java.util.*
import java.util.Locale.filter
import kotlin.collections.ArrayList


class StaffListActivity  : BaseKotlinActivity() , View.OnClickListener {

    lateinit var txt_assn_name:TextView
    lateinit var txt_gate_name:TextView
    lateinit var txt_device_name:TextView
    lateinit var tv_nodata: TextView
    private lateinit var tv: EditText
    private val REQUEST_CODE_SPEECH_INPUT = 100
    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.buttonAdd -> {
                buttonAdd.setEnabled(false)
                buttonAdd.setClickable(false)
                val intentReg = Intent(this@StaffListActivity, WorkersTypeList::class.java)
                startActivity(intentReg);
                finish()

            }

        }
    }

    //    val staff: ArrayList<String> = ArrayList();
    // private var sv_staff: SearchView? = null
    //    private var rv_staff: RecyclerView? = null
    private var arrayList: ArrayList<Worker>? = null
    //private lateinit var WorkerAdapter: StaffAdapter
    var WorkerAdapter: StaffAdapter?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocale(Prefs.getString(PrefKeys.LANGUAGE, null))
        setContentView(R.layout.activity_staff_list)
        initRealm()
        getServiceProviderList()

        tv_nodata = findViewById(R.id.tv_nodata)
        tv = findViewById<EditText>(R.id.edt_search_text1)
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
        rv_staff?.setLayoutManager(androidx.recyclerview.widget.GridLayoutManager(this@StaffListActivity, 1))

        //    sv_staff = findViewById(R.id.sv_staff);

//        sv_staff!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String): Boolean {
//                return false
//            }
//
//            override fun onQueryTextChange(query: String): Boolean {
//                //FILTER AS YOU TYPE
//                WorkerAdapter!!.getFilter().filter(query)
//                return false
//            }
//        })

       // if (::WorkerAdapter.isInitialized) {


//            tv.addTextChangedListener(object : TextWatcher {
//                override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
//                    if(WorkerAdapter!=null){
//                        WorkerAdapter!!.getFilter().filter(charSequence)
//
//                    }
//                }
//
//                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
//                    try{
//                        if(WorkerAdapter!=null){
//                            WorkerAdapter!!.getFilter().filter(charSequence)
//
//                        }
//                    }
//                    catch (e:KotlinNullPointerException){
//                        e.printStackTrace()
//                    }
//                }
//
//                override fun afterTextChanged(editable: Editable) {
//
//                }
//            });


        tv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if(WorkerAdapter!=null){
                    // WorkerAdapter!!.getFilter().filter(charSequence)

                }
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                try{
                    if(WorkerAdapter!=null){
                        // Toast.makeText(this@StaffListActivity,charSequence,Toast.LENGTH_LONG).show()
                        //  WorkerAdapter!!.getFilter().filter(charSequence)
                        WorkerAdapter!!.filter(charSequence)


                    }
                }
                catch (e:KotlinNullPointerException){

                }
            }

            override fun afterTextChanged(editable: Editable) {

            }
        });
      //  }
        btn_mic.setOnClickListener {
            Speak()
        }
    }

    private fun getServiceProviderList() {

        RetrofitClinet.instance
            .workerList("7470AD35-D51C-42AC-BC21-F45685805BBE", intToString(LocalDb.getAssociation().asAssnID))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CommonDisposable<GetWorkersResponse<WorkersList>>() {


                override fun onSuccessResponse(workerListResponse: GetWorkersResponse<WorkersList>) {
                    if (workerListResponse.data.worker != null) {
                        tv_nodata.visibility=View.INVISIBLE
                        Log.d("getServiceProviderList", ""+workerListResponse.data.worker.size)
                        if (workerListResponse.data.worker != null) {
                            //Log.d("WorkerList success", workerListResponse.data.toString())

                            val _arrayList = workerListResponse.data.worker
                            val realmDB = DataBaseHelper()
                            realmDB.saveStaffsList(_arrayList)


                            WorkerAdapter = StaffAdapter(DataBaseHelper.getStaffs(), this@StaffListActivity)
                            rv_staff!!.adapter = WorkerAdapter
                        }
//                        Collections.sort(arrayList, object : Comparator<WorkerDetails> {
//                            override fun compare(lhs: WorkerDetails, rhs: WorkerDetails): Int {
//                                return lhs.wkfName.compareTo(rhs.wkfName)
//                            }
//                        })
//
//                        LocalDb.saveStaffList(arrayList);
//


                    } else {

                        tv_nodata.visibility=View.VISIBLE
                        //rv_staff.setEmptyAdapter("No items to show!", false, 0)
//                        Toast.makeText(this@StaffListActivity, "No Data", Toast.LENGTH_LONG)
//                            .show()
//                        LovelyStandardDialog(this@StaffListActivity, LovelyStandardDialog.ButtonLayout.VERTICAL)
//                            .setTopColorRes(R.color.google_red)
//                            .setIcon(R.drawable.ic_info_black_24dp)
//                            //This will add Don't show again checkbox to the dialog. You can pass any ID as argument
//                            .setTitle("No Staff Data")
//                            .setTitleGravity(Gravity.CENTER)
//                            .setMessage("No Staff Data")
//                            .setMessageGravity(Gravity.CENTER)
//                            .setPositiveButton("Add") {
//                                val mainIntent = Intent(this@StaffListActivity, WorkersTypeList::class.java)
//                                startActivity(mainIntent)
//                            }
//
//                            .show()
                    }
                }

                override fun onErrorResponse(e: Throwable) {

                    //rv_staff.setEmptyAdapter(getString(R.string.some_wrng), false, 0)
                    Toast.makeText(this@StaffListActivity, e.toString(), Toast.LENGTH_LONG)
                        .show()
                    Log.d("Error WorkerList", e.toString())

                }

                override fun noNetowork() {
                    Toast.makeText(this@StaffListActivity, "No network call ", Toast.LENGTH_LONG)
                        .show()
                }
            })
    }

    override fun onResume() {
        super.onResume()

      //  Handler().postDelayed({
           // getServiceProviderList()
      //  }, 1000)

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
                    tv.setText(result[0] + "")

                }
            }
        }
    }


    override fun onRestart() {
        super.onRestart()


      //  clearApplicationData(this@StaffListActivity)
      //  deleteAppData()
        val intent=Intent(this@StaffListActivity, StaffListActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK );


        startActivity(intent)
        this.finish();
    }

 fun deleteAppData() {
     try {
    // clearing app data
    val packageName = getApplicationContext().getPackageName();
    val runtime = Runtime.getRuntime();
    runtime.exec("pm clear "+packageName);

} catch ( e:Exception) {
    e.printStackTrace();
} }


    fun clearApplicationData(context: Context) {
        val cache = context.cacheDir
        val appDir = File(cache.parent)
        if (appDir.exists()) {
            val children = appDir.list()
            for (s in children!!) {
                if (s != "lib") {
                   // if (s == "cache") {
                        deleteDir(File(appDir, s))
                        Log.i(
                            "EEEEEERRRRRROOOOOOORRRR",
                            "**************** File /data/data/APP_PACKAGE/$s DELETED *******************"
                        )
                   // }
                }
            }
        }
    }

    fun deleteDir(dir: File?): Boolean {
        if (dir != null && dir.isDirectory) {
            val children = dir.list()
            var i = 0
            while (i < children!!.size) {
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
                i++
            }
        }

        assert(dir != null)
        return dir!!.delete()
    }






}