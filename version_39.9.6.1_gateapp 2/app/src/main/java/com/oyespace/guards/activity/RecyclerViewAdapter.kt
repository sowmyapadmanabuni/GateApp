package com.oyespace.guards.com.oyespace.guards.activity

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.oyespace.guards.R

/**
 * Created by farooq on 9/12/2017.
 */
class RecyclerViewAdapter(val list:ArrayList<MyData>):RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.activity_recycle_sos_items, parent, false)
        return ViewHolder(v)
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: RecyclerViewAdapter.ViewHolder, position: Int) {
        holder.bindItems(list[position])
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bindItems(data : MyData){
            val _textView:TextView = itemView.findViewById(R.id.textview)
            val _imageView:ImageView = itemView.findViewById(R.id.imageview)
            val _imageView2:ImageView = itemView.findViewById(R.id.img2)
            val _textView2:TextView=itemView.findViewById(R.id.t2)
            _imageView.setImageBitmap(data.image)
            _textView.text = data.text
             _imageView2.setImageBitmap(data.image2)
            _textView2.text=data.phoneNum


    }
    }
}
