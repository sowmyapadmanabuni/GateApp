package com.oyespace.guards.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.oyespace.guards.R
import com.squareup.picasso.Picasso

class HorizontalImagesAdapter(private val mcontext: Context, val arrayList: Array<String>) :
    RecyclerView.Adapter<HorizontalImagesAdapter.ItemViewHolder>() {

    var imagePopup: AlertDialog? = null
    var imageList: RecyclerView? = null

    init {

        val view = LayoutInflater.from(mcontext).inflate(R.layout.imageviewer_layout, null, false)
        imagePopup = AlertDialog.Builder(mcontext)
            .setView(view)
            .create()

        imageList = view.findViewById(R.id.imageviewer_list)
        imageList?.adapter = PopupImagesAdapter(arrayList)
        PagerSnapHelper().attachToRecyclerView(imageList)
    }

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

//        Picasso.with(mcontext)
//            .load("http://mediaupload.oyespace.com/" + arrayList[position])
//            .placeholder(R.drawable.placeholder_dark).error(R.drawable.placeholder_dark).into(holder.image)

        val imageBytes = Base64.decode(arrayList[position], Base64.DEFAULT)
        val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        holder.image!!.setImageBitmap(decodedImage)

        holder.image.setOnClickListener {
            imageList?.scrollToPosition(position)
            imagePopup?.show()
        }

    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    class ItemViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val image: ImageView

        init {
            image = itemView.findViewById(R.id.image)
        }
    }

    inner class PopupImagesAdapter(val images: Array<String>) : RecyclerView.Adapter<PopupImageVH>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopupImageVH {
            return PopupImageVH(LayoutInflater.from(mcontext).inflate(R.layout.popup_image, parent, false))
        }

        override fun getItemCount(): Int = images.size

        override fun onBindViewHolder(holder: PopupImageVH, position: Int) {

            holder.setImage(images.get(position))

        }

    }

    inner class PopupImageVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun setImage(img: String) {
            val imgv = itemView.findViewById<ImageView>(R.id.image)
//            Picasso.with(mcontext)
//                .load("http://mediaupload.oyespace.com/" + img)
//                .placeholder(R.drawable.placeholder_dark).error(R.drawable.placeholder_dark).into(imgv)

            val imageBytes = Base64.decode(img, Base64.DEFAULT)
            val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            imgv!!.setImageBitmap(decodedImage)
        }

    }


}
