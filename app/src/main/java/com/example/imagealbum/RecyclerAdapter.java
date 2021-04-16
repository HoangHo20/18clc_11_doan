package com.example.imagealbum;

import androidx.recyclerview.widget.RecyclerView;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> {
    private ArrayList<Uri> uri;

    public RecyclerAdapter(ArrayList<Uri> uri) {
        this.uri = uri;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.show_album_item, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder horizontalViewHolder, int position) {
        horizontalViewHolder.mImageRecyclerView.setImageURI(uri.get(position));
        horizontalViewHolder.mImageRecyclerView.setScaleType(ImageView.ScaleType.CENTER_CROP);
    }

    @Override
    public int getItemCount() {
        return uri.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageRecyclerView;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            mImageRecyclerView = itemView.findViewById(R.id.img);
        }
    }
}
