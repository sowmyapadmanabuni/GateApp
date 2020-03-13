package com.oyespace.guards.camtest;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.oyespace.guards.R;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder> {

private ArrayList<String> imageList;
Context context;
String visibility;

public class MyViewHolder extends RecyclerView.ViewHolder {
ImageView imageview,iv_delete;
CardView lyt_card;
    public MyViewHolder(View view) {
        super(view);
        imageview=view.findViewById(R.id.imageview);
        lyt_card=view.findViewById(R.id.lyt_card);
        iv_delete=view.findViewById(R.id.iv_delete);

    }
}


    public  ImageAdapter(ArrayList<String> imageList, Context context,String visibility) {
        this.imageList = imageList;
        this.context=context;
        this.visibility=visibility;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_custom_row_layout_camtest, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {


    if(visibility.equals("On")){
        holder.iv_delete.setVisibility(View.VISIBLE);
    }
    else if(visibility.equals("Off")){
        holder.iv_delete.setVisibility(View.GONE);
    }


        ImageHelper.loadImage(context, imageList.get(position), holder.imageview);
        holder.lyt_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int pos = (holder.getLayoutPosition());
                Intent intent=new Intent(context,ViewFullImageActivity.class);
                intent.putExtra("pos",pos);
                intent.putStringArrayListExtra("ImageList",imageList);
                context.startActivity(intent);

            }
        });
        holder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete image");
                builder.setCancelable(false);
                builder.setMessage("Do you want to delete this image");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        imageList.remove(position);
                        //iamgeLyt.removeView(imageView);
                        notifyDataSetChanged();

                        if (imageList.size() == 19) {
                            AddCarFragment.image_Gallery.setVisibility(View.VISIBLE);
                        }


                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        builder.setCancelable(true);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }
}
