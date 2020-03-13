package com.oyespace.guards.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Base64
import android.widget.ImageView
import java.io.IOException
import java.io.InputStream
import java.net.URL


class GetImageFromUrl(ivStaff: ImageView) : AsyncTask<String, Void, Bitmap>() {
    var bitmap: Bitmap? = null
    val ivStaff: ImageView? = ivStaff
    var encodedImage: String? = null

    override fun doInBackground(vararg url: String): Bitmap? {
        val stringUrl: String = url.get(0)
        bitmap = null
        val inputStream: InputStream
        try {
            inputStream = URL(stringUrl).openStream()
            bitmap = BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bitmap
    }

    override fun onPreExecute() {
        super.onPreExecute()
        // ...
    }

    override fun onPostExecute(result: Bitmap?) {
        super.onPostExecute(result)
        encodedImage = RandomUtils.encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 100)
                val imageBytes = Base64.decode(encodedImage, Base64.DEFAULT)
               val decodedImage =
                   BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        ivStaff!!.setImageBitmap(decodedImage)
    }
}