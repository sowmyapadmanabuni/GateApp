package com.oyespace.guards.PojoClasses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName





class DashboardPojo {

    @SerializedName("data")
    @Expose
    private var data: VisitorsData? = null
    @SerializedName("apiVersion")
    @Expose
    private var apiVersion: String? = null
    @SerializedName("success")
    @Expose
    private var success: Boolean? = null

    fun getData(): VisitorsData? {
        return data
    }

    fun setData(data: VisitorsData) {
        this.data = data
    }

    fun getApiVersion(): String? {
        return apiVersion
    }

    fun setApiVersion(apiVersion: String) {
        this.apiVersion = apiVersion
    }

    fun getSuccess(): Boolean? {
        return success
    }

    fun setSuccess(success: Boolean?) {
        this.success = success
    }


    inner class VisitorsData {

        @SerializedName("visitorlogbydate")
        @Expose
        var visitorlogbydate: ArrayList<Visitorlogbydate>?= null;

//        fun getVisitorlogbydate(): ArrayList<Visitorlogbydate> {
//            return visitorlogbydate
//        }
//
//        fun setVisitorlogbydate(visitorlogbydate: ArrayList<Visitorlogbydate>) {
//            this.visitorlogbydate = visitorlogbydate
//        }


        inner class Visitorlogbydate {

            @SerializedName("vlVisLgID")
            @Expose
            var vlVisLgID: Int? = null
            @SerializedName("vlfName")
            @Expose
            var vlfName: String? = null
            @SerializedName("vllName")
            @Expose
            var vllName: String? = null
            @SerializedName("vlMobile")
            @Expose
            var vlMobile: String? = null
            @SerializedName("vlVisType")
            @Expose
            var vlVisType: String? = null
            @SerializedName("vlComName")
            @Expose
            var vlComName: String? = null
            @SerializedName("vlpOfVis")
            @Expose
            var vlpOfVis: String? = null
            @SerializedName("vlVisCnt")
            @Expose
            var vlVisCnt: Int? = null
            @SerializedName("vlVehNum")
            @Expose
            var vlVehNum: String? = null
            @SerializedName("vlVehType")
            @Expose
            var vlVehType: String? = null
            @SerializedName("vlItmCnt")
            @Expose
            var vlItmCnt: Int? = null
            @SerializedName("unUniName")
            @Expose
            var unUniName: String? = null
            @SerializedName("vlEntyWID")
            @Expose
            var vlEntyWID: Int? = null
            @SerializedName("vlExitWID")
            @Expose
            var vlExitWID: Int? = null
            @SerializedName("vlEntryT")
            @Expose
            var vlEntryT: String? = null
            @SerializedName("vlExitT")
            @Expose
            var vlExitT: String? = null
            @SerializedName("reRgVisID")
            @Expose
            var reRgVisID: Int? = null
            @SerializedName("meMemID")
            @Expose
            var meMemID: Int? = null
            @SerializedName("vlCmnts")
            @Expose
            var vlCmnts: String? = null
            @SerializedName("vlCmntImg")
            @Expose
            var vlCmntImg: String? = null
            @SerializedName("vlVerStat")
            @Expose
            var vlVerStat: String? = null
            @SerializedName("vlGtName")
            @Expose
            var vlGtName: String? = null
            @SerializedName("unUnitID")
            @Expose
            var unUnitID: Int? = null
            @SerializedName("vlPrmStat")
            @Expose
            var vlPrmStat: String? = null
            @SerializedName("vlPrmBy")
            @Expose
            var vlPrmBy: String? = null
            @SerializedName("fmid")
            @Expose
            var fmid: Int? = null
            @SerializedName("vlVisImgN")
            @Expose
            var vlVisImgN: String? = null
            @SerializedName("asAssnID")
            @Expose
            var asAssnID: Int? = null
            @SerializedName("vldCreated")
            @Expose
            var vldCreated: String? = null
            @SerializedName("vldUpdated")
            @Expose
            var vldUpdated: String? = null
            @SerializedName("vlIsActive")
            @Expose
            var vlIsActive: Boolean? = null

        }
    }

}