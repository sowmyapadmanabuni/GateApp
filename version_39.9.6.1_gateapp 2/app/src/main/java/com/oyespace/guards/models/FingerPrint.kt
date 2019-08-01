package com.oyespace.guards.models

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
open class FingerPrint:RealmObject(){

    @PrimaryKey
    var ASAssnID:Int = 0
    var FMID: Int=0
    var FPFngName: String=""
    var FPImg1: ByteArray = ByteArray(8192)
    var FPImg2: ByteArray = ByteArray(8192)
    var FPImg3: ByteArray = ByteArray(8192)
    var FPMemType: String = ""


}
data class GetFingersResponse<T>(
    val apiVersion: String,
    val `data`: FingersList,
    val success: Boolean
)
data class FingersList(
    val visitorLog: RealmList<FingerPrint>
)