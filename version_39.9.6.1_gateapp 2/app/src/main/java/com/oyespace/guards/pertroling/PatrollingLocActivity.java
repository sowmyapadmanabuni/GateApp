package com.oyespace.guards.pertroling;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.zxing.Result;
import com.oyespace.guards.R;
import com.oyespace.guards.activity.BaseKotlinActivity;
import com.oyespace.guards.broadcastreceiver.GeofenceBroadcastReceiver;
import com.oyespace.guards.models.CheckPointsOfSheduleListResponse;
import com.oyespace.guards.models.PatrolShift;
import com.oyespace.guards.models.ScheduleCheckPointsData;
import com.oyespace.guards.network.CommonDisposable;
import com.oyespace.guards.network.RetrofitClinet;
import com.oyespace.guards.pojo.CheckPointData;
import com.oyespace.guards.pojo.GetCheckPointResponse;
import com.oyespace.guards.utils.Prefs;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.core.ViewFinderView;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static com.oyespace.guards.utils.ConstantUtils.ACTIVE_PATROLLING_LAST_CP;
import static com.oyespace.guards.utils.ConstantUtils.ACTIVE_PATROLLING_SCHEDULE;
import static com.oyespace.guards.utils.ConstantUtils.ASSOCIATION_ID;
import static com.oyespace.guards.utils.ConstantUtils.CHECKPOINT_DISTANCE_THRESHOLD;
import static com.oyespace.guards.utils.ConstantUtils.CHECKPOINT_TYPE_END;
import static com.oyespace.guards.utils.ConstantUtils.CHECKPOINT_TYPE_START;
import static com.oyespace.guards.utils.ConstantUtils.OYE247TOKEN;
import static com.oyespace.guards.utils.ConstantUtils.PATROLLING_SCHEDULE_ID;

public class PatrollingLocActivity extends BaseKotlinActivity implements ZXingScannerView.ResultHandler, OnCompleteListener<Void> {

    public LocationService locationService;
    public Location mLocation, mPredictedLocation;
    public TextToSpeech toSpeech;
    public int scheduleId;
    private BroadcastReceiver locationUpdateReceiver;
    private BroadcastReceiver predictedLocationReceiver;
    private ZXingScannerView mScannerView;
    private boolean isScanEnabled =false;
    private int mActiveSchedule = 0;
    ArrayList<ScheduleCheckPointsData> scheduleCheckPoints = new ArrayList<ScheduleCheckPointsData>();


    private enum PendingGeofenceTask {
        ADD, REMOVE, NONE
    }

    private static final String PACKAGE_NAME = "com.google.android.gms.location.Geofence";

    static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";
    private GeofencingClient mGeofencingClient;
    private ArrayList<Geofence> mGeofenceList=new ArrayList<>();
    private PendingIntent mGeofencePendingIntent;
    private PendingGeofenceTask mPendingGeofenceTask = PendingGeofenceTask.NONE;

    private static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;
    static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;
    static final float GEOFENCE_RADIUS_IN_METERS = 40;

    //TextView loc;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            String name = className.getClassName();

            if (name.endsWith("LocationService")) {
                locationService = ((LocationService.LocationServiceBinder) service).getService();
                locationService.startUpdatingLocation();
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            if (className.getClassName().equals("LocationService")) {
                locationService = null;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_scanner_point);

        //@Todo: Remove these two lines
//        Prefs.remove(ACTIVE_PATROLLING_SCHEDULE);
//        Prefs.remove(ACTIVE_PATROLLING_LAST_CP);

        mActiveSchedule = getIntent().getIntExtra(PATROLLING_SCHEDULE_ID,0);
        getScheduleCheckPoints();

        startLocationListener();
        initScanner();
        initSpeech();


        int ongoingSchedule = Prefs.getInt(ACTIVE_PATROLLING_SCHEDULE, -1);
        int lastScannedCP = Prefs.getInt(ACTIVE_PATROLLING_LAST_CP, -1);

        if(ongoingSchedule == mActiveSchedule && ongoingSchedule != -1){
            showPendingWarning();
        }

        //mGeofencePendingIntent = null;
        //mGeofencingClient = LocationServices.getGeofencingClient(this);
        //populateGeofenceList();
        //addGeofences();
    }


    private void getScheduleCheckPoints(){
        if(mActiveSchedule != 0) {
            showProgressrefresh();
            RetrofitClinet.Companion.getInstance()
                    .scheduleCheckPointsList(OYE247TOKEN, "" + mActiveSchedule)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new CommonDisposable<CheckPointsOfSheduleListResponse<ArrayList<PatrolShift>>>() {
                        @Override
                        public void noNetowork() {
                            dismissProgressrefresh();
                        }

                        @Override
                        public void onErrorResponse(@NotNull Throwable e) {
                            dismissProgressrefresh();
                        }

                        @Override
                        public void onSuccessResponse(CheckPointsOfSheduleListResponse<ArrayList<PatrolShift>> checkPointResponse) {
                            try {
                                dismissProgressrefresh();
                                Log.e("getScheduleCheckPoints",""+checkPointResponse.getData().getCheckPointsBySchedule());
                                if(checkPointResponse.getSuccess() && checkPointResponse.getData().getCheckPointsBySchedule().size()>0){
                                    isScanEnabled = true;
                                    PatrolShift patrolShift = checkPointResponse.getData().getCheckPointsBySchedule().get(0);
                                    scheduleCheckPoints = patrolShift.getPoint();

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
        }
    }


    private void showPendingWarning(){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage("Do you want to continue scanning or reset the existing data?");
        alertBuilder.setCancelable(false);
        alertBuilder.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Prefs.remove(ACTIVE_PATROLLING_SCHEDULE);
                Prefs.remove(ACTIVE_PATROLLING_LAST_CP);
                Toast.makeText(PatrollingLocActivity.this,"Please go to the first checkpoint",Toast.LENGTH_LONG).show();
                dialog.cancel();
            }
        });
        alertBuilder.setNegativeButton("Continue", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(PatrollingLocActivity.this,"Please go to the first checkpoint",Toast.LENGTH_LONG).show();
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.setTitle("WARNING");
        alertDialog.show();
    }

    private void populateGeofenceList(){
        mGeofenceList.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId("0")

                // Set the circular region of this geofence.
                .setCircularRegion(
                        9.6146591,
                        76.3209412,
                        GEOFENCE_RADIUS_IN_METERS
                )

                // Set the expiration duration of the geofence. This geofence gets automatically
                // removed after this period of time.
                .setExpirationDuration(GEOFENCE_EXPIRATION_IN_MILLISECONDS)

                // Set the transition types of interest. Alerts are only generated for these
                // transition. We track entry and exit transitions in this sample.
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)

                // Create the geofence.
                .build());
    }

    @SuppressWarnings("MissingPermission")
    private void addGeofences() {

        mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnCompleteListener(this);
    }

    @SuppressWarnings("MissingPermission")
    private void removeGeofences() {

        mGeofencingClient.removeGeofences(getGeofencePendingIntent()).addOnCompleteListener(this);
    }

    private GeofencingRequest getGeofencingRequest(){
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        mGeofencePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }

    @Override
    public void onComplete(@NonNull Task<Void> task) {
        mPendingGeofenceTask = PendingGeofenceTask.NONE;
        if (task.isSuccessful()) {
            updateGeofencesAdded(!getGeofencesAdded());
            //setButtonsEnabledState();

            String messageId = getGeofencesAdded() ? "geofences_added" :
                    "geofences_removed";
            Toast.makeText(this, (messageId), Toast.LENGTH_SHORT).show();
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            // String errorMessage = GeofenceErrorMessages.getErrorString(this, task.getException());
            Log.w("GEOFENCING", ""+task.getException());
        }
    }

    private void updateGeofencesAdded(boolean added) {
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putBoolean(GEOFENCES_ADDED_KEY, added)
                .apply();
    }
    private boolean getGeofencesAdded() {
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
                GEOFENCES_ADDED_KEY, false);
    }


    private void startLocationListener() {
        final Intent serviceStart = new Intent(this.getApplication(), LocationService.class);
        this.getApplication().startService(serviceStart);
        this.getApplication().bindService(serviceStart, serviceConnection, Context.BIND_AUTO_CREATE);

        mLocation = new GPSTracker(this).getLocation();
        mPredictedLocation = mLocation;

        locationUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Location newLocation = intent.getParcelableExtra("location");
                Log.e("newLocation", "" + newLocation);
                Log.e("mPredictedLocation", "" + mPredictedLocation);
                if (mPredictedLocation == null) {
                    mPredictedLocation = newLocation;
                }
                mLocation = newLocation;
                //loc.setText(""+newLocation.getLatitude()+" , "+newLocation.getLongitude()+", Acc: "+newLocation.getAccuracy());
            }
        };

        predictedLocationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Location predictedLocation = intent.getParcelableExtra("location");
                Log.e("predictedLocation", "" + predictedLocation);
                mPredictedLocation = predictedLocation;
                //drawPredictionRange(predictedLocation);

            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(
                locationUpdateReceiver,
                new IntentFilter("LocationUpdated"));

        LocalBroadcastManager.getInstance(this).registerReceiver(
                predictedLocationReceiver,
                new IntentFilter("PredictLocation"));


    }

    private void initScanner() {

        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this) {
            @Override
            protected IViewFinder createViewFinderView(Context context) {
                return new CustomViewFinderView(context);
            }
        };
        contentFrame.addView(mScannerView);
    }

    private void initSpeech() {
        toSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR)
                    toSpeech.setLanguage(Locale.getDefault());
            }
        });
    }

    @Override
    public void handleResult(final Result result) {
        //showProgressrefreshWithText("Calibrating.. please wait for 5 seconds");
        mScannerView.stopCamera();
        toSpeech.speak("Calibrating.. Please wait..", TextToSpeech.QUEUE_FLUSH, null);
        showAnimatedDialog("Calibrating.. Please wait..", R.raw.gps, false, "");

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideAnimatedDialog();
                String patrolingdata = result.getText();
                Log.e("ACTUAL", "" + result.getText());
                String[] patrolingdataList = patrolingdata.split(",");
                if (patrolingdataList.length > 4) {
                    getCheckPoint(patrolingdataList);
                }
            }
        }, 6000);
    }

    private void parseCheckPoint(CheckPointData qrCheckpoint) {
        try {
            String mCPName = qrCheckpoint.getCpCkPName();
            String locationStr = qrCheckpoint.getCpgpsPnt();
            Double mCPLatitude = 0.0;
            Double mCPLongitude = 0.0;

            if (locationStr.contains(" ")) {
                String[] locationStrArray = locationStr.split(" ");
                if (locationStrArray.length == 2) {
                    String latStr = locationStrArray[0].trim();
                    String lonStr = locationStrArray[1].trim();
                    mCPLatitude = Double.parseDouble(latStr);
                    mCPLongitude = Double.parseDouble(lonStr);
                }
            }

            int mCPAssociation = qrCheckpoint.getAsAssnID();
            int mCPIdentifier = qrCheckpoint.getCpChkPntID();
            String mCPType = qrCheckpoint.getCpcPntAt();


            int mCurrentAssociation = Prefs.getInt(ASSOCIATION_ID, 0);

            Log.e("mCurrentAssociation", "" + mCurrentAssociation);

            if (mCurrentAssociation != 0 && mCurrentAssociation == mCPAssociation) {

                if (isValidCheckPoint(qrCheckpoint)) {


                } else {
                    showAnimatedDialog("Invalid QR Code.", R.raw.error, true, "OK");
                }

            } else {
               // showAnimatedDialog("Wrong QR Code.", R.raw.error, true, "OK");
            }

            Log.e("PATROLL", "Patroling Data " + qrCheckpoint);
            Log.e("PARSED", " " + mCPLatitude + " - " + mCPLongitude);


            onResume();
        } catch (Exception e) {
            e.printStackTrace();
            onResume();
        }
    }


    private boolean isValidCheckPoint(CheckPointData checkPointData) {
        //@Todo: Check cpID exist in schedule
        //@Todo: Check cpID order
        //@Todo: Check Start Point if it is a fresh scan

        int ongoingSchedule = Prefs.getInt(ACTIVE_PATROLLING_SCHEDULE, -1);
        int lastScannedCP = Prefs.getInt(ACTIVE_PATROLLING_LAST_CP, -1);
        String mCPType = checkPointData.getCpcPntAt();
        String locationStr = checkPointData.getCpgpsPnt();
        CoordinateFromString coordinate = new CoordinateFromString(locationStr);
        Log.e("CHECKPOIINT:: ", "" + checkPointData);
        calculateWifiSignalWeightage(checkPointData.getCpSurrName());
        if (ongoingSchedule == -1) {
            //Fresh Start
            int nextCP = getNextCheckPoint(-1);

            if(nextCP==checkPointData.getCpChkPntID()){
                if (mCPType.equals(CHECKPOINT_TYPE_START)) {

                    if (isValidDistance(coordinate.getLat(), coordinate.getLon())) {
                        //@Todo: Zero must replace with actual current schedule id.
                        Prefs.putInt(ACTIVE_PATROLLING_SCHEDULE, mActiveSchedule);
                        Prefs.putInt(ACTIVE_PATROLLING_LAST_CP, checkPointData.getCpChkPntID());
                        toSpeech.speak("Checkpoint Scanned Successfully", TextToSpeech.QUEUE_FLUSH, null);
                        showAnimatedDialog("Checkpoint Scanned Successfully", R.raw.done, true, "OK");
                        return true;
                    } else {
                        toSpeech.speak("You are out of checkpoint location", TextToSpeech.QUEUE_FLUSH, null);
                        showAnimatedDialog("Out of checkpoint location", R.raw.error, true, "OK");
                        return false;
                    }
                }else {
                    toSpeech.speak("You missed the starting checkpoint", TextToSpeech.QUEUE_FLUSH, null);
                    showAnimatedDialog("Wrong Starting Checkpoint", R.raw.error, true, "OK");
                    return false;
                }
            }
            else {
                toSpeech.speak("You are scanning the wrong checkpoint", TextToSpeech.QUEUE_FLUSH, null);
                showAnimatedDialog("Wrong Checkpoint", R.raw.error, true, "OK");
                return false;
            }

        } else {
            //@Todo: Get the last scanned checkpoint. Check the next checkpoint & validate
            //@Todo: Add this check in dashboard/FRTDB for automatic resume
            //@Todo: Check already scanned : Not in priority

            //@Todo: Uncomment the commented line
            int nextCP = getNextCheckPoint(lastScannedCP);
            if (nextCP == checkPointData.getCpChkPntID() && !mCPType.equalsIgnoreCase(CHECKPOINT_TYPE_START)) {

                if (isValidDistance(coordinate.getLat(), coordinate.getLon())) {
                    //toSpeech.speak("VALID", TextToSpeech.QUEUE_FLUSH, null);
                    if (mCPType.equalsIgnoreCase(CHECKPOINT_TYPE_END)) {
                        Prefs.remove(ACTIVE_PATROLLING_SCHEDULE);
                        Prefs.remove(ACTIVE_PATROLLING_LAST_CP);
                        toSpeech.speak("Patrolling Completed", TextToSpeech.QUEUE_FLUSH, null);
                        showAnimatedDialog("Patrolling Completed", R.raw.done, true, "OK");
                        onBackPressed();
                    }else{
                        Prefs.putInt(ACTIVE_PATROLLING_SCHEDULE, mActiveSchedule);
                        Prefs.putInt(ACTIVE_PATROLLING_LAST_CP, checkPointData.getCpChkPntID());
                        toSpeech.speak("Checkpoint Scanned Successfully", TextToSpeech.QUEUE_FLUSH, null);
                        showAnimatedDialog("Checkpoint Scanned Successfully", R.raw.done, true, "OK");
                       //
                    }
                    return true;
                } else {
                    toSpeech.speak("You are out of checkpoint location", TextToSpeech.QUEUE_FLUSH, null);
                    showAnimatedDialog("Out of checkpoint location", R.raw.error, true, "OK");
                    return false;
                }

            } else {
                toSpeech.speak("You are scanning the wrong checkpoint", TextToSpeech.QUEUE_FLUSH, null);
                showAnimatedDialog("Missed a checkpoint", R.raw.error, true, "OK");
                return false;
            }

        }


    }

    private int getNextCheckPoint(int lastCheckPoint) {
        //@Todo: Based on order of checkpoints in schedule, fetch the next checkpoint and return
        //-1 = Error
        //0 = No more checkpoints (Reached Last Checkpoint)

        if(scheduleCheckPoints.size() > 0){

            if(lastCheckPoint == -1){
                return scheduleCheckPoints.get(0).getPsChkPID();
            }

            int currentIndex = -1;
            int nextCheckPoint = -1;
            for(int i=0;i<scheduleCheckPoints.size();i++){
                ScheduleCheckPointsData cp = scheduleCheckPoints.get(i);
                if(cp.getPsChkPID() == lastCheckPoint){
                    currentIndex = i;
                    break;
                }
            }
            if(scheduleCheckPoints.size() == currentIndex+1){
                return 0;
            }else{
                try{
                    return scheduleCheckPoints.get(currentIndex+1).getPsChkPID();
                }catch (Exception e){
                    e.printStackTrace();
                    return 0;
                }
            }

        }

        return -1;
    }

    private boolean isValidDistance(Double mCPLatitude, Double mCPLongitude) {
        float result = calculateDistance(mCPLatitude, mCPLongitude);
        return result < CHECKPOINT_DISTANCE_THRESHOLD ? true : false;

//        if (results[0] < CHECKPOINT_DISTANCE_THRESHOLD) {
//            //toSpeech.speak("Checkpoint scanned successfully. Move to next checkpoint", TextToSpeech.QUEUE_FLUSH, null);
//            //Toast.makeText(PatrollingLocActivity.this, "Scanned Successfully " + "Distance: " + results[0], Toast.LENGTH_LONG).show();
//            //startService(new Intent(PatrollingLocActivity.this, SGPatrollingService.class));
//        } else {
//            toSpeech.speak("You are out of checkpoint location", TextToSpeech.QUEUE_FLUSH, null);
//            stopService(new Intent(PatrollingLocActivity.this, SGPatrollingService.class));
//            Toast.makeText(PatrollingLocActivity.this, "Out of Checkpoint " + "Distance: " + results[0], Toast.LENGTH_LONG).show();
//        }
    }

    private float calculateDistance(Double mCPLatitude, Double mCPLongitude) {
        float[] results = new float[2];
        //calculateWifiSignalWeightage();
        Location.distanceBetween(mPredictedLocation.getLatitude(), mPredictedLocation.getLongitude(), mCPLatitude, mCPLongitude, results);
        //Toast.makeText(PatrollingLocActivity.this, "Distance: " + results[0], Toast.LENGTH_LONG).show();
        return results[0];
    }

    private float calculateWifiSignalWeightage(String surr) {

        JSONArray mSurroundings = new JSONArray();
        int weightage = 0;
        int totalSignalsMatched = 0;
        int totalLevelsMatched = 0;

        if (surr != "" && surr != null) {
            try {
                mSurroundings = new JSONArray(surr);

            } catch (Exception e) {

            }
        }
        /**
         * 0: NO_SIGNAL: NO WIFI AVAILABLE
         * 1: WEAK
         * 2: FAIR
         * 3: GOOD
         * 4: EXCELLENT
         *
         * Excellent >-50 dBm
         *
         * Good -50 to -60 dBm
         *
         * Fair -60 to -70 dBm
         *
         * Weak < -70 dBm
         */

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

// Level of a Scan Result
        List<ScanResult> wifiList = wifiManager.getScanResults();
        Log.e("WIFILIST", "" + wifiList);


        for (int i = 0; i < mSurroundings.length(); i++) {
            try {
                JSONObject wifiObj = mSurroundings.getJSONObject(i);
                String cpBssId = wifiObj.getString("BSSID");
                int cpLevel = wifiObj.getInt("level");
                for (ScanResult scanResult : wifiList) {
                    Log.e("CURRENRT_LEVEL", "Prev " + scanResult.level + " out of 5");

                    String localBSSID = scanResult.BSSID;
                    int localLevel = scanResult.level;

                    if (localBSSID.equalsIgnoreCase(cpBssId)) {
                        int localSignalStrength = WifiManager.calculateSignalLevel(localLevel, 5);
                        int cpSignalStrength = WifiManager.calculateSignalLevel(cpLevel, 5);
                        Log.e("OBTAINED_LEVEL", "Level is " + localSignalStrength + " out of 5");
                    }


                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


//// Level of current connection
//        int rssi = wifiManager.getConnectionInfo().getRssi();
//        int level = WifiManager.calculateSignalLevel(rssi, 5);
//        System.out.println("Level is " + level + " out of 5");

        return 0;
    }

    private void getCheckPoint(String[] patrolingdataList) {
        try {
            int mCPIdentifier = Integer.parseInt(patrolingdataList[4]);
            RetrofitClinet.Companion.getInstance()
                    .getCheckPointInfo(OYE247TOKEN, "" + mCPIdentifier)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new CommonDisposable<GetCheckPointResponse<CheckPointData>>() {
                        @Override
                        public void noNetowork() {

                        }

                        @Override
                        public void onErrorResponse(@NotNull Throwable e) {

                        }

                        @Override
                        public void onSuccessResponse(GetCheckPointResponse<CheckPointData> checkPointResponse) {
                            try {
                                if (checkPointResponse.getData().getCheckPointListByChkPntID() != null) {
                                    CheckPointData cpData = checkPointResponse.getData().getCheckPointListByChkPntID();
                                    parseCheckPoint(cpData);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(PatrollingLocActivity.this);
            }
        }, 5000);
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    protected void onDestroy() {
        try {

            if (toSpeech != null) {
                toSpeech.stop();
                toSpeech.shutdown();
            }
            if (locationUpdateReceiver != null) {
                unregisterReceiver(locationUpdateReceiver);
            }

            if (predictedLocationReceiver != null) {
                unregisterReceiver(predictedLocationReceiver);
            }
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }

       // removeGeofences();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent i = new Intent();
        setResult(RESULT_OK, i);
        finish();
    }

    private static class CustomViewFinderView extends ViewFinderView {
        public static final String TRADE_MARK_TEXT = "ZXing";
        public static final int TRADE_MARK_TEXT_SIZE_SP = 40;
        public final Paint PAINT = new Paint();

        public CustomViewFinderView(Context context) {
            super(context);
            init();
        }

        public CustomViewFinderView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        private void init() {
            PAINT.setColor(Color.WHITE);
            PAINT.setAntiAlias(true);
            float textPixelSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                    TRADE_MARK_TEXT_SIZE_SP, getResources().getDisplayMetrics());
            PAINT.setTextSize(textPixelSize);
            setSquareViewFinder(true);
        }

        @Override
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            // drawTradeMark(canvas);
        }

    }

    private final class CoordinateFromString {
        Double lat = 0.0, lon = 0.0;

        public CoordinateFromString(String locationStr) {
            if (locationStr.contains(" ")) {
                String[] locationStrArray = locationStr.split(" ");
                if (locationStrArray.length == 2) {
                    String latStr = locationStrArray[0].trim();
                    String lonStr = locationStrArray[1].trim();
                    this.lat = Double.parseDouble(latStr);
                    this.lon = Double.parseDouble(lonStr);
                }
            }
        }

        public Double getLat() {
            return lat;
        }

        public void setLat(Double lat) {
            this.lat = lat;
        }

        public Double getLon() {
            return lon;
        }

        public void setLon(Double lon) {
            this.lon = lon;
        }
    }
}