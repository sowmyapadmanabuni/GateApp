package com.oyespace.guards.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class VisitorEntryFirebaseObject : RealmObject() {

    @PrimaryKey
    var vlVisLgID: Int = 0
    var vlUpdatedTime: String = ""
    var vlStatus: String = "pending"

}