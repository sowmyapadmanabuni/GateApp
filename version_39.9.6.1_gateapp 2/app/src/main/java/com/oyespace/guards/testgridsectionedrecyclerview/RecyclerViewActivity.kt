package com.oyespace.guards.testgridsectionedrecyclerview

import android.os.Bundle
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.MenuItem
import com.oyespace.guards.R
import com.oyespace.guards.pojo.SPData


class RecyclerViewActivity : AppCompatActivity() {
    var recyclerViewType: RecyclerViewType? = null
    var recyclerView: androidx.recyclerview.widget.RecyclerView? = null

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recycler_view_activity)

        //get enum type passed from MainActivity
        recyclerViewType = intent.getSerializableExtra(RECYCLER_VIEW_TYPE) as RecyclerViewType
        setUpToolbarTitle()
        setUpRecyclerView()
        populateRecyclerView()
    }

    //set toolbar title and set back button
    private fun setUpToolbarTitle() {
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        when (recyclerViewType) {
            //  LINEAR_HORIZONTAL -> supportActionBar!!.setTitle(resources.getString(R.string.linear_sectioned_recyclerview_horizontal))
            // LINEAR_VERTICAL -> supportActionBar!!.setTitle(resources.getString(R.string.linear_sectioned_recyclerview_vertical))
            RecyclerViewType.GRID -> supportActionBar!!.setTitle(resources.getString(R.string.grid_sectioned_recyclerview))
        }
    }

    //setup recycler view
    private fun setUpRecyclerView() {
        recyclerView = findViewById(R.id.sectioned_recycler_view) as androidx.recyclerview.widget.RecyclerView
        recyclerView!!.setHasFixedSize(true)
        val linearLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        recyclerView!!.layoutManager = linearLayoutManager
    }

    //populate recycler view
    private fun populateRecyclerView() {
        val sectionModelArrayList: ArrayList<SectionModel> = ArrayList()


        //for loop for sections
        for (i in 1..5) {
            val itemArrayList: ArrayList<String> = ArrayList()
            //for loop for items
            for (j in 1..10) {
                itemArrayList.add("Item $j")
            }

            //add the section and items to array list
            sectionModelArrayList.add(SectionModel("Section $i", itemArrayList))
        }

        val adapter = SectionRecyclerViewAdapter(this, recyclerViewType, sectionModelArrayList)
        recyclerView!!.adapter = adapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {

        val RECYCLER_VIEW_TYPE = "recycler_view_type"
    }
}

