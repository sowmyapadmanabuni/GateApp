package com.oyespace.guards.guest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;
import com.oyespace.guards.R;
import com.oyespace.guards.network.ChampApiClient;
import com.oyespace.guards.network.ChampApiInterface;
import com.oyespace.guards.qrscanner.BaseScannerActivity;
import com.oyespace.guards.qrscanner.VehicleGuestQRRegistration;
import com.oyespace.guards.request.InvitationUpdateReq;
import com.oyespace.guards.responce.InvitationRequestResponse;
import com.oyespace.guards.utils.Prefs;

import java.util.Locale;

import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.core.ViewFinderView;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.oyespace.guards.constants.PrefKeys.LANGUAGE;
import static com.oyespace.guards.utils.ConstantUtils.ASSOCIATION_ID;
import static com.oyespace.guards.utils.ConstantUtils.COMPANY_NAME;
import static com.oyespace.guards.utils.ConstantUtils.COUNTRYCODE;
import static com.oyespace.guards.utils.ConstantUtils.FLOW_TYPE;
import static com.oyespace.guards.utils.ConstantUtils.FROMDATE;
import static com.oyespace.guards.utils.ConstantUtils.FROMTIME;
import static com.oyespace.guards.utils.ConstantUtils.GUEST;
import static com.oyespace.guards.utils.ConstantUtils.INVITATIONID;
import static com.oyespace.guards.utils.ConstantUtils.MOBILENUMBER;
import static com.oyespace.guards.utils.ConstantUtils.NUMBEROFPERSONS;
import static com.oyespace.guards.utils.ConstantUtils.PERSONNAME;
import static com.oyespace.guards.utils.ConstantUtils.TODATE;
import static com.oyespace.guards.utils.ConstantUtils.UNITID;
import static com.oyespace.guards.utils.ConstantUtils.UNITNAME;
import static com.oyespace.guards.utils.ConstantUtils.VEHICLENUMBER;
import static com.oyespace.guards.utils.ConstantUtils.VEHICLE_GUESTWITHQRCODE;
import static com.oyespace.guards.utils.ConstantUtils.VISITOR_TYPE;
import static com.oyespace.guards.utils.DateTimeUtils.compareDate;
import static com.oyespace.guards.utils.RandomUtils.entryExists;

public class GuestCustomViewFinderScannerActivity extends BaseScannerActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    private Button noQrcodeButton;
    private TextView dataTextView;
    androidx.appcompat.app.AlertDialog alertDialog;
    ChampApiInterface champApiInterface;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setLocale(Prefs.getString(LANGUAGE,null));
        setContentView(R.layout.activity_custom_view_finder_scanner);
        champApiInterface = ChampApiClient.getClient().create(ChampApiInterface.class);
        noQrcodeButton = findViewById(R.id.button2);
        dataTextView =findViewById(R.id.scaneddata);
        noQrcodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(GuestCustomViewFinderScannerActivity.this,"NO QR Code ",Toast.LENGTH_LONG).show();
                Intent in = new Intent(GuestCustomViewFinderScannerActivity.this, GuestBlockSelectionActivity.class);
                startActivity(in);
                finish();

            }
        });

       // setupToolbar();

        ViewGroup contentFrame = findViewById(R.id.content_frame);
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
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
//        Toast.makeText(this, "Contents = " + rawResult.getText() +
//                ", Format = " + rawResult.getBarcodeFormat().toString(), Toast.LENGTH_SHORT).show();
        Log.d("Contents resident", rawResult.getText());

        String guestdata = rawResult.getText();
        if(guestdata.contains(",")) {
            final String[] guestdataList = guestdata.split(",");
          //  System.out.println("Guest Data " + guestdataList[0] + " " + " " + guestdataList[1] + " " + guestdataList[2] + " " + guestdataList[3] + " " + guestdataList[5]);

            dataTextView.setText(rawResult.getText());

            if(guestdataList.length>12) {
                System.out.println("Guest Data 2 " + guestdataList[6] + " "+ guestdataList[7]  + " " + guestdataList[8] + " " + guestdataList[9] + " " + guestdataList[10] + " " + guestdataList[11]);

                if(guestdataList[11].equalsIgnoreCase( Prefs.getInt(ASSOCIATION_ID,0)+"")) {
                    if(!compareDate(guestdataList[6],guestdataList[10])){
                        Toast.makeText(GuestCustomViewFinderScannerActivity.this, "You are/were Invited from "+guestdataList[6]+" to "+guestdataList[10], Toast.LENGTH_LONG).show();
                    }else  if(entryExists(guestdataList[1].replace("+",""),guestdataList[2])) {


//                        Toast.makeText(this,"Mobile Number already used for Visitor Entry", Toast.LENGTH_SHORT).show()
                        AlertDialog.Builder builder =new  AlertDialog.Builder(GuestCustomViewFinderScannerActivity.this);
                        // builder.setTitle("Entry already done");
                        builder.setMessage("This number is being used by a person already in");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.setCancelable(false);
                        builder.show();
                    } else if ((guestdataList[12].equalsIgnoreCase("false"))) {

                        getInviation(Integer.parseInt(guestdataList[3]), guestdataList[0], guestdataList[1], guestdataList[2], guestdataList[4], guestdataList[5], guestdataList[6], guestdataList[7], guestdataList[8], guestdataList[9], guestdataList[10], guestdataList[11]);
                    } else {
                        //Toast.makeText(GuestCustomViewFinderScannerActivity.this, "Valid Invitation", Toast.LENGTH_LONG).show();

                        ViewGroup viewGroup = findViewById(android.R.id.content);

                        View dialogView = LayoutInflater.from(GuestCustomViewFinderScannerActivity.this).inflate(R.layout.layout_qrcodedailog, viewGroup, false);


                        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(GuestCustomViewFinderScannerActivity.this);

                        ImageView dialog_imageview = dialogView.findViewById(R.id.dialog_imageview);
                        TextView tv_msg = dialogView.findViewById(R.id.tv_msg);
                        tv_msg.setText("Valid Invitation");
                        Drawable drawable = getResources().getDrawable(R.drawable.valid_invi);
                        dialog_imageview.setImageDrawable(drawable);
                        Button btn_ok = dialogView.findViewById(R.id.btn_ok);
                        btn_ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                                Intent intent = new Intent(GuestCustomViewFinderScannerActivity.this, VehicleGuestQRRegistration.class);
                                intent.putExtra(FLOW_TYPE, VEHICLE_GUESTWITHQRCODE);
                                intent.putExtra(VISITOR_TYPE, GUEST);
                                intent.putExtra(COMPANY_NAME, GUEST);
                                intent.putExtra(PERSONNAME, guestdataList[0]);
                                intent.putExtra(COUNTRYCODE, guestdataList[1]);
                                intent.putExtra(MOBILENUMBER, guestdataList[2]);
                                intent.putExtra(INVITATIONID, guestdataList[3]);
                                intent.putExtra(UNITID, guestdataList[4]);
                                intent.putExtra(UNITNAME, guestdataList[5]);
                                intent.putExtra(FROMDATE, guestdataList[6]);
                                intent.putExtra(FROMTIME, guestdataList[7]);
                                intent.putExtra(VEHICLENUMBER, guestdataList[8]);
                                intent.putExtra(NUMBEROFPERSONS, guestdataList[9]);
                                intent.putExtra(TODATE, guestdataList[10]);
                                intent.putExtra("Association Id", guestdataList[11]);
                                startActivity(intent);
                                finish();

                            }
                        });

                        builder.setView(dialogView);

                        //finally creating the alert dialog and displaying it
                        alertDialog = builder.create();

                        alertDialog.show();

                    }
                }else{

                    ViewGroup viewGroup = findViewById(android.R.id.content);

                    View dialogView = LayoutInflater.from(GuestCustomViewFinderScannerActivity.this).inflate(R.layout.layout_qrcodedailog, viewGroup, false);


                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(GuestCustomViewFinderScannerActivity.this);

                    ImageView dialog_imageview = dialogView.findViewById(R.id.dialog_imageview);
                    Drawable drawable = getResources().getDrawable(R.drawable.invalid_invi);
                    dialog_imageview.setImageDrawable(drawable);
                    TextView tv_msg = dialogView.findViewById(R.id.tv_msg);
                    tv_msg.setText("Invalid invitation for this location");
                    Button btn_ok = dialogView.findViewById(R.id.btn_ok);
                    btn_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
//
                            finish();

                        }
                    });

                    builder.setView(dialogView);

                    //finally creating the alert dialog and displaying it
                    alertDialog = builder.create();

                    alertDialog.show();

                    // Toast.makeText(GuestCustomViewFinderScannerActivity.this,"Not Invited for this Association/Society",Toast.LENGTH_LONG).show();
                }
            }else{

                ViewGroup viewGroup = findViewById(android.R.id.content);

                View dialogView = LayoutInflater.from(GuestCustomViewFinderScannerActivity.this).inflate(R.layout.layout_qrcodedailog, viewGroup, false);


                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(GuestCustomViewFinderScannerActivity.this);

                ImageView dialog_imageview = dialogView.findViewById(R.id.dialog_imageview);
                Drawable drawable = getResources().getDrawable(R.drawable.invalid_invi);
                dialog_imageview.setImageDrawable(drawable);
                TextView tv_msg = dialogView.findViewById(R.id.tv_msg);
                tv_msg.setText("Invalid QR Code");
                Button btn_ok = dialogView.findViewById(R.id.btn_ok);
                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();

                        finish();

                    }
                });

                builder.setView(dialogView);

                //finally creating the alert dialog and displaying it
                alertDialog = builder.create();

                alertDialog.show();

                // Toast.makeText(GuestCustomViewFinderScannerActivity.this,"Invalid QR Code",Toast.LENGTH_LONG).show();
            }
        }else{
            //Toast.makeText(GuestCustomViewFinderScannerActivity.this,"Invalid QR Code Data",Toast.LENGTH_LONG).show();
            ViewGroup viewGroup = findViewById(android.R.id.content);

            View dialogView = LayoutInflater.from(GuestCustomViewFinderScannerActivity.this).inflate(R.layout.layout_qrcodedailog, viewGroup, false);


            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(GuestCustomViewFinderScannerActivity.this);

            ImageView dialog_imageview = dialogView.findViewById(R.id.dialog_imageview);
            Drawable drawable = getResources().getDrawable(R.drawable.invalid_invi);
            dialog_imageview.setImageDrawable(drawable);
            TextView tv_msg = dialogView.findViewById(R.id.tv_msg);
            tv_msg.setText("Invalid QR Code Data");
            Button btn_ok = dialogView.findViewById(R.id.btn_ok);
            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();

                    finish();

                }
            });

            builder.setView(dialogView);

            //finally creating the alert dialog and displaying it
            alertDialog = builder.create();

            alertDialog.show();
        }
        Log.e("SCANNER",rawResult.getText());

        // Note:
        // * Wait 2 seconds to resume the preview.
        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
        // * I don't know why this is the case but I don't have the time to figure out.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(GuestCustomViewFinderScannerActivity.this);
            }
        }, 2000);

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        Intent d = new Intent(GuestCustomViewFinderScannerActivity.this, Dashboard.class);
//        startActivity(d);
        finish();
    }

//    void showDailog(){
//
//        ViewGroup viewGroup = findViewById(android.R.id.content);
//
//        View dialogView = LayoutInflater.from(GuestCustomViewFinderScannerActivity.this).inflate(R.layout.layout_qrcodedailog, viewGroup, false);
//
//
//        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(GuestCustomViewFinderScannerActivity.this);
//
//        ImageView dialog_imageview = dialogView.findViewById(R.id.dialog_imageview);
//        Button btn_ok=dialogView.findViewById(R.id.btn_ok);
//        btn_ok.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                alertDialog.dismiss();
//                Intent intent = new Intent(GuestCustomViewFinderScannerActivity.this, VehicleGuestQRRegistration.class);
//                intent.putExtra(FLOW_TYPE, VEHICLE_GUESTWITHQRCODE);
//                intent.putExtra(VISITOR_TYPE, GUEST);
//                intent.putExtra(COMPANY_NAME, GUEST);
//                intent.putExtra(PERSONNAME, guestdataList[0]);
//                intent.putExtra(COUNTRYCODE, guestdataList[1]);
//                intent.putExtra(MOBILENUMBER, guestdataList[2]);
//                intent.putExtra(INVITATIONID, guestdataList[3]);
//                intent.putExtra(UNITID, guestdataList[4]);
//                intent.putExtra(UNITNAME, guestdataList[5]);
//                intent.putExtra(FROMDATE, guestdataList[6]);
//                intent.putExtra(FROMTIME, guestdataList[7]);
//                intent.putExtra(VEHICLENUMBER, guestdataList[8]);
//                intent.putExtra(NUMBEROFPERSONS, guestdataList[9]);
//                intent.putExtra(TODATE, guestdataList[10]);
//                intent.putExtra("Association Id", guestdataList[11]);
//                startActivity(intent);
//                finish();
//
//            }
//        });
//
//        builder.setView(dialogView);
//
//        //finally creating the alert dialog and displaying it
//      alertDialog = builder.create();
//
//        alertDialog.show();
//    }

    void getInviation(final int invitationId, final String personname, final String countryCode, final String mobileNumber, final String unitID, final String unitName, final String fromDate, final String fromTime, final String vehicleNumber, final String numberofPersons, final String toDate, final String associationId) {


        Call<InvitationRequestResponse> call = champApiInterface.getInvitationResponse(invitationId);
        call.enqueue(new Callback<InvitationRequestResponse>() {
            @Override
            public void onResponse(Call<InvitationRequestResponse> call, Response<InvitationRequestResponse> response) {

                // if (response.body().getSuccess() == true) {

                if (response.body().getData() != null) {
                    Boolean val = response.body().getData().getInvitation().getInIsUsed();


                    if (response.body().getData().getInvitation().getInIsUsed() == false) {


                        ViewGroup viewGroup = findViewById(android.R.id.content);

                        View dialogView = LayoutInflater.from(GuestCustomViewFinderScannerActivity.this).inflate(R.layout.layout_qrcodedailog, viewGroup, false);


                        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(GuestCustomViewFinderScannerActivity.this);

                        ImageView dialog_imageview = dialogView.findViewById(R.id.dialog_imageview);
                        TextView tv_msg = dialogView.findViewById(R.id.tv_msg);
                        tv_msg.setText("Valid Invitation");
                        Drawable drawable = getResources().getDrawable(R.drawable.valid_invi);
                        dialog_imageview.setImageDrawable(drawable);
                        Button btn_ok = dialogView.findViewById(R.id.btn_ok);
                        btn_ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                                Intent intent = new Intent(GuestCustomViewFinderScannerActivity.this, VehicleGuestQRRegistration.class);
                                intent.putExtra(FLOW_TYPE, VEHICLE_GUESTWITHQRCODE);
                                intent.putExtra(VISITOR_TYPE, GUEST);
                                intent.putExtra(COMPANY_NAME, GUEST);
                                intent.putExtra(PERSONNAME, personname);
                                intent.putExtra(COUNTRYCODE, countryCode);
                                intent.putExtra(MOBILENUMBER, mobileNumber);
                                intent.putExtra(INVITATIONID, invitationId);
                                intent.putExtra(UNITID, unitID);
                                intent.putExtra(UNITNAME, unitName);
                                intent.putExtra(FROMDATE, fromDate);
                                intent.putExtra(FROMTIME, fromTime);
                                intent.putExtra(VEHICLENUMBER, vehicleNumber);
                                intent.putExtra(NUMBEROFPERSONS, numberofPersons);
                                intent.putExtra(TODATE, toDate);
                                intent.putExtra("Association Id", associationId);
                                startActivity(intent);
                                finish();

                            }
                        });

                        builder.setView(dialogView);

                        //finally creating the alert dialog and displaying it
                        alertDialog = builder.create();

                        alertDialog.show();
                        //  invitationUpdate(invitationId, "True",personname,countryCode,mobileNumber,unitID,unitName,fromDate,fromTime,vehicleNumber,numberofPersons,toDate,associationId);
                    } else {
                        ViewGroup viewGroup = findViewById(android.R.id.content);

                        View dialogView = LayoutInflater.from(GuestCustomViewFinderScannerActivity.this).inflate(R.layout.layout_qrcodedailog, viewGroup, false);


                        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(GuestCustomViewFinderScannerActivity.this);

                        ImageView dialog_imageview = dialogView.findViewById(R.id.dialog_imageview);
                        Drawable drawable = getResources().getDrawable(R.drawable.invalid_invi);
                        dialog_imageview.setImageDrawable(drawable);
                        TextView tv_msg = dialogView.findViewById(R.id.tv_msg);
                        tv_msg.setText("Invalid QR Code");
                        Button btn_ok = dialogView.findViewById(R.id.btn_ok);
                        btn_ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();

                                finish();

                            }
                        });

                        builder.setView(dialogView);

                        //finally creating the alert dialog and displaying it
                        alertDialog = builder.create();

                        alertDialog.show();
                    }

                    //  }
                } else {
                    // Toast.makeText(GuestCustomViewFinderScannerActivity.this, "NoData", Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(Call<InvitationRequestResponse> call, Throwable t) {
                call.cancel();
            }
        });


    }

    void invitationUpdate(final int inviteID, String value, final String personname, final String countryCode, final String mobileNumber, final String unitID, final String unitName, final String fromDate, final String fromTime, final String vehicleNumber, final String numberofPersons, final String toDate, final String associationId) {

        InvitationUpdateReq invitationUpdateReq = new InvitationUpdateReq();

        invitationUpdateReq.iNInvtID = inviteID;
        invitationUpdateReq.iNIsUsed = value;

        Call<InvitationRequestResponse> call = champApiInterface.updateInvitation(invitationUpdateReq);
        call.enqueue(new Callback<InvitationRequestResponse>() {
            @Override
            public void onResponse(Call<InvitationRequestResponse> call, Response<InvitationRequestResponse> response) {


                ViewGroup viewGroup = findViewById(android.R.id.content);

                View dialogView = LayoutInflater.from(GuestCustomViewFinderScannerActivity.this).inflate(R.layout.layout_qrcodedailog, viewGroup, false);


                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(GuestCustomViewFinderScannerActivity.this);

                ImageView dialog_imageview = dialogView.findViewById(R.id.dialog_imageview);
                TextView tv_msg = dialogView.findViewById(R.id.tv_msg);
                tv_msg.setText("Valid Invitation");
                Drawable drawable = getResources().getDrawable(R.drawable.valid_invi);
                dialog_imageview.setImageDrawable(drawable);
                Button btn_ok = dialogView.findViewById(R.id.btn_ok);
                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        Intent intent = new Intent(GuestCustomViewFinderScannerActivity.this, VehicleGuestQRRegistration.class);
                        intent.putExtra(FLOW_TYPE, VEHICLE_GUESTWITHQRCODE);
                        intent.putExtra(VISITOR_TYPE, GUEST);
                        intent.putExtra(COMPANY_NAME, GUEST);
                        intent.putExtra(PERSONNAME, personname);
                        intent.putExtra(COUNTRYCODE, countryCode);
                        intent.putExtra(MOBILENUMBER, mobileNumber);
                        intent.putExtra(INVITATIONID, inviteID);
                        intent.putExtra(UNITID, unitID);
                        intent.putExtra(UNITNAME, unitName);
                        intent.putExtra(FROMDATE, fromDate);
                        intent.putExtra(FROMTIME, fromTime);
                        intent.putExtra(VEHICLENUMBER, vehicleNumber);
                        intent.putExtra(NUMBEROFPERSONS, numberofPersons);
                        intent.putExtra(TODATE, toDate);
                        intent.putExtra("Association Id", associationId);
                        startActivity(intent);
                        finish();

                    }
                });

                builder.setView(dialogView);

                //finally creating the alert dialog and displaying it
                alertDialog = builder.create();

                alertDialog.show();

            }

            @Override
            public void onFailure(Call<InvitationRequestResponse> call, Throwable t) {
                Toast.makeText(GuestCustomViewFinderScannerActivity.this, t.toString(), Toast.LENGTH_LONG).show();
                call.cancel();
            }
        });

    }

}
