package com.oyespace.guards.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.oyespace.guards.R;

import java.util.ArrayList;

public class SOSImageAdapter extends PagerAdapter {

    ArrayList<String> images = new ArrayList();
    Context mContext;

    public SOSImageAdapter(Context context, ArrayList images){
        this.images = images;
        this.mContext =context;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View layout = inflater.inflate(R.layout.sos_image, collection, false);
        try{
            ImageView img = (ImageView) layout.findViewById(R.id.sos_img);
            byte[] data = Base64.decode(images.get(position), Base64.DEFAULT);
            //Bitmap bmp = BitmapFactory.decodeByteArray(data,0,data.length);
            Log.e("IMAGE_SOS",""+images.get(position));
            //img.setImageBitmap(bmp);
            Glide.with(mContext)
                    .load(data)
                    .asBitmap()
                    .placeholder(R.drawable.oyespace_splash)
                    .into(img);


           // Base64.decode(images.get(position), Base64.DEFAULT)
            //val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            //sos_image!!.setImageBitmap(decodedImage)
        }catch (Exception e){
            Log.e("IMAGE_SOS_CRASH",""+e);
            e.printStackTrace();
        }
        collection.addView(layout);
        return layout;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout)object);
    }
}
