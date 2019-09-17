package com.oyespace.guards.activity;

//For registering finger print. Wired
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.oyespace.guards.BackgroundSyncReceiver;
import com.oyespace.guards.DataBaseHelper;
import com.oyespace.guards.R;

import com.oyespace.guards.constants.PrefKeys;
import com.oyespace.guards.network.*;
import com.oyespace.guards.request.FingerPrintCreateReq;
import com.oyespace.guards.responce.FingerPrintCreateResp;
import com.oyespace.guards.utils.LocalDb;
import com.oyespace.guards.utils.Prefs;

import java.nio.ByteBuffer;
import java.util.Locale;

import SecuGen.FDxSDKPro.JSGFPLib;
import SecuGen.FDxSDKPro.SGAutoOnEventNotifier;
import SecuGen.FDxSDKPro.SGFDxConstant;
import SecuGen.FDxSDKPro.SGFDxDeviceName;
import SecuGen.FDxSDKPro.SGFDxErrorCode;
import SecuGen.FDxSDKPro.SGFDxSecurityLevel;
import SecuGen.FDxSDKPro.SGFDxTemplateFormat;
import SecuGen.FDxSDKPro.SGFingerInfo;
import SecuGen.FDxSDKPro.SGFingerPresentEvent;

import static com.oyespace.guards.utils.ConstantUtils.*;
import static com.oyespace.guards.utils.Utils.showToast;


public class RegisterFingerPrint extends AppCompatActivity implements ResponseHandler, View.OnClickListener, Runnable, SGFingerPresentEvent {

    private static final String TAG = "SecuGen USB" ;
    String finger_type="", MemberType="Regular";
    String key_left_thumb="left_thumb";
    String key_left_index="left_index";
    String key_left_centre="left_centre";
    String key_left_ring="left_ring";
    String key_left_small="left_little";
    TextView txt_assn_name,txt_device_name,txt_gate_name ;

    String key_right_thumb="right_thumb";
    String key_right_index="right_index";
    String key_right_centre="right_centre";
    String key_right_ring="right_ring";
    String key_right_small="right_little";

    private Button mButtonRegister,mButtonRegister1,mButtonRegister2,mButtonRegister3, save,next,change,previous,buttonDone;
    private android.widget.TextView mTextViewResult;
    private android.widget.ToggleButton mToggleButtonAutoOn;
    private PendingIntent mPermissionIntent;
    private ImageView mImageFingerprint1,mImageFingerprint2,mImageFingerprint3;
    private RelativeLayout relLayout1,relLayout2,relLayout3;

    private boolean copy1=false,copy2=false,copy3=false ;
    private boolean mLed, mAutoOnEnabled, bSecuGenDeviceOpened, usbPermissionRequested;

    private byte[] mFingerprint1Template,mFingerprint2Template,mFingerprint3Template;
    private byte[] mRegisterImage=null, mRegisterTemplate=null;
    private int[] mMaxTemplateSize;
    private int mImageWidth, mImageHeight;
    private int mImageDPI;
    private int[] grayBuffer;
    private Bitmap grayBitmap;
    private IntentFilter filter; //2014-04-11
    private SGAutoOnEventNotifier autoOn;
    private int nCaptureModeN, fingerId=0, memId=0;
    DataBaseHelper dbh;

    TextToSpeech t1;
    private JSGFPLib sgfplib;

    boolean[] existInDB = new boolean[1];
    byte[] tempFP;

    Cursor curData;

    TextView fingerDetails;//080 42074082
    ImageView left_thumb,left_index,left_middle,left_ring,left_small,right_thumb,right_index,right_middle,right_ring,right_small;
    Button btn_left_thumb,btn_left_index,btn_left_middle,btn_left_ring,btn_left_small,btn_right_thumb,btn_right_index,btn_right_middle,btn_right_ring,btn_right_small;

    boolean bl_left_thumb=false,bl_left_index=false,bl_left_middle=false,bl_left_ring=false,bl_left_little=false,
            bl_right_thumb=false,bl_right_index=false,bl_right_middle=false,bl_right_ring=false,bl_right_little=false;

    //This broadcast receiver is necessary to get user permissions to access the attached USB device
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //Log.d(TAG,"Enter mUsbReceiver.onReceive()");
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if(device != null){
                        }
                        else
                            Log.e(TAG, "mUsbReceiver.onReceive() Device is null");
                    }
                    else
                        Log.e(TAG, "mUsbReceiver.onReceive() permission denied for device " + device);
                }
            }

            if(UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)){
                // Log.d("p22g","connectUsb");
                onResume();
                sgfplib = new JSGFPLib((UsbManager)getSystemService(Context.USB_SERVICE));
                bSecuGenDeviceOpened = false;
                usbPermissionRequested = false;

//        // debugMessage("Starting Activity\n");
                // debugMessage("jnisgfplib version: " + Integer.toHexString((int)sgfplib.Version()) + "\n");
                mLed = false;
                mAutoOnEnabled = false;
                // autoOn = new SGAutoOnEventNotifier (sgfplib, this);
                nCaptureModeN = 0;
            }else if(UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                DisableControls();
            }

            Toast.makeText(getApplicationContext(),
                    "Inside "+action, Toast.LENGTH_SHORT).show();
        }


    };

    public Handler fingerDetectedHandler = new Handler(){
        // @Override
        public void handleMessage(Message msg) {
            //Handle the message
            CaptureFingerPrint();
            if (mAutoOnEnabled) {
                mToggleButtonAutoOn.toggle();
                EnableControls();
            }
        }
    };

    public void EnableControls(){
        this.mButtonRegister.setClickable(true);
        this.mButtonRegister.setTextColor(getResources().getColor(android.R.color.white));
        this.mButtonRegister1.setClickable(true);
        this.mButtonRegister1.setTextColor(getResources().getColor(android.R.color.white));
        this.mButtonRegister2.setClickable(true);
        this.mButtonRegister2.setTextColor(getResources().getColor(android.R.color.white));
        this.mButtonRegister3.setClickable(true);
        this.mButtonRegister3.setTextColor(getResources().getColor(android.R.color.white));
    }

    public void DisableControls(){
        this.mButtonRegister.setVisibility(View.INVISIBLE);
        this.mButtonRegister.setTextColor(getResources().getColor(android.R.color.black));
        this.mButtonRegister1.setVisibility(View.INVISIBLE);
        this.mButtonRegister1.setTextColor(getResources().getColor(android.R.color.black));
        this.mButtonRegister2.setVisibility(View.INVISIBLE);
        this.mButtonRegister2.setTextColor(getResources().getColor(android.R.color.black));
        this.mButtonRegister3.setVisibility(View.INVISIBLE);
        this.mButtonRegister3.setTextColor(getResources().getColor(android.R.color.black));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("btn_biometric","bf setContentView");
        setLocale(Prefs.getString(PrefKeys.LANGUAGE, null));

        setContentView(R.layout.activity_register_finger_print);
        Log.d("btn_biometric","af setContentView");
        txt_assn_name=findViewById(R.id.txt_assn_name);
        txt_device_name=findViewById(R.id.txt_device_name);
        txt_device_name=findViewById(R.id.txt_device_name);


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
        dbh=new DataBaseHelper(getApplicationContext());
//        prefManager=new PrefManager(getApplicationContext());

//        getSupportActionBar().setTitle("Finger Print Registration");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        left_thumb= findViewById(R.id.left_thumb);
        left_index= findViewById(R.id.left_index);
        left_middle= findViewById(R.id.left_centre);
        left_ring= findViewById(R.id.left_ring);
        left_small= findViewById(R.id.left_small);

        right_thumb= findViewById(R.id.right_thumb);
        right_index= findViewById(R.id.right_index);
        right_middle= findViewById(R.id.right_centre);
        right_ring= findViewById(R.id.right_ring);
        right_small= findViewById(R.id.right_small);


        btn_left_thumb= findViewById(R.id.btn_left_thumb);
        btn_left_index= findViewById(R.id.btn_left_index);
        btn_left_middle= findViewById(R.id.btn_left_middle);
        btn_left_ring= findViewById(R.id.btn_left_ring);
        btn_left_small= findViewById(R.id.btn_left_small);

        btn_right_thumb= findViewById(R.id.btn_right_thumb);
        btn_right_index= findViewById(R.id.btn_right_index);
        btn_right_middle= findViewById(R.id.btn_right_centre);
        btn_right_ring= findViewById(R.id.btn_right_ring);
        btn_right_small= findViewById(R.id.btn_right_little);

        save=findViewById(R.id.buttonSaveToDB);
        save.setVisibility(View.INVISIBLE);
        next=findViewById(R.id.buttonNext);
        previous=findViewById(R.id.buttonPrevious);
        buttonDone=findViewById(R.id.buttonDone);

        curData=dbh.getRegularVisitorsFinger();
        if(curData!=null){
            curData.moveToFirst();
        }
        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status!=TextToSpeech.ERROR)
                    t1.setLanguage(Locale.ENGLISH);
            }
        });

        memId=getIntent().getIntExtra(WORKER_ID,0);
        Intent ddc  = new Intent(RegisterFingerPrint.this,BackgroundSyncReceiver.class);
        Log.d("btn_biometric","memId "+memId);

        ddc.putExtra(BSR_Action, SYNC_STAFF_BIOMETRIC);
        ddc.putExtra("ID",memId );
        sendBroadcast(ddc);

        fingerDetails= findViewById(R.id.txt_member);
       // MemberType=getIntent().getStringExtra("memberType");

        if(getIntent().getIntExtra("memId",0)!=0) {
            fingerDetails.setText("Register "+getIntent().getStringExtra(PERSONNAME));
           // +getIntent().getStringExtra("finger"));

        }else{
//            finish();
//            ((TextView) findViewById(R.id.txt_member)).setText("memId");
        }

//        showToast(this,  "create fmid " + memId);

        mButtonRegister = findViewById(R.id.buttonRegister);
        mButtonRegister1 = findViewById(R.id.buttonRegister1);
        mButtonRegister2 = findViewById(R.id.buttonRegister2);
        mButtonRegister3 = findViewById(R.id.buttonRegister3);

        if(dbh.fingercount(memId)==10){
//
        next.setVisibility(View.INVISIBLE);
            mButtonRegister.setVisibility(View.INVISIBLE);
            mButtonRegister1.setVisibility(View.INVISIBLE);
            mButtonRegister2.setVisibility(View.INVISIBLE);
            mButtonRegister3.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "All finger are registration", Toast.LENGTH_SHORT).show();
            highlightFingers();
        }else{
            selectedFinger();
        }

//        selectedFinger();

        mButtonRegister.setOnClickListener(this);
        mButtonRegister1.setOnClickListener(this);
        mButtonRegister2.setOnClickListener(this);
        mButtonRegister3.setOnClickListener(this);
        mTextViewResult = findViewById(R.id.textViewResult);

        mToggleButtonAutoOn = findViewById(R.id.toggleButtonAutoOn);
        mToggleButtonAutoOn.setOnClickListener(this);

        mImageFingerprint1 = findViewById(R.id.imageFingerprint1);
        mImageFingerprint2 = findViewById(R.id.imageFingerprint2);
        mImageFingerprint3 = findViewById(R.id.imageFingerprint3);

        relLayout1 = findViewById(R.id.layout_fp1_done);
        relLayout2 = findViewById(R.id.layout_fp2_done);
        relLayout3 = findViewById(R.id.layout_fp3_done);

        grayBuffer = new int[JSGFPLib.MAX_IMAGE_WIDTH_ALL_DEVICES*JSGFPLib.MAX_IMAGE_HEIGHT_ALL_DEVICES];
        for (int i=0; i<grayBuffer.length; ++i)
            grayBuffer[i] = Color.GRAY;//getResources().getColor(R.color.google_light);
        grayBitmap = Bitmap.createBitmap(JSGFPLib.MAX_IMAGE_WIDTH_ALL_DEVICES, JSGFPLib.MAX_IMAGE_HEIGHT_ALL_DEVICES, Bitmap.Config.ARGB_8888);
        grayBitmap.setPixels(grayBuffer, 0, JSGFPLib.MAX_IMAGE_WIDTH_ALL_DEVICES, 0, 0, JSGFPLib.MAX_IMAGE_WIDTH_ALL_DEVICES, JSGFPLib.MAX_IMAGE_HEIGHT_ALL_DEVICES);

        mImageFingerprint1.setImageBitmap(grayBitmap);
        mImageFingerprint2.setImageBitmap(grayBitmap);
        mImageFingerprint3.setImageBitmap(grayBitmap);

        int[] sintbuffer = new int[(JSGFPLib.MAX_IMAGE_WIDTH_ALL_DEVICES/2)*(JSGFPLib.MAX_IMAGE_HEIGHT_ALL_DEVICES/2)];
        for (int i=0; i<sintbuffer.length; ++i)
            sintbuffer[i] = Color.GRAY;
        Bitmap sb = Bitmap.createBitmap(JSGFPLib.MAX_IMAGE_WIDTH_ALL_DEVICES/2, JSGFPLib.MAX_IMAGE_HEIGHT_ALL_DEVICES/2, Bitmap.Config.ARGB_8888);
        sb.setPixels(sintbuffer, 0, JSGFPLib.MAX_IMAGE_WIDTH_ALL_DEVICES/2, 0, 0, JSGFPLib.MAX_IMAGE_WIDTH_ALL_DEVICES/2, JSGFPLib.MAX_IMAGE_HEIGHT_ALL_DEVICES/2);

        mMaxTemplateSize = new int[1];

        //USB Permissions
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        filter.addAction(UsbManager.EXTRA_PERMISSION_GRANTED);
        filter.addAction(ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver, filter);

//       	registerReceiver(mUsbReceiver, filter);
        sgfplib = new JSGFPLib((UsbManager)getSystemService(Context.USB_SERVICE));
        bSecuGenDeviceOpened = false;
        usbPermissionRequested = false;

//        // debugMessage("Starting Activity\n");
        // debugMessage("jnisgfplib version: " + Integer.toHexString((int)sgfplib.Version()) + "\n");
        mLed = false;
        mAutoOnEnabled = false;
        autoOn = new SGAutoOnEventNotifier (sgfplib, this);
        nCaptureModeN = 0;

    }

    @Override
    public void onBackPressed() {
/*
        if(dbh.fingercount(memId)>=4) {
*/
            super.onBackPressed();
        /*}
        else {
            if (bSecuGenDeviceOpened == true) {
                new android.support.v7.app.AlertDialog.Builder(RegisterFingerPrint.this)
                        .setTitle("Finger Print Registration")
                        .setMessage("Please register four finger")
                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })

                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                //open activity
                            }
                        }).create().show();
                Toast.makeText(this, "Please register four finger", Toast.LENGTH_SHORT).show();
            }
            else{
                new android.support.v7.app.AlertDialog.Builder(RegisterFingerPrint.this)
                        .setTitle("Finger Print Registration")
                        .setMessage("Please connect biometric device 1")
                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })

                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                //open activity
                            }
                        }).create().show();
                Toast.makeText(this, "Please connect biometric device3", Toast.LENGTH_SHORT).show();
            }

        }*/

    }


    @Override
    public void onPause() {
        //Log.d(TAG, "onPause()");
        if (bSecuGenDeviceOpened)
        {
            autoOn.stop();
            EnableControls();
            sgfplib.CloseDevice();
            bSecuGenDeviceOpened = false;
        }
        unregisterReceiver(mUsbReceiver);
        mRegisterImage = null;
        mRegisterTemplate = null;
        mFingerprint1Template = null;mFingerprint2Template = null;mFingerprint3Template = null;
//        mImageViewFingerprint.setImageBitmap(grayBitmap);
        mImageFingerprint1.setImageBitmap(grayBitmap);
        mImageFingerprint2.setImageBitmap(grayBitmap);
        mImageFingerprint3.setImageBitmap(grayBitmap);
//        mImageViewRegister.setImageBitmap(grayBitmap);

        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();  return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume(){
        //Log.d(TAG, "onResume()");
        super.onResume();
        DisableControls();
        registerReceiver(mUsbReceiver, filter);
        try {
            long error = sgfplib.Init( SGFDxDeviceName.SG_DEV_AUTO);
            if (error != SGFDxErrorCode.SGFDX_ERROR_NONE){
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
                if (error == SGFDxErrorCode.SGFDX_ERROR_DEVICE_NOT_FOUND)
                    dlgAlert.setMessage("The attached fingerprint device is not supported on Android");
                else
                    dlgAlert.setMessage("Fingerprint device initialization failed!");
                dlgAlert.setTitle("SecuGen Fingerprint SDK");
                dlgAlert.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int whichButton){
//                            finish();
                                return;
                            }
                        }
                );
                dlgAlert.setCancelable(false);
//            dlgAlert.create().show();
                new androidx.appcompat.app.AlertDialog.Builder(RegisterFingerPrint.this)
                        .setTitle("Finger Print Registration")
                        .setMessage("Please connect biometric device2")
                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })

                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                //open activity
                            }
                        }).create().show();

            }
            else {
                UsbDevice usbDevice = sgfplib.GetUsbDevice();
                if (usbDevice == null){
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
                    dlgAlert.setMessage("SecuGen fingerprint sensor not found!");
                    dlgAlert.setTitle("SecuGen Fingerprint SDK");
                    dlgAlert.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int whichButton){
                                    finish();
                                    return;
                                }
                            }
                    );
                    dlgAlert.setCancelable(false);
                    dlgAlert.create().show();
                }
                else {
                    boolean hasPermission = sgfplib.GetUsbManager().hasPermission(usbDevice);
                    if (!hasPermission) {
                        if (!usbPermissionRequested)
                        {
                            // debugMessage("Requesting USB Permission\n");
                            //Log.d(TAG, "Call GetUsbManager().requestPermission()");
                            usbPermissionRequested = true;
                            sgfplib.GetUsbManager().requestPermission(usbDevice, mPermissionIntent);
                        }
                        else
                        {
                            //wait up to 20 seconds for the system to grant USB permission
                            hasPermission = sgfplib.GetUsbManager().hasPermission(usbDevice);
                            // debugMessage("Waiting for USB Permission\n");
                            int i=0;
                            while ((hasPermission == false) && (i <= 40))
                            {
                                ++i;
                                hasPermission = sgfplib.GetUsbManager().hasPermission(usbDevice);
                                try {
                                    Thread.sleep(50);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                //Log.d(TAG, "Waited " + i*50 + " milliseconds for USB permission");
                            }
                        }
                    }
                    if (hasPermission) {
                        // debugMessage("Opening SecuGen Device\n");
                        error = sgfplib.OpenDevice(0);
                        // debugMessage("OpenDevice() ret: " + error + "\n");
                        if (error == SGFDxErrorCode.SGFDX_ERROR_NONE)
                        {
                            bSecuGenDeviceOpened = true;
                            SecuGen.FDxSDKPro.SGDeviceInfoParam deviceInfo = new SecuGen.FDxSDKPro.SGDeviceInfoParam();
                            error = sgfplib.GetDeviceInfo(deviceInfo);
                            // debugMessage("GetDeviceInfo() ret: " + error + "\n");
                            mImageWidth = deviceInfo.imageWidth;
                            mImageHeight= deviceInfo.imageHeight;
                            mImageDPI = deviceInfo.imageDPI;
                            // debugMessage("Image width: " + mImageWidth + "\n");
                            // debugMessage("Image height: " + mImageHeight + "\n");
                            // debugMessage("Image resolution: " + mImageDPI + "\n");
                            // debugMessage("Serial Number: " + new String(deviceInfo.deviceSN()) + "\n");
                            sgfplib.SetTemplateFormat(SGFDxTemplateFormat.TEMPLATE_FORMAT_SG400);
                            sgfplib.GetMaxTemplateSize(mMaxTemplateSize);
                            // debugMessage("TEMPLATE_FORMAT_SG400 SIZE: " + mMaxTemplateSize[0] + "\n");
                            mRegisterTemplate = new byte[mMaxTemplateSize[0]];
                            mFingerprint1Template = new byte[mMaxTemplateSize[0]];
                            mFingerprint2Template = new byte[mMaxTemplateSize[0]];
                            mFingerprint3Template = new byte[mMaxTemplateSize[0]];

                            EnableControls();
                            sgfplib.WriteData(SGFDxConstant.WRITEDATA_COMMAND_ENABLE_SMART_CAPTURE, (byte)1);
                            if (mAutoOnEnabled){
                                autoOn.start();
                                DisableControls();
                            }
                        }
                        else
                        {
                            // debugMessage("Waiting for USB Permission\n");
                        }
                    }
                    //Thread thread = new Thread(this);
                    //thread.start();
                 //   mButtonRegister.setVisibility(View.VISIBLE);
                    mButtonRegister1.setVisibility(View.VISIBLE);
                    mButtonRegister2.setVisibility(View.VISIBLE);
                    mButtonRegister3.setVisibility(View.VISIBLE);
                }
            }
        }catch (Exception ex){
            Toast.makeText(getApplicationContext(),"Connect Secugen Correctly",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        //Log.d(TAG, "onDestroy()");

        if (bSecuGenDeviceOpened)
        {
            autoOn.stop();
//            EnableControls();
            sgfplib.CloseDevice();
            bSecuGenDeviceOpened = false;
        }
        // sgfplib.CloseDevice();
        mRegisterImage = null;
        mRegisterTemplate = null;
        mFingerprint1Template = null;
        mFingerprint2Template = null;
        mFingerprint3Template = null;
        // sgfplib.Close();
//    	unregisterReceiver(mUsbReceiver);
        super.onDestroy();
    }

    //Converts image to grayscale (NEW)
    public Bitmap toGrayscale(byte[] mImageBuffer)
    {
        byte[] Bits = new byte[mImageBuffer.length * 4];
        for (int i = 0; i < mImageBuffer.length; i++) {
            Bits[i * 4] = Bits[i * 4 + 1] = Bits[i * 4 + 2] = mImageBuffer[i]; // Invert the source bits
            Bits[i * 4] = Bits[i * 4 + 1]  = mImageBuffer[i]; Bits[i * 4 + 2] = -1; // Invert the source bits //trial 4
//            Bits[i * 4+1] = Bits[i * 4 + 2]  = mImageBuffer[i]; Bits[i * 4 ] = -1; // Invert the source bits //trial 2
//            Bits[i * 4] = Bits[i * 4 + 2]  = mImageBuffer[i]; Bits[i * 4 + 1] = -1; // Invert the source bits //trial 3
            Bits[i * 4 + 3] = -1;// 0xff, that's the alpha.

        }

        Bitmap bmpGrayscale = Bitmap.createBitmap(mImageWidth, mImageHeight, Bitmap.Config.ARGB_4444);
        //Bitmap bm contains the fingerprint img
        bmpGrayscale.copyPixelsFromBuffer(ByteBuffer.wrap(Bits));
        return bmpGrayscale;
    }

    public void SGFingerPresentCallback (){
        autoOn.stop();
        fingerDetectedHandler.sendMessage(new Message());
    }

    public void CaptureFingerPrint(){
//        this.mCheckBoxMatched.setChecked(false);
        byte[] buffer = new byte[mImageWidth*mImageHeight];
        //long result = sgfplib.GetImage(buffer);
        long result = sgfplib.GetImageEx(buffer, 50,50);
//        if (this.mToggleButtonNFIQ.isChecked()) {
//            long nfiq = sgfplib.ComputeNFIQ(buffer, mImageWidth, mImageHeight);

//        mImageViewFingerprint.setImageBitmap(this.toGrayscale(buffer));

        buffer = null;

    }

    public void onClick(View v) {

        if (v == this.mButtonRegister) {
            if (mRegisterImage != null)
                mRegisterImage = null;
            mRegisterImage = new byte[mImageWidth*mImageHeight];
         //   previous=findViewById(R.id.buttonPrevious);
            previous.setVisibility(View.INVISIBLE);
            next.setVisibility(View.INVISIBLE);
            save.setVisibility(View.VISIBLE);

            long result = sgfplib.GetImage(mRegisterImage);
            Log.d("size  1",result+" "+mRegisterImage.length);

//            result = sgfplib.SetTemplateFormat(SecuGen.FDxSDKPro.SGFDxTemplateFormat.TEMPLATE_FORMAT_SG400);
            Log.d("size  2",result+" "+mRegisterImage.length);

            SGFingerInfo fpInfo = new SGFingerInfo();
            for (int i=0; i< mRegisterTemplate.length; ++i)
                mRegisterTemplate[i] = 0;
//            result = sgfplib.CreateTemplate(fpInfo, mRegisterImage, mRegisterTemplate);

            if(copy1==false){

                mImageFingerprint1.setImageBitmap(this.toGrayscale(mRegisterImage));

                for (int i=0; i< mFingerprint1Template.length; ++i)
                    mFingerprint1Template[i] = 0;
                result = sgfplib.CreateTemplate(fpInfo, mRegisterImage, mFingerprint1Template);

                relLayout1.setVisibility(View.VISIBLE);
                change=findViewById(R.id.btn_delete_fp1);
                change.setVisibility(View.VISIBLE);
                change=findViewById(R.id.btn_delete_fp2);
                change.setVisibility(View.INVISIBLE);
                change=findViewById(R.id.btn_delete_fp3);
                change.setVisibility(View.INVISIBLE);
                t1.speak("Thank You", TextToSpeech.QUEUE_FLUSH,null);

                copy1=true;
            }else if(copy2==false){

                for (int i=0; i< mFingerprint2Template.length; ++i)
                    mFingerprint2Template[i] = 0;
                result = sgfplib.CreateTemplate(fpInfo, mRegisterImage, mFingerprint2Template);

                boolean[] existInDB1 = new boolean[1];
                existInDB1 = new boolean[1];
                long res;
                res=sgfplib.MatchTemplate(mFingerprint1Template, mFingerprint2Template, SGFDxSecurityLevel.SL_NORMAL, existInDB1);
                if (existInDB1[0]) {
                    mTextViewResult.setText("MATCHED!!\n");//+curData.getString(1)+" "+curData.getString(2));
//                    this.mCheckBoxMatched.setChecked(true);
                    mImageFingerprint2.setImageBitmap(this.toGrayscale(mRegisterImage));
//                    Bitmap waterMarkedPhoto1 = BitmapFactory.decodeByteArray(mFingerprint2Template, 0, mFingerprint2Template.length);
//                    mImageFingerprint1.setImageBitmap(waterMarkedPhoto1);

                    relLayout2.setVisibility(View.VISIBLE);
                    t1.speak("Thank You", TextToSpeech.QUEUE_FLUSH,null);
                    change=findViewById(R.id.btn_delete_fp2);
                    change.setVisibility(View.VISIBLE);
                    change=findViewById(R.id.btn_delete_fp1);
                    change.setVisibility(View.INVISIBLE);
                    change=findViewById(R.id.btn_delete_fp3);
                    change.setVisibility(View.INVISIBLE);
                    copy2=true;
                }else{
                    t1.speak(" Not match", TextToSpeech.QUEUE_FLUSH,null);
                }

            }else if(copy3==false){

                for (int i=0; i< mFingerprint3Template.length; ++i)
                    mFingerprint3Template[i] = 0;
                result = sgfplib.CreateTemplate(fpInfo, mRegisterImage, mFingerprint3Template);

                boolean[] existInDB1 = new boolean[1];
                long res;
                res=sgfplib.MatchTemplate(mFingerprint1Template, mFingerprint3Template, SGFDxSecurityLevel.SL_NORMAL, existInDB1);
                if (existInDB1[0]) {

                    mTextViewResult.setText("MATCHED!!\n");//+curData.getString(1)+" "+curData.getString(2));
//                    this.mCheckBoxMatched.setChecked(true);
                    mImageFingerprint3.setImageBitmap(this.toGrayscale(mRegisterImage));
                    relLayout3.setVisibility(View.VISIBLE);
                    t1.speak("Thank You", TextToSpeech.QUEUE_FLUSH,null);
                    change=findViewById(R.id.btn_delete_fp3);
                    change.setVisibility(View.VISIBLE);
                    change=findViewById(R.id.btn_delete_fp1);
                    change.setVisibility(View.INVISIBLE);
                    change=findViewById(R.id.btn_delete_fp2);
                    change.setVisibility(View.INVISIBLE);
                    //relLayout3.setVisibility(View.INVISIBLE);
                    copy3=true;
                }else{
                    t1.speak(" Not match", TextToSpeech.QUEUE_FLUSH,null);
                }

            }

            Log.d("size  3",result+" "+mRegisterImage.length);
            Log.d("size  4",result+" "+mRegisterTemplate.length);

            mRegisterImage = null;
            fpInfo = null;

        }
        if (v == this.mButtonRegister1) {
            if (mRegisterImage != null)
                mRegisterImage = null;
            mRegisterImage = new byte[mImageWidth*mImageHeight];
           previous=findViewById(R.id.buttonPrevious);
            previous.setVisibility(View.INVISIBLE);
            next.setVisibility(View.INVISIBLE);
            save.setVisibility(View.VISIBLE);

            long result = sgfplib.GetImage(mRegisterImage);
            Log.d("size  1",result+" "+mRegisterImage.length);

//            result = sgfplib.SetTemplateFormat(SecuGen.FDxSDKPro.SGFDxTemplateFormat.TEMPLATE_FORMAT_SG400);
            Log.d("size  2",result+" "+mRegisterImage.length);

            SGFingerInfo fpInfo = new SGFingerInfo();
            for (int i=0; i< mRegisterTemplate.length; ++i)
                mRegisterTemplate[i] = 0;
//            result = sgfplib.CreateTemplate(fpInfo, mRegisterImage, mRegisterTemplate);

            if(copy1==false){

                mImageFingerprint1.setImageBitmap(this.toGrayscale(mRegisterImage));

                for (int i=0; i< mFingerprint1Template.length; ++i)
                    mFingerprint1Template[i] = 0;
                result = sgfplib.CreateTemplate(fpInfo, mRegisterImage, mFingerprint1Template);

                relLayout1.setVisibility(View.VISIBLE);
                change=findViewById(R.id.btn_delete_fp1);
                change.setVisibility(View.VISIBLE);
                change=findViewById(R.id.btn_delete_fp2);
                change.setVisibility(View.INVISIBLE);
                change=findViewById(R.id.btn_delete_fp3);
                change.setVisibility(View.INVISIBLE);
                mButtonRegister1.setVisibility(View.INVISIBLE);
                t1.speak("Thank You", TextToSpeech.QUEUE_FLUSH,null);

                copy1=true;
            }

            Log.d("size  3",result+" "+mRegisterImage.length);
            Log.d("size  4",result+" "+mRegisterTemplate.length);

            mRegisterImage = null;
            fpInfo = null;

        }
        if (v == this.mButtonRegister2) {
            if (mRegisterImage != null)
                mRegisterImage = null;
            mRegisterImage = new byte[mImageWidth*mImageHeight];
            previous=findViewById(R.id.buttonPrevious);
            previous.setVisibility(View.INVISIBLE);
            next.setVisibility(View.INVISIBLE);
            save.setVisibility(View.VISIBLE);

            long result = sgfplib.GetImage(mRegisterImage);
            Log.d("size  1",result+" "+mRegisterImage.length);

//            result = sgfplib.SetTemplateFormat(SecuGen.FDxSDKPro.SGFDxTemplateFormat.TEMPLATE_FORMAT_SG400);
            Log.d("size  2",result+" "+mRegisterImage.length);

            SGFingerInfo fpInfo = new SGFingerInfo();
            for (int i=0; i< mRegisterTemplate.length; ++i)
                mRegisterTemplate[i] = 0;
//            result = sgfplib.CreateTemplate(fpInfo, mRegisterImage, mRegisterTemplate);

            if(copy2==false){

                for (int i=0; i< mFingerprint2Template.length; ++i)
                    mFingerprint2Template[i] = 0;
                result = sgfplib.CreateTemplate(fpInfo, mRegisterImage, mFingerprint2Template);

                boolean[] existInDB1 = new boolean[1];
                existInDB1 = new boolean[1];
                long res;
                res=sgfplib.MatchTemplate(mFingerprint1Template, mFingerprint2Template, SGFDxSecurityLevel.SL_NORMAL, existInDB1);
                if (existInDB1[0]) {
                    mTextViewResult.setText("MATCHED!!\n");//+curData.getString(1)+" "+curData.getString(2));
//                    this.mCheckBoxMatched.setChecked(true);
                    mImageFingerprint2.setImageBitmap(this.toGrayscale(mRegisterImage));
//                    Bitmap waterMarkedPhoto1 = BitmapFactory.decodeByteArray(mFingerprint2Template, 0, mFingerprint2Template.length);
//                    mImageFingerprint1.setImageBitmap(waterMarkedPhoto1);

                    relLayout2.setVisibility(View.VISIBLE);
                    t1.speak("Thank You", TextToSpeech.QUEUE_FLUSH,null);
                    change=findViewById(R.id.btn_delete_fp2);
                    change.setVisibility(View.VISIBLE);
                    change=findViewById(R.id.btn_delete_fp1);
                    change.setVisibility(View.INVISIBLE);
                    change=findViewById(R.id.btn_delete_fp3);
                    change.setVisibility(View.INVISIBLE);
                    mButtonRegister2.setVisibility(View.INVISIBLE);
                    copy2=true;
                }else{
                    t1.speak(" Not match", TextToSpeech.QUEUE_FLUSH,null);
                }

            }

            Log.d("size  3",result+" "+mRegisterImage.length);
            Log.d("size  4",result+" "+mRegisterTemplate.length);

            mRegisterImage = null;
            fpInfo = null;

        }
        if (v == this.mButtonRegister3) {
            if (mRegisterImage != null)
                mRegisterImage = null;
            mRegisterImage = new byte[mImageWidth*mImageHeight];
            previous=findViewById(R.id.buttonPrevious);
            previous.setVisibility(View.INVISIBLE);
            next.setVisibility(View.INVISIBLE);
            save.setVisibility(View.VISIBLE);

            long result = sgfplib.GetImage(mRegisterImage);
            Log.d("size  1",result+" "+mRegisterImage.length);

//            result = sgfplib.SetTemplateFormat(SecuGen.FDxSDKPro.SGFDxTemplateFormat.TEMPLATE_FORMAT_SG400);
            Log.d("size  2",result+" "+mRegisterImage.length);

            SGFingerInfo fpInfo = new SGFingerInfo();
            for (int i=0; i< mRegisterTemplate.length; ++i)
                mRegisterTemplate[i] = 0;

            if(copy3==false){

                for (int i=0; i< mFingerprint3Template.length; ++i)
                    mFingerprint3Template[i] = 0;
                result = sgfplib.CreateTemplate(fpInfo, mRegisterImage, mFingerprint3Template);

                boolean[] existInDB1 = new boolean[1];
                long res;
                res=sgfplib.MatchTemplate(mFingerprint1Template, mFingerprint3Template, SGFDxSecurityLevel.SL_NORMAL, existInDB1);
                if (existInDB1[0]) {

                    mTextViewResult.setText("MATCHED!!\n");//+curData.getString(1)+" "+curData.getString(2));
//                    this.mCheckBoxMatched.setChecked(true);
                    mImageFingerprint3.setImageBitmap(this.toGrayscale(mRegisterImage));
                    relLayout3.setVisibility(View.VISIBLE);
                    t1.speak("Thank You", TextToSpeech.QUEUE_FLUSH,null);
                    change=findViewById(R.id.btn_delete_fp3);
                    change.setVisibility(View.VISIBLE);
                    change=findViewById(R.id.btn_delete_fp1);
                    change.setVisibility(View.INVISIBLE);
                    change=findViewById(R.id.btn_delete_fp2);
                    change.setVisibility(View.INVISIBLE);
                    //relLayout3.setVisibility(View.INVISIBLE);
                    mButtonRegister3.setVisibility(View.INVISIBLE);
                    copy3=true;
                }else{
                    t1.speak(" Not match", TextToSpeech.QUEUE_FLUSH,null);
                }

            }

            Log.d("size  3",result+" "+mRegisterImage.length);
            Log.d("size  4",result+" "+mRegisterTemplate.length);

            mRegisterImage = null;
            fpInfo = null;

        }

        if(v.getId()==R.id.btn_delete_fp1){
            mImageFingerprint1.setImageBitmap(grayBitmap);
            for (int i=0; i< mFingerprint1Template.length; ++i)
                mFingerprint1Template[i] = 0;
            // next=findViewById(R.id.buttonNext);
            next.setVisibility(View.VISIBLE);
            previous.setVisibility(View.VISIBLE);
            save.setVisibility(View.INVISIBLE);

            relLayout1.setVisibility(View.INVISIBLE);
            mButtonRegister1.setVisibility(View.VISIBLE);

            copy1=false;
        }else if(v.getId()==R.id.btn_delete_fp2){
            mImageFingerprint2.setImageBitmap(grayBitmap);
            for (int i=0; i< mFingerprint2Template.length; ++i)
                mFingerprint2Template[i] = 0;

            relLayout2.setVisibility(View.INVISIBLE);
            copy2=false;
            change=findViewById(R.id.btn_delete_fp1);
            change.setVisibility(View.VISIBLE);
            mButtonRegister2.setVisibility(View.VISIBLE);

        }else if(v.getId()==R.id.btn_delete_fp3){
            mImageFingerprint3.setImageBitmap(grayBitmap);
            for (int i=0; i< mFingerprint3Template.length; ++i)
                mFingerprint3Template[i] = 0;
            relLayout3.setVisibility(View.INVISIBLE);
            copy3=false;
            change=findViewById(R.id.btn_delete_fp1);
            change.setVisibility(View.INVISIBLE);
            change=findViewById(R.id.btn_delete_fp2);
            change.setVisibility(View.VISIBLE);
            mButtonRegister3.setVisibility(View.VISIBLE);

        }

        if(v.getId()==R.id.buttonSaveToDB){
            if(copy1==false){
                Toast.makeText(getApplicationContext(),"Take 1st finger print",Toast.LENGTH_SHORT).show();
                t1.speak("take 1st finger print", TextToSpeech.QUEUE_FLUSH,null);
            }else if(copy2==false){
                Toast.makeText(getApplicationContext(),"Take 2nd finger print",Toast.LENGTH_SHORT).show();
                t1.speak("take 2nd finger print", TextToSpeech.QUEUE_FLUSH,null);
            }else if(copy3==false){
                Toast.makeText(getApplicationContext(),"Take 3rd finger print",Toast.LENGTH_SHORT).show();
                t1.speak("take 3rd finger print", TextToSpeech.QUEUE_FLUSH,null);

            }else{

                t1.speak("Finger print data saved", TextToSpeech.QUEUE_FLUSH,null);
                uploadFingerPrint(mFingerprint1Template,mFingerprint2Template,mFingerprint3Template);

                next=findViewById(R.id.buttonNext);
                next.setVisibility(View.VISIBLE);
                previous.setVisibility(View.VISIBLE);
                save.setVisibility(View.INVISIBLE);
                mButtonRegister1.setVisibility(View.VISIBLE);
                mButtonRegister2.setVisibility(View.VISIBLE);
                mButtonRegister3.setVisibility(View.VISIBLE);

            }
        }

        if(v.getId()==R.id.buttonDone){
            Intent d  =new  Intent(RegisterFingerPrint.this, StaffDetails.class);
            d.putExtra(WORKER_ID, getIntent().getIntExtra(WORKER_ID,0));
            d.putExtra(PERSONNAME, getIntent().getStringExtra(PERSONNAME));
            d.putExtra(UNITID, getIntent().getStringExtra(UNITID));
            d.putExtra(UNITNAME, getIntent().getStringExtra(UNITNAME));
            d.putExtra(FLOW_TYPE, getIntent().getStringExtra(FLOW_TYPE));
            d.putExtra(VISITOR_TYPE, getIntent().getStringExtra(VISITOR_TYPE));
            d.putExtra(COMPANY_NAME, getIntent().getStringExtra(COMPANY_NAME));
            d.putExtra(MOBILENUMBER, getIntent().getStringExtra(MOBILENUMBER));
            d.putExtra(COUNTRYCODE, getIntent().getStringExtra(COUNTRYCODE));
            startActivity(d);
            finish();
        }
        if(v.getId()==R.id.buttonNext){
            if(copy1==false && copy2==false && copy3==false){
                fingerId++;
                selectedFinger();

            }else{
                Toast.makeText(getApplicationContext(),"Complete the Current selected Registration",Toast.LENGTH_SHORT).show();
                t1.speak("Complete the Current selected Registration", TextToSpeech.QUEUE_FLUSH,null);
            }
        }

        if(v.getId()==R.id.buttonPrevious){
            if(copy1==false && copy2==false && copy3==false){
                fingerId--;
                selectedFinger();

            }else{
                Toast.makeText(getApplicationContext(),"Complete the Current selected Registration",Toast.LENGTH_SHORT).show();
                t1.speak("Complete the Current selected Registration", TextToSpeech.QUEUE_FLUSH,null);
            }
        }


    }


    private void resetCapures(){
        mImageFingerprint1.setImageBitmap(grayBitmap);
        for (int i=0; i< mFingerprint1Template.length; ++i)
            mFingerprint1Template[i] = 0;

        relLayout1.setVisibility(View.INVISIBLE);
        copy1=false;

        mImageFingerprint2.setImageBitmap(grayBitmap);
        for (int i=0; i< mFingerprint2Template.length; ++i)
            mFingerprint2Template[i] = 0;

        relLayout2.setVisibility(View.INVISIBLE);
        copy2=false;

        mImageFingerprint3.setImageBitmap(grayBitmap);
        for (int i=0; i< mFingerprint3Template.length; ++i)
            mFingerprint3Template[i] = 0;

        relLayout3.setVisibility(View.INVISIBLE);
        copy3=false;

    }
    @Override
    public void onFailure(Exception e, int urlId) {

        showToast(this, e.getMessage()+" id "+urlId);
    }

    @Override
    public void onSuccess(String responce, Object data, int urlId, int position) {

        if (urlId == URLData.URL_SAVE_FINGERPRINT.getUrlId()) {
            FingerPrintCreateResp loginDetailsResponce = (FingerPrintCreateResp) data;
            if (loginDetailsResponce != null) {
                Log.d("str3", "str3: " + urlId+" id "+position+" "+memId+" "+MemberType+" "+loginDetailsResponce.success.toString());
                if(loginDetailsResponce.success.equalsIgnoreCase("true")) {
                    showToast(this, "Fingerprint Saved");
                  //  dbh.insertUserDetails(memId + "", finger_type, mFingerprint1Template, mFingerprint2Template, mFingerprint3Template, MemberType);
                    selectedFinger();
                    resetCapures();
                }else{
                    showToast(this, "Fingerprint not saved ");
                }

            } else {
                showToast(this, "Something went wrong . please try again ");
            }

        }
      //  showToast(this, urlId+" id "+position+" "+memId+" "+MemberType+" ");

//        finish();
    }
    public void uploadFingerPrint(final byte[] byteArray_fp1,final byte[] byteArray_fp2,final byte[] byteArray_fp3){

        String str1= Base64.encodeToString(byteArray_fp1,
                Base64.DEFAULT);
        String str2= Base64.encodeToString(byteArray_fp2,
                Base64.DEFAULT);
        String str3= Base64.encodeToString(byteArray_fp3,
                Base64.DEFAULT);

        RestClient restClient = RestClient.getInstance();
        FingerPrintCreateReq loginReq = new FingerPrintCreateReq();
        loginReq.ASAssnID =  Prefs.getInt(ASSOCIATION_ID,0) + "";
        loginReq.FMID = memId+"";
        loginReq.FPImg1 =  str1 ;
        loginReq.FPFngName = finger_type;
        loginReq.FPImg2 =  str2 ;
        loginReq.FPImg3 = str3;
        loginReq.FPMemType = MemberType;
      //  showToast(this, loginReq.ASAssnID + " fmid " + loginReq.FMID+" "+loginReq.FPImg1);
        Log.d("str3", "str3: " + str3);


        // restClient.post(this, loginReq, LoginDetailsResponce.class, this, URLData.URL_LOGIN);
        restClient.addHeader(OYE247KEY, OYE247TOKEN);

        restClient.post(this, loginReq, FingerPrintCreateResp.class, this, URLData.URL_SAVE_FINGERPRINT);

       /* FingerPrintCreateReq req=new FingerPrintCreateReq( ASSOCIATIONID+"",memId+"",finger_type,
                str1,str2,str3,MemberType);

        ChampApiInterface apiService = ChampApiClient.getClient().create(ChampApiInterface.class);
        Call<FingerPrintCreateResp> call = apiService.createFingerPrintCall(req);

        call.enqueue(new Callback<FingerPrintCreateResp>() {
            @Override
            public void onResponse(Call<FingerPrintCreateResp> call, Response<FingerPrintCreateResp> response) {
                try {
                    Log.d("image1", "Number of movies received: " + response.body());

                    Toast.makeText(getApplicationContext(), "Fingerprint saved Successfully", Toast.LENGTH_SHORT).show();
                    dbh.insertUserDetails(memId + "", finger_type, mFingerprint1Template, mFingerprint2Template, mFingerprint3Template,MemberType);

                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), "Fingerprint not saved", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<FingerPrintCreateResp> call, Throwable t) {
                // Log error here since request failed
                Log.d("Dgddfdf", t.toString());
                Toast.makeText(getApplicationContext(), "Not Uploaded", Toast.LENGTH_SHORT).show();
            }
        });*/



    }

//    public void selectedFinger(){
//
//        highlightFingers();
//
//        right_thumb.setVisibility(View.INVISIBLE);
//        right_index.setVisibility(View.INVISIBLE);
//        right_middle.setVisibility(View.INVISIBLE);
//        right_ring.setVisibility(View.INVISIBLE);
//        right_small.setVisibility(View.INVISIBLE);
//
//        left_thumb.setVisibility(View.INVISIBLE);
//        left_index.setVisibility(View.INVISIBLE);
//        left_middle.setVisibility(View.INVISIBLE);
//        left_ring.setVisibility(View.INVISIBLE);
//        left_small.setVisibility(View.INVISIBLE);
//
//        if(fingerId%10==0){
//            right_thumb.setVisibility(View.VISIBLE);
//            finger_type= key_right_thumb;
//            if(bl_right_thumb) {
//                fingerId++;
//                selectedFinger();
//            }
//        }else if(fingerId%10==1){
//            right_index.setVisibility(View.VISIBLE);
//            finger_type= key_right_index;
//            if(bl_right_index) {
//                fingerId++;
//                selectedFinger();
//            }
//        }else if(fingerId%10==2){
//            right_middle.setVisibility(View.VISIBLE);
//            finger_type= key_right_centre;
//            if(bl_right_middle) {
//                fingerId++;
//                selectedFinger();
//            }
//        }else if(fingerId%10==3){
//            right_ring.setVisibility(View.VISIBLE);
//            finger_type= key_right_ring;
//            if(bl_right_ring) {
//                fingerId++;
//                selectedFinger();
//            }
//        }else if(fingerId%10==4){
//            right_small.setVisibility(View.VISIBLE);
//            finger_type= key_right_small;
//            if(bl_right_little) {
//                fingerId++;
//                selectedFinger();
//            }
//        }else if(fingerId%10==5){
//            left_thumb.setVisibility(View.VISIBLE);
//            finger_type= key_left_thumb;
//            if(bl_left_thumb) {
//                fingerId++;
//                selectedFinger();
//            }
//        }else if(fingerId%10==6){
//            left_index.setVisibility(View.VISIBLE);
//            finger_type= key_left_index;
//            if(bl_left_index) {
//                fingerId++;
//                selectedFinger();
//            }
//        }else if(fingerId%10==7){
//            left_middle.setVisibility(View.VISIBLE);
//            finger_type= key_left_centre;
//            if(bl_left_middle) {
//                fingerId++;
//                selectedFinger();
//            }
//        }else if(fingerId%10==8){
//            left_ring.setVisibility(View.VISIBLE);
//            finger_type= key_left_ring;
//            if(bl_left_ring) {
//                fingerId++;
//                selectedFinger();
//            }
//        }else if(fingerId%10==9){
//            left_small.setVisibility(View.VISIBLE);
//            finger_type= key_left_small;
//            if(bl_left_little) {
//                fingerId++;
//                selectedFinger();
//            }
//        }
//        fingerDetails.setText("Register "+dbh.getMemName(getIntent().getIntExtra("memId",0))+"'s "+finger_type.replace("_"," "));
//
//    }

    public void selectedFinger(){

        highlightFingers();

        right_thumb.setVisibility(View.INVISIBLE);
        right_index.setVisibility(View.INVISIBLE);
        right_middle.setVisibility(View.INVISIBLE);
        right_ring.setVisibility(View.INVISIBLE);
        right_small.setVisibility(View.INVISIBLE);

        left_thumb.setVisibility(View.INVISIBLE);
        left_index.setVisibility(View.INVISIBLE);
        left_middle.setVisibility(View.INVISIBLE);
        left_ring.setVisibility(View.INVISIBLE);
        left_small.setVisibility(View.INVISIBLE);

        if(fingerId%10==0){
            right_thumb.setVisibility(View.VISIBLE);
            finger_type= key_right_thumb;
            if(bl_right_thumb) {
                fingerId++;
                selectedFinger();
            }
        }else if(fingerId%10==1||fingerId%10==-1){
            right_index.setVisibility(View.VISIBLE);
            finger_type= key_right_index;
            if(bl_right_index) {
                fingerId++;
                selectedFinger();
            }
        }else if(fingerId%10==2||fingerId%10==-2){
            right_middle.setVisibility(View.VISIBLE);
            finger_type= key_right_centre;
            if(bl_right_middle) {
                fingerId++;
                selectedFinger();
            }
        }else if(fingerId%10==3||fingerId%10==-3){
            right_ring.setVisibility(View.VISIBLE);
            finger_type= key_right_ring;
            if(bl_right_ring) {
                fingerId++;
                selectedFinger();
            }
        }else if(fingerId%10==4||fingerId%10==-4){
            right_small.setVisibility(View.VISIBLE);
            finger_type= key_right_small;
            if(bl_right_little) {
                fingerId++;
                selectedFinger();
            }
        }else if(fingerId%10==5||fingerId%10==-5){
            left_thumb.setVisibility(View.VISIBLE);
            finger_type= key_left_thumb;
            if(bl_left_thumb) {
                fingerId++;
                selectedFinger();
            }
        }else if(fingerId%10==6||fingerId%10==-6){
            left_index.setVisibility(View.VISIBLE);
            finger_type= key_left_index;
            if(bl_left_index) {
                fingerId++;
                selectedFinger();
            }
        }else if(fingerId%10==7||fingerId%10==-7){
            left_middle.setVisibility(View.VISIBLE);
            finger_type= key_left_centre;
            if(bl_left_middle) {
                fingerId++;
                selectedFinger();
            }
        }else if(fingerId%10==8||fingerId%10==-8){
            left_ring.setVisibility(View.VISIBLE);
            finger_type= key_left_ring;
            if(bl_left_ring) {
                fingerId++;
                selectedFinger();
            }
        }else if(fingerId%10==9||fingerId%10==-9){
            left_small.setVisibility(View.VISIBLE);
            finger_type= key_left_small;
            if(bl_left_little) {
                fingerId++;
                selectedFinger();
            }
        }
        String stFinger="";
        if(!finger_type.contains("thumb")){
            stFinger=" finger";
        }
        fingerDetails.setText("Register "+getIntent().getStringExtra(PERSONNAME)+"'s "+finger_type.replace("_"," ")+stFinger);

    }


    int i=0;

    public void highlightFingers(){

        bl_left_thumb=dbh.getMemberFingerExists(memId+"","left_thumb");
        bl_left_index=dbh.getMemberFingerExists(memId+"","left_index");
        bl_left_middle=dbh.getMemberFingerExists(memId+"","left_centre");
        bl_left_ring=dbh.getMemberFingerExists(memId+"","left_ring");
        bl_left_little=dbh.getMemberFingerExists(memId+"","left_little");

        bl_right_thumb=dbh.getMemberFingerExists(memId+"","right_thumb");
        bl_right_index=dbh.getMemberFingerExists(memId+"","right_index");
        bl_right_middle=dbh.getMemberFingerExists(memId+"","right_centre");
        bl_right_ring=dbh.getMemberFingerExists(memId+"","right_ring");
        bl_right_little=dbh.getMemberFingerExists(memId+"","right_little");

        if(bl_left_thumb)
            btn_left_thumb.setAlpha((float) 0.3);
        if(bl_left_index)
            btn_left_index.setAlpha((float) 0.3);
        if(bl_left_middle)
            btn_left_middle.setAlpha((float) 0.3);
        if(bl_left_ring)
            btn_left_ring.setAlpha((float) 0.3);
        if(bl_left_little)
            btn_left_small.setAlpha((float) 0.3);

        if(bl_right_thumb)
            btn_right_thumb.setAlpha((float) 0.3);
        if(bl_right_index)
            btn_right_index.setAlpha((float) 0.3);
        if(bl_right_middle)
            btn_right_middle.setAlpha((float) 0.3);
        if(bl_right_ring)
            btn_right_ring.setAlpha((float) 0.3);
        if(bl_right_little)
            btn_right_small.setAlpha((float) 0.3);

       // Toast.makeText(getApplicationContext(),"Hi there"+dbh.fingercount(memId),Toast.LENGTH_SHORT).show();

    if(dbh.fingercount(memId)>3) {
        buttonDone.setVisibility(View.VISIBLE);
    }else{
        buttonDone.setVisibility(View.GONE);

    }
    }

    public void run() {
        i++;
        //Toast.makeText(getApplicationContext(),"Hi there"+i,Toast.LENGTH_SHORT).show();
    }

    public void clicked(View view) {
        right_thumb.setVisibility(View.INVISIBLE);
        right_index.setVisibility(View.INVISIBLE);
        right_middle.setVisibility(View.INVISIBLE);
        right_ring.setVisibility(View.INVISIBLE);
        right_small.setVisibility(View.INVISIBLE);

        left_thumb.setVisibility(View.INVISIBLE);
        left_index.setVisibility(View.INVISIBLE);
        left_middle.setVisibility(View.INVISIBLE);
        left_ring.setVisibility(View.INVISIBLE);
        left_small.setVisibility(View.INVISIBLE);

        if(copy1==false) {
            switch (view.getId()) {

                case R.id.btn_left_small: {

                    left_small.setVisibility(View.VISIBLE);
                    fingerId = 9;
                    finger_type = key_left_small;
                    if (bl_left_little) {
                        selectedFinger();
                    }
                    break;
                }
                case R.id.btn_left_ring: {

                    left_ring.setVisibility(View.VISIBLE);
                    fingerId = 8;
                    finger_type = key_left_ring;
                    if (bl_left_ring) {
                        selectedFinger();
                    }
                    break;
                }
                case R.id.btn_left_middle: {

                    left_middle.setVisibility(View.VISIBLE);
                    fingerId = 7;
                    finger_type = key_left_centre;
                    if (bl_left_middle) {
                        selectedFinger();
                    }
                    break;
                }
                case R.id.btn_left_index: {

                    left_index.setVisibility(View.VISIBLE);
                    fingerId = 6;
                    finger_type = key_left_index;
                    if (bl_left_index) {
                        selectedFinger();
                    }
                    break;
                }
                case R.id.btn_left_thumb: {

                    left_thumb.setVisibility(View.VISIBLE);
                    fingerId = 5;
                    finger_type = key_left_thumb;
                    if (bl_left_thumb) {
                        selectedFinger();
                    }
                    break;
                }
                case R.id.btn_right_thumb: {

                    right_thumb.setVisibility(View.VISIBLE);
                    fingerId = 0;
                    finger_type = key_right_thumb;
                    if (bl_right_thumb) {
                        selectedFinger();
                    }
                    break;
                }
                case R.id.btn_right_index: {

                    right_index.setVisibility(View.VISIBLE);
                    fingerId = 1;
                    finger_type = key_right_index;
                    if (bl_right_index) {
                        selectedFinger();
                    }
                    break;
                }
                case R.id.btn_right_centre: {

                    right_middle.setVisibility(View.VISIBLE);
                    fingerId = 2;
                    finger_type = key_right_centre;
                    if (bl_right_middle) {
                        selectedFinger();
                    }
                    break;
                }
                case R.id.btn_right_ring: {

                    right_ring.setVisibility(View.VISIBLE);
                    fingerId = 3;
                    finger_type = key_right_ring;
                    if (bl_right_ring) {
                        selectedFinger();
                    }
                    break;
                }
                case R.id.btn_right_little: {

                    right_small.setVisibility(View.VISIBLE);
                    fingerId = 4;
                    finger_type = key_right_small;
                    if (bl_right_little) {
                        selectedFinger();
                    }
                    break;
                }
            }
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

}
