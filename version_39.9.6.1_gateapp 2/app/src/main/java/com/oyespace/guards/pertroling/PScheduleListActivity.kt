package com.oyespace.guards.pertroling

import android.app.Activity
import android.content.Intent
import android.os.BatteryManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import com.oyespace.guards.R
import com.oyespace.guards.activity.BaseKotlinActivity
import com.oyespace.guards.adapter.PatrolShiftsAdapter
import com.oyespace.guards.models.CheckPointsOfSheduleListResponse
import com.oyespace.guards.models.PatrolShift
import com.oyespace.guards.models.ShiftsListResponse
import com.oyespace.guards.network.CommonDisposable
import com.oyespace.guards.network.RetrofitClinet
import com.oyespace.guards.utils.AppUtils
import com.oyespace.guards.utils.ConstantUtils
import com.oyespace.guards.utils.ConstantUtils.*
import com.oyespace.guards.utils.Prefs
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_pschedule_list.*
import kotlinx.android.synthetic.main.header_with_back.*

class PScheduleListActivity:BaseKotlinActivity(){


    var mPatrolShiftArray = ArrayList<PatrolShift>()
    var mPatrolShiftsAdapter: PatrolShiftsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pschedule_list)
        title_block.findViewById<AppCompatTextView>(R.id.header_title).text = "Patrolling Schedules"
        ot_back.setOnClickListener{
            finish()
        }
        getPatrollingSchedules()
    }

    override fun onStart() {
        Prefs.putBoolean(ConstantUtils.ACTIVE_ALERT, true)
        super.onStart()
    }

    override fun onDestroy() {
        Prefs.putBoolean(ConstantUtils.ACTIVE_ALERT, false)
        super.onDestroy()
    }

    override fun onPause() {
        Prefs.putBoolean(ConstantUtils.ACTIVE_ALERT, false)
        super.onPause()
    }

    override fun onResume() {
        Prefs.putBoolean(ConstantUtils.ACTIVE_ALERT, true)
        super.onResume()
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

    private fun onPageClick(selectedShift:PatrolShift, index:Int){
        val ongoingSchedule = Prefs.getInt(ACTIVE_PATROLLING_SCHEDULE, -1)
        var scheduleExist:Boolean = false
        if(ongoingSchedule != -1) {
            for (shift: PatrolShift in mPatrolShiftArray) {
                if(shift.psPtrlSID == ongoingSchedule){
                    scheduleExist = true;
                }
            }
        }
        if(ongoingSchedule == -1 || ongoingSchedule == selectedShift.psPtrlSID){
            navigateToScanView(selectedShift)
        }else if(ongoingSchedule != -1 && !scheduleExist){
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

        val bm: BatteryManager = this.getSystemService(BATTERY_SERVICE) as BatteryManager;
        val batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)

        Log.e("BATTERY",""+batLevel)
        if(batLevel>30) {
            startActivityForResult(mPatrolIntent, 1)
        }else{
            showAnimatedDialog("Please connect your charger to continue",R.raw.battery,false,"OK")
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
                        mPatrolShiftArray = PatrolList.data.patrollingShifts;
                        setSchedulesAdapter()
                    }
                }

                override fun onErrorResponse(e: Throwable) {
                    dismissProgressrefresh()
                    Toast.makeText(this@PScheduleListActivity, "Error ", Toast.LENGTH_LONG).show()

                }

                override fun noNetowork() {
                    dismissProgressrefresh()
                    Toast.makeText(this@PScheduleListActivity, "No network call ", Toast.LENGTH_LONG).show()
                }
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1){
            if(resultCode == Activity.RESULT_OK){
                getPatrollingSchedules()
            }
        }
    }

}