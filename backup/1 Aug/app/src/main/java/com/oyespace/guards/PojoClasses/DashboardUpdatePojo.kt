package com.oyespace.guards.PojoClasses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class DashboardUpdatePojo {

    @SerializedName("data")
    @Expose
    private var data: Any? = null
    @SerializedName("apiVersion")
    @Expose
    private var apiVersion: String? = null
    @SerializedName("success")
    @Expose
    private var success: Boolean? = null

    fun getData(): Any? {
        return data
    }

    fun setData(data: Any) {
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


}