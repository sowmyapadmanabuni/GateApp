package com.oyespace.guards.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.oyespace.guards.R;
import com.veinhorn.scrollgalleryview.MediaInfo;
import com.veinhorn.scrollgalleryview.ScrollGalleryView;
import com.veinhorn.scrollgalleryview.loader.picasso.PicassoImageLoader;

import java.util.ArrayList;
import java.util.List;


public class GalleryViewActivity extends AppCompatActivity {

    DatabaseReference mSosReference;
    private ScrollGalleryView galleryView;
    private ArrayList<String> images = new ArrayList<>();
    private int currentIndex = 0;
    private String mSOSImagePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_view);

        galleryView = findViewById(R.id.scroll_gallery_view);
        String jsonImages = getIntent().getStringExtra("images");
        images = new Gson().fromJson(jsonImages, ArrayList.class);
        mSOSImagePath = getIntent().getStringExtra("sospath") + "/emergencyImages";
        //initFRTDB();
        List<MediaInfo> infos = new ArrayList<>(images.size());
        for (String url : images) infos.add(MediaInfo.mediaLoader(new PicassoImageLoader(url)));

        galleryView.setThumbnailSize(100)
                .setZoom(true)
                .setFragmentManager(getSupportFragmentManager())
                .addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        currentIndex = position;
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                })
                .addMedia(infos);


    }

    private void initFRTDB() {

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mSosReference = FirebaseDatabase.getInstance().getReference(mSOSImagePath);
        mSosReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null) {
                    images = (ArrayList<String>) dataSnapshot.getValue();
                    galleryView.clearGallery();

                    List<MediaInfo> infos = new ArrayList<>(images.size());
                    for (String url : images)
                        infos.add(MediaInfo.mediaLoader(new PicassoImageLoader(url)));
                    galleryView.addMedia(infos);
                    Log.e("SOSIMAGES", "" + images);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
