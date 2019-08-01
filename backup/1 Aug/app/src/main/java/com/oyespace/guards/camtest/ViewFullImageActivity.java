package com.oyespace.guards.camtest;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import com.oyespace.guards.R;
import java.util.ArrayList;

public class ViewFullImageActivity extends AppCompatActivity {

    ImageView iv_full;
    int pos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_full_image);

        pos=getIntent().getIntExtra("pos",0);
        Log.i("List", "Passed Array List :: " + pos);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
       viewPager.setAdapter(new ViewPagerAdapter(this));
       viewPager.setCurrentItem(pos);

    }
}
