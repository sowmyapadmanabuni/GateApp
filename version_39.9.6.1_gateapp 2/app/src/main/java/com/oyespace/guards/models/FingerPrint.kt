package com.oyespace.guards.models

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
open class FingerPrint:RealmObject(){

    //@PrimaryKey
    val ASAssnID: Int=0
    val FMID: Int=0
    val FPFngName: String=""
    val FPImg1: ByteArray = ByteArray(8192)
    val FPImg2: ByteArray = ByteArray(8192)
    val FPImg3: ByteArray = ByteArray(8192)
    val FPMemType: String = ""


}
data class GetFingersResponse<T>(
    val apiVersion: String,
    val `data`: FingersList,
    val success: Boolean
)
data class FingersList(
    val visitorLog: RealmList<FingerPrint>
)