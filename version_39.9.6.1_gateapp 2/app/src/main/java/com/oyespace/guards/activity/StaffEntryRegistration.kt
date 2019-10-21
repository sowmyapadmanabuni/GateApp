package com.oyespace.guards.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.database.FirebaseDatabase
import com.oyespace.guards.BackgroundSyncReceiver
import com.oyespace.guards.R
import com.oyespace.guards.camtest.ImageAdapter
import com.oyespace.guards.constants.PrefKeys.LANGUAGE
import com.oyespace.guards.models.NotificationSyncModel
import com.oyespace.guards.models.VisitorLog
import com.oyespace.guards.network.CommonDisposable
import com.oyespace.guards.network.RetrofitClinet
import com.oyespace.guards.pojo.*
import com.oyespace.guards.realm.VisitorEntryLogRealm
import com.oyespace.guards.repo.VisitorLogRepo
import com.oyespace.guards.utils.*
import com.oyespace.guards.utils.AppUtils.Companion.intToString
import com.oyespace.guards.utils.ConstantUtils.*
import com.oyespace.guards.utils.DateTimeUtils.getCurrentTimeLocal
import com.oyespace.guards.utils.NumberUtils.toInteger
import com.oyespace.guards.utils.UploadImageApi.Companion.uploadImage
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_final_registration.*
import java.io.File
import java.util.*

class StaffEntryRegistration : BaseKotlinActivity(), View.OnClickListener {
    internal var TAKE_PHOTO_REQUEST = 1034
    var destination: File? = null
    var imgName: String? = null
    internal var list = ArrayList<String>()
    lateinit var imageAdapter: ImageAdapter
    lateinit var mBitmap: Bitmap
    lateinit var txt_assn_name: TextView
    lateinit var txt_gate_name: TextView
    lateinit var txt_device_name: TextView

    var count = 0
    val visitorsList: ArrayList<VisitorLog> = arrayListOf<VisitorLog>()

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.button_done -> {
                button_done.isEnabled = false
                button_done.isClickable = false

                if (intent.getStringExtra(UNITID).contains(",")) {


                    var unitname_dataList: Array<String>
                    var unitid_dataList: Array<String>
                    var unitAccountId_dataList: Array<String>
                    unitname_dataList = intent.getStringExtra(UNITNAME).split(",".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                    unitid_dataList = intent.getStringExtra(UNITID).split(",".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                    unitAccountId_dataList = intent.getStringExtra(UNIT_ACCOUNT_ID).split(",".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                    if (unitname_dataList.size > 0) {

                        count = unitname_dataList.size
                        showProgress()

                        for (i in 0 until unitname_dataList.size) {

                            visitorLog(
                                unitname_dataList.get(i).replace(" ", ""),
                                unitid_dataList.get(i).replace(" ", ""),
                                unitAccountId_dataList.get(i).replace(" ", "")
                            )


                        }

                    }
                } else {

                    visitorLog(
                        intent.getStringExtra(UNITNAME),
                        intent.getStringExtra(UNITID),
                        intent.getStringExtra(UNIT_ACCOUNT_ID)
                    )
                }


            }

            R.id.profile_image -> {
                Log.d("button_done ", "StaffEntry " + FLOW_TYPE + " " + STAFF_REGISTRATION + " " + FLOW_TYPE.equals(STAFF_REGISTRATION, true))
                val wrrw = intent.getByteArrayExtra(PERSON_PHOTO)
                if (wrrw != null) {

                    val alertadd = AlertDialog.Builder(this@StaffEntryRegistration)
                    val factory = LayoutInflater.from(this@StaffEntryRegistration)
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

    var minteger = 1
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
    var SPPrdImg11 = ""
    var SPPrdImg12 = ""
    var SPPrdImg13 = ""
    var SPPrdImg14 = ""
    var SPPrdImg15 = ""
    var SPPrdImg16 = ""
    var SPPrdImg17 = ""
    var SPPrdImg18 = ""
    var SPPrdImg19 = ""
    var SPPrdImg20 = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocale(Prefs.getString(LANGUAGE, null))
        setContentView(R.layout.activity_final_registration)
        //launchCamera()
//        val service =  Intent(getBaseContext(), CapPhoto::class.java)
//        startService(service);

//         imgName = "Selfie" + "Association" + Prefs.getInt(
//            ASSOCIATION_ID,
//            0
//        ) + "Gantname" + Prefs.getString(ConstantUtils.GATE_NO, "") + System.currentTimeMillis() + ".jpg"

// val front_translucent =   Intent(getBaseContext(), CapPhoto::class.java)
//                front_translucent.putExtra("Front_Request", true);
//        front_translucent.putExtra("ImageName",imgName)
//               // front_translucent.putExtra("Quality_Mode", camCapture.getQuality());
//                getApplication().getApplicationContext().startService(
//                        front_translucent);


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

        // tv_name.setText(resources.getString(R.string.textname)+": "+intent.getStringExtra(PERSONNAME))
        tv_name.text = intent.getStringExtra(PERSONNAME)
        // tv_mobilenumber.setText(resources.getString(R.string.textmobile)+": + "+intent.getStringExtra(COUNTRYCODE)+""+intent.getStringExtra(MOBILENUMBER))

        val input = intent.getStringExtra(MOBILENUMBER)
        //  val countrycode = Prefs.getString(PrefKeys.COUNTRY_CODE,"")
        val number = input.replaceFirst("(\\d{4})(\\d{3})(\\d+)".toRegex(), "$1 $2 $3")
        tv_mobilenumber.text = intent.getStringExtra(COUNTRYCODE) + " " + number


        tv_for.text = resources.getString(R.string.textvisiting) + ":  " + intent.getStringExtra(UNITNAME)

        menuAdd.setOnClickListener {
            minteger++
            menuCount.text = "" + minteger

        }
        menuRemove.setOnClickListener {
            if (minteger > 1) {
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
                // Toast.makeText(this@StaffEntryRegistration,intent.getIntExtra(ACCOUNT_ID, 0).toString(),Toast.LENGTH_LONG).show()
                singUp(intent.getStringExtra(PERSONNAME), intent.getStringExtra(COUNTRYCODE), intent.getStringExtra(MOBILENUMBER))

            }
        }

        val wrrw = intent.getByteArrayExtra(PERSON_PHOTO)
        if (wrrw != null) {

            mBitmap = BitmapFactory.decodeByteArray(wrrw, 0, wrrw.size)
            profile_image.setImageBitmap(mBitmap)

        }

        list = intent.getStringArrayListExtra(ITEMS_PHOTO_LIST)

        for (i in list.indices) {

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
                10 -> SPPrdImg11 = list[i]
                11 -> SPPrdImg12 = list[i]
                12 -> SPPrdImg13 = list[i]
                13 -> SPPrdImg14 = list[i]
                14 -> SPPrdImg15 = list[i]
                15 -> SPPrdImg16 = list[i]
                16 -> SPPrdImg17 = list[i]
                17 -> SPPrdImg18 = list[i]
                18 -> SPPrdImg19 = list[i]
                19 -> SPPrdImg20 = list[i]

                else -> { // Note the block
                    print("x is neither 1 nor 2")
                    Log.d("CreateVisitorLogResp", "else destination " + i)

                }
            }
            println(list[i])
        }

        val mLayoutManager = GridLayoutManager(applicationContext, 2)
        rv_image.layoutManager = mLayoutManager
        imageAdapter = ImageAdapter(list, this@StaffEntryRegistration, "Off")
        rv_image.adapter = imageAdapter

    }


    private fun visitorLog(UNUniName: String, UNUnitID: String, Unit_ACCOUNT_ID: String) {
        val imgName = "PERSON" + "NONREGULAR" + intent.getStringExtra(MOBILENUMBER) + ".jpg"

        val req = CreateVisitorLogReq(
            Prefs.getInt(ASSOCIATION_ID, 0), 0, UNUniName,
            UNUnitID, intent.getStringExtra(COMPANY_NAME), intent.getStringExtra(PERSONNAME),
            LocalDb.getAssociation()!!.asAsnName, 0, "", intent.getStringExtra(COUNTRYCODE) + intent.getStringExtra(MOBILENUMBER),
            intToString(minteger), "", "", "",
            minteger, intent.getStringExtra(VISITOR_TYPE), SPPrdImg1, SPPrdImg2, SPPrdImg3, SPPrdImg4, SPPrdImg5
            , SPPrdImg6, SPPrdImg7, SPPrdImg8, SPPrdImg9, SPPrdImg10, imgName.toString(), imgName, Prefs.getString(ConstantUtils.GATE_NO, ""), getCurrentTimeLocal(), SPPrdImg11, SPPrdImg12, SPPrdImg13, SPPrdImg14, SPPrdImg15
            , SPPrdImg16, SPPrdImg17, SPPrdImg18, SPPrdImg19, SPPrdImg20
        )

        compositeDisposable.add(
            RetrofitClinet.instance.createVisitorLogCall(OYE247TOKEN, req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : CommonDisposable<CreateVisitorLogResp<VLRData>>() {
                    override fun onSuccessResponse(globalApiObject: CreateVisitorLogResp<VLRData>) {

                        if (globalApiObject.success) {

                            val vlid = globalApiObject.data.visitorLog.vlVisLgID
                            Log.i("taaag", "saving... $vlid")

                            val vlog = VisitorLog()
                            vlog.vlVisLgID = vlid
                            vlog.asAssnID = Prefs.getInt(ASSOCIATION_ID, 0)
                            vlog.mEMemID = globalApiObject.data.visitorLog.meMemID
                            vlog.reRgVisID = globalApiObject.data.visitorLog.vlVisLgID
                            vlog.uNUnitID = toInteger(intent.getStringExtra(UNITID))
                            vlog.vlfName = intent.getStringExtra(PERSONNAME)
                            vlog.vlMobile = intent.getStringExtra(MOBILENUMBER)
                            vlog.vlComName = intent.getStringExtra(COMPANY_NAME)
                            vlog.vlVisType = intent.getStringExtra(VISITOR_TYPE)
                            vlog.unUniName = intent.getStringExtra(UNITNAME)
                            vlog.vlVisCnt = 1
                            vlog.vlEntryT = getCurrentTimeLocal()
                            visitorsList.add(vlog)


//                            VisitorEntryLogRealm.addVisitorEntries(
//                                vlid,
//                                Prefs.getInt(ASSOCIATION_ID, 0),
//                                memID,
//                                vlid,
//                                toInteger(intent.getStringExtra(UNITID)),
//                                intent.getStringExtra(PERSONNAME),
//                                intent.getStringExtra(MOBILENUMBER),
//                                intent.getStringExtra(COMPANY_NAME),
//                                intent.getStringExtra(VISITOR_TYPE),
//                                intent.getStringExtra(UNITNAME),
//                                1,
//                                getCurrentTimeLocal(),
//                                object : VisitorEntryLogRealm.VisitorEntryListener {
//                                    override fun onEntrySave(visitorId: Int, success: Boolean) {
//
////                                        updateFirebaseColor(visitorId.toString(), "#00FF00")
//                                        count--
//                                        Log.i("taaag", "count $count")
//                                        if (count <= 0) {
//
//                                            for (i in list.indices) {
//                                                val fileName = list[i].substring(list[i].lastIndexOf("/") + 1)
//                                                val dir = Environment.getExternalStorageDirectory().path
//                                                val file = File(dir, fileName)
//                                                file.delete()
//                                            }
//
//                                            val dir = File(Environment.getExternalStorageDirectory().toString() + "/DCIM/myCapturedImages")
//                                            deleteDir(dir.absolutePath)
//
//                                            uploadImage(imgName, mBitmap)
//
//                                            val visitors = VisitorLogRepo.get_IN_VisitorsForPhone(intent.getStringExtra(MOBILENUMBER))
//                                            if (visitors != null) {
//                                                for (visitor in visitors) {
//                                                    updateFirebaseColor(visitor.vlVisLgID.toString(), "#ff00ff")
//                                                }
//                                            }
//
//                                            dismissProgress()
//                                            finish()
//                                        }
//
//                                    }
//
//                                }
//                            )

                            val d = Intent(this@StaffEntryRegistration, BackgroundSyncReceiver::class.java)
                            d.putExtra(BSR_Action, VisitorEntryFCM)
                            d.putExtra("msg", intent.getStringExtra(PERSONNAME) + " from " + intent.getStringExtra(COMPANY_NAME) + " is coming to your home" + "(" + UNUniName + ")")
                            d.putExtra("mobNum", intent.getStringExtra(MOBILENUMBER))
                            d.putExtra("name", intent.getStringExtra(PERSONNAME))
                            d.putExtra("nr_id", intToString(globalApiObject.data.visitorLog.vlVisLgID))
                            d.putExtra("unitname", UNUniName)
                            d.putExtra("memType", "Owner")
                            d.putExtra(UNITID, UNUnitID.toString())
                            d.putExtra(COMPANY_NAME, intent.getStringExtra(COMPANY_NAME))
                            d.putExtra(UNIT_ACCOUNT_ID, Unit_ACCOUNT_ID)
                            d.putExtra("VLVisLgID", vlid)
                            d.putExtra(VISITOR_TYPE, intent.getStringExtra(VISITOR_TYPE))
                            sendBroadcast(d)

                            count--
                            Log.i("taaag", "count $count")
                            if (count <= 0) {

                                VisitorEntryLogRealm.addVisitorEntries(
                                    visitorsList,
                                    object : VisitorEntryLogRealm.VisitorEntryListener {
                                        override fun onEntrySave(success: Boolean) {

                                            for (i in list.indices) {
                                                val fileName = list[i].substring(list[i].lastIndexOf("/") + 1)
                                                val dir = Environment.getExternalStorageDirectory().path
                                                val file = File(dir, fileName)
                                                file.delete()
                                            }

                                            val dir = File(Environment.getExternalStorageDirectory().toString() + "/DCIM/myCapturedImages")
                                            deleteDir(dir.absolutePath)

                                            uploadImage(imgName, mBitmap)

                                            dismissProgress()
                                            finish()

                                        }

                                    }
                                )

                            }

                        } else {
                            Utils.showToast(applicationContext, globalApiObject.apiVersion)
                            dismissProgress()
                            finish()
                        }
                    }

                    override fun onErrorResponse(e: Throwable) {
                        Utils.showToast(applicationContext, getString(R.string.some_wrng) + e.toString())
                        Log.d("CreateVisitorLogResp", "onErrorResponse  " + e.toString())
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
            "", mobNum, "", "", "", "", imgName.toString()
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
                            uploadImage(imgName.toString(), mBitmap)
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

    override fun onStop() {
        super.onStop()
        val visitors = VisitorLogRepo.get_IN_VisitorsForPhone(intent.getStringExtra(MOBILENUMBER))
        Toast.makeText(this@StaffEntryRegistration, "no: ${visitors?.size}", Toast.LENGTH_SHORT).show()
        if (visitors != null) {
            for (visitor in visitors) {
                updateFirebaseColor(visitor.vlVisLgID.toString())
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

    override fun onActivityResult(
        requestCode: Int, resultCode: Int,
        data: Intent?
    ) {
        if (resultCode == Activity.RESULT_OK
            && requestCode == TAKE_PHOTO_REQUEST
        ) {
            // processCapturedPhoto()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun updateFirebaseColor(visitorId: String, buttonColor: String = "#ffb81a") {

        Log.i("taaag", "push to firebase: " + visitorId)
        val ref = FirebaseDatabase.getInstance().getReference("NotificationSync")
        val id = ref.push().key
        val notificationSyncModel = NotificationSyncModel(visitorId, buttonColor)
        ref.child(visitorId).setValue(notificationSyncModel).addOnCompleteListener {
            //            Toast.makeText(this@StaffEntryRegistration, "DONE", Toast.LENGTH_LONG).show()
        }

    }

}
