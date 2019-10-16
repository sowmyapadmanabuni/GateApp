package com.oyespace.guards.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.oyespace.guards.R
import com.oyespace.guards.testgridsectionedrecyclerview.ItemRecyclerViewAdapter
import com.oyespace.guards.testgridsectionedrecyclerview.SectionModel

class ServiceProviderAdapter  (private val context: Context, private val itemArrayList: ArrayList<SectionModel>
) : RecyclerView.Adapter<ServiceProviderAdapter.ServiceProviderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceProviderViewHolder {

        return ServiceProviderViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_serviceprovider_adapter_row,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ServiceProviderViewHolder, position: Int) {
        val sectionModel = itemArrayList[position]
//        holder.section_label.text = sectionModel.sectionLabel

        holder.itemRecyclerView.setHasFixedSize(true)
        holder.itemRecyclerView.isNestedScrollingEnabled = false


        val gridLayoutManager = GridLayoutManager(context, 3)
                holder.itemRecyclerView.layoutManager = gridLayoutManager


        val adapter = ItemRecyclerViewAdapter(context, sectionModel.itemArrayList)
        holder.itemRecyclerView.adapter = adapter

    }

    override fun getItemCount(): Int {
        return itemArrayList.size
    }

    class ServiceProviderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val itemRecyclerView: RecyclerView
        val section_label: TextView

        init {
            section_label = itemView.findViewById(R.id.section_label)

            itemRecyclerView = itemView.findViewById(R.id.item_recycler_view)
        }
    }
}