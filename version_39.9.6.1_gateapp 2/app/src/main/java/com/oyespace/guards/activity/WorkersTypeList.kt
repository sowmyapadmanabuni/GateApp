package com.oyespace.guards.activity

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.oyespace.guards.R
import com.oyespace.guards.adapter.WorkersTypeListAdapter
import com.oyespace.guards.constants.PrefKeys.LANGUAGE
import com.oyespace.guards.utils.ConstantUtils.GATE_NO
import com.oyespace.guards.utils.LocalDb
import com.oyespace.guards.utils.Prefs
import kotlinx.android.synthetic.main.activity_worker.*
import java.util.*


class WorkersTypeList: BaseKotlinActivity() {
    val workType: ArrayList<String> = ArrayList();
    lateinit var txt_assn_name:TextView
    lateinit var txt_gate_name:TextView
    lateinit var txt_device_name: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocale(Prefs.getString(LANGUAGE, null))
        setContentView(R.layout.activity_worker)
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

        addEntries()
        rv_workerlist.setLayoutManager(
            androidx.recyclerview.widget.GridLayoutManager(
                this@WorkersTypeList,
                3
            )
        )
        rv_workerlist.adapter = WorkersTypeListAdapter( workType,this)

    }

    fun addEntries() {
        workType.add("Assistant Manager")
        workType.add("Assistant Security Officer")
        workType.add("CareTaker")
        workType.add("Cook")
        workType.add("Driver")
        workType.add("Electrician")
        workType.add("Gardener")
        workType.add("Gym Trainer")
        workType.add("Head Guard")
        workType.add("Health Instructor")
        workType.add("HouseKeeping")
        workType.add("Lady Supervisor")
        workType.add("Lady Senior Security Guard")
        workType.add("Lady Security Guard")
        workType.add("Maid")
        workType.add("Manager")
        workType.add("Nurse")
        workType.add("Others")
        workType.add("Plumber")
        workType.add("Stay at Home Maid")
        workType.add("Security Guard")
        workType.add("Security Officer")
        workType.add("Senior Security")
        workType.add("Security Supervisor")
        workType.add("Senior Supervisor")
        workType.add("Sweeper")
        workType.add("Tuition Teacher")
        // workType.add("Doctor")

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
}