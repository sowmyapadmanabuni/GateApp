package com.oyespace.guards.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.oyespace.guards.R
import com.oyespace.guards.activity.*
import com.oyespace.guards.constants.PrefKeys
import com.oyespace.guards.pojo.VendorType
import com.oyespace.guards.utils.ConstantUtils.*
import com.oyespace.guards.utils.Prefs

class ChangeCompanyItemRVAdapter(private val mcontext: Context, private val arrayList: ArrayList<VendorType>, private val unitId:String, private val unitNames:String, private val accountId: Int, private val mobileNumber:String, private val countryCode:String, private val flowType:String, private val visitorType:String, private val unitAccountId:String, private val personName:String, private val image:String, private val photoList: java.util.ArrayList<String>) :
    RecyclerView.Adapter<ChangeCompanyItemRVAdapter.ItemViewHolder>() {

    var vendorName:String?=null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_custom_row_layout, parent, false)
        )

    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

         if (Prefs.getString(PrefKeys.LANGUAGE, null).equals("en")) {
             Toast.makeText(mcontext,arrayList[position].vTypeEng,Toast.LENGTH_LONG).show()
             holder.itemLabel.text = arrayList[position].vTypeEng
         }else{
             holder.itemLabel.text = arrayList[position].vTypeHin
         }

        val imageBytes = Base64.decode(arrayList[position].vtImg, Base64.DEFAULT)
        val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        holder.img_logo!!.setImageBitmap(decodedImage)

        holder.lv_itemrecyclerview.setOnClickListener {

            if (Prefs.getString(PrefKeys.LANGUAGE, null).equals("en")) {
                vendorName=arrayList[position].vTypeEng
            }else{
                vendorName=arrayList[position].vTypeHin
            }

            if(vendorName.equals("Others")){

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

            }else if(vendorName.equals("अन्य")){

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
                val intent = Intent(mcontext, StaffEntryRegistration::class.java)
                intent.putExtra(UNITID, unitId)
                intent.putExtra(UNITNAME, unitNames)
                intent.putExtra(ACCOUNT_ID,accountId)
                intent.putExtra(MOBILENUMBER, mobileNumber)
                intent.putExtra(COUNTRYCODE, countryCode)
                intent.putExtra(FLOW_TYPE, flowType)
                intent.putExtra(VISITOR_TYPE, visitorType)
                intent.putExtra(UNIT_ACCOUNT_ID,unitAccountId)
                intent.putExtra(PERSONNAME, personName)
                intent.putExtra("Base64",image)
                intent.putExtra(ITEMS_PHOTO_LIST,photoList)
                intent.putExtra(COMPANY_NAME, vendorName)
                mcontext.startActivity(intent)
                (mcontext as Activity).finish()
            }




        }
//        if(arrayList[position].image_url>0) {
//            Picasso.with(mcontext)
//                .load(arrayList[position].image_url)
//                .placeholder(R.drawable.placeholder_dark).error(R.drawable.placeholder_dark).into(holder.img_logo)
//        }

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
