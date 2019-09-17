package com.oyespace.guards.adapter

import android.content.Context
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.oyespace.guards.R
import com.oyespace.guards.pojo.UnitPojo


class UnitSearchResultAdapter(private val mcontext: Context, private val arrayList: ArrayList<UnitPojo>, val clickListener:(UnitPojo, Int) -> Unit):
    androidx.recyclerview.widget.RecyclerView.Adapter<UnitSearchResultAdapter.ItemViewHolder>(){


    override fun onBindViewHolder(p0: ItemViewHolder, p1: Int) {
        p0.itemView.requestLayout();
        p0.mPageNumber.text = arrayList[p1].unUniName;

        p0.itemView.setOnClickListener(View.OnClickListener {
            //arrayList[activePage].isActive = false;
            //arrayList[p1].isActive = true;
            //this.notifyDataSetChanged()
            clickListener(arrayList[p1],p1)
        })
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ItemViewHolder {
        return UnitSearchResultAdapter.ItemViewHolder(
            LayoutInflater.from(p0.context).inflate(R.layout.search_result_view, p0, false)
        )
    }

    override fun getItemCount(): Int {
       return arrayList.size;
    }


    class ItemViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val mPageNumber: AppCompatTextView


        init {
            mPageNumber = itemView.findViewById(R.id.search_result_text)
        }
    }

}