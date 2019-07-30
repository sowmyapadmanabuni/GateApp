package com.oyespace.guards.camtest;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.oyespace.guards.Myapp;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.Callback;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import com.oyespace.guards.R;


/**
 * Created by kalyan_pvs on 14/5/16.
 */
public class ImageHelper {

    public static Picasso picasso;
    private static boolean isPicassoEnabled;

    static {
        initialiseStaticBlock();
    }

    private static void initialiseStaticBlock() {
        if (isPicassoEnabled) {
            OkHttpClient okHttpClient = null;
            try {
                long cacheSize = 100 * (1024 * 1024);
                okHttpClient = new OkHttpClient();
                okHttpClient.setCache(new Cache(Myapp.getInstance().getCacheDir(), cacheSize));
            } catch (Exception e) {
                e.printStackTrace();
            }
            picasso = new Picasso.Builder(Myapp.getInstance()).downloader(new OkHttpDownloader(okHttpClient)).build();
        }
    }

    public static void loadImageToCenterCenter(Context ctx, final String imageUrl, int placeHolder, final ImageView imageView) {

        if (isPicassoEnabled) {
            picasso.load(imageUrl).fit().centerCrop().placeholder(placeHolder).into(imageView);
        } else {
            Glide.with(ctx).load(imageUrl).centerCrop().placeholder(placeHolder).into(imageView);
        }
    }

    public static void loadImageToCenterCenterWithDrawable(Context ctx, final String imageUrl, Drawable placeHolder, final ImageView imageView) {

        if (isPicassoEnabled) {
            picasso.load(imageUrl).fit().centerCrop().placeholder(placeHolder).into(imageView);
        } else {

            Glide.with(ctx).load(imageUrl).centerCrop().placeholder(placeHolder).into(imageView);
        }
    }

    public static void loadImageToCenterCenter(Context ctx, final String imageUrl, final ImageView imageView) {

        if (isPicassoEnabled) {
            picasso.load(imageUrl).fit().centerCrop().into(imageView);
        } else {

            Glide.with(ctx).load(imageUrl).centerCrop().into(imageView);
        }
    }

    public static void loadImage(Context ctx, String imageUrl, final ImageView imageView) {
//        if (isPicassoEnabled) {
//            picasso.load(imageUrl).into(imageView);
//        } else {
//            Glide.with(ctx).load(imageUrl).into(imageView);
//        }
        loadImage(ctx, imageUrl, R.drawable.placeholder_dark, R.drawable.placeholder_dark, imageView);
    }

    public static void loadImage(Context ctx, String imageUrl, int placeHolder, int errorPlaceHolder, final ImageView imageView) {
        if (isPicassoEnabled) {
            picasso.load(imageUrl).placeholder(placeHolder).error(errorPlaceHolder).into(imageView);
        } else {
            Glide.with(ctx).load(imageUrl).placeholder(placeHolder).error(errorPlaceHolder).into(imageView);
        }
    }

    // Method used for the Flight Icons
    public static void loadImageToFit(Context ctx, String imageUrl, int placeHolder, int errorPlaceHolder, final ImageView imageView) {
        if (isPicassoEnabled) {
            picasso.load(imageUrl).placeholder(placeHolder).error(errorPlaceHolder).into(imageView);
        } else {

            Glide.with(ctx).load(imageUrl).asBitmap().fitCenter().placeholder(placeHolder).error(errorPlaceHolder).into(imageView);
        }
    }

    public static void loadImageToFitWithoutPlaceHolder(Context ctx, String imageUrl, final ImageView imageView) {
        if (isPicassoEnabled) {
            picasso.load(imageUrl).fit().centerCrop().into(imageView);
        } else {
            Glide.with(ctx).load(imageUrl).centerCrop().into(imageView);
        }

    }

    public static void loadImageToFitWithoutPlaceHolderWithoutDownloader(Context ctx, String imageUrl, final ImageView imageView) {
        if (isPicassoEnabled) {
            Picasso.Builder builder = new Picasso.Builder(ctx);
            builder.listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                }
            });
            builder.build().load(imageUrl).into(imageView);

        } else {
            Glide.with(ctx).load(imageUrl).listener(new LoggingListener<String, GlideDrawable>()).into(imageView);
        }
    }

    public static void loadImageWithoutPlaceHolder(Context ctx, final String imageUrl, final ImageView imageView) {
        if (isPicassoEnabled) {
            picasso.load(imageUrl).into(imageView);
        } else {
            Glide.with(ctx).load(imageUrl).into(new SimpleTarget<GlideDrawable>() {
                @Override
                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                    imageView.setImageDrawable(resource);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                }
            });
        }
    }

    public static void fadeImageToCenterWithoutPlaceHolder(Context ctx, final String imageUrl, final ImageView imageView) {
        if (isPicassoEnabled) {
            picasso.load(imageUrl).into(imageView);
        } else {
            Glide.with(ctx).load(imageUrl).centerCrop().crossFade().into(new SimpleTarget<GlideDrawable>() {
                @Override
                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                    imageView.setImageDrawable(resource);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                }
            });
        }
    }

    public static void loadImageUrlWithCallbackUrl(Context ctx, String imageUrl, String callBackUrl, int placeHolder, ImageView imageView) {
        if (isPicassoEnabled) {
            picasso.load(imageUrl).placeholder(placeHolder).error(placeHolder).fit().into(imageView, new PicassoCallback(ctx, placeHolder, callBackUrl, imageView));
        } else {
            Glide.with(ctx).load(imageUrl).placeholder(placeHolder).error(placeHolder).fitCenter().listener(new GlideCallback(ctx, placeHolder, callBackUrl, imageView)).into(imageView);
        }
    }

    public static void loadImageUrlWithAspectFill(Context ctx, String imageUrl, String callBackUrl, int placeHolder, ImageView imageView) {
        if (isPicassoEnabled) {
            picasso.load(imageUrl).placeholder(placeHolder).into(imageView, new PicassoCallback(ctx, placeHolder, callBackUrl, imageView));
        } else {
            Glide.with(ctx).load(imageUrl).asBitmap().centerCrop().listener(new GlideCallback(ctx, placeHolder, callBackUrl, imageView)).into(imageView);
        }
    }

    public static void loadImageToCenterCenterWithCallback(Context ctx, String imageUrl, int placeHolder, ImageView imageView, final ProgressBar bar) {
        bar.setVisibility(View.VISIBLE);

        // Add RequestListner instaed of CallBack while using glide

        if (isPicassoEnabled) {
            picasso.load(imageUrl).fit().centerCrop().placeholder(placeHolder).into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                    bar.setVisibility(View.GONE);
                }

                @Override
                public void onError() {
                    bar.setVisibility(View.GONE);
                }
            });
        } else {

            Glide.with(ctx).load(imageUrl).centerCrop().placeholder(placeHolder).listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, com.bumptech.glide.request.target.Target<GlideDrawable> target, boolean isFirstResource) {
                    bar.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, com.bumptech.glide.request.target.Target<GlideDrawable> target, boolean isFromMemoryCache,
                                               boolean isFirstResource) {
                    bar.setVisibility(View.GONE);
                    return false;
                }
            }).into(imageView);
        }
    }


    static class LoggingListener<T, R> implements RequestListener<T, R> {
        @Override
        public boolean onException(Exception e, T model, com.bumptech.glide.request.target.Target<R> target, boolean isFirstResource) {
            return false;
        }

        @Override
        public boolean onResourceReady(R resource, T model, com.bumptech.glide.request.target.Target<R> target, boolean isFromMemoryCache, boolean isFirstResource) {
            return false;
        }
    }

    public static class PicassoCallback implements Callback {

        private String callBackUrl;
        private Context context;
        private ImageView imageView;
        private int placeHolder;

        public PicassoCallback(Context ctx, int placeHolder, String url, ImageView imageView) {
            super();
            callBackUrl = url;
            this.context = ctx;
            this.imageView = imageView;
            this.placeHolder = placeHolder;
        }

        @Override
        public void onError() {
            if (imageView != null) {
                Picasso.with(context).load(callBackUrl).placeholder(placeHolder).error(placeHolder).into(imageView);
            }
        }

        @Override
        public void onSuccess() {
        }
    }

    public static class GlideCallback implements RequestListener {

        private String callBackUrl;
        private Context context;
        private ImageView imageView;
        private int placeHolder;

        public GlideCallback(Context ctx, int placeHolder, String url, ImageView imageView) {
            super();
            callBackUrl = url;
            this.context = ctx;
            this.imageView = imageView;
            this.placeHolder = placeHolder;
        }

        @Override
        public boolean onException(Exception e, Object model, com.bumptech.glide.request.target.Target target, boolean isFirstResource) {
            if (imageView != null) {
                Glide.with(context).load(callBackUrl).asBitmap().dontTransform().placeholder(placeHolder).error(placeHolder).into(imageView);
            }

            return true;
        }

        @Override
        public boolean onResourceReady(Object resource, Object model, com.bumptech.glide.request.target.Target target, boolean isFromMemoryCache, boolean isFirstResource) {

            return false;
        }
    }


}
