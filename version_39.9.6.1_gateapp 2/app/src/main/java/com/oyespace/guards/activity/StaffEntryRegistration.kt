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
import com.oyespace.guards.BackgroundSyncReceiver
import com.oyespace.guards.R
import com.oyespace.guards.camtest.ImageAdapter
import com.oyespace.guards.constants.PrefKeys.LANGUAGE
import com.oyespace.guards.models.VisitorLog
import com.oyespace.guards.network.CommonDisposable
import com.oyespace.guards.network.ImageApiClient
import com.oyespace.guards.network.ImageApiInterface
import com.oyespace.guards.network.RetrofitClinet
import com.oyespace.guards.pojo.*
import com.oyespace.guards.repo.VisitorLogRepo
import com.oyespace.guards.utils.*
import com.oyespace.guards.utils.AppUtils.Companion.intToString
import com.oyespace.guards.utils.ConstantUtils.*
import com.oyespace.guards.utils.DateTimeUtils.getCurrentTimeLocal
import com.oyespace.guards.utils.FirebaseDBUtils.Companion.updateFirebaseColor
import com.oyespace.guards.utils.FirebaseDBUtils.Companion.updateVisitorLog
import com.squareup.picasso.Picasso
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
    var destination: File? = null
    var imgName: String? = null
    internal var list = ArrayList<String>()
    lateinit var imageAdapter: ImageAdapter
     var mBitmap: Bitmap?=null
    lateinit var txt_assn_name: TextView
    lateinit var txt_gate_name: TextView
    lateinit var txt_device_name: TextView
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

                curTime = getCurrentTimeLocal()

                if (intent.getStringExtra(VISITOR_TYPE).contains(STAFF, true)) {

                    staffVisitorLog(
                        intent.getStringExtra(UNITID),
                        intent.getStringExtra(PERSONNAME),
                        intent.getStringExtra(MOBILENUMBER),
                        intent.getStringExtra("DESIGNATION"),
                        intent.getStringExtra("WORKTYPE"),
                        intent.getIntExtra("WORKERID", 0),
                        intent.getStringExtra(UNITNAME)
                    )


                } else {

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
                  //  dialog_imageview.background = profile_image.getDrawable()

                    alertadd.setView(view)
                    alertadd.show()
                }
//                else {
//                    val alertadd = AlertDialog.Builder(this@StaffEntryRegistration)
//                    val factory = LayoutInflater.from(this@StaffEntryRegistration)
//                    val view = factory.inflate(R.layout.dialog_big_image, null)
//                    var dialog_imageview: ImageView? = null
//                    dialog_imageview = view.findViewById(R.id.dialog_imageview)
//                    Glide.with(this)
//                        .load(Uri.parse(IMAGE_BASE_URL + "Images/" + "PERSONNONREGULAR" + intent.getStringExtra(MOBILENUMBER).replace("+91", "") + ".jpg"))
//                        .placeholder(R.drawable.user_icon_black)
//                        .diskCacheStrategy(DiskCacheStrategy.NONE)
//                        .skipMemoryCache(false)
//                        .signature(StringSignature(System.currentTimeMillis().toString()))
//                        .into(dialog_imageview)
//                    alertadd.setView(view)
//                    alertadd.show()
//                }
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

        purpose=intent.getStringExtra(VISITOR_PURPOSE)
        buttonNext.text=resources.getString(R.string.textdone)

        if (intent.getStringExtra(FLOW_TYPE).equals(STAFF_REGISTRATION, true)) {

            //  tv_from.setText(resources.getString(R.string.textfrom) +intent.getStringExtra(COMPANY_NAME))
            txt_header.text = LocalDb.getAssociation()!!.asAsnName
            tv_from.text = intent.getStringExtra(FLOW_TYPE)

        } else {
            txt_header.text = LocalDb.getAssociation()!!.asAsnName
            tv_from.text =
                intent.getStringExtra(COMPANY_NAME)
        }

        tv_changefrom.setOnClickListener {


            val d = Intent(this@StaffEntryRegistration, ServiceProviderListActivity::class.java)
            d.putExtra(UNITID, intent.getStringExtra(UNITID))
            d.putExtra(UNITNAME, intent.getStringExtra(UNITNAME))
             d.putExtra(ACCOUNT_ID, intent.getStringArrayExtra(ACCOUNT_ID))
            d.putExtra(MOBILENUMBER, intent.getStringExtra(MOBILENUMBER))
            d.putExtra(COUNTRYCODE, intent.getStringExtra(COUNTRYCODE))
            d.putExtra(FLOW_TYPE, intent.getStringExtra(FLOW_TYPE))
            d.putExtra(VISITOR_TYPE, intent.getStringExtra(VISITOR_TYPE))
            d.putExtra(UNIT_ACCOUNT_ID, intent.getStringExtra(UNIT_ACCOUNT_ID))
            d.putExtra("RESIDENT_NUMBER", intent.getStringExtra("RESIDENT_NUMBER"))
            d.putExtra(UNITOCCUPANCYSTATUS, intent.getStringExtra(UNITOCCUPANCYSTATUS))
            d.putExtra(PERSONNAME, intent.getStringExtra(PERSONNAME))
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

        // tv_name.setText(resources.getString(R.string.textname)+": "+intent.getStringExtra(PERSONNAME))
        tv_name.text = intent.getStringExtra(PERSONNAME)
        // tv_mobilenumber.setText(resources.getString(R.string.textmobile)+": + "+intent.getStringExtra(COUNTRYCODE)+""+intent.getStringExtra(MOBILENUMBER))

        val input = intent.getStringExtra(COUNTRYCODE)+intent.getStringExtra(MOBILENUMBER)
        //  val countrycode = Prefs.getString(PrefKeys.COUNTRY_CODE,"")
        val number = input.replaceFirst("(\\d{4})(\\d{3})(\\d+)".toRegex(), "$1 $2 $3")
        tv_mobilenumber.text = number


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
                singUp(intent.getStringExtra(PERSONNAME), intent.getStringExtra(COUNTRYCODE), intent.getStringExtra(MOBILENUMBER).substring(3))

            }
        }

       // val url=intent.getStringExtra(PERSON_PHOTO)


        val imageAsBytes = android.util.Base64.decode(intent.getStringExtra("Base64"),android.util.Base64.DEFAULT);
        val decodedImage = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.size);
        profile_image.setImageBitmap(decodedImage)


//       val wrrw = intent.getByteArrayExtra(PERSON_PHOTO)
//       if (wrrw != null) {
////            //   imageView1.setImageBitmap(photo);
////
//           mBitmap = BitmapFactory.decodeByteArray(wrrw, 0, wrrw.size)
//           profile_image.setImageBitmap(mBitmap)
//
//           intent.getStringExtra("Base64")
////
//       }intent.getStringExtra("Base64")
//        else{
//           //                    Log.v("IIIIII","Images/" + "PERSONNONREGULAR" + getIntent().getStringExtra(MOBILENUMBER).replace("+91", "") + ".jpg");
////                    Glide.with(this)
////                            .load(Uri.parse(IMAGE_BASE_URL + "Images/" + "PERSON" + getIntent().getStringExtra(MOBILENUMBER).replace("+91", "") + ".jpg"))
////                            .placeholder(R.drawable.user_icon_black)
////                            .diskCacheStrategy(DiskCacheStrategy.NONE)
////                            .skipMemoryCache(false)
////                            .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
////                            .into(imageView1);
//           Picasso.with(this)
//               .load(IMAGE_BASE_URL + "Images/PERSON" + intent.getStringExtra(MOBILENUMBER).replace("+91", "") + ".jpg")
//               .placeholder(R.drawable.user_icon_black).error(R.drawable.user_icon_black).into(profile_image)
//       }
       //else {
//               //imageView1.setImageBitmap(photo);
//            Toast.makeText(applicationContext, "222 ", Toast.LENGTH_SHORT).show()
//
//            val image = IMAGE_BASE_URL + "Images/" + "PERSONNONREGULAR" + intent.getStringExtra(MOBILENUMBER).replace("+", "") + ".jpg"
////            Picasso.with(this)
////                .load(image)
////                .placeholder(R.drawable.user_icon_black).error(R.drawable.user_icon_black).into(profile_image)
//            Glide.with(this)
//                .load(Uri.parse(IMAGE_BASE_URL + "Images/" + url))
//                .placeholder(R.drawable.user_icon_black)
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .skipMemoryCache(false)
//                .signature(StringSignature(System.currentTimeMillis().toString()))
//                .into(profile_image)
//        }

        list = intent.getStringArrayListExtra(ITEMS_PHOTO_LIST)

        for (i in list.indices) {

            when (i) {

                0 -> SPPrdImg1 = list[i]
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
      //  val imgName = "PERSON" + "NONREGULAR" + intent.getStringExtra(MOBILENUMBER).substring(3) + ".jpg"
        val imgName = "PERSON" + intent.getStringExtra(MOBILENUMBER).substring(3) + ".jpg"

        Log.i("taaag", "cutTIme: $curTime")
        Log.e("VISITOR_LOG","::"+Unit_ACCOUNT_ID+" - "+UNUniName)

        val req = CreateVisitorLogReq(
            Prefs.getInt(ASSOCIATION_ID, 0), 0, UNUniName,
            UNUnitID, intent.getStringExtra(COMPANY_NAME), intent.getStringExtra(PERSONNAME),
            LocalDb.getAssociation()!!.asAsnName, 0, "", intent.getStringExtra(MOBILENUMBER),
            purpose.toString(), "", "", "",
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


                               //   uploadImage(imgName,personPhoto);
//                                val ddc = Intent(this@StaffEntryRegistration, BackgroundSyncReceiver::class.java)
//                                Log.d("btn_biometric", "af $imgName")
//
//                                ddc.putExtra(BSR_Action, UPLOAD_STAFF_PHOTO)
//                                ddc.putExtra("imgName", imgName)
//                                ddc.putExtra(PERSON_PHOTO,  intent.getByteArrayExtra(PERSON_PHOTO))
//                                sendBroadcast(ddc)


                              //  uploadImage(imgName, mBitmap)

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
                       // buttonNext.isEnabled = true
                       // buttonNext.isClickable = true
                        Utils.showToast(applicationContext, getString(R.string.some_wrng) + e.toString())
                        Log.d("CreateVisitorLogResp", "onErrorResponse  " + e.toString())
                    }

                    override fun noNetowork() {
                        dismissProgress()
                       // buttonNext.isEnabled = true
                       // buttonNext.isClickable = true
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
                            uploadAccountImage(imgName.toString(), mBitmap)
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

    private fun staffVisitorLog(
        unitId: String, personName: String, mobileNumb: String, desgn: String,
        workerType: String, staffID: Int, unitName: String
    ) {


        var memID: Int = 410
        if (BASE_URL.contains("dev", true)) {
            memID = 64
        } else if (BASE_URL.contains("uat", true)) {
            memID = 64
        }
//        var memID:Int=64;
//        if(!BASE_URL.contains("dev",true)){
//            memID=410;
//        }
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
        val req = CreateVisitorLogReq(
            Prefs.getInt(ASSOCIATION_ID, 0), staffID,
            unitName, unitId, desgn,
            personName, "", 0, "+", mobileNumb,
            "", "", "", "",
            1, workerType, SPPrdImg1, SPPrdImg2, SPPrdImg3, SPPrdImg4, SPPrdImg5
            ,
            SPPrdImg6,
            SPPrdImg7,
            SPPrdImg8,
            SPPrdImg9,
            SPPrdImg10,
            "",
            intent.getStringExtra("Image"),
            Prefs.getString(ConstantUtils.GATE_NO, ""),
            DateTimeUtils.getCurrentTimeLocal(),
            "", "", "", "", "", "", "", "", "", "",""
        )
        Log.d("CreateVisitorLogResp", "StaffEntry " + req.toString())

        CompositeDisposable().add(
            RetrofitClinet.instance.createVisitorLogCall(OYE247TOKEN, req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : CommonDisposable<CreateVisitorLogResp<VLRData>>() {
                    override fun onSuccessResponse(globalApiObject: CreateVisitorLogResp<VLRData>) {
                        if (globalApiObject.success == true) {

                            val id = globalApiObject.data.visitorLog.vlVisLgID
                            updateFirebaseColor(id, "#f0f0f0")

                            val ddc = Intent(this@StaffEntryRegistration, BackgroundSyncReceiver::class.java)
                            ddc.putExtra(ConstantUtils.BSR_Action, ConstantUtils.SENDFCM_toSYNC_VISITORENTRY)
                            ddc.putExtra("msg", personName + " " + desgn + " is coming to your home")
                            ddc.putExtra("mobNum", mobileNumb)
                            ddc.putExtra("name", personName)
                            ddc.putExtra("nr_id", AppUtils.intToString(globalApiObject.data.visitorLog.vlVisLgID))
                            ddc.putExtra("unitname", unitName)
                            ddc.putExtra("memType", "Owner")
                            ddc.putExtra(COMPANY_NAME, desgn)
                            this@StaffEntryRegistration.sendBroadcast(ddc)

                            Log.d("CreateVisitorLogResp", "StaffEntry " + globalApiObject.data.toString())
                        } else {
                            Log.d("CreateVisitorLogResp", "StaffEntry " + globalApiObject.toString())

                            Utils.showToast(this@StaffEntryRegistration, "Entry not Saved" + globalApiObject.toString())
                        }
                        finish()
                    }

                    override fun onErrorResponse(e: Throwable) {
                        Log.d("onErrorResponse", "StaffEntry " + e.toString())
                        buttonNext.isEnabled = true
                        buttonNext.isClickable = true
                        Utils.showToast(this@StaffEntryRegistration, "Something went wrong")
//                    dismissProgress()
                    }

                    override fun noNetowork() {
                        buttonNext.isEnabled = true
                        buttonNext.isClickable = true
                        Utils.showToast(this@StaffEntryRegistration, resources.getString(R.string.no_internet))
                    }

                    override fun onShowProgress() {
//                    showProgress()
                    }

                    override fun onDismissProgress() {
//                    dismissProgress()
                    }
                })
        )
    }

    fun uploadImage(localImgName: String, incidentPhoto: Bitmap?) {
        Log.d("uploadImage",localImgName)
        var byteArrayProfile: ByteArray?
        val mPath = Environment.getExternalStorageDirectory().toString() + "/" + localImgName + ".jpg"


        val imageFile = File(mPath)


        try {
            val outputStream = FileOutputStream(imageFile)
            val quality = 50
            if (incidentPhoto != null) {
                incidentPhoto.compress(Bitmap.CompressFormat.PNG, quality, outputStream)
            }
            outputStream.flush()
            outputStream.close()

            val bosProfile = ByteArrayOutputStream()
            if (incidentPhoto != null) {
                incidentPhoto.compress(Bitmap.CompressFormat.PNG, 50, bosProfile)
            }
            byteArrayProfile = bosProfile.toByteArray()
            val len = bosProfile.toByteArray().size
            println("AFTER COMPRESSION-===>$len")
            bosProfile.flush()
            bosProfile.close()
            if (incidentPhoto != null) {
            }
            Timber.e("uploadImage  bf", "sfas")
        } catch (ex: Exception) {

            Log.d("uploadImage ererer bf", ex.toString())
        }


        val file = File(imageFile.toString())
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val body = MultipartBody.Part.createFormData("Test", localImgName, requestFile)
        val apiService = ImageApiClient.getImageClient().create(ImageApiInterface::class.java)
        val call = apiService.updateImageProfile(body)

        call.enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: retrofit2.Response<Any>) {
                try {
                    Log.d("uploadImage111", "response:" + response.body()!!)
                    file.delete()
                    // Toast.makeText(getApplicationContext(),"Uploaded Successfully",Toast.LENGTH_SHORT).show();

                } catch (ex: Exception) {
                    Log.d("uploadImage222", "errr:" + ex.toString())

                }

            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                // Log error here since request failed
                Log.d("uploadImage", t.toString())

            }
        })


    }

    fun uploadAccountImage(localImgName: String, incidentPhoto: Bitmap?) {
        Log.d("uploadImage",localImgName)
        var byteArrayProfile: ByteArray?
        val mPath = Environment.getExternalStorageDirectory().toString() + "/" + localImgName + ".jpg"
        val imageFile = File(mPath)

        try {
            val outputStream = FileOutputStream(imageFile)
            val quality = 75
            if (incidentPhoto != null) {
                incidentPhoto.compress(Bitmap.CompressFormat.PNG, quality, outputStream)
            }
            outputStream.flush()
            outputStream.close()

            val bosProfile = ByteArrayOutputStream()
            if (incidentPhoto != null) {
                incidentPhoto.compress(Bitmap.CompressFormat.PNG, 75, bosProfile)
            }
            // bmp1.compress(Bitmap.CompressFormat.JPEG, 50, bos);
            //InputStream in = new ByteArrayInputStream(bos.toByteArray());
            byteArrayProfile = bosProfile.toByteArray()
            val len = bosProfile.toByteArray().size
            println("AFTER COMPRESSION-===>$len")
            bosProfile.flush()
            bosProfile.close()
            if (incidentPhoto != null) {
                //    incidentPhoto.recycle()
            }
            Timber.e("uploadImage  bf", "sfas")
        } catch (ex: Exception) {
            byteArrayProfile = null
            Log.d("uploadImage ererer bf", ex.toString())
        }

//        val uriTarget = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, ContentValues())
//
//        val imageFileOS: OutputStream?
//        try {
//            imageFileOS = contentResolver.openOutputStream(uriTarget!!)
//            imageFileOS!!.write(byteArrayProfile!!)
//            imageFileOS.flush()
//            imageFileOS.close()
//
//            Log.d("uploadImage Path bf", uriTarget.toString())
//        } catch (e: FileNotFoundException) {
//            // TODO Auto-generated catch block
//            e.printStackTrace()
//        } catch (e: IOException) {
//            // TODO Auto-generated catch block
//            e.printStackTrace()
//        }

        val file = File(imageFile.toString())
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val body = MultipartBody.Part.createFormData("Test", localImgName, requestFile)
        val apiService = ImageApiClient.getImageClient().create(ImageApiInterface::class.java)
        val call = apiService.updateImageProfile(body)

        call.enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: retrofit2.Response<Any>) {
                try {
                    Log.d("uploadImage", "response:" + response.body()!!)

                } catch (ex: Exception) {
                    Log.d("uploadImage", "errr:" + ex.toString())

                }

            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                // Log error here since request failed
                Log.d("uploadImage", t.toString())

            }
        })


    }

}
