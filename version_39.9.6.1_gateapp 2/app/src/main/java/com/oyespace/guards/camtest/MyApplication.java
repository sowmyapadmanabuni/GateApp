package com.oyespace.guards.camtest;

import android.app.Application;
import android.text.TextUtils;

/**
 * Created by SONU on 05/04/16.
 */
public class MyApplication extends Application {

    public static final String TAG = MyApplication.class
            .getSimpleName();
    private static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }
}




  /*class VolleySingleton extends MyApplication {







    private RequestQueue mRequestQueue;

    private static VolleySingleton mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized VolleySingleton getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }


}
*/