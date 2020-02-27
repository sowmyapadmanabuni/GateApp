
package com.oyespace.guards.PojoClasses;

import com.google.gson.annotations.SerializedName;


@SuppressWarnings("unused")
public class GetLatestRecord {
    @SerializedName("apiVersion")
    private String mApiVersion;
    @SerializedName("data")
    private Data mData;
    @SerializedName("success")
    private Boolean mSuccess;

    public String getApiVersion() {
        return mApiVersion;
    }

    public void setApiVersion(String apiVersion) {
        mApiVersion = apiVersion;
    }

    public Data getData() {
        return mData;
    }

    public void setData(Data data) {
        mData = data;
    }

    public Boolean getSuccess() {
        return mSuccess;
    }

    public void setSuccess(Boolean success) {
        mSuccess = success;
    }

}
