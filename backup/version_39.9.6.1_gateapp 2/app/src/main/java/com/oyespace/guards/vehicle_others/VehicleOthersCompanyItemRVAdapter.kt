package com.oyespace.guards.vehicle_others

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.oyespace.guards.R
import com.oyespace.guards.utils.ConstantUtils.*
import android.app.Activity

class VehicleOthersCompanyItemRVAdapter(private val mcontext: Context, private val arrayList: ArrayList<String>) :
    RecyclerView.Adapter<VehicleOthersCompanyItemRVAdapter.ItemViewHolder>() {

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

        holder.itemLabel.text = arrayList[position]
        holder.lv_itemrecyclerview.setOnClickListener {

            val intent = Intent(mcontext, VehicleOthersUnitScreen::class.java)
            intent.putExtra(FLOW_TYPE, VEHICLE_OTHERS)
            intent.putExtra(VISITOR_TYPE, DELIVERY)
            intent.putExtra(VEHICLE_NUMBER,mcontextintent.getStringExtra(VEHICLE_NUMBER) )
            if(arrayList[position].equals("Others")){
                intent.putExtra(COMPANY_NAME, OTHERS)
            }else{
                intent.putExtra(COMPANY_NAME, arrayList[position])

            }

            //intent.putExtra(COMPANY_NAME, arrayList[position])


            mcontext.startActivity(intent)
            mcontext.finish()

        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemLabel: TextView
        val lv_itemrecyclerview: LinearLayout

        init {
            itemLabel = itemView.findViewById(R.id.item_label)
            lv_itemrecyclerview = itemView.findViewById(R.id.lv_itemrecyclerview)

        }
    }

}
