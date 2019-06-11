package com.oyespace.guards.responce;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TicketListingTesponse {

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

        @SerializedName("ticketing")
        @Expose
        private Ticketing ticketing;

        public Ticketing getTicketing() {
            return ticketing;
        }

        public void setTicketing(Ticketing ticketing) {
            this.ticketing = ticketing;
        }


    public class Ticketing {

        @SerializedName("tkTktID")
        @Expose
        private Integer tkTktID;
        @SerializedName("tkType")
        @Expose
        private String tkType;
        @SerializedName("tkTkIdent")
        @Expose
        private String tkTkIdent;
        @SerializedName("tkRaiseDT")
        @Expose
        private String tkRaiseDT;
        @SerializedName("tkgpsPnt")
        @Expose
        private String tkgpsPnt;
        @SerializedName("tkStat")
        @Expose
        private String tkStat;
        @SerializedName("tkRaisdBy")
        @Expose
        private String tkRaisdBy;
        @SerializedName("tkrbCmnts")
        @Expose
        private String tkrbCmnts;
        @SerializedName("tkrbEvid")
        @Expose
        private String tkrbEvid;
        @SerializedName("tkApprBy")
        @Expose
        private String tkApprBy;
        @SerializedName("tkabCmnts")
        @Expose
        private String tkabCmnts;
        @SerializedName("tkIsRjctd")
        @Expose
        private String tkIsRjctd;
        @SerializedName("tkRjctBy")
        @Expose
        private String tkRjctBy;
        @SerializedName("tkrCmnts")
        @Expose
        private String tkrCmnts;
        @SerializedName("tkAsgnTo")
        @Expose
        private String tkAsgnTo;
        @SerializedName("tkAsgnBy")
        @Expose
        private String tkAsgnBy;
        @SerializedName("tkAsgnOn")
        @Expose
        private String tkAsgnOn;
        @SerializedName("tkRsldBy")
        @Expose
        private String tkRsldBy;
        @SerializedName("tkRsCmnts")
        @Expose
        private String tkRsCmnts;
        @SerializedName("tkRsImage")
        @Expose
        private String tkRsImage;
        @SerializedName("tketa")
        @Expose
        private String tketa;
        @SerializedName("tkRsldOn")
        @Expose
        private String tkRsldOn;
        @SerializedName("ttTktTyID")
        @Expose
        private Integer ttTktTyID;
        @SerializedName("meMemID")
        @Expose
        private Integer meMemID;
        @SerializedName("tkEmail")
        @Expose
        private String tkEmail;
        @SerializedName("tkMobile")
        @Expose
        private String tkMobile;
        @SerializedName("unUnitID")
        @Expose
        private Integer unUnitID;
        @SerializedName("asAssnID")
        @Expose
        private Integer asAssnID;
        @SerializedName("tkdCreated")
        @Expose
        private String tkdCreated;
        @SerializedName("tkdUpdated")
        @Expose
        private String tkdUpdated;
        @SerializedName("tkIsActive")
        @Expose
        private Boolean tkIsActive;

        public Integer getTkTktID() {
            return tkTktID;
        }

        public void setTkTktID(Integer tkTktID) {
            this.tkTktID = tkTktID;
        }

        public String getTkType() {
            return tkType;
        }

        public void setTkType(String tkType) {
            this.tkType = tkType;
        }

        public String getTkTkIdent() {
            return tkTkIdent;
        }

        public void setTkTkIdent(String tkTkIdent) {
            this.tkTkIdent = tkTkIdent;
        }

        public String getTkRaiseDT() {
            return tkRaiseDT;
        }

        public void setTkRaiseDT(String tkRaiseDT) {
            this.tkRaiseDT = tkRaiseDT;
        }

        public String getTkgpsPnt() {
            return tkgpsPnt;
        }

        public void setTkgpsPnt(String tkgpsPnt) {
            this.tkgpsPnt = tkgpsPnt;
        }

        public String getTkStat() {
            return tkStat;
        }

        public void setTkStat(String tkStat) {
            this.tkStat = tkStat;
        }

        public String getTkRaisdBy() {
            return tkRaisdBy;
        }

        public void setTkRaisdBy(String tkRaisdBy) {
            this.tkRaisdBy = tkRaisdBy;
        }

        public String getTkrbCmnts() {
            return tkrbCmnts;
        }

        public void setTkrbCmnts(String tkrbCmnts) {
            this.tkrbCmnts = tkrbCmnts;
        }

        public String getTkrbEvid() {
            return tkrbEvid;
        }

        public void setTkrbEvid(String tkrbEvid) {
            this.tkrbEvid = tkrbEvid;
        }

        public String getTkApprBy() {
            return tkApprBy;
        }

        public void setTkApprBy(String tkApprBy) {
            this.tkApprBy = tkApprBy;
        }

        public String getTkabCmnts() {
            return tkabCmnts;
        }

        public void setTkabCmnts(String tkabCmnts) {
            this.tkabCmnts = tkabCmnts;
        }

        public String getTkIsRjctd() {
            return tkIsRjctd;
        }

        public void setTkIsRjctd(String tkIsRjctd) {
            this.tkIsRjctd = tkIsRjctd;
        }

        public String getTkRjctBy() {
            return tkRjctBy;
        }

        public void setTkRjctBy(String tkRjctBy) {
            this.tkRjctBy = tkRjctBy;
        }

        public String getTkrCmnts() {
            return tkrCmnts;
        }

        public void setTkrCmnts(String tkrCmnts) {
            this.tkrCmnts = tkrCmnts;
        }

        public String getTkAsgnTo() {
            return tkAsgnTo;
        }

        public void setTkAsgnTo(String tkAsgnTo) {
            this.tkAsgnTo = tkAsgnTo;
        }

        public String getTkAsgnBy() {
            return tkAsgnBy;
        }

        public void setTkAsgnBy(String tkAsgnBy) {
            this.tkAsgnBy = tkAsgnBy;
        }

        public String getTkAsgnOn() {
            return tkAsgnOn;
        }

        public void setTkAsgnOn(String tkAsgnOn) {
            this.tkAsgnOn = tkAsgnOn;
        }

        public String getTkRsldBy() {
            return tkRsldBy;
        }

        public void setTkRsldBy(String tkRsldBy) {
            this.tkRsldBy = tkRsldBy;
        }

        public String getTkRsCmnts() {
            return tkRsCmnts;
        }

        public void setTkRsCmnts(String tkRsCmnts) {
            this.tkRsCmnts = tkRsCmnts;
        }

        public String getTkRsImage() {
            return tkRsImage;
        }

        public void setTkRsImage(String tkRsImage) {
            this.tkRsImage = tkRsImage;
        }

        public String getTketa() {
            return tketa;
        }

        public void setTketa(String tketa) {
            this.tketa = tketa;
        }

        public String getTkRsldOn() {
            return tkRsldOn;
        }

        public void setTkRsldOn(String tkRsldOn) {
            this.tkRsldOn = tkRsldOn;
        }

        public Integer getTtTktTyID() {
            return ttTktTyID;
        }

        public void setTtTktTyID(Integer ttTktTyID) {
            this.ttTktTyID = ttTktTyID;
        }

        public Integer getMeMemID() {
            return meMemID;
        }

        public void setMeMemID(Integer meMemID) {
            this.meMemID = meMemID;
        }

        public String getTkEmail() {
            return tkEmail;
        }

        public void setTkEmail(String tkEmail) {
            this.tkEmail = tkEmail;
        }

        public String getTkMobile() {
            return tkMobile;
        }

        public void setTkMobile(String tkMobile) {
            this.tkMobile = tkMobile;
        }

        public Integer getUnUnitID() {
            return unUnitID;
        }

        public void setUnUnitID(Integer unUnitID) {
            this.unUnitID = unUnitID;
        }

        public Integer getAsAssnID() {
            return asAssnID;
        }

        public void setAsAssnID(Integer asAssnID) {
            this.asAssnID = asAssnID;
        }

        public String getTkdCreated() {
            return tkdCreated;
        }

        public void setTkdCreated(String tkdCreated) {
            this.tkdCreated = tkdCreated;
        }

        public String getTkdUpdated() {
            return tkdUpdated;
        }

        public void setTkdUpdated(String tkdUpdated) {
            this.tkdUpdated = tkdUpdated;
        }

        public Boolean getTkIsActive() {
            return tkIsActive;
        }

        public void setTkIsActive(Boolean tkIsActive) {
            this.tkIsActive = tkIsActive;
        }
    }
    }
}
