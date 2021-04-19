package com.example.imagealbum;

import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.HorizontalViewHolder> {
    private static int SEND_IMAGE = 1;
    private ArrayList<image> imagelist;
    private Context context;

    public RecyclerAdapter(ArrayList<image> uri, Context context) {

        this.imagelist = uri;
        this.context = context;
    }

    @NonNull
    @Override
    public HorizontalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.show_album_item, parent, false);
        return new HorizontalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HorizontalViewHolder horizontalViewHolder, int position) {
        horizontalViewHolder.mImageRecyclerView.setImageURI(imagelist.get(position).getImage_URI());
        horizontalViewHolder.mImageRecyclerView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        horizontalViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, viewImage.class);
                intent.putExtra("IMAGE", imagelist.get(position).toJson());
                intent.putExtra("POS", String.valueOf(position));
                ((Activity) context).startActivityForResult(intent, SEND_IMAGE);;
            }
        });
    }

    @Override
    public int getItemCount() {
        return imagelist.size();
    }

    public class HorizontalViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageRecyclerView;

        public HorizontalViewHolder(View itemView) {
            super(itemView);
            mImageRecyclerView = itemView.findViewById(R.id.img);
        }
    }
}
