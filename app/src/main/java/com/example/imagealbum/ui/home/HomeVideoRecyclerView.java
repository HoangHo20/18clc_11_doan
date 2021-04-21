package com.example.imagealbum.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.imagealbum.R;
import com.example.imagealbum.image;

import java.util.ArrayList;

public class HomeVideoRecyclerView extends RecyclerView.Adapter<HomeVideoRecyclerView.ViewHolder> {
    ArrayList<image> images;
    Context context;

    // Constructor for initialization
    public HomeVideoRecyclerView(Context context, ArrayList<image> images) {
        this.context = context;
        this.images = images;
    }

    @NonNull
    @Override
    public HomeVideoRecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflating the Layout(Instantiates list_item.xml layout file into View object)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_album_item, parent, false);

        // Passing view to ViewHolder
        return new HomeVideoRecyclerView.ViewHolder(view);
    }

    // Binding data to the into specified position
    @Override
    public void onBindViewHolder(@NonNull HomeVideoRecyclerView.ViewHolder holder, int position) {
        // TypeCast Object to int type
        image image_res = images.get(position);
        try {
            Glide.with(context)
                    .load(images.get(position).getImage_URI().toString())
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade(500))
                    .into(holder.mImageView);
//        holder.mImageView.setImageURI(image_res.getImage_URI());
        } catch (Exception e) {
            System.out.println("EEE: " + e.toString());
        }
    }

    @Override
    public int getItemCount() {
        // Returns number of items currently available in Adapter
        return images.size();
    }

    // Initializing the Views
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        View itemView;

        public ViewHolder(View view) {
            super(view);
            itemView = view;
            mImageView = (ImageView) view.findViewById(R.id.albumIten_img);
        }
    }
}
