package com.oyespace.guards.models

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
open class Worker:RealmObject(){
    @PrimaryKey
    var wkWorkID:Int = 0
    var wkfName:String = ""
    var wklName:String = ""
    var wkMobile:String = ""
    var wkEntryImg:String = ""
    var wkWrkType:String = ""
    var wkDesgn:String = ""
    var wkdob:String = ""
    var wkidCrdNo:String =""
    var vnVendorID:Long = 0
    var blBlockID: String = ""
    var unUnitID:String = ""
    var asAssnID:Long = 0
    var wkIsActive:Boolean = false
    var unUniName:String = ""

}
data class GetWorkersResponse<T>(
    val apiVersion: String,
    val `data`: WorkersList,
    val success: Boolean
)
data class WorkersList(
    val worker: RealmList<Worker>
)

data class GetGuardsListResponse<T>(
    val apiVersion: String,
    val `data`: GuardsList,
    val success: Boolean
)

data class GuardsList(
    val workers: RealmList<Worker>
)