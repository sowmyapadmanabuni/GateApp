package com.oyespace.guards.kidexit;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.custom.FirebaseModelInputOutputOptions;
import com.google.firebase.ml.custom.FirebaseModelInterpreter;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentTextRecognizer;
import com.oyespace.guards.R;
import com.oyespace.guards.activity.StaffEntryRegistration;
import com.oyespace.guards.camtest.ImageHelper;
import com.oyespace.guards.constants.PrefKeys;
import com.oyespace.guards.network.ChampApiInterface;
import com.oyespace.guards.network.ResponseHandler;
import com.oyespace.guards.network.RestClient;
import com.oyespace.guards.network.URLData;
import com.oyespace.guards.ocr.CheckUser;
import com.oyespace.guards.ocr.VehicalReq;
import com.oyespace.guards.qrscanner.CustomViewFinderScannerActivity;
import com.oyespace.guards.utils.LocalDb;
import com.oyespace.guards.utils.Prefs;
import com.oyespace.guards.vehicle_others.VehicleOthersServiceProviderListActivity;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.PriorityQueue;

import static com.oyespace.guards.constants.PrefKeys.LANGUAGE;
import static com.oyespace.guards.utils.ConstantUtils.ACCOUNT_ID;
import static com.oyespace.guards.utils.ConstantUtils.ASSOCIATION_ID;
import static com.oyespace.guards.utils.ConstantUtils.COMPANY_NAME;
import static com.oyespace.guards.utils.ConstantUtils.COUNTRYCODE;
import static com.oyespace.guards.utils.ConstantUtils.FLOW_TYPE;
import static com.oyespace.guards.utils.ConstantUtils.GATE_NO;
import static com.oyespace.guards.utils.ConstantUtils.GUARDIANNAME;
import static com.oyespace.guards.utils.ConstantUtils.ITEMS_PHOTO_LIST;
import static com.oyespace.guards.utils.ConstantUtils.MOBILENUMBER;
import static com.oyespace.guards.utils.ConstantUtils.PERSONNAME;
import static com.oyespace.guards.utils.ConstantUtils.UNITID;
import static com.oyespace.guards.utils.ConstantUtils.UNITNAME;
import static com.oyespace.guards.utils.ConstantUtils.UNITOCCUPANCYSTATUS;
import static com.oyespace.guards.utils.ConstantUtils.UNIT_ACCOUNT_ID;
import static com.oyespace.guards.utils.ConstantUtils.VEHICLE_NUMBER;
import static com.oyespace.guards.utils.ConstantUtils.VISITOR_TYPE;
import static com.oyespace.guards.utils.Utils.showToast;

public class KidExitCaptureImageOcr extends Activity implements View.OnClickListener,ResponseHandler  {

    String imgNamee;
    public static final int REQUEST_CAMERA = 0, SELECT_FILE = 1, PICK_INSURANCE_REQUEST_CODE = 2, PICK_RCBOOK_REQUEST_CODE = 3;
    private static final int DIM_BATCH_SIZE = 1;
    private static final int DIM_PIXEL_SIZE = 3;
    private static final int DIM_IMG_SIZE_X = 224;
    private static final int DIM_IMG_SIZE_Y = 224;
    private ViewPager viewPager;
    private  String vehicalNumberData;
    private LinearLayout iamgeLyt;
    private ArrayList<String> list = new ArrayList<>();
    private ArrayList<String> listpager_Array = new ArrayList<>();
    private Button resident , guest , other  ,buttonNext;
    private ChampApiInterface champApiInterface;
    /**
     * Name of the model file hosted with Firebase.
     */
    private static final String HOSTED_MODEL_NAME = "cloud_model_1";
    private static final String LOCAL_MODEL_ASSET = "mobilenet_v1_1.0_224_quant.tflite";
    private static final int RESULTS_TO_SHOW = 3;
    private final PriorityQueue<Map.Entry<String, Float>> sortedLabels =
            new PriorityQueue<>(
                    RESULTS_TO_SHOW,
                    new Comparator<Map.Entry<String, Float>>() {
                        @Override
                        public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float>
                                o2) {
                            return (o1.getValue()).compareTo(o2.getValue());
                        }
                    });
    /* Preallocated buffers for storing image data. */
    private final int[] intValues = new int[DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y];
    private Integer mImageMaxWidth;
    // Max height (portrait mode)
    private Integer mImageMaxHeight;
    private Bitmap mSelectedImage;
    private View view;
    private ImageView image_Gallery, insurance_file_name, rc_book_file_name;
    private EditText vehicalnumber ;
    private ProgressDialog mProgressDialog;
     TextView txt_assn_name;
    TextView txt_gate_name;
     TextView txt_device_name;
    /**
     * An instance of the driver class to run model inference with Firebase.
     */
    private FirebaseModelInterpreter mInterpreter;
    /**
     * Data configuration of input & output data of model.
     */
    private FirebaseModelInputOutputOptions mDataOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLocale(Prefs.getString(LANGUAGE,null));
        setContentView(R.layout.ocr_layout);
        initViews();


        txt_assn_name = findViewById(R.id.txt_assn_name);
        txt_gate_name = findViewById(R.id.txt_gate_name);
        txt_device_name = findViewById(R.id.txt_device_name);
        if (Prefs.getString(PrefKeys.MODEL_NUMBER, null).equals("Nokia 1")) {
            txt_assn_name.setTextSize(5 * getResources().getDisplayMetrics().density);
        }
        txt_assn_name.setText("Society: " + LocalDb.getAssociation().getAsAsnName());
        txt_gate_name.setText("Gate No: " + Prefs.getString(GATE_NO, null));
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


        mProgressDialog =new ProgressDialog(this);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        // this function will call cloud api .

    }

    //cloud data to retrive data

    private void runCloudTextRecognition(Bitmap mSelectedImage) {

        // mCloudButton.setEnabled(false);

        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(mSelectedImage);
        FirebaseVisionDocumentTextRecognizer recognizer = FirebaseVision.getInstance()
                .getCloudDocumentTextRecognizer();
        recognizer.processImage(image)
                .addOnSuccessListener(
                        new OnSuccessListener<FirebaseVisionDocumentText>() {
                            @Override
                            public void onSuccess(FirebaseVisionDocumentText texts) {
                                //  mCloudButton.setEnabled(true);
                                processCloudTextRecognitionResult(texts);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception

                                //  mCloudButton.setEnabled(true);
                                e.printStackTrace();
                            }
                        });
    }


    private void processCloudTextRecognitionResult(FirebaseVisionDocumentText text) {
        // Task completed successfully
        if (text == null) {

            new LovelyStandardDialog(this, LovelyStandardDialog.ButtonLayout.VERTICAL)
                    .setTopColorRes(R.color.google_red)
                    .setIcon(R.drawable.ic_info_black_24dp)
                    //This will add Don't show again checkbox to the dialog. You can pass any ID as argument
                    .setTitle("Invalid ")
                    .setTitleGravity(Gravity.CENTER)
                    .setMessage("Sorry we are not able to detect Vehicle Number ")
                    .setMessageGravity(Gravity.CENTER)
                    .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    })

                    .show();

            return;
        }
//        mGraphicOverlay.clear();
        List<FirebaseVisionDocumentText.Block> blocks = text.getBlocks();
        for (int i = 0; i < blocks.size(); i++) {
            List<FirebaseVisionDocumentText.Paragraph> paragraphs = blocks.get(i).getParagraphs();
            for (int j = 0; j < paragraphs.size(); j++) {
                List<FirebaseVisionDocumentText.Word> words = paragraphs.get(j).getWords();
                for (int l = 0; l < words.size(); l++) {

                    Log.d("data in text ", text.getText());

                    vehicalnumber.setText(text.getText().replaceAll("[-+^]*", " "));
                    vehicalNumberData = vehicalnumber.getText().toString().trim();

                }
            }
        }
    }


    private void initViews() {
        vehicalnumber = findViewById(R.id.txt_Vehical_otp);
        iamgeLyt = findViewById(R.id.imageLyt);
        resident = findViewById(R.id.resident);
        guest = findViewById(R.id.guest);
        other = findViewById(R.id.other_guest);
        buttonNext = findViewById(R.id.buttonNext);
        buttonNext.setText(getResources().getString(R.string.textverify));
        resident.setVisibility(View.INVISIBLE);
        guest.setVisibility(View.INVISIBLE);
        other.setVisibility(View.INVISIBLE);
        resident.setOnClickListener(this);
        guest.setOnClickListener(this);
        other.setOnClickListener(this);
        buttonNext.setOnClickListener(this);
        selectImage();

        iamgeLyt.removeAllViews();
        list.clear();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.buttonNext:

                if(!vehicalnumber.getText().toString().matches("")){
                   // checkValidateUserVehical(vehicalnumber.getText().toString());

                    Intent i_vehicle = new Intent(KidExitCaptureImageOcr.this, KidExitStaffEntryRegistration.class);
                    i_vehicle.putExtra(UNITID, getIntent().getStringExtra(UNITID));
                    i_vehicle.putExtra(UNITNAME, getIntent().getStringExtra(UNITNAME));
                    i_vehicle.putExtra(ACCOUNT_ID, getIntent().getIntExtra(ACCOUNT_ID,0));
                    i_vehicle.putExtra(MOBILENUMBER, getIntent().getStringExtra(MOBILENUMBER));
                    i_vehicle.putExtra(COUNTRYCODE, getIntent().getStringExtra(COUNTRYCODE));
                    i_vehicle.putExtra(FLOW_TYPE, getIntent().getStringExtra(FLOW_TYPE));
                    i_vehicle.putExtra(VISITOR_TYPE, getIntent().getStringExtra(VISITOR_TYPE));
                    i_vehicle.putExtra(UNIT_ACCOUNT_ID, getIntent().getStringExtra(UNIT_ACCOUNT_ID));
                    i_vehicle.putExtra("RESIDENT_NUMBER", getIntent().getStringExtra("RESIDENT_NUMBER"));
                    i_vehicle.putExtra(UNITOCCUPANCYSTATUS, getIntent().getStringExtra(UNITOCCUPANCYSTATUS));
                    i_vehicle.putExtra(PERSONNAME, getIntent().getStringExtra(PERSONNAME));
                    i_vehicle.putExtra("Base64",getIntent().getStringExtra("Base64"));
                    i_vehicle.putExtra(ITEMS_PHOTO_LIST,getIntent().getStringArrayListExtra(ITEMS_PHOTO_LIST));
                    i_vehicle.putExtra(VEHICLE_NUMBER,vehicalnumber.getText().toString());
                    i_vehicle.putExtra(COMPANY_NAME, getIntent().getStringExtra(COMPANY_NAME));
                    i_vehicle.putExtra(GUARDIANNAME,getIntent().getStringExtra(GUARDIANNAME));
                    startActivity(i_vehicle);
                    finish();
                } else {
                    showToast(KidExitCaptureImageOcr.this,"Enter Vehicle number");
                }

                break;
            case R.id.resident:

                break;
            case R.id.guest:

                deleteImage();
                Intent in = new Intent(this, CustomViewFinderScannerActivity.class);
                startActivity(in);
                finish();
                break;

            case R.id.other_guest:

                deleteImage();
                Intent in2 = new Intent(this, VehicleOthersServiceProviderListActivity.class);
                in2.putExtra(VEHICLE_NUMBER,vehicalnumber.getText().toString());
                startActivity(in2);
                finish();
                break;

        }

    }

    @Override
    public void onFailure(Exception e, int urlId) {

    }

    @Override
    public void onSuccess(String responce, Object data, int urlId, int position) {

        if (urlId == URLData.URL_CHEK_VEHICAL.getUrlId()) {
            CheckUser loginDetailsResponce = (CheckUser) data;
            if (loginDetailsResponce != null) {
                mProgressDialog.dismiss();
                if(loginDetailsResponce.success == true && loginDetailsResponce.data.message.equalsIgnoreCase("Vehicle Exists")) {

                    showToast(this,loginDetailsResponce.data.vehicleRegdNumber);

                    new LovelyStandardDialog(this, LovelyStandardDialog.ButtonLayout.VERTICAL)
                            .setTopColorRes(R.color.green)
                            .setIcon(R.drawable.ic_home_black_24dp)
                            //This will add Don't show again checkbox to the dialog. You can pass any ID as argument
                            .setTitle("Vehicle Verified ")
                            .setTitleGravity(Gravity.CENTER)
                            .setMessage("Vehicle have been verified as Resident "+loginDetailsResponce.data.vehicleRegdNumber)
                            .setMessageGravity(Gravity.CENTER)
                            .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
//                                    Intent intent = new Intent(CaptureImageOcr.this, Dashboard.class);
//                                    startActivity(intent);
                                    finish();
                                }
                            })

                            .show();
                    resident.setVisibility(View.GONE);
                    other.setVisibility(View.GONE);
                    guest.setVisibility(View.GONE);

                }else{
                    new LovelyStandardDialog(this, LovelyStandardDialog.ButtonLayout.VERTICAL)
                            .setTopColorRes(R.color.red)
                            .setIcon(R.drawable.ic_info_black_24dp)
                            //This will add Don't show again checkbox to the dialog. You can pass any ID as argument
                            .setTitle("Invalid ")
                            .setTitleGravity(Gravity.CENTER)
                            .setMessage("Unregistered Vehicle")
                            .setMessageGravity(Gravity.CENTER)
                            .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            })

                            .show();
//                    other.setVisibility(View.VISIBLE);
//                    guest.setVisibility(View.VISIBLE);
//                    resident.setVisibility(View.GONE);
                }

            } else {
                showToast(this, "Something went wrong . please try again ");
            }

        }

    }

//    network call

    public void checkValidateUserVehical(String val){

//        mProgressDialog =new ProgressDialog(this);
//        mProgressDialog.setIndeterminate(false);
//        mProgressDialog.setCancelable(false);
//        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        RestClient restClient = RestClient.getInstance();
        VehicalReq vehicalReq = new  VehicalReq();
        vehicalReq.VERegNo = val;
        vehicalReq.ASAssnID=String.valueOf( Prefs.getInt(ASSOCIATION_ID,0));
        restClient.addHeader("X-Champ-APIKey", "1FDF86AF-94D7-4EA9-8800-5FBCCFF8E5C1");
        restClient.post(this,vehicalReq,CheckUser.class,this, URLData.URL_CHEK_VEHICAL);

    }


// CAMERA AND GALLERY

    private void selectImage() {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                    }


    public void showViewPager() {
        Log.e("Data", "Recicved");

    }

    public void onCaptureImageResult(Intent data, Context context) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        assert thumbnail != null;
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        runCloudTextRecognition(thumbnail);
        imgNamee = System.currentTimeMillis() + ".jpg";

        File destination = new File(Environment.getExternalStorageDirectory().getPath(),
                imgNamee);

          setviewPager(String.valueOf(destination.getAbsoluteFile()), context);
        Log.d("Image url ", destination.getAbsoluteFile().toString());

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setviewPager(final String selectedImagePath, final Context context) {
        try {

//            if (list.size() >= 4) {
//                image_Gallery.setVisibility(View.GONE);
//            } else {
//                image_Gallery.setVisibility(View.VISIBLE);
//            }
//
final ImageView imageView = (ImageView) LayoutInflater.from(context).inflate(R.layout.image_list, null);
            list.add(selectedImagePath);
            ImageHelper.loadImage(context, selectedImagePath, imageView);
            iamgeLyt.addView(imageView);

            /*  sumeeth code tried for deleting the photo  it worked */

//            imageView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    final AlertDialog.Builder builder = new AlertDialog.Builder(CaptureImageOcr.this);
//                    builder.setTitle("Delete image");
//                    builder.setCancelable(false);
//                    builder.setMessage("Do you want to delete this image");
//                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            list.remove(selectedImagePath);
//                            iamgeLyt.removeView(imageView);
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
//                }
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
                    takePhoto();
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

    }

    public void hideViewPager() {
        image_Gallery.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Log.e("Activity", "Result OK");

            if (requestCode == KidExitCaptureImageOcr.REQUEST_CAMERA) {
                onCaptureImageResult(data, this);
                showViewPager();
            }

        } else {
            Log.e("Activity", "Result Fail");
            finish();
        }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();

//        Intent i=new Intent(CaptureImageOcr.this,Dashboard.class);
//        startActivity(i);
        finish();
    }

    public void deleteImage() {
        File dir = new File(Environment.getExternalStorageDirectory().getPath());
        File file = new File(dir, imgNamee);
        file.delete();
    }
}
