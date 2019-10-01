package com.oyespace.guards.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.getIntent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.oyespace.guards.R
import com.oyespace.guards.activity.BlockSelectionActivity
import com.oyespace.guards.camtest.ViewFullImageActivity
import com.oyespace.guards.camtest.ViewPagerAdapter
import com.oyespace.guards.pojo.VendorPojo
import com.oyespace.guards.utils.ConstantUtils
import com.oyespace.guards.utils.ConstantUtils.IMAGE_BASE_URL
import com.squareup.picasso.Picasso

class HorizontalImagesAdapter(private val mcontext: Context,  val arrayList: Array<String>) :
    androidx.recyclerview.widget.RecyclerView.Adapter<HorizontalImagesAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_horizontal_images, parent, false)
        )

    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
//
//        holder.itemLabel.text = arrayList[position].vendor_names
//
//        holder.lv_itemrecyclerview.setOnClickListener {
//
//            val intent = Intent(mcontext, BlockSelectionActivity::class.java)
//            intent.putExtra(ConstantUtils.FLOW_TYPE, ConstantUtils.DELIVERY)
//            intent.putExtra(ConstantUtils.VISITOR_TYPE, ConstantUtils.DELIVERY)
//            if(arrayList[position].vendor_names.equals("Others")){
//                intent.putExtra(ConstantUtils.COMPANY_NAME, ConstantUtils.OTHERS)
//            }else{
//                intent.putExtra(ConstantUtils.COMPANY_NAME, arrayList[position].vendor_names)
//
//            }
//            mcontext.startActivity(intent)
//            (mcontext as Activity).finish()
//
//            Log.v("PROJECT", arrayList[position].vendor_names)
//
//
//        }
//        if(arrayList[position].image_url>0) {
            Picasso.with(mcontext)
                .load("http://mediaupload.oyespace.com/"+arrayList[position])
                .placeholder(R.drawable.placeholder_dark).error(R.drawable.placeholder_dark).into(holder.image)
//holder.image.setOnClickListener {
//
//
//    val alertadd = AlertDialog.Builder(mcontext)
//    val factory = LayoutInflater.from(mcontext)
//    val view = factory.inflate(R.layout.activity_view_full_image, null)
//
//        val viewPager =view. findViewById(R.id.viewpager) as ViewPager
//        viewPager.adapter = IamgeViewPagerAdapter(mcontext,arrayList)
//        viewPager.currentItem = position
//
//
//    alertadd.setView(view)
//    alertadd.show()
//
////    val pos = holder.layoutPosition
////    val intent = Intent(mcontext, ViewFullImageActivity::class.java)
////    intent.putExtra("pos", pos)
////    mcontext.startActivity(intent)
//}

    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    class ItemViewHolder(itemView: View) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        val image: ImageView

        init {

            image=itemView.findViewById(R.id.image)
        }
    }

}
