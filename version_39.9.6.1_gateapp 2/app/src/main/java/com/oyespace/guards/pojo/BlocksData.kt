package com.oyespace.guards.com.oyespace.guards.pojo

data class BlocksData(
    var isSelected: Boolean = false,
    var asiCrFreq: String,
    var blBlockID: Int,
    var blBlkName: String,
    var blBlkType: String,
    var acAccntID: Int,
    var blNofUnit: Int,
    var asAssnID: Int,
    var bldCreated: String,
    var bldUpdated: String,
    var blIsActive: Boolean,
    var blMgrName: String,
    var blMgrMobile: String,
    var blMgrEmail: String,
    var asMtType: String,
    var asMtDimBs: Float,
    var asMtFRate: Float,
    var asUniMsmt: String,
    var asbGnDate: String,
    var aslpcType: String,
    var aslpChrg: Int
)

data class BlocksList<T>(
    val apiVersion: String,
    val data: BlockByAssociation,
    val success: Boolean
)

data class BlockByAssociation(
    val blocksByAssoc: ArrayList<BlocksData>
)
