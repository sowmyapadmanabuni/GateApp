package com.oyespace.guards

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import com.oyespace.guards.activity.BaseKotlinActivity
import com.oyespace.guards.constants.PrefKeys
import com.oyespace.guards.utils.ConstantUtils
import com.oyespace.guards.utils.Prefs
import kotlinx.android.synthetic.main.activity_final_registration.*
import kotlinx.android.synthetic.main.activity_img_view.*


class ImageBigView : BaseKotlinActivity(){
    lateinit var mBitmap: Bitmap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_img_view)

        val wrrw = intent.getByteArrayExtra(ConstantUtils.PERSON_PHOTO)
        if(wrrw!=null) {
//            var mBitmap: Bitmap;
            mBitmap = BitmapFactory.decodeByteArray(wrrw, 0, wrrw.size)
            img_view.setImageBitmap(mBitmap)

        }
    }

}