package com.oyespace.guards.responce;

public class StaffImageRes {


    private AttadancData data;

    public AttadancData getData() {
        return data;
    }

    public void setData(AttadancData data) {
        this.data = data;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    private String apiVersion;
    private Boolean success;

}
