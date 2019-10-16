package com.oyespace.guards.services;
// This service is called when the patrolling is started

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;

import com.oyespace.guards.R;

public class SOSSirenService extends Service {
    MediaPlayer mediaPlayer;


    public SOSSirenService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent2, int flags, int startId) {
        // Let it continue running until it is stopped.


        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
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
