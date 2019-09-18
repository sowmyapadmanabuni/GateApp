package com.oyespace.guards.vehicle_others;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.oyespace.guards.R;

public class VehicleOthersViewFullImageActivity extends AppCompatActivity {

    ImageView iv_full;
    int pos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_full_image);

        pos=getIntent().getIntExtra("pos",0);
        Log.i("List", "Passed Array List :: " + pos);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
       viewPager.setAdapter(new VehicleOthersViewPagerAdapter(this));
       viewPager.setCurrentItem(pos);

    }

}
