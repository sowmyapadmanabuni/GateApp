package com.oyespace.guards.models

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import java.io.StringReader

@RealmClass
open class FingerPrint:RealmObject(){

    @PrimaryKey
    var FPID: Int=0
    var FMID: Int=0
    var userName: String=""
    var ASAssnID:Int = 0
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


/**
 * Capture Fingerprint Response Handler
 */

data class CaptureFPResponse<T>(
    val apiVersion: String,
    val `data`: FPLabel,
    val success: Boolean
)

data class FPLabel(
    val fingerPrint: CapturedFPObject
)

data class CapturedFPObject(
   val fpid:Int = 0,
   val fpFngName: String,
   val fmid: Int,
   val fpImg1: String,
   val fpImg2: String,
   val fpImg3: String,
   val fpMemType: String = "",
   val asAssnID: Int,
   val fpdCreated: String,
   val fpdUpdated: String,
   val fpIsActive:Boolean
)