package com.oyespace.guards.com.oyespace.guards.activity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.oyespace.guards.R


class RecyclerViewAdapter(val list: ArrayList<MyData>, val clickListener: (MyData, Int) -> Unit) :
    RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.activity_recycle_sos_items, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerViewAdapter.ViewHolder, position: Int) {
        holder.bindItems(list[position])
        holder.itemView.setOnClickListener(View.OnClickListener {
            clickListener(list[position],position)
        })
    }


    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindItems(data: MyData) {
            val _textView: TextView = itemView.findViewById(R.id.textview)
            val _imageView: ImageView = itemView.findViewById(R.id.imageview)
            val _imageView2: ImageView = itemView.findViewById(R.id.img2)
            val _textView2: TextView = itemView.findViewById(R.id.t2)
            _imageView.setImageBitmap(data.image)
            _textView.text = data.text
            _imageView2.setImageBitmap(data.image2)
            _textView2.text = data.phoneNum


        }
    }
}
