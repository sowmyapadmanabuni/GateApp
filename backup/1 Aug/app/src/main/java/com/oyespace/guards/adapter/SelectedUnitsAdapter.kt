package com.oyespace.guards.com.oyespace.guards.adapter

import android.content.Context
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import com.malinskiy.superrecyclerview.swipe.SwipeLayout
import com.oyespace.guards.R
import com.oyespace.guards.com.oyespace.guards.pojo.BlocksData
import com.oyespace.guards.com.oyespace.guards.pojo.PaginationData
import com.oyespace.guards.pojo.UnitPojo


class SelectedUnitsAdapter(private val mcontext: Context, private val arrayList: ArrayList<UnitPojo>, val clickListener:(UnitPojo, Int) -> Unit):
    RecyclerView.Adapter<SelectedUnitsAdapter.ItemViewHolder>(){


    override fun onBindViewHolder(p0: ItemViewHolder, p1: Int) {
        //p0.itemView.requestLayout();
//        var density = mcontext.getResources().getDisplayMetrics().density;
//        var dp = 40;
//        var dpVal = (density*dp).toInt()
//        var margin = (8*dp).toInt()

        var block = arrayList[p1].unUniName
//        if(block.length > 3){
//            p0.mPageCard.layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,dpVal)
//        }else{
//
//            p0.mPageCard.layoutParams = RelativeLayout.LayoutParams(dpVal,dpVal)
//        }
        p0.mUnitName.text = block;
        //p0.itemView.requestLayout();
//        if(arrayList[p1].isSelected){
//            activePage = p1;
//            p0.mPageCard.setCardBackgroundColor(mcontext.resources.getColor(R.color.orange))
//            p0.mPageNumber.setTextColor(mcontext.resources.getColor(R.color.white))
//        }else{
//            p0.mPageCard.setCardBackgroundColor(mcontext.resources.getColor(R.color.white))
//            p0.mPageNumber.setTextColor(mcontext.resources.getColor(R.color.black))
//        }
//
        p0.mChipClose.setOnClickListener(View.OnClickListener {
            clickListener(arrayList[p1],p1)
        })
//        p0.itemView.setOnClickListener(View.OnClickListener {
//            clickListener(arrayList[p1],p1)
//        })
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ItemViewHolder {
        return SelectedUnitsAdapter.ItemViewHolder(
            LayoutInflater.from(p0.context).inflate(R.layout.unit_chip_view, p0, false)
        )
    }

    override fun getItemCount(): Int {
       return arrayList.size;
    }


    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mUnitName: AppCompatTextView
        val mChipClose: ImageView


        init {
            mUnitName = itemView.findViewById(R.id.selected_unit_name)
            mChipClose = itemView.findViewById(R.id.selected_unit_close)
        }
    }

}