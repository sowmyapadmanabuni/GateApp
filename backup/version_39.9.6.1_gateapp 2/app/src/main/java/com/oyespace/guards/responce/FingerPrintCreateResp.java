package com.oyespace.guards.responce;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FingerPrintCreateResp {
    public String apiVersion;

   // public FingerPrintCreateRespData data;

    public String success;

    public class FingerPrintCreateRespData
    {
        public FingerPrint fingerPrint;


    }
    public class FingerPrint
    {
        public String fpIsActive;

        public String fpFngName;

        public String fpImg3;

        public String fmid;

        public String fpImg2;

        public String fpImg1;

        public String fpid;

        public String fpdUpdated;

        public String fpMemType;

        public String asAssnID;

        public String fpdCreated;


    }

  /*  @SerializedName("data")
    @Expose
    private FPCRespData data;
    @SerializedName("apiVersion")
    @Expose
    private String apiVersion;
    @SerializedName("success")
    @Expose
    private Boolean success;

    public FPCRespData getData() {
        return data;
    }

    public void setData(FPCRespData data) {
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

    public class FPCRespData {

        @SerializedName("fingerPrint")
        @Expose
        private FingerPrint fingerPrint;

        public FingerPrint getFingerPrint() {
            return fingerPrint;
        }

        public void setFingerPrint(FingerPrint fingerPrint) {
            this.fingerPrint = fingerPrint;
        }

    }

    public class FingerPrint {

        @SerializedName("fpid")
        @Expose
        private Integer fpid;
        @SerializedName("fpFngName")
        @Expose
        private Object fpFngName;
        @SerializedName("fmid")
        @Expose
        private Integer fmid;
        @SerializedName("fpImg1")
        @Expose
        private Object fpImg1;
        @SerializedName("fpImg2")
        @Expose
        private Object fpImg2;
        @SerializedName("fpImg3")
        @Expose
        private Object fpImg3;
        @SerializedName("fpMemType")
        @Expose
        private Object fpMemType;
        @SerializedName("asAssnID")
        @Expose
        private Integer asAssnID;
        @SerializedName("fpdCreated")
        @Expose
        private String fpdCreated;
        @SerializedName("fpdUpdated")
        @Expose
        private String fpdUpdated;
        @SerializedName("fpIsActive")
        @Expose
        private Boolean fpIsActive;

        public Integer getFpid() {
            return fpid;
        }

        public void setFpid(Integer fpid) {
            this.fpid = fpid;
        }

        public Object getFpFngName() {
            return fpFngName;
        }

        public void setFpFngName(Object fpFngName) {
            this.fpFngName = fpFngName;
        }

        public Integer getFmid() {
            return fmid;
        }

        public void setFmid(Integer fmid) {
            this.fmid = fmid;
        }

        public Object getFpImg1() {
            return fpImg1;
        }

        public void setFpImg1(Object fpImg1) {
            this.fpImg1 = fpImg1;
        }

        public Object getFpImg2() {
            return fpImg2;
        }

        public void setFpImg2(Object fpImg2) {
            this.fpImg2 = fpImg2;
        }

        public Object getFpImg3() {
            return fpImg3;
        }

        public void setFpImg3(Object fpImg3) {
            this.fpImg3 = fpImg3;
        }

        public Object getFpMemType() {
            return fpMemType;
        }

        public void setFpMemType(Object fpMemType) {
            this.fpMemType = fpMemType;
        }

        public Integer getAsAssnID() {
            return asAssnID;
        }

        public void setAsAssnID(Integer asAssnID) {
            this.asAssnID = asAssnID;
        }

        public String getFpdCreated() {
            return fpdCreated;
        }

        public void setFpdCreated(String fpdCreated) {
            this.fpdCreated = fpdCreated;
        }

        public String getFpdUpdated() {
            return fpdUpdated;
        }

        public void setFpdUpdated(String fpdUpdated) {
            this.fpdUpdated = fpdUpdated;
        }

        public Boolean getFpIsActive() {
            return fpIsActive;
        }

        public void setFpIsActive(Boolean fpIsActive) {
            this.fpIsActive = fpIsActive;
        }

    }*/
}
