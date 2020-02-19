package com.oyespace.guards.residentidcard

import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.oyespace.guards.R
import com.oyespace.guards.constants.PrefKeys
import com.oyespace.guards.ocr.CaptureImageOcr
import com.oyespace.guards.utils.ConstantUtils
import com.oyespace.guards.utils.LocalDb
import com.oyespace.guards.utils.Prefs

class ResidentOptionActivity : AppCompatActivity(), View.OnClickListener {

    var iv_torch: Button?=null
    var clickable1 = 0
    var bt_vehicleScanner:Button?=null
    var bt_qrcodescanner:Button?=null
    var bt_missedcall:Button?=null
    lateinit var txt_assn_name: TextView
    lateinit var txt_gate_name: TextView
    lateinit var txt_device_name: TextView

   var buttonNext:Button?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resident_option)

        init()

    }

    fun init(){
        txt_assn_name = findViewById(R.id.txt_assn_name)
        txt_gate_name = findViewById(R.id.txt_gate_name)
        txt_device_name = findViewById(R.id.txt_device_name)
        if (Prefs.getString(PrefKeys.MODEL_NUMBER, null).equals("Nokia 1")) {
            txt_assn_name.textSize = 5 * resources.displayMetrics.density
        }
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

        bt_vehicleScanner=findViewById(R.id.bt_vehicleScanner)
        bt_vehicleScanner!!.setOnClickListener(this)
        bt_qrcodescanner=findViewById(R.id.bt_qrcodescanner)
        bt_qrcodescanner!!.setOnClickListener(this)
        bt_missedcall=findViewById(R.id.bt_missedcall)
        bt_missedcall!!.setOnClickListener(this)
        buttonNext=findViewById(R.id.buttonNext)
        iv_torch=findViewById(R.id.iv_torch)
        iv_torch!!.setOnClickListener(this)
        buttonNext!!.visibility=View.GONE

    }

    override fun onClick(v: View?) {

        when(v!!.id){

            R.id.bt_vehicleScanner->{

                val i_staff = Intent(this@ResidentOptionActivity, CaptureImageOcr::class.java)
                startActivity(i_staff)
            }

            R.id.bt_qrcodescanner->{

                val i_staff = Intent(this@ResidentOptionActivity, ResidentIdActivity_OLD::class.java)
                startActivity(i_staff)

            }
            R.id.bt_missedcall->{
                val i_staff = Intent(this@ResidentOptionActivity, ResidentMobileNumberScreenwithOTP::class.java)
                startActivity(i_staff)
            }
            R.id.iv_torch->{
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
        }
    }
}
