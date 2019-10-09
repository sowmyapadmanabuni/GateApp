package com.oyespace.guards.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.oyespace.guards.R
import com.oyespace.guards.adapter.StaffAdapter
import com.oyespace.guards.constants.PrefKeys
import com.oyespace.guards.database.RealmDB
import com.oyespace.guards.models.GetWorkersResponse
import com.oyespace.guards.models.WorkersList
import com.oyespace.guards.network.CommonDisposable
import com.oyespace.guards.network.RetrofitClinet
import com.oyespace.guards.pojo.WorkerDetails
import com.oyespace.guards.utils.AppUtils.Companion.intToString
import com.oyespace.guards.utils.ConstantUtils
import com.oyespace.guards.utils.LocalDb
import com.oyespace.guards.utils.Prefs
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_staff_list.*
import java.io.File
import java.util.*


class StaffListActivity : BaseKotlinActivity(), View.OnClickListener {

    lateinit var txt_assn_name: TextView
    lateinit var txt_gate_name: TextView
    lateinit var txt_device_name: TextView
    lateinit var tv_nodata: TextView
    private lateinit var tv: EditText
    private val REQUEST_CODE_SPEECH_INPUT = 100
    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.buttonAdd -> {
                Prefs.putString(ConstantUtils.TYPE, "Create")
                buttonAdd.isEnabled = false
                buttonAdd.isClickable = false
                val intentReg = Intent(this@StaffListActivity, WorkersTypeList::class.java)
                startActivity(intentReg)
                finish()

            }

        }
    }

    //    val staff: ArrayList<String> = ArrayList();
    // private var sv_staff: SearchView? = null
    //    private var rv_staff: RecyclerView? = null
    private var arrayList: ArrayList<WorkerDetails>? = null
    //private lateinit var WorkerAdapter: StaffAdapter
    var WorkerAdapter: StaffAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocale(Prefs.getString(PrefKeys.LANGUAGE, null))
        setContentView(R.layout.activity_staff_list)
        initRealm()
        getServiceProviderList()

        tv_nodata = findViewById(R.id.tv_nodata)
        tv = findViewById<EditText>(R.id.edt_search_text1)
        txt_assn_name = findViewById(R.id.txt_assn_name)
        txt_gate_name = findViewById(R.id.txt_gate_name)
        txt_device_name = findViewById(R.id.txt_device_name)

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
        rv_staff?.setLayoutManager(GridLayoutManager(this@StaffListActivity, 1))

        tv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (WorkerAdapter != null) {
                    WorkerAdapter!!.filter.filter(charSequence)

                }
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                try {
                    if (WorkerAdapter != null) {
                        WorkerAdapter!!.filter.filter(charSequence)

                    }
                } catch (e: KotlinNullPointerException) {

                }
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })
        //  }
        btn_mic.setOnClickListener {
            Speak()
        }
    }

    private fun getServiceProviderList() {

        RetrofitClinet.instance
            .workerList(
                "7470AD35-D51C-42AC-BC21-F45685805BBE",
                intToString(LocalDb.getAssociation().asAssnID)
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CommonDisposable<GetWorkersResponse<WorkersList>>() {


                override fun onSuccessResponse(workerListResponse: GetWorkersResponse<WorkersList>) {
                    if (workerListResponse.data.worker != null) {
                        tv_nodata.visibility = View.INVISIBLE
                        Log.d("getServiceProviderList", "" + workerListResponse.data.worker.size)
                        if (workerListResponse.data.worker != null) {
                            //Log.d("WorkerList success", workerListResponse.data.toString())

                            val _arrayList = workerListResponse.data.worker
                            RealmDB.saveStaffsList(_arrayList)
                            WorkerAdapter = StaffAdapter(RealmDB.getStaffs(), this@StaffListActivity)
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

                        tv_nodata.visibility = View.VISIBLE
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
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
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


    fun deleteAppData() {
        try {
            // clearing app data
            val packageName = applicationContext.packageName
            val runtime = Runtime.getRuntime()
            runtime.exec("pm clear " + packageName)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


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