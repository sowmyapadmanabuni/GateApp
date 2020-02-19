package com.oyespace.guards.models

import com.oyespace.guards.constants.PrefKeys
import com.oyespace.guards.utils.DateTimeUtils.getCurrentTimeLocal
import com.oyespace.guards.utils.Prefs

class NotificationSyncModel(
    val visitorlogId: Int,
    val buttonColor: String,
    val status:String,
    val unit_id:String,
    val residentAccountId:String,
    val visitorJSON:String,
    val isKidExit:Boolean,
    val opened: Boolean = false,
    val updatedTime: String = getCurrentTimeLocal(),
    val newAttachment: Boolean = false,
    val attempt_counter:Int = 0,
    val created_time:String  = getCurrentTimeLocal(),
    val updated_time:String = getCurrentTimeLocal(),
    val gate_mobile:String = Prefs.getString(PrefKeys.MOBILE_NUMBER, null)


) {
    constructor() : this(0, "","","","","",false)
}

