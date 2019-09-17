package com.oyespace.guards;

import android.content.Context;


import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.oyespace.guards.utils.Prefs;
import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import timber.log.Timber;

/**
 * Created by Kalyan on 4/19/2017.
 */

public class Myapp extends MultiDexApplication {
    private static Myapp mInstance;


    private static Context mApplicationContext;

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
                .migration(new RealmDataMigration())
                .build();
        Realm.setDefaultConfiguration(config);

        mInstance = this;

//        Thread.setDefaultUncaughtExceptionHandler (new Thread.UncaughtExceptionHandler()
//        {
//            @Override
//            public void uncaughtException (Thread thread, Throwable e)
//            {
//                handleUncaughtException (thread, e);
//            }
//        });

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
//    private void handleUncaughtException (Thread thread, Throwable e) {
//
//        // The following shows what I'd like, though it won't work like this.
//        Intent intent = new Intent (getApplicationContext(), SplashActivity.class);
//        startActivity(intent);
//
//        // Add some code logic if needed based on your requirement
//    }


}
