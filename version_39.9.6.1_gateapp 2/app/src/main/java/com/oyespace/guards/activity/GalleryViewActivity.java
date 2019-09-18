package com.oyespace.guards.activity;
//import static com.veinhorn.scrollgalleryview.loader.picasso.dsl.DSL.*;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

//import com.denzcoskun.imageslider.ImageSlider;
//import com.denzcoskun.imageslider.models.SlideModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.oyespace.guards.R;
import com.veinhorn.scrollgalleryview.MediaInfo;
import com.veinhorn.scrollgalleryview.ScrollGalleryView;
import com.veinhorn.scrollgalleryview.builder.GallerySettings;
//import com.veinhorn.scrollgalleryview.loader.picasso.PicassoImageLoader;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class GalleryViewActivity extends FragmentActivity {

    //private ScrollGalleryView galleryView;
    private ArrayList<String> images = new ArrayList<>();
    private int currentIndex = 0;
    private String mSOSImagePath = "";
    DatabaseReference mSosReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_view);

        String jsonImages = getIntent().getStringExtra("images");
        images = new Gson().fromJson(jsonImages,ArrayList.class);
//        List<SlideModel> infos = new ArrayList<>(images.size());
//        for (String url : images) infos.add(new SlideModel(url));

// imageList.add(SlideModel("String Url" or R.drawable)
// imageList.add(SlideModel("String Url" or R.drawable, "title") You can add title
// imageList.add(SlideModel("String Url" or R.drawable, "title", true) Also you can add centerCrop scaleType for this image
//        images.add(SlideModel("https://1.bp.blogspot.com/-GUZsgr8my50/XJUWOhyHyaI/AAAAAAAABUo/bljp3LCS3SUtj-judzlntiETt7G294WcgCLcBGAs/s1600/fox.jpg", "Foxes live wild in the city.", true))
//        images.add(SlideModel("https://2.bp.blogspot.com/-CyLH9NnPoAo/XJUWK2UHiMI/AAAAAAAABUk/D8XMUIGhDbwEhC29dQb-7gfYb16GysaQgCLcBGAs/s1600/tiger.jpg"))
//        images.add(SlideModel("https://3.bp.blogspot.com/-uJtCbNrBzEc/XJUWQPOSrfI/AAAAAAAABUs/ZlReSwpfI3Ack60629Rv0N8hSrPFHb3TACLcBGAs/s1600/elephant.jpg", "The population of elephants is decreasing in the world."))
//        ImageSlider imageSlider = findViewById(R.id.scroll_gallery_view);
//        imageSlider.setImageList(infos);


//        galleryView = findViewById(R.id.scroll_gallery_view);
//        String jsonImages = getIntent().getStringExtra("images");
//        images = new Gson().fromJson(jsonImages,ArrayList.class);
//        mSOSImagePath = getIntent().getStringExtra("sospath")+"/emergencyImages";
//        //initFRTDB();
//        List<MediaInfo> infos = new ArrayList<>(images.size());
//        for (String url : images) infos.add(image(url));
//
//        galleryView.setThumbnailSize(100)
//                .setZoom(true)
//                .addMedia(infos);



    }

    private void initFRTDB(){

//        DatabaseReference mDatabase =  FirebaseDatabase.getInstance().getReference();
//        mSosReference = FirebaseDatabase.getInstance().getReference(mSOSImagePath);
//        mSosReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                if(dataSnapshot.getValue() != null){
//                    images = (ArrayList<String>) dataSnapshot.getValue();
//                    galleryView.clearGallery();
//
//                    List<MediaInfo> infos = new ArrayList<>(images.size());
//                    for (String url : images) infos.add(MediaInfo.mediaLoader(new PicassoImageLoader(url)));
//                    galleryView.addMedia(infos);
//                    Log.e("SOSIMAGES",""+images);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
