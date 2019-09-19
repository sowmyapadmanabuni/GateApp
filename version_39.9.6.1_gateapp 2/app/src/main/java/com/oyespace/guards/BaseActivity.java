package com.oyespace.guards;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.oyespace.guards.listeners.PermissionCallback;
import com.oyespace.guards.network.ResponseHandler;
import com.oyespace.guards.utils.Utils;

import java.util.ArrayList;

/**
 * Created by linuxy on 4/10/17.
 */

public class BaseActivity extends AppCompatActivity implements ResponseHandler, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final int LOCATION_REQ = 7446;

    private PermissionCallback callback;
    private int requestcode;
    private GoogleApiClient mGoogleApiClient;
    private PermissionCallback mCallback;
    private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    /**
     * Method to show toolbar
     * Called by the child activities to display their title and
     * child activities can override this method to give their own impl
     *
     * @param title
     * @param enableBack
     */
    public void setUpToolbar(String title, boolean enableBack) {
        setUpToolbar(title, enableBack, 0);
    }

    /**
     * Method to show toolbar
     * Called by the child activities to display their title and
     * child activities can override this method to give their own impl
     *
     * @param title
     * @param enableBack
     */
    public void setUpToolbar(String title, boolean enableBack, int icon) {
        try {
            Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(mToolbar);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle(null);
            mToolbar.setTitle(title);
            actionBar.setDisplayHomeAsUpEnabled(enableBack);
            if (icon != 0) {
                mToolbar.setNavigationIcon(icon);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean isNetworkAvailable() {
        return Utils.isNetworkAvailable(getApplicationContext());
    }


    @Override
    public void onSuccess(String responce, Object data, int urlId, int position) {

    }

    @Override
    public void onFailure(Exception e, int urlId) {

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.push_right_in,
                        R.anim.push_right_out);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (callback != null) {
            if (requestCode == this.requestcode) {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                            callback.onPermissionStatus(false);
                            break;
                        }
                    }
                    callback.onPermissionStatus(true);
                } else {
                    callback.onPermissionStatus(false);
                }
            } else {
                callback.onPermissionStatus(true);
            }
        }

    }

    public boolean isPemissionAllowed(String permission) {
        return ContextCompat.checkSelfPermission(getApplicationContext(),
                permission) == PackageManager.PERMISSION_GRANTED;
    }


    public void showLocationOnDialog(final PermissionCallback callback) {
        mCallback = callback;
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(30 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
//                LocationSettingsStates locationSettingsStates = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
//                        callback.onPermissionStatus(true);
                        startFetchingLocation();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(
                                    BaseActivity.this, LOCATION_REQ);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        callback.onPermissionStatus(false);
                        break;
                }
            }
        });
    }

    public void requestPermission(String permission, int requestcode, PermissionCallback callback) {
        this.requestcode = requestcode;
        if (isPemissionAllowed(permission)) {
            callback.onPermissionStatus(true);
        } else {
            this.requestcode = requestcode;
            this.callback = callback;
            ActivityCompat.requestPermissions(this,
                    new String[]{permission},
                    requestcode);
        }
    }

    public void requestPermission(String[] permission, int requestcode, PermissionCallback callback) {
        try {
            ArrayList<String> list = isPemissionAllowed(permission);
            if (list.size() == 0) {
                callback.onPermissionStatus(true);
            } else {
                String[] permissionList = new String[list.size()];
                for (int i = 0; i < permissionList.length; i++) {
                    permissionList[i] = list.get(i);
                }
                this.requestcode = requestcode;
                this.callback = callback;
                ActivityCompat.requestPermissions(this,
                        permissionList,
                        requestcode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<String> isPemissionAllowed(String[] permission) {
        ArrayList<String> list = new ArrayList<>();
        try {
            for (String permssion : permission) {
                boolean isGranted = ContextCompat.checkSelfPermission(getApplicationContext(),
                        permssion) == PackageManager.PERMISSION_GRANTED;
                if (!isGranted) {
                    list.add(permssion);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case LOCATION_REQ:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        if (mCallback != null) {
                            mCallback.onPermissionStatus(true);
                        }
                        break;
                    case Activity.RESULT_CANCELED:
                        if (mCallback != null) {
                            mCallback.onPermissionStatus(false);
                        }
                        break;
                }
        }
        super.onActivityResult(requestCode, resultCode, data);

    }


    public void startFetchingLocation() {
        try {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            Log.d("LocationService", "onConnected (line 49): " + location);
            if (location == null) {
                if (mLocationRequest == null) {
                    mLocationRequest = LocationRequest.create()
                            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                            .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                            .setFastestInterval(1 * 1000);
                }
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            } else {
//                LocationData locationData = new LocationData();
//                locationData.location = location;
//                EventBus.getDefault().post(locationData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void onConnected(Bundle dataBundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        if (i == CAUSE_SERVICE_DISCONNECTED) {
            Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
            mGoogleApiClient.reconnect();
        } else if (i == CAUSE_NETWORK_LOST) {
            Toast.makeText(this, "Network lost. Please re-connect.", Toast.LENGTH_SHORT).show();
            mGoogleApiClient.reconnect();
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//        LocationData locationData = new LocationData();
//        locationData.location = null;
//        EventBus.getDefault().post(locationData);
    }


    @Override
    public void onLocationChanged(Location location) {
        try {
//            LocationData locationData = new LocationData();
//            locationData.location = location;
//            EventBus.getDefault().post(locationData);
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
