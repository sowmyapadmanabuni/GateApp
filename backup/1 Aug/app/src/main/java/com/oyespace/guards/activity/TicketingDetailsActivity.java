package com.oyespace.guards.activity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import com.oyespace.guards.*;
import com.oyespace.guards.network.*;
import com.oyespace.guards.pertroling.GPSTracker;
import com.oyespace.guards.request.CreateTicketingActionReq;
import com.oyespace.guards.responce.CreateTicketingActionResp;
import com.oyespace.guards.responce.SubscriptionResponse;
import com.oyespace.guards.responce.TicketListingTesponse;
import com.oyespace.guards.responce.VisitorLogExitResp;
import com.oyespace.guards.utils.ConstantUtils;
import com.oyespace.guards.utils.DateTimeUtils;
import com.oyespace.guards.utils.LocalDb;
import com.oyespace.guards.utils.Prefs;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.oyespace.guards.constants.PrefKeys.EMERGENCY_SOUND_ON;
import static com.oyespace.guards.utils.ConstantUtils.*;
import static com.oyespace.guards.utils.Utils.showToast;

public class TicketingDetailsActivity extends BaseActivity implements OnMapReadyCallback , ResponseHandler {
    ChampApiInterface champApiInterface;
    private GoogleMap mMap;
    List<LatLng> patrollingRoute;
    Handler handler;
    public ImageView img_incident;
    Bitmap bitmapImage=null;
    private List<ViewReportList> mySGList = new ArrayList<ViewReportList>();
    //private IncidentReportAdapter mAdapter;
    RecyclerView recyclerView ;
    DataBaseHelper dbh;
    TelephonyManager telMgr;
    GPSTracker gps;
    int EmergencyID=0;
    RelativeLayout resolve;
    Bitmap[] incidentimages=new Bitmap[5];
    int p=0;
    ViewFlipper viewFlipper;
    Cursor curImage;
    int i=0;
    int Nid=0;
    String mobile_number=null;
    TextView tv_name,tv_mobileno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_response);
        tv_name=findViewById(R.id.tv_name);
        tv_mobileno=findViewById(R.id.tv_mobileno);
        gps = new GPSTracker(getApplicationContext());
        dbh=new DataBaseHelper(getApplicationContext());

        telMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        resolve=findViewById(R.id.rl_refresh);

        EmergencyID= 1063;
        Cursor cursor=dbh.getEmergencyNotifications();
        if(cursor.getCount()>0) {
            cursor.moveToFirst();

//                            cursor.getInt(cursor.getColumnIndex("Nid"));
//                            cursor.getInt(cursor.getColumnIndex("AssociatoinID")),
//                            cursor.getString(cursor.getColumnIndex("noti_title")),
//                            cursor.getString(cursor.getColumnIndex("sub_title")),
//                            cursor.getString(cursor.getColumnIndex("notified")),
//                            cursor.getString(cursor.getColumnIndex("noti_type")),
            EmergencyID=  cursor.getInt(cursor.getColumnIndex("noti_id"));
            mobile_number    =         cursor.getString(cursor.getColumnIndex("MobileNumber"));
            Nid=cursor.getInt(cursor.getColumnIndex("Nid"));

        }else{
            Toast.makeText(getApplicationContext(), "FInish " , Toast.LENGTH_LONG).show();
//            finish();

        }

        cursor.close();
        dbh.updatesecuritynotification_setNotified(Nid);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map1);
        mapFragment.getMapAsync(this);



        recyclerView = findViewById(R.id.incident_list);
        champApiInterface = ChampApiClient.getClient().create(ChampApiInterface.class);
        // mAdapter = new IncidentReportAdapter(getApplicationContext(), mySGList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.setAdapter(mAdapter);

        viewFlipper=findViewById(R.id.flipperid);
        loadincidentimages();
        Log.d("hello","155");

        //loadincidentimages();

        patrollingRoute=new ArrayList<>();
        handler = new Handler();

        Log.d("Dgddfdf emer gency","Dgddfdfeemer "+EmergencyID+ " ");

        viewFlipper.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                viewFlipper.setFlipInterval(500);
                Log.d("Dgddfdf","Hello");
                if(viewFlipper.isFlipping())
                {
                    viewFlipper.stopFlipping();
                }else
                    viewFlipper.startFlipping();
                //viewFlipper.stopFlipping();
                return false;
            }
        });

    }

    private void showincidentimages() {
        Log.d("Dgddfd incid0ent", "A2"+incidentimages.length);
        for (int i = 0; i < incidentimages.length; i++) {
            if(incidentimages[i]!=null){
                Log.d("Dgddfd incid1ent", "B3");
                setFlipperImage(incidentimages[i]);
            }
            else {
                Log.d("Dgddfd incid1ent", "A3");
            }
        }

    }

    private void setFlipperImage(Bitmap res) {
        Log.d("Dgddfd Called", "B");
        ImageView image = new ImageView(getApplicationContext());
        image.setImageBitmap(res);
        viewFlipper.addView(image);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadincidentimages();
    }


    private void loadincidentimages() {
        for(i=0;i<5;i++)
        {
            try {

                String imgName= "Images/"+"Association"+Prefs.getInt(ASSOCIATION_ID,0)+"EMERGENCY"+"INCIDENT"+EmergencyID+"N"+0+".jpg";

                Log.v("IMAGEURL",IMAGE_BASE_URL+"..."+imgName);
                Picasso.with(getApplicationContext())
                        .load(IMAGE_BASE_URL+imgName)
//                            .placeholder(R.drawable.mock_test)
                        .error(R.drawable.usericon)
//                            .resize(60,60)
                        .into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bm, Picasso.LoadedFrom from) {
                                Log.d("Dgddfdf picas","3");
                                if(i==0)
                                {
                                    if(bm!=null){
                                        incidentimages[i]=bm;
                                        setFlipperImage(incidentimages[i]);
                                    }
                                    bm=null;
                                }else if(i==1)
                                {

                                    if(bm!=null)
                                    {
                                        incidentimages[i]=bm;
                                        setFlipperImage(incidentimages[i]);
                                    }
                                    bm=null;
                                } else if(i==2)
                                {

                                    if(bm!=null)
                                    {
                                        incidentimages[i]=bm;
                                        setFlipperImage(incidentimages[i]);
                                    }
                                    bm=null;
                                } else if(i==3)
                                {

                                    if(bm!=null)
                                    {
                                        incidentimages[i]=bm;
                                        setFlipperImage(incidentimages[i]);
                                    }
                                    bm=null;
                                }else if(i==4)
                                {

                                    if(bm!=null)
                                    {
                                        incidentimages[i]=bm;
                                        setFlipperImage(incidentimages[i]);
                                    }
                                    bm=null;
                                }
                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {

                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });
                Log.d("photoid 3", " "  + " " + imgName);

            } catch (Exception ex) {
                Log.d("photoid value img","  "+ex.toString()+" ");

            }
        }

    }

    private class ViewReportList {
        private Integer IncidentID, GuardID;
        private String IncidentDetails,DateTime;

        public ViewReportList(Integer incidentID, Integer guardID, String incidentDetails,
                              String dateTime) {
            this.IncidentID = incidentID;
            this.GuardID = guardID;
            this.IncidentDetails = incidentDetails;
            this.DateTime = dateTime;
        }

        public Integer getIncidentID() {
            return IncidentID;
        }

        public void setIncidentID(Integer incidentID) {
            IncidentID = incidentID;
        }

        public Integer getGuardID() {
            return GuardID;
        }

        public void setGuardID(Integer guardID) {
            GuardID = guardID;
        }

        public String getIncidentDetails() {
            return IncidentDetails;
        }

        public void setIncidentDetails(String incidentDetails) {
            IncidentDetails = incidentDetails;
        }

        public String getDateTime() {
            return DateTime;
        }

        public void setDateTime(String dateTime) {
            DateTime = dateTime;
        }

    }

//    private class IncidentReportAdapter extends RecyclerView.Adapter<IncidentReportAdapter.MyViewHolder> {
//        private List<ViewReportList> moviesList;
//        int p = 0;
//
//        public class MyViewHolder extends RecyclerView.ViewHolder {
//            public TextView memId,  assnId, memRoleId, memStatus;
//            Button btn_call;
//
//            public MyViewHolder(View view) {
//                super(view);
//                memId = view.findViewById(R.id.memberId);
//                assnId = view.findViewById(R.id.assnId);
//                memStatus = view.findViewById(R.id.memStatus);
//                btn_call=view.findViewById(R.id.call);
//            }
//        }
//
//        public IncidentReportAdapter(Context context, List<ViewReportList> moviesList) {
//            this.moviesList = moviesList;
//        }
//
//        @Override
//        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            View itemView = LayoutInflater.from(parent.getContext())
//                    .inflate(R.layout.incident_response_list, parent, false);
//
//            return new MyViewHolder(itemView);
//        }
//
//
//
//        @Override
//        public void onBindViewHolder(final MyViewHolder holder, int position) {
//            final ViewReportList movie = moviesList.get(position);
//
//            holder.memId.setText("" + dbh.getGuardName1(movie.getGuardID()) + " ");
//            holder.assnId.setText(""+movie.getIncidentDetails()+" On: \n" + Methods.GMT_TO_IST_YMDhms(movie.getDateTime()));
//            holder.memStatus.setText(""+dbh.getGuardMobile(movie.getGuardID()));
//            final String uservalue = holder.memId.getText().toString();
//            Log.d("name",uservalue);
////            if(movie.getIncidentDetails().contains("Resolved")) {
////                holder.btn_incient_resolved.setVisibility(View.GONE);
////            }
//
//            final String unitName=dbh.getUnitName(prefManager.getMemUnitID());
//            Log.d("unit",unitName+dbh.getIncidentUnitName(EmergencyID));
//
//            Log.d(" value318emer"," res pagr  |"+dbh.getIncidentStatus(EmergencyID)+"| " +" ");
//
//            holder.memStatus.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    p=holder.getPosition();
//                    if(telMgr.getSimState()== TelephonyManager.SIM_STATE_ABSENT) {
//                        Snackbar snackbar = Snackbar
//                                .make(getCurrentFocus(), No_Sim_Found, Snackbar.LENGTH_LONG);
//                        snackbar.show();
//                    }else  if(telMgr.getSimState()== TelephonyManager.SIM_STATE_UNKNOWN) {
//                        Snackbar snackbar = Snackbar
//                                .make(getCurrentFocus(), Unknown_SIM_State, Snackbar.LENGTH_LONG);
//                        snackbar.show();
//                    }else {
//                        Intent intenta = new Intent(Intent.ACTION_CALL);
//                        intenta.setData(Uri.parse("tel:" + dbh.getGuardMobile(movie.getGuardID())));
//                        startActivity(intenta);
//                    }
//                }
//            });
//
//            holder.btn_call.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    p=holder.getPosition();
//                    if(telMgr.getSimState()== TelephonyManager.SIM_STATE_ABSENT) {
//                        Snackbar snackbar = Snackbar
//                                .make(getCurrentFocus(), No_Sim_Found, Snackbar.LENGTH_LONG);
//                        snackbar.show();
//                    }else  if(telMgr.getSimState()== TelephonyManager.SIM_STATE_UNKNOWN) {
//                        Snackbar snackbar = Snackbar
//                                .make(getCurrentFocus(), Unknown_SIM_State, Snackbar.LENGTH_LONG);
//                        snackbar.show();
//                    }else {
//                        Intent intenta = new Intent(Intent.ACTION_CALL);
//                        intenta.setData(Uri.parse("tel:" + dbh.getGuardMobile(movie.getGuardID())));
//                        startActivity(intenta);
//                    }
//
//                }
//            });
//
//        }
//
//        public void IncidentResolved(final int incidentId){
//            new AsyncTask<String, String, String>() {
//
//                @Override
//                protected void onPreExecute() {
//                    super.onPreExecute();
//                }
//
//                @Override
//                protected String doInBackground(String... params) {
//                    String response=null;
//
//                    try {
//                        response = Methods.postIncidentResolved("{ \"IncidentID\":" + incidentId + "," +
//                                "\"GguardID\":" + prefManager.getGuardID() + "," +
//                                "\"Status\": \"" + Resolved+ "\" }");
//                        Log.d("Dgddfdf req", response + "");
//                    }catch (Exception ex){
//                        return null;
//                    }
//                    return response;
//                }
//
//                //            @Override
//                protected void onPostExecute(String s) {
//                    super.onPostExecute(s);
//                    if (s != null) {
//                        try {
//                            Log.d("Dgddfdf res",s+"");
//                            JSONObject jsonObj = new JSONObject(s);
//                            boolean resl=jsonObj.getBoolean(success);
//                            if(resl==true){
//                                Toast.makeText(getApplicationContext(),
//                                        "Incident Resolved " ,
//                                        Toast.LENGTH_SHORT).show();
//
//                            }
//
//                            mAdapter.notifyDataSetChanged();
//
//                        } catch (final JSONException e) {
//                            Log.d("Dgddfdf 2", "Json parsing error: " + e.getMessage());
//                        }
//
//                    } else {
//                        Log.d("Dgddfdf 3", "Couldn't get json from server.");
//                    }
//
//                }
//            }.execute("");
//
//        }
//
//        @Override
//        public int getItemCount() {
//            return moviesList.size();
//        }
//    }

    public  void onButtonClicked(View view){

        switch (view.getId()){

            case R.id.rl_refresh:
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 7);
                //  Toast.makeText(getBaseContext(),"You resolved the incident",Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(getBaseContext(), ViewIncidentReportActivity.class);
//                    intent.putExtra("GuardName",uservalue);
                //  updateResponse(Integer.valueOf(getIntent().getStringExtra("emergencyId")));
//                mySGList.clear();
//                getGPSPoints(IConstant.dateFormat_MDY.format(new Date()));
//                getAssociationList();

                break;
            case R.id.rl_Attend:

                saveGuardAction("Attended",gps.getLatitude()+","+gps.getLongitude(),EmergencyID);
                Log.v("LATLONG",gps.getLatitude()+","+gps.getLongitude()+", "+EmergencyID);
                dbh.updatesecuritynotification_setNotified(Nid);
                break;
            case R.id.rl_pass:

                saveGuardAction("Pass",gps.getLatitude()+","+gps.getLongitude(),EmergencyID);
                dbh.updatesecuritynotification_setNotified(Nid);
                Prefs.putBoolean(EMERGENCY_SOUND_ON,false);
                finish();
                break;
        }
    }

    private void saveGuardAction(String actionName,String gpsPoint,int emergencyId) {

        RestClient restClient = RestClient.getInstance();

        CreateTicketingActionReq loginReq = new CreateTicketingActionReq();

        int memID=64;
        if(!BASE_URL.equalsIgnoreCase("dev")){
            memID=410;
        }

        loginReq.ASAssnID= Prefs.getInt(ASSOCIATION_ID,0);
        loginReq.MEMemID=memID+"";
        loginReq.TRGPSPnt=gpsPoint;
        loginReq.WKWorkID= LocalDb.getStaffList().get(0).getWkWorkID();
        loginReq.TKTktID = emergencyId ;
        loginReq.TRDateT= DateTimeUtils.getCurrentTimeLocal();
        loginReq.TRDetails=actionName;

        Log.d("saveCheckPoints","StaffEntry "+loginReq.ASAssnID+" "+loginReq.TRDateT+" "
                +loginReq.WKWorkID+" "+loginReq.TKTktID+" "+loginReq.TRDetails );

        restClient.addHeader(OYE247KEY, OYE247TOKEN);
        restClient.post(this, loginReq, CreateTicketingActionResp.class, this, URLData.URL_CREATE_TICKETING_RESPONSE);

        if(actionName.equals("Attended")){
//            FCMApiInterface apiService = FCMApiClient.getClient().create(FCMApiInterface.class);
//
//            AttendEmergencyPayload payloadData=new AttendEmergencyPayload("emergencyResponse",
//                    prefManager.getFirstName()+" "+prefManager.getLastName()+" is coming to your help. ",
//                    prefManager.getCountryCode()+prefManager.getMobileNo(),String.valueOf(gps.getLatitude()+","+gps.getLongitude()),prefManager.getAssociationId());
//            AttendEmergencyReq sendOTPRequest=new AttendEmergencyReq(payloadData,"/topics/Phone"+intent.getStringExtra("mob"));
//            Call<SendFCMResponse> call = apiService.sendAttendEmergency(sendOTPRequest);
//
//            call.enqueue(new Callback<SendFCMResponse>() {
//                @Override
//                public void onResponse(Call<SendFCMResponse> call, Response<SendFCMResponse> response) {
//                    Log.d("TAG", "Number of movies received: " + response.body().getMessage_id());
//                    if(response.body().getMessage_id()!=null){
//                        Toast.makeText(context,Notified,Toast.LENGTH_SHORT).show();
//
//                    }else{
//                        Toast.makeText(context,Failed_to_Notify,Toast.LENGTH_SHORT).show();
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<SendFCMResponse> call, Throwable t) {
//                     Log error here since request failed
//                    Log.d("TAG", t.toString());
//                }
//            });

            Intent intentAction1 =new Intent(getApplicationContext(), BackgroundSyncReceiver.class);
            intentAction1.putExtra(ConstantUtils.BSR_Action, "sendFCM_toStopEmergencyAlert");
            sendBroadcast(intentAction1);

        }

    }

    @Override
    public void onFailure(Exception e, int urlId) {

        showToast(this, e.getMessage()+" id "+urlId);
    }

    @Override
    public void onSuccess(String responce, Object data, int urlId, int position) {

        if (urlId == URLData.URL_CREATE_TICKETING_RESPONSE.getUrlId()) {

            CreateTicketingActionResp loginDetailsResponce = (CreateTicketingActionResp) data;
            if (loginDetailsResponce != null) {
                Log.d("str3 Service", "saveCheckPoints: " + urlId+" id "+position+" "+" "+" "+loginDetailsResponce.toString());
                if(loginDetailsResponce.success.equalsIgnoreCase("true")) {
                    showToast(this, "Saved");
                    finish();
                }else{
                    showToast(this, " not saved ");
                }

            } else {
                showToast(this, "Something went wrong . please try again Service");
            }

        }

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(getIntent().getStringExtra("gps")==null) {

        }else{
            try {
                String[] loccc = null;
                loccc = getIntent().getStringExtra("gps").split(",");
                Log.d("latlon ", getIntent().getStringExtra("gps") + "");
                patrollingRoute.clear();
                LatLng tempLatLong = new LatLng(Double.parseDouble(loccc[0].trim()), Double.parseDouble(loccc[1].trim()));
                patrollingRoute.add(tempLatLong);

//            mMap.clear();
                PolylineOptions lineOptions = null;
                lineOptions = new PolylineOptions();

                for (int i = 0; i < patrollingRoute.size(); i++) {
                    mMap.addMarker(new MarkerOptions()
                            .position(patrollingRoute.get(i))
                            .title("Marker" + i));
                }
                Log.d("latlon ", patrollingRoute.size() + "");
                lineOptions.addAll(patrollingRoute);
                lineOptions.width(5);
                lineOptions.color(Color.MAGENTA);

                if (lineOptions != null) {
                    mMap.addPolyline(lineOptions);
                }

                for (int i = 0; i < patrollingRoute.size(); i++) {
                    mMap.addMarker(new MarkerOptions()
                            .position(patrollingRoute.get(i))
                            .title("Marker" + i));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(patrollingRoute.get(i), 20));

                }
            }catch (Exception e)
            {

            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadincidentimages();
        getTicketList();
    }

    void getTicketList(){

        Call<TicketListingTesponse> call = champApiInterface.getTicketingListResponse(EmergencyID);
        call.enqueue(new Callback<TicketListingTesponse>() {
            @Override
            public void onResponse(Call<TicketListingTesponse> call, Response<TicketListingTesponse> response) {


                if (response.body().getSuccess() == true) {

                    if(response.body().getData()!=null) {

                        Toast.makeText(TicketingDetailsActivity.this, response.body().toString(), Toast.LENGTH_LONG).show();
                        System.out.println("SSSSOS" + response.body().getData().toString());

                         tv_name.setText(response.body().getData().getTicketing().getTkRaisdBy());

                        tv_mobileno.setText(response.body().getData().getTicketing().getTkMobile());

                    }
                } else {
                    Toast.makeText(TicketingDetailsActivity.this, "No Data", Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(Call<TicketListingTesponse> call, Throwable t) {
                call.cancel();
            }
        });


    }

}
