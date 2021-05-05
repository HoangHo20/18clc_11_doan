package com.example.imagealbum.ui.album;

import android.content.Context;

import com.example.imagealbum.Album;
import com.example.imagealbum.ui.album.database.AlbumEntity;
import com.example.imagealbum.ui.album.database.MediaDao;
import com.example.imagealbum.ui.album.database.MediaEntity;
import com.example.imagealbum.ui.album.database.database;

import java.util.ArrayList;
import java.util.List;

public class AlbumModel {
    Context context;

    public AlbumModel(Context context) {
        this.context = context;
    }

    // ---------------- Album ----------------
    public List<AlbumEntity> getAllAlbums() {
        if (context == null) {
            return null;
        }

        return database.getInstance(context)
                .albumDao()
                .getAll();
    }

    public List<AlbumEntity> getAlbumByName(String name) {
        if (context == null) return null;

        return database.getInstance(context)
                .albumDao()
                .getAlbumByName(name);
    }

    public AlbumEntity getAlbumByID(int id) {
        if (context == null) return null;

        return database.getInstance(context)
                .albumDao()
                .getAlbumByID(id);
    }

    public void insertAlbum(AlbumEntity album) {
        if (context != null) {
            database.getInstance(context)
                    .albumDao()
                    .insert(album);
        }
    }

    public void deleteAlbum(AlbumEntity album) {
        if (context!=null) {
            database.getInstance(context)
                    .albumDao()
                    .delete(album);
        }
    }

    public void updateAlbum(AlbumEntity album) {
        if (context!=null) {
            database.getInstance(context)
                    .albumDao()
                    .update(album);
        }
    }


    // ---------- Media (Image/ Video)  ------------
    public List<MediaEntity> getMediaByAlbumID(int id) {
        if (context == null) {
            return null;
        }

        return database.getInstance(context)
                .mediaDao()
                .getItemByAlbumID(id);
    }

    public MediaEntity getOneMediaByAlbumID(int id) {
        if (context == null) {
            return null;
        }

        return database.getInstance(context)
                .mediaDao()
                .getOneItemByAlbumID(id);
    }

    public void insertMedia(MediaEntity media) {
        if (context != null) {
            database.getInstance(context)
                    .mediaDao()
                    .insert(media);
        }
    }

    public void insertMedia(ArrayList<MediaEntity> mediaEntities) {
        if (context != null) {
            database.getInstance(context)
                    .mediaDao()
                    .insert(mediaEntities);
        }
    }

    public void deleteMedia(MediaEntity media) {
        if (context != null) {
            database.getInstance(context)
                    .mediaDao()
                    .delete(media);
        }
    }

    public void deleteMedia(List<MediaEntity> mediaEntities) {
        if (context != null) {
            database.getInstance(context)
                    .mediaDao()
                    .delete(mediaEntities);
        }
    }

    public void updateMedia(MediaEntity media) {
        if (context != null) {
            database.getInstance(context)
                    .mediaDao()
                    .update(media);
        }
    }
}
