package com.oyespace.guards.qrscanner

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import com.oyespace.guards.BackgroundSyncReceiver
import com.oyespace.guards.ImageBigView
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
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_final_registration.*
import java.util.*

class VehicleGuestQRRegistration : BaseKotlinActivity(), View.OnClickListener {
    var minteger = 0
    internal var list = ArrayList<String>()
    lateinit var imageAdapter: ImageAdapter
    var accountId:String?=null
    var unitName:String?=null
    lateinit var mBitmap: Bitmap
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
                button_done.setEnabled(false)
                button_done.setClickable(false)
                Log.d("button_done ", "StaffEntry " + FLOW_TYPE + " " + STAFF_REGISTRATION + " " + FLOW_TYPE.equals(STAFF_REGISTRATION, true))
                invitationupdate("True", intent.getIntExtra(INVITATIONID, 0))
                visitorLog()

            }

            R.id.profile_image ->{
                Log.d("button_done ","StaffEntry "+FLOW_TYPE+" "+STAFF_REGISTRATION+" "+FLOW_TYPE.equals( STAFF_REGISTRATION,true))
                val wrrw = intent.getByteArrayExtra(PERSON_PHOTO)
                if(wrrw!=null) {
                    val d = Intent(this@VehicleGuestQRRegistration, ImageBigView::class.java)
                    d.putExtra(PERSON_PHOTO, intent.getByteArrayExtra(PERSON_PHOTO))
                    startActivity(d)

                }else{

                }
            }
        }
    }


    val entries: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocale(Prefs.getString(LANGUAGE, null))
        setContentView(R.layout.activity_final_registration)
        minteger = intent.getStringExtra(NUMBEROFPERSONS).toInt()
        getUnitLog(intent.getStringExtra(UNITID).toInt())

        itemLyt.visibility = View.VISIBLE
        lyt_count.visibility = View.VISIBLE

        if (intent.getStringExtra(FLOW_TYPE).equals(VEHICLE_GUESTWITHQRCODE, true)) {
            profile_image.visibility = View.GONE
        }
        Log.d(
            "intentdata StaffEntry", "" + intent.getStringExtra(UNITNAME) + " " + intent.getStringExtra(UNITID)
                    + " " + intent.getStringExtra(MOBILENUMBER) + " " + intent.getStringExtra(COUNTRYCODE) + " "
                    + intent.getStringExtra(PERSONNAME) + " "
                    + " " + intent.getStringExtra(FLOW_TYPE) + " "
                    + intent.getStringExtra(VISITOR_TYPE) + " " + intent.getStringExtra(COMPANY_NAME)
        )
        txt_header.text=resources.getString(R.string.textidcard)+" "+ LocalDb.getAssociation()!!.asAsnName

        tv_name.setText(resources.getString(R.string.textname)+": "+intent.getStringExtra(PERSONNAME))
        val input =intent.getStringExtra(MOBILENUMBER)
        val number = input.replaceFirst("(\\d{4})(\\d{3})(\\d+)".toRegex(), "$1 $2 $3")
        tv_mobilenumber.setText("+"+91+" "+number)


        //  tv_mobilenumber.setText(resources.getString(R.string.textmobile)+": " + intent.getStringExtra(COUNTRYCODE) + "" + intent.getStringExtra(MOBILENUMBER))

        tv_totalperson.setText(
            resources.getString(R.string.textperson) + ": " + intent.getStringExtra(
                NUMBEROFPERSONS
            ).toInt()
        )
        tv_from.setText(resources.getString(R.string.textfrom) + intent.getStringExtra(COMPANY_NAME))

        menuAdd.setOnClickListener {


            minteger++
            //  menuCount.setText("" + minteger)
            tv_totalperson.setText(resources.getString(R.string.textperson) + ": " + minteger)

        }

        menuRemove.setOnClickListener {
            if (minteger > 1) {
                minteger--
                //  menuCount.setText("" + minteger)
                tv_totalperson.setText(resources.getString(R.string.textperson) + ": " + minteger)

            } else {

            }
        }
        tv_from.visibility = View.GONE
        if (intent.getStringExtra(FLOW_TYPE) == STAFF_REGISTRATION) {
            tv_from.setText("Designation: " + intent.getStringExtra(COMPANY_NAME))
            itemLyt.setVisibility(View.GONE)
        } else {
            itemLyt.setVisibility(View.VISIBLE)
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
        var imgName = "PERSON" + "Association" + Prefs.getInt(ASSOCIATION_ID,0) + "NONREGULAR" + intent.getStringExtra(MOBILENUMBER)+ ".jpg"

        var memID:Int=410;
        if(BASE_URL.contains("dev",true)){
            memID=64;
        }
        else if(BASE_URL.contains("uat",true)){
            memID=64;
        }
        val req = CreateVisitorLogReq(Prefs.getInt(ASSOCIATION_ID,0), 0, unitName!!,
            intent.getStringExtra(UNITID),
            intent.getStringExtra(COMPANY_NAME),
            intent.getStringExtra(PERSONNAME),
            LocalDb.getAssociation()!!.asAsnName,
            0,
            "",  intent.getStringExtra(COUNTRYCODE) + "" + intent.getStringExtra(MOBILENUMBER), intToString(minteger), "",
            "", "", minteger, ConstantUtils.GUEST,SPPrdImg1, SPPrdImg2, SPPrdImg3, SPPrdImg4, SPPrdImg5
            , SPPrdImg6, SPPrdImg7, SPPrdImg8, SPPrdImg9, SPPrdImg10,"",imgName,Prefs.getString(ConstantUtils.GATE_NO, ""),
            DateTimeUtils.getCurrentTimeLocal()
        )
        Log.d("CreateVisitorLogResp", "StaffEntry " + req.toString())
        compositeDisposable.add(RetrofitClinet.instance.createVisitorLogCall(OYE247TOKEN, req)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CommonDisposable<CreateVisitorLogResp<VLRData>>() {
                override fun onSuccessResponse(globalApiObject: CreateVisitorLogResp<VLRData>) {
                    if (globalApiObject.success == true) {
                        // getInvitationCreate(intent.getStringExtra(UNITID).toInt(),intent.getStringExtra(PERSONNAME),"",intent.getStringExtra(COUNTRYCODE)+intent.getStringExtra(MOBILENUMBER),"","","","",getCurrentTimeLocal(),getCurrentTimeLocal(),"",true,Prefs.getInt(ASSOCIATION_ID,0),true)

                       // visitorEntryLog(globalApiObject.data.visitorLog.vlVisLgID)
//                        val d  =  Intent(this@VehicleGuestQRRegistration, BackgroundSyncReceiver::class.java)
//                        d.putExtra(BSR_Action, VisitorEntryFCM)
//                        d.putExtra("msg", intent.getStringExtra(PERSONNAME)+" from "+intent.getStringExtra(COMPANY_NAME)+" is coming to your home")
//                        d.putExtra("mobNum", intent.getStringExtra(MOBILENUMBER))
//                        d.putExtra("name", intent.getStringExtra(PERSONNAME))
//                        d.putExtra("nr_id", intToString(globalApiObject.data.visitorLog.vlVisLgID))
//                        d.putExtra("unitname", intent.getStringExtra(UNITNAME))
//                        d.putExtra(UNITID,intent.getStringExtra(UNITID))
//                        d.putExtra("memType", "Owner")
////                        intent.getStringExtra("msg"),intent.getStringExtra("mobNum"),
////                        intent.getStringExtra("name"),intent.getStringExtra("nr_id"),
////                        intent.getStringExtra("unitname"),intent.getStringExtra("memType")
//                        sendBroadcast(d);


                        val d  =  Intent(this@VehicleGuestQRRegistration,BackgroundSyncReceiver::class.java)
                        d.putExtra(BSR_Action, VisitorEntryFCM)
                        d.putExtra(
                            "msg",
                            intent.getStringExtra(PERSONNAME) + " from " + intent.getStringExtra(
                                COMPANY_NAME
                            ) + " is coming to your home" + "(" + unitName + ")"
                        )
                        d.putExtra("mobNum", intent.getStringExtra(MOBILENUMBER))
                        d.putExtra("name", intent.getStringExtra(PERSONNAME))
                        d.putExtra("nr_id", intToString(globalApiObject.data.visitorLog.vlVisLgID))
                        d.putExtra("unitname", unitName)
                        d.putExtra("memType", "Owner")
                        d.putExtra(UNITID,intent.getStringExtra(UNITID))
                        d.putExtra(COMPANY_NAME,intent.getStringExtra(COMPANY_NAME))
                        d.putExtra(UNIT_ACCOUNT_ID,accountId)
                        d.putExtra("VLVisLgID",globalApiObject.data.visitorLog.vlVisLgID)
                        d.putExtra(VISITOR_TYPE, intent.getStringExtra(VISITOR_TYPE))

//                        intent.getStringExtra("msg"),intent.getStringExtra("mobNum"),
//                        intent.getStringExtra("name"),intent.getStringExtra("nr_id"),
//                        intent.getStringExtra("unitname"),intent.getStringExtra("memType")
                        sendBroadcast(d);

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

        val req = SignUpReq(
            "", "", "", "", "",
            name, "+" + isdCode, "", "", "", "",
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

  //  override fun onBackPressed() {
//        super.onBackPressed()
//        val d = Intent(this@VehicleGuestQRRegistration, CustomViewFinderScannerActivity::class.java)

/*        Log.d(
            "intentdata NameEntr",
            "buttonNext " + getIntent().getStringExtra(UNITNAME) + " " + intent.getStringExtra(UNITID)
                    + " " + getIntent().getStringExtra(MOBILENUMBER) + " " + getIntent().getStringExtra(COUNTRYCODE) + " " + intent.getStringExtra(
                PERSONNAME
            )
        );


        */

//        d.putExtra(UNITID, intent.getStringExtra(UNITID))
//        d.putExtra(UNITNAME, intent.getStringExtra(UNITNAME))
//        d.putExtra(FLOW_TYPE, intent.getStringExtra(FLOW_TYPE))
//        d.putExtra(VISITOR_TYPE, intent.getStringExtra(VISITOR_TYPE))
//        d.putExtra(COMPANY_NAME, intent.getStringExtra(COMPANY_NAME))
//        d.putExtra(MOBILENUMBER, intent.getStringExtra(MOBILENUMBER))
//        d.putExtra(COUNTRYCODE, intent.getStringExtra(COUNTRYCODE))
//        d.putExtra(PERSONNAME, intent.getStringExtra(PERSONNAME))
//        d.putExtra(ACCOUNT_ID, intent.getIntExtra(ACCOUNT_ID, 0))

//        startActivity(d);
//        finish();
   // }

    private fun visitorEntryLog(visitorLogID: Int) {
//        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
//        val currentDate = sdf.format(Date())
//        System.out.println(" C DATE is  "+currentDate)

        val req = VisitorEntryReq(getCurrentTimeLocal(),0, visitorLogID)
        Log.d("CreateVisitorLogResp", "StaffEntry " + req.toString())

        compositeDisposable.add(RetrofitClinet.instance.visitorEntryCall(OYE247TOKEN, req)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CommonDisposable<VisitorExitResp>() {
                override fun onSuccessResponse(globalApiObject: VisitorExitResp) {
                    if (globalApiObject.success == true) {


//                        Log.d("VisitorEntryReq","StaffEntry "+globalApiObject.data.toString())
//                        val d = Intent(this@VehicleGuestQRRegistration, Dashboard::class.java)
//                        startActivity(d)
                        finish();
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
//        val d = Intent(this@VehicleGuestQRRegistration, Dashboard::class.java)
//        startActivity(d)
        finish()
    }

    private fun getUnitLog(unitId:Int) {

        RetrofitClinet.instance
            .getUnitListbyUnitId("1FDF86AF-94D7-4EA9-8800-5FBCCFF8E5C1", unitId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CommonDisposable<UnitlistbyUnitID>() {

                override fun onSuccessResponse(UnitList: UnitlistbyUnitID) {

                    if (UnitList.success == true) {

                        tv_for.setText(resources.getString(R.string.textto) + UnitList.data.unit.unUniName)
                        accountId=UnitList.data.unit.acAccntID.toString()
                        unitName=UnitList.data.unit.unUniName
//
//                        val ddc  =  Intent(mcontext, BackgroundSyncReceiver::class.java)
//                        ddc.putExtra(ConstantUtils.BSR_Action, ConstantUtils.VisitorEntryFCM)
//                        ddc.putExtra("msg", personName+" "+desgn +" is coming to your home")
//                        ddc.putExtra("mobNum", mobileNumb)
//                        ddc.putExtra("name", personName)
//                        ddc.putExtra("nr_id", vlVisLgID.toString())
//                        ddc.putExtra("unitname", unitName)
//                        ddc.putExtra("memType", "Owner")
//                        ddc.putExtra(UNITID,unitId.toString())
//                        ddc.putExtra(COMPANY_NAME,"Staff")
//                        ddc.putExtra(UNIT_ACCOUNT_ID,UnitList.data.unit.acAccntID.toString())
//                        ddc.putExtra("VLVisLgID",vlVisLgID)
////                        intent.getStringExtra("msg"),intent.getStringExtra("mobNum"),
////                        intent.getStringExtra("name"),intent.getStringExtra("nr_id"),
////                        intent.getStringExtra("unitname"),intent.getStringExtra("memType")
//                        mcontext.sendBroadcast(ddc);


                    } else {
                    }
                }

                override fun onErrorResponse(e: Throwable) {
                    Log.d("cdvd", e.message);


                }

                override fun noNetowork() {

                }
            })
    }

    private fun invitationupdate(iNIsUsed: String, iNInvtID: Int) {


        val dataReq = InvitationUpdateReq(iNIsUsed, iNInvtID)


        RetrofitClinet.instance
            .updateInvitation(OYE247TOKEN, dataReq)
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