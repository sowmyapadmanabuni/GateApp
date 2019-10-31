package com.oyespace.guards.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
            return "" + price
        }

        fun getTimeFromDate(serverTime: String?): String {
            try {
                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                val formatter = SimpleDateFormat("hh:mm a")
                val formattedDate = formatter.format(parser.parse(serverTime))
                return formattedDate
            } catch (e: Exception) {
                return serverTime!!
            }
        }

        fun decodeBitmap(data: ByteArray): Bitmap {
            var bitmap: Bitmap? = null
            try {
                val bfOptions = BitmapFactory.Options()
                bfOptions.inDither = false // Disable Dithering mode
                bfOptions.inPurgeable = true // Tell to gc that whether it needs free
                // memory, the Bitmap can be cleared
                bfOptions.inInputShareable = true // Which kind of reference will be
                // used to recover the Bitmap data
                // after being clear, when it will
                // be used in the future
                bfOptions.inTempStorage = ByteArray(32 * 1024)

                if (data != null)
                    bitmap = BitmapFactory.decodeByteArray(
                        data, 0, data.size,
                        bfOptions
                    )

                return bitmap!!
            }catch (e:java.lang.Exception){
                e.printStackTrace()
                return bitmap!!;
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

            val childName = "A_${Prefs.getInt(ConstantUtils.ASSOCIATION_ID, 0)}"
            Log.i("taaag", "push to firebase: " + visitorId)
            val notificationSyncModel = NotificationSyncModel(visitorId, buttonColor)
            FirebaseDatabase.getInstance()
                .getReference("NotificationSync")
                .child(childName)
                .child(visitorId.toString()).setValue(notificationSyncModel)

        }

        fun removeFBNotificationSyncEntry(visitorId: Int) {

            val childName = "A_${Prefs.getInt(ConstantUtils.ASSOCIATION_ID, 0)}"
            Log.i("taaag", "remove from firebase: " + visitorId)
            FirebaseDatabase.getInstance()
                .getReference("NotificationSync")
                .child(childName)
                .child(visitorId.toString())
                .removeValue()

        }

        fun addWalkieTalkieAudioFirebase(fileName: String) {
            val childName = "A_${Prefs.getInt(ConstantUtils.ASSOCIATION_ID, 0)}"
            FirebaseDatabase.getInstance()
                .getReference("walkie_talkie_audio")
                .child(childName)
                .child("filename")
                .setValue(fileName)
        }

        fun removeWalkieTalkieAudioFirebase() {
            val childName = "A_${Prefs.getInt(ConstantUtils.ASSOCIATION_ID, 0)}"
            FirebaseDatabase.getInstance()
                .getReference("walkie_talkie_audio")
                .child(childName)
                .child("filename")
                .removeValue()
        }

        fun playWalkieTalkiAudio(context: Context, filename: String) {


            Log.i("taaaag", "playing audio: $filename")
            var mp = MediaPlayer.create(context, R.raw.walkietalkiestart)

            try {
                if (mp.isPlaying) {
                    mp.stop()
                    mp.release()
                    //getAudio(remoteMessage!!.data["entry_type"].toString())
                    mp = MediaPlayer.create(context, R.raw.walkietalkieinterference)
                }

                mp.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }


            val mediaPlayer: MediaPlayer
//
            val am = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0)
            mediaPlayer = MediaPlayer()


            val spb = SoundPool.Builder()
            spb.setMaxStreams(10)
            val attrBuilder = AudioAttributes.Builder()
            attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC)
            spb.setAudioAttributes(attrBuilder.build())
            spb.build()

            mediaPlayer.setDataSource(ConstantUtils.IMAGE_BASE_URL + filename)
            try {
                mediaPlayer.prepareAsync()

                mediaPlayer.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }


            val baseDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .absolutePath
            val f = File(baseDir + filename)
            f.delete()

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