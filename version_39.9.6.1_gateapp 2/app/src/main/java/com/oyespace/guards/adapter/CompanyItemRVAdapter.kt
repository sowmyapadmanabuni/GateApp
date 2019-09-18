package com.oyespace.guards.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.oyespace.guards.R
import com.oyespace.guards.activity.BlockSelectionActivity
import com.oyespace.guards.pojo.VendorPojo
import com.oyespace.guards.utils.ConstantUtils.*
import com.squareup.picasso.Picasso

class CompanyItemRVAdapter(private val mcontext: Context, private val arrayList: ArrayList<VendorPojo>) :
    androidx.recyclerview.widget.RecyclerView.Adapter<CompanyItemRVAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_custom_row_layout, parent, false)
        )

    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        holder.itemLabel.text = arrayList[position].vendor_names

        holder.lv_itemrecyclerview.setOnClickListener {

            val intent = Intent(mcontext, BlockSelectionActivity::class.java)
            intent.putExtra(FLOW_TYPE, DELIVERY)
            intent.putExtra(VISITOR_TYPE, DELIVERY)
            if(arrayList[position].vendor_names.equals("Others")){
                intent.putExtra(COMPANY_NAME, OTHERS)
            }else{
                intent.putExtra(COMPANY_NAME, arrayList[position].vendor_names)

            }
            mcontext.startActivity(intent)
            (mcontext as Activity).finish()

            Log.v("PROJECT", arrayList[position].vendor_names)


        }
        if(arrayList[position].image_url>0) {
            Picasso.with(mcontext)
                .load(arrayList[position].image_url)
                .placeholder(R.drawable.placeholder_dark).error(R.drawable.placeholder_dark).into(holder.img_logo)
        }

    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    class ItemViewHolder(itemView: View) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val itemLabel: TextView
        val lv_itemrecyclerview: LinearLayout
        val img_logo:ImageView

        init {
            itemLabel = itemView.findViewById(R.id.item_label)
            lv_itemrecyclerview = itemView.findViewById(R.id.lv_itemrecyclerview)
            img_logo=itemView.findViewById(R.id.img_logo)
        }
    }

}
