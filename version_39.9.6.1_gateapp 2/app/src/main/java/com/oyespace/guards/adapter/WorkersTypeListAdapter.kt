package com.oyespace.guards.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.oyespace.guards.R
import com.oyespace.guards.activity.BlockSelectionActivity
import com.oyespace.guards.staffManaualEntry.ManulBlockSelectionActivity
import com.oyespace.guards.utils.ConstantUtils.*
import com.oyespace.guards.utils.Prefs


class WorkersTypeListAdapter(val items: ArrayList<String>, val mcontext: Context, val flowType: String) :
    androidx.recyclerview.widget.RecyclerView.Adapter<WorkersTypeListAdapter.WorkerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): WorkerViewHolder {
        return WorkerViewHolder(LayoutInflater.from(mcontext).inflate(R.layout.layout_worker_adapter_row, parent, false))
    }

    override fun onBindViewHolder(holder: WorkerViewHolder, position: Int) {

        holder.tv_worktype.text=items[position]

        if (Prefs.getString(TYPE, "").equals("Create")) {
            holder.lv_staff.setOnClickListener {
                val intent = Intent(mcontext, BlockSelectionActivity::class.java)
                intent.putExtra(FLOW_TYPE, flowType)
                intent.putExtra(VISITOR_TYPE, "STAFF")
                intent.putExtra(COMPANY_NAME, items[position])
                mcontext.startActivity(intent)
                (mcontext as Activity).finish()
            }
        } else {


            holder.lv_staff.setOnClickListener {
                val d = Intent(mcontext, ManulBlockSelectionActivity::class.java)
                d.putExtra("UNITID", (mcontext as Activity).intent.getIntExtra("UNITID", 0))
                d.putExtra("FIRSTNAME", mcontext.intent.getStringExtra("FIRSTNAME"))
                d.putExtra("LASTNAME", mcontext.intent.getStringExtra("LASTNAME"))
                d.putExtra(MOBILENUMBER, mcontext.intent.getStringExtra(MOBILENUMBER))
                d.putExtra(
                    "DESIGNATION",
                    mcontext.intent.getStringExtra("DESIGNATION")
                )
                d.putExtra("WORKTYPE", mcontext.intent.getStringExtra("WORKTYPE"))
                d.putExtra(WORKER_ID, mcontext.intent.getIntExtra(WORKER_ID, 0))
                d.putExtra("UNITNAME", mcontext.intent.getStringExtra("UNITNAME"))
                d.putExtra("BIRTHDAY", mcontext.intent.getStringExtra("BIRTHDAY"))
                d.putExtra(FLOW_TYPE, flowType)
                d.putExtra(VISITOR_TYPE, "STAFF")
                d.putExtra(COMPANY_NAME, items[position])
                d.putExtras(mcontext.intent)
                mcontext.startActivity(d)
                mcontext.finish()
            }
        }




    }

    override fun getItemCount(): Int {
        return items.size
    }

    class WorkerViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        val lv_staff : LinearLayout
        val tv_worktype : TextView


        init {
            lv_staff=view.findViewById(R.id.lv_staff)
            tv_worktype=view.findViewById(R.id.tv_worktype)

        }

    }
}

