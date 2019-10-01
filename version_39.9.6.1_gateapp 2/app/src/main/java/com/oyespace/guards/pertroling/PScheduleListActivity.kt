package com.oyespace.guards.pertroling

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import com.oyespace.guards.R
import com.oyespace.guards.activity.BaseKotlinActivity
import com.oyespace.guards.adapter.PatrolShiftsAdapter
import com.oyespace.guards.models.PatrolShift
import com.oyespace.guards.models.ShiftsListResponse
import com.oyespace.guards.network.CommonDisposable
import com.oyespace.guards.network.RetrofitClinet
import com.oyespace.guards.utils.AppUtils
import com.oyespace.guards.utils.ConstantUtils.*
import com.oyespace.guards.utils.Prefs
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_pschedule_list.*

class PScheduleListActivity:BaseKotlinActivity(){


    var mPatrolShiftArray = ArrayList<PatrolShift>()
    var mPatrolShiftsAdapter: PatrolShiftsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pschedule_list)
        title_block.findViewById<AppCompatTextView>(R.id.header_title).text = "Patrolling Schedules"
        getPatrollingSchedules()
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
        val mPatrolIntent = Intent(this@PScheduleListActivity, PatrollingLocActivity::class.java)
        mPatrolIntent.putExtra(PATROLLING_SCHEDULE_ID,selectedShift.psPtrlSID)
        startActivity(mPatrolIntent)
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
}