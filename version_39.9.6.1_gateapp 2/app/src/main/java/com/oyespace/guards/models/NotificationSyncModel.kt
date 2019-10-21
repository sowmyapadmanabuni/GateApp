package com.oyespace.guards.models

class NotificationSyncModel(
    val visitorlogId: String,
    val buttonColor: String,
    val opened: Boolean = false
) {
    constructor() : this("", "")
}

