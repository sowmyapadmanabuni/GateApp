package com.oyespace.guards

import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.LocalBroadcastManager
import android.util.Base64
import android.util.Log
import android.widget.Toast
import com.oyespace.guards.cloudfunctios.CloudFunctionRetrofitClinet
import com.oyespace.guards.fcm.FCMRetrofitClinet
import com.oyespace.guards.network.CommonDisposable
import com.oyespace.guards.network.ImageApiClient
import com.oyespace.guards.network.ImageApiInterface
import com.oyespace.guards.network.RetrofitClinet
import com.oyespace.guards.pojo.*
import com.oyespace.guards.responce.VisitorLogExitResp
import com.oyespace.guards.utils.AppUtils.Companion.intToString
import com.oyespace.guards.utils.ConstantUtils
import com.oyespace.guards.utils.ConstantUtils.*
import com.oyespace.guards.utils.DateTimeUtils
import com.oyespace.guards.utils.LocalDb
import com.oyespace.guards.utils.Prefs
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.io.*
import java.util.*

class

BackgroundSyncReceiver : BroadcastReceiver() {
    var unAccountID:String?=null
    lateinit var mcontext: Context
    override fun onReceive(context: Context, intent: Intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        val action2 = intent.getStringExtra(BSR_Action)
        mcontext=context
        if(intent.getStringExtra(BSR_Action).equals(VisitorEntryFCM)){

            if(intent.getStringExtra("unitname").contains(",")){

                var unitname_dataList: Array<String>
                var unitid_dataList: Array<String>
                var unitAccountId_dataList: Array<String>
                unitname_dataList = intent.getStringExtra("unitname").split(",".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                unitid_dataList=intent.getStringExtra(UNITID).split(",".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
               // unitAccountId_dataList=intent.getStringExtra(UNIT_ACCOUNT_ID).split(",".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                if(unitid_dataList.size>0) {
                    for (i in 0 until unitid_dataList.size) {

try {

    getUnitLog(
        unitid_dataList.get(i).replace(" ", "").toInt(),
        intent.getStringExtra("name"),
        "",
        intent.getStringExtra(VISITOR_TYPE),
        "Staff",
        0,
        unitname_dataList.get(i).replace(" ", ""),
        intent.getIntExtra("VLVisLgID", 0),
        intent.getStringExtra("msg"),
        intent.getStringExtra("nr_id")
    )
}catch (e:Exception){

}
//                        sendFCM(intent.getStringExtra("msg"), intent.getStringExtra("mobNum"),
//                            intent.getStringExtra("name"), intent.getStringExtra("nr_id"),
//                            unitname_dataList.get(i).replace(" ",""), intent.getStringExtra("memType"));
//
//                        getNotificationCreate(unitAccountId_dataList.get(i).replace(" ",""),Prefs.getInt(ASSOCIATION_ID,0).toString(),"gate_app",intent.getStringExtra("msg"),unitid_dataList.get(i).replace(" ",""),intent.getIntExtra("VLVisLgID",0).toString(),unitid_dataList.get(i).replace(" ","")+"admin","gate_app",LocalDb.getAssociation()!!.asAsnName,"gate_app",
//                            DateTimeUtils.getCurrentTimeLocal(),
//                            DateTimeUtils.getCurrentTimeLocal(),
//                            intent.getIntExtra("VLVisLgID",0).toString()
//                        )
//
//                        sendCloudFunctionNotification(Prefs.getInt(ASSOCIATION_ID,0),LocalDb.getAssociation()!!.asAsnName,intent.getStringExtra("msg"),intent.getStringExtra(COMPANY_NAME),"gate_app",
//                            unitid_dataList.get(i).replace(" ","")+"admin",Prefs.getInt(DEVICE_ID,0),unitid_dataList.get(i).replace(" ",""))
                    }
                }
            }else{

                try{
                getUnitLog(intent.getStringExtra(UNITID).toInt(),intent.getStringExtra("name"),"",intent.getStringExtra(VISITOR_TYPE),"Staff",0, intent.getStringExtra("name"),intent.getIntExtra("VLVisLgID",0),intent.getStringExtra("msg"),intent.getStringExtra("nr_id"))

            }catch (e:Exception){

            }
//                sendFCM(intent.getStringExtra("msg"),intent.getStringExtra("mobNum"),
//                    intent.getStringExtra("name"),intent.getStringExtra("nr_id"),
//                    intent.getStringExtra("unitname").replace(" ",""),intent.getStringExtra("memType"));
//
//
//                getNotificationCreate(intent.getStringExtra(UNIT_ACCOUNT_ID),Prefs.getInt(ASSOCIATION_ID,0).toString(),"gate_app",intent.getStringExtra("msg"),intent.getStringExtra(UNITID),intent.getIntExtra("VLVisLgID",0).toString(),intent.getStringExtra(UNITID)+"admin","gate_app",LocalDb.getAssociation()!!.asAsnName,"gate_app",
//                    DateTimeUtils.getCurrentTimeLocal(),
//                    DateTimeUtils.getCurrentTimeLocal(),
//                    intent.getIntExtra("VLVisLgID",0).toString()
//                )
//
//                sendCloudFunctionNotification(Prefs.getInt(ASSOCIATION_ID,0),LocalDb.getAssociation()!!.asAsnName,intent.getStringExtra("msg"),intent.getStringExtra(COMPANY_NAME),"gate_app",
//                    intent.getStringExtra(UNITID)+"admin",Prefs.getInt(DEVICE_ID,0),intent.getStringExtra(UNITID))
            }
            sendFCM_toSyncNonreg()
            Log.d("SYCNCHECK","in 65")

        }else  if(intent.getStringExtra(BSR_Action).equals(SENDFCM_toSYNC_VISITORENTRY)){
            sendFCM_toSyncNonreg();
        }else  if(intent.getStringExtra(BSR_Action).equals("sendFCM_toStopEmergencyAlert")){
            sendFCM_toStopEmergencyAlert();
        }else  if(intent.getStringExtra(BSR_Action).equals(SYNC_STAFF_BIOMETRIC)){
            downloadFingerPrint_newFunction(intent.getIntExtra("ID",0));

        }else  if(intent.getStringExtra(BSR_Action).equals(UPLOAD_STAFF_PHOTO)){
            Log.d("uploadImage","in "+intent.getStringExtra("imgName"))
            val wrrw = intent.getByteArrayExtra(PERSON_PHOTO)
            if(wrrw!=null) {
                var mBitmap: Bitmap;
                mBitmap = BitmapFactory.decodeByteArray(wrrw, 0, wrrw.size)
                uploadImage(intent.getStringExtra("imgName"),mBitmap);

            }else{
                Log.d("uploadImage","else "+intent.getStringExtra("imgName"))
            }
        }else  if(intent.getStringExtra(BSR_Action).equals(SYNC_STAFF_LIST)){
            getStaffList()
            getCheckPointList()
        }else  if(intent.getStringExtra(BSR_Action).equals(SYNC_UNIT_LIST)){
            getUnitList()
            getCheckPointList()
        }
        else if(intent.getStringExtra(BSR_Action).equals(VISITOR_ENTRY_SYNC)){
            Log.d("SYCNCHECK","in 86")
            getVisitorLogEntryList()
        }


        else  if(intent.getStringExtra(BSR_Action).equals(UPLOAD_GUARD_PHOTO)) {
            Log.d("uploadImage", "in " + intent.getStringExtra("imgName"))
            val wrrw = intent.getByteArrayExtra("GUARD_PHOTO")
            if (wrrw != null) {
                var mBitmap: Bitmap;
                mBitmap = BitmapFactory.decodeByteArray(wrrw, 0, wrrw.size)
                uploadImage(intent.getStringExtra("imgName"), mBitmap);

            } else {
                Log.d("uploadImage22", "else " + intent.getStringExtra("imgName"))
            }
        }
        else if(intent.getStringExtra(BSR_Action).equals(SENDAUDIO)){
            //Toast.makeText(context,"coming",Toast.LENGTH_LONG).show()
            sendFCM_forAudioMessage(intent.getStringExtra("FILENAME"))
        }

    }

    private  fun downloadFingerPrint_newFunction(  workerID:Int){
        var ba_fp1: ByteArray
        var ba_fp2: ByteArray
        var ba_fp3: ByteArray

        RetrofitClinet.instance
            .getStaffBiometric(OYE247TOKEN, workerID,"Regular")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CommonDisposable<StaffBiometricResp<StaffBiometricData>>() {

                override fun onSuccessResponse(staffBiometricResp: StaffBiometricResp<StaffBiometricData>) {

                    if (staffBiometricResp.success == true) {
                        Log.d("getStaffBiometric",staffBiometricResp.data.toString())
                        try {
                            var dbh: DataBaseHelper= DataBaseHelper(mcontext);

                            for (i in 0 until staffBiometricResp.data.fingerPrint.size) {

                                Log.d("getStaffBiometric",
                                    "finger " + " " +intToString(staffBiometricResp.data.fingerPrint.get(i).fmid)+ " " +
                                            staffBiometricResp.data.fingerPrint.get(i).fpFngName+ " " )
                                val fp1 = staffBiometricResp.data.fingerPrint.get(i).fpImg1
                                val fp2 = staffBiometricResp.data.fingerPrint.get(i).fpImg2
                                val fp3 = staffBiometricResp.data.fingerPrint.get(i).fpImg3

                                try {

                                    ba_fp1 = Base64.decode(fp1, Base64.DEFAULT)
                                    ba_fp2 = Base64.decode(fp2, Base64.DEFAULT)
                                    ba_fp3 = Base64.decode(fp3, Base64.DEFAULT)

                                    dbh.insertUserDetails(intToString(staffBiometricResp.data.fingerPrint.get(i).fmid),
                                        staffBiometricResp.data.fingerPrint.get(i).fpFngName,
                                        ba_fp1,
                                        ba_fp2,
                                        ba_fp3,
                                        staffBiometricResp.data.fingerPrint.get(i).fpMemType,
                                        staffBiometricResp.data.fingerPrint.get(i).asAssnID
                                    )
                                } catch (e: Exception) {
                                    Log.d("getStaffBiometric", "Exception$e")
                                }

                            }

                            Log.d("getStaffBiometric", "after finger " + staffBiometricResp.data.fingerPrint.size)

                        } catch (ex: Exception) {
                            Log.d("getStaffBiometric", "$ex ")
                        }

                    } else {
                        //rv_staff.setEmptyAdapter("No items to show!", false, 0)
                    }
                }

                override fun onErrorResponse(e: Throwable) {

                    //rv_staff.setEmptyAdapter(getString(R.string.some_wrng), false, 0)
                    Log.d("Error WorkerList",e.toString())

                }

                override fun noNetowork() {

                }
            })

    }

    private fun sendFCM(msg :String,  mobNum : String,name :String,nr_id :String,unitname:String,memType:String) {

        val dataReq = VisitorEntryFCMData("visitorEntryApproval", Prefs.getInt(ASSOCIATION_ID,0), msg, mobNum, name, nr_id )
        Log.d("sendFCM","dataReq "+dataReq.toString())
        var req =VisitorEntryFCMReq(dataReq, "/topics/UnitOwner" + unitname.trim()+"Assn"+Prefs.getInt(ASSOCIATION_ID,0) )
        Log.d("sendFCM","req "+req.toString())

        if(memType.equals("Tenant")) {
            req = VisitorEntryFCMReq(dataReq, "/topics/UnitTenant" + unitname + "Assn" + Prefs.getInt(ASSOCIATION_ID,0))
        }

        val compositeDisposable = CompositeDisposable()

        compositeDisposable.add(
            FCMRetrofitClinet.instance.sendFCM_VisitorEntry(ConstantUtils.FCMToken,req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : CommonDisposable<VisitorEntryFCMResp>() {
                    override fun onSuccessResponse(globalApiObject: VisitorEntryFCMResp) {
                        Log.d("sendFCM","StaffEntry "+globalApiObject.message_id+" "+globalApiObject.toString())
                    }

                    override fun onErrorResponse(e: Throwable) {
//                    Utils.showToast(applicationContext, getString(R.string.some_wrng))
                        Log.d("sendFCM","onErrorResponse  "+e.toString())
                    }

                    override fun noNetowork() {
//                    Utils.showToast(applicationContext, getString(R.string.no_internet))
                    }

                    override fun onShowProgress() {
                    }

                    override fun onDismissProgress() {
                    }
                }))


    }

    fun uploadImage(localImgName: String, incidentPhoto: Bitmap?) {
        Log.d("uploadImage",localImgName)
        var byteArrayProfile: ByteArray?
        val mPath = Environment.getExternalStorageDirectory().toString() + "/" + localImgName + ".jpg"
        val imageFile = File(mPath)

        try {
            val outputStream = FileOutputStream(imageFile)
            val quality = 50
            if (incidentPhoto != null) {
                incidentPhoto.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            }
            outputStream.flush()
            outputStream.close()

            val bosProfile = ByteArrayOutputStream()
            if (incidentPhoto != null) {
                incidentPhoto.compress(Bitmap.CompressFormat.JPEG, 50, bosProfile)
            }
            // bmp1.compress(Bitmap.CompressFormat.JPEG, 50, bos);
            //InputStream in = new ByteArrayInputStream(bos.toByteArray());
            byteArrayProfile = bosProfile.toByteArray()
            val len = bosProfile.toByteArray().size
            println("AFTER COMPRESSION-===>$len")
            bosProfile.flush()
            bosProfile.close()
            if (incidentPhoto != null) {
                //    incidentPhoto.recycle()
            }
            Timber.e("uploadImage  bf", "sfas")
        } catch (ex: Exception) {
            byteArrayProfile = null
            Log.d("uploadImage ererer bf", ex.toString())
        }




        val file = File(imageFile.toString())
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val body = MultipartBody.Part.createFormData("Test", localImgName, requestFile)
        val apiService = ImageApiClient.getImageClient().create(ImageApiInterface::class.java)
        val call = apiService.updateImageProfile(body)

        call.enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: retrofit2.Response<Any>) {
                try {
                    Log.d("uploadImage", "response:" + response.body()!!)
                    file.delete()

                } catch (ex: Exception) {
                    Log.d("uploadImage", "errr:" + ex.toString())
                }

            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                Log.d("uploadImage", t.toString())
               // Toast.makeText(mcontext, "Not Uploaded", Toast.LENGTH_SHORT).show()

            }
        })

    }

    private fun getStaffList() {

        RetrofitClinet.instance
            .workerList(OYE247TOKEN, intToString( LocalDb.getAssociation().asAssnID))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CommonDisposable<GetWorkerListbyAssnIDResp<WorkerListbyAssnIDData>>() {

                override fun onSuccessResponse(workerListResponse: GetWorkerListbyAssnIDResp<WorkerListbyAssnIDData>) {

                    if (workerListResponse.data.worker !=null) {
                        Log.d("WorkerList success",workerListResponse.data.toString())
                        var arrayList: ArrayList<WorkerDetails>? = null
                        arrayList=ArrayList()
                        arrayList = workerListResponse.data.worker

                        Collections.sort(arrayList, object : Comparator<WorkerDetails>{
                            override  fun compare(lhs: WorkerDetails, rhs: WorkerDetails): Int {
                                return lhs.wkfName.compareTo(rhs.wkfName)
                            }
                        })

                        LocalDb.saveStaffList(arrayList);

                    } else {

                    }
                }

                override fun onErrorResponse(e: Throwable) {
                    Log.d("Error WorkerList",e.toString())
                }

                override fun noNetowork() {

                }
            })
    }

    private fun getUnitList() {

        RetrofitClinet.instance
            .unitList(CHAMPTOKEN, intToString( Prefs.getInt(ASSOCIATION_ID,0)))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CommonDisposable<UnitList<ArrayList<UnitPojo>>>() {

                override fun onSuccessResponse(UnitList: UnitList<ArrayList<UnitPojo>>) {

                    if (UnitList.success == true) {
                        //  Log.d("cdvd",UnitList.toString());
                        var arrayListUnits = ArrayList<UnitPojo>()

                        arrayListUnits=ArrayList()
                        arrayListUnits = UnitList.data.unit

                        Collections.sort(arrayListUnits, object : Comparator<UnitPojo>{
                            override  fun compare(lhs: UnitPojo, rhs: UnitPojo): Int {
                                return lhs.unUniName.compareTo (rhs.unUniName,true)
                            }
                        })
                        //  LocalDb.saveUnitList(arrayListUnits);

                    } else {

                    }
                }

                override fun onErrorResponse(e: Throwable) {
                    Log.d("cdvd",e.message);

                }

                override fun noNetowork() {

                }
            })

    }

//    fun sendFCM_toSyncNonreg_byID(NoNregID: Int) {
//        val apiService = FCMApiClient.getClient().create(FCMApiInterface::class.java)
//
//        val payloadData = EntryPermissionPayload(
//            NONREGULAR_BYID,
//            NONREGULAR, NoNregID,
//            entry_type[3], GlobalVariables.getGlobal_mobilenumber(), prefManager.getAssociationId()
//        )
//        val sendOTPRequest =
//            SendEntryPermissionRequest(payloadData, "/topics/AllGuards" + prefManager.getAssociationId())
//        val call = apiService.sendEntryPermission(sendOTPRequest)
//
//        call.enqueue(object : Callback<SendFCMResponse> {
//            override fun onResponse(call: Call<SendFCMResponse>, response: Response<SendFCMResponse>) {
//                Log.d("Dgddfdf", "fcm: " + response.body()!!.getMessage_id())
//                if (response.body()!!.getMessage_id() != null) {
//                    Toast.makeText(context1, Notified, Toast.LENGTH_SHORT).show()
//                } else {
//                    Toast.makeText(context1, Failed_to_Notify, Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onFailure(call: Call<SendFCMResponse>, t: Throwable) {
//                // Log error here since request failed
//                Log.d("TAG", t.toString())
//            }
//        })
//
//    }

    private fun getVisitorLogEntryList() {
        Log.d("SYCNCHECK","in 408")
        RetrofitClinet.instance
            .getVisitorLogEntryList(OYE247TOKEN,  Prefs.getInt(ASSOCIATION_ID,0))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CommonDisposable<VisitorLogEntryResp<ArrayList<VisitorEntryLog>>>() {

                override fun onSuccessResponse(visitorList: VisitorLogEntryResp<ArrayList<VisitorEntryLog>>) {
                    Log.d("SYCNCHECK","in 416")
                    Log.d("SYCNCHECK","in 417"+visitorList.toString())

                    if (visitorList.success == true) {
                        Log.d("SYCNCHECK", "in 421")
                        Log.d("cdvd", visitorList.toString());
                        var arrayListVisitors = ArrayList<VisitorEntryLog>()
                        arrayListVisitors = visitorList.data.visitorLog





                        if(visitorList.data.visitorLog!=null) {

                            Collections.sort(arrayListVisitors, object : Comparator<VisitorEntryLog> {
                                override fun compare(lhs:  VisitorEntryLog, rhs:VisitorEntryLog): Int {
                                    return (DateTimeUtils.formatDateDMY(rhs.vldCreated) +" "+(rhs.vlEntryT).replace("1900-01-01T","")).compareTo(
                                        DateTimeUtils.formatDateDMY(lhs.vldCreated) +" "+(lhs.vlEntryT).replace("1900-01-01T",""))

                                }
                            })
                        }
                        LocalDb.saveEnteredVisitorLog(arrayListVisitors);

                        val smsIntent = Intent(ConstantUtils.SYNC)
                        smsIntent.putExtra("message", VISITOR_ENTRY_SYNC)
                        LocalBroadcastManager.getInstance(mcontext).sendBroadcast(smsIntent)

                    } else {
                        Log.d("SYCNCHECK","in 437")
                    }
                }

                override fun onErrorResponse(e: Throwable) {
//                    Log.d("cdvd",e.message);
//                    Log.d("SYCNCHECK","in 441")

                }

                override fun noNetowork() {

                }
            })

    }

    fun sendFCM_toSyncNonreg() {

        Log.d("SYCNCHECK","in 452")
        val dataReq = VisitorEntryFCMData(BACKGROUND_SYNC, Prefs.getInt(ASSOCIATION_ID,0), "", "", NONREGULAR, "" )
        Log.d("sendFCM","dataReq "+dataReq.toString())
        var req =VisitorEntryFCMReq(dataReq, "/topics/AllGuards" +Prefs.getInt(ASSOCIATION_ID,0) )
        Log.d("sendFCM","req "+req.toString())

        val compositeDisposable = CompositeDisposable()

        compositeDisposable.add(
            FCMRetrofitClinet.instance.sendFCM_VisitorEntry(ConstantUtils.FCMToken,req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : CommonDisposable<VisitorEntryFCMResp>() {
                    override fun onSuccessResponse(globalApiObject: VisitorEntryFCMResp) {
                        Log.d("sendFCM","StaffEntry "+globalApiObject.message_id+" "+globalApiObject.toString())
                        Log.d("SYCNCHECK","in 468")
                    }

                    override fun onErrorResponse(e: Throwable) {
//                    Utils.showToast(applicationContext, getString(R.string.some_wrng))
                        Log.d("sendFCM","onErrorResponse  "+e.toString())
                        Log.d("SYCNCHECK","in 473")
                    }

                    override fun noNetowork() {
//                    Utils.showToast(applicationContext, getString(R.string.no_internet))
                    }

                    override fun onShowProgress() {
                    }

                    override fun onDismissProgress() {
                    }
                }))
    }

    fun sendFCM_toStopEmergencyAlert() {

        Log.d("toStopEmergencyAlert","in 452")
        val dataReq = VisitorEntryFCMData("emergencyAttend", Prefs.getInt(ASSOCIATION_ID,0), "", "", "", "" )
        Log.d("sendFCM","dataReq "+dataReq.toString())
        var req =VisitorEntryFCMReq(dataReq, "/topics/AllGuards" +Prefs.getInt(ASSOCIATION_ID,0) )
        Log.d("toStopEmergencyAlert","req "+req.toString())

        val compositeDisposable = CompositeDisposable()

        compositeDisposable.add(
            FCMRetrofitClinet.instance.sendFCM_VisitorEntry(ConstantUtils.FCMToken,req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : CommonDisposable<VisitorEntryFCMResp>() {
                    override fun onSuccessResponse(globalApiObject: VisitorEntryFCMResp) {
                        Log.d("sendFCM","StaffEntry "+globalApiObject.message_id+" "+globalApiObject.toString())
                        Log.d("toStopEmergencyAlert","in 468")
                    }

                    override fun onErrorResponse(e: Throwable) {
//                    Utils.showToast(applicationContext, getString(R.string.some_wrng))
                        Log.d("sendFCM","onErrorResponse  "+e.toString())
                        Log.d("toStopEmergencyAlert","in 473")
                    }

                    override fun noNetowork() {
//                    Utils.showToast(applicationContext, getString(R.string.no_internet))
                    }

                    override fun onShowProgress() {
                    }

                    override fun onDismissProgress() {
                    }
                }))
    }

    fun sendFCM_forAudioMessage(filename:String) {


        val dataReq = VisitorEntryFCMData("audiomessage", Prefs.getInt(ASSOCIATION_ID,0), filename, "", "", "" )
        Log.d("sendFCM","dataReq "+dataReq.toString())
        var req =VisitorEntryFCMReq(dataReq, "/topics/AllGuards" +Prefs.getInt(ASSOCIATION_ID,0) )
        Log.d("toStopEmergencyAlert","req "+req.toString())

        val compositeDisposable = CompositeDisposable()

        compositeDisposable.add(
            FCMRetrofitClinet.instance.sendFCM_VisitorEntry(ConstantUtils.FCMToken,req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : CommonDisposable<VisitorEntryFCMResp>() {
                    override fun onSuccessResponse(globalApiObject: VisitorEntryFCMResp) {
                        Log.d("SENDAUDIO","StaffEntry "+globalApiObject.message_id+" "+globalApiObject.toString())
                        Log.d("SENDAUDIO","in 549")
                    }

                    override fun onErrorResponse(e: Throwable) {
//                    Utils.showToast(applicationContext, getString(R.string.some_wrng))
                        Log.d("SENDAUDIO","onErrorResponse  "+e.toString())
                        Log.d("SENDAUDIO","in 555")
                    }

                    override fun noNetowork() {
//                    Utils.showToast(applicationContext, getString(R.string.no_internet))
                    }

                    override fun onShowProgress() {
                    }

                    override fun onDismissProgress() {
                    }
                }))
    }





    private fun getCheckPointList() {

        RetrofitClinet.instance
            .getCheckPointList(OYE247TOKEN, intToString( LocalDb.getAssociation().asAssnID))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CommonDisposable<CheckpointListResp<CheckPointByAssocID>>() {

                override fun onSuccessResponse(workerListResponse: CheckpointListResp<CheckPointByAssocID>) {

                    if (workerListResponse.data.checkPointListByAssocID !=null) {
                        Log.d("WorkerList success",workerListResponse.data.toString())
                        var arrayList: ArrayList<CheckPointByAssocID>? = null
                        arrayList=ArrayList()
                        arrayList = workerListResponse.data.checkPointListByAssocID

                        Collections.sort(arrayList, object : Comparator<CheckPointByAssocID>{
                            override  fun compare(lhs: CheckPointByAssocID, rhs: CheckPointByAssocID): Int {
                                return lhs.cpCkPName.compareTo(rhs.cpCkPName)
                            }
                        })

                        LocalDb.saveCheckPointList(arrayList);

                    } else {

                    }
                }

                override fun onErrorResponse(e: Throwable) {
                    Log.d("Error WorkerList",e.toString())
                }

                override fun noNetowork() {

                }
            })
    }


    private fun sendCloudFunctionNotification(associationID: Int, associationName: String, ntDesc: String, ntTitle: String, ntType: String, sbSubID: String, userID: Int,unitID:String) {

        val dataReq = CloudFunctionNotificationReq(associationID,associationName,ntDesc,ntTitle,ntType,sbSubID,userID,unitID )


        CloudFunctionRetrofitClinet.instance
            .sendCloud_VisitorEntry(dataReq)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CommonDisposable<Any>() {

                override fun onSuccessResponse(any: Any) {

//                    if (workerListResponse.data.checkPointListByAssocID !=null) {
//                        Log.d("WorkerList success",workerListResponse.data.toString())
//                        var arrayList: ArrayList<CheckPointByAssocID>? = null
//                        arrayList=ArrayList()
//                        arrayList = workerListResponse.data.checkPointListByAssocID
//
//                        Collections.sort(arrayList, object : Comparator<CheckPointByAssocID>{
//                            override  fun compare(lhs: CheckPointByAssocID, rhs: CheckPointByAssocID): Int {
//                                return lhs.cpCkPName.compareTo(rhs.cpCkPName)
//                            }
//                        })
//
//                        LocalDb.saveCheckPointList(arrayList);
//
//                    } else {
//
//                    }
                }

                override fun onErrorResponse(e: Throwable) {
                    Log.d("Error WorkerList",e.toString())
                }

                override fun noNetowork() {

                }
            })
    }




//    {
//        "ACAccntID"  : 1,
//        "ASAssnID"   : 2,
//        "NTType"     : "Join",
//        "NTDesc"     : "Joining as Owner",
//        "SBUnitID" : 23,
//        "SBMemID"  : 3,
//        "SBSubID"  : 2,
//        "SBRoleID" : 2,
//        "ASAsnName" : "AssociationName",
//        "MRRolName" : "Owner",
//        "NTDUpdated" : "2019-09-12 12:00:00",
//        "NTDCreated" : "2019-01-29 11:11:11"
//
//
//    }


    private fun getNotificationCreate(ACAccntID:String,ASAssnID:String,NTType:String,NTDesc:String,SBUnitID:String,SBMemID:String,SBSubID:String,SBRoleID:String,ASAsnName:String,MRRolName:String,NTDUpdated:String,NTDCreated:String,VLVisLgID:String) {


        val dataReq = NotificationCreateReq(ACAccntID,ASAssnID,NTType,NTDesc,SBUnitID,SBMemID,SBSubID,SBRoleID ,ASAsnName,MRRolName,NTDUpdated,NTDCreated,VLVisLgID,"","")


        RetrofitClinet.instance
            .getNotificationCreate(OYE247TOKEN, dataReq)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CommonDisposable<NotificationCreateResponse>() {

                override fun onSuccessResponse(notificationCreateResponse: NotificationCreateResponse) {

                }


                override fun onErrorResponse(e: Throwable) {
                    Log.d("Error WorkerList",e.toString())
                }

                override fun noNetowork() {

                }
            })
    }

    private fun getUnitLog(unitId:Int,personName:String,mobileNumb:String, desgn:String,
                           workerType:String,staffID:Int,unitName:String,vlVisLgID:Int,msg:String,nrId:String) {


        RetrofitClinet.instance
            .getUnitListbyUnitId("1FDF86AF-94D7-4EA9-8800-5FBCCFF8E5C1", unitId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CommonDisposable<UnitlistbyUnitID>() {

                override fun onSuccessResponse(UnitList: UnitlistbyUnitID) {
                    if (UnitList.success == true) {

                        if(UnitList.data.unit.unOcStat.contains("Sold Owner Occupied Unit")){

                            if(!UnitList.data.unit.owner.isEmpty()){
                                unAccountID= UnitList.data.unit.owner[0].acAccntID.toString()
                            }
                            else{
                                unAccountID="0"
                            }


                        }
                        else if(UnitList.data.unit.unOcStat.contains("Sold Tenant Occupied Unit")){
                            if(!UnitList.data.unit.tenant.isEmpty()) {
                                unAccountID = UnitList.data.unit.tenant[0].acAccntID.toString()
                            }
                                else{
                                    unAccountID="0"
                                }

                        }
                        else if(UnitList.data.unit.unOcStat.contains("UnSold Tenant Occupied Unit")){

                                if(!UnitList.data.unit.tenant.isEmpty()) {
                                    unAccountID = UnitList.data.unit.tenant[0].acAccntID.toString()
                                } else{
                                    unAccountID="0"
                                }

                        }else if(UnitList.data.unit.unOcStat.contains("UnSold Vacant Unit")){
//                                    if(!UnitList.data.unit.owner.isEmpty()) {
//                                        unAccountID = "0"
//                                    } else{
                                        unAccountID="0"
                                   // }

                        }else if(UnitList.data.unit.unOcStat.contains("Sold Vacant Unit")){
                                        if(!UnitList.data.unit.owner.isEmpty()) {
                                            unAccountID = UnitList.data.unit.owner[0].acAccntID.toString()
                                        } else{
                                            unAccountID="0"
                                        }
                        }else{
                            unAccountID="0"
                        }


                        try {      sendFCM(msg, mobileNumb,
                            personName, nrId,
                           unitName, "Owner");

                    }catch (e:KotlinNullPointerException){

                    }

                        try {     getNotificationCreate(unAccountID.toString(),Prefs.getInt(ASSOCIATION_ID,0).toString(),"gate_app",msg,unitId.toString(),vlVisLgID.toString(),unitId.toString()+"admin","gate_app",LocalDb.getAssociation()!!.asAsnName,"gate_app",
                            DateTimeUtils.getCurrentTimeLocal(),
                            DateTimeUtils.getCurrentTimeLocal(),
                            vlVisLgID.toString()
                        )
                        }catch (e:KotlinNullPointerException){

                        }
//                        sendCloudFunctionNotification(Prefs.getInt(ASSOCIATION_ID,0),LocalDb.getAssociation()!!.asAsnName,msg,desgn,"gate_app",
//                            unitId.toString()+"admin",Prefs.getInt(DEVICE_ID,0),unAccountID.toString())

                        try {
                            sendCloudFunctionNotification(
                                Prefs.getInt(ASSOCIATION_ID, 0),
                                LocalDb.getAssociation()!!.asAsnName,
                                msg,
                                desgn,
                                "gate_app",
                                unitId.toString() + "admin",
                                unAccountID!!.toInt(),
                                unAccountID.toString()
                            )
                        }catch (e:KotlinNullPointerException){

                        }

//                        val ddc  =  Intent(this, BackgroundSyncReceiver::class.java)
//                        ddc.putExtra(ConstantUtils.BSR_Action, ConstantUtils.VisitorEntryFCM)
//                        ddc.putExtra("msg", personName+" "+desgn +" is coming to your home")
//                        ddc.putExtra("mobNum", mobileNumb)
//                        ddc.putExtra("name", personName)
//                        ddc.putExtra("nr_id", vlVisLgID.toString())
//                        ddc.putExtra("unitname", unitName)
//                        ddc.putExtra("memType", "Owner")
//                        ddc.putExtra(UNITID,unitId.toString())
//                        ddc.putExtra(COMPANY_NAME,intent.getStringExtra(COMPANY_NAME))
//                        ddc.putExtra(UNIT_ACCOUNT_ID,UnitList.data.unit.acAccntID.toString())
//                        ddc.putExtra("VLVisLgID",vlVisLgID)
////                        intent.getStringExtra("msg"),intent.getStringExtra("mobNum"),
////                        intent.getStringExtra("name"),intent.getStringExtra("nr_id"),
////                        intent.getStringExtra("unitname"),intent.getStringExtra("memType")
//                        this@MobileNumberforEntryScreen.sendBroadcast(ddc);


                    } else {
                    }
                }

                override fun onErrorResponse(e: Throwable) {
                    Log.d("cdvd", e.message);


                }

                override fun noNetowork() {

                }
            })

    }

}