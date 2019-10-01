package com.oyespace.guards.testgridsectionedrecyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.oyespace.guards.R


class SectionRecyclerViewAdapter(
    private val context: Context,
    private val recyclerViewType: RecyclerViewType?,
    private val sectionModelArrayList: ArrayList<SectionModel>
) : RecyclerView.Adapter<SectionRecyclerViewAdapter.SectionViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder {

        return SectionViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.section_custom_row_layout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        val sectionModel = sectionModelArrayList[position]
//        holder.section_label.text = sectionModel.sectionLabel

        holder.itemRecyclerView.setHasFixedSize(true)
        holder.itemRecyclerView.isNestedScrollingEnabled = false

        when (recyclerViewType) {

            RecyclerViewType.GRID -> {
                val gridLayoutManager = GridLayoutManager(context, 3)
                holder.itemRecyclerView.layoutManager = gridLayoutManager
            }
        }
        val adapter = ItemRecyclerViewAdapter(context, sectionModel.itemArrayList)
        holder.itemRecyclerView.adapter = adapter



    }

    override fun getItemCount(): Int {
        return sectionModelArrayList.size
    }

    class SectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val itemRecyclerView: RecyclerView
        val section_label: TextView

        init {
            section_label = itemView.findViewById(R.id.section_label)
            // showAllButton = itemView.findViewById(R.id.section_show_all_button)
            itemRecyclerView = itemView.findViewById(R.id.item_recycler_view)
        }
    }
}