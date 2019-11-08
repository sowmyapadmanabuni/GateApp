package com.oyespace.guards.resident;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.google.zxing.Result;
import com.oyespace.guards.R;
import com.oyespace.guards.com.oyespace.guards.resident.ResidentChecker;
import com.oyespace.guards.databinding.ActivityResidentIdBinding;
import com.oyespace.guards.qrscanner.BaseScannerActivity;
import com.oyespace.guards.utils.Utils;

import org.jetbrains.annotations.NotNull;

import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.core.ViewFinderView;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ResidentIdActivity extends BaseScannerActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityResidentIdBinding b = DataBindingUtil.setContentView(this, R.layout.activity_resident_id);

        ViewGroup contentFrame = findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this) {
            @Override
            protected IViewFinder createViewFinderView(Context context) {
                return new CustomViewFinderView(context);
            }
        };
        contentFrame.addView(mScannerView);

        b.toolbar.setVisibility(View.VISIBLE);
        b.toolbar.setText(getString(R.string.scan_res_id_here));

    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    public void onMissedCallPress(View view) {

        finish();
        startActivity(new Intent(this, ResidentMissedCallActivity.class));

    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @SuppressLint("CheckResult")
    @Override
    public void handleResult(Result result) {

        String[] qrData = result.getText().split(";");
        if (qrData.length < 2) {
            Utils.getAlertDialog(
                    ResidentIdActivity.this,
                    "Invalid QR code", R.drawable.invalid_invi,
                    v -> finish()).show();
            return;
        }

        String phone = qrData[0];
        String associateId = qrData[1];

        try {
            new ResidentChecker().isResident(phone, Integer.parseInt(associateId), new ResidentChecker.ResponseListener() {
                @Override
                public void onResult(boolean isResident) {
                    if (isResident) {
                        Utils.getAlertDialog(
                                ResidentIdActivity.this,
                                getString(R.string.valid), -1,
                                v -> finish()).show();
                    } else {
                        Utils.getAlertDialog(
                                ResidentIdActivity.this,
                                getString(R.string.invalid), R.drawable.invalid_invi,
                                v -> finish()).show();
                    }
                }

                @Override
                public void onError(@NotNull String error) {
                    Utils.getAlertDialog(
                            ResidentIdActivity.this,
                            getString(R.string.invalid), R.drawable.invalid_invi,
                            v -> finish()).show();
                }
            });
        } catch (Exception e) {
            Utils.getAlertDialog(
                    ResidentIdActivity.this,
                    getString(R.string.invalid), R.drawable.invalid_invi,
                    v -> finish()).show();
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
}
