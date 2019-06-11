package com.oyespace.guards.activity


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.location.places.GeoDataClient
import com.google.android.gms.location.places.Places
import com.oyespace.guards.fragment.BaseKotlinFragment
import com.oyespace.guards.listeners.LocationCallback
import com.oyespace.guards.listeners.PermissionCallback
import com.oyespace.guards.pojo.SearchResult
import com.oyespace.guards.utils.ConstantUtils
import com.oyespace.guards.utils.LocationUtils
import timber.log.Timber
import java.util.*


class LocationSearchActivity : BaseLocationActivity() {


    companion object {
        val LOCATION_REQUEST = 100
    }


    private var defaultSearchList = ArrayList<SearchResult>()

    private var mGeoDataClient: GeoDataClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_location_search)
        //  setUpToolbar("Search")
        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null)
        //  addDefaultItems()

    }


    private fun senndResult(searchItem: SearchResult) {
        val intent = Intent()
        intent.putExtra(ConstantUtils.DATA, searchItem)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun handleLocation() {
        requestPermission(
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            1,
            PermissionCallback { isGranted ->
                if (isGranted) {
                    checkForLocationStatus()
                } else {

                }
            })
    }

    private fun checkForLocationStatus() {
        val baseLocationActivity = this as BaseLocationActivity
        baseLocationActivity.showLocationOnDialog(PermissionCallback { isGranted: Boolean ->
            if (isGranted) {
                baseLocationActivity.startFetchingLocation()
            } else {
                Toast.makeText(this, "Location Alert", Toast.LENGTH_SHORT).show()
            }
        })
    }


    override fun onLocationReceived(location: Location?) {
        if (location != null) {
            Timber.d(BaseKotlinFragment.TAG)

            LocationUtils.getAddress(location.latitude, location.longitude, object : LocationCallback {
                override fun onAddress(data: String?) {
                    data.let {
                        val searchResult =
                            SearchResult(data, false, "", location.latitude.toString(), location.longitude.toString())
                        senndResult(searchResult)
                        //  LocalDb.saveSearchData(searchResult)
                    }
                }
            })
        } else {
            Toast.makeText(this, "Failed to fetch location", Toast.LENGTH_SHORT).show()
        }
    }


}
