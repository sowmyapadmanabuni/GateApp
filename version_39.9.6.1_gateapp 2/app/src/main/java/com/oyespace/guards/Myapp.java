package com.oyespace.guards;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.oyespace.guards.broadcastreceiver.NetworkChangeReceiver;
import com.oyespace.guards.com.oyespace.guards.pojo.SOSModel;
import com.oyespace.guards.models.CheckPointMultipleScanRequest;
import com.oyespace.guards.models.CheckPointScanRealm;
import com.oyespace.guards.network.CommonDisposable;
import com.oyespace.guards.network.RetrofitClinet;
import com.oyespace.guards.pojo.CheckPointMultipleScanResponse;
import com.oyespace.guards.pojo.CheckPointScanRequest;
import com.oyespace.guards.pojo.CheckPointScanResponse;
import com.oyespace.guards.utils.ConstantUtils;
import com.oyespace.guards.utils.Prefs;
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import timber.log.Timber;

import static com.oyespace.guards.utils.ConstantUtils.ASSOCIATION_ID;
import static com.oyespace.guards.utils.ConstantUtils.BSR_Action;
import static com.oyespace.guards.utils.ConstantUtils.GATE_DEVICE_ID;
import static com.oyespace.guards.utils.ConstantUtils.GATE_NO;
import static com.oyespace.guards.utils.ConstantUtils.MEDIA_URL;
import static com.oyespace.guards.utils.ConstantUtils.OYE247TOKEN;
import static com.oyespace.guards.utils.ConstantUtils.PATROLLING_HIDDEN_SELFIE;

/**
 * Created by Kalyan on 4/19/2017.
 */

public class Myapp extends MultiDexApplication implements InternetConnectivityListener {
    private static Myapp mInstance;


    private static Context mApplicationContext;
    private InternetAvailabilityChecker mInternetAvailabilityChecker;
    public int internetCounter = 0;

//    public static Handler sHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate() {
//
//        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//                .detectDiskReads()
//                .detectDiskWrites()
//                .detectNetwork()
//                .penaltyLog()
//                .build());


        super.onCreate();


        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        this.registerReceiver(new NetworkChangeReceiver(), intentFilter);

        FirebaseApp.initializeApp(mApplicationContext);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

       // Fabric.with(this, new Crashlytics());
        Fabric.with(this, new Crashlytics());
        Timber.plant(new Timber.DebugTree());
        mApplicationContext = getApplicationContext();
        Prefs.initPrefs(getApplicationContext());


        CrashlyticsCore crashlyticsCore = new CrashlyticsCore.Builder()
                .disabled(BuildConfig.DEBUG)
                .build();
        Fabric.with(this, new Crashlytics.Builder().core(crashlyticsCore).build());
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().name("oysepace.realm")
                .schemaVersion(2)
//                .migration(new RealmDataMigration())
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.where(SOSModel.class).findAll().deleteAllFromRealm();
        realm.commitTransaction();
        mInstance = this;

//        Thread.setDefaultUncaughtExceptionHandler (new Thread.UncaughtExceptionHandler()
//        {
//            @Override
//            public void uncaughtException (Thread thread, Throwable e)
//            {
//                handleUncaughtException (thread, e);
//            }
//        });

        initInternetChecker();

    }

    private void initInternetChecker(){
        InternetAvailabilityChecker.init(this);
        mInternetAvailabilityChecker = InternetAvailabilityChecker.getInstance();
        mInternetAvailabilityChecker.addInternetConnectivityListener(this);
    }


    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }


    public static Context getContext() {
        return mApplicationContext;
    }


    public static final String TAG = Myapp.class
            .getSimpleName();


    public static synchronized Myapp getInstance() {
        return mInstance;
    }

    @Override
    public void onInternetConnectivityChanged(boolean isConnected) {

        if(isConnected) {
            Toast.makeText(mApplicationContext, "Internet is back", Toast.LENGTH_SHORT).show();
//            Intent offlineSync = new Intent(mApplicationContext,BackgroundSyncReceiver.class);
//            offlineSync.putExtra(BSR_Action, ConstantUtils.BGS_OFFLINE_SYNC);
//            mApplicationContext.sendBroadcast(offlineSync);
            Realm realm = Realm.getDefaultInstance();
            RealmResults<CheckPointScanRealm> scannedResults = realm.where(CheckPointScanRealm.class).findAll();
            List<CheckPointScanRealm> ss = new ArrayList();
            ss.addAll(scannedResults);
            Log.e("CheckPointScanRealm",""+ss);
            Log.e("CheckPointScanRealm",""+ss.size());

            if (ss.size() > 0) {
                ArrayList<CheckPointScanRequest> checkPointScanRequest = new ArrayList<>();
                for(int i=0;i<ss.size();i++){

                    //    val TRGPSPnt:String,
                    //    val CPCkPName:String,
                    //    val TRImage:String,
                    //    val deId:Int,
                    //    val CPCPntAt:String
                    CheckPointScanRequest scanRequest = new CheckPointScanRequest(
                            ss.get(i).getASAssnID(),
                            ss.get(i).getWKWorkID(),
                            ss.get(i).getWkfName(),
                            ss.get(i).getPSPtrlSID(),
                            ss.get(i).getTRTDateT(),
                            ss.get(i).getTRGPSPnt(),
                            ss.get(i).getCPCkPName(),
                            ss.get(i).getTRImage(),
                            ss.get(i).getDeId(),
                            ss.get(i).getCPCPntAt()
                    );
                    checkPointScanRequest.add(scanRequest);

                }


                CheckPointMultipleScanRequest multipleScanRequest = new CheckPointMultipleScanRequest(
                        ss.get(0).getASAssnID(),
                        ss.get(0).getWKWorkID(),
                        ss.get(0).getWkfName(),
                        ss.get(0).getPSPtrlSID(),
                        ss.get(0).getTRImage(),
                        ss.get(0).getDeId(), checkPointScanRequest
                );
                Log.e("CheckPointScanRealm",""+multipleScanRequest);
                String gson = new Gson().toJson(multipleScanRequest);
                Log.e("CheckPointScanRealm",""+gson);
                try {
                    RetrofitClinet.Companion.getInstance()
                            .scanMultipleCheckPoint(OYE247TOKEN, multipleScanRequest)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new CommonDisposable<CheckPointMultipleScanResponse>() {

                                @Override
                                public void noNetowork() {
                                    Log.e("sendScannedCheckPoint", "No Netwrok");
                                    // showAnimatedDialog("No Internet Connectivity", R.raw.error, true, "OK");
                                }

                                @Override
                                public void onErrorResponse(@NotNull Throwable e) {
                                    Log.e("SCNANNED_ERR", "" + e);


                                }

                                @Override
                                public void onSuccessResponse(CheckPointMultipleScanResponse checkPointScanResponse) {
                                    Log.e("SCNANNED_NETWR", "" + checkPointScanResponse);
                                    try {
                                        if (!realm.isInTransaction()) {
                                            realm.beginTransaction();
                                        }
                                        realm.delete(CheckPointScanRealm.class);
                                        if (realm.isInTransaction()) {
                                            realm.commitTransaction();
                                        }
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

    }


//    private void handleUncaughtException (Thread thread, Throwable e) {
//
//        // The following shows what I'd like, though it won't work like this.
//        Intent intent = new Intent (getApplicationContext(), SplashActivity.class);
//        startActivity(intent);
//
//        // Add some code logic if needed based on your requirement
//    }


}
