package com.oyespace.guards.responce;

import java.io.Serializable;

public class RequestDTO {
	public String vLFName;
	public String vLLName;
	public String vLMobile;
	public String vLVisType;
	public String vLComName;
	public String vLPOfVis;
	public int vLVisCnt;
	public String vLVehNum;
	public String vLVehType;
	public int vLItmCnt;
	public String uNUniName;
	public int rERgVisID;

	public String getVLEntryImg() {
		return VLEntryImg;
	}

	public void setVLEntryImg(String VLEntryImg) {
		this.VLEntryImg = VLEntryImg;
	}

	public String VLEntryImg;
	public int mEMemID;
	public String vLVerStat;
	public String vLGtName;
	public int uNUnitID;
	public int aSAssnID;
	public String VLENGName;

	public void setVLFName(String vLFName){
		this.vLFName = vLFName;
	}

	public String getVLFName(){
		return vLFName;
	}

	public void setVLLName(String vLLName){
		this.vLLName = vLLName;
	}

	public String getVLLName(){
		return vLLName;
	}

	public void setVLMobile(String vLMobile){
		this.vLMobile = vLMobile;
	}

	public String getVLMobile(){
		return vLMobile;
	}

	public void setVLVisType(String vLVisType){
		this.vLVisType = vLVisType;
	}

	public String getVLVisType(){
		return vLVisType;
	}

	public void setVLComName(String vLComName){
		this.vLComName = vLComName;
	}

	public String getVLComName(){
		return vLComName;
	}

	public void setVLPOfVis(String vLPOfVis){
		this.vLPOfVis = vLPOfVis;
	}

	public String getVLPOfVis(){
		return vLPOfVis;
	}

	public void setVLVisCnt(int vLVisCnt){
		this.vLVisCnt = vLVisCnt;
	}

	public int getVLVisCnt(){
		return vLVisCnt;
	}

	public void setVLVehNum(String vLVehNum){
		this.vLVehNum = vLVehNum;
	}

	public String getVLVehNum(){
		return vLVehNum;
	}

	public void setVLVehType(String vLVehType){
		this.vLVehType = vLVehType;
	}

	public String getVLVehType(){
		return vLVehType;
	}

	public void setVLItmCnt(int vLItmCnt){
		this.vLItmCnt = vLItmCnt;
	}

	public int getVLItmCnt(){
		return vLItmCnt;
	}

	public void setUNUniName(String uNUniName){
		this.uNUniName = uNUniName;
	}

	public String getUNUniName(){
		return uNUniName;
	}

	public void setRERgVisID(int rERgVisID){
		this.rERgVisID = rERgVisID;
	}

	public int getRERgVisID(){
		return rERgVisID;
	}

	public void setMEMemID(int mEMemID){
		this.mEMemID = mEMemID;
	}

	public int getMEMemID(){
		return mEMemID;
	}

	public void setVLVerStat(String vLVerStat){
		this.vLVerStat = vLVerStat;
	}

	public String getVLVerStat(){
		return vLVerStat;
	}

	public void setVLGtName(String vLGtName){
		this.vLGtName = vLGtName;
	}

	public String getVLGtName(){
		return vLGtName;
	}

	public void setUNUnitID(int uNUnitID){
		this.uNUnitID = uNUnitID;
	}

	public int getUNUnitID(){
		return uNUnitID;
	}

	public void setASAssnID(int aSAssnID){
		this.aSAssnID = aSAssnID;
	}

	public int getASAssnID(){
		return aSAssnID;
	}

	@Override
 	public String toString(){
		return 
			"RequestDTO{" + 
			"vLFName = '" + vLFName + '\'' + 
			",vLLName = '" + vLLName + '\'' + 
			",vLMobile = '" + vLMobile + '\'' + 
			",vLVisType = '" + vLVisType + '\'' + 
			",vLComName = '" + vLComName + '\'' + 
			",vLPOfVis = '" + vLPOfVis + '\'' + 
			",vLVisCnt = '" + vLVisCnt + '\'' + 
			",vLVehNum = '" + vLVehNum + '\'' + 
			",vLVehType = '" + vLVehType + '\'' + 
			",vLItmCnt = '" + vLItmCnt + '\'' + 
			",uNUniName = '" + uNUniName + '\'' + 
			",rERgVisID = '" + rERgVisID + '\'' + 
			",mEMemID = '" + mEMemID + '\'' + 
			",vLVerStat = '" + vLVerStat + '\'' + 
			",vLGtName = '" + vLGtName + '\'' + 
			",uNUnitID = '" + uNUnitID + '\'' + 
			",aSAssnID = '" + aSAssnID + '\'' +
					",VLEntryImg = '" + VLEntryImg + '\'' +
					"}";
		}
}