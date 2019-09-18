package com.oyespace.guards.pertroling;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
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
import android.speech.tts.TextToSpeech;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.zxing.Result;
import com.oyespace.guards.R;
import com.oyespace.guards.activity.BaseKotlinActivity;
import com.oyespace.guards.network.CommonDisposable;
import com.oyespace.guards.network.RetrofitClinet;
import com.oyespace.guards.pojo.CheckPointData;
import com.oyespace.guards.pojo.GetCheckPointResponse;
import com.oyespace.guards.utils.Prefs;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

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

public class PatrollingLocActivity extends BaseKotlinActivity implements ZXingScannerView.ResultHandler {

    public LocationService locationService;
    public Location mLocation, mPredictedLocation;
    public TextToSpeech toSpeech;
    public int scheduleId;
    private BroadcastReceiver locationUpdateReceiver;
    private BroadcastReceiver predictedLocationReceiver;
    private ZXingScannerView mScannerView;
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

        Prefs.remove(ACTIVE_PATROLLING_SCHEDULE);
        Prefs.remove(ACTIVE_PATROLLING_LAST_CP);

        startLocationListener();
        initScanner();
        initSpeech();


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
                showAnimatedDialog("Wrong QR Code.", R.raw.error, true, "OK");
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

            if (mCPType.equals(CHECKPOINT_TYPE_START)) {

                if (isValidDistance(coordinate.getLat(), coordinate.getLon())) {
                    //@Todo: Zero must replace with actual current schedule id.
                    Prefs.putInt(ACTIVE_PATROLLING_SCHEDULE, 0);
                    Prefs.putInt(ACTIVE_PATROLLING_LAST_CP, checkPointData.getCpChkPntID());
                    toSpeech.speak("Checkpoint Scanned Successfully", TextToSpeech.QUEUE_FLUSH, null);
                    showAnimatedDialog("Checkpoint Scanned Successfully", R.raw.done, true, "OK");
                    return true;
                } else {
                    toSpeech.speak("You are out of checkpoint location", TextToSpeech.QUEUE_FLUSH, null);
                    showAnimatedDialog("Out of checkpoint location", R.raw.error, true, "OK");
                    return false;
                }
            } else {
                toSpeech.speak("You missed the starting checkpoint", TextToSpeech.QUEUE_FLUSH, null);
                showAnimatedDialog("Wrong Starting Checkpoint", R.raw.error, true, "OK");
                return false;
            }

        } else {
            //@Todo: Get the last scanned checkpoint. Check the next checkpoint & validate
            //@Todo: Add this check in dashboard/FRTDB for automatic resume
            //@Todo: Check already scanned : Not in priority

            //@Todo: Uncomment the commented line
            int nextCP = checkPointData.getCpChkPntID();//getNextCheckPoint(lastScannedCP);
            if (nextCP == checkPointData.getCpChkPntID() && !mCPType.equalsIgnoreCase(CHECKPOINT_TYPE_START)) {

                if (isValidDistance(coordinate.getLat(), coordinate.getLon())) {
                    toSpeech.speak("VALID", TextToSpeech.QUEUE_FLUSH, null);
                    if (mCPType.equalsIgnoreCase(CHECKPOINT_TYPE_END)) {
                        Prefs.remove(ACTIVE_PATROLLING_SCHEDULE);
                        Prefs.remove(ACTIVE_PATROLLING_LAST_CP);
                        showAnimatedDialog("Patrolling Completed", R.raw.done, true, "OK");
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
        return 122;
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


        super.onDestroy();
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
