package com.oyespace.guards.pojo

import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.oyespace.guards.com.oyespace.guards.pojo.BlocksData
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class SearchResult(
    val name: String?,
    val isHeader: Boolean = false,
    val subText: String? = "",
    val lat: String = "",
    val lng: String = ""
) : Parcelable

data class GlobalApiObject<T>(val success: Boolean?, val apiVersion: String?, val data: VisitorLog)
data class VisitorLog(val visitorlogbydate: ArrayList<Visitorlogbydate>)

data class Visitorlogbydate(
    val asAssnID: Int?,
    val fmid: Int?,
    val meMemID: Int?,
    val reRgVisID: Int,
    val unUniName: String,
    val unUnitID: Int,
    val vlCmntImg: String,
    val vlCmnts: String,
    val vlComName: String,
    val vlEntryT: String,
    val vlEntyWID: Int,
    var vlExitT: String,
    val vlExitWID: Int,
    val vlGtName: String,
    val vlIsActive: Boolean,
    val vlItmCnt: Int,
    val vlMobile: String,
    val vlPrmBy: String,
    val vlPrmStat: String,
    val vlVehNum: String,
    val vlVehType: String,
    val vlVerStat: String,
    val vlVisCnt: Int,
    val vlVisImgN: String,
    val vlVisLgID: Int,
    val vlVisType: String,
    val vldCreated: String,
    val vldUpdated: String,
    val vlfName: String,
    val vllName: String,
    val vlpOfVis: String
)



data class UnitListSearch<T>(
    val apiVersion: String,
    val `data`: UnitDataSingle,
    val success: Boolean
)

data class UnitDataSingle(
    val unit: UnitPojo
)


data class UnitList<T>(
    val apiVersion: String,
    val `data`: UnitData,
    val success: Boolean
)

data class UnitData(
    val unit: ArrayList<UnitPojo>
)

data class UnitPojo(
    val acAccntID: Int,
    val asAssnID: Int,
    val blBlockID: Int,
    val flFloorID: Int,
    val owner: ArrayList<UnitOwner>,
    val tenant: ArrayList<Tenant>,
    val unCalType: String,
    val unCurrBal: Double,
    val unDimens: Int,
    val unIsActive: Boolean,
    val unOcSDate: String,
    val unOcStat: String,
    val unOpenBal: Double,
    val unOwnStat: String,
    val unRate: Double,
    val unSldDate: String,
    val unUniIden: String,
    val unUniName: String,
    val unUniType: String,
    val unUnitID: Int,
    val undCreated: String,
    val undUpdated: String,
    val unitParkingLot: List<Any>,
    val unitbankaccount: Any,
    var isSelected: Boolean=false

)
data class Tenant(
    val acAccntID: Int,
    val asAssnID: Int,
    val blBlockID: Int,
    val unUnitID: Int,
    val utEmail: String,
    val utEmail1: String,
    val utIsActive: Boolean,
    val utMobile: String,
    val utMobile1: String,
    val utdCreated: String,
    val utdUpdated: String,
    val utfName: String,
    val utid: Int,
    val utisdCode: Any,
    val utlName: String,
    val utoEndD: String,
    val utoStrtD: String
)

data class UnitOwner(
    val acAccntID: Int,
    val asAssnID: Int,
    val blBlockID: Int,
    val unUnitID: Int,
    val uoEmail: String,
    val uoEmail1: String,
    val uoEmail2: String,
    val uoEmail3: String,
    val uoEmail4: String,
    val uoIsActive: Boolean,
    val uoMobile: String,
    val uoMobile1: String,
    val uoMobile2: String,
    val uoMobile3: String,
    val uoMobile4: String,
    val uocdAmnt: Double,
    val uodCreated: String,
    val uodUpdated: String,
    val uofName: String,
    val uoid: Int,
    val uoisdCode: Any,
    val uolName: String
)
data class UnitPojo2(
    val acAccntID: Int,
    val asAssnID: Int,
    val blBlockID: Int,
    val flFloorID: Int,
    val owner: ArrayList<Owner>,
    val tenant: ArrayList<Tenant>,
    val unCalType: String,
    val unCurrBal: Int,
    val unDimens: Int,
    val unIsActive: Boolean,
    val unOcSDate: String,
    val unOcStat: String,
    val unOpenBal: Int,
    val unOwnStat: String,
    val unRate: Int,
    val unSldDate: String,
    val unUniIden: String,
    val unUniName: String,
    val unUniType: String,
    val unUnitID: Int,
    val undCreated: String,
    val undUpdated: String,
    val unitParkingLot: List<UnitParkingLot>,
    val unitbankaccount: Any
)
data class Owner(
    val acAccntID: Int,
    val asAssnID: Int,
    val unUnitID: Int,
    val uoEmail: String,
    val uoEmail1: String,
    val uoEmail2: String,
    val uoEmail3: String,
    val uoEmail4: String,
    val uoIsActive: Boolean,
    val uoMobile: String,
    val uoMobile1: String,
    val uoMobile2: String,
    val uoMobile3: String,
    val uoMobile4: String,
    val uocdAmnt: Double,
    val uodCreated: String,
    val uodUpdated: String,
    val uofName: String,
    val uoid: Int,
    val uoisdCode: Any,
    val uolName: String
)


data class UnitParkingLot(
    val acAccntID: Int,
    val asAssnID: Int,
    val meMemID: Int,
    val unUnitID: Int,
    val upIsActive: Boolean,
    val upSrtD: String,
    val updCreated: String,
    val updUpdated: String,
    val upgpsPnt: String,
    val upid: Int,
    val uplNum: String
)


data class StaffRegistrationReq(
    val ASAssnID: Int,
    val BLBlockID: Int,
    val EmailID: String,
    val FLFloorID: Int,
    val OYEMemberID: Int,
    val OYEMemberRoleID: Int,
    val VNVendorID: Int,
    val WKDesgn: String,
    val WKFName: String,
    val WKIDCrdNo: String,
    val WKISDCode: String,
    val WKLName: String,
    val WKMobile: String,
    val WKWrkType: String,
    val UNUnitID : Int,
    val UNUniName : String,
val WKEntryImg: String
)


//this will do the demo part



//Create staff response
data class CreateStaffResponse<T>(
    val apiVersion: String,
    val `data`: WorkerData,
    val success: Boolean
)

data class WorkerData(
    val worker: Worker
)

data class Worker(
    val asAssnID: Int,
    val blBlockID: Int,
    val flFloorID: Int,
    val vnVendorID: Int,
    val wkDesgn: String,
    val WKEntryImg: String,
    val wkIsActive: Boolean,
    val wkMobile: String,
    val wkWorkID: Int,
    val wkWrkType: String,
    val wkdCreated: String,
    val wkdUpdated: String,
    val wkfName: String,
    val wkidCrdNo: String,
    val wkisdCode: Any,
    val wklName: String
)


// Visitor entry request
data class VisitorEntryReq(
    val VLEntryT: String,
    val VLEntyWID: Int,
    val VLVisLgID: Int
)
//visitor exit request
data class VisitorExitReq(
    val VLExitT: String,
    val VLExitWID: Int,
    val VLVisLgID: Int
)
//Visitor exit response
data class VisitorExitResp(
    val apiVersion: String,
    val data: String,
    val success: Boolean
)

//Patrolling Create Req
data class StartPatrollingReq(
    val ASAssnID: Int,
    val WKWorkID: Int,
    val wkfName: String,
    val PSPtrlSID : Int
)

//Patrolling  Start Resp
data class StartPatrollingResp(
    val apiVersion: String,
    val `data`: StartPatrollingData,
    val success: Boolean
)

data class StartPatrollingData(
    val patrolling: Patrolling
)

data class Patrolling(
    val asAssnID: Int,
    val psPtrlSID: Int,
    val ptIsActive: Boolean,
    val ptPtrlID: Int,
    val ptdCreated: String,
    val ptdUpdated: String,
    val pteDateT: String,
    val ptsDateT: String,
    val wkfName: Any
)

//Patrolling stop Req
data class StopPatrollingReq(
    val PTEDateT: String,
    val PTPtrlID: Int
)

//Patrolling stop Resp
data class StopPatrollingResp(
    val apiVersion: String,
    val `data`: Any,
    val success: Boolean
)

//GET OTP Request
data class GetOTPReq(
    val CountryCode: String,
    val MobileNumber: String
)

//GET OTP response
data class GetOTPResp(
    val apiVersion: String,
    val data: String,
    val success: Boolean
)

// Worker List Response
data class WorkerListResponse(
    val apiVersion: String,
    val `data`: WorkerListData,
    val success: Boolean
)

data class WorkerListData(
    val workers: ArrayList<WorkerList>
)

data class WorkerList(
    val asAssnID: Int,
    val blBlockID: Int,

    val vnVendorID: Int,
    val wkDesgn: String,
    val WKEntryImg: String,
    val wkIsActive: Boolean,
    val wkMobile: String,
    val wkWorkID: Int,
    val wkWrkType: String,
    val wkdCreated: String,
    val wkdUpdated: String,
    val wkfName: String,
    val wkidCrdNo: String,
    val wkisdCode: Any,
    val wklName: String
)

data class GetWorkerListbyAssnIDResp<T>(
    val apiVersion: String,
    val `data`: WorkerListbyAssnIDData,
    val success: Boolean
)

data class WorkerListbyAssnIDData(
    val worker: ArrayList<WorkerDetails>
)

data class WorkerDetails(
    val asAssnID: Int,
    val blBlockID: Int,

    val unUnitID: Int,
    val unUniName: String,
    val vnVendorID: Int,
    val wkDesgn: String,
    val WKEntryImg: String,
    val wkIsActive: Boolean,
    val wkMobile: String,
    val wkWorkID: Int,
    val wkWrkType: String,
    val wkdCreated: String,
    val wkdUpdated: String,
    val wkfName: String,
    val wkidCrdNo: String,
    val wkisdCode: Any,
    val wklName: String
)

//Finger print req
data class FingerPrintCreateReq1(
    val ASAssnID: Int,
    val FMID: Int,
    val FPFngName: String,
    val FPImg1: String,
    val FPImg2: String,
    val FPImg3: String,
    val FPMemType: String
)
//Finger Print Response
data class FingerPrintCreateResp1(
    val apiVersion: String,
    val `data`: FingerPrintData,
    val success: Boolean
)

data class FingerPrintData(
    val fingerPrint: FingerPrint
)

data class FingerPrint(
    val asAssnID: Int,
    val fmid: Int,
    val fpFngName: String,
    val fpImg1: String,
    val fpImg2: String,
    val fpImg3: String,
    val fpIsActive: Boolean,
    val fpMemType: String,
    val fpdCreated: String,
    val fpdUpdated: String,
    val fpid: Int
)
//ServiceProvider List Response
data class SeviceProviderListResponse(
    val apiVersion: String,
    val `data`: SeviceProviderData,
    val success: Boolean
)

data class SeviceProviderData(
    val serviceProviders: ArrayList<ServiceProvider>
)

data class ServiceProvider(
    val coid: Int,
    val ctName: String,
    val spStatus: Boolean,
    val spType: String,
    val spdCreated: String,
    val spImgPath: String,
    val spid: Int
)
data class SeviceProviderListResponse1(
    val apiVersion: String,
    val `data`: SeviceProviderData,
    val success: Boolean
)

data class SeviceProviderData1(
    val courier: ArrayList<SPData>,
    val food: ArrayList<SPData>,
    val groceries: ArrayList<SPData>,
    val service: ArrayList<SPData>,
    val shopping: ArrayList<SPData>
)

data class SPData(
    val coid: Int,
    val ctName: String,
    val spStatus: Boolean,
    val spType: String,
    val spdCreated: String,
    val spid: Int
)

data class Food(
    val coid: Int,
    val ctName: String,
    val spStatus: Boolean,
    val spType: String,
    val spdCreated: String,
    val spid: Int
)

data class Service(
    val coid: Int,
    val ctName: String,
    val spStatus: Boolean,
    val spType: String,
    val spdCreated: String,
    val spid: Int
)

data class Grocery(
    val coid: Int,
    val ctName: String,
    val spStatus: Boolean,
    val spType: String,
    val spdCreated: String,
    val spid: Int
)

data class Shopping(
    val coid: Int,
    val ctName: String,
    val spStatus: Boolean,
    val spType: String,
    val spdCreated: String,
    val spid: Int
)

data class Courier(
    val coid: Int,
    val ctName: String,
    val spStatus: Boolean,
    val spType: String,
    val spdCreated: String,
    val spid: Int
)


//visitor log req
data class CreateVisitorLogReq(
    val ASAssnID: Int,
    val MEMemID: Int,
    val RERgVisID: Int,
    val UNUniName: String,
    val UNUnitID: Int,
    val VLComName: String,
    val VLFName: String,
    val VLGtName: String,
    val VLItmCnt: Int,
    val VLLName: String,
    val VLMobile: String,
    val VLPOfVis: String,
    val VLVehNum: String,
    val VLVehType: String,
    val VLVerStat: String,
    val VLVisCnt: Int,
    val VLVisType: String,
    val SPPrdImg1: String,
    val SPPrdImg2: String,
    val SPPrdImg3: String,
    val SPPrdImg4: String,
    val SPPrdImg5: String,
    val SPPrdImg6: String,
    val SPPrdImg7: String,
    val SPPrdImg8: String,
    val SPPrdImg9: String,
    val SPPrdImg10: String,
val WKSelfImg:String,
val VLEntryImg:String


)

//visitor log resp
data class CreateVisitorLogResp<T>(
    val apiVersion: String,
    val `data`: VLRData,
    val success: Boolean
)

data class VLRData(
    val visitorLog: VisitorLogResp
)

data class VisitorLogResp(
    val asAssnID: Int,
    val endDate: String,
    val fmid: Int,
    val meMemID: Int,
    val reRgVisID: Int,
    val startDate: String,
    val unUniName: Any,
    val unUnitID: Int,
    val vlCmntImg: Any,
    val vlCmnts: Any,
    val vlComName: Any,
    val vlEntryT: String,
    val vlEntyWID: Int,
    val vlExitT: String,
    val vlExitWID: Int,
    val vlGtName: Any,
    val vlIsActive: Boolean,
    val vlItmCnt: Int,
    val vlMobile: Any,
    val vlPrmBy: Any,
    val vlPrmStat: Any,
    val vlVehNum: Any,
    val vlVehType: Any,
    val vlVerStat: Any,
    val vlVisCnt: Int,
    val vlVisImgN: Any,
    val vlVisLgID: Int,
    val vlVisType: Any,
    val vldCreated: String,
    val vldUpdated: String,
    val vlfName: Any,
    val vllName: Any,
    val vlpOfVis: Any
)


/// Get device info


data class GetDeviceInfobyMobImeiReq(
    val DEIMEI: String,
    val DEMobileNo: String
)

//get Worker / device info
//data class GetDeviceInfobyMobImeiResp<T>(
//    val apiVersion: String,
//    val `data`: DeviceInfobyMobImeiData,
//    val success: Boolean
//)
//
//
//data class DeviceInfobyMobImeiData(
//    val device: List<Device>
//)
//
//data class Device(
//    val asAssnID: Int,
//    val deMobileNo: String,
//    val deStatus: Boolean,
//    val deid: Int,
//    val deimei: String
//)
data class GetDeviceInfobyMobImeiResp<T>(
    val apiVersion: String,
    val `data`: DeviceInfobyMobImeiData,
    val success: Boolean
)

data class DeviceInfobyMobImeiData(
    val device: List<Device>
)

data class Device(
    val asAssnID: Int,
    val deGateNo: String,
    val deMobileNo: String,
    val deStatus: Boolean,
    val deTokenID: String,
    val dedCreated: String,
    val dedUpdated: String,
    val deid: Int,
    val deimei: String
)



/*
* verifing the mobile number by sumeeth TODO:mobile verification
*
* */


data class SendMobileVerification(
    val WKMobile:String
)
data class VerifyMobileData<T>(
    val apiVersion: String,
    val data :MessageData,
    val success: Boolean
)


data class MessageData(
    val message:String
)

/*
* verifing the mobile number ended
* */

data class DeviceInfoData(
    val workers: Workers
)

data class Workers(
    val asAssnID: Int,
    val blBlockID: Int,

    val unUnitID: Int,
    val vnVendorID: Int,
    val wkDesgn: String,
    val WKEntryImg: String,
    val wkIsActive: Boolean,
    val wkMobile: String,
    val wkWorkID: Int,
    val wkWrkType: String,
    val wkdCreated: String,
    val wkdUpdated: String,
    val wkfName: String,
    val wkidCrdNo: String,
    val wkisdCode: Any,
    val wklName: String
)


//Association by association ID Response
data class GetAssnInfoResp<T>(
    val apiVersion: String,
    val `data`: AssnData,
    val success: Boolean
)

data class AssnData(
    val association: Association
)

data class Association(
    val asAddress: String,
    val asAlexaItg: Boolean,
    val asAsnEmail: String,
    val asAsnLogo: String,
    val asAsnName: String,
    val asAssnID: Int,
    val asCity: String,
    val asCorItg: Boolean,
    val asCountry: String,
    val asFaceDet: String,
    val asIsActive: Boolean,
    val asMgrEmail: String,
    val asMgrMobile: String,
    val asMgrName: String,
    val asMtDimBs: Int,
    val asMtFRate: Int,
    val asMtType: String,
    val asNofBlks: Int,
    val asNofUnit: Int,
    val asOkGItg: Boolean,
    val asPinCode: String,
    val asPrpCode: String,
    val asPrpName: String,
    val asPrpType: String,
    val asRefCode: String,
    val asRegrNum: String,
    val asSiriItg: Boolean,
    val asState: String,
    val asTrnsCur: String,
    val asUniMsmt: String,
    val asWebURL: String,
    val asaInvc: Boolean,
    val asaiPath: String,
    val asavPymnt: Boolean,
    val asbGnDate: String,
    val asbToggle: Boolean,
    val asciPath: String,
    val asdCreated: String,
    val asdPyDate: String,
    val asdUpdated: String,
    val asgpsPnt: Any,
    val asgstNo: String,
    val asiCrFreq: Int,
    val aslpChrg: Int,
    val aslpcType: String,
    val aslpsDate: String,
    val asokgiPath: String,
    val asoloStat: String,
    val asomStat: String,
    val asonStat: String,
    val asopStat: String,
    val asotpStat: String,
    val aspanDoc: String,
    val aspanNum: String,
    val aspanStat: String,
    val assiPath: String,
    val bankDetails: List<BankDetail>
)

data class BankDetail(
    val asAssnID: Int,
    val baActBal: Long,
    val baActID: Int,
    val baActNo: String,
    val baActType: String,
    val baIsActive: Boolean,
    val baIsDeft: Boolean,
    val babName: String,
    val badCreated: String,
    val badUpdated: String,
    val baifsc: String
)


//// mobile number account
data class GetAccountDetailsByMobReq(
    val ACISDCode: String,
    val ACMobile: String
)

data class GetAccountDetailsByMobResp<T>(
    val apiVersion: String,
    val `data`: AccountDetailsData,
    val success: Boolean
)

data class AccountDetailsData(
    val accountByMobile: List<AccountByMobile>
)

data class AccountByMobile(
    val acAccntID: Int,
    val acE1Vfied: Boolean,
    val acE2Vfied: Boolean,
    val acE3Vfied: Boolean,
    val acE4Vfied: Boolean,
    val acEmail: String,
    val acEmail1: String,
    val acEmail2: String,
    val acEmail3: String,
    val acEmail4: String,
    val acIsActive: Boolean,
    val acM1Vfied: Boolean,
    val acM2Vfied: Boolean,
    val acM3Vfied: Boolean,
    val acM4Vfied: Boolean,
    val acMobile: String,
    val acMobile1: String,
    val acMobile2: String,
    val acMobile3: String,
    val acMobile4: String,
    val acdCreated: String,
    val acdUpdated: String,
    val aceVfied: Boolean,
    val acfName: String,
    val acisdCode: String,
    val acisdCode1: String,
    val acisdCode2: String,
    val acisdCode3: String,
    val acisdCode4: String,
    val aclName: String,
    val acmVfied: Boolean
)


// Sign Up API Request

data class SignUpReq(
    val ACEmail: String,
    val ACEmail1: String,
    val ACEmail2: String,
    val ACEmail3: String,
    val ACEmail4: String,
    val ACFName: String,
    val ACISDCode: String,
    val ACISDCode1: String,
    val ACISDCode2: String,
    val ACISDCode3: String,
    val ACISDCode4: String,
    val ACLName: String,
    val ACMobile: String,
    val ACMobile1: String,
    val ACMobile2: String,
    val ACMobile3: String,
    val ACMobile4: String
)


data class SignUpResp<T>(
    val apiVersion: String,
    val `data`: SignUpData,
    val success: Boolean
)

data class SignUpData(
    val accessToken: String,
    val account: Account
)

data class Account(
    val acAccntID: Int,
    val acE1Vfied: Boolean,
    val acE2Vfied: Boolean,
    val acE3Vfied: Boolean,
    val acE4Vfied: Boolean,
    val acEmail: String,
    val acEmail1: String,
    val acEmail2: String,
    val acEmail3: String,
    val acEmail4: String,
    val acIsActive: Boolean,
    val acM1Vfied: Boolean,
    val acM2Vfied: Boolean,
    val acM3Vfied: Boolean,
    val acM4Vfied: Boolean,
    val acMobile: String,
    val acMobile1: String,
    val acMobile2: String,
    val acMobile3: String,
    val acMobile4: String,
    val acdCreated: String,
    val acdUpdated: String,
    val aceVfied: Boolean,
    val acfName: String,
    val acisdCode: String,
    val acisdCode1: String,
    val acisdCode2: String,
    val acisdCode3: String,
    val acisdCode4: String,
    val aclName: String,
    val acmVfied: Boolean
)

//Verify OTP Requst

data class GetVerifyOTPRequest(
    val CountryCode: String,
    val MobileNumber: String,
    val OTPnumber: String
)

//Verify OTP Response
data class GetVerifyOTPResponse(
    val apiVersion: String,
    val `data`: Data,
    val success: Boolean
)

data class Data(
    val accessToken: String,
    val account: Account1,
    val member: List<Member>
)

data class Member(
    val acAccntID: Int,
    val acMobile: String,
    val asAssnID: Int,
    val meIsActive: Boolean,
    val meMemID: Int,
    val mePhName: String,
    val meVisATyp: String,
    val medCreated: String,
    val medUpdated: String,
    val medndStat: Boolean,
    val medndStop: String,
    val medndStrt: String,
    val melvaGrd: Boolean,
    val melvagSrt: String,
    val melvagStp: String,
    val meosDate: String,
    val meuMemShp: Boolean,
    val mrmRoleID: Int,
    val unUnitID: Int
)

data class Account1(
    val acAccntID: Int,
    val acE1Vfied: Boolean,
    val acE2Vfied: Boolean,
    val acE3Vfied: Boolean,
    val acE4Vfied: Boolean,
    val acEmail: String,
    val acEmail1: String,
    val acEmail2: String,
    val acEmail3: String,
    val acEmail4: String,
    val acIsActive: Boolean,
    val acM1Vfied: Boolean,
    val acM2Vfied: Boolean,
    val acM3Vfied: Boolean,
    val acM4Vfied: Boolean,
    val acMobile: String,
    val acMobile1: String,
    val acMobile2: String,
    val acMobile3: String,
    val acMobile4: String,
    val acdCreated: String,
    val acdUpdated: String,
    val aceVfied: Boolean,
    val acfName: String,
    val acisdCode: String,
    val acisdCode1: String,
    val acisdCode2: String,
    val acisdCode3: String,
    val acisdCode4: String,
    val aclName: String,
    val acmVfied: Boolean
)

// FCM request body
data class VisitorEntryFCMReq(
    val `data`: VisitorEntryFCMData,
    val to: String
)

data class VisitorEntryFCMData(
    val activt: String,
    val assid: Int,
    val entry_type: String,
    val mobile: String,
    val name: String,
    val nr_id: String
)

// FCM response body
data class VisitorEntryFCMResp(
    val message_id: Long
)


//Biometric Response
data class StaffBiometricResp<T>(
    val apiVersion: String,
    val `data`: StaffBiometricData,
    val success: Boolean
)

data class StaffBiometricData(
    val fingerPrint: List<FingerPrintSBD>
)

data class FingerPrintSBD(
    val asAssnID: Int,
    val fmid: Int,
    val fpFngName: String,
    val fpImg1: String,
    val fpImg2: String,
    val fpImg3: String,
    val fpIsActive: Boolean,
    val fpMemType: String,
    val fpdCreated: String,
    val fpdUpdated: String,
    val fpid: Int
)

//VisitorLogEntry
data class VisitorLogEntryResp<T>(
    val apiVersion: String,
    val `data`: VisitorLogData,
    val success: Boolean
)

data class VisitorLogData(
    val visitorLog: ArrayList<VisitorEntryLog>
)

data class VisitorEntryLog(
    val asAssnID: Int,
    val endDate: String,
    val fmid: Int,
    val meMemID: Int,
    val reRgVisID: Int,
    val spPrdImg1: String,
    val spPrdImg10: String,
    val spPrdImg2: String,
    val spPrdImg3: String,
    val spPrdImg4: String,
    val spPrdImg5: String,
    val spPrdImg6: String,
    val spPrdImg7: String,
    val spPrdImg8: String,
    val spPrdImg9: String,
    val startDate: String,
    val unUniName: String,
    val unUnitID: Int,
    val vlCmntImg: String,
    val vlCmnts: String,
    val vlComName: String,
    val vlEntryT: String,
    val vlEntyWID: Int,
    var vlExitT: String,
    val vlExitWID: Int,
    val vlGtName: String,
    val vlIsActive: Boolean,
    val vlItmCnt: Int,
    val vlMobile: String,
    val vlPrmBy: String,
    val vlPrmStat: String,
    val vlVehNum: String,
    val vlVehType: String,
    val vlVerStat: String,
    val vlVisCnt: Int,
    val vlVisImgN: String,
    val vlVisLgID: Int,
    val vlVisType: String,
    val vldCreated: String,
    val vldUpdated: String,
    val vlfName: String,
    val vlgpsPnt: String,
    val vllName: String,
    val vlpOfVis: String
)

///Ticketing Responses
data class GetTicketingResponsesRes<T>(
    val apiVersion: String,
    val `data`: TicketingResponseData,
    val success: Boolean
)

data class TicketingResponseData(
    val ticketingResponse: TicketingResponse
)

data class TicketingResponse(
    val asAssnID: Int,
    val meMemID: Int,
    val tkTktID: Int,
    val trDateT: String,
    val trDetails: String,
    val trIsActive: Boolean,
    val trdCreated: String,
    val trdUpdated: String,
    val trgpsPnt: String,
    val trid: Int,
    val wkWorkID: Int
)
//VisitorLogExit
data class VisitorLogExitResp<T>(
    val apiVersion: String,
    val `data`: VisitorLogExitData,
    val success: Boolean
)

data class VisitorLogExitData(
    val visitorLog: ArrayList<VisitorExitLog>
)

data class VisitorExitLog(
    val asAssnID: Int,
    val endDate: String,
    val fmid: Int,
    val meMemID: Int,
    val reRgVisID: Int,
    val spPrdImg1: String,
    val spPrdImg10: String,
    val spPrdImg2: String,
    val spPrdImg3: String,
    val spPrdImg4: String,
    val spPrdImg5: String,
    val spPrdImg6: String,
    val spPrdImg7: String,
    val spPrdImg8: String,
    val spPrdImg9: String,
    val startDate: String,
    val unUniName: String,
    val unUnitID: Int,
    val vlCmntImg: String,
    val vlCmnts: String,
    val vlComName: String,
    val vlEntryT: String,
    val vlEntyWID: Int,
    val vlExitT: String,
    val vlExitWID: Int,
    val vlGtName: String,
    val vlIsActive: Boolean,
    val vlItmCnt: Int,
    val vlMobile: String,
    val vlPrmBy: String,
    val vlPrmStat: String,
    val vlVehNum: String,
    val vlVehType: String,
    val vlVerStat: String,
    val vlVisCnt: Int,
    val vlVisImgN: String,
    val vlVisLgID: Int,
    val vlVisType: String,
    val vldCreated: String,
    val vldUpdated: String,
    val vlfName: String,
    val vlgpsPnt: String,
    val vllName: String,
    val vlpOfVis: String
)


//Check point list response
data class CheckpointListResp<T>(
    val apiVersion: String,
    val `data`: CheckpointListData,
    val success: Boolean
)

data class CheckpointListData(
    val checkPointListByAssocID: ArrayList<CheckPointByAssocID>
)

data class CheckPointByAssocID(
    val asAssnID: Int,
    val cpChkPntID: Int,
    val cpCkPName: String,
    val cpIsActive: Boolean,
    val cpdCreated: String,
    val cpdUpdated: String,
    val cpgpsPnt: String,
    val meMemID: Int
)
//getDeviceList
data class getDeviceList(
    val apiVersion: String,
    val `data`: DeviceData,
    val success: Boolean
)

data class DeviceData(
    val deviceListByAssocID: ArrayList<DeviceByAssocID>
)

data class DeviceByAssocID(
    val asAssnID: Int,
    val deGateNo: String,
    val deMobileNo: String,
    val deStatus: Boolean,
    val deTokenID: String,
    val dedCreated: String,
    val dedUpdated: String,
    val deid: Int,
    val deimei: String
)
//GetVisitorLogEntryListByMobileNumber
data class VisitorEntryByMobileNumber(
    val apiVersion: String,
    val `data`: VisitorEntryByMobileData,
    val success: Boolean
)

data class VisitorEntryByMobileData(
    val visitorEntryByMobile: ArrayList<VisitorEntryByMobile>
)

data class VisitorEntryByMobile(
    val asAssnID: Int,
    val endDate: String,
    val fmid: Int,
    val meMemID: Int,
    val reRgVisID: Int,
    val spPrdImg1: String,
    val spPrdImg10: String,
    val spPrdImg2: String,
    val spPrdImg3: String,
    val spPrdImg4: String,
    val spPrdImg5: String,
    val spPrdImg6: String,
    val spPrdImg7: String,
    val spPrdImg8: String,
    val spPrdImg9: String,
    val startDate: String,
    val unUniName: String,
    val unUnitID: Int,
    val vlCmntImg: String,
    val vlCmnts: String,
    val vlComName: String,
    val vlEntryGPS: String,
    val vlEntryImg: String,
    val vlEntryT: String,
    val vlEntyWID: Int,
    val vlExitGPS: String,
    val vlExitImg: String,
    val vlExitT: String,
    val vlExitWID: Int,
    val vlGtName: String,
    val vlIsActive: Boolean,
    val vlItmCnt: Int,
    val vlMobile: String,
    val vlPrmBy: String,
    val vlPrmStat: String,
    val vlSelfImg: String,
    val vlVehNum: String,
    val vlVehType: String,
    val vlVerStat: String,
    val vlVisCnt: Int,
    val vlVisLgID: Int,
    val vlVisType: String,
    val vldCreated: String,
    val vldUpdated: String,
    val vlfName: String,
    val vllName: String,
    val vlpOfVis: String
)
//Cloud Function Request
data class CloudFunctionNotificationReq(
    val associationID: Int,
    val associationName: String,
    val ntDesc: String,
    val ntTitle: String,
    val ntType: String,
    val sbSubID: String,
    val userID: Int
)

//sendGateAppNotification
data class SendGateAppNotificationRequest(
    val associationID: Int,
    val associationName: String,
    val ntDesc: String,
    val ntTitle: String,
    val ntType: String,
    val sbSubID: String,
    val userID: Int
)
//getVisitorByWorkerId
data class getVisitorDataByWorker(
    val `data`: VisitorData,
    val apiVersion: String,
    val success: Boolean
)

data class VisitorData(
    val visitorLog: WorkerMultiEntryCheck
)

data class WorkerMultiEntryCheck(

val vlVisLgID: Int,
val vlfName: String,
val vllName: String,
val vlMobile: String,
val vlVisType: String,
val vlComName: String,
val vlpOfVis : String,
val vlSelfImg : String,
val vlVisCnt : Int,
val vlVehNum: String,
val vlVehType: String,
val vlItmCnt: Int,
val unUniName: String,
val vlEntyWID: Int,
val vlEntryImg: String,
val vlExitImg: String,
val vlEntryGPS: String,
val vlExitGPS: String,
val vlExitWID: Int,
val vlEntryT: String,
val vlExitT: String,
val reRgVisID: Int,
val meMemID: Int,
val vlCmnts: String,
val vlCmntImg: String,
val vlVerStat: String,
val vlGtName: String,
val unUnitID: Int,
val vlPrmStat: String,
val vlPrmBy: String,
val fmid: Int,
val asAssnID: Int,
val vldCreated: String,
val vldUpdated: String,
val vlIsActive: Boolean,
val startDate: String,
val endDate: String,
val spPrdImg1: String,
val spPrdImg2: String,
val spPrdImg3: String,
val spPrdImg4: String,
val spPrdImg5: String,
val spPrdImg6: String,
val spPrdImg7: String,
val spPrdImg8: String,
val spPrdImg9: String,
val spPrdImg10: String
)



































