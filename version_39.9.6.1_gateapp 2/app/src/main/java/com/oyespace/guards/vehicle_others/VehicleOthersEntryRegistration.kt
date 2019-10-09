package com.oyespace.guards.vehicle_others

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
import com.oyespace.guards.activity.BaseKotlinActivity
import com.oyespace.guards.constants.PrefKeys.LANGUAGE
import com.oyespace.guards.network.CommonDisposable
import com.oyespace.guards.network.ImageApiClient
import com.oyespace.guards.network.ImageApiInterface
import com.oyespace.guards.network.RetrofitClinet
import com.oyespace.guards.pojo.*
import com.oyespace.guards.utils.*
import com.oyespace.guards.utils.AppUtils.Companion.intToString
import com.oyespace.guards.utils.ConstantUtils.*
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_final_registration.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*

class VehicleOthersEntryRegistration : BaseKotlinActivity(), View.OnClickListener {

    var imgName: String? = null
    internal var list = ArrayList<String>()
    lateinit var imageAdapter: VehicleOthersImageAdapter
    // lateinit var mBitmap: Bitmap
    var mBitmap: Bitmap? = null
    lateinit var txt_assn_name: TextView
    lateinit var txt_gate_name: TextView
    lateinit var txt_device_name: TextView
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
    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.button_done -> {

                button_done.isEnabled = false
                button_done.isClickable = false

                if (intent.getStringExtra(UNITID).contains(",")) {
                    var unitname_dataList: Array<String>
                    var unitid_dataList: Array<String>
                    var unitAccountId_dataList: Array<String>
                    unitname_dataList = intent.getStringExtra(UNITNAME).split(",".toRegex())
                        .dropLastWhile({ it.isEmpty() }).toTypedArray()
                    unitid_dataList = intent.getStringExtra(UNITID).split(",".toRegex())
                        .dropLastWhile({ it.isEmpty() }).toTypedArray()
                    unitAccountId_dataList = intent.getStringExtra(UNIT_ACCOUNT_ID)
                        .split(",".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                    if (unitname_dataList.size > 0) {
                        for (i in 0 until unitname_dataList.size) {

                            showProgress()
                            visitorLog(
                                unitname_dataList.get(i).replace(" ", ""),
                                unitid_dataList.get(i).replace(" ", ""),
                                unitAccountId_dataList.get(i).replace(" ", "")
                            )
                        }
                    }
                } else {
                    showProgress()
                    visitorLog(
                        intent.getStringExtra(UNITNAME),
                        intent.getStringExtra(UNITID),
                        intent.getStringExtra(UNIT_ACCOUNT_ID)
                    ); }

                // visitorLog()
            }

            R.id.profile_image -> {
                Log.d("button_done ", "StaffEntry " + FLOW_TYPE + " " + STAFF_REGISTRATION + " " + FLOW_TYPE.equals(STAFF_REGISTRATION, true))
                val wrrw = intent.getByteArrayExtra(PERSON_PHOTO)
                if (wrrw != null) {
//            var mBitmap: Bitmap;
                    val alertadd = AlertDialog.Builder(this@VehicleOthersEntryRegistration)
                    val factory = LayoutInflater.from(this@VehicleOthersEntryRegistration)
                    val view = factory.inflate(R.layout.dialog_big_image, null)
                    var dialog_imageview: ImageView? = null
                    dialog_imageview = view.findViewById(R.id.dialog_imageview)
                    mBitmap = BitmapFactory.decodeByteArray(wrrw, 0, wrrw.size)
                    dialog_imageview.setImageBitmap(mBitmap)


                    alertadd.setView(view)
                    alertadd.show()

                } else {
                    val alertadd = AlertDialog.Builder(this@VehicleOthersEntryRegistration)
                    val factory = LayoutInflater.from(this@VehicleOthersEntryRegistration)
                    val view = factory.inflate(R.layout.dialog_big_image, null)
                    var dialog_imageview: ImageView? = null
                    dialog_imageview = view.findViewById(R.id.dialog_imageview)
                    //   mBitmap = BitmapFactory.decodeByteArray(wrrw, 0, wrrw.size)
                    dialog_imageview.background = profile_image.drawable


                    alertadd.setView(view)
                    alertadd.show()
                }
            }
        }
    }

    var minteger = 1
    val entries: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocale(Prefs.getString(LANGUAGE, null))
        setContentView(R.layout.activity_final_registration)

        txt_assn_name = findViewById(R.id.txt_assn_name)
        txt_gate_name = findViewById(R.id.txt_gate_name)
        txt_device_name = findViewById(R.id.txt_device_name)

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

//        Log.d("intentdata StaffEntry",""+intent.getStringExtra(UNITNAME)+" "+intent.getStringExtra(UNITID)
//                +" "+intent.getStringExtra(MOBILENUMBER)+" "+intent.getStringExtra(COUNTRYCODE)+" "
//                +intent.getStringExtra(PERSONNAME)+" "
//                +" "+intent.getStringExtra(FLOW_TYPE)+" "
//                +intent.getStringExtra(VISITOR_TYPE)+" "+intent.getStringExtra(COMPANY_NAME)+"..."+intent.getStringExtra(VEHICLE_NUMBER))
        txt_header.text = LocalDb.getAssociation()!!.asAsnName

        tv_name.text = resources.getString(R.string.textname) + ": " + intent.getStringExtra(PERSONNAME)
        val input = intent.getStringExtra(MOBILENUMBER)
        //val countrycode = Prefs.getString(PrefKeys.COUNTRY_CODE,"")

        val number = input.replaceFirst("(\\d{4})(\\d{3})(\\d+)".toRegex(), "$1 $2 $3")
        tv_mobilenumber.text = "+" + "91" + " " + number
        // tv_mobilenumber.setText(resources.getString(R.string.textmobile)+": + "+intent.getStringExtra(COUNTRYCODE)+""+intent.getStringExtra(MOBILENUMBER))
        // tv_for.setText(resources.getString(R.string.textto) +intent.getStringExtra(UNITNAME))
        tv_for.text = resources.getString(R.string.textvisiting) + ":  " + intent.getStringExtra(UNITNAME)

        tv_totalperson.text = resources.getString(R.string.textperson)
        // tv_from.setText(resources.getString(R.string.textfrom) +intent.getStringExtra(COMPANY_NAME))
        tv_from.text = intent.getStringExtra(COMPANY_NAME) + " " + intent.getStringExtra(FLOW_TYPE)


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
                singUp(intent.getStringExtra(PERSONNAME), intent.getStringExtra(COUNTRYCODE), intent.getStringExtra(MOBILENUMBER))

            }
        }

        val wrrw = intent.getByteArrayExtra(PERSON_PHOTO)
        if (wrrw != null) {
//            var mBitmap: Bitmap;
            mBitmap = BitmapFactory.decodeByteArray(wrrw, 0, wrrw.size)
            profile_image.setImageBitmap(mBitmap)

        } else {
            Picasso.with(this)
                .load(
                    IMAGE_BASE_URL + "Images/PERSON" + "NONREGULAR" + intent.getStringExtra(MOBILENUMBER) + ".jpg"
                )
                .placeholder(R.drawable.user_icon_black).error(R.drawable.user_icon_black)
                .into(profile_image)
        }

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
        imageAdapter = VehicleOthersImageAdapter(list, this@VehicleOthersEntryRegistration)
        rv_image.adapter = imageAdapter

    }

    private fun visitorLog(UNUniName: String, UNUnitID: String, Unit_ACCOUNT_ID: String) {
        //      imgName="PERSON"+"Association"+Prefs.getInt(ASSOCIATION_ID,0)+"NONREGULAR" +intent.getStringExtra(MOBILENUMBER) + ".jpg"

        imgName = "PERSON" + "NONREGULAR" + intent.getStringExtra(MOBILENUMBER) + ".jpg"

//        var memID:Int=64
//        if(!BASE_URL.contains("dev",true)){
//            memID=410
//        }


        var memID: Int = 410
        if (BASE_URL.contains("dev", true)) {
            memID = 64
        } else if (BASE_URL.contains("uat", true)) {
            memID = 64
        }
        val req = CreateVisitorLogReq(
            Prefs.getInt(ASSOCIATION_ID, 0), 0, UNUniName,
            UNUnitID, intent.getStringExtra(COMPANY_NAME), intent.getStringExtra(PERSONNAME),
            LocalDb.getAssociation()!!.asAsnName, 0, "", intent.getStringExtra(COUNTRYCODE) + intent.getStringExtra(MOBILENUMBER),
            intToString(minteger), intent.getStringExtra(VEHICLE_NUMBER), "", "",
            minteger, intent.getStringExtra(VISITOR_TYPE), SPPrdImg1, SPPrdImg2, SPPrdImg3, SPPrdImg4, SPPrdImg5
            , SPPrdImg6, SPPrdImg7, SPPrdImg8, SPPrdImg9, SPPrdImg10, "", imgName.toString(), Prefs.getString(ConstantUtils.GATE_NO, "")
        )
        Log.d("CreateVisitorLogResp", "StaffEntry " + req.toString())

        compositeDisposable.add(
            RetrofitClinet.instance.createVisitorLogCall(OYE247TOKEN, req)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CommonDisposable<CreateVisitorLogResp<VLRData>>() {
                override fun onSuccessResponse(globalApiObject: CreateVisitorLogResp<VLRData>) {
                    if (globalApiObject.success == true) {
                        // Utils.showToast(applicationContext, intToString(globalApiObject.data.visitorLog.vlVisLgID))

                        for (i in list.indices) {
                            val fileName = list[i].substring(list[i].lastIndexOf("/") + 1)
                            val dir = Environment.getExternalStorageDirectory().path
                            val file = File(dir, fileName)
                            file.delete()
                        }

                        deleteDir(Environment.getExternalStorageDirectory().toString() + "/DCIM/myCapturedImages")

                        val d = Intent(this@VehicleOthersEntryRegistration, BackgroundSyncReceiver::class.java)
                        d.putExtra(BSR_Action, VisitorEntryFCM)
                        d.putExtra("msg", intent.getStringExtra(PERSONNAME) + " from " + intent.getStringExtra(COMPANY_NAME) + " is coming to your home" + "(" + UNUniName + ")")
                        d.putExtra("mobNum", intent.getStringExtra(MOBILENUMBER))
                        d.putExtra("name", intent.getStringExtra(PERSONNAME))
                        d.putExtra("nr_id", intToString(globalApiObject.data.visitorLog.vlVisLgID))
                        d.putExtra("unitname", intent.getStringExtra(UNITNAME))
                        d.putExtra(UNITID, UNUnitID.toString())
                        d.putExtra("memType", "Owner")
                        d.putExtra(COMPANY_NAME, intent.getStringExtra(COMPANY_NAME))
                        d.putExtra(UNIT_ACCOUNT_ID, Unit_ACCOUNT_ID)
                        d.putExtra("VLVisLgID", globalApiObject.data.visitorLog.vlVisLgID)
                        d.putExtra(VISITOR_TYPE, intent.getStringExtra(VISITOR_TYPE))
                        sendBroadcast(d)
                        uploadImage(imgName.toString(), mBitmap)
                        Log.d("CreateVisitorLogResp", "StaffEntry " + globalApiObject.data.toString())

                    } else {
                        Utils.showToast(applicationContext, globalApiObject.apiVersion)
                    }
                    finish()
                    dismissProgress()
                }

                override fun onErrorResponse(e: Throwable) {
                    Utils.showToast(applicationContext, getString(R.string.some_wrng))
                    Log.d("CreateVisitorLogResp", "onErrorResponse  " + e.toString())

                    dismissProgress()
                }

                override fun noNetowork() {
                    Utils.showToast(applicationContext, getString(R.string.no_internet))
                }

                override fun onShowProgress() {
                    showProgress()
                }

                override fun onDismissProgress() {
                    dismissProgress()
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
                        //  var imgName="PERSON" +globalApiObject.data.account.acAccntID  + ".jpg"
                        uploadAccountImage(imgName.toString(), mBitmap)
                        Log.d("CreateVisitorLogResp", "StaffEntry " + globalApiObject.data.toString())
                    } else {
//                        Utils.showToast(applicationContext, globalApiObject.apiVersion)
                        Log.d("CreateVisitorLogResp", "globalApiObject  " + globalApiObject.data.toString())

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

    fun uploadImage(localImgName: String, incidentPhoto: Bitmap?) {
        Log.d("uploadImage", localImgName)
        var byteArrayProfile: ByteArray?
        val mPath = Environment.getExternalStorageDirectory().toString() + "/" + localImgName + ".jpg"
        val imageFile = File(mPath)

        try {
            val outputStream = FileOutputStream(imageFile)
            val quality = 50
            if (incidentPhoto != null) {
                incidentPhoto.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            }
            outputStream.flush()
            outputStream.close()

            val bosProfile = ByteArrayOutputStream()
            if (incidentPhoto != null) {
                incidentPhoto.compress(Bitmap.CompressFormat.JPEG, 50, bosProfile)
            }
            // bmp1.compress(Bitmap.CompressFormat.JPEG, 50, bos);
            //InputStream in = new ByteArrayInputStream(bos.toByteArray());
            byteArrayProfile = bosProfile.toByteArray()
            val len = bosProfile.toByteArray().size
            println("AFTER COMPRESSION-===>$len")
            bosProfile.flush()
            bosProfile.close()
            if (incidentPhoto != null) {
//                incidentPhoto.recycle()
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
                    file.delete()
                    // Toast.makeText(getApplicationContext(),"Uploaded Successfully",Toast.LENGTH_SHORT).show();

                } catch (ex: Exception) {
                    Log.d("uploadImage", "errr:" + ex.toString())

                    Toast.makeText(applicationContext, "Image Not Uploaded", Toast.LENGTH_SHORT)
                        .show()
                }

//                val d = Intent(this@VehicleOthersEntryRegistration, Dashboard::class.java)
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//                startActivity(d)
                finish()
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                // Log error here since request failed
                Log.d("uploadImage", t.toString())
                Toast.makeText(applicationContext, "Not Uploaded", Toast.LENGTH_SHORT).show()
//                finish()
            }
        })

    }

    fun uploadAccountImage(localImgName: String, incidentPhoto: Bitmap?) {
        Log.d("uploadImage", localImgName)
        var byteArrayProfile: ByteArray?
        val mPath = Environment.getExternalStorageDirectory().toString() + "/" + localImgName + ".jpg"
        val imageFile = File(mPath)

        try {
            val outputStream = FileOutputStream(imageFile)
            val quality = 50
            if (incidentPhoto != null) {
                incidentPhoto.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            }
            outputStream.flush()
            outputStream.close()

            val bosProfile = ByteArrayOutputStream()
            if (incidentPhoto != null) {
                incidentPhoto.compress(Bitmap.CompressFormat.JPEG, 50, bosProfile)
            }
            // bmp1.compress(Bitmap.CompressFormat.JPEG, 50, bos);
            //InputStream in = new ByteArrayInputStream(bos.toByteArray());
            byteArrayProfile = bosProfile.toByteArray()
            val len = bosProfile.toByteArray().size
            println("AFTER COMPRESSION-===>$len")
            bosProfile.flush()
            bosProfile.close()
            if (incidentPhoto != null) {
//                incidentPhoto.recycle()
            }
            Timber.e("uploadImage  bf", "sfas")
        } catch (ex: Exception) {
            byteArrayProfile = null
            Log.d("uploadImage ererer bf", ex.toString())
        }
//
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

    override fun onBackPressed() {
        super.onBackPressed()
//        val intent= Intent(this@VehicleOthersEntryRegistration, Dashboard::class.java)
//        startActivity(intent)
        finish()
    }


}