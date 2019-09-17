package com.oyespace.guards.pertroling;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class TimeSetter extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (true) {
//
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    final Intent mainIntent = new Intent(TimeSetter.this,ReciverTimmer.class );
                    TimeSetter.this.startActivity(mainIntent);
                    TimeSetter.this.finish();
                }
            }, 50000);


        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    final Intent mainIntent = new Intent(TimeSetter.this,ReciverTimmer.class);
                    TimeSetter.this.startActivity(mainIntent);
                    TimeSetter.this.finish();
                }
            }, 1*60 *500000000);
        }

    }
}
