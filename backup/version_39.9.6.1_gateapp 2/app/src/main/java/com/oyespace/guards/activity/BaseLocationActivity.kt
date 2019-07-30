package com.oyespace.guards.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.oyespace.guards.listeners.PermissionCallback

/**
 * Created by Kalyan on 08-Nov-17.
 */
abstract class BaseLocationActivity : BaseKotlinActivity(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private var mGoogleApiClient: GoogleApiClient? = null
    private var mCallback: PermissionCallback? = null
    private lateinit var mLocationRequest: LocationRequest


    fun showLocationOnDialog(callback: PermissionCallback) {
        mCallback = callback
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build()
        mGoogleApiClient?.connect()
        mLocationRequest = LocationRequest.create()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = (30 * 1000).toLong()
        mLocationRequest.fastestInterval = (5 * 1000).toLong()
        val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest)
        builder.setAlwaysShow(true)
        val result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build())
        result.setResultCallback { result ->
            val status = result.status
            //                LocationSettingsStates locationSettingsStates = result.getLocationSettingsStates();
            when (status.statusCode) {
                LocationSettingsStatusCodes.SUCCESS ->
                    //                        callback.onPermissionStatus(true);
                    startFetchingLocation()
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                    status.startResolutionForResult(
                            this@BaseLocationActivity, LOCATION_REQ)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> callback.onPermissionStatus(false)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            LOCATION_REQ -> when (resultCode) {
                Activity.RESULT_OK -> if (mCallback != null) {
                    mCallback?.onPermissionStatus(true)
                }
                Activity.RESULT_CANCELED -> if (mCallback != null) {
                    mCallback?.onPermissionStatus(false)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)

    }


    fun startFetchingLocation() {
        try {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return
            }
            val location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)
            if (location == null) {
                if (mLocationRequest == null) {
                    mLocationRequest = LocationRequest.create()
                            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                            .setInterval((10 * 1000).toLong())        // 10 seconds, in milliseconds
                            .setFastestInterval((1 * 1000).toLong())
                }
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this)
            } else {
                onLocationReceived(location)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onConnectionSuspended(i: Int) {
        if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
            Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show()
            mGoogleApiClient?.reconnect()
        } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
            Toast.makeText(this, "Network lost. Please re-connect.", Toast.LENGTH_SHORT).show()
            mGoogleApiClient?.reconnect()
        }
    }


    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        onLocationReceived(null)
    }


    override fun onLocationChanged(location: Location) {
        try {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this)
            onLocationReceived(location)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onConnected(dataBundle: Bundle?) {

    }

    abstract  fun onLocationReceived(location: Location?)



}