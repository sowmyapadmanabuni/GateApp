/*
package com.oyespace.guards.snippet

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.goohungrry.ecode.R
import com.goohungrry.ecode.pojo.OrderData
import com.goohungrry.ecode.utils.AppUtils
import com.goohungrry.ecode.utils.DateTimeUtils

*/
/**
 * Created by linuxy on 6/2/17.
 *//*


class OrderListAdapter(private val listOrders: ArrayList<OrderData>?, private val mcontext: Context) : RecyclerView.Adapter<OrderListAdapter.MenuHolder>() {

    private val mInflater: LayoutInflater


    init {
        mInflater = LayoutInflater.from(mcontext)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuHolder {
        val mainGroup = mInflater.inflate(R.layout.order_list_item, parent, false) as ViewGroup
        return MenuHolder(mainGroup)
    }

    override fun onBindViewHolder(holder: MenuHolder, position: Int) {
        val orderData = listOrders?.get(position)
        holder.hotelName.text = orderData?.rName
        holder.pricetxt.text = "Total " + AppUtils.getPrice(orderData?.finalpayedbill)
        val menuItems = orderData?.menuItems
        val list = ArrayList<String>()
        menuItems?.forEach { list.add(it?.menuQuantity + " " + it?.menuName) }
        holder.itemsTxt.text = TextUtils.join(",", list)
        holder.orderTime.text = "Ordered at " + DateTimeUtils.formatDate(orderData?.ordertimedate)
    }

    override fun getItemCount(): Int {
        return listOrders?.size ?: 0
    }


    inner class MenuHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val hotelName: TextView
        val orderTime: TextView
        val itemsTxt: TextView
        val pricetxt: TextView

        init {
            hotelName = view.findViewById(R.id.hotelName)
            orderTime = view.findViewById(R.id.orderTime)
            itemsTxt = view.findViewById(R.id.itemsTxt)
            pricetxt = view.findViewById(R.id.pricetxt)
        }


    }
}
*/
