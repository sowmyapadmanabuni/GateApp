package com.oyespace.guards.com.oyespace.guards.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.core.app.ActivityCompat
import androidx.appcompat.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.database.*
import com.google.gson.Gson
import com.oyespace.guards.R
import com.oyespace.guards.activity.BaseKotlinActivity
import com.oyespace.guards.activity.GalleryViewActivity
import com.oyespace.guards.com.oyespace.guards.pojo.SOSModel
import com.oyespace.guards.pojo.GoogleMapDTO
import com.oyespace.guards.utils.ConstantUtils
import com.oyespace.guards.utils.LocalDb
import com.oyespace.guards.utils.Prefs
import com.squareup.picasso.Picasso
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_sos_recycle.*
import kotlinx.android.synthetic.main.activity_sos_screen_gate.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

open class SosGateAppActivity : BaseKotlinActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {



    lateinit var edittext: EditText
    lateinit var edittext1: EditText
    lateinit var edittext2: EditText
    lateinit var save: Button
    var currentSOS:SOSModel = SOSModel()
    var sosLocation : LatLng = LatLng(0.0,0.0)
    var guardLocation : LatLng = LatLng(0.0,0.0)
    var totalGuards:Int = Prefs.getInt("TOTAL_GUARDS",1)
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location

    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var locationUpdateState = false

    private var mDatabase: DatabaseReference? = null
    private var mSosReference: DatabaseReference? = null
    private var isBackEnabled:Boolean = false
    lateinit var sosMarker:Marker
    lateinit var sosListener:ValueEventListener
    lateinit var mPolyline:Polyline
    private var lineoption = PolylineOptions()
    private var isResolving = false
    var userId:Int = 0
    var emergencyImages:ArrayList<String> = ArrayList();
    internal var t1: TextToSpeech?=null
    var mSosPath:String = "";

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onStart() {
        super.onStart()
        Prefs.putBoolean("ACTIVE_SOS",true);
    }

    override fun onDestroy() {
        mSosReference!!.removeEventListener(sosListener)
        Prefs.putBoolean("ACTIVE_SOS",false);
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        Prefs.putBoolean("ACTIVE_SOS",false);
    }

    override fun onResume() {
        super.onResume()
        if(currentSOS != null) {
            var lng: Double = currentSOS.longitude.toDouble();
            var lat: Double = currentSOS.latitude.toDouble();
            sosLocation = LatLng(lat, lng)
            getDirections()
        }else{
            getSOS()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sos_screen_gate)
        initRealm()
        getSOS()
        initMap()
        setEmergencyContacts()


        btn_dismiss_sos.setOnClickListener {dismissSOS()}
        btn_attend_sos.setOnClickListener({ attendSOS() })

        t1 = TextToSpeech(applicationContext, TextToSpeech.OnInitListener { status ->
            if (status != TextToSpeech.ERROR)
                t1?.language=Locale.getDefault()
        })

        sos_image.setOnClickListener({
            if(emergencyImages.size > 0) {
                val galleryIntent = Intent(this@SosGateAppActivity, GalleryViewActivity::class.java)
                var emergencyImagesJson = Gson().toJson(emergencyImages)
                galleryIntent.putExtra("images", emergencyImagesJson)
                galleryIntent.putExtra("sospath", mSosPath)
                startActivity(galleryIntent)
            }
        })

    }



    private fun initFRTDB(){
        mSosPath = "SOS/"+ LocalDb.getAssociation()!!.asAssnID+"/"+currentSOS.userId
        Log.e(TAG,""+mSosPath)
        mDatabase = FirebaseDatabase.getInstance().reference
        mSosReference = FirebaseDatabase.getInstance().getReference(mSosPath)
        initSOSListener()
    }


    private fun attendSOS(){
        Log.e("ATTEND",""+btn_attend_sos.text+" "+isResolving)
        if(isResolving) {
            removeCurrentSOSRealm()
            mSosReference!!.removeValue()
            isResolving = false
            checkNextSOS()

        }else{
            //mSosReference
            btn_dismiss_sos.setVisibility(View.GONE)
            btn_attend_sos.text = "Resolved"
            mSosReference!!.child("attendedBy").setValue(Prefs.getString(ConstantUtils.GATE_NO, ""))
            mSosReference!!.child("attendedByMobile").setValue(Prefs.getString(ConstantUtils.GATE_MOB, ""))
            isResolving = true
        }
    }

    private fun initSOSListener(){
        sosListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.e("SOS_ACTIVE_IN",""+dataSnapshot)
                if (dataSnapshot.exists()) {

                    try {
                        //dataSnapshot.children.forEach {
                        val it = dataSnapshot;
                            val user_id = it.key;
                            //val sos = it.getValue()
                            val isActive = it.child("isActive").getValue(Boolean::class.java)
                            var unitName = ""
                            var unitId: Int = 0
                            var userName:String = ""
                            var userMobile:String = ""
                            var sosImage:String = ""
                            var latitude:String = ""
                            var longitude:String = ""
                            var attendedBy:String = ""
                            var id:Int = 0
                            //var userId: Int = 0
                            var totalPassed:Int = 0
                            var passedBy:HashMap<String,String> = HashMap()


                            if(it.hasChild("unitName") && it.hasChild("unitName")!=null){
                                unitName = it.child("unitName").getValue(String::class.java)!!
                            }
                            if(it.hasChild("unitId") && it.hasChild("unitId")!=null){
                                unitId = it.child("unitId").getValue(Int::class.java)!!
                            }
                            if(it.hasChild("userName") && it.hasChild("userName")!=null){
                                userName = it.child("userName").getValue(String::class.java)!!
                            }
                            if(it.hasChild("userMobile") && it.hasChild("userMobile")!=null){
                                userMobile = it.child("userMobile").getValue(String::class.java)!!
                            }

                            Log.e("LATITUDE",""+it.child("latitude").getValue())

                            if(it.hasChild("latitude")){
                                latitude = it.child("latitude").getValue().toString()
                            }
                            if(it.hasChild("longitude")){
                                longitude = it.child("longitude").getValue().toString()
                            }
                            if(it.hasChild("sosImage")){
                                sosImage = it.child("sosImage").getValue(String::class.java)!!
                            }
                            if(it.hasChild("userId")){
                                userId = it.child("userId").getValue(Int::class.java)!!
                            }
//                            if(it.hasChild("totalpassed")){
//                                totalPassed = it.child("totalpassed").getValue(Int::class.java)!!
//                            }
                            if(it.hasChild("passedby")){
                                val type = object : GenericTypeIndicator<HashMap<String,String>?>() {}
                                passedBy = it.child("passedby").getValue(type)!!
                                totalPassed = passedBy.size
                            }
                            if(it.hasChild("emergencyImages")){
                                val _tp = object: GenericTypeIndicator<ArrayList<String>>(){}
                                emergencyImages = (it.child("emergencyImages").getValue(_tp))!!
                            }

                            if(it.hasChild("attendedBy")){
                                attendedBy = it.child("attendedBy").getValue(String::class.java)!!
                            }

                        Log.e("EMER_G",""+emergencyImages);
                            if (isActive != null && isActive && userId != 0) {
                                runOnUiThread {

                                    val currentGate = Prefs.getString(ConstantUtils.GATE_NO,"");
                                    if(attendedBy.equals("") || attendedBy.equals(currentGate)) {


                                        if (userMobile != "" && userMobile != null) {
                                            sos_usermobile.text = userMobile
                                        }
                                        if (userName != "" && userName != null) {
                                            sos_username.text = userName
                                        }
                                        if (emergencyImages != null && emergencyImages.size > 0) {

                                            Picasso.with(applicationContext)
                                                .load(emergencyImages[0])
                                                .placeholder(R.drawable.newicons_camera)
                                                .error(R.drawable.newicons_camera).into(sos_image)
                                        } else {
                                            Picasso.with(applicationContext)
                                                .load(R.drawable.newicons_camera).into(sos_image)
                                        }
                                        if (latitude != "" && latitude != null && longitude != "" && longitude != null) {
                                            val lat = latitude.toDouble()
                                            val lon = longitude.toDouble()
                                            sosLocation = LatLng(lat, lon)
                                            getDirections()
                                        }

                                        if ((totalGuards - totalPassed) == 1) {
                                            btn_dismiss_sos.visibility = View.GONE
                                        } else {
                                            btn_dismiss_sos.visibility = View.VISIBLE
                                        }
                                    }else{
                                        //Someone else accepted the SOS
                                        try {
                                            Toast.makeText(this@SosGateAppActivity,""+attendedBy+" accepted the S.O.S",Toast.LENGTH_LONG).show()
                                            t1?.speak(""+attendedBy+" accepted the SOS", TextToSpeech.QUEUE_FLUSH, null)
                                            Log.e("DEleted",""+currentSOS);
                                            val sosObj = realm.where<SOSModel>().equalTo("userId",userId).findFirst()
                                            if(sosObj!= null){
                                                realm.executeTransaction {
                                                    sosObj.deleteFromRealm()
                                                }
                                            }
                                            checkNextSOS()
                                        }catch (e:Exception){
                                            checkNextSOS()
                                        }
                                    }
                                }
                            }else{

                                if(userId != 0 && userId != null) {
                                    Log.e("USERID","NOT NULL")
                                    val sosObj = realm.where<SOSModel>().equalTo("userId",userId).findFirst()
                                    Log.e("SOSOBJE",""+sosObj)
                                    if(sosObj != null) {
                                        Log.e("SOSOBJECTCHECK",""+sosObj)
                                        realm.executeTransaction {
                                            sosObj.deleteFromRealm()
                                        }

                                    }
                                }

                                checkNextSOS()

                              //  isBackEnabled = true
                              //  onBackPressed()
                            }

                            Log.e("CHILD", "" + isActive);
                        //}

//                        val totalSOS = realm.where<SOSModel>().count()
//                        Log.e("totalSOS",""+totalSOS);
//                        if(totalSOS > 0){
//                            val i_vehicle = Intent(applicationContext, SosGateAppActivity::class.java)
//                            i_vehicle.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(i_vehicle)
//                        }
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }else{
                    try {

                        Log.e("DEleted",""+currentSOS);
                        val sosObj = realm.where<SOSModel>().equalTo("userId",userId).findFirst()
                        if(sosObj!= null){
                            realm.executeTransaction {
                                sosObj.deleteFromRealm()
                            }
                        }
//                        realm.executeTransaction {
//                            currentSOS.deleteFromRealm()
//                        }
                        checkNextSOS()
                    }catch (e:Exception){
                        e.printStackTrace()
                        checkNextSOS()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("SOS_LISTEN","Error")
            }
        }
        mSosReference!!.addValueEventListener(sosListener)
    }

    private fun checkNextSOS(){
        val nextSos = realm.where<SOSModel>().findFirst()
        if(nextSos != null){


            if (::sosMarker.isInitialized) {
                sosMarker.remove()
            }
            if(::sosListener.isInitialized && mSosReference!=null){
                mSosReference!!.removeEventListener(sosListener)
            }
            if(::mPolyline.isInitialized){
                mPolyline.remove()
            }
            getSOS()
            updateSOSLocation()
        }else{
            isBackEnabled = true
            onBackPressed()
        }
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

    private fun updateSOSLocation(){
        if(currentSOS!=null && currentSOS.latitude != "" && currentSOS.longitude != "") {
            var lng:Double = currentSOS.longitude.toDouble();
            var lat:Double = currentSOS.latitude.toDouble();

            sosLocation = LatLng(lat, lng)
            val markerOption:MarkerOptions = MarkerOptions().position(sosLocation).title("SOS Location")
            sosMarker =  mMap.addMarker(markerOption)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sosLocation,15.0f))
        }
    }

    private fun placeMarkerOnMap(location: LatLng) {
        val markerOptions = MarkerOptions().position(location)
        val titleStr = "You"
        markerOptions.title(titleStr)
        mMap.addMarker(markerOptions)

        getDirections()
    }

    private fun getDirections(){
        val URL = getDirectionURL(guardLocation,sosLocation)
        GetDirection(URL).execute()
    }

    private fun getSOS(){
        val sosObj = realm.where<SOSModel>().findFirst()
        Log.e("getSOS",""+sosObj);
        if(sosObj != null){
            currentSOS = sosObj
            val currentGate:String = Prefs.getString("GATE_NO",null)

            if(currentSOS.userMobile != "" && currentSOS.userMobile != null){
                sos_usermobile.text = currentSOS.userMobile
            }
            if(currentSOS.userName != "" && currentSOS.userName != null){
                sos_username.text = currentSOS.userName
            }
            if(currentSOS.sosImage != "" && currentSOS.sosImage != null){
                Picasso.with(applicationContext)
                    .load(currentSOS.sosImage)
                    .placeholder(R.drawable.newicons_camera).error(R.drawable.newicons_camera).into(sos_image)
            }else{
                Picasso.with(applicationContext)
                    .load(R.drawable.newicons_camera).into(sos_image)
            }
            if(currentGate!=null && currentGate != "" && currentSOS.attendedBy.equals(currentGate)){
                btn_dismiss_sos.visibility = View.GONE
                btn_attend_sos.text = "Resolved"
                isResolving = true
            }
            initFRTDB()
        }
    }

    private fun dismissSOS(){
        val builder = AlertDialog.Builder(this)
        val dview = layoutInflater.inflate(R.layout.activity_custom_alert, null)
        val alert = builder.create()
        alert.setView(dview)
        alert.show()
        //alert.getWindow().setLayout(900, 500)
        val button: Button = dview.findViewById(R.id.b1)
        button.setOnClickListener({
            updatePassedSOS()
            alert.dismiss()
            removeCurrentSOSRealm()
            checkNextSOS()
            //isBackEnabled = true
            //onBackPressed()
        })
    }

    private fun removeCurrentSOSRealm(){
        try {
            val userId = currentSOS.userId
            if (userId != 0 && userId != null) {
                val sosObj = realm.where<SOSModel>().equalTo("userId", userId).findFirst()
                if (sosObj != null) {
                    realm.executeTransaction {
                        sosObj.deleteFromRealm()
                    }

                }
            }
        }catch (e:java.lang.Exception){

        }
    }

    private fun updatePassedSOS(){

        //val key = mSosReference!!.child("passedby").push().key
       // val key = Prefs.getString("GATE_NO","")
        var passedReference:DatabaseReference = mSosReference!!.child("passedby")
        //passedReference.addListenerForSingleValueEvent(n)

        val passedListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.e("passedListener", "" + dataSnapshot)
                if (dataSnapshot.exists()) {

                    try {
                        Log.e("PASSEDCOUNT",""+dataSnapshot.childrenCount)
                    } catch (e: Exception) {
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("passedListener","Error")
            }
        }
        passedReference!!.addValueEventListener(passedListener)
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())

        mSosReference!!.child("passedby").child(Prefs.getString("GATE_NO","")).setValue(""+currentDate)

    }

    private fun setEmergencyContacts(){

        val contacts = ArrayList<EmergencyModel>()
        contacts.add(
            EmergencyModel(
                BitmapFactory.decodeResource(resources, R.mipmap.amb_new),
                "Ambulance",
                BitmapFactory.decodeResource(resources, R.mipmap.call_orange_call),
                "108"
            )
        )
        contacts.add(
            EmergencyModel(
                BitmapFactory.decodeResource(resources, R.mipmap.pol_ice_1),
                "Police",
                BitmapFactory.decodeResource(resources, R.mipmap.call_orange_call),
                "100"
            )
        )
        contacts.add(
            EmergencyModel(
                BitmapFactory.decodeResource(resources, R.mipmap.fir_birgade_new),
                "Fire Brigade",
                BitmapFactory.decodeResource(resources, R.mipmap.call_orange_call),
                "101"
            )
        )
        rcv_emergency.setLayoutManager(androidx.recyclerview.widget.GridLayoutManager(this@SosGateAppActivity, 3))
        val adapter = EmrgencyContactAdapter(contacts,clickListener = {
                unit,index -> onEmergencyClick(unit,index)
        })
        rcv_emergency.adapter = adapter
    }

    fun onEmergencyClick(unit:EmergencyModel, index:Int){
        val intent = Intent(Intent.ACTION_CALL);
        intent.data = Uri.parse("tel:"+unit.phoneNum)
        startActivity(intent)
    }

    override fun onBackPressed() {
        if(isBackEnabled) {
//            super.onBackPressed()
            finish()
        }
    }



    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        mMap.getUiSettings().setZoomControlsEnabled(true)
        setUpMap()
        updateSOSLocation()
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        return true
    }

    private fun getDirectionURL(origin:LatLng,dest:LatLng) : String{
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
            lineoption = PolylineOptions()
            for (i in result.indices){
                lineoption.addAll(result[i])
                lineoption.width(10f)
                lineoption.color(Color.BLACK)
                lineoption.geodesic(true)
            }
            mPolyline = mMap.addPolyline(lineoption)
        }
    }

    private fun decodePolyline(encoded: String): List<LatLng> {

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



