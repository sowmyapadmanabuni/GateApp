package com.oyespace.guards.getOTPbyCall

import com.oyespace.guards.pojo.GetOTPbyCallReq
import com.oyespace.guards.pojo.OTPbyCallResponse
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST

interface GetOTPbyCallApi {

    @POST("retryotp.php?")
    fun sendOTPbyCallReq(@Body getOTPbyCallReq: GetOTPbyCallReq)
            : Single<OTPbyCallResponse>


}