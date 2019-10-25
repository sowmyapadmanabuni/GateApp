package com.oyespace.guards.models

class NotificationSyncModel(
    val visitorlogId: Int,
    val buttonColor: String,
    val opened: Boolean = false
) {
    constructor() : this(0, "")
}

