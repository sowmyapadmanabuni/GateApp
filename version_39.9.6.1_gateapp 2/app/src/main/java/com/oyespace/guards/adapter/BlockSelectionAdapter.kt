package com.oyespace.guards.adapter

import android.content.Context
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.malinskiy.superrecyclerview.swipe.SwipeLayout
import com.oyespace.guards.R

import com.oyespace.guards.pojo.BlocksData


class BlockSelectionAdapter(private val mcontext: Context, private val arrayList: ArrayList<BlocksData>, val clickListener:(BlocksData, Int) -> Unit):
    androidx.recyclerview.widget.RecyclerView.Adapter<BlockSelectionAdapter.ItemViewHolder>(){


    override fun onBindViewHolder(p0: ItemViewHolder, p1: Int) {
        //p0.itemView.requestLayout();
        var density = mcontext.getResources().getDisplayMetrics().density;
        var dp = 40;
        var dpVal = (density*dp).toInt()
        var margin = (8*dp).toInt()

        var block = arrayList[p1].blBlkName
        if(block.length > 3){
            p0.mPageCard.layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,dpVal)
        }else{

            p0.mPageCard.layoutParams = RelativeLayout.LayoutParams(dpVal,dpVal)
        }
        p0.mPageNumber.text = block;
        //p0.itemView.requestLayout();
        if(!arrayList[p1].isSelected){
            p0.mPageCard.setCardBackgroundColor(mcontext.resources.getColor(R.color.orange))
            p0.mPageNumber.setTextColor(mcontext.resources.getColor(R.color.white))
        }else{
            p0.mPageCard.setCardBackgroundColor(mcontext.resources.getColor(R.color.dark_grey))
            p0.mPageNumber.setTextColor(mcontext.resources.getColor(R.color.white))
        }

        p0.itemView.setOnClickListener(View.OnClickListener {
            clickListener(arrayList[p1],p1)
        })
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ItemViewHolder {
        return BlockSelectionAdapter.ItemViewHolder(
            LayoutInflater.from(p0.context).inflate(R.layout.block_selection_view, p0, false)
        )
    }

    override fun getItemCount(): Int {
       return arrayList.size;
    }


    class ItemViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val mPageNumber: AppCompatTextView
        val mPageCard: androidx.cardview.widget.CardView


        init {
            mPageNumber = itemView.findViewById(R.id.block_name_sel)
            mPageCard = itemView.findViewById(R.id.block_sel_shape)
        }
    }

}