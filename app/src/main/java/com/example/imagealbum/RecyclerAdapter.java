package com.example.imagealbum;

import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.HorizontalViewHolder> {
    private static int SEND_IMAGE = 1;
    private ArrayList<image> imagelist;
    private ArrayList<Integer> selectedImages;
    private Context context;
    private boolean inSelectionMode = false;
    private int standard_width;
    private int standard_height;

    public RecyclerAdapter(ArrayList<image> uri, Context context) {

        this.imagelist = uri;
        this.context = context;
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        this.standard_height = (int)(displayMetrics.heightPixels / 5);
        this.standard_width = (int) (displayMetrics.widthPixels / 4);
        this.selectedImages = new ArrayList<Integer>();
    }

    public void setInSelectionMode(boolean mode){
        this.inSelectionMode = mode;
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
        horizontalViewHolder.mImageRecyclerView.setScaleType(ImageView.ScaleType.FIT_XY);
        horizontalViewHolder.mImageRecyclerView.getLayoutParams().height = standard_height;
        horizontalViewHolder.mImageRecyclerView.getLayoutParams().width = standard_width;

        if(selectedImages.contains(position)){
            horizontalViewHolder.itemView.setBackgroundColor(Color.BLUE);
        }
        else {
            horizontalViewHolder.itemView.setBackgroundColor(Color.WHITE);
        }

        horizontalViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inSelectionMode){
                    selectedImages.add(position);
                    horizontalViewHolder.itemView.setBackgroundColor(Color.BLUE);
                }
                else{
                    Intent intent = new Intent(context, viewImage.class);
                    intent.putExtra("IMAGE", imagelist.get(position).toJson());
                    intent.putExtra("POS", String.valueOf(position));
                    ((Activity) context).startActivityForResult(intent, SEND_IMAGE);;
                }
            }
        });
    }

    public void deSelectedAll(){
        selectedImages.clear();
        this.notifyDataSetChanged();
    }

    public ArrayList<Integer> getSelectedImages(){
        ArrayList<Integer> tmp = new ArrayList<>();
        selectedImages.clear();
        return tmp;
    }

    @Override
    public int getItemCount() {
        return imagelist.size();
    }

    public class HorizontalViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageRecyclerView;
        View itemView;

        public HorizontalViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            mImageRecyclerView = itemView.findViewById(R.id.albumIten_img);
        }
    }
}
