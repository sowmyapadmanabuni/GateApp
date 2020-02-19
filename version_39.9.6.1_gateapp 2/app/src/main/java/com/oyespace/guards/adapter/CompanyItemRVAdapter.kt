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
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.oyespace.guards.R
import com.oyespace.guards.activity.BlockSelectionActivity
import com.oyespace.guards.activity.NameEntryScreen
import com.oyespace.guards.activity.PurposeScreen
import com.oyespace.guards.pojo.VendorPojo
import com.oyespace.guards.utils.ConstantUtils.*
import com.squareup.picasso.Picasso

class CompanyItemRVAdapter(private val mcontext: Context, private val arrayList: ArrayList<VendorPojo>, private val unitId:String, private val unitNames:String, private val flowType:String, private val visitorType:String, private val mobileNumber:String, private val countryCode:String,private val unitAccountId:String) :
    RecyclerView.Adapter<CompanyItemRVAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_custom_row_layout, parent, false)
        )

    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        holder.itemLabel.text = arrayList[position].vendor_names

        holder.lv_itemrecyclerview.setOnClickListener {


            if(arrayList[position].vendor_names.equals("Others")){

                val intent = Intent(mcontext, PurposeScreen::class.java)
                intent.putExtra(UNITID, unitId)
                intent.putExtra(UNITNAME, unitNames)
                intent.putExtra(FLOW_TYPE, flowType)
                intent.putExtra(VISITOR_TYPE, visitorType)
                intent.putExtra(MOBILENUMBER, mobileNumber)
                intent.putExtra(COUNTRYCODE, countryCode)
                intent.putExtra(COMPANY_NAME, OTHERS)
                intent.putExtra(UNIT_ACCOUNT_ID,unitAccountId)
                mcontext.startActivity(intent)
                (mcontext as Activity).finish()

            }else if(arrayList[position].vendor_names.equals("अन्य")){

                val intent = Intent(mcontext, PurposeScreen::class.java)
                intent.putExtra(UNITID, unitId)
                intent.putExtra(UNITNAME, unitNames)
                intent.putExtra(FLOW_TYPE, flowType)
                intent.putExtra(VISITOR_TYPE, visitorType)
                intent.putExtra(MOBILENUMBER, mobileNumber)
                intent.putExtra(COUNTRYCODE, countryCode)
                intent.putExtra(COMPANY_NAME, OTHERS)
                intent.putExtra(UNIT_ACCOUNT_ID,unitAccountId)
                mcontext.startActivity(intent)
                (mcontext as Activity).finish()

            }else{
                val intent = Intent(mcontext, NameEntryScreen::class.java)
                intent.putExtra(UNITID, unitId)
                intent.putExtra(UNITNAME, unitNames)
                intent.putExtra(FLOW_TYPE, flowType)
                intent.putExtra(VISITOR_TYPE, visitorType)
                intent.putExtra(MOBILENUMBER, mobileNumber)
                intent.putExtra(COUNTRYCODE, countryCode)
                intent.putExtra(COMPANY_NAME, arrayList[position].vendor_names)
                intent.putExtra(UNIT_ACCOUNT_ID,unitAccountId)
                mcontext.startActivity(intent)
                (mcontext as Activity).finish()
            }

//            val intent = Intent(mcontext, BlockSelectionActivity::class.java)
//            intent.putExtra(FLOW_TYPE, DELIVERY)
//            intent.putExtra(VISITOR_TYPE, DELIVERY)
//            if(arrayList[position].vendor_names.equals("Others")){
//                intent.putExtra(COMPANY_NAME, OTHERS)
//            }
//            else if(arrayList[position].vendor_names.equals("अन्य")){
//                intent.putExtra(COMPANY_NAME, OTHERS)
//            }
//            else{
//               val data= arrayList[position].vendor_names
//                intent.putExtra(COMPANY_NAME, data)
//
//            }
//            mcontext.startActivity(intent)
//            (mcontext as Activity).finish()

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

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
