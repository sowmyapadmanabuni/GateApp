package com.oyespace.guards.com.oyespace.guards.activity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.oyespace.guards.R


class EmrgencyContactAdapter(
    val list: ArrayList<EmergencyModel>,
    val clickListener: (EmergencyModel, Int) -> Unit
) : androidx.recyclerview.widget.RecyclerView.Adapter<EmrgencyContactAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EmrgencyContactAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_recycle_sos_items, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: EmrgencyContactAdapter.ViewHolder, position: Int) {
        holder.bindItems(list[position])
        holder.itemView.setOnClickListener(View.OnClickListener {
            clickListener(list[position], position)
        })
    }


    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        fun bindItems(data: EmergencyModel) {
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
