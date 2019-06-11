package com.oyespace.guards.responce;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ResponseVisitorLog {

    @SerializedName("data")
    @Expose
    private Data data;
    @SerializedName("apiVersion")
    @Expose
    private String apiVersion;
    @SerializedName("success")
    @Expose
    private Boolean success;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
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

    public class Data {

        @SerializedName("visitorlogbydate")
        @Expose
        private ArrayList<Visitorlogbydate> visitorlogbydate = null;

        public ArrayList<Visitorlogbydate> getVisitorlogbydate() {
            return visitorlogbydate;
        }

        public void setVisitorlogbydate(ArrayList<Visitorlogbydate> visitorlogbydate) {
            this.visitorlogbydate = visitorlogbydate;
        }


        public class Visitorlogbydate {

            @SerializedName("vlVisLgID")
            @Expose
            private Integer vlVisLgID;
            @SerializedName("vlfName")
            @Expose
            private String vlfName;
            @SerializedName("vllName")
            @Expose
            private String vllName;
            @SerializedName("vlMobile")
            @Expose
            private String vlMobile;
            @SerializedName("vlVisType")
            @Expose
            private String vlVisType;
            @SerializedName("vlComName")
            @Expose
            private String vlComName;
            @SerializedName("vlpOfVis")
            @Expose
            private String vlpOfVis;
            @SerializedName("vlVisCnt")
            @Expose
            private Integer vlVisCnt;
            @SerializedName("vlVehNum")
            @Expose
            private String vlVehNum;
            @SerializedName("vlVehType")
            @Expose
            private String vlVehType;
            @SerializedName("vlItmCnt")
            @Expose
            private Integer vlItmCnt;
            @SerializedName("unUniName")
            @Expose
            private String unUniName;
            @SerializedName("vlEntyWID")
            @Expose
            private Integer vlEntyWID;
            @SerializedName("vlExitWID")
            @Expose
            private Integer vlExitWID;
            @SerializedName("vlEntryT")
            @Expose
            private String vlEntryT;
            @SerializedName("vlExitT")
            @Expose
            private String vlExitT;
            @SerializedName("reRgVisID")
            @Expose
            private Integer reRgVisID;
            @SerializedName("meMemID")
            @Expose
            private Integer meMemID;
            @SerializedName("vlCmnts")
            @Expose
            private String vlCmnts;
            @SerializedName("vlCmntImg")
            @Expose
            private String vlCmntImg;
            @SerializedName("vlVerStat")
            @Expose
            private String vlVerStat;
            @SerializedName("vlGtName")
            @Expose
            private String vlGtName;
            @SerializedName("unUnitID")
            @Expose
            private Integer unUnitID;
            @SerializedName("vlPrmStat")
            @Expose
            private String vlPrmStat;
            @SerializedName("vlPrmBy")
            @Expose
            private String vlPrmBy;
            @SerializedName("fmid")
            @Expose
            private Integer fmid;
            @SerializedName("vlVisImgN")
            @Expose
            private String vlVisImgN;
            @SerializedName("asAssnID")
            @Expose
            private Integer asAssnID;
            @SerializedName("vldCreated")
            @Expose
            private String vldCreated;
            @SerializedName("vldUpdated")
            @Expose
            private String vldUpdated;
            @SerializedName("vlIsActive")
            @Expose
            private Boolean vlIsActive;
            @SerializedName("vlgpsPnt")
            @Expose
            private String vlgpsPnt;
            @SerializedName("startDate")
            @Expose
            private String startDate;
            @SerializedName("endDate")
            @Expose
            private String endDate;

            public Integer getVlVisLgID() {
                return vlVisLgID;
            }

            public void setVlVisLgID(Integer vlVisLgID) {
                this.vlVisLgID = vlVisLgID;
            }

            public String getVlfName() {
                return vlfName;
            }

            public void setVlfName(String vlfName) {
                this.vlfName = vlfName;
            }

            public String getVllName() {
                return vllName;
            }

            public void setVllName(String vllName) {
                this.vllName = vllName;
            }

            public String getVlMobile() {
                return vlMobile;
            }

            public void setVlMobile(String vlMobile) {
                this.vlMobile = vlMobile;
            }

            public String getVlVisType() {
                return vlVisType;
            }

            public void setVlVisType(String vlVisType) {
                this.vlVisType = vlVisType;
            }

            public String getVlComName() {
                return vlComName;
            }

            public void setVlComName(String vlComName) {
                this.vlComName = vlComName;
            }

            public String getVlpOfVis() {
                return vlpOfVis;
            }

            public void setVlpOfVis(String vlpOfVis) {
                this.vlpOfVis = vlpOfVis;
            }

            public Integer getVlVisCnt() {
                return vlVisCnt;
            }

            public void setVlVisCnt(Integer vlVisCnt) {
                this.vlVisCnt = vlVisCnt;
            }

            public String getVlVehNum() {
                return vlVehNum;
            }

            public void setVlVehNum(String vlVehNum) {
                this.vlVehNum = vlVehNum;
            }

            public String getVlVehType() {
                return vlVehType;
            }

            public void setVlVehType(String vlVehType) {
                this.vlVehType = vlVehType;
            }

            public Integer getVlItmCnt() {
                return vlItmCnt;
            }

            public void setVlItmCnt(Integer vlItmCnt) {
                this.vlItmCnt = vlItmCnt;
            }

            public String getUnUniName() {
                return unUniName;
            }

            public void setUnUniName(String unUniName) {
                this.unUniName = unUniName;
            }

            public Integer getVlEntyWID() {
                return vlEntyWID;
            }

            public void setVlEntyWID(Integer vlEntyWID) {
                this.vlEntyWID = vlEntyWID;
            }

            public Integer getVlExitWID() {
                return vlExitWID;
            }

            public void setVlExitWID(Integer vlExitWID) {
                this.vlExitWID = vlExitWID;
            }

            public String getVlEntryT() {
                return vlEntryT;
            }

            public void setVlEntryT(String vlEntryT) {
                this.vlEntryT = vlEntryT;
            }

            public String getVlExitT() {
                return vlExitT;
            }

            public void setVlExitT(String vlExitT) {
                this.vlExitT = vlExitT;
            }

            public Integer getReRgVisID() {
                return reRgVisID;
            }

            public void setReRgVisID(Integer reRgVisID) {
                this.reRgVisID = reRgVisID;
            }

            public Integer getMeMemID() {
                return meMemID;
            }

            public void setMeMemID(Integer meMemID) {
                this.meMemID = meMemID;
            }

            public String getVlCmnts() {
                return vlCmnts;
            }

            public void setVlCmnts(String vlCmnts) {
                this.vlCmnts = vlCmnts;
            }

            public String getVlCmntImg() {
                return vlCmntImg;
            }

            public void setVlCmntImg(String vlCmntImg) {
                this.vlCmntImg = vlCmntImg;
            }

            public String getVlVerStat() {
                return vlVerStat;
            }

            public void setVlVerStat(String vlVerStat) {
                this.vlVerStat = vlVerStat;
            }

            public String getVlGtName() {
                return vlGtName;
            }

            public void setVlGtName(String vlGtName) {
                this.vlGtName = vlGtName;
            }

            public Integer getUnUnitID() {
                return unUnitID;
            }

            public void setUnUnitID(Integer unUnitID) {
                this.unUnitID = unUnitID;
            }

            public String getVlPrmStat() {
                return vlPrmStat;
            }

            public void setVlPrmStat(String vlPrmStat) {
                this.vlPrmStat = vlPrmStat;
            }

            public String getVlPrmBy() {
                return vlPrmBy;
            }

            public void setVlPrmBy(String vlPrmBy) {
                this.vlPrmBy = vlPrmBy;
            }

            public Integer getFmid() {
                return fmid;
            }

            public void setFmid(Integer fmid) {
                this.fmid = fmid;
            }

            public String getVlVisImgN() {
                return vlVisImgN;
            }

            public void setVlVisImgN(String vlVisImgN) {
                this.vlVisImgN = vlVisImgN;
            }

            public Integer getAsAssnID() {
                return asAssnID;
            }

            public void setAsAssnID(Integer asAssnID) {
                this.asAssnID = asAssnID;
            }

            public String getVldCreated() {
                return vldCreated;
            }

            public void setVldCreated(String vldCreated) {
                this.vldCreated = vldCreated;
            }

            public String getVldUpdated() {
                return vldUpdated;
            }

            public void setVldUpdated(String vldUpdated) {
                this.vldUpdated = vldUpdated;
            }

            public Boolean getVlIsActive() {
                return vlIsActive;
            }

            public void setVlIsActive(Boolean vlIsActive) {
                this.vlIsActive = vlIsActive;
            }

            public String getVlgpsPnt() {
                return vlgpsPnt;
            }

            public void setVlgpsPnt(String vlgpsPnt) {
                this.vlgpsPnt = vlgpsPnt;
            }

            public String getStartDate() {
                return startDate;
            }

            public void setStartDate(String startDate) {
                this.startDate = startDate;
            }

            public String getEndDate() {
                return endDate;
            }

            public void setEndDate(String endDate) {
                this.endDate = endDate;
            }

        }
    }
}