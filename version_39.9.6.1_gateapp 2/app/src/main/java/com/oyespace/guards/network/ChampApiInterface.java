package com.oyespace.guards.network;

import com.oyespace.guards.request.InvitationUpdateReq;
import com.oyespace.guards.request.ResidentValidationRequest;
import com.oyespace.guards.request.SendStaffImageReq;
import com.oyespace.guards.responce.InvitationRequestResponse;
import com.oyespace.guards.responce.ResidentValidationResponse;
import com.oyespace.guards.responce.StaffImageRes;
import com.oyespace.guards.responce.SubscriptionResponse;
import com.oyespace.guards.responce.TicketListingTesponse;
import com.oyespace.guards.responce.VisitorLogExitResp;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

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
    Call<InvitationRequestResponse> getInvitationResponse(@Path("id") int invitationID);

    @POST("oye247/api/v1/Invitation/InvitationUsedStatusUpdate")
    @Headers("X-OYE247-APIKey:7470AD35-D51C-42AC-BC21-F45685805BBE")
    Call<InvitationRequestResponse> updateInvitation(@Body InvitationUpdateReq invitationUpdateReq);

    @POST("oyesafe/api/v1/Unit/GetMobileNumberByResident")
    @Headers("X-OYE247-APIKey:7470AD35-D51C-42AC-BC21-F45685805BBE")
    Call<ResidentValidationResponse> residentValidation(@Body ResidentValidationRequest residentValidationRequest);

//
//    @GET("oyesafe/api/v1/GetVisitorLogEntryListByMobileNumber/{}")
//    @Headers("X-OYE247-APIKey:7470AD35-D51C-42AC-BC21-F45685805BBE")
//    Call getVisitorLogEntryList(@Header("X-OYE247-APIKey") token: String,@Path("mobilenumber") mobileno:String):Single<VisitorEntryByMobileNumber>


}
