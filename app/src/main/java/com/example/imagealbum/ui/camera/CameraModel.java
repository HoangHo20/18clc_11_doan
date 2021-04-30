package com.example.imagealbum.ui.camera;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.core.app.ActivityCompat;
import androidx.core.graphics.BitmapCompat;

import java.io.OutputStream;
import java.util.List;

public class CameraModel {

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
        long date_milli = System.currentTimeMillis() / 1000L;
        String date = String.valueOf(date_milli);

        Location location = getLastKnownLocation(context);

        double longitude = location.getLongitude();
        double latitude = location.getLatitude();

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, title);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, title);
        values.put(MediaStore.Images.Media.DESCRIPTION, description);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.SIZE, BitmapCompat.getAllocationByteCount(bitmap));

        values.put(MediaStore.Images.Media.DATE_ADDED, date_milli);
        values.put(MediaStore.Images.Media.DATE_TAKEN, date_milli);
        values.put(MediaStore.Images.Media.DATE_MODIFIED, date_milli);
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
