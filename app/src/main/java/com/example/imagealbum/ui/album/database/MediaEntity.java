package com.example.imagealbum.ui.album.database;

import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Insert;
import androidx.room.PrimaryKey;

import com.example.imagealbum.Global;

@Entity (tableName = Global.MEDIA_TABLE,
        foreignKeys = @ForeignKey(entity = AlbumEntity.class,
                parentColumns = Global.ALBUM_COLUMN._ID,
                childColumns = Global.MEDIA_COLUMN.ALBUM_ID))
public class MediaEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = Global.ALBUM_COLUMN._ID)
    public int id;

    @ColumnInfo(name = Global.MEDIA_COLUMN.URI)
    public String uri;

    @ColumnInfo(name = Global.MEDIA_COLUMN.PATH)
    public String path;

    @ColumnInfo(name = Global.MEDIA_COLUMN.PASSWORD)
    private String password;

    @ColumnInfo(name = Global.MEDIA_COLUMN.TYPE)
    public int type;

    @ColumnInfo(name = Global.MEDIA_COLUMN.STATUS)
    public String status;

    @ColumnInfo(name = Global.MEDIA_COLUMN.ALBUM_ID, index = true)
    public int albumID;

    @Ignore
    public Bitmap bitmap;

    @Ignore
    private boolean isSelected;

    @Ignore
    public MediaEntity(Uri uri, String path, int type, String status, int albumID) {
        this.uri = uri.toString();
        this.path = path;
        this.type = type;
        this.status = status;
        this.albumID = albumID;
        this.isSelected = Global.SELECTED_MODE_OFF;
    }

    @Ignore
    public MediaEntity(Uri uri, String path, int type, int albumID) {
        this.uri = uri.toString();
        this.path = path;
        this.type = type;
        this.albumID = albumID;
        this.isSelected = Global.SELECTED_MODE_OFF;
    }

    public MediaEntity() {
        this.isSelected = Global.SELECTED_MODE_OFF;
    }

    public int getID() {
        return this.id;
    }

    public String getUriString() {
        return this.uri;
    }

    public void setUriString(Uri uri) {
        this.uri = uri.toString();
    }

    public String getPath() {
        return this.path;
    }

    public void setPath() {
        this.path = path;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getAlbumID() {
        return this.albumID;
    }

    public void setAlbumID(int id) {
        this.albumID = id;
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

    public void setPassword(String password){this.password = password;}

    public String getPassword(){return this.password;}

    public Bitmap getBitmap() {
        return this.bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public boolean isImage() {
        return this.type == Global.IMAGE_TYPE;
    }

    public void setSelected(boolean mode) {
        this.isSelected = mode;
    }

    public boolean isSelected() {
        return this.isSelected;
    }
}
