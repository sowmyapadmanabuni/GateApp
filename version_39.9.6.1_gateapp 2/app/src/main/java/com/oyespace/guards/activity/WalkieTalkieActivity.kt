package com.oyespace.guards.activity

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaRecorder
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.oyespace.guards.BackgroundSyncReceiver
import com.oyespace.guards.R
import com.oyespace.guards.network.ImageApiClient
import com.oyespace.guards.network.ImageApiInterface
import com.oyespace.guards.utils.ConstantUtils
import com.oyespace.guards.utils.ConstantUtils.BSR_Action
import kotlinx.android.synthetic.main.activity_walkie_talkie.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.io.*


class WalkieTalkieActivity : AppCompatActivity() {
    var audioclip: String? = null
    var audiofile: File? = null
    private var mFileName = ""
    private var myAudioRecorder: MediaRecorder? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_walkie_talkie)

       val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        try {
            audiofile = File.createTempFile("AudioRecording", ".3gp", dir)
            Log.d("uploadAudio 43",audiofile.toString() )
        } catch (e: IOException) {
            //            Log.e(TAG, "external storage access error");
            return
        }


        mFileName = audiofile!!.getAbsolutePath()
        Log.d("uploadAudio 51",mFileName )

        record.setOnClickListener {
            myAudioRecorder = MediaRecorder()
            myAudioRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
            myAudioRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            myAudioRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            myAudioRecorder!!.setOutputFile(mFileName)
            Log.d("uploadAudio 59",mFileName )

            try {
                myAudioRecorder!!.prepare()
                myAudioRecorder!!.start()
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }

// Started to stop after 5 sec automatically
            Handler().postDelayed({
                if (myAudioRecorder != null) {
                    myAudioRecorder!!.reset()
                    myAudioRecorder!!.release()
                    Toast.makeText(applicationContext, "Recording Stopped", Toast.LENGTH_LONG).show()
                    uploadAudio()
                    myAudioRecorder = null
                }

                record.isEnabled = true
            }, 5000)

            // To stop after 5 sec automatically end

            Toast.makeText(applicationContext, "Recording Started", Toast.LENGTH_LONG).show()

        }
    }

    fun uploadAudio() {


        val Audio = audiofile
        audioclip = Audio.toString()


        val file = File(audiofile.toString())
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val body = MultipartBody.Part.createFormData("Test", audioclip, requestFile)
        val apiService = ImageApiClient.getImageClient().create(ImageApiInterface::class.java)
        val call = apiService.updateImageProfile(body)

        call.enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: retrofit2.Response<Any>) {
                try {

                    val intentAction1 = Intent(applicationContext, BackgroundSyncReceiver::class.java)
                    intentAction1.putExtra(BSR_Action, ConstantUtils.SENDAUDIO)
                    intentAction1.putExtra("FILENAME", response.body().toString())
                    sendBroadcast(intentAction1)
                    Log.d("uploadAudio 110", "response:" + response.body()!!)
                    Log.d("uploadAudio 112", file.toString())


                } catch (ex: Exception) {
                    Log.d("uploadAudio 113", "errr:" + ex.toString())

                }

            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                // Log error here since request failed
                Log.d("uploadAudio 121", t.toString())

            }
        })

    }
}



