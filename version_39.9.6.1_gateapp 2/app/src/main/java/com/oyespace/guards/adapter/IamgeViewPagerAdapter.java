package com.oyespace.guards.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.viewpager.widget.PagerAdapter;

import com.oyespace.guards.R;
import com.oyespace.guards.camtest.AddCarFragment;
import com.oyespace.guards.camtest.ImageHelper;




public class IamgeViewPagerAdapter extends PagerAdapter {


    Context mContext;
    LayoutInflater mLayoutInflater;
    String imagesList[];


    public IamgeViewPagerAdapter(Context context, String imagesList[]) {

        mContext = context;
        imagesList=imagesList;

        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return imagesList.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.layout_viewpager_iem, container, false);


        ImageView imageView = (ImageView) itemView.findViewById(R.id.image);
      //  ImageHelper.loadImage(mContext, imagesList[position], imageView);


        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }

}
