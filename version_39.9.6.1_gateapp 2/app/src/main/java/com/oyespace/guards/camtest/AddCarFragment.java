package com.oyespace.guards.camtest;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.oyespace.guards.BackgroundSyncReceiver;
import com.oyespace.guards.R;
import com.oyespace.guards.activity.Biometric;
import com.oyespace.guards.activity.StaffDetails;
import com.oyespace.guards.activity.StaffEntryRegistration;
import com.oyespace.guards.constants.PrefKeys;
import com.oyespace.guards.network.ChampApiClient;
import com.oyespace.guards.network.ChampApiInterface;
import com.oyespace.guards.network.ResponseHandler;
import com.oyespace.guards.network.RestClient;
import com.oyespace.guards.network.URLData;
import com.oyespace.guards.request.SendStaffImageReq;
import com.oyespace.guards.request.StaffRegistrationReqJv;
import com.oyespace.guards.responce.StaffImageRes;
import com.oyespace.guards.responce.StaffRegistrationRespJv;
import com.oyespace.guards.utils.LocalDb;
import com.oyespace.guards.utils.Prefs;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.oyespace.guards.constants.PrefKeys.LANGUAGE;
import static com.oyespace.guards.utils.ConstantUtils.ACCOUNT_ID;
import static com.oyespace.guards.utils.ConstantUtils.ASSOCIATION_ID;
import static com.oyespace.guards.utils.ConstantUtils.BLOCK_ID;
import static com.oyespace.guards.utils.ConstantUtils.BSR_Action;
import static com.oyespace.guards.utils.ConstantUtils.COMPANY_NAME;
import static com.oyespace.guards.utils.ConstantUtils.COUNTRYCODE;
import static com.oyespace.guards.utils.ConstantUtils.FLOW_TYPE;
import static com.oyespace.guards.utils.ConstantUtils.GATE_NO;
import static com.oyespace.guards.utils.ConstantUtils.IMAGE_BASE_URL;
import static com.oyespace.guards.utils.ConstantUtils.ITEMS_PHOTO_LIST;
import static com.oyespace.guards.utils.ConstantUtils.MOBILENUMBER;
import static com.oyespace.guards.utils.ConstantUtils.OYE247KEY;
import static com.oyespace.guards.utils.ConstantUtils.OYE247TOKEN;
import static com.oyespace.guards.utils.ConstantUtils.PERSONNAME;
import static com.oyespace.guards.utils.ConstantUtils.PERSON_PHOTO;
import static com.oyespace.guards.utils.ConstantUtils.STAFF_REGISTRATION;
import static com.oyespace.guards.utils.ConstantUtils.SYNC_STAFF_LIST;
import static com.oyespace.guards.utils.ConstantUtils.UNITID;
import static com.oyespace.guards.utils.ConstantUtils.UNITNAME;
import static com.oyespace.guards.utils.ConstantUtils.UNIT_ACCOUNT_ID;
import static com.oyespace.guards.utils.ConstantUtils.UPLOAD_STAFF_PHOTO;
import static com.oyespace.guards.utils.ConstantUtils.VISITOR_TYPE;
import static com.oyespace.guards.utils.ConstantUtils.WORKER_ID;
import static com.oyespace.guards.utils.Utils.showToast;


public class AddCarFragment extends Activity implements ResponseHandler, View.OnClickListener {

    /*sumeeth fragment*/
    // File destination;
    File file;

    ChampApiInterface champApiInterface;

    private final int REQUEST_CODE_SPEECH_INPUT = 100;
    EditText Ed_Name;
    public static Button image_Gallery, submit_button, buttonCapture;
    private ViewPager viewPager;
    ImageAdapter imageAdapter;
    RecyclerView rv_image;
    ImageView iv_delete, imageView1, iv_edit;
    final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    Bitmap photo = null;
    TextView tv_name;
    TextView txt_assn_name, txt_device_name, txt_gate_name;
    private View view;
    // private LinearLayout iamgeLyt;
    String imgName;
    private FragmentManager fragmentManager;
    ImageView dialog_imageview;
    static ArrayList<String> list = new ArrayList<>();
    private TextView upload_rc_book,
            upload_insurance, Make, model, year, variant, fuel, color, transmission, owners, insurance;
    //    private ViewPager viewPager_Image;
    private EditText notes, Regno, kms, exp_date, exp_price, car_id;
    private ProgressDialog progressDialog;
    private ImageView insurance_file_name, rc_book_file_name;
    private ArrayList<String> listpager_Array = new ArrayList<>();
    //    private FloatingActionButton floatButton;
    public static final int REQUEST_CAMERA = 0, SELECT_FILE = 1, PICK_INSURANCE_REQUEST_CODE = 2, PICK_RCBOOK_REQUEST_CODE = 3;
    private CarImages_Adapter adapter;
    private CheckBox sell_to_dealer;
    private ProgressDialog dialog;
    //    private final int Date_id = 7;
    private String makeId = "", modelId = "", variant_id = "";
    private String rcImagePath, insImagePath;

    public AddCarFragment() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLocale(Prefs.getString(LANGUAGE, null));
        setContentView(R.layout.add_car_fragment);
        initViews();
        //  imgName="PERSON"+"Association"+Prefs.getInt(ASSOCIATION_ID,0)+"STAFF" + getIntent().getStringExtra(MOBILENUMBER) + ".jpg";
        // String imgName="PERSON"+"Association"+Prefs.getInt(ASSOCIATION_ID,0)+"STAFF" +workerResponce.data.worker.wkWorkID  + ".jpg";
        Dexter.withActivity(this)
                .withPermissions(
                        //   Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            // do you work now
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // permission is denied permenantly, navigate user to app settings
                            finish();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();
    }

    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

            photo = bitmap;
            imageView1.setImageBitmap(photo);
            Log.d("target picas", "onBitmapLoaded");
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            Log.d("target picas", "7onBitmapFailed " + IMAGE_BASE_URL + "Images/" + "PERSON" + getIntent().getIntExtra(ACCOUNT_ID, 0) + ".jpg");

        }


        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            Log.d("target picas", "7 onPrepareLoad ");

        }
    };

    private void initViews() {

        // makeArrayList = new ArrayList<>();

//        viewPager_Image = (ViewPager) view.findViewById(R.id.add_car_view_pager);
        //GridView gridview
        champApiInterface = ChampApiClient.getClient().create(ChampApiInterface.class);

        tv_name = findViewById(R.id.tv_name);
        iv_edit = findViewById(R.id.iv_edit);
        tv_name.setText(getIntent().getStringExtra(PERSONNAME));
        txt_assn_name = findViewById(R.id.txt_assn_name);
        txt_device_name = findViewById(R.id.txt_device_name);
        txt_gate_name = findViewById(R.id.txt_gate_name);
        image_Gallery = findViewById(R.id.btnCaptureItemPhoto);
        //iamgeLyt = (LinearLayout) findViewById(R.id.imageLyt);
        rv_image = findViewById(R.id.rv_image);

        buttonCapture = findViewById(R.id.buttonCapture);
        imageView1 = findViewById(R.id.imageView1);

//        if(Prefs.getString("Retake",null).equals("Yes")){
//            buttonCapture.setText("RETAKE PERSON PHOTO");
//        }


        if (Prefs.getString(PrefKeys.MODEL_NUMBER, null).equals("Nokia 1")) {
            txt_assn_name.setTextSize(5 * getResources().getDisplayMetrics().density);
        }
        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ViewGroup viewGroup = findViewById(android.R.id.content);

                View dialogView = LayoutInflater.from(AddCarFragment.this).inflate(R.layout.dialog_big_image, viewGroup, false);


                AlertDialog.Builder builder = new AlertDialog.Builder(AddCarFragment.this);

                dialog_imageview = dialogView.findViewById(R.id.dialog_imageview);
                dialog_imageview.setBackground(imageView1.getDrawable());

                // Picasso.with(AddCarFragment.this).load(IMAGE_BASE_URL +"Images/PERSON"+"NONREGULAR"+getIntent().getStringExtra(MOBILENUMBER)+".jpg").placeholder(R.drawable.user_icon_black).memoryPolicy(MemoryPolicy.NO_CACHE).into(dialog_imageview);


//                Picasso.with(AddCarFragment.this)
//                        .load(IMAGE_BASE_URL +"Images/PERSON"+"NONREGULAR"+getIntent().getStringExtra(MOBILENUMBER)+".jpg")
//                        .placeholder(R.drawable.user_icon_black).error(R.drawable.user_icon_black).into(dialog_imageview);

                builder.setView(dialogView);

                //finally creating the alert dialog and displaying it
                final AlertDialog alertDialog = builder.create();

                alertDialog.show();
            }
        });

//        if() {
//
//            buttonCapture.setText(getResources().getString(R.string.texttakepersonphoto));
//        }
//        else {
//            buttonCapture.setText(getResources().getString(R.string.textretakepersonphoto));
//
//        }

        txt_assn_name.setText("Society: " + LocalDb.getAssociation().getAsAsnName());
        txt_gate_name.setText("Gate No: " + Prefs.getString(GATE_NO, null));
//        txt_device_name.setText("Gate: "+Prefs.getInt(ASSOCIATION_ID,0) );
        try {
            String appVersion = "";
            PackageManager manager = getBaseContext().getPackageManager();
            PackageInfo info = manager.getPackageInfo(getBaseContext().getPackageName(), 0);
            appVersion = info.versionName;
            Log.d("tag", "app " + appVersion + " " + info.versionName);
            txt_device_name.setText("V: " + appVersion);

        } catch (Exception ex) {
            ex.printStackTrace();
            txt_device_name.setText(" ");

        }

//        floatButton = (FloatingActionButton) view.findViewById(R.id.fab);
        image_Gallery.setOnClickListener(this);

//        floatButton.setOnClickListener(this);
        submit_button = findViewById(R.id.buttonNext);


        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 4);
        rv_image.setLayoutManager(mLayoutManager);

        //iamgeLyt.removeAllViews();
        list.clear();

//        Log.d("intentdata ", " AddCarFragment " + getIntent().getStringExtra(UNITNAME) + " " + getIntent().getStringExtra(UNITID)
//                + " " + getIntent().getStringExtra(MOBILENUMBER) + " " + getIntent().getStringExtra(COUNTRYCODE) + " " + getIntent().getStringExtra(PERSONNAME));

        if (getIntent().getStringExtra(FLOW_TYPE).equals(STAFF_REGISTRATION)) {
            image_Gallery.setVisibility(View.INVISIBLE);
            rv_image.setVisibility(View.INVISIBLE);
            ((TextView) findViewById(R.id.txt_header)).setText(getString(R.string.textpersonphotoscreen));
        } else {
            if (getIntent().getIntExtra(ACCOUNT_ID, 0) != 0) {


                Picasso.with(this).load(IMAGE_BASE_URL + "Images/" + "PERSON" + "NONREGULAR" + getIntent().getStringExtra(MOBILENUMBER) + ".jpg").into(target);

                Log.v("CALLER IMAGEVIEW", IMAGE_BASE_URL + "Images/" + "PERSON" + "NONREGULAR" + getIntent().getStringExtra(MOBILENUMBER) + ".jpg");

                imageView1.setImageBitmap(photo);

//                Glide.with(this)
//                        .load(Uri.parse(IMAGE_BASE_URL+"Images/"+"PERSON"+"NONREGULAR"+getIntent().getStringExtra(MOBILENUMBER)+".jpg"))
//                        .placeholder(R.drawable.user_icon_black)
//                        .diskCacheStrategy(DiskCacheStrategy.NONE)
//                        .skipMemoryCache(false)
//                        .into(imageView1);

                //   Picasso.with(this).load(IMAGE_BASE_URL +"Images/PERSON"+"NONREGULAR"+getIntent().getStringExtra(MOBILENUMBER)+".jpg").placeholder(R.drawable.user_icon_black).memoryPolicy(MemoryPolicy.NO_CACHE).into(imageView1);

                Picasso.with(this)
                        .load(IMAGE_BASE_URL + "Images/PERSON" + "NONREGULAR" + getIntent().getStringExtra(MOBILENUMBER) + ".jpg")
                        .placeholder(R.drawable.user_icon_black).error(R.drawable.user_icon_black).into(imageView1);

            }

        }

        buttonCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

            }
        });

        iv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewGroup viewGroup = findViewById(android.R.id.content);

                View dialogView = LayoutInflater.from(AddCarFragment.this).inflate(R.layout.layout_editname_dailog, viewGroup, false);


                AlertDialog.Builder builder = new AlertDialog.Builder(AddCarFragment.this);
                Ed_Name = dialogView.findViewById(R.id.Ed_Name);
                ImageView btn_Mic = dialogView.findViewById(R.id.btn_Mic);

                btn_Mic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        speak();
                    }
                });

                Button btn_done = dialogView.findViewById(R.id.btn_done);

                builder.setView(dialogView);

                //finally creating the alert dialog and displaying it
                final AlertDialog alertDialog = builder.create();
                btn_done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        });

        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit_button.setEnabled(false);
                submit_button.setClickable(false);

//                val imgName = "Association"+ "0" + ".jpg"

//                if(personPhoto==null && getIntent().getStringExtra(MOBILENUMBER).toString().length()==0) {

                if (photo == null) {
                    Toast.makeText(getApplicationContext(), "Capture Photo ", Toast.LENGTH_SHORT).show();
                    submit_button.setEnabled(true);
                    submit_button.setClickable(true);
                }
//                else if ( getIntent().getStringExtra(FLOW_TYPE).equals(DELIVERY) && list.size() ==0){
//
//
//
//                    Toast.makeText(getApplicationContext(),"Capture Product Photo ", Toast.LENGTH_SHORT).show();
//                }
                else {

                    if (getIntent().getStringExtra(FLOW_TYPE).equalsIgnoreCase(STAFF_REGISTRATION)) {
                        submit_button.setEnabled(false);
                        submit_button.setClickable(false);
                        staffRegistration();
                    } else {


                        byte[] byteArray = null;
                        try {
                            Log.d("Dgddfdf picas", "5 2");
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            photo.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                            byteArray = bos.toByteArray();
                            int len = bos.toByteArray().length;
                            System.out.println("AFTER COMPRESSION-===>" + len);
                            bos.flush();
                            bos.close();
                        } catch (Exception ex) {
                            Log.d("Dgddfdf picas", "7");
                        }


                        Intent d = new Intent(AddCarFragment.this, StaffEntryRegistration.class);
//                        Log.d("intentdata personPhoto", "buttonNext " + getIntent().getStringExtra(UNITNAME) + " " + getIntent().getStringExtra(UNITID)
//                                + " " + getIntent().getStringExtra(MOBILENUMBER) + " " + getIntent().getStringExtra(COUNTRYCODE) + " " + getIntent().getStringExtra(PERSONNAME));
                        d.putExtra(UNITID, getIntent().getStringExtra(UNITID));
                        d.putExtra(UNITNAME, getIntent().getStringExtra(UNITNAME));
                        d.putExtra(FLOW_TYPE, getIntent().getStringExtra(FLOW_TYPE));
                        d.putExtra(VISITOR_TYPE, getIntent().getStringExtra(VISITOR_TYPE));
                        d.putExtra(COMPANY_NAME, getIntent().getStringExtra(COMPANY_NAME));
                        d.putExtra(MOBILENUMBER, getIntent().getStringExtra(MOBILENUMBER));
                        d.putExtra(COUNTRYCODE, getIntent().getStringExtra(COUNTRYCODE));
                        d.putExtra(PERSONNAME, getIntent().getStringExtra(PERSONNAME));
                        d.putExtra(PERSON_PHOTO, byteArray);
                        d.putExtra(ITEMS_PHOTO_LIST, list);
                        d.putExtra(ACCOUNT_ID, getIntent().getIntExtra(ACCOUNT_ID, 0));
                        d.putExtra(UNIT_ACCOUNT_ID, getIntent().getStringExtra(UNIT_ACCOUNT_ID));
                        d.putExtra(BLOCK_ID, getIntent().getStringExtra(BLOCK_ID));
                        startActivity(d);
                        finish();


                    }

                }

            }
        });
    }

    @Override
    public void onFailure(Exception e, int urlId) {

        // showToast(this, "Staff Registration Failed");
    }

    private void staffRegistration() {

        RestClient restClient = RestClient.getInstance();

        StaffRegistrationReqJv loginReq = new StaffRegistrationReqJv();

        loginReq.ASAssnID = Prefs.getInt(ASSOCIATION_ID, 0);
        loginReq.BLBlockID = getIntent().getStringExtra(BLOCK_ID);
        loginReq.EmailID = "";
        loginReq.FLFloorID = 0;
        loginReq.OYEMemberID = 0;
        loginReq.OYEMemberRoleID = 0;

        loginReq.VNVendorID = 0;
        loginReq.WKDesgn = getIntent().getStringExtra(COMPANY_NAME);
        loginReq.WKFName = getIntent().getStringExtra(PERSONNAME);
        loginReq.WKIDCrdNo = "";
        loginReq.WKDOB = getIntent().getStringExtra("DOB");
        loginReq.WKISDCode = "";
        //loginReq.WKISDCode = "+"+ getIntent().getStringExtra(COUNTRYCODE);
        loginReq.WKLName = "";

        loginReq.WKMobile = getIntent().getStringExtra(COUNTRYCODE) + getIntent().getStringExtra(MOBILENUMBER);
        loginReq.WKWrkType = getIntent().getStringExtra(VISITOR_TYPE);
        //loginReq.UNUnitID=toInteger( getIntent().getStringExtra(UNITID));
        loginReq.UNUnitID = getIntent().getStringExtra(UNITID);
        // Toast.makeText(AddCarFragment.this,getIntent().getStringExtra(UNITID),Toast.LENGTH_LONG).show();
        loginReq.UNUniName = getIntent().getStringExtra(UNITNAME);
        loginReq.WKEntryImg = imgName;

        Log.d("saveCheckPoints", "StaffEntry " + loginReq.ASAssnID + " " + loginReq.WKFName + " "
                + loginReq.UNUnitID + " " + loginReq.WKMobile + " " + loginReq.UNUniName);

        restClient.addHeader(OYE247KEY, OYE247TOKEN);
        restClient.post(this, loginReq, StaffRegistrationRespJv.class, this, URLData.URL_STAFF_REGISTRATION);

    }

    @Override
    public void onSuccess(String responce, Object data, int urlId, int position) {

        if (urlId == URLData.URL_STAFF_REGISTRATION.getUrlId()) {
            StaffRegistrationRespJv workerResponce = (StaffRegistrationRespJv) data;
            if (workerResponce != null) {
                Log.d("str3", "str3: " + urlId + " id " + position + " " + " " + " " + workerResponce.success);
                if (workerResponce.success.equalsIgnoreCase("true")) {
                    imgName = "PERSON" + "Association" + Prefs.getInt(ASSOCIATION_ID, 0) + "STAFF" + workerResponce.data.worker.wkWorkID + ".jpg";

                    sendStaffImage(imgName, "", String.valueOf(workerResponce.data.worker.wkWorkID));

                    byte[] byteArray = null;
                    try {
                        Log.d("Dgddfdf picas", "5 2");
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        photo.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                        byteArray = bos.toByteArray();
                        int len = bos.toByteArray().length;
                        System.out.println("AFTER COMPRESSION-===>" + len);
                        bos.flush();
                        bos.close();
                    } catch (Exception ex) {
                        Log.d("Dgddfdf picas", "7");
                    }

                    if (Prefs.getString(PrefKeys.MODEL_NUMBER, null).equals("Nokia 2.1")) {

                        Intent d = new Intent(AddCarFragment.this, Biometric.class);
                        d.putExtra(WORKER_ID, workerResponce.data.worker.wkWorkID);
                        d.putExtra(PERSONNAME, getIntent().getStringExtra(PERSONNAME));
                        d.putExtra(UNITID, getIntent().getStringExtra(UNITID));
                        d.putExtra(UNITNAME, getIntent().getStringExtra(UNITNAME));
                        d.putExtra(FLOW_TYPE, getIntent().getStringExtra(FLOW_TYPE));
                        d.putExtra(VISITOR_TYPE, getIntent().getStringExtra(VISITOR_TYPE));
                        d.putExtra(COMPANY_NAME, getIntent().getStringExtra(COMPANY_NAME));
                        d.putExtra(MOBILENUMBER, getIntent().getStringExtra(MOBILENUMBER));
                        d.putExtra(COUNTRYCODE, getIntent().getStringExtra(COUNTRYCODE));
                        startActivity(d);
                    } else if (Prefs.getString(PrefKeys.MODEL_NUMBER, null).equals("Nokia 1")) {
                        Intent intent = new Intent(AddCarFragment.this, StaffDetails.class);
                        intent.putExtra(WORKER_ID, workerResponce.data.worker.wkWorkID);
                        intent.putExtra(PERSONNAME, getIntent().getStringExtra(PERSONNAME));
                        intent.putExtra(UNITID, getIntent().getStringExtra(UNITID));
                        intent.putExtra(UNITNAME, getIntent().getStringExtra(UNITNAME));
                        intent.putExtra(FLOW_TYPE, getIntent().getStringExtra(FLOW_TYPE));
                        intent.putExtra(VISITOR_TYPE, getIntent().getStringExtra(VISITOR_TYPE));
                        intent.putExtra(COMPANY_NAME, getIntent().getStringExtra(COMPANY_NAME));
                        intent.putExtra(MOBILENUMBER, getIntent().getStringExtra(MOBILENUMBER));
                        intent.putExtra(COUNTRYCODE, getIntent().getStringExtra(COUNTRYCODE));
                        startActivity(intent);
                        finish();
                    } else {
                        Intent d = new Intent(AddCarFragment.this, Biometric.class);
                        d.putExtra(WORKER_ID, workerResponce.data.worker.wkWorkID);
                        d.putExtra(PERSONNAME, getIntent().getStringExtra(PERSONNAME));
                        d.putExtra(UNITID, getIntent().getStringExtra(UNITID));
                        d.putExtra(UNITNAME, getIntent().getStringExtra(UNITNAME));
                        d.putExtra(FLOW_TYPE, getIntent().getStringExtra(FLOW_TYPE));
                        d.putExtra(VISITOR_TYPE, getIntent().getStringExtra(VISITOR_TYPE));
                        d.putExtra(COMPANY_NAME, getIntent().getStringExtra(COMPANY_NAME));
                        d.putExtra(MOBILENUMBER, getIntent().getStringExtra(MOBILENUMBER));
                        d.putExtra(COUNTRYCODE, getIntent().getStringExtra(COUNTRYCODE));
                        startActivity(d);
                    }
                    //   uploadImage(imgName,personPhoto);

                    Intent ddc = new Intent(AddCarFragment.this, BackgroundSyncReceiver.class);
                    Log.d("btn_biometric", "af " + imgName);

                    ddc.putExtra(BSR_Action, UPLOAD_STAFF_PHOTO);
                    ddc.putExtra("imgName", imgName);
                    ddc.putExtra(PERSON_PHOTO, byteArray);
                    sendBroadcast(ddc);
                    Intent ddc1 = new Intent(AddCarFragment.this, BackgroundSyncReceiver.class);
                    Log.d("SYNC_STAFF_LIST", "af " + imgName);
                    ddc1.putExtra(BSR_Action, SYNC_STAFF_LIST);
                    sendBroadcast(ddc1);
                    showToast(this, "Staff Registered");
                    finish();
                } else {
                    showToast(this, "Staff not Registered");
                }

            } else {
                showToast(this, "Something went wrong . please try again ");
            }

        }
        //  showToast(this, urlId+" id "+position+" "+memId+" "+MemberType+" ");

    }

    @Override
    public void onDestroy() {  // could be in onPause or onStop
        Picasso.with(this).cancelRequest(target);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//
//          Intent i=new Intent(AddCarFragment.this, Dashboard.class);
//          startActivity(i);
        finish();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnCaptureItemPhoto:
//                selectImage();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_CAMERA);
                break;

        }
    }

    public void showViewPager() {
        Log.e("Data", "Recicved");
//        image_Gallery.setVisibility(View.GONE);
//        viewPager_Image.setVisibility(View.VISIBLE);
//        floatButton.setVisibility(View.VISIBLE);
    }

    public void onCaptureImageResult(Intent data, Context context) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        byte[] byteArray = null;
        try {
            Log.d("Dgddfdf picas", "5 2");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            byteArray = bos.toByteArray();
            int len = bos.toByteArray().length;
            System.out.println("AFTER COMPRESSION-===>" + len);
            bos.flush();
            bos.close();
        } catch (Exception ex) {
            Log.d("Dgddfdf picas", "7");
        }

        assert thumbnail != null;
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        String imgName = System.currentTimeMillis() + ".jpg";

        file = new File(Environment.getExternalStorageDirectory().getPath(), imgName);
        // file= new File(Environment.getExternalStorageDirectory()+ "/DCIM/myCapturedImages", imgName);
        // String fileName = imgName.substring(imgName.lastIndexOf("/")+1);
        // List<String> myList = new ArrayList<String>(Collections.singletonList(String.valueOf(destination.getAbsoluteFile())));
        setviewPager(String.valueOf(file.getAbsoluteFile()), context);
//
        Intent ddc = new Intent(AddCarFragment.this, BackgroundSyncReceiver.class);
        Log.d("btn_biometric", "af " + file.getAbsoluteFile());

        ddc.putExtra(BSR_Action, UPLOAD_STAFF_PHOTO);
        ddc.putExtra("imgName", imgName);
        ddc.putExtra(PERSON_PHOTO, byteArray);
        sendBroadcast(ddc);

        FileOutputStream fo;
        try {
            file.createNewFile();
            fo = new FileOutputStream(file);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();

        }


    }


    private void setviewPager(final String selectedImagePath, final Context context) {
        try {


            if (list.size() == 19) {
                image_Gallery.setVisibility(View.GONE);
            } else {
                image_Gallery.setVisibility(View.VISIBLE);
            }
            final ImageView imageView = (ImageView) LayoutInflater.from(context).inflate(R.layout.image_list, null);
            list.add(selectedImagePath);
            ImageHelper.loadImage(context, selectedImagePath, imageView);

            imageAdapter = new ImageAdapter(list, AddCarFragment.this, "On");
            rv_image.setAdapter(imageAdapter);
            // iamgeLyt.addView(imageView);
            // iv_delete = findViewById(R.id.iv_delete);
            /*  sumeeth code tried for deleting the photo  it worked */

//            iv_delete.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    final AlertDialog.Builder builder = new AlertDialog.Builder(AddCarFragment.this);
//                    builder.setTitle("Delete image");
//                    builder.setCancelable(false);
//                    builder.setMessage("Do you want to delete this image");
//                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            list.remove(selectedImagePath);
//                            //iamgeLyt.removeView(imageView);
//                            imageAdapter.notifyDataSetChanged();
////                            if (list.size() == 18) {
////                                image_Gallery.setVisibility(View.VISIBLE);
////                            } else {
////                                image_Gallery.setVisibility(View.GONE);
////                            }
//                        }
//                    });
//
//                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            builder.setCancelable(true);
//                        }
//                    });
//                    AlertDialog dialog = builder.create();
//                    dialog.show();
//              }
//            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // takePhoto();
                }
                break;
        }
    }

    public void takePhoto() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 1);
            } else {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_CAMERA);
                //   take_picture();
            }
        }
//        else{
//            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            startActivityForResult(intent, REQUEST_CAMERA);
//        }
    }

    public void hideViewPager() {
//        viewPager_Image.setVisibility(View.GONE);
//        floatButton.setVisibility(View.GONE);
        image_Gallery.setVisibility(View.VISIBLE);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE_SPEECH_INPUT:
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Ed_Name.setText(result.get(0));
                    tv_name.setText(result.get(0));
                }
                break;
            case REQUEST_CAMERA:
                if (resultCode == Activity.RESULT_OK) {
                    onCaptureImageResult(data, this);


                    showViewPager();
                }
                break;
            case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    photo = (Bitmap) data.getExtras().get("data");
                    imageView1.setImageBitmap(photo);
                    //personPhoto=photo;
                    SaveImage(photo);

                }
                break;


        }
//        if (requestCode == AddCarFragment.REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {
//            Log.e("Activity", "Result OK");
//
//                onCaptureImageResult(data, this);
//                showViewPager();
//
//        } else  if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
//            Bitmap photo = (Bitmap) data.getExtras().get("data");
//            imageView1.setImageBitmap(photo);
//            personPhoto=photo;
//            Log.d("Activity", "CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE ");
//        }
//        else if(requestCode==REQUEST_CODE_SPEECH_INPUT&& requestCode==Activity.RESULT_OK&&data != null) {
//         //   if (data != null) {
//                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//                Ed_Name.setText(result.get(0));
//           // }
//        }


        Log.d("Activity", "Result Fail");
    }

    public void setLocale(String lang) {
        if (lang == null) {
            lang = "en";
        } else {
        }
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }

    public void speak() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "say something");

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (Exception e) {
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


    void sendStaffImage(String WKEntryImg, String WKEntryGPS, String WKWorkID) {


        SendStaffImageReq sendStaffImageReq = new SendStaffImageReq();

        sendStaffImageReq.WKEntryImg = WKEntryImg;
        sendStaffImageReq.WKEntryGPS = WKEntryGPS;
        sendStaffImageReq.WKWorkID = WKWorkID;

        Log.v("IMAGE NAME....", WKEntryImg);
        //  Toast.makeText(AddCarFragment.this,WKEntryImg,Toast.LENGTH_LONG).show();


        Call<StaffImageRes> call = champApiInterface.sendStaffImage(sendStaffImageReq);
        call.enqueue(new Callback<StaffImageRes>() {
            @Override
            public void onResponse(Call<StaffImageRes> call, Response<StaffImageRes> response) {

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

            @Override
            public void onFailure(Call<StaffImageRes> call, Throwable t) {
                Toast.makeText(AddCarFragment.this, t.toString(), Toast.LENGTH_LONG).show();
                call.cancel();
            }
        });

    }

    public void SaveImage(Bitmap showedImgae) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/DCIM/myCapturedImages");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "FILENAME-" + n + ".jpg";
        file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            showedImgae.compress(Bitmap.CompressFormat.JPEG, 100, out);
            // Toast.makeText(AddCarFragment.this, "Image Saved", Toast.LENGTH_SHORT).show();
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        getApplicationContext().sendBroadcast(mediaScanIntent);
    }


}

