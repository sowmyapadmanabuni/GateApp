package com.oyespace.guards.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.oyespace.guards.R;
import com.oyespace.guards.SGPatrollingService;
import com.oyespace.guards.pertroling.PScheduleListActivity;
import com.oyespace.guards.utils.ConstantUtils;
import com.oyespace.guards.utils.Prefs;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class PatrollingAlert extends AppCompatActivity {


    TextView mMessage;
    LottieAnimationView mAnim;
    Button mBtn;
    int scheduleId = 0;
    Runnable runnable;
    Handler handler = new android.os.Handler();

    @Override
    protected void onStart() {
        Prefs.putBoolean(ConstantUtils.ACTIVE_ALERT,true);
        startSiren();
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        Prefs.putBoolean(ConstantUtils.ACTIVE_ALERT,false);
        stopSiren();
        try {
            handler.removeCallbacks(runnable);
        }catch (Exception e){
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patrolling_alert);

        mMessage = findViewById(R.id.alert_dlg_text);
        mAnim = findViewById(R.id.alert_dlg_lottie);
        mBtn = findViewById(R.id.alert_dlg_btn);


        String message = getIntent().getStringExtra("MSG");
        String btnText = getIntent().getStringExtra("BTN_TEXT");
        int anim = getIntent().getIntExtra("ANIM",R.raw.error);
        scheduleId = getIntent().getIntExtra("SCHEDULEID",0);

        mMessage.setText(message);
        mBtn.setText(btnText);
        mAnim.setAnimation(anim);

        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mBtn.getText().toString().contains("Snooze")){
                    int snoozed = 0;
                    try {
                        snoozed = Prefs.getInt(ConstantUtils.SNOOZE_COUNT + scheduleId, 0);
                    }catch (Exception e){
                        e.printStackTrace();
                        stopSiren();
                    }
                    snoozed+=1;

                    Prefs.putBoolean(ConstantUtils.SNOOZE_IS_ACTIVE+scheduleId,true);
                    Prefs.putInt(ConstantUtils.SNOOZE_COUNT+scheduleId,snoozed);
                    Prefs.putString(ConstantUtils.SNOOZE_TIME+scheduleId,new SimpleDateFormat("dd/MM/yyyy hh:mm:ss").format(new Date()));
                    stopSiren();
                    Toast.makeText(PatrollingAlert.this,"Snoozed for next 5 minutes",Toast.LENGTH_SHORT).show();
                }else{
                    Intent patrolIntent = new Intent(PatrollingAlert.this, PScheduleListActivity.class);
                    startActivity(patrolIntent);
                }
                Prefs.putBoolean(ConstantUtils.ACTIVE_ALERT,false);
                finish();
            }
        });

//
//        runnable = new Runnable() {
//            public void run() {
//                finish();
//            }
//        };
//        handler = new android.os.Handler();
//        handler.postDelayed(runnable, 25000);
    }

    public void startSiren(){
        Intent intent = new Intent(PatrollingAlert.this, SGPatrollingService.class);
        this.startService(intent);
    }

    public void stopSiren(){
        Intent intent = new Intent(PatrollingAlert.this, SGPatrollingService.class);
        this.stopService(intent);
    }


    public void exit(){

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
