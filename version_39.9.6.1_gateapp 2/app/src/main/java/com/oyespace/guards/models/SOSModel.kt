package com.oyespace.guards.com.oyespace.guards.pojo

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class SOSModel : RealmObject() {
    @PrimaryKey
    var userId: Int = 0
    var isActive: Boolean = false
    var unitName: String = ""
    var unitId: Int = 0
    var userName: String = ""
    var userMobile: String = ""
    var sosImage: String = ""
    var latitude: String = ""
    var longitude: String = ""
    var attendedBy: String = ""
    var passedBY: RealmList<PassesSOSGuards> = RealmList()
    var id: Int = 0

}

open class PassesSOSGuards : RealmObject() {
    var gateName: String = ""
    var passedTime: String = ""
}