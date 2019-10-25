package com.oyespace.guards.models

import com.oyespace.guards.utils.DateTimeUtils.getCurrentTimeLocal

class NotificationSyncModel(
    val visitorlogId: Int,
    val buttonColor: String,
    val opened: Boolean = false,
val time:String=getCurrentTimeLocal()
) {
    constructor() : this(0, "")
}

