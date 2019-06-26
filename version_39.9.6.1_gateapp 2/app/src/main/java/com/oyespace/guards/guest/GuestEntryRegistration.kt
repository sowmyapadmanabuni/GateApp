package com.oyespace.guards.guest

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.oyespace.guards.BackgroundSyncReceiver
import com.oyespace.guards.Dashboard
import com.oyespace.guards.R
import com.oyespace.guards.activity.BaseKotlinActivity
import com.oyespace.guards.camtest.ImageAdapter
import com.oyespace.guards.constants.PrefKeys
import com.oyespace.guards.constants.PrefKeys.LANGUAGE
import com.oyespace.guards.network.*
import com.oyespace.guards.pojo.*
import com.oyespace.guards.utils.AppUtils.Companion.intToString
import com.oyespace.guards.utils.ConstantUtils.*
import com.oyespace.guards.utils.DateTimeUtils.getCurrentTimeLocal
import com.oyespace.guards.utils.LocalDb
import com.oyespace.guards.utils.NumberUtils.toInteger
import com.oyespace.guards.utils.Prefs
import com.oyespace.guards.utils.Utils
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_final_registration.*
import kotlinx.android.synthetic.main.dialog_big_image.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import timber.log.Timber
import java.io.*
import java.util.*

class GuestEntryRegistration : BaseKotlinActivity() , View.OnClickListener {

    internal var list = ArrayList<String>()
    lateinit var imageAdapter: ImageAdapter
     var mBitmap: Bitmap?=null
    lateinit var txt_assn_name:TextView
    lateinit var txt_gate_name:TextView
    lateinit var txt_device_name:TextView
    var SPPrdImg1=""
    var SPPrdImg2=""
    var SPPrdImg3=""
    var SPPrdImg4=""
    var SPPrdImg5=""
    var SPPrdImg6=""
    var SPPrdImg7=""
    var SPPrdImg8=""
    var SPPrdImg9=""
    var SPPrdImg10=""

    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.button_done ->{
                Log.d("button_done ","StaffEntry "+FLOW_TYPE+" "+GUEST_REGISTRATION+" "+FLOW_TYPE.equals( GUEST_REGISTRATION,true))
                button_done.setEnabled(false)
                button_done.setClickable(false)
                visitorLog();

            }
            R.id.profile_image ->{
                Log.d("button_done ","StaffEntry "+FLOW_TYPE+" "+STAFF_REGISTRATION+" "+FLOW_TYPE.equals( STAFF_REGISTRATION,true))
                val wrrw = intent.getByteArrayExtra(PERSON_PHOTO)
                if(wrrw!=null) {
//            var mBitmap: Bitmap;
//                    val d = Intent(this@StaffEntryRegistration, ImageBigView::class.java)
//                    d.putExtra(PERSON_PHOTO, intent.getByteArrayExtra(PERSON_PHOTO))
//                    startActivity(d)


                    val alertadd = AlertDialog.Builder(this@GuestEntryRegistration)
                    val factory = LayoutInflater.from(this@GuestEntryRegistration)
                    val view = factory.inflate(R.layout.dialog_big_image, null)
                    var dialog_imageview: ImageView? = null
                    dialog_imageview = view.findViewById(R.id.dialog_imageview)
                    mBitmap = BitmapFactory.decodeByteArray(wrrw, 0, wrrw.size)
                    dialog_imageview.setImageBitmap(mBitmap)

                    alertadd.setView(view)
                    alertadd.show()

                }else{

                    val alertadd = AlertDialog.Builder(this@GuestEntryRegistration)
                    val factory = LayoutInflater.from(this@GuestEntryRegistration)
                    val view = factory.inflate(R.layout.dialog_big_image, null)
                    var dialog_imageview: ImageView? = null
                    dialog_imageview = view.findViewById(R.id.dialog_imageview)
                    //   mBitmap = BitmapFactory.decodeByteArray(wrrw, 0, wrrw.size)
                    dialog_imageview.background = profile_image.getDrawable()


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
        txt_header.text= LocalDb.getAssociation()!!.asAsnName
        tv_from.text=intent.getStringExtra(FLOW_TYPE)
        Log.d("intentdata StaffEntry",""+intent.getStringExtra(UNITNAME)+" "+intent.getStringExtra(UNITID)
                +" "+intent.getStringExtra(MOBILENUMBER)+" "+intent.getStringExtra(COUNTRYCODE)+" "
                +intent.getStringExtra(PERSONNAME)+" "
                +" "+intent.getStringExtra(FLOW_TYPE)+" "
                +intent.getStringExtra(VISITOR_TYPE)+" "+intent.getStringExtra(COMPANY_NAME));

        tv_name.setText(intent.getStringExtra(PERSONNAME))
        val input =intent.getStringExtra(MOBILENUMBER)
        val countrycode = Prefs.getString(PrefKeys.COUNTRY_CODE,"")

        val number = input.replaceFirst("(\\d{4})(\\d{3})(\\d+)".toRegex(), "$1 $2 $3")
        tv_mobilenumber.setText(getIntent().getStringExtra(COUNTRYCODE)+" "+number)
       // tv_mobilenumber.setText(resources.getString(R.string.textmobile)+": +"+intent.getStringExtra(COUNTRYCODE)+""+intent.getStringExtra(MOBILENUMBER))
       // tv_for.setText(resources.getString(R.string.textto)+intent.getStringExtra(UNITNAME))
        tv_for.setText(resources.getString(R.string.textvisiting)+":  " +intent.getStringExtra(UNITNAME))

        tv_totalperson.setText(resources.getString(R.string.textperson))
        tv_from.setText(intent.getStringExtra(COMPANY_NAME))

        menuAdd.setOnClickListener {
            minteger++
            menuCount.setText(""+minteger)
        }

        menuRemove.setOnClickListener {
            if (minteger>1) {
                minteger--
                menuCount.setText("" + minteger)

            }else{

            }
        }
        if (intent.getStringExtra(FLOW_TYPE) == GUEST_REGISTRATION) {
            tv_from.setText(resources.getString(R.string.textfrom)+intent.getStringExtra(COMPANY_NAME))
            itemLyt.setVisibility(View.VISIBLE)
        } else {
            if (intent.getIntExtra(ACCOUNT_ID, 0) == 0) {

            }
        }

        val wrrw = intent.getByteArrayExtra(PERSON_PHOTO)
        if(wrrw!=null) {
//            var mBitmap: Bitmap;
            mBitmap = BitmapFactory.decodeByteArray(wrrw, 0, wrrw.size)
            profile_image.setImageBitmap(mBitmap)

        }else{
          //  profile_image.visibility=View.GONE
            Picasso.with(this)
                .load(
                    IMAGE_BASE_URL + "Images/PERSONAssociation" + Prefs.getInt(
                        ASSOCIATION_ID,
                        0
                    ) + "NONREGULAR" + intent.getStringExtra(MOBILENUMBER) + ".jpg"
                )
                .placeholder(R.drawable.user_icon_black).error(R.drawable.user_icon_black).into(profile_image)
        }

        list=intent.getStringArrayListExtra(ITEMS_PHOTO_LIST);

        val mLayoutManager = GridLayoutManager(applicationContext, 2)
        rv_image.layoutManager = mLayoutManager
        imageAdapter = ImageAdapter(list, this@GuestEntryRegistration)
        rv_image.adapter = imageAdapter

    }

    private fun visitorLog() {
       // var imgName="PERSON"+"Association"+Prefs.getInt(ASSOCIATION_ID,0)+"NONREGULAR" +globalApiObject.data.visitorLog.vlVisLgID  + ".jpg"
        var imgName="PERSON"+"Association"+Prefs.getInt(ASSOCIATION_ID,0)+"NONREGULAR" +intent.getStringExtra(MOBILENUMBER)  + ".jpg"

//        var memID:Int=64;
//        if(!BASE_URL.contains("dev",true)){
//            memID=410;
//        }

        var memID:Int=410;
        if(BASE_URL.contains("dev",true)){
            memID=64;
        }
        else if(BASE_URL.contains("uat",true)){
            memID=64;
        }
        val req = CreateVisitorLogReq(Prefs.getInt(ASSOCIATION_ID,0), memID, 0, intent.getStringExtra(UNITNAME),
            toInteger(intent.getStringExtra(UNITID)),intent.getStringExtra(COMPANY_NAME) ,intent.getStringExtra(PERSONNAME),
            "",0,"",intent.getStringExtra(COUNTRYCODE)+intent.getStringExtra(MOBILENUMBER),
            intToString(minteger),"","","",
            minteger,intent.getStringExtra(VISITOR_TYPE),SPPrdImg1, SPPrdImg2, SPPrdImg3, SPPrdImg4, SPPrdImg5
            , SPPrdImg6, SPPrdImg7, SPPrdImg8, SPPrdImg9, SPPrdImg10,"",imgName)
        Log.d("CreateVisitorLogResp","StaffEntry "+req.toString())

        compositeDisposable.add(RetrofitClinet.instance.createVisitorLogCall(OYE247TOKEN,req)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CommonDisposable<CreateVisitorLogResp<VLRData>>() {
                override fun onSuccessResponse(globalApiObject: CreateVisitorLogResp<VLRData>) {
                    if (globalApiObject.success == true) {
                        // Utils.showToast(applicationContext, intToString(globalApiObject.data.visitorLog.vlVisLgID))
                        visitorEntryLog(globalApiObject.data.visitorLog.vlVisLgID)
                        val dd  =  Intent(this@GuestEntryRegistration, BackgroundSyncReceiver::class.java)
                        dd.putExtra(BSR_Action, VisitorEntryFCM)
                        dd.putExtra("msg", intent.getStringExtra(PERSONNAME)+"is coming to your home" )
                        //dd.putExtra("msg", intent.getStringExtra(PERSONNAME)+" from "+intent.getStringExtra(COMPANY_NAME)+" is coming to your home")
                        dd.putExtra("mobNum", intent.getStringExtra(MOBILENUMBER))
                        dd.putExtra("name", intent.getStringExtra(PERSONNAME))
                        dd.putExtra("nr_id", intToString(globalApiObject.data.visitorLog.vlVisLgID))
                        dd.putExtra("unitname", intent.getStringExtra(UNITNAME))
                        dd.putExtra("memType", "Owner")
                        dd.putExtra(COMPANY_NAME,intent.getStringExtra(COMPANY_NAME))
//                        intent.getStringExtra("msg"),intent.getStringExtra("mobNum"),
//                        intent.getStringExtra("name"),intent.getStringExtra("nr_id"),
//                        intent.getStringExtra("unitname"),intent.getStringExtra("memType")
                        sendBroadcast(dd);
                        uploadImage(imgName,mBitmap)
                        Log.d("CreateVisitorLogResp","StaffEntry "+globalApiObject.data.toString())

//                        val d = Intent(this@GuestEntryRegistration, DashBoard::class.java)
//                        startActivity(d)
                    } else {
                        Utils.showToast(applicationContext, globalApiObject.apiVersion)
                    }
                }


                override fun onErrorResponse(e: Throwable) {
                    Utils.showToast(applicationContext, getString(R.string.some_wrng))
                    Log.d("CreateVisitorLogResp","onErrorResponse  "+e.toString())

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
            }))
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
                //   incidentPhoto.recycle()
            }
            Timber.e("uploadImage  bf", "sfas")
        } catch (ex: Exception) {
            byteArrayProfile = null
            Log.d("uploadImage ererer bf", ex.toString())
        }

        val uriTarget = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, ContentValues())

        val imageFileOS: OutputStream?
        try {
            imageFileOS = contentResolver.openOutputStream(uriTarget!!)
            imageFileOS!!.write(byteArrayProfile!!)
            imageFileOS.flush()
            imageFileOS.close()

            Log.d("uploadImage Path bf", uriTarget.toString())
        } catch (e: FileNotFoundException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

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
                    //Toast.makeText(getApplicationContext(),"Uploaded Successfully",Toast.LENGTH_SHORT).show();

                } catch (ex: Exception) {
                    Log.d("uploadImage", "errr:" + ex.toString())

                    Toast.makeText(applicationContext, "Image Not Uploaded", Toast.LENGTH_SHORT).show()
                }

//                val d = Intent(this@GuestEntryRegistration, Dashboard::class.java)
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

//    override fun onBackPressed() {
//        super.onBackPressed()
//        val d = Intent(this@GuestEntryRegistration, GuestAddCarFragment::class.java)
//
//        Log.d("intentdata NameEntr","buttonNext "+getIntent().getStringExtra(UNITNAME)+" "+intent.getStringExtra(UNITID)
//                +" "+getIntent().getStringExtra(MOBILENUMBER)+" "+getIntent().getStringExtra(COUNTRYCODE)+" "+intent.getStringExtra(PERSONNAME));
//        d.putExtra(UNITID,intent.getStringExtra(UNITID) )
//        d.putExtra(UNITNAME, intent.getStringExtra(UNITNAME))
//        d.putExtra(FLOW_TYPE,intent.getStringExtra(FLOW_TYPE))
//        d.putExtra(VISITOR_TYPE,intent.getStringExtra(VISITOR_TYPE))
//        d.putExtra(COMPANY_NAME,intent.getStringExtra(COMPANY_NAME))
//        d.putExtra(MOBILENUMBER, intent.getStringExtra(MOBILENUMBER))
//        d.putExtra(COUNTRYCODE, intent.getStringExtra(COUNTRYCODE))
//        d.putExtra(PERSONNAME, intent.getStringExtra(PERSONNAME))
//        d.putExtra(ACCOUNT_ID, intent.getIntExtra(ACCOUNT_ID, 0))
//
//        startActivity(d)
//
//    }

    private fun visitorEntryLog( visitorLogID: Int) {
//        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
//        val currentDate = sdf.format(Date())
//        System.out.println(" C DATE is  "+currentDate)

        val req = VisitorEntryReq(getCurrentTimeLocal(), LocalDb.getStaffList()[0].wkWorkID, visitorLogID)
        Log.d("CreateVisitorLogResp","StaffEntry "+req.toString())

        compositeDisposable.add(RetrofitClinet.instance.visitorEntryCall(OYE247TOKEN,req)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CommonDisposable<VisitorExitResp>() {
                override fun onSuccessResponse(globalApiObject: VisitorExitResp) {
                    if (globalApiObject.success == true) {
//                        Log.d("VisitorEntryReq","StaffEntry "+globalApiObject.data.toString())
//                        finish();

                        val d = Intent(this@GuestEntryRegistration, Dashboard::class.java)
                        startActivity(d)
                        finish()

                    } else {
                        Utils.showToast(applicationContext, globalApiObject.apiVersion)
                    }
                }

                override fun onErrorResponse(e: Throwable) {
                    Utils.showToast(applicationContext, getString(R.string.some_wrng))
                    dismissProgress()
                }

                override fun noNetowork() {
                    Utils.showToast(applicationContext, getString(R.string.no_internet))
                }

                override fun onShowProgress() {
//                    showProgress()
                }

                override fun onDismissProgress() {
                    dismissProgress()
                }
            }))
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
        val d = Intent(this@GuestEntryRegistration, Dashboard::class.java)
        startActivity(d)
        finish()
    }


}