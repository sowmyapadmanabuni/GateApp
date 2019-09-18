package com.oyespace.guards.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.oyespace.guards.listeners.Common


class SMSListener : BroadcastReceiver() {

    var mListener: Common.OTPListener? = null
    override fun onReceive(context: Context, intent: Intent) {

//        val bundle = intent.extras
//        var smsm: Array<SmsMessage>? = null
//        var sms_str = ""
//
//        if (bundle != null) {
//            // Get the SMS message
//            val pdus = bundle.get("pdus") as Array<Any>
//            smsm = arrayOfNulls(pdus.size)
//
//            for (i in smsm.indices) {
//                smsm[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray)
//
//                sms_str += "\r\nMessage: "
//                sms_str += smsm[i].messageBody.toString()
//                sms_str += "\r\n"
//
//                val Sender = smsm[i].originatingAddress
//                //Check here sender is yours
//                val smsIntent = Intent("otp")
//                smsIntent.putExtra("message", sms_str)
//
//                LocalBroadcastManager.getInstance(context).sendBroadcast(smsIntent)
//
//            }
//        }
//    }




    }


}
