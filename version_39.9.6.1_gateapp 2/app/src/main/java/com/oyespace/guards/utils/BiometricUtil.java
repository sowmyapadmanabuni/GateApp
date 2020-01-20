package com.oyespace.guards.utils;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

import SecuGen.FDxSDKPro.JSGFPLib;
import SecuGen.FDxSDKPro.SGAutoOnEventNotifier;
import SecuGen.FDxSDKPro.SGDeviceInfoParam;
import SecuGen.FDxSDKPro.SGFDxConstant;
import SecuGen.FDxSDKPro.SGFDxDeviceName;
import SecuGen.FDxSDKPro.SGFDxErrorCode;
import SecuGen.FDxSDKPro.SGFDxTemplateFormat;
import SecuGen.FDxSDKPro.SGFingerInfo;

import static SecuGen.FDxSDKPro.SGFDxErrorCode.SGFDX_ERROR_EXTRACT_FAIL;

public class BiometricUtil {

    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                }
            }

            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                Log.e("taaag", "usb attached");
            }

        }
    };
    private Context context;
    private JSGFPLib jlib;
    private SGAutoOnEventNotifier autoOn;
    private int mImageWidth = 0;
    private int mImageHeight = 0;
    private int mImageDPI = 0;
    private int[] mMaxTemplateSize = new int[1];
    private byte[] mVerifyTemplate;

    public BiometricUtil(Context context) {
        this.context = context;
      //  jlib = new JSGFPLib(context, (UsbManager) context.getSystemService(Context.USB_SERVICE));
        autoOn = new SGAutoOnEventNotifier(jlib, () -> {

            Log.i("taaag", "detected");
//            Toast.makeText(context, "Detected", Toast.LENGTH_SHORT).show();

        });

        init();

    }

    public void init() {

        jlib.Init(SGFDxDeviceName.SG_DEV_AUTO);
        long error = jlib.OpenDevice(0);
        if (error == SGFDxErrorCode.SGFDX_ERROR_NONE) {

            Log.i("taaag", "no error detecing biometric");

            jlib.SetTemplateFormat(SGFDxTemplateFormat.TEMPLATE_FORMAT_SG400);
            jlib.GetMaxTemplateSize(mMaxTemplateSize);

            SGDeviceInfoParam deviceInfo = new SGDeviceInfoParam();
            jlib.GetDeviceInfo(deviceInfo);
            mImageWidth = deviceInfo.imageWidth;
            mImageHeight = deviceInfo.imageHeight;
            mImageDPI = deviceInfo.imageDPI;


            mVerifyTemplate = new byte[mMaxTemplateSize[0]];

            jlib.WriteData(SGFDxConstant.WRITEDATA_COMMAND_ENABLE_SMART_CAPTURE, (byte) 1);

        }

        autoOn.start();

    }

    private byte[] getFingerprintFromScanner() {


        int[] templateSize = new int[1];
        jlib.GetMaxTemplateSize(templateSize);
        byte[] fpTemp = new byte[templateSize[0]];
        byte[] fpImg = new byte[mImageWidth * mImageHeight];
        long code = 0;
        try {
            jlib.GetImageEx(fpImg, 10000, 50);
            code = jlib.CreateTemplate(new SGFingerInfo(), fpImg, fpTemp);
        } catch (NullPointerException ignored) {
            return null;
        }
        if (code == SGFDX_ERROR_EXTRACT_FAIL) {
            return null;
        }

        return fpTemp;

    }

    public void close() {

        autoOn.stop();
        jlib.Close();

    }


    public interface OnFingerPrintCaptureListener {
        void onFingerPrintCapture(byte[] fingerData);
    }

}
