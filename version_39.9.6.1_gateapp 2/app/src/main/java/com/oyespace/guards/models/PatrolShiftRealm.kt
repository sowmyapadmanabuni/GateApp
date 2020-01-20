package com.oyespace.guards.models

import com.oyespace.guards.pojo.CheckPointScanRequest
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
//

open class PatrolShiftRealm:RealmObject() {
    @PrimaryKey
    var psPtrlSID: Int = 0
    var psSnooze: Boolean = false
    var pssTime: String = ""
    var pseTime: String = ""
    var psRepDays: String=""
    var deName: String=""
    var psSltName: String=""
    var asAssnID: Int=0
    var psdCreated: String=""
    var psdUpdated: String=""
    var psIsActive: Boolean=false
    //var point: RealmList<ScheduleCheckPointsDataRealm> = RealmList()
}


data class CheckPointMultipleScanRequest(
    val ASAssnID: Int,
    val WKWorkID: String,
    val wkfName: String,
    val PSPtrlSID : Int,
    val TRImage:String,
    val deId:Int,
    val tracking:List<CheckPointScanRequest>
)

open class CheckPointScanRealm:RealmObject() {
    @PrimaryKey
    var id:Int=0
    var ASAssnID: Int=0
    var WKWorkID: String=""
    var wkfName: String=""
    var PSPtrlSID: Int=0
    var TRTDateT: String=""
    var TRGPSPnt: String=""
    var CPCkPName: String=""
    var TRImage: String=""
    var deId: Int=0
    var CPCPntAt: String=""
}

open class CheckPointsOfSchedule:RealmObject(){
    @PrimaryKey
    var id:Int=0
    var psChkPID:Int=0
    var asAssnID:Int=0
    var pcIsActive:Boolean=false
    var cpgpsPnts:String=""
    var cpCkPName:String=""
    //val checks:ArrayList<CheckpointDetailRealm>
    var pcid:Int = 0
    var psPtrlSID: Int = 0
    var cpcPntAt:String = ""
    var cpOrder:String = ""

}

data class ShiftsListResponseRealm<T>(
    val apiVersion: String,
    val data: ShiftsDataRealm,
    val success: Boolean
)

data class ShiftsDataRealm(
    val patrollingShifts: RealmList<PatrolShiftRealm> = RealmList()
)


data class CheckPointsOfSheduleListResponseRealm<T>(
    val apiVersion: String,
    val data: CheckPointsScheduleDataRealm,
    val success: Boolean
)

data class CheckPointsScheduleDataRealm(
    val checkPointsBySchedule: ArrayList<PatrolShiftRealm>
)

data class ScheduleCheckPointsDataRealm(
    val psChkPID:Int,
    val asAssnID:Int,
    val pcIsActive:Boolean,
    val cpgpsPnts:String,
    val cpCkPName:String,
    val checks:ArrayList<CheckpointDetailRealm>,
    val pcid:Int
)

data class CheckpointDetailRealm(
    val cpCkPName:String
)
