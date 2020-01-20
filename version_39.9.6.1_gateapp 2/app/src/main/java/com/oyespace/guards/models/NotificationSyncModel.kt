package com.oyespace.guards.models

import com.oyespace.guards.utils.DateTimeUtils.getCurrentTimeLocal

class NotificationSyncModel(
    val visitorlogId: Int,
    val buttonColor: String,
    val status:String,
    val opened: Boolean = false,
    val updatedTime: String = getCurrentTimeLocal(),
    val newAttachment: Boolean = false

) {
    constructor() : this(0, "","")
}

