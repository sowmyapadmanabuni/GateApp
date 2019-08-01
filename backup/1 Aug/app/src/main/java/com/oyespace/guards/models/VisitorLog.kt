package com.oyespace.guards.models

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmClass
open class VisitorLog:RealmObject(){

    /**
     * VisitorLogEntryResp(apiVersion=1.0, data=VisitorLogData(visitorLog=[
     * VisitorEntryLog(asAssnID=4217,
     * endDate=0001-01-01T00:00:00, fmid=0,
     * meMemID=64, reRgVisID=0, spPrdImg1=, s
     * pPrdImg10=, spPrdImg2=, spPrdImg3=,
     * spPrdImg4=, spPrdImg5=, spPrdImg6=, spPrdImg7=, spPrdImg8=, spPrdImg9=,
     * startDate=0001-01-01T00:00:00,
     * unUniName=Ransingh, b101, b103, b105, unUnitID=4730,
     * vlCmntImg=, vlCmnts=, vlComName=Zomato,
     * vlEntryT=1900-01-01T16:18:00, vlEntyWID=20,
     * vlExitT=0001-01-01T00:00:00, vlExitWID=0, vlGtName=, vlIsActive=true,
     * vlItmCnt=0, vlMobile=+919447679600, vlPrmBy=, vlPrmStat=, vlVehNum=,
     * vlVehType=, vlVerStat=, vlVisCnt=1, vlVisImgN=null, vlVisLgID=12269,
     * vlVisType=Delivery,
     * vldCreated=2019-07-29T04:00:18,
     * vldUpdated=2019-07-29T04:00:18, vlfName=Anooj Krishnan, vlgpsPnt=null, vllName=, vlpOfVis=1)]), success=true)
     */

    @PrimaryKey
    var vlVisLgID:Int = 0
    var reRgVisID:Int = 0;
    var mEMemID:Int = 0;
    var vlfName:String=""
    var vllName:String="";
    var vlMobile:String = "";
    var vlVisType:String = "";
    var vlComName:String="";
    var vLPOfVis:String = "";
    var vlVisCnt:Int = 0;
    var vLVehNum:String = "";
    var vLVehType:String = "";
    var vLItmCnt:Int = 0;
    var unUniName:String = "";
    var vLVerStat:String = "";
    var vLGtName:String = "";
    var uNUnitID:Int = 0;
    var asAssnID:Int = 0;
    var vlEntryT:String = "";
    var vlExitT:String = "";
    var vldCreated:String = ""
    var vldUpdated:String = ""
    var vlEntryImg:String = ""


}
data class GetVisitorsResponse<T>(
    val apiVersion: String,
    val `data`: VisitorsList,
    val success: Boolean
)
data class VisitorsList(
    val visitorLog: RealmList<VisitorLog>
)