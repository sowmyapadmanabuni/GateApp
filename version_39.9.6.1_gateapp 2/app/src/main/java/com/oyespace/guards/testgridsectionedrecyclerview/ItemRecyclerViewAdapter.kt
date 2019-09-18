package com.oyespace.guards.testgridsectionedrecyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.oyespace.guards.R


class ItemRecyclerViewAdapter(private val context: Context, private val arrayList: ArrayList<String>) :
    androidx.recyclerview.widget.RecyclerView.Adapter<ItemRecyclerViewAdapter.ItemViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_custom_row_layout,
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.itemLabel.text = arrayList[position]
        holder.lv_itemrecyclerview.setOnClickListener {
            Toast.makeText(context, "Coming", Toast.LENGTH_LONG).show()

        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    class ItemViewHolder(itemView: View) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val itemLabel: TextView
        val lv_itemrecyclerview: LinearLayout

        init {
            itemLabel = itemView.findViewById(R.id.item_label)
            lv_itemrecyclerview = itemView.findViewById(R.id.lv_itemrecyclerview)
        }
    }

}
