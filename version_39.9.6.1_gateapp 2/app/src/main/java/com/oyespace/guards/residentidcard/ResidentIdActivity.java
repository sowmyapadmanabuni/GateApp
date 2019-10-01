package com.oyespace.guards.residentidcard;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
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
import com.oyespace.guards.request.ResidentValidationRequest;
import com.oyespace.guards.responce.ResidentValidationResponse;
import com.oyespace.guards.utils.Prefs;

import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.core.ViewFinderView;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.oyespace.guards.utils.ConstantUtils.ASSOCIATION_ID;

public class ResidentIdActivity extends BaseScannerActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;
    private Button btn_missedcall;
    AlertDialog alertDialog;
    ChampApiInterface champApiInterface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resident_id);

        champApiInterface = ChampApiClient.getClient().create(ChampApiInterface.class);

        btn_missedcall=findViewById(R.id.btn_missedcall);

        btn_missedcall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(ResidentIdActivity.this, ResidentIdCardMobileNumberActivity.class);
                startActivity(i);
                finish();

            }
        });
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
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {

        String residentdata = result.getText();


        if(residentdata.contains(";")) {
            final String[] residentdataList = residentdata.split(";");


            if(residentdataList.length>1) {

                if(residentdataList[1].equalsIgnoreCase( Prefs.getInt(ASSOCIATION_ID,0)+"")) {

                    System.out.println("Guest Data CustomViewFinderScannerActivity " + residentdataList[0]  );


                    getResidentValidation(residentdataList[0].toString(), Prefs.getInt(ASSOCIATION_ID, 0));
                }else {
                    ViewGroup viewGroup = findViewById(android.R.id.content);

                    View dialogView = LayoutInflater.from(ResidentIdActivity.this).inflate(R.layout.layout_qrcodedailog, viewGroup, false);


                   AlertDialog.Builder builder = new AlertDialog.Builder(ResidentIdActivity.this);

                    ImageView dialog_imageview = dialogView.findViewById(R.id.dialog_imageview);
                    Drawable drawable  = getResources().getDrawable(R.drawable.invalid_invi);
                    dialog_imageview.setImageDrawable(drawable);
                    TextView tv_msg=dialogView.findViewById(R.id.tv_msg);
                    tv_msg.setText("Invalid ");
                    Button btn_ok=dialogView.findViewById(R.id.btn_ok);
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
                }
            }
            }else{
                ViewGroup viewGroup = findViewById(android.R.id.content);

                View dialogView = LayoutInflater.from(ResidentIdActivity.this).inflate(R.layout.layout_qrcodedailog, viewGroup, false);


                AlertDialog.Builder builder = new AlertDialog.Builder(ResidentIdActivity.this);

                ImageView dialog_imageview = dialogView.findViewById(R.id.dialog_imageview);
                Drawable drawable  = getResources().getDrawable(R.drawable.invalid_invi);
                dialog_imageview.setImageDrawable(drawable);
                TextView tv_msg=dialogView.findViewById(R.id.tv_msg);
                tv_msg.setText("Invalid");
                Button btn_ok=dialogView.findViewById(R.id.btn_ok);
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

            }



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

    void getResidentValidation( String mobileNumber,int associationId) {


        ResidentValidationRequest residentValidationRequest = new ResidentValidationRequest();

        residentValidationRequest.MobileNumber = mobileNumber;
        residentValidationRequest.AssociationID =associationId;



        Call<ResidentValidationResponse> call = champApiInterface.residentValidation(residentValidationRequest);
        call.enqueue(new Callback<ResidentValidationResponse>() {
            @Override
            public void onResponse(Call<ResidentValidationResponse> call, Response<ResidentValidationResponse> response) {


                ViewGroup viewGroup = findViewById(android.R.id.content);

                View dialogView = LayoutInflater.from(ResidentIdActivity.this).inflate(R.layout.layout_qrcodedailog, viewGroup, false);


                AlertDialog.Builder builder = new AlertDialog.Builder(ResidentIdActivity.this);

                ImageView dialog_imageview = dialogView.findViewById(R.id.dialog_imageview);
                TextView tv_msg=dialogView.findViewById(R.id.tv_msg);
                tv_msg.setText("Valid");
                Drawable drawable  = getResources().getDrawable(R.drawable.valid_invi);
                dialog_imageview.setImageDrawable(drawable);
                Button btn_ok=dialogView.findViewById(R.id.btn_ok);
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

            @Override
            public void onFailure(Call<ResidentValidationResponse> call, Throwable t) {
              //  Toast.makeText(ResidentIdActivity.this,t.toString(),Toast.LENGTH_LONG).show();

                ViewGroup viewGroup = findViewById(android.R.id.content);

                View dialogView = LayoutInflater.from(ResidentIdActivity.this).inflate(R.layout.layout_qrcodedailog, viewGroup, false);


                AlertDialog.Builder builder = new AlertDialog.Builder(ResidentIdActivity.this);

                ImageView dialog_imageview = dialogView.findViewById(R.id.dialog_imageview);
                Drawable drawable  = getResources().getDrawable(R.drawable.invalid_invi);
                dialog_imageview.setImageDrawable(drawable);
                TextView tv_msg=dialogView.findViewById(R.id.tv_msg);
                tv_msg.setText("Invalid");
                Button btn_ok=dialogView.findViewById(R.id.btn_ok);
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
                call.cancel();
            }
        });

    }
}
