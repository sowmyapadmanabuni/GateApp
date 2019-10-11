package com.oyespace.guards.network


import com.oyespace.guards.models.*
import com.oyespace.guards.pojo.*
import com.oyespace.guards.pojo.VisitorEntryLog
import com.oyespace.guards.pojo.VisitorLog
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

    @GET("oye247/api/v1/GetWorkersListByWorkerTypeAndAssocID/{assnId}/{type}")
    fun getGuardsList(@Header("X-OYE247-APIKey") token: String, @Path("assnId") assid: String, @Path("type") type: String)
            : Single<GetGuardsListResponse<GuardsList>>

    @GET("oyeliving/api/v1/Block/GetBlockListByAssocID/{id}")
    fun blocksList(@Header("X-Champ-APIKey") token: String, @Path("id") assid: String)
            : Single<BlocksList<ArrayList<BlocksData>>>

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
    @POST("oyesafe/api/v1/VisitorLogcreate/Create")
    fun createVisitorLogCall(@Header(OYE247KEY) token: String, @Body createVisitorLogReq: CreateVisitorLogReq): Single<CreateVisitorLogResp<VLRData>>

    @POST("oyesafe/api/v1/VisitorEntryWIDAndTime/Update")
    fun visitorEntryCall(@Header("X-OYE247-APIKey") token: String, @Body visitorExitReq: VisitorEntryReq)
            : Single<VisitorExitResp>


    @GET("oye247/api/v1/GetWorkerListByAssocID/{id}")
    fun workerList(@Header("X-OYE247-APIKey") token: String, @Path("id") assid: String)
            : Single<GetWorkerListbyAssnIDResp<WorkerListbyAssnIDData>>

    @POST("oyesafe/api/v1/SOS/SOSStopUpdate")
    fun updateSOS(@Header("X-OYE247-APIKey") token: String, @Body sosUpdateReq: SOSUpdateReq)
            : Single<SOSUpdateResp>

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

    @GET("oye247/api/v1/CheckPoint/GetCheckPointListByCheckPointID/{id}")
    fun getCheckPointInfo(@Header(OYE247KEY) token: String, @Path("id") checkPointId: String)
            : Single<GetCheckPointResponse<CheckPointData>>

    @GET("oyesafe/api/v1/Device/GetDeviceListByAssocID/{id}")
    fun getDeviceListResponse(@Header("X-OYE247-APIKey") token: String, @Path("id") assid: String):Single<getDeviceList>


    @GET("oyesafe/api/v1/VisitorLog/GetVisitorLogListByWorkerID/{workerid}/{id}")
    fun getVisitorByWorkerId(@Header("X-OYE247-APIKey")token: String,@Path("workerid")workerId:Int,@Path("id")assid:Int):Single<getVisitorDataByWorker>

    @POST("oye247/api/v1/Worker/WorkerDetailsUpdate")
    fun staffUpdate(@Header(OYE247KEY) token: String, @Body staffEditRequest:StaffEditRequest):Single<StaffEditResponse>


    @POST("oye247/api/v1/Worker/WorkerEntryImgGPSUpdate")
    fun sendStaffImageUpdate(@Header(OYE247KEY) token: String, @Body sendStaffImageReq: SendStaffImageReq)
            : Single<SendStaffImageRes>

    @POST("oye247/api/v1/GetWorkersListByMobileNumberAndAssocID")
    fun GetWorkersListByMobileNumberAndAssocID(@Header(OYE247KEY) token: String, @Body getWorkersListByMobileNumberReq:GetWorkersListByMobileNumberReq ):
            Single<GetWorkersListByMobileNumberResp>

    @POST("oyesafe/api/v1/Notification/Notificationcreate")
    fun getNotificationCreate(@Header(OYE247KEY) token: String,@Body notificationCreateReq:NotificationCreateReq):Single<NotificationCreateResponse>

    @GET("oyeliving/api/v1/Unit/GetUnitListByUnitID/{id}")
    fun getUnitListbyUnitId(@Header(CHAMPKEY) token: String, @Path("id") unitId: Int): Single<UnitlistbyUnitID>

    @POST("oyeliving/api/v1/Unit/UnitNameSearchByAssociationID")
    fun searchUnits(@Body unitSearch: SearchUnitRequest, @Header("X-Champ-APIKey") token: String)
            : Single<UnitListSearch<UnitPojo>>

    @GET("oyeliving/api/v1/Unit/GetUnitListByBlockID/{id}")
    fun getUnitsFromBlock(@Header("X-Champ-APIKey") token: String, @Path("id") assid: String)
            : Single<UnitsList<ArrayList<UnitPojo>>>

    @POST("oye247/api/v1/Invitation/create")
    fun sendInviteRequest(@Header(OYE247KEY) token: String, @Body inviteCreateReq: InviteCreateReq)
            : Single<InviteCreateRes>

    @GET("oye247/api/v1/Invitation/GetInvitationByInvitationID/{id}")
    fun getInvitationByInvitationID(@Header(CHAMPKEY) token: String, @Path("id") invitationId: String): Single<InviteCreateRes>

    @POST("oye247/api/v1/Invitation/InvitationUsedStatusUpdate")
    fun updateInvitation(@Header(OYE247KEY) token: String, @Body invitationUpdateReq: InvitationUpdateReq): Single<InviteCreateRes>

    @GET("oyesafe/api/v1/GetFamilyMemberListByAssocAndUnitID/{uid}/{aid}/{accid}")
    fun getFamilyMemberList(@Header("X-OYE247-APIKey") token: String, @Path("uid") unUnitID: String, @Path("aid")asAssnID: String,@Path("accid") accountID:String):Single<GetFamilyMemberResponse>

    @POST("oyesafe/api/v1/Unit/GetMobileNumberByResident")
    fun residentValidation(@Header("X-OYE247-APIKey") token: String, @Body residentValidationRequest: ResidentValidationRequest)
            : Single<ResidentValidationResponse>

    @GET("oye247/api/v1/GetCheckPointNamesByPatrollingSchedule/{schedId}")
    fun scheduleCheckPointsList(@Header("X-OYE247-APIKey") token: String, @Path("schedId") scheduleId: String)
            : Single<CheckPointsOfSheduleListResponse<ArrayList<PatrolShift>>>

    @GET("oye247/api/v1/GetPatrollingShiftsListByGateNameANDAssociationID/{gate}/{assnId}/")
    fun patrolScheduleList(@Header("X-OYE247-APIKey") token: String, @Path("gate") gate: String, @Path("assnId") assnId: String)
            : Single<ShiftsListResponse<ArrayList<PatrolShift>>>

    @GET("oyesafe/api/v1/Subscription/GetLatestSubscriptionByAssocID/{id}")
    fun getSubscriptionData(@Header("X-OYE247-APIKey") token: String,@Path("id") assid: String):Single<SubscriptionResp>
}