package com.oyespace.guards.models


data class PatrolShift(

    var psPtrlSID: Int,
    var psSnooze: Boolean,
    var pssTime: String,
    var pseTime: String,
    var psRepDays: String,
    var deName: String,
    var psSltName: String,
    var asAssnID: Int,
    var psdCreated: String,
    var psdUpdated: String,
    var psIsActive: Boolean
)

data class ShiftsListResponse<T>(
    val apiVersion: String,
    val data: ShiftsData,
    val success: Boolean
)

data class ShiftsData(
    val patrollingShifts: ArrayList<PatrolShift>
)


data class CheckPointsOfSheduleListResponse<T>(
    val apiVersion: String,
    val data: CheckPointsScheduleData,
    val success: Boolean
)

data class CheckPointsScheduleData(
    val checkPointsBySchedule: ArrayList<PatrolShift>
)
