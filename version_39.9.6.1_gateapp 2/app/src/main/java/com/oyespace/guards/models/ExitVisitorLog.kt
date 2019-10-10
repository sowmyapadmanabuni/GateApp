package com.oyespace.guards.models

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
open class ExitVisitorLog : RealmObject() {

    @PrimaryKey
    var vlVisLgID: Int = 0
    var reRgVisID: Int = 0
    var mEMemID: Int = 0
    var vlfName: String = ""
    var vllName: String = ""
    var vlMobile: String = ""
    var vlVisType: String = ""
    var vlComName: String = ""
    var vLPOfVis: String = ""
    var vlVisCnt: Int = 0
    var vLVehNum: String = ""
    var vLVehType: String = ""
    var vLItmCnt: Int = 0
    var unUniName: String = ""
    var vLVerStat: String = ""
    var vLGtName: String = ""
    var uNUnitID: Int = 0
    var asAssnID: Int = 0
    var vlEntryT: String = ""
    var vlExitT: String = ""
    var vldCreated: String = ""
    var vldUpdated: String = ""
    var vlEntryImg: String = ""
    var vlVenName: String = ""
    var vlVenImg: String = ""
    var vlVoiceNote: String = ""


}

data class GetExitVisitorsResponse<T>(
    val apiVersion: String,
    val `data`: ExitVisitorsList,
    val success: Boolean
)

data class ExitVisitorsList(
    val visitorLog: RealmList<ExitVisitorLog>
)


