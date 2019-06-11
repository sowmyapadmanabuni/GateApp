/*
package com.oyespace.guards.snippet

import android.content.Context
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.goohungrry.ecode.R
import com.goohungrry.ecode.activity.HotelMenuDetailsActivity
import com.goohungrry.ecode.pojo.BannerInfo
import com.goohungrry.ecode.pojo.HotelData
import com.goohungrry.ecode.utils.ConstantUtils
import com.goohungrry.ecode.utils.ImageLoader


*/
/**
 * Created by Kalyan on 22-Oct-17.
 *//*

class HotelListAdapter(val context: Context, var items: ArrayList<Any>?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    companion object {
        val BANNER_ITEM = 0
        val LIST_ITEM = 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        if (getItemViewType(position) == BANNER_ITEM) {
            bindPagerData(holder as PagerViewHolder, position)
        } else {
            bindhotelData(holder as HotelViewHolder, position)

        }
    }

    private fun bindPagerData(pagerViewHolder: PagerViewHolder, position: Int) {
        val item = items?.get(position) as ArrayList<BannerInfo>
        pagerViewHolder.pagerList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val hotelBannerAdapter = HotelBannerAdapter(context, item)
        pagerViewHolder.pagerList.adapter = hotelBannerAdapter
    }

    private fun bindhotelData(holder: HotelViewHolder, position: Int) {
        val item = items?.get(position) as HotelData
        holder.hotelname.text = item.name
        holder.hotelCuisineType.text = TextUtils.join(",", item.cuisine)
        ImageLoader.loadImage(item.image, holder.hotelImage, R.color.grey)
        holder.deliverytime.text = item.deliverytime
        holder.costOfTwo.text = "cost of two " + context.getString(R.string.Rupee) + item.costoftwo
        holder.ratings.text = item.ratings.toString()
        val discount = item.discount
        holder.discountTxt.visibility = View.GONE
        if (discount != null) {
            val discountDouble = discount.discount
            val disc: Int = (discountDouble?.times(100))?.toInt() ?: 0
            if (disc != 0) {
                holder.discountTxt.visibility = View.VISIBLE
                holder.discountTxt.text = "$disc% Off"
            }
        } else {
            holder.discountTxt.visibility = View.GONE
        }
        holder.rootView?.setOnClickListener({
            val intent = Intent(context, HotelMenuDetailsActivity::class.java)
            intent.putExtra(ConstantUtils.DATA, item)
            context.startActivity(intent)
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == BANNER_ITEM) {
            val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.hotel_pager_item, parent, false)
            return PagerViewHolder(v)
        } else {
            val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.hotel_list_item2, parent, false)
            return HotelViewHolder(v)

        }
    }

    override fun getItemViewType(position: Int): Int {
        val any = items?.get(position)
        if (any is HotelData) {
            return LIST_ITEM
        } else {
            return BANNER_ITEM
        }
    }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }

    class HotelViewHolder(val rootView: View?) : RecyclerView.ViewHolder(rootView) {


        var hotelImage: ImageView
        var hotelname: TextView
        var hotelCuisineType: TextView
        var deliverytime: TextView
        var costOfTwo: TextView
        var ratings: TextView
        var discountTxt: TextView

        init {
            hotelImage = rootView?.findViewById<ImageView>(R.id.hotelImage) as ImageView
            hotelname = rootView.findViewById<TextView>(R.id.hotelname) as TextView
            hotelCuisineType = rootView.findViewById<TextView>(R.id.hotelCuisineType) as TextView
            deliverytime = rootView.findViewById<TextView>(R.id.deliverytime) as TextView
            costOfTwo = rootView.findViewById<TextView>(R.id.costOfTwo) as TextView
            ratings = rootView.findViewById<TextView>(R.id.ratings) as TextView
            discountTxt = rootView.findViewById<TextView>(R.id.discountTxt) as TextView
        }
    }


    class PagerViewHolder(rootView: View?) : RecyclerView.ViewHolder(rootView) {
        var pagerList: RecyclerView

        init {
            pagerList = rootView?.findViewById<RecyclerView>(R.id.pagerList) as RecyclerView
        }
    }


    fun addAll(hotelDataItems: ArrayList<HotelData>?) {
        hotelDataItems.let {
            items?.addAll(hotelDataItems!!.asIterable())

        }
    }


}*/
