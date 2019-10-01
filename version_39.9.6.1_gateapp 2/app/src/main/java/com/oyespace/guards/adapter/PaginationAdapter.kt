package com.oyespace.guards.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.oyespace.guards.R
import com.oyespace.guards.pojo.PaginationData

var activePage: Int = 0

class PaginationAdapter(private val mcontext: Context, private val arrayList: ArrayList<PaginationData>, val clickListener:(PaginationData, Int) -> Unit):
    RecyclerView.Adapter<PaginationAdapter.ItemViewHolder>() {


    override fun onBindViewHolder(p0: ItemViewHolder, p1: Int) {
        p0.itemView.requestLayout()
        p0.mPageNumber.text = arrayList[p1].pageNumber
        if(arrayList[p1].isActive){
            activePage = p1
            p0.mPageCard.setCardBackgroundColor(mcontext.resources.getColor(R.color.orange))
            p0.mPageNumber.setTextColor(mcontext.resources.getColor(R.color.white))
        }else{
            p0.mPageCard.setCardBackgroundColor(mcontext.resources.getColor(R.color.white))
            p0.mPageNumber.setTextColor(mcontext.resources.getColor(R.color.black))
        }

        p0.itemView.setOnClickListener(View.OnClickListener {
            //arrayList[activePage].isActive = false;
            //arrayList[p1].isActive = true;
            //this.notifyDataSetChanged()
            clickListener(arrayList[p1],p1)
        })
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ItemViewHolder {
        return PaginationAdapter.ItemViewHolder(
            LayoutInflater.from(p0.context).inflate(R.layout.page_number_view, p0, false)
        )
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }


    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mPageNumber: AppCompatTextView
        val mPageCard: CardView


        init {
            mPageNumber = itemView.findViewById(R.id.page_number_text)
            mPageCard = itemView.findViewById(R.id.page_number_shape)
        }
    }

}