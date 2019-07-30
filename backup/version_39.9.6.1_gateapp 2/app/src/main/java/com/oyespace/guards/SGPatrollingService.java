package com.oyespace.guards;
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

public class SGPatrollingService extends Service implements ResponseHandler {
    public SGPatrollingService() {
    }

    Handler handler;
    MediaPlayer mediaPlayer;
    GPSTracker gpsTracker;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent2, int flags, int startId) {
        // Let it continue running until it is stopped.

        handler = new Handler();
        gpsTracker = new GPSTracker(SGPatrollingService.this);

        // check if GPS enabled
        saveLatLongPoints();

        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        am.setStreamVolume(AudioManager.STREAM_MUSIC,am.getStreamMaxVolume(AudioManager.STREAM_MUSIC),0);
        mediaPlayer = MediaPlayer.create(this, R.raw.whistle);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        return START_STICKY;
    }

    public void saveLatLongPoints(){
        final Runnable r = new Runnable() {
            public void run() {
                saveLatLongPoints();
            }
        };
        if(gpsTracker.canGetLocation()){
            saveCheckPoints("",gpsTracker.getLatitude()+","+gpsTracker.getLongitude());
        }

        if(Prefs.getInt(PATROLLING_ID,0) !=0) {
            handler.postDelayed(r, 5000);

        }else {
            stopSelf();
        }

    }

    private void saveCheckPoints(String checkPointName,String gpsPoint) {

        RestClient restClient = RestClient.getInstance();

        SaveTrackingReq loginReq = new SaveTrackingReq();

        loginReq.ASAssnID=Prefs.getInt(ASSOCIATION_ID,0);
        loginReq.CPCkPName=checkPointName;
        loginReq.TRGPSPnt=gpsPoint;
        loginReq.WKWorkID=LocalDb.getStaffList().get(0).getWkWorkID();
        loginReq.PTPtrlID = Prefs.getInt(PATROLLING_ID,0) ;
        loginReq.TRTDateT=DateTimeUtils.getCurrentTimeLocal();

        Log.d("saveCheckPoints","StaffEntry "+loginReq.ASAssnID+" "+loginReq.CPCkPName+" "
                +loginReq.WKWorkID+" "+loginReq.PTPtrlID+" "+loginReq.TRTDateT );

        restClient.addHeader(OYE247KEY, OYE247TOKEN);
        restClient.post(this, loginReq, SaveTrackingResp.class, this, URLData.URL_SAVE_CHECK_POINT);

    }

    @Override
    public void onFailure(Exception e, int urlId) {

        showToast(this, e.getMessage()+" id "+urlId);
    }

    @Override
    public void onSuccess(String responce, Object data, int urlId, int position) {

      if (urlId == URLData.URL_SAVE_CHECK_POINT.getUrlId()) {

            SaveTrackingResp loginDetailsResponce = (SaveTrackingResp) data;
            if (loginDetailsResponce != null) {
                Log.d("str3 Service", "saveCheckPoints: " + urlId+" id "+position+" "+" "+" "+loginDetailsResponce.toString());
                if(loginDetailsResponce.success.equalsIgnoreCase("true")) {
//                    showToast(this, "Go To Next Check point Service");
                }else{
//                    showToast(this, " not saved Service");
                }

            } else {
//                showToast(this, "Something went wrong . please try again Service");
            }

        }

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();

//        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }
}
