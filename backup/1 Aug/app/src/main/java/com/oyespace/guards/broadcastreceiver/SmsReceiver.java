package com.oyespace.guards.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import com.oyespace.guards.listeners.Common;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import org.jetbrains.annotations.NotNull;

public class SmsReceiver extends BroadcastReceiver {
    private static Common.OTPListener mListener;
    boolean b;
    String abcd,xyz;
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data  = intent.getExtras();
        Object[] pdus = (Object[]) data.get("pdus");
        for(int i=0;i<pdus.length;i++){
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
            String sender = smsMessage.getDisplayOriginatingAddress();
            // b=sender.endsWith("WNRCRP");  //Just to fetch otp sent from WNRCRP
            String messageBody = smsMessage.getMessageBody();
            abcd=messageBody.replaceAll("[^0-9]","");   // here abcd contains otp
            //Pass on the text to our listener.
            if(b==true) {
                mListener.onOTPReceived(abcd);  // attach value to interface

            }
            else
            {
            }
        }
    }
    public static void bindListener(Common.OTPListener listener) {
        mListener = listener;
    }


}
