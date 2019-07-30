package com.oyespace.guards.fcm


import com.oyespace.guards.pojo.*
import com.oyespace.guards.request.FingerPrintCreateReq
import com.oyespace.guards.responce.FingerPrintCreateResp
import com.oyespace.guards.utils.ConstantUtils.*
import io.reactivex.Single
import retrofit2.http.*

/**
 * Created by Kalyan on 10/13/2017.
 */
interface FCMWebApi {

    @POST("send")
    fun sendFCM_VisitorEntry(@Header(FCMAuth) token:String, @Body visitorEntryFCMReq:VisitorEntryFCMReq)
            :Single<VisitorEntryFCMResp>


}