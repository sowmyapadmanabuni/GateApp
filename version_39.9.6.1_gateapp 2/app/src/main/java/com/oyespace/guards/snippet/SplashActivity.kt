/*
package com.oyespace.guards.snippet

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.iid.FirebaseInstanceId
import com.goohungrry.ecode.R
import com.goohungrry.ecode.fragment.BaseKotlinFragment
import com.goohungrry.ecode.listeners.LocationCallback
import com.goohungrry.ecode.listeners.PermissionCallback
import com.goohungrry.ecode.network.CommonDisposable
import com.goohungrry.ecode.network.RetrofitClinet
import com.goohungrry.ecode.pojo.GlobalApiObject
import com.goohungrry.ecode.pojo.SearchResult
import com.goohungrry.ecode.pojo.UserDeviceDetailReq
import com.goohungrry.ecode.utils.ConstantUtils
import com.goohungrry.ecode.utils.LocalDb
import com.goohungrry.ecode.utils.LocationUtils
import com.goohungrry.ecode.utils.Utils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

*/
/**
 * Created by linuxy on 5/31/17.
 *//*


class SplashActivity : BaseLocationActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)
        val searchData = LocalDb.getSearchData()
        makeDeviceRegApi()
        if (searchData == null) {
            handleLocation()
        } else {
            launchMainActivity()
        }
    }

    private fun makeDeviceRegApi() {
        try {
            val loginDetails = LocalDb.getLoginDetails()
            var apikey: String? = null
            if (loginDetails != null) {
                apikey = loginDetails.apikey
            }
            val detailReq = UserDeviceDetailReq("Android", Build.MODEL, Utils.getDeviceId(this), FirebaseInstanceId.getInstance().token, apikey)
            RetrofitClinet.instance.adddeviceid(detailReq).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : CommonDisposable<GlobalApiObject<String>>() {
                        override fun onSuccessResponse(t: GlobalApiObject<String>) {
                            Log.d(TAG, "onSuccessResponse")
                        }

                        override fun onErrorResponse(e: Throwable) {
                        }

                        override fun noNetowork() {
                        }
                    });
        } catch (e: Exception) {
        }
    }

    private fun launchLocationSelectionActivity() {
        val intent = Intent(this, LocationSearchActivity::class.java)
        startActivityForResult(intent, LocationSearchActivity.LOCATION_REQUEST)
    }


    private fun handleLocation() {
        requestPermission(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), 1, PermissionCallback { isGranted ->
            if (isGranted) {
                checkForLocationStatus()
            } else {
                launchLocationSelectionActivity()
//                finish()
            }
        })
    }


//    private fun makeDeviceRegApi() {
//        try {
//            val loginDetails = LocalDb.getLoginDetails()
//            var apikey: String? = null
//            if (loginDetails != null) {
//                apikey = loginDetails.apikey
//            }
//            val detailReq = UserDeviceDetailReq("Android", Build.MODEL, Utils.getDeviceId(this), FirebaseInstanceId.getInstance().token, apikey)
//            RetrofitClinet.instance.adddeviceid(detailReq).subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribeWith(object : CommonDisposable<GlobalApiObject<String>>() {
//                        override fun onSuccessResponse(t: GlobalApiObject<String>) {
//                            Log.d(TAG, "onSuccessResponse")
//                        }
//
//                        override fun onErrorResponse(e: Throwable) {
//                        }
//
//                        override fun noNetowork() {
//                        }
//                    });
//        } catch (e: Exception) {
//        }
//    }

    private fun checkForLocationStatus() {
        val baseLocationActivity = this as BaseLocationActivity
        baseLocationActivity.showLocationOnDialog(PermissionCallback { isGranted: Boolean ->
            if (isGranted) {
                baseLocationActivity.startFetchingLocation()
            } else {
                launchLocationSelectionActivity()
//                finish()
//                Toast.makeText(activity, R.string.location_off_alert, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onLocationReceived(location: Location?) = if (location != null) {
        Timber.d(BaseKotlinFragment.TAG)
        LocationUtils.getAddress(location.latitude, location.longitude, object : LocationCallback {
            override fun onAddress(data: String?) {
                data.let {
                    val searchResult = SearchResult(data, false, "", location.latitude.toString(), location.longitude.toString())
                    LocalDb.saveSearchData(searchResult)
                    launchMainActivity()
                }
            }
        })
    } else {
        launchLocationSelectionActivity()
        Toast.makeText(this, R.string.failed_location_alert, Toast.LENGTH_SHORT).show()
    }

    private fun launchMainActivity() {
        val mainIntent = Intent(this@SplashActivity, NewHomeActivity::class.java)
        startActivity(mainIntent)
        overridePendingTransition(R.anim.push_left_in,
                R.anim.push_left_out)
        finish()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LocationSearchActivity.LOCATION_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    val searchResult = data.getParcelableExtra<SearchResult>(ConstantUtils.DATA)
                    if (searchResult != null) {
                        LocalDb.saveSearchData(searchResult)
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

}
*/
