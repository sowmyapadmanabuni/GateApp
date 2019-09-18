package com.oyespace.guards.ui

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.annotation.Nullable


class RecyclerViewEmptyExtdener : androidx.recyclerview.widget.RecyclerView {
    private var mContext: Context? = null
    //The empty view which is shown when the data is empty
    private var emptyView: View? = null

    constructor(context: Context) : super(context) {
        mContext = context
    }

    constructor(context: Context, @Nullable attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        mContext = context
    }

    constructor(context: Context, @Nullable attrs: AttributeSet) : super(context, attrs) {
        mContext = context
    }

    override fun setAdapter(adapter: androidx.recyclerview.widget.RecyclerView.Adapter<*>?) {
        super.setAdapter(adapter)
        //Setting the adapter data change listener
        adapter!!.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onChanged() {
                Log.d("item count:", adapter.itemCount.toString() + "")
                //If data count is not 0 emptyView visibility is set to gone and recycler view is shown
                if (adapter.itemCount != 0) {
                    if (emptyView != null) {
                        emptyView!!.setVisibility(View.GONE)
                        visibility = View.VISIBLE
                    }
                } else {
                    //If data count is 0 emptyView visibility is set to visible and recycler view
                    // visibility is set to gone
                    if (emptyView != null) {
                        visibility = View.GONE
                        emptyView!!.setVisibility(View.VISIBLE)
                    }
                }
            }
        })
    }

    //Call this function to set the empty view
    fun setEmptyView(emptyView: View) {
        this.emptyView = emptyView
    }

}