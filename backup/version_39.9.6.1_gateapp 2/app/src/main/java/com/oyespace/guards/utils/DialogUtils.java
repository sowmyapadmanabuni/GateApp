package com.oyespace.guards.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.TextUtils;


/**
 * Created by kalyan pvs on 6/17/2016.
 */
public class DialogUtils {


    public static void showAlertDialog(Activity activity, String titletext, String posBtnTxt, String negBtntxt, String description, final DialogListener listener) {
        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);

            if (!TextUtils.isEmpty(titletext)) {
             alertDialogBuilder.setTitle(titletext);
            }

            if (!TextUtils.isEmpty(description)) {
                alertDialogBuilder.setMessage(description);
            }

            if (!TextUtils.isEmpty(negBtntxt)) {
                alertDialogBuilder.setNegativeButton(negBtntxt, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (listener != null) {
                            listener.onNegativeButtonClick();
                        }
                    }
                });
            }

            if (!TextUtils.isEmpty(posBtnTxt)) {
                alertDialogBuilder.setPositiveButton(posBtnTxt, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (listener != null) {
                            listener.onPositiveButtonClick();
                        }
                    }
                });
            }

            final AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialogBuilder.setCancelable(false);
            alertDialog.setCancelable(false);
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface DialogListener {
        void onPositiveButtonClick();

        void onNegativeButtonClick();
    }

}
