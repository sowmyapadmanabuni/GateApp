package com.oyespace.guards.camtest;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.oyespace.guards.R;

import java.util.ArrayList;

public class ViewFullImageActivity extends AppCompatActivity {

    ViewPagerAdapter viewPagerAdapter;
    ImageView iv_full;
    int pos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_full_image);

        pos=getIntent().getIntExtra("pos",0);
        Log.i("List", "Passed Array List :: " + pos);

        ViewPager viewPager = findViewById(R.id.viewpager);
        ArrayList<String> ar1=getIntent().getExtras().getStringArrayList("ImageList");

        Log.v("IMAGESDATA",ar1.get(0));

        viewPagerAdapter=new ViewPagerAdapter(this,ar1);
        viewPager.setAdapter(viewPagerAdapter);
       viewPager.setCurrentItem(pos);

    }
}
