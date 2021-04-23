package com.example.imagealbum.ui.home;

import android.content.ContentResolver;
import android.content.ContentUris;
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

    private MutableLiveData< ArrayList<ArrayList<image>> > LiveData;
    private ArrayList<ArrayList<image>> date_groups;

    public HomeImageViewModelByDate() {
        LiveData = new MutableLiveData<>();
        date_groups = new ArrayList<>();
    }

    public MutableLiveData<ArrayList<ArrayList<image>>> getImageMutableLiveData() {
        return LiveData;
    }

    private void init() {

    }

    public void loadImageFromDevice(Context context) {
        date_groups.clear();

        String cur_date_string = "";
        ArrayList<image> temp_images = new ArrayList<>();

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

            String date_String = "";
            try {
                long date_milli = date_second * 1000L;
                date_String = new SimpleDateFormat("dd/MM/yyyy").format(new Date(date_milli));
            } catch (Exception ex) {
                System.out.println("EEE: " + ex);
            }

            image imageFile = new image(uri, size, displayName, "Unknown", date_String, absolutePathOfImage);

           //Load to hasmap
            if (!cur_date_string.equals(date_String)) {
                temp_images = new ArrayList<>();
                temp_images.add(imageFile);
                date_groups.add(temp_images);
                cur_date_string = date_String;
            } else {
                temp_images.add(imageFile);
            }
        }
        cursor.close();

        //update live data
        LiveData.setValue(date_groups);
    }

    public void deleteImageInDevice(image img, Context context){
        // Set up the projection (we only need the ID)
        String[] projection = {MediaStore.Images.Media._ID};

        // Match on the file path
        String selection = MediaStore.Images.Media.DATA + " = ?";
        String[] selectionArgs = new String[]{img.getPath()};

        // Query for the ID of the media matching the file path
        Uri queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = context.getContentResolver();
        Cursor c = contentResolver.query(queryUri, projection, selection, selectionArgs, null);
        if (c.moveToFirst()) {
            // We found the ID. Deleting the item via the content provider will also remove the file
            long id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
            Uri deleteUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
            contentResolver.delete(deleteUri, null, null);
        } else {
            // File not found in media store DB
        }
        c.close();
    }
}
