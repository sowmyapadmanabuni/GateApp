package com.oyespace.guards.guest

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
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.oyespace.guards.PojoClasses.GetLatestRecord
import com.oyespace.guards.R
import com.oyespace.guards.camtest.CarImages_Adapter
import com.oyespace.guards.camtest.ImageAdapter
import com.oyespace.guards.camtest.ImageHelper
import com.oyespace.guards.constants.PrefKeys
import com.oyespace.guards.network.CommonDisposable
import com.oyespace.guards.network.RetrofitClinet
import com.oyespace.guards.utils.*
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class GuestPhotoScreen : AppCompatActivity(), View.OnClickListener {
    var encodedImage: String? = null
    var iv_torch: Button? = null
    var clickable1 = 0
    var txt_assn_name: TextView? = null
    var txt_device_name:TextView? = null
    var txt_gate_name:TextView? = null
    var tv_name: TextView? = null
    var file: File? = null

    //    private FloatingActionButton floatButton;
    val REQUEST_CAMERA = 0 //    private FloatingActionButton floatButton;
    val SELECT_FILE = 1  //    private FloatingActionButton floatButton;
    val PICK_INSURANCE_REQUEST_CODE = 2  //    private FloatingActionButton floatButton;
    val PICK_RCBOOK_REQUEST_CODE = 3
    var list = ArrayList<String>()
    var imageAdapter: ImageAdapter? = null
    var rv_image: RecyclerView? = null
    var iv_delete: ImageView? = null
    var imageView1:android.widget.ImageView? = null
    var iv_edit:android.widget.ImageView? = null
    var CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034
    var photo: Bitmap? = null

    /*sumeeth fragment*/
    private val fragmentManager: FragmentManager? = null
    private val viewPager: ViewPager? = null
    private val view: View? = null
    private val notes: EditText? = null
    private  var Regno:EditText? = null
    private  var kms:EditText? = null
    private  var exp_date:EditText? = null
    private  var exp_price:EditText? = null
    private  var car_id:EditText? = null
    private var image_Gallery: Button? = null
    private  var submit_button:android.widget.Button? = null
    private  var buttonCapture:android.widget.Button? = null
    private  var buttonSkip:android.widget.Button? = null

    //    private ViewPager viewPager_Image;
    private val upload_rc_book: TextView? = null

    //    private ViewPager viewPager_Image;
    private val upload_insurance: TextView? = null  //    private ViewPager viewPager_Image;
    private val Make: TextView? = null  //    private ViewPager viewPager_Image;
    private val model: TextView? = null  //    private ViewPager viewPager_Image;
    private val year: TextView? = null //    private ViewPager viewPager_Image;
    private val variant: TextView? = null  //    private ViewPager viewPager_Image;
    private val fuel: TextView? = null //    private ViewPager viewPager_Image;
    private val color: TextView? = null  //    private ViewPager viewPager_Image;
    private val transmission: TextView? = null  //    private ViewPager viewPager_Image;
    private val owners: TextView? = null  //    private ViewPager viewPager_Image;
    private val insurance: TextView? = null

    //  private LinearLayout iamgeLyt;
    private val progressDialog: ProgressDialog? = null
    private val insurance_file_name: ImageView? = null
    private  var rc_book_file_name:android.widget.ImageView? = null
    private val listpager_Array =
        ArrayList<String>()
    private val adapter: CarImages_Adapter? = null
    private val sell_to_dealer: CheckBox? = null
    private val dialog: ProgressDialog? = null

    //    private final int Date_id = 7;
    private val makeId = ""  //    private final int Date_id = 7;
    private val modelId = ""  //    private final int Date_id = 7;
    private val variant_id = ""
    private val rcImagePath: String? = null
    private  var insImagePath:kotlin.String? = null
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

    fun GuestAddCarFragment() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocale(Prefs.getString(PrefKeys.LANGUAGE, null))
        setContentView(R.layout.add_car_fragment)
        initViews()
        txt_assn_name!!.text = "Society: " + LocalDb.getAssociation().asAsnName
        txt_gate_name!!.text = "Gate No: " + Prefs.getString(ConstantUtils.GATE_NO, "")
        try {
            var appVersion = ""
            val manager = baseContext.packageManager
            val info =
                manager.getPackageInfo(baseContext.packageName, 0)
            appVersion = info.versionName
            Log.d("tag", "app " + appVersion + " " + info.versionName)
            txt_device_name!!.text = "V: $appVersion"
        } catch (ex: Exception) {
            ex.printStackTrace()
            txt_device_name!!.text = " "
        }
    }

    private fun initViews() {

        // makeArrayList = new ArrayList<>();

//        viewPager_Image = (ViewPager) view.findViewById(R.id.add_car_view_pager);
        //GridView gridview
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
        tv_name = findViewById(R.id.tv_name)
        iv_edit = findViewById(R.id.iv_edit)
        iv_edit!!.setVisibility(View.GONE)
        tv_name!!.setText(intent.getStringExtra(ConstantUtils.PERSONNAME))
        txt_gate_name = findViewById(R.id.txt_gate_name)
        txt_assn_name = findViewById(R.id.txt_assn_name)
        if (Prefs.getString(PrefKeys.MODEL_NUMBER, null) == "Nokia 1") {
            txt_assn_name!!.setTextSize(5 * resources.displayMetrics.density)
        }

        getLatestRecordData(intent.getStringExtra(ConstantUtils.MOBILENUMBER),intent.getIntExtra(ConstantUtils.ACCOUNT_ID, 0).toString())

        txt_device_name = findViewById(R.id.txt_device_name)
        image_Gallery = findViewById(R.id.btnCaptureItemPhoto)
        buttonSkip = findViewById(R.id.buttonSkip)
        // buttonSkip.setVisibility(View.GONE);
        //iamgeLyt = (LinearLayout) findViewById(R.id.imageLyt);
        rv_image = findViewById(R.id.rv_image)
        iv_delete = findViewById(R.id.iv_delete)
        //        floatButton = (FloatingActionButton) view.findViewById(R.id.fab);
        image_Gallery!!.setOnClickListener(this)
        //        floatButton.setOnClickListener(this);
        submit_button = findViewById(R.id.buttonNext)
        buttonCapture = findViewById(R.id.buttonCapture)
        imageView1 = findViewById(R.id.imageView1)
        imageView1!!.setOnClickListener(View.OnClickListener {
            val viewGroup = findViewById<ViewGroup>(android.R.id.content)
            val dialogView = LayoutInflater.from(this@GuestPhotoScreen)
                .inflate(R.layout.dialog_big_image, viewGroup, false)
            val builder =
                AlertDialog.Builder(this@GuestPhotoScreen)
            val dialog_imageview =
                dialogView.findViewById<ImageView>(R.id.dialog_imageview)
            dialog_imageview.background = imageView1!!.getDrawable()


            //Picasso.with(GuestAddCarFragment.this).load(IMAGE_BASE_URL + "Images/PERSON" + "NONREGULAR" + getIntent().getStringExtra(MOBILENUMBER) + ".jpg").placeholder(R.drawable.user_icon_black).memoryPolicy(MemoryPolicy.NO_CACHE).into(dialog_imageview);


            //                Picasso.with(GuestAddCarFragment.this)
            //                        .load(IMAGE_BASE_URL +"Images/PERSONAssociation"+Prefs.getInt(ASSOCIATION_ID,0)+"NONREGULAR"+getIntent().getStringExtra(MOBILENUMBER)+".jpg")
            //                        .placeholder(R.drawable.user_icon_black).error(R.drawable.user_icon_black).into(dialog_imageview);
            builder.setView(dialogView)

            //finally creating the alert dialog and displaying it
            val alertDialog = builder.create()
            alertDialog.show()
        })
        val mLayoutManager: RecyclerView.LayoutManager =
            GridLayoutManager(applicationContext, 4)
        rv_image!!.setLayoutManager(mLayoutManager)

        // iamgeLyt.removeAllViews();
        GuestAddCarFragment.list.clear()


//        Picasso.with(this)
//                .load(IMAGE_BASE_URL +"Images/PERSONAssociation"+Prefs.getInt(ASSOCIATION_ID,0)+"NONREGULAR"+getIntent().getStringExtra(MOBILENUMBER)+".jpg")
//                .placeholder(R.drawable.user_icon_black).error(R.drawable.user_icon_black).into(imageView1);
        Log.v(
            "Oyespace",
            ConstantUtils.IMAGE_BASE_URL + "Images/PERSON" + "NONREGULAR" + intent.getStringExtra(
                ConstantUtils.MOBILENUMBER
            ) + ".jpg"
        )

//        Log.d("intentdata ", " AddCarFragment " + getIntent().getStringExtra(UNITNAME) + " " + getIntent().getStringExtra(UNITID)
//                + " " + getIntent().getStringExtra(MOBILENUMBER) + " " + getIntent().getStringExtra(COUNTRYCODE) + " " + getIntent().getStringExtra(PERSONNAME));
        if (intent.getStringExtra(ConstantUtils.FLOW_TYPE) == ConstantUtils.GUEST_REGISTRATION) {
            image_Gallery!!.setVisibility(View.INVISIBLE)
            rv_image!!.setVisibility(View.INVISIBLE)
            (findViewById<View>(R.id.txt_header) as TextView).text =
                getString(R.string.textpersonphotoscreen)
        } else {
            if (intent.getIntExtra(ConstantUtils.ACCOUNT_ID, 0) != 0) {
                // Picasso.with(this).load(IMAGE_BASE_URL + "Images/" + "PERSON"+"NONREGULAR" + getIntent().getStringExtra(MOBILENUMBER) + ".jpg").into(target);
                imageView1!!.setImageBitmap(photo)
                //                Picasso.with(this)
//                        .load(IMAGE_BASE_URL + "Images/"+"PERSONAssociation"+Prefs.getInt(ASSOCIATION_ID,0)+"NONREGULAR" + getIntent().getStringExtra(MOBILENUMBER) + ".jpg")
//                        .placeholder(R.drawable.placeholder_dark).error(R.drawable.placeholder_dark).into(imageView1);

                //   Picasso.with(this).load(IMAGE_BASE_URL +"Images/PERSON"+getIntent().getStringExtra(MOBILENUMBER)+".jpg").placeholder(R.drawable.user_icon_black).memoryPolicy(MemoryPolicy.NO_CACHE).into(imageView1);
            }
        }
        iv_edit!!.setOnClickListener(View.OnClickListener {
            val i = Intent(this@GuestPhotoScreen, GuestNameEntryScreen::class.java)
            i.putExtra(ConstantUtils.UNITID, intent.getStringExtra(ConstantUtils.UNITID))
            i.putExtra(
                ConstantUtils.UNITNAME,
                intent.getStringExtra(ConstantUtils.UNITNAME)
            )
            i.putExtra(
                ConstantUtils.FLOW_TYPE,
                intent.getStringExtra(ConstantUtils.FLOW_TYPE)
            )
            i.putExtra(
                ConstantUtils.VISITOR_TYPE,
                intent.getStringExtra(ConstantUtils.VISITOR_TYPE)
            )
            i.putExtra(
                ConstantUtils.COMPANY_NAME,
                intent.getStringExtra(ConstantUtils.COMPANY_NAME)
            )
            i.putExtra(
                ConstantUtils.MOBILENUMBER,
                intent.getStringExtra(ConstantUtils.MOBILENUMBER)
            )
            i.putExtra(
                ConstantUtils.COUNTRYCODE,
                intent.getStringExtra(ConstantUtils.COUNTRYCODE)
            )
            i.putExtra(
                ConstantUtils.UNIT_ACCOUNT_ID,
                intent.getStringExtra(ConstantUtils.UNIT_ACCOUNT_ID)
            )
            i.putExtra(
                ConstantUtils.BLOCK_ID,
                intent.getStringExtra(ConstantUtils.BLOCK_ID)
            )
            startActivity(i)
            finish()
        })
        buttonCapture!!.setOnClickListener(View.OnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
        })

//        buttonSkip.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent d = new Intent(GuestAddCarFragment.this, GuestEntryRegistration.class);
//                Log.d("intentdata personPhoto", "buttonNext " + getIntent().getStringExtra(UNITNAME) + " " + getIntent().getStringExtra(UNITID)
//                        + " " + getIntent().getStringExtra(MOBILENUMBER) + " " + getIntent().getStringExtra(COUNTRYCODE) + " " + getIntent().getStringExtra(PERSONNAME));
//                d.putExtra(UNITID, getIntent().getStringExtra(UNITID));
//                d.putExtra(UNITNAME, getIntent().getStringExtra(UNITNAME));
//                d.putExtra(FLOW_TYPE, getIntent().getStringExtra(FLOW_TYPE));
//                d.putExtra(VISITOR_TYPE, getIntent().getStringExtra(VISITOR_TYPE));
//                d.putExtra(COMPANY_NAME, getIntent().getStringExtra(COMPANY_NAME));
//                d.putExtra(MOBILENUMBER, getIntent().getStringExtra(MOBILENUMBER));
//                d.putExtra(COUNTRYCODE, getIntent().getStringExtra(COUNTRYCODE));
//                d.putExtra(PERSONNAME, getIntent().getStringExtra(PERSONNAME));
//                d.putExtra(PERSON_PHOTO, "");
//                d.putExtra(ITEMS_PHOTO_LIST, list);
//                d.putExtra(ACCOUNT_ID, getIntent().getIntExtra(ACCOUNT_ID, 0));
//
//                startActivity(d);
//                finish();
//
//            }
//        });
        submit_button!!.setOnClickListener(View.OnClickListener {
            submit_button!!.setEnabled(false)
            submit_button!!.setClickable(false)
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
            //                if (personPhoto == null) {
            //                    Toast.makeText(getApplicationContext(), "Capture Photo ", Toast.LENGTH_SHORT).show();
            //                } else {
            val d = Intent(this@GuestPhotoScreen, GuestEntryRegistration::class.java)
            ////                    Log.d("intentdata personPhoto", "buttonNext " + getIntent().getStringExtra(UNITNAME) + " " + getIntent().getStringExtra(UNITID)
            ////                            + " " + getIntent().getStringExtra(MOBILENUMBER) + " " + getIntent().getStringExtra(COUNTRYCODE) + " " + getIntent().getStringExtra(PERSONNAME));
            d.putExtra(ConstantUtils.UNITID, intent.getStringExtra(ConstantUtils.UNITID))
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
            d.putExtra(ConstantUtils.ITEMS_PHOTO_LIST, GuestAddCarFragment.list)
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
            d.putExtra("Base64", encodedImage)
            startActivity(d)
            finish()
        } //}
        )
    }

    override fun onDestroy() {  // could be in onPause or onStop
        Picasso.with(this).cancelRequest(target)
        super.onDestroy()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        //        Intent d = new Intent(GuestAddCarFragment.this, Dashboard.class);
//        startActivity(d);
        finish()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnCaptureItemPhoto -> {
                //                selectImage();
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, GuestAddCarFragment.REQUEST_CAMERA)
            }
        }
    }


    fun showViewPager() {
        Log.e("Data", "Recicved")
//        image_Gallery.setVisibility(View.GONE);
//        viewPager_Image.setVisibility(View.VISIBLE);
//        floatButton.setVisibility(View.VISIBLE);
    }

    fun onCaptureImageResult(data: Intent, context: Context) {
        val thumbnail = data.extras["data"] as Bitmap
        val bytes = ByteArrayOutputStream()
        assert(thumbnail != null)
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val destination = File(
            Environment.getExternalStorageDirectory().path,
            System.currentTimeMillis().toString() + ".jpg"
        )

        // List<String> myList = new ArrayList<String>(Collections.singletonList(String.valueOf(destination.getAbsoluteFile())));
        setviewPager(destination.absoluteFile.toString(), context)
        val fo: FileOutputStream
        try {
            destination.createNewFile()
            fo = FileOutputStream(destination)
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
            if (GuestAddCarFragment.list.size >= 20) {
                image_Gallery!!.visibility = View.GONE
            } else {
                image_Gallery!!.visibility = View.VISIBLE
            }
            val imageView = LayoutInflater.from(context)
                .inflate(R.layout.image_list, null) as ImageView
            GuestAddCarFragment.list.add(selectedImagePath)
            ImageHelper.loadImage(context, selectedImagePath, imageView)
            imageAdapter = ImageAdapter(GuestAddCarFragment.list, this@GuestPhotoScreen, "On")
            rv_image!!.adapter = imageAdapter
            // iamgeLyt.addView(imageView);

            /*  sumeeth code tried for deleting the photo  it worked */iv_delete!!.setOnClickListener {
                val builder =
                    AlertDialog.Builder(this@GuestPhotoScreen)
                builder.setTitle("Delete image")
                builder.setCancelable(false)
                builder.setMessage("Do you want to delete this image")
                builder.setPositiveButton(
                    "Yes"
                ) { dialog, which ->
                    GuestAddCarFragment.list.remove(selectedImagePath)
                    //iamgeLyt.removeView(imageView);
                    imageAdapter!!.notifyDataSetChanged()
                }
                builder.setNegativeButton(
                    "No"
                ) { dialog, which -> builder.setCancelable(true) }
                val dialog = builder.create()
                dialog.show()
            }
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
                takePhoto()
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
                startActivityForResult(intent, GuestAddCarFragment.REQUEST_CAMERA)
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
        image_Gallery!!.visibility = View.VISIBLE
    }

    /*code to move from one fragment to another */
    /* void changePostion(){
     *//* Fragment f = new MyStockFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id., f);

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();*//*

        fragmentManager.beginTransaction().replace(R.id.frame_container, new MyStockFragment()).commit();

    }*/




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GuestAddCarFragment.REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {
            Log.e("Activity", "Result OK")
            onCaptureImageResult(data!!, this)
            showViewPager()
        } else if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            photo = data!!.extras["data"] as Bitmap
            imageView1!!.setImageBitmap(photo)
            // personPhoto = photo;
            SaveImage(photo!!)
            Log.d("Activity", "CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE ")
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

    fun getLatestRecordData(mobileNumber:String,accountId:String) {
        RetrofitClinet.instance.getLatestRecord(
                ConstantUtils.OYE247TOKEN,
                mobileNumber
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : CommonDisposable<GetLatestRecord>() {

                override fun onSuccessResponse(getdata: GetLatestRecord) {
                    if(getdata.data!=null) {
                        if (getdata.data.visitorLatestRecord.vlEntryImg!=null) {

                            try {
                                if (getdata.data.visitorLatestRecord.vlEntryImg.contains("PERSON")) {

                                    val url =
                                        ConstantUtils.IMAGE_BASE_URL + "Images/" + getdata.data.visitorLatestRecord.vlEntryImg

                                    GetImageFromUrl(imageView1!!).execute(url);

//                           val image= getBitmapFromURL(ConstantUtils.IMAGE_BASE_URL + "Images/" + getdata.data.visitorLatestRecord.vlEntryImg)
//
//                            encodedImage = RandomUtils.encodeToBase64(image, Bitmap.CompressFormat.JPEG, 100)
//                            val imageBytes = Base64.decode(encodedImage, Base64.DEFAULT)
//                            val decodedImage =
//                                BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
//                            imageView1!!.setImageBitmap(decodedImage)
                                } else {
                                    val imageBytes = Base64.decode(getdata.data.visitorLatestRecord.vlEntryImg, Base64.DEFAULT)
                                    val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                                    imageView1!!.setImageBitmap(decodedImage)
                                }
                            } catch (e: IllegalStateException) {

                            }
                        } else {
                            imageView1!!.setBackgroundResource(R.drawable.user_icon_black)
                        }

                    }
                }

                override fun onErrorResponse(e: Throwable) {
                    // visitorLog(unitId, personName, mobileNumb, desgn, workerType, staffID, unitName,wkEntryImg)
                    //  visitorLogBiometric(unitId, personName, mobileNumb, desgn, workerType, staffID, unitName,wkEntryImg)


                }

                override fun noNetowork() {
                    Toast.makeText(
                        this@GuestPhotoScreen,
                        "No network call ",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })

    }
}

