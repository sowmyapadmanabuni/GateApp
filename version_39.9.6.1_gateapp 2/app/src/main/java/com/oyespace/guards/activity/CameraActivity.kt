package com.oyespace.guards.activity

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_camera.*
import timber.log.Timber
import java.io.*
import com.oyespace.guards.R
import com.oyespace.guards.constants.PrefKeys.LANGUAGE
import com.oyespace.guards.network.ImageApiClient
import com.oyespace.guards.network.ImageApiInterface
import com.oyespace.guards.utils.ConstantUtils.*
import com.oyespace.guards.utils.Prefs
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import java.util.*

class CameraActivity : BaseKotlinActivity() {

    val APP_TAG = "MyCustomApp"

    val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034
    var photoFile: File? = null
    var incidentPhoto: Bitmap? = null
    private var filePath: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLocale(Prefs.getString(LANGUAGE, null))
        setContentView(R.layout.activity_camera)

        button1.setOnClickListener{
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
        }

        button2.setOnClickListener {
            Timber.d("hhhh")
            val imgName = "Association"+ "0" + ".jpg"
            //uploadImage(imgName, incidentPhoto)
        }

        buttonNext.setOnClickListener {
            Timber.d("hhhh")
            val imgName = "Association"+ "0" + ".jpg"
            val d = Intent(this@CameraActivity, StaffEntryRegistration::class.java)
            Log.d("intentdata NameEntr","buttonNext "+getIntent().getStringExtra(UNITNAME)+" "+intent.getStringExtra(UNITID)
                    +" "+getIntent().getStringExtra(MOBILENUMBER)+" "+getIntent().getStringExtra(COUNTRYCODE)+" "+intent.getStringExtra(PERSONNAME));
            d.putExtra(UNITID, intent.getStringExtra(UNITID))
            d.putExtra(UNITNAME, intent.getStringExtra(UNITNAME))
            d.putExtra(FLOW_TYPE,intent.getStringExtra(FLOW_TYPE))
            d.putExtra(VISITOR_TYPE,intent.getStringExtra(VISITOR_TYPE))
            d.putExtra(COMPANY_NAME,intent.getStringExtra(COMPANY_NAME))
            d.putExtra(MOBILENUMBER, intent.getStringExtra(MOBILENUMBER))
            d.putExtra(COUNTRYCODE, intent.getStringExtra(COUNTRYCODE))
            d.putExtra(PERSONNAME, intent.getStringExtra(PERSONNAME))

            startActivity(d);
            finish();
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val d = Intent(this@CameraActivity , NameEntryScreen::class.java)
        Log.d("intentdata NameEntr","buttonNext "+getIntent().getStringExtra(UNITNAME)+" "+intent.getStringExtra(UNITID)
                +" "+getIntent().getStringExtra(MOBILENUMBER)+" "+getIntent().getStringExtra(COUNTRYCODE)+" ");
        d.putExtra(UNITID, intent.getStringExtra(UNITNAME))
        d.putExtra(UNITNAME, intent.getStringExtra(UNITID))
        d.putExtra(FLOW_TYPE,intent.getStringExtra(FLOW_TYPE))
        d.putExtra(VISITOR_TYPE,intent.getStringExtra(VISITOR_TYPE))
        d.putExtra(COMPANY_NAME,intent.getStringExtra(COMPANY_NAME))
        d.putExtra(MOBILENUMBER, intent.getStringExtra(MOBILENUMBER))
        d.putExtra(COUNTRYCODE, intent.getStringExtra(COUNTRYCODE))
        d.putExtra(PERSONNAME, intent.getStringExtra(PERSONNAME))

        startActivity(d);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val photo = data!!.extras!!.get("data") as Bitmap
            incidentPhoto = photo
            imageView1.setImageBitmap(incidentPhoto)

        }
    }


    fun uploadImage(imgName: String, incidentPhoto: Bitmap?) {
        Log.d("bhavani",imgName)
        var byteArrayProfile: ByteArray?
        val mPath = Environment.getExternalStorageDirectory().toString() + "/" + imgName + ".jpg"
        val imageFile = File(mPath)

        try {
            val outputStream = FileOutputStream(imageFile)
            val quality = 50
            if (incidentPhoto != null) {
                incidentPhoto.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            }
            outputStream.flush()
            outputStream.close()

            val bosProfile = ByteArrayOutputStream()
            if (incidentPhoto != null) {
                incidentPhoto.compress(Bitmap.CompressFormat.JPEG, 50, bosProfile)
            }
            // bmp1.compress(Bitmap.CompressFormat.JPEG, 50, bos);
            //InputStream in = new ByteArrayInputStream(bos.toByteArray());
            byteArrayProfile = bosProfile.toByteArray()
            val len = bosProfile.toByteArray().size
            println("AFTER COMPRESSION-===>$len")
            bosProfile.flush()
            bosProfile.close()
            if (incidentPhoto != null) {
                incidentPhoto.recycle()
            }
            Timber.e("Dgddfdf  bf", "sfas")
        } catch (ex: Exception) {
            byteArrayProfile = null
            Log.d("Dgddfdf ererer bf", ex.toString())
        }


        val uriTarget = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, ContentValues())

        val imageFileOS: OutputStream?
        try {
            imageFileOS = contentResolver.openOutputStream(uriTarget!!)
            imageFileOS!!.write(byteArrayProfile!!)
            imageFileOS.flush()
            imageFileOS.close()

            Log.d("Dgddfdf Path bf", uriTarget.toString())
        } catch (e: FileNotFoundException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }


        val file = File(imageFile.toString())
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val body = MultipartBody.Part.createFormData("Test", imgName, requestFile)
        val apiService = ImageApiClient.getImageClient().create(ImageApiInterface::class.java)
        val call = apiService.updateImageProfile(body)

        call.enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: retrofit2.Response<Any>) {
                try {
                    Log.d("Dgddfdf", "response:" + response.body()!!)
                    file.delete()
                    Toast.makeText(getApplicationContext(),"Image Uploaded Successfully",Toast.LENGTH_SHORT).show();


                } catch (ex: Exception) {
                    Toast.makeText(applicationContext, "Image Not Uploaded", Toast.LENGTH_SHORT).show()
                }

                finish()

/*if(photoInt<5) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeMenuActivity.this);
                    alertDialog.setTitle("Do you want to capture another Picture?");
                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, 7);
                            dialog.cancel();
                        }
                    });
                    alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    // Showing Alert Message
                    alertDialog.show();
                }
*/

            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                // Log error here since request failed
                Log.d("Dgddfdf", t.toString())
                Toast.makeText(applicationContext, "Not Uploaded", Toast.LENGTH_SHORT).show()
                finish()
            }
        })


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

}