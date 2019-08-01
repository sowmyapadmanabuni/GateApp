package com.oyespace.guards.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.oyespace.guards.R
import com.oyespace.guards.activity.BlockSelectionActivity
import com.oyespace.guards.activity.UnitListActivity
import com.oyespace.guards.utils.ConstantUtils.*

class WorkersTypeListAdapter (val items : ArrayList<String>, val mcontext: Context) : RecyclerView.Adapter<WorkersTypeListAdapter.WorkerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): WorkerViewHolder {
        return WorkerViewHolder(LayoutInflater.from(mcontext).inflate(R.layout.layout_worker_adapter_row, parent, false))
    }

    override fun onBindViewHolder(holder: WorkerViewHolder, position: Int) {

        holder.tv_worktype.text=items[position]
        holder.lv_staff.setOnClickListener {
            val intent = Intent(mcontext, BlockSelectionActivity::class.java)
            intent.putExtra(FLOW_TYPE, STAFF_REGISTRATION)
            intent.putExtra(VISITOR_TYPE, "STAFF")
            intent.putExtra(COMPANY_NAME, items[position])
            mcontext.startActivity(intent)
            (mcontext as Activity).finish()
        }


    }

    override fun getItemCount(): Int {
        return items.size
    }

    class WorkerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val lv_staff : LinearLayout
        val tv_worktype : TextView


        init {
            lv_staff=view.findViewById(R.id.lv_staff)
            tv_worktype=view.findViewById(R.id.tv_worktype)

        }

    }
}

