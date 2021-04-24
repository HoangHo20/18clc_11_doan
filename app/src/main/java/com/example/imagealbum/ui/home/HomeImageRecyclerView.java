package com.example.imagealbum.ui.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.VideoBitmapDecoder;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.imagealbum.R;
import com.example.imagealbum.image;
import com.example.imagealbum.viewImage;

public class HomeImageRecyclerView extends RecyclerView.Adapter<HomeImageRecyclerView.ViewHolder> {
    private static int SEND_IMAGE = 1;
    private ArrayList<Integer> selectedImages;
    private boolean inSelectionMode = false;
    ArrayList<image> images;
    Context context;

    // Constructor for initialization
    public HomeImageRecyclerView(Context context, ArrayList<image> images) {
        this.context = context;
        this.images = images;
        this.selectedImages = new ArrayList<Integer>();
    }

    public void setInSelectionMode(boolean mode){
        this.inSelectionMode = mode;
    }

    @NonNull
    @Override
    public HomeImageRecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflating the Layout(Instantiates list_item.xml layout file into View object)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_album_item, parent, false);

        // Passing view to ViewHolder
        return new HomeImageRecyclerView.ViewHolder(view);
    }

    // Binding data to the into specified position
    @Override
    public void onBindViewHolder(@NonNull HomeImageRecyclerView.ViewHolder holder, int position) {
        // TypeCast Object to int type
        image image_res = images.get(position);

        if (image_res.isImage()) {
            try {
                Glide.with(context)
                        .load(images.get(position).getPath())
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .centerCrop()
                        .transition(DrawableTransitionOptions.withCrossFade(500))
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .into(holder.mImageView);
//        holder.mImageView.setImageURI(image_res.getImage_URI());
            } catch (Exception e) {
                System.out.println("EEE: " + e.toString());
            }
        } else {

            Glide.with(context)
                    .load(images.get(position).getPath())
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .transition(DrawableTransitionOptions.withCrossFade(500))
                    .into(holder.mImageView);
        }

        if(selectedImages.contains(position)){
            holder.itemView.setBackgroundColor(Color.BLUE);
        }
        else {
            holder.itemView.setBackgroundColor(Color.WHITE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inSelectionMode){
                    if(selectedImages.contains(position)){
                        selectedImages.remove(new Integer(position));
                        holder.itemView.setBackgroundColor(Color.WHITE);
                    }
                    else{
                        selectedImages.add(position);
                        holder.itemView.setBackgroundColor(Color.BLUE);
                    }
                }
                else{
                    Intent intent = new Intent(context, viewImage.class);
                    intent.putExtra("IMAGE", images.get(position).toJson());
                    intent.putExtra("POS", String.valueOf(position));
                    ((Activity) context).startActivityForResult(intent, SEND_IMAGE);
                }
            }
        });
    }

    public void deSelectedAll(){
        selectedImages.clear();
        this.notifyDataSetChanged();
    }

    public ArrayList<image> deleteSelectedImages(){
        ArrayList<image> res = getSelectedImages();
        for(image index: res){
            images.remove(index);
        }

        return res;
    }

    public ArrayList<image> getSelectedImages(){
        ArrayList<image> res = new ArrayList<>();
        for(int index: selectedImages){
            res.add(images.get(index));
        }
        deSelectedAll();
        return res;
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
