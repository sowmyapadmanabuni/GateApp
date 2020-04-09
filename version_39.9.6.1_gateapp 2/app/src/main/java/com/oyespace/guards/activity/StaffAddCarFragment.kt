package com.oyespace.guards.activity

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat.setBackground
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.oyespace.guards.BackgroundSyncReceiver
import com.oyespace.guards.R
import com.oyespace.guards.camtest.CarImages_Adapter
import com.oyespace.guards.camtest.ImageAdapter
import com.oyespace.guards.camtest.ImageHelper
import com.oyespace.guards.constants.PrefKeys
import com.oyespace.guards.network.ChampApiClient
import com.oyespace.guards.network.ChampApiInterface
import com.oyespace.guards.network.RestClient
import com.oyespace.guards.network.URLData
import com.oyespace.guards.request.SendStaffImageReq
import com.oyespace.guards.request.StaffRegistrationReqJv
import com.oyespace.guards.responce.StaffImageRes
import com.oyespace.guards.responce.StaffRegistrationRespJv
import com.oyespace.guards.staff.StaffAddCarFragment
import com.oyespace.guards.utils.*
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class StaffAddCarFragment : AppCompatActivity(), View.OnClickListener {
    var file: File? = null
    var encodedImage: String? = null
    var iv_torch: Button? = null
    var clickable1 = 0
    var champApiInterface: ChampApiInterface? = null

    private val REQUEST_CODE_SPEECH_INPUT = 100
    var Ed_Name: EditText? = null
    var image_Gallery: Button? = null
    var submit_button:android.widget.Button? = null
    var buttonCapture:android.widget.Button? = null
    private val viewPager: ViewPager? = null
    var imageAdapter: ImageAdapter? = null
    var rv_image: RecyclerView? = null
    var iv_delete: ImageView? = null
    var imageView1:android.widget.ImageView? = null
    var iv_edit:android.widget.ImageView? = null
    val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034
    var photo: Bitmap? = null
    var images: Bitmap? = null
    var tv_name: TextView? = null
    var txt_assn_name: TextView? = null
    var txt_device_name:TextView? = null
    var txt_gate_name:TextView? = null
    private val view: View? = null
    var imgName: String? = null
    var dialog_imageview: ImageView? = null
    var list = ArrayList<String>()
    val REQUEST_CAMERA = 0
    val SELECT_FILE = 1
    val PICK_INSURANCE_REQUEST_CODE = 2
    val PICK_RCBOOK_REQUEST_CODE = 3



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocale(Prefs.getString(PrefKeys.LANGUAGE, null))
        setContentView(R.layout.add_car_fragment)
        initViews()
        // String imgName="PERSON"+"Association"+Prefs.getInt(ASSOCIATION_ID,0)+"STAFF" +workerResponce.data.worker.wkWorkID  + ".jpg";
        Dexter.withActivity(this)
            .withPermissions( //   Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    // check if all permissions are granted
                    if (report.areAllPermissionsGranted()) {
                        // do you work now
                    }

                    // check for permanent denial of any permission
                    if (report.isAnyPermissionPermanentlyDenied) {
                        // permission is denied permenantly, navigate user to app settings
                        finish()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            })
            .onSameThread()
            .check()
    }


    private val target: Target = object : Target {
        override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
            photo = bitmap
            imageView1!!.setImageBitmap(photo)
            Log.d("target picas", "onBitmapLoaded")
        }

        override fun onBitmapFailed(errorDrawable: Drawable) {
            Log.d(
                "target picas",
                "7onBitmapFailed " + ConstantUtils.IMAGE_BASE_URL + "Images/" + "PERSON" + intent.getIntExtra(
                    ConstantUtils.ACCOUNT_ID,
                    0
                ) + ".jpg"
            )
        }

        override fun onPrepareLoad(placeHolderDrawable: Drawable) {
            Log.d("target picas", "7 onPrepareLoad ")
        }
    }

    private fun initViews() {
        iv_torch = findViewById<Button>(R.id.iv_torch)
        iv_torch!!.setOnClickListener(View.OnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val camManager =
                    getSystemService(Context.CAMERA_SERVICE) as CameraManager
                var cameraId: String? = null
                try {
                    cameraId = camManager.cameraIdList[0]
                    if (clickable1 == 0) {
                        try {
                            iv_torch!!.setBackground(resources.getDrawable(R.drawable.torch_off))
                            camManager.setTorchMode(cameraId, true) //Turn ON

                            //  iv_torch!!.text = "OFF"
                            clickable1 = 1
                        } catch (e: CameraAccessException) {
                            e.printStackTrace()
                        }
                    } else if (clickable1 == 1) {
                        camManager.setTorchMode(cameraId, false)
                        // iv_torch!!.text = "ON"
                        iv_torch!!.setBackground(resources.getDrawable(R.drawable.torch_on))
                        clickable1 = 0
                    }
                } catch (e: CameraAccessException) {
                    e.printStackTrace()
                }
            }
        })
        // makeArrayList = new ArrayList<>();

//        viewPager_Image = (ViewPager) view.findViewById(R.id.add_car_view_pager);
        //GridView gridview
        champApiInterface =
            ChampApiClient.getClient().create(ChampApiInterface::class.java)
        tv_name = findViewById<TextView>(R.id.tv_name)
        iv_edit = findViewById<ImageView>(R.id.iv_edit)
        tv_name!!.setText(intent.getStringExtra(ConstantUtils.PERSONNAME))
        txt_assn_name = findViewById<TextView>(R.id.txt_assn_name)
        txt_device_name = findViewById<TextView>(R.id.txt_device_name)
        txt_gate_name = findViewById<TextView>(R.id.txt_gate_name)
        StaffAddCarFragment.image_Gallery =
            findViewById(R.id.btnCaptureItemPhoto)
        //iamgeLyt = (LinearLayout) findViewById(R.id.imageLyt);
        rv_image = findViewById<RecyclerView>(R.id.rv_image)
        StaffAddCarFragment.buttonCapture =
            findViewById(R.id.buttonCapture)
        imageView1 = findViewById<ImageView>(R.id.imageView1)

//        if(Prefs.getString("Retake",null).equals("Yes")){
//            buttonCapture.setText("RETAKE PERSON PHOTO");
//        }
        if (Prefs.getString(PrefKeys.MODEL_NUMBER, null) == "Nokia 1") {
            txt_assn_name!!.setTextSize(5 * resources.displayMetrics.density)
        }
        imageView1!!.setOnClickListener(View.OnClickListener {
            val viewGroup = findViewById<ViewGroup>(android.R.id.content)
            val dialogView = LayoutInflater.from(this@StaffAddCarFragment)
                .inflate(R.layout.dialog_big_image, viewGroup, false)
            val builder =
                AlertDialog.Builder(this@StaffAddCarFragment)
            dialog_imageview =
                dialogView.findViewById<ImageView>(R.id.dialog_imageview)
            dialog_imageview!!.setBackground(imageView1!!.getDrawable())
            builder.setView(dialogView)

            //finally creating the alert dialog and displaying it
            val alertDialog = builder.create()
            alertDialog.show()
        })

//        if() {
//
//            buttonCapture.setText(getResources().getString(R.string.texttakepersonphoto));
//        }
//        else {
//            buttonCapture.setText(getResources().getString(R.string.textretakepersonphoto));
//
//        }
        txt_assn_name!!.setText("Society: " + LocalDb.getAssociation().asAsnName)
        txt_gate_name!!.setText("Gate No: " + Prefs.getString(ConstantUtils.GATE_NO, null))
        //        txt_device_name.setText("Gate: "+Prefs.getInt(ASSOCIATION_ID,0) );
        try {
            var appVersion = ""
            val manager = baseContext.packageManager
            val info =
                manager.getPackageInfo(baseContext.packageName, 0)
            appVersion = info.versionName
            Log.d("tag", "app " + appVersion + " " + info.versionName)
            txt_device_name!!.setText("V: $appVersion")
        } catch (ex: Exception) {
            ex.printStackTrace()
            txt_device_name!!.setText(" ")
        }

//        floatButton = (FloatingActionButton) view.findViewById(R.id.fab);
        StaffAddCarFragment.image_Gallery.setOnClickListener(this)

//        floatButton.setOnClickListener(this);
        StaffAddCarFragment.submit_button =
            findViewById(R.id.buttonNext)
        val mLayoutManager: RecyclerView.LayoutManager =
            GridLayoutManager(applicationContext, 4)
        rv_image!!.setLayoutManager(mLayoutManager)

        //iamgeLyt.removeAllViews();
        list.clear()

//        Log.d("intentdata ", " AddCarFragment " + getIntent().getStringExtra(UNITNAME) + " " + getIntent().getStringExtra(UNITID)
//                + " " + getIntent().getStringExtra(MOBILENUMBER) + " " + getIntent().getStringExtra(COUNTRYCODE) + " " + getIntent().getStringExtra(PERSONNAME));
        if (intent.getStringExtra(ConstantUtils.FLOW_TYPE) == ConstantUtils.STAFF_REGISTRATION) {
            StaffAddCarFragment.image_Gallery.visibility = View.INVISIBLE
            rv_image!!.setVisibility(View.INVISIBLE)
            (findViewById<View>(R.id.txt_header) as TextView).text =
                getString(R.string.textpersonphotoscreen)
        } else {
            if (intent.getIntExtra(ConstantUtils.ACCOUNT_ID, 0) != 0) {
                Picasso.with(this).load(
                    ConstantUtils.IMAGE_BASE_URL + "Images/" + "PERSON" + intent.getStringExtra(
                        ConstantUtils.MOBILENUMBER
                    ) + ".jpg"
                ).into(target)
                imageView1!!.setImageBitmap(photo)


//                    Log.v("IIIIII","Images/" + "PERSONNONREGULAR" + getIntent().getStringExtra(MOBILENUMBER).replace("+91", "") + ".jpg");
//                    Glide.with(this)
//                            .load(Uri.parse(IMAGE_BASE_URL + "Images/" + "PERSON" + getIntent().getStringExtra(MOBILENUMBER).replace("+91", "") + ".jpg"))
//                            .placeholder(R.drawable.user_icon_black)
//                            .diskCacheStrategy(DiskCacheStrategy.NONE)
//                            .skipMemoryCache(false)
//                            .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
//                            .into(imageView1);
                Picasso.with(this)
                    .load(
                        ConstantUtils.IMAGE_BASE_URL + "Images/PERSON" + intent.getStringExtra(
                            ConstantUtils.MOBILENUMBER
                        ).replace("+91", "") + ".jpg"
                    )
                    .placeholder(R.drawable.user_icon_black).error(R.drawable.user_icon_black)
                    .into(imageView1)
            }
        }
        StaffAddCarFragment.buttonCapture.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
        }
        iv_edit!!.setOnClickListener(View.OnClickListener {
            val viewGroup = findViewById<ViewGroup>(android.R.id.content)
            val dialogView = LayoutInflater.from(this@StaffAddCarFragment)
                .inflate(R.layout.layout_editname_dailog, viewGroup, false)
            val builder =
                AlertDialog.Builder(this@StaffAddCarFragment)
            Ed_Name = dialogView.findViewById<EditText>(R.id.Ed_Name)
            val btn_Mic =
                dialogView.findViewById<ImageView>(R.id.btn_Mic)
            btn_Mic.setOnClickListener { speak() }
            val btn_done =
                dialogView.findViewById<Button>(R.id.btn_done)
            builder.setView(dialogView)

            //finally creating the alert dialog and displaying it
            val alertDialog = builder.create()
            btn_done.setOnClickListener { alertDialog.dismiss() }
            alertDialog.show()
        })
        StaffAddCarFragment.submit_button.setOnClickListener(View.OnClickListener {
            StaffAddCarFragment.submit_button.isEnabled = false
            StaffAddCarFragment.submit_button.isClickable = false
            if (!Utils.isConnectedToInternet()) {
                StaffAddCarFragment.submit_button.isEnabled = true
                StaffAddCarFragment.submit_button.isEnabled = true
                Utils.showToast(
                    this@StaffAddCarFragment,
                    getString(R.string.no_internet)
                )
                return@OnClickListener
            }
            if (imageView1!!.getDrawable()
                    .getConstantState() !== resources.getDrawable(R.drawable.user_icon_black)
                    .constantState
            ) {
                if (intent.getStringExtra(ConstantUtils.FLOW_TYPE)
                        .equals(ConstantUtils.STAFF_REGISTRATION, ignoreCase = true)
                ) {
                    StaffAddCarFragment.submit_button.isEnabled = false
                    StaffAddCarFragment.submit_button.isClickable = false
                    //staffRegistration()
                } else {
                    var byteArray: ByteArray? = null
                    try {
                        Log.d("Dgddfdf picas", "5 2")
                        val bos = ByteArrayOutputStream()
                        photo!!.compress(Bitmap.CompressFormat.JPEG, 100, bos)
                        byteArray = bos.toByteArray()
                        val len = bos.toByteArray().size
                        println("AFTER COMPRESSION-===>$len")
                        bos.flush()
                        bos.close()
                    } catch (ex: Exception) {
                        Log.d("Dgddfdf picas", "7")
                    }
                    val drawable = imageView1!!.getDrawable() as BitmapDrawable
                    val bitmap = drawable.bitmap
                    encodedImage =
                        RandomUtils.encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 100)
                    val d =
                        Intent(this@StaffAddCarFragment, StaffEntryRegistration::class.java)
                    //                        Log.d("intentdata personPhoto", "buttonNext " + getIntent().getStringExtra(UNITNAME) + " " + getIntent().getStringExtra(UNITID)
                    //                                + " " + getIntent().getStringExtra(MOBILENUMBER) + " " + getIntent().getStringExtra(COUNTRYCODE) + " " + getIntent().getStringExtra(PERSONNAME));
                    d.putExtra(
                        ConstantUtils.UNITID,
                        intent.getStringExtra(ConstantUtils.UNITID)
                    )
                    d.putExtra(
                        ConstantUtils.UNITNAME,
                        intent.getStringExtra(ConstantUtils.UNITNAME)
                    )
                    d.putExtra(
                        ConstantUtils.FLOW_TYPE,
                        intent.getStringExtra(ConstantUtils.FLOW_TYPE)
                    )
                    d.putExtra(
                        ConstantUtils.VISITOR_TYPE,
                        intent.getStringExtra(ConstantUtils.VISITOR_TYPE)
                    )
                    d.putExtra(
                        ConstantUtils.COMPANY_NAME,
                        intent.getStringExtra(ConstantUtils.COMPANY_NAME)
                    )
                    d.putExtra(
                        ConstantUtils.MOBILENUMBER,
                        intent.getStringExtra(ConstantUtils.MOBILENUMBER)
                    )
                    d.putExtra(
                        ConstantUtils.COUNTRYCODE,
                        intent.getStringExtra(ConstantUtils.COUNTRYCODE)
                    )
                    d.putExtra(
                        ConstantUtils.PERSONNAME,
                        intent.getStringExtra(ConstantUtils.PERSONNAME)
                    )
                    d.putExtra(ConstantUtils.PERSON_PHOTO, byteArray)
                    d.putExtra(
                        ConstantUtils.ITEMS_PHOTO_LIST,
                        list
                    )
                    d.putExtra(
                        ConstantUtils.ACCOUNT_ID,
                        intent.getIntExtra(ConstantUtils.ACCOUNT_ID, 0)
                    )
                    d.putExtra(
                        ConstantUtils.UNIT_ACCOUNT_ID,
                        intent.getStringExtra(ConstantUtils.UNIT_ACCOUNT_ID)
                    )
                    d.putExtra(
                        ConstantUtils.BLOCK_ID,
                        intent.getStringExtra(ConstantUtils.BLOCK_ID)
                    )
                    d.putExtra(
                        ConstantUtils.UNITOCCUPANCYSTATUS,
                        intent.getStringExtra(ConstantUtils.UNITOCCUPANCYSTATUS)
                    )
                    d.putExtra(
                        ConstantUtils.VISITOR_PURPOSE,
                        intent.getStringExtra(ConstantUtils.VISITOR_PURPOSE)
                    )
                    d.putExtra("Base64", encodedImage)
                    startActivity(d)
                    finish()
                }
            } else {
                Toast.makeText(applicationContext, "Capture Photo ", Toast.LENGTH_SHORT)
                    .show()
                StaffAddCarFragment.submit_button.isEnabled = true
                StaffAddCarFragment.submit_button.isClickable = true
            }
        })
    }

    fun onFailure(e: Exception, urlId: Int) {
        Utils.showToast(this, "Staff Registration Failed")
        Log.e("taaag", "e: " + e.message)
        e.printStackTrace()
    }

//    private fun staffRegistration() {
//        val drawable = imageView1!!.getDrawable() as BitmapDrawable
//        val bitmap = drawable.bitmap
//        encodedImage = RandomUtils.encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 100)
//        val restClient = RestClient.getInstance()
//        val loginReq = StaffRegistrationReqJv()
//        loginReq.ASAssnID = Prefs.getInt(ConstantUtils.ASSOCIATION_ID, 0)
//        loginReq.BLBlockID = intent.getStringExtra(ConstantUtils.BLOCK_ID)
//        loginReq.VNVendorID = 0
//        loginReq.WKDesgn = intent.getStringExtra(ConstantUtils.COMPANY_NAME)
//        loginReq.WKFName = intent.getStringExtra(ConstantUtils.PERSONNAME)
//        loginReq.WKIDCrdNo = ""
//        loginReq.WKDOB = intent.getStringExtra("DOB")
//        loginReq.WKISDCode = ""
//        loginReq.WKLName = ""
//        loginReq.WKMobile = intent.getStringExtra(ConstantUtils.MOBILENUMBER)
//        loginReq.WKWrkType = intent.getStringExtra(ConstantUtils.VISITOR_TYPE)
//        loginReq.UNUnitID = intent.getStringExtra(ConstantUtils.UNITID)
//        loginReq.UNUniName = intent.getStringExtra(ConstantUtils.UNITNAME)
//        loginReq.IDPrfType = intent.getStringExtra("DocumentType")
//        loginReq.WKExpiry = intent.getStringExtra("DocumentExpiry")
//        loginReq.WKStatus = intent.getStringExtra("Status")
//        loginReq.WKExpDate = intent.getStringExtra("DocumentExpiryDate")
//        loginReq.WKEntryImg = encodedImage
//        // loginReq.IDPrfImg=getIntent().getStringExtra("DocumentImage");
//        Log.d(
//            "saveCheckPoints", "StaffEntry " + loginReq.ASAssnID + " " + loginReq.WKFName + " "
//                    + loginReq.UNUnitID + " " + loginReq.WKMobile + " " + loginReq.UNUniName
//        )
//        restClient.addHeader(ConstantUtils.OYE247KEY, ConstantUtils.OYE247TOKEN)
//        restClient.post<Any>(this, loginReq, StaffRegistrationRespJv::class.java, this, URLData.URL_STAFF_REGISTRATION)
//    }

    fun onSuccess(
        responce: String?,
        data: Any?,
        urlId: Int,
        position: Int
    ) {
        if (urlId == URLData.URL_STAFF_REGISTRATION.urlId) {
            val workerResponce = data as StaffRegistrationRespJv?
            if (workerResponce != null) {
                Log.d(
                    "str3",
                    "str3: " + urlId + " id " + position + " " + " " + " " + workerResponce.success
                )
                if (workerResponce.success.equals("true", ignoreCase = true)) {
                    imgName = "PERSON" + "Association" + Prefs.getInt(
                        ConstantUtils.ASSOCIATION_ID,
                        0
                    ) + "STAFF" + workerResponce.data.worker.wkWorkID + ".jpg"

                    //   sendStaffImage(imgName, "", String.valueOf(workerResponce.data.worker.wkWorkID));
                    val intent_ =
                        Intent(this@StaffAddCarFragment, BackgroundSyncReceiver::class.java)
                    intent_.putExtra(ConstantUtils.BSR_Action, ConstantUtils.VisitorEntryFCM)
                    intent_.putExtra(
                        "msg",
                        intent.getStringExtra(ConstantUtils.PERSONNAME) + " from " + intent.getStringExtra(
                            ConstantUtils.COMPANY_NAME
                        ) + " is coming to your home" + "(" + intent.getStringExtra(
                            ConstantUtils.UNITNAME
                        ) + ")"
                    )
                    intent_.putExtra(
                        "mobNum",
                        intent.getStringExtra(ConstantUtils.MOBILENUMBER)
                    )
                    intent_.putExtra("name", intent.getStringExtra(ConstantUtils.PERSONNAME))
                    intent_.putExtra("nr_id", workerResponce.data.worker.wkWorkID.toString())
                    intent_.putExtra("unitname", intent.getStringExtra(ConstantUtils.UNITNAME))
                    intent_.putExtra("memType", "Owner")
                    intent_.putExtra(
                        ConstantUtils.UNITID,
                        intent.getStringExtra(ConstantUtils.UNITID)
                    )
                    intent_.putExtra(
                        ConstantUtils.COMPANY_NAME,
                        intent.getStringExtra(ConstantUtils.COMPANY_NAME)
                    )
                    intent_.putExtra(ConstantUtils.UNIT_ACCOUNT_ID, "")
                    intent_.putExtra("VLVisLgID", workerResponce.data.worker.wkWorkID)
                    intent_.putExtra(ConstantUtils.VISITOR_TYPE, "Staff")
                    intent_.putExtra(ConstantUtils.UNITOCCUPANCYSTATUS, "")
                    sendBroadcast(intent_)
                    var byteArray: ByteArray? = null
                    try {
                        Log.d("Dgddfdf picas", "5 2")
                        val bos = ByteArrayOutputStream()
                        photo!!.compress(Bitmap.CompressFormat.JPEG, 100, bos)
                        byteArray = bos.toByteArray()
                        val len = bos.toByteArray().size
                        println("AFTER COMPRESSION-===>$len")
                        bos.flush()
                        bos.close()
                    } catch (ex: Exception) {
                        Log.d("Dgddfdf picas", "7")
                    }
                    val drawable = imageView1!!.getDrawable() as BitmapDrawable
                    val bitmap = drawable.bitmap
                    encodedImage =
                        RandomUtils.encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 100)
                    if (Prefs.getString(PrefKeys.MODEL_NUMBER, null) == "Nokia 2.1") {
                        val d = Intent(this@StaffAddCarFragment, Biometric::class.java)
                        d.putExtra(ConstantUtils.WORKER_ID, workerResponce.data.worker.wkWorkID)
                        d.putExtra(
                            ConstantUtils.PERSONNAME,
                            intent.getStringExtra(ConstantUtils.PERSONNAME)
                        )
                        d.putExtra(
                            ConstantUtils.UNITID,
                            intent.getStringExtra(ConstantUtils.UNITID)
                        )
                        d.putExtra(
                            ConstantUtils.UNITNAME,
                            intent.getStringExtra(ConstantUtils.UNITNAME)
                        )
                        d.putExtra(
                            ConstantUtils.FLOW_TYPE,
                            intent.getStringExtra(ConstantUtils.FLOW_TYPE)
                        )
                        d.putExtra(
                            ConstantUtils.VISITOR_TYPE,
                            intent.getStringExtra(ConstantUtils.VISITOR_TYPE)
                        )
                        d.putExtra(
                            ConstantUtils.COMPANY_NAME,
                            intent.getStringExtra(ConstantUtils.COMPANY_NAME)
                        )
                        d.putExtra(
                            ConstantUtils.MOBILENUMBER,
                            intent.getStringExtra(ConstantUtils.MOBILENUMBER)
                        )
                        d.putExtra(
                            ConstantUtils.COUNTRYCODE,
                            intent.getStringExtra(ConstantUtils.COUNTRYCODE)
                        )
                        d.putExtra("Base64", encodedImage)
                        startActivity(d)
                    } else if (Prefs.getString(PrefKeys.MODEL_NUMBER, null) == "Nokia 1") {
                        val intent =
                            Intent(this@StaffAddCarFragment, StaffDetails::class.java)
                        intent.putExtra(
                            ConstantUtils.WORKER_ID,
                            workerResponce.data.worker.wkWorkID
                        )
                        intent.putExtra(
                            ConstantUtils.PERSONNAME,
                            getIntent().getStringExtra(ConstantUtils.PERSONNAME)
                        )
                        intent.putExtra(
                            ConstantUtils.UNITID,
                            getIntent().getStringExtra(ConstantUtils.UNITID)
                        )
                        intent.putExtra(
                            ConstantUtils.UNITNAME,
                            getIntent().getStringExtra(ConstantUtils.UNITNAME)
                        )
                        intent.putExtra(
                            ConstantUtils.FLOW_TYPE,
                            getIntent().getStringExtra(ConstantUtils.FLOW_TYPE)
                        )
                        intent.putExtra(
                            ConstantUtils.VISITOR_TYPE,
                            getIntent().getStringExtra(ConstantUtils.VISITOR_TYPE)
                        )
                        intent.putExtra(
                            ConstantUtils.COMPANY_NAME,
                            getIntent().getStringExtra(ConstantUtils.COMPANY_NAME)
                        )
                        intent.putExtra(
                            ConstantUtils.MOBILENUMBER,
                            getIntent().getStringExtra(ConstantUtils.MOBILENUMBER)
                        )
                        intent.putExtra(
                            ConstantUtils.COUNTRYCODE,
                            getIntent().getStringExtra(ConstantUtils.COUNTRYCODE)
                        )
                        startActivity(intent)
                        finish()
                    } else {
                        val d = Intent(this@StaffAddCarFragment, Biometric::class.java)
                        d.putExtra(ConstantUtils.WORKER_ID, workerResponce.data.worker.wkWorkID)
                        d.putExtra(
                            ConstantUtils.PERSONNAME,
                            intent.getStringExtra(ConstantUtils.PERSONNAME)
                        )
                        d.putExtra(
                            ConstantUtils.UNITID,
                            intent.getStringExtra(ConstantUtils.UNITID)
                        )
                        d.putExtra(
                            ConstantUtils.UNITNAME,
                            intent.getStringExtra(ConstantUtils.UNITNAME)
                        )
                        d.putExtra(
                            ConstantUtils.FLOW_TYPE,
                            intent.getStringExtra(ConstantUtils.FLOW_TYPE)
                        )
                        d.putExtra(
                            ConstantUtils.VISITOR_TYPE,
                            intent.getStringExtra(ConstantUtils.VISITOR_TYPE)
                        )
                        d.putExtra(
                            ConstantUtils.COMPANY_NAME,
                            intent.getStringExtra(ConstantUtils.COMPANY_NAME)
                        )
                        d.putExtra(
                            ConstantUtils.MOBILENUMBER,
                            intent.getStringExtra(ConstantUtils.MOBILENUMBER)
                        )
                        d.putExtra(
                            ConstantUtils.COUNTRYCODE,
                            intent.getStringExtra(ConstantUtils.COUNTRYCODE)
                        )
                        d.putExtra("Base64", encodedImage)
                        startActivity(d)
                    }
                    //   uploadImage(imgName,personPhoto);

//                    Intent ddc = new Intent(StaffAddCarFragment.this, BackgroundSyncReceiver.class);
//                    Log.d("btn_biometric", "af " + imgName);
//
//                    ddc.putExtra(BSR_Action, UPLOAD_STAFF_PHOTO);
//                    ddc.putExtra("imgName", imgName);
//                    ddc.putExtra(PERSON_PHOTO, byteArray);
//                    sendBroadcast(ddc);
                    val ddc1 =
                        Intent(this@StaffAddCarFragment, BackgroundSyncReceiver::class.java)
                    Log.d("SYNC_STAFF_LIST", "af $imgName")
                    ddc1.putExtra(ConstantUtils.BSR_Action, ConstantUtils.SYNC_STAFF_LIST)
                    sendBroadcast(ddc1)
                    Utils.showToast(this, "Staff Registered")
                    finish()
                } else {
                    Utils.showToast(this, "Staff not Registered")
                }
            } else {
                Utils.showToast(
                    this,
                    "Something went wrong . please try again "
                )
            }
        }
        //  showToast(this, urlId+" id "+position+" "+memId+" "+MemberType+" ");
    }

    override fun onDestroy() {  // could be in onPause or onStop
        Picasso.with(this).cancelRequest(target)
        // Glide.clear(target);
        super.onDestroy()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        //
//          Intent i=new Intent(AddCarFragment.this, Dashboard.class);
//          startActivity(i);
        finish()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnCaptureItemPhoto -> {
                //                selectImage();
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(
                    intent,
                    StaffAddCarFragment.REQUEST_CAMERA
                )
            }
        }
    }

    fun showViewPager() {
        Log.e("Data", "Recicved")
//        image_Gallery.setVisibility(View.GONE);
//        viewPager_Image.setVisibility(View.VISIBLE);
//        floatButton.setVisibility(View.VISIBLE);
    }

    fun onCaptureImageResult(data: Intent?, context: Context) {
        val thumbnail = data!!.extras["data"] as Bitmap
        val bytes = ByteArrayOutputStream()
        var byteArray: ByteArray? = null
        try {
            Log.d("Dgddfdf picas", "5 2")
            val bos = ByteArrayOutputStream()
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bos)
            byteArray = bos.toByteArray()
            val len = bos.toByteArray().size
            println("AFTER COMPRESSION-===>$len")
            bos.flush()
            bos.close()
        } catch (ex: Exception) {
            Log.d("Dgddfdf picas", "7")
        }
        assert(thumbnail != null)
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val imgName = System.currentTimeMillis().toString() + ".jpg"
        file = File(Environment.getExternalStorageDirectory().path, imgName)
        // file= new File(Environment.getExternalStorageDirectory()+ "/DCIM/myCapturedImages", imgName);
        // String fileName = imgName.substring(imgName.lastIndexOf("/")+1);
        // List<String> myList = new ArrayList<String>(Collections.singletonList(String.valueOf(destination.getAbsoluteFile())));
        setviewPager(file!!.getAbsoluteFile().toString(), context)
        //
        val ddc = Intent(this@StaffAddCarFragment, BackgroundSyncReceiver::class.java)
        Log.d("btn_biometric", "af " + file!!.getAbsoluteFile())
        ddc.putExtra(ConstantUtils.BSR_Action, ConstantUtils.UPLOAD_STAFF_PHOTO)
        ddc.putExtra("imgName", imgName)
        ddc.putExtra(ConstantUtils.PERSON_PHOTO, byteArray)
        sendBroadcast(ddc)
        val fo: FileOutputStream
        try {
            file!!.createNewFile()
            fo = FileOutputStream(file)
            fo.write(bytes.toByteArray())
            fo.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    private fun setviewPager(
        selectedImagePath: String,
        context: Context
    ) {
        try {
            if (list.size == 19) {
               image_Gallery!!.visibility = View.GONE
            } else {
                StaffAddCarFragment.image_Gallery.visibility = View.VISIBLE
            }
            val imageView = LayoutInflater.from(context)
                .inflate(R.layout.image_list, null) as ImageView
            list.add(selectedImagePath)
            ImageHelper.loadImage(context, selectedImagePath, imageView)
            imageAdapter = ImageAdapter(
               list,
                this@StaffAddCarFragment,
                "On"
            )
            rv_image!!.setAdapter(imageAdapter)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // takePhoto();
            }
        }
    }

    fun takePhoto() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), 1)
            } else {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(
                    intent,
                    StaffAddCarFragment.REQUEST_CAMERA
                )
                //   take_picture();
            }
        }
//        else{
//            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            startActivityForResult(intent, REQUEST_CAMERA);
//        }
    }

    fun hideViewPager() {
//        viewPager_Image.setVisibility(View.GONE);
//        floatButton.setVisibility(View.GONE);
        StaffAddCarFragment.image_Gallery.visibility = View.VISIBLE
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_SPEECH_INPUT -> if (resultCode == Activity.RESULT_OK && null != data) {
                val result = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                Ed_Name!!.setText(result[0])
                tv_name!!.setText(result[0])
            }
            StaffAddCarFragment.REQUEST_CAMERA -> if (resultCode == Activity.RESULT_OK) {
                onCaptureImageResult(data, this)
                showViewPager()
            }
            CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE -> if (resultCode == Activity.RESULT_OK) {
                photo = data!!.extras["data"] as Bitmap
                imageView1!!.setImageBitmap(photo)
                //personPhoto=photo;
                SaveImage(photo!!)
            }
        }
        Log.d("Activity", "Result Fail")
    }

    fun setLocale(lang: String?) {
        var lang = lang
        if (lang == null) {
            lang = "en"
        } else {
        }
        val myLocale = Locale(lang)
        val res = resources
        val dm = res.displayMetrics
        val conf = res.configuration
        conf.locale = myLocale
        res.updateConfiguration(conf, dm)
    }

    fun speak() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "say something")
        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
        } catch (e: Exception) {
        }
    }
//    void getVisitorLogEntryListByMobileNumber(){
//
//        Call<TicketListingTesponse> call = champApiInterface.getTicketingListResponse(EmergencyID);
//        call.enqueue(new Callback<TicketListingTesponse>() {
//            @Override
//            public void onResponse(Call<TicketListingTesponse> call, Response<TicketListingTesponse> response) {
//
//
//                if (response.body().getSuccess() == true) {
//
//                    if(response.body().getData()!=null) {
//
//                        Toast.makeText(TicketingDetailsActivity.this, response.body().toString(), Toast.LENGTH_LONG).show();
//                        System.out.println("SSSSOS" + response.body().getData().toString());
//
//                        tv_name.setText(response.body().getData().getTicketing().getTkRaisdBy());
//
//                        tv_mobileno.setText(response.body().getData().getTicketing().getTkMobile());
//
//                    }
//                } else {
//                    Toast.makeText(TicketingDetailsActivity.this, "No Data", Toast.LENGTH_LONG).show();
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call<TicketListingTesponse> call, Throwable t) {
//                call.cancel();
//            }
//        });
//
//
//    }
//


//    private void staffImageUpload(String wkEntryImg, String wkEntryGPS, String wkWorkID ) {
//
//        RestClient restClient = RestClient.getInstance();
//
//        SendStaffImageReq sendStaffImageReq = new SendStaffImageReq();
//
//
//
//        sendStaffImageReq.WKEntryImg=imgName;
//        sendStaffImageReq.WKEntryGPS=wkEntryGPS;
//        sendStaffImageReq.WKWorkID=wkWorkID;
//
//
//
//        Log.d("saveCheckPoints","StaffEntry "+sendStaffImageReq.WKWorkID+" "+sendStaffImageReq.WKEntryImg+" "
//                +sendStaffImageReq.WKEntryGPS);
//
//        restClient.addHeader(OYE247KEY, OYE247TOKEN);
//        restClient.post(this, sendStaffImageReq, StaffImageRes.class, this, URLData.URL_SEND_STAFF_IMAGE);
//
//    }


    //    void getVisitorLogEntryListByMobileNumber(){
    //
    //        Call<TicketListingTesponse> call = champApiInterface.getTicketingListResponse(EmergencyID);
    //        call.enqueue(new Callback<TicketListingTesponse>() {
    //            @Override
    //            public void onResponse(Call<TicketListingTesponse> call, Response<TicketListingTesponse> response) {
    //
    //
    //                if (response.body().getSuccess() == true) {
    //
    //                    if(response.body().getData()!=null) {
    //
    //                        Toast.makeText(TicketingDetailsActivity.this, response.body().toString(), Toast.LENGTH_LONG).show();
    //                        System.out.println("SSSSOS" + response.body().getData().toString());
    //
    //                        tv_name.setText(response.body().getData().getTicketing().getTkRaisdBy());
    //
    //                        tv_mobileno.setText(response.body().getData().getTicketing().getTkMobile());
    //
    //                    }
    //                } else {
    //                    Toast.makeText(TicketingDetailsActivity.this, "No Data", Toast.LENGTH_LONG).show();
    //
    //                }
    //            }
    //
    //            @Override
    //            public void onFailure(Call<TicketListingTesponse> call, Throwable t) {
    //                call.cancel();
    //            }
    //        });
    //
    //
    //    }
    //
    //    private void staffImageUpload(String wkEntryImg, String wkEntryGPS, String wkWorkID ) {
    //
    //        RestClient restClient = RestClient.getInstance();
    //
    //        SendStaffImageReq sendStaffImageReq = new SendStaffImageReq();
    //
    //
    //
    //        sendStaffImageReq.WKEntryImg=imgName;
    //        sendStaffImageReq.WKEntryGPS=wkEntryGPS;
    //        sendStaffImageReq.WKWorkID=wkWorkID;
    //
    //
    //
    //        Log.d("saveCheckPoints","StaffEntry "+sendStaffImageReq.WKWorkID+" "+sendStaffImageReq.WKEntryImg+" "
    //                +sendStaffImageReq.WKEntryGPS);
    //
    //        restClient.addHeader(OYE247KEY, OYE247TOKEN);
    //        restClient.post(this, sendStaffImageReq, StaffImageRes.class, this, URLData.URL_SEND_STAFF_IMAGE);
    //
    //    }
    fun sendStaffImage(
        WKEntryImg: String?,
        WKEntryGPS: String?,
        WKWorkID: String?
    ) {
        val sendStaffImageReq =
            SendStaffImageReq()
        sendStaffImageReq.WKEntryImg = WKEntryImg
        sendStaffImageReq.WKEntryGPS = WKEntryGPS
        sendStaffImageReq.WKWorkID = WKWorkID
        Log.v("IMAGE NAME....", WKEntryImg)
        //  Toast.makeText(AddCarFragment.this,WKEntryImg,Toast.LENGTH_LONG).show();
        val call: Call<StaffImageRes> =
            champApiInterface!!.sendStaffImage(sendStaffImageReq)
        call.enqueue(object : Callback<StaffImageRes?> {
            override fun onResponse(
                call: Call<StaffImageRes?>,
                response: Response<StaffImageRes?>
            ) {

                // Toast.makeText(AddCarFragment.this,"Shalinii",Toast.LENGTH_LONG).show();


//                if (response.body().getSuccess() == true) {
//                    DateFormat dateFormat_DMY = new SimpleDateFormat("dd-MM-yyyy");
//                    String CurrentString = response.body().data.getSubscription().sueDate;
//                    String[] separated = CurrentString.split("T");
//                    subscriptionDate = separated[0];
//
//                    tv_subscriptiondate.setText("Valid till: " + subscriptionDate);
//                    //  if(PrefManager.getValidityDate().length()>0) {
//                    try {
//                        java.util.Date dt_dwnld_date = dateFormat_DMY.parse(response.body().data.getSubscription().sueDate);
//                        Calendar c1 = Calendar.getInstance();
//                        c1.setTime(dt_dwnld_date);
//
//                        long days = (c1.getTimeInMillis() - System.currentTimeMillis()) / (24 * 60 * 60 * 1000) + 1;
//
//                        if (0 < days && days <= 7) {
//                            android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(DashBoard.this);
//                            alertDialog.setTitle("Your Association Subscription Expires in " + days + " days");
//                            alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.cancel();
//                                }
//                            });
//                            // Showing Alert Message
//                            if (!DashBoard.this.isFinishing()) {
//                                alertDialog.show();
//                            }
//                        }
//
//                    } catch (Exception ex) {
//
//                    }
//
//                } else {
//                }
            }

            override fun onFailure(
                call: Call<StaffImageRes?>,
                t: Throwable
            ) {
                Toast.makeText(this@StaffAddCarFragment, t.toString(), Toast.LENGTH_LONG).show()
                call.cancel()
            }
        })
    }

    fun SaveImage(showedImgae: Bitmap) {
        val root = Environment.getExternalStorageDirectory().toString()
        val myDir = File("$root/DCIM/myCapturedImages")
        myDir.mkdirs()
        val generator = Random()
        var n = 10000
        n = generator.nextInt(n)
        val fname = "FILENAME-$n.jpg"
        file = File(myDir, fname)
        if (file!!.exists()) file!!.delete()
        try {
            val out = FileOutputStream(file)
            showedImgae.compress(Bitmap.CompressFormat.JPEG, 100, out)
            // Toast.makeText(AddCarFragment.this, "Image Saved", Toast.LENGTH_SHORT).show();
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val contentUri = Uri.fromFile(file)
        mediaScanIntent.data = contentUri
        applicationContext.sendBroadcast(mediaScanIntent)
    }


}