package com.example.imagealbum.ui.album;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.imagealbum.Album;
import com.example.imagealbum.R;
import com.example.imagealbum.RecyclerAdapter;
import com.example.imagealbum.ui.album.database.AlbumEntity;

import java.util.List;

public class AlbumRecyclerAdapter extends RecyclerView.Adapter<AlbumRecyclerAdapter.ViewHolder> {
    Context context;
    List<AlbumEntity> albums;
    private RecyclerViewAdapterListener listener;

    public AlbumRecyclerAdapter(Context context, RecyclerViewAdapterListener listener, List<AlbumEntity> albums) {
        this.context = context;
        this.albums = albums;
        this.listener = listener;
    }

    public void setDataAndUpdate(List<AlbumEntity> newAlbums) {
        this.albums = newAlbums;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_item, parent, false);
        return new AlbumRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position < getItemCount()) {
            AlbumEntity album = albums.get(position);

            //Load avatar of the album
            if (album.isPrivate()) {
                Glide.with(context)
                        .load(R.drawable.ic_baseline_lock_24)
                        .into(holder.imageView);
            } else {
                if (album.avatar != null) {
                    Glide.with(context)
                            .load(album.avatar.uri)
                            .into(holder.imageView);
                } else {
                    Glide.with(context)
                            .load(R.drawable.ic_baseline_image_not_supported_24)
                            .into(holder.imageView);
                }
            }

            //Load title and size (if has)
            holder.name_title.setText(album.getName());

            holder.linearlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onRecyclerAdapterClick(album);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (this.albums != null && !this.albums.isEmpty()) {
            return this.albums.size();
        }

        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView imageView;
        TextView name_title, size_title;
        View linearlayout;

        public ViewHolder(View view) {
            super(view);
            linearlayout = view.findViewById(R.id.album_whole_item);
            //cardView = (CardView) view.findViewById(R.id.album_item_cardview);
            imageView = (ImageView) view.findViewById(R.id.album_avatar);
            name_title = (TextView) view.findViewById(R.id.album_title);
            size_title = (TextView) view.findViewById(R.id.album_size);
        }
    }

    public interface RecyclerViewAdapterListener {
        void onRecyclerAdapterClick(AlbumEntity album);
    }
}
