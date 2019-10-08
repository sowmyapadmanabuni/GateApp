package com.oyespace.guards.com.oyespace.guards.resident

import android.annotation.SuppressLint
import com.oyespace.guards.network.CommonDisposable
import com.oyespace.guards.network.RetrofitClinet
import com.oyespace.guards.pojo.ResidentCheckReq
import com.oyespace.guards.pojo.ResidentCheckResponse
import com.oyespace.guards.utils.ConstantUtils.OYE247TOKEN
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class ResidentChecker {

    @SuppressLint("CheckResult")
    fun isResident(phone: String, ascId: Int, listener: ResponseListener) {

        RetrofitClinet.instance
            .checkIfResident(OYE247TOKEN, ResidentCheckReq(phone, ascId))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CommonDisposable<ResidentCheckResponse>() {

                @SuppressLint("DefaultLocale")
                override fun onSuccessResponse(data: ResidentCheckResponse) {

                    listener.onResult(data.data.`object`.message.toLowerCase().equals("Resident".toLowerCase()))

                }

                override fun onErrorResponse(e: Throwable) {
                    listener.onError(e.localizedMessage)
                }

                override fun noNetowork() {

                }

            })


    }

    interface ResponseListener {

        fun onResult(isResident: Boolean)

        fun onError(error: String)

    }

}