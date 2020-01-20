package com.oyespace.guards.utils

import android.graphics.Bitmap
import android.widget.Toast
import com.oyespace.guards.network.CommonDisposable
import com.oyespace.guards.pojo.GetCallResponse
import com.oyespace.guards.zeotelapi.ZeotelRetrofitClinet
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class TaptoCallApi {

    companion object{

        fun taptocallApi(gateMobileNumber: String, agentMobileNumber: String) {

            ZeotelRetrofitClinet.instance.getCall("KI_3t1wBwDQ2odmnvIclEdg-1391508276", "4000299", gateMobileNumber, agentMobileNumber, "120", "json")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : CommonDisposable<GetCallResponse>() {

                    override fun onSuccessResponse(getdata: GetCallResponse) {

                    }

                    override fun onErrorResponse(e: Throwable) {

                    }

                    override fun noNetowork() {
                       // Toast.makeText(mcontext, "No network call ", Toast.LENGTH_LONG).show()
                    }
                })
        }
    }
}