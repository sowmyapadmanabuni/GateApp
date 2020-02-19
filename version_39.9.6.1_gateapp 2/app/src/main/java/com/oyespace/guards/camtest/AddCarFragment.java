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
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
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
import com.oyespace.guards.PojoClasses.GetLatestRecord;
import com.oyespace.guards.R;
import com.oyespace.guards.activity.BaseKotlinActivity;
import com.oyespace.guards.activity.Biometric;
import com.oyespace.guards.activity.StaffDetails;
import com.oyespace.guards.activity.StaffEntryRegistration;
import com.oyespace.guards.constants.PrefKeys;
import com.oyespace.guards.network.*;
import com.oyespace.guards.request.SendStaffImageReq;
import com.oyespace.guards.request.StaffRegistrationReqJv;
import com.oyespace.guards.responce.StaffImageRes;
import com.oyespace.guards.responce.StaffRegistrationRespJv;
import com.oyespace.guards.utils.LocalDb;
import com.oyespace.guards.utils.Prefs;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static com.oyespace.guards.constants.PrefKeys.LANGUAGE;
import static com.oyespace.guards.constants.PrefKeys.MOBILE_NUMBER;
import static com.oyespace.guards.utils.ConstantUtils.*;
import static com.oyespace.guards.utils.RandomUtils.encodeToBase64;
import static com.oyespace.guards.utils.Utils.showToast;


public class AddCarFragment extends BaseKotlinActivity implements View.OnClickListener {

    /*sumeeth fragment*/
    File file;
    String encodedImage;
    Button iv_torch;
    int clickable1 = 0;
    ChampApiInterface champApiInterface;

    private final int REQUEST_CODE_SPEECH_INPUT = 100;
    EditText Ed_Name;
    public static Button image_Gallery, submit_button, buttonCapture;
    private ViewPager viewPager;
    ImageAdapter imageAdapter;
    RecyclerView rv_image;
    ImageView iv_delete, imageView1;
    final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    Bitmap photo = null;
    Bitmap images = null;
    TextView tv_name;
    TextView txt_assn_name, txt_device_name, txt_gate_name;
    private View view;
    // private LinearLayout iamgeLyt;
    String imgName;
    private FragmentManager fragmentManager;
    ImageView dialog_imageview;
    static ArrayList<String> list = new ArrayList<>();
    private EditText notes, Regno, kms, exp_date, exp_price, car_id;
    private ProgressDialog progressDialog;
    private ImageView insurance_file_name, rc_book_file_name;
    private ArrayList<String> listpager_Array = new ArrayList<>();
    public static final int REQUEST_CAMERA = 0;
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
        getInviation();
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

        iv_torch=findViewById(R.id.iv_torch);

        iv_torch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    CameraManager camManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                    String cameraId = null;
                    try {
                        cameraId = camManager.getCameraIdList()[0];

                        if (clickable1 == 0) {
                            try {

                                iv_torch.setBackground(getResources().getDrawable(R.drawable.torch_off));
                                camManager.setTorchMode(cameraId, true);   //Turn ON

                                //  iv_torch!!.text = "OFF"
                                clickable1 = 1;
                            } catch (CameraAccessException e){
                                e.printStackTrace();
                            }
                        } else if (clickable1 == 1) {
                            camManager.setTorchMode(cameraId, false);
                            // iv_torch!!.text = "ON"
                            iv_torch.setBackground(getResources().getDrawable(R.drawable.torch_on));
                            clickable1 = 0;

                        }

                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        champApiInterface = ChampApiClient.getClient().create(ChampApiInterface.class);

        tv_name = findViewById(R.id.tv_name);
        tv_name.setText(getIntent().getStringExtra(PERSONNAME));
        txt_assn_name = findViewById(R.id.txt_assn_name);
        txt_device_name = findViewById(R.id.txt_device_name);
        txt_gate_name = findViewById(R.id.txt_gate_name);
        image_Gallery = findViewById(R.id.btnCaptureItemPhoto);
        //iamgeLyt = (LinearLayout) findViewById(R.id.imageLyt);
        rv_image = findViewById(R.id.rv_image);

        buttonCapture = findViewById(R.id.buttonCapture);
        imageView1 = findViewById(R.id.imageView1);



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


                builder.setView(dialogView);

                //finally creating the alert dialog and displaying it
                final AlertDialog alertDialog = builder.create();

                alertDialog.show();
            }
        });


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

        image_Gallery.setOnClickListener(this);

        submit_button = findViewById(R.id.buttonNext);


        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 4);
        rv_image.setLayoutManager(mLayoutManager);

        //iamgeLyt.removeAllViews();
        list.clear();

        buttonCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

            }
        });

        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit_button.setEnabled(false);
                submit_button.setClickable(false);



                if (imageView1.getDrawable().getConstantState()!=getResources().getDrawable(R.drawable.user_icon_black).getConstantState()) {

                        byte[] byteArray=null;
                        try {
                            Log.d("Dgddfdf picas","5 2");
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            photo.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                            byteArray = bos.toByteArray();
                            int len = bos.toByteArray().length;
                            System.out.println("AFTER COMPRESSION-===>" + len);

                            bos.flush();
                            bos.close();
                        }catch (Exception ex){
                            Log.d("Dgddfdf picas","7");
                        }

                        BitmapDrawable drawable = (BitmapDrawable) imageView1.getDrawable();
                        Bitmap bitmap = drawable.getBitmap();
                        encodedImage = encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 100);

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
                        d.putExtra(ACCOUNT_ID, getIntent().getIntExtra(ACCOUNT_ID,0));
                        d.putExtra(UNIT_ACCOUNT_ID,getIntent().getStringExtra(UNIT_ACCOUNT_ID));
                        d.putExtra(BLOCK_ID,getIntent().getStringExtra(BLOCK_ID));
                        d.putExtra(UNITOCCUPANCYSTATUS,getIntent().getStringExtra(UNITOCCUPANCYSTATUS));
                        d.putExtra(VISITOR_PURPOSE,getIntent().getStringExtra(VISITOR_PURPOSE));
                        d.putExtra("Base64", encodedImage);
                        startActivity(d);
                        finish();

                }
                else {

                    Toast.makeText(getApplicationContext(),"Capture Photo ", Toast.LENGTH_SHORT).show();
                    submit_button.setEnabled(true);
                    submit_button.setClickable(true);

                }

            }
        });
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();

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


    public void hideViewPager() {

        image_Gallery.setVisibility(View.VISIBLE);
    }



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
    void getInviation() {
        Call<GetLatestRecord> call = champApiInterface.getLatestRecord(getIntent().getStringExtra(MOBILE_NUMBER), String.valueOf(LocalDb.getAssociation().getAsAssnID()));
        call.enqueue(new Callback<GetLatestRecord>() {
            @Override
            public void onResponse(Call<GetLatestRecord> call, Response<GetLatestRecord> response) {

                if (response.body().getData() != null) {
                    String val = response.body().getData().getVisitorLatestRecord().getVlEntryImg();

                    Toast.makeText(AddCarFragment.this, response.body().getData().toString(), Toast.LENGTH_SHORT).show();
                    byte[] imageBytes = Base64.decode(val, Base64.DEFAULT);
                    Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    imageView1.setImageBitmap(decodedImage);


                } else {

                }
            }

            @Override
            public void onFailure(Call<GetLatestRecord> call, Throwable t) {
                call.cancel();
            }
        });


    }


}