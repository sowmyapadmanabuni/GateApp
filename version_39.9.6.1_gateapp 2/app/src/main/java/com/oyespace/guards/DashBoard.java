package com.oyespace.guards;

import SecuGen.FDxSDKPro.*;
import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.*;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.*;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import com.google.firebase.messaging.FirebaseMessaging;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.oyespace.guards.activity.*;
import com.oyespace.guards.adapter.VistorEntryListAdapter;
import com.oyespace.guards.adapter.VistorListAdapter;
import com.oyespace.guards.guest.GuestCustomViewFinderScannerActivity;
import com.oyespace.guards.network.*;
import com.oyespace.guards.ocr.*;
import com.oyespace.guards.pertroling.PatrollingActivitynew;
import com.oyespace.guards.pojo.VisitorEntryLog;
import com.oyespace.guards.pojo.WorkerDetails;
import com.oyespace.guards.request.VisitorEntryReqJv;
import com.oyespace.guards.request.VisitorExitReqJv;
import com.oyespace.guards.responce.*;
import com.oyespace.guards.utils.LocalDb;
import com.oyespace.guards.utils.Prefs;
import com.oyespace.guards.utils.RandomUtils;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.oyespace.guards.constants.PrefKeys.BG_NOTIFICATION_ON;
import static com.oyespace.guards.constants.PrefKeys.LANGUAGE;
import static com.oyespace.guards.constants.PrefKeys.PATROLLING_ID;
import static com.oyespace.guards.utils.ConstantUtils.*;
import static com.oyespace.guards.utils.DateTimeUtils.getCurrentTimeLocal;
import static com.oyespace.guards.utils.DateTimeUtils.getCurrentTimeLocalYMD;
import static com.oyespace.guards.utils.Utils.showToast;

public class DashBoard extends BaseActivity implements AdapterView.OnItemSelectedListener,View.OnClickListener,
        ResponseHandler, Runnable, SGFingerPresentEvent {
    ArrayList<VisitorEntryLog> newAl =new ArrayList<>();
    Handler mHandler;

    private File audiofile = null;
    VistorEntryListAdapter vistorEntryListAdapter;
    Spinner spinner;
    private String mFileName = "";
    private MediaRecorder myAudioRecorder;
    ImageView record;
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    ImageView iv_settings;
    TextView tv_nodata;
    // LinearLayout lyt_settings;
    int clickable = 0;
    int clickable1=0;
    RelativeLayout re_vehicle, re_staff, re_guest, re_delivery, lyt_settings;
    ChampApiInterface champApiInterface;
    SuperRecyclerView rv_dashboard;
    TextView tv_subscriptiondate, tv_version, tv_languagesettings ,txt_assn_name,txt_device_name,txt_gate_name ;
    String subscriptionDate, stringNumber, stringCode;
    DataBaseHelper dbh;
    String language = "";

    TelephonyManager telMgr;
    boolean[] existInDB1 = new boolean[1], existInDB2 = new boolean[1], existInDB3 = new boolean[1];
    byte[] tempFP1, tempFP2, tempFP3;
    Cursor curData;
    TextToSpeech t1;
    String memName = "";
    int nnnn = 0;
    int autoooooo = 0;
    private SwipeRefreshLayout swipeContainer;
    ///Start Added by Rajesh
    private Uri imageUri;
    private PendingIntent mPermissionIntent;
    private byte[] mVerifyImage, mVerifyTemplate;
    private int[] mMaxTemplateSize, grayBuffer;
    private int mImageWidth, mImageHeight, mImageDPI;
    private int nCaptureModeN;
    private Bitmap grayBitmap;
    private IntentFilter filter; //2014-04-11
    private SGAutoOnEventNotifier autoOn;

    //    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
//    private DatabaseReference mRootReference =firebaseDatabase.getReference();
//    private DatabaseReference mChildReference;
    private boolean mLed, mAutoOnEnabled = true, bSecuGenDeviceOpened, usbPermissionRequested, usbConnected = true;
    private JSGFPLib sgfplib;
    //a separate thread.
    public Handler fingerDetectedHandler = new Handler() {
        // @Override
        public void handleMessage(Message msg) {
            //Handle the message +sgfplib.DeviceInUse()
            Log.d("Dgddfdfhhjhj : ", "ff bf entrybywalk " + autoooooo + " " + " " + " " + mAutoOnEnabled + " " + usbConnected);

            if (mAutoOnEnabled) {

                Log.d("Dgddfdfhhjhj : ", "bf bf entrybywalk " + autoooooo + " " + nnnn + " " + " " + mAutoOnEnabled + " " + usbConnected);
                if (usbConnected) {
                    CaptureFingerPrint();
                }
                Log.d("Dgddfdfhhjhj : ", "ff af entrybywalk " + autoooooo + " " + nnnn + " " + " " + mAutoOnEnabled + " " + usbConnected);
                mAutoOnEnabled = false;
                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        // your code here
                        mAutoOnEnabled = true;
                    }
                };

                Handler myHandler = new Handler();
                //final int TIME_TO_WAIT = 2000;

                myHandler.postDelayed(myRunnable, 5000);

            }
        }
    };
    private BroadcastReceiver mReceiver;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase(SYNC)) {
                final String message = intent.getStringExtra("message");

                if(message.equalsIgnoreCase(VISITOR_ENTRY_SYNC)){
                    ArrayList<VisitorEntryLog> newAl =new ArrayList<>();
                    if(LocalDb.getVisitorEnteredLog()!=null){
                        newAl=LocalDb.getVisitorEnteredLog();
                        // LocalDb.saveAllVisitorLog(newAl);

                         vistorEntryListAdapter = new VistorEntryListAdapter(newAl, DashBoard.this);
                        rv_dashboard.setAdapter(vistorEntryListAdapter);
                    }else {
                         vistorEntryListAdapter = new VistorEntryListAdapter(newAl, DashBoard.this);
                        rv_dashboard.setAdapter(vistorEntryListAdapter);

                    }
                }

            }
        }
    };

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //Log.d(TAG,"Enter mUsbReceiver.onReceive()");
            if (ACTION_USB_PERMISSION.equals(action)) {
                usbConnected = true;
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                        } else
                            Log.e("TAG", "mUsbReceiver.onReceive() Device is null");
                    } else
                        Log.e("TAG", "mUsbReceiver.onReceive() permission denied for device " + device);
                }

            }
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                onResume();
                sgfplib = new JSGFPLib((UsbManager) getSystemService(Context.USB_SERVICE));
                bSecuGenDeviceOpened = false;
                usbPermissionRequested = false;

//        // debugMessage("Starting Activity\n");
                mLed = false;
//                mAutoOnEnabled = false;
                // autoOn = new SGAutoOnEventNotifier (sgfplib, this);
                nCaptureModeN = 0;
                usbConnected = true;
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                usbConnected = false;
            }
        }
    };

    public static boolean isTimeAutomatic(Context c) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.Global.getInt(c.getContentResolver(), Settings.Global.AUTO_TIME, 0) == 1;
        } else {
            return android.provider.Settings.System.getInt(c.getContentResolver(), android.provider.Settings.System.AUTO_TIME, 0) == 1;
        }
    }

    public static boolean isTimeZoneAutomatic(Context c) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.Global.getInt(c.getContentResolver(), Settings.Global.AUTO_TIME_ZONE, 0) == 1;
        } else {
            return android.provider.Settings.System.getInt(c.getContentResolver(), android.provider.Settings.System.AUTO_TIME, 0) == 1;
        }
    }

    public static void trimCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir1(dir);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public static boolean deleteDir1(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLocale(Prefs.getString(LANGUAGE,null));
        setContentView(R.layout.activity_dash_board);
        init();

        language=Prefs.getString(LANGUAGE,null);
        if(language!=null){
            Log.v("language",language);
        }else {
            Prefs.putString(LANGUAGE,"en");
        }
        if(!Prefs.getBoolean(BG_NOTIFICATION_ON,false)) {
            startService(new Intent(DashBoard.this, BGService.class));
        }
        getLatestSubscription();
        System.out.println("Shalini" + getCurrentTimeLocalYMD());
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_CALL_LOG,
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.RECORD_AUDIO
                        //Manifest.permission.CAPTURE_AUDIO_OUTPUT
                )

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
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();

//        prefManager=new PrefManager(getApplicationContext());
//        mChildReference = mRootReference.child("message"+prefManager.getAssociationId());
//
//        mChildReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if(dataSnapshot.getValue(String.class)==null)
//                {
//                    prefManager.setWelcomeMessage("Welcome");
//                }else {
//                    prefManager.setWelcomeMessage(dataSnapshot.getValue(String.class));
//                    sendFCM_welcomeMsg(prefManager.getWelcomeMessage());
//                }
//                Log.d("Message","A"+prefManager.getWelcomeMessage());
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

//        setLocale(prefManager.getLanguage());
        //Todo setContentView here for Language
//        language=prefManager.getLanguage();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mReceiver = new BatteryBroadcastReceiver();

        dbh = new DataBaseHelper(getApplicationContext());
        telMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        sendAnalyticsData("SDDashB_Oncreate", "Start", new Date() + "");

//        imageView= (ImageView) findViewById(leftpalm.id.imageViewV);

//        if(prefManager.getMemRoleID()==ROLE_GUARD) {
//            FirebaseMessaging.getInstance().subscribeToTopic("AllGuards" + prefManager.getAssociationId());
//            FirebaseMessaging.getInstance().subscribeToTopic("Guard" + prefManager.getGuardID());
//        }
//        if(prefManager.getMemRoleID()==ROLE_SUPERVISOR ){
//            FirebaseMessaging.getInstance().subscribeToTopic("AllGuards" + prefManager.getAssociationId());
//            FirebaseMessaging.getInstance().subscribeToTopic("AllSupervisor" + prefManager.getAssociationId());
//            FirebaseMessaging.getInstance().subscribeToTopic("Guard" + prefManager.getGuardID());
//        }

//        startService(new Intent(DashBoard.this, SGTrackingService.class));

//        curData=dbh.getRegularVisitorsFinger(GMT_YMD_CurrentTime());
       // curData = dbh.getRegularVisitorsFingerPrint();
        if (curData != null) {
            curData.moveToFirst();
        }
        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR)
                    t1.setLanguage(Locale.getDefault());
            }
        });

        grayBuffer = new int[JSGFPLib.MAX_IMAGE_WIDTH_ALL_DEVICES * JSGFPLib.MAX_IMAGE_HEIGHT_ALL_DEVICES];
        for (int i = 0; i < grayBuffer.length; ++i)
            grayBuffer[i] = Color.GRAY;
        grayBitmap = Bitmap.createBitmap(JSGFPLib.MAX_IMAGE_WIDTH_ALL_DEVICES, JSGFPLib.MAX_IMAGE_HEIGHT_ALL_DEVICES, Bitmap.Config.ARGB_8888);
        grayBitmap.setPixels(grayBuffer, 0, JSGFPLib.MAX_IMAGE_WIDTH_ALL_DEVICES, 0, 0, JSGFPLib.MAX_IMAGE_WIDTH_ALL_DEVICES, JSGFPLib.MAX_IMAGE_HEIGHT_ALL_DEVICES);

        int[] sintbuffer = new int[(JSGFPLib.MAX_IMAGE_WIDTH_ALL_DEVICES / 2) * (JSGFPLib.MAX_IMAGE_HEIGHT_ALL_DEVICES / 2)];
        for (int i = 0; i < sintbuffer.length; ++i)
            sintbuffer[i] = Color.GRAY;
        Bitmap sb = Bitmap.createBitmap(JSGFPLib.MAX_IMAGE_WIDTH_ALL_DEVICES / 2, JSGFPLib.MAX_IMAGE_HEIGHT_ALL_DEVICES / 2, Bitmap.Config.ARGB_8888);
        sb.setPixels(sintbuffer, 0, JSGFPLib.MAX_IMAGE_WIDTH_ALL_DEVICES / 2, 0, 0, JSGFPLib.MAX_IMAGE_WIDTH_ALL_DEVICES / 2, JSGFPLib.MAX_IMAGE_HEIGHT_ALL_DEVICES / 2);
        mMaxTemplateSize = new int[1];

        //USB Permissions
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        filter.addAction(UsbManager.EXTRA_PERMISSION_GRANTED);
        filter.addAction(ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver, filter);
        sgfplib = new JSGFPLib((UsbManager) getSystemService(Context.USB_SERVICE));

        bSecuGenDeviceOpened = false;
        usbPermissionRequested = false;

        mLed = false;
        mAutoOnEnabled = true;
        autoOn = new SGAutoOnEventNotifier(sgfplib, this);
        nCaptureModeN = 0;

        txt_assn_name=findViewById(R.id.txt_assn_name);
        txt_device_name=findViewById(R.id.txt_device_name);

        txt_assn_name.setText("Society: " + LocalDb.getAssociation().getAsAsnName());
        txt_gate_name.setText("Gate No: "+Prefs.getString(GATE_NO,null));
//        txt_device_name.setText("Gate: "+Prefs.getInt(ASSOCIATION_ID,0) );
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
        downloadBiometricData_Loop();
    }

    private void sendFCM_welcomeMsg(String welcomeMessage) {
//        FCMApiInterface apiService =
//                FCMApiClient.getClient().create(FCMApiInterface.class);
//
//        EntryPermissionPayload payloadData = new EntryPermissionPayload("getFirebaseWelcomeMsgReply",
//                welcomeMessage,1,
//                entry_type[3], GlobalVariables.getGlobal_mobilenumber(),prefManager.getAssociationId());
//        SendEntryPermissionRequest sendOTPRequest = new SendEntryPermissionRequest(payloadData, "/topics/Admin" +prefManager.getAssociationId());
//        Call<SendFCMResponse> call = apiService.sendEntryPermission(sendOTPRequest);
//
//        call.enqueue(new Callback<SendFCMResponse>() {
//            @Override
//            public void onResponse(Call<SendFCMResponse> call, Response<SendFCMResponse> response) {
//                Log.d("Dgddfdf", "fcm: " + response.body().getMessage_id());
//                if (response.body().getMessage_id() != null) {
//
//                } else {
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call<SendFCMResponse> call, Throwable t) {
//                 Log error here since request failed
//                Log.d("TAG", t.toString());
//                sendExceptions("SGDash","FCM fail 4522"+t.toString());
//            }
//        });
    }

    @Override
    protected void onResume() {
       // refreshAdapter();
//        if(!Prefs.getBoolean(BG_NOTIFICATION_ON,false)) {
//            startService(new Intent(DashBoard.this, BGService.class));
//        }

        LocalBroadcastManager.getInstance(this).
                registerReceiver(receiver, new IntentFilter("SYNC"));//constant
        super.onResume();

        spinner.setSelection(0);
        //  ArrayList<VisitorEntryLog> newAl =new ArrayList<>();
        if(LocalDb.getVisitorEnteredLog()!=null) {
            newAl = LocalDb.getVisitorEnteredLog();
            // LocalDb.saveAllVisitorLog(newAl);
//            rv_dashboard.setVisibility(View.VISIBLE);
//            tv_nodata.setVisibility(View.GONE);

            if ((newAl.isEmpty()) ){
                tv_nodata.setVisibility(View.VISIBLE);
                rv_dashboard.setVisibility(View.GONE);
            }
            vistorEntryListAdapter = new VistorEntryListAdapter(newAl, DashBoard.this);

            rv_dashboard.setAdapter(vistorEntryListAdapter);
        }

//        }else {
//            rv_dashboard.setVisibility(View.GONE);
//            tv_nodata.setVisibility(View.VISIBLE);
//        }
        Intent intentAction1 =new Intent(getApplicationContext(), BackgroundSyncReceiver.class);
        intentAction1.putExtra(BSR_Action, VISITOR_ENTRY_SYNC);
        sendBroadcast(intentAction1);
        //Toast.makeText(DashBoard.this,"NO data",Toast.LENGTH_LONG).show();
//}

        if (isTimeAutomatic(getApplication())) {

        } else {
            android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(DashBoard.this);
            alertDialogBuilder.setTitle("Time settings");

            // Setting Dialog Message
            alertDialogBuilder.setMessage("Time is not automatic. Please select automatic time");

            // On pressing Settings button
            alertDialogBuilder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_DATE_SETTINGS);
                    startActivity(intent);
                }
            });
            alertDialogBuilder.setCancelable(false);

            android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

        }


        if (isTimeZoneAutomatic(getApplication())) {

        } else {
            android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(DashBoard.this);
            alertDialogBuilder.setTitle("Time settings");

            // Setting Dialog Message
            alertDialogBuilder.setMessage("Timezone is not automatic. Please select automatic timezone");

            // On pressing Settings button
            alertDialogBuilder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_DATE_SETTINGS);
                    startActivity(intent);
                }
            });
            alertDialogBuilder.setCancelable(false);

            android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

        }

//        Intent intentAction3 = new Intent(getApplicationContext(), DownloadResDataReceiver.class);
//        intentAction3.putExtra(action, UPLOAD_IMAGES);
//        sendBroadcast(intentAction3);

//        Log.d("Count_image","ll"+idb.pending_getImages_toUpload());
//        sendAnalyticsData("SDDashB_OnResume ", "Image Count "+dbh.getAssociationName(prefManager.getAssociationId()), "Count: "+idb.pending_getImages_toUpload());

        bSecuGenDeviceOpened = false;
        usbPermissionRequested = false;

        mLed = false;
        mAutoOnEnabled = true;
        autoOn = new SGAutoOnEventNotifier(sgfplib, this);
        nCaptureModeN = 0;

//        prefManager.setOnForeground(true);

//        Log.d("AppVersionValidity"," onresume "+prefManager.getAppVersionValidity()+" ");

//        Intent intentAction = new Intent(getApplicationContext(), DownloadResDataReceiver.class);
//        intentAction.putExtra(action, DAILY_HELP);
//         sendBroadcast(intentAction);
//        Log.d("stfdhi ",prefManager.getGuardStartTime().equalsIgnoreCase("0001-01-01T00:00:00")+" ");

        try {
            long error = sgfplib.Init(SGFDxDeviceName.SG_DEV_AUTO);
            Log.d("onResume", "onResume( )" + nnnn);
            if (error != SGFDxErrorCode.SGFDX_ERROR_NONE) {
                android.app.AlertDialog.Builder dlgAlert = new android.app.AlertDialog.Builder(this);
                if (error == SGFDxErrorCode.SGFDX_ERROR_DEVICE_NOT_FOUND)
//                Toast.makeText(this, "connect secugen", Toast.LENGTH_LONG).show();
                    dlgAlert.setMessage("The attached fingerprint device is not supported on Android");
            } else {
                UsbDevice usbDevice = sgfplib.GetUsbDevice();
                if (usbDevice == null) {
                    android.app.AlertDialog.Builder dlgAlert = new android.app.AlertDialog.Builder(this);
                    dlgAlert.setMessage("SecuGen fingerprint sensor not found!");
                    dlgAlert.setTitle("SecuGen Fingerprint SDK");
                    dlgAlert.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    finish();
                                    return;
                                }
                            }
                    );
                    dlgAlert.setCancelable(false);
                    dlgAlert.create().show();
                } else {
                    boolean hasPermission = sgfplib.GetUsbManager().hasPermission(usbDevice);
                    Log.d("onResume", nnnn + " o " + hasPermission);
                    if (!hasPermission) {
                        if (!usbPermissionRequested) {
                            //Log.d(TAG, "Call GetUsbManager().requestPermission()");
                            usbPermissionRequested = true;
                            sgfplib.GetUsbManager().requestPermission(usbDevice, mPermissionIntent);
                        } else {
                            //wait up to 20 seconds for the system to grant USB permission
                            hasPermission = sgfplib.GetUsbManager().hasPermission(usbDevice);
                            int i = 0;
                            while ((hasPermission == false) && (i <= 40)) {
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
                        error = sgfplib.OpenDevice(0);
                        if (error == SGFDxErrorCode.SGFDX_ERROR_NONE) {
                            bSecuGenDeviceOpened = true;
                            SecuGen.FDxSDKPro.SGDeviceInfoParam deviceInfo = new SecuGen.FDxSDKPro.SGDeviceInfoParam();
                            error = sgfplib.GetDeviceInfo(deviceInfo);
                            mImageWidth = deviceInfo.imageWidth;
                            mImageHeight = deviceInfo.imageHeight;
                            mImageDPI = deviceInfo.imageDPI;

                            sgfplib.SetTemplateFormat(SGFDxTemplateFormat.TEMPLATE_FORMAT_SG400);
                            sgfplib.GetMaxTemplateSize(mMaxTemplateSize);

                            mVerifyTemplate = new byte[mMaxTemplateSize[0]];

                            sgfplib.WriteData(SGFDxConstant.WRITEDATA_COMMAND_ENABLE_SMART_CAPTURE, (byte) 1);
                            autoOn.start();
                        } else {

                        }
                    }

                }
            }
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "Connect Secugen Correctly", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        dbh.getResidentsLogVehicles();
//            prefManager.setOnForeground(false);
//            startService(new Intent(DashBoard.this, ByteDownloaderService.class));
//            Intent intentAction1 = new Intent(getApplicationContext(), DownloadResDataReceiver.class);
//            intentAction1.putExtra(action, NONREGULAR);
//            sendBroadcast(intentAction1);
//            Intent intentAction3 = new Intent(getApplicationContext(), DownloadResDataReceiver.class);
//            intentAction3.putExtra(action, UPLOAD_IMAGES);
//            sendBroadcast(intentAction3);
//            Intent intentAction22 = new Intent(getApplicationContext(), DownloadResDataReceiver.class);
//            intentAction22.putExtra(action, DAILY_HELP);
//            sendBroadcast(intentAction22);
//            Intent intentAction23 = new Intent(getApplicationContext(), DownloadResDataReceiver.class);
//            intentAction23.putExtra(action, PATROLLING);
//            sendBroadcast(intentAction23);
//            Intent intentAction24 = new Intent(getApplicationContext(), DownloadResDataReceiver.class);
//            intentAction24.putExtra(action, "SYNC_ASSOCIATIONS");
//            sendBroadcast(intentAction24);
//            Cursor cur=dbh.getMemberByAssnID_date(prefManager.getAssociationId(),IConstant.dateFormat_YMD.format(new Date()));
//            Cursor cur1=dbh.getMemberByAssnID(prefManager.getAssociationId());

//            if(cur.getCount()>0 || cur1.getCount()<1) {
//                Intent intentAction2 = new Intent(getApplicationContext(), DownloadResDataReceiver.class);
//                intentAction2.putExtra(action, OYE_MEMBER);
//                sendBroadcast(intentAction2);
//            }

//            startService(new Intent(DashBoard.this, ImageUploadService.class));

    }

    @Override
    public void onFailure(Exception e, int urlId) {

        showToast(this, e.getMessage()+" id "+urlId);
    }

    @Override
    public void onSuccess(String responce, Object data, int urlId, int position) {

//        if (urlId == URLData.URL_VISITOR_LOG.getUrlId()) {
//            VisitorLogCreateResp loginDetailsResponce = (VisitorLogCreateResp) data;
//            if (loginDetailsResponce != null) {
//                Log.d("str3", "str3: " + urlId+" id "+position+" "+" "+" "+loginDetailsResponce.success.toString());
//                if(loginDetailsResponce.success.equalsIgnoreCase("true")) {
////                    showToast(this, " Saved");
//                    visitorEntryLog(loginDetailsResponce.data.visitorLog.vlVisLgID);
//
//                }else{
//                    showToast(this, "Visitor Data not saved ");
//                }
//
//            } else {
//                showToast(this, "Something went wrong . please try again ");
//            }
//
//        }
//        else
            if (urlId == URLData.URL_VISITOR_MAKE_ENTRY.getUrlId()) {

            VisitorLogCreateResp loginDetailsResponce = (VisitorLogCreateResp) data;
            if (loginDetailsResponce != null) {
                Log.d("str3", "str3: " + urlId+" id "+position+" "+" "+" "+loginDetailsResponce.success.toString());
                if(loginDetailsResponce.success.equalsIgnoreCase("true")) {
                    showToast(this, " Welcome");
                    rv_dashboard.setVisibility(View.VISIBLE);
                    tv_nodata.setVisibility(View.GONE);
                    Intent intentAction1 =new Intent(getApplicationContext(), BackgroundSyncReceiver.class);
                    intentAction1.putExtra(BSR_Action, SENDFCM_toSYNC_VISITORENTRY);
                    sendBroadcast(intentAction1);

                }else{
                    showToast(this, "Visitor Details not saved ");
                }

            } else {
                rv_dashboard.setVisibility(View.GONE);
                tv_nodata.setVisibility(View.VISIBLE);

                showToast(this, "Something went wrong . please try again ");
            }
        }else if (urlId == URLData.URL_VISITOR_MAKE_EXIT.getUrlId()) {

            VisitorLogCreateResp loginDetailsResponce = (VisitorLogCreateResp) data;
            if (loginDetailsResponce != null) {
                rv_dashboard.setVisibility(View.VISIBLE);
                tv_nodata.setVisibility(View.GONE);
                Log.d("str3", "str3: " + urlId+" id "+position+" "+" "+" "+loginDetailsResponce.success.toString());
                if(loginDetailsResponce.success.equalsIgnoreCase("true")) {
                    showToast(this, " Thank You");
                   // rv_dashboard.setVisibility(View.VISIBLE);
                   // tv_nodata.setVisibility(View.GONE);
                    Intent intentAction1 =new Intent(getApplicationContext(), BackgroundSyncReceiver.class);
                    intentAction1.putExtra(BSR_Action, SENDFCM_toSYNC_VISITORENTRY);
                    sendBroadcast(intentAction1);

                }else{
                    showToast(this, "Exit Details not saved ");
                }

            } else {
                showToast(this, "Something went wrong . please try again ");

                rv_dashboard.setVisibility(View.GONE);
                tv_nodata.setVisibility(View.VISIBLE);
            }

        }

        //  showToast(this, urlId+" id "+position+" "+memId+" "+MemberType+" ");

//        finish();
    }

    private void visitorEntryLog(int vlVisLgID) {

        RestClient restClient = RestClient.getInstance();

        VisitorEntryReqJv loginReq = new VisitorEntryReqJv();

        loginReq.VLVisLgID =  vlVisLgID ;
        loginReq.VLEntryT=getCurrentTimeLocal();
        loginReq.VLEntyWID = LocalDb.getStaffList().get(0).getWkWorkID();

        Log.d("CreateVisitorLogResp","StaffEntry "+loginReq.toString());
        //  showToast(this, loginReq.ASAssnID + " fmid " + loginReq.FMID+" "+loginReq.FPImg1);

        restClient.addHeader(OYE247KEY, OYE247TOKEN);
        restClient.post(this, loginReq, VisitorLogCreateResp.class, this, URLData.URL_VISITOR_MAKE_ENTRY);

    }

//    private void visitorLog(int unitId,String personName,String mobileNumb,String desgn,
//                            String workerType, int staffID, String unitName) {
//
//        RestClient restClient = RestClient.getInstance();
//
//        RequestDTO loginReq = new RequestDTO();
//
//        int memID=64;
//        if(!BASE_URL.contains("dev")){
//            memID=410;
//        }
//
//        loginReq.aSAssnID =  LocalDb.getAssociation().getAsAssnID() ;
//        loginReq.mEMemID=memID;
//        loginReq.rERgVisID = staffID;
//        loginReq.uNUnitID =  unitId ;
//        loginReq.vLFName =  personName ;
//        loginReq.vLMobile = mobileNumb;
//        loginReq.vLComName=desgn;
//        loginReq.vLVisType = workerType;
//        loginReq.uNUniName = unitName;
//        loginReq.vLVisCnt=1;
//
//        Log.d("CreateVisitorLogResp","StaffEntry "+loginReq.toString());
//        //  showToast(this, loginReq.ASAssnID + " fmid " + loginReq.FMID+" "+loginReq.FPImg1);
//
//        restClient.addHeader(OYE247KEY, OYE247TOKEN);
//        restClient.post(this, loginReq, VisitorLogCreateResp.class, this, URLData.URL_VISITOR_LOG);
//
//        Intent ddc  =new  Intent(DashBoard.this, BackgroundSyncReceiver.class);
//        ddc.putExtra(BSR_Action, VisitorEntryFCM);
//        ddc.putExtra("msg", personName+" "+desgn +" is coming to your home");
//        ddc.putExtra("mobNum", mobileNumb);
//        ddc.putExtra("name", personName);
//        ddc.putExtra("nr_id", "0");
//        ddc.putExtra("unitname", unitName);
//        ddc.putExtra("memType", "Owner");
//        sendBroadcast(ddc);
//
//    }

    public void VisitorExit(final int vlVisLgID) {
        RestClient restClient = RestClient.getInstance();

        VisitorExitReqJv loginReq = new VisitorExitReqJv();

        loginReq.VLVisLgID =  vlVisLgID ;
        loginReq.VLExitT=getCurrentTimeLocal();
        loginReq.VLExitWID = LocalDb.getStaffList().get(0).getWkWorkID();

        Log.d("CreateVisitorLogResp","StaffEntry "+loginReq.toString());
        //  showToast(this, loginReq.ASAssnID + " fmid " + loginReq.FMID+" "+loginReq.FPImg1);

        restClient.addHeader(OYE247KEY, OYE247TOKEN);
        restClient.post(this, loginReq, VisitorLogCreateResp.class, this, URLData.URL_VISITOR_MAKE_EXIT);

    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (bSecuGenDeviceOpened) {
            autoOn.stop();
            sgfplib.CloseDevice();
            bSecuGenDeviceOpened = false;
        }
//        sgfplib.CloseDevice();

        mVerifyImage = null;
        mVerifyTemplate = null;
//        sgfplib.Close();
        unregisterReceiver(mUsbReceiver);
        Intent ddc2 =new Intent(DashBoard.this, BackgroundSyncReceiver.class);
        Log.d("SYNC_UNIT_LIST", "af " );
        ddc2.putExtra(BSR_Action, SYNC_UNIT_LIST);
        sendBroadcast(ddc2);
        Intent ddc1 =new Intent(DashBoard.this, BackgroundSyncReceiver.class);
        Log.d("SYNC_STAFF_LIST", "af " );
        ddc1.putExtra(BSR_Action, SYNC_STAFF_LIST);
        sendBroadcast(ddc1);
        super.onDestroy();
        try {
            trimCache(this);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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

    private void restartInLocale(String lang) {
        if (lang == null) {
            lang = "en";
        } else {
        }
        Locale myLocale = new Locale(lang);
        Configuration config = new Configuration();
        config.locale = myLocale;
        Resources resources = getResources();
        resources.updateConfiguration(config, resources.getDisplayMetrics());
        recreate();
    }

    public void SGFingerPresentCallback() {
        autoooooo++;
        if (usbConnected) {
            fingerDetectedHandler.sendMessage(new Message());
        }
    }

    @Override
    public void run() {

    }

    public void CaptureFingerPrint() {
        Log.d("abcdef", "7172");
        if (bSecuGenDeviceOpened == true) {
            //DEBUG Log.d(TAG, "Clicked MATCH");
            if (mVerifyImage != null)
                mVerifyImage = null;
            mVerifyImage = new byte[mImageWidth * mImageHeight];

            try {
                long result = sgfplib.GetImage(mVerifyImage);
                Log.d("match  1", result + " " + mVerifyImage.length);

                result = sgfplib.SetTemplateFormat(SecuGen.FDxSDKPro.SGFDxTemplateFormat.TEMPLATE_FORMAT_SG400);
                Log.d("match  2", result + " " + mVerifyImage.length);

                SGFingerInfo fpInfo = new SGFingerInfo();
                for (int i = 0; i < mVerifyTemplate.length; ++i)
                    mVerifyTemplate[i] = 0;

                result = sgfplib.CreateTemplate(fpInfo, mVerifyImage, mVerifyTemplate);
                Log.d("match  3", result + " " + mVerifyTemplate.length);

                boolean[] matched = new boolean[1];

                Log.d("match  5", result + " " + mVerifyTemplate.length + " " + matched[0]);
                Log.d("Dgddfdfhhjhj : ", "CaptureFingerPrint entrybywalk " + mVerifyTemplate.length + " " + autoooooo + " " + nnnn + " " + " " + mAutoOnEnabled + " " + usbConnected);
                if (matched[0]) {
//                                Toast.makeText(getApplicationContext(), "MATCHED!!\n ", Toast.LENGTH_SHORT).show();
                } else {
//                                Toast.makeText(getApplicationContext(), "NOT MATCHED!! ", Toast.LENGTH_SHORT).show();
                }

//                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//                am.setStreamVolume(AudioManager.STREAM_SYSTEM, am.getStreamMaxVolume(AudioManager.STREAM_SYSTEM), 0);

                int tempNumber = checkFingerPrint(mVerifyTemplate);
                Log.d("Biometric 953"," ");
                if (tempNumber == 0) {
                    t1.speak("Not Match", TextToSpeech.QUEUE_FLUSH, null);

                } else if (tempNumber > 0 && tempNumber < 4) {

                    ArrayList<VisitorEntryLog> enteredStaff = new ArrayList<>();
                    Log.d("Biometric 973"," "+(LocalDb.getVisitorEnteredLog()!=null));
                    //looping through existing elements
                    if(LocalDb.getVisitorEnteredLog()!=null) {
                        for (VisitorEntryLog s : LocalDb.getVisitorEnteredLog()) {
                            //if the existing elements contains the search input
                            if (s.getReRgVisID() == Integer.parseInt(memName)) {
                                //adding the element to filtered list
                                enteredStaff.add(s);
                            } else {

                            }
                        }
                    }
                    Log.d("Biometric 983"," ");

                    if (enteredStaff.size()>0 ) {

                        t1.speak("Thank You " + enteredStaff.get(0).getVlfName(), TextToSpeech.QUEUE_FLUSH, null);

                        Log.d("check 79 ", "bio");
                        VisitorExit(enteredStaff.get(0).getVlVisLgID());

                    } else {

//                                t1.speak("Welcome " + dbh.getMemName(Integer.parseInt(memName)), TextToSpeech.QUEUE_FLUSH, null);

                        LocalDb.getStaffList();
                        ArrayList<WorkerDetails> filterdNames = new ArrayList<>();

                        //looping through existing elements
                        for (WorkerDetails s : LocalDb.getStaffList()) {
                            //if the existing elements contains the search input
                            if (s.getWkWorkID()==Integer.parseInt(memName)) {
                                //adding the element to filtered list
                                filterdNames.add(s);
                            }
                        }
                        if(filterdNames.size()>0) {
                            WorkerDetails workerDetails = filterdNames.get(0);
//                            visitorLog(workerDetails.getUnUnitID(), workerDetails.getWkfName() + " " + workerDetails.getWklName(),
//                                    workerDetails.getWkMobile(), workerDetails.getWkDesgn(), workerDetails.getWkWrkType(),
//                                    workerDetails.getWkWorkID(), workerDetails.getUnUniName());

                            Log.d("check 78 ", "bio");
                            t1.speak("Welcome " + workerDetails.getWkfName() + workerDetails.getWklName(), TextToSpeech.QUEUE_FLUSH, null);
                        }else{
                            Toast.makeText(getApplicationContext(), "No Data" , Toast.LENGTH_SHORT).show();

                        }
                    }

//                                Toast.makeText(getApplicationContext(), "" + tempNumber, Toast.LENGTH_SHORT).show();
                } else {
//                                Toast.makeText(getApplicationContext(), "" + tempNumber, Toast.LENGTH_LONG).show();
                }
                Log.d("Biometric 1030"," ");

                mVerifyImage = null;
                fpInfo = null;
                matched = null;
                this.sgfplib.SetBrightness(100);
            } catch (Exception ex) {
                sendExceptions("SGDBA_CptFingPt", ex.toString());
                Log.d("Biometric 1035"," "+ex.toString());
                Toast.makeText(getApplicationContext(), "Biometric not attached correctly ", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Biometric Device Not Attached", Toast.LENGTH_LONG).show();
        }

        byte[] buffer = new byte[mImageWidth * mImageHeight];
//        long result = sgfplib.GetImageEx(buffer, 100,50);

        buffer = null;
    }

    public int checkFingerPrint(byte[] template) {
        nnnn++;
        boolean exists = false;
        int number = 0;
        int itera = 0;
//        curData=dbh.getRegularVisitorsFinger(GMT_YMD_CurrentTime());
       // curData = dbh.getRegularVisitorsFingerPrint();
        if (curData != null) {
            curData.moveToFirst();
        }
        if (curData != null) {
            if (curData.moveToFirst()) {
                existInDB1 = new boolean[1];
                existInDB2 = new boolean[1];
                existInDB3 = new boolean[1];
                memName = "";
                do {
                    itera++;
                    tempFP1 = new byte[mImageWidth * mImageHeight];
                    for (int j = 0; j < tempFP1.length; ++j)
                        tempFP1[j] = 0;

                    if (curData.getBlob(3) != null) {
                        Log.d("Dgddfdf hhjhj : ", "ff in entrybywalk " + autoooooo + " " + nnnn + " " + curData.getString(1) + " " + itera + " " + memName);
                        tempFP1 = curData.getBlob(3);
                        long res;
                        res = sgfplib.MatchTemplate(template, tempFP1, SGFDxSecurityLevel.SL_NORMAL, existInDB1);
                        if (existInDB1[0]) {
                            number++;
                            //  Log.d("Dgddfdf string hhjhj", "ff  entrybywalk "+curData.getString(3).toString()+" "+" ");
//                            Toast.makeText(getApplicationContext(), " MATCHED!! " + curData.getString(1) + " " + curData.getString(2), Toast.LENGTH_SHORT).show();
                            memName = curData.getString(1);
                        }
                        Log.d("data", curData.getString(0) + " ");
                    } else if (curData.getBlob(4) != null) {
                        tempFP2 = new byte[mImageWidth * mImageHeight];
                        for (int j = 0; j < tempFP2.length; ++j)
                            tempFP2[j] = 0;

                        tempFP2 = curData.getBlob(4);
                        long res2;
                        res2 = sgfplib.MatchTemplate(template, tempFP2, SGFDxSecurityLevel.SL_NORMAL, existInDB2);
                        if (existInDB2[0]) {
                            number++;

//                            Toast.makeText(getApplicationContext(), " MATCHED!! " + curData.getString(1) + " " + curData.getString(2), Toast.LENGTH_SHORT).show();
                            memName = curData.getString(1);
                        }
                        Log.d("data", curData.getString(0) + " ");
                    } else if (curData.getBlob(5) != null) {
                        tempFP3 = new byte[mImageWidth * mImageHeight];
                        for (int j = 0; j < tempFP3.length; ++j)
                            tempFP3[j] = 0;
                        tempFP3 = curData.getBlob(5);
                        long res3;
                        res3 = sgfplib.MatchTemplate(template, tempFP3, SGFDxSecurityLevel.SL_NORMAL, existInDB3);
                        if (existInDB3[0]) {
                            number++;

//                            Toast.makeText(getApplicationContext(), " MATCHED!! " + curData.getString(1) + " " + curData.getString(2), Toast.LENGTH_SHORT).show();
                            memName = curData.getString(1);
                        }
                    }
                    if (number > 0) {
                        return number;
                    }

                } while (curData.moveToNext());
            }
        }
        return number;
    }

    @Override
    protected void onStart() {
        registerReceiver(mReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        super.onStart();

        //startService(new Intent(DashBoard.this, ImageUploadService.class));

//        mChildReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Log.d("Message","A"+dataSnapshot.getValue(String.class));
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.d("Message",databaseError.getDetails());
//
//            }
//        });
    }

    @Override
    protected void onStop() {
        unregisterReceiver(mReceiver);

        super.onStop();
    }

    public void sendExceptions(String id, String execeptionString) {

//        Tracker t1 = ((GlobalPool) getApplication()).getTracker(
//                GlobalPool.TrackerName.APP_TRACKER);
//        t1.setScreenName("SGDashboard");
//        t1.send(new HitBuilders.EventBuilder()
//                .setCategory("Exceptions")
//                .setAction(id)
//                .setLabel(execeptionString)
//                .build());

    }

    public void sendAnalyticsData(String _Activity, String id, String execeptionString) {

//        Tracker t1 = ((GlobalPool) getApplication()).getTracker(
//                GlobalPool.TrackerName.APP_TRACKER);
//        t1.setScreenName("SGDashboard");
//        t1.send(new HitBuilders.EventBuilder()
//                .setCategory(_Activity)
//                .setAction(id)
//                .setLabel(execeptionString)
//                .build());

    }

    @Override
    public void onClick(View v) {
        Log.d("clcik", "view onClick" + v.getId());

        onTabClicked(v);
        switch (v.getId()) {
            case R.id.iv_settings:
                if (clickable == 0) {
                    lyt_settings.setVisibility(View.VISIBLE);
                    iv_settings.setBackgroundResource(R.drawable.cancel);
                    clickable = 1;
                } else if (clickable == 1) {
                    lyt_settings.setVisibility(View.GONE);
                    iv_settings.setBackgroundResource(R.drawable.settings);
                    clickable = 0;
                }
                break;
            case R.id.tv_patrolling:
                Intent i_vehicle = new Intent(DashBoard.this, PatrollingActivitynew.class);
                startActivity(i_vehicle);
                break;
            case R.id.tv_emergency:
                Intent i_emer = new Intent(DashBoard.this, TicketingDetailsActivity.class);
                startActivity(i_emer);
                break;

//            case R.id.tv_filter:
//                if (clickable1 == 0) {
//                    lyt_settings.setVisibility(View.VISIBLE);
//                    iv_settings.setBackgroundResource(R.drawable.cancel);
//                    clickable1 = 1;
//                } else if (clickable1 == 1) {
//                    lyt_settings.setVisibility(View.GONE);
//                    iv_settings.setBackgroundResource(R.drawable.settings);
//                    clickable1 = 0;
//                }
//                break;



        }

    }

    void onTabClicked(View v) {
        Log.d("clcik", "view " + v.getId());
        switch (v.getId()) {

            case R.id.re_vehicle:
                Intent i_vehicle = new Intent(DashBoard.this, CaptureImageOcr.class);
                startActivity(i_vehicle);

                break;

            case R.id.re_delivery:

                Intent i_delivery = new Intent(DashBoard.this, ServiceProviderListActivity.class);
                startActivity(i_delivery);
                break;

            case R.id.re_guest:

                Intent i_guest = new Intent(DashBoard.this, GuestCustomViewFinderScannerActivity.class);
                startActivity(i_guest);
                break;

            case R.id.re_staff:
                Intent i_staff = new Intent(DashBoard.this, StaffListActivity.class);
                startActivity(i_staff);
                break;

            case R.id.tv_subscriptiondate:
                break;
            case R.id.tv_languagesettings:

                showDialog();
                break;
            case R.id.record:

                Intent i=new Intent(DashBoard.this, WalkieTalkieActivity.class);
                startActivity(i);


//                myAudioRecorder = new MediaRecorder();
//                myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//                myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//                myAudioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//                myAudioRecorder.setOutputFile(mFileName);
//
//
//                try {
//                    myAudioRecorder.prepare();
//                    myAudioRecorder.start();
//                } catch (IllegalStateException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//// Started to stop after 5 sec automatically
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if(myAudioRecorder!= null)
//                        {
//                            myAudioRecorder.reset();
//                            myAudioRecorder.release();
//                            Toast.makeText(getApplicationContext(),"Recording Stopped",Toast.LENGTH_LONG).show();
//                            myAudioRecorder = null;
//                        }
//
//                        record.setEnabled(true);
//
//                    }
//                }, 5000);
//
//                // To stop after 5 sec automatically end
//
//                Toast.makeText(getApplicationContext(), "Recording Started", Toast.LENGTH_LONG).show();

                break;
        }
    }

    public void downloadBiometricData_Loop(){
        for (WorkerDetails s : LocalDb.getStaffList()) {
            //if the existing elements contains the search input
            if(dbh.fingercount(s.getWkWorkID())>3) {

            }else{
                Intent ddc =new Intent(getApplicationContext(), BackgroundSyncReceiver.class);
                Log.d("btn_biometric", "af " + s.getWkWorkID());
                ddc.putExtra(BSR_Action, SYNC_STAFF_BIOMETRIC);
                ddc.putExtra("ID", s.getWkWorkID());
                sendBroadcast(ddc);

            }

        }
    }

    /// End Added by Rajesh

    void init() {
        this.mHandler = new Handler();
        m_Runnable.run();

        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        try {
            audiofile = File.createTempFile("AudioRecording", ".3gp", dir);
        } catch (IOException e) {
//            Log.e(TAG, "external storage access error");
            return;
        }

        mFileName = audiofile.getAbsolutePath();
        tv_nodata=findViewById(R.id.tv_nodata);
        record=findViewById(R.id.record);
        record.setOnClickListener(this);
        //spinner = (Spinner) findViewById(R.id.spinner);
        // tv_filter=findViewById(R.id.tv_filter);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        champApiInterface = ChampApiClient.getClient().create(ChampApiInterface.class);
        tv_languagesettings = findViewById(R.id.tv_languagesettings);
        tv_languagesettings.setOnClickListener(this);
        tv_version = findViewById(R.id.tv_version);
        tv_version.setOnClickListener(this);
        tv_subscriptiondate = findViewById(R.id.tv_subscriptiondate);
        txt_gate_name=findViewById(R.id.txt_gate_name);
        tv_subscriptiondate.setOnClickListener(this);
        iv_settings = findViewById(R.id.iv_settings);
        iv_settings.setOnClickListener(this);
        lyt_settings = findViewById(R.id.lyt_settings);
        iv_settings.setBackgroundResource(R.drawable.settings);
        lyt_settings.setVisibility(View.GONE);
        re_vehicle = findViewById(R.id.re_vehicle);
        re_vehicle.setOnClickListener(this);
        re_guest = findViewById(R.id.re_guest);
        re_guest.setOnClickListener(this);
        re_staff = findViewById(R.id.re_staff);
        re_staff.setOnClickListener(this);
        re_delivery = findViewById(R.id.re_delivery);
        re_delivery.setOnClickListener(this);
        rv_dashboard = findViewById(R.id.rv_dashboard);
        rv_dashboard.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));



        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.

                fetchTimelineAsync(0);
                swipeContainer.setRefreshing(false);
            }
        });

        FirebaseMessaging.getInstance().subscribeToTopic("AllGuards" +LocalDb.getAssociation().getAsAssnID());
        //FirebaseMessaging.getInstance().subscribeToTopic("Guard" + prefManager.getGuardID());

        spinner.setOnItemSelectedListener(this);

        List<String> filter_option = new ArrayList<String>();
        filter_option.add(getResources().getString(R.string.in));
        filter_option.add(getResources().getString(R.string.out));

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, filter_option);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                // TODO Auto-generated method stub
                spinner.getSelectedItem().toString();
                if(spinner.getSelectedItem().toString().equalsIgnoreCase(getResources().getString(R.string.out))){

                    //TODO : exitvistorLog here

                    getExitVisitorLog();

                }else if(spinner.getSelectedItem().toString().equalsIgnoreCase(getResources().getString(R.string.in))){


                    if(LocalDb.getVisitorEnteredLog()!=null){
                        tv_nodata.setVisibility(View.GONE);
                        rv_dashboard.setVisibility(View.VISIBLE);
                        newAl=LocalDb.getVisitorEnteredLog();
                        // LocalDb.saveAllVisitorLog(newAl);

                         vistorEntryListAdapter = new VistorEntryListAdapter(newAl, DashBoard.this);
                        rv_dashboard.setAdapter(vistorEntryListAdapter);
                    }
                    else {

                        tv_nodata.setVisibility(View.VISIBLE);
                        rv_dashboard.setVisibility(View.GONE);
//                        AlertDialog.Builder builder = new AlertDialog.Builder(DashBoard.this);
//                        // builder.setTitle("Need Permissions");
//                        builder.setMessage("No data");
//                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.cancel();
//                                //openSettings();
//                            }
//                        });
////                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
////                            @Override
////                            public void onClick(DialogInterface dialog, int which) {
////                                dialog.cancel();
////                            }
////                        });
//                        builder.show();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });


        if(!Prefs.getBoolean(BG_NOTIFICATION_ON,false)) {
            startService(new Intent(DashBoard.this, BGService.class));
        }



        if(Prefs.getInt(PATROLLING_ID, 0) !=0){
            startService(new Intent(DashBoard.this, SGPatrollingService.class));
            AlertDialog.Builder builder = new AlertDialog.Builder(DashBoard.this);
            builder.setTitle("Patrolling Not Completed");
            builder.setMessage("Complete Now");
            builder.setPositiveButton("GOTO Patrolling", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Intent i_vehicle = new Intent(DashBoard.this, PatrollingActivitynew.class);
                    startActivity(i_vehicle);
                    dialog.cancel();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        }
    }
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DashBoard.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    public void getExitVisitorLog() {

        Call<VisitorLogExitResp> call = champApiInterface.getVisitorLogExitList(LocalDb.getAssociation().getAsAssnID()+"");
        Log.d("button_done ", "visitorlogbydate " + LocalDb.getAssociation().getAsAssnID() + " " + getCurrentTimeLocalYMD());

        call.enqueue(new Callback<VisitorLogExitResp>() {
            @Override
            public void onResponse(Call<VisitorLogExitResp> call, Response<VisitorLogExitResp> response) {

                if (response.body().getSuccess() == true) {

                    if (response.body().getData().getVisitorLog() != null) {
                        tv_nodata.setVisibility(View.GONE);

                        rv_dashboard.setVisibility(View.VISIBLE);

                        ArrayList<VisitorLogExitResp.Data.VisitorLog> nonExitedSort = new ArrayList<>();
                        ArrayList<VisitorLogExitResp.Data.VisitorLog> exitedSort = new ArrayList<>();

                        ArrayList<VisitorLogExitResp.Data.VisitorLog> arrayList = response.body().getData().getVisitorLog();

                        for (VisitorLogExitResp.Data.VisitorLog s : arrayList) {
                            //if the existing elements contains the search input
                            Log.d("button_done ", "visitorlogbydate " + s.getVlExitT() + " " + s.getVlExitT().equals("0001-01-01T00:00:00"));

//                        if (s.getVlExitT().equals("0001-01-01T00:00:00")) {
//                            Log.d("vlExitT ", "visitorlogbydate " + s.getVlExitT() + " " + s.getVlfName() + " ");
//                            nonExitedSort.add(s);
//
//                            //adding the element to filtered list
//                        } else {
//                            exitedSort.add(s);
//                        }
                        }

                        LocalDb.saveEnteredVisitorLog_old(nonExitedSort);

//                    Collections.sort(exitedSort, new Comparator<VisitorLogExitResp.Data.VisitorLog>() {
//                        @Override
//                        public int compare(VisitorLogExitResp.Data.VisitorLog lhs, VisitorLogExitResp.Data.VisitorLog rhs) {
//                            // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
//                            return rhs.getVlExitT().compareTo(lhs.getVlExitT());
//
//                        }
//                    });

                        ArrayList<VisitorLogExitResp.Data.VisitorLog> newAl = new ArrayList<>();

                        newAl = RandomUtils.getSortedVisitorLog_old(response.body().getData().getVisitorLog());
                        LocalDb.saveAllVisitorLog(newAl);

                        //  VistorListAdapter vistorListAdapter = new VistorListAdapter(newAl, DashBoard.this);
                        VistorListAdapter vistorListAdapter = new VistorListAdapter(response.body().getData().getVisitorLog(), DashBoard.this);
                        rv_dashboard.setAdapter(vistorListAdapter);

                        if (arrayList.size() == 0) {
                            Toast.makeText(DashBoard.this, "No items", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                       // rv_dashboard.setEmptyAdapter("No items to show!", false, 0);
                        tv_nodata.setVisibility(View.VISIBLE);

                        rv_dashboard.setVisibility(View.GONE);
//                        AlertDialog.Builder builder = new AlertDialog.Builder(DashBoard.this);
//                       // builder.setTitle("Need Permissions");
//                        builder.setMessage("No data");
//                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.cancel();
//                                //openSettings();
//                            }
//                        });
////                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
////                            @Override
////                            public void onClick(DialogInterface dialog, int which) {
////                                dialog.cancel();
////                            }
////                        });
//                        builder.show();
                    }
                }

            }

            @Override
            public void onFailure(Call<VisitorLogExitResp> call, Throwable t) {
                call.cancel();
                Log.d("button_done ", "visitorlogbydate " + t.getMessage() + " " + getCurrentTimeLocalYMD());

                //   rv_dashboard.setEmptyAdapter(getString(R.string.some_wrng), false, 0);

            }
        });

    }

    void getLatestSubscription() {

        Call<SubscriptionResponse> call = champApiInterface.getLatestSubscription(String.valueOf(Prefs.getInt(ASSOCIATION_ID,0)));
        call.enqueue(new Callback<SubscriptionResponse>() {
            @Override
            public void onResponse(Call<SubscriptionResponse> call, Response<SubscriptionResponse> response) {


                if (response.body().getSuccess() == true) {
                    DateFormat dateFormat_DMY = new SimpleDateFormat("dd-MM-yyyy");
                    String CurrentString = response.body().data.getSubscription().sueDate;
                    String[] separated = CurrentString.split("T");
                    subscriptionDate = separated[0];

                    tv_subscriptiondate.setText("Valid till: " + subscriptionDate);
                    //  if(PrefManager.getValidityDate().length()>0) {
                    try {
                        java.util.Date dt_dwnld_date = dateFormat_DMY.parse(response.body().data.getSubscription().sueDate);
                        Calendar c1 = Calendar.getInstance();
                        c1.setTime(dt_dwnld_date);

                        long days = (c1.getTimeInMillis() - System.currentTimeMillis()) / (24 * 60 * 60 * 1000) + 1;

                        if (0 < days && days <= 7) {
                            android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(DashBoard.this);
                            alertDialog.setTitle("Your Association Subscription Expires in " + days + " days");
                            alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            // Showing Alert Message
                            if (!DashBoard.this.isFinishing()) {
                                alertDialog.show();
                            }
                        }

                    } catch (Exception ex) {

                    }

                } else {
                }
            }

            @Override
            public void onFailure(Call<SubscriptionResponse> call, Throwable t) {
                call.cancel();
            }
        });

    }

    public void fetchTimelineAsync(int page) {


        if(spinner.getSelectedItem().toString().equalsIgnoreCase("Out")){

            getExitVisitorLog();
        }else if(spinner.getSelectedItem().toString().equalsIgnoreCase("In")){

            Intent intentAction1 =new Intent(getApplicationContext(), BackgroundSyncReceiver.class);
            intentAction1.putExtra(BSR_Action, VISITOR_ENTRY_SYNC);
            sendBroadcast(intentAction1);

        }

//        ArrayList<VisitorEntryLog> newAl =new ArrayList<>();
//        if(LocalDb.getVisitorEnteredLog()!=null){
//            newAl=LocalDb.getVisitorEnteredLog();
//            // LocalDb.saveAllVisitorLog(newAl);
//
//            VistorEntryListAdapter vistorEntryListAdapter = new VistorEntryListAdapter(newAl, DashBoard.this);
//            rv_dashboard.setAdapter(vistorEntryListAdapter);
//        }else {
//            Toast.makeText(DashBoard.this,"NO data",Toast.LENGTH_LONG).show();
//        }
//        Log.v("SWIPEREFRESH", getCurrentTimeLocal());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private class BatteryBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            // mBatteryLevelText.setText(getString(R.string.battery_level) + " " + level);
            // mBatteryLevelProgress.setProgress(level);
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, ifilter);
            int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL;

            int status_level = 0;
            Log.d("action 3964 charging", String.valueOf(isCharging) + " Level:" + status_level);
            if (level == 50) {
                Log.d("battery", String.valueOf(level));
                if (isCharging) {
                    Log.d("battery charging", String.valueOf(level));

                } else {
                    if (status_level > 3) {

                    } else {
                        status_level++;
//                        t1.speak("Battery 50%. Connect to  charger", TextToSpeech.QUEUE_FLUSH, null);

                        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
                        builder.setMessage("Battery 50%. Connect to  charger")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //do things

                                        dialog.cancel();
                                    }
                                });
                        android.app.AlertDialog alert = builder.create();
                        if (!DashBoard.this.isFinishing()) {
//                            alert.show();
                        }
                    }
                }
            } else if (level == 30) {
                if (isCharging) {

                } else {
                    t1.speak("Battery 30%. Connect to  charger", TextToSpeech.QUEUE_FLUSH, null);

                    final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
                    builder.setMessage("Battery 30%. Connect to  charger")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things

                                    dialog.cancel();
                                }
                            });
                    android.app.AlertDialog alert = builder.create();
                    if (!DashBoard.this.isFinishing()) {
                        alert.show();
                    }

                }
                Log.d("battery", String.valueOf(level));

            } else if (level == 20) {

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
                android.app.AlertDialog alert = builder.create();
                Log.d("battery", String.valueOf(level));
                if (isCharging) {
                    alert.dismiss();
                    Log.d("checkit 4022", "hi");
                } else {
                    t1.speak("Battery low Connect to  charger", TextToSpeech.QUEUE_FLUSH, null);
//                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Battery 20%. Connect to charger")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things
                                    dialog.cancel();
                                }
                            });
//                    AlertDialog alert = builder.create();
                    if (!DashBoard.this.isFinishing()) {
                        alert.show();
                    }

                }

            } else if (level == 10) {
                if (isCharging) {
                } else {
                    Log.d("battery", String.valueOf(level));
                    t1.speak("Battery 10%. Connect to  charger", TextToSpeech.QUEUE_FLUSH, null);

                    final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
                    builder.setMessage("Battery 10%. Connect to charger")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things
                                    dialog.cancel();
                                }
                            });
                    android.app.AlertDialog alert = builder.create();
                    if (!DashBoard.this.isFinishing()) {
                        alert.show();
                    }

                }

            } else if (level == 5) {

                if (isCharging) {
                } else {
//                    sendFCM_battery_alert();
                    Log.d("battery", String.valueOf(level));
                    t1.speak("Battery critical. Connect to charger", TextToSpeech.QUEUE_FLUSH, null);

                    final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
                    builder.setMessage("Battery critical. Connect to  charger")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things
                                    dialog.cancel();
                                }
                            });
                    android.app.AlertDialog alert = builder.create();
                    if (!DashBoard.this.isFinishing()) {
                        alert.show();
                    }
                }

            }
        }
    }

    RadioButton rb_english,rb_hindi;
    RadioGroup rg_language;
    private void showDialog() {

        dialogs = new Dialog(DashBoard.this);
        //dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogs.setCancelable(true);
        dialogs.setContentView(R.layout.layout_language_dailog);
        rg_language=dialogs.findViewById(R.id.rg_language);
        rb_english=dialogs.findViewById(R.id.rb_english);
        rb_hindi=dialogs.findViewById(R.id.rb_hindi);
        if(Prefs.getString(LANGUAGE,null).equalsIgnoreCase("en")){
            rb_english.setChecked(true);
        }
        else if(Prefs.getString(LANGUAGE,null).equalsIgnoreCase("hi")){
            rb_hindi.setChecked(true);
        }

//        Button btn_submit = dialogs.findViewById(R.id.btn_submit);
//        btn_submit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String result = "Selected Language: ";
//                result+= (rb_english.isChecked())?"English":(rb_hindi.isChecked())?"Hindi":"";
//                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
//                dialogs.dismiss();
//            }
//        });

        dialogs.show();

    }

    public void onRadioButtonClicked(View v)
    {
        boolean checked = ((RadioButton) v).isChecked();
        String str="";
        // Check which radio button was clicked
        switch(v.getId()) {
            case R.id.rb_hindi:
                if(checked)
                    str = "Hindi";
                restartInLocale("hi");
                dialogs.dismiss();
                Prefs.putString(LANGUAGE,"hi");

                break;
            case R.id.rb_english:
                if(checked)
                    str = "English";
                Prefs.putString(LANGUAGE,"en");
                restartInLocale("en");
                dialogs.dismiss();
                break;

        }
    }
    Dialog dialogs;

//public void refreshAdapter(){
//    final Handler handler = new Handler();
//    handler.postDelayed( new Runnable() {
//
//        @Override
//        public void run() {
//            refreshAdapter();
//             vistorEntryListAdapter.notifyDataSetChanged();
//            //handler.postDelayed( this, 60 * 1000 );
//        }
//    }, 30 * 1000 );
//}

    private final Runnable m_Runnable = new Runnable()
    {
        public void run()

        {
            //Toast.makeText(DashBoard.this,"in runnable",Toast.LENGTH_SHORT).show();
         //   vistorEntryListAdapter.notifyDataSetChanged();
            DashBoard.this.mHandler.postDelayed(m_Runnable,20000);
        }

    };
}
