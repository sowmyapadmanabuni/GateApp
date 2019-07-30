package com.oyespace.guards.utils;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;


import com.oyespace.guards.Myapp;
import com.oyespace.guards.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Created by kalyan pvs on 28-Sep-16.
 */

public class ImageLoader {

    public static String LOCAL_FILE_PREFIX = "file://";

    private static boolean isGif(String isGif) {
        return isGif != null && isGif.endsWith(".gif");
    }

    public static void loadImage(String imagePath, ImageView imageView, int placeHolder) {
        loadImageIntrnl(imagePath, imageView, placeHolder);
    }

    public static void loadImage(String imagePath, final ImageView imageView, final View progressView) {
        loadImageIntrnl(imagePath, imageView, progressView, R.drawable.tim);
    }


    public static void loadCircularImage(String imagePath, final ImageView imageView, int placeHolder) {
        loadImageIntrnl(imagePath, imageView, placeHolder);
    }


    public static void loadCircularImage(String imagePath, final ImageView imageView, final View progressView) {
        loadImageIntrnl(imagePath, imageView, progressView, R.drawable.tim);
    }

    public static void loadLocalImage(String imagePath, ImageView imageView, int placeHolder) {
        Picasso.with(Myapp.getContext()).load(imagePath)
                .placeholder(placeHolder)
                .error(placeHolder)
                .into(imageView);
    }

    private static void loadImageIntrnl(String imagePath, ImageView imageView, int placeHolder) {
        try {
            Picasso.with(imageView.getContext())
                    .load(imagePath)
                    .placeholder(placeHolder)
                    .error(placeHolder)
                    .into(imageView);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void loadImageIntrnl(String imagePath, final ImageView imageView, final View progressView, int placeholder) {
        try {
            if (TextUtils.isEmpty(imagePath)) {
                imageView.setImageResource(placeholder);
                progressView.setVisibility(View.GONE);
            } else {
                progressView.setVisibility(View.VISIBLE);
                Picasso.with(imageView.getContext())
                        .load(imagePath)
                        .placeholder(placeholder)
                        .error(placeholder)
                        .into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                imageView.setImageBitmap(bitmap);
                                progressView.setVisibility(View.GONE);
                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {
                                imageView.setImageDrawable(errorDrawable);
                                progressView.setVisibility(View.GONE);
                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {
                                imageView.setImageDrawable(placeHolderDrawable);
                                progressView.setVisibility(View.GONE);
                            }
                        });

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void loadImageBackground(String imagePath, final ImageView imageView, int placeholder) {
        try {
            if (TextUtils.isEmpty(imagePath)) {
                imageView.setBackgroundResource(placeholder);
            } else {
                Picasso.with(imageView.getContext())
                        .load(imagePath)
                        .placeholder(placeholder)
                        .error(placeholder)
//                        .transform(new BlurTransformation(imageView.getContext(), 2))
                        .into(imageView);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
