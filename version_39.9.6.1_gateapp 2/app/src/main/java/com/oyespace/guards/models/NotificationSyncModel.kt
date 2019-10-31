package com.oyespace.guards.models

import com.oyespace.guards.utils.DateTimeUtils

class NotificationSyncModel(
    val visitorlogId: Int,
    val buttonColor: String,
    val opened: Boolean = false,
    val updatedTime: String = DateTimeUtils.getCurrentTimeLocal(),
    val newAttachment: Boolean = false
) {
    constructor() : this(0, "")
}

