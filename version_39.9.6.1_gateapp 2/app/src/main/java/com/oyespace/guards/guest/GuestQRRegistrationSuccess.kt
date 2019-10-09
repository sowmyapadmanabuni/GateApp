package com.oyespace.guards.guest

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.oyespace.guards.BackgroundSyncReceiver
import com.oyespace.guards.R
import com.oyespace.guards.activity.BaseKotlinActivity
import com.oyespace.guards.camtest.ImageAdapter
import com.oyespace.guards.constants.PrefKeys.LANGUAGE
import com.oyespace.guards.network.CommonDisposable
import com.oyespace.guards.network.ImageApiClient
import com.oyespace.guards.network.ImageApiInterface
import com.oyespace.guards.network.RetrofitClinet
import com.oyespace.guards.pojo.*
import com.oyespace.guards.utils.AppUtils.Companion.intToString
import com.oyespace.guards.utils.ConstantUtils
import com.oyespace.guards.utils.ConstantUtils.*
import com.oyespace.guards.utils.LocalDb
import com.oyespace.guards.utils.Prefs
import com.oyespace.guards.utils.Utils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_final_registration.*
import kotlinx.android.synthetic.main.activity_final_registration.txt_assn_name
import kotlinx.android.synthetic.main.activity_final_registration.txt_device_name
import kotlinx.android.synthetic.main.activity_final_registration.txt_gate_name
import kotlinx.android.synthetic.main.activity_name_entry.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import timber.log.Timber
import java.io.*
import java.util.*

class GuestQRRegistrationSuccess : BaseKotlinActivity(), View.OnClickListener {

    internal var list = ArrayList<String>()
    lateinit var imageAdapter: ImageAdapter
    lateinit var mBitmap: Bitmap
     var txtassnname :TextView?= null
     var txtgatename:TextView? = null
     var txtdevicename: TextView? = null
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

            R.id.button_done -> {
                buttonNext.isEnabled = false
                buttonNext.isClickable = false
                Log.d("button_done ", "StaffEntry " + FLOW_TYPE + " " + STAFF_REGISTRATION + " " + FLOW_TYPE.equals(STAFF_REGISTRATION, true))
                visitorLog()

            }

        }
    }

    var minteger = 1
    val entries: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocale(Prefs.getString(LANGUAGE, null))
        setContentView(R.layout.activity_final_registration)
        txtassnname=findViewById(R.id.txt_assn_name)
        txtgatename=findViewById(R.id.txt_gate_name)
        txtdevicename=findViewById(R.id.txt_device_name)
        txt_assn_name?.text = "Society: " + LocalDb.getAssociation()!!.asAsnName
        txt_gate_name?.text = "Gate No: " + Prefs.getString(GATE_NO, "")
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
        if (intent.getStringExtra(FLOW_TYPE).equals(VEHICLE_GUESTWITHQRCODE, true)) {
            profile_image.visibility = View.GONE
        }
//        Log.d(
//            "intentdata StaffEntry", "" + intent.getStringExtra(UNITNAME) + " " + intent.getStringExtra(UNITID)
//                    + " " + intent.getStringExtra(MOBILENUMBER) + " " + intent.getStringExtra(COUNTRYCODE) + " "
//                    + intent.getStringExtra(PERSONNAME) + " "
//                    + " " + intent.getStringExtra(FLOW_TYPE) + " "
//                    + intent.getStringExtra(VISITOR_TYPE) + " " + intent.getStringExtra(COMPANY_NAME)
//        )

        tv_name.text = intent.getStringExtra(PERSONNAME)

        val input =intent.getStringExtra(MOBILENUMBER)
        //  val countrycode = Prefs.getString(PrefKeys.COUNTRY_CODE,"")

        val number = input.replaceFirst("(\\d{2})(\\d{4})(\\d{3})(\\d+)".toRegex(), "$1 $2 $3 $4")
        tv_mobilenumber.text = number
//        tv_mobilenumber.setText(resources.getString(R.string.textmobile)+": " + intent.getStringExtra(COUNTRYCODE)
//                + "" + intent.getStringExtra(MOBILENUMBER))
        tv_for.text = resources.getString(R.string.textto) + intent.getStringExtra(UNITNAME)
        tv_totalperson.text = resources.getString(R.string.textperson)
        tv_from.text = resources.getString(R.string.textfrom) + intent.getStringExtra(COMPANY_NAME)

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
                singUp(
                    intent.getStringExtra(PERSONNAME),
                    intent.getStringExtra(COUNTRYCODE),
                    intent.getStringExtra(MOBILENUMBER)
                )

            }
        }

    }

    private fun visitorLog() {
        var memID: Int = 64
        if(!BASE_URL.contains("dev",true)){
            memID = 410
        }
        val req = CreateVisitorLogReq(
            Prefs.getInt(ASSOCIATION_ID, 0),
            0,
            intent.getStringExtra(UNITNAME),
            intent.getStringExtra(UNITID),
            intent.getStringExtra(COMPANY_NAME), intent.getStringExtra(PERSONNAME), LocalDb.getAssociation()!!.asAsnName, 0, "",
            intent.getStringExtra(COUNTRYCODE)+intent.getStringExtra(MOBILENUMBER), intToString(minteger), "", "", "",
            minteger, ConstantUtils.GUEST,SPPrdImg1, SPPrdImg2, SPPrdImg3, SPPrdImg4, SPPrdImg5
            , SPPrdImg6, SPPrdImg7, SPPrdImg8, SPPrdImg9, SPPrdImg10,"","",Prefs.getString(ConstantUtils.GATE_NO, "")
        )
        Log.d("CreateVisitorLogResp", "StaffEntry " + req.toString())
        compositeDisposable.add(RetrofitClinet.instance.createVisitorLogCall(OYE247TOKEN, req)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CommonDisposable<CreateVisitorLogResp<VLRData>>() {
                override fun onSuccessResponse(globalApiObject: CreateVisitorLogResp<VLRData>) {
                    if (globalApiObject.success == true) {

                        //  getInvitationCreate(intent.getStringExtra(UNITID).toInt(),intent.getStringExtra(PERSONNAME),"",intent.getStringExtra(COUNTRYCODE)+intent.getStringExtra(MOBILENUMBER),"","","","",getCurrentTimeLocal(),getCurrentTimeLocal(),"",true,Prefs.getInt(ASSOCIATION_ID,0),true)

                        var imgName = "PERSON" + "Association" + Prefs.getInt(ASSOCIATION_ID,0) + "NONREGULAR" + globalApiObject.data.visitorLog.vlVisLgID + ".jpg"

                        val d  =  Intent(this@GuestQRRegistrationSuccess, BackgroundSyncReceiver::class.java)
                        d.putExtra(BSR_Action, VisitorEntryFCM)
                        d.putExtra("msg", intent.getStringExtra(PERSONNAME)+" from "+intent.getStringExtra(COMPANY_NAME)+" is coming to your home"+"("+intent.getStringExtra(UNITNAME)+")")
                        d.putExtra("mobNum", intent.getStringExtra(MOBILENUMBER))
                        d.putExtra("name", intent.getStringExtra(PERSONNAME))
                        d.putExtra("nr_id", intToString(globalApiObject.data.visitorLog.vlVisLgID))
                        d.putExtra("unitname", intent.getStringExtra(UNITNAME))
                        d.putExtra(VISITOR_TYPE, GUEST)
                        d.putExtra("memType", "Owner")
                        d.putExtra(UNITID,intent.getStringExtra(UNITID))
                        d.putExtra(VISITOR_TYPE, intent.getStringExtra(VISITOR_TYPE))

//                        intent.getStringExtra("msg"),intent.getStringExtra("mobNum"),
//                        intent.getStringExtra("name"),intent.getStringExtra("nr_id"),
//                        intent.getStringExtra("unitname"),intent.getStringExtra("memType")
                        sendBroadcast(d)

//                        val intentdata = Intent(this@GuestQRRegistrationSuccess, Dashboard::class.java)
//                        startActivity(intentdata)

                        finish()


                        Log.d("CreateVisitorLogResp", "StaffEntry " + globalApiObject.data.toString())
                    } else {
                        Utils.showToast(applicationContext, globalApiObject.apiVersion)
                    }
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
        Toast.makeText(this@GuestQRRegistrationSuccess,"Hiii",Toast.LENGTH_LONG).show()

        val req = SignUpReq(
            "", "", "", "", "",
            name, isdCode, "", "", "", "",
            "", mobNum, "", "", "", "",""
        )
        Log.d("singUp", "StaffEntry " + req.toString())

        compositeDisposable.add(RetrofitClinet.instance.signUpCall(CHAMPTOKEN, req)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CommonDisposable<SignUpResp<Account>>() {
                override fun onSuccessResponse(globalApiObject: SignUpResp<Account>) {
                    if (globalApiObject.success == true) {
                        var imgName = "PERSON" + globalApiObject.data.account.acAccntID + ".jpg"
                        //uploadAccountImage(imgName, mBitmap)
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
                incidentPhoto.recycle()
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
                    Toast.makeText(applicationContext, "Uploaded Successfully", Toast.LENGTH_SHORT)
                        .show()

                } catch (ex: Exception) {
                    Log.d("uploadImage", "errr:" + ex.toString())

                    Toast.makeText(applicationContext, "Image Not Uploaded", Toast.LENGTH_SHORT).show()
                }

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
//        val d = Intent(this@GuestQRRegistrationSuccess, Dashboard::class.java)
//        startActivity(d)
        finish()
    }

    private fun getInvitationCreate(
        unUnitID: String,
        INFName: String,
        INLName: String,
        INMobile: String,
        INEmail: String,
        INVchlNo: String,
        INVisCnt: String,
        INPhoto: String,
        INSDate: String,
        INEDate: String,
        INPOfInv: String,
        INMultiEy: Boolean,
        ASAssnID: Int,
        INQRCode: Boolean
    ) {


        val dataReq = InviteCreateReq(
            unUnitID,
            INFName,
            INLName,
            INMobile,
            INEmail,
            INVchlNo,
            INVisCnt,
            INPhoto,
            INSDate,
            INEDate,
            INPOfInv,
            INMultiEy,
            ASAssnID,
            INQRCode
        )


        RetrofitClinet.instance
            .sendInviteRequest(OYE247TOKEN, dataReq)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CommonDisposable<InviteCreateRes>() {

                override fun onSuccessResponse(inviteCreateRes: InviteCreateRes) {


                }


                override fun onErrorResponse(e: Throwable) {
                    Log.d("Error WorkerList", e.toString())
                }

                override fun noNetowork() {

                }
            })
    }



}