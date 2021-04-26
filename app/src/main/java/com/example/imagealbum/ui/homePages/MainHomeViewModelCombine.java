package com.example.imagealbum.ui.homePages;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;
import androidx.core.graphics.BitmapCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.imagealbum.Global;
import com.example.imagealbum.customutil.CustomComparatorDate;
import com.example.imagealbum.image;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class MainHomeViewModelCombine extends ViewModel {
    String[] projection = {MediaStore.MediaColumns._ID,

            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.DATE_MODIFIED};

    String[] projection_video = {MediaStore.MediaColumns._ID,

            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.DATE_MODIFIED};

    private MutableLiveData< TreeMap<String, ArrayList<image>> > LiveData;
    private TreeMap<String, ArrayList<image>>  date_groups;

    private boolean isInSelectedMode;
    private boolean isInSlideShow;
    private boolean isInDeleteMode;

    public MainHomeViewModelCombine() {
        LiveData = new MutableLiveData<>();
        date_groups = new TreeMap<>(new CustomComparatorDate());
        isInSelectedMode = false;
        isInSlideShow = false;
        isInDeleteMode = false;
    }

    public MutableLiveData<TreeMap<String, ArrayList<image>>> getImageMutableLiveData() {
        return LiveData;
    }

    public boolean isInSelectedMode() {
        return this.isInSelectedMode;
    }

    public void setInSelectedMode(boolean mode) {
        this.isInSelectedMode = mode;
    }

    public boolean isInSlideShow() {
        return this.isInSlideShow;
    }

    public void setInSlideShow(boolean mode) {
        this.isInSlideShow = mode;
    }

    public boolean isInDeleteMode() {
        return this.isInDeleteMode;
    }

    public void setInDeleteMode(boolean mode) {
        this.isInDeleteMode = mode;
    }

    public void deSelectedAll() {
        for (Iterator<Map.Entry<String, ArrayList<image>>> it = date_groups.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, ArrayList<image>> me = it.next();
            ArrayList<image> temp_images = date_groups.get(me.getKey());

            for (image i : temp_images) {
                i.setSelectedMode(Global.SELECTED_MODE_OFF);
            }
        }
    }

    public ArrayList<image> getSelectedImages() {
        ArrayList<image> selected_images = new ArrayList<>();

        for (Iterator<Map.Entry<String, ArrayList<image>>> it = date_groups.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, ArrayList<image>> me = it.next();
            ArrayList<image> temp_images = date_groups.get(me.getKey());

            for (image i : temp_images) {
                if (i.isSelected()) {
                    selected_images.add(i);
                }
            }
        }

        return selected_images;
    }

    private void init() {

    }

    public void loadImageFromDevice(Context context) {
        for (Iterator<Map.Entry<String, ArrayList<image>>> it = date_groups.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, ArrayList<image>> me = it.next();
            date_groups.get(me.getKey()).clear();
        }

        String cur_date_string = "";
        ArrayList<image> temp_images = new ArrayList<>();

        //Load image
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, "date_modified DESC");
        while (cursor.moveToNext()) {
            //Load data from column
            String absolutePathOfImage = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
            long size = 0L;
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

            image imageFile = new image(uri, size, displayName, "Unknown", date_String, absolutePathOfImage, Global.IMAGE_TYPE);

            //Load to Map
            if (date_groups.containsKey(date_String)) {
                date_groups.get(date_String).add(imageFile);
            } else {
                ArrayList<image> temp_array = new ArrayList<>();
                temp_array.add(imageFile);

                date_groups.put(date_String, temp_array);
            }
        }
        cursor.close();
        //End get images

        //Load Videos
        cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection_video, null, null, "date_modified DESC");
        while (cursor.moveToNext()) {
            //Load data from column
            String absolutePathOfImage = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
            long size = 0L;
            try {
                size = Long.parseLong(cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.SIZE)));
            } catch (Exception ignored) {}
            String displayName = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME));
            int date_second = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns.DATE_MODIFIED));
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));



            Uri uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, String.valueOf(id));
            int duration = 0;
            //int duration = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns.DURATION));

            // TODO: load all properties to image

            String date_String = "";
            try {
                long date_milli = date_second * 1000L;
                date_String = new SimpleDateFormat("dd/MM/yyyy").format(new Date(date_milli));
            } catch (Exception ex) {
                System.out.println("EEE: " + ex);
            }

            image imageFile = new image(uri, size, displayName, "Unknown", date_String, absolutePathOfImage, Global.VIDEO_TYPE, duration);

            //Load to Map
            if (date_groups.containsKey(date_String)) {
                date_groups.get(date_String).add(imageFile);
            } else {
                ArrayList<image> temp_array = new ArrayList<>();
                temp_array.add(imageFile);

                date_groups.put(date_String, temp_array);
            }
        }
        cursor.close();
        //End get videos

        //update live data
        LiveData.postValue(date_groups);
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

    public void insertToDevice(Context context, Bitmap bitmap, String title, String description){
//        MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, title , description);

        int date_second = (int) (System.currentTimeMillis() / 1000);
        String date = String.valueOf(date_second);

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, title);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, title);
        values.put(MediaStore.Images.Media.DESCRIPTION, description);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.SIZE, BitmapCompat.getAllocationByteCount(bitmap));
        // Add the date meta data to ensure the image is added at the front of the gallery
        values.put(MediaStore.Images.Media.DATE_ADDED, date);
        values.put(MediaStore.Images.Media.DATE_TAKEN, date);
        values.put(MediaStore.Images.Media.DATE_MODIFIED, date);

        ContentResolver contentResolver = context.getContentResolver();
        Uri url = null;

        try {
            url = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            if (bitmap != null) {
                OutputStream imageOut = contentResolver.openOutputStream(url);
                try {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, imageOut);
                } finally {
                    imageOut.close();
                }

            } else {
                contentResolver.delete(url, null, null);
                url = null;
            }
        } catch (Exception e) {
            if (url != null) {
                contentResolver.delete(url, null, null);
            }
        }
    }
}
