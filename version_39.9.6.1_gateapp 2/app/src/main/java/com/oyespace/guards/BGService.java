package com.oyespace.guards;
// This service is called when the patrolling is started

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import com.oyespace.guards.network.ResponseHandler;
import com.oyespace.guards.network.RestClient;
import com.oyespace.guards.network.URLData;
import com.oyespace.guards.pertroling.GPSTracker;
import com.oyespace.guards.pojo.VisitorEntryLog;
import com.oyespace.guards.request.SaveTrackingReq;
import com.oyespace.guards.responce.SaveTrackingResp;
import com.oyespace.guards.utils.ConstantUtils;
import com.oyespace.guards.utils.DateTimeUtils;
import com.oyespace.guards.utils.LocalDb;
import com.oyespace.guards.utils.Prefs;

import java.util.Locale;

import static com.oyespace.guards.constants.PrefKeys.BG_NOTIFICATION_ON;
import static com.oyespace.guards.constants.PrefKeys.LANGUAGE;
import static com.oyespace.guards.utils.ConstantUtils.*;
import static com.oyespace.guards.utils.DateTimeUtils.deliveryTimeUp;
import static com.oyespace.guards.utils.DateTimeUtils.getCurrentTimeLocal;
import static com.oyespace.guards.utils.Utils.showToast;

public class BGService extends Service  {
    String language;
    public BGService() {
    }

    Handler handler;
    TextToSpeech t1 ;
    String overStayingNames="";

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent2, int flags, int startId) {
        // Let it continue running until it is stopped.

        handler = new Handler();

        // check if GPS enabled
        saveLatLongPoints();
        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status!=TextToSpeech.ERROR)
                    //  language=Prefs.getString(LANGUAGE,null);

                    //  if(language.equals("en")){
                    t1.setLanguage(Locale.getDefault());
                // }
//                else if(language.equals("hi")){
//                    t1.setLanguage(Locale.forLanguageTag(language));
//                }


                //new Locale("hi")
            }
        });

        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        am.setStreamVolume(AudioManager.STREAM_MUSIC,am.getStreamMaxVolume(AudioManager.STREAM_MUSIC),0);


        return START_STICKY;
    }

    public void saveLatLongPoints(){
        final Runnable r = new Runnable() {
            public void run() {
                saveLatLongPoints();
                Prefs.putBoolean(BG_NOTIFICATION_ON,true);
            }
        };
        overStayingNames="";
        if(LocalDb.getVisitorEnteredLog()!=null) {
            for (VisitorEntryLog s : LocalDb.getVisitorEnteredLog()) {
                //if the existing elements contains the search input
                if (s.getVlVisType().equalsIgnoreCase(DELIVERY)&&deliveryTimeUp(s.getVlEntryT(),getCurrentTimeLocal(),1)) {
                    //adding the element to filtered list
                    overStayingNames+=s.getVlfName();
                }
            }
        }

        if(overStayingNames.length()>2){
            if(t1 != null) {
             //   t1.speak("Attention Security", TextToSpeech.QUEUE_FLUSH, null);

                try {
                    Thread.sleep((long) 3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
               // t1.speak("Overstaying " + overStayingNames, TextToSpeech.QUEUE_FLUSH, null);

            }
            else {
                Log.e("YOUR_TAG", "TextToSpeech Null");
            }

//
//            try {
//                Thread.sleep((long) 3000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            t1.speak("Overstaying " + overStayingNames, TextToSpeech.QUEUE_FLUSH, null);

        }

        if(Prefs.getBoolean(BG_NOTIFICATION_ON,true)) {
            handler.postDelayed(r, 100000);

        }else {
            Prefs.putBoolean(BG_NOTIFICATION_ON,false);
            stopSelf();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Prefs.putBoolean(BG_NOTIFICATION_ON,false);
//        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }
}
