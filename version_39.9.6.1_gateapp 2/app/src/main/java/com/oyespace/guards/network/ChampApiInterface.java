package com.oyespace.guards.network;

import com.oyespace.guards.request.FingerPrintCreateReq;
import com.oyespace.guards.request.InvitationUpdateReq;
import com.oyespace.guards.request.SendStaffImageReq;
import com.oyespace.guards.responce.*;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * Created by Basavarajesh Koni on 4/2/2018.
 */

public interface ChampApiInterface {

    //Subscription By AssocID
    @GET("oyesafe/api/v1/Subscription/GetLatestSubscriptionByAssocID/{id}")
    @Headers("X-OYE247-APIKey:7470AD35-D51C-42AC-BC21-F45685805BBE")
    Call<SubscriptionResponse> getLatestSubscription(@Path("id") String associationID);

    @GET("oye247/api/v1/GetVisitorLogExitListByAssocID/{id}")
    @Headers("X-OYE247-APIKey:7470AD35-D51C-42AC-BC21-F45685805BBE")
    Call<VisitorLogExitResp> getVisitorLogExitList(@Path("id")String associationID);

    @GET("oye247/api/v1/GetTicketingListByTicketingID/1141{id}")
    @Headers("X-OYE247-APIKey:7470AD35-D51C-42AC-BC21-F45685805BBE")
    Call<TicketListingTesponse> getTicketingListResponse( @Path("id") int ticketID);


    @POST("oye247/api/v1/Worker/WorkerEntryImgGPSUpdate")
    @Headers("X-OYE247-APIKey:7470AD35-D51C-42AC-BC21-F45685805BBE")
    Call<StaffImageRes>sendStaffImage(@Body SendStaffImageReq sendStaffImageReq);

    @GET("oye247/api/v1/Invitation/GetInvitationByInvitationID/{id}")
    @Headers("X-OYE247-APIKey:7470AD35-D51C-42AC-BC21-F45685805BBE")
    Call<InvitationRequestResponse> getInvitationResponse( @Path("id") int invitationID);

    @POST("oye247/api/v1/Invitation/InvitationUsedStatusUpdate")
    @Headers("X-OYE247-APIKey:7470AD35-D51C-42AC-BC21-F45685805BBE")
    Call<InvitationRequestResponse>updateInvitation(@Body InvitationUpdateReq invitationUpdateReq);

//
//    @GET("oyesafe/api/v1/GetVisitorLogEntryListByMobileNumber/{}")
//    @Headers("X-OYE247-APIKey:7470AD35-D51C-42AC-BC21-F45685805BBE")
//    Call getVisitorLogEntryList(@Header("X-OYE247-APIKey") token: String,@Path("mobilenumber") mobileno:String):Single<VisitorEntryByMobileNumber>


}
