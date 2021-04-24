package com.example.imagealbum.ui.homePages;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.imagealbum.Global;
import com.example.imagealbum.customutil.CustomComparatorDate;
import com.example.imagealbum.image;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@RequiresApi(api = Build.VERSION_CODES.Q)
public class MainHomeViewModelCombine extends ViewModel {
    String[] projection = {MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.BUCKET_DISPLAY_NAME,
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.DATE_MODIFIED};

    private MutableLiveData< TreeMap<String, ArrayList<image>> > LiveData;
    private TreeMap<String, ArrayList<image>>  date_groups;

    public MainHomeViewModelCombine() {
        LiveData = new MutableLiveData<>();
        date_groups = new TreeMap<>(new CustomComparatorDate());
    }

    public MutableLiveData<TreeMap<String, ArrayList<image>>> getImageMutableLiveData() {
        return LiveData;
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

        //Load Videos
        cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null, "date_modified DESC");
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

            image imageFile = new image(uri, size, displayName, "Unknown", date_String, absolutePathOfImage, Global.VIDEO_TYPE);

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



        //update live data
        LiveData.postValue(date_groups);
    }
}
