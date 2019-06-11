package com.oyespace.guards.camtest;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Movie;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.oyespace.guards.R;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder> {

private List<String> imageList;
Context context;

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


    public ImageAdapter(List<String> imageList, Context context) {
        this.imageList = imageList;
        this.context=context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_custom_row_layout_camtest, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {


        ImageHelper.loadImage(context, imageList.get(position), holder.imageview);
        holder.lyt_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int pos = (holder.getLayoutPosition());
                Intent intent=new Intent(context,ViewFullImageActivity.class);
                intent.putExtra("pos",pos);
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
