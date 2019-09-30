package com.oyespace.guards.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.oyespace.guards.BackgroundSyncReceiver;
import com.oyespace.guards.pojo.SOSUpdateReq;
import com.oyespace.guards.utils.ConstantUtils;
import com.oyespace.guards.utils.NetworkUtil;
import com.oyespace.guards.utils.Prefs;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {

        int status = NetworkUtil.getConnectivityStatusString(context);
        Log.e("NETWORK_STAT", ""+status);
        if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
            if (status == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
               // new ForceExitPause(context).execute();
                Log.e("STATUS","OFFLINE");
            } else {
                Log.e("STATUS","ONLINE");
                String sos = Prefs.getString("PENDING_SOS","");
                Log.e("PENIDN",""+sos);
                if(!sos.equals("") && sos != null){

                    try{
                        SOSUpdateReq sosUpdateReq = new Gson().fromJson(sos,SOSUpdateReq.class);
                        Intent intentAction1 = new Intent(context, BackgroundSyncReceiver.class);

                        intentAction1.putExtra(
                                ConstantUtils.BSR_Action,
                                ConstantUtils.BGS_SOS_STATUS
                        );
                        intentAction1.putExtra("sos_id", sosUpdateReq.getSOSID());
                        intentAction1.putExtra("sos_status", ""+sosUpdateReq.getSOStatus());
                        context.sendBroadcast(intentAction1);
                    }catch (Exception e){
                        Log.e("NETWRERR",""+e);
                        e.printStackTrace();
                    }


                }

            }
        }
    }
}