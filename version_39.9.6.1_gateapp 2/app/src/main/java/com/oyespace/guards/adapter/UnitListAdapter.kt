package com.oyespace.guards.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.oyespace.guards.R
import com.oyespace.guards.activity.MobileNumberScreen
import com.oyespace.guards.pojo.UnitPojo
import com.oyespace.guards.utils.ConstantUtils.*


class UnitListAdapter(private val listVistor: ArrayList<UnitPojo>, private val mcontext: Context) :
    RecyclerView.Adapter<UnitListAdapter.MenuHolder>() {

    private val mInflater: LayoutInflater


    init {
        mInflater = LayoutInflater.from(mcontext)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuHolder {
        val mainGroup = mInflater.inflate(R.layout.layout_unit_adapter_row, parent, false) as ViewGroup
        return MenuHolder(mainGroup)
    }


    override fun onBindViewHolder(holder: MenuHolder, position: Int) {
        val orderData = listVistor.get(position)
        val vistordate = orderData.asAssnID
        holder.apartmentNamee.text = orderData.unUniName
//        holder.entryTime.text = orderData?.unCalType
//        holder.exitTime.text = orderData?.unOcSDate
//        holder.serviceProvider.text = orderData?.unOcSDate
//        holder.visitorName.text = orderData?.unUnitID
        
        holder.cb_unit.setOnCheckedChangeListener {buttonView, isChecked ->
           // Toast.makeText(this,isChecked.toString(),Toast.LENGTH_SHORT).show()
            listVistor.get(position).isSelected=isChecked

        }
        holder.iv_unit.setOnClickListener {
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:"+orderData.owner[0].uoisdCode+orderData.owner[0].uoMobile)
            mcontext.startActivity(intent)
        }
        holder.lv_itemrecyclerview.setOnClickListener({
            val mcontextintent = (mcontext as Activity).intent

            val intent = Intent(mcontext, MobileNumberScreen::class.java)
            intent.putExtra(FLOW_TYPE,mcontextintent.getStringExtra(FLOW_TYPE))
            intent.putExtra(VISITOR_TYPE,mcontextintent.getStringExtra(VISITOR_TYPE))
            intent.putExtra(COMPANY_NAME,mcontextintent.getStringExtra(COMPANY_NAME))
            intent.putExtra(UNITID, orderData.unUnitID)
            intent.putExtra(UNITNAME, orderData.unUniName)
            mcontext.startActivity(intent)
            mcontext.finish()

        })
    }

    override fun getItemCount(): Int {
        return listVistor.size
    }

    inner class MenuHolder(private val view: View) : RecyclerView.ViewHolder(view) {
//        val entryTime: TextView
//        val exitTime: TextView
        val iv_unit: ImageView
        val cb_unit: CheckBox
        val apartmentNamee: TextView
        val lv_itemrecyclerview: RelativeLayout

        init {
//            entryTime = view.findViewById(R.id.tv_unit)
//            exitTime = view.findViewById(R.id.tv_exittime)
            iv_unit = view.findViewById(R.id.iv_unit)
            cb_unit = view.findViewById(R.id.cb_unit)
            apartmentNamee = view.findViewById(R.id.tv_unit)
            lv_itemrecyclerview=view.findViewById(R.id.lv_itemrecyclerview)

        }

    }
}