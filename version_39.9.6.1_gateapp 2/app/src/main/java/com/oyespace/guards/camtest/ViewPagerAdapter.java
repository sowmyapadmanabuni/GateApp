package com.oyespace.guards.camtest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.viewpager.widget.PagerAdapter;

import com.oyespace.guards.R;

import java.util.ArrayList;

public class ViewPagerAdapter extends PagerAdapter {


    Context mContext;
    LayoutInflater mLayoutInflater;
    ArrayList<String>imagesList;


    public ViewPagerAdapter(Context context) {

        mContext = context;
        imagesList=new ArrayList<>();
        imagesList=AddCarFragment.list;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return imagesList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.layout_viewpager_iem, container, false);


        ImageView imageView = itemView.findViewById(R.id.image);
        ImageHelper.loadImage(mContext, imagesList.get(position), imageView);


        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }

}
