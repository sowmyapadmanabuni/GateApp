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
//     Context mContext;
//    int pos;
//    ArrayList<String>imagesList;
//
//    public ViewPagerAdapter(Context context,int pos) {
//        mContext = context;
//        pos=pos;
//        imagesList=new ArrayList<>();
//        imagesList=AddCarFragment.list;
//    }
//
//    @Override
//    public Object instantiateItem(ViewGroup collection, int position) {
//       // ModelObject modelObject = ModelObject.values()[position];
//        LayoutInflater inflater = LayoutInflater.from(mContext);
//        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.layout_viewpager_iem, collection, false);
//        collection.addView(layout);
//        return layout;
//    }
//
//    @Override
//    public void destroyItem(ViewGroup collection, int position, Object view) {
//        collection.removeView((View) view);
//    }
//
//    @Override
//    public int getCount() {
//       // return ModelObject.values().length;
//        return imagesList.size();
//    }
//
//    @Override
//    public boolean isViewFromObject(View view, Object object) {
//        return view == object;
//    }
//
//    @Override
//    public CharSequence getPageTitle(int position) {
////        ModelObject customPagerEnum = ModelObject.values()[position];
////       return mContext.getString(customPagerEnum.getTitleResId());
//        return "kjbkc";
//    }

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
