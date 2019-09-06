package com.oyespace.guards.pojo

import com.oyespace.guards.pojo.UnitPojo


data class _UnitsData(
    var unUnitID: Int,
    var unUniName: String,
    var unUniIden: String,
    var unUniType: String,
    var unOpenBal: Int,
    var unCurrBal: Int,
    var unOcStat: String,
    var unOcSDate: String,
    var unOwnStat: String,
    var unSldDate: String,
    var unDimens: Int,
    var unCalType: String,
    var blBlockID: String,
    var unRate: Int,
    var asAssnID: Int,
    var acAccntID: Int,
    var undCreated: String,
    var undUpdated: String,
    var unIsActive: String,
    var isSelected:Boolean = false
)

data class UnitsList<T>(
    val apiVersion: String,
    val data: UnitsByBlock,
    val success: Boolean
)

data class UnitsByBlock(
    val unitsByBlockID: ArrayList<UnitPojo>
)

data class SearchUnitRequest(
    var ASAssnID:Int,
    var UNUniName:String
)