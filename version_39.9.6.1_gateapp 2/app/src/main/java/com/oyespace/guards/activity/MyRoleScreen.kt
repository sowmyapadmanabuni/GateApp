package com.oyespace.guards.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import com.oyespace.guards.Dashboard
import com.oyespace.guards.network.CommonDisposable
import com.oyespace.guards.network.RetrofitClinet
import com.oyespace.guards.pojo.*
import com.oyespace.guards.utils.AppUtils.Companion.intToString
import com.oyespace.guards.utils.LocalDb
import com.oyespace.guards.utils.Prefs
import com.oyespace.guards.utils.Utils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import com.oyespace.guards.R
import com.yarolegovich.lovelydialog.LovelyStandardDialog

import com.oyespace.guards.constants.PrefKeys
import com.oyespace.guards.utils.ConstantUtils.*
import kotlinx.android.synthetic.main.activity_mobile_number.*
import java.util.*






class MyRoleScreen : BaseKotlinActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        getDeviceRegistrationInfo()

    }

    private fun getDeviceRegistrationInfo() {
        val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        var Mobile_IMEI_NO = tm.deviceId
     //   Mobile_IMEI_NO=""
        val getSimNumber = tm.getLine1Number()


//        if(getSimNumber.equals("")){
//            val mainIntent = Intent(this@MyRoleScreen, LoginActivity::class.java)
//            startActivity(mainIntent)
//            finish()
//        }

//if(getSimNumber!=null){
    if(getSimNumber.length==0){
    Log.v("GetSimNumber",getSimNumber)

}
        else{
    Prefs.putString(PrefKeys.MOBILE_NUMBER, getSimNumber)
        }

       // Prefs.putString(PrefKeys.COUNTRY_CODE, countryCode.toString())



        // val req = GetDeviceInfobyMobImeiReq(Mobile_IMEI_NO, "+"+Prefs.getString(PrefKeys.COUNTRY_CODE,null)+Prefs.getString(PrefKeys.MOBILE_NUMBER,null))
        val req = GetDeviceInfobyMobImeiReq(Mobile_IMEI_NO, "+"+Prefs.getString(PrefKeys.MOBILE_NUMBER,null))


        Log.d("CreateVisitorLogResp","StaffEntry "+req.toString()+"..."+getSimNumber)

        compositeDisposable.add(
            RetrofitClinet.instance.getDeviceInfobyMobImeiCall(OYE247TOKEN,req)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CommonDisposable<GetDeviceInfobyMobImeiResp<Device>>() {
                override fun onSuccessResponse(globalApiObject: GetDeviceInfobyMobImeiResp<Device>) {
                    if (globalApiObject.data.device!= null) {
                       // Utils.showToast(applicationContext, intToString( globalApiObject.data.workers.asAssnID))
                        Log.d("getDeviceInfoCall","StaffEntry " +globalApiObject.data.toString())

                        val exitedSort = ArrayList<Device>()
                        val arrayList = globalApiObject.data.device

                        //looping through existing elements
                        for (s in arrayList) {
                            //if the existing elements contains the search input
                            Log.d("button_done ","device "+" "+" "+s.deGateNo)
                            Prefs.putString(GATE_NO,s.deGateNo)
                            if (s.deStatus) {
                                Log.d("device ","device "+s.deStatus+" "+s.deStatus+" ")


                                exitedSort.add(s)
                                //adding the element to filtered list
                            } else {
                                exitedSort.add(s)
                            }
                        }
                        if(exitedSort!=null) {
                            getAssnInfo(globalApiObject.data.device[0].asAssnID)
                            getDeviceList(globalApiObject.data.device[0].asAssnID)

                        //    LocalDb.saveDeviceInfo(globalApiObject.data.workers)
                        }else{

                            LovelyStandardDialog(this@MyRoleScreen, LovelyStandardDialog.ButtonLayout.VERTICAL)
                                .setTopColorRes(R.color.google_red)
                                //.setIcon(R.drawable.ic_info_black_24dp)
                                //This will add Don't show again checkbox to the dialog. You can pass any ID as argument
                                .setTitle("invalid")
                                .setTitleGravity(Gravity.CENTER)
                                .setMessage("Not Registered as Gate Device")
                                .setMessageGravity(Gravity.CENTER)
                                .setPositiveButton(android.R.string.ok) {

                                }

                                .show()

                        }

                    } else {

                        LovelyStandardDialog(this@MyRoleScreen, LovelyStandardDialog.ButtonLayout.VERTICAL)
                            .setTopColorRes(R.color.google_red)
                            .setIcon(R.drawable.ic_info_black_24dp)
                            //This will add Don't show again checkbox to the dialog. You can pass any ID as argument
                            .setTitle("invalid")
                            .setTitleGravity(Gravity.CENTER)
                            .setMessage("Not Registered as Gate Device")
                            .setMessageGravity(Gravity.CENTER)
                            .setPositiveButton(android.R.string.ok) {

                            }

                            .show()
                    }
                }

                override fun onErrorResponse(e: Throwable) {
                    Log.d("onErrorResponse","StaffEntry "+e.toString())
//                    Utils.showToast(applicationContext, getString(R.string.some_wrng))

                    if(getSimNumber.length==0){

                        val input =Prefs.getString(PrefKeys.MOBILE_NUMBER,null)
                        LovelyStandardDialog(this@MyRoleScreen, LovelyStandardDialog.ButtonLayout.VERTICAL)
                            .setTopColorRes(R.color.google_red)
                            // .setIcon(R.drawable.ic_info_black_24dp)
                            //This will add Don't show again checkbox to the dialog. You can pass any ID as argument
                            .setTitle("Device is not registered as Gate device")
                            .setTitleGravity(Gravity.CENTER)
                             .setMessage(Mobile_IMEI_NO+" and +"+Prefs.getString(PrefKeys.MOBILE_NUMBER,null)+" is Not Registered as Gate Device")
                            //.setMessage(Mobile_IMEI_NO + " and +91" + intent.getStringExtra("MOBIELNUMBER") + " is not registered as Gate Device")

                            .setMessageGravity(Gravity.CENTER)
                            .setPositiveButton(android.R.string.ok) {
                                finish()
                            }

                            .show()
                        dismissProgress()
                    }else {


                        LovelyStandardDialog(this@MyRoleScreen, LovelyStandardDialog.ButtonLayout.VERTICAL)
                            .setTopColorRes(R.color.google_red)
                            // .setIcon(R.drawable.ic_info_black_24dp)
                            //This will add Don't show again checkbox to the dialog. You can pass any ID as argument
                            .setTitle("Device is not registered as Gate device")
                            .setTitleGravity(Gravity.CENTER)
                            // .setMessage(Mobile_IMEI_NO+" and +"+Prefs.getString(PrefKeys.COUNTRY_CODE,null)+Prefs.getString(PrefKeys.MOBILE_NUMBER,null)+" is Not Registered as Gate Device")
                            .setMessage(Mobile_IMEI_NO + " and +" + getSimNumber + " is not registered as Gate Device")

                            .setMessageGravity(Gravity.CENTER)
                            .setPositiveButton(android.R.string.ok) {
                                finish()
                            }

                            .show()
                        dismissProgress()
                    }
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

    private fun getAssnInfo(AssnID: Int ) {

        RetrofitClinet.instance
            .getAssociationInfocall(CHAMPTOKEN, intToString( AssnID))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CommonDisposable<GetAssnInfoResp<Association>>() {

                override fun onSuccessResponse(globalApiObject: GetAssnInfoResp<Association>) {

                    if (globalApiObject.success == true) {
                        Log.d("getAssociationIn","StaffEntry "+globalApiObject.data.toString())
                        Prefs.putObject("AssnInfo",globalApiObject.data.association)
                        LocalDb.saveAssociation(globalApiObject.data.association)
                        Prefs.putInt(ASSOCIATION_ID,globalApiObject.data.association.asAssnID)


                        getStaffList(AssnID)

                    } else {
                        //rv_staff.setEmptyAdapter("No items to show!", false, 0)
                        Log.d("getAssociationIn","StaffEntry "+globalApiObject.data.toString())
                        Toast.makeText(this@MyRoleScreen, "No Data", Toast.LENGTH_LONG)
                            .show()
                    }
                }

                override fun onErrorResponse(e: Throwable) {

                    //rv_staff.setEmptyAdapter(getString(R.string.some_wrng), false, 0)
                    Toast.makeText(this@MyRoleScreen, e.toString(), Toast.LENGTH_LONG)
                        .show()
                    Log.d("Error StaffEntry",e.toString())

                }

                override fun noNetowork() {
                    Toast.makeText(this@MyRoleScreen, "No network call ", Toast.LENGTH_LONG)
                        .show()
                }
            })


    }

    private fun getStaffList(AssnID: Int) {

        RetrofitClinet.instance
            .workerList(OYE247TOKEN, intToString(AssnID))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CommonDisposable<GetWorkerListbyAssnIDResp<WorkerListbyAssnIDData>>() {

                override fun onSuccessResponse(workerListResponse: GetWorkerListbyAssnIDResp<WorkerListbyAssnIDData>) {

                    if (workerListResponse.data.worker!= null) {
                        Log.d("WorkerList success",workerListResponse.data.toString())

                        val arrayList = workerListResponse.data.worker

                        Collections.sort(arrayList, object : Comparator<WorkerDetails>{
                            override  fun compare(lhs: WorkerDetails, rhs: WorkerDetails): Int {
                                return lhs.wkfName.compareTo(rhs.wkfName)
                            }
                        })

                        LocalDb.saveStaffList(arrayList);
                        val mainIntent = Intent(this@MyRoleScreen, Dashboard::class.java)
                        mainIntent.putExtra("STAFF","Available")
                        startActivity(mainIntent)
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out)
                        finish()
                    }
                    else{
                        //LocalDb.saveStaffList(arrayList);
                        val mainIntent = Intent(this@MyRoleScreen, Dashboard::class.java)
                        mainIntent.putExtra("STAFF","Not Available")
                        startActivity(mainIntent)
                        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out)
                        finish()
                    }
//                    else {
//                        //rv_staff.setEmptyAdapter("No items to show!", false, 0)
//
//                        LovelyStandardDialog(this@MyRoleScreen, LovelyStandardDialog.ButtonLayout.VERTICAL)
//                            .setTopColorRes(R.color.google_red)
//                            .setIcon(R.drawable.ic_info_black_24dp)
//                            //This will add Don't show again checkbox to the dialog. You can pass any ID as argument
//                            .setTitle("No Staff Data")
//                            .setTitleGravity(Gravity.CENTER)
//                            .setMessage("No Staff Data")
//                            .setMessageGravity(Gravity.CENTER)
//                            .setPositiveButton("Add") {
//                                val mainIntent = Intent(this@MyRoleScreen, StaffListActivity::class.java)
//                                startActivity(mainIntent)
//                                finish()
//                            }
//
//                            .show()
//                    }
                }

                override fun onErrorResponse(e: Throwable) {

                    //rv_staff.setEmptyAdapter(getString(R.string.some_wrng), false, 0)
                    Toast.makeText(this@MyRoleScreen, getString(R.string.some_wrng), Toast.LENGTH_LONG)
                        .show()
                    Log.d("Error WorkerList",e.toString())

                }

                override fun noNetowork() {
                    Toast.makeText(this@MyRoleScreen, "No network call ", Toast.LENGTH_LONG)
                        .show()
                }
            })
    }
fun getDeviceList(AssnID: Int){
    RetrofitClinet.instance.getDeviceListResponse(OYE247TOKEN, intToString(AssnID))
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeWith(object : CommonDisposable<getDeviceList>() {

            override fun onSuccessResponse(deviceListResponse: getDeviceList) {

                if (deviceListResponse.data.deviceListByAssocID!= null) {
                    Log.d("WorkerList success",deviceListResponse.data.toString())

                    val arrayList = deviceListResponse.data.deviceListByAssocID

                    if(arrayList.size ==1 || arrayList.size == 0){
                        Prefs.putString(WALKIETALKIE,"OFF")
                        Log.e("Device List",arrayList.size.toString())
                    }
                    else{
                        Prefs.putString(WALKIETALKIE,"ON")
                        Log.e("Device List",arrayList.size.toString())
                    }

//                    Collections.sort(arrayList, object : Comparator<WorkerDetails>{
//                        override  fun compare(lhs: WorkerDetails, rhs: WorkerDetails): Int {
//                            return lhs.wkfName.compareTo(rhs.wkfName)
//                        }
//                    })

//                    LocalDb.saveStaffList(arrayList);
//                    val mainIntent = Intent(this@MyRoleScreen, Dashboard::class.java)
//                    startActivity(mainIntent)
//                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out)
//                    finish()
                } else {

//                    val mainIntent = Intent(this@MyRoleScreen, Dashboard::class.java)
//                    startActivity(mainIntent)
//                    finish()

                    //rv_staff.setEmptyAdapter("No items to show!", false, 0)
//
//                    LovelyStandardDialog(this@MyRoleScreen, LovelyStandardDialog.ButtonLayout.VERTICAL)
//                        .setTopColorRes(R.color.google_red)
//                        .setIcon(R.drawable.ic_info_black_24dp)
//                        //This will add Don't show again checkbox to the dialog. You can pass any ID as argument
//                        .setTitle("No Staff Data")
//                        .setTitleGravity(Gravity.CENTER)
//                        .setMessage("No Staff Data")
//                        .setMessageGravity(Gravity.CENTER)
//                        .setPositiveButton("Add") {
//                            val mainIntent = Intent(this@MyRoleScreen, StaffListActivity::class.java)
//                            startActivity(mainIntent)
//                            finish()
//                        }
//
//                        .show()
                }
            }

            override fun onErrorResponse(e: Throwable) {

                //rv_staff.setEmptyAdapter(getString(R.string.some_wrng), false, 0)
                Toast.makeText(this@MyRoleScreen, getString(R.string.some_wrng), Toast.LENGTH_LONG)
                    .show()
                Log.d("Error WorkerList",e.toString())

            }

            override fun noNetowork() {
                Toast.makeText(this@MyRoleScreen, "No network call ", Toast.LENGTH_LONG)
                    .show()
            }
        })
}
}