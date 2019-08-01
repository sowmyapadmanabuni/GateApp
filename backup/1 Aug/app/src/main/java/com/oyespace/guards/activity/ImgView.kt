package com.oyespace.guards.activity

import android.os.Bundle
import android.util.Log
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_img_view.*
import com.oyespace.guards.R

class ImgView : BaseKotlinActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_img_view)

        Log.d("intentdata ImgView", "ImgView " + intent.getStringExtra("URL_IMAGE") + " " + " " + " ");

        if (intent.getStringExtra("URL_IMAGE").length > 0) {
            Picasso.with(applicationContext)
                .load(intent.getStringExtra("URL_IMAGE"))
                .placeholder(R.drawable.placeholder_dark).error(R.drawable.placeholder_dark).into(img_view)
        } else {

        }

    }

}