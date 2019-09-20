package com.oyespace.guards.services;
// This service is called when the patrolling is started

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.oyespace.guards.R;
import com.oyespace.guards.network.ResponseHandler;
import com.oyespace.guards.network.RestClient;
import com.oyespace.guards.network.URLData;
import com.oyespace.guards.pertroling.GPSTracker;
import com.oyespace.guards.request.SaveTrackingReq;
import com.oyespace.guards.responce.SaveTrackingResp;
import com.oyespace.guards.utils.DateTimeUtils;
import com.oyespace.guards.utils.LocalDb;
import com.oyespace.guards.utils.Prefs;

import static com.oyespace.guards.constants.PrefKeys.PATROLLING_ID;
import static com.oyespace.guards.utils.ConstantUtils.ASSOCIATION_ID;
import static com.oyespace.guards.utils.ConstantUtils.OYE247KEY;
import static com.oyespace.guards.utils.ConstantUtils.OYE247TOKEN;
import static com.oyespace.guards.utils.Utils.showToast;

public class SOSSirenService extends Service {
    public SOSSirenService() {
    }


    MediaPlayer mediaPlayer;


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent2, int flags, int startId) {
        // Let it continue running until it is stopped.


        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        am.setStreamVolume(AudioManager.STREAM_MUSIC,am.getStreamMaxVolume(AudioManager.STREAM_MUSIC),0);
        mediaPlayer = MediaPlayer.create(this, R.raw.siren);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();

//        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }
}
