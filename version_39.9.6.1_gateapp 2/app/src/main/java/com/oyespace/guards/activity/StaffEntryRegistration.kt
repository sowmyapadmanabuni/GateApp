package com.oyespace.guards.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.speech.RecognizerIntent
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.oyespace.guards.BackgroundSyncReceiver
import com.oyespace.guards.R
import com.oyespace.guards.camtest.ImageAdapter
import com.oyespace.guards.constants.PrefKeys.LANGUAGE
import com.oyespace.guards.models.VisitorLog
import com.oyespace.guards.network.CommonDisposable
import com.oyespace.guards.network.ImageApiClient
import com.oyespace.guards.network.ImageApiInterface
import com.oyespace.guards.network.RetrofitClinet
import com.oyespace.guards.ocr.CaptureImageOcr
import com.oyespace.guards.pojo.*
import com.oyespace.guards.repo.VisitorLogRepo
import com.oyespace.guards.utils.*
import com.oyespace.guards.utils.AppUtils.Companion.intToString
import com.oyespace.guards.utils.ConstantUtils.*
import com.oyespace.guards.utils.DateTimeUtils.getCurrentTimeLocal
import com.oyespace.guards.utils.FirebaseDBUtils.Companion.updateFirebaseColor
import com.oyespace.guards.utils.FirebaseDBUtils.Companion.updateVisitorLog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_final_registration.*
import kotlinx.android.synthetic.main.header_with_next.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.collections.ArrayList

class StaffEntryRegistration : BaseKotlinActivity(), View.OnClickListener {
    internal var TAKE_PHOTO_REQUEST = 1034
    private val REQUEST_CODE_SPEECH_INPUT = 100
    var destination: File? = null
    var imgName: String? = null
    internal var list = ArrayList<String>()
    lateinit var imageAdapter: ImageAdapter
     var mBitmap: Bitmap?=null
    lateinit var txt_assn_name: TextView
    lateinit var txt_gate_name: TextView
    lateinit var txt_device_name: TextView
    var vehicleNumber:String?=null
var purpose:String?=null
    var count = 0

    lateinit var curTime: String

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.buttonNext -> {
                buttonNext.isEnabled = false
                buttonNext.isClickable = false

                if (!Utils.isConnectedToInternet()) {
                    buttonNext.isEnabled = true
                    buttonNext.isClickable = true
                    Utils.showToast(applicationContext, getString(R.string.no_internet))
                    return
                }
                if(tv_vehiclenumber.text.toString().length==0){
                    vehicleNumber=""
                }
                else{
                    vehicleNumber=tv_vehiclenumber.text.toString()
                }
                curTime = getCurrentTimeLocal()

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
                    dialog_imageview!!.setBackground(profile_image!!.getDrawable())

                    alertadd.setView(view)
                    alertadd.show()
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


        purpose=intent.getStringExtra(VISITOR_PURPOSE)
        buttonNext.text=resources.getString(R.string.textdone)

        if(intent.getStringExtra(VEHICLE_NUMBER)!=null) {
            tv_vehiclenumber?.text = (intent.getStringExtra(VEHICLE_NUMBER))

      }


        if (intent.getStringExtra(FLOW_TYPE).equals(STAFF_REGISTRATION, true)) {

            txt_header.text = LocalDb.getAssociation()!!.asAsnName
            tv_from.text = intent.getStringExtra(FLOW_TYPE)

        } else {
            tv_changefrom.visibility=View.VISIBLE
            txt_header.text = LocalDb.getAssociation()!!.asAsnName
            tv_from.text =
                intent.getStringExtra(COMPANY_NAME)
        }

        iv_mike.setOnClickListener{
            Speak()
        }
        iv_scanner.setOnClickListener{
            val i_vehicle = Intent(this@StaffEntryRegistration, CaptureImageOcr::class.java)
            i_vehicle.putExtra(UNITID, intent.getStringExtra(UNITID))
            i_vehicle.putExtra(UNITNAME, intent.getStringExtra(UNITNAME))
            i_vehicle.putExtra(ACCOUNT_ID, intent.getIntExtra(ACCOUNT_ID,0))
            i_vehicle.putExtra(MOBILENUMBER, intent.getStringExtra(MOBILENUMBER))
            i_vehicle.putExtra(COUNTRYCODE, intent.getStringExtra(COUNTRYCODE))
            i_vehicle.putExtra(FLOW_TYPE, intent.getStringExtra(FLOW_TYPE))
            i_vehicle.putExtra(VISITOR_TYPE, intent.getStringExtra(VISITOR_TYPE))
            i_vehicle.putExtra(UNIT_ACCOUNT_ID, intent.getStringExtra(UNIT_ACCOUNT_ID))
            i_vehicle.putExtra("RESIDENT_NUMBER", intent.getStringExtra("RESIDENT_NUMBER"))
            i_vehicle.putExtra(UNITOCCUPANCYSTATUS, intent.getStringExtra(UNITOCCUPANCYSTATUS))
            i_vehicle.putExtra(PERSONNAME, intent.getStringExtra(PERSONNAME))
            i_vehicle.putExtra("Base64",intent.getStringExtra("Base64"))
            i_vehicle.putExtra(ITEMS_PHOTO_LIST,intent.getStringArrayListExtra(ITEMS_PHOTO_LIST))
            i_vehicle.putExtra(ConstantUtils.COMPANY_NAME, intent.getStringExtra(ConstantUtils.COMPANY_NAME))
            startActivity(i_vehicle)
            finish()
        }

        tv_changefrom.setOnClickListener {

            Prefs.putString("CLASS","StaffEntryRegistration")
            val d = Intent(this@StaffEntryRegistration, ChangeServiceProviderListActivity::class.java)
            d.putExtra(UNITID, intent.getStringExtra(UNITID))
            d.putExtra(UNITNAME, intent.getStringExtra(UNITNAME))
             d.putExtra(ACCOUNT_ID, intent.getIntExtra(ACCOUNT_ID,0))
            d.putExtra(MOBILENUMBER, intent.getStringExtra(MOBILENUMBER))
            d.putExtra(COUNTRYCODE, intent.getStringExtra(COUNTRYCODE))
            d.putExtra(FLOW_TYPE, intent.getStringExtra(FLOW_TYPE))
            d.putExtra(VISITOR_TYPE, intent.getStringExtra(VISITOR_TYPE))
            d.putExtra(UNIT_ACCOUNT_ID, intent.getStringExtra(UNIT_ACCOUNT_ID))
            d.putExtra("RESIDENT_NUMBER", intent.getStringExtra("RESIDENT_NUMBER"))
            d.putExtra(UNITOCCUPANCYSTATUS, intent.getStringExtra(UNITOCCUPANCYSTATUS))
            d.putExtra(PERSONNAME, intent.getStringExtra(PERSONNAME))
            d.putExtra("Base64",intent.getStringExtra("Base64"))
            d.putExtra(ITEMS_PHOTO_LIST,intent.getStringArrayListExtra(ITEMS_PHOTO_LIST))
            startActivity(d)
            finish()
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

        val input = intent.getStringExtra(COUNTRYCODE)+intent.getStringExtra(MOBILENUMBER)
        val number = input.replaceFirst("(\\d{4})(\\d{3})(\\d+)".toRegex(), "$1 $2 $3")
        tv_mobilenumber.text = number


        tv_for.text = resources.getString(R.string.textvisiting) + ":  " + intent.getStringExtra(UNITNAME)
        menuCount.text ="" + minteger

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
                singUp(intent.getStringExtra(PERSONNAME), intent.getStringExtra(COUNTRYCODE), intent.getStringExtra(MOBILENUMBER))

            }
        }

        val imageAsBytes = android.util.Base64.decode(intent.getStringExtra("Base64"),android.util.Base64.DEFAULT);
        val decodedImage = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.size);
        profile_image.setImageBitmap(decodedImage)


        list = intent.getStringArrayListExtra(ITEMS_PHOTO_LIST)

        for (i in list.indices) {

            when (i) {

                0 -> SPPrdImg1 =convertToBase64(list[i])
                1 -> SPPrdImg2 = convertToBase64(list[i])
                2 -> SPPrdImg3 = convertToBase64(list[i])
                3 -> SPPrdImg4 = convertToBase64(list[i])
                4 -> SPPrdImg5 = convertToBase64(list[i])
                5 -> SPPrdImg6 = convertToBase64(list[i])
                6 -> SPPrdImg7 = convertToBase64(list[i])
                7 -> SPPrdImg8 = convertToBase64(list[i])
                8 -> SPPrdImg9 = convertToBase64(list[i])
                9 -> SPPrdImg10 = convertToBase64(list[i])
                10 -> SPPrdImg11 = convertToBase64(list[i])
                11 -> SPPrdImg12 = convertToBase64(list[i])
                12 -> SPPrdImg13 = convertToBase64(list[i])
                13 -> SPPrdImg14 = convertToBase64(list[i])
                14 -> SPPrdImg15 = convertToBase64(list[i])
                15 -> SPPrdImg16 = convertToBase64(list[i])
                16 -> SPPrdImg17 = convertToBase64(list[i])
                17 -> SPPrdImg18 = convertToBase64(list[i])
                18 -> SPPrdImg19 = convertToBase64(list[i])
                19 -> SPPrdImg20 = convertToBase64(list[i])

                else -> { // Note the block
                    print("x is neither 1 nor 2")
                    Log.d("CreateVisitorLogResp", "else destination " + i)

                }
            }
            println("List Image..."+list[i])
        }

        val mLayoutManager = GridLayoutManager(applicationContext, 2)
        rv_image.layoutManager = mLayoutManager
        imageAdapter = ImageAdapter(list, this@StaffEntryRegistration, "Off")
        rv_image.adapter = imageAdapter

    }

    private fun visitorLog(UNUniName: String, UNUnitID: String, Unit_ACCOUNT_ID: String) {

        val req = CreateVisitorLogReq(
            Prefs.getInt(ASSOCIATION_ID, 0), 0, UNUniName,
            UNUnitID, intent.getStringExtra(COMPANY_NAME), intent.getStringExtra(PERSONNAME),
            LocalDb.getAssociation()!!.asAsnName, 0, "","+91"+ intent.getStringExtra(MOBILENUMBER),
            purpose.toString(), vehicleNumber!!, "", "",
            minteger, intent.getStringExtra(VISITOR_TYPE), SPPrdImg1, SPPrdImg2, SPPrdImg3, SPPrdImg4, SPPrdImg5
            , SPPrdImg6, SPPrdImg7, SPPrdImg8, SPPrdImg9, SPPrdImg10, imgName.toString(), intent.getStringExtra("Base64"), Prefs.getString(ConstantUtils.GATE_NO, ""), curTime, SPPrdImg11, SPPrdImg12, SPPrdImg13, SPPrdImg14, SPPrdImg15
            , SPPrdImg16, SPPrdImg17, SPPrdImg18, SPPrdImg19, SPPrdImg20,""
        )

        Log.d("taaag", "log request: $req")

        compositeDisposable.add(
            RetrofitClinet.instance.createVisitorLogCall(OYE247TOKEN, req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : CommonDisposable<CreateVisitorLogResp<VLRData>>() {
                    override fun onSuccessResponse(globalApiObject: CreateVisitorLogResp<VLRData>) {

                        if (globalApiObject.success) {


                            val vlid = globalApiObject.data.visitorLog.vlVisLgID
                            Log.d("taaag", "saving... $vlid for $UNUniName at entryTime: ${getCurrentTimeLocal()}")


                            count--
                            Log.v("taaag", "count $count")
                            if (count <= 0) {

                                for (i in list.indices) {
                                    val fileName = list[i].substring(list[i].lastIndexOf("/") + 1)
                                    val dir = Environment.getExternalStorageDirectory().path
                                    val file = File(dir, fileName)
                                    file.delete()
                                }

                                val dir = File(Environment.getExternalStorageDirectory().toString() + "/DCIM/myCapturedImages")
                                deleteDir(dir.absolutePath)



                                VisitorLogRepo.get_IN_VisitorLog(true, object : VisitorLogRepo.VisitorLogFetchListener {
                                    override fun onFetch(visitorLog: ArrayList<VisitorLog>?, error: String?) {

                                        val visitors = VisitorLogRepo.get_IN_VisitorsForTimeTime(curTime)
                                        if (debug) {
                                            Toast.makeText(this@StaffEntryRegistration, "no: ${visitors?.size}", Toast.LENGTH_SHORT).show()
                                        }
                                        if (visitors != null) {

                                            for (visitor in visitors) {
                                                try{

                                                    var visitorObj = JSONObject();
                                                    visitorObj.put(BSR_Action, VisitorEntryFCM)
                                                    visitorObj.put("msg", intent.getStringExtra(PERSONNAME) + " from " + intent.getStringExtra(COMPANY_NAME) + " is coming to your home" + "(" + visitor.unUniName + ")")
                                                    visitorObj.put("mobNum", intent.getStringExtra(MOBILENUMBER))
                                                    visitorObj.put("name", intent.getStringExtra(PERSONNAME))
                                                    visitorObj.put("nr_id", intToString(visitor.vlVisLgID))
                                                    visitorObj.put("unitname", visitor.unUniName)
                                                    visitorObj.put("memType", "Owner")
                                                    visitorObj.put(UNITID, visitor.unUnitID)
                                                    visitorObj.put(COMPANY_NAME, intent.getStringExtra(COMPANY_NAME))
                                                    visitorObj.put(UNIT_ACCOUNT_ID, Unit_ACCOUNT_ID)
                                                    visitorObj.put("VLVisLgID", visitor.vlVisLgID)
                                                    visitorObj.put(VISITOR_TYPE, intent.getStringExtra(VISITOR_TYPE))
                                                    visitorObj.put(UNITOCCUPANCYSTATUS,intent.getStringExtra(UNITOCCUPANCYSTATUS))

                                                    updateVisitorLog(visitor.vlVisLgID,visitor,Unit_ACCOUNT_ID,visitorObj.toString())

                                                    val d = Intent(this@StaffEntryRegistration, BackgroundSyncReceiver::class.java)
                                                    d.putExtra(BSR_Action, VisitorEntryFCM)
                                                    d.putExtra("msg", intent.getStringExtra(PERSONNAME) + " from " + intent.getStringExtra(COMPANY_NAME) + " is coming to your home" + "(" + visitor.unUniName + ")")
                                                    d.putExtra("mobNum", intent.getStringExtra(MOBILENUMBER))
                                                    d.putExtra("name", intent.getStringExtra(PERSONNAME))
                                                    d.putExtra("nr_id", intToString(visitor.vlVisLgID))
                                                    d.putExtra("unitname", visitor.unUniName)
                                                    d.putExtra("memType", "Owner")
                                                    d.putExtra(UNITID, visitor.unUnitID)
                                                    d.putExtra(COMPANY_NAME, intent.getStringExtra(COMPANY_NAME))
                                                    d.putExtra(UNIT_ACCOUNT_ID, Unit_ACCOUNT_ID)
                                                    d.putExtra("VLVisLgID", visitor.vlVisLgID)
                                                    d.putExtra(VISITOR_TYPE, intent.getStringExtra(VISITOR_TYPE))
                                                    d.putExtra(UNITOCCUPANCYSTATUS,intent.getStringExtra(UNITOCCUPANCYSTATUS))
                                                    d.putExtra("EntryTime",visitor.vlsActTm)
                                                    sendBroadcast(d)

                                                    Log.v("DELIVERY",visitor.vlVisLgID.toString()+intent.getStringExtra(VISITOR_TYPE))
                                                }catch (e:java.lang.Exception){

                                                }

                                            }
                                        }
                                        dismissProgress()
                                        finish()

                                    }
                                })


                            }

                        } else {
                            Utils.showToast(applicationContext, globalApiObject.apiVersion)
                            dismissProgress()
                            finish()
                        }
                    }

                    override fun onErrorResponse(e: Throwable) {
                        Toast.makeText(this@StaffEntryRegistration, "222", Toast.LENGTH_SHORT).show()
                        dismissProgress()
                        Utils.showToast(applicationContext, getString(R.string.some_wrng) + e.toString())
                        Log.d("CreateVisitorLogResp", "onErrorResponse  " + e.toString())
                    }

                    override fun noNetowork() {
                        dismissProgress()

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
            "", mobNum, "", "", "", "", intent.getStringExtra("Base64")
        )
        Log.d("singUp", "StaffEntry " + req.toString())

        compositeDisposable.add(
            RetrofitClinet.instance.signUpCall(CHAMPTOKEN, req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : CommonDisposable<SignUpResp<Account>>() {
                    override fun onSuccessResponse(globalApiObject: SignUpResp<Account>) {
                        if (globalApiObject.success == true) {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            when(requestCode){
                REQUEST_CODE_SPEECH_INPUT ->{
                    if (resultCode == Activity.RESULT_OK  && null!= data){
                        val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                        tv_vehiclenumber.setText(result[0].replace(" ", "").trim())

                    }
                }
            }
    }


    fun convertToBase64(imagePath: String): String {
        val bm = BitmapFactory.decodeFile(imagePath)
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val byteArrayImage = baos.toByteArray()
        return Base64.encodeToString(byteArrayImage, Base64.DEFAULT)
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


}
