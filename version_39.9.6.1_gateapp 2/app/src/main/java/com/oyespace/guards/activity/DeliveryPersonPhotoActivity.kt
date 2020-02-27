package com.oyespace.guards.activity

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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
import com.oyespace.guards.PojoClasses.GetLatestRecord
import com.oyespace.guards.R
import com.oyespace.guards.camtest.CarImages_Adapter
import com.oyespace.guards.camtest.ImageAdapter
import com.oyespace.guards.camtest.ImageHelper
import com.oyespace.guards.constants.PrefKeys
import com.oyespace.guards.network.ChampApiClient
import com.oyespace.guards.network.ChampApiInterface
import com.oyespace.guards.network.CommonDisposable
import com.oyespace.guards.network.RetrofitClinet
import com.oyespace.guards.utils.*
import com.oyespace.guards.utils.ConstantUtils.MOBILENUMBER
import com.oyespace.guards.utils.ConstantUtils.PERSON_PHOTO
import com.oyespace.guards.utils.RandomUtils.getBitmapFromURL
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class DeliveryPersonPhotoActivity : AppCompatActivity() , View.OnClickListener {

    var file: File? = null
    var encodedImage: String? = null
    var iv_torch: Button? = null
    var clickable1 = 0
    var champApiInterface: ChampApiInterface? = null
    private val REQUEST_CODE_SPEECH_INPUT = 100
    var Ed_Name: EditText? = null
    var image_Gallery: Button? = null
    var submit_button: android.widget.Button? = null
    var buttonCapture: android.widget.Button? = null
    private val viewPager: ViewPager? = null
    var imageAdapter: ImageAdapter? = null
    var rv_image: RecyclerView? = null
    var iv_delete: ImageView? = null
    var imageView1: android.widget.ImageView? = null
    var iv_edit: android.widget.ImageView? = null
    val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034
    var photo: Bitmap? = null
    var images: Bitmap? = null
    var tv_name: TextView? = null
    var txt_assn_name: TextView? = null
    var txt_device_name: TextView? = null
    var txt_gate_name: TextView? = null
    var imgName: String? = null
    var dialog_imageview: ImageView? = null
    var list = ArrayList<String>()

    val REQUEST_CAMERA = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_car_fragment)
        initViews()
        Dexter.withActivity(this)
            .withPermissions( //   Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) { // check if all permissions are granted
                    if (report.areAllPermissionsGranted()) { // do you work now
                    }
                    // check for permanent denial of any permission
                    if (report.isAnyPermissionPermanentlyDenied) { // permission is denied permenantly, navigate user to app settings
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
        iv_torch = findViewById(R.id.iv_torch)
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
        champApiInterface =
            ChampApiClient.getClient().create(ChampApiInterface::class.java)
        tv_name = findViewById(R.id.tv_name)
        tv_name!!.setText(intent.getStringExtra(ConstantUtils.PERSONNAME))
        txt_assn_name = findViewById(R.id.txt_assn_name)
        txt_device_name = findViewById(R.id.txt_device_name)
        txt_gate_name = findViewById(R.id.txt_gate_name)
        image_Gallery = findViewById(R.id.btnCaptureItemPhoto)
        //iamgeLyt = (LinearLayout) findViewById(R.id.imageLyt);
        rv_image = findViewById(R.id.rv_image)
        buttonCapture = findViewById(R.id.buttonCapture)
        imageView1 = findViewById(R.id.imageView1)
        if (Prefs.getString(PrefKeys.MODEL_NUMBER, null) == "Nokia 1") {
            txt_assn_name!!.setTextSize(5 * resources.displayMetrics.density)
        }
        getLatestRecordData(intent.getStringExtra(MOBILENUMBER),intent.getIntExtra(ConstantUtils.ACCOUNT_ID, 0).toString())
        imageView1!!.setOnClickListener(View.OnClickListener {
            val viewGroup = findViewById<ViewGroup>(android.R.id.content)
            val dialogView = LayoutInflater.from(this@DeliveryPersonPhotoActivity)
                .inflate(R.layout.dialog_big_image, viewGroup, false)
            val builder =
                AlertDialog.Builder(this@DeliveryPersonPhotoActivity)
            dialog_imageview =
                dialogView.findViewById(R.id.dialog_imageview)
            dialog_imageview!!.setBackground(imageView1!!.getDrawable())
            builder.setView(dialogView)
            //finally creating the alert dialog and displaying it
            val alertDialog = builder.create()
            alertDialog.show()
        })
        txt_assn_name!!.setText("Society: " + LocalDb.getAssociation().asAsnName)
        txt_gate_name!!.setText("Gate No: " + Prefs.getString(ConstantUtils.GATE_NO, null))
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
        image_Gallery!!.setOnClickListener(this)
        submit_button = findViewById(R.id.buttonNext)
        val mLayoutManager: RecyclerView.LayoutManager = GridLayoutManager(applicationContext, 4)
        rv_image!!.setLayoutManager(mLayoutManager)
        //iamgeLyt.removeAllViews();
        list.clear()
        buttonCapture!!.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
        }
        submit_button!!.setOnClickListener {
           submit_button!!.isEnabled = false
            submit_button!!.isClickable = false
            if (imageView1!!.getDrawable().constantState !== resources.getDrawable(R.drawable.user_icon_black).constantState) {
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
                encodedImage = RandomUtils.encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 100)
                val d = Intent(this@DeliveryPersonPhotoActivity, StaffEntryRegistration::class.java)
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
                d.putExtra(ConstantUtils.ITEMS_PHOTO_LIST,list)
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
            } else {
                Toast.makeText(applicationContext, "Capture Photo ", Toast.LENGTH_SHORT)
                    .show()
               submit_button!!.isEnabled = true
               submit_button!!.isClickable = true
            }
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnCaptureItemPhoto -> {
                //                selectImage();
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, REQUEST_CAMERA)
            }
        }
    }

    fun showViewPager() {
        Log.e("Data", "Recicved")
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
        setviewPager(file!!.absoluteFile.toString(), context)
        //
        val ddc = Intent(this@DeliveryPersonPhotoActivity, BackgroundSyncReceiver::class.java)
        Log.d("btn_biometric", "af " + file!!.absoluteFile)
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
                image_Gallery!!.visibility = View.VISIBLE
            }
            val imageView = LayoutInflater.from(context).inflate(
                R.layout.image_list,
                null
            ) as ImageView
            list.add(selectedImagePath)
            ImageHelper.loadImage(context, selectedImagePath, imageView)
            imageAdapter = ImageAdapter(list, this@DeliveryPersonPhotoActivity, "On")
            rv_image!!.adapter = imageAdapter
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
            1 -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) { // takePhoto();
            }
        }
    }


    fun hideViewPager() {
        image_Gallery!!.visibility = View.VISIBLE
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_SPEECH_INPUT -> if (resultCode == Activity.RESULT_OK && null != data) {
                val result = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                Ed_Name!!.setText(result[0])
                tv_name!!.text = result[0]
            }
            REQUEST_CAMERA -> if (resultCode == Activity.RESULT_OK) {
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


    fun getLatestRecordData(mobileNumber:String,accountId:String) {
        RetrofitClinet.instance.getLatestRecord(
            ConstantUtils.OYE247TOKEN,
            mobileNumber,
            LocalDb.getAssociation()!!.asAssnID.toString()
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CommonDisposable<GetLatestRecord>() {

                override fun onSuccessResponse(getdata: GetLatestRecord) {

                    try {
                        if(getdata.data.visitorLatestRecord.vlEntryImg.contains("PERSON")){

                            val url = ConstantUtils.IMAGE_BASE_URL + "Images/" + getdata.data.visitorLatestRecord.vlEntryImg

                            GetImageFromUrl(imageView1!!).execute(url);

//                           val image= getBitmapFromURL(ConstantUtils.IMAGE_BASE_URL + "Images/" + getdata.data.visitorLatestRecord.vlEntryImg)
//
//                            encodedImage = RandomUtils.encodeToBase64(image, Bitmap.CompressFormat.JPEG, 100)
//                            val imageBytes = Base64.decode(encodedImage, Base64.DEFAULT)
//                            val decodedImage =
//                                BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
//                            imageView1!!.setImageBitmap(decodedImage)
                        }
                        else {
                            val imageBytes = Base64.decode(getdata.data.visitorLatestRecord.vlEntryImg, Base64.DEFAULT)
                            val decodedImage =
                                BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                            imageView1!!.setImageBitmap(decodedImage)
                        }
                    }catch (e:IllegalStateException){

                    }

                }

                override fun onErrorResponse(e: Throwable) {
                    // visitorLog(unitId, personName, mobileNumb, desgn, workerType, staffID, unitName,wkEntryImg)
                    //  visitorLogBiometric(unitId, personName, mobileNumb, desgn, workerType, staffID, unitName,wkEntryImg)


                }

                override fun noNetowork() {
                    Toast.makeText(
                        this@DeliveryPersonPhotoActivity,
                        "No network call ",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })

    }
}



