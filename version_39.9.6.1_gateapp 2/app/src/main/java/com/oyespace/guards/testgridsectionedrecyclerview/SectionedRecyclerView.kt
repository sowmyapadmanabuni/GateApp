package com.oyespace.guards.testgridsectionedrecyclerview

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import com.oyespace.guards.R


class SectionedRecyclerView : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sectioned_recycler_view)


    }

    fun gridSectionedRecyclerView(view: View) {
        startRecyclerViewActivity(RecyclerViewType.GRID);
    }

    private fun startRecyclerViewActivity(recyclerViewType: RecyclerViewType) {
        val bundle = Bundle()
        bundle.putSerializable(RecyclerViewActivity.RECYCLER_VIEW_TYPE, recyclerViewType)
        startActivity(Intent(this, RecyclerViewActivity::class.java).putExtras(bundle))
    }
}
