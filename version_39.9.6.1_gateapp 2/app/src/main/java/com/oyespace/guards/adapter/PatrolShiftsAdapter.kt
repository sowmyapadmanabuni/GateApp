package com.oyespace.guards.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import com.airbnb.lottie.LottieAnimationView
import com.oyespace.guards.R
import com.oyespace.guards.models.PatrolShift
import com.oyespace.guards.pojo.BlocksData
import com.oyespace.guards.utils.AppUtils
import com.oyespace.guards.utils.ConstantUtils.ACTIVE_PATROLLING_SCHEDULE
import com.oyespace.guards.utils.Prefs


class PatrolShiftsAdapter(private val mcontext: Context, private val arrayList: ArrayList<PatrolShift>, val clickListener:(PatrolShift, Int) -> Unit):
    androidx.recyclerview.widget.RecyclerView.Adapter<PatrolShiftsAdapter.ItemViewHolder>() {


    override fun onBindViewHolder(p0: ItemViewHolder, p1: Int) {
        val ongoingSchedule = Prefs.getInt(ACTIVE_PATROLLING_SCHEDULE, -1);
        if(ongoingSchedule != -1 && ongoingSchedule == arrayList[p1].psPtrlSID){
            p0.mWarning.visibility = View.VISIBLE
        }else{
            p0.mWarning.visibility = View.GONE
        }
        p0.mScheduleName.text = arrayList[p1].psSltName
        p0.mScheduleTime.isSelected = true
        p0.mScheduleDays.text = arrayList[p1].psRepDays
        p0.mScheduleTime.text = "From "+AppUtils.getTimeFromDate(arrayList[p1].pssTime)+" To "+AppUtils.getTimeFromDate(arrayList[p1].pseTime)

        p0.itemView.setOnClickListener(View.OnClickListener {
            clickListener(arrayList[p1],p1)
        })
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ItemViewHolder {
        return PatrolShiftsAdapter.ItemViewHolder(
            LayoutInflater.from(p0.context).inflate(R.layout.item_patroll_shedule, p0, false)
        )
    }

    override fun getItemCount(): Int {
       return arrayList.size;
    }


    class ItemViewHolder(itemView: View) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val mScheduleName: AppCompatTextView
        val mScheduleTime: AppCompatTextView
        val mScheduleDays:AppCompatTextView
        val mWarning: LottieAnimationView
        val mScheduleCard:CardView

        init {
            mScheduleName = itemView.findViewById(R.id.schedule_name)
            mScheduleTime = itemView.findViewById(R.id.schedule_time)
            mScheduleDays = itemView.findViewById(R.id.schedule_days)
            mWarning = itemView.findViewById(R.id.warning_pending)
            mScheduleCard = itemView.findViewById(R.id.schedule_card)
        }
    }

}