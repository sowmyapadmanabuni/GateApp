package com.oyespace.guards.responce;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubscriptionResponse {

    @SerializedName("data")
    @Expose
    public SubscriptionData data;
    @SerializedName("apiVersion")
    @Expose
    public String apiVersion;
    @SerializedName("success")
    @Expose
    public Boolean success;

    public SubscriptionData getData() {
        return data;
    }

    public void setData(SubscriptionData data) {
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

    public class SubscriptionData {

        @SerializedName("subscription")
        @Expose
        public Subscription subscription;

        public Subscription getSubscription() {
            return subscription;
        }

        public void setSubscription(Subscription subscription) {
            this.subscription = subscription;
        }

    }

    public class Subscription {

        @SerializedName("suid")
        @Expose
        public Integer suid;
        @SerializedName("susDate")
        @Expose
        public String susDate;
        @SerializedName("sueDate")
        @Expose
        public String sueDate;
        @SerializedName("sulPymtD")
        @Expose
        public String sulPymtD;
        @SerializedName("sulPymtBy")
        @Expose
        public Integer sulPymtBy;
        @SerializedName("suNoofUnit")
        @Expose
        public Integer suNoofUnit;
        @SerializedName("suTotVal")
        @Expose
        public Double suTotVal;
        @SerializedName("prid")
        @Expose
        public Integer prid;
        @SerializedName("pyid")
        @Expose
        public Integer pyid;
        @SerializedName("asAssnID")
        @Expose
        public Integer asAssnID;
        @SerializedName("sudCreated")
        @Expose
        public String sudCreated;
        @SerializedName("sudUpdated")
        @Expose
        public String sudUpdated;
        @SerializedName("suIsActive")
        @Expose
        public Boolean suIsActive;

        public Integer getSuid() {
            return suid;
        }

        public void setSuid(Integer suid) {
            this.suid = suid;
        }

        public String getSusDate() {
            return susDate;
        }

        public void setSusDate(String susDate) {
            this.susDate = susDate;
        }

        public String getSueDate() {
            return sueDate;
        }

        public void setSueDate(String sueDate) {
            this.sueDate = sueDate;
        }

        public String getSulPymtD() {
            return sulPymtD;
        }

        public void setSulPymtD(String sulPymtD) {
            this.sulPymtD = sulPymtD;
        }

        public Integer getSulPymtBy() {
            return sulPymtBy;
        }

        public void setSulPymtBy(Integer sulPymtBy) {
            this.sulPymtBy = sulPymtBy;
        }

        public Integer getSuNoofUnit() {
            return suNoofUnit;
        }

        public void setSuNoofUnit(Integer suNoofUnit) {
            this.suNoofUnit = suNoofUnit;
        }

        public Double getSuTotVal() {
            return suTotVal;
        }

        public void setSuTotVal(Double suTotVal) {
            this.suTotVal = suTotVal;
        }

        public Integer getPrid() {
            return prid;
        }

        public void setPrid(Integer prid) {
            this.prid = prid;
        }

        public Integer getPyid() {
            return pyid;
        }

        public void setPyid(Integer pyid) {
            this.pyid = pyid;
        }

        public Integer getAsAssnID() {
            return asAssnID;
        }

        public void setAsAssnID(Integer asAssnID) {
            this.asAssnID = asAssnID;
        }

        public String getSudCreated() {
            return sudCreated;
        }

        public void setSudCreated(String sudCreated) {
            this.sudCreated = sudCreated;
        }

        public String getSudUpdated() {
            return sudUpdated;
        }

        public void setSudUpdated(String sudUpdated) {
            this.sudUpdated = sudUpdated;
        }

        public Boolean getSuIsActive() {
            return suIsActive;
        }

        public void setSuIsActive(Boolean suIsActive) {
            this.suIsActive = suIsActive;
        }

    }
}
