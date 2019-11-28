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
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.zxing.Result;
import com.oyespace.guards.CapPhoto;
import com.oyespace.guards.R;
import com.oyespace.guards.SGPatrollingService;
import com.oyespace.guards.activity.BaseKotlinActivity;
import com.oyespace.guards.broadcastreceiver.GeofenceBroadcastReceiver;
import com.oyespace.guards.models.CheckPointsOfSheduleListResponse;
import com.oyespace.guards.models.PatrolShift;
import com.oyespace.guards.models.ScheduleCheckPointsData;
import com.oyespace.guards.network.CommonDisposable;
import com.oyespace.guards.network.RetrofitClinet;
import com.oyespace.guards.pojo.CheckPointData;
import com.oyespace.guards.pojo.CheckPointScanRequest;
import com.oyespace.guards.pojo.CheckPointScanResponse;
import com.oyespace.guards.pojo.GetCheckPointResponse;
import com.oyespace.guards.utils.ConstantUtils;
import com.oyespace.guards.utils.Prefs;
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.core.ViewFinderView;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static com.oyespace.guards.utils.ConstantUtils.ACTIVE_PATROLLING_LAST_CP;
import static com.oyespace.guards.utils.ConstantUtils.ACTIVE_PATROLLING_LAST_TIME;
import static com.oyespace.guards.utils.ConstantUtils.ACTIVE_PATROLLING_SCHEDULE;
import static com.oyespace.guards.utils.ConstantUtils.ASSOCIATION_ID;
import static com.oyespace.guards.utils.ConstantUtils.CHECKPOINT_DISTANCE_THRESHOLD;
import static com.oyespace.guards.utils.ConstantUtils.CHECKPOINT_TYPE_END;
import static com.oyespace.guards.utils.ConstantUtils.CHECKPOINT_TYPE_START;
import static com.oyespace.guards.utils.ConstantUtils.GATE_DEVICE_ID;
import static com.oyespace.guards.utils.ConstantUtils.GATE_NO;
import static com.oyespace.guards.utils.ConstantUtils.MEDIA_URL;
import static com.oyespace.guards.utils.ConstantUtils.OYE247TOKEN;
import static com.oyespace.guards.utils.ConstantUtils.PATROLLING_COMPLETED_ON;
import static com.oyespace.guards.utils.ConstantUtils.PATROLLING_HIDDEN_SELFIE;
import static com.oyespace.guards.utils.ConstantUtils.PATROLLING_SCHEDULE_ID;
import static com.oyespace.guards.utils.ConstantUtils.PERSON_PHOTO;

public class PatrollingLocActivity extends BaseKotlinActivity implements ZXingScannerView.ResultHandler, OnCompleteListener<Void>, View.OnClickListener, OnLocationUpdate, InternetConnectivityListener {

    public LocationService locationService;
    public Location mLocation, mPredictedLocation;
    public TextToSpeech toSpeech;
    private Button mPauseBtn, mStopBtn;
    private TextView mScanTitle, mAccuracyText, mSatellitesText, mLocationTimeText;
    private ImageView mGPSIcon;
    public int scheduleId;
    static final float GEOFENCE_RADIUS_IN_METERS = 40;
    private static final String PACKAGE_NAME = "com.google.android.gms.location.Geofence";
    static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";
    private static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;
    static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;
    private BroadcastReceiver locationUpdateReceiver;
    private BroadcastReceiver predictedLocationReceiver;
    private ZXingScannerView mScannerView;
    private boolean isScanEnabled =false;
    private int mActiveSchedule = 0;
    private boolean isPlayingSiren = false;
    ArrayList<ScheduleCheckPointsData> scheduleCheckPoints = new ArrayList<ScheduleCheckPointsData>();
    private GPSTracker gpsTracker;
    private Timer gpsTimeTimer;
    private long currentLocationAge = 0;
    private float currentLocationAccuracy = 0;
    private int currentSatelliteCount = 0;
    private  InternetAvailabilityChecker mInternetAvailabilityChecker;

    @Override
    public void onGPSLocationUpdate(Location location) {
        mPredictedLocation = location;
        mLocation = location;
        setSatellitesAccuracy();
    }

    @Override
    public void onInternetConnectivityChanged(boolean isConnected) {
        if(!isConnected){
            showAnimatedDialog("No Internet Connectivity..",R.raw.error_alert,true,"OK");
        }
    }


    private enum PendingGeofenceTask {
        ADD, REMOVE, NONE
    }

    private GeofencingClient mGeofencingClient;
    private ArrayList<Geofence> mGeofenceList = new ArrayList<>();
    private PendingIntent mGeofencePendingIntent;
    private PendingGeofenceTask mPendingGeofenceTask = PendingGeofenceTask.NONE;
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
    protected void onStart() {
        Prefs.putBoolean(ConstantUtils.ACTIVE_ALERT, true);
        super.onStart();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_scanner_point);

        InternetAvailabilityChecker.init(this);
        mInternetAvailabilityChecker = InternetAvailabilityChecker.getInstance();
        mInternetAvailabilityChecker.addInternetConnectivityListener(this);

        initSpeech();
        mActiveSchedule = getIntent().getIntExtra(PATROLLING_SCHEDULE_ID,0);
        getScheduleCheckPoints();

        byte[] wrrw = getIntent().getByteArrayExtra(PERSON_PHOTO);
        if(wrrw != null){
            //Log.e("IMAGE_CAPTURED",""+wrrw);
        }
        mAccuracyText = findViewById(R.id.text_accuracy);
        mSatellitesText = findViewById(R.id.text_satellites);
        mLocationTimeText = findViewById(R.id.text_location_time);
        mGPSIcon = findViewById(R.id.gps_stat);
        mPauseBtn = findViewById(R.id.btn_pause);
        mStopBtn = findViewById(R.id.btn_stop);
        mStopBtn.setVisibility(View.GONE);
        mScanTitle = findViewById(R.id.toolbar);


        startLocationListener();
        initScanner();



        int ongoingSchedule = Prefs.getInt(ACTIVE_PATROLLING_SCHEDULE, -1);
        int lastScannedCP = Prefs.getInt(ACTIVE_PATROLLING_LAST_CP, -1);

        if(ongoingSchedule == mActiveSchedule && ongoingSchedule != -1){
            startSiren();
           // showPendingWarning();
        }

        //mGeofencePendingIntent = null;
        //mGeofencingClient = LocationServices.getGeofencingClient(this);
        //populateGeofenceList();
        //addGeofences();

        mPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPatrollingLastSeen();
                onBackPressed();
            }
        });

        mStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSiren();
                resetSavedCheckpoints();
                setNextCheckPointLabel();
            }
        });
    }

    private long setLocationTime(){
        long age = getLocationAge(mPredictedLocation)/1000;
        currentLocationAge = age;
        currentLocationAccuracy = getAccuracy(mPredictedLocation);
        currentSatelliteCount = getAvailableSatellites(mPredictedLocation);

        String ageString = ""+age+" seconds";
        if(age>60){
            long ageMinutes = age/60;
            ageString = ""+ageMinutes+" minutes";
        }
        mLocationTimeText.setText("Same location for "+ageString);
        if(age == 15 || age == 30){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(PatrollingLocActivity.this,"Requesting GPS location",Toast.LENGTH_SHORT).show();
                    gpsTracker.getLocation();
                }
            });


        }
        return age;
    }
    private void setSatellitesAccuracy(){
        try {
            setLocationTime();
            //int satellites = getAvailableSatellites(mPredictedLocation);
            //float accuracy = getAccuracy(mPredictedLocation);

            mSatellitesText.setText("Satellites: " + currentSatelliteCount);
            mAccuracyText.setText("Accuracy: " + currentLocationAccuracy + "m");
            ;

            if ((currentSatelliteCount > 4 && currentLocationAge < 15) || (currentSatelliteCount <=4 && currentLocationAccuracy < 15)) {
                //mGPSIcon.setImageDrawable(getResources().getDrawable(R.drawable.gps_online));
                Glide.with(this).load(R.drawable.gps_online).into(mGPSIcon);
            } else {
                Glide.with(this).load(R.drawable.gps_offline).into(mGPSIcon);
                //mGPSIcon.setImageDrawable(getResources().getDrawable(R.drawable.gps_offline));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void setNextCheckPointLabel(){
        //-1 = Error
        //0 = No more checkpoints (Reached Last Checkpoint)
        int lastCheckPoint = Prefs.getInt(ACTIVE_PATROLLING_LAST_CP, -1);
        //Log.e("setNextCheckPointLabel",""+lastCheckPoint);
        if(scheduleCheckPoints.size() > 0) {
            //Log.e("setNextCheckPointLabel","Size > 0");
            if (lastCheckPoint == -1) {
                mScanTitle.setText("Scan First Checkpoint\n"+scheduleCheckPoints.get(0).getChecks().get(0).getCpCkPName());
                toSpeech.speak("Scan First Checkpoint", TextToSpeech.QUEUE_FLUSH, null);
            } else {
                //Log.e("setNextCheckPointLabel","is not -1");
                    boolean isCheckPointFound = false;

                    int currentIndex = -1;
                    int nextCheckPoint = -1;
                    for (int i = 0; i < scheduleCheckPoints.size(); i++) {
                        ScheduleCheckPointsData cp = scheduleCheckPoints.get(i);
                        //Log.e("setNextCheckPointLabel",""+cp.getPsChkPID()+" - "+lastCheckPoint);
                        if (cp.getPsChkPID() == lastCheckPoint) {
                            //Log.e("setNextCheckPointLabel","Found "+i);
                            currentIndex = i;
                            isCheckPointFound = true;
                            break;
                        }
                    }
                    //Log.e("setNextCheckPointLabel",""+isCheckPointFound+" - "+currentIndex);
                    if (isCheckPointFound) {
                        if (scheduleCheckPoints.size() == currentIndex + 1) {
                            //Log.e("setNextCheckPointLabel","Next_LAST "+scheduleCheckPoints.get(0).getChecks().get(0).getCpCkPName());
                            mScanTitle.setText("Scan First Checkpoint\n"+scheduleCheckPoints.get(0).getChecks().get(0).getCpCkPName());
                            toSpeech.speak("Scan First Checkpoint", TextToSpeech.QUEUE_FLUSH, null);
                        } else {
                            try {
                                //Log.e("setNextCheckPointLabel","Next_ELSE "+scheduleCheckPoints.get(currentIndex + 1).getChecks().get(0).getCpCkPName());
                                mScanTitle.setText("Scan Checkpoint\n"+scheduleCheckPoints.get(currentIndex + 1).getChecks().get(0).getCpCkPName());
                                toSpeech.speak("Scan Next Checkpoint", TextToSpeech.QUEUE_FLUSH, null);
                            } catch (Exception e) {
                                e.printStackTrace();
                                mScanTitle.setText("Scan Checkpoint\n"+scheduleCheckPoints.get(0).getChecks().get(0).getCpCkPName());
                                toSpeech.speak("Scan Next Checkpoint", TextToSpeech.QUEUE_FLUSH, null);
                                //return 0;
                            }
                        }
                    } else {
                        //return -1;
                        //showAnimatedDialog("Checkpoint not found. Please go to starting point",R.raw.error_alert,true,"OK");
                    }
                }


        }
    }

    private long getLocationAge(Location newLocation) {
        long locationAge;
        if (Build.VERSION.SDK_INT >= 17) {
            long currentTimeInMilli = SystemClock.elapsedRealtimeNanos() / 1000000;
            long locationTimeInMilli = newLocation.getElapsedRealtimeNanos() / 1000000;
            locationAge = currentTimeInMilli - locationTimeInMilli;
        } else {
            locationAge = System.currentTimeMillis() - newLocation.getTime();
        }
        return locationAge;
    }

    private int getAvailableSatellites(Location newLocation) {
        return newLocation.getExtras().getInt("satellites",0);
    }
    private float getAccuracy(Location newLocation) {
        return newLocation.getAccuracy();
    }

    private void checkDrawOverWindowPermission(){
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 1234);
            }else{
                startHiddenCamera();
            }
        } else {
            startHiddenCamera();
        }
    }

    private void resetSavedCheckpoints(){
        Prefs.remove(ACTIVE_PATROLLING_SCHEDULE);
        Prefs.remove(ACTIVE_PATROLLING_LAST_CP);
        Prefs.remove(ACTIVE_PATROLLING_LAST_TIME);
    }

    private void startHiddenCamera(){
        String imgName = "Selfie" + "Association" + Prefs.getInt(
                ASSOCIATION_ID,
                0
        ) + "GUARD_PATROLLING" + Prefs.getString(ConstantUtils.GATE_NO, "") + System.currentTimeMillis() + ".jpg";

        Intent front_translucent =  new Intent(getBaseContext(), CapPhoto.class);
        front_translucent.putExtra("Front_Request", true);
        front_translucent.putExtra("ImageName",imgName);

        // front_translucent.putExtra("Quality_Mode", camCapture.getQuality());
        getApplication().getApplicationContext().startService(
                front_translucent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1234) {
           startHiddenCamera();
        }
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
                            showAnimatedDialog("No internet connectivity", R.raw.error, true, "OK");
                        }

                        @Override
                        public void onErrorResponse(@NotNull Throwable e) {
                            dismissProgressrefresh();
                           // showAnimatedDialog("No internet connectivity", R.raw.error, true, "OK");
                        }

                        @Override
                        public void onSuccessResponse(CheckPointsOfSheduleListResponse<ArrayList<PatrolShift>> checkPointResponse) {
                            try {
                                dismissProgressrefresh();
                                //Log.e("getScheduleCheckPoints",""+checkPointResponse.getData().getCheckPointsBySchedule());
                                if(checkPointResponse.getSuccess() && checkPointResponse.getData().getCheckPointsBySchedule().size()>0){
                                    isScanEnabled = true;
                                    PatrolShift patrolShift = checkPointResponse.getData().getCheckPointsBySchedule().get(0);
                                    scheduleCheckPoints = patrolShift.getPoint();
                                    setNextCheckPointLabel();

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
                setNextCheckPointLabel();
                stopSiren();
                Toast.makeText(PatrollingLocActivity.this,"Please go to the first checkpoint",Toast.LENGTH_LONG).show();
                dialog.cancel();
            }
        });
        alertBuilder.setNegativeButton("Continue", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
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

    private GeofencingRequest getGeofencingRequest() {
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
        gpsTracker = new GPSTracker(this,this::onGPSLocationUpdate);
        mLocation = gpsTracker.getLocation();
        mPredictedLocation = mLocation;
        setSatellitesAccuracy();
        startLocationTimeCheck();

        locationUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Location newLocation = intent.getParcelableExtra("location");
                //Log.e("newLocation", "" + newLocation);
                //Log.e("mPredictedLocation", "" + mPredictedLocation);
//                if (mPredictedLocation == null) {
//                    mPredictedLocation = newLocation;
//                }
//                mLocation = newLocation;
                //loc.setText(""+newLocation.getLatitude()+" , "+newLocation.getLongitude()+", Acc: "+newLocation.getAccuracy());
            }
        };

        predictedLocationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Location predictedLocation = intent.getParcelableExtra("location");
                //Log.e("predictedLocation", "" + predictedLocation);
//                mPredictedLocation = predictedLocation;
//                setSatellitesAccuracy();
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

        ViewGroup contentFrame = findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this) {
            @Override
            protected IViewFinder createViewFinderView(Context context) {
                return new CustomViewFinderView(context);
            }
        };
        contentFrame.addView(mScannerView);
    }

    private void startLocationTimeCheck(){
        try {
            if (mPredictedLocation != null) {
                if(gpsTimeTimer == null) {
                    gpsTimeTimer = new Timer();
                }
                gpsTimeTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        setLocationTime();
                    }

                }, 0, 1000);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
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
        //Log.e("SCANNED",""+result);
        //Toast.makeText(PatrollingLocActivity.this,"Reached result",Toast.LENGTH_SHORT).show();
        try {
            String qrResultTemp = result.getText();
            String[] qrCPDataTemp = qrResultTemp.split(",");

            if (qrCPDataTemp.length > 4) {
                mScannerView.stopCamera();
                toSpeech.speak("Calibrating.. Please wait..", TextToSpeech.QUEUE_FLUSH, null);
                showAnimatedDialog("Calibrating.. Please wait..", R.raw.gps, false, "");
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hideAnimatedDialog();
                        String patrolingdata = result.getText();
                        String[] patrolingdataList = patrolingdata.split(",");
                        mScannerView.startCamera();
                        if (patrolingdataList.length > 4) {
                            getCheckPoint(patrolingdataList);
                        }else{

                        }
                    }
                }, 6000);
            }else{
                mScannerView.resumeCameraPreview(this);
            }
        }catch (Exception e){
            e.printStackTrace();
            mScannerView.resumeCameraPreview(this);
        }

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

            //Log.e("mCurrentAssociation", "" + mCurrentAssociation);

            if (mCurrentAssociation != 0 && mCurrentAssociation == mCPAssociation) {
                //gpsTracker.getLocation();
                //long age = getLocationAge(mPredictedLocation);
                //int satellites = getAvailableSatellites(mPredictedLocation);
                //float accuracy = getAccuracy(mPredictedLocation);
                //setSatellitesAccuracy();
                boolean hasInternet = mInternetAvailabilityChecker.getCurrentInternetAvailabilityStatus();
//                if(hasInternet) {
//                    if (currentSatelliteCount > 4) {
                        //if(currentLocationAccuracy < 8){
                        //if (currentLocationAge < 15) {
                        isValidCheckPoint(qrCheckpoint);
//                        }else{
//                            showAnimatedDialog("Same GPS location from last 15 seconds", R.raw.error, true, "OK");
//                            gpsTracker.getLocation();
//                        }
//                    } else {
//                        showAnimatedDialog("Signal accuracy is very low", R.raw.error, true, "OK");
//                        gpsTracker.getLocation();
//                    }
//                    } else {
////                    String msg = "No Satellites found. Unable to calculate location";
////                    if(currentSatelliteCount > 0){
////                        msg = "Only "+currentSatelliteCount+" Satellites found. Unable to calculate location";
////                    }
////                    showAnimatedDialog(msg, R.raw.error, true, "OK");
////                    gpsTracker.getLocation();
//
//                        if (currentLocationAccuracy < 15) {
//                            isValidCheckPoint(qrCheckpoint);
//                        } else {
//                            String msg = "Low location accuracy. Please try again";
//                            showAnimatedDialog(msg, R.raw.error, true, "OK");
//                            gpsTracker.getLocation();
//                        }
//                    }
//                }else{
//                    showAnimatedDialog("Poor connectivity", R.raw.error_alert, true, "OK");
//                }

            } else {
               // showAnimatedDialog("Wrong QR Code.", R.raw.error, true, "OK");
            }

            //Log.e("PATROLL", "Patroling Data " + qrCheckpoint);
            //Log.e("PARSED", " " + mCPLatitude + " - " + mCPLongitude);


            onResume();
        } catch (Exception e) {
            e.printStackTrace();
            onResume();
        }
    }

    private void setPatrollingLastSeen(){
        Date currentDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String dateString = format.format(currentDate);
        Prefs.putString(ACTIVE_PATROLLING_LAST_TIME, dateString);
        setNextCheckPointLabel();
    }

    private boolean isValidCheckPoint(CheckPointData checkPointData) {
        int ongoingSchedule = Prefs.getInt(ACTIVE_PATROLLING_SCHEDULE, -1);
        int lastScannedCP = Prefs.getInt(ACTIVE_PATROLLING_LAST_CP, -1);
        String mCPType = checkPointData.getCpcPntAt();
        String locationStr = checkPointData.getCpgpsPnt();
        CoordinateFromString coordinate = new CoordinateFromString(locationStr);
        //Log.e("CHECKPOIINT:: ", "" + checkPointData);
        //calculateWifiSignalWeightage(checkPointData.getCpSurrName());
        if (ongoingSchedule == -1) {
            //Fresh Start
            int nextCP = getNextCheckPoint(-1);
            //Log.e("NEXT_CHECKPOINT",""+nextCP);
            if(nextCP==checkPointData.getCpChkPntID()){
                if (mCPType.equals(CHECKPOINT_TYPE_START)) {

                    if (isValidDistance(coordinate.getLat(), coordinate.getLon())) {
                        Prefs.putInt(ACTIVE_PATROLLING_SCHEDULE, mActiveSchedule);
                        Prefs.putInt(ACTIVE_PATROLLING_LAST_CP, checkPointData.getCpChkPntID());
                        setPatrollingLastSeen();
                        toSpeech.speak("Checkpoint Scanned Successfully", TextToSpeech.QUEUE_ADD, null);
                        showAnimatedDialog("Checkpoint Scanned Successfully", R.raw.done, true, "OK");
                        startSiren();
                        sendScannedCheckPoint(checkPointData);
                        return true;
                    } else {
                        toSpeech.speak("You are out of checkpoint location", TextToSpeech.QUEUE_FLUSH, null);
                        showAnimatedDialog("Out of checkpoint location", R.raw.error, true, "OK");
                        return false;
                    }
                }else {
                    toSpeech.speak("You missed the starting checkpoint", TextToSpeech.QUEUE_FLUSH, null);
                    showAnimatedDialog("Wrong Starting Checkpoint", R.raw.error, true, "OK");
                    resetSavedCheckpoints();
                    return false;
                }
            }
            else {
                toSpeech.speak("You are scanning the wrong checkpoint", TextToSpeech.QUEUE_FLUSH, null);
                showAnimatedDialog("Wrong Checkpoint", R.raw.error, true, "OK");
                return false;
            }

        } else {
            if(!isPlayingSiren){
                startSiren();
            }
            int nextCP = getNextCheckPoint(lastScannedCP);
            //Log.e("NEXT_CHECKPOINT_SEC",""+nextCP);
            if (nextCP == checkPointData.getCpChkPntID() && !mCPType.equalsIgnoreCase(CHECKPOINT_TYPE_START)) {

                if (isValidDistance(coordinate.getLat(), coordinate.getLon())) {
                    //toSpeech.speak("VALID", TextToSpeech.QUEUE_FLUSH, null);
                    if (mCPType.equalsIgnoreCase(CHECKPOINT_TYPE_END)) {
                        Prefs.putInt(ACTIVE_PATROLLING_LAST_CP, checkPointData.getCpChkPntID());
                        setNextCheckPointLabel();
                        resetSavedCheckpoints();
                        toSpeech.speak("Patrolling Completed", TextToSpeech.QUEUE_ADD, null);
                        showAnimatedDialog("Patrolling Completed", R.raw.done, true, "OK");
                        Prefs.putString(PATROLLING_COMPLETED_ON+mActiveSchedule,new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(new Date()));
                        stopSiren();
                    }else{
                        Prefs.putInt(ACTIVE_PATROLLING_SCHEDULE, mActiveSchedule);
                        Prefs.putInt(ACTIVE_PATROLLING_LAST_CP, checkPointData.getCpChkPntID());
                        setPatrollingLastSeen();
                        toSpeech.speak("Checkpoint Scanned Successfully", TextToSpeech.QUEUE_ADD, null);
                        showAnimatedDialog("Checkpoint Scanned Successfully", R.raw.done, true, "OK");
                    }
                    sendScannedCheckPoint(checkPointData);
                    return true;
                } else {
                    toSpeech.speak("You are out of checkpoint location", TextToSpeech.QUEUE_ADD, null);
                    showAnimatedDialog("Out of checkpoint location", R.raw.error, true, "OK");
                    return false;
                }

            }else if(nextCP == -1){
                toSpeech.speak("Please scan from first checkpoint", TextToSpeech.QUEUE_ADD, null);
                resetSavedCheckpoints();
                showAnimatedDialog("Something went wrong. Start from beginning", R.raw.error, true, "OK");
                finish();
                return false;
            }
            else {
                toSpeech.speak("You are scanning the wrong checkpoint", TextToSpeech.QUEUE_ADD, null);
                showAnimatedDialog("Missed a checkpoint", R.raw.error, true, "OK");
                return false;
            }

        }


    }

    private void startSiren(){
        startService(new Intent(PatrollingLocActivity.this, SGPatrollingService.class));
        isPlayingSiren = true;
    }

    private void stopSiren(){
        stopService(new Intent(PatrollingLocActivity.this, SGPatrollingService.class));
        isPlayingSiren = false;
    }

    private int getNextCheckPoint(int lastCheckPoint) {

        //-1 = Error
        //0 = No more checkpoints (Reached Last Checkpoint)

        if(scheduleCheckPoints.size() > 0){
            boolean isCheckPointFound = false;
            if(lastCheckPoint == -1){
                return scheduleCheckPoints.get(0).getPsChkPID();
            }

            int currentIndex = -1;
            int nextCheckPoint = -1;
            for(int i=0;i<scheduleCheckPoints.size();i++){
                ScheduleCheckPointsData cp = scheduleCheckPoints.get(i);
                if(cp.getPsChkPID() == lastCheckPoint){
                    currentIndex = i;
                    isCheckPointFound = true;
                    break;
                }
            }

            if(isCheckPointFound) {
                if (scheduleCheckPoints.size() == currentIndex + 1) {
                    return 0;
                } else {
                    try {
                        return scheduleCheckPoints.get(currentIndex + 1).getPsChkPID();
                    } catch (Exception e) {
                        e.printStackTrace();
                        return 0;
                    }
                }
            }else{
                return -1;
                //showAnimatedDialog("Checkpoint not found. Please go to starting point",R.raw.error_alert,true,"OK");
            }

        }

        return -1;
    }

    private boolean isValidDistance(Double mCPLatitude, Double mCPLongitude) {
        float result = calculateDistance(mCPLatitude, mCPLongitude);
        //return true;
        //Log.e("DISTANCE_LOC",""+result);
        Toast.makeText(this,"Distance: "+result,Toast.LENGTH_LONG).show();
        return true;//result <= CHECKPOINT_DISTANCE_THRESHOLD ? true : false;
    }

    private float calculateDistance(Double mCPLatitude, Double mCPLongitude) {
        float[] results = new float[2];
        //calculateWifiSignalWeightage();
        //Log.e("CALUCLAUTAING",""+mPredictedLocation.getLatitude()+" - "+mPredictedLocation.getLongitude()+" - "+ mCPLatitude+" - "+ mCPLongitude);
//        Location instantLocation = gpsTracker.getLocation();
//        if(instantLocation != null){
//            mPredictedLocation = instantLocation;
//        }


        Location.distanceBetween(mPredictedLocation.getLatitude(), mPredictedLocation.getLongitude(), mCPLatitude, mCPLongitude, results);
        //Toast.makeText(PatrollingLocActivity.this, "Distance: " + results[0], Toast.LENGTH_LONG).show();
        //Toast.makeText(this,"Age: "+getLocationAge(mPredictedLocation)+" & Ditance="+results[0],Toast.LENGTH_LONG).show();
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
        //Log.e("WIFILIST", "" + wifiList);


        for (int i = 0; i < mSurroundings.length(); i++) {
            try {
                JSONObject wifiObj = mSurroundings.getJSONObject(i);
                String cpBssId = wifiObj.getString("BSSID");
                int cpLevel = wifiObj.getInt("level");
                for (ScanResult scanResult : wifiList) {
                    //Log.e("CURRENRT_LEVEL", "Prev " + scanResult.level + " out of 5");

                    String localBSSID = scanResult.BSSID;
                    int localLevel = scanResult.level;

                    if (localBSSID.equalsIgnoreCase(cpBssId)) {
                        int localSignalStrength = WifiManager.calculateSignalLevel(localLevel, 5);
                        int cpSignalStrength = WifiManager.calculateSignalLevel(cpLevel, 5);
                        //Log.e("OBTAINED_LEVEL", "Level is " + localSignalStrength + " out of 5");
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
                            showAnimatedDialog("No Internet Connectivity", R.raw.error, true, "OK");
                        }

                        @Override
                        public void onErrorResponse(@NotNull Throwable e) {
                            //mScannerView.resumeCameraPreview(PatrollingLocActivity.this);
                           // showAnimatedDialog("No Internet Connectivity", R.raw.error, true, "OK");
                        }

                        @Override
                        public void onSuccessResponse(GetCheckPointResponse<CheckPointData> checkPointResponse) {
                            try {
                                //mScannerView.resumeCameraPreview(PatrollingLocActivity.this);
                                if (checkPointResponse.getData().getCheckPointListByChkPntID() != null) {
                                    CheckPointData cpData = checkPointResponse.getData().getCheckPointListByChkPntID();
                                    parseCheckPoint(cpData);
                                }else{
                                    showAnimatedDialog("Checkpoint does not exist",R.raw.error,true,"OK");
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

    private void sendScannedCheckPoint(CheckPointData checkPointData){

        CheckPointScanRequest scanRequest = new CheckPointScanRequest(
                Prefs.getInt(ASSOCIATION_ID,0),
                "",
                Prefs.getString(GATE_NO, ""),
                mActiveSchedule,
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),checkPointData.getCpgpsPnt(),
                checkPointData.getCpCkPName(),MEDIA_URL+Prefs.getString(PATROLLING_HIDDEN_SELFIE,""),Prefs.getInt(GATE_DEVICE_ID,0),checkPointData.getCpcPntAt());

        Log.e("scanRequest",""+scanRequest);
            RetrofitClinet.Companion.getInstance()
                .scanCheckPoint(OYE247TOKEN, scanRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new CommonDisposable<CheckPointScanResponse>() {

                    @Override
                    public void noNetowork() {
                        Log.e("sendScannedCheckPoint","No Netwrok");
                        showAnimatedDialog("No Internet Connectivity", R.raw.error, true, "OK");
                    }

                    @Override
                    public void onErrorResponse(@NotNull Throwable e) {
                        Log.e("SCNANNED_ERR",""+e);
                       // showAnimatedDialog("No Internet Connectivity", R.raw.error, true, "OK");
                    }

                    @Override
                    public void onSuccessResponse(CheckPointScanResponse checkPointScanResponse) {
                        Log.e("SCNANNED_NETWR",""+checkPointScanResponse);
                    }
                });
    }

    @Override
    public void onResume() {
        Prefs.putBoolean(ConstantUtils.ACTIVE_ALERT, true);

        byte[] wrrw = getIntent().getByteArrayExtra(PERSON_PHOTO);
        if(wrrw != null){
            //Log.e("IMAGE_CAPTURED_res",""+wrrw);
        }

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
        Prefs.putBoolean(ConstantUtils.ACTIVE_ALERT, false);
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    protected void onDestroy() {
        try {
            Prefs.putBoolean(ConstantUtils.ACTIVE_ALERT, false);
            mInternetAvailabilityChecker.removeInternetConnectivityChangeListener(this);
            stopSiren();
            if (toSpeech != null) {
                toSpeech.stop();
                toSpeech.shutdown();
            }
            if(gpsTimeTimer != null){
                gpsTimeTimer.cancel();
            }
            if (locationUpdateReceiver != null) {
                unregisterReceiver(locationUpdateReceiver);
            }

            if (predictedLocationReceiver != null) {
                unregisterReceiver(predictedLocationReceiver);
            }

            if(locationService != null){
                locationService.stopUpdatingLocation();
            }

            if(mLocation != null){
              //  mLocation.reset();
            }
        } catch (Exception ex) {
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