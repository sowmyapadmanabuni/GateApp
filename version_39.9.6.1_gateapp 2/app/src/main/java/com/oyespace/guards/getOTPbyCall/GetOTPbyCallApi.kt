package com.oyespace.guards.getOTPbyCall

import com.oyespace.guards.pojo.*
import com.oyespace.guards.utils.ConstantUtils
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface GetOTPbyCallApi {

    @POST("retryotp.php?")
    fun sendOTPbyCallReq(@Body getOTPbyCallReq:GetOTPbyCallReq)
            : Single<OTPbyCallResponse>


}