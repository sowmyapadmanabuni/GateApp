package com.oyespace.guards.vehicle_others

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.oyespace.guards.R
import com.oyespace.guards.utils.ConstantUtils.*
import android.app.Activity
import android.widget.ImageView
import com.oyespace.guards.pojo.VendorPojo
import com.oyespace.guards.vehicle_guest.Vehicle_Guest_BlockSelectionActivity
import com.squareup.picasso.Picasso

class VehicleOthersCompanyItemRVAdapter(private val mcontext: Context, private val arrayList: ArrayList<VendorPojo>) :
    androidx.recyclerview.widget.RecyclerView.Adapter<VehicleOthersCompanyItemRVAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_custom_row_layout,
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        val mcontextintent = (mcontext as Activity).intent


        holder.itemLabel.text = arrayList[position].vendor_names
        holder.lv_itemrecyclerview.setOnClickListener {

            val intent = Intent(mcontext, Vehicle_Others_BlockSelectionActivity::class.java)
            intent.putExtra(FLOW_TYPE, VEHICLE_OTHERS)
            intent.putExtra(VISITOR_TYPE, DELIVERY)
            intent.putExtra(VEHICLE_NUMBER,mcontextintent.getStringExtra(VEHICLE_NUMBER) )
            if(arrayList[position].equals("Others")){
                intent.putExtra(COMPANY_NAME, OTHERS)
            }else{
                intent.putExtra(COMPANY_NAME, arrayList[position].vendor_names)

            }

            //intent.putExtra(COMPANY_NAME, arrayList[position])


            mcontext.startActivity(intent)
            mcontext.finish()

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

    class ItemViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val itemLabel: TextView
        val lv_itemrecyclerview: LinearLayout
        val img_logo: ImageView

        init {
            itemLabel = itemView.findViewById(R.id.item_label)
            lv_itemrecyclerview = itemView.findViewById(R.id.lv_itemrecyclerview)
            img_logo=itemView.findViewById(R.id.img_logo)

        }
    }

}
