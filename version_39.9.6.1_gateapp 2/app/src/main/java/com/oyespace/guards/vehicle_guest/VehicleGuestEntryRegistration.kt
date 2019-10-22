package com.oyespace.guards.vehicle_guest

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
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.oyespace.guards.BackgroundSyncReceiver
import com.oyespace.guards.R
import com.oyespace.guards.activity.BaseKotlinActivity
import com.oyespace.guards.camtest.ImageAdapter
import com.oyespace.guards.constants.PrefKeys.LANGUAGE
import com.oyespace.guards.network.CommonDisposable
import com.oyespace.guards.network.RetrofitClinet
import com.oyespace.guards.pojo.*
import com.oyespace.guards.utils.*
import com.oyespace.guards.utils.AppUtils.Companion.intToString
import com.oyespace.guards.utils.ConstantUtils.*
import com.oyespace.guards.utils.DateTimeUtils.getCurrentTimeLocal
import com.oyespace.guards.utils.UploadImageApi.Companion.uploadImage
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_final_registration.*
import java.util.*

class VehicleGuestEntryRegistration : BaseKotlinActivity(), View.OnClickListener {
    var imageName: String? = null
    internal var list = ArrayList<String>()
    lateinit var imageAdapter: ImageAdapter
    var mBitmap: Bitmap? = null
    lateinit var txt_assn_name: TextView
    lateinit var txt_gate_name: TextView
    lateinit var txt_device_name: TextView
    var imgName: String? = null

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
                Log.d("button_done ", "StaffEntry " + FLOW_TYPE + " " + STAFF_REGISTRATION + " " + FLOW_TYPE.equals(STAFF_REGISTRATION, true))

                if (intent.getStringExtra(FLOW_TYPE).equals(STAFF_REGISTRATION, true)) {

                } else {
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
                }

            }

            R.id.profile_image -> {
                Log.d("button_done ", "StaffEntry " + FLOW_TYPE + " " + STAFF_REGISTRATION + " " + FLOW_TYPE.equals(STAFF_REGISTRATION, true))
                val wrrw = intent.getByteArrayExtra(PERSON_PHOTO)
                if (wrrw != null) {
//            var mBitmap: Bitmap;
//                    val d = Intent(this@VehicleGuestEntryRegistration, ImageBigView::class.java)
//                    d.putExtra(PERSON_PHOTO, intent.getByteArrayExtra(PERSON_PHOTO))
//                    startActivity(d)

                    val alertadd = AlertDialog.Builder(this@VehicleGuestEntryRegistration)
                    val factory = LayoutInflater.from(this@VehicleGuestEntryRegistration)
                    val view = factory.inflate(R.layout.dialog_big_image, null)
                    var dialog_imageview: ImageView? = null
                    dialog_imageview = view.findViewById(R.id.dialog_imageview)
                    mBitmap = BitmapFactory.decodeByteArray(wrrw, 0, wrrw.size)
                    dialog_imageview.setImageBitmap(mBitmap)
                    alertadd.setView(view)
                    alertadd.show()

                } else {
                    val alertadd = AlertDialog.Builder(this@VehicleGuestEntryRegistration)
                    val factory = LayoutInflater.from(this@VehicleGuestEntryRegistration)
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
//                +intent.getStringExtra(VISITOR_TYPE)+" "+intent.getStringExtra(COMPANY_NAME));
        txt_header.text = LocalDb.getAssociation()!!.asAsnName

        tv_name.text = intent.getStringExtra(PERSONNAME)
        val input = intent.getStringExtra(MOBILENUMBER)
        // val countrycode = Prefs.getString(PrefKeys.COUNTRY_CODE,"")

        val number = input.replaceFirst("(\\d{4})(\\d{3})(\\d+)".toRegex(), "$1 $2 $3")
        tv_mobilenumber.text = intent.getStringExtra(COUNTRYCODE) + " " + number
        // tv_mobilenumber.setText(resources.getString(R.string.textmobile)+": + " +intent.getStringExtra(COUNTRYCODE)+""+intent.getStringExtra(MOBILENUMBER))
        tv_for.text =
            resources.getString(R.string.textvisiting) + ":  " + intent.getStringExtra(UNITNAME)
        tv_totalperson.text = resources.getString(R.string.textperson)
        tv_from.text = intent.getStringExtra(COMPANY_NAME)

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
            tv_from.text = resources.getString(R.string.textdesignation) + intent.getStringExtra(COMPANY_NAME)
            itemLyt.visibility = View.GONE
        } else {
            if (intent.getIntExtra(ACCOUNT_ID, 0) == 0) {
                singUp(intent.getStringExtra(PERSONNAME), intent.getStringExtra(COUNTRYCODE), intent.getStringExtra(MOBILENUMBER))

            }
        }

        val wrrw = intent.getByteArrayExtra(PERSON_PHOTO)
        if (wrrw != null) {
//            var mBitmap: Bitmap;
            imageName = "PERSON" + "NONREGULAR" + intent.getStringExtra(MOBILENUMBER) + ".jpg"
            mBitmap = BitmapFactory.decodeByteArray(wrrw, 0, wrrw.size)
            profile_image.setImageBitmap(mBitmap)

        } else {

            imageName = ""
            //  profile_image.visibility=View.GONE
//            Picasso.with(this)
//                .load(
//                    IMAGE_BASE_URL + "Images/PERSON" + "NONREGULAR" + intent.getStringExtra(MOBILENUMBER) + ".jpg"
//                )
//                .placeholder(R.drawable.user_icon_black).error(R.drawable.user_icon_black).into(profile_image)
        }

        list = intent.getStringArrayListExtra(ITEMS_PHOTO_LIST)

        val mLayoutManager = GridLayoutManager(applicationContext, 2)
        rv_image.layoutManager = mLayoutManager
        imageAdapter = ImageAdapter(list, this@VehicleGuestEntryRegistration, "Off")
        rv_image.adapter = imageAdapter

    }

    private fun visitorLog(UNUniName: String, UNUnitID: String, Unit_ACCOUNT_ID: String) {

        val req = CreateVisitorLogReq(Prefs.getInt(ASSOCIATION_ID,0), 0, UNUniName,
            UNUnitID,intent.getStringExtra(COMPANY_NAME) ,intent.getStringExtra(PERSONNAME),
            LocalDb.getAssociation()!!.asAsnName,0,"",intent.getStringExtra(COUNTRYCODE)+intent.getStringExtra(MOBILENUMBER),
            intToString(minteger),"","","",
            minteger,ConstantUtils.GUEST,SPPrdImg1, SPPrdImg2, SPPrdImg3, SPPrdImg4, SPPrdImg5
            ,
            SPPrdImg6,
            SPPrdImg7,
            SPPrdImg8,
            SPPrdImg9,
            SPPrdImg10,
            "",
            imageName.toString(),
            Prefs.getString(ConstantUtils.GATE_NO, ""), DateTimeUtils.getCurrentTimeLocal(),"","","","","","","","","",""
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
                            getInvitationCreate(
                                UNUnitID,
                                intent.getStringExtra(PERSONNAME),
                                "",
                                intent.getStringExtra(COUNTRYCODE) + intent.getStringExtra(MOBILENUMBER),
                                "",
                                "",
                                "",
                                imageName.toString(),
                                getCurrentTimeLocal(),
                                getCurrentTimeLocal(),
                                "",
                                false,
                                Prefs.getInt(ASSOCIATION_ID, 0),
                                false
                            )

                            AppUtils.updateFirebaseColor(globalApiObject.data.visitorLog.vlVisLgID)

                            val d = Intent(this@VehicleGuestEntryRegistration, BackgroundSyncReceiver::class.java)
                            d.putExtra(BSR_Action, VisitorEntryFCM)
                            d.putExtra("msg", intent.getStringExtra(PERSONNAME) + " from " + intent.getStringExtra(COMPANY_NAME) + " is coming to your home" + "(" + UNUniName + ")")
                            d.putExtra("mobNum", intent.getStringExtra(MOBILENUMBER))
                            d.putExtra("name", intent.getStringExtra(PERSONNAME))
                            d.putExtra("nr_id", intToString(globalApiObject.data.visitorLog.vlVisLgID))
                            d.putExtra("unitname", UNUniName)
                            d.putExtra(UNITID, UNUnitID.toString())
                            d.putExtra("memType", "Owner")
                            d.putExtra(COMPANY_NAME, intent.getStringExtra(COMPANY_NAME))
                            d.putExtra(UNIT_ACCOUNT_ID, Unit_ACCOUNT_ID)
                            d.putExtra("VLVisLgID", globalApiObject.data.visitorLog.vlVisLgID)
                            d.putExtra(VISITOR_TYPE, intent.getStringExtra(VISITOR_TYPE))
                            sendBroadcast(d)
                            uploadImage(imageName.toString(), mBitmap)
                            Log.d("CreateVisitorLogResp", "StaffEntry " + globalApiObject.data.toString())

                            deleteDir(Environment.getExternalStorageDirectory().toString() + "/DCIM/myCapturedImages")


                        } else {
                            Utils.showToast(applicationContext, globalApiObject.apiVersion)
                        }
                        finish()
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
            "", mobNum, "", "", "", "", imageName.toString()
        )
        //  Log.d("singUp","StaffEntry "+req.toString(),imgName.toString())

        compositeDisposable.add(
            RetrofitClinet.instance.signUpCall(CHAMPTOKEN, req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : CommonDisposable<SignUpResp<Account>>() {
                    override fun onSuccessResponse(globalApiObject: SignUpResp<Account>) {
                        if (globalApiObject.success == true) {
                            // var imgName="PERSON" +globalApiObject.data.account.acAccntID  + ".jpg"
                            uploadImage(imageName.toString(), mBitmap)
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