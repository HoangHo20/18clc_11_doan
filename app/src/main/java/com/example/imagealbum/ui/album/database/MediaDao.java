package com.example.imagealbum.ui.album.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.imagealbum.Global;

import java.util.List;

@Dao
public interface MediaDao {
    @Query("SELECT * FROM " + Global.MEDIA_TABLE)
    public List<MediaEntity> getAll();

    @Query("SELECT * FROM "
            + Global.MEDIA_TABLE
            + " WHERE " + Global.MEDIA_COLUMN.ALBUM_ID + " = :id")
    public List<MediaEntity> getItemByAlbumID(int id);

    @Insert
    public void insert(MediaEntity media);

    @Delete
    public void delete(MediaEntity media);

    @Update
    public void update(MediaEntity media);
}
