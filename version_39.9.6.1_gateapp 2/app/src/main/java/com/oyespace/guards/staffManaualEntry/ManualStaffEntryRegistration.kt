package com.oyespace.guards.staffManaualEntry

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.oyespace.guards.BackgroundSyncReceiver
import com.oyespace.guards.CapPhoto
import com.oyespace.guards.R
import com.oyespace.guards.activity.BaseKotlinActivity
import com.oyespace.guards.camtest.ImageAdapter
import com.oyespace.guards.constants.PrefKeys.LANGUAGE
import com.oyespace.guards.models.VisitorLog
import com.oyespace.guards.network.CommonDisposable
import com.oyespace.guards.network.RetrofitClinet
import com.oyespace.guards.pojo.*
import com.oyespace.guards.utils.*
import com.oyespace.guards.utils.AppUtils.Companion.intToString
import com.oyespace.guards.utils.ConstantUtils.*
import com.oyespace.guards.utils.DateTimeUtils.getCurrentTimeLocal
import com.oyespace.guards.utils.FirebaseDBUtils.Companion.updateFirebaseColor
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_final_registration.*
import kotlinx.android.synthetic.main.header_with_next.*
import java.io.File
import java.util.*

class ManualStaffEntryRegistration : BaseKotlinActivity(), View.OnClickListener {
    var iv_torch: Button? = null
    var clickable1 = 0
    internal var TAKE_PHOTO_REQUEST = 1034
    var destination: File? = null
    var imgName: String? = null
    var token: Double? = 0.0
    internal var list = ArrayList<String>()
    lateinit var imageAdapter: ImageAdapter
    lateinit var mBitmap: Bitmap
    lateinit var txt_assn_name: TextView
    lateinit var txt_gate_name: TextView
    lateinit var txt_device_name: TextView
    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.buttonNext -> {
                buttonNext.isEnabled = false
                buttonNext.isClickable = false

                getVisitorByWorkerId(
                    Prefs.getInt(ASSOCIATION_ID, 0),
                    intent.getIntExtra(ConstantUtils.WORKER_ID, 0),
                    intent.getStringExtra(UNITID),
                    intent.getStringExtra(PERSONNAME),
                    intent.getStringExtra(MOBILENUMBER),
                    intent.getStringExtra(VISITOR_TYPE),
                    intent.getStringExtra(COMPANY_NAME),
                    intent.getIntExtra(ConstantUtils.WORKER_ID, 0),
                    intent.getStringExtra(UNITNAME)
                )
            }

            R.id.profile_image -> {
                Log.d(
                    "button_done ",
                    "StaffEntry " + FLOW_TYPE + " " + STAFF_REGISTRATION + " " + FLOW_TYPE.equals(
                        STAFF_REGISTRATION,
                        true
                    )
                )
                val wrrw = intent.getByteArrayExtra(PERSON_PHOTO)
                if (wrrw != null) {
                    val alertadd = AlertDialog.Builder(this@ManualStaffEntryRegistration)
                    val factory = LayoutInflater.from(this@ManualStaffEntryRegistration)
                    val view = factory.inflate(R.layout.dialog_big_image, null)
                    var dialog_imageview: ImageView? = null
                    dialog_imageview = view.findViewById(R.id.dialog_imageview)
                    mBitmap = BitmapFactory.decodeByteArray(wrrw, 0, wrrw.size)
                    dialog_imageview.setImageBitmap(mBitmap)

                    alertadd.setView(view)
                    alertadd.show()

                } else {

                }
            }

        }
    }

    var minteger = 0
    val entries: ArrayList<String> = ArrayList()
    var SPPrdImg1 = ""
    var SPPrdImg2 = ""
    var SPPrdImg3 = ""
    var SPPrdImg4 = ""
    var SPPrdImg5 = ""
    var SPPrdImg6 = ""
    var SPPrdImg7 = ""
    var SPPrdImg8 = ""
    var SPPrdImg9 = ""
    var SPPrdImg10 = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocale(Prefs.getString(LANGUAGE, null))
        setContentView(R.layout.activity_final_registration)

        tv_vehiclenumber.visibility = View.GONE
        iv_mike.visibility = View.GONE
        iv_scanner.visibility = View.GONE
        iv_torch = findViewById(R.id.iv_torch)
        iv_torch!!.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                val camManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager;
                var cameraId: String? = null
                cameraId = camManager.getCameraIdList()[0];
                if (clickable1 == 0) {
                    try {
                        iv_torch!!.background = resources.getDrawable(R.drawable.torch_off)
                        camManager.setTorchMode(cameraId, true);

                        clickable1 = 1
                    } catch (e: CameraAccessException) {
                        e.printStackTrace();
                    }
                } else if (clickable1 == 1) {
                    camManager.setTorchMode(cameraId, false);
                    // iv_torch!!.text = "ON"
                    iv_torch!!.background = resources.getDrawable(R.drawable.torch_on)
                    clickable1 = 0

                }
            }

        }
        buttonNext.text = resources.getString(R.string.textdone)

        token = Math.random()

        imgName = "Selfie" + "Association" + Prefs.getInt(
            ASSOCIATION_ID,
            0
        ) + "Gantname" + Prefs.getString(
            ConstantUtils.GATE_NO,
            ""
        ) + System.currentTimeMillis() + ".jpg"

        val front_translucent = Intent(baseContext, CapPhoto::class.java)
        front_translucent.putExtra("Front_Request", true)
        front_translucent.putExtra("ImageName", imgName)

        // front_translucent.putExtra("Quality_Mode", camCapture.getQuality());
        application.applicationContext.startService(
            front_translucent
        )


        if (intent.getStringExtra(FLOW_TYPE).equals(STAFF_REGISTRATION, true)) {

            //  tv_from.setText(resources.getString(R.string.textfrom) +intent.getStringExtra(COMPANY_NAME))
            txt_header.text = LocalDb.getAssociation()!!.asAsnName
            tv_from.text = intent.getStringExtra(FLOW_TYPE)

        } else {
            txt_header.text = LocalDb.getAssociation()!!.asAsnName
            tv_from.text =
                intent.getStringExtra(COMPANY_NAME) + " " + intent.getStringExtra(FLOW_TYPE)
        }

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

        tv_name.text = intent.getStringExtra(PERSONNAME)
        if (intent.getStringExtra(MOBILENUMBER) != "") {
            tv_mobilenumber.visibility = View.VISIBLE
            tv_mobilenumber.setText(
                resources.getString(R.string.textmobile) + ": " + intent.getStringExtra(
                    MOBILENUMBER
                )
            )
        } else {
            tv_mobilenumber.visibility = View.GONE
        }


        val input = intent.getStringExtra(MOBILENUMBER)
        val number = input.replaceFirst("(\\d{4})(\\d{3})(\\d+)".toRegex(), "$1 $2 $3")


        tv_for.text =
            resources.getString(R.string.textvisiting) + ":  " + intent.getStringExtra(UNITNAME)


        menuAdd.setOnClickListener {
            minteger++
            menuCount.text = "" + minteger

        }

        menuRemove.setOnClickListener {
            if (minteger >= 1) {
                minteger--
                menuCount.text = "" + minteger

            } else {

            }
        }
        if (intent.getStringExtra(FLOW_TYPE) == STAFF_REGISTRATION) {
            tv_from.text = "Designation: " + intent.getStringExtra(COMPANY_NAME)
            itemLyt.visibility = View.GONE
        } else {
            if (intent.getIntExtra(ACCOUNT_ID, 0) == 0) {
                singUp(
                    intent.getStringExtra(PERSONNAME),
                    intent.getStringExtra(COUNTRYCODE),
                    intent.getStringExtra(MOBILENUMBER)
                )

            }
        }

        val imageAsBytes = android.util.Base64.decode(intent.getStringExtra("Base64"),android.util.Base64.DEFAULT);
        val decodedImage = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.size);
        profile_image.setImageBitmap(decodedImage)

        list = intent.getStringArrayListExtra(ITEMS_PHOTO_LIST)

        for (i in list.indices) {
            Log.d("CreateVisitorLogResp", "for destination " + i + " " + list.size)

            when (i) {

                0 -> {
                    Log.d("CreateVisitorLogResp", "when destination " + i)

                    SPPrdImg1 = list[i]
                }
                1 -> SPPrdImg2 = list[i]
                2 -> SPPrdImg3 = list[i]
                3 -> SPPrdImg4 = list[i]
                4 -> SPPrdImg5 = list[i]
                5 -> SPPrdImg6 = list[i]
                6 -> SPPrdImg7 = list[i]
                7 -> SPPrdImg8 = list[i]
                8 -> SPPrdImg9 = list[i]
                9 -> SPPrdImg10 = list[i]

                else -> { // Note the block
                    print("x is neither 1 nor 2")
                    Log.d("CreateVisitorLogResp", "else destination " + i)

                }
            }
            println(list[i])
        }

        val mLayoutManager = GridLayoutManager(applicationContext, 2)
        rv_image.layoutManager = mLayoutManager
        imageAdapter = ImageAdapter(list, this@ManualStaffEntryRegistration, "Off")
        rv_image.adapter = imageAdapter

    }


    private fun visitorLog(UNUniName: String, UNUnitID: String, Unit_ACCOUNT_ID: String) {
        //  var imgName="PERSON"+"Association"+Prefs.getInt(ASSOCIATION_ID,0)+"NONREGULAR" +intent.getStringExtra(MOBILENUMBER)  + ".jpg"

        imgName = "PERSON" + "Association" + Prefs.getInt(
            ASSOCIATION_ID,
            0
        ) + "STAFF" + intent.getIntExtra(ConstantUtils.WORKER_ID, 0) + token + ".jpg"

        var memID: Int = 410
        if (BASE_URL.contains("dev", true)) {
            memID = 64
        } else if (BASE_URL.contains("uat", true)) {
            memID = 64
        }

        val req = CreateVisitorLogReq(Prefs.getInt(ASSOCIATION_ID, 0), intent.getIntExtra(WORKER_ID, 0), UNUniName, UNUnitID, intent.getStringExtra(COMPANY_NAME), intent.getStringExtra(PERSONNAME), LocalDb.getAssociation()!!.asAsnName, 0, "", intent.getStringExtra(COUNTRYCODE) + intent.getStringExtra(MOBILENUMBER), intToString(minteger), "", "", "", minteger, intent.getStringExtra(VISITOR_TYPE), SPPrdImg1, SPPrdImg2, SPPrdImg3, SPPrdImg4, SPPrdImg5, SPPrdImg6, SPPrdImg7, SPPrdImg8, SPPrdImg9, SPPrdImg10, imgName.toString(), intent.getStringExtra("Base64"), Prefs.getString(ConstantUtils.GATE_NO, ""), DateTimeUtils.getCurrentTimeLocal(), "", "", "", "", "", "", "", "", "", "", "")

        Log.d("CreateVisitorLogResp", "StaffEntry destination " + req.toString())

        compositeDisposable.add(
            RetrofitClinet.instance.createVisitorLogCall(OYE247TOKEN, req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : CommonDisposable<CreateVisitorLogResp<VLRData>>() {
                    override fun onSuccessResponse(globalApiObject: CreateVisitorLogResp<VLRData>) {
                        if (globalApiObject.success == true) {

                            val visitorLogID = globalApiObject.data.visitorLog.vlVisLgID

                            // TODO shift this realm code into ReamDB
                            Log.d("taaag", "response for id: " + globalApiObject.data.visitorLog.vlVisLgID)
                            //realm.executeTransaction {
                            realm = Realm.getDefaultInstance()
                            if (!realm.isInTransaction) {
                                realm.beginTransaction()
                            }

                            var vlog = realm.where(VisitorLog::class.java).equalTo("vlVisLgID", globalApiObject.data.visitorLog.vlVisLgID).findFirst()

                            val vlogCount = (realm.where(VisitorLog::class.java).equalTo("vlVisLgID", globalApiObject.data.visitorLog.vlVisLgID).count()).toInt()
                            if (vlogCount == 0) { vlog = realm.createObject(VisitorLog::class.java, globalApiObject.data.visitorLog.vlVisLgID) }
                            vlog!!.asAssnID = Prefs.getInt(ASSOCIATION_ID, 0)
                            vlog.mEMemID = memID
                            vlog.reRgVisID = globalApiObject.data.visitorLog.vlVisLgID
                            vlog.unUnitID = intent.getStringExtra(UNITID)
                            vlog.vlfName = intent.getStringExtra(PERSONNAME)
                            vlog.vlMobile = intent.getStringExtra(MOBILENUMBER)
                            vlog.vlComName = intent.getStringExtra(COMPANY_NAME)
                            vlog.vlVisType = intent.getStringExtra(VISITOR_TYPE)
                            vlog.unUniName = intent.getStringExtra(UNITNAME)
                            vlog.vlVisCnt = 1
                            vlog.vlEntryT = getCurrentTimeLocal()
                            realm.commitTransaction()



                            for (i in list.indices) {
                                val fileName = list[i].substring(list[i].lastIndexOf("/") + 1)
                                val dir = Environment.getExternalStorageDirectory().path
                                val file = File(dir, fileName)
                                file.delete()
                            }

                            if (intent.getStringExtra(UNITID).contains(",")) {

                                var unitname_dataList: Array<String>
                                var unitid_dataList: Array<String>

                                unitname_dataList =
                                    intent.getStringExtra(UNITNAME).split(",".toRegex())
                                        .dropLastWhile({ it.isEmpty() }).toTypedArray()
                                unitid_dataList =
                                    intent.getStringExtra(UNITID).split(",".toRegex())
                                        .dropLastWhile({ it.isEmpty() }).toTypedArray()
                                // unitAccountId_dataList=intent.getStringExtra(UNIT_ACCOUNT_ID).split(",".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                                if (unitid_dataList.size > 0) {
                                    for (i in 0 until unitid_dataList.size) {

                                         updateFirebaseColor(visitorLogID, "#f0f0f0")

                                        val d = Intent(this@ManualStaffEntryRegistration, BackgroundSyncReceiver::class.java)
                                        d.putExtra(BSR_Action, VisitorEntryFCM)
                                        d.putExtra("msg", intent.getStringExtra(PERSONNAME) + " from " + " is coming to your home" + "(" + unitname_dataList.get(i).replace(" ", "") + ")")
                                        d.putExtra("mobNum", intent.getStringExtra(MOBILENUMBER))
                                        d.putExtra("name", intent.getStringExtra(PERSONNAME))
                                        d.putExtra("nr_id", intToString(visitorLogID))
                                        d.putExtra("unitname", unitname_dataList.get(i).replace(" ", ""))
                                        d.putExtra("memType", "Owner")
                                        d.putExtra(UNITID, unitid_dataList.get(i).replace(" ", ""))
                                        d.putExtra(COMPANY_NAME, intent.getStringExtra(COMPANY_NAME))
                                        d.putExtra("VLVisLgID", visitorLogID)
                                        d.putExtra(VISITOR_TYPE, intent.getStringExtra(COMPANY_NAME))
                                        d.putExtra("EntryTime", globalApiObject.data.visitorLog.vlsActTm)
                                        sendBroadcast(d)

                                    }
                                }
                            } else {

                                updateFirebaseColor(visitorLogID, "#f0f0f0")
                                val d = Intent(this@ManualStaffEntryRegistration, BackgroundSyncReceiver::class.java)
                                d.putExtra(BSR_Action, VisitorEntryFCM)
                                d.putExtra("msg", intent.getStringExtra(PERSONNAME) + " from " + " is coming to your home" + "(" + intent.getStringExtra(UNITNAME) + ")")
                                d.putExtra("mobNum", intent.getStringExtra(MOBILENUMBER))
                                d.putExtra("name", intent.getStringExtra(PERSONNAME))
                                d.putExtra("nr_id", intToString(visitorLogID))
                                d.putExtra("unitname", intent.getStringExtra(UNITNAME))
                                d.putExtra("memType", "Owner")
                                d.putExtra(UNITID, intent.getStringExtra(UNITID))
                                d.putExtra(COMPANY_NAME, intent.getStringExtra(COMPANY_NAME))
                                d.putExtra("VLVisLgID", visitorLogID)
                                d.putExtra(VISITOR_TYPE, intent.getStringExtra(COMPANY_NAME))
                                d.putExtra("EntryTime", globalApiObject.data.visitorLog.vlsActTm)
                                sendBroadcast(d)
                            }

                            deleteDir(
                                Environment.getExternalStorageDirectory()
                                    .toString() + "/DCIM/myCapturedImages"
                            )

                            updateStaffImage(
                                intent.getStringExtra("Base64"),
                                "",
                                intent.getIntExtra(ConstantUtils.WORKER_ID, 0),
                                intent.getStringExtra(PERSONNAME)
                            )
                            Log.d(
                                "CreateVisitorLogResp",
                                "StaffEntry " + globalApiObject.data.toString()
                            )
                        } else {
                            Utils.showToast(applicationContext, globalApiObject.apiVersion)
                        }
                        finish()
                    }

                    override fun onErrorResponse(e: Throwable) {
                        Utils.showToast(
                            applicationContext,
                            getString(R.string.some_wrng) + e.toString()
                        )
                        Log.d("CreateVisitorLogResp", "onErrorResponse  " + e.toString())

                        buttonNext.isEnabled = true
                        buttonNext.isClickable = true

                        dismissProgress()
                    }

                    override fun noNetowork() {
                        Utils.showToast(applicationContext, getString(R.string.no_internet))
                    }

                    override fun onShowProgress() {

                    }

                    override fun onDismissProgress() {

                    }
                })
        )
    }

    private fun singUp(name: String, isdCode: String, mobNum: String) {

        val req = SignUpReq(
            "", "", "", "", "",
            name, isdCode, "", "", "", "",
            "", mobNum, "", "", "", "",  intent.getStringExtra("Base64")
        )
        Log.d("singUp", "StaffEntry " + req.toString())

        compositeDisposable.add(
            RetrofitClinet.instance.signUpCall(CHAMPTOKEN, req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : CommonDisposable<SignUpResp<Account>>() {
                    override fun onSuccessResponse(globalApiObject: SignUpResp<Account>) {
                        if (globalApiObject.success == true) {
                            // var imgName="PERSON" +globalApiObject.data.account.acAccntID  + ".jpg"
                            Log.d(
                                "CreateVisitorLogResp",
                                "StaffEntry " + globalApiObject.data.toString()
                            )
                        } else {
//                        Utils.showToast(applicationContext, globalApiObject.apiVersion)
                            Log.d(
                                "CreateVisitorLogResp",
                                "globalApiObject  " + globalApiObject.data.toString()
                            )

                        }
                    }

                    override fun onErrorResponse(e: Throwable) {
//                    Utils.showToast(applicationContext, getString(R.string.some_wrng))
                        Log.d("CreateVisitorLogResp", "onErrorResponse  " + e.toString())
                    }

                    override fun noNetowork() {
//                    Utils.showToast(applicationContext, getString(R.string.no_internet))
                    }

                    override fun onShowProgress() {
                    }

                    override fun onDismissProgress() {
                    }
                })
        )
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

    private fun launchCamera() {
        val values = ContentValues(1)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
        val fileUri = contentResolver
            .insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values
            )
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            val mCurrentPhotoPath = fileUri.toString()
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
            intent.putExtra(
                "android.intent.extras.CAMERA_FACING",
                android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT
            )
            intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1)
            intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true)
            intent.putExtra("android.intent.extra.quickCapture", true)
            intent.addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION
                        or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            startActivityForResult(intent, TAKE_PHOTO_REQUEST)
        }
    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int,
        data: Intent?
    ) {
        if (resultCode == Activity.RESULT_OK
            && requestCode == TAKE_PHOTO_REQUEST
        ) {

        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }


    fun getVisitorByWorkerId(
        assnID: Int,
        workerID: Int,
        unitId: String,
        personName: String,
        mobileNumb: String,
        desgn: String,
        workerType: String,
        staffID: Int,
        unitName: String
    ) {

        RetrofitClinet.instance.getVisitorByWorkerId(OYE247TOKEN, workerID, assnID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CommonDisposable<getVisitorDataByWorker>() {

                override fun onSuccessResponse(getdata: getVisitorDataByWorker) {

                    if (getdata.success == true) {


                        if (getdata.data.visitorLog.vlMobile.equals(
                                intent.getStringExtra(
                                    COUNTRYCODE
                                ) + intent.getStringExtra(MOBILENUMBER)
                            )
                        ) {
                            Utils.showToast(this@ManualStaffEntryRegistration, "Duplicate Entry not allowed")
                        } else {
                            visitorLog(intent.getStringExtra(UNITNAME), intent.getStringExtra(UNITID), intent.getStringExtra(UNIT_ACCOUNT_ID))
                        }
                    }
                }

                override fun onErrorResponse(e: Throwable) {
                    visitorLog(intent.getStringExtra(UNITNAME), intent.getStringExtra(UNITID), intent.getStringExtra(UNIT_ACCOUNT_ID))
                }

                override fun noNetowork() {
                    Toast.makeText(this@ManualStaffEntryRegistration, "No network call ", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun updateStaffImage(
        WKEntryImg: String,
        WKEntryGPS: String,
        WKWorkID: Int,
        WKFName: String
    ) {


        val req = SendStaffImageReq(WKEntryImg, WKEntryGPS, WKWorkID)

        compositeDisposable.add(
            RetrofitClinet.instance.sendStaffImageUpdate(ConstantUtils.OYE247TOKEN, req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : CommonDisposable<SendStaffImageRes>() {
                    override fun onSuccessResponse(globalApiObject: SendStaffImageRes) {

                    }

                    override fun onErrorResponse(e: Throwable) {

                        Log.d("CreateVisitorLogResp", "onErrorResponse  " + e.toString())
                    }

                    override fun noNetowork() {
                    }

                    override fun onShowProgress() {
                    }

                    override fun onDismissProgress() {
                    }
                })
        )
    }

}
