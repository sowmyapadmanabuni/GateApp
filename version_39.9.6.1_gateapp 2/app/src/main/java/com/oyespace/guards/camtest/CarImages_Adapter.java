package com.oyespace.guards.camtest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.viewpager.widget.PagerAdapter;

import com.oyespace.guards.R;

import java.io.FileNotFoundException;
import java.util.ArrayList;


/**
 * Created by SONU on 01-03-2016.
 */
public class CarImages_Adapter extends PagerAdapter {
    private Context context;
    private ArrayList<String> listimage;


    public CarImages_Adapter(Context context, ArrayList<String> listimage) {
        this.context = context;
        this.listimage = listimage;
    }

    @Override
    public int getCount() {
        return listimage.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View pagerview = inflater.inflate(R.layout.custom_add_car_images_layout, container, false);
        final ImageView pagerImage = pagerview.findViewById(R.id.car_images);
        Log.e("Path", "" + listimage.get(position));
        Bitmap b = null;
        try {
            b = new BitmapDecoder().decodeUri(context, listimage.get(position), 0);
            Log.e("Bitmap", "" + b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(context, "Oops! File is missing.", Toast.LENGTH_SHORT).show();
        }
        pagerImage.setImageBitmap(b);
        container.addView(pagerview, 0);
        final ImageView imagedelete = pagerview.findViewById(R.id.image_delete_button);
        imagedelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete Image");
                builder.setCancelable(false);
                builder.setMessage("You want to delete this image");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listimage.remove(position);
                        if (listimage.size() == 0)
                            new AddCarFragment().hideViewPager();
                        notifyDataSetChanged();
//                        finish();
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                    builder.setCancelable(true);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        return pagerview;
    }
}
