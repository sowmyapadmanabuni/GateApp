package com.oyespace.guards.responce;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InvitationRequestResponse {

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

        @SerializedName("invitation")
        @Expose
        private Invitation invitation;

        public Invitation getInvitation() {
            return invitation;
        }

        public void setInvitation(Invitation invitation) {
            this.invitation = invitation;
        }

        public class Invitation {

            @SerializedName("inInvtID")
            @Expose
            private Integer inInvtID;
            @SerializedName("meMemID")
            @Expose
            private Integer meMemID;
            @SerializedName("unUnitID")
            @Expose
            private Integer unUnitID;
            @SerializedName("infName")
            @Expose
            private String infName;
            @SerializedName("inlName")
            @Expose
            private String inlName;
            @SerializedName("inMobile")
            @Expose
            private String inMobile;
            @SerializedName("inEmail")
            @Expose
            private String inEmail;
            @SerializedName("inVchlNo")
            @Expose
            private String inVchlNo;
            @SerializedName("inVisCnt")
            @Expose
            private Integer inVisCnt;
            @SerializedName("inPhoto")
            @Expose
            private String inPhoto;
            @SerializedName("insDate")
            @Expose
            private String insDate;
            @SerializedName("ineDate")
            @Expose
            private String ineDate;
            @SerializedName("inpOfInv")
            @Expose
            private String inpOfInv;
            @SerializedName("inIsUsed")
            @Expose
            private Boolean inIsUsed;
            @SerializedName("inqrCode")
            @Expose
            private Boolean inqrCode;
            @SerializedName("inMultiEy")
            @Expose
            private Boolean inMultiEy;
            @SerializedName("asAssnID")
            @Expose
            private Integer asAssnID;
            @SerializedName("indCreated")
            @Expose
            private String indCreated;
            @SerializedName("indUpdated")
            @Expose
            private String indUpdated;
            @SerializedName("inIsActive")
            @Expose
            private Boolean inIsActive;

            public Integer getInInvtID() {
                return inInvtID;
            }

            public void setInInvtID(Integer inInvtID) {
                this.inInvtID = inInvtID;
            }

            public Integer getMeMemID() {
                return meMemID;
            }

            public void setMeMemID(Integer meMemID) {
                this.meMemID = meMemID;
            }

            public Integer getUnUnitID() {
                return unUnitID;
            }

            public void setUnUnitID(Integer unUnitID) {
                this.unUnitID = unUnitID;
            }

            public String getInfName() {
                return infName;
            }

            public void setInfName(String infName) {
                this.infName = infName;
            }

            public String getInlName() {
                return inlName;
            }

            public void setInlName(String inlName) {
                this.inlName = inlName;
            }

            public String getInMobile() {
                return inMobile;
            }

            public void setInMobile(String inMobile) {
                this.inMobile = inMobile;
            }

            public String getInEmail() {
                return inEmail;
            }

            public void setInEmail(String inEmail) {
                this.inEmail = inEmail;
            }

            public String getInVchlNo() {
                return inVchlNo;
            }

            public void setInVchlNo(String inVchlNo) {
                this.inVchlNo = inVchlNo;
            }

            public Integer getInVisCnt() {
                return inVisCnt;
            }

            public void setInVisCnt(Integer inVisCnt) {
                this.inVisCnt = inVisCnt;
            }

            public String getInPhoto() {
                return inPhoto;
            }

            public void setInPhoto(String inPhoto) {
                this.inPhoto = inPhoto;
            }

            public String getInsDate() {
                return insDate;
            }

            public void setInsDate(String insDate) {
                this.insDate = insDate;
            }

            public String getIneDate() {
                return ineDate;
            }

            public void setIneDate(String ineDate) {
                this.ineDate = ineDate;
            }

            public String getInpOfInv() {
                return inpOfInv;
            }

            public void setInpOfInv(String inpOfInv) {
                this.inpOfInv = inpOfInv;
            }

            public Boolean getInIsUsed() {
                return inIsUsed;
            }

            public void setInIsUsed(Boolean inIsUsed) {
                this.inIsUsed = inIsUsed;
            }

            public Boolean getInqrCode() {
                return inqrCode;
            }

            public void setInqrCode(Boolean inqrCode) {
                this.inqrCode = inqrCode;
            }

            public Boolean getInMultiEy() {
                return inMultiEy;
            }

            public void setInMultiEy(Boolean inMultiEy) {
                this.inMultiEy = inMultiEy;
            }

            public Integer getAsAssnID() {
                return asAssnID;
            }

            public void setAsAssnID(Integer asAssnID) {
                this.asAssnID = asAssnID;
            }

            public String getIndCreated() {
                return indCreated;
            }

            public void setIndCreated(String indCreated) {
                this.indCreated = indCreated;
            }

            public String getIndUpdated() {
                return indUpdated;
            }

            public void setIndUpdated(String indUpdated) {
                this.indUpdated = indUpdated;
            }

            public Boolean getInIsActive() {
                return inIsActive;
            }

            public void setInIsActive(Boolean inIsActive) {
                this.inIsActive = inIsActive;
            }

        }
    }

}
