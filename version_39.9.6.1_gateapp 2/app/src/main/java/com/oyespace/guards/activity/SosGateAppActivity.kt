package com.oyespace.guards.com.oyespace.guards.activity

import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Location
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.gson.Gson
import com.oyespace.guards.R
import com.oyespace.guards.activity.BaseKotlinActivity
import com.oyespace.guards.com.oyespace.guards.pojo.SOSModel
import com.oyespace.guards.pojo.GoogleMapDTO
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_sos_recycle.*
import kotlinx.android.synthetic.main.activity_sos_screen_gate.*
import okhttp3.OkHttpClient
import okhttp3.Request

open class SosGateAppActivity : BaseKotlinActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {



    lateinit var edittext: EditText
    lateinit var edittext1: EditText
    lateinit var edittext2: EditText
    lateinit var save: Button
    var currentSOS:SOSModel = SOSModel()
    var sosLocation : LatLng = LatLng(0.0,0.0)
    var guardLocation : LatLng = LatLng(0.0,0.0)
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location

    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var locationUpdateState = false

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val REQUEST_CHECK_SETTINGS = 2
        private const val PLACE_PICKER_REQUEST = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sos_screen_gate)
        initRealm()
        getSOS()
        initMap()
        setEmergencyContacts()


        btn_dismiss_sos.setOnClickListener {dismissSOS()}
        btn_attend_sos.setOnClickListener({ btn_dismiss_sos.setVisibility(View.GONE) })

    }

    private fun initMap(){
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                lastLocation = p0.lastLocation
                guardLocation = LatLng(lastLocation.latitude, lastLocation.longitude)
                placeMarkerOnMap(guardLocation)
            }
        }
    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        mMap.isMyLocationEnabled = true
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                lastLocation = location
                guardLocation = LatLng(location.latitude, location.longitude)
                placeMarkerOnMap(guardLocation)

                //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
            }
        }
    }

    private fun placeMarkerOnMap(location: LatLng) {
        val markerOptions = MarkerOptions().position(location)
        val titleStr = "You"
        markerOptions.title(titleStr)
        mMap.addMarker(markerOptions)

        val URL = getDirectionURL(guardLocation,sosLocation)
        GetDirection(URL).execute()
    }


    private fun getSOS(){
        val sosObj = realm.where<SOSModel>().findFirst()
        if(sosObj != null){
            currentSOS = sosObj
        }
    }

    private fun dismissSOS(){
        val builder = AlertDialog.Builder(this)
        val dview = layoutInflater.inflate(R.layout.activity_custom_alert, null)
        val alert = builder.create()
        alert.setView(dview)
        alert.show()
        alert.getWindow().setLayout(900, 500)
        val button: Button = dview.findViewById(R.id.b1)
        button.setOnClickListener({
            alert.dismiss()
        })
    }

    private fun setEmergencyContacts(){

        val contacts = ArrayList<MyData>()
        contacts.add(
            MyData(
                BitmapFactory.decodeResource(resources, R.mipmap.amb_new),
                "Ambulance",
                BitmapFactory.decodeResource(resources, R.mipmap.call_orange_call),
                "108"
            )
        )
        contacts.add(
            MyData(
                BitmapFactory.decodeResource(resources, R.mipmap.pol_ice_1),
                "Police",
                BitmapFactory.decodeResource(resources, R.mipmap.call_orange_call),
                "100"
            )
        )
        contacts.add(
            MyData(
                BitmapFactory.decodeResource(resources, R.mipmap.fir_birgade_new),
                "Fire Brigade",
                BitmapFactory.decodeResource(resources, R.mipmap.call_orange_call),
                "101"
            )
        )

        rcv_emergency.layoutManager = GridLayoutManager(this, 3)
        val adapter = RecyclerViewAdapter(contacts)
        rcv_emergency.adapter = adapter
    }

    override fun onBackPressed() {
        //NO BACK BUTTON SKIP ALLOWED
        //super.onBackPressed()
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        mMap.getUiSettings().setZoomControlsEnabled(true)
        setUpMap()
        if(currentSOS!=null && currentSOS.latitude != "" && currentSOS.longitude != "") {
            var lng:Double = currentSOS.longitude.toDouble();
            var lat:Double = currentSOS.latitude.toDouble();

            sosLocation = LatLng(lat, lng)
            mMap.addMarker(MarkerOptions().position(sosLocation).title("SOS Location"))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sosLocation,15.0f))
        }
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        return true
    }
    fun getDirectionURL(origin:LatLng,dest:LatLng) : String{
        return "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}&destination=${dest.latitude},${dest.longitude}&sensor=true&mode=walking&key="+resources.getString(R.string.google_maps_key)
    }

    private inner class GetDirection(val url : String) : AsyncTask<Void, Void, List<List<LatLng>>>(){
        override fun doInBackground(vararg params: Void?): List<List<LatLng>> {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val data = response.body()!!.string()
            Log.e("GoogleMap" , " data : $data")
            val result =  ArrayList<List<LatLng>>()
            try{
                val respObj = Gson().fromJson(data, GoogleMapDTO::class.java)

                val path =  ArrayList<LatLng>()

                for (i in 0..(respObj.routes[0].legs[0].steps.size-1)){
//                    val startLatLng = LatLng(respObj.routes[0].legs[0].steps[i].start_location.lat.toDouble()
//                            ,respObj.routes[0].legs[0].steps[i].start_location.lng.toDouble())
//                    path.add(startLatLng)
//                    val endLatLng = LatLng(respObj.routes[0].legs[0].steps[i].end_location.lat.toDouble()
//                            ,respObj.routes[0].legs[0].steps[i].end_location.lng.toDouble())
                    path.addAll(decodePolyline(respObj.routes[0].legs[0].steps[i].polyline.points))
                }
                result.add(path)
            }catch (e:Exception){
                e.printStackTrace()
            }
            return result
        }

        override fun onPostExecute(result: List<List<LatLng>>) {
            val lineoption = PolylineOptions()
            for (i in result.indices){
                lineoption.addAll(result[i])
                lineoption.width(10f)
                lineoption.color(Color.BLUE)
                lineoption.geodesic(true)
            }
            mMap.addPolyline(lineoption)
        }
    }

    public fun decodePolyline(encoded: String): List<LatLng> {

        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val latLng = LatLng((lat.toDouble() / 1E5),(lng.toDouble() / 1E5))
            poly.add(latLng)
        }

        return poly
    }
}



