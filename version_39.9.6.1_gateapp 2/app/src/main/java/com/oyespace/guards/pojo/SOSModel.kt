package com.oyespace.guards.com.oyespace.guards.pojo

import com.google.firebase.database.PropertyName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class SOSModel: RealmObject() {
    @PrimaryKey
    var userId: Int = 0
    var isActive: Boolean = false
    var unitName: String = ""
    var unitId: Int = 0
    var userName:String = ""
    var userMobile:String = ""
    var sosImage:String = ""
    var latitude:String = ""
    var longitude:String = ""
    var id:Int = 0

}

data class PassesSOSGuards(
    val guardId:Int=0,
    val passedTime:String = ""
)