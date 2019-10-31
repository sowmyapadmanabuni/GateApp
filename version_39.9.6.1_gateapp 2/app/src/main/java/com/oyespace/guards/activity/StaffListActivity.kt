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
import com.oyespace.guards.models.Worker
import com.oyespace.guards.pojo.WorkerDetails
import com.oyespace.guards.repo.StaffRepo
import com.oyespace.guards.utils.ConstantUtils
import com.oyespace.guards.utils.LocalDb
import com.oyespace.guards.utils.Prefs
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

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {



                try {

                        if (WorkerAdapter != null) {
                            WorkerAdapter!!.applySearch(charSequence.toString())

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

    override fun onStart() {
        super.onStart()
        Log.i("taaag", "staff refresh")
        StaffRepo.getStaffList(true, object : StaffRepo.StaffFetchListener {
            override fun onFetch(staff: ArrayList<Worker>?) {

                if (staff == null || staff.isEmpty()) {
                    tv_nodata.visibility = View.VISIBLE
                } else {
                    tv_nodata.visibility = View.INVISIBLE
                    WorkerAdapter = StaffAdapter(staff, this@StaffListActivity)
                    rv_staff!!.adapter = WorkerAdapter

                    val searchString = tv.text.toString()
                    if (!searchString.isEmpty()) {
                        WorkerAdapter!!.applySearch(searchString)
                    }
                }
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