package com.oyespace.guards

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.gson.Gson
import com.oyespace.guards.activity.PatrollingAlert
import com.oyespace.guards.cloudfunctios.CloudFunctionRetrofitClinet
import com.oyespace.guards.fcm.FCMRetrofitClinet
import com.oyespace.guards.models.*
import com.oyespace.guards.network.CommonDisposable
import com.oyespace.guards.network.ImageApiClient
import com.oyespace.guards.network.ImageApiInterface
import com.oyespace.guards.network.RetrofitClinet
import com.oyespace.guards.pojo.*
import com.oyespace.guards.realm.RealmDB
import com.oyespace.guards.repo.StaffRepo
import com.oyespace.guards.repo.VisitorLogRepo
import com.oyespace.guards.utils.*
import com.oyespace.guards.utils.AppUtils.Companion.intToString
import com.oyespace.guards.utils.ConstantUtils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmResults
import io.realm.exceptions.RealmPrimaryKeyConstraintException
import io.realm.kotlin.createObject
import io.realm.kotlin.delete
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_final_registration.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class

BackgroundSyncReceiver : BroadcastReceiver() {
    var unAccountID: Int? = 0
    var unitid:Int?=0
    lateinit var mcontext: Context
    var time:String?=null

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        val action2 = intent.getStringExtra(BSR_Action)
        mcontext = context
        if (intent.getStringExtra(BSR_Action).equals(VisitorEntryFCM)) {

            val sendNotification = intent.getBooleanExtra(SEND_NOTIFICATION, true)

            if (intent.getStringExtra("unitname").contains(",")) {

                val unitname_dataList: Array<String>
                val unitid_dataList: Array<String>
                var unitAccountId_dataList: Array<String>
                var unitOccupancyStatues:Array<String>
                unitname_dataList = intent.getStringExtra("unitname").split(",".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                unitid_dataList = intent.getStringExtra(UNITID).split(",".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                 unitAccountId_dataList=intent.getStringExtra(UNIT_ACCOUNT_ID).split(",".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
//                unitOccupancyStatues=intent.getStringExtra(UNITOCCUPANCYSTATUS).split(",".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                if (unitid_dataList.size > 0) {
                    for (i in 0 until unitid_dataList.size) {
                        if(intent.getStringExtra("EntryTime")!=""){
                            time=intent.getStringExtra("EntryTime")
                        }else{
                            time=DateTimeUtils.getCurrentTimeLocal()
                        }

                        try {
                            val unitid = unitid_dataList.get(i).replace(" ", "").toInt();
                            getUnitLog(
                                unitid,
                                intent.getStringExtra("name"),
                                "",
                                intent.getStringExtra(VISITOR_TYPE),
                                intent.getStringExtra(VISITOR_TYPE),
                                0,
                                unitname_dataList.get(i).replace(" ", ""),
                                intent.getIntExtra("VLVisLgID", 0),
                                intent.getStringExtra("msg"),
                                intent.getStringExtra("nr_id"),
                                sendNotification,""+Prefs.getInt(ASSOCIATION_ID, 0).toString()+ NOTIF_STAFF_ENTRY+unitid,
                                time!!
                            )
                        } catch (e: Exception) {

                        }
                    }
                }
            } else {
                if(intent.getStringExtra("EntryTime")!=""){
                    time=intent.getStringExtra("EntryTime")
                }else{
                    time=DateTimeUtils.getCurrentTimeLocal()
                }

                try {
                    val unitid = intent.getStringExtra(UNITID).toInt()
                    getUnitLog(intent.getStringExtra(UNITID).toInt(), intent.getStringExtra("name"), "", intent.getStringExtra(VISITOR_TYPE), intent.getStringExtra(VISITOR_TYPE), 0, intent.getStringExtra("name"), intent.getIntExtra("VLVisLgID", 0), intent.getStringExtra("msg"), intent.getStringExtra("nr_id"), sendNotification, ""+Prefs.getInt(ASSOCIATION_ID, 0).toString()+NOTIF_STAFF_ENTRY+unitid,time!!)
                } catch (e: Exception) {
                    e.printStackTrace()
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
            Log.d("SYCNCHECK", "in 65")

        } else if (intent.getStringExtra(BSR_Action).equals(SENDFCM_toSYNC_VISITORENTRY)) {
            sendFCM_toSyncNonreg()
        } else if (intent.getStringExtra(BSR_Action).equals("sendFCM_toStopEmergencyAlert")) {
            sendFCM_toStopEmergencyAlert()
        } else if (intent.getStringExtra(BSR_Action).equals(SYNC_STAFF_BIOMETRIC)) {
            downloadFingerPrint_newFunction(intent.getIntExtra("ID", 0))

        } else if (intent.getStringExtra(BSR_Action).equals(UPLOAD_STAFF_PHOTO)) {
            Log.d("uploadImage", "in " + intent.getStringExtra("imgName"))
            val wrrw = intent.getByteArrayExtra(PERSON_PHOTO)
            if (wrrw != null) {
                var mBitmap: Bitmap

                mBitmap = BitmapFactory.decodeByteArray(wrrw, 0, wrrw.size)
                uploadImage(intent.getStringExtra("imgName"), mBitmap)

            } else {
                Log.e("uploadImage", "else " + intent.getStringExtra("imgName"))
            }
        } else if (intent.getStringExtra(BSR_Action).equals(SYNC_STAFF_LIST)) {
            StaffRepo.getStaffList(true)
            getCheckPointList()
        } else if (intent.getStringExtra(BSR_Action).equals(SYNC_UNIT_LIST)) {
            getUnitList()
            getCheckPointList()
        } else if (intent.getStringExtra(BSR_Action).equals(VISITOR_ENTRY_SYNC)) {

            Log.d("SYCNCHECK", "in 86")
            VisitorLogRepo.get_IN_VisitorLog(true, object : VisitorLogRepo.VisitorLogFetchListener {
                override fun onFetch(visitorLog: ArrayList<VisitorLog>?, errorMessage: String?) {

                    val smsIntent = Intent(SYNC)
                    smsIntent.putExtra("message", VISITOR_ENTRY_SYNC)
                    LocalBroadcastManager.getInstance(context).sendBroadcast(smsIntent)

                }

            })

        } else if (intent.getStringExtra(BSR_Action).equals(UPLOAD_GUARD_PHOTO)) {
            Log.d("uploadImage", "in " + intent.getStringExtra("imgName"))
            val wrrw = intent.getByteArrayExtra("GUARD_PHOTO")
            if (wrrw != null) {
                var mBitmap: Bitmap
                mBitmap = BitmapFactory.decodeByteArray(wrrw, 0, wrrw.size)
                uploadImage(intent.getStringExtra("imgName"), mBitmap)

            } else {
                Log.d("uploadImage22", "else " + intent.getStringExtra("imgName"))
            }
        } else if (intent.getStringExtra(BSR_Action).equals(SENDAUDIO)) {
            //Toast.makeText(context,"coming",Toast.LENGTH_LONG).show()
            sendFCM_forAudioMessage(intent.getStringExtra("FILENAME"))
        } else if (intent.getStringExtra(BSR_Action).equals(BGS_SOS_STATUS)) {
            Log.e("BGS_SOS_STATUS", "BGS_SOS_STATUS")
            val sosId = intent.getIntExtra("sos_id", 0)
            val sosStatus = intent.getStringExtra("sos_status")
            val gateNumber = Prefs.getString(ConstantUtils.GATE_NO, "")
            val gateMob = Prefs.getString(ConstantUtils.GATE_MOB, "")
            if (sosId != 0 && !sosStatus.equals("")) {
                Log.e("BGS_SOS_STATUS", "" + sosId + " " + gateNumber + " " + gateMob + " " + sosStatus)
                val sosObj: SOSUpdateReq = SOSUpdateReq(sosId, gateNumber, gateMob, sosStatus)
                updateSOS(sosObj)
            }
        } else if (intent.getStringExtra(BSR_Action).equals(BGS_PATROLLING_ALARM)) {
            getPatrollingSchedules()
        } else if (intent.getStringExtra(BSR_Action).equals(VISITOR_EXIT_NOTIFY)) {
            try {
                val associationID: Int = intent.getIntExtra("associationID", 0)//14948
                val associationName: String = intent.getStringExtra("associationName")
                val ntDesc: String = intent.getStringExtra("ntDesc")
                val ntTitle: String = intent.getStringExtra("ntTitle")
                val ntType: String = intent.getStringExtra("ntType")
                val sbSubID: String = intent.getStringExtra("sbSubID")//40841
                val userID: Int = intent.getIntExtra("userID", 0)
                val unitID: String = intent.getStringExtra("unitID")//40841
                Log.e("BEFORE_",""+associationID+"-"+associationName+"-"+ntDesc+"-"+ntTitle+"-"+ntType+"-"+sbSubID+"-"+userID+"-"+unitID);
                val topic = ""+associationID+ NOTIF_STAFF_EXIT+unitID;
                sendCloudFunctionNotification(associationID,associationName,ntDesc,ntTitle,ntType,sbSubID,userID,unitID,topic)
            }catch (e:Exception){
                e.printStackTrace()
            }
        }else if (intent.getStringExtra(BSR_Action).equals(BGS_OFFLINE_SYNC)) {
            Log.e("BGS_OFFLINE_SYNC","BGS_OFFLINE_SYNC")
            Toast.makeText(mcontext,"Syncing offline ...",Toast.LENGTH_LONG).show()
        }

    }

    private fun downloadFingerPrint_newFunction(workerID: Int) {
        var ba_fp1: ByteArray
        var ba_fp2: ByteArray
        var ba_fp3: ByteArray

        RetrofitClinet.instance
            .getStaffBiometric(OYE247TOKEN, workerID, "Regular")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CommonDisposable<StaffBiometricResp<StaffBiometricData>>() {

                override fun onSuccessResponse(staffBiometricResp: StaffBiometricResp<StaffBiometricData>) {

                    if (staffBiometricResp.success) {
                        Log.d("getStaffBiometric", staffBiometricResp.data.toString())
                        try {

                            for (i in 0 until staffBiometricResp.data.fingerPrint.size) {

                                Log.d(
                                    "getStaffBiometric",
                                    "finger " + " " + intToString(staffBiometricResp.data.fingerPrint.get(i).fmid) + " " +
                                            staffBiometricResp.data.fingerPrint.get(i).fpFngName + " "
                                )
                                val fp1 = staffBiometricResp.data.fingerPrint.get(i).fpImg1
                                val fp2 = staffBiometricResp.data.fingerPrint.get(i).fpImg2
                                val fp3 = staffBiometricResp.data.fingerPrint.get(i).fpImg3

                                try {

                                    ba_fp1 = Base64.decode(fp1, Base64.DEFAULT)
                                    ba_fp2 = Base64.decode(fp2, Base64.DEFAULT)
                                    ba_fp3 = Base64.decode(fp3, Base64.DEFAULT)

                                    RealmDB.insertFingerPrints(
                                        staffBiometricResp.data.fingerPrint.get(i).fpid,
                                        intToString(staffBiometricResp.data.fingerPrint.get(i).fmid),
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
                    Log.d("Error WorkerList", e.toString())

                }

                override fun noNetowork() {

                }
            })

    }

    private fun sendFCM(msg: String, mobNum: String, name: String, nr_id: String, unitname: String, memType: String) {

        val dataReq = VisitorEntryFCMData("visitorEntryApproval", Prefs.getInt(ASSOCIATION_ID, 0), msg, mobNum, name, nr_id)
        Log.d("sendFCM", "dataReq " + dataReq.toString())
        var req = VisitorEntryFCMReq(dataReq, "/topics/UnitOwner" + unitname.trim() + "Assn" + Prefs.getInt(ASSOCIATION_ID, 0))
        Log.d("sendFCM", "req " + req.toString())

        if (memType.equals("Tenant")) {
            req = VisitorEntryFCMReq(dataReq, "/topics/UnitTenant" + unitname + "Assn" + Prefs.getInt(ASSOCIATION_ID, 0))
        }

        val compositeDisposable = CompositeDisposable()

        compositeDisposable.add(
            FCMRetrofitClinet.instance.sendFCM_VisitorEntry(ConstantUtils.FCMToken, req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : CommonDisposable<VisitorEntryFCMResp>() {
                    override fun onSuccessResponse(globalApiObject: VisitorEntryFCMResp) {
                        Log.d("sendFCM", "StaffEntry " + globalApiObject.message_id + " " + globalApiObject.toString())
                    }

                    override fun onErrorResponse(e: Throwable) {
//                    Utils.showToast(applicationContext, getString(R.string.some_wrng))
                        Log.d("sendFCM", "onErrorResponse  " + e.toString())
                    }

                    override fun noNetowork() {
//                    Utils.showToast(applicationContext, getString(R.string.no_internet))
                    }

                    override fun onShowProgress() {
                    }

                    override fun onDismissProgress() {
                    }
                })
        )


    }

    fun uploadImage(localImgName: String, incidentPhoto: Bitmap?) {
        Log.e("uploadImage", localImgName)
        var byteArrayProfile: ByteArray?
        val mPath = Environment.getExternalStorageDirectory().toString() + "/" + localImgName + ".jpg"
        val imageFile = File(mPath)

        try {
            val outputStream = FileOutputStream(imageFile)
            val quality = 80
            if (incidentPhoto != null) {
                incidentPhoto.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            }
            outputStream.flush()
            outputStream.close()

            val bosProfile = ByteArrayOutputStream()
            if (incidentPhoto != null) {
                incidentPhoto.compress(Bitmap.CompressFormat.JPEG, quality, bosProfile)
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
                    Log.e("uploadImage", "response:" + response.body()!!)
                    Prefs.putString(ConstantUtils.PATROLLING_HIDDEN_SELFIE, "" + localImgName)
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

    private fun getPatrollingSchedules() {
        RetrofitClinet.instance
            .patrolScheduleList(OYE247TOKEN, (Prefs.getString(GATE_NO, "")), AppUtils.intToString(Prefs.getInt(ASSOCIATION_ID, 0)))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CommonDisposable<ShiftsListResponse<ArrayList<PatrolShift>>>() {

                override fun onSuccessResponse(PatrolList: ShiftsListResponse<ArrayList<PatrolShift>>) {

                    if (PatrolList.success == true) {
                        var realm:Realm = Realm.getDefaultInstance()
                        var mTempShifts = PatrolList.data.patrollingShifts;
                        try {
                            if(!realm.isInTransaction) {
                                realm.beginTransaction()
                                realm.delete<CheckPointsOfSchedule>()
                            }
                            for (shift: PatrolShift in mTempShifts) {
                                var schedule: PatrolShiftRealm;
                                try {
                                    schedule = realm.createObject<PatrolShiftRealm>(shift.psPtrlSID)
                                }catch (e: RealmPrimaryKeyConstraintException){
                                    schedule = realm.where<PatrolShiftRealm>().equalTo("psPtrlSID",shift.psPtrlSID).findFirstAsync()
                                }
                                schedule.asAssnID = shift.asAssnID
                                schedule.deName = shift.deName;
                                schedule.psIsActive = shift.psIsActive
                                schedule.psRepDays = shift.psRepDays
                                schedule.psSltName = shift.psSltName
                                schedule.psSnooze = shift.psSnooze
                                schedule.psdCreated = shift.psdCreated
                                schedule.psdUpdated = shift.psdUpdated
                                schedule.pseTime = shift.pseTime
                                schedule.pssTime = shift.pssTime
                                realm.insertOrUpdate(schedule)

                                var maxId:Number? = realm.where<CheckPointsOfSchedule>().max("id")
                                if(maxId != null){
                                    maxId = maxId.toInt()+1
                                }else{
                                    maxId = 1
                                }
                                for(checkpointInfo: ScheduleCheckPointsData in shift.point){
                                    Log.e("MAXID",""+maxId)
                                    val checkPoint = realm.createObject<CheckPointsOfSchedule>(maxId)
                                    checkPoint.psPtrlSID = shift.psPtrlSID
                                    checkPoint.asAssnID = checkpointInfo.asAssnID
                                    checkPoint.cpCkPName = checkpointInfo.checks.get(0).cpCkPName
                                    checkPoint.cpcPntAt = checkpointInfo.checks.get(0).cpcPntAt
                                    checkPoint.cpgpsPnts = checkpointInfo.cpgpsPnts
                                    checkPoint.pcIsActive = checkpointInfo.pcIsActive
                                    checkPoint.pcid = checkpointInfo.pcid
                                    checkPoint.psChkPID = checkpointInfo.psChkPID
                                    checkPoint.cpOrder = checkpointInfo.cpOrder
                                    realm.insertOrUpdate(checkPoint)
                                    maxId +=1
                                }
                            }
                            if(realm.isInTransaction()){
                                realm.commitTransaction()
                            }
                            realm.close()
                        }catch (e: java.lang.Exception){
                            e.printStackTrace()
                        }



                        Log.e("ALARM_PATR", "" + PatrolList.data.patrollingShifts)

                        processPatrollingAlarm()

                    }
                }

                override fun onErrorResponse(e: Throwable) {


                }

                override fun noNetowork() {
                }
            })
    }

    fun processPatrollingAlarm(){
        val realm = Realm.getDefaultInstance()
        val patrollingShifts: RealmResults<PatrolShiftRealm> = realm.where<PatrolShiftRealm>()
            .findAllAsync()
        for (schedules: PatrolShiftRealm in patrollingShifts) {
            val sdf: SimpleDateFormat = SimpleDateFormat("EEEE")
            val d: Date = Date()
            val day = sdf.format(d)
            Log.e("ALARM_DAY", "" + day)
            if (schedules.psRepDays.contains(day, ignoreCase = true)) {
                Log.e("ALARM_DAYFOUND", "" + day)
                val timeFormat = SimpleDateFormat("yyyy-MM-dd")
                var currentTimeObj = timeFormat.format(Date())


                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                val formatter = SimpleDateFormat("HH:mm:ss")
                val formattedDate = formatter.format(parser.parse(schedules.pssTime))

                currentTimeObj = currentTimeObj + "T" + formattedDate

                //val startTime:String = AppUtils.getTimeFromDate(schedules.pssTime)
                //var startTimeObj = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(schedules.pssTime)
                val startTimeObj = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(currentTimeObj)

                Log.e("startTimeObj_NEW", "" + currentTimeObj)
                Log.e("startTimeObj", "" + startTimeObj.time)
                Log.e("endTime", "" + Date().time)
                val diff = startTimeObj.time - Date().time
                val seconds = diff / 1000
                val minutes = seconds / 60

                val snoozeScheduleTime = Prefs.getString(SNOOZE_SCHEDULE_TIME + schedules.psPtrlSID, "")
                val tempIsSnoozed = Prefs.getBoolean("IS_SNOOZED_" + schedules.psPtrlSID, false)
                if (tempIsSnoozed && !snoozeScheduleTime.equals("") && !snoozeScheduleTime.equals(schedules.pssTime)) {
                    Prefs.remove(SNOOZE_COUNT + schedules.psPtrlSID)
                    Prefs.remove(SNOOZE_IS_ACTIVE + schedules.psPtrlSID)
                    Prefs.remove(SNOOZE_TIME + schedules.psPtrlSID)
                    Prefs.remove(SNOOZE_SCHEDULE_TIME + schedules.psPtrlSID)
                }

                val isSnoozed: Boolean = Prefs.getBoolean("IS_SNOOZED_" + schedules.psPtrlSID, false)
                val snoozeCount: Int = Prefs.getInt(SNOOZE_COUNT + schedules.psPtrlSID, 0)
                val snoozedTime: String = Prefs.getString(SNOOZE_TIME + schedules.psPtrlSID, "")
                val snoozeMins: Long = getTimeDifference(snoozedTime)
                val completedTime = Prefs.getString(PATROLLING_COMPLETED_ON + schedules.psPtrlSID, "")
                var isPatrollingCompleted = false
                if (!completedTime.equals("")) {
                    val completedMins: Long = getTimeDifference(completedTime)
                    isPatrollingCompleted = completedMins < 6
                }

                Log.e("THE_DIFF", "" + minutes + " - " + isSnoozed + " - " + snoozeMins + "  - " + isPatrollingCompleted)

                if (((minutes <= 5 && minutes > -1) || (isSnoozed && snoozeCount < 3 && snoozeMins >= 5 && snoozeMins < 17)) && !isPatrollingCompleted) {
                    Log.e("INSIDE_1", "" + (minutes <= 5 && minutes > -1) + " - " + (isSnoozed && snoozeCount < 3 && snoozeMins >= 5 && snoozeMins < 17))
                    if (schedules.psSnooze) {
                        //Snooze enabled
                        showDialog("Active patrolling starts in few minutes", "Patrolling", true, "Snooze", schedules.psPtrlSID, schedules.pssTime)
                    } else {
                        showDialog("Active patrolling starts in few minutes", "Patrolling", true, "OK", schedules.psPtrlSID, "")
                    }
                    break

                } else if (isSnoozed && snoozeCount >= 3 && snoozeMins >= 20) {
                    Log.e("INSIDE_2", "" + (isSnoozed && snoozeCount >= 3 && snoozeMins >= 20))
                    Prefs.remove(SNOOZE_COUNT + schedules.psPtrlSID)
                    Prefs.remove(SNOOZE_IS_ACTIVE + schedules.psPtrlSID)
                    Prefs.remove(SNOOZE_TIME + schedules.psPtrlSID)

                    if (minutes <= 5 && minutes > -1 && !isPatrollingCompleted) {

                        if (schedules.psSnooze) {
                            //Snooze enabled
                            showDialog("Active patrolling starts in few minutes", "Patrolling", true, "Snooze", schedules.psPtrlSID, schedules.pssTime)
                        } else {
                            showDialog("Active patrolling starts in few minutes", "Patrolling", true, "OK", schedules.psPtrlSID, "")
                        }
                        break

                    }
                }
                Log.e("TIME_DIFF", "" + minutes)

            }
        }
    }

    fun showDialog(desc: String, title: String, isCancellable: Boolean, btnText: String, id: Int, scheduleTime: String) {
        val isSOS: Boolean = Prefs.getBoolean("ACTIVE_SOS", false)
        val isActiveAlert: Boolean = Prefs.getBoolean("ACTIVE_ALERT", false)
        val isSnoozed: Boolean = Prefs.getBoolean("IS_SNOOZED_" + id, false)


        Log.e("IS_SNOOZED_" + id, "" + isSnoozed + " - ATCVE? " + isActiveAlert)

        if (!isSOS && !isActiveAlert) {
            val alertDlg =
                Intent(mcontext, PatrollingAlert::class.java)
            alertDlg.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            alertDlg.putExtra("MSG", desc)
            alertDlg.putExtra("BTN_TEXT", btnText)
            alertDlg.putExtra("ANIM", R.raw.alarm)
            alertDlg.putExtra("TYPE", "PATROLLING_ALARM")
            alertDlg.putExtra("SCHEDULEID", id)
            alertDlg.putExtra("SNOOZED_SCHEDULE_TIME", scheduleTime)

            if (isSnoozed) {
                val snoozeCount: Int = Prefs.getInt(SNOOZE_COUNT + id, 0)
                val snoozedTime: String = Prefs.getString(SNOOZE_TIME + id, "")
                val minutes = getTimeDifference(snoozedTime)

                Log.e(SNOOZE_COUNT + id, "" + snoozeCount)
                Log.e(SNOOZE_TIME + id, "" + snoozedTime)
                Log.e(SNOOZE_TIME + id, "DIFFERE: " + minutes)

                if (snoozeCount < 3 && snoozedTime != null && !snoozedTime.equals("") && minutes >= 5) {
                    mcontext.startActivity(alertDlg)
                }
            } else {
                mcontext.startActivity(alertDlg)
            }
        }
    }


    private fun getTimeDifference(dateTime: String): Long {

        try {
            val snoozedTimeObj = SimpleDateFormat("dd/MM/yyyy hh:mm:ss").parse(dateTime)
            val currentTimeStr = SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(Date())
            val currentTimeObj = SimpleDateFormat("dd/MM/yyyy hh:mm:ss").parse(currentTimeStr)


            val diff = currentTimeObj.time - snoozedTimeObj.time
            val seconds = diff / 1000
            val minutes = seconds / 60
            return minutes
        } catch (e: java.lang.Exception) {
            return 0
        }
    }


    private fun updateSOS(sosUpdateReq: SOSUpdateReq) {

        RetrofitClinet.instance
            .updateSOS(OYE247TOKEN, sosUpdateReq)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CommonDisposable<SOSUpdateResp>() {
                override fun onSuccessResponse(t: SOSUpdateResp) {
                    Log.e("updateSOS", "SUCCESS " + t)
                    Prefs.remove("PENDING_SOS")
                }

                override fun onErrorResponse(e: Throwable) {
                    Log.e("updateSOS", "ERROR " + e)
                    val json: String = Gson().toJson(sosUpdateReq)
                    Prefs.putString("PENDING_SOS", json)
                }

                override fun noNetowork() {
                    Log.e("updateSOS", "NONETWROK ")
                    val json: String = Gson().toJson(sosUpdateReq)
                    Prefs.putString("PENDING_SOS", json)
                }

            })

    }


    private fun getUnitList() {

        RetrofitClinet.instance
            .unitList(CHAMPTOKEN, intToString(Prefs.getInt(ASSOCIATION_ID, 0)))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CommonDisposable<UnitList<ArrayList<UnitPojo>>>() {

                override fun onSuccessResponse(UnitList: UnitList<ArrayList<UnitPojo>>) {

                    if (UnitList.success == true) {
                        //  Log.d("cdvd",UnitList.toString());
                        var arrayListUnits = ArrayList<UnitPojo>()

                        arrayListUnits = ArrayList()
                        arrayListUnits = UnitList.data.unit

                        Collections.sort(arrayListUnits, object : Comparator<UnitPojo> {
                            override fun compare(lhs: UnitPojo, rhs: UnitPojo): Int {
                                return lhs.unUniName.compareTo(rhs.unUniName, true)
                            }
                        })
                        //  LocalDb.saveUnitList(arrayListUnits);

                    } else {

                    }
                }

                override fun onErrorResponse(e: Throwable) {
                    Log.d("cdvd", e.message)

                }

                override fun noNetowork() {

                }
            })

    }

    fun sendFCM_toSyncNonreg() {

        Log.d("SYCNCHECK", "in 452")
        val dataReq = VisitorEntryFCMData(BACKGROUND_SYNC, Prefs.getInt(ASSOCIATION_ID, 0), "", "", NONREGULAR, "")
        Log.d("sendFCM", "dataReq " + dataReq.toString())
        var req = VisitorEntryFCMReq(dataReq, "/topics/AllGuards" + Prefs.getInt(ASSOCIATION_ID, 0))
        Log.d("sendFCM", "req " + req.toString())

        val compositeDisposable = CompositeDisposable()

        compositeDisposable.add(
            FCMRetrofitClinet.instance.sendFCM_VisitorEntry(ConstantUtils.FCMToken, req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : CommonDisposable<VisitorEntryFCMResp>() {
                    override fun onSuccessResponse(globalApiObject: VisitorEntryFCMResp) {
                        Log.d("sendFCM", "StaffEntry " + globalApiObject.message_id + " " + globalApiObject.toString())
                        Log.d("SYCNCHECK", "in 468")
                    }

                    override fun onErrorResponse(e: Throwable) {
//                    Utils.showToast(applicationContext, getString(R.string.some_wrng))
                        Log.d("sendFCM", "onErrorResponse  " + e.toString())
                        Log.d("SYCNCHECK", "in 473")
                    }

                    override fun noNetowork() {
//                    Utils.showToast(applicationContext, getString(R.string.no_internet))
                    }

                    override fun onShowProgress() {
                    }

                    override fun onDismissProgress() {
                    }
                })
        )
    }

    fun sendFCM_toStopEmergencyAlert() {

        Log.d("toStopEmergencyAlert", "in 452")
        val dataReq = VisitorEntryFCMData("emergencyAttend", Prefs.getInt(ASSOCIATION_ID, 0), "", "", "", "")
        Log.d("sendFCM", "dataReq " + dataReq.toString())
        var req = VisitorEntryFCMReq(dataReq, "/topics/AllGuards" + Prefs.getInt(ASSOCIATION_ID, 0))
        Log.d("toStopEmergencyAlert", "req " + req.toString())

        val compositeDisposable = CompositeDisposable()

        compositeDisposable.add(
            FCMRetrofitClinet.instance.sendFCM_VisitorEntry(ConstantUtils.FCMToken, req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : CommonDisposable<VisitorEntryFCMResp>() {
                    override fun onSuccessResponse(globalApiObject: VisitorEntryFCMResp) {
                        Log.d("sendFCM", "StaffEntry " + globalApiObject.message_id + " " + globalApiObject.toString())
                        Log.d("toStopEmergencyAlert", "in 468")
                    }

                    override fun onErrorResponse(e: Throwable) {
//                    Utils.showToast(applicationContext, getString(R.string.some_wrng))
                        Log.d("sendFCM", "onErrorResponse  " + e.toString())
                        Log.d("toStopEmergencyAlert", "in 473")
                    }

                    override fun noNetowork() {
//                    Utils.showToast(applicationContext, getString(R.string.no_internet))
                    }

                    override fun onShowProgress() {
                    }

                    override fun onDismissProgress() {
                    }
                })
        )
    }

    fun sendFCM_forAudioMessage(filename: String) {


        val dataReq = VisitorEntryFCMData("audiomessage", Prefs.getInt(ASSOCIATION_ID, 0), filename, "", "", "")
        Log.d("sendFCM", "dataReq " + dataReq.toString())
        var req = VisitorEntryFCMReq(dataReq, "/topics/AllGuards" + Prefs.getInt(ASSOCIATION_ID, 0))
        Log.d("toStopEmergencyAlert", "req " + req.toString())

        val compositeDisposable = CompositeDisposable()

        compositeDisposable.add(
            FCMRetrofitClinet.instance.sendFCM_VisitorEntry(ConstantUtils.FCMToken, req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : CommonDisposable<VisitorEntryFCMResp>() {
                    override fun onSuccessResponse(globalApiObject: VisitorEntryFCMResp) {
                        Log.d("SENDAUDIO", "StaffEntry " + globalApiObject.message_id + " " + globalApiObject.toString())
                        Log.d("SENDAUDIO", "in 549")
                    }

                    override fun onErrorResponse(e: Throwable) {
//                    Utils.showToast(applicationContext, getString(R.string.some_wrng))
                        Log.d("SENDAUDIO", "onErrorResponse  " + e.toString())
                        Log.d("SENDAUDIO", "in 555")
                    }

                    override fun noNetowork() {
//                    Utils.showToast(applicationContext, getString(R.string.no_internet))
                    }

                    override fun onShowProgress() {
                    }

                    override fun onDismissProgress() {
                    }
                })
        )
    }


    private fun getCheckPointList() {

        RetrofitClinet.instance
            .getCheckPointList(OYE247TOKEN, intToString(LocalDb.getAssociation().asAssnID))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CommonDisposable<CheckpointListResp<CheckPointByAssocID>>() {

                override fun onSuccessResponse(workerListResponse: CheckpointListResp<CheckPointByAssocID>) {

                    if (workerListResponse.data.checkPointListByAssocID != null) {
                        Log.d("WorkerList success", workerListResponse.data.toString())
                        var arrayList: ArrayList<CheckPointByAssocID>? = null
                        arrayList = ArrayList()
                        arrayList = workerListResponse.data.checkPointListByAssocID

                        Collections.sort(arrayList, object : Comparator<CheckPointByAssocID> {
                            override fun compare(lhs: CheckPointByAssocID, rhs: CheckPointByAssocID): Int {
                                return lhs.cpCkPName.compareTo(rhs.cpCkPName)
                            }
                        })

                        LocalDb.saveCheckPointList(arrayList)

                    } else {

                    }
                }

                override fun onErrorResponse(e: Throwable) {
                    Log.d("Error WorkerList", e.toString())
                }

                override fun noNetowork() {

                }
            })
    }


    private fun sendCloudFunctionNotification(associationID: Int, associationName: String, ntDesc: String, ntTitle: String, ntType: String, sbSubID: String, userID: Int, unitID: String, topicName:String) {

        val dataReq = CloudFunctionNotificationReq(associationID, associationName, ntDesc, ntTitle, ntType, sbSubID, userID, unitID, topicName)

        CloudFunctionRetrofitClinet.instance
            .sendCloud_VisitorEntry(dataReq)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CommonDisposable<Any>() {

                override fun onSuccessResponse(any: Any) {

                    Log.i("baaag", "cloud notification sent to -> $dataReq")

                }

                override fun onErrorResponse(e: Throwable) {
                    Log.d("Error WorkerList", e.toString())
                }

                override fun noNetowork() {

                }
            })
    }

    private fun sendCloudFunctionNotificationAdmin(associationID: Int, associationName: String, ntDesc: String, ntTitle: String, ntType: String, sbSubID: String, userID: Int, unitID: String, topicName:String) {

        val dataReq = CloudFunctionNotificationReq(associationID, associationName, ntDesc, ntTitle, ntType, sbSubID, userID, unitID, topicName)


        CloudFunctionRetrofitClinet.instance
            .sendResidentAdminNotification(dataReq)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CommonDisposable<Any>() {

                override fun onSuccessResponse(any: Any) {


                }

                override fun onErrorResponse(e: Throwable) {
                    Log.d("Error WorkerList", e.toString())
                }

                override fun noNetowork() {

                }
            })
    }


    private fun getNotificationCreate(ACAccntID: String, ASAssnID: String, NTType: String, NTDesc: String, SBUnitID: String, SBMemID: String, SBSubID: String, SBRoleID: String, ASAsnName: String, MRRolName: String, NTDUpdated: String, NTDCreated: String, VLVisLgID: String) {


        val dataReq = NotificationCreateReq(ACAccntID, ASAssnID, NTType, NTDesc, SBUnitID, SBMemID, SBSubID, SBRoleID, ASAsnName, MRRolName, NTDUpdated, NTDCreated, VLVisLgID, "", "")

        Log.v("VisitorLog Id DATA", dataReq.SBMemID + ".." + dataReq.SBUnitID)

        RetrofitClinet.instance
            .getNotificationCreate(OYE247TOKEN, dataReq)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CommonDisposable<NotificationCreateResponse>() {

                override fun onSuccessResponse(notificationCreateResponse: NotificationCreateResponse) {

                    Log.i("baaag", "notification sent to ${VLVisLgID} -> $dataReq")

                }


                override fun onErrorResponse(e: Throwable) {
                    Log.d("Error WorkerList", e.toString())
                }

                override fun noNetowork() {

                }
            })
    }

    private fun getUnitLog(unitId: Int, personName: String, mobileNumb: String, desgn: String,
                           workerType: String, staffID: Int, unitName: String, vlVisLgID: Int, msg: String, nrId: String, sendNotification: Boolean,topicName: String,entryTime:String) {

        Log.e("getUnitLog_SAV",""+unitId+" - "+personName);
        RetrofitClinet.instance
            .getUnitListbyUnitId("1FDF86AF-94D7-4EA9-8800-5FBCCFF8E5C1", unitId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CommonDisposable<UnitlistbyUnitID>() {

                override fun onSuccessResponse(UnitList: UnitlistbyUnitID) {
                    if (UnitList.success) {

                        if (UnitList.data.unit.unOcStat.contains("Sold Owner Occupied Unit")) {

                            if (!UnitList.data.unit.owner.isEmpty()) {

                                try {
                                 // for (i in 0..UnitList.data.unit.owner.size) {
                                        unAccountID = UnitList.data.unit.owner[0].acAccntID
                                        getFamilyMemberData(
                                            unitId.toString(), Prefs.getInt(ASSOCIATION_ID, 0),
                                            unAccountID!!.toInt(), desgn, msg, vlVisLgID,
                                            sendNotification,topicName,entryTime
                                        )
                                  //  }
                                } catch (e: IndexOutOfBoundsException) {

                                }
                            } else {
                                unAccountID = 0
                            }


                        } else if (UnitList.data.unit.unOcStat.contains("Sold Tenant Occupied Unit")) {
                            if (!UnitList.data.unit.tenant.isEmpty()) {
                                try {
                                   // for (i in 0..UnitList.data.unit.tenant.size) {

                                        unAccountID = UnitList.data.unit.tenant[0].acAccntID
                                        getFamilyMemberData(
                                            unitId.toString(), Prefs.getInt(ASSOCIATION_ID, 0),
                                            unAccountID!!.toInt(), desgn, msg, vlVisLgID,
                                            sendNotification,topicName,entryTime
                                        )

                                  //  }
                                } catch (e: IndexOutOfBoundsException) {

                                }
                            } else {
                                unAccountID = 0
                            }

                        } else if (UnitList.data.unit.unOcStat.contains("UnSold Tenant Occupied Unit")) {

                            if (!UnitList.data.unit.tenant.isEmpty()) {

                                try {
                                   // for (i in 0..UnitList.data.unit.tenant.size) {

                                        unAccountID =
                                            UnitList.data.unit.tenant[0].acAccntID
                                        getFamilyMemberData(
                                            unitId.toString(), Prefs.getInt(ASSOCIATION_ID, 0),
                                            unAccountID!!.toInt(), desgn, msg, vlVisLgID,
                                            sendNotification,topicName,entryTime
                                        )

                                  //  }
                                } catch (e: IndexOutOfBoundsException) {

                                }

                            } else {
                                unAccountID = 0
                            }

                        }
//                        else if (UnitList.data.unit.unOcStat.contains("UnSold Vacant Unit")) {
////                                    if(!UnitList.data.unit.owner.isEmpty()) {
////                                        unAccountID = "0"
////                                    } else{
//                            unAccountID = 0
//                            // }
//
//                        } else if (UnitList.data.unit.unOcStat.contains("Sold Vacant Unit")) {
////                            if (!UnitList.data.unit.owner.isEmpty()) {
////                                try {
////
////                                    for (i in 0..UnitList.data.unit.owner.size) {
////                                        unAccountID = UnitList.data.unit.owner[i].acAccntID.toString()
////                                        getFamilyMemberData(
////                                            unitId.toString(), Prefs.getInt(ASSOCIATION_ID, 0),
////                                            unAccountID!!.toInt(), desgn, msg, vlVisLgID,
////                                            sendNotification, topicName
////                                        )
////                                    }
////                                } catch (e: IndexOutOfBoundsException) {
////
////                                }
////
////                            }
//                          //  else {
//                                unAccountID = 0
//                       //     }
//                        } else {
//                            unAccountID = 0
//                        }


                        try {
                            sendFCM(
                                msg, mobileNumb,
                                personName, nrId,
                                unitName, "Owner"
                            )

                        } catch (e: KotlinNullPointerException) {

                        }

                        try {
                            if (sendNotification) {
                                getNotificationCreate(
                                    unAccountID.toString(), Prefs.getInt(ASSOCIATION_ID, 0).toString(), "gate_app", msg, unitId.toString(), vlVisLgID.toString(), vlVisLgID.toString() + "admin", "gate_app", LocalDb.getAssociation()!!.asAsnName, "gate_app",
                                    DateTimeUtils.getCurrentTimeLocal(),
                                    DateTimeUtils.getCurrentTimeLocal(),
                                    vlVisLgID.toString()
                                )
                            }
                        } catch (e: KotlinNullPointerException) {

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
                                vlVisLgID.toString() + "admin",
                                unAccountID!!.toInt(),
                                unAccountID.toString(),topicName
                            )
                        } catch (e: KotlinNullPointerException) {

                        }

                    } else {
                    }
                }

                override fun onErrorResponse(e: Throwable) {
                    Log.d("cdvd", "$unitId ->>> ${e.message}")


                }

                override fun noNetowork() {

                }
            })

    }


    fun getFamilyMemberData(unitId: String, assnID: Int, accountId: Int, desgn: String, msg: String, vlVisLgID: Int, sendNotification: Boolean, topicName:String,entryTime:String) {
        RetrofitClinet.instance.getFamilyMemberList(OYE247TOKEN, unitId, assnID.toString(), accountId.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CommonDisposable<GetFamilyMemberResponse<ArrayList<FamilyMember>>>() {

                override fun onSuccessResponse(getdata: GetFamilyMemberResponse<ArrayList<FamilyMember>>) {

                    if (getdata.success) {


                        try {

                            for (i in 0..getdata.data.familyMembers.size) {
                                if (sendNotification) {
                                    try {
                                        getNotificationCreate(
                                            getdata.data.familyMembers[i].acAccntID.toString(),
                                            Prefs.getInt(ASSOCIATION_ID, 0).toString(),
                                            "gate_app",
                                            msg,
                                            unitId.toString(),
                                            vlVisLgID.toString(),
                                            vlVisLgID.toString() + "admin",
                                            "gate_app",
                                            LocalDb.getAssociation()!!.asAsnName,
                                            "gate_app",
                                            DateTimeUtils.getCurrentTimeLocal(),
                                            entryTime,
                                            vlVisLgID.toString()
                                        )
                                    } catch (e: KotlinNullPointerException) {

                                    }
                                }
                                sendCloudFunctionNotification(
                                    Prefs.getInt(ASSOCIATION_ID, 0),
                                    LocalDb.getAssociation()!!.asAsnName,
                                    msg,
                                    desgn,
                                    "gate_app",
                                    vlVisLgID.toString() + "admin",
                                    getdata.data.familyMembers[i].acAccntID,
                                    getdata.data.familyMembers[i].acAccntID.toString(),topicName
                                )

                            }
                        } catch (e: IndexOutOfBoundsException) {

                        }
                    }

                }

                override fun onErrorResponse(e: Throwable) {
                    // visitorLog(unitId, personName, mobileNumb, desgn, workerType, staffID, unitName,wkEntryImg)
                    //  visitorLogBiometric(unitId, personName, mobileNumb, desgn, workerType, staffID, unitName,wkEntryImg)

                }

                override fun noNetowork() {
                    Toast.makeText(mcontext, "No network call ", Toast.LENGTH_LONG).show()
                }
            })


    }

}