package com.oyespace.guards.activity

import android.app.Activity
import android.app.DatePickerDialog
import android.content.*
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.oyespace.guards.R
import com.oyespace.guards.constants.PrefKeys
import com.oyespace.guards.network.CommonDisposable
import com.oyespace.guards.network.ImageApiClient
import com.oyespace.guards.network.ImageApiInterface
import com.oyespace.guards.network.RetrofitClinet
import com.oyespace.guards.pojo.*
import com.oyespace.guards.utils.ConstantUtils
import com.oyespace.guards.utils.ConstantUtils.*
import com.oyespace.guards.utils.LocalDb
import com.oyespace.guards.utils.Prefs
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_mobile_number.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import timber.log.Timber
import java.io.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class EditStaffActivity : BaseKotlinActivity(), AdapterView.OnItemSelectedListener{


    var s_dob:String?=null
    var date:String?=null
    var inputt:SimpleDateFormat?=null
    var output:SimpleDateFormat?=null
    var oneWayTripDate:Date?=null
    var out= arrayOf(String())
    var y:Int?=0
    var m:Int?=0
    var d:Int?=0
    var token:Double?=0.0
    lateinit var txt_assn_name:TextView
    lateinit var txt_gate_name:TextView
    lateinit var txt_device_name: TextView
    var receiver:BroadcastReceiver?=null
    var datepickerdialog:DatePickerDialog?=null
    var ccd:String?=null
    var mobileNumber:String?=null
    var imgName: String?=null
    internal var personPhoto: Bitmap? = null
    var tv_name:TextView?=null
    var designation = arrayOf("Assistant Manager", "Assistant Security Officer", "CareTaker", "Cook", "Driver","Electrician","Gardener","Gym Trainer","Head Guard","Health Instructor","Lady Head Guard","Lady Supervisor","Lady Senior Security Guard","Lady Security Guard","Maid","Manager","Nurse","Others","Plumber","Stay at Home Maid","Security Guard","Security Officer","Senior Security","Security Supervisor","Senior Supervisor","Sweeper","Tuition Teacher")
    var tv_designation:TextView?=null
    var overlapImage:ImageView?=null
    internal val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034
    var iv_personphoto:ImageView?=null
    var calender:ImageView?=null
    private val datePicker: DatePicker? = null
    private var cal: Calendar? = null
    private var year: Int = 0
    var month: Int = 0
    var day: Int = 0
    var ed_dob:TextView?=null
    var btn_update:Button?=null
    var tv_mobilenumber:TextView?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_staff)


        btn_update=findViewById(R.id.btn_update)

        ed_dob=findViewById(R.id.ed_dob)
       // ed_dob.text=intent.getStringExtra("DOB")
//        calendar = Calendar.getInstance();
//        year = calendar!!.get(Calendar.YEAR);
      val sDOB=intent.getStringExtra("DOB")
       sDOB.substring(0,10);

         cal = Calendar.getInstance()
         y = cal!!.get(Calendar.YEAR)
         m = cal!!.get(Calendar.MONTH)
         d = cal!!.get(Calendar.DAY_OF_MONTH)


        date =sDOB.substring(0,10);
        s_dob=sDOB.substring(0,10);

 inputt =  SimpleDateFormat("yyyy-MM-dd");
 output =  SimpleDateFormat("dd-MM-yyyy");
try {
    oneWayTripDate = inputt!!.parse(date);                 // parse input
    //0001-01-01

    if(output!!.format(oneWayTripDate).equals("01-01-0001")){
        ed_dob?.setHint(resources.getString(R.string.textselectdob));    // format output
    }else
    {
        ed_dob?.setText(output!!.format(oneWayTripDate));    // format output

 out = output!!.format(oneWayTripDate).split(",").toTypedArray();

        try {
            System.out.println("Year = " + out[2]);
System.out.println("Month = " + out[0]);
System.out.println("Day = " + out[1]);
            y = out[2].toInt()
            m = out[0].toInt()
            d = out[1].toInt()
        }catch (e:IndexOutOfBoundsException){

        }


    }

} catch (e: ParseException) {
    e.printStackTrace();
}

//
     //  ed_dob?.setText(sDOB.substring(0,10))
//        month = calendar!!.get(Calendar.MONTH);
//        day = calendar!!.get(Calendar.DAY_OF_MONTH);

       // showDate(year, month+1, day);

        tv_name= findViewById(R.id.tv_name)
        overlapImage=findViewById(R.id.overlapImage)
        iv_personphoto=findViewById(R.id.iv_personphoto)
        tv_mobilenumber=findViewById(R.id.tv_mobilenumber)
        tv_mobilenumber!!.text=intent.getStringExtra(ConstantUtils.MOBILENUMBER)
//        calendar = Calendar.getInstance();
//        year = calendar!!.get(Calendar.YEAR);

        ed_dob!!.setOnClickListener {
            //setDate()




             datepickerdialog = DatePickerDialog(this@EditStaffActivity, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                // Display Selected date in textbox

                var userAge =  GregorianCalendar(year,month,day);
                var minAdultAge =  GregorianCalendar();
                minAdultAge.add(Calendar.YEAR, -18);
                if (minAdultAge.before(userAge)) {
                    Toast.makeText(this,"Age is below 18.",Toast.LENGTH_LONG).show();
                }
                else{
                  //  ed_dob!!.setText("" + dayOfMonth + "-" + (monthOfYear+1) + "-" + year)
                    ed_dob!!.setText("" + dayOfMonth + "-" + (monthOfYear+1) + "-" + year)
                    s_dob=(""+year+"-"+(monthOfYear+1)+"-"+dayOfMonth)

                }
            }, y!!, m!!, d!!)
            try {
               // datepickerdialog!!.getDatePicker().setMinDate(1919, 5, 23));
            datepickerdialog!!.getDatePicker().setMaxDate(cal!!.getTimeInMillis());
            }
            catch (e:KotlinNullPointerException){

            }
            datepickerdialog!!.show()
        }
        val input =Prefs.getString(PrefKeys.MOBILE_NUMBER,"")
        // val number = input.replaceFirst("(\\d{3})(\\d{3})(\\d+)".toRegex(), "$1 $2 $3")
        val number = input.replaceFirst("(\\d{2})(\\d{4})(\\d{3})(\\d+)".toRegex(), "$1 $2 $3 $4")
        tv_guardnumber.setText(resources.getString(R.string.textgivemissedcall)+" +"+number)

        receiver =  object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {

                val telephony = context?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                telephony.listen(object : PhoneStateListener() {

                    override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                        super.onCallStateChanged(state, phoneNumber)
                        if (state == TelephonyManager.CALL_STATE_RINGING) {

                            val bundle = intent?.getExtras();
                            val number = bundle?.getString("incoming_number");

                            //   Toast.makeText(applicationContext, number, Toast.LENGTH_LONG).show();
                            if (tv_mobilenumber != null && number != null) {
                                // textview.text = number.replace("+91", "")
                                tv_mobilenumber!!.text=number.toString()
                                ccd= number.substring(0,3)

                                mobileNumber=number.substring(3,13)

                             GetWorkersListByMobileNumberAndAssocID(
                         tv_mobilenumber!!.text.toString(),
                           Prefs.getInt(ASSOCIATION_ID, 0))
                            }
                            LocalDb.disconnectCall(context);
                        }
                    }

                }, PhoneStateListener.LISTEN_CALL_STATE);

                //
            }
        };

        tv_name?.text= intent.getStringExtra("FIRSTNAME")
        tv_designation=findViewById(R.id.tv_designation)
        tv_designation!!.text=intent.getStringExtra("DESIGNATION")
       // sp_designation?.setOnItemSelectedListener(this);
        txt_assn_name=findViewById(R.id.txt_assn_name)
        txt_gate_name=findViewById(R.id.txt_gate_name)
        txt_device_name=findViewById(R.id.txt_device_name)
        if(Prefs.getString(PrefKeys.MODEL_NUMBER,null).equals("Nokia 1")) {
            txt_assn_name!!.setTextSize(5 * getResources().getDisplayMetrics().density);
        }
        txt_assn_name.text = "Society: " + LocalDb.getAssociation()!!.asAsnName
        txt_gate_name.text = "Gate No: " + Prefs.getString(ConstantUtils.GATE_NO, "")
        try {
            var appVersion = ""
            val manager = baseContext.packageManager
            val info = manager.getPackageInfo(baseContext.packageName, 0)
            appVersion = info.versionName
            Log.d("tag", "app " + appVersion + " " + info.versionName)
            txt_device_name.text = "V: $appVersion"

        } catch (ex: Exception) {
            ex.printStackTrace()
            txt_device_name.text = " "

        }

        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, designation)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        //Setting the ArrayAdapter data on the Spinner
        //sp_designation?.setAdapter(aa)
       // selectSpinnerValue(sp_designation!!,intent.getStringExtra("DESIGNATION"))

        overlapImage!!.setOnClickListener {

            imgName = "PERSON" + "Association" + Prefs.getInt(ASSOCIATION_ID, 0) + "STAFF" +intent.getIntExtra(ConstantUtils.WORKER_ID,0)+token+ ".jpg"


            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
        }



        token=Math.random();
        Picasso.with(this@EditStaffActivity)
            .load(
                IMAGE_BASE_URL + "Images/" + intent.getStringExtra("IMAGE")
            )
            .placeholder(R.drawable.user_icon_black).error(R.drawable.user_icon_black).into(iv_personphoto)

        btn_update!!.setOnClickListener {

//            if(personPhoto== null){
//               // Toast.makeText(applicationContext, "Capture Photo ", Toast.LENGTH_SHORT).show()
//            }
//            else
                if(tv_name!!.text.trim().length==0){
                Toast.makeText(this,"Enter Name", Toast.LENGTH_SHORT).show()

            }
            else if(ed_dob!!.length()==0){
                Toast.makeText(this,"Select DOB", Toast.LENGTH_SHORT).show()
            }
            else {

                   // if(tv_mobilenumber!!.length()==0){
                        showProgress()

                        if(imgName!=null) {
                            editStaff(tv_name!!.text.toString(), tv_mobilenumber!!.text.toString(), imgName!!, "Staff", tv_designation!!.text.toString(), "", s_dob.toString(), true, intent.getIntExtra(ConstantUtils.WORKER_ID,0))

                        }
                        else{
                            editStaff(tv_name!!.text.toString(), tv_mobilenumber!!.text.toString(), intent.getStringExtra("IMAGE"), "Staff", tv_designation!!.text.toString(), "", s_dob.toString(), true, intent.getIntExtra(ConstantUtils.WORKER_ID,0))

                        }
//                    }else {
//                        GetWorkersListByMobileNumberAndAssocID(
//                            tv_mobilenumber!!.text.toString(),
//                            Prefs.getInt(ASSOCIATION_ID, 0)
//                        )
//
//                    }



            }  }


    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

    }

    private fun selectSpinnerValue(spinner: Spinner, myString: String) {
        val index = 0
        for (i in 0 until spinner.count) {
            if (spinner.getItemAtPosition(i).toString() == myString) {
                spinner.setSelection(i)

            }
        }
    }


//    override fun onCreateDialog(id: Int): Dialog? {
//        // TODO Auto-generated method stub
//        return if (id == 999) {
//            DatePickerDialog(this,myDateListener, year, month, day)
//
//
//        } else null
//    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {

            CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE -> if (resultCode == Activity.RESULT_OK) {
                val photo = data!!.getExtras()!!.get("data") as Bitmap
                iv_personphoto!!.setImageBitmap(photo)
                personPhoto = photo

            }
        }

    }
//    fun setDate() {
//        showDialog(999)
////        Toast.makeText(
////            applicationContext, "ca",
////            Toast.LENGTH_SHORT
////        )
////            .show()
//    }
//    private fun showDate(year: Int, month: Int, day: Int) {
//        ed_dob!!.setText(
//            StringBuilder().append(day).append("/")
//                .append(month).append("/").append(year)
//        )
//    }
//    private val myDateListener = DatePickerDialog.OnDateSetListener { arg0, arg1, arg2, arg3 ->
//        // TODO Auto-generated method stub
//        // arg1 = year
//        // arg2 = month
//        // arg3 = day
//        showDate(arg1, arg2 + 1, arg3)
//    }



//    {
//        "WKFName"	 : "knlk",
//        "WKMobile"   : "+919490791523",
//        "WKImgName"  : "sdsd",
//        "WKWrkType"  : "sdsd",
//        "WKDesgn"    : "ddf00",
//        "WKIDCrdNo"  : "sdfds",
//        "WKDOB"		 : "11-09-2019",
//        "WKIsActive" : "true",
//        "WKWorkID"   : 8
//    }
    private fun editStaff(WKFName: String, WKMobile: String, WKImgName: String,WKWrkType:String,WKDesgn:String,WKIDCrdNo:String,WKDOB:String,WKIsActive:Boolean,WKWorkID:Int) {

        val req = StaffEditRequest(
            WKFName, WKMobile, WKImgName, WKWrkType, WKDesgn,WKIDCrdNo,WKDOB,WKIsActive,WKWorkID

        )

        compositeDisposable.add(
            RetrofitClinet.instance.staffUpdate(ConstantUtils.OYE247TOKEN, req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : CommonDisposable<StaffEditResponse>() {
                    override fun onSuccessResponse(globalApiObject: StaffEditResponse) {
                        if (globalApiObject.success == true) {

                            dismissProgress()
                            if (imgName != null) {

                                var byteArray: ByteArray? = null
                                try {
                                    Log.d("Dgddfdf picas", "5 2")
                                    val bos = ByteArrayOutputStream()
                                    personPhoto!!.compress(Bitmap.CompressFormat.JPEG, 100, bos)
                                    byteArray = bos.toByteArray()
                                    val len = bos.toByteArray().size
                                    println("AFTER COMPRESSION-===>$len")
                                    bos.flush()
                                    bos.close()
                                } catch (ex: Exception) {
                                    Log.d("Dgddfdf picas", "7")
                                }
                                uploadImage(imgName.toString(), personPhoto);
                            } else {

                               var drawable: BitmapDrawable = iv_personphoto!!.getDrawable() as BitmapDrawable
                              personPhoto = drawable.getBitmap();

                                uploadImage(intent.getStringExtra("IMAGE"), personPhoto);

                            }
                        }
                    }

                    override fun onErrorResponse(e: Throwable) {
                        dismissProgress()
//                    Utils.showToast(applicationContext, getString(R.string.some_wrng))
                        Log.d("CreateVisitorLogResp", "onErrorResponse  " + e.toString())
                    }

                    override fun noNetowork() {
//                    Utils.showToast(applicationContext, getString(R.string.no_internet))
                    }

                    override fun onShowProgress() {
                    }

                    override fun onDismissProgress() {
                    }
                })
        )
    }

    fun uploadImage(localImgName: String, incidentPhoto: Bitmap?) {
        Log.d("uploadImage",localImgName)
        var byteArrayProfile: ByteArray?
        val mPath = Environment.getExternalStorageDirectory().toString() + "/" + localImgName + ".jpg"
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
                //    incidentPhoto.recycle()
            }
            Timber.e("uploadImage  bf", "sfas")
        } catch (ex: Exception) {
            byteArrayProfile = null
            Log.d("uploadImage ererer bf", ex.toString())
        }

        val uriTarget = this.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, ContentValues())

        val imageFileOS: OutputStream?
        try {
            imageFileOS = this.contentResolver.openOutputStream(uriTarget!!)
            imageFileOS!!.write(byteArrayProfile!!)
            imageFileOS.flush()
            imageFileOS.close()

            Log.d("uploadImage Path bf", uriTarget.toString())
        } catch (e: FileNotFoundException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

        val file = File(imageFile.toString())
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val body = MultipartBody.Part.createFormData("Test", localImgName, requestFile)
        val apiService = ImageApiClient.getImageClient().create(ImageApiInterface::class.java)
        val call = apiService.updateImageProfile(body)

        call.enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: retrofit2.Response<Any>) {
                try {
                    Log.d("uploadImage", "response:" + response.body()!!)

                    if (imgName != null) {

                        updateStaffImage(imgName.toString(), "", intent.getIntExtra(ConstantUtils.WORKER_ID,0),intent.getStringExtra("FIRSTNAME"))

                    }else{
                        updateStaffImage(intent.getStringExtra("IMAGE"), "", intent.getIntExtra(ConstantUtils.WORKER_ID,0),intent.getStringExtra("FIRSTNAME"))

                    }
                    file.delete()
                    //  Toast.makeText(mcontext,"Uploaded Successfully", Toast.LENGTH_SHORT).show();

                } catch (ex: Exception) {
                    Log.d("uploadImage", "errr:" + ex.toString())

                    Toast.makeText(this@EditStaffActivity, "Image Not Uploaded", Toast.LENGTH_SHORT).show()
                }

            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                dismissProgress()
                // Log error here since request failed
                Log.d("uploadImage", t.toString())
                Toast.makeText(this@EditStaffActivity, "Not Uploaded", Toast.LENGTH_SHORT).show()

            }
        })

    }

    private fun updateStaffImage(WKEntryImg: String, WKEntryGPS: String, WKWorkID: Int,WKFName:String) {


        val req = SendStaffImageReq(WKEntryImg, WKEntryGPS, WKWorkID)

        compositeDisposable.add(
            RetrofitClinet.instance.sendStaffImageUpdate(ConstantUtils.OYE247TOKEN, req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : CommonDisposable<SendStaffImageRes>() {
                    override fun onSuccessResponse(globalApiObject: SendStaffImageRes) {
                        if (globalApiObject.success == true) {

                            dismissProgress()



                            val intent = Intent(this@EditStaffActivity,EditBiometric::class.java)
                            intent.putExtra(WORKER_ID,WKWorkID)
                            intent.putExtra(PERSONNAME, WKFName)
                            intent.putExtra(UNITID, getIntent().getStringExtra(UNITID))
                            intent.putExtra(UNITNAME, getIntent().getStringExtra(UNITNAME))
                            intent.putExtra(FLOW_TYPE, STAFF_REGISTRATION)
                            intent.putExtra(VISITOR_TYPE, "STAFF")
                            intent.putExtra(COMPANY_NAME,  getIntent().getStringExtra(COMPANY_NAME))
                            intent.putExtra(COUNTRYCODE,"")
                            intent.putExtra(MOBILENUMBER, getIntent().getStringExtra(MOBILENUMBER))
                            startActivity(intent)
                            finish()


                        } else {


                        }
                    }

                    override fun onErrorResponse(e: Throwable) {
                        dismissProgress()
//                    Utils.showToast(applicationContext, getString(R.string.some_wrng))
                        Log.d("CreateVisitorLogResp", "onErrorResponse  " + e.toString())
                    }

                    override fun noNetowork() {
//                    Utils.showToast(applicationContext, getString(R.string.no_internet))
                    }

                    override fun onShowProgress() {
                    }

                    override fun onDismissProgress() {
                    }
                })
        )
    }
    override fun onPause() {

        unregisterReceiver(receiver)

        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        val action = "android.intent.action.PHONE_STATE"
        registerReceiver(receiver, IntentFilter(action))
    }

    private fun GetWorkersListByMobileNumberAndAssocID(WKMobile: String,ASAssnID: Int) {


        val req = GetWorkersListByMobileNumberReq(WKMobile, ASAssnID)

        compositeDisposable.add(
            RetrofitClinet.instance.GetWorkersListByMobileNumberAndAssocID(ConstantUtils.OYE247TOKEN, req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : CommonDisposable<GetWorkersListByMobileNumberResp>() {
                    override fun onSuccessResponse(globalApiObject: GetWorkersListByMobileNumberResp) {
                        if (globalApiObject.success == true) {

                            if(globalApiObject.data.message.equals("Invalid MobileNumber")){

//                            showProgress()
//
//                            if(imgName!=null) {
//                                editStaff(tv_name!!.text.toString(), tv_mobilenumber!!.text.toString(), imgName!!, "Staff", tv_designation!!.text.toString(), "", ed_dob!!.text.toString(), true, intent.getIntExtra(ConstantUtils.WORKER_ID, 0))
//
//                            }
//                            else{
//                                editStaff(tv_name!!.text.toString(), tv_mobilenumber!!.text.toString(), intent.getStringExtra("IMAGE"), "Staff", tv_designation!!.text.toString(), "", ed_dob!!.text.toString(), true, intent.getIntExtra(ConstantUtils.WORKER_ID, 0))
//
//                            }

                        } else {

                                if(output!!.format(oneWayTripDate).equals("01-01-0001")){
                                    ed_dob?.setHint(resources.getString(R.string.textselectdob));    // format output
                                }else {
                                    ed_dob?.setText(output!!.format(oneWayTripDate));
                                }
                                val builder = AlertDialog.Builder(this@EditStaffActivity)
                                // builder.setTitle("Vendor Entry already done")
                                builder.setMessage(globalApiObject.data.message+". Please Try again")
                                builder.setPositiveButton("Ok") { dialog, which ->


                                    dialog.dismiss()
                                 tv_mobilenumber!!.text=intent.getStringExtra(ConstantUtils.MOBILENUMBER)


//                                    val d = Intent(this@MobileNumberScreen, Dashboard::class.java)
//                                    startActivity(d)
//                                    finish()
                                }
                                builder.setCancelable(false);
                                builder.show()
                           // Toast.makeText(this@EditStaffActivity,globalApiObject.data.message,Toast.LENGTH_LONG).show()

                        }
                        }
                    }

                    override fun onErrorResponse(e: Throwable) {
//                    Utils.showToast(applicationContext, getString(R.string.some_wrng))
                        Log.d("CreateVisitorLogResp", "onErrorResponse  " + e.toString())
                    }

                    override fun noNetowork() {
//                    Utils.showToast(applicationContext, getString(R.string.no_internet))
                    }

                    override fun onShowProgress() {
                    }

                    override fun onDismissProgress() {
                    }
                })
        )
    }


}
