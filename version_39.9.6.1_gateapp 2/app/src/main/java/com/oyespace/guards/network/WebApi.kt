package com.oyespace.guards.network


import com.oyespace.guards.pojo.*
import com.oyespace.guards.request.FingerPrintCreateReq
import com.oyespace.guards.responce.FingerPrintCreateResp
import com.oyespace.guards.utils.ConstantUtils.CHAMPKEY
import com.oyespace.guards.utils.ConstantUtils.OYE247KEY
import io.reactivex.Single
import retrofit2.http.*

/**
 * Created by Kalyan on 10/13/2017.
 */
interface WebApi {

    @GET("oye247/api/v1/CheckPoint/GetCheckPointByAssocID/{id}")
    fun getCheckPointList(@Header("X-OYE247-APIKey") token: String, @Path("id") assid: String)
            : Single<CheckpointListResp<CheckPointByAssocID>>


    @GET("oyesafe/api/v1/VisitorLog/GetVisitorLogListByDCreatedAndAssocID/{id}/{date}")
    fun visitorList(@Header("X-OYE247-APIKey") token: String, @Path("id") assid: String, @Path("date") currentdate: String)
            : Single<GlobalApiObject<ArrayList<VisitorLog>>>

    //http://apidev.oyespace.com/oyeliving/api/v1/Unit/GetUnitListByAssocID/2
    @GET("oyeliving/api/v1/Unit/GetUnitListByAssocID/{id}")
    fun unitList(@Header("X-Champ-APIKey") token: String, @Path("id") assid: String)
            : Single<UnitList<ArrayList<UnitPojo>>>

    @POST("oye247/api/v1/Worker/Create")
    fun creatStaff(@Body staffReq: StaffRegistrationReq, @Header("X-OYE247-APIKey") token: String)
            : Single<CreateStaffResponse<WorkerData>>

    @POST("oyesafe/api/v1/VisitorExitWIDAndTime/Update")
    fun visitorExitCall(@Header("X-OYE247-APIKey") token: String, @Body visitorExitReq: VisitorExitReq)
            : Single<VisitorExitResp>

    //http://api.oyespace.com/oyeliving/api/v1/account/sendotp
    @POST("oyeliving/api/v1/account/sendotp")
    fun getOTPCall(@Header(CHAMPKEY) token: String, @Body visitorExitReq: GetOTPReq)
            : Single<GetOTPResp>

    @POST("oyeliving/api/v1/account/verifyotp")
    fun getVerifyOTP(@Header(CHAMPKEY) token: String, @Body otprequest: GetVerifyOTPRequest): Single<GetVerifyOTPResponse>

    @GET("oyesafe/api/v1/ServiceProviders/GetServiceProvidersList")
    fun serviceProviderList(@Header("X-OYE247-APIKey") token: String): Single<SeviceProviderListResponse>

    //http://apidev.oyespace.com/oyesafe/api/v1/VisitorLog/Create
    @POST("oyesafe/api/v1/VisitorLog/Create")
    fun createVisitorLogCall(@Header(OYE247KEY) token: String, @Body createVisitorLogReq: CreateVisitorLogReq): Single<CreateVisitorLogResp<VLRData>>

    @POST("oyesafe/api/v1/VisitorEntryWIDAndTime/Update")
    fun visitorEntryCall(@Header("X-OYE247-APIKey") token: String, @Body visitorExitReq: VisitorEntryReq)
            : Single<VisitorExitResp>


    @GET("oye247/api/v1/GetWorkerListByAssocID/{id}")
    fun workerList(@Header("X-OYE247-APIKey") token: String, @Path("id") assid: String)
            : Single<GetWorkerListbyAssnIDResp<WorkerListbyAssnIDData>>

    @POST("oyesafe/api/v1/FingerPrint/Create")
    fun createFingerPrintCall(@Header(OYE247KEY) token: String, @Body fingerPrintCreateReq: FingerPrintCreateReq)
            : Single<FingerPrintCreateResp>

    //    this function  check the number exists as guard or not
    @POST("oye247/api/v1/MobileNumberVerification")
    fun verifyMobileNumber(@Header(OYE247KEY) token: String, @Body mobilenumber: SendMobileVerification):
            Single<VerifyMobileData<MessageData>>

    //http://api.oyespace.com/oyesafe/api/v1/Device/GetDeviceByMobileNumberIMEI
    @POST("oyesafe/api/v1/Device/GetDeviceByMobileNumberIMEI")
    fun getDeviceInfobyMobImeiCall(@Header(OYE247KEY) token: String, @Body getDeviceInfoReq: GetDeviceInfobyMobImeiReq)
            : Single<GetDeviceInfobyMobImeiResp<Device>>

    @GET("oyeliving/api/v1/association/getassociationlist/{id}")
    fun getAssociationInfocall(@Header(CHAMPKEY) token: String, @Path("id") assid: String)
            : Single<GetAssnInfoResp<Association>>

    @POST("oyeliving/api/v1/Account/GetAccountDetailsByMobileNumber")
    fun GetAccountDetailsByMobCall(@Header(CHAMPKEY) token: String, @Body getAccountDetailsByMobReq: GetAccountDetailsByMobReq)
            : Single<GetAccountDetailsByMobResp<AccountByMobile>>

    @POST("oyeliving/api/v1/account/signup")
    fun signUpCall(@Header(CHAMPKEY) token: String, @Body signUpReq: SignUpReq)
            : Single<SignUpResp<Account>>

    @POST("oye247/api/v1/Patrolling/create")
    fun startPatrollingCall(@Header(OYE247KEY) token: String, @Body startPatrollingReq: StartPatrollingReq)
            : Single<StartPatrollingResp>

    @POST("oye247/api/v1/Patrolling/PatrollingEndDateUpdate")
    fun stopPatrollingCall(@Header(OYE247KEY) token: String, @Body stopPatrollingReq: StopPatrollingReq)
            : Single<StopPatrollingResp>

    @POST("oye247/api/v1/Tracking/Create")
    fun savePatrollingPointsCall(@Header(OYE247KEY) token: String, @Body visitorExitReq: VisitorEntryReq)
            : Single<VisitorExitResp>

    @GET("oyesafe/api/v1/FingerPrint/GetFingerPrintListByFMIDAndMemType/{id}/{memType}")
    fun getStaffBiometric(@Header(OYE247KEY) token: String, @Path("id") wrkId: Int, @Path("memType") MemType: String)
            : Single<StaffBiometricResp<StaffBiometricData>>

    @GET("oye247/api/v1/GetVisitorLogEntryListByAssocID/{id}")
    fun getVisitorLogEntryList(@Header(OYE247KEY) token: String, @Path("id") assnId: Int)
            : Single<VisitorLogEntryResp<ArrayList<VisitorEntryLog>>>

    @GET("oye247/api/v1/TicketingResponse/GetTicketingResponseListByTicketingResID/{id}")
    fun getTicketingResponses(@Header(OYE247KEY) token: String, @Path("id") ticketID: String)
            : Single<GetTicketingResponsesRes<TicketingResponseData>>

    @GET("oyesafe/api/v1/Device/GetDeviceListByAssocID/{id}")
    fun getDeviceListResponse(@Header("X-OYE247-APIKey") token: String, @Path("id") assid: String):Single<getDeviceList>


    @GET("oyesafe/api/v1/VisitorLog/GetVisitorLogListByWorkerID/{workerid}/{id}")
    fun getVisitorByWorkerId(@Header("X-OYE247-APIKey")token: String,@Path("workerid")workerId:Int,@Path("id")assid:Int):Single<getVisitorDataByWorker>

}