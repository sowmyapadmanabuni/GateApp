package com.oyespace.guards.pertroling;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.zxing.Result;
import com.oyespace.guards.R;
import com.oyespace.guards.SGPatrollingService;
import com.oyespace.guards.network.ResponseHandler;
import com.oyespace.guards.network.RestClient;
import com.oyespace.guards.network.URLData;
import com.oyespace.guards.pojo.CheckPointByAssocID;
import com.oyespace.guards.qrscanner.BaseScannerActivity;
import com.oyespace.guards.request.PatrollingStartReq;
import com.oyespace.guards.request.PatrollingStopReq;
import com.oyespace.guards.request.SaveTrackingReq;
import com.oyespace.guards.responce.PatrollingStartResp;
import com.oyespace.guards.responce.SaveTrackingResp;
import com.oyespace.guards.utils.AppUtils;
import com.oyespace.guards.utils.DateTimeUtils;
import com.oyespace.guards.utils.LocalDb;
import com.oyespace.guards.utils.Prefs;
import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.core.ViewFinderView;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import java.util.Locale;

import static com.oyespace.guards.constants.PrefKeys.LANGUAGE;
import static com.oyespace.guards.constants.PrefKeys.PATROLLING_ID;
import static com.oyespace.guards.utils.ConstantUtils.*;
import static com.oyespace.guards.utils.Utils.showToast;

public class PatrollingActivitynew extends BaseScannerActivity implements ResponseHandler, ZXingScannerView.ResultHandler {
        private ZXingScannerView mScannerView;
    private Button noQrcodeButton;
    private TextView dataTextView;
    public   String[] patrolingdataList;


    GPSTracker gpsTracker;
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setLocale(Prefs.getString(LANGUAGE, null));

        setContentView(R.layout.activity_check_scanner_point);

        gpsTracker=new GPSTracker(getApplicationContext());
        gpsTracker.getLocation();
        noQrcodeButton = findViewById(R.id.button2);
        dataTextView =findViewById(R.id.scaneddata);
        noQrcodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // setupToolbar();
        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this) {
            @Override
            protected IViewFinder createViewFinderView(Context context) {
                return new CustomViewFinderView(context);
            }
        };
        contentFrame.addView(mScannerView);

    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(PatrollingActivitynew.this);
            }
        }, 5000);
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }


    String currentCheckPoint="",nextCheckpoint="";

    @Override
    public void handleResult(Result rawResult) {
//        Toast.makeText(this, "Contents = " + rawResult.getText(), Toast.LENGTH_SHORT).show();
        Log.d("handleResult patrolling","Contents = " + rawResult.getText() +
                ", Format = " + rawResult.getBarcodeFormat().toString());
        dataTextView.setText(rawResult.getText());

        final String patrolingdata = rawResult.getText();

        patrolingdataList = patrolingdata.split(",");
        if(patrolingdataList.length>4) {
            System.out.println("Patroling Data " + patrolingdataList[0]+" " + patrolingdataList[1] + " " + patrolingdataList[2] + " " + patrolingdataList[3]);
            ((TextView)findViewById(R.id.scanned_text)).setText(""+patrolingdataList[0]);

            if (!patrolingdataList[4].equalsIgnoreCase(Prefs.getInt(ASSOCIATION_ID, 0) + "")) {
                Toast.makeText(this, "Belongs to different Society / Association ", Toast.LENGTH_SHORT).show();


            } else if (Prefs.getInt(PATROLLING_ID, 0) == 0
                    && patrolingdataList[0].equalsIgnoreCase(PATROLLING_START_POINT)
                    ||Prefs.getInt(PATROLLING_ID, 0) == 0
                    && patrolingdataList[0].equalsIgnoreCase(PATROLLING_START_POINT_AND_END_POINT)) {

                currentCheckPoint=patrolingdataList[0];
                if(LocalDb.getCheckPointList()!=null) {
                    for (int i=0;i<LocalDb.getCheckPointList().size();i++) {
                        //if the existing elements contains the search input
                        if (LocalDb.getCheckPointList().get(i).getCpCkPName().equalsIgnoreCase(PATROLLING_END_POINT)) {
                            //adding the element to filtered list
                            nextCheckpoint=LocalDb.getCheckPointList().get(i+1).getCpCkPName();
                        } else {

                        }
                    }
                }
               // patrollingStart();
                if(gpsTracker.canGetLocation()) {

                    try {
                        int diff = AppUtils.Companion.calGeoLocationDiff(gpsTracker.getLatitude(),
                                gpsTracker.getLongitude(),
                                Double.parseDouble(patrolingdataList[1]),
                                Double.parseDouble(patrolingdataList[2]));

                        Log.d("xgdssd acc2",  " " + diff+" "+gpsTracker.getLatitude()+" "+gpsTracker.getLongitude());
                        if(diff>CHECKPOINT_ACCURACY){
                            Toast.makeText(this, diff+" meters away from Check Point", Toast.LENGTH_SHORT).show();
                        }else{
                            patrollingStart();
                        }

                    } catch ( Exception  ex) {
                        Log.d("xgdssd acc2",  "ex " + ex.toString());
                        Toast.makeText(this, "Parsing error ", Toast.LENGTH_SHORT).show();

                    }

                }else {
                    Toast.makeText(this, "Unable to receive Location ", Toast.LENGTH_SHORT).show();
                }

            } else  if (Prefs.getInt(PATROLLING_ID, 0) != 0
                    && patrolingdataList[0].equalsIgnoreCase(PATROLLING_END_POINT)
                    || Prefs.getInt(PATROLLING_ID, 0) != 0
                    && patrolingdataList[0].equalsIgnoreCase(PATROLLING_START_POINT_AND_END_POINT)) {
                //patrollingStop();
                if(gpsTracker.canGetLocation()) {

                    try {
                        int diff = AppUtils.Companion.calGeoLocationDiff(gpsTracker.getLatitude(),
                                gpsTracker.getLongitude(),
                                Double.parseDouble(patrolingdataList[1]),
                                Double.parseDouble(patrolingdataList[2]));

                        Log.d("xgdssd acc2",  " " + diff);
                        if(diff>CHECKPOINT_ACCURACY){
                            Toast.makeText(this, diff+" meters away from Check Point", Toast.LENGTH_SHORT).show();
                        }else{
                            patrollingStop();
                        }

                    } catch ( Exception  ex) {
                        Log.d("xgdssd acc2",  "ex " + ex.toString());
                        Toast.makeText(this, "Parsing error ", Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(this, "Unable to receive Location ", Toast.LENGTH_SHORT).show();
                }

            } else if(Prefs.getInt(PATROLLING_ID, 0) != 0){
                gpsTracker.getLocation();
                if(!patrolingdataList[0].equalsIgnoreCase(PATROLLING_START_POINT)) {
                    currentCheckPoint=patrolingdataList[0];
                    if(LocalDb.getCheckPointList()!=null) {
                        for (int i=0;i<LocalDb.getCheckPointList().size();i++) {
                            //if the existing elements contains the search input
                            if (LocalDb.getCheckPointList().get(i).getCpCkPName().equalsIgnoreCase(currentCheckPoint)) {
                                //adding the element to filtered list
                                nextCheckpoint=LocalDb.getCheckPointList().get(i+1).getCpCkPName();
                            } else {

                            }
                        }
                    }
//                    if (gpsTracker.canGetLocation()) {
//                        saveCheckPoints(patrolingdataList[0], gpsTracker.getLatitude() + "," + gpsTracker.getLongitude());
//                    }
//                    Toast.makeText(this, "Check Point Saved ", Toast.LENGTH_SHORT).show();

                    if(gpsTracker.canGetLocation()) {

                        try {
                            int diff = AppUtils.Companion.calGeoLocationDiff(gpsTracker.getLatitude(),
                                    gpsTracker.getLongitude(),
                                    Double.parseDouble(patrolingdataList[1]),
                                    Double.parseDouble(patrolingdataList[2]));

                            Log.d("xgdssd acc2",  " " + diff);
                            if(diff>CHECKPOINT_ACCURACY){
                                Toast.makeText(this, diff+" meters away from Check Point", Toast.LENGTH_SHORT).show();
                            }else{
                                saveCheckPoints(patrolingdataList[0], gpsTracker.getLatitude() + "," + gpsTracker.getLongitude());
                                Toast.makeText(this, "Check Point Saved ", Toast.LENGTH_SHORT).show();
                            }

                        } catch ( Exception  ex) {
                            Log.d("xgdssd acc2",  "ex " + ex.toString());
                            Toast.makeText(this, "Parsing error ", Toast.LENGTH_SHORT).show();
                        }

                    }else {
                        Toast.makeText(this, "Unable to receive Location ", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(this, "Go To Next Check Point", Toast.LENGTH_SHORT).show();
                }
            } else if(Prefs.getInt(PATROLLING_ID, 0) == 0){
                Toast.makeText(this, "Invalid Start Point", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Invalid Check Point", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Invalid Check Point QR Code ", Toast.LENGTH_SHORT).show();


        }

     /*  new LovelyStandardDialog(PatrollingActivitynew.this, LovelyStandardDialog.ButtonLayout.VERTICAL)
                                .setTopColorRes(R.color.google_red)
                .setIcon(R.drawable.ic_info_black_24dp)

                .setTitle(patrolingdataList[0])
                .setTitleGravity(Gravity.CENTER)
                .setMessage("")
                .setMessageGravity(Gravity.CENTER)
               .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {

                   }
               })

               .show();
*/

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(PatrollingActivitynew.this);
            }
        }, 5000);

    }

    private void patrollingStart() {

        RestClient restClient = RestClient.getInstance();
        PatrollingStartReq loginReq = new PatrollingStartReq();

        loginReq.ASAssnID = Prefs.getInt(ASSOCIATION_ID,0) ;
        loginReq.WKWorkID=LocalDb.getStaffList().get(0).getWkWorkID();
        loginReq.wkfName = Prefs.getString(GATE_NO, "");//LocalDb.getStaffList().get(0).getWkfName()+LocalDb.getStaffList().get(0).getWklName();
        loginReq.PSPtrlSID =  1 ;
        //loginReq.PTSDateT=DateTimeUtils.getCurrentTimeLocal();

        //Log.d("patrollingStart","StaffEntry "+loginReq.PTSDateT.toString());
        Log.d("patrollingStart","StaffEntry "+loginReq.ASAssnID+" "+loginReq.WKWorkID+" "
                +loginReq.wkfName+" "+loginReq.PSPtrlSID+" " );

        restClient.addHeader(OYE247KEY, OYE247TOKEN);
        restClient.post(this, loginReq, PatrollingStartResp.class, this, URLData.URL_PATROLLING_CREATE);

    }

    private void patrollingStop() {

        RestClient restClient = RestClient.getInstance();
        PatrollingStopReq loginReq = new PatrollingStopReq();

        loginReq.PTPtrlID = Prefs.getInt(PATROLLING_ID,0) ;
        loginReq.PTEDateT=DateTimeUtils.getCurrentTimeLocal();

        Log.d("patrollingStop","StaffEntry "+loginReq.toString()+" "+loginReq.PTPtrlID+" "+loginReq.PTEDateT );

        restClient.addHeader(OYE247KEY, OYE247TOKEN);
        restClient.post(this, loginReq, PatrollingStartResp.class, this, URLData.URL_PATROLLING_STOP);

    }

    private void saveCheckPoints(String checkPointName,String gpsPoint) {

        RestClient restClient = RestClient.getInstance();

        SaveTrackingReq loginReq = new SaveTrackingReq();

        loginReq.ASAssnID=Prefs.getInt(ASSOCIATION_ID,0);
        loginReq.CPCkPName=checkPointName;
        loginReq.TRGPSPnt=gpsPoint;
        loginReq.WKWorkID=LocalDb.getStaffList().get(0).getWkWorkID();
        loginReq.PTPtrlID = Prefs.getInt(PATROLLING_ID,0) ;
        loginReq.TRTDateT=DateTimeUtils.getCurrentTimeLocal();

        Log.d("saveCheckPoints","StaffEntry "+loginReq.ASAssnID+" "+loginReq.CPCkPName+" "
                +loginReq.WKWorkID+" "+loginReq.PTPtrlID+" "+loginReq.TRTDateT );

        restClient.addHeader(OYE247KEY, OYE247TOKEN);
        restClient.post(this, loginReq, SaveTrackingResp.class, this, URLData.URL_SAVE_CHECK_POINT);

    }

    @Override
    public void onFailure(Exception e, int urlId) {

        showToast(this, e.getMessage()+" id "+urlId);
    }

    @Override
    public void onSuccess(String responce, Object data, int urlId, int position) {

        if (urlId == URLData.URL_PATROLLING_CREATE.getUrlId()) {
            PatrollingStartResp loginDetailsResponce = (PatrollingStartResp) data;
            if (loginDetailsResponce != null) {
                Log.d("str3", "str3: " + urlId+" id "+position+" "+" "+" "+loginDetailsResponce.success.toString());
                if(loginDetailsResponce.success.equalsIgnoreCase("true")) {
                    Prefs.putInt(PATROLLING_ID,loginDetailsResponce.data.patrolling.ptPtrlID);
                    startService(new Intent(PatrollingActivitynew.this, SGPatrollingService.class));
                    showToast(this, "Patrollling started. Next point is "+nextCheckpoint);
                }else{
                    showToast(this, "Patrollling not started");
                }

            } else {
                showToast(this, "Something went wrong . please try again ");
            }

        }else if (urlId == URLData.URL_PATROLLING_STOP.getUrlId()) {
            PatrollingStartResp loginDetailsResponce = (PatrollingStartResp) data;
            if (loginDetailsResponce != null) {
                Log.d("str3", "str3: " + urlId+" id "+position+" "+" "+" "+loginDetailsResponce.success.toString());
                if(loginDetailsResponce.success.equalsIgnoreCase("true")) {
                    Prefs.putInt(PATROLLING_ID,0);
                    showToast(this, "Patrollling stoped");
                    finish();
                }else{
                    showToast(this, "Patrollling not stopped "+loginDetailsResponce.toString());
                }

            } else {
                showToast(this, "Something went wrong . please try again ");
            }
        }else if (urlId == URLData.URL_SAVE_CHECK_POINT.getUrlId()) {

            SaveTrackingResp loginDetailsResponce = (SaveTrackingResp) data;
            if (loginDetailsResponce != null) {
                Log.d("str3", "saveCheckPoints: " + urlId+" id "+position+" "+" "+" "+loginDetailsResponce.toString());
                if(loginDetailsResponce.success.equalsIgnoreCase("true")) {
                    showToast(this, "Go To Next Check Point: "+nextCheckpoint);
                }else{
                    showToast(this, " not saved ");
                }

            } else {
                showToast(this, "Something went wrong . please try again ");
            }

        }

        //  showToast(this, urlId+" id "+position+" "+memId+" "+MemberType+" ");

    }

    private static class CustomViewFinderView extends ViewFinderView {
        public static final String TRADE_MARK_TEXT = "ZXing";
        public static final int TRADE_MARK_TEXT_SIZE_SP = 40;
        public final Paint PAINT = new Paint();

        public CustomViewFinderView(Context context) {
            super(context);
            init();
        }

        public CustomViewFinderView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        private void init() {
            PAINT.setColor(Color.WHITE);
            PAINT.setAntiAlias(true);
            float textPixelSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                    TRADE_MARK_TEXT_SIZE_SP, getResources().getDisplayMetrics());
            PAINT.setTextSize(textPixelSize);
            setSquareViewFinder(true);
        }

        @Override
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            // drawTradeMark(canvas);
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
