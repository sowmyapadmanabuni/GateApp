package com.oyespace.guards.activity

import android.app.Dialog
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.location.Location
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.database.*
import com.google.gson.Gson
import com.kodmap.app.library.PopopDialogBuilder
import com.oyespace.guards.BackgroundSyncReceiver
import com.oyespace.guards.R
import com.oyespace.guards.adapter.SOSImageAdapter
import com.oyespace.guards.com.oyespace.guards.activity.EmergencyModel
import com.oyespace.guards.com.oyespace.guards.pojo.SOSModel
import com.oyespace.guards.constants.PrefKeys
import com.oyespace.guards.pojo.GoogleMapDTO
import com.oyespace.guards.services.SOSSirenService
import com.oyespace.guards.utils.ConstantUtils
import com.oyespace.guards.utils.LocalDb
import com.oyespace.guards.utils.Prefs
import com.squareup.picasso.Picasso
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_sos_screen_gate.*
import kotlinx.android.synthetic.main.header_with_next.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

open class SosGateAppActivity : BaseKotlinActivity(), OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener, View.OnClickListener {

    override fun onClick(v: View) {
        when (v.id) {
            R.id.call_ambulance -> {
                onEmergencyClick("108")
            }
            R.id.call_police -> {
                onEmergencyClick("100")
            }
            R.id.call_fire -> {
                onEmergencyClick("101")
            }
        }

    }

    var iv_torch: Button?=null
    var clickable1 = 0

    lateinit var edittext: EditText
    lateinit var edittext1: EditText
    lateinit var edittext2: EditText
    lateinit var save: Button
    var currentSOS: SOSModel = SOSModel()
    var sosId: Int = 0
    var sosLocation: LatLng = LatLng(0.0, 0.0)
    var guardLocation: LatLng = LatLng(0.0, 0.0)
    var totalGuards: Int = Prefs.getInt("TOTAL_GUARDS", 1)
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location

    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var locationUpdateState = false

    private var mDatabase: DatabaseReference? = null
    private var mSosReference: DatabaseReference? = null
    private var isBackEnabled: Boolean = false
    lateinit var sosMarker: Marker
    lateinit var sosListener: ValueEventListener
    //lateinit var sosOnceListener:
    lateinit var mPolyline: Polyline
    private var lineoption = PolylineOptions()
    private var isResolving = false
    var userId: Int = 0
    var emergencyImages: ArrayList<String> = ArrayList()
    internal var t1: TextToSpeech? = null
    var mSosPath: String = ""

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    fun startSiren() {
        val intent = Intent(this, SOSSirenService::class.java)
        this.startService(intent)
    }

    fun stopSiren() {
        val intent = Intent(this, SOSSirenService::class.java)
        this.stopService(intent)
    }

    override fun onStart() {
        super.onStart()
        Prefs.putBoolean("ACTIVE_SOS", true)
        startSiren()
    }

    override fun onDestroy() {
        try {
            stopSiren()
            Prefs.putBoolean("ACTIVE_SOS", false)
            mSosReference!!.removeEventListener(sosListener)

        } catch (e: Exception) {
            stopSiren()
            Prefs.putBoolean("ACTIVE_SOS", false)
        }
        super.onDestroy()
    }

    override fun onPause() {
        stopSiren()
        super.onPause()
        Prefs.putBoolean("ACTIVE_SOS", false)
    }

    override fun onResume() {

        try {
            if (currentSOS != null && currentSOS.isValid) {
                var lng: Double = currentSOS.longitude.toDouble()
                var lat: Double = currentSOS.latitude.toDouble()

                Log.v("LOcation is",lng.toString() +".."+lat)
               sosLocation = LatLng(lat, lng)


                getDirections()
            } else {
                getSOS()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        super.onResume()
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sos_screen_gate)

        buttonNext.visibility=View.GONE

        iv_torch=findViewById(R.id.iv_torch)
        iv_torch!!.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                val camManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager;
                var cameraId: String? = null
                cameraId = camManager.getCameraIdList()[0];
                if(clickable1==0){
                    try {
                        iv_torch!!.background=resources.getDrawable(R.drawable.torch_off)
                        camManager.setTorchMode(cameraId, true);   //Turn ON

                        //  iv_torch!!.text = "OFF"
                        clickable1=1
                    } catch (e: CameraAccessException) {
                        e.printStackTrace();
                    }
                }
                else if(clickable1==1){
                    camManager.setTorchMode(cameraId, false);
                    // iv_torch!!.text = "ON"
                    iv_torch!!.background=resources.getDrawable(R.drawable.torch_on)
                    clickable1=0

                }
            }

        }

        var notificationManager: NotificationManager =
            getSystemService(
                Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()

        initRealm()
        getSOS()
        initMap()
        setEmergencyContacts()


        btn_dismiss_sos.setOnClickListener { dismissSOS() }
        btn_attend_sos.setOnClickListener({ attendSOS() })

        t1 = TextToSpeech(applicationContext, TextToSpeech.OnInitListener { status ->
            if (status != TextToSpeech.ERROR)
                t1?.language = Locale.getDefault()
        })

        sos_image.setOnClickListener({
            if (emergencyImages.size > 0) {
//                val galleryIntent = Intent(this@SosGateAppActivity, GalleryViewActivity::class.java)
//                var emergencyImagesJson = Gson().toJson(emergencyImages)
//                galleryIntent.putExtra("images", emergencyImagesJson)
//                galleryIntent.putExtra("sospath", mSosPath)
//                startActivity(galleryIntent)
                showImages()
            }
        })

    }


    private fun showImages() {
        val urlList = ArrayList<String>()
        urlList.addAll(emergencyImages)
//        val dialog = PopopDialogBuilder(this@SosGateAppActivity).setList(urlList).build()
//        dialog.show()
        showDialog(urlList)
    }

    private fun showDialog(images: ArrayList<String>) {
        val dialog = Dialog(this@SosGateAppActivity)
        dialog .requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog .setCancelable(true)
        dialog .setContentView(R.layout.sos_image_dialog)

        val pager = dialog.findViewById(R.id.sos_pager) as ViewPager
        val closebtn = dialog.findViewById(R.id.sos_close) as Button
        val sosPagerAdapter = SOSImageAdapter(this@SosGateAppActivity,images)
        pager.adapter = sosPagerAdapter;

        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
//        val body = dialog .findViewById(R.id.body) as TextView
//        body.text = title
//        val yesBtn = dialog .findViewById(R.id.yesBtn) as Button
//        val noBtn = dialog .findViewById(R.id.noBtn) as TextView
        closebtn.setOnClickListener {
            dialog .dismiss()
        }
//        noBtn.setOnClickListener { dialog .dismiss() }
        dialog .show()

    }

    private fun initFRTDB() {
        try {
            mSosPath = "SOS/" + LocalDb.getAssociation()!!.asAssnID + "/" + currentSOS.userId
            Log.e(TAG, "" + mSosPath)
            mDatabase = FirebaseDatabase.getInstance().reference
            mSosReference = FirebaseDatabase.getInstance().getReference(mSosPath)
            mSosReference!!.keepSynced(true)
            initSOSListener()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun sendSOSStatus(status: String) {
        try {
            if (sosId != 0) {
                val intentAction1 = Intent(applicationContext, BackgroundSyncReceiver::class.java)
                intentAction1.putExtra(
                    ConstantUtils.BSR_Action,
                    ConstantUtils.BGS_SOS_STATUS
                )
                intentAction1.putExtra("sos_id", sosId)
                intentAction1.putExtra("sos_status", "" + status)
                sendBroadcast(intentAction1)
            }
        } catch (e: java.lang.Exception) {
            Log.e("sendSOSStatus", "" + status)
            e.printStackTrace()
        }
    }


    private fun attendSOS() {
        try {
            if (isResolving) {
                sendSOSStatus(ConstantUtils.SOS_STATUS_COMPLETED)
            }
            val postListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    try {

                        if (dataSnapshot.exists()) {
                            if (isResolving) {
                                removeCurrentSOSRealm(false)
                                mSosReference!!.removeValue()
                                isResolving = false
                                checkNextSOS()

                            } else {
                                //mSosReference
                                btn_dismiss_sos.visibility = View.GONE
                                btn_attend_sos.text = "Resolved"
                                isResolving = true
                                try {
                                    var gate = Prefs.getString(ConstantUtils.GATE_NO, "")
                                    if (gate.equals("")) {
                                        gate = "Gate 1"
                                    }
                                    mSosReference?.child("attendedBy")
                                        ?.setValue(Prefs.getString(ConstantUtils.GATE_NO, ""))
                                    mSosReference?.child("attendedByMobile")
                                        ?.setValue(Prefs.getString(PrefKeys.MOBILE_NUMBER, ""))
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        } else {
                            removeCurrentSOSRealm(false)
                            mSosReference!!.removeValue()
                            isResolving = false
                            checkNextSOS()
                        }
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message
                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException())

                }
            }
            mSosReference!!.addListenerForSingleValueEvent(postListener)
        } catch (e: Exception) {
            Log.e("ATTENDERROR", "" + e)
            e.printStackTrace()
        }

    }

    private fun initSOSListener() {
        try {
            sosListener = object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    Log.e("SOS_ACTIVE_IN", "" + dataSnapshot)
                    handleFRTDBSnapshot(dataSnapshot)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("SOS_LISTEN", "Error")
                }
            }
            mSosReference!!.addValueEventListener(sosListener)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun handleFRTDBSnapshot(dataSnapshot: DataSnapshot) {
        if (dataSnapshot.exists()) {

            try {
                //dataSnapshot.children.forEach {
                val it = dataSnapshot
                val user_id = it.key
                //val sos = it.getValue()
                var isActive: Boolean = true//it.child("isActive").getValue(Boolean::class.java)
                var unitName = ""
                var unitId: Int = 0
                var userName: String = ""
                var userMobile: String = ""
                var sosImage: String = ""
                var latitude: String = ""
                var longitude: String = ""
                var attendedBy: String = ""
                var attendedByMob: String = ""
                var id: Int = 0
                //var userId: Int = 0
                var totalPassed: Int = 0
                var passedBy: HashMap<String, String> = HashMap()
                var isValidSOS: Boolean = true

                if (it.hasChild("isActive") && it.hasChild("isActive") != null) {
                    isActive = it.child("isActive").getValue(Boolean::class.java)!!
                }
                if (it.hasChild("unitName") && it.hasChild("unitName") != null) {
                    unitName = it.child("unitName").getValue(String::class.java)!!
                }
                if (it.hasChild("unitId") && it.hasChild("unitId") != null) {
                    unitId = it.child("unitId").getValue(Int::class.java)!!
                }
                if (it.hasChild("userName") && it.hasChild("userName") != null) {
                    userName = it.child("userName").getValue(String::class.java)!!
                }


                if (it.hasChild("userMobile") && it.hasChild("userMobile") != null) {
                    userMobile = it.child("userMobile").getValue(String::class.java)!!
                }

                Log.e("LATITUDE", "" + it.child("latitude").getValue())

                if (it.hasChild("latitude")) {
                    latitude = it.child("latitude").getValue().toString()
                }
                if (it.hasChild("longitude")) {
                    longitude = it.child("longitude").getValue().toString()
                }
                if (it.hasChild("sosImage")) {
                    sosImage = it.child("sosImage").getValue(String::class.java)!!
                }
                if (it.hasChild("id")) {
                    id = it.child("id").getValue(Int::class.java)!!
                    sosId = id
                }
                if (it.hasChild("userId")) {
                    userId = it.child("userId").getValue(Int::class.java)!!
                } else {
                    isValidSOS = false
                }
//                            if(it.hasChild("totalpassed")){
//                                totalPassed = it.child("totalpassed").getValue(Int::class.java)!!
//                            }
                if (it.hasChild("passedby")) {
                    val type = object : GenericTypeIndicator<HashMap<String, String>?>() {}
                    passedBy = it.child("passedby").getValue(type)!!
                    totalPassed = passedBy.size
                }
                if (it.hasChild("emergencyImages")) {
                    val _tp = object : GenericTypeIndicator<ArrayList<String>>() {}
                    emergencyImages = (it.child("emergencyImages").getValue(_tp))!!
                }

                if (it.hasChild("attendedBy")) {
                    attendedBy = it.child("attendedBy").getValue(String::class.java)!!
                }
                if (it.hasChild("attendedByMobile")) {
                    attendedByMob = it.child("attendedByMobile").getValue(String::class.java)!!
                }

                Log.e("EMER_G", "" + emergencyImages)
                if (isActive != null && isActive && userId != 0 && isValidSOS) {
                    runOnUiThread {

                        val currentGate = Prefs.getString(PrefKeys.MOBILE_NUMBER, "")
                        if (attendedByMob.equals("") || attendedByMob.equals(currentGate)) {


                            if (userMobile != "" && userMobile != null) {
                                sos_usermobile.text = userMobile
                            }
                            if (userName != "" && userName != null) {
                                sos_username.text = userName
                            }
                            if (unitName != "" && unitName != null && !unitName.equals("")) {
                                sos_unitname.text = "Unit: " + unitName
                            }
                            if (emergencyImages != null && emergencyImages.size > 0) {

//                                Picasso.with(applicationContext)
//                                    .load(emergencyImages[0])
//                                    .placeholder(R.drawable.newicons_camera)
//                                    .error(R.drawable.newicons_camera).into(sos_image)


                                try{
                                    val imageBytes = Base64.decode(emergencyImages[0], Base64.DEFAULT)
                                    val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                                    sos_image!!.setImageBitmap(decodedImage)
                                }catch (e:java.lang.Exception){
                                    e.printStackTrace()
                                }
                            } else {
                                Picasso.with(applicationContext)
                                    .load(R.drawable.newicons_camera).into(sos_image)
                            }
                            if (latitude != "" && latitude != null && longitude != "" && longitude != null) {
                                val lat = latitude.toDouble()
                                val lon = longitude.toDouble()
                                sosLocation = LatLng(lat, lon)
                                //getDirections()
                            }

                            if ((totalGuards - totalPassed) <= 1) {
                                btn_dismiss_sos.visibility = View.GONE
                            } else {
                                if (attendedBy.equals("")) {
                                    btn_dismiss_sos.visibility = View.VISIBLE
                                } else if (attendedBy.equals(currentGate)) {
                                    btn_dismiss_sos.visibility = View.GONE
                                }

                            }
                        } else {
                            //Someone else accepted the SOS
                            try {
                                Toast.makeText(
                                    this@SosGateAppActivity,
                                    "" + attendedBy + " accepted the S.O.S",
                                    Toast.LENGTH_LONG
                                ).show()
                                t1?.speak(
                                    "" + attendedBy + " accepted the SOS",
                                    TextToSpeech.QUEUE_FLUSH,
                                    null
                                )
                                Log.e("DEleted", "" + currentSOS)
                                val sosObj =
                                    realm.where<SOSModel>().equalTo("userId", userId).findFirst()
                                if (sosObj != null) {
                                    realm.executeTransaction {
                                        sosObj.deleteFromRealm()
                                    }

                                }
                                checkNextSOS()
                            } catch (e: Exception) {
                                checkNextSOS()
                            }
                        }
                    }
                } else {

                    if (userId != 0 && userId != null) {
                        Log.e("USERID", "NOT NULL")
                        val sosObj = realm.where<SOSModel>().equalTo("userId", userId).findFirst()
                        Log.e("SOSOBJE", "" + sosObj)
                        if (sosObj != null) {
                            Log.e("SOSOBJECTCHECK", "" + sosObj)
                            realm.executeTransaction {
                                sosObj.deleteFromRealm()
                            }

                        }
                    }

                    checkNextSOS()

                    //  isBackEnabled = true
                    //  onBackPressed()
                }

                Log.e("CHILD", "" + isActive)
                //}

//                        val totalSOS = realm.where<SOSModel>().count()
//                        Log.e("totalSOS",""+totalSOS);
//                        if(totalSOS > 0){
//                            val i_vehicle = Intent(applicationContext, SosGateAppActivity::class.java)
//                            i_vehicle.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(i_vehicle)
//                        }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            try {

                Log.e("DEleted", "" + currentSOS)
                Log.e("DEleted_USR", "" + userId)
                if (userId == 0 && currentSOS.isValid && currentSOS != null && currentSOS.userId != 0) {
                    userId = currentSOS.userId
                }
                val sosObj = realm.where<SOSModel>().equalTo("userId", userId).findFirst()
                if (sosObj != null) {
                    realm.executeTransaction {
                        sosObj.deleteFromRealm()
                    }
                }
//                        realm.executeTransaction {
//                            currentSOS.deleteFromRealm()
//                        }
                checkNextSOS()
            } catch (e: Exception) {
                e.printStackTrace()
                checkNextSOS()
            }
        }
    }

    private fun checkNextSOS() {
        try {
            val nextSos = realm.where<SOSModel>().findFirst()
            if (nextSos != null) {


                if (::sosMarker.isInitialized) {
                    sosMarker.remove()
                }
                if (::sosListener.isInitialized && mSosReference != null) {
                    mSosReference!!.removeEventListener(sosListener)
                }
                if (::mPolyline.isInitialized) {
                    mPolyline.remove()
                }
                getSOS()
                updateSOSLocation()
            } else {
                isBackEnabled = true
                onBackPressed()
            }
        } catch (e: java.lang.Exception) {
            isBackEnabled = true
            onBackPressed()
            e.printStackTrace()
        }
    }

    private fun initMap() {
        try {
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
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun setUpMap() {
        try {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
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
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

    }

    private fun updateSOSLocation() {
        try {
            if (currentSOS != null && currentSOS.latitude != "" && currentSOS.longitude != "") {
                var lng: Double = currentSOS.longitude.toDouble()
                var lat: Double = currentSOS.latitude.toDouble()

                sosLocation = LatLng(lat, lng)
                val markerOption: MarkerOptions =
                    MarkerOptions().position(sosLocation).title("SOS Location")
                sosMarker = mMap.addMarker(markerOption)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sosLocation, 15.0f))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun placeMarkerOnMap(location: LatLng) {
        try {

            val markerOptions = MarkerOptions().position(location)
            val titleStr = "You"
            markerOptions.title(titleStr)
            mMap.addMarker(markerOptions)

            getDirections()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getDirections() {
        try {
            val URL = getDirectionURL(guardLocation, sosLocation)
            GetDirection(URL).execute()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun getSOS() {
        try {
            sosId = 0
            val sosObj = realm.where<SOSModel>().findFirst()
            Log.e("getSOS", "" + sosObj)
            if (sosObj != null && sosObj.userName != null && !sosObj.userName.equals("")) {
                currentSOS = sosObj
                sosId = currentSOS.id
                val currentGate: String = Prefs.getString(PrefKeys.MOBILE_NUMBER, "");

                if (currentSOS.userMobile != "" && currentSOS.userMobile != null) {
                    sos_usermobile.text = currentSOS.userMobile
                }
                if (currentSOS.userName != "" && currentSOS.userName != null) {
                    sos_username.text = currentSOS.userName
                }
                if (currentSOS.sosImage != "" && currentSOS.sosImage != null) {
//                    Picasso.with(applicationContext)
//                        .load(currentSOS.sosImage)
//                        .placeholder(R.drawable.newicons_camera).error(R.drawable.newicons_camera)
//                        .into(sos_image)
                    try{
                        val imageBytes = Base64.decode(currentSOS.sosImage, Base64.DEFAULT)
                        val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                        sos_image!!.setImageBitmap(decodedImage)


                    }catch (e:java.lang.Exception){
                        e.printStackTrace()
                    }


                } else {
                    Picasso.with(applicationContext)
                        .load(R.drawable.newicons_camera).into(sos_image)
                }
                if (currentGate != null && currentGate != "" && currentSOS.attendedBy.equals(
                        currentGate
                    )
                ) {
                    btn_dismiss_sos.visibility = View.GONE
                    btn_attend_sos.text = "Resolved"
                    isResolving = true
                }else{
                    btn_dismiss_sos.visibility = View.VISIBLE
                    btn_attend_sos.text = "Attend"
                    isResolving = false
                }
                initFRTDB()
            } else {
                isBackEnabled = true
                onBackPressed()
            }
        } catch (e: java.lang.Exception) {
            isBackEnabled = true
            onBackPressed()
            e.printStackTrace()
        }
    }

    private fun dismissSOS() {

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
            removeCurrentSOSRealm(true)

            //isBackEnabled = true
            //onBackPressed()
        })
    }

    private fun removeCurrentSOSRealm(isdismiss: Boolean) {
        try {
            var _userId = userId
            if (currentSOS.isValid && currentSOS != null) {
                _userId = currentSOS.userId
            }
            if (_userId != 0 && _userId != null) {
                val sosObj = realm.where<SOSModel>().equalTo("userId", _userId).findFirst()
                if (sosObj != null) {
                    realm.executeTransaction {
                        sosObj.deleteFromRealm()
                    }

                }
            }
            if (isdismiss) {
                checkNextSOS()
            }
        } catch (e: java.lang.Exception) {
            Log.e("ERRRRR", "" + e)
            if (isdismiss) {
                checkNextSOS()
            }
        }
    }

    private fun updatePassedSOS() {
        try {
            sendSOSStatus(ConstantUtils.SOS_STATUS_PASSED)

            //val key = mSosReference!!.child("passedby").push().key
            // val key = Prefs.getString("GATE_NO","")
            var passedReference: DatabaseReference = mSosReference!!.child("passedby")
            //passedReference.addListenerForSingleValueEvent(n)

            val passedListener = object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    Log.e("passedListener", "" + dataSnapshot)
                    if (dataSnapshot.exists()) {

                        try {
                            Log.e("PASSEDCOUNT", "" + dataSnapshot.childrenCount)
                        } catch (e: Exception) {
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("passedListener", "Error")
                }
            }
            passedReference.addValueEventListener(passedListener)
            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
            val currentDate = sdf.format(Date())

            mSosReference!!.child("passedby").child(Prefs.getString(PrefKeys.MOBILE_NUMBER, ""))
                .setValue("" + currentDate)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setEmergencyContacts() {

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

        val ambulanceCard = findViewById<View>(R.id.call_ambulance)
        val policeCard = findViewById<View>(R.id.call_police)
        val fireCard = findViewById<View>(R.id.call_fire)
        for (emer in contacts) {

            var view = policeCard
            if (emer.phoneNum.equals("100")) {
                view = policeCard
            } else if (emer.phoneNum.equals("101")) {
                view = fireCard
            } else if (emer.phoneNum.equals("108")) {
                view = ambulanceCard
            }
            val _textView: TextView = view.findViewById(R.id.textview)
            val _imageView: ImageView = view.findViewById(R.id.imageview)
            val _imageView2: ImageView = view.findViewById(R.id.img2)
            val _textView2: TextView = view.findViewById(R.id.t2)
            _imageView.setImageBitmap(emer.image)
            _textView.text = emer.text
            _imageView2.setImageBitmap(emer.image2)
            _textView2.text = emer.phoneNum

        }

        ambulanceCard.setOnClickListener(this)
        policeCard.setOnClickListener(this)
        fireCard.setOnClickListener(this)


        //val gridLayoutManager = androidx.recyclerview.widget.GridLayoutManager(this@SosGateAppActivity, 3,GridLayoutManager.HORIZONTAL,false)
//        val spanSizeLookup = object : androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup() {
//            override fun getSpanSize(position: Int): Int {
//                return 3
//            }
//        }
//        gridLayoutManager.spanSizeLookup = spanSizeLookup

//        val linearLM = LinearLayoutManager(this@SosGateAppActivity,LinearLayoutManager.HORIZONTAL,false);
//        rcv_emergency.setLayoutManager(linearLM)
//        val adapter = EmrgencyContactAdapter(contacts,clickListener = {
//                unit,index -> onEmergencyClick(unit,index)
//        })
//        rcv_emergency.adapter = adapter
    }

    fun onEmergencyClick(phone: String) {
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:" + phone)
        startActivity(intent)
    }

    override fun onBackPressed() {
        if (isBackEnabled) {
//            super.onBackPressed()
            finish()
        }
    }


    override fun onMapReady(p0: GoogleMap) {
        try {
            mMap = p0
            mMap.uiSettings.isZoomControlsEnabled = true
            setUpMap()
            updateSOSLocation()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        return true
    }

    private fun getDirectionURL(origin: LatLng, dest: LatLng): String {
        return "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}&destination=${dest.latitude},${dest.longitude}&sensor=true&mode=walking&key=" + resources.getString(
            R.string.google_maps_key
        )
    }

    private inner class GetDirection(val url: String) :
        AsyncTask<Void, Void, List<List<LatLng>>>() {
        override fun doInBackground(vararg params: Void?): List<List<LatLng>> {

            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val data = response.body()!!.string()
            Log.e("GoogleMap", " data : $data")
            val result = ArrayList<List<LatLng>>()
            try {
                val respObj = Gson().fromJson(data, GoogleMapDTO::class.java)

                val path = ArrayList<LatLng>()

                try {

                    for (i in 0..(respObj.routes[0].legs[0].steps.size - 1)) {
//                    val startLatLng = LatLng(respObj.routes[0].legs[0].steps[i].start_location.lat.toDouble()
//                            ,respObj.routes[0].legs[0].steps[i].start_location.lng.toDouble())
//                    path.add(startLatLng)
//                    val endLatLng = LatLng(respObj.routes[0].legs[0].steps[i].end_location.lat.toDouble()
//                            ,respObj.routes[0].legs[0].steps[i].end_location.lng.toDouble())
                        path.addAll(decodePolyline(respObj.routes[0].legs[0].steps[i].polyline.points))
                    }
                }catch (e:IndexOutOfBoundsException){

                }
                result.add(path)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return result
        }

        override fun onPostExecute(result: List<List<LatLng>>) {
            try {
                lineoption = PolylineOptions()
                for (i in result.indices) {
                    lineoption.addAll(result[i])
                    lineoption.width(10f)
                    lineoption.color(Color.BLACK)
                    lineoption.geodesic(true)
                }
                mPolyline = mMap.addPolyline(lineoption)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun decodePolyline(encoded: String): List<LatLng> {

        var poly = ArrayList<LatLng>()
        try {
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

                val latLng = LatLng((lat.toDouble() / 1E5), (lng.toDouble() / 1E5))
                poly.add(latLng)
            }
            return poly
        } catch (e: java.lang.Exception) {
            return poly
        }


    }
}

