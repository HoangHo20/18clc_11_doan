package com.example.imagealbum.ui.home;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.imagealbum.image;

import java.util.ArrayList;

public class HomeVideoViewModel extends ViewModel {
    String[] projection = {MediaStore.MediaColumns._ID, MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.SIZE, MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.MediaColumns.DATE_MODIFIED};

    private MutableLiveData<ArrayList<image>> imageLiveData;
    private ArrayList<image> images;

    public HomeVideoViewModel() {
        imageLiveData = new MutableLiveData<>();
        images = new ArrayList<>();
    }

    public MutableLiveData<ArrayList<image>> getImageMutableLiveData() {
        return imageLiveData;
    }

    private void init() {

    }

    public void loadImageFromDevice(Context context) {
        images.clear();
        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
        while (cursor.moveToNext()) {
            String absolutePathOfImage = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
            Long size = new Long(0);
            try {
                size = Long.parseLong(cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.SIZE)));
            } catch (Exception ignored) {}
            String displayName = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME));
            String date = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATE_MODIFIED));
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri uri = Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, String.valueOf(id));
            // TODO: load all properties to image
            image imageFile = new image(uri, size, displayName, "Unknown", date, absolutePathOfImage);

            images.add(imageFile);
        }
        cursor.close();

        //update live data
        imageLiveData.setValue(images);
    }
}
