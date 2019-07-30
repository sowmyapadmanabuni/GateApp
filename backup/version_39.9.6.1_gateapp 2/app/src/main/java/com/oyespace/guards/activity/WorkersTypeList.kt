package com.oyespace.guards.activity

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import com.oyespace.guards.adapter.WorkersTypeListAdapter
import kotlinx.android.synthetic.main.activity_worker.*
import com.oyespace.guards.R
import com.oyespace.guards.constants.PrefKeys.LANGUAGE
import com.oyespace.guards.utils.ConstantUtils
import com.oyespace.guards.utils.Prefs
import java.util.*
import android.R.attr.versionName
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.oyespace.guards.utils.ConstantUtils.GATE_NO
import com.oyespace.guards.utils.LocalDb

import android.util.Log
import android.widget.TextView


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
        rv_workerlist.setLayoutManager( GridLayoutManager(this@WorkersTypeList, 3))
        rv_workerlist.adapter = WorkersTypeListAdapter( workType,this)

    }

    fun addEntries() {
        workType.add("Security Guard")
        workType.add("Security Supervisor")
        workType.add("Maid")
        workType.add("Cook")
        workType.add("Driver")
        workType.add("Plumber")
        workType.add("Electrician")
        workType.add("Manager")
        workType.add("Gardener")
        workType.add("Sweeper")
        workType.add("Gym Trainer")
       // workType.add("Doctor")
        workType.add("Nurse")
        workType.add("Tuition Teacher")
        workType.add("CareTaker")
        workType.add("Stay at Home Maid")
        workType.add("Health Instructor")
        workType.add("Assistant Manager")
        workType.add("Security Officer")
        workType.add("Assistant Security Officer")
        workType.add("Senior Supervisor")
        workType.add("Head Guard")
        workType.add("Senior Security")
        workType.add("Lady Supervisor")
        workType.add("Lady Head Guard")
        workType.add("Lady Senior Security Guard")
        workType.add("Lady Security Guard")
        workType.add("Others")

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