package com.example.imagealbum.ui.album.showalbum;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.imagealbum.Global;
import com.example.imagealbum.R;
import com.example.imagealbum.ui.album.database.MediaEntity;

import java.util.ArrayList;
import java.util.List;

public class ShowAlbumRecyclerAdapter extends RecyclerView.Adapter<ShowAlbumRecyclerAdapter.ViewHolder> {
    private Context context;
    private List<MediaEntity> mediaList;
    private boolean isPrivate;
    private boolean isInSelectedMode;

    public ShowAlbumRecyclerAdapter(Context context, List<MediaEntity> media, boolean isPrivate) {
        this.context = context;
        this.mediaList = media;
        this.isPrivate = isPrivate;
        this.isInSelectedMode = Global.SELECTED_MODE_OFF;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_album_item, parent, false);

        return new ShowAlbumRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MediaEntity media = mediaList.get(position);

        if (isPrivate) {
            //TODO: load bitmap
        } else {
            Glide.with(context)
                    .load(media.getPath())
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade(500))
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .into(holder.imageView);

            if (!media.isImage()) {//not image == video
                holder.videoTag.setVisibility(View.VISIBLE);
            }
        }

        if(media.isSelected()){
            holder.itemView.setBackgroundColor(context.getColor(R.color.background_selected));
        }
        else {
            if(Global.loadLastTheme(context) == 0)
            {
                holder.itemView.setBackgroundColor(context.getColor(R.color.background));
            }
            else{
                holder.itemView.setBackgroundColor(context.getColor(R.color.background_dark));
            }
        }

        //TODO: open image
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInSelectedMode) {
                    media.setSelected(!media.isSelected());

                    if(media.isSelected()){
                        holder.itemView.setBackgroundColor(context.getColor(R.color.background_selected));
                    }
                    else {
                        if(Global.loadLastTheme(context) == 0)
                        {
                            holder.itemView.setBackgroundColor(context.getColor(R.color.background));
                        }
                        else{
                            holder.itemView.setBackgroundColor(context.getColor(R.color.background_dark));
                        }
                    }
                }
            }
        });
    }

    public void setInSelectedMode(boolean mode) {
        this.isInSelectedMode = mode;
    }

    public boolean isInSelectedMode() {
        return this.isInSelectedMode;
    }

    @Override
    public int getItemCount() {
        if (this.mediaList != null && !this.mediaList.isEmpty()) {
            return this.mediaList.size();
        }

        return 0;
    }

    public void deSelectedAll() {
        for (MediaEntity m : mediaList) {
            m.setSelected(Global.SELECTED_MODE_OFF);
        }

        notifyDataSetChanged();
    }

    public ArrayList<MediaEntity> getSelectedMedia() {
        ArrayList<MediaEntity> selectItems = new ArrayList<>();

        for (MediaEntity m : this.mediaList) {
            if (m.isSelected()) {
                selectItems.add(m);
            }
        }

        return selectItems;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        ImageView videoTag;
        View itemView;

        public ViewHolder(View view) {
            super(view);

            itemView = view;
            imageView = (ImageView) view.findViewById(R.id.albumIten_img);
            videoTag = (ImageView) view.findViewById(R.id.albumItem_video_tag);
        }
    }
}
