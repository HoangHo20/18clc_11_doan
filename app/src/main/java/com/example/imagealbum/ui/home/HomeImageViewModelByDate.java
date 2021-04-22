package com.example.imagealbum.ui.home;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.imagealbum.image;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class HomeImageViewModelByDate extends ViewModel {
    String[] projection = {MediaStore.MediaColumns._ID, MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.SIZE, MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.MediaColumns.DATE_MODIFIED};

    private MutableLiveData< HashMap<String, ArrayList<image>> > LiveData;
    private HashMap<String, ArrayList<image>> date_groups;

    public HomeImageViewModelByDate() {
        LiveData = new MutableLiveData<>();
        date_groups = new HashMap<>();
    }

    public MutableLiveData<HashMap<String, ArrayList<image>>> getImageMutableLiveData() {
        return LiveData;
    }

    private void init() {

    }

    public void loadImageFromDevice(Context context) {
        date_groups.clear();

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, "date_modified DESC");
        while (cursor.moveToNext()) {
            String absolutePathOfImage = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
            Long size = new Long(0);
            try {
                size = Long.parseLong(cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.SIZE)));
            } catch (Exception ignored) {}
            String displayName = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME));
            int date_second = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns.DATE_MODIFIED));
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, String.valueOf(id));
            // TODO: load all properties to image
            image imageFile = new image(uri, size, displayName, "Unknown", String.valueOf(date_second), absolutePathOfImage);

            String date_time = "";
            try {
                long date_milli = date_second * 1000L;
                date_time = new SimpleDateFormat("dd/MM/yyyy").format(new Date(date_milli));
            } catch (Exception ex) {
                System.out.println("EEE: " + ex);
            }

           //Load to hasmap
            if (date_groups.containsKey(date_time)) {
                date_groups.get(date_time.toString()).add(imageFile);
            } else {
                ArrayList<image> images_temp = new ArrayList<>();

                images_temp.add(imageFile);

                date_groups.put(date_time.toString(), images_temp);
            }
        }
        cursor.close();

        //update live data
        LiveData.setValue(date_groups);
    }
}
