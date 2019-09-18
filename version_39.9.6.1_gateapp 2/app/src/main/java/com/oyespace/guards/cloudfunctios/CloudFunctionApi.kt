package oyespace.guards.cloudfunctios

import com.oyespace.guards.pojo.CloudFunctionNotificationReq
import com.oyespace.guards.pojo.SendGateAppNotificationRequest
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST

interface CloudFunctionApi {
//https://us-central1-oyespace-dc544.cloudfunctions.net/sendAdminNotificationFromKotlin
    @POST("sendAdminNotificationFromKotlin")
    fun sendCloud_VisitorEntry(@Body cloudFunctionNotificationReq: CloudFunctionNotificationReq)
            : Single<Any>

    @POST("sendGateAppNotification")
    fun getNotification(@Body sendGateAppNotificationRequest: SendGateAppNotificationRequest)
            : Single<Any>

}