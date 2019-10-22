package com.oyespace.guards.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Environment
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.oyespace.guards.Myapp
import com.oyespace.guards.R
import com.oyespace.guards.models.NotificationSyncModel
import java.io.File
import java.text.SimpleDateFormat


/**
 * Created by Kalyan on 04-Nov-17.
 */
class AppUtils {
    companion object {
        fun getPrice(price: Int?): String {
            val string = Myapp.getContext().getString(R.string.rupee)
            return string + " " + price
        }

        fun getPrice(price: String?): String {
            val string = Myapp.getContext().getString(R.string.rupee)
            return string + " " + price
        }

        fun getPrice(price: Double?): String {
            val string = Myapp.getContext().getString(R.string.rupee)
            return string + " " + price
        }

        fun intToString(price: Int?): String {
            return  ""+ price
        }

        fun getTimeFromDate(serverTime:String?):String{
            try {
                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                val formatter = SimpleDateFormat("hh:mm a")
                val formattedDate = formatter.format(parser.parse(serverTime))
                return formattedDate
            }catch (e:Exception){
                return serverTime!!
            }
        }



        fun calGeoLocationDiff(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Int {
            val earthRadius = 6371000.0 //meters
            val dLat = Math.toRadians(lat2 - lat1)
            val dLng = Math.toRadians(lng2 - lng1)
            val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                    Math.sin(dLng / 2) * Math.sin(dLng / 2)
            val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
            val dist = (earthRadius * c).toInt()

            Log.d("xgdssd Dgddfdf", "$lat1 $lng1 $lat2 $lng2 $dist")
            return dist
        }

        fun updateFirebaseColor(visitorId: Int, buttonColor: String = "#ffb81a") {

            Log.i("taaag", "push to firebase: " + visitorId)
            val ref = FirebaseDatabase.getInstance().getReference("NotificationSync")
            val id = ref.push().key
            val notificationSyncModel = NotificationSyncModel(visitorId, buttonColor)
            ref.child(visitorId.toString()).setValue(notificationSyncModel).addOnCompleteListener {
                //            Toast.makeText(this@StaffEntryRegistration, "DONE", Toast.LENGTH_LONG).show()
            }

        }

        fun playAudio(mcontext: Context, filename: String) {

            val mediaPlayer: MediaPlayer
//
            val am = mcontext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0)
            mediaPlayer = MediaPlayer()


            var spb = SoundPool.Builder()
            spb.setMaxStreams(10)
            var attrBuilder = AudioAttributes.Builder()
            attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC)
            spb.setAudioAttributes(attrBuilder.build())
            spb.build()

            mediaPlayer.setDataSource("http://mediaupload.oyespace.com/" + filename)
            mediaPlayer.prepare()

            mediaPlayer.start()


            val baseDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .absolutePath
            val f = File(baseDir + filename)
            f.delete()

        }

    }




}