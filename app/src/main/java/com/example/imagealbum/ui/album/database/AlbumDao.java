package com.example.imagealbum.ui.album.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.imagealbum.Album;
import com.example.imagealbum.Global;

import java.util.List;

@Dao
public interface AlbumDao {
    @Query("SELECT * FROM " + Global.ALBUM_TABLE)
    List<AlbumEntity> getAll();

    @Query("SELECT * FROM "
            + Global.ALBUM_TABLE
            + " WHERE " + Global.ALBUM_COLUMN._ID + " = :id")
    AlbumEntity getAlbumByID(int id);

    @Query("SELECT * FROM "
            + Global.ALBUM_TABLE
            + " WHERE " + Global.ALBUM_COLUMN.NAME + " LIKE :name")
    List<AlbumEntity> getAlbumByName(String name);

    @Insert
    void insert(AlbumEntity album);

    @Delete
    void delete(AlbumEntity album);

    @Update
    void update(AlbumEntity album);
}
