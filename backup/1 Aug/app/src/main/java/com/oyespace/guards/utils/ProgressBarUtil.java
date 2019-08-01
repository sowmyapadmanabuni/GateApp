package com.oyespace.guards.utils;


import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.oyespace.guards.R;


/**
 * Class used for orientation lock during Async task
 */
public class ProgressBarUtil {

    private Dialog progressSend = null;


    public void showProgress(Context mActivity, String msg) {
        try {
            if (isShowing()) {
                dismissProgress();
            }
            progressSend = create(mActivity, msg);
            progressSend.setCancelable(false);
            WindowManager.LayoutParams params = progressSend.getWindow()
                    .getAttributes();
            if (mActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                params.y = 200;
                params.x = 0;
            } else {
                params.y = 120;
                params.x = 0;
            }

            progressSend.getWindow().setAttributes(params);
            progressSend.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dismissProgress() {
        try {
            if (null != progressSend && progressSend.isShowing())
                progressSend.cancel();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean isShowing() {
        try {
            if (progressSend != null) {
                return progressSend.isShowing();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public static Dialog create(Context context,
                                String mesg) {
        final Dialog dialog = new Dialog(context, R.style.Base_Theme_AppCompat_Dialog);

        LinearLayout layout = new LinearLayout(context);

        layout.setBackgroundResource(R.drawable.circle_bg);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(params);

        View progressview = LayoutInflater.from(context).inflate(R.layout.loading_view, null);
        layout.addView(progressview);
        TextView tv = new TextView(context);
        //tv.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Bold.ttf"));
        tv.setLayoutParams(params);
        tv.setTextColor(context.getResources().getColor(R.color.app_theme_color));
        tv.setTextSize(1, 14f);
        tv.setText(mesg);
        layout.addView(tv);
        dialog.setContentView(layout);

        return dialog;
    }
}