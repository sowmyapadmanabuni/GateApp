package com.oyespace.guards.models

//import io.realm.RealmList
//import io.realm.RealmObject
//import io.realm.annotations.PrimaryKey
//

//open class PatrolShiftRealm:RealmObject() {
//    @PrimaryKey
//    var psPtrlSID: Int = 0
//    var psSnooze: Boolean = false
//    var pssTime: String = ""
//    var pseTime: String = ""
//    var psRepDays: String=""
//    var deName: String=""
//    var psSltName: String=""
//    var asAssnID: Int=0
//    var psdCreated: String=""
//    var psdUpdated: String=""
//    var psIsActive: Boolean=false
//    var point: RealmList<ScheduleCheckPointsDataRealm> = RealmList()
//}
//
//data class ShiftsListResponseRealm<T>(
//    val apiVersion: String,
//    val data: ShiftsDataRealm,
//    val success: Boolean
//)
//
//open class ShiftsDataRealm() {
//    val patrollingShifts: RealmList<PatrolShiftRealm> = RealmList()
//}
//
//
//data class CheckPointsOfSheduleListResponseRealm<T>(
//    val apiVersion: String,
//    val data: CheckPointsScheduleDataRealm,
//    val success: Boolean
//)
//
//data class CheckPointsScheduleDataRealm(
//    val checkPointsBySchedule: ArrayList<PatrolShiftRealm>
//)
//
//data class ScheduleCheckPointsDataRealm(
//    val psChkPID:Int,
//    val asAssnID:Int,
//    val pcIsActive:Boolean,
//    val cpgpsPnts:String,
//    val cpCkPName:String,
//    val checks:ArrayList<CheckpointDetailRealm>,
//    val pcid:Int
//)
//
//data class CheckpointDetailRealm(
//    val cpCkPName:String
//)
