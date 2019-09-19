package com.oyespace.guards.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

import com.oyespace.guards.Myapp;
import com.oyespace.guards.R;
import com.oyespace.guards.databinding.LayoutQrcodedailogBinding;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;
import java.util.UUID;

public class Utils {


    public static String getPrice(String price) {
        String string = Myapp.getContext().getString(R.string.rupee);
        return string + " " + price;
    }

    public static String getPrice(int price) {
        String string = Myapp.getContext().getString(R.string.rupee);
        return string + " " + price;
    }

    public static String getRequestId() {
        return UUID.randomUUID().toString();
    }

    public static String getDeviceId(Context context) {
        String android_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return android_id;
    }


    public static void disable(View view) {
        if (view.isEnabled()) {
            view.setEnabled(false);
        }
        if (view.isClickable()) {
            view.setClickable(false);
        }
    }

    public static void enable(View view) {
        if (!view.isEnabled()) {
            view.setEnabled(true);
        }
        if (!view.isClickable()) {
            view.setClickable(true);
        }
    }

    public static int dpToPx(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * scale);
    }

    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static void logMessage(String className, String message) {
//        Crashlytics.log(Log.ERROR, className, message);
    }

    public static void logCrash(Exception e) {
//        if (e != null) {
//            Crashlytics.logException(e);
//            if (BuildConfig.DEBUG) {
//                e.printStackTrace();
//            }
//        }
    }

    public static String getVersionName(Context ctx) {
        try {
            PackageInfo pInfo = ctx.getPackageManager().getPackageInfo(
                    ctx.getPackageName(), 0);
            String version = pInfo.versionName;
            return version;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }

    }

    public static float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public static float sp2px(Resources resources, float sp) {
        final float scale = resources.getDisplayMetrics().scaledDensity;
        return sp * scale;
    }

    public static boolean isOnline(Context context) {
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo() != null;
        }
        return false;
    }


    public static void hideKeyboard(Activity ctx) {
        // Check if no view has focus:
        View view = ctx.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
            //inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    public static boolean isValidemail(String text) {
        if (TextUtils.isEmpty(text) || text.length() > 255) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(text).matches();
        }
    }


    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }


    public static String getMimeType(String mPath) {

        if (!mPath.endsWith(".flv")) {
            String mimeType = "";
            Uri uri = Uri.fromFile(new File(mPath));
            if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
                ContentResolver cr = Myapp.getContext().getContentResolver();
                mimeType = cr.getType(uri);
            } else {
                String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                        .toString());
                if (TextUtils.isEmpty(fileExtension)) {
                    String filePath = uri.toString();
                    //mimeType = filePath.substring(filePath.lastIndexOf(".") + 1);
                    mimeType = getType(filePath);
                } else {
                    mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                            fileExtension.toLowerCase());
                }
            }
            return mimeType;
        } else {
            return "audio/flv";
        }
    }

    /**
     * Determines the MIME type for a given filename.
     *
     * @param filename The file to determine the MIME type of.
     * @return The MIME type of the file, or a wildcard if none could be
     * determined.
     */
    public static String getType(final String filename) {
        // There does not seem to be a way to ask the OS or file itself for this
        // information, so unfortunately resorting to extension sniffing.
        int pos = filename.lastIndexOf('.');
        if (pos != -1) {
            String ext = filename.substring(filename.lastIndexOf('.') + 1,
                    filename.length());
            //Video mime types
            if (ext.equalsIgnoreCase("mp4"))
                return "video/mp4";
            if (ext.equalsIgnoreCase("avi"))
                return "video/x-msvideo";
            if (ext.equalsIgnoreCase("wmv"))
                return "video/x-ms-wmv";

            //image mime types
            if (ext.equalsIgnoreCase("png"))
                return "image/png";
            if (ext.equalsIgnoreCase("jpg"))
                return "image/jpeg";
            if (ext.equalsIgnoreCase("jpe"))
                return "image/jpeg";
            if (ext.equalsIgnoreCase("jpeg"))
                return "image/jpeg";
            if (ext.equalsIgnoreCase("gif"))
                return "image/gif";
        }
        return "*/*";
    }


    public static void showToast(Context ctx, String text) {
        Toast toast = Toast.makeText(ctx, text, Toast.LENGTH_SHORT);
//        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }

    public static AlertDialog getAlertDialog(Context context, String text, int drawable, View.OnClickListener onClickListener) {

        LayoutQrcodedailogBinding b = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_qrcodedailog, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setView(b.getRoot());
        b.tvMsg.setText(text);
        if (drawable == -1) {
            b.dialogImageview.setImageDrawable(context.getResources().getDrawable(R.drawable.valid_invi));
        } else {
            b.dialogImageview.setImageDrawable(context.getResources().getDrawable(drawable));
        }
        final AlertDialog alertDialog = builder.create();
        b.btnOk.setOnClickListener(v -> {
            alertDialog.dismiss();
            if (onClickListener != null) {
                onClickListener.onClick(v);
            }
        });

        return alertDialog;

    }


    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;


    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(extStorageState);
    }

    public static void createFolder(Context context) {
        if (isExternalStorageAvailable()) {
            File file = new File(Environment.getExternalStorageDirectory() + "/ProfilePics");
            if (!file.exists()) {
                if (file.mkdir()) ; //directory is created;

            }
        } else {
            File mydir = context.getDir("ProfilePics", Context.MODE_PRIVATE); //Creating an internal dir;
            if (!mydir.exists()) {
                mydir.mkdirs();

            }
        }

    }


    public static boolean isEmpty(List list) {
        return list == null || list.size() == 0;
    }

    public static Uri getTempFile(boolean isImg, Context mContext) {
        File root = new File(Environment.getExternalStorageDirectory(),
                Environment.DIRECTORY_PICTURES);
        if (!root.exists()) {
            root.mkdirs();
        }

        String filename = "" + System.currentTimeMillis();
        File file;
        if (isImg) {
            file = new File(root, filename + ".jpeg");
        } else {
            file = new File(root, filename + ".mp4");
        }
        Uri muri;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            muri = Uri.fromFile(file);
        } else {
            muri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".provider", file);
        }
        return muri;
    }


    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

    public static String getMiles(float meters) {
        double miles = meters * 0.000621371192;
        String format = new DecimalFormat("#0.0").format(miles);
        return format;
    }


    public static void showKeyboard(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public static boolean isNetworkAvailable(Context context) {
        if (context.checkCallingOrSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            if (connectivity == null) {
                Log.w("Utility", "couldn't get connectivity manager");
            } else {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();
                if (info != null) {
                    for (int i = 0; i < info.length; i++) {
                        if (info[i].isAvailable()) {
                            Log.d("Utility", "network is available");
                            return true;
                        }
                    }
                }
            }
        }
        Log.d("Utility", "network is not available");
        return false;
    }


//    public static boolean isLoggedIn() {
//        LoginDetails loginDetails = LocalDb.getLoginDetails();
//        return loginDetails != null;
//    }


//    public static void makeDeviceReginfoApi(String refreshedToken, Context context) {
//        UserDeviceDetailReq deviceDetail = new UserDeviceDetailReq();
//        deviceDetail.deviceplatform = "Android";
//        deviceDetail.devicename = Build.MODEL;
//        deviceDetail.deviceid = Utils.getDeviceId(context);
//        deviceDetail.registrationid = refreshedToken;
//        LoginDetails loginDetails = LocalDb.getLoginDetails();
//        if (loginDetails != null) {
//            deviceDetail.apikey = loginDetails.apikey;
//        } else {
//            deviceDetail.apikey = "";
//        }
//        RestClient restClient = RestClient.getInstance();
//        restClient.post(context, deviceDetail, SuccessDetails.class, new ResponseHandler() {
//            @Override
//            public void onSuccess(String response, Object data, int urlId, int position) {
//                SuccessDetails details = (SuccessDetails) data;
//                if (details != null && details.status == 1) {
//                    Log.d("Utils", "onSuccess: ");
//                }
//            }
//
//            @Override
//            public void onFailure(Exception e, int urlId) {
//
//            }
//        }, URLData.URL_ADD_DEVICEID);
//    }


}
