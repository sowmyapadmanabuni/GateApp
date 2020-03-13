package com.oyespace.guards.staff

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.oyespace.guards.R
import com.oyespace.guards.activity.BaseKotlinActivity
import com.oyespace.guards.adapter.WorkersTypeListAdapter
import com.oyespace.guards.constants.PrefKeys.LANGUAGE
import com.oyespace.guards.network.CommonDisposable
import com.oyespace.guards.network.RetrofitClinet
import com.oyespace.guards.pojo.WorkerType
import com.oyespace.guards.pojo.WorkerTypes
import com.oyespace.guards.utils.ConstantUtils
import com.oyespace.guards.utils.ConstantUtils.FLOW_TYPE
import com.oyespace.guards.utils.ConstantUtils.GATE_NO
import com.oyespace.guards.utils.LocalDb
import com.oyespace.guards.utils.Prefs
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_worker.*
import kotlinx.android.synthetic.main.header_with_next.*
import java.util.*


class WorkersTypeList: BaseKotlinActivity() {
    val workType: ArrayList<String> = ArrayList()
    lateinit var txt_assn_name:TextView
    lateinit var txt_gate_name:TextView
    lateinit var txt_device_name: TextView
    var iv_torch: Button?=null

    var clickable1 = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocale(Prefs.getString(LANGUAGE, null))
        setContentView(R.layout.activity_worker)

        buttonNext.visibility=View.GONE
        getWorkerType()
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

       // addEntries()
        rv_workerlist.setLayoutManager(
            androidx.recyclerview.widget.GridLayoutManager(
                this@WorkersTypeList,
                3
            )
        )


    }

//    fun addEntries() {
//        workType.add("Assistant Manager")
//        workType.add("Assistant Security Officer")
//        workType.add("CareTaker")
//        workType.add("Cook")
//        workType.add("Driver")
//        workType.add("Electrician")
//        workType.add("Gardener")
//        workType.add("Gym Trainer")
//        workType.add("Head Guard")
//        workType.add("Health Instructor")
//        workType.add("HouseKeeping")
//        workType.add("Lady Supervisor")
//        workType.add("Lady Senior Security Guard")
//        workType.add("Lady Security Guard")
//        workType.add("Maid")
//        workType.add("Manager")
//        workType.add("Nurse")
//        workType.add("Others")
//        workType.add("Plumber")
//        workType.add("Stay at Home Maid")
//        workType.add("Security Guard")
//        workType.add("Security Officer")
//        workType.add("Senior Security")
//        workType.add("Security Supervisor")
//        workType.add("Senior Supervisor")
//        workType.add("Sweeper")
//        workType.add("Tuition Teacher")
//        // workType.add("Doctor")
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

    fun getWorkerType() {
        RetrofitClinet.instance.getWorkerTypes(
                ConstantUtils.OYE247TOKEN)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CommonDisposable<WorkerTypes<ArrayList<WorkerType>>>() {

                override fun onSuccessResponse(getdata: WorkerTypes<ArrayList<WorkerType>>) {

                    rv_workerlist.adapter = WorkersTypeListAdapter(getdata.data.workerTypes, this@WorkersTypeList, intent.getStringExtra(FLOW_TYPE))

                }

                override fun onErrorResponse(e: Throwable) {
                }

                override fun noNetowork() {
                    Toast.makeText(
                        this@WorkersTypeList,
                        "No network call ",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })

    }

}