package com.example.imagealbum.ui.album.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.imagealbum.Global;

@Database(entities = {MediaEntity.class, AlbumEntity.class},
        version = Global.DATABASE_VERSION)
public abstract class database extends RoomDatabase {
    private static final String DATABASE_NAME = Global.DATABASE_NAME;
    private static database instance;

    public static synchronized database getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    database.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .build();
        }

        return instance;
    }

    public abstract AlbumDao albumDao();
    public abstract MediaDao mediaDao();
}
