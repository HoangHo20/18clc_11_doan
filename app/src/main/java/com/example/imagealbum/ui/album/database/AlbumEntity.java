package com.example.imagealbum.ui.album.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.imagealbum.BuildConfig;
import com.example.imagealbum.Global;

@Entity(tableName = Global.ALBUM_TABLE,
        indices = {@Index(value = {Global.ALBUM_COLUMN.NAME},
                unique = true)})
public class AlbumEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = Global.ALBUM_COLUMN._ID)
    public int id;

    @ColumnInfo(name = Global.ALBUM_COLUMN.NAME)
    public String name;

    @ColumnInfo(name = Global.ALBUM_COLUMN.PASSWORD)
    private String password;

    @Ignore
    public MediaEntity avatar = null;

    @Ignore
    public int size;

    @Ignore
    public boolean isPrivate() {
        return this.password != null && !this.password.isEmpty();
    }

    @Ignore
    public AlbumEntity(String name) {
        this.name = name;
    }

    public int getID() {
        return this.id;
    }

    public boolean checkPassword(String Password) {
        if (this.password == null || this.password.isEmpty()) {
            return true;
        } else {
            return Password != null
                    && !Password.isEmpty()
                    && this.password.equals(Password);
        }
    }

    public boolean changePassword(String oldPass, String newPass) {
        if (checkPassword(oldPass)) {
            this.password = newPass;
            return true;
        }

        return false;
    }

    public AlbumEntity(String name, String password){
        this.name = name;
        this.password = password;
    }

    public void setPassword(String password){this.password = password;}

    public String getPassword() {
        return this.password;
    }

    public String getName() {
        return this.name;
    }

    public void changeName(String newName) {
        if (newName != null && !newName.isEmpty()) {
            this.name = newName;
        }
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setAvatar(MediaEntity avatar) {
        this.avatar = avatar;
    }
}
