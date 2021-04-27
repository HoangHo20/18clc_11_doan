package com.example.imagealbum.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.graphics.BitmapCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.imagealbum.R;
import com.example.imagealbum.image;

import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HomeImageViewModel extends ViewModel {
    String[] projection = {MediaStore.MediaColumns._ID, MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.SIZE, MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.MediaColumns.DATE_MODIFIED};

    private MutableLiveData<ArrayList<image>> imageLiveData;
    private ArrayList<image> images;

    public HomeImageViewModel() {
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
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
        while (cursor.moveToNext()) {
            String absolutePathOfImage = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
            Long size = new Long(0);
            try {
                size = Long.parseLong(cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.SIZE)));
            } catch (Exception ignored) {
            }
            String displayName = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME));
            String date = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATE_MODIFIED));
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, String.valueOf(id));
            // TODO: load all properties to image
            image imageFile = new image(uri, size, displayName, "Unknown", date, absolutePathOfImage);

            images.add(imageFile);
        }
        cursor.close();

        //update live data
        imageLiveData.setValue(images);
    }

    public void loadImageFromDevice_byDate(Context context) {
        images.clear();
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, "date_modified DESC");
        while (cursor.moveToNext()) {
            String absolutePathOfImage = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
            Long size = new Long(0);
            try {
                size = Long.parseLong(cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.SIZE)));
            } catch (Exception ignored) {
            }
            String displayName = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME));
            String date = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATE_MODIFIED));
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, String.valueOf(id));
            // TODO: load all properties to image
            image imageFile = new image(uri, size, displayName, "Unknown", date, absolutePathOfImage);

            images.add(imageFile);
        }
        cursor.close();

        //update live data
        imageLiveData.setValue(images);
    }


    public void deleteImageInDevice(image img, Context context) {
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

    private Location getLastKnownLocation(Context context) {
        LocationManager mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions

            }
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    public void insertToDevice(Context context, Bitmap bitmap, String title, String description) {
//      MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, title , description);
        long date_second = System.currentTimeMillis();
        String date = String.valueOf(date_second);

        Location location = getLastKnownLocation(context);

        double longitude = location.getLongitude();
        double latitude = location.getLatitude();

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, title);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, title);
        values.put(MediaStore.Images.Media.DESCRIPTION, description);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.SIZE, BitmapCompat.getAllocationByteCount(bitmap));

        values.put(MediaStore.Images.Media.DATE_ADDED, date_second);
        values.put(MediaStore.Images.Media.DATE_TAKEN, date_second);
        values.put(MediaStore.Images.Media.DATE_MODIFIED, date_second);
        values.put(MediaStore.Images.Media.LONGITUDE, longitude);
        values.put(MediaStore.Images.Media.LATITUDE, latitude);

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
                url = null;
            }
        }
    }
}