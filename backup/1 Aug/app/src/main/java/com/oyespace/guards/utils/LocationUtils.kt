package com.oyespace.guards.utils


import android.location.Geocoder
import com.oyespace.guards.Myapp
import com.oyespace.guards.listeners.LocationCallback
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 * Created by Kalyan on 09-Nov-17.
 */
class LocationUtils {


    companion object {
        val geoCoder = Geocoder(Myapp.getContext(), Locale.getDefault())

        fun getAddress(lat: Double, lng: Double, locationCallback: LocationCallback?) {
            Observable.just(1)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .map {
                        geoCoder.getFromLocation(lat, lng, 1)
                    }
                    .subscribe({
                        if (!Utils.isEmpty(it)) {
                            val address = it[0]
                            val locality = address.locality
                            locationCallback?.onAddress(locality)
                        }
                    }, {
                        locationCallback?.onAddress(null)
                    })


        }
    }
}