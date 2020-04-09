package com.oyespace.guards.pertroling

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import com.androidhiddencamera.CameraConfig
import com.oyespace.guards.BackgroundSyncReceiver
import com.oyespace.guards.R
import com.oyespace.guards.activity.BaseKotlinActivity
import com.oyespace.guards.activity.PatrollingAlert
import com.oyespace.guards.adapter.PatrolShiftsAdapter
import com.oyespace.guards.models.*
import com.oyespace.guards.network.CommonDisposable
import com.oyespace.guards.network.RetrofitClinet
import com.oyespace.guards.services.APictureCapturingService
import com.oyespace.guards.services.PictureCapturingListener
import com.oyespace.guards.services.PictureCapturingServiceImpl
import com.oyespace.guards.utils.AppUtils
import com.oyespace.guards.utils.ConstantUtils
import com.oyespace.guards.utils.ConstantUtils.*
import com.oyespace.guards.utils.Prefs
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmQuery
import io.realm.RealmResults
import io.realm.exceptions.RealmPrimaryKeyConstraintException
import io.realm.kotlin.createObject
import io.realm.kotlin.delete
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_pschedule_list.*
import kotlinx.android.synthetic.main.header_with_back.*
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.fixedRateTimer

class PScheduleListActivity: BaseKotlinActivity(), PictureCapturingListener, ActivityCompat.OnRequestPermissionsResultCallback {


    var pictureService: APictureCapturingService?=null;
    var iv_torch: Button?=null
    var clickable1 = 0
    var mPatrolShiftArray = ArrayList<PatrolShift>()
    var mPatrolShiftsAdapter: PatrolShiftsAdapter? = null
    var mCameraConfig:CameraConfig? = null;
    var mSelectedShift:PatrolShift? =null
    var pTimer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pschedule_list)
        title_block.findViewById<AppCompatTextView>(R.id.header_title).text = "Patrolling Schedules"

        iv_torch=findViewById<Button>(R.id.iv_torch)
        iv_torch?.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                val camManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager;
                var cameraId: String? = null
                cameraId = camManager.getCameraIdList()[0];
                if(clickable1==0){
                    try {
                        iv_torch!!.background=resources.getDrawable(R.drawable.torch_off)
                        camManager.setTorchMode(cameraId, true);   //Turn ON

                        //  iv_torch!!.text = "OFF"
                        clickable1=1
                    } catch (e: CameraAccessException) {
                        e.printStackTrace();
                    }
                }
                else if(clickable1==1){
                    camManager.setTorchMode(cameraId, false);
                    // iv_torch!!.text = "ON"
                    iv_torch!!.background=resources.getDrawable(R.drawable.torch_on)
                    clickable1=0

                }
            }

        }

        ot_back.setOnClickListener{
            finish()
        }
        initRealm()

        loadFromRealm()
        getPatrollingSchedules()




        pictureService = PictureCapturingServiceImpl.getInstance(this);

    }

    override fun onStart() {
        //Prefs.putBoolean(ConstantUtils.ACTIVE_ALERT, true)
        super.onStart()
    }

    override fun onDestroy() {
       // Prefs.putBoolean(ConstantUtils.ACTIVE_ALERT, false)

        realm.close()
        super.onDestroy()
    }

    override fun onPause() {
       // Prefs.putBoolean(ConstantUtils.ACTIVE_ALERT, false)
        super.onPause()
    }

    override fun onResume() {
        //Prefs.putBoolean(ConstantUtils.ACTIVE_ALERT, true)
        super.onResume()
    }

    fun getMinuteDifference(incomingString:String):Long{
        val currentSDF = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        val currentDateString = currentSDF.format(Date())

        val currentDateObj = currentSDF.parse(currentDateString)
        val pausedDateObj = currentSDF.parse(incomingString)
        val diff = currentDateObj.time-pausedDateObj.time
        val seconds = diff / 1000
        val minutes = seconds / 60
        return minutes;
    }

    fun startMinutesTimer() {
        if(pTimer==null) {
            pTimer = fixedRateTimer("schedule_timer_checker", false, 0, 60000) {
                this@PScheduleListActivity.runOnUiThread {
                    Log.e("TIMER_1","Started")
                    val ongoingSchedule = Prefs.getInt(ACTIVE_PATROLLING_SCHEDULE, -1)
                    val pausedTimeString = Prefs.getString(ACTIVE_PATROLLING_LAST_TIME, "")
                    val scheduleExist = isScheduleExist(ongoingSchedule)
                    if (scheduleExist && ongoingSchedule != -1 && !pausedTimeString.equals("")) {
                        val minuteDiff = getMinuteDifference(pausedTimeString)
                        Log.e("checkMinutesTimer", "" + minuteDiff)
                        if(minuteDiff>=PATROLLING_PAUSE_TIMER){
                            Prefs.remove(ACTIVE_PATROLLING_SCHEDULE)
                            Prefs.remove(ACTIVE_PATROLLING_LAST_CP)
                            Prefs.remove(ACTIVE_PATROLLING_LAST_TIME)
                            setSchedulesAdapter()
                            if(pTimer != null){
                                pTimer!!.cancel()
                            }
                        }else if(minuteDiff == PATROLLING_PAUSE_REMINDER_TIMER){

                            val isReminder = Prefs.getBoolean(ConstantUtils.PATROLLING_RESUMED_TIME + ongoingSchedule,false)
                            if(!isReminder) {
                                val alertDlg =
                                    Intent(this@PScheduleListActivity, PatrollingAlert::class.java)
                                alertDlg.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                alertDlg.putExtra("MSG", "Resume patrolling in 2 minutes")
                                alertDlg.putExtra("BTN_TEXT", "OK")
                                alertDlg.putExtra("ANIM", R.raw.alarm)
                                alertDlg.putExtra("TYPE", "PATROLLING_PAUSE")
                                alertDlg.putExtra("SCHEDULEID", ongoingSchedule)
                                startActivity(alertDlg)
                                //showAnimatedDialog("Resume patrolling in 2 minutes",R.raw.error_alert,true,"OK")
                            }
                        }

                    }
                }
            }
        }else{
            Log.e("TIMER_2","NOT NULL")
        }
    }

    private fun setSchedulesAdapter(){
        mPatrolShiftsAdapter =
            PatrolShiftsAdapter(this@PScheduleListActivity, mPatrolShiftArray,clickListener = {
                    block,index -> onPageClick(block,index)
            })
        rcv_pat_schedules.adapter = mPatrolShiftsAdapter
        rcv_pat_schedules.setLayoutManager(
            androidx.recyclerview.widget.LinearLayoutManager(
                this@PScheduleListActivity
            )
        );
        mPatrolShiftsAdapter!!.notifyDataSetChanged()
    }

    private fun isScheduleExist(ongoingSchedule:Int):Boolean{
        for (shift: PatrolShift in mPatrolShiftArray) {
            if(shift.psPtrlSID == ongoingSchedule){
                return true;
            }
        }
        return false
    }

    private fun onPageClick(selectedShift:PatrolShift, index:Int){
        Log.e("ONPAGSECLIC","CLIKED");
        val ongoingSchedule = Prefs.getInt(ACTIVE_PATROLLING_SCHEDULE, -1)
        var scheduleExist:Boolean = false
        if(ongoingSchedule != -1) {
            scheduleExist = isScheduleExist(ongoingSchedule)
        }
        if(ongoingSchedule == -1 || ongoingSchedule == selectedShift.psPtrlSID){
            navigateToScanView(selectedShift)
        }else if(ongoingSchedule != -1 && !scheduleExist){
            Prefs.remove(ACTIVE_PATROLLING_SCHEDULE)
            Prefs.remove(ACTIVE_PATROLLING_LAST_CP)
            Prefs.remove(ACTIVE_PATROLLING_LAST_TIME)
            navigateToScanView(selectedShift)
        }
        else{
            showAnimatedDialog("Please complete the pending patrolling",R.raw.error_alert,true,"OK")
        }
    }

    private fun navigateToScanView(selectedShift:PatrolShift){
        val mPatrolIntent =
            Intent(this@PScheduleListActivity, PatrollingLocActivity::class.java)
        mPatrolIntent.putExtra(PATROLLING_SCHEDULE_ID, selectedShift.psPtrlSID)

        Prefs.remove(SNOOZE_COUNT+selectedShift.psPtrlSID)
        Prefs.remove(SNOOZE_IS_ACTIVE+selectedShift.psPtrlSID)
        Prefs.remove(SNOOZE_TIME+selectedShift.psPtrlSID)
        Prefs.remove(PATROLLING_RESUMED_TIME + selectedShift.psPtrlSID)
        if(pTimer != null){
            pTimer!!.cancel()
            pTimer = null
        }
        val bm: BatteryManager = this.getSystemService(BATTERY_SERVICE) as BatteryManager;
        val batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)

        Log.e("BATTERY",""+batLevel)
        if(batLevel>=30) {
            //startActivityForResult(mPatrolIntent, 1)
            mSelectedShift = selectedShift
            checkDrawOverWindowPermission()
        }else{
            showAnimatedDialog("Please connect your charger to continue",R.raw.battery,false,"OK")
        }
    }

    private fun startHiddenCamera(){
       // takePicture()
        showProgress("Fetching schedules..")
        pictureService!!.startCapturing(this)
    }


    private fun checkDrawOverWindowPermission(){


        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {
                val intent =  Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 1234);
            }else{
                startHiddenCamera();
            }
        } else {
            startHiddenCamera();
        }
    }

    override fun onCaptureDone(pictureUrl: String?, pictureData: ByteArray?) {
        dismissProgress()
        Log.e("CAPTURE_PIC_1",""+pictureUrl);

    }

    override fun onDoneCapturingAllPhotos(picturesTaken: TreeMap<String, ByteArray>?) {
    try {
        Log.e("CAPTURE_PIC_2", "" + picturesTaken!!.lastEntry().key);
        Log.e("CAPTURE_PIC_2", "" + picturesTaken!!.lastEntry().value);
        //Toast.makeText(this@PScheduleListActivity, "CAPTURE DONE", Toast.LENGTH_LONG).show()


        var bmp: Bitmap = AppUtils.decodeBitmap(picturesTaken!!.lastEntry().value)
        if (bmp != null) {
            val bytes = ByteArrayOutputStream()

            var byteArray: ByteArray? = null
            bmp.compress(Bitmap.CompressFormat.JPEG, 80, bytes)
            byteArray = bytes.toByteArray()


            //   uploadImage(imgName,personPhoto);
            val spl: List<String> = picturesTaken!!.lastEntry().key.split("/");
            Log.e("Acced", "" + spl.get(spl.size - 1))
            val ddc = Intent(applicationContext, BackgroundSyncReceiver::class.java)

            ddc.putExtra(BSR_Action, UPLOAD_GUARD_PHOTO)
            ddc.putExtra("imgName", spl.get(spl.size - 1))
            ddc.putExtra("GUARD_PHOTO", byteArray)
            sendBroadcast(ddc)
            dismissProgress()

            val mPatrolIntent =
                Intent(this@PScheduleListActivity, PatrollingLocActivity::class.java)
            mPatrolIntent.putExtra(PATROLLING_SCHEDULE_ID, mSelectedShift!!.psPtrlSID)

            Prefs.remove(SNOOZE_COUNT + mSelectedShift!!.psPtrlSID)
            Prefs.remove(SNOOZE_IS_ACTIVE + mSelectedShift!!.psPtrlSID)
            Prefs.remove(SNOOZE_TIME + mSelectedShift!!.psPtrlSID)

            val bm: BatteryManager = this.getSystemService(BATTERY_SERVICE) as BatteryManager;
            val batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)

            Log.e("BATTERY", "" + batLevel)
            //if(batLevel>=30) {

            startActivityForResult(mPatrolIntent, 1)
        } else {
            dismissProgress()
        }
        //Toast.makeText(this@PScheduleListActivity, "onDoneCapturingAllPhotos", Toast.LENGTH_LONG).show()
    }catch (e:Exception){
        dismissProgress()
        e.printStackTrace()
    }
    }





    private fun getPatrollingSchedules(){
        showProgressrefresh()
        RetrofitClinet.instance
            .patrolScheduleList(OYE247TOKEN,(Prefs.getString(GATE_NO, "")), AppUtils.intToString(Prefs.getInt(ASSOCIATION_ID, 0)))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CommonDisposable<ShiftsListResponse<ArrayList<PatrolShift>>>() {

                override fun onSuccessResponse(PatrolList: ShiftsListResponse<ArrayList<PatrolShift>>) {
                    dismissProgressrefresh()
                    if (PatrolList.success == true) {
                        var mTempShifts = PatrolList.data.patrollingShifts;
                        try {
                            if(!realm.isInTransaction) {
                                realm.beginTransaction()
                                realm.delete<CheckPointsOfSchedule>()
                                realm.delete<PatrolShiftRealm>()
                            }
                            for (shift: PatrolShift in mTempShifts) {
                                var schedule:PatrolShiftRealm;
                                try {
                                    schedule = realm.createObject<PatrolShiftRealm>(shift.psPtrlSID)
                                }catch (e:RealmPrimaryKeyConstraintException){
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
                                    checkPoint.cpOrder = checkpointInfo.cpOrder
                                    checkPoint.cpgpsPnts = checkpointInfo.cpgpsPnts
                                    checkPoint.pcIsActive = checkpointInfo.pcIsActive
                                    checkPoint.pcid = checkpointInfo.pcid
                                    checkPoint.psChkPID = checkpointInfo.psChkPID
                                    realm.insertOrUpdate(checkPoint)
                                    maxId +=1
                                }
                            }
                            if(realm.isInTransaction()){
                                realm.commitTransaction()
                            }
                            realm.close()
                        }catch (e:Exception){
                            e.printStackTrace()
                        }

                        loadFromRealm()

                    }else{
                        try {
                            Log.e("Error_Sched", "No")
                            if (!realm.isInTransaction) {
                                Log.e("Error_Sched", "Notintras")
                                realm.beginTransaction()
                                realm.delete<CheckPointsOfSchedule>()
                                realm.delete<PatrolShiftRealm>()
                            }
                            if (realm.isInTransaction()) {
                                Log.e("Error_Sched", "Commit")
                                realm.commitTransaction()
                            }
                            loadFromRealm()
                        }catch (e:Exception){
                            e.printStackTrace()
                        }
                    }
                }

                override fun onErrorResponse(e: Throwable) {
                    dismissProgressrefresh()
                    Toast.makeText(this@PScheduleListActivity, "Error ", Toast.LENGTH_LONG).show()
                  //  loadFromRealm()

                }

                override fun noNetowork() {
                    dismissProgressrefresh()
                    Toast.makeText(this@PScheduleListActivity, "No network call ", Toast.LENGTH_LONG).show()
                    //loadFromRealm()
                }
            })
    }

    fun loadFromRealm(){
        initRealm()
        val schedulesFromRealm:RealmResults<PatrolShiftRealm>  = realm.where<PatrolShiftRealm>()
            .findAllAsync()
        var updatedArrayList = ArrayList<PatrolShift>()
        val sdf: SimpleDateFormat = SimpleDateFormat("EEEE")
        val d: Date = Date()
        val day = sdf.format(d)
        for(shift:PatrolShiftRealm in schedulesFromRealm){
            if(shift.psRepDays.contains(day,ignoreCase = true)){
                val shifter:PatrolShift = PatrolShift(
                    psPtrlSID = shift.psPtrlSID,
                    psSnooze = shift.psSnooze,
                    pssTime = shift.pssTime,
                    pseTime = shift.pseTime,
                    psRepDays = shift.psRepDays,
                    deName = shift.deName,
                    psSltName = shift.psSltName,
                    asAssnID = shift.asAssnID,
                    psdCreated = shift.psdCreated,
                    psdUpdated = shift.psdUpdated,
                    psIsActive = shift.psIsActive,
                    point = ArrayList()

                )
                updatedArrayList.add(shifter)
            }
        }
//
        mPatrolShiftArray = updatedArrayList
        setSchedulesAdapter()

        realm.close()

        val ongoingSchedule = Prefs.getInt(ACTIVE_PATROLLING_SCHEDULE, -1)
        var scheduleExist:Boolean = false
        if(ongoingSchedule != -1) {
            scheduleExist = isScheduleExist(ongoingSchedule)
            if(scheduleExist){
                Log.e("Starting_","Calling Timer")
                startMinutesTimer()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1){
            if(resultCode == Activity.RESULT_OK){
                getPatrollingSchedules()
            }
        } else if (requestCode == 1234) {
            startHiddenCamera();
        }
    }

}