
package com.oyespace.guards.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import com.oyespace.guards.Dashboard
import com.oyespace.guards.Myapp
import com.oyespace.guards.R
import com.oyespace.guards.constants.PrefKeys
import com.oyespace.guards.fragment.BaseKotlinFragment
import com.oyespace.guards.listeners.LocationCallback
import com.oyespace.guards.listeners.PermissionCallback
import com.oyespace.guards.network.CommonDisposable
import com.oyespace.guards.network.RetrofitClinet
import com.oyespace.guards.pojo.Device
import com.oyespace.guards.pojo.GetDeviceInfobyMobImeiReq
import com.oyespace.guards.pojo.GetDeviceInfobyMobImeiResp
import com.oyespace.guards.pojo.SearchResult
import com.oyespace.guards.utils.ConstantUtils
import com.oyespace.guards.utils.LocalDb
import com.oyespace.guards.utils.LocationUtils
import com.oyespace.guards.utils.Prefs
import com.yarolegovich.lovelydialog.LovelyStandardDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*


class SplashActivity : BaseLocationActivity() {

  var app: Myapp?=null
    var Mobile_IMEI_NO:String?=null
    var getSimNumber:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)
        Prefs.putBoolean("ACTIVE_SOS", false)
        // app = getApplication() as Myapp?;
        val searchData = LocalDb.getSearchData()

        if (searchData == null) {
            handleLocation()
        } else {
            launchMainActivity()
        }
    }

    private fun handleLocation() {
        requestPermission(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.MODIFY_PHONE_STATE,
            Manifest.permission.ANSWER_PHONE_CALLS
        ), 1, PermissionCallback { isGranted ->
            if (isGranted) {
                checkForLocationStatus()
            } else {

            }
        })
    }

    private fun checkForLocationStatus() {
        val baseLocationActivity = this as BaseLocationActivity
        baseLocationActivity.showLocationOnDialog(PermissionCallback { isGranted: Boolean ->
            if (isGranted) {
                baseLocationActivity.startFetchingLocation()
            } else {

                Toast.makeText(this@SplashActivity, "Location off", Toast.LENGTH_SHORT).show()
                finish()
            }
        })
    }

    override fun onLocationReceived(location: Location?) = if (location != null) {
        Timber.d(BaseKotlinFragment.TAG)
        LocationUtils.getAddress(location.latitude, location.longitude, object : LocationCallback {
            override fun onAddress(data: String?) {
                data.let {
                    val searchResult =
                        SearchResult(data, false, "", location.latitude.toString(), location.longitude.toString())

                    launchMainActivity()
                    Timber.d("Location", location.latitude, location.longitude)
                    Log.d("Location Data", location.latitude.toString() + " " + location.longitude.toString())
                }
            }
        })
    } else {
      //  launchLocationSelectionActivity()
        Toast.makeText(this, "location failed", Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("MissingPermission")
    private fun launchMainActivity() {
        // val mainIntent = Intent(this@SplashActivity, DashboardActivity::class.java)


        val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        Mobile_IMEI_NO = tm.deviceId
        //   Mobile_IMEI_NO=""
        getSimNumber = tm.line1Number
        val modelno=android.os.Build.MODEL
        Prefs.putString(PrefKeys.MODEL_NUMBER,modelno)

        if(getSimNumber?.length==0){


            if(Prefs.getString(PrefKeys.MOBILE_NUMBER,null)==null) {


                val mainIntent = Intent(this@SplashActivity, LoginActivity::class.java)
                startActivity(mainIntent)
                finish()


            }else {
                if (LocalDb.getStaffList() == null) {
                    // Toast.makeText(this, " failed", Toast.LENGTH_SHORT).show()
                    val mainIntent = Intent(this@SplashActivity, MyRoleScreen::class.java)
                    intent.putExtra("MOBIELNUMBER", PrefKeys.MOBILE_NUMBER)
                    startActivity(mainIntent)
                    finish()
                } else {
                    val mainIntent = Intent(this@SplashActivity, Dashboard::class.java)
//            val mainIntent = Intent(this@SplashActivity, MyRoleScreen::class.java)
                    startActivity(mainIntent)
                    finish()
                }
            }


        } else {
            Prefs.putString(PrefKeys.MOBILE_NUMBER, getSimNumber)

            getDeviceRegistrationInfo()
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LocationSearchActivity.LOCATION_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    val searchResult = data.getParcelableExtra<SearchResult>(ConstantUtils.DATA)
                    if (searchResult != null) {

                        launchMainActivity()
                    } else {
                        finish()
                    }
                } else {
                    finish()
                }
            } else {
                finish()
            }
        }
    }

    private fun getDeviceRegistrationInfo() {





        // val req = GetDeviceInfobyMobImeiReq(Mobile_IMEI_NO, "+"+Prefs.getString(PrefKeys.COUNTRY_CODE,null)+Prefs.getString(PrefKeys.MOBILE_NUMBER,null))
        val req = GetDeviceInfobyMobImeiReq(Mobile_IMEI_NO.toString(), "+"+getSimNumber)


        Log.d("CreateVisitorLogResp","StaffEntry "+req.toString()+"..."+getSimNumber)

        compositeDisposable.add(
            RetrofitClinet.instance.getDeviceInfobyMobImeiCall(ConstantUtils.OYE247TOKEN,req)
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
                                Prefs.putString(ConstantUtils.GATE_NO,s.deGateNo)
                                if (s.deStatus) {
                                    Log.d("device ","device "+s.deStatus+" "+s.deStatus+" ")


                                    exitedSort.add(s)
                                    //adding the element to filtered list
                                } else {
                                    exitedSort.add(s)
                                }
                            }
                            if(exitedSort!=null) {

                                if(Prefs.getString(PrefKeys.MOBILE_NUMBER,null)==null) {

                                    val mainIntent = Intent(this@SplashActivity, MyRoleScreen::class.java)
                                    startActivity(mainIntent)
                                    finish()


                                }else {
                                    if (LocalDb.getStaffList() == null) {
                                        // Toast.makeText(this, " failed", Toast.LENGTH_SHORT).show()
                                        val mainIntent = Intent(this@SplashActivity, MyRoleScreen::class.java)
                                        intent.putExtra("MOBIELNUMBER", PrefKeys.MOBILE_NUMBER)
                                        startActivity(mainIntent)
                                        finish()
                                    } else {
                                        val mainIntent = Intent(this@SplashActivity, Dashboard::class.java)
//            val mainIntent = Intent(this@SplashActivity, MyRoleScreen::class.java)
                                        startActivity(mainIntent)
                                        finish()
                                    }
                                }
//                                getAssnInfo(globalApiObject.data.device[0].asAssnID)
//                                getDeviceList(globalApiObject.data.device[0].asAssnID)

                                //    LocalDb.saveDeviceInfo(globalApiObject.data.workers)
                            }else{

                                LovelyStandardDialog(this@SplashActivity, LovelyStandardDialog.ButtonLayout.VERTICAL)
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

                            LovelyStandardDialog(this@SplashActivity, LovelyStandardDialog.ButtonLayout.VERTICAL)
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

                        Toast.makeText(this@SplashActivity,e.toString(),Toast.LENGTH_LONG).show()

                        if(getSimNumber?.length==0) {

                            val input = Prefs.getString(PrefKeys.MOBILE_NUMBER, null)
                            LovelyStandardDialog(this@SplashActivity, LovelyStandardDialog.ButtonLayout.VERTICAL)
                                .setTopColorRes(R.color.google_red)
                                // .setIcon(R.drawable.ic_info_black_24dp)
                                //This will add Don't show again checkbox to the dialog. You can pass any ID as argument
                                .setTitle("Device is not registered as Gate device")
                                .setTitleGravity(Gravity.CENTER)
                                .setMessage(
                                    Mobile_IMEI_NO + " and +" + Prefs.getString(
                                        PrefKeys.COUNTRY_CODE,
                                        null
                                    ) + Prefs.getString(
                                        PrefKeys.MOBILE_NUMBER,
                                        null
                                    ) + " is Not Registered as Gate Device"
                                )
                                //.setMessage(Mobile_IMEI_NO + " and +91" + intent.getStringExtra("MOBIELNUMBER") + " is not registered as Gate Device")

                                .setMessageGravity(Gravity.CENTER)
                                .setPositiveButton(android.R.string.ok) {
                                    finish()
                                }

                                .show()
                            dismissProgress()
                       // }
                        }else {


                            LovelyStandardDialog(this@SplashActivity, LovelyStandardDialog.ButtonLayout.VERTICAL)
                                .setTopColorRes(R.color.google_red)
                               //  .setIcon(R.drawable.google_red)
                                //This will add Don't show again checkbox to the dialog. You can pass any ID as argument
                                .setTitle("Device is not registered as Gate device")
                                .setTitleGravity(Gravity.CENTER)
                                 .setMessage(Mobile_IMEI_NO+" and +"+Prefs.getString(PrefKeys.COUNTRY_CODE,null)+Prefs.getString(PrefKeys.MOBILE_NUMBER,null)+" is Not Registered as Gate Device")
                                .setMessage(Mobile_IMEI_NO + " and " + getSimNumber + " is not registered as Gate Device")

                                .setMessageGravity(Gravity.CENTER)
                                .setPositiveButton(android.R.string.ok) {
                                    finish()
                                }

                                .show()
                            dismissProgress()
                        }
                    }

                    override fun noNetowork() {
                        LovelyStandardDialog(this@SplashActivity, LovelyStandardDialog.ButtonLayout.VERTICAL)
                            .setTopColorRes(R.color.white)
                            .setIcon(R.drawable.oyespace_toolbar)
                            //This will add Don't show again checkbox to the dialog. You can pass any ID as argument
                            .setTitle("Please check Internet connection")
                            .setTitleGravity(Gravity.CENTER)
                            // .setMessage(Mobile_IMEI_NO+" and +"+Prefs.getString(PrefKeys.COUNTRY_CODE,null)+Prefs.getString(PrefKeys.MOBILE_NUMBER,null)+" is Not Registered as Gate Device")
                            // .setMessage(Mobile_IMEI_NO + " and +" + getSimNumber + " is not registered as Gate Device")

                            .setMessageGravity(Gravity.CENTER)
                            .setPositiveButton(android.R.string.ok) {
                                finish()
                            }

                            .show()
                        dismissProgress()                    }

                    override fun onShowProgress() {
//                        showProgress()
                    }

                    override fun onDismissProgress() {
                     //   dismissProgress()
                    }
                }))
    }

}