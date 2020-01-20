package com.oyespace.guards.utils

import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
import android.widget.Toast
import com.oyespace.guards.network.ImageApiClient
import com.oyespace.guards.network.ImageApiInterface
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class UploadImageApi {

    companion object {

        fun uploadImage(localImgName: String, incidentPhoto: Bitmap?) {
            Log.d("uploadImage", localImgName)
            var byteArrayProfile: ByteArray?
            val mPath =
                Environment.getExternalStorageDirectory().toString() + "/" + localImgName + ".jpg"


            val imageFile = File(mPath)

            //  Log.v("FILENamen vmxc vmc11",imageFile)

            try {
                val outputStream = FileOutputStream(imageFile)
                val quality = 80
                if (incidentPhoto != null) {
                    incidentPhoto.compress(Bitmap.CompressFormat.PNG, quality, outputStream)
                }
                outputStream.flush()
                outputStream.close()

                val bosProfile = ByteArrayOutputStream()
                if (incidentPhoto != null) {
                    incidentPhoto.compress(Bitmap.CompressFormat.PNG, quality, bosProfile)
                }

                byteArrayProfile = bosProfile.toByteArray()
                val len = bosProfile.toByteArray().size
                println("AFTER COMPRESSION-===>$len")
                bosProfile.flush()
                bosProfile.close()
                if (incidentPhoto != null) {
                    //    incidentPhoto.recycle()
                }
                Timber.e("uploadImage  bf", "sfas")
            } catch (ex: Exception) {
                byteArrayProfile = null
                Log.d("uploadImage ererer bf", ex.toString())
            }


            val file = File(imageFile.toString())
            val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
            val body = MultipartBody.Part.createFormData("Test", localImgName, requestFile)
            val apiService = ImageApiClient.getImageClient().create(ImageApiInterface::class.java)
            val call = apiService.updateImageProfile(body)

            call.enqueue(object : Callback<Any> {
                override fun onResponse(call: Call<Any>, response: retrofit2.Response<Any>) {
                    try {
                        Log.d("uploadImage", "response:" + response.body()!!)


                        // file.delete()

                    } catch (ex: Exception) {
                        Log.d("uploadImage", "errr:" + ex.toString())

                    }


                }

                override fun onFailure(call: Call<Any>, t: Throwable) {
                    Log.d("uploadImage", t.toString())

                }
            })


        }
    }
}