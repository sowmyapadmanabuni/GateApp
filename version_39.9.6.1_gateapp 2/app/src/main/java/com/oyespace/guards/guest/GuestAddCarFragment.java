package com.oyespace.guards.guest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.util.Base64;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.common.util.IOUtils;
import com.oyespace.guards.R;
import com.oyespace.guards.activity.BaseKotlinActivity;
import com.oyespace.guards.camtest.CarImages_Adapter;
import com.oyespace.guards.camtest.ImageAdapter;
import com.oyespace.guards.camtest.ImageHelper;
import com.oyespace.guards.constants.PrefKeys;
import com.oyespace.guards.utils.ConstantUtils;
import com.oyespace.guards.utils.LocalDb;
import com.oyespace.guards.utils.Prefs;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

import static com.oyespace.guards.constants.PrefKeys.LANGUAGE;
import static com.oyespace.guards.utils.ConstantUtils.ACCOUNT_ID;
import static com.oyespace.guards.utils.ConstantUtils.BLOCK_ID;
import static com.oyespace.guards.utils.ConstantUtils.COMPANY_NAME;
import static com.oyespace.guards.utils.ConstantUtils.COUNTRYCODE;
import static com.oyespace.guards.utils.ConstantUtils.FLOW_TYPE;
import static com.oyespace.guards.utils.ConstantUtils.GATE_NO;
import static com.oyespace.guards.utils.ConstantUtils.GUEST_REGISTRATION;
import static com.oyespace.guards.utils.ConstantUtils.IMAGE_BASE_URL;
import static com.oyespace.guards.utils.ConstantUtils.ITEMS_PHOTO_LIST;
import static com.oyespace.guards.utils.ConstantUtils.MOBILENUMBER;
import static com.oyespace.guards.utils.ConstantUtils.PERSONNAME;
import static com.oyespace.guards.utils.ConstantUtils.PERSON_PHOTO;
import static com.oyespace.guards.utils.ConstantUtils.UNITID;
import static com.oyespace.guards.utils.ConstantUtils.UNITNAME;
import static com.oyespace.guards.utils.ConstantUtils.UNIT_ACCOUNT_ID;
import static com.oyespace.guards.utils.ConstantUtils.VISITOR_TYPE;
import static com.oyespace.guards.utils.RandomUtils.encodeToBase64;


public class GuestAddCarFragment extends BaseKotlinActivity implements View.OnClickListener {

    String encodedImage;
    Button iv_torch;
    int clickable1 = 0;
    TextView txt_assn_name,txt_device_name,txt_gate_name ;
    TextView tv_name;
    File file;
    //    private FloatingActionButton floatButton;
    public static final int REQUEST_CAMERA = 0, SELECT_FILE = 1, PICK_INSURANCE_REQUEST_CODE = 2, PICK_RCBOOK_REQUEST_CODE = 3;
    static ArrayList<String> list = new ArrayList<>();
    ImageAdapter imageAdapter;
    RecyclerView rv_image;
    ImageView iv_delete, imageView1,iv_edit;
    int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    Bitmap photo = null;
    /*sumeeth fragment*/
    private FragmentManager fragmentManager;
    private ViewPager viewPager;
    private View view;
    private EditText notes, Regno, kms, exp_date, exp_price, car_id;
    private Button image_Gallery, submit_button, buttonCapture,buttonSkip;
    //    private ViewPager viewPager_Image;
    private TextView upload_rc_book,
            upload_insurance, Make, model, year, variant, fuel, color, transmission, owners, insurance;
  //  private LinearLayout iamgeLyt;
    private ProgressDialog progressDialog;
    private ImageView insurance_file_name, rc_book_file_name;
    private ArrayList<String> listpager_Array = new ArrayList<>();
    private CarImages_Adapter adapter;
    private CheckBox sell_to_dealer;
    private ProgressDialog dialog;
    //    private final int Date_id = 7;
    private String makeId = "", modelId = "", variant_id = "";
    private String rcImagePath, insImagePath;
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

    public GuestAddCarFragment() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLocale(Prefs.getString(LANGUAGE, null));

        setContentView(R.layout.add_car_fragment);
        initViews();
        txt_assn_name.setText("Society: " + LocalDb.getAssociation().getAsAsnName());
        txt_gate_name.setText("Gate No: "+Prefs.getString(GATE_NO,""));
        try {
            String appVersion="";
            PackageManager manager = getBaseContext().getPackageManager();
            PackageInfo info = manager.getPackageInfo(getBaseContext().getPackageName(), 0);
            appVersion = info.versionName;
            Log.d("tag","app "+appVersion+" "+info.versionName);
            txt_device_name.setText("V: "+appVersion);

        }catch (Exception ex){
            ex.printStackTrace();
            txt_device_name.setText(" " );

        }
    }

    private void initViews() {

        // makeArrayList = new ArrayList<>();

//        viewPager_Image = (ViewPager) view.findViewById(R.id.add_car_view_pager);
        //GridView gridview

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

        tv_name=findViewById(R.id.tv_name);
        iv_edit=findViewById(R.id.iv_edit);
        iv_edit.setVisibility(View.GONE);
        tv_name.setText(getIntent().getStringExtra(PERSONNAME));
        txt_gate_name=findViewById(R.id.txt_gate_name);
        txt_assn_name=findViewById(R.id.txt_assn_name);
        if(Prefs.getString(PrefKeys.MODEL_NUMBER,null).equals("Nokia 1")) {
            txt_assn_name.setTextSize(5 * getResources().getDisplayMetrics().density);
        }
        txt_device_name=findViewById(R.id.txt_device_name);


        image_Gallery = findViewById(R.id.btnCaptureItemPhoto);
        buttonSkip=findViewById(R.id.buttonSkip);
       // buttonSkip.setVisibility(View.GONE);
        //iamgeLyt = (LinearLayout) findViewById(R.id.imageLyt);
        rv_image = findViewById(R.id.rv_image);
        iv_delete = findViewById(R.id.iv_delete);
//        floatButton = (FloatingActionButton) view.findViewById(R.id.fab);
        image_Gallery.setOnClickListener(this);
//        floatButton.setOnClickListener(this);
        submit_button = findViewById(R.id.buttonNext);

        buttonCapture = findViewById(R.id.buttonCapture);
        imageView1 = findViewById(R.id.imageView1);

        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                ViewGroup viewGroup = findViewById(android.R.id.content);

                View dialogView = LayoutInflater.from(GuestAddCarFragment.this).inflate(R.layout.dialog_big_image, viewGroup, false);


                AlertDialog.Builder builder = new AlertDialog.Builder(GuestAddCarFragment.this);

                ImageView dialog_imageview = dialogView.findViewById(R.id.dialog_imageview);
                dialog_imageview.setBackground(imageView1.getDrawable());


                //Picasso.with(GuestAddCarFragment.this).load(IMAGE_BASE_URL + "Images/PERSON" + "NONREGULAR" + getIntent().getStringExtra(MOBILENUMBER) + ".jpg").placeholder(R.drawable.user_icon_black).memoryPolicy(MemoryPolicy.NO_CACHE).into(dialog_imageview);


//                Picasso.with(GuestAddCarFragment.this)
//                        .load(IMAGE_BASE_URL +"Images/PERSONAssociation"+Prefs.getInt(ASSOCIATION_ID,0)+"NONREGULAR"+getIntent().getStringExtra(MOBILENUMBER)+".jpg")
//                        .placeholder(R.drawable.user_icon_black).error(R.drawable.user_icon_black).into(dialog_imageview);

                builder.setView(dialogView);

                //finally creating the alert dialog and displaying it
                final AlertDialog alertDialog = builder.create();

                alertDialog.show();
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 4);
        rv_image.setLayoutManager(mLayoutManager);

       // iamgeLyt.removeAllViews();
        list.clear();


//        Picasso.with(this)
//                .load(IMAGE_BASE_URL +"Images/PERSONAssociation"+Prefs.getInt(ASSOCIATION_ID,0)+"NONREGULAR"+getIntent().getStringExtra(MOBILENUMBER)+".jpg")
//                .placeholder(R.drawable.user_icon_black).error(R.drawable.user_icon_black).into(imageView1);

        Log.v("Oyespace", IMAGE_BASE_URL + "Images/PERSON" + "NONREGULAR" + getIntent().getStringExtra(MOBILENUMBER) + ".jpg");

//        Log.d("intentdata ", " AddCarFragment " + getIntent().getStringExtra(UNITNAME) + " " + getIntent().getStringExtra(UNITID)
//                + " " + getIntent().getStringExtra(MOBILENUMBER) + " " + getIntent().getStringExtra(COUNTRYCODE) + " " + getIntent().getStringExtra(PERSONNAME));


        if (getIntent().getStringExtra(FLOW_TYPE).equals(GUEST_REGISTRATION)) {
            image_Gallery.setVisibility(View.INVISIBLE);
            rv_image.setVisibility(View.INVISIBLE);
            ((TextView) findViewById(R.id.txt_header)).setText(getString(R.string.textpersonphotoscreen));
        } else {
            if (getIntent().getIntExtra(ACCOUNT_ID, 0) != 0) {
                // Picasso.with(this).load(IMAGE_BASE_URL + "Images/" + "PERSON"+"NONREGULAR" + getIntent().getStringExtra(MOBILENUMBER) + ".jpg").into(target);
                imageView1.setImageBitmap(photo);
//                Picasso.with(this)
//                        .load(IMAGE_BASE_URL + "Images/"+"PERSONAssociation"+Prefs.getInt(ASSOCIATION_ID,0)+"NONREGULAR" + getIntent().getStringExtra(MOBILENUMBER) + ".jpg")
//                        .placeholder(R.drawable.placeholder_dark).error(R.drawable.placeholder_dark).into(imageView1);

               //   Picasso.with(this).load(IMAGE_BASE_URL +"Images/PERSON"+getIntent().getStringExtra(MOBILENUMBER)+".jpg").placeholder(R.drawable.user_icon_black).memoryPolicy(MemoryPolicy.NO_CACHE).into(imageView1);


            }

        }

        iv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(GuestAddCarFragment.this, GuestNameEntryScreen.class);
                i.putExtra(UNITID,getIntent().getStringExtra(UNITID) );
                i.putExtra(UNITNAME, getIntent().getStringExtra(UNITNAME));
                i.putExtra(FLOW_TYPE,getIntent().getStringExtra(FLOW_TYPE));
                i.putExtra(VISITOR_TYPE,getIntent().getStringExtra(VISITOR_TYPE));
                i.putExtra(COMPANY_NAME,getIntent().getStringExtra(COMPANY_NAME));
                i.putExtra(MOBILENUMBER, getIntent().getStringExtra(MOBILENUMBER));
                i.putExtra(COUNTRYCODE, getIntent().getStringExtra(COUNTRYCODE));
                i.putExtra(UNIT_ACCOUNT_ID,getIntent().getStringExtra(ConstantUtils.UNIT_ACCOUNT_ID));
                i.putExtra(BLOCK_ID, getIntent().getStringExtra(BLOCK_ID));

                startActivity(i);
                finish();
            }
        });

        buttonCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

            }
        });

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

        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                submit_button.setEnabled(false);
                submit_button.setClickable(false);


                byte[] byteArray = null;
                try {
                    Log.d("Dgddfdf picas", "5 2");
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    byteArray = bos.toByteArray();
                    int len = bos.toByteArray().length;
                    System.out.println("AFTER COMPRESSION-===>" + len);

                    BitmapDrawable drawable = (BitmapDrawable) imageView1.getDrawable();
                    Bitmap bitmap = drawable.getBitmap();
                     encodedImage = encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 100);

//                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                    Bitmap bitmapp = BitmapFactory.decodeResource(getResources(), R.drawable.user_icon_black);
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                    byte[] imageBytes = baos.toByteArray();
//                    String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
//
//                    imageBytes = Base64.decode(imageString, Base64.DEFAULT);
//                    Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
//                    imageView.setImageBitmap(decodedImage);

                    bos.flush();
                    bos.close();
                } catch (Exception ex) {
                    Log.d("Dgddfdf picas", "7");
                }

//                if (personPhoto == null) {
//                    Toast.makeText(getApplicationContext(), "Capture Photo ", Toast.LENGTH_SHORT).show();
//                } else {


                    Intent d = new Intent(GuestAddCarFragment.this, GuestEntryRegistration.class);
////                    Log.d("intentdata personPhoto", "buttonNext " + getIntent().getStringExtra(UNITNAME) + " " + getIntent().getStringExtra(UNITID)
////                            + " " + getIntent().getStringExtra(MOBILENUMBER) + " " + getIntent().getStringExtra(COUNTRYCODE) + " " + getIntent().getStringExtra(PERSONNAME));
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
                d.putExtra(UNIT_ACCOUNT_ID, getIntent().getStringExtra(ConstantUtils.UNIT_ACCOUNT_ID));
                d.putExtra(BLOCK_ID, getIntent().getStringExtra(BLOCK_ID));
                d.putExtra("Base64", encodedImage);
                    startActivity(d);
                   finish();
                }

            //}
        });
    }

    @Override
    public void onDestroy() {  // could be in onPause or onStop
        Picasso.with(this).cancelRequest(target);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        Intent d = new Intent(GuestAddCarFragment.this, Dashboard.class);
//        startActivity(d);
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
        assert thumbnail != null;
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory().getPath(),
                System.currentTimeMillis() + ".jpg");

        // List<String> myList = new ArrayList<String>(Collections.singletonList(String.valueOf(destination.getAbsoluteFile())));
        setviewPager(String.valueOf(destination.getAbsoluteFile()), context);

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

            if (list.size() >= 20) {
                image_Gallery.setVisibility(View.GONE);
            } else {
                image_Gallery.setVisibility(View.VISIBLE);
            }
            final ImageView imageView = (ImageView) LayoutInflater.from(context).inflate(R.layout.image_list, null);
            list.add(selectedImagePath);
            ImageHelper.loadImage(context, selectedImagePath, imageView);
            imageAdapter = new ImageAdapter(list, GuestAddCarFragment.this,"On");
            rv_image.setAdapter(imageAdapter);
            // iamgeLyt.addView(imageView);

            /*  sumeeth code tried for deleting the photo  it worked */

            iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(GuestAddCarFragment.this);
                    builder.setTitle("Delete image");
                    builder.setCancelable(false);
                    builder.setMessage("Do you want to delete this image");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            list.remove(selectedImagePath);
                            //iamgeLyt.removeView(imageView);
                            imageAdapter.notifyDataSetChanged();
                        }
                    });

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            builder.setCancelable(true);
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });


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
        if (requestCode == GuestAddCarFragment.REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {
            Log.e("Activity", "Result OK");

            onCaptureImageResult(data, this);
            showViewPager();

        } else if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");
            imageView1.setImageBitmap(photo);
            // personPhoto = photo;
            SaveImage(photo);
            Log.d("Activity", "CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE ");
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

