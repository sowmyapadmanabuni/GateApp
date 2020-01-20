package com.oyespace.guards.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.oyespace.guards.R
import com.oyespace.guards.constants.PrefKeys
import com.oyespace.guards.network.CommonDisposable
import com.oyespace.guards.pojo.FamilyMember
import com.oyespace.guards.pojo.GetCallResponse
import com.oyespace.guards.utils.Prefs
import com.oyespace.guards.utils.TaptoCallApi
import com.oyespace.guards.zeotelapi.ZeotelRetrofitClinet
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class FamilMembersAdapter ( val listVistor: ArrayList<FamilyMember>,  val mcontext: Context) :
    RecyclerView.Adapter<FamilMembersAdapter.MenuHolder>() {

    private val mInflater: LayoutInflater


    init {
        mInflater = LayoutInflater.from(mcontext)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuHolder {
        val mainGroup = mInflater.inflate(R.layout.layout_familyphonenumber_row, parent, false) as ViewGroup
        return MenuHolder(mainGroup)
    }


    override fun onBindViewHolder(holder: MenuHolder, position: Int) {
        val orderData = listVistor.get(position)

       // if(orderData.fmMinor==false) {



            holder.tv_relation.text = orderData?.fmRltn

            holder.iv_unit.setOnClickListener {

                var agentNumber = "AGENTNUMBER=" + orderData.fmMobile.replace("+91", "")
                var gateMobileNumber = Prefs.getString(PrefKeys.MOBILE_NUMBER, "").replace("91", "")

                TaptoCallApi.taptocallApi(gateMobileNumber, agentNumber)

          //  }
        }

    }

    override fun getItemCount(): Int {
        return listVistor.size
    }

    inner class MenuHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        //        val entryTime: TextView
        val tv_relation: TextView
        val iv_unit: ImageView


        init {
            tv_relation = view.findViewById(R.id.tv_relation)
            iv_unit = view.findViewById(R.id.iv_unit)
        }

    }
}